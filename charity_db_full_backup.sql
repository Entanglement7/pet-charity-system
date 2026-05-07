-- MySQL dump 10.13  Distrib 8.4.8, for Win64 (x86_64)
--
-- Host: localhost    Database: charity_db
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `charity_db`
--

/*!40000 DROP DATABASE IF EXISTS `charity_db`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `charity_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `charity_db`;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `applications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `organization_id` bigint NOT NULL,
  `item_id` bigint NOT NULL,
  `quantity` int DEFAULT '1',
  `reason` text,
  `status` tinyint DEFAULT '0' COMMENT '0=待审核 1=审核通过 2=审核拒绝 3=已发货 4=已收货',
  `reject_reason` varchar(255) DEFAULT NULL,
  `usage_report` text COMMENT '使用情况说明',
  `usage_status` tinyint NOT NULL DEFAULT '0' COMMENT '0=未填写 1=待审核 2=已公示 3=已驳回',
  `usage_reject_reason` varchar(255) DEFAULT NULL COMMENT '使用情况驳回原因',
  `deleted` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `applications`
--

LOCK TABLES `applications` WRITE;
/*!40000 ALTER TABLE `applications` DISABLE KEYS */;
INSERT INTO `applications` VALUES (1,1,1,1,'1212',1,NULL,NULL,0,NULL,0,'2026-05-06 22:53:52','2026-05-06 22:53:52'),(2,1,1,1,'121',1,NULL,NULL,0,NULL,0,'2026-05-06 23:46:24','2026-05-06 23:46:24'),(3,1,2,3,'1121',1,NULL,NULL,0,NULL,0,'2026-05-06 23:46:32','2026-05-06 23:46:32'),(4,1,2,1,'w1212',4,NULL,'dajiahao2121212',2,NULL,0,'2026-05-06 23:53:04','2026-05-06 23:53:04');
/*!40000 ALTER TABLE `applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `auditor_id` bigint NOT NULL,
  `ref_type` varchar(32) NOT NULL COMMENT 'organization/donation/application',
  `ref_id` bigint NOT NULL,
  `action` varchar(16) NOT NULL COMMENT 'approve/reject',
  `reason` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
INSERT INTO `audit_logs` VALUES (1,1,'organization',1,'approve','资质审核通过','2026-05-06 22:37:21'),(2,1,'organization',2,'approve','资质审核通过','2026-05-06 22:37:21'),(3,1,'donation',1,'approve','物品符合捐赠标准','2026-05-06 22:37:21'),(4,1,'donation',2,'approve','物品符合捐赠标准','2026-05-06 22:37:21'),(5,1,'donation',3,'approve','物品符合捐赠标准','2026-05-06 22:37:21'),(6,1,'application',1,'approve',NULL,'2026-05-06 23:00:50'),(7,1,'application',3,'approve',NULL,'2026-05-06 23:46:46'),(8,1,'application',2,'approve',NULL,'2026-05-06 23:46:47'),(9,1,'application',4,'approve',NULL,'2026-05-06 23:53:44'),(10,1,'usage_report',4,'approve','','2026-05-07 00:00:17');
/*!40000 ALTER TABLE `audit_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `donations`
--

DROP TABLE IF EXISTS `donations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `donations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(128) NOT NULL,
  `category` varchar(64) DEFAULT NULL,
  `condition_level` varchar(32) DEFAULT NULL COMMENT '成色',
  `quantity` int DEFAULT '1',
  `unit` varchar(16) DEFAULT NULL,
  `description` text,
  `images` text COMMENT 'JSON数组，图片URL',
  `status` tinyint DEFAULT '0' COMMENT '0=待审核 1=审核通过 2=审核拒绝 3=已发货 4=已收货',
  `reject_reason` varchar(255) DEFAULT NULL,
  `points_awarded` int DEFAULT '0',
  `deleted` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `donations`
--

LOCK TABLES `donations` WRITE;
/*!40000 ALTER TABLE `donations` DISABLE KEYS */;
INSERT INTO `donations` VALUES (1,2,'二手宠物笼','宠物用品',NULL,2,'个','9成新宠物笼，尺寸60*40*50cm',NULL,1,NULL,20,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(2,2,'宠物玩具一批','宠物玩具',NULL,10,'个','各种宠物玩具，球、玩具老鼠等',NULL,1,NULL,30,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(3,2,'宠物食品','宠物食品',NULL,5,'袋','猫粮狗粮各半',NULL,1,NULL,25,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(4,2,'212','pet_snack','new',1,'3','1212','[\"data:image/png;base64,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\"]',0,NULL,0,0,'2026-05-06 23:12:29','2026-05-06 23:12:29'),(5,2,'121','pet_food','new',1,'','21212','[\"data:image/png;base64,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\"]',0,NULL,0,0,'2026-05-06 23:39:12','2026-05-06 23:39:12'),(6,2,'12121','pet_snack','new',1,'','1212','[\"data:image/png;base64,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\"]',0,NULL,0,0,'2026-05-07 16:03:39','2026-05-07 16:03:39');
/*!40000 ALTER TABLE `donations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exchange_items`
--

DROP TABLE IF EXISTS `exchange_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exchange_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `description` text,
  `image` text,
  `points_cost` int NOT NULL,
  `stock` int DEFAULT '0',
  `deleted` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exchange_items`
--

LOCK TABLES `exchange_items` WRITE;
/*!40000 ALTER TABLE `exchange_items` DISABLE KEYS */;
INSERT INTO `exchange_items` VALUES (1,'宠物自动喂食器','智能定时定量喂食器','data:image/png;base64,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',200,28,0,'2026-05-06 22:37:21','2026-05-06 23:38:23'),(2,'宠物牵引绳','舒适耐用牵引绳',NULL,80,99,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(3,'宠物玩具套装','含球、飞盘等玩具',NULL,50,150,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(4,'宠物尿垫','一次性尿垫60片装',NULL,30,200,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(5,'宠物食品罐头','营养均衡宠物罐头',NULL,40,180,0,'2026-05-06 22:37:21','2026-05-06 22:37:21');
/*!40000 ALTER TABLE `exchange_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exchange_records`
--

DROP TABLE IF EXISTS `exchange_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exchange_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `item_id` bigint NOT NULL,
  `quantity` int DEFAULT '1',
  `points_cost` int NOT NULL,
  `delivery_address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `status` tinyint DEFAULT '0' COMMENT '0=待发货 1=已发货 2=已收货',
  `deleted` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exchange_records`
--

LOCK TABLES `exchange_records` WRITE;
/*!40000 ALTER TABLE `exchange_records` DISABLE KEYS */;
INSERT INTO `exchange_records` VALUES (1,2,1,1,200,'张三 13800000000 北京市海淀区',0,0,'2026-05-06 23:17:20','2026-05-06 23:17:20'),(2,2,1,1,200,'212121',0,0,'2026-05-06 23:17:58','2026-05-06 23:17:58'),(3,2,2,1,80,'1212',0,0,'2026-05-07 16:03:49','2026-05-07 16:03:49');
/*!40000 ALTER TABLE `exchange_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `donation_id` bigint NOT NULL,
  `title` varchar(128) NOT NULL,
  `category` varchar(64) DEFAULT NULL,
  `quantity` int DEFAULT '1',
  `remaining` int DEFAULT '1',
  `unit` varchar(16) DEFAULT NULL,
  `description` text,
  `images` text,
  `deleted` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (1,1,'二手宠物笼','宠物用品',2,0,'个','9成新宠物笼，尺寸60*40*50cm',NULL,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(2,2,'宠物玩具一批','宠物玩具',10,4,'个','各种宠物玩具',NULL,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(3,3,'宠物食品','宠物食品',5,5,'袋','猫粮狗粮各半',NULL,0,'2026-05-06 22:37:21','2026-05-06 22:37:21');
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logistics`
--

DROP TABLE IF EXISTS `logistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `logistics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ref_type` varchar(32) NOT NULL COMMENT 'donation/application/exchange',
  `ref_id` bigint NOT NULL,
  `company` varchar(64) DEFAULT NULL,
  `tracking_no` varchar(128) DEFAULT NULL,
  `status` tinyint DEFAULT '0' COMMENT '0=待发货 1=已发货 2=已收货',
  `sender_name` varchar(64) DEFAULT NULL,
  `sender_phone` varchar(32) DEFAULT NULL,
  `sender_address` varchar(255) DEFAULT NULL,
  `receiver_name` varchar(64) DEFAULT NULL,
  `receiver_phone` varchar(32) DEFAULT NULL,
  `receiver_address` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `shipped_at` datetime DEFAULT NULL,
  `received_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logistics`
--

LOCK TABLES `logistics` WRITE;
/*!40000 ALTER TABLE `logistics` DISABLE KEYS */;
INSERT INTO `logistics` VALUES (1,'application',3,'顺丰速运','1212212',1,NULL,NULL,NULL,'1221','2121','2121',NULL,'2026-05-06 23:52:42',NULL,'2026-05-06 23:50:45','2026-05-06 23:52:42'),(2,'application',4,'中通快递','1',1,NULL,NULL,NULL,'1','11','1',NULL,'2026-05-06 23:56:56',NULL,'2026-05-06 23:54:02','2026-05-06 23:56:56');
/*!40000 ALTER TABLE `logistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_addresses`
--

DROP TABLE IF EXISTS `organization_addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `organization_addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `organization_id` bigint NOT NULL,
  `receiver` varchar(64) NOT NULL,
  `phone` varchar(32) NOT NULL,
  `province` varchar(32) DEFAULT NULL,
  `city` varchar(32) DEFAULT NULL,
  `district` varchar(32) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `is_default` tinyint DEFAULT '0',
  `deleted` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_addresses`
--

LOCK TABLES `organization_addresses` WRITE;
/*!40000 ALTER TABLE `organization_addresses` DISABLE KEYS */;
INSERT INTO `organization_addresses` VALUES (1,1,'张三','13800138001','北京市','北京市','朝阳区','朝阳区某街道123号',1,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(2,2,'李四','13800138002','上海市','上海市','浦东新区','浦东新区某路456号',1,0,'2026-05-06 22:37:21','2026-05-06 22:37:21');
/*!40000 ALTER TABLE `organization_addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organizations`
--

DROP TABLE IF EXISTS `organizations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `organizations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `name` varchar(128) NOT NULL,
  `contact` varchar(64) DEFAULT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `description` text,
  `license_url` text,
  `status` tinyint DEFAULT '0' COMMENT '0=待审核 1=已通过 2=已拒绝',
  `reject_reason` varchar(255) DEFAULT NULL,
  `deleted` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organizations`
--

LOCK TABLES `organizations` WRITE;
/*!40000 ALTER TABLE `organizations` DISABLE KEYS */;
INSERT INTO `organizations` VALUES (1,3,'爱心宠物救助站','张三','13800138001','love@pet.org','专门救助流浪宠物，提供医疗和领养服务','data:image/png;base64,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',1,NULL,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(2,4,'流浪动物保护协会','李四','13800138002','protect@animal.org','保护流浪动物，推广领养理念',NULL,1,NULL,0,'2026-05-06 22:37:21','2026-05-06 22:37:21');
/*!40000 ALTER TABLE `organizations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points`
--

DROP TABLE IF EXISTS `points`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `balance` int DEFAULT '0',
  `total_earned` int DEFAULT '0',
  `total_spent` int DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points`
--

LOCK TABLES `points` WRITE;
/*!40000 ALTER TABLE `points` DISABLE KEYS */;
INSERT INTO `points` VALUES (1,1,0,0,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(2,2,20,500,480,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(3,3,0,0,0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(4,4,0,0,0,'2026-05-06 22:37:21','2026-05-06 22:37:21');
/*!40000 ALTER TABLE `points` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `points_records`
--

DROP TABLE IF EXISTS `points_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `points_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `amount` int NOT NULL COMMENT '正=收入 负=支出',
  `type` varchar(32) DEFAULT NULL COMMENT 'donate/exchange',
  `ref_id` bigint DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `points_records`
--

LOCK TABLES `points_records` WRITE;
/*!40000 ALTER TABLE `points_records` DISABLE KEYS */;
INSERT INTO `points_records` VALUES (1,2,500,'donate',NULL,'新用户注册奖励','2026-05-06 22:37:21'),(3,2,-200,'exchange',1,'兑换: 宠物自动喂食器 x1','2026-05-06 23:17:20'),(4,2,-200,'exchange',1,'兑换: 宠物自动喂食器 x1','2026-05-06 23:17:58'),(5,2,-80,'exchange',2,'兑换: 宠物牵引绳 x1','2026-05-07 16:03:49');
/*!40000 ALTER TABLE `points_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password` varchar(128) NOT NULL,
  `role` varchar(32) NOT NULL COMMENT 'admin/user/organization',
  `avatar` varchar(255) DEFAULT '/default-avatar.png',
  `introduction` varchar(255) DEFAULT NULL,
  `deleted` tinyint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','admin','/default-avatar.png','系统管理员',0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(2,'testuser','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','user','/default-avatar.png','测试用户',0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(3,'org1','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','organization','/default-avatar.png','爱心宠物救助站',0,'2026-05-06 22:37:21','2026-05-06 22:37:21'),(4,'org2','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','organization','/default-avatar.png','流浪动物保护协会',0,'2026-05-06 22:37:21','2026-05-06 22:37:21');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'charity_db'
--

--
-- Dumping routines for database 'charity_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-07 16:04:48
