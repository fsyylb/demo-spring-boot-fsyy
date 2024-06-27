# docker命令大全详解
https://blog.csdn.net/a1053765496/article/details/95164627

docker 安装部分
1、更新yum源（新服务执行，老服务可以跳过）
yum update -y
yum install epel-release -y
yum clean all
yum list

2、安装Docker

安装docker

yum install -y yum-utils
yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
yum install -y docker-ce
启动docker，后台运行

systemctl start docker
检查安装结果

docker info

查看docker版本

docker -v
停止docker

systemctl stop docker
重启docker

systemctl restart docker
开机自动启动docker

systemctl enable docker

docker 命令大全
systemctl daemon-reload : 修改docker配置文件后通知docker服务做出的修改

systemctl restart docker.servcie : 重启docker服务

docker update 容器id --restart=always    : 开机自动启动容器

docker update 容器id --restart=no        : 关闭开机自动启动容器

docker images : 查看存在的镜像

docker rmi 镜像id : 删除镜像  // 存在多个镜像时, 使用镜像名称:tag删除, 删除时存在该镜像的容器, 建议先删除容器,再删除镜像

docker search 镜像名称 : 搜索网络镜像

docker pull 镜像名称 : 下载镜像

docker logs 容器id : 查看容器运行打印的日志

docker tag 已存在的镜像名称:tag  新的镜像名称 : 把已经存在的镜像重新打一个tag版本

docker exec -it 容器id /bin/bash : 进入后台运行中docker容器里面

docker history 镜像id : 查看镜像Dockerfile文件在build构建时的层

docker ps : 查看正在运行的容器 || docker container ls

docker ps -a : 查看所有的容器(正在运行中和未运行的)

docker ps -aq : 查看所有的容器只显示容器id

docker container ls -a | awk {'print$列号'} : 查看所有的容器只显示指定的列号

示例: docker container ls -a | awk {'print$1'} : 查看所有的容器只显示容器id

docker ps -f "status=exited" [-q] : 查看状态为exited退出的容器 [-q]只返回容器id

docker stop 容器id : 停止运行中的容器

docker start 容器名称 : 重新启动停止状态的容器

docker rm [-f] 容器id : 删除容器  [-f]强制删除 

docker rm $(docker ps -aq) : 批量删除容器, 删除所有容器

docker rm $(docker ps -f "status=exited" -q) : 批量删除容器, 删除状态为exited退出的容器

docker run --name 自定义名称 镜像名称 : 运行镜像, 运行中的镜像为容器

注释: -d 后台运行, --name 自定义名称, -h 自定义主机名, -it 进入并且运行中

docker run [--name 自定义名称] [-h 自定义主机名] [-p 物理端口:程序端口] 镜像名称 : 映射端口

docker run -m 200M --memory-swap 300M 镜像名称 : 限制启动的容器占用的内存

注释:    -m 或 -memory：设置内存的使用限额，例如100MB，2GB。

         --memory-swap：设置内存+swap的使用限额。

docker run -c 2 -m 200M --memory-swap 300M 镜像名称 : 限制容器cpu的权重

注释:    默认设置下，所有容器可以平等地使用host CPU资源并且没有限制。 

        docker可以通过 -c 或 –cpu-shares 设置容器使用CPU的权重。如果不指定，默认值为1024

        与内存限额不同，通过 -c 设置的 CPU share 并不是CPU资源的绝对数量，而是一个相对的权重值。

            某个容器最终能分配到CPU资源取决于它的CPU share占所有容器CPU share总和的比例。

        即通过CPU share可以设置容器使用CPU的优先级。

docker run -d -e 环境变量key=环境变量value 镜像名称 : -e 给容器设置环境变量

docker network inspect bridge : 查看连接在docker的bridge网络上的容器, docker默认的网络使用 docker network ls 查看

docker container commit : 从容器的更改中创建一个新镜像, 可以简写成 docker commit

    命令: docker commit 容器名称 自定义组织名称/自定义镜像名称  // 容器名称: 使用docker run 运行容器时的 --name参数, 

      自定义组织名称/自定义镜像名称: commit新镜像的名称

    示例: docker commit mynginx lixun/nginx-commit

docker image build :  用Dockerfile构建一个镜像, 可以简写成 docker build

        命令:    docker build -t 自定义组织名称/自定义镜像名称 Dockerfile文件地址

    示例:    docker build -t lixun/nginx-test .    // . 表示构建当前目录下的Dockerfile


docker 数据持久化
数据持久化

第一种方式: docker后台自动创建管理的volume数据源

docker volume ls : 查看数据源

docker volume inspect 数据源名称 : 查看数据源的详细信息, 数据源名称通过docker volume ls命令获取

docker run -d -v mysql:/var/lib/mysql --name mysql1 -e MYSQL_ALLOW_EMPTY_PASSWORD=true mysql : 给数据源自定义名称

注释:    --name mysql1 // 自定义容器名称叫mysql1

        -v volume名称:容器的目录  // 数据持久化的目录

        -e MYSQL_ALLOW_EMPTY_PASSWORD=true // mysql容器的系统环境变量, 表示不用密码

上面这样做后, 当容器被停止被删除后, 下次重新启动该容器, 怎么保证以前的数据还在? 使用以下命令语法:

docker run -d -v mysql:/var/lib/mysql --name mysql2 -e MYSQL_ALLOW_EMPTY_PASSWORD=true mysql

注释:    再次启动新的容器的时候 -v 指定之前容器使用的volume名称即可

第二种方式: 绑定路径的方式

docker run -d -v /本地路径:/容器里的路径 -p 本地端口:容器端口 --name 自定义容器名称 镜像名称:数据tag


docker 远程仓库
构建 image 镜像:

    命令: docker build -t 自定义组织名/自定义镜像名 .  

    示例: docker build -t lixun/hello .

向 docker hub 远程仓库上提交镜像:

    linux上登录docker hub:

        docker login 回车, 输入账号和密码

    提交本机docker的镜像到docker hub远程仓库:

        命令: docker push 镜像名称:版本

        示例: docker push lixun/centos-vim:latest // lixun/centos-vim的lixun必须与docker hub的账号名一致, 不然拒绝资源请求

    拉取远程仓库镜像到本机:

        命令: docker pull 镜像名称

        示例: docker pull lixun/centos-vim

搭建私有远程仓库服务:

        执行该命令即安装启动成功(端口5000, 此种方式的远程仓库只有命令行的方式,不提供页面操作窗口):

        mkdir -p /opt/data/registry

        docker run -d -p 5000:5000 -v /opt/data/registry:/var/lib/registry --restart always --name registry registry:latest

向私有远程仓库提交镜像:

    将要提交的镜像重新build构建成私有仓库地址的镜像:

        命令: docker build -t 私有仓库ip:端口/自定义镜像名 .

        示例: docker build -t 192.168.1.1:5000/centos-vim .

    提交本机docker的镜像到docker hub远程私有仓库:

        命令: docker push 192.168.1.1:5000/自定义镜像名:版本

        示例: docker push 192.168.1.1:5000/centos-vim:latest

    验证是否提交成功:

        registry官方api文档: https://docs.docker.com/registry/spec/api/

        使用网页访问registry api接口, 会返回在私有仓库里的镜像列表: 

            http://192.168.1.1:5000/v2/_catalog            

    注(配置):

        私有远程仓库是不被docker服务信任的, 所以需要让本机docker服务信任(本机docker关联私有仓库)

        步骤1: 在/etc/docker/daemon.json添加以下代码(daemon.json文件默认是不存在的,需要创建)

            {

                "insecure-registries":["192.168.1.1:5000"]

            }

        步骤2: 在/lib/systemd/system/docker.service添加以下命令

            ExecStart下添加...

            EnvironmentFile=-/etc/docker/daemon.json 

        步骤3: 重新加载配置文件，重启docker服务

            systemctl daemon-reload

            service docker restart 

docker compose   
安装docker compose

下载地址：https://github.com/docker/compose/tags

下载 docker-compose-linux-x86_64

上传到linux服务器的 /usr/local/bin 目录，

docker-compose-linux-x86_64 重命名为 docker-compose

chmod 777 /usr/local/bin/docker-compose

查看 docker-compose 是否安装成功：docker-compose

docker compose : docker批处理工具, compose命令适用于开发代码时一键部署运行环境, compose的stack适用于生产 批处理命令写在docker-compose.yml配置文件里

docker-compose up [-d]: 启动docker-compose.yml文件里的所有services服务应用 -d 后台执行,不会打印日志(非-d执行会打印日志)。 如果不想用默认的docker-compose.yml这个文件名,需要在up的时候指定使用哪个yml文件, 命令: docker-compose -f docker-compose.yml up

docker-compose build : 重新构建compose, 如果代码修改了,可以使用此命令

docker-compose ps : 查看当前的compose的services服务

docker-compose images : 查看compose中定义的容器, 和容器所使用的镜像

docker-compose stop : 停止compose中启动的应用

docker-compose start : 启动通过stop停止的compose中启动的应用

docker-compose down : 停止并且删除compose中的应用

docker-compose exec service名称 bash : 进入compose运行中的容器, service名称为在docker-compose.yml文件中定义的服务名称

docker-compose up --scale  service名称=启动数量 up : 通过compose相同的service服务应用启动多个, 注:启动多个不要在docker-compose.yml文件中映射端口, 并且docker-compose.yml文件中需要添加端口代理,因为没有映射端口,外部服务不知道启动服务的端口就无法访问,docker-compose.yml的端口代理配置在services下写, 并且Dockerfile的容器要指定一个端口,如EXPOSE 80, 

代码如下(单机版):

    lb:

      image: dockercloud/haproxy

      links:

        - service名称

      ports:

        - 8080:80

      volumes:

        - /var/run/docker.sock:/var/run/docker.sock    // 需要volumes就写上, 不需要就删除

使用 docker stack启动compose:

docker stack deploy 自定义容器名称 -c=docker-compose.yml

docker stack services 容器名称 : 查看通过stack创建的容器

docker swarm 集群
swarm 是docker的容器编排工具, 和Kubernetes的作用一样,Kubernetes是谷歌的产品, 它们是竞争对手. 在容器编排的竞争中swarm败下阵来,很多大型厂商选择了Kubernetes, 所以docker只能选择支持Kubernetes(2017年之前是docker产品是不支持Kubernetes的使用的)

swarm集群式manage(主)和work(从)的方式

环境测试使用的paly with docker网站练习:

docker swarm init --advertise-addr=ip : 创建manage主节点,会输出以下内容, 表示创建如节点成功:



创建从节点: 拷贝上面的第 3 行输出的命令到从节点主机执行即可创建成功, 如下图:



第1行执行创建从节点命令, 第2行返回创建成功结果

docker node ls : 查看当前swarm集群的节点信息

docker swarm leave : 让从节点脱离集群, 在从节点上出入该命令

docker service create --name 自定义容器名称 镜像名称 : 在集群环境下创建容器(docker service相当于docker run命令)

示例: docker service create --name demo1 busybox sh -c "while true;do sleep 3600;done"

docker service scale 自定义容器名称=个数 : 扩展容器分布, scale发现有容器出异常会自动检测,会在集群中任意一个节点再启动一个, 保证程序的可用

docker service ls : 查看集群的所有容器

docker service ps 容器名称 : 查看指定的容器在集群的分布情况, 使用docker ps 查看主机上运行的容器

docker service rm 容器名称 : 删除集群上的容器

docker network create -d overlay 自定义网络名称 : 集群中的docker容器通信要创建overlay网络

docker service create --name 自定义容器名称 --env 环境变量=值 --network 网络名称 --mount type=volume,source=/本机目录,destination=/容器目录 镜像名称 : 集群中启动容器

注释: --env:相当于单机run启动中的-e表示环境变量, --mount:相当于单机run启动中的-v表示数据持久化, --network: 集群中的容器网络通信要指定相同的 先使用docker network create -d overlay 自定义网络名称, 然后通过--network指定

docker secret management
docker加密, 用于数据库或者其他登录用户名密码不明文显示

创建一个secret, 使用文件的方式:

    命令:    docker secret create 自定义secret名称 密码文件

            注释: 密码文件使用vim创建一个文件,文件内容存入密码

    示例:    docker secret create my-pw password.txt

创建一个secret, 使用输入的方式:

    命令:    echo "密码" | docker secret create 自定义secret名称 -

    示例:    echo "123456" | docker secret create my-pw2 -

docker secret ls : 查看存在的secret

docker secret rm secret名称 : 删除secret

docker service create --name 容器名称  --secret secret名称 镜像名称 sh -c "while true; do sleep 3600; done" : 容器使用secret(docker run不能使用secret), 使用--secret后会在容器里生成/run/secrets目录 

mysql使用secret:

docker service create --name db --secret my-pw -e MYSQL_ROOT_PASSWORD_FILE=/run/secrets/my-pw mysql

secret在docker compose中使用:

如果secrets没有在docker服务中创建, 在docker-compose.yml使用下面的语法创建:

    在networks下写(不推荐在compose文件中创建secret):

        secrets:

            my-pw:    #secret名称

                file: ./密码文件.txt

    compose.yml使用secret:

        在services的service服务下写:

            secrets:

                - my-pw    #secret名称

            environment:

                环境变量key: /run/secrets/my-pw    #密码在容器中的文件目录


Kubernetes (docker集群编排)
kubernetes是master(主)node(从)的方式:

kubernetes的主要组件:

    pod:    kubernetes的最小单元, 由一个容器或多个容器组成

    controllers: 管理部署pod

    service: 放置pod失联, 定义了一组pod的访问策略

    kubelet:    用于容器,数据卷,network的创建和管理

    kube-proxy:    用于端口代理和转发,负载均衡等

    fluentd:    日志采集存储和查询



kubernetes集群的安装(重要):



etcd自签SSL证书:

依次执行以下命令:

----------------------------------------------

curl -L https://pkg.cfssl.org/R1.2/cfssl_linux-amd64 -o /usr/local/bin/cfssl

curl -L https://pkg.cfssl.org/R1.2/cfssljson_linux-amd64 -o /usr/local/bin/cfssljson

curl -L https://pkg.cfssl.org/R1.2/cfssl-certinfo_linux-amd64 -o /usr/local/bin/cfssl-certinfo

chmod +x /usr/local/bin/cfssl /usr/local/bin/cfssljson /usr/local/bin/cfssl-certinfo

----------------------------------------------

cat > ca-config.json <<EOF

{

  "signing" : {

    "default" : {

      "expiry" : "87600h"

    },

    "profiles" : {

      "www": {

        "expiry" : "87600h",

        "usages" : [

          "signing",

          "key encipherment",

          "server auth",

          "client auth"

        ]

      }

    }

  }

}

EOF

---------------------------------------------- 

cat > ca-csr.json << EOF

{

  "CN" : "etcd CA",

  "key" : {

    "algo" : "rsa",

    "size" : 2048

  },

  "names" : [

    {

      "C" : "CN",

      "L" : "ZheJiang",

      "ST": "ZheJiang"

    }

  ]

}

EOF

---------------------------------------------- 

cfssl gencert -initca ca-csr.json | cfssljson -bare ca -

---------------------------------------------- 

cat > server-csr.json <<EOF

{

  "CN" : "etcd",

  "hosts" : [

    "192.168.0.23",    // 写上需要安装etcd的主机地址

    "192.168.0.22",

    "192.168.0.21"

  ],

  "key" : {

    "algo" : "rsa",

    "size" : 2048

  },

  "names" : [

    {

      "C" : "CN",

      "L" : "ZheJiang",

      "ST": "ZheJiang"

    }

  ]

}

EOF

---------------------------------------------- 

cfssl gencert -ca=ca.pem -ca-key=ca-key.pem -config=ca-config.json -profile=www server-csr.json | cfssljson -bare server

---------------------------------------------- 

使用minikube的方式安装kubenets集群:

    minikube的安装, 安装官网执行命令即可成功 minikube start | minikube

    安装完成后使用, 

    minikube version

    kubectl version 查看打印版本

执行minikube start 命令,minikube就会在本机创建好kubernetes集群

    命令: minikube start

