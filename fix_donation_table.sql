-- 修复 donations 表缺少 condition_level 字段的问题
-- 执行方式: mysql -u root -p charity_db < fix_donation_table.sql

USE charity_db;

-- 添加 condition_level 字段
ALTER TABLE donations
ADD COLUMN condition_level VARCHAR(32)
COMMENT '成色：new=全新, like_new=九成新, good=八成新, fair=七成新'
AFTER category;

-- 为已有数据设置默认值
UPDATE donations SET condition_level = 'good' WHERE condition_level IS NULL;

SELECT 'donations 表已成功添加 condition_level 字段' AS result;
