# Docker安装jdk1.8
安装JDK1.8：docker pull java:8 或者 docker pull openjdk:8 (2023年8月3日，发现上一条指令官方已废弃。)
查看已安装的镜像： docker images
运行容器：docker run -d -it --name openjdk-8 openjdk:8
进入容器：docker exec -it openjdk-8 /bin/bash
查看java版本，进入java-8容器后输入 ： java -version
Docker 安装JDK1.8镜像，完成！

参数	说明
-i	以交互模式运行容器
-t	为容器分配一个伪终端
-it	-i 和 -t 参数的组合

# docker-compose.yml
```yaml
version: '3.7'
networks:
    fsyy-network:
      ipam:
        config:
        - subnet: 192.167.0.0/16

#volumes:

services:
    javaclient:
      image: openjdk:8
      restart: always
      expose:
        - 8080
      ports:
        - "8080:8080"
      networks:
        - fsyy-network
      volumes:
        - ./build:/data
#      environment:
      command:
        - /bin/bash
        - -c
        - |
          java -jar /data/app.jar
```