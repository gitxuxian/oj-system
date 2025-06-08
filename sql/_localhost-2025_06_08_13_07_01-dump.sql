-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: my_db
-- ------------------------------------------------------
-- Server version	8.0.34

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
-- Table structure for table `creator_application`
--

DROP TABLE IF EXISTS `creator_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creator_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '申请用户ID',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `reason` text NOT NULL COMMENT '申请理由',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态 0-待审核 1-已通过 2-已拒绝',
  `create_time` datetime NOT NULL COMMENT '申请时间',
  `judge_time` datetime DEFAULT NULL COMMENT '审核时间',
  `judge_user_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `judge_username` varchar(100) DEFAULT NULL COMMENT '审核人用户名',
  `judge_comment` text COMMENT '审核备注',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='出题者权限申请表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creator_application`
--

LOCK TABLES `creator_application` WRITE;
/*!40000 ALTER TABLE `creator_application` DISABLE KEYS */;
INSERT INTO `creator_application` VALUES (1,1919326111829139457,'develop_01','我想要成为出题人，为了OJ平台的发展',0,'2025-05-16 19:47:41',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `creator_application` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game`
--

DROP TABLE IF EXISTS `game`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '比赛id',
  `gameName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '比赛名称',
  `gameDate` datetime NOT NULL COMMENT '比赛开始日期和时间',
  `durationMinutes` int DEFAULT NULL COMMENT '比赛总时间（分钟）',
  `rules` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '比赛规则',
  `awards` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '比赛奖励',
  `creatorUserId` bigint DEFAULT NULL COMMENT '创建比赛的用户id',
  `type` tinyint DEFAULT '0' COMMENT '比赛类型（例如 0: ACM, 1: OI, 2: 其他）',
  `status` tinyint DEFAULT '0' COMMENT '比赛状态（例如 0: 未开始, 1: 进行中, 2: 已结束, 3: 已归档）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '比赛密码（如有）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '比赛描述',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_creatorUserId` (`creatorUserId`) USING BTREE,
  KEY `idx_gameDate` (`gameDate`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='比赛信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game`
--

LOCK TABLES `game` WRITE;
/*!40000 ALTER TABLE `game` DISABLE KEYS */;
INSERT INTO `game` VALUES (1,'2025春季算法编程挑战赛','2025-03-15 22:00:00',240,'### 比赛规则\n1. 比赛时长为4小时。\n2. 采用ACM赛制，根据解题数量和罚时进行排名。\n3. 禁止任何形式的作弊行为，一经发现取消比赛资格。\n4. 允许使用C, C++, Java, Python语言提交。\n5. 详细规则请参考比赛页面的公告。','### 比赛奖励\n- 一等奖（前5%）：荣誉证书 + 价值500元的技术书籍\n- 二等奖（前15%）：荣誉证书 + 价值200元的技术书籍\n- 三等奖（前30%）：荣誉证书\n- 所有成功解出一题的参赛者均可获得参与奖（电子版参与证明）。',1919326111829139457,0,0,NULL,'欢迎参加2025年春季算法编程挑战赛！本次比赛旨在提高大家的算法设计与编程实现能力，题目难度适中，覆盖常见算法考点。祝大家比赛顺利，取得好成绩！','2025-05-17 15:51:33','2025-05-17 15:52:18',1),(2,'2025春季算法编程挑战赛','2025-03-15 22:00:00',240,'### 比赛规则\n1. 比赛时长为4小时。\n2. 采用ACM赛制，根据解题数量和罚时进行排名。\n3. 禁止任何形式的作弊行为，一经发现取消比赛资格。\n4. 允许使用C, C++, Java, Python语言提交。\n5. 详细规则请参考比赛页面的公告。','### 比赛奖励\n- 一等奖（前5%）：荣誉证书 + 价值500元的技术书籍\n- 二等奖（前15%）：荣誉证书 + 价值200元的技术书籍\n- 三等奖（前30%）：荣誉证书\n- 所有成功解出一题的参赛者均可获得参与奖（电子版参与证明）。',1919326111829139457,0,0,NULL,'欢迎参加2025年春季算法编程挑战赛！本次比赛旨在提高大家的算法设计与编程实现能力，题目难度适中，覆盖常见算法考点。祝大家比赛顺利，取得好成绩！','2025-05-17 15:52:37','2025-05-17 15:52:37',0),(3,'2025春季算法编程挑战赛','2025-06-15 22:00:00',240,'### 比赛规则\n1. 比赛时长为4小时。\n2. 采用ACM赛制，根据解题数量和罚时进行排名。\n3. 禁止任何形式的作弊行为，一经发现取消比赛资格。\n4. 允许使用C, C++, Java, Python语言提交。\n5. 详细规则请参考比赛页面的公告。','### 比赛奖励\n- 一等奖（前5%）：荣誉证书 + 价值500元的技术书籍\n- 二等奖（前15%）：荣誉证书 + 价值200元的技术书籍\n- 三等奖（前30%）：荣誉证书\n- 所有成功解出一题的参赛者均可获得参与奖（电子版参与证明）。',1919326111829139457,0,0,NULL,'欢迎参加2025年春季算法编程挑战赛！本次比赛旨在提高大家的算法设计与编程实现能力，题目难度适中，覆盖常见算法考点。祝大家比赛顺利，取得好成绩！','2025-05-17 16:16:33','2025-05-17 16:16:52',1);
/*!40000 ALTER TABLE `game` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_question`
--

DROP TABLE IF EXISTS `game_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联id',
  `gameId` bigint NOT NULL COMMENT '比赛id',
  `questionId` bigint NOT NULL COMMENT '题目id',
  `displayOrder` int DEFAULT '0' COMMENT '题目在比赛中的显示顺序或编号',
  `score` int DEFAULT '100' COMMENT '该题目在本次比赛中的分值 (适用于OI等赛制)',
  `titleAlias` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '题目在比赛中的别名或自定义标题 (例如 A, B, C)',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uq_game_question` (`gameId`,`questionId`) USING BTREE COMMENT '确保同一比赛同一题目不重复关联',
  KEY `idx_gameId_gq` (`gameId`) USING BTREE,
  KEY `idx_questionId_gq` (`questionId`) USING BTREE,
  CONSTRAINT `game_question_ibfk_1` FOREIGN KEY (`gameId`) REFERENCES `game` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `game_question_ibfk_2` FOREIGN KEY (`questionId`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='比赛与题目关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_question`
--

LOCK TABLES `game_question` WRITE;
/*!40000 ALTER TABLE `game_question` DISABLE KEYS */;
INSERT INTO `game_question` VALUES (1,2,1923362818250379264,1,60,'两数之和','2025-05-17 17:32:07','2025-05-17 17:32:07');
/*!40000 ALTER TABLE `game_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_rank`
--

DROP TABLE IF EXISTS `game_rank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_rank` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `gameId` bigint DEFAULT NULL COMMENT '竞赛id',
  `userId` bigint DEFAULT NULL COMMENT '用户id',
  `userName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户昵称',
  `totalMemory` int DEFAULT NULL COMMENT '总空间（kb）',
  `totalTime` int DEFAULT NULL COMMENT '总用时（ms）',
  `totalScore` int DEFAULT NULL COMMENT '总得分',
  `gameDetail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '竞赛详情',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_gameId` (`gameId`) USING BTREE,
  KEY `idx_userId` (`userId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_rank`
--

LOCK TABLES `game_rank` WRITE;
/*!40000 ALTER TABLE `game_rank` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_rank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `tags` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签列表（json 数组）',
  `thumbNum` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `favourNum` int NOT NULL DEFAULT '0' COMMENT '收藏数',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_favour`
--

DROP TABLE IF EXISTS `post_favour`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_favour` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint NOT NULL COMMENT '帖子 id',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_postId` (`postId`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子收藏';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_favour`
--

LOCK TABLES `post_favour` WRITE;
/*!40000 ALTER TABLE `post_favour` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_favour` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_thumb`
--

DROP TABLE IF EXISTS `post_thumb`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_thumb` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint NOT NULL COMMENT '帖子 id',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_postId` (`postId`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子点赞';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_thumb`
--

LOCK TABLES `post_thumb` WRITE;
/*!40000 ALTER TABLE `post_thumb` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_thumb` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `tags` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签列表（json 数组）',
  `answer` text COLLATE utf8mb4_unicode_ci COMMENT '题目答案',
  `submitNum` int NOT NULL DEFAULT '0' COMMENT '题目提交数',
  `acceptedNum` int NOT NULL DEFAULT '0' COMMENT '题目通过数',
  `judgeCase` text COLLATE utf8mb4_unicode_ci COMMENT '判题用例（json 数组）',
  `judgeConfig` text COLLATE utf8mb4_unicode_ci COMMENT '判题配置（json 对象）',
  `throughRate` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通过率',
  `favourNum` int NOT NULL DEFAULT '0' COMMENT '收藏数',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  `acm` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1923362818250379265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (1919334236691763200,'意义','1 3','[\"数组\"]','1 2',0,0,'[{\"input\":\"1 2\",\"output\":\"4 3\"}]','{\"timeLimit\":40,\"memoryLimit\":30,\"stackLimit\":50}',NULL,0,1919326111829139457,'2025-05-05 18:11:56','2025-05-16 20:59:27',1,0),(1919335116887429120,'','123123','[]','123123',0,0,'[{\"input\":\"123123123\",\"output\":\"123123123\"}]','{\"timeLimit\":50,\"memoryLimit\":60,\"stackLimit\":40}',NULL,0,1919326111829139457,'2025-05-05 18:15:26','2025-05-16 20:59:27',1,0),(1919748327486918656,'两数之和','1 2','[\"基础算法\"]','3',0,0,'[{\"input\":\"5 6\",\"output\":\"11\"},{\"input\":\"-3 7\",\"output\":\"4\"},{\"input\":\"0 0\",\"output\":\"0\"},{\"input\":\"123 456\",\"output\":\"579\"}]','{\"timeLimit\":100,\"memoryLimit\":128,\"stackLimit\":128}',NULL,0,1919326111829139457,'2025-05-06 21:37:23','2025-05-16 20:59:27',0,1),(1923362818250379264,'A+B Problem','### 题目描述\n\n给定两个整数A和B，计算A+B的和。\n\n### 输入格式\n\n输入包含多组测试数据。\n每组测试数据占一行，包含两个整数A和B（-10<sup>9</sup> ≤ A, B ≤ 10<sup>9</sup>）。\n\n### 输出格式\n\n对于每组输入，输出A+B的和，每组输出占一行。\n\n### 样例输入\n\n```\n1 5\n10 20\n```\n\n### 样例输出\n\n```\n6\n30\n```\n','[\"入门\",\"简单\",\"A+B\"]','A+B的和',7,0,'[{\"input\":\"1 5\",\"output\":\"6\"},{\"input\":\"10 20\",\"output\":\"30\"},{\"input\":\"-1 1\",\"output\":\"0\"},{\"input\":\"1000000000 1000000000\",\"output\":\"2000000000\"},{\"input\":\"-1000000000 -1000000000\",\"output\":\"-2000000000\"}]','{\"timeLimit\":1000,\"memoryLimit\":128,\"stackLimit\":64}',NULL,0,1919326111829139457,'2025-05-16 21:00:05','2025-05-16 22:35:48',0,1);
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_submit`
--

DROP TABLE IF EXISTS `question_submit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_submit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `language` varchar(128) NOT NULL COMMENT '编程语言',
  `code` text NOT NULL COMMENT '用户代码',
  `judgeInfo` text COMMENT '判题信息（json 对象）',
  `status` int NOT NULL DEFAULT '1' COMMENT '判题状态（1 - 排队中, 2 - 处理中, 3 - 通过, 4 - 答案错误, 5 - 时间超限, 6 - 编译错误, 7 - 运行时错误 (段错误), 8 - 运行时错误 (文件大小超限), 9 - 运行时错误 (浮点异常), 10 - 运行时错误 (中止), 11 - 运行时错误 (非零退出码), 12 - 运行时错误 (其他), 13 - 内部错误, 14 - 执行格式错误）',
  `questionId` bigint NOT NULL COMMENT '题目 id',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_questionId` (`questionId`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题目提交';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_submit`
--

LOCK TABLES `question_submit` WRITE;
/*!40000 ALTER TABLE `question_submit` DISABLE KEYS */;
INSERT INTO `question_submit` VALUES (2,'java','public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',0,1919748327486918656,1919326111829139457,'2025-05-06 21:41:24','2025-05-06 21:41:24',0),(3,'java','public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',0,1919748327486918656,1919326111829139457,'2025-05-06 21:50:10','2025-05-06 21:50:10',0),(4,'java','public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-06 21:54:04','2025-05-06 22:10:02',0),(5,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-06 22:15:53','2025-05-06 22:15:53',0),(6,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-06 22:23:35','2025-05-06 22:23:35',0),(7,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 20:29:02','2025-05-11 20:29:02',0),(8,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 20:32:12','2025-05-11 20:32:12',0),(9,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 20:43:34','2025-05-11 20:43:34',0),(10,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 20:46:22','2025-05-11 20:46:23',0),(11,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 21:15:37','2025-05-11 21:15:38',0),(12,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 21:23:23','2025-05-11 21:23:24',0),(13,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 21:24:54','2025-05-11 21:24:54',0),(14,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 21:28:07','2025-05-11 21:28:07',0),(15,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 21:37:06','2025-05-11 21:37:06',0),(16,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 21:40:14','2025-05-11 21:40:14',0),(17,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 21:42:32','2025-05-11 21:42:32',0),(18,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 21:54:23','2025-05-11 21:54:23',0),(19,'java','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',1,1919748327486918656,1919326111829139457,'2025-05-11 22:02:01','2025-05-11 22:02:02',0),(20,'Java (OpenJDK 13.0.1)','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-14 20:36:24','2025-05-14 20:36:25',0),(21,'Java (OpenJDK 13.0.1)','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-14 20:39:03','2025-05-14 20:39:03',0),(22,'Java (OpenJDK 13.0.1)','import java.util.Scanner;\n public class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n        System.out.println(a + b);\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-14 20:50:00','2025-05-14 20:50:00',0),(23,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n        int b = scanner.nextInt();\n        System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-14 21:03:14','2025-05-14 21:03:14',0),(24,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-14 21:07:38','2025-05-14 21:07:38',0),(25,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-14 21:16:42','2025-05-14 21:17:02',0),(26,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-14 21:43:58','2025-05-14 21:43:58',0),(27,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-14 21:45:38','2025-05-14 21:45:47',0),(28,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-14 22:01:34','2025-05-14 22:01:34',0),(29,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-14 22:14:01','2025-05-14 22:14:01',0),(30,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-16 15:06:03','2025-05-16 15:06:03',0),(31,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-16 15:07:58','2025-05-16 15:07:58',0),(32,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-16 15:18:24','2025-05-16 15:18:24',0),(33,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-16 15:22:21','2025-05-16 15:22:32',0),(34,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-16 15:32:32','2025-05-16 15:32:46',0),(35,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-16 16:10:38','2025-05-16 16:11:32',0),(36,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{}',2,1919748327486918656,1919326111829139457,'2025-05-16 17:00:11','2025-05-16 17:00:33',0),(37,'Java (OpenJDK 13.0.1)','public class Main {\n    public static void main(String[] args) {\n       int a = Integer.parseInt(args[0]);\n         int b = Integer.parseInt(args[1]);\n         System.out.println((a + b));\n    }\n}','{\"message\":\"内存溢出\",\"memory\":69042176,\"time\":276}',3,1919748327486918656,1919326111829139457,'2025-05-16 17:09:19','2025-05-16 17:24:46',0),(38,'C (GCC 9.2.0)','#include <iostream>\nint main() {\n    long long a, b; \n    while (std::cin >> a >> b) { \n        std::cout << a + b << std::endl; \n    }\n    return 0;\n}','{}',2,1923362818250379264,1919326111829139457,'2025-05-16 21:07:26','2025-05-16 21:07:54',0),(39,'C (GCC 9.2.0)','#include <iostream>\nint main() {\n    long long a, b; \n    while (std::cin >> a >> b) { \n        std::cout << a + b << std::endl; \n    }\n    return 0;\n}','{}',2,1923362818250379264,1919326111829139457,'2025-05-16 21:21:46','2025-05-16 21:21:46',0),(40,'C++ (GCC 9.2.0)','#include <iostream>\nint main() {\n    long long a, b; \n    while (std::cin >> a >> b) { \n        std::cout << a + b << std::endl; \n    }\n    return 0;\n}','{}',2,1923362818250379264,1919326111829139457,'2025-05-16 21:25:05','2025-05-16 21:25:05',0),(41,'C++ (GCC 9.2.0)','#include <iostream>\nint main() {\n    long long a, b; \n    while (std::cin >> a >> b) { \n        std::cout << a + b << std::endl; \n    }\n    return 0;\n}','{}',2,1923362818250379264,1919326111829139457,'2025-05-16 22:31:18','2025-05-16 22:31:18',0),(42,'C++ (GCC 9.2.0)','#include <iostream>\nint main() {\n    long long a, b; \n    while (std::cin >> a >> b) { \n        std::cout << a + b << std::endl; \n    }\n    return 0;\n}','{}',2,1923362818250379264,1919326111829139457,'2025-05-16 22:33:03','2025-05-16 22:33:17',0),(43,'C++ (GCC 9.2.0)','#include <iostream>\nint main() {\n    long long a, b; \n    while (std::cin >> a >> b) { \n        std::cout << a + b << std::endl; \n    }\n    return 0;\n}','{\"message\":\"内存溢出\",\"memory\":576,\"time\":3,\"detail\":\"Memory Limit Exceeded\"}',3,1923362818250379264,1919326111829139457,'2025-05-16 22:34:31','2025-05-16 22:34:34',0),(44,'C++ (GCC 9.2.0)','#include <iostream>\nint main() {\n    long long a, b; \n    while (std::cin >> a >> b) { \n        std::cout << a + b << std::endl; \n    }\n    return 0;\n}','{\"message\":\"内存溢出\",\"memory\":576,\"time\":3,\"detail\":\"Memory Limit Exceeded\"}',3,1923362818250379264,1919326111829139457,'2025-05-16 22:35:47','2025-05-16 22:39:05',0);
/*!40000 ALTER TABLE `question_submit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userAccount` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `userPassword` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `unionId` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信开放平台id',
  `mpOpenId` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号openId',
  `userName` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
  `userAvatar` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
  `userProfile` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户简介',
  `userRole` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  `email` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '电子邮箱',
  `phone` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '电话号码',
  PRIMARY KEY (`id`),
  KEY `idx_unionId` (`unionId`)
) ENGINE=InnoDB AUTO_INCREMENT=1919326111829139458 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1919326111829139457,'1919326111796322304','e156c79f90eb2ca4c9ccc28e25b76f27',NULL,NULL,'develop_01',NULL,NULL,'admin','2025-05-05 17:39:39','2025-05-05 18:37:44',0,'2351617301@qq.com','18196326476');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_game`
--

DROP TABLE IF EXISTS `user_game`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_game` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联id',
  `userId` bigint NOT NULL COMMENT '用户id',
  `gameId` bigint NOT NULL COMMENT '比赛id',
  `registrationTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名或参赛时间',
  `status` tinyint DEFAULT '0' COMMENT '参赛状态（例如 0: 已报名, 1: 已参加, 2: 已结束, 3: 缺席）',
  `isOfficial` tinyint DEFAULT '1' COMMENT '是否为正式参赛者 (区分打星/打铁用户, 1: 是, 0: 否)',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uq_user_game` (`userId`,`gameId`) USING BTREE COMMENT '确保同一用户不重复参加同一比赛',
  KEY `idx_userId_ug` (`userId`) USING BTREE,
  KEY `idx_gameId_ug` (`gameId`) USING BTREE,
  CONSTRAINT `user_game_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user_game_ibfk_2` FOREIGN KEY (`gameId`) REFERENCES `game` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户参赛记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_game`
--

LOCK TABLES `user_game` WRITE;
/*!40000 ALTER TABLE `user_game` DISABLE KEYS */;
INSERT INTO `user_game` VALUES (1,1919326111829139457,2,'2025-05-17 17:53:55',0,1,'2025-05-17 17:53:55','2025-05-17 17:53:55');
/*!40000 ALTER TABLE `user_game` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-08 13:07:01
