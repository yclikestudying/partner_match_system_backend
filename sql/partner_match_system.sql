-- auto-generated definition
# 用户表
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256)                       null comment '用户昵称',
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    tags         varchar(1024)                      null comment '标签 json 列表',
    profile      varchar(1024)                      null comment '个人简介'
)
    comment '用户';


-- 队伍表
create table team
(
    id          bigint auto_increment comment 'id' primary key,
    name        varchar(256)       not null comment '队伍名称',
    description varchar(1024) null comment '描述',
    maxNum      int      default 1 not null comment '最大人数',
    expireTime  datetime null comment '过期时间',
    userId      bigint comment '用户id（队长 id）',
    status      int      default 0 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512) null comment '密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0 not null comment '是否删除'
) comment '队伍';

-- 用户队伍关系
create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint comment '用户id',
    teamId     bigint comment '队伍id',
    joinTime   datetime null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0 not null comment '是否删除'
) comment '用户队伍关系';

-- 好友表
-- auto-generated definition
create table friend_list
(
    id         bigint auto_increment comment '主键'
        primary key,
    fromUserId bigint                             null comment '请求方',
    toUserId   bigint                             null comment '同意方',
    status     int      default 0                 not null comment '0 - 等待请求
1 - 请求完成',
    isDelete   int      default 0                 null comment '0 - 未删除
1 - 删除',
    createTime datetime default CURRENT_TIMESTAMP not null,
    remark     varchar(32)                        null comment '备注',
    constraint friend_list_id_uindex
        unique (id)
)
    comment '好友列表' auto_increment = 77;

-- 好友消息表
-- auto-generated definition
create table message
(
    id         bigint auto_increment comment 'id'
        primary key,
    fromUserId bigint                             not null comment '发送消息方用户id',
    toUserId   bigint                             not null comment '接收消息方id',
    message    varchar(1024)                      not null comment '消息内容',
    createTime datetime default CURRENT_TIMESTAMP not null comment '发送消息时间',
    isDelete   int      default 0                 not null comment '0 - 未删除
1 - 已删除',
    constraint message_id_uindex
        unique (id)
)
    auto_increment = 157;

-- 队伍消息表
-- auto-generated definition
create table team_message
(
    id         bigint auto_increment comment 'id'
        primary key,
    fromUserId bigint                             not null comment '发送用户id',
    teamId     bigint                             not null comment '队伍id
',
    message    varchar(1024)                      not null comment '消息内容',
    createTime datetime default CURRENT_TIMESTAMP not null comment '发送时间',
    idDelete   int      default 0                 not null comment '0 - 未删除
1 - 已删除',
    constraint team_messge_id_uindex
        unique (id)
);



