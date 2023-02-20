/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

CREATE TABLE `tb_record_task_template` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `template_id` varchar(64) NOT NULL DEFAULT '' COMMENT '任务id',
  `template_name` varchar(128) NOT NULL DEFAULT '' COMMENT '任务名称',
  `type` int(2) NOT NULL DEFAULT '0' COMMENT '任务类型（0:java）',
  `app_name` varchar(128) NOT NULL DEFAULT '' COMMENT '应用名称',
  `template_config` longtext COMMENT '执行任务配置',
  `template_desc` longtext COMMENT '执行任务描述',
  `create_user` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_user` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `delete_state` int(2) NOT NULL DEFAULT '1' COMMENT '数据删除状态,1为可用，0为删除状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_idx_template_id` (`template_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='录制模板配置表';

CREATE TABLE `tb_task_run_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `task_run_id` varchar(64) NOT NULL DEFAULT '' COMMENT '任务运行id',
  `template_id` varchar(64) NOT NULL DEFAULT '' COMMENT '任务id',
  `app_name` varchar(128) NOT NULL DEFAULT '' COMMENT '应用名称',
  `run_desc` longtext COMMENT '运行描述',
  `run_env` varchar(32) NOT NULL DEFAULT '' COMMENT '运行环境信息',
  `run_hosts` longtext COMMENT '任务执行机器列表',
  `run_status` int(2) NOT NULL COMMENT '0:未开始 1:运行中 2:运行完毕',
  `run_config` longtext COMMENT '运行配置',
  `run_type` int(11) NOT NULL DEFAULT '0' COMMENT '任务类型（0:录制任务 1:回放任务）',
  `create_user` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `task_start_time` timestamp NULL DEFAULT NULL COMMENT '任务开始时间',
  `task_end_time` timestamp NULL DEFAULT NULL COMMENT '任务结束时间',
  `delete_state` int(2) NOT NULL DEFAULT '1' COMMENT '数据删除状态,1为可用，0为删除状态',
  `record_run_id` varchar(64) NOT NULL DEFAULT '' COMMENT '录制任务执行id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_idx_task_run_id` (`task_run_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='任务执行表';

CREATE TABLE `tb_heartbeat_info` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `task_run_id` varchar(64) NOT NULL DEFAULT '' COMMENT '任务运行id',
  `env` varchar(32) NOT NULL DEFAULT '' COMMENT '环境名称',
  `ip` varchar(128) NOT NULL DEFAULT '' COMMENT '机器ip',
  `last_heartbeat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后上报时间--',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_task_run_id_ip` (`task_run_id`,`ip`) USING BTREE,
  KEY `idx_last_heartbeat_time` (`last_heartbeat_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='心跳信息表';

CREATE TABLE `tb_special_mock_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `app_name` varchar(128) NOT NULL DEFAULT '' COMMENT '应用名称',
  `mock_type` int(2) NOT NULL DEFAULT '0' COMMENT '特殊处理类型，1:时间相关类',
  `content_json` longtext COMMENT 'json格式内容',
  `create_user` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_user` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_idx_app_name_mock_type` (`app_name`,`mock_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='特殊处理表';

CREATE TABLE `tb_task_run_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `task_run_id` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '任务运行id',
  `content` longtext COLLATE utf8mb4_bin COMMENT '日志内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='日志记录表';

CREATE TABLE `tb_agent_file` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `file_name` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件名称',
  `content` longtext COLLATE utf8mb4_bin COMMENT '文件写入路径',
  `digest_hex` varchar(128) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件hash值',
  `update_user` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '更新人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_idx_file_name` (`file_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='agent文件';

CREATE TABLE `tb_replay_diff_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `app_name` varchar(128) NOT NULL DEFAULT '' COMMENT '应用名称',
  `diff_uri` varchar(512) DEFAULT '' COMMENT '当前接口uri',
  `field_path` varchar(255) NOT NULL COMMENT '对比字段或者路径列表',
  `diff_scope` int(2) NOT NULL COMMENT '作用范围',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` varchar(32) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_user` varchar(32) NOT NULL DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_update_time` (`update_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='接口uri回放对比配置';


INSERT INTO `tb_agent_file` (file_name, content, digest_hex, update_user, create_time, update_time)
VALUES
("moonbox-agent", "/moonbox/moonbox-agent.tar", "1", "moonbox", "2023/01/01", "2023/01/01"),
("sandbox-agent", "/moonbox/sandbox-agent.tar", "1", "moonbox", "2023/01/01", "2023/01/01");

INSERT INTO tb_record_task_template (template_id,template_name,`type`,app_name,template_config,template_desc,create_user,update_user,update_time,create_time,delete_state) VALUES
    ('tm_id_d2506554c774ccc89a04b402f41a9705','demo',0,'moon-box-web','{"dubboRecordInterfaces":[],"httpRecordInterfaces":[{"uri":"/api/agent/test/test1","desc":"单个接口测试","sampleRate":10000}],"javaRecordInterfaces":[{"classPattern":"com.vivo.internet.moonbox.web.agent.TestAgentController","methodPatterns":["testJava"],"obtainApplicationContextMethod":"com.vivo.internet.moonbox.service.common.utils.ApplicationContextUtils","desc":"java录制测试","sampleRate":10000}],"recordCount":500,"recordTaskDuration":60,"subInvocationPlugins":["http","dubbo-provider","java-entrance","dubbo-consumer","mybatis","mybatis-plus","redis","ibatis","okhttp","apache-http-client","guava-cache","hibernate-plugin","caffeine-cache","eh-cache","spring-mongo","elasticsearch","local-date-time","java-shuffle"],"sandboxLogLevel":"INFO","repeaterLogLevel":"INFO"}','','admin','admin','2023-02-15 10:33:56.0','2022-11-10 16:18:16.0',1)
;
INSERT INTO tb_special_mock_config (app_name,mock_type,content_json,create_user,update_user,update_time,create_time) VALUES
('moon-box-web',1,'{"sysTimeMockClasses":["com.vivo.internet.moonbox.web.agent.TestAgentController"]}','admin','admin','2023-02-15 10:36:58.0','2023-02-15 10:36:58.0'),
('moon-box-web',2,'[{"className":"com.vivo.internet.nex.repeater.console.interfaces.test.DubboConsumerTest","methodList":["randParam"]}]','admin','admin','2023-02-15 10:37:35.0','2023-02-15 10:37:35.0')
;
