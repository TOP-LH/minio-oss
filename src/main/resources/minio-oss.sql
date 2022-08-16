-- --------------------------------------------------------
-- 主机:                           10.99.10.123
-- 服务器版本:                        5.7.35 - MySQL Community Server (GPL)
-- 服务器操作系统:                      Linux
-- HeidiSQL 版本:                  12.0.0.6468
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;


-- 导出 minio_oss 的数据库结构
DROP
DATABASE IF EXISTS `minio_oss`;
CREATE
DATABASE IF NOT EXISTS `minio_oss` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE
`minio_oss`;

-- 导出  表 minio_oss.upload_file_log 结构
DROP TABLE IF EXISTS `upload_file_log`;
CREATE TABLE IF NOT EXISTS `upload_file_log`
(
    `id` char
(
    20
) NOT NULL COMMENT '分布式唯一ID',
    `source_service` varchar
(
    255
) NOT NULL COMMENT '来源系统名称',
    `source_key` varchar
(
    50
) DEFAULT NULL COMMENT '来源系统ID',
    `login_code` varchar
(
    50
) NOT NULL COMMENT '创建人登录名',
    `zone_code` varchar
(
    5
) NOT NULL COMMENT '板块编码',
    `zone_name`           varchar(50)  NOT NULL COMMENT '板块名称',
    `target_category`     varchar(10)  NOT NULL COMMENT '文件分类(部门文件还是项目文件)',
    `salt`                varchar(10)  NOT NULL COMMENT '带盐值',
    `dept_code`           varchar(50)  DEFAULT NULL COMMENT '部门编码',
    `dept_name`           varchar(255) DEFAULT NULL COMMENT '部门名称',
    `project_code`        varchar(50)  DEFAULT NULL COMMENT '项目编码',
    `project_name`        varchar(255) DEFAULT NULL COMMENT '项目名称',
    `custom_name`         varchar(255) NOT NULL COMMENT '用户自定义文件名称',
    `file_name`           varchar(255) NOT NULL COMMENT '原始文件名称',
    `file_type`           varchar(100) NOT NULL COMMENT '文件类型',
    `file_size`           bigint(20)   NOT NULL COMMENT '文件大小',
    `first_level_folder`  varchar(255) NOT NULL COMMENT '一级文件夹名称',
    `second_level_folder` varchar(255) NOT NULL COMMENT '二级文件夹名称',
    `remark`              text COMMENT '备注',
    `create_time`         datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `delete_flag`         bit(1)       DEFAULT b'1' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_source_service` (`source_service`, `source_key`),
    KEY `idx_login_code` (`login_code`),
    KEY `idx_zone` (`zone_code`, `zone_name`),
    KEY `idx_target_category` (`target_category`),
    KEY `idx_delete_flag` (`delete_flag`),
    KEY `idx_dept` (`dept_code`, `dept_name`),
    KEY `idx_project` (`project_code`, `project_name`),
    KEY `idx_first_level_folder` (`first_level_folder`),
    KEY `idx_second_level_folder` (`second_level_folder`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='上传文件记录表';

-- 数据导出被取消选择。

/*!40103 SET TIME_ZONE = IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE = IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS = IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES = IFNULL(@OLD_SQL_NOTES, 1) */;
