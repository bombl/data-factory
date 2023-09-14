CREATE DATABASE IF NOT EXISTS  `df`  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `df`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

/*==============================================================*/
/* Table: mockit_service_class                                  */
/*==============================================================*/
create table IF NOT EXISTS df_datasource
(
    id                   varchar(64) comment '主键',
    name                 varchar(64) comment '名称',
    link                 varchar(256) comment '链接',
    ip                   varchar(16) comment 'IP',
    port                 int comment '端口号',
    username             varchar(64) comment '用户名',
    password             varchar(64) comment '密码',
    enabled              int default 1 comment '是否启用（1.是0.否）',
    defaults             int default 1 comment '是否默认（1.是0.否）',
    valid                int default 1 comment '是否有效（1.是0.否）',
    remarks              varchar(512) comment '备注',
    create_at            datetime comment '创建时间',
    update_at            datetime comment '更新时间',
    primary key (id)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;