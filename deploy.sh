#!/bin/bash

# 宠物慈善系统 Docker 一键部署脚本

set -e

echo "=========================================="
echo "  宠物慈善系统 Docker 部署"
echo "=========================================="
echo ""

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ 错误: Docker 未安装"
    echo "请先安装 Docker: https://docs.docker.com/get-docker/"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "❌ 错误: Docker Compose 未安装"
    echo "请先安装 Docker Compose: https://docs.docker.com/compose/install/"
    exit 1
fi

echo "✅ Docker 和 Docker Compose 已安装"
echo ""

# 显示菜单
echo "请选择操作："
echo "1) 启动所有服务"
echo "2) 停止所有服务"
echo "3) 重启所有服务"
echo "4) 查看服务状态"
echo "5) 查看日志"
echo "6) 重新构建并启动"
echo "7) 清理所有容器和数据"
echo "8) 备份数据库"
echo "9) 退出"
echo ""

read -p "请输入选项 (1-9): " choice

case $choice in
    1)
        echo ""
        echo "🚀 启动所有服务..."
        docker-compose up -d
        echo ""
        echo "✅ 服务启动成功！"
        echo ""
        echo "访问地址："
        echo "  前端: http://localhost"
        echo "  后端: http://localhost:8080"
        echo ""
        echo "查看日志: docker-compose logs -f"
        ;;
    2)
        echo ""
        echo "🛑 停止所有服务..."
        docker-compose stop
        echo "✅ 服务已停止"
        ;;
    3)
        echo ""
        echo "🔄 重启所有服务..."
        docker-compose restart
        echo "✅ 服务已重启"
        ;;
    4)
        echo ""
        echo "📊 服务状态："
        docker-compose ps
        ;;
    5)
        echo ""
        echo "📋 查看日志 (Ctrl+C 退出)："
        docker-compose logs -f
        ;;
    6)
        echo ""
        echo "🔨 重新构建并启动..."
        docker-compose down
        docker-compose build --no-cache
        docker-compose up -d
        echo "✅ 重新构建完成"
        ;;
    7)
        echo ""
        read -p "⚠️  警告: 这将删除所有容器和数据，确认吗? (yes/no): " confirm
        if [ "$confirm" = "yes" ]; then
            echo "🗑️  清理中..."
            docker-compose down -v --rmi all
            echo "✅ 清理完成"
        else
            echo "❌ 已取消"
        fi
        ;;
    8)
        echo ""
        BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
        echo "💾 备份数据库到 $BACKUP_FILE ..."
        docker-compose exec -T mysql mysqldump -u root -p123456 charity_db > "$BACKUP_FILE"
        echo "✅ 备份完成: $BACKUP_FILE"
        ;;
    9)
        echo "👋 再见！"
        exit 0
        ;;
    *)
        echo "❌ 无效选项"
        exit 1
        ;;
esac
