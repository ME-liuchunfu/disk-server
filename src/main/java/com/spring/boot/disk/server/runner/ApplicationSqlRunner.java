package com.spring.boot.disk.server.runner;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * description: sql执行器
 *
 * @author spring
 * date 2025/11/10 11:39
 * version 1.0
 */
@Slf4j
@Component
public class ApplicationSqlRunner implements ApplicationRunner {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createTable(getVmDiskFileSql());
        createTable(getVmDiskUserSql());
        createTable(getVmOperateLogSql());
        createTable(getVmUserFileSql());
    }

    private void createTable(String sql) {
        try {
            log.info("检测创建表:\n{}", sql);
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            log.error("创建sql表错误:\n{}", sql, e);
        }
    }

    private String getVmDiskFileSql() {
        return "CREATE TABLE IF NOT EXISTS `vm_disk_file` (\n" +
                "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  `stat_date` date DEFAULT NULL COMMENT '日期',\n" +
                "  `hash_value` varchar(255) NOT NULL COMMENT '哈希值',\n" +
                "  `file_name` varchar(255) NOT NULL COMMENT '文件名',\n" +
                "  `file_size` bigint unsigned NOT NULL DEFAULT '0' COMMENT '文件大小',\n" +
                "  `file_type` varchar(20) NOT NULL DEFAULT 'txt' COMMENT '文件类型',\n" +
                "  `path` varchar(3000) DEFAULT NULL COMMENT '路径',\n" +
                "  `pref_view_path` varchar(3000) DEFAULT NULL COMMENT '预览路径',\n" +
                "  `owner` varchar(64) DEFAULT NULL COMMENT '所有者',\n" +
                "  `create_time` datetime DEFAULT NULL COMMENT '创建时间',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `un_index` (`hash_value`) USING BTREE,\n" +
                "  KEY `common_index` (`stat_date`,`hash_value`,`file_name`,`file_type`) USING BTREE\n" +
                ") ENGINE=InnoDB COMMENT='文件库';";
    }

    private String getVmDiskUserSql() {
        return "CREATE TABLE IF NOT EXISTS `vm_disk_user` (\n" +
                "  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  `user_name` varchar(64) NOT NULL COMMENT '用户名',\n" +
                "  `user_nick` varchar(64) DEFAULT NULL COMMENT '昵称',\n" +
                "  `password` varchar(128) NOT NULL COMMENT '密码',\n" +
                "  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',\n" +
                "  `ac_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '状态',\n" +
                "  `create_time` datetime NOT NULL COMMENT '创建时间',\n" +
                "  PRIMARY KEY (`user_id`),\n" +
                "  UNIQUE KEY `un_index` (`user_name`) USING BTREE,\n" +
                "  KEY `common_index` (`user_name`,`user_nick`,`password`,`ac_status`) USING BTREE\n" +
                ") ENGINE=InnoDB COMMENT='用户信息表';";
    }

    private String getVmOperateLogSql() {
        return "CREATE TABLE IF NOT EXISTS `vm_operate_log` (\n" +
                "  `id` bigint NOT NULL COMMENT '主键',\n" +
                "  `invoke_method` varchar(128) DEFAULT NULL COMMENT '执行方法',\n" +
                "  `invoke_params` varchar(3000) DEFAULT NULL COMMENT '参数',\n" +
                "  `invoke_type` varchar(64) DEFAULT NULL COMMENT '操作类型',\n" +
                "  `owner` varchar(128) DEFAULT NULL COMMENT '操作人',\n" +
                "  `create_time` datetime DEFAULT NULL COMMENT '时间',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `common_index` (`invoke_method`,`invoke_type`,`owner`) USING BTREE\n" +
                ") ENGINE=InnoDB COMMENT='操作表';";
    }

    private String getVmUserFileSql() {
        return "CREATE TABLE IF NOT EXISTS  `vm_user_file` (\n" +
                "  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  `parent_id` bigint DEFAULT NULL COMMENT '父级',\n" +
                "  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '标题',\n" +
                "  `ref_file_id` bigint DEFAULT NULL COMMENT '关联文件id',\n" +
                "  `ref_avatar_file_id` bigint DEFAULT NULL COMMENT '关联头像文件id',\n" +
                "  `folder` tinyint(1) NOT NULL DEFAULT '1' COMMENT '目录',\n" +
                "  `create_time` datetime DEFAULT NULL COMMENT '创建时间',\n" +
                "  `owner` bigint NOT NULL COMMENT '所属人',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `un_index` (`parent_id`,`title`,`owner`) USING BTREE,\n" +
                "  KEY `common_index` (`title`,`ref_file_id`,`folder`,`owner`) USING BTREE\n" +
                ") ENGINE=InnoDB COMMENT='文件项';";
    }
}
