# 修复兑换物品图片上传错误

## 问题描述
管理员修改可兑换物品图片时，接口 `PUT /api/points/exchange/item/{id}` 返回 500 错误。

## 根本原因
数据库中 `exchange_items` 表的 `image` 字段类型为 `VARCHAR(255)`，无法存储 base64 编码的图片数据（通常超过 10KB）。

## 修复内容

### 1. 后端代码改进
- 在 `PointsController.updateExchangeItem` 方法中添加了：
  - 物品存在性验证
  - 图片大小验证（限制 100KB）
  - 更新结果检查
  - 更详细的错误提示

### 2. 数据库架构更新
- 将 `exchange_items.image` 字段从 `VARCHAR(255)` 改为 `TEXT` 类型
- 支持存储更大的 base64 编码图片

## 应用修复

### 步骤 1: 停止后端服务
```bash
# 如果后端正在运行，先停止
```

### 步骤 2: 执行数据库迁移
```bash
mysql -h localhost -u root -p charity_db < backend/fix_exchange_items_image.sql
```

或者手动执行 SQL：
```sql
USE charity_db;
ALTER TABLE exchange_items MODIFY COLUMN image TEXT;
```

### 步骤 3: 启动更新后的后端
```bash
cd backend
java -jar target/charity-backend-1.0.0.jar
```

## 验证修复
1. 登录管理员账号
2. 进入"可兑换物品管理"页面
3. 编辑任意物品并上传图片
4. 确认保存成功，不再报 500 错误

## 注意事项
- 建议图片大小控制在 100KB 以内以获得最佳性能
- 如果需要支持更大的图片，可以考虑使用文件存储服务（如 OSS）而不是 base64 编码
