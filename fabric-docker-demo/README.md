引入docker-maven-plugin插件
```shell script
 <!-- 添加docker-maven插件 -->
             <plugin>
                 <groupId>io.fabric8</groupId>
                 <artifactId>docker-maven-plugin</artifactId>
                 <version>0.40.3</version>
                 <executions>
                     <execution>
                         <phase>package</phase>
                         <goals>
                             <goal>build</goal>
                             <!--<goal>push</goal>-->
                             <!--<goal>remove</goal>-->
                         </goals>
                     </execution>
                 </executions>
                 <configuration>
                     <!-- 连接到带docker环境的linux服务器编译image -->
                     <!-- <dockerHost>http://localhost:2375</dockerHost> -->
                      <dockerHost>http://localhost:2375</dockerHost>
 
                     <!-- Docker 推送镜像仓库地址 -->
                     <!--<pushRegistry>${docker.hub}</pushRegistry>-->
                     <images>
                         <!--<image>
                             &lt;!&ndash;推送到私有镜像仓库，镜像名需要添加仓库地址 &ndash;&gt;
                             <name>
                                 ${docker.hub}/lb/${project.artifactId}:${project.version}-UTC-${maven.build.timestamp}</name>
                             &lt;!&ndash;定义镜像构建行为 &ndash;&gt;
                             <build>
                                 <dockerFileDir>${project.basedir}</dockerFileDir>
                             </build>
                         </image>-->
                         <image>
                             <name>
                                 ${docker.hub}/lb/${project.artifactId}:${project.version}</name>
                             <build>
                                 <dockerFileDir>${project.basedir}</dockerFileDir>
                             </build>
                         </image>
                     </images>
                     <!--<authConfig>
                         &lt;!&ndash; 认证配置，用于私有镜像仓库registry认证 &ndash;&gt;
                         <username>${docker.username}</username>
                         <password>${docker.password}</password>
                     </authConfig>-->
                 </configuration>
             </plugin>
```

同时需要在 properties 节点部分定义变量docker.hub、maven.build.timestamp
```shell script
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.build.timestamp.format>yyyyMMdd-HH</maven.build.timestamp.format>
        <docker.hub>fsyy.com</docker.hub>
        <java.version>1.8</java.version>
        <skipTests>true</skipTests>
    </properties>
```

编辑Dockerfile 文件

```shell script
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

```

执行打包命令
```shell script
mvn clean package
```

简单测试
```shell script
docker run --name docker-demo -d -p 8080:8080 fsyy.com/lb/fabric-docker-demo:0.0.1-SNAPSHOT
```
注意事项
```text
${docker.hub}/lb/${project.artifactId}:${project.version}
其中${docker.hub}是镜像仓库地址，阿里云的一般是registry.cn-hangzhou.aliyuncs.com
lb是命名空间，需要在阿里云上设置并对应,没有对应上会报错
${project.artifactId}:${project.version}是镜像名称和版本号
```