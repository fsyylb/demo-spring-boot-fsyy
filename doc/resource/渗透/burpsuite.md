# 安装网址
```text
https://github.com/h3110w0r1d-y/BurpLoaderKeygen/blob/main/how-to-use.md
https://github.com/HeartSleep/BurpSuite-Crack/tree/master
https://sysin.org/blog/burp-suite-pro/#%E4%B8%8B%E8%BD%BD%E5%9C%B0%E5%9D%80-92
https://sysin.org/blog/burp-suite-pro-2024/
https://sysin.org/tags/#macOS

https://yaklang.io/

```

# 本机安装
```text
本机在此下载地址下载
https://sysin.org/blog/burp-suite-pro/#%E4%B8%8B%E8%BD%BD%E5%9C%B0%E5%9D%80-92

安装后，目录在
/Applications/Burp Suite Professional.app/
把群内下载的BurpLoaderKeygen_v1.17.jar
复制到/Applications/Burp Suite Professional.app/Contents/Resources/app目录下
然后修改/Applications/Burp Suite Professional.app/Contents/vmoptions.txt

在其最后添加
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED
--add-opens=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED
--add-opens=java.base/jdk.internal.org.objectweb.asm.Opcodes=ALL-UNNAMED
-javaagent:BurpLoaderKeygen_v1.17.jar
-noverify

然后运行即可

```

# Burpsuite2024安装与基本使用
```text


一、安装
二、设置介绍
三、各插件使用介绍
1、Dashboard
2、Target插件
3、HaE插件
4、Authz插件
5、Repeater
6、Collaborator
7、Sequencer
8、Decoder解码编码
9、Comparer对比上下文字段不同处
10、Logger查看经过burpsuite所有浏览记录
11、Organizer模块允许用户对扫描结果进行分类、整理和导出，方便管理和分析
12、Intruder攻击页面
一、安装
1、安装jdk11(我用的是macbookAirM1好像不装jdk也能用)

/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
brew update
brew install openjdk@11
echo 'export PATH="/opt/homebrew/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
java -version
openjdk version "11.0.25" 2024-10-15
OpenJDK Runtime Environment Homebrew (build 11.0.25+0)
OpenJDK 64-Bit Server VM Homebrew (build 11.0.25+0, mixed mode)
1
2
3
4
5
6
7
8
9
2、下载Bursuite_2024_11_2.dmz
https://portswigger.net/burp/releases
https://blog.csdn.net/qq_27868757/article/details/126692333
3、下载注册机和中文包 https://github.com/HeartSleep/BurpSuite-Crack/tree/master
BurpLoaderKeygen.jar
BurpSuiteCnV2.0.jar
4、安装bp并启动，然后关闭
5、进入bp安装目录把BurpLoaderKeygen.jar拷贝到bp安装目录的app目录下
6、返回到Contents目录，在vmoptions.txt末尾添加

--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED
--add-opens=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED
--add-opens=java.base/jdk.internal.org.objectweb.asm.Opcodes=ALL-UNNAMED
-javaagent:BurpLoaderKeygen.jar
-javaagent:BurpSuiteCnV2.0.jar
-noverify
1
2
3
4
5
6
7
6、启动bp查看help-license显示激活成功


7、如果不行就进入bp安装目录的app目录下启动注册机并激活

Java -jar  BurpLoaderKeygen.jar
1
二、设置介绍
1、

2、保存项目配置 save project settings

3、加载项目配置文件


4、设置burpsuite拦截规则

默认不拦截css js结尾的url，因为它们是静态资源，bp只拦截与服务器交互的动态资源，否则会占用过大内存和资源
这里的规则匹配是自上而下的ke通过up down调整

5、显示隐藏的表单，比如token

三、各插件使用介绍
1、Dashboard
主动漏洞扫描newscan：在scan details处写好要扫描的url(或者在Target-sitemap处右键发送到scan效果一样)，结果如下
被动漏洞扫描：这两个模块默认开启会对经过burpsuite的流量进行被动漏洞扫描，被动扫描中是根据用户浏览的网页进行扫描不会加入payload所以对系统是比较安全的，扫描结果如下
1.Live passive实时被动爬路径
2.Live audit实时审计漏洞

主动扫描可以做一些设置

在issues页面可以导出漏洞详情为html


2、Target插件
Site map:可以把经过burpsuite的流量以树形结构展现出来，并显示基本的安全问题

右键可以分析网站的动/静态url和参数url有哪些

筛选

Target-sitemap 右键Discover content可以自动爬目录/文件

Scope：可以设置拦截过滤规则，设置Proxy的HTTP history中只保留需要的url(没有sitemap中的筛选好用)


3、HaE插件
配置文件存放在~/.config/HaE/
通过正则表达式匹配有用的信息并进行高亮显示，在HTTP-history处可以看到高亮内容，可以通过HaE-databoard查看所有匹配规则

4、Authz插件
如果Orig Response Size和Response Size相等，说明可能存在未授权访问漏洞或越权漏洞(不是一定的)

5、Repeater
通过intercept页面把数据包发送到Repeater并完成Send后，下一次还想用Repeater功能最好不要从intercept再次发送，而是通过浏览器再一次访问页面并使用intercept截获最新的数据包来完成Repeater功能,如果使用payload 1’ or 1=1不好使，可以把空格转换成+号 1’+or+1=1, 或转换成%20 1’%20or%201=1,或转换成/**/
post中请求头和请求体中间至少要空2行
把鼠标悬停在payload上可以弹出payload弹框便于快速预览

6、Collaborator
即使不使用Collaborator也可以可以通过免费dnslog服务器测试无回显漏洞
有些漏洞验证过程中并不会直接返回漏洞信息给客户端，这是就需要Collaborator生成临时服务器，当客户端向服务器发送playloads时(playload中包含collaborator地址)，目标服务器会去访问collaborator地址，Collaborator服务器会记录目标服务器的请求日志从而判断目标服务器是否存在漏洞：如 SSRF（服务器端请求伪造）、XXE（XML 外部实体注入）、命令注入（含回连行为） 等。当漏洞触发时，目标系统会向 Collaborator 提供的临时地址发起请求或传输数据，Collaborator 捕获这些交互，证明漏洞存在。
(1) 先检查Burp Collaborator连通性(没问题就可以关闭这个页面了)

(2) burpsuite collaborator client
复制生成的临时域名，然后通过浏览器访问(或把域名植入playloads让目标服务器访问)域名，之后就会再此处显示collaborator服务器记录的请求日志信息
collaborator服务器生成的域名多数会被解析到国外ip

7、Sequencer
用来分析token cookie session等加密字符串是否具有规律可行，以便攻击者分析并破解(很少会用到)

8、Decoder解码编码
上面输入原文，然后点击encode as URL就可以拿到十六进制编码

9、Comparer对比上下文字段不同处
10、Logger查看经过burpsuite所有浏览记录
11、Organizer模块允许用户对扫描结果进行分类、整理和导出，方便管理和分析
12、Intruder攻击页面
选择Cluster bomb attack模式可以同时攻击多个注入点
当通过Add&添加多个注入点时，payload position会出现多个字段1-1 2-112(数字含义不用管)，每一个字段对应一个注入点，通过选择不同字段去设置不同payload

Resource Poll可以设置攻击频率，一开始可设置并发3，请求间隔1000ms，观察是否被封，如果没问题再增加并发和请求间隔

原文链接：https://blog.csdn.net/qq_25096749/article/details/144987263

```

# 保姆级 | 最新Burpsuite安装配置_burpsuite许可证密钥
```text
文章目录

0x00 前言

0x01 环境说明

0x02 准备工作

0x03 安装JDK

0x04 配置JDK环境

0x05 Burpsuite安装

0x06 Burpsuite环境配置

0x07 Burpsuite设置代理

0x08 Burpsuite使用验证

0x09 Burpsuite设置快捷启动

0x10 总结

0x00 前言
    Burp Suite 是用于攻击 web 应用程序的集成平台，包含了许多工具。Burp Suite为这些工具设计了许多接口，以加快攻击应用程序的过程。此文章主要对 Burpsuite 安装过程中使用到的工具链接做一个备忘，以便于下次安装。
0x01 环境说明
Windows 10
JDK 17.0.4.1
BurpLoaderKeygen
Burpsuite 2022.12.5
0x02 准备工作
    首先对需要使用到的工具进行下载，Burpsuite 版本根据自身需求选择即可，注意要与 JDK 版本匹配。由于版权原因，部分图片不能正常发出。
Ⅰ下载Burpsuite(选择JAR)

https://portswigger.net/burp/releases/professional-community-2022-12-5?requestededition=professional&requestedplatform=
Ⅱ下载BurpLoaderKeygen

https://github.com/NepoloHebo/BurpSuite-BurpLoaderKeygen/tree/main
Ⅲ下载JDK

https://www.oracle.com/java/technologies/downloads/#jdk18-windows
0x03 安装JDK
Ⅰ运行java安装包



Ⅱ点击Change修改jdk路径



Ⅲ安装完成



0x04 配置JDK环境
Ⅰ打开系统属性



win + R    #--键盘快捷键--
sysdm.cpl
Ⅱ选择高级，环境变量



Ⅲ在系统变量中新建一个JAVA环境变量并写入jdk安装路径



JAVA_HOME
E:\Java 17.0.5
Ⅳ双击Path环境变量，写入bin目录



%JAVA_HOME%\bin
%JAVA_HOME%\jre\bin
Ⅴ查看java，显示版本号则完成环境配置



java -version
0x05 Burpsuite安装
Ⅰ将下载好的BurpLoaderKeygen和burpsuite放在一个文件夹中



Ⅱ运行BurpLoaderKeygen



Ⅲ将注册机中的许可证密钥复制粘贴到打开后的burp suite软件输入许可证代码选项框中



Ⅳ选择Manual activation



Ⅴ将burp suite软件中的代码粘贴到注册机的第二个选项框中，激活响应中会生成激活代码



Ⅵ在激活响应中生成的激活代码复制粘贴到burp suite软件中的第三行选项框中，点击下一个



Ⅶ到这里即安装成功，您的许可证已成功安装并激活



Ⅷ点击next即可开始使用burp suite软件



0x06 Burpsuite环境配置
    文中使用的环境在 Google chrome 浏览器中，别的浏览器过程也与之类似。
Ⅰ在burp suite软件中开启代理



Ⅱ在浏览器中访问URL127.0.0.1:8080



http://127.0.0.1:8080/
Ⅲ选择右上角的CA Certificate下载软件需要的信任证书



Ⅳ在浏览器中选择设置



Ⅴ在高级中选择隐私和安全性



Ⅵ在隐私设置和安全性中选择管理证书的选项



Ⅶ导入刚刚下载好的CA证书即可



Ⅷ选择刚刚下载的CA证书



Ⅸ将证书存储在受信任的根证书颁发机构中



Ⅹ点击完成即可成功导入



0x07 Burpsuite设置代理
Ⅰ打开Google Chrome中的设置并选择隐私设置和安全性选项



Ⅱ在系统目录下选择打开代理设置



Ⅲ选择局域网设置



Ⅳ只对代理服务器中的地址进行设置，设置地址为http://127.0.0.1，端口为8080



0x08 Burpsuite使用验证
    安装好浏览器 CA 认证证书及代理后，即可正常使用 Burp Suite 软件进行抓包改包等功能进行渗透测试操作了，那么软件是否能够正常使用需要测试验证。        
Ⅰ重启浏览器



Ⅱ在burp suite软件中开启拦截进行抓包测试



Ⅲ浏览器访问www.baidu.com检测是否正常抓包，测试可以正常使用



0x09 Burpsuite设置快捷启动
    因为 Burpsuite 是一款使用 Java 语言编写的一个安全审计与扫描套件，每次启动都需要调用 Java 启动参数来执行。为了方便工作和学习，我们也可以自行设置 Burpsuite 的快捷启动。
Ⅰ在Burpsuite目录中创建一个 run.bat 批处理文件，这个文件用来启动Burpsuite软件；创建一个 start.vbs 快捷启动脚本，这个文件用来快捷启动Burpsuite软件。



Ⅱ编辑 run.bat 文件，为文件设置Burpsuite启动参数。关于Burpsuite的启动参数可以参考 BurpLoaderKeygen 文件包中的启动参考选项。设置成功后点击 run.bat 批处理文件可以打开Burpsuite软件



javaw -noverify --add-opens=java.desktop/javax.swing=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED --add-opens=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED --add-opens=java.base/jdk.internal.org.objectweb.asm.Opcodes=ALL-UNNAMED -javaagent:BurpLoaderKeygen.jar -jar burpsuite_pro_v2022.12.5.jar
Ⅲ编辑 start.vbs 文件，为文件设置Burpsuite快捷启动参数。设置成功后，点击 start.vbs 文件会调用 run.bat 批处理文件用来打开Burpsuite软件。



Set ws = CreateObject("Wscript.Shell")
ws.run "cmd /c run.bat",vbhide
Ⅳ给 start.vbs 快捷启动脚本添加一个快捷方式，重命名为 Burpsuite 并设置一个logo。



Ⅴ启动验证Burpsuite是否正常启动，验证可以正常启动Burpsuite软件。



Ⅵ这里附上Burpsuite.jpg的 logo 图片



Burpsuite.jpg

https://convertio.co/zh/jpg-ico/    #jpg图片转换为ico图标
0x10 总结
至此 Burp suite 安装配置完成。由于作者水平有限，文中若有错误与不足欢迎留言，便于及时更正。

黑客学习资源推荐
最后给大家分享一份全套的网络安全学习资料，给那些想学习 网络安全的小伙伴们一点帮助！

对于从来没有接触过网络安全的同学，我们帮你准备了详细的学习成长路线图。可以说是最科学最系统的学习路线，大家跟着这个大的方向学习准没问题。

😝朋友们如果有需要的话，可以V扫描下方二维码联系领取~



1️⃣零基础入门
① 学习路线
对于从来没有接触过网络安全的同学，我们帮你准备了详细的学习成长路线图。可以说是最科学最系统的学习路线，大家跟着这个大的方向学习准没问题。



需要详细路线图的，下面获取



② 路线对应学习视频
同时每个成长路线对应的板块都有配套的视频提供：



2️⃣视频配套工具&国内外网安书籍、文档
① 工具


② 视频


③ 书籍


资源较为敏感，未展示全面，需要的下面获取

 ### 3️⃣Python面试集锦

① 面试资料


② 简历模板




因篇幅有限，资料较为敏感仅展示部分资料，添加上方即可获取👆

------ 🙇‍♂️ 本文转自网络，如有侵权，请联系删除 🙇‍♂️ ------

本文转自 https://blog.csdn.net/Java_ZZZZZ/article/details/135685886?spm=1001.2014.3001.5501，如有侵权，请联系删除。
             
原文链接：https://blog.csdn.net/Java_ZZZZZ/article/details/135703650
```

# burp suite pro 激活
```text
1. 简介
burp suite 是进行 web 应用安全测试的一个集成平台，无缝融合各种安全工具并提供全面的接口适配，支持完整的 web 应用测试流程，从最初的映射和应用程序的攻击面分析到发现和利用安全漏洞等领域均适用。

2. 社区版与付费专业版本
burp suite 提供了两个版本给用户选择，社区免费版本和付费的专业版本，社区版本与专业版本的本质是一样的，区别在于一些功能的限制。在 kali linux 系统中默认安装的是 burp suite 社区版本，所以如果想使用专业版本那么只能自己自行下载安装。但是我并没有选择在 kali 中重新安装专业版，而是在 windows10 上手动下载安装 burp suite pro 专业版的破解版本。

下面的截图是 kali linux 自带的社区免费版本，图中红色框框起的 new scan 按钮无法使用。版本2020.12.1


20210905174609.jpg

下面的截图是在 windows10 中自己安装的专业付费版本，图中红色框框起的 new scan 按钮可以使用。版本2021.3.3

20210905174622.jpg

下图是 burp suite new live task 面板的区别，左图明显标注 live audit 是 pro 版本才有的特有功能。所以如果你需要使用高级的功能那么只能使用专业版本。

20210905220338.jpg
3. 下载专业破解版
先来下载破解版的 burp suite 至于软件这里有：https://www.jb51.net/softs/768238.html，下载后会发现得到的不是一个 .exe 文件而是 java 格式的可执行 .jar 文件。这是因为 burp suite 是基于 java 开发的，所以 burp suite 是需要基于 java 的 JVM 虚拟机进行执行的换而言之就是电脑上需要有 java 的运行环境 JRE (Java Running Environment)，所以电脑需要提前安装 JDK。

下图是解压后的文件，其中 burploader.jar 是用来生成激活证书码的激活工具，burpsuite_pro_v2021.3.3.jar 就是我们需要使用的 burp suite。


20210905221233.jpg
4. 手动激活
第 1 步，复制窗口中 license 文本框中的所有内容，复制好后点击 Run 按钮，从图中文本框中的命令可以知道点击按钮后这个激活工具会立即调用 burpsuite_pro_v2021.3.3.jar 来启动 burp suite。


20210905222615.jpg
第 2 步，这时将弹出下面这个窗口，将第 1 步复制的内容粘贴到这个 license key 窗口的文本框中，点击 Next 按钮。


20210905223314.jpg
第 3 步，这时将弹出下面这个窗口，这里什么也不用选择，直接点击 Manual activation 按钮。


20210905224346.jpg
第 4 步，这时将弹出下面这个窗口，在这个窗口直接复制 copy request 按钮左侧文本框的内容，或直接点击 copy request 按钮文本也会直接自动复制。


20210905224655.jpg
第 5 步，返回到激活工具 burploader.jar 这个软件的窗口，将第 4 步复制的内容粘贴到这个激活工具Activation Request 右侧的文本框中，这时 Activation Response 右侧的文本框中自动产生我们需要的激活码，再复制 Activation Response 右侧的文本框中的激活码内容。


20210905225450.jpg
第 6 步，返回到 burp suite 的 Manual activation 手动激活窗口，将第 5 步复制的内容粘贴到 Paste Response 按钮左侧文本框，或直接点击 Paste Response 按钮文本也会直接自动粘贴到这里。点击 Next按钮，将弹出激活成功窗口。


20210905225717.jpg
这样就激活完成了，点击 Finish 按钮结束。


20210905230244.jpg
注意：激活后直接点击burpsuite_pro_v2021.3.3.jar 来运行 burp suite pro 发现又是没有激活，发现需要激活，对于这个问题是以后启动 burp suite pro 都使用激活工具 burploader.jar 中的 Run 按钮来启动 burp suite pro 这样就不会提示需要重新激活的问题。



作者：zhbi98
链接：https://www.jianshu.com/p/60965310c414
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
```


# Burp Suite Professional 2025.6 发布，新增功能简介
```text
Burp Suite Professional 2025.6 发布，新增功能简介
Burp Suite Professional 2025.6 (macOS, Linux, Windows) - Web 应用安全、测试和扫描
Burp Suite Professional, Test, find, and exploit vulnerabilities.
请访问原文链接：sysin.org/blog/burp-s… 查看最新版。原创作品，转载请保留出处。
作者主页：sysin.org

Burp Suite Professional，更快、更可靠的安全测试，领先的 Web 安全测试工具包。

新增功能
Burp Suite Professional / Community 2025.6
2025 年 6 月 12 日
此版本引入了通过自定义操作自动修改 Burp Repeater 中消息的功能。此外，还新增了用于快速切换标签页视图的切换按钮，简化了扩展设置管理，并包含多项使用体验改进。
✅ 使用自定义操作在 Repeater 中修改消息
现在可以使用自定义操作来自动修改 Burp Repeater 中的请求和响应 (sysin)。这有助于您根据自己的工作流构建 Repeater 功能，从而简化手动测试。
例如，您现在可以执行以下操作：

修改并重新发送请求，然后自动更新响应。
直接在消息编辑器中对数据进行编码或解码，以加快分析速度。
对响应添加注释，以便更快地进行比较或分析。

✅ 轻松切换标签页视图
为方便在不同标签视图之间切换，在 Burp Repeater、Collaborator 和 Intruder 的右上角添加了切换按钮：

点击可将标签显示为单行。
再次点击则可将标签分布在多行显示。

✅ 无需编写 UI 代码即可添加扩展设置面板
现在可以使用内置的面板构建器向“Settings”对话框添加自定义设置面板 (sysin)，无需编写 UI 代码。这使得为扩展添加设置更加容易。
面板构建器支持文本与数字字段、复选框以及下拉菜单。您可以选择将设置保存到用户数据、当前项目文件，或完全不持久化。
✅ 更完善的扫描检查扩展集成
Montoya API 现在为扫描检查提供了更精细的控制。您可以按请求或主机级别配置检查，而不仅仅是按插入点进行配置。这使得您更容易控制扫描的执行时机，并减少不必要的请求。
此外，以前自定义检查中发出的请求使用的是通用 http 对象，绕过了 Burp Scanner 的配置，导致扫描默认使用标准设置。现在传入扫描检查的 http 对象已完全集成 Burp Scanner，将确保应用所有配置的扫描设置。
✅ 使用体验改进
做出了以下使用体验方面的改进：

现在可以更方便地在自定义操作和扩展中运行 shell 命令，使用 utilities().shellUtils().execute()。您可以设置超时并定义处理方式，以便使用外部工具自动化更复杂的任务。
现在可以将 Organizer 数据导出为 CSV 文件，便于在 Burp 外部保存和共享工作内容。
对于没有头部的响应（包括 HTTP/0.9 响应），Burp 现在会添加一个占位状态行，使其在消息编辑器中可见。
Burp 现在能够识别所有类型请求中的 GraphQL，不再局限于 POST 和 GET。
创建 Repeater 标签组时，现在可以使用“全选” (sysin)，更方便地将现有标签归组。
使用 Montoya API 创建的 WebSocket 连接现在会自动对 PING 帧做出 PONG 响应，避免连接意外中断。

✅ Bug 修复
修复了一个 Bug，该问题导致在 HTTP 过滤器 Bambda 编辑器中无法使用剪切（Cut）功能。
✅ 浏览器升级
已将 Burp 内置浏览器升级至以下版本：

Windows 和 macOS：Chromium 137.0.7151.69
Linux：Chromium 137.0.7151.68

详细信息请参阅 Chromium 发布说明。
下载地址
Burp Suite Professional 2025.6, 12 June 2025

更新说明（详述见上）：
此版本引入了通过自定义操作自动修改 Burp Repeater 中消息的功能。此外，还新增了用于快速切换标签页视图的切换按钮，简化了扩展设置管理，并包含多项使用体验改进。
百度网盘链接：sysin.org/blog/burp-s…


































Architectures/DescriptionFile name (Professional)Apple Intel x64 Installerburpsuite_pro_macos_x64_v2025_6.dmgApple ARM64/M Chips Installerburpsuite_pro_macos_arm64_v2025_6.dmgLinux x64 Installerburpsuite_pro_linux_v2025_6.shLinux ARM64 Installerburpsuite_pro_linux_arm64_v2025_6.shWindows x64 Installerburpsuite_pro_windows-x64_v2025_6.exeWindows ARM64 Installerburpsuite_pro_windows-arm64_v2025_6.exe

for macOS：Burp Suite Professional 2025.6 for macOS x64 & ARM64 - 领先的 Web 渗透测试软件
for Windows：Burp Suite Professional 2025.6 for Windows x64 - 领先的 Web 渗透测试软件
更多：HTTP 协议与安全

作者：sysin
链接：https://juejin.cn/post/7514997889565081663
来源：稀土掘金
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。



https://zhuanlan.zhihu.com/p/1917234110360971023
```



# Mac安装使用渗透测试神器Burpsuite[详细教程] — — 9大模块使用
```text
Mac安装配置渗透测试神器Burpsuite
介绍
Burp Suite是一个集成化的渗透测试工具，它集合了多种渗透测试组件，使我们自动化地或手工地能更好的完成对web应用的渗透测试和攻击。Burp Suite主要组件：

proxy：代理，默认地址是127.0.0.1，端口是8080
target：站点目标，地图
spider：爬虫
scanner：漏洞扫描
repeater：http请求消息与响应消息修改重放
intruder：暴力破解
sequencer：随机数分析（破解cookie、token等）
decoder：各种编码格式和散列转换
comparer：可视化差异对比功能
安装
我本地是Mac arm m3芯片，其他操作系统激活与下载方式类似。
注意：

Burp suit是java编写，需要有java环境，java官方地址：
https://www.oracle.com/cn/java/technologies/downloads/
选择对应版本下载dmg，然后直接安装即可

安装完成后，打开终端执行java -version ，检查是否安装成功
官网下载
官网2022.3版本下载链接：https://portswigger.net/burp/releases/professional-community-2022-3

其他版本下载链接：https://portswigger.net/burp/releases#professional

下载成功后是.dmg，双击后，正常安装即可：


注册机下载
https://www.123pan.com/s/dpIYjv-0e8Bh.html提取码:1234

激活
在应用程序文件夹找到burp，右击显示包的内容

修改vmoptions.txt
直接复制下面内容，替换原有内容

# Enter one VM parameter per line
# For example, to adjust the maximum memory usage to 512 MB, uncomment the following line:
# -Xmx512m
# To include another file, uncomment the following line:
# -include-options [path to other .vmoption file]

-XX:MaxRAMPercentage=50
-include-options user.vmoptions
-javaagent:BurpLoaderKeygen.jar
-javaagent:BurpSuiteChs.jar
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED
--add-opens=java.base/jdk.internal.org.objectweb.asm.tree=ALL-UNNAMED
--add-opens=java.base/jdk.internal.org.objectweb.asm.Opcodes=ALL-UNNAMED
-noverify
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
添加jar包到burpsuit包下
# finder放达打开如下路径：
/Applications/Burp Suite Professional.app/Contents/Resources/app
1
2
点击访达 - command+shift+G，输入/Applications/Burp Suite Professional.app/Contents/Resources/app

将破解工具中的BurpLoaderKeygen.jar和BurpSuiteChs.jar放入app文件夹中：

注意：该列为启用中文包，注释可取消中文


运行BurpSuit，出现如下页面：


运行注册机

重新打开一个新终端，运行注册机


随便修改个License Text的内容，然后复制License的内容，粘贴到上一步中的Enter license key，点击Next

然后点击手动激活
再选择“Manual activation”


复制"Manual activation"中的request，粘贴到注册机中的"Activation Request"，并将注册机中生成的"Activation Response"复制粘贴到"Manual activation"中的response

注册成功

使用
1 proxy 代理
默认地址是127.0.0.1，端口是8080

方式一：使用内置浏览器(无需配置代理)
如果通过burp suite自带的浏览器访问网站，是不用配置代理的

Intercept拦截：开启拦截之后，在请求到达服务器前，我们可以对包进行操作


打开内置浏览器，输入网址：


方式二：配置浏览器代理
以Firefox为例（其他浏览器都大同小异）

打开浏览器，点击右上角 - 设置


搜索代理


选择手动配置代理并设置BurpSuite代理

BurpSuite开启代理后，默认地址：127.0.0.1 8080



4. 浏览器输入http://burp，出现如下页面表示代理配置成功


2 target 目标，显示我们访问过的站点
Target模块下的Site map会以树状的显示展示我们访问过的站点



3 spider 爬虫
爬虫模块用于自动爬取网站的每个页面内容，并生成完整的网站地图

首先点击目标，进入目标模块


点击站点，鼠标右击，选中discovery content


配置爬取参数（是否只扫描文件，最大爬取子目录深度、爬取的文件拓展名等）




可以看到会话正在运行表明正在爬取网站

4. 可以将扫描站点添加到scope进行进一步分析


5. 来到dashboard，点击new scan，就可以看到我们开始添加的scope已经被添加到其中了

后续就可以针对其进行漏洞扫描，具体操作参考下方scanner模块


4 scanner 漏洞扫描
1. 主动式：new scan
新建扫描


设置扫描范围：
可以选择爬虫和审计或者只审计，扫描的url列表及scope配置



设置扫描类型（扫描参数配置）
一般默认即可。可以配置爬虫和审计的规则，一般不用选，默认就行。



配置用户凭证
如果扫描过程中需要登录，用户配置username和password。
这个只有在爬虫时检测到登陆表单会自动提交，在审计时用不到



自定义资源池（并发请求数、请求间隔时间等）


2. 被动式：new live scan (根据用户浏览的网页进行分析)
被动式是几乎不额外构造请求进行爬虫和扫描，根据用户浏览网页进行常规请求，并对请求的数据进行简单分析。

Dashboard - new live scan，创建被动扫描

配置扫描详细参数：扫描目标等

配置扫描信息：只扫描、审计严重问题等

配置扫描资源池：同时并发请求数

5 repeater 请求重放
选中请求 - 鼠标右键添加到Repeater - 编辑请求内容 - 点击Send重放请求

将数据包发送到Repeater模块中


编辑内容


点击Send 发送请求数据


6 intruder 自动化攻击（暴力破解）
在进行网页爆破的时候会用到Intruder，在Target页面对目标右键发送到Intruder。我们可以通过设置不同的负载和位置，进行各种复杂的攻击测试。
Intruder模块中主要包含四种不同攻击方式：

Sniper
Battering ram
Pitchfork
Cluster bomb
1. Sniper 狙击手模式（每次只替换一处变量）
这种模式主要适用于：竞争条件测试（选择Null payloads），密码、验证码暴力破解，重放攻击等场景。
狙击手模式使用一组payload集合，它一次只使用一个payload位置，假设你标记了两个位置“A”和“B”，payload值为“1”和“2”，那么它攻击会形成以下组合（除原始数据外）：

具体操作：

选中对应请求，鼠标右击发送到Intruder模块


选择攻击模式，添加变量信息


配置payload载荷

设置自定义爆破字典
载荷处理： 如果我们想对载荷的值进行处理，也可以点击下方Payload Processing进行前置处理，例如：我们字典中的值是admin，我们想要给他添加前缀hei，使得最终请求的载荷为heiadmin


点击右上角的start attack，发起攻击


2. Battering ram 攻城锤模式(每次替换全部变量)
攻城锤模式与狙击手模式类似的地方是，同样只使用一个payload集合，不同的地方在于每次攻击都是替换所有payload标记位置，而狙击手模式每次只能替换一个payload标记位置。


还是上面的例子，我们定义username、password两个变量，同时添加载荷，观察情况

添加username、password两个变量

设置变量可能的值

点击send attack，发起攻击，查看结果
载荷处理： 如果我们想对载荷的值进行处理，也可以点击下方Payload Processing进行前置处理，例如：我们字典中的值是admin，我们想要给他添加前缀hei，使得最终请求的载荷为heiadmin



3. Pitchfork 草叉模式(每次替换全部变量，且一一对应)
草叉模式允许使用多组payload组合，在每个标记位置上遍历所有payload组合，假设有两个位置“A”和“B”，payload组合1的值为“1”和“2”，payload组合2的值为“3”和“4”，则攻击模式如下：

Pitchfork最多只能设置三个Payload以及三个position，如果超过三个就会出现以下报错
还是按照上面的例子，我们定义了username、password两个变量

设置负载（载荷），此时我们会发现，我们定义了几个变量就可以设置几个变量字典，字典1对应变量1、字典2对应变量2…
载荷处理： 如果我们想对载荷的值进行处理，也可以点击下方Payload Processing进行前置处理，例如：我们字典中的值是admin，我们想要给他添加前缀hei，使得最终请求的载荷为heiadmin


3. 点击start attack，查看结果


4. Cluster bomb 集束炸弹模式(每次将变量进行排列组合)
集束炸弹模式跟草叉模式不同的地方在于，集束炸弹模式会对payload组进行笛卡尔积，还是上面的例子，如果用集束炸弹模式进行攻击，则除baseline请求外，会有四次请求：

选中对应请求，鼠标右击发送到Intruder模块

设置负载（载荷），我们定义了几个变量就可以设置几个变量字典，字典1对应变量1、字典2对应变量2…


点击start attack，发起攻击
载荷处理： 如果我们想对载荷的值进行处理，也可以点击下方Payload Processing进行前置处理，例如：我们字典中的值是admin，我们想要给他添加前缀hei，使得最终请求的载荷为heiadmin



查看结果

总结
sniper 对变量依次进行破解。多个标记依次进行(单字典尝试，每次只替换一处变量)
Battering ram 对变量同时进行破解。多个标记同时进行（单字典尝试，每次替换所有变量）
Pitchfork 每一个变量对应一个字典，取每一个字典的对应项（多字典尝试，每次多个字典值组合一一对应进行尝试）
Cluster bomb 每个变量对应一个字典，并且进行交集破解，尝试各种组合，适用于用户名+密码的破解（多字典尝试，每次多个字典值排列组合/笛卡尔积进行尝试）
7 sequencer 随机数分析（验证cookie等）
验证cookie、token等信息是否可以预测，是否随机。此功能新手了解即可，一般使用较少。

选中请求，鼠标右击发送到sequencer模块

来到sequencer模块，配置破解参数信息
下方的 LIve Capture Option可以配置暴力破解时的并发数、请求间间隔时长


3. 点击start live capture，开始破解

4. 运行完成或者爆破请求到一定数量时，就可以分析结果

主要判断：

比如cookie或token是否是一个周期之后又轮回，比如token总共有100种，每次请求都不一样，第101次时，又开始新一周期，返回第1种token
cookie、token所用的生成方式是否可预测、可破解，比如都是对当前时间戳进行base64编码后返回等


8 decoder 解码
Burp Decoder是Burp Suite中一款编码解码工具，将原始数据转换成各种编码和哈希表的简单工具。

顶部选中Decoder模块
输入需要编解码的内容，右侧选择编解码类型，点击之后下方输入框会直接展示编解码的结果


9 comparer 比对
可视化差异对比功能，比较两个请求包或响应包的差异

选中对应请求，添加到comparer模块

点击comparer模块


参考文章：
https://www.cnblogs.com/zohn/p/17983767
https://blog.csdn.net/xiangxue666/article/details/142873022
https://blog.csdn.net/weixin_43876557/article/details/108583391
https://blog.csdn.net/weixin_45808483/article/details/121392526

原文链接：https://blog.csdn.net/weixin_45565886/article/details/144973331
```