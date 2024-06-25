docker-compose.yml

    mysql:
      image: "mysql:8.0.28"
      expose:
        - 3306
      ports:
        - "3307:3306"
      volumes:
        - ./confs/mysql/init:/docker-entrypoint-initdb.d/
        - ./shared_dir/data/mysql:/var/lib/mysql
        - /etc/localtime:/etc/localtime:ro
      restart: always
      environment:
        MYSQL_ALLOW_EMPTY_PASSWORD: "yes"
      networks:
        - fate-network
        
MySQL容器docker-entrypoint-initdb.d目录
docker-entrypoint-initdb.d 是一个特殊的目录，专用于 Docker 中的 MySQL 容器。
当使用 Docker 运行 MySQL 容器时，如果这个目录存在并且包含 SQL 脚本或其它可执行文件，Docker 会在 MySQL 服务启动之初自动执行这些脚本。
这一机制使得在首次启动容器时能够方便地进行一些初始化设置，比如创建数据库、表、用户，以及导入数据等。
具体来说，docker-entrypoint-initdb.d
目录下的文件执行有以下特点：
执行顺序通常是按文件名排序。
支持 .sh 脚本（可执行权限）和 SQL 脚本（.sql）。
这些脚本只在容器的 MySQL 数据卷为空时运行，即首次启动时。如果数据卷已有数据（例如容器重启），这些初始化脚本不会再次执行，以防止数据被重复初始化覆盖。
利用这个机制，开发者可以通过 Dockerfile 或者在运行容器时通过 -v 选项挂载包含初始化脚本的本地目录到 docker-entrypoint-initdb.d，从而实现 MySQL 容器的自动化配置和数据初始化。

docker-entrypoint-initdb.d下的sql脚本在启动docker时没有执行原因
docker镜像启动时会执行entrypoint.sh脚本；
其中关于是否执行数据库初始化的核心代码如下： 
由脚本可以看出，对于docker-entrypoint-initdb.d 目录下的sql 文件，在执行前会判断  DATABASE_ALREADY_EXISTS 是否为false
即 $DATADIR/mysql 目录并不存在；
但凡该服务器已经存在任何数据库，那么这个文件肯定不为空，故而不会执行sql脚本； 
https://www.jb51.net/server/3214793fm.htm



1、问题
    想进入docker修改配置文件时，发现没有vi和vim命令
2、一键更换国内源

Debian系统
sed -i -E 's/(deb|security).debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list

3、处理方法

# 进入容器
docker exec -u root -it 容器名/容器id /bin/bash
apt-get update
apt-get install vim

https://www.cnblogs.com/fireblackman/p/16453884.html


docker-compose中：
command:
    --default-authentication-plugin=mysql_native_password

一、mysql_native_password身份验证插件是什么

        mysql_native_password是MySQL数据库中的一种身份验证插件，它负责处理用户登录时的密码验证。这个插件使用MySQL自己的密码哈希算法，将用户提供的密码与存储在数据库中的密码哈希进行比较，以验证用户的身份。

二、ALTER USER命令与mysql_native_password

        ALTER USER是MySQL中用于修改已有用户账户的命令。通过这个命令，我们可以更改用户的密码、身份验证插件以及其他账户相关的属性。在MySQL 8.0及更高版本中，默认的身份验证插件已经从mysql_native_password变更为caching_sha2_password。因此，如果你需要使用mysql_native_password作为身份验证插件，就需要使用ALTER USER命令来明确指定。
https://blog.csdn.net/u013558123/article/details/138027607


Mysql之三种免密登录方式
一、示例环境版本说明
操作系统版本centos7.6

[wuhs@test1 mysql]$ cat /etc/redhat-release
CentOS Linux release 7.6.1810 (Core)

mysql数据库版本5.7.32

[wuhs@test1 mysql]$ mysql -V
mysql Ver 14.14 Distrib 5.7.32, for el7 (x86_64) using EditLine wrapper

二、MySQL免密登录方式配置示例
1、通过设置client标签，直接编辑/etc/my.cnf文件
编辑/etc/my.cnf文件，添加如下代码

[wuhs@test1 mysql]$ cat /etc/my.cnf
[client]
user = root
password = 123456
port = 3306

配置完成后可以使用mysql命令直接登录数据库

[wuhs@test1 mysql]$ mysql
Welcome to the MySQL monitor. Commands end with ; or \g.
Your MySQL connection id is 6
Server version: 5.7.32-log MySQL Community Server (GPL)
<br>
Copyright © 2000, 2020, Oracle and/or its affiliates. All rights reserved.
<br>
Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.
<br>
Type ‘help;’ or ‘\h’ for help. Type ‘\c’ to clear the current input statement.
<br>
mysql>

此方式最大问题是明文存储密码，见配置文件各用户可见，非常的不安全。

2、我们通过my.cnf来配置，设置到~/.my.cnf来配置免密码
编辑~/.my.cnf文件，添加如下代码

[wuhs@test1 mysql]$ cat ~/.my.cnf
[client]
user = root
password = 123456
port = 3306

修改my.cnf属性

#chmod 600 ~/.my.cnf
[wuhs@test1 mysql]$ ll ~/.my.cnf
-rw-------. 1 wuhs wuhs 51 Dec 29 22:56 /home/wuhs/.my.cnf

配置完成后可以使用mysql命令直接登录数据库

[wuhs@test1 mysql]$ mysql
Welcome to the MySQL monitor. Commands end with ; or \g.
Your MySQL connection id is 6
Server version: 5.7.32-log MySQL Community Server (GPL)
<br>
Copyright © 2000, 2020, Oracle and/or its affiliates. All rights reserved.
<br>
Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.
<br>
Type ‘help;’ or ‘\h’ for help. Type ‘\c’ to clear the current input statement.
<br>
mysql>

此种方式也是明文存储，配置方式同第一种，文件为隐藏文件，设置文件为改用户可读，与第一种方式相比安全性有所提高。经验证测试，~/.my.cnf配置文件优先于/etc/my.cnf。

3、通过mysql_config_editor命令
使用mysql_config_editor命令一个test标签

[wuhs@test1 mysql]$ mysql_config_editor set -G test -S /tmp/mysql.sock -uroot -p
Enter password: [此处输入root账户密码]

执行如上步骤后生成了隐藏文件.mylogin.cnf，文件类型为data，是一个二进制文件

[wuhs@test1 mysql]$ file ~/.mylogin.cnf
/home/wuhs/.mylogin.cnf: data

查看该文件，密码为加密存储

[wuhs@test1 mysql]$ mysql_config_editor print --all
[test]
user = root
password = *****
socket = /tmp/mysql.sock

使用mysql --login-path="标签"登录

[wuhs@test1 mysql]$ mysql --login-path=test
Welcome to the MySQL monitor. Commands end with ; or \g.
Your MySQL connection id is 18
Server version: 5.7.32-log MySQL Community Server (GPL)
<br>
Copyright © 2000, 2020, Oracle and/or its affiliates. All rights reserved.
<br>
Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.
<br>
Type ‘help;’ or ‘\h’ for help. Type ‘\c’ to clear the current input statement.
<br>
mysql>

第三种方式登录文件为隐藏的二进制文件，且密码通过密文存储，安全性最高。
https://blog.csdn.net/carefree2005/article/details/111987324

配置mysql免密登录
https://cloud.tencent.com/developer/article/2152876

skip-grant-tables作用

MySQL 的 SOCK 文件默认位置是 /tmp/mysql.sock 或 /var/run/mysqld/mysqld.sock。
如果是用官方rpm安装或者拉取的官方docker镜像起的MySQL服务，mysql.sock的路径是 /var/lib/mysql/mysql.sock。如果是下载的官方MySQL二进制安装包或者编译安装（编译时使用的默认值，没有特意指定mysql.sockl路径），那么mysql.sock的路径是 /tmp/mysql.sock


Linux 查看mysql.sock的位置
https://blog.51cto.com/u_16213424/7423987
docker中一般是/var/run/mysqld/mysqld.sock

/var/lib/mysql是什么目录
数据目录

/var/lib/mysql是MySQL数据库在Linux系统上的默认安装位置和数据目录。
在大多数Linux发行版中，MySQL的默认安装位置是/var/lib/mysql目录。这个目录是MySQL数据库服务器使用的默认数据目录，所有数据库文件都存储在这里。MySQL使用一系列文件和子目录来组织和存储数据，确保理解MySQL数据目录的结构对于管理和维护MySQL数据库至关重要。
安装好MySQL 8之后，可以查看如下的目录结构，其中数据库文件的存放路径就是/var/lib/mysql/。这个目录包含了MySQL的所有数据库文件，是MySQL服务器的数据目录。
此外，除了数据目录外，MySQL的相关命令目录位于`/usr/bin`和`/usr/sbin`，而配置文件目录则位于/usr/share/mysql-8.0和/etc/mysql（如my.cnf）。这些信息对于管理和配置MySQL数据库系统非常重要。