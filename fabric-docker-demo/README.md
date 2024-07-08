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

maven配置server时对password进行加密
```text
1.需求
因为公司的编译服务器部署在了阿里云上，需要在编译完成后上传编译后的aar文件到公司maven私服上，普通的maven部署方式是把maven私服的账号密码以明文的方式配置在settings.xml文件中

<server>
      <id>deploymentRepo</id>
      <username>admin</username>
      <password>12345678</password>
</server>

这种方式的配置很容易被别人看到从而泄漏了你的个人信息，显然是不适应于我的这种环境下使用的。

2.加密
maven其实可以对用户密码进行加密，需要用到下面2个命令

mvn --encrypt-master-password <password>
mvn --encrypt-password <password>

2-1 获取master密码
执行mvn --encrypt-master-password <password>即可得到一个master密码，例如对12345678这个密码进行加密:
mvn --encrypt-master-password 12345678
得到加密串:
{VrVw6/Cg8FYHpfLj8oO/qRbMY5VrfGtIeR7RX5OHeV0=}
我们需要打开~/.m2/settings-security.xml这个文件(如果没有就手动创建)
添加到标签中，文件内容如下:

<settingsSecurity>
    <master>{VrVw6/Cg8FYHpfLj8oO/qRbMY5VrfGtIeR7RX5OHeV0=}</master>
</settingsSecurity>

2-2 获取server加密密码
执行mvn --encrypt-password <password>即可得到一个server密码,例如再对12345678这个密码进行加密:
mvn --encrypt-password 12345678
得到加密串:
{2Db+TFdWDgQHlN7gBd1PAZHEC5h5E3Wuhcs9NBLdVIE=}
把这个加密串添加到settings.xml中server节点的password中:

<server>
      <id>deploymentRepo</id>
      <username>admin</username>
      <password>{2Db+TFdWDgQHlN7gBd1PAZHEC5h5E3Wuhcs9NBLdVIE=}</password>
    </server>
https://blog.csdn.net/u013648164/article/details/81005876
https://blog.csdn.net/qq_16127313/article/details/132512424
```

