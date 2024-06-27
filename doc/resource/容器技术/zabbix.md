```shell script
docker network create -d bridge --subnet 172.18.0.0/16 zabbix-net 

docker run --name zabbix-mysql -t -e MYSQL_DATABASE="zabbix" -e MYSQL_USER="zabbix" -e MYSQL_PASSWORD="zabbix" -e MYSQL_ROOT_PASSWORD="root123" -e TZ="Asia/Shanghai" -e ZBX_DBTLSCONNECT="required" --network=zabbix-net --ip=172.18.0.2 --restart=always --privileged=true -d mysql:8.0.28 --character-set-server=utf8 --collation-server=utf8_bin

docker run --name zabbix-server-mysql_1 -t -e DB_SERVER_HOST="zabbix-mysql" -e MYSQL_DATABASE="zabbix" -e MYSQL_USER="zabbix" -e MYSQL_PASSWORD="zabbix" -e MYSQL_ROOT_PASSWORD="root123" -e TZ="Asia/Shanghai" --network=zabbix-net --ip=172.18.0.3 -p 10051:10051 --restart=always --privileged=true -d zabbix/zabbix-server-mysql

----需修改
docker run --name zabbix-web-nginx-mysql_1 -t -e ZBX_SERVER_HOST="zabbix-server-mysql_1" -e DB_SERVER_HOST="zabbix-mysql" -e MYSQL_DATABASE="zabbix" -e MYSQL_USER="zabbix" -e MYSQL_PASSWORD="zabbix" -e MYSQL_ROOT_PASSWORD="root123" -e TZ="Asia/Shanghai" -e PHP_TZ="Asia/Shanghai" --network=zabbix-net --ip=172.18.0.4 -p 18080:8080 --restart=always --privileged=true -d zabbix/zabbix-web-nginx-mysql
----

先启动一个临时容器 复制出来相关文件
docker cp zabbix-web-nginx-mysql_1:/usr/share/zabbix/assets/fonts /root/conf/zabbix-web/
docker cp zabbix-web-nginx-mysql_1:/usr/share/zabbix/include/defines.inc.php /root/conf/zabbix-web/
docker run --name zabbix-web-nginx-mysql_1 -t -v /root/conf/zabbix-web/defines.inc.php:/usr/share/zabbix/include/defines.inc.php -v /root/conf/zabbix-web/fonts:/usr/share/zabbix/assets/fonts -e ZBX_SERVER_HOST="zabbix-server-mysql_1" -e DB_SERVER_HOST="zabbix-mysql" -e MYSQL_DATABASE="zabbix" -e MYSQL_USER="zabbix" -e MYSQL_PASSWORD="zabbix" -e MYSQL_ROOT_PASSWORD="root123" -e TZ="Asia/Shanghai" -e PHP_TZ="Asia/Shanghai" --network=zabbix-net --ip=172.18.0.4 -p 18080:8080 --restart=always --privileged=true -d zabbix/zabbix-web-nginx-mysql

host模式 ZBX_SERVER_HOST需指定172.18.0.3
docker run --name zabbix-agent-2 -e ZBX_SERVER_HOST="172.18.0.3,172.22.192.144" -e ZBX_HOSTNAME="Zabbix server" -e TZ="Asia/Shanghai" --network=host --restart=always --privileged=true -d zabbix/zabbix-agent2

非host模式 但不能监控宿主机eth0网卡
docker run --name zabbix-agent-2 -e ZBX_SERVER_HOST="zabbix-server-mysql_1" -e ZBX_HOSTNAME="Zabbix server" -e TZ="Asia/Shanghai" --network=zabbix-net --ip=172.18.0.5 -p 10050:10050 --restart=always --privileged=true -d zabbix/zabbix-agent2

```


【zabbix】解决zabbix在web页面显示中文乱码问题
https://blog.csdn.net/liu_chen_yang/article/details/126890093

#进入目录
vim /usr/share/zabbix/include/defines.inc.php

#修改两处地方；
#修改一：可以查找到这个单词“ZBX_GRAPH_FONT_NAME”，默认应该是在72行；
原：define('ZBX_GRAPH_FONT_NAME',           'graphfont'); // font file name
改为：define('ZBX_GRAPH_FONT_NAME',           'zabbix'); // font file name

#修改二：可以查找到这个单词“ZBX_FONT_NAME”，默认应该是在113行；
原：define('ZBX_FONT_NAME', 'graphfont');
改为：define('ZBX_FONT_NAME', 'zabbix');


zabbix6.4报错问题汇总：zabbix server无法连接zabbix agent主机
https://blog.csdn.net/m0_51453764/article/details/136838476
检查10050端口是否开放，以下三种方式都可以查看端口是否开放。
1.nc -zv <服务器IP> <端口号>
2.telnet <服务器IP> <端口号>
3.netstat -tuln  #查看被监听的端口



# https://hub.docker.com/u/zabbix

# Zabbix 使用手册
https://www.zabbix.com/documentation/6.0/zh/manual


# ZABBIX 6.0 中文乱码解决
https://www.cnblogs.com/aroin/p/15992571.html
