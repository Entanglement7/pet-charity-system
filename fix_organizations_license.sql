-- 修改 organizations 表的 license_url 字段类型，支持 base64 编码的图片
USE charity_db;

ALTER TABLE organizations MODIFY COLUMN license_url TEXT;
