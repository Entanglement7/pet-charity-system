CREATE DATABASE IF NOT EXISTS charity_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE charity_db;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(64) NOT NULL UNIQUE,
    password    VARCHAR(128) NOT NULL,
    role        VARCHAR(32) NOT NULL COMMENT 'admin/user/organization',
    avatar      VARCHAR(255) DEFAULT '/default-avatar.png',
    introduction VARCHAR(255),
    deleted     TINYINT DEFAULT 0,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 机构表
CREATE TABLE IF NOT EXISTS organizations (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL UNIQUE,
    name            VARCHAR(128) NOT NULL,
    contact         VARCHAR(64),
    phone           VARCHAR(32),
    email           VARCHAR(128),
    description     TEXT,
    license_url     TEXT,
    status          TINYINT DEFAULT 0 COMMENT '0=待审核 1=已通过 2=已拒绝',
    reject_reason   VARCHAR(255),
    deleted         TINYINT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 机构地址表
CREATE TABLE IF NOT EXISTS organization_addresses (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    receiver        VARCHAR(64) NOT NULL,
    phone           VARCHAR(32) NOT NULL,
    province        VARCHAR(32),
    city            VARCHAR(32),
    district        VARCHAR(32),
    detail          VARCHAR(255),
    is_default      TINYINT DEFAULT 0,
    deleted         TINYINT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 捐赠表
CREATE TABLE IF NOT EXISTS donations (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    title           VARCHAR(128) NOT NULL,
    category        VARCHAR(64),
    quantity        INT DEFAULT 1,
    unit            VARCHAR(16),
    description     TEXT,
    images          TEXT COMMENT 'JSON数组，图片URL',
    status          TINYINT DEFAULT 0 COMMENT '0=待审核 1=审核通过 2=审核拒绝 3=已发货 4=已收货',
    reject_reason   VARCHAR(255),
    points_awarded  INT DEFAULT 0,
    deleted         TINYINT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 物流表（通用，关联类型+关联ID）
CREATE TABLE IF NOT EXISTS logistics (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    ref_type        VARCHAR(32) NOT NULL COMMENT 'donation/application/exchange',
    ref_id          BIGINT NOT NULL,
    company         VARCHAR(64),
    tracking_no     VARCHAR(128),
    status          TINYINT DEFAULT 0 COMMENT '0=待发货 1=已发货 2=已收货',
    sender_name     VARCHAR(64),
    sender_phone    VARCHAR(32),
    sender_address  VARCHAR(255),
    receiver_name   VARCHAR(64),
    receiver_phone  VARCHAR(32),
    receiver_address VARCHAR(255),
    remark          VARCHAR(255),
    shipped_at      DATETIME,
    received_at     DATETIME,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 可申领物资（从审核通过的捐赠转化）
CREATE TABLE IF NOT EXISTS items (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    donation_id     BIGINT NOT NULL,
    title           VARCHAR(128) NOT NULL,
    category        VARCHAR(64),
    quantity        INT DEFAULT 1,
    remaining       INT DEFAULT 1,
    unit            VARCHAR(16),
    description     TEXT,
    images          TEXT,
    deleted         TINYINT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 申领表
CREATE TABLE IF NOT EXISTS applications (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    item_id         BIGINT NOT NULL,
    quantity        INT DEFAULT 1,
    reason          TEXT,
    status          TINYINT DEFAULT 0 COMMENT '0=待审核 1=审核通过 2=审核拒绝 3=已发货 4=已收货',
    reject_reason   VARCHAR(255),
    deleted         TINYINT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 积分账户
CREATE TABLE IF NOT EXISTS points (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL UNIQUE,
    balance         INT DEFAULT 0,
    total_earned    INT DEFAULT 0,
    total_spent     INT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 积分流水
CREATE TABLE IF NOT EXISTS points_records (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    amount          INT NOT NULL COMMENT '正=收入 负=支出',
    type            VARCHAR(32) COMMENT 'donate/exchange',
    ref_id          BIGINT,
    description     VARCHAR(255),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 可兑换物品
CREATE TABLE IF NOT EXISTS exchange_items (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(128) NOT NULL,
    description     TEXT,
    image           TEXT,
    points_cost     INT NOT NULL,
    stock           INT DEFAULT 0,
    deleted         TINYINT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 兑换记录
CREATE TABLE IF NOT EXISTS exchange_records (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    item_id         BIGINT NOT NULL,
    quantity        INT DEFAULT 1,
    points_cost     INT NOT NULL,
    status          TINYINT DEFAULT 0 COMMENT '0=待发货 1=已发货 2=已收货',
    deleted         TINYINT DEFAULT 0,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 审核日志
CREATE TABLE IF NOT EXISTS audit_logs (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    auditor_id      BIGINT NOT NULL,
    ref_type        VARCHAR(32) NOT NULL COMMENT 'organization/donation/application',
    ref_id          BIGINT NOT NULL,
    action          VARCHAR(16) NOT NULL COMMENT 'approve/reject',
    reason          VARCHAR(255),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化管理员账号（密码: admin123，BCrypt加密）
INSERT IGNORE INTO users (username, password, role, introduction)
VALUES
    ('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'admin', '超级管理员'),
    ('user1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'user', '普通用户'),
    ('org1',  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'organization', '机构用户');

-- 初始化积分账户
INSERT IGNORE INTO points (user_id, balance)
SELECT id, 0 FROM users WHERE role = 'user';

-- 初始化可兑换物品
INSERT IGNORE INTO exchange_items (name, description, points_cost, stock)
VALUES
    ('保温杯', '不锈钢保温杯，500ml', 100, 50),
    ('帆布包', '环保帆布手提包', 80, 100),
    ('笔记本', '精装笔记本套装', 50, 200);
