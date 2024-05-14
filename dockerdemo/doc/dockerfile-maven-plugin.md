目录
一、简介
二、概述
三、将spring-boot-app打包成docker镜像
创建示例应用
修改pom文件
增加Dockerfile文件
使用Maven打包应用
运行应用镜像
四、分析mvn package 命令的控制台输出
引用
一、简介
maven是一个项目依赖管理和构建的工具，dockerfile-maven-plugin是一个maven的插件，主要作用是在项目构建的时候生成基于项目的docker镜像文件。

简而言之，此插件将maven和docker进行集成。

正常情况下，我们在开发了一个应用程序后，会使用maven进行打包，生成对应的jar文件。而后，会使用docker将jar文件build成一个镜像（docker image）。之后，就可以在docker daemon中创建基于镜像的容器，并可提供服务了。

dockerfile-maven-plugin的目标就是将maven的打包过程和docker的build过程结合在一起，当成功打包，既生成了对应的jar，也已生成了对应的docker镜像。当然，这只是最基础的功能，更详细的功能参见：https://github.com/spotify/dockerfile-maven

二、概述
我们知道maven是apache公司开发的一个产品，但是dockerfile-maven-plugin并不是apache官方开发的插件，是由一个叫做Spotify的组织开发的。

github主页：https://spotify.github.io/

github开源地址：https://github.com/spotify/dockerfile-maven

本文仅讨论如何基于一个Spring Boot的项目生成对应的docker镜像。

基本的原理如下：

首先，dockerfile-maven-plugin插件已经存储在maven的仓库中
然后，当在本地开发的时候，需要在项目的pom文件中引入此插件，在pom-build-plugins下面增加plugin配置节点
再然后，在executions节点中配置此插件如何工作；并且在configuration节点中加入需要的配置信息
最后，当我们执行mvn package的时候就可以得到docker image 了
环境：

Ideal版本：2020.01
java版本：8
maven版本：3.6.1
docker版本：19.03.12
ideal和docker deamon运行在同一台机器上面

三、将spring-boot-app打包成docker镜像
创建示例应用
使用ideal自带的Spring Initializr生成一个Spring Web 的示例项目

app对外提供一个hello的接口，访问该接口可以得到Hello，World的响应结果。应用主启动类代码如下：

package com.naylor.dockerfilemavenplugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@SpringBootApplication
public class DockerfileMavenPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerfileMavenPluginApplication.class, args);
    }

    @GetMapping("/hello")
    public  String hello(){
        return  "Hello,World";
    }

}


编译并运行项目，在浏览器中访问「http://127.0.0.1:8080/hello」 可以得到预期的响应结果

修改pom文件
在pom中增加对dockerfile-maven-plugin插件的引用，核心代码如下:

 <!--   dockerfile-maven-plugin      -->
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>dockerfile-maven-plugin</artifactId>
                <version>1.3.6</version>
                <executions>
                    <execution>
                        <id>default</id>
                        <goals>
                            <goal>build</goal>
                            
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <repository>com.naylor/${project.artifactId}</repository>
                    <tag>${project.version}</tag>
                    <buildArgs>
                        <JAR_FILE>${project.build.finalName}.jar</JAR_FILE>
                    </buildArgs>
                </configuration>
            </plugin>

其中：

g,a,v 为对插件的引用

executions中的build标识，在maven的packege环节执行此插件

configuration中的repository是生成的镜像的repository信息

tag为镜像的tag信息

buildArgs是在docker构建镜像过程中的参数，此处定义的JAR_FILE参数在执行docker build 的时候会消费

完整的pom文件如下：
```text
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.4.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.naylor</groupId>
    <artifactId>dockerfile-maven-plugin</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>dockerfile-maven-plugin</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.junit.vintage</groupId>
                    <artifactId>junit-vintage-engine</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>

            <!--   dockerfile-maven-plugin      -->
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>dockerfile-maven-plugin</artifactId>
                <version>1.3.6</version>
                <executions>
                    <execution>
                        <id>default</id>
                        <goals>
                            <goal>build</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <repository>com.naylor/${project.artifactId}</repository>
                    <tag>${project.version}</tag>
                    <buildArgs>
                        <JAR_FILE>${project.build.finalName}.jar</JAR_FILE>
                    </buildArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

增加Dockerfile文件
在项目根目录（和pom文件在同一级）新建一个Dokerfile文件，文件内容如下:

FROM java:8
EXPOSE 8080
ARG JAR_FILE
ADD target/${JAR_FILE} /app.jar
ENTRYPOINT ["java", "-jar","/app.jar"]

使用Maven打包应用
首先清理一下maven工程，在ideal的Maven面版中点击Lifecycle-clean或者使用命令行执行mvn clean。

然后，使用maven构建app，在ideal的Maven面版中点击Liftcycle-package或者使用命令行执行 mvn package

再然后在命令行工具中执行docker image ls ，如果不出意外，可以看到一个repository为com.naylor/dockerfile-maven-plugin的docker镜像。


运行应用镜像
在命令行工具中执行如下命令运行容器：

docker run -d -p 8081:8080 ImageId
ImageId为上一步生成的镜像的id，每次生成的镜像id都不一样
此命令作用为基于ImageId构建一个容器，将宿主机的8081端口映射到容器的8080端口
在宿主机浏览器中访问「http://127.0.0.1:8081/hello」可以得到Hello,World的响应。

四、分析mvn package 命令的控制台输出
通过mvn package的控制台输出，我们可以清晰的观察到整个流程的执行步骤，完整的输出如下:

/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/bin/java -Dmaven.multiModuleProjectDirectory=/Users/chenhd/code/DebrisApp_Springboot/debris-app "-Dmaven.home=/Applications/IntelliJ IDEA 2.app/Contents/plugins/maven/lib/maven3" "-Dclassworlds.conf=/Applications/IntelliJ IDEA 2.app/Contents/plugins/maven/lib/maven3/bin/m2.conf" "-Dmaven.ext.class.path=/Applications/IntelliJ IDEA 2.app/Contents/plugins/maven/lib/maven-event-listener.jar" "-javaagent:/Applications/IntelliJ IDEA 2.app/Contents/lib/idea_rt.jar=58649:/Applications/IntelliJ IDEA 2.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath "/Applications/IntelliJ IDEA 2.app/Contents/plugins/maven/lib/maven3/boot/plexus-classworlds-2.6.0.jar" org.codehaus.classworlds.Launcher -Didea.version2020.1 package
[INFO] Scanning for projects...
[INFO] 
[INFO] -----------------< com.naylor:dockerfile-maven-plugin >-----------------
[INFO] Building dockerfile-maven-plugin 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-resources-plugin:3.1.0:resources (default-resources) @ dockerfile-maven-plugin ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource
[INFO] Copying 0 resource
[INFO] 
[INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ dockerfile-maven-plugin ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-resources-plugin:3.1.0:testResources (default-testResources) @ dockerfile-maven-plugin ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /Users/chenhd/code/DebrisApp_Springboot/debris-app/dockerfile-maven-plugin/src/test/resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.8.1:testCompile (default-testCompile) @ dockerfile-maven-plugin ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-surefire-plugin:2.22.2:test (default-test) @ dockerfile-maven-plugin ---
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplicationTests
13:49:50.713 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating CacheAwareContextLoaderDelegate from class [org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate]
13:49:50.735 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating BootstrapContext using constructor [public org.springframework.test.context.support.DefaultBootstrapContext(java.lang.Class,org.springframework.test.context.CacheAwareContextLoaderDelegate)]
13:49:50.762 [main] DEBUG org.springframework.test.context.BootstrapUtils - Instantiating TestContextBootstrapper for test class [com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplicationTests] from class [org.springframework.boot.test.context.SpringBootTestContextBootstrapper]
13:49:50.781 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Neither @ContextConfiguration nor @ContextHierarchy found for test class [com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplicationTests], using SpringBootContextLoader
13:49:50.785 [main] DEBUG org.springframework.test.context.support.AbstractContextLoader - Did not detect default resource location for test class [com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplicationTests]: class path resource [com/naylor/dockerfilemavenplugin/DockerfileMavenPluginApplicationTests-context.xml] does not exist
13:49:50.785 [main] DEBUG org.springframework.test.context.support.AbstractContextLoader - Did not detect default resource location for test class [com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplicationTests]: class path resource [com/naylor/dockerfilemavenplugin/DockerfileMavenPluginApplicationTestsContext.groovy] does not exist
13:49:50.785 [main] INFO org.springframework.test.context.support.AbstractContextLoader - Could not detect default resource locations for test class [com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplicationTests]: no resource found for suffixes {-context.xml, Context.groovy}.
13:49:50.786 [main] INFO org.springframework.test.context.support.AnnotationConfigContextLoaderUtils - Could not detect default configuration classes for test class [com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplicationTests]: DockerfileMavenPluginApplicationTests does not declare any static, non-private, non-final, nested classes annotated with @Configuration.
13:49:50.826 [main] DEBUG org.springframework.test.context.support.ActiveProfilesUtils - Could not find an 'annotation declaring class' for annotation type [org.springframework.test.context.ActiveProfiles] and class [com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplicationTests]
13:49:50.926 [main] DEBUG org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider - Identified candidate component class: file [/Users/chenhd/code/DebrisApp_Springboot/debris-app/dockerfile-maven-plugin/target/classes/com/naylor/dockerfilemavenplugin/DockerfileMavenPluginApplication.class]
13:49:50.932 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Found @SpringBootConfiguration com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplication for test class com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplicationTests
13:49:51.069 [main] DEBUG org.springframework.boot.test.context.SpringBootTestContextBootstrapper - @TestExecutionListeners is not present for class [com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplicationTests]: using defaults.
13:49:51.070 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Loaded default TestExecutionListener class names from location [META-INF/spring.factories]: [org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener, org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener, org.springframework.boot.test.autoconfigure.restdocs.RestDocsTestExecutionListener, org.springframework.boot.test.autoconfigure.web.client.MockRestServiceServerResetTestExecutionListener, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrintOnlyOnFailureTestExecutionListener, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverTestExecutionListener, org.springframework.boot.test.autoconfigure.webservices.client.MockWebServiceServerTestExecutionListener, org.springframework.test.context.web.ServletTestExecutionListener, org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener, org.springframework.test.context.support.DependencyInjectionTestExecutionListener, org.springframework.test.context.support.DirtiesContextTestExecutionListener, org.springframework.test.context.transaction.TransactionalTestExecutionListener, org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener, org.springframework.test.context.event.EventPublishingTestExecutionListener]
13:49:51.096 [main] DEBUG org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Skipping candidate TestExecutionListener [org.springframework.test.context.transaction.TransactionalTestExecutionListener] due to a missing dependency. Specify custom listener classes or make the default listener classes and their required dependencies available. Offending class: [org/springframework/transaction/interceptor/TransactionAttributeSource]
13:49:51.098 [main] DEBUG org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Skipping candidate TestExecutionListener [org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener] due to a missing dependency. Specify custom listener classes or make the default listener classes and their required dependencies available. Offending class: [org/springframework/transaction/interceptor/TransactionAttribute]
13:49:51.098 [main] INFO org.springframework.boot.test.context.SpringBootTestContextBootstrapper - Using TestExecutionListeners: [org.springframework.test.context.web.ServletTestExecutionListener@5acf93bb, org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener@7e7be63f, org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener@6cd28fa7, org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener@614ca7df, org.springframework.test.context.support.DirtiesContextTestExecutionListener@4738a206, org.springframework.test.context.event.EventPublishingTestExecutionListener@66d3eec0, org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener@1e04fa0a, org.springframework.boot.test.autoconfigure.restdocs.RestDocsTestExecutionListener@1af2d44a, org.springframework.boot.test.autoconfigure.web.client.MockRestServiceServerResetTestExecutionListener@18d87d80, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrintOnlyOnFailureTestExecutionListener@618425b5, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverTestExecutionListener@58695725, org.springframework.boot.test.autoconfigure.webservices.client.MockWebServiceServerTestExecutionListener@543588e6]
13:49:51.114 [main] DEBUG org.springframework.test.context.support.AbstractDirtiesContextTestExecutionListener - Before test class: context [DefaultTestContext@209da20d testClass = DockerfileMavenPluginApplicationTests, testInstance = [null], testMethod = [null], testException = [null], mergedContextConfiguration = [WebMergedContextConfiguration@e15b7e8 testClass = DockerfileMavenPluginApplicationTests, locations = '{}', classes = '{class com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplication}', contextInitializerClasses = '[]', activeProfiles = '{}', propertySourceLocations = '{}', propertySourceProperties = '{org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}', contextCustomizers = set[org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@27ae2fd0, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@4278a03f, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@2bbf180e, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizerFactory$Customizer@96def03, org.springframework.boot.test.context.SpringBootTestArgs@1], resourceBasePath = 'src/main/webapp', contextLoader = 'org.springframework.boot.test.context.SpringBootContextLoader', parent = [null]], attributes = map['org.springframework.test.context.web.ServletTestExecutionListener.activateListener' -> true]], class annotated with @DirtiesContext [false] with mode [null].
13:49:51.152 [main] DEBUG org.springframework.test.context.support.TestPropertySourceUtils - Adding inlined properties to environment: {spring.jmx.enabled=false, org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true}

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.3.4.RELEASE)

2020-10-12 13:49:51.449  INFO 16693 --- [           main] .d.DockerfileMavenPluginApplicationTests : Starting DockerfileMavenPluginApplicationTests on neiyo with PID 16693 (started by chenhd in /Users/chenhd/code/DebrisApp_Springboot/debris-app/dockerfile-maven-plugin)
2020-10-12 13:49:51.451  INFO 16693 --- [           main] .d.DockerfileMavenPluginApplicationTests : No active profile set, falling back to default profiles: default
2020-10-12 13:49:52.458  INFO 16693 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2020-10-12 13:49:52.732  INFO 16693 --- [           main] .d.DockerfileMavenPluginApplicationTests : Started DockerfileMavenPluginApplicationTests in 1.559 seconds (JVM running for 2.86)
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.313 s - in com.naylor.dockerfilemavenplugin.DockerfileMavenPluginApplicationTests
2020-10-12 13:49:53.042  INFO 16693 --- [extShutdownHook] o.s.s.concurrent.ThreadPoolTaskExecutor  : Shutting down ExecutorService 'applicationTaskExecutor'
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] 
[INFO] --- maven-jar-plugin:3.2.0:jar (default-jar) @ dockerfile-maven-plugin ---
[INFO] Building jar: /Users/chenhd/code/DebrisApp_Springboot/debris-app/dockerfile-maven-plugin/target/dockerfile-maven-plugin-0.0.1-SNAPSHOT.jar
[INFO] 
[INFO] --- spring-boot-maven-plugin:2.3.4.RELEASE:repackage (repackage) @ dockerfile-maven-plugin ---
[INFO] Replacing main artifact with repackaged archive
[INFO] 
[INFO] --- dockerfile-maven-plugin:1.3.6:build (default) @ dockerfile-maven-plugin ---
[INFO] Building Docker context /Users/chenhd/code/DebrisApp_Springboot/debris-app/dockerfile-maven-plugin
[INFO] 
[INFO] Image will be built as com.naylor/dockerfile-maven-plugin:0.0.1-SNAPSHOT
[INFO] 
[INFO] Step 1/5 : FROM java:8
[INFO] 
[INFO] Pulling from library/java
[INFO] Digest: sha256:c1ff613e8ba25833d2e1940da0940c3824f03f802c449f3d1815a66b7f8c0e9d
[INFO] Status: Image is up to date for java:8
[INFO]  ---> d23bdf5b1b1b
[INFO] Step 2/5 : EXPOSE 8080
[INFO] 
[INFO]  ---> Using cache
[INFO]  ---> 75767466e0be
[INFO] Step 3/5 : ARG JAR_FILE
[INFO] 
[INFO]  ---> Using cache
[INFO]  ---> 2ecdd1234dc2
[INFO] Step 4/5 : ADD target/${JAR_FILE} /app.jar
[INFO] 
[INFO]  ---> 6169104a5073
[INFO] Step 5/5 : ENTRYPOINT ["java", "-jar","/app.jar"]
[INFO] 
[INFO]  ---> Running in 23596d4612b6
[INFO] Removing intermediate container 23596d4612b6
[INFO]  ---> 993715a0e72a
[INFO] Successfully built 993715a0e72a
[INFO] Successfully tagged com.naylor/dockerfile-maven-plugin:0.0.1-SNAPSHOT
[INFO] 
[INFO] Detected build of image with id 993715a0e72a
[INFO] Building jar: /Users/chenhd/code/DebrisApp_Springboot/debris-app/dockerfile-maven-plugin/target/dockerfile-maven-plugin-0.0.1-SNAPSHOT-docker-info.jar
[INFO] Successfully built com.naylor/dockerfile-maven-plugin:0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  16.597 s
[INFO] Finished at: 2020-10-12T13:50:03+08:00
[INFO] ------------------------------------------------------------------------

重点分析一下此行后面的日志：

--- dockerfile-maven-plugin:1.3.6:build (default) @ dockerfile-maven-plugin ---

Building Docker context /Users/chenhd/code/DebrisApp_Springboot/debris-app/dockerfile-maven-plugin

执行dockerfile-maven-plugin项目的docker上下文的构建

Image will be built as com.naylor/dockerfile-maven-plugin:0.0.1-SNAPSHOT

构建完成之后镜像的名称为：com.naylor/dockerfile-maven-plugin:0.0.1-SNAPSHOT

Step 1/5 : FROM java:8

dockerfile中一共定义了5步来执行构建，第一步是拉取java8的镜像，如果本地没有会从远程仓库中搜索并下载下来

Successfully tagged com.naylor/dockerfile-maven-plugin:0.0.1-SNAPSHOT

成功打包了镜像

引用
1：官网：https://github.com/spotify/dockerfile-maven

2：dockerfile参考：https://docs.docker.com/engine/reference/builder/


# 运行报错
[ERROR] Failed to execute goal com.spotify:docker-maven-plugin:1.0.0:build (default-cli) on project eureka-service: Exception caught: com.spotify.docker.client.shaded.com.fasterxml.jackson.databind.JsonMappingException: Can not construct instance of com.spotify.docker.client.messages.RegistryAuth: no String-argument constructor/factory method to deserialize from String value ('desktop')
[ERROR]  at [Source: N/A; line: -1, column: -1] (through reference chain: java.util.LinkedHashMap["credsStore"])
[ERROR] -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoExecutionException


升级插件版本，我升级的为1.4.9版本


/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home/bin/java -Dmaven.multiModuleProjectDirectory=/Users/lianbang/fsyygithub/demo-spring-boot-fsyy/dockerdemo "-Dmaven.home=/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3" "-Dclassworlds.conf=/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/m2.conf" "-Dmaven.ext.class.path=/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven-event-listener.jar" "-javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=59912:/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath "/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/boot/plexus-classworlds.license:/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/boot/plexus-classworlds-2.6.0.jar" org.codehaus.classworlds.Launcher -Didea.version2020.1.3 package
[INFO] Scanning for projects...
[INFO] 
[INFO] ------------------------< com.fsyy:dockerdemo >-------------------------
[INFO] Building dockerdemo 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-resources-plugin:3.1.0:resources (default-resources) @ dockerdemo ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource
[INFO] Copying 0 resource
[INFO] 
[INFO] --- maven-compiler-plugin:3.8.0:compile (default-compile) @ dockerdemo ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-resources-plugin:3.1.0:testResources (default-testResources) @ dockerdemo ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /Users/lianbang/fsyygithub/demo-spring-boot-fsyy/dockerdemo/src/test/resources
[INFO] 
[INFO] --- maven-compiler-plugin:3.8.0:testCompile (default-testCompile) @ dockerdemo ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-surefire-plugin:2.22.1:test (default-test) @ dockerdemo ---
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.fsyy.dockerdemo.DockerdemoApplicationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s - in com.fsyy.dockerdemo.DockerdemoApplicationTests
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] 
[INFO] --- maven-jar-plugin:3.1.0:jar (default-jar) @ dockerdemo ---
[INFO] Building jar: /Users/lianbang/fsyygithub/demo-spring-boot-fsyy/dockerdemo/target/dockerdemo-0.0.1-SNAPSHOT.jar
[INFO] 
[INFO] --- spring-boot-maven-plugin:2.1.1.RELEASE:repackage (repackage) @ dockerdemo ---
[INFO] Replacing main artifact with repackaged archive
[INFO] 
[INFO] --- dockerfile-maven-plugin:1.4.9:build (default) @ dockerdemo ---
[INFO] Building Docker context /Users/lianbang/fsyygithub/demo-spring-boot-fsyy/dockerdemo
[INFO] 
[INFO] Image will be built as com.fsyy/dockerdemo:0.0.1-SNAPSHOT
[INFO] 
[INFO] Step 1/5 : FROM openjdk:8
[INFO] 
[INFO] Pulling from library/openjdk
[INFO] Digest: sha256:86e863cc57215cfb181bd319736d0baf625fe8f150577f9eb58bd937f5452cb8
[INFO] Status: Image is up to date for openjdk:8
[INFO]  ---> b273004037cc
[INFO] Step 2/5 : EXPOSE 8080
[INFO] 
[INFO]  ---> Running in 17bfd76493b0
[INFO] Removing intermediate container 17bfd76493b0
[INFO]  ---> f84b7273b8fa
[INFO] Step 3/5 : ARG JAR_FILE
[INFO] 
[INFO]  ---> Running in df3c67d87cef
[INFO] Removing intermediate container df3c67d87cef
[INFO]  ---> aa6e4d145573
[INFO] Step 4/5 : ADD target/${JAR_FILE} /app.jar
[INFO] 
[INFO]  ---> 0f2daa9c719d
[INFO] Step 5/5 : ENTRYPOINT ["java", "-jar","/app.jar"]
[INFO] 
[INFO]  ---> Running in a39a8b307f62
[INFO] Removing intermediate container a39a8b307f62
[INFO]  ---> 8f785f3ad5b8
[INFO] Successfully built 8f785f3ad5b8
[INFO] Successfully tagged com.fsyy/dockerdemo:0.0.1-SNAPSHOT
[INFO] 
[INFO] Detected build of image with id 8f785f3ad5b8
[INFO] Building jar: /Users/lianbang/fsyygithub/demo-spring-boot-fsyy/dockerdemo/target/dockerdemo-0.0.1-SNAPSHOT-docker-info.jar
[INFO] Successfully built com.fsyy/dockerdemo:0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  16.090 s
[INFO] Finished at: 2024-05-14T09:10:35+08:00
[INFO] ------------------------------------------------------------------------

### 
Spotify的dockerfile-maven-plugin如何使用“docker-compose build”？
要使用Spotify的dockerfile-maven-plugin来执行“docker-compose build”，你需要做以下几个步骤：

首先，确保你已经在项目的根目录下创建了一个名为Dockerfile的文件，该文件描述了你的Docker映像的构建过程。

在你的pom.xml文件中添加以下配置，以将dockerfile-maven-plugin作为构建插件使用：

<build>
    <plugins>
        <plugin>
            <groupId>com.spotify</groupId>
            <artifactId>dockerfile-maven-plugin</artifactId>
            <version>1.4.13</version>
            <executions>
                <execution>
                    <id>default</id>
                    <goals>
                        <goal>build</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <dockerDirectory>.</dockerDirectory>
                <imageName>your-image-name</imageName>
                <skipDockerBuild>false</skipDockerBuild>
            </configuration>
        </plugin>
    </plugins>
</build>
请确保将your-image-name替换为你自己的Docker映像名称。

在命令行中执行以下命令，以使用docker-compose构建和启动你的应用程序：
mvn clean install dockerfile:build docker-compose:up
这将执行mvn clean install来构建你的项目，并使用dockerfile-maven-plugin构建Docker映像。然后使用docker-compose构建和启动你的应用程序。

请注意，你需要确保已经在项目根目录下创建了一个名为docker-compose.yml的文件，其中包含你的应用程序的Docker Compose配置。

希望这可以帮助到你！


Maven插件构建Docker镜像
Maven是一个强大的项目管理与构建工具，可以用来构建Docker镜像。以下几款Maven的Docker插件比较常用，插件名称可以从GitHub开源项目中搜索到。

spotify

fabric8io

bibryam

从各项目的功能性、文档易用性、更新频率、社区活跃度、Stars等几个纬度考虑，我们选用了第一款由Spotify公司开发的Maven插件，作为构建Docker镜像的工具。下面我们来详细探讨如何使用Maven插件构建Docker镜像。


### docker-compose-maven-plugin
<dependency>
    <groupId>com.dkanejs.maven.plugins</groupId>
    <artifactId>docker-compose-maven-plugin</artifactId>
    <version>$VERSION</version>
</dependency>

https://github.com/Systemmanic/docker-compose-maven-plugin


https://www.cnblogs.com/lcmlyj/p/12120089.html