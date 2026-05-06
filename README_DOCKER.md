# 🐾 宠物慈善系统 - Docker 快速部署

## 📦 一键部署

### Windows 用户
双击运行 `deploy.bat`，选择"启动所有服务"

### Linux/Mac 用户
```bash
./deploy.sh
```
选择"启动所有服务"

## 🚀 手动部署

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

## 🌐 访问地址

启动成功后，访问：

- **前端**: http://localhost
- **后端 API**: http://localhost:8080
- **数据库**: localhost:3306

## 📋 默认配置

- **MySQL 用户名**: root
- **MySQL 密码**: 123456
- **数据库名**: charity_db

## 🛠️ 常用命令

```bash
# 停止服务
docker-compose stop

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f backend
docker-compose logs -f frontend

# 进入容器
docker-compose exec backend bash
docker-compose exec mysql mysql -u root -p123456

# 备份数据库
docker-compose exec mysql mysqldump -u root -p123456 charity_db > backup.sql

# 清理所有
docker-compose down -v
```

## 📝 服务说明

### 容器列表
- `charity-mysql` - MySQL 8.0 数据库
- `charity-backend` - Spring Boot 后端服务
- `charity-frontend` - Vue + Nginx 前端服务

### 端口映射
- `80` → 前端 Nginx
- `8080` → 后端 Spring Boot
- `3306` → MySQL 数据库

### 数据持久化
MySQL 数据存储在 Docker volume `mysql-data` 中，删除容器不会丢失数据。

## ⚠️ 注意事项

1. **首次启动需要等待** - 数据库初始化和后端启动需要 1-2 分钟
2. **端口占用** - 确保 80、8080、3306 端口未被占用
3. **内存要求** - 建议至少 4GB 可用内存
4. **Docker 版本** - 需要 Docker 20.10+ 和 Docker Compose 2.0+

## 🔧 故障排查

### 服务无法启动
```bash
# 查看详细日志
docker-compose logs

# 检查端口占用
netstat -ano | findstr "80"
netstat -ano | findstr "8080"
netstat -ano | findstr "3306"
```

### 数据库连接失败
```bash
# 检查 MySQL 是否就绪
docker-compose exec mysql mysqladmin ping -h localhost -u root -p123456

# 重启 MySQL
docker-compose restart mysql
```

### 前端无法访问后端
```bash
# 检查后端健康状态
curl http://localhost:8080/actuator/health

# 查看后端日志
docker-compose logs backend
```

## 📚 详细文档

查看 [DOCKER_DEPLOY.md](DOCKER_DEPLOY.md) 获取完整部署文档。

## 🎯 快速测试

启动成功后：

1. 访问 http://localhost
2. 使用默认账号登录（查看数据库初始化脚本）
3. 测试各项功能

## 🔄 更新部署

```bash
# 拉取最新代码
git pull

# 重新构建并启动
docker-compose up -d --build
```

## 🗑️ 完全清理

```bash
# 停止并删除所有容器、网络、卷
docker-compose down -v

# 删除镜像
docker-compose down --rmi all
```

---

**技术栈**: Vue 2 + Element UI + Spring Boot 3 + MySQL 8 + Docker
