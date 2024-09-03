# CronAsynService.java
集群部署时定时任务只在某一服务中同步逻辑
```text
1、开始
2、服务启动时读取当前同步类型的version值，无数据则初始化数据库并设置version值为1，设置成员变量
3、等待至定时任务设定时间...
4、更新数据库version值为version+1
5、判定是否更新成功
6、若否，同步数据库version值至成员变量并返回，返回至下一轮步骤3
7、若是，处理逻辑，示例（设置所有数据enable_flag为0，调用接口并更新数据库）
8、返回至下一轮步骤3
9、若3-8步骤异常，则结束
```

# 数据库表
```sql
create table if not exists `fsyy_db`.`fsyy_cron_asyn`(
    `asyn_type` varchar(255) not null comment '同步类型，字符串',
    `asyn_version` int(11) not null comment '版本号，频率一般为每天一更，int类型能hold住',
    `lock_status` int(2) default null comment '锁定状态，0-未锁定，1-锁定；暂无需使用',
    `asyn_desc` varchar(255) default null comment '描述，暂为空',
    primary key(`asyn_type`)
)engine=InnoDB default charset=utf8mb4 collate=utf8mb4_bin comment = '定时任务同步表';
```

# 批量更新示例
前提，需批量表中添加一个enable_flag启用标识，0-不启用，1-启用；默认为1启用，为0可定时任务删除
```text
1、数据库分布式锁同步逻辑启用，见CronAsynService
2、设置所有数据的enable_flag为0
3、逻辑处理，插入或更新，插入和更新的数据中enable_flag设置为1
4、完成（后续批量删除enable_flag为0的数据，记住，需要在批量更新完成之后）
```