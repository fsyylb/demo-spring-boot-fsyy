# 2025年最新MacBook苹果电脑安装JDK8、JDK11、JDK17、JDK22教程，配置环境变量 + 快速切换JDK版本
```text
   2024年02月01日更新：环境变量新增jdk17配置代码。

        2024年05月13日更新：记录解决添加maven后不能自动切换jdk版本。

        2024年06月16日更新：友情提醒：如果是m芯片，推荐下载arm版本。

        2024年08月08日更新：新增JDK22版本安装。

        2024年11月28日更新：更新排版和封面图片。

在这里我要说一下，本帖所有内容本人亲手操作百分百成功。

教程是提供方式方法供参考，个人觉得并不适合傻瓜式伸手党，

看评论区看的心累，本帖从此永不更新。 



1、概述
本文主要为在MacBook苹果电脑系统下安装JDK及环境变量配置。

教程摘抄自互联网，本人作为更新+整理+亲测。（也算给自己记录一贴）

本帖分为四步：

在Oracle官网下载JDK
安装JDk
配置环境变量
快速切换
JDK是Java开发工具包（Java Development Kit）。

JDK 8 版本于2014年3月18日发布。引入了一系列新功能，如Lambda表达式、函数式接口、Stream API和新的日期/时间API等。
JDK 11 版本于2018年9月25日发布。提供了一些重要的改进，如模块化系统、增强的性能、新的HTTP客户端API和局部变量类型推断等。
JDK 17 版本于2021年9月14日发布，是Oracle官方长期支持（LTS）版本，提供了一些性能优化、安全性改进、API 更新等方面的调整，以提高 Java 平台的稳定性和可靠性。
JavaSE、JavaEE和JavaME是Java平台的不同配置和规范。

JavaSE（Java Standard Edition）是Java平台的标准配置，提供了基本的Java编程环境和核心API，适用于通用的桌面、服务器和嵌入式应用程序开发。
JavaEE（Java Enterprise Edition）是Java平台的企业级配置，提供了用于开发大型企业应用程序的扩展API和服务，包括Web应用程序开发、企业级数据库访问、消息队列和分布式计算等。
JavaME（Java Micro Edition）是Java平台的微型配置，专门用于嵌入式设备和移动设备的应用程序开发，如手机、智能卡和小型传感器等。
Installer和Compressed Archive区别：

        Installer下载的是一个.dmg可执行文档。

        Compressed Archive压缩文档，下载的是一个.tar.gz压缩包。

另外，刚接触Mac的小伙伴你需要知道：

        DMG文件通常用作Mac应用程序的安装包。是苹果电脑上常见的磁盘映像文件格式，全称为Disk Image。



2、下载JDK（官网）
2.1、官网下载dmg安装包
Java Archive | Oracle

​​​​​​Java Archive | Oracle 中国



选择需要安装的jdk版本，我需要的是JDK8版本，这里我选择Java SE 8 (8u211 and later)，点击进入下载页面。

2.2、根据对应系统选择下载安装包
我是Intel Core i9芯片，选择的是 macOS x64 DMG Installer 点击下载后面的dmg文件

这里友情提示：有时候网络延迟，可能不会立即响应下载，需要耐心等一会，或者刷新页面重复下载。

，弹出对话框 选择勾选同意协议即可。 

注意：

        评论区有人提醒我说ARM编译更快，且m芯片必须ARM版本否则编译报错，在此提醒各位 可以下载 macOS ARM64 DMG Installer安装！！！    本人型号VK2，I9芯片，下载截图中版本一切正常。

 如果提示需要登录Oracle账号，这里提供一个：

# 提醒：为了大伙的方便，请不要随便用该邮箱重新注册Oracle账号！
账号：yawoniu@163.com
密码：Oracle.123
感谢好心人，摘自《快速搞定 MAC 系统 JDK 安装及环境变量配置，让你的开发之路更加顺畅》

2.3、下载成功 找到安装包
之后在 访达 下载 中找到，下载好的jdk是这样式的





3、安装JDK
3.1、打开安装包
我已经安装好8版本了，这里我以jdk11为例。

3.2、安装


点击继续



弹出窗口，输入密码（同锁屏密码）



安装完成，关闭。



4、配置环境变量
4.1、查看安装路径
打开终端窗口，

终端窗口一般在 启动台-其他 文件夹里。

执行如下命令：

# 进入 JDK 安装目录
cd /Library/Java/JavaVirtualMachines
 
# 查看文件
ls
➜  jdk-1.8.jdk	jdk-11.jdk
 
# 查看路径
pwd
➜  /Library/Java/JavaVirtualMachines
4.2、配置环境变量文件
# 进入当前用户的 home 目录
cd /Users/xxx    xxx改为自己的电脑用户名称
 
# 输入
cd ~
 
# 打开环境变量配置文件
vi ～/.bash_profile
# 注意这里用touch命令第一次配置环境变量会报错：.bash_profile does not exist.，意思需要创建文件 直接vi、vim都行。
command+V粘贴到文件中,注意JAVA_HOME替换为自己目录 上面使用pwd已经查了

这里我写好两套代码：

        4.2.1.如果只安装一个版本jdk 如jdk8 复制下面代码粘贴到配置文件中即可。

# JDK Config
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
CLASS_PATH="$JAVA_HOME/lib"
PATH="$PATH:$JAVA_HOME/bin"
       4.2.2.如果想安装多个版本，比如像我一样安装jdk8和11，用下面这段代码。

# JDK Config
JAVA_HOME_8=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
JAVA_HOME_11=/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home
 
export JAVA_HOME=$JAVA_HOME_8
alias jdk8="export JAVA_HOME=$JAVA_HOME_8 && echo current JDK has switched to oracle jdk version 1.8. && java -version"
alias jdk11="export JAVA_HOME=$JAVA_HOME_11 && echo current JDK has switched to openjdk version 11. && java -version"
 
CLASS_PATH="$JAVA_HOME/lib"
PATH="$PATH:$JAVA_HOME/bin"
⚠️：配置文件中alias定义的别名jdk8、jdk11是用来切换jdk版本的。



按下ESC键盘   输入:wq   保存退出。

2024年02月01日更新一版 新增jdk17，懒人专属：

# JDK Config
JAVA_HOME_8=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
JAVA_HOME_11=/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home
JAVA_HOME_17=/Library/Java/JavaVirtualMachines/jdk-17.0.2.jdk/Contents/Home
 
export JAVA_HOME=$JAVA_HOME_8
alias jdk8="export JAVA_HOME=$JAVA_HOME_8 && echo current JDK has switched to oracle jdk version 1.8. && java -version"
alias jdk11="export JAVA_HOME=$JAVA_HOME_11 && echo current JDK has switched to openjdk version 11. && java -version"
alias jdk17="export JAVA_HOME=$JAVA_HOME_17 && echo current JDK has switched to openjdk version 17. && java -version"
 
CLASS_PATH="$JAVA_HOME/lib"
PATH="$PATH:$JAVA_HOME/bin"
4.3、将配置生效
敲重点：配置文件必须执行命令生效！！！

# 配置文件立即生效
source ~/.bash_profile
 
# 验证：查看 JAVA_HOME 目录
echo $JAVA_HOME
 
# 查看 JDK 版本信息
java -version
返回查看版本信息，即安装成功。





5、快速切换jdk版本
在上面的4.2.2中已配置好环境变量，直接切换即可。

原文《Mac中安装JDK1.8和JDK11双版本并任意切换》(原帖已丢失）

xxx@xxxdeMacBook-Air ~ % jdk8
current JDK has switched to oracle jdk version 1.8.
java version "1.8.0_391"
Java(TM) SE Runtime Environment (build 1.8.0_391-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.391-b13, mixed mode)
 
xxx@xxxdeMacBook-Air ~ % java -version
java version "1.8.0_391"
Java(TM) SE Runtime Environment (build 1.8.0_391-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.391-b13, mixed mode)
 
xxx@xxxdeMacBook-Air ~ % jdk11
current JDK has switched to openjdk version 11.
java version "11.0.21" 2023-10-17 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.21+9-LTS-193)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.21+9-LTS-193, mixed mode)
 
xxx@xxxdeMacBook-Air ~ % java -version
java version "11.0.21" 2023-10-17 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.21+9-LTS-193)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.21+9-LTS-193, mixed mode)

5.1、记录解决配置maven后jdk无法切换版本
2024年05月13日，今天发现在配置文件中jdk配置下方加上maven后输入jdk8报错，无法切换版本。解决办法是将maven配置置顶，不知道什么原理 有懂的大佬欢迎评论区指点迷津。

export MAVEN_HOME=/Users/mac/Owhy/apache-maven-3.9.6
export PATH=$PATH:$MAVEN_HOME/bin:$PATH:.
 
# JDK Config
JAVA_HOME_8=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
JAVA_HOME_11=/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home
JAVA_HOME_17=/Library/Java/JavaVirtualMachines/jdk-17jdk/Contents/Home
 
export JAVA_HOME=$JAVA_HOME_8
alias jdk8="export JAVA_HOME=$JAVA_HOME_8 && echo current JDK has switched to oracle jdk version 1.8. && java -version"
alias jdk11="export JAVA_HOME=$JAVA_HOME_11 && echo current JDK has switched to openjdk version 11. && java -version"
alias jdk17="export JAVA_HOME=$JAVA_HOME_17 && echo current JDK has switched to openjdk version 17. && java -version"
 
CLASS_PATH="$JAVA_HOME/lib"
PATH="$PATH:$JAVA_HOME/bin"
5.2、 解决无法切换jdk版本（command not found: jdk）
通常是在重启电脑后，或者上一次终端关闭后再次切换jdk版本，输入jdk8提示command not found: jdk8，这种情况通常是配置文件失效，重新source ~/.bash_profile即可。





6、JDK22安装
6.1、下载
依旧下载的是macOS x64 DMG Installer



6.2、安装








6.3、检查是否安装成功


6.4、修改配置文件
cd进入jdk22路径，pwd复制一下方便后面改配置文件。







懒人直接cv：

export MAVEN_HOME=/Users/mac/Owhy/apache-maven-3.9.6
export PATH=$PATH:$MAVEN_HOME/bin:$PATH:.
 
# JDK Config
JAVA_HOME_8=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
JAVA_HOME_11=/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home
JAVA_HOME_17=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
JAVA_HOME_22=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home
 
alias jdk8="export JAVA_HOME=$JAVA_HOME_8 && echo current JDK has switched to oracle jdk version 1.8. && java -version"
alias jdk11="export JAVA_HOME=$JAVA_HOME_11 && echo current JDK has switched to openjdk version 11. && java -version"
alias jdk17="export JAVA_HOME=$JAVA_HOME_17 && echo current JDK has switched to openjdk version 17. && java -version"
alias jdk22="export JAVA_HOME=$JAVA_HOME_22 && echo current JDK has switched to openjdk version 22. && java -version"
 
CLASS_PATH="$JAVA_HOME/lib"
PATH="$PATH:$JAVA_HOME/bin"



切换版本也依旧丝滑：




7、配置文件讲解：
首先，这个配置文件脚本的目的是在你的系统上设置多个Java开发工具包（JDK）的版本，并能方便地在它们之间切换。

7.1、环境变量
JAVA_HOME: 这个变量指向JDK安装的根目录。

JAVA_HOME_8=/Library/Java/JavaVirtualMachines/jdk-1.8.jdk/Contents/Home
JAVA_HOME_11=/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home
JAVA_HOME_17=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
JAVA_HOME_22=/Library/Java/JavaVirtualMachines/jdk-22.jdk/Contents/Home
每个 JAVA_HOME_xx 变量指定的是系统上安装的不同版本的JDK的路径。里面有我们熟悉的bin目录：



7.2、别名
比如jdk8、jdk17... 都是切换JDK版本的别名，这些别名方便使用者轻松地在不同的JDK版本之间切换。

alias jdk8="export JAVA_HOME=$JAVA_HOME_8 && echo current JDK has switched to oracle jdk version 1.8. && java -version"
alias jdk11="export JAVA_HOME=$JAVA_HOME_11 && echo current JDK has switched to openjdk version 11. && java -version"
alias jdk17="export JAVA_HOME=$JAVA_HOME_17 && echo current JDK has switched to openjdk version 17. && java -version"
alias jdk22="export JAVA_HOME=$JAVA_HOME_22 && echo current JDK has switched to openjdk version 22. && java -version"
alias jdk8: 将 JAVA_HOME 设置为 JDK 1.8，打印确认信息，并显示当前JDK的版本。
alias jdk11: 将 JAVA_HOME 设置为 JDK 11，打印确认信息，并显示当前JDK的版本。
alias jdk17: 将 JAVA_HOME 设置为 JDK 17，打印确认信息，并显示当前JDK的版本。
alias jdk22: 将 JAVA_HOME 设置为 JDK 22，打印确认信息，并显示当前JDK的版本。
其中，我以【alias jdk22="export JAVA_HOME=$JAVA_HOME_22 && echo current JDK has switched to openjdk version 22. && java -version"】 为例，再详细讲一下：

① alias jdk22：这是在Shell（如bash或zsh）中创建的一个” jdk22“别名。 当在终端中输入 jdk22 时，系统会执行后面定义的一系列命令。

② export：将一个变量设置为环境变量，使得该变量在所有子进程中可见。

③ JAVA_HOME=$JAVA_HOME_22：将 JAVA_HOME 变量的值设置为 JAVA_HOME_22 的值，从而改变路径。

④ echo：在终端中打印一段文字，用于提示切换成功，这里你想提示什么就写什么，甚至可以直接去掉&&符号和后面的内容！
               
原文链接：https://blog.csdn.net/Lwehne/article/details/135867512
```