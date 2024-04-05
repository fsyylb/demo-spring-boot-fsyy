-- add some sql script
create table if not exists fsyy_log
(
    id bigint not null auto_increment comment '主键',
    event_id varchar(50),
    event_date datetime,
    thread varchar(50),
    class varchar(255),
    `function` varchar(50),
    message varchar(255),
    exception text,
    level varchar(10),
    time datatime,
    submit_time varchar(20) comment '提交时间',
    execute_sql varchar(500) comment '运行sql',
    primary key (id)
) comment '运行日志';


create table if not exists task_status
(
    id bigint not null auto_increment comment '主键',
    taskid varchar(20) comment '任务id',
    status_value varchar(20) comment '状态描述',
    creator varchar(20) not null comment '创建人',
    create_time datetime not null comment '创建时间',
    modifier varchar(20) comment '修改人',
    modify_time datetime comment '修改时间',
    primary key (id)
) comment '任务状态';