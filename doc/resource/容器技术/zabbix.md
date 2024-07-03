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

# docker zabbix-agent 客户端搭建
https://www.cnblogs.com/leihongnu/p/16489602.html

# zabbix-监控网卡流量
https://blog.csdn.net/weixin_42637022/article/details/124024041

# 第八篇、【Zabbix监控项之网卡流量监控】 
https://www.cnblogs.com/ygbh/p/12120918.html#_label12


zabbix怎么监控网络
Zabbix可以通过自定义脚本来监控网络。以下是一个基本的步骤和示例：

创建监控项：

在Zabbix中，进入相应的主机或模板，创建新的监控项。

设置类型为“Zabbix监控”，然后选择适当的键值。

创建触发器：

根据监控项的数据创建触发器，设置合适的阈值，以便在网络异常时接收警告。

创建脚本（可选）：

如果需要更复杂的网络监控，可以编写自定义脚本。

脚本需要输出简单的数据格式，如文本或JSON。

创建Zabbix代理的自定义键值：

在Zabbix代理的配置文件（zabbix_agentd.conf）中，使用UserParameter指定自定义的命令。

以下是一个简单的自定义脚本示例，用于监控网络接口的流量：

#!/bin/bash
 
# 检查是否指定了接口名称作为参数
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <interface>"
    exit 1
fi
 
# 使用ifconfig命令获取指定接口的流量信息
# 需要安装ifconfig命令或使用其他工具如ip
interface_traffic=$(/sbin/ifconfig $1 | grep 'RX packets' | awk '{print $2}')
 
# 输出接口接收的字节数
echo $interface_traffic
在Zabbix中创建监控项时，键值应该与脚本的名称相对应，例如：

UserParameter=network.traffic[*],/path/to/your/script.sh "$1"
然后在Zabbix前端创建相应的监控项、触发器和图形，以便可视化和管理网络监控。




https://www.zabbix.com/documentation/3.4/zh/manual/quickstart/host

# 使用 speedtest_cli 测试宽带速度
https://www.jianshu.com/p/f1cb52bd73a9

# linux测试网速命令
https://worktile.com/kb/ask/290405.html

# Linux内核中流量控制(11)
https://blog.51cto.com/enchen/158034

# 服务器出口带宽限速方法
https://blog.csdn.net/white_li5/article/details/131413673

# Linux TC 流量控制与排队规则 qdisc 树型结构详解（以HTB和RED为例）
https://blog.csdn.net/qq_44577070/article/details/123967699

# TC(Traffic Control)命令—linux自带高级流控
https://cloud.tencent.com/developer/article/1409664

# Linux 下 TC 命令原理及详解
https://blog.csdn.net/pansaky/article/details/88801249

# tc的linux命令详解
https://worktile.com/kb/ask/387361.html

# centos8 wondershaper 限制网络带宽
https://www.jianshu.com/p/52c252ffad2a

# linux下使用tc和netem模拟网络异常（一）
https://www.cnblogs.com/little-monica/p/11459772.html


Cannot find device "ifb0"
报错解释：

"ifb0" 是一种高速数据转发设备，通常用作Linux内核中的网络防火墙或虚拟网络。如果系统报告“Cannot find device 'ifb0'”，这意味着系统无法识别或找到名为“ifb0”的虚拟接口。

解决方法：

确认内核模块是否加载：

执行 lsmod | grep ifb 查看ifb模块是否已加载。如果没有加载，使用 modprobe ifb 加载ifb模块。

创建虚拟接口：

如果ifb模块已加载，但接口不存在，可以使用 ip link add ifb0 type ifb 创建名为“ifb0”的虚拟接口。

启用接口：

使用 ip link set ifb0 up 启用该接口。

检查接口状态：

使用 ip link show 或 ifconfig（如果已安装）检查“ifb0”接口是否正确创建并启用。

如果以上步骤无法解决问题，可能需要检查系统配置或者是否有其他相关的错误信息，以便进一步诊断问题。


# Zabbix6.0 TLS : Ubuntu 20.04 安装（一）
https://cloud.tencent.com/developer/article/1956230

# Zabbix 3.0 从入门到精通(zabbix使用详解)
https://www.cnblogs.com/clsn/p/7885990.html

# 在 Linux 中使用 Wondershaper 限制网络带宽
https://linux.cn/article-10084-1.html

# Ubuntu下 Docker、Docker Compose 的安装教程
https://blog.csdn.net/justlpf/article/details/132982953

# 修改Docker Hub为国内镜像源
https://www.icorgi.cn/2023/09/06/589.html

# nsenter命令
https://blog.csdn.net/hezuijiudexiaobai/article/details/130495974

# docker容器操作宿主机执行命令
https://segmentfault.com/a/1190000042002239

# 限制Docker容器上下行带宽
https://mudew.com/2024/04/15/%E9%99%90%E5%88%B6Docker%E5%AE%B9%E5%99%A8%E4%B8%8A%E4%B8%8B%E8%A1%8C%E5%B8%A6%E5%AE%BD/

# 掌握Linux限速利器：八款工具推荐
https://www.linuxpack.net/1474.html

# Docker：Docker网络管理（宿主机和容器互相访问，容器间互相访问，跨机器访问）
https://www.jianshu.com/p/5db52e909f59



https://www.zabbix.com/documentation/current/en/manual/appendix/config/zabbix_agent2

https://hub.docker.com/r/zabbix/zabbix-web-nginx-mysql

https://hub.docker.com/u/zabbix

# Zabbix容器化：快速部署与高效监控相结合
https://blog.csdn.net/2301_79223017/article/details/134806785

# Docker Hub 镜像加速器
https://gist.github.com/y0ngb1n/7e8f16af3242c7815e7ca2f0833d3ea6?permalink_comment_id=5082662

# https://github.com/docker-library/official-images#architectures-other-than-amd64

# Kiali
  Kiali 是具有服务网格配置和验证功能的 Istio 可观测性的控制台。通过监视流量来推断拓扑和错误报告，它可以帮助您了解服务网格的结构和运行状态。 Kiali 提供了详细的的指标并与 Grafana 进行基础集成，可以用于高级查询。通过与 Jaeger 来提供分布式链路追踪功能。