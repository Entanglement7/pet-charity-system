-- 修改 exchange_items 表的 image 字段类型，支持 base64 编码的图片
USE charity_db;

ALTER TABLE exchange_items MODIFY COLUMN image TEXT;
