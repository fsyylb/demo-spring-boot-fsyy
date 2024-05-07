### 集群方式安装
docker compose安装zookeeper3.4版本
编写配置文件，并将其命名为：docker-compose.yml
配置文件内容

```text
version: '2'
services:
    zoo1:
        image: zookeeper:3.4
        restart: always
        container_name: zoo1
        ports:
            - "2181:2181"
        environment:
            ZOO_MY_ID: 1
            ZOO_SERVERS: server.1=zoo1:2888:3888 server.2=zoo2:2888:3888 server.3=zoo3:2888:3888

    zoo2:
        image: zookeeper:3.4
        restart: always
        container_name: zoo2
        ports:
            - "2182:2181"
        environment:
            ZOO_MY_ID: 2
            ZOO_SERVERS: server.1=zoo1:2888:3888 server.2=zoo2:2888:3888 server.3=zoo3:2888:3888

    zoo3:
        image: zookeeper:3.4
        restart: always
        container_name: zoo3
        ports:
            - "2183:2181"
        environment:
            ZOO_MY_ID: 3
            ZOO_SERVERS: server.1=zoo1:2888:3888 server.2=zoo2:2888:3888 server.3=zoo3:2888:3888
```

此配置文件表示，Docker需要启动三个zookeeper实例，并将2181，2182，2183三个端口号映射到容器内的2181这个端口上。
ZOO_MY_ID：表示zk服务的ID, 取值为1-255之间的整数，且必须唯一
ZOO_SERVERS：表示zk集群的主机列表

启动zookeeper集群
docker-compose up -d
该命令执行需要在docker-compose配置文件的目录下执行

查看zookeeper集群节点主从关系
使用
docker exec -it zoo1 /bin/bash
这个命令进入zoo1节点中，之后输入
./bin/zkServer.sh statu
来查看节点主从关系