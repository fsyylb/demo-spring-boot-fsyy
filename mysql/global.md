# mysql查看是否只读
SHOW GLOBAL VARIABLES LIKE 'read_only';

MySQL数据库只读错误是一个常见的问题，它可能会在多种情况下出现，例如在主从复制、系统故障恢复或者权限设置不当等情况下。这种错误会导致你无法对数据库进行写入操作。那么，如何解决这个问题呢？以下是一些可能的解决策略。
首先，我们需要确定只读状态是否被设置。你可以通过运行以下SQL命令来检查：

SHOW VARIABLES LIKE 'read_only';
如果结果显示'read_only'为'ON'，那么说明MySQL处于只读模式。

要取消这个模式, 你可以运行以下SQL命令：

SET GLOBAL read_only = OFF;
然而, 这并不总是有效的. 有时候, 即使我们尝试关闭只读模式, MySQL还是会保持在该状态. 这通常发生在主从复制配置中.

如果你正在使用主从复制，并且发现从服务器处于只读模式，则需要检查'read_only'和'super_read_only'两个系统变量。当'super_read_only'被设置为ON时，在关闭'read_only’之前必须先关闭它。

SET GLOBAL super_read_only = OFF;
SET GLOBAL read_onlY = OFF;

# Too many connections解决方案
一、原因：
my.ini 中设定的并发连接数太少或者系统繁忙导致连接数被占满。
连接数超过了 MySQL 设置的值，与 max_connections 和 wait_timeout 都有关。
wait_timeout 的值越大，连接的空闲等待就越长，这样就会造成当前连接数越大。

二、解决方式：
打开 MYSQL 安装目录打开 my.ini 找到 max_connections 默认是 100， 一般设置到500～1000比较合适，重启 MySQL。

显示哪些线程正在运行
show full processlist;

Command状态描述：
１.　SLEEP
线程正在等待客户端发送新的请求。

２.　QUERY
线程正在执行查询或者正在将结果发送给客户端。
　
３.　LOCKED
在MYSQL服务层，该线程正在等待表锁。在存储引擎级别实现的锁，如INNODB的行锁，并不会体现在线程状态中。　对于MYISAM来说这是一个比较典型的状态。但在其他没有行锁的引擎中也经常会出现。　

４.　ANALYZING　AND STATISTICS
线程正在收集存储引擎的统计信息，　并生成查询的执行计划。

５.　COPYING TO TMP TABLE （ON DISK）
线程正在执行查询，　并且将其结果集都复制到一个临时文件中，　这种状态一般要么是在做GROUP BY操作，要么是文件排序操作，　或者是UNION操作。　如果这个状态后面还有ON DISK的标　，　那表示MYSQL正在将一个内存临时表放到磁盘上。

６.　SORTING RESULT
线程正在对结果集进行排序。

７.　SENDING DATA
线程可能在多个状态之间传送数据，或者生成结果集，或者在向客户端返回数据。

三、连接数设置多少是合理的?
查看mysql的最大连接数：
show variables like '%max_connections%';

查看服务器响应的最大连接数:
show global status like 'Max_used_connections';


服务器响应的最大连接数为3，远低于mysql服务器允许的最大连接数值

对于mysql服务器最大连接数值的设置范围比较理想的是：服务器响应的最大连接数值占服务器上限连接数值的比例值在10%以上，如果在10%以下，说明mysql服务器最大连接上限值设置过高。

Max_used_connections / max_connections * 100% = 3/512 *100% ≈ 0.6%

四、wait_timeout
wait_timeout — 指的是mysql在关闭一个非交互的连接之前所要等待的秒数

如果你没有修改过MySQL的配置，wait_timeout的初始值是28800

wait_timeout 过大有弊端，其体现就是MySQL里大量的SLEEP进程无法及时释放，拖累系统性能，不过也不能把这个指设置的过小，否则你可能会遭遇到“MySQL has gone away”之类的问题

show global variables like 'wait_timeout'; #查看
set global wait_timeout=100; #设置

五、interactive_time
指的是mysql在关闭一个交互的连接之前所要等待的秒数
set global interactive_timeout=300;

mysql终端查看timeout的设置
show global variables like '%timeout%';

六、总结
MySQL服务器所支持的最大连接数是有上限的，因为每个连接的建立都会消耗内存，因此客户端在连接到MySQL Server处理完相应的操作后，应该断开连接并释放占用的内存。

如果MySQL Server有大量的闲置连接，不仅会白白消耗内存，而且如果连接一直在累加而不断开，最终肯定会达到MySQL Server的连接上限数，这会报'too many connections'的错误。

对于wait_timeout的值设定，应该根据系统的运行情况来判断。在系统运行一段时间后，可以通过show processlist命令查看当前系统的连接状态，如果发现有大量的sleep状态的连接进程，则说明该参数设置的过大，可以进行适当的调整小些。

增加最大连接数
 通过命令行临时增加连接数：
使用命令 SET GLOBAL max_connections = 新的连接数; 来即时增加最大连接数。例如，将最大连接数增加到500：
sql
Copy Code
SET GLOBAL max_connections = 500;
请注意，这种方法需要具有足够的权限才能执行，并且更改是临时的，重启MySQL服务后需要重新设置。
 通过‌配置文件永久增加连接数：
找到MySQL的配置文件（通常是my.cnf或my.ini），编辑该文件，增加max_connections的值。例如：
ini
Copy Code
[mysqld]
max_connections = 500
修改完配置文件后，需要重启MySQL服务以使更改生效。
