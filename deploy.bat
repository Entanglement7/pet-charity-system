@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: 宠物慈善系统 Docker 一键部署脚本 (Windows)

echo ==========================================
echo   宠物慈善系统 Docker 部署
echo ==========================================
echo.

:: 检查 Docker 是否安装
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: Docker 未安装
    echo 请先安装 Docker Desktop: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

:: 检查 Docker Compose 是否安装
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: Docker Compose 未安装
    pause
    exit /b 1
)

echo ✅ Docker 和 Docker Compose 已安装
echo.

:menu
echo 请选择操作：
echo 1) 启动所有服务
echo 2) 停止所有服务
echo 3) 重启所有服务
echo 4) 查看服务状态
echo 5) 查看日志
echo 6) 重新构建并启动
echo 7) 清理所有容器和数据
echo 8) 备份数据库
echo 9) 退出
echo.

set /p choice="请输入选项 (1-9): "

if "%choice%"=="1" goto start
if "%choice%"=="2" goto stop
if "%choice%"=="3" goto restart
if "%choice%"=="4" goto status
if "%choice%"=="5" goto logs
if "%choice%"=="6" goto rebuild
if "%choice%"=="7" goto clean
if "%choice%"=="8" goto backup
if "%choice%"=="9" goto exit
echo ❌ 无效选项
pause
goto menu

:start
echo.
echo 🚀 启动所有服务...
docker-compose up -d
echo.
echo ✅ 服务启动成功！
echo.
echo 访问地址：
echo   前端: http://localhost
echo   后端: http://localhost:8080
echo.
echo 查看日志: docker-compose logs -f
pause
goto end

:stop
echo.
echo 🛑 停止所有服务...
docker-compose stop
echo ✅ 服务已停止
pause
goto end

:restart
echo.
echo 🔄 重启所有服务...
docker-compose restart
echo ✅ 服务已重启
pause
goto end

:status
echo.
echo 📊 服务状态：
docker-compose ps
pause
goto end

:logs
echo.
echo 📋 查看日志 (Ctrl+C 退出)：
docker-compose logs -f
goto end

:rebuild
echo.
echo 🔨 重新构建并启动...
docker-compose down
docker-compose build --no-cache
docker-compose up -d
echo ✅ 重新构建完成
pause
goto end

:clean
echo.
set /p confirm="⚠️  警告: 这将删除所有容器和数据，确认吗? (yes/no): "
if /i "%confirm%"=="yes" (
    echo 🗑️  清理中...
    docker-compose down -v --rmi all
    echo ✅ 清理完成
) else (
    echo ❌ 已取消
)
pause
goto end

:backup
echo.
for /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c%%a%%b)
for /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
set BACKUP_FILE=backup_%mydate%_%mytime%.sql
echo 💾 备份数据库到 %BACKUP_FILE% ...
docker-compose exec -T mysql mysqldump -u root -p123456 charity_db > %BACKUP_FILE%
echo ✅ 备份完成: %BACKUP_FILE%
pause
goto end

:exit
echo 👋 再见！
exit /b 0

:end
endlocal
