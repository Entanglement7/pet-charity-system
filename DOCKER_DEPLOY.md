# 宠物慈善系统 Docker 部署指南

## 项目结构

```
pet-charity-system/
├── backend/                 # 后端服务
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── src/                     # 前端源码
├── public/
├── package.json
├── Dockerfile              # 前端 Dockerfile
├── nginx.conf              # Nginx 配置
├── docker-compose.yml      # Docker Compose 配置
└── .dockerignore
```

## 快速开始

### 1. 前置要求

- Docker 20.10+
- Docker Compose 2.0+

### 2. 一键启动

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 3. 访问系统

- **前端地址**: http://localhost
- **后端 API**: http://localhost:8080
- **MySQL**: localhost:3306

### 4. 默认账号

系统会自动初始化数据库，默认账号请查看 `schema.sql` 中的初始化数据。

## 服务说明

### MySQL 数据库
- **容器名**: charity-mysql
- **端口**: 3306
- **数据库**: charity_db
- **用户名**: root
- **密码**: 123456
- **数据持久化**: 使用 Docker volume `mysql-data`

### 后端服务
- **容器名**: charity-backend
- **端口**: 8080
- **技术栈**: Spring Boot 3.2.5 + MyBatis-Plus + MySQL
- **健康检查**: /actuator/health

### 前端服务
- **容器名**: charity-frontend
- **端口**: 80
- **技术栈**: Vue 2 + Element UI + Nginx
- **API 代理**: /dev-api/ → http://backend:8080/

## 常用命令

### 启动服务
```bash
# 启动所有服务
docker-compose up -d

# 启动指定服务
docker-compose up -d mysql
docker-compose up -d backend
docker-compose up -d frontend
```

### 停止服务
```bash
# 停止所有服务
docker-compose stop

# 停止指定服务
docker-compose stop backend
```

### 重启服务
```bash
# 重启所有服务
docker-compose restart

# 重启指定服务
docker-compose restart backend
```

### 查看日志
```bash
# 查看所有服务日志
docker-compose logs -f

# 查看指定服务日志
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

### 进入容器
```bash
# 进入后端容器
docker-compose exec backend bash

# 进入前端容器
docker-compose exec frontend sh

# 进入 MySQL 容器
docker-compose exec mysql bash
```

### 重新构建
```bash
# 重新构建所有服务
docker-compose build

# 重新构建指定服务
docker-compose build backend
docker-compose build frontend

# 重新构建并启动
docker-compose up -d --build
```

### 清理
```bash
# 停止并删除容器
docker-compose down

# 停止并删除容器、网络、卷
docker-compose down -v

# 删除所有相关镜像
docker-compose down --rmi all
```

## 数据库管理

### 备份数据库
```bash
docker-compose exec mysql mysqldump -u root -p123456 charity_db > backup.sql
```

### 恢复数据库
```bash
docker-compose exec -T mysql mysql -u root -p123456 charity_db < backup.sql
```

### 连接数据库
```bash
docker-compose exec mysql mysql -u root -p123456 charity_db
```

## 环境变量配置

可以创建 `.env` 文件来覆盖默认配置：

```env
# MySQL 配置
MYSQL_ROOT_PASSWORD=your_password
MYSQL_DATABASE=charity_db

# 后端配置
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/charity_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# 端口配置
FRONTEND_PORT=80
BACKEND_PORT=8080
MYSQL_PORT=3306
```

## 生产环境部署建议

### 1. 修改默认密码
修改 `docker-compose.yml` 中的 MySQL 密码：
```yaml
environment:
  MYSQL_ROOT_PASSWORD: your_secure_password
```

### 2. 使用外部数据库
如果使用外部 MySQL，注释掉 mysql 服务，修改后端环境变量：
```yaml
backend:
  environment:
    SPRING_DATASOURCE_URL: jdbc:mysql://your-mysql-host:3306/charity_db
    SPRING_DATASOURCE_USERNAME: your_username
    SPRING_DATASOURCE_PASSWORD: your_password
```

### 3. 配置域名和 HTTPS
修改 `nginx.conf`，添加 SSL 配置：
```nginx
server {
    listen 443 ssl;
    server_name your-domain.com;
    
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    
    # ... 其他配置
}
```

### 4. 资源限制
在 `docker-compose.yml` 中添加资源限制：
```yaml
backend:
  deploy:
    resources:
      limits:
        cpus: '2'
        memory: 2G
      reservations:
        cpus: '1'
        memory: 1G
```

### 5. 日志管理
配置日志驱动：
```yaml
backend:
  logging:
    driver: "json-file"
    options:
      max-size: "10m"
      max-file: "3"
```

## 故障排查

### 后端无法连接数据库
```bash
# 检查 MySQL 是否启动
docker-compose ps mysql

# 查看 MySQL 日志
docker-compose logs mysql

# 检查网络连接
docker-compose exec backend ping mysql
```

### 前端无法访问后端 API
```bash
# 检查后端是否启动
docker-compose ps backend

# 查看后端日志
docker-compose logs backend

# 测试后端健康检查
curl http://localhost:8080/actuator/health
```

### 数据库初始化失败
```bash
# 删除数据卷重新初始化
docker-compose down -v
docker-compose up -d
```

## 性能优化

### 1. 使用多阶段构建
Dockerfile 已使用多阶段构建，减小镜像体积。

### 2. 利用构建缓存
修改代码后，Docker 会利用缓存加速构建。

### 3. 数据库连接池
后端已配置 HikariCP 连接池，默认配置在 `application.yml`。

### 4. Nginx 静态资源缓存
`nginx.conf` 已配置静态资源缓存策略。

## 监控和维护

### 查看资源使用情况
```bash
docker stats
```

### 查看容器详情
```bash
docker-compose ps
docker inspect charity-backend
```

### 更新服务
```bash
# 拉取最新代码
git pull

# 重新构建并启动
docker-compose up -d --build
```

## 技术支持

如有问题，请查看：
1. 容器日志：`docker-compose logs -f`
2. 后端日志：`docker-compose logs backend`
3. 数据库日志：`docker-compose logs mysql`

## 许可证

本项目仅供学习和研究使用。
