#基础镜像
#FROM openjdk:8-jre-alpine
FROM openjdk:8

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

#把你的项目war包引入到容器的root目录下
#COPY target/*.war /app.war

#把你的项目war包引入到容器的root目录下
COPY target/*.jar /app.jar

#证实有用
CMD ["--server.port=8080"]

#项目的启动方式
#ENTRYPOINT ["java","-Xmx400m","-Xms400m","-Xmn150m","-Xss1024k","-jar","/app.war", "--spring.profiles.active=prod"]
ENTRYPOINT ["java","-jar","/app.jar"]
