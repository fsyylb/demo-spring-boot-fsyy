# 深入解析：GCC 默认路径与库文件管理指南
```text
深入解析：GCC 默认路径与库文件管理指南
——如何查找头文件、库路径？编译时和运行时有何区别？

1. 引言
在 Linux 开发中，GCC/G++ 的默认搜索路径决定了编译器如何查找头文件（.h）和库文件（.so/.a）。理解这些路径的规则，能帮助你解决 #include 报错、ld 链接失败、运行时库缺失等问题。

本文将详细讲解：
✅ 如何查看 GCC 默认的头文件和库路径
✅ 编译时 vs 运行时的关键区别
✅ 如何自定义路径（解决第三方库安装问题）
✅ 针对嵌入式开发（如 Rockchip MPP）的实践建议

2. 查看 GCC 默认路径
(1) 查看头文件搜索路径（#include <xxx.h>）
# C 语言头文件路径
gcc -v -E -xc - < /dev/null 2>&1 | grep -A 100 "#include <...> search"

# C++ 头文件路径
g++ -v -E -xc++ - < /dev/null 2>&1 | grep -A 100 "#include <...> search"
1
2
3
4
5
典型输出：

#include <...> search starts here:
 /usr/include
 /usr/local/include
 /usr/lib/gcc/aarch64-linux-gnu/11/include
 /usr/include/aarch64-linux-gnu
1
2
3
4
5
关键路径：

/usr/include：系统标准头文件（如 stdio.h）。
/usr/local/include：用户手动安装的软件头文件（优先级更高）。
/usr/include/aarch64-linux-gnu：架构相关头文件（如 ARM64）。
(2) 查看库文件搜索路径（-lxxx 链接时）
gcc -print-search-dirs | grep libraries
1
典型输出：

libraries: =/usr/lib:/usr/local/lib:/lib/aarch64-linux-gnu
1
关键路径：

/usr/lib：系统标准库（如 libc.so）。
/usr/local/lib：用户手动安装的库（如 libmpp.so）。
/lib/aarch64-linux-gnu：架构相关库（如 ARM64 的 glibc）。
3. 编译时 vs 运行时的关键区别
特性	编译时（gcc）	运行时（./program）
作用阶段	代码编译和链接阶段	程序执行时动态加载库
路径决定因素	-I、-L、LIBRARY_PATH	LD_LIBRARY_PATH、ldconfig
目标文件	.h（头文件）、.so/.a（库）	仅 .so（动态库）
典型报错	fatal error: xxx.h: No such file
ld: cannot find -lxxx	error while loading shared libraries: libxxx.so not found
调试工具	gcc -v、nm、ar	ldd、patchelf、ldconfig
4. 如何自定义路径？
(1) 临时添加路径（仅当前终端有效）
# 头文件路径
export C_INCLUDE_PATH=/path/to/headers:$C_INCLUDE_PATH  # C 语言
export CPLUS_INCLUDE_PATH=/path/to/headers:$CPLUS_INCLUDE_PATH  # C++

# 库文件路径（编译时）
export LIBRARY_PATH=/path/to/libs:$LIBRARY_PATH

# 运行时库路径
export LD_LIBRARY_PATH=/path/to/libs:$LD_LIBRARY_PATH
1
2
3
4
5
6
7
8
9
(2) 永久添加路径（对所有用户生效）
# 头文件：在 ~/.bashrc 或 /etc/profile 中添加环境变量
echo 'export C_INCLUDE_PATH=/path/to/headers:$C_INCLUDE_PATH' >> ~/.bashrc
source ~/.bashrc

# 库文件：写入系统配置
sudo echo "/path/to/libs" > /etc/ld.so.conf.d/custom.conf
sudo ldconfig  # 更新缓存
1
2
3
4
5
6
7
5. 常见问题与解决方案
Q1: 编译通过，但运行时提示 libxxx.so not found
原因：编译时 -L 指定了路径，但运行时未设置 LD_LIBRARY_PATH。
解决：
export LD_LIBRARY_PATH=/path/to/libs:$LD_LIBRARY_PATH
./your_program
1
2
或永久生效：
sudo echo "/path/to/libs" >> /etc/ld.so.conf
sudo ldconfig
1
2
Q2: 交叉编译时头文件/库路径不对
原因：交叉编译工具链（如 aarch64-linux-gnu-gcc）的路径未正确指定。
解决：
aarch64-linux-gnu-gcc -I/usr/aarch64-linux-gnu/include -L/usr/aarch64-linux-gnu/lib -lxxx
1
Q3: 如何强制程序使用指定路径的库？
方法：用 patchelf 修改程序的库搜索路径：
patchelf --set-rpath '/path/to/libs' your_program
1
6. 总结
查看默认路径：
头文件：gcc -v -E -xc - < /dev/null
库文件：gcc -print-search-dirs
编译时路径：-I（头文件）、-L（库文件）、LIBRARY_PATH。
运行时路径：LD_LIBRARY_PATH、/etc/ld.so.conf。
嵌入式开发：交叉编译时需显式指定架构相关路径（如 aarch64-linux-gnu）。

原文链接：https://blog.csdn.net/qq_30883899/article/details/147629003
```


# 
```text
有关LD_LIBRARY_PATH与ld.so.conf 原创
HHFCodeRv2021-04-23 17:51:52

文章标签LD_LIBRARY_PATH文章分类运维阅读数1046

我之前写过一篇关于 LD_LIBRARY_PATH与gcc/g++ -L的关系的文章，于是我自己用CPACK制作了一个Debian安装包，然后我在/home/.bashrc里添加了export LD_LIBRARY_PATH=/usr/loca/lib:$LD_LIBRARY_PATH,再然后老大告诉我这个方法不行，打回重新想办法，经过一番寻找终于找到了---ld.so.conf可以完美解决这个问题。

 

首先说说，为什么LD_LIBRARY_PATH不行？ 可以看看老外是怎么说的

一般LD用在下面情况：

升级共享库时，替换之前先测试一下
类似的，升级后的某个程序可能依赖于一些动态链接库，如果你将某个链接库替换了，程序可能就无法工作了。这时候，你可以使用LD_LIBRARY_PATH指向存有备份的一个目录，然后，你可以没有顾忌地替换系统版本了。万一出错，拷贝回去就是了
感觉LD_LIBRARY_PATH就是临时使用的，为什么呢？因为LD_LIBRARY_PATH如果设置成全局的话，如果被破坏掉的，那么就会出现大规模的破坏。不要说这个变量不会改，但是这样做就是不妥的办法，网上大家可以找到许许多多的关于LD_LIBRARY_PATH不好的文章。这里就不再多说

 

但是，如果不让用LD_LIBRARY_PATH，我们该怎么办呢？

Linux系统为大家已经想好了办法，我们使用ld.so.conf来解决。我们已经知道linux在加载动态库的时候，会去标准路径下(/lib,/usr/lib)下去寻找应用程序用到的动态库。但对于我们那些不标准的路径下我们安装了lib库，例如我把我的库安装到了/usr/local/lib下了，我们怎么办呢?

 

Linux的通常做法是：将非标准路经加入 /etc/ld.so.conf，然后运行 ldconfig 生成 ld.so.cache。 Linux在加载共享库的时候，会从 ld.so.cache 查找。[ldconfig需要在root权限下执行]

所以，我们在安装了库，但是编译程序后，ldd发现链接不到库，那么我们就要查看你安装的库是否是非标准路径，如果是那么就把你的非标准路径加入到/etc/ld.so.conf文件中，然后调用ldconfig生成下ld.so.cache，就可以了。想查看下你的库是否已经在ld.so.cache中，可以这样 ldconfig -p | grep lib**就可以了。

 

对于，Ubuntu来说，还与其他的LINUX系统不一样，在/etc/ld.so.conf中只有一句include /etc/ld.so.conf.d/*.conf，也就是说它我们不能在/etc/ld.so.conf下添加，但是我们可以在/etc/ld.so.conf.d下新建一个*.conf在这里面添加你的非标准路径就可以了，记得调用sudo ldconfig 生成ld.so.cache文件就可以了

 

补充：

 

说说今天又遇到的问题：（cmake之后出现的问题）

问题1：/usr/bin/ld: warning: libboost_system.so.1.55.0, need by /usr/local/lib/libboost_thread.so, may conflict with libboost_system.so.1.48.0

            .....

问题2: `.text._ZN5boost16exceptions_detail10bad_alloc_D2Ev' refereced section `.text._ZN5boost16exceptions_detail10bad_alloc_D1Ev'..... of /usr/lib/gcc/x86_64-linux-gnu/4.8.2/../../../libboost_thread.a(thread.o)

 

这两个问题折腾我了一下午，这两个问题的变现不一样，其实他们都是一个问题造成的。

首先说说为什么会这样？

第一个问题：是由于系统里面在/usr/lib下面有一个libboost_system.so.1.48.0，而在/usr/local/lib下我安装了一个libboost_system.so.1.55.0，造成了g++链接时的冲突

第二个问题：是由于在/usr/lib下有一个libboost_thread.a，而我在/usr/local/lib下安装了libboost_thread.so.1.55.0，造成了g++链接的冲突错误。

 

现在问题来了，他们为什么会冲突呢？为什么呢？我们在编译程序的时候，gcc/g++是怎样搜索链接库呢？

经过仔细寻找，我们使用 ld --verbose | grep SEARCH_DIR会出现下面的内容

SEARCH_DIR("/usr/i686-linux-gnu/lib32"); SEARCH_DIR("=/usr/local/lib32"); SEARCH_DIR("=/lib32"); SEARCH_DIR("=/usr/lib32"); SEARCH_DIR("=/usr/local/lib/i386-linux-gnu"); SEARCH_DIR("=/usr/local/lib"); SEARCH_DIR("=/lib/i386-linux-gnu"); SEARCH_DIR("=/lib"); SEARCH_DIR("=/usr/lib/i386-linux-gnu"); SEARCH_DIR("=/usr/lib");

 

这就是gcc再编译的时候，所搜寻的库文件所在的路径，所以就出现了上面的问题。当gcc/g++在/usr/lib中发现这些库之后，就不再搜索了，然后就让gcc/g++链接了这些库，而我需要的正确的库应该在/usr/local/lib下，这就造成了链接了错误的库文件。

解决方法就是：删除掉/usr/lib下同名的不需要的或者版本过低的库文件（目前是这样做的，也许有更好的方法，知道的请告诉我）

 

上面查看gcc/g++/ld搜索路径还有其他方法：

            gcc -Wl,  --verbose 

            gcc --print-search-dirs
https://blog.51cto.com/u_15177889/2727559
```

# linux动态库默认搜索路径设置的三种方法
```text
Linux 动态库的默认搜索路径是 /lib 和 /usr/lib 。

动态库被创建后，一般都复制到这两个目录中。当程序执行时需要某动态库， 并且该动态库还未加载到内存中，则系统会自动到这两个默认搜索路径中去查找相应的动态库文件，然后加载该文件到内存中，这样程序就可以使用该动态库中的函数，以及该动态库的其它资源了。在 Linux 中，动态库的搜索路径除了默认的搜索路径外，还可以通过以下三种方法来指定。

方法一：在配置文件 /etc/ld.so.conf 中指定动态库搜索路径。每次编辑完该文件后，都必须运行命令 ldconfig 使修改后的配置生效 。

方法二：通过环境变量 LD_LIBRARY_PATH 指定动态库搜索路径。

export LD_LIBRARY_PATH = /share/lib: /usr/mylib
方法三：在编译目标代码时指定该程序的动态库搜索路径。

-Wl, 表示后面的参数将传给 link 程序 ld （因为 gcc 可能会自动调用ld ）。参数 "-Wl,-rpath," 指定。当指定多个动态库搜索路径时，路径之间用冒号 " ： " 分隔。

# gcc -o pos main.c -L. -lpos -Wl,-rpath=.:..:lib

#查看 可执行程序的 rpath
$ readelf -d ***


搜索动态库的先后顺序

（1）编译目标代码时指定的动态库搜索路径

（2）LD_LIBRARY_PATH

（3）/etc/ld.so.conf

（4）default path /lib, and then /usr/lib.

https://zhuanlan.zhihu.com/p/568606652
```

# ld.so.conf中添加了路径后，但gcc --print-search-dirs时还是未显示
```text
在Linux系统中，gcc --print-search-dirs显示的是编译器自身的搜索路径，而ld.so.conf配置的是运行时动态链接器(ld)的库搜索路径，两者属于不同的机制。以下是具体原因和解决方法：

机制差异‌

gcc --print-search-dirs输出的是编译时搜索路径（包括头文件和库文件），受LIBRARY_PATH和CPATH等环境变量影响
ld.so.conf仅影响程序运行时动态库的加载路径，需通过ldconfig生成缓存后才生效

验证步骤‌

执行ldconfig -v | grep 添加的路径确认路径是否已加载到缓存
通过ldd 可执行文件检查运行时是否能正确找到库

解决方案‌

若需在编译时让gcc识别路径，应通过-L参数指定或设置LIBRARY_PATH环境变量
对于运行时问题，确保修改ld.so.conf后执行了sudo ldconfig更新缓存

建议通过export LIBRARY_PATH=/your/path:$LIBRARY_PATH临时添加编译期库路径，或永久写入shell配置文件。
```

#
```text
Linux gcc链接动态库出错：LIBRARY_PATH和LD_LIBRARY_PATH的区别
发布于 2024-03-29 13:50:02
1.1K00
代码可运行
举报
文章被收录于专栏：
typecodes
关联问题
换一批
LIBRARY_PATH和LD_LIBRARY_PATH在gcc链接动态库时具体有什么区别？
如何正确设置LIBRARY_PATH和LD_LIBRARY_PATH以确保gcc能找到动态库？
为什么在Linux中使用gcc链接动态库时需要关注LIBRARY_PATH和LD_LIBRARY_PATH？
昨天在自己的CentOs7.1上写makefile的时候，发现在一个C程序在编译并链接一个已生成好的lib动态库的时候出错。链接命令大概是这样的：

代码语言：javascript代码运行次数：0
运行
AI代码解释
[root@typecodes tcpmsg]# gcc -o hello main.c -lmyhello
/usr/bin/ld: cannot find -lmyhello
collect2: error: ld returned 1 exit status
Linux gcc链接动态库出错
Linux gcc链接动态库出错
1 gcc链接动态库时的搜索路径
自以为在当前工程中设置好了环境变量LD_LIBRARY_PATH包含了工程中的lib库路径，并且还在/etc/ld.so.conf/apphome.conf中配置了lib库的路径。那么在调用动态库的时候，gcc就应该能自动去搜索该目录。

gcc链接动态库时的搜索路径
gcc链接动态库时的搜索路径
很遗憾ld链接器报了如上的错误，但是如果在上面的gcc命令中添加上-L /root/gcc_test/tcp_msg/lib/参数，即明确动态库的绝对路径，是能够链接成功的。

2 Google上查找 /usr/bin/ld: cannot find -l* 的出错原因
gg了很久gcc ld链接动态库出错的原因，结果还是没找到理想的答案。后来猜想是不是在CentOs7中LD_LIBRARY_PATH不起作用的缘故，但是也不应该，因为自己用的GCC（version 4.8.3）跟操作系统没关系。于是重新搜索了gcc LD_LIBRARY_PATH的作用，竟然发现gcc在编译链接时链接的动态库跟LIBRARY_PATH有关而跟LD_LIBRARY_PATH没关系！

3 关于Linux gcc中的LIBRARY_PATH和LD_LIBRARY_PATH参数说明
下面摘取了两篇较权威的说明资料： 

1、GNU上关于LIBRARY_PATH的说明：

代码语言：javascript代码运行次数：0
运行
AI代码解释
LIBRARY_PATH
The value of LIBRARY_PATH is a colon-separated list of directories, much like PATH.
When configured as a native compiler, GCC tries the directories thus specified when searching for special linker files, if it can't find them using GCC_EXEC_PREFIX.
Linking using GCC also uses these directories when searching for ordinary libraries for the -l option (but directories specified with -L come first).
2、man7上关于LD_LIBRARY_PATH的说明：

代码语言：javascript代码运行次数：0
运行
AI代码解释
LD_LIBRARY_PATH
A colon-separated list of directories in which to search for
ELF libraries at execution-time.  Similar to the PATH
environment variable.  Ignored in set-user-ID and set-group-ID
programs.
后面发现StackOverflow上关于LIBRARY_PATH和LD_LIBRARY_PATH的解释更直白：

代码语言：javascript代码运行次数：0
运行
AI代码解释
LIBRARY_PATH is used by gcc before compilation to search for directories containing libraries that need to be linked to your program.

LD_LIBRARY_PATH is used by your program to search for directories containing the libraries after it has been successfully compiled and linked.

EDIT: As pointed below, your libraries can be static or shared.
If it is static then the code is copied over into your program and you don't need to search for the library after your program is compiled and linked.
If your library is shared then it needs to be dynamically linked to your program and that's when LD_LIBRARY_PATH comes into play.
通过这三篇资料的说明，很快明白了LIBRARY_PATH和LD_LIBRARY_PATH的作用。于是，自己在项目配置文件中添加export LIBRARY_PATH=
L
I
B
R
A
R
Y
P
A
T
H
:
{APPHOME}/lib。接着将这个配置文件加载到CentOs的环境变量中，这样就在gcc编译不用加-L参数生成目标文件CommuTcp了。

4 总结
关于LIBRARY_PATH和LD_LIBRARY_PATH的关系，这里自己再总结一下。

4.1 Linux gcc编译链接时的动态库搜索路径
GCC编译、链接生成可执行文件时，动态库的搜索路径就包含LIBRARY_PATH，具体的搜索路径顺序如下（注意不会递归性地在其子目录下搜索）：

代码语言：javascript代码运行次数：0
运行
AI代码解释
1、gcc编译、链接命令中的-L选项；
2、gcc的环境变量的LIBRARY_PATH（多个路径用冒号分割）；
3、gcc默认动态库目录：/lib:/usr/lib:usr/lib64:/usr/local/lib。
4.2 执行二进制文件时的动态库搜索路径
链接生成二进制可执行文件后，运行该程序加载动态库文件时就会搜索包含LD_LIBRARY_PATH路径下的动态库，具体顺序如下：

代码语言：javascript代码运行次数：0
运行
AI代码解释
1、编译目标代码时指定的动态库搜索路径：用选项-Wl,rpath和include指定的动态库的搜索路径，比如gcc -Wl,-rpath,include -L. -ldltest hello.c，在执行文件时会搜索路径`./include`；
2、环境变量LD_LIBRARY_PATH（多个路径用冒号分割）；
3、在 /etc/ld.so.conf.d/ 目录下的配置文件指定的动态库绝对路径（通过ldconfig生效，一般是非root用户时使用）；
4、gcc默认动态库目录：/lib:/usr/lib:usr/lib64:/usr/local/lib等。
其中，Linux GCC默认的动态库搜索路径可以通过ld --verbose命令查看：

代码语言：javascript代码运行次数：0
运行
AI代码解释
[root@typecodes tcpmsg]# ld --verbose
    ............
    SEARCH_DIR("/usr/x86_64-redhat-linux/lib64");
    SEARCH_DIR("/usr/local/lib64");
    SEARCH_DIR("/lib64");
    SEARCH_DIR("/usr/lib64");               ##### 64位系统
    SEARCH_DIR("/usr/x86_64-redhat-linux/lib");
    SEARCH_DIR("/usr/local/lib");
    SEARCH_DIR("/lib");
    SEARCH_DIR("/usr/lib");
Linux GCC中ld --verbose命令
Linux GCC中ld --verbose命令


https://cloud.tencent.com/developer/article/2403045
```

```text
GCC（GNU Compiler Collection）是一款广泛使用的编译器，用于编译C、C++、Objective-C、Fortran、Ada等程序。GCC是Linux操作系统的默认编译器，也是许多应用程序和库的编译工具。但是，在某些情况下，GCC可能会遇到问题，导致无法使用，这会给开发者带来一定的麻烦。

以下是一些可能导致GCC不可用的原因以及解决方法：

1. 没有安装GCC

很多Linux发行版默认都提供了GCC。但是，如果你不是通过包管理器安装的Linux发行版，或者你的发行版没有提供GCC，则你需要手动安装它。在大多数情况下，安装GCC只需要几个命令。下面是在Ubuntu上安装GCC的命令：

sudo apt-get update

sudo apt-get install build-essential

在CentOS上，可以使用以下命令：

sudo yum install gcc

安装成功后，GCC就可以使用了。如果你的系统中已经安装了GCC，但是它仍然无法工作，则可以考虑以下其他原因。

2. GCC版本过低

有时候，你可能会遇到需要GCC新版本才能编译的程序。如果你的GCC版本过低，那么你将无法编译这些程序。

要解决这个问题，你需要更新GCC。这可以通过包管理器或手动下载源代码来完成。在Ubuntu上，你可以使用以下命令升级GCC：

sudo apt-get update

sudo apt-get upgrade

在CentOS上，你可以使用以下命令：

sudo yum update

更新完成后，你应该能够使用最新版本的GCC编译你的程序。

3. GCC无法找到头文件或库文件

当编译程序时，GCC需要访问标准头文件和库文件。如果这些文件不在GCC的搜索路径中，GCC将无法找到它们。

要解决这个问题，你需要告诉GCC在哪里可以找到这些文件。你可以使用以下命令将包含文件和库文件的路径添加到GCC的搜索路径中：

export C_INCLUDE_PATH=/path/to/headers

export LIBRARY_PATH=/path/to/libraries

在这里，/path/to/headers和/path/to/libraries是包含文件和库文件的路径。这将确保GCC可以找到这些文件并成功编译你的程序。

4. GCC已损坏或损坏

在某些情况下，GCC可能已经被损坏了。这可能导致GCC崩溃或无法编译程序。

要解决这个问题，你需要重新安装GCC。在Ubuntu上，你可以使用以下命令重新安装GCC：

sudo apt-get install –reinstall gcc

在CentOS上，你可以使用以下命令：

sudo yum reinstall gcc

重新安装GCC应该解决所有相关的问题并让你能够重新开始编译程序。

在Linux操作系统中，GCC是一款非常重要的工具。它可以帮助程序员编译程序并使其运行。如果你遇到GCC不可用的问题，请不要惊慌。下面了四个可能导致GCC不可用的原因以及相应的解决方法：

1.没有安装GCC：通过安uild-essential软件包或者yum install gcc 命令安装gcc

2.GCC版本过低：通过update、upgrade或yum update升级GCC

3.GCC无法找到头文件或库文件：通过export C_INCLUDE_PATH=/path/to/headers 和export LIBRARY_PATH=/path/to/libraries 命令告诉GCC头文件和库的位置

4.GCC已损坏或损坏：通过重新安装命令sudo apt-get install –reinstall gcc 或 yum reinstall gcc再安装GCC

参考:

1.https://www.studytonight.com/gcc/gcc-error-when-compiling-a-c-program-on-linux

2.https://tecadmin.net/install-gcc-on-centos/#

3.https://stackoverflow.com/questions/22128357/gcc-cant-find-header-included-in-sys-strdefs-h

相关问题拓展阅读：

Linux下C编程使用shmat gcc编译通不过，怎么解决
Linux下C编程使用shmat gcc编译通不过，怎么解决
1.源程序的编译

　　　　在Linux下面,如果要编译一个C语言源程序,我们要使用GNU的gcc编译器. 下面

　　我们以一个实例来说明如何使用gcc编译器.

　　假设我们有下面一个非常简单的源程序(hello.c):

　　int main(int argc,char **argv)

　　{

　　printf(“Hello Linux\n”);

　　}

　　要编译这个程序,我们只要在命令行下执行:

　　gcc -o hello hello.c

　　　　gcc 编译器就会为我们生成一个hello的可执行文件.执行./hello就可以中袭看到程

　　序的输出结果了.命令行中 gcc表示我们是用gcc来编译我们的源程序,-o 选项表示

　　我们要求编译器给我们输出的可执行文件名为hello 而hello.c是我们的源程序文件.

　　　　gcc编译器有许多选项,一般来说我们只要知道其中的几个就够了. -o选项我们

　　已经知道了,表示我们要求输出的可执行文件名. -c选项表示我们只要求编译器输出

　　目标代码,而不必要输出可执行文件. -g选项表示我们要求编译器在编译的时候提

　　供我们以后对程序进行调试的信息.

　　　　知道了这三个选项,我们就可以编译我们自己所写的简单的源程序了,如果你

　　想要知道更多的选项,可以查看gcc的帮助文档,那里有着许多对其它选项的详细说

　　明.

　　2.Makefile的编写

　　假设我们有下面这样的一个程序,源代码如下:

　　

　　#include “mytool1.h”

　　#include “mytool2.h”

　　int main(int argc,char **argv)

　　{

　　mytool1_print(“hello”);

　　mytool2_print(“hello”);

　　}

　　

　　#ifndef _MYTOOL_1_H

　　#define _MYTOOL_1_H

　　void mytool1_print(char *print_str);

　　#endif

　　

　　#include “mytool1.h”

　　void mytool1_print(char *print_str)

　　{

　　printf(“This is mytool1 print %s\n”,print_str);

　　}

　　

　　#ifndef _MYTOOL_2_H

　　#define _MYTOOL_2_H

　　void mytool2_print(char *print_str);

　　如唤#endif

　　

　卖橡兄　#include “mytool2.h”

　　void mytool2_print(char *print_str)

　　{

　　printf(“This is mytool2 print %s\n”,print_str);

　　}

　　当然由于这个程序是很短的我们可以这样来编译

　　gcc -c main.c

　　gcc -c mytool1.c

　　gcc -c mytool2.c

　　gcc -o main main.o mytool1.o mytool2.o

　　　　这样的话我们也可以产生main程序,而且也不时很麻烦.但是如果我们考虑一

　　下如果有一天我们修改了其中的一个文件(比如说mytool1.c)那么我们难道还要重

　　新输入上面的命令?也许你会说,这个很容易解决啊,我写一个SHELL脚本,让她帮我

　　去完成不就可以了.是的对于这个程序来说,是可以起到作用的.但是当我们把事情

　　想的更复杂一点,如果我们的程序有几百个源程序的时候,难道也要编译器重新一

　　个一个的去编译?

　　　　为此,聪明的程序员们想出了一个很好的工具来做这件事情,这就是make.我们

　　只要执行以下make,就可以把上面的问题解决掉.在我们执行make之前,我们要先

　　编写一个非常重要的文件.–Makefile.对于上面的那个程序来说,可能的一个

　　Makefile的文件是:

　　# 这是上面那个程序的Makefile文件

　　main:main.o mytool1.o mytool2.o

　　gcc -o main main.o mytool1.o mytool2.o

　　main.o:main.c mytool1.h mytool2.h

　　gcc -c main.c

　　mytool1.o:mytool1.c mytool1.h

　　gcc -c mytool1.c

　　mytool2.o:mytool2.c mytool2.h

　　gcc -c mytool2.c

　　　　有了这个Makefile文件,不过我们什么时候修改了源程序当中的什么文件,我们

　　只要执行make命令,我们的编译器都只会去编译和我们修改的文件有关的文件,其

　　它的文件她连理都不想去理的.

　　　　下面我们学习Makefile是如何编写的.

　　　　在Makefile中也#开始的行都是注释行.Makefile中最重要的是描述文件的依赖

　　关系的说明.一般的格式是:

　　target: components

　　TAB rule

　　　　之一行表示的是依赖关系.第二行是规则.

　　　　比如说我们上面的那个Makefile文件的第二行

　　main:main.o mytool1.o mytool2.o

　　　　表示我们的目标(target)main的依赖对象(components)是main.o mytool1.o

　　mytool2.o 当倚赖的对象在目标修改后修改的话,就要去执行规则一行所指定的命

　　令.就象我们的上面那个Makefile第三行所说的一样要执行 gcc -o main main.o

　　mytool1.o mytool2.o 注意规则一行中的TAB表示那里是一个TAB键

　　Makefile有三个非常有用的变量.分别是$@,$^,$~/sin 命令,然后看~/sin

　　文件,到那里面去找了. 在sin文件当中,我会找到这样的一行libm-2.1.2.so:00009fa0

　　W sin 这样我就知道了sin在 libm-2.1.2.so库里面,我用 -lm选项就可以了(去掉前面

　　的lib和后面的版本标志,就剩下m了所以是 -lm).

　　

　　4.程序的调试

　　　　我们编写的程序不太可能一次性就会成功的,在我们的程序当中,会出现许许

　　多多我们想不到的错误,这个时候我们就要对我们的程序进行调试了.

　　　　最常用的调试软件是gdb.如果你想在图形界面下调试程序,那么你现在可以选

　　择xxgdb.记得要在编译的时候加入 -g选项.关于gdb的使用可以看gdb的帮助文件.由

　　于我没有用过这个软件,所以我也不能够说出如何使用. 不过我不喜欢用gdb.跟踪

　　一个程序是很烦的事情,我一般用在程序当中输出中间变量的值来调试程序的.当

　　然你可以选择自己的办法,没有必要去学别人的.现在有了许多IDE环境,里面已经自

　　己带了调试器了.你可以选择几个试一试找出自己喜欢的一个用.

　　

　　5.头文件和系统求助

　　　　有时候我们只知道一个函数的大概形式,不记得确切的表达式,或者是不记得函数在那个头文件进行了说明.这个时候我们可以求助系统，比如说我们想知道fread这个函数的确切形式,我们只要执行 man fread 系统就会输出着函数的详细解释的.和这个函数所在的头文件说明了。如果我们要write这个函数说明，当我们执行man write时，输出的结果却不是我们所需要的。因为我们要的是write这个函数的说明，可是出来的却是write这个命令的说明。为了得到write的函数说明我们要用man 2 write。2表示我们用的是write这个函数是系统调用函数，还有一个我们常用的是3表示函数是c的库函数。

linux无法使用gcc的介绍就聊到这里吧，感谢你花时间阅读本站内容，更多关于linux无法使用gcc,遇到问题？Linux下GCC又不可用！,Linux下C编程使用shmat gcc编译通不过，怎么解决的信息别忘了在本站进行查找喔。

https://shuyeidc.com/wp/193347.html
```

```text
Linux系统C++程序编译与运行时加载动态库路径的设置

lyingcloud

于 2023-11-29 14:37:18 发布

阅读量1.4k
 收藏 7

点赞数
文章标签： linux c++ c语言
版权

2048 AI社区
文章已被社区收录
加入社区
库文件在链接（静态库和共享库）和运行（仅限于使用共享库的程序）时被使用，其搜索路径是在系统中进行设置的。一般 Linux 系统把/lib和 /usr/lib 两个目录作为默认的库搜索路径，所以使用这两个目录中的库时不需要进行设置搜索路径即可直接使用。但是，对于处于默认库搜索路径之外的库，就需要将库的位置添加到库的搜索路径之中。设置库文件的搜索路径有下列两种方式，可任选其一使用：

在环境变量 LD_LIBRARY_PATH 中指明库的搜索路径。
在 /etc/ld.so.conf 文件中添加库的搜索路径。
方法一
要在 Linux 系统中添加库路径，我们很容易想到环境变量 LD_LIBRARY_PATH 。这种设置方式很简单，也不需要 root 权限，命令格式如下：

$ export LD_LIBRARY_PATH=<your-lib-path>:$LD_LIBRARY_PATH
例如：

$ export LD_LIBRARY_PATH=/opt/gtk/lib:$LD_LIBRARY_PATH
可以用如下命令查看 LD_LIBRAY_PATH 是否设置成功。

$ echo $LD_LIBRARY_PATH
要注意的是，这种方法只是临时设置环境变量 LD_LIBRARY_PATH ，重启或打开新的 Shell 之后，一切设置将不复存在。为了让这种方法更完美一些，可以将该 LD_LIBRARY_PATH 的 export 语句写到系统文件中，例如 /etc/profile、/etc/export、~/.bashrc 或者 ~/.bash_profile 等等。

写在不同文件对该语句的读取时机会有所不同，例如，~/.bashrc 在每次登陆和每次打开 shell 都读取一次，而 ~/.bash_profile 只在登陆时读取一次。但是对于嵌入式Linux来说，有些文件可能没有，这就需要根据目标机器的情况来设置了。我一般是加到 ~/.bashrc 中，将 export 语句添加在该文件的未尾。修改完后，还差一步，使配置生效！可以打开一个 Shell，或者使用 source 或 . （点命令）使配置文件生效。如：

$ source ~/.bashrc
方法二
将自己可能存放库文件的路径都加入到 /etc/ld.so.conf 中是明智的选择，因为这种添加库路径的效果是永久的。添加方法也很简单，将库文件的绝对路径直接写进去就OK了，一行一个。例如：

/usr/X11R6/lib
/usr/local/lib
/opt/lib
需要注意的是：这种搜索路径的设置方式对于程序连接时的库（包括共享库和静态库）的定位已经足够了，但是对于使用了共享库的程序的执行还是不够的。这是因为为了加快程序执行时对共享库的定位速度，避免使用搜索路径查找共享库的低效率，所以是直接读取库列表文件 /etc/ld.so.cache 从中进行搜索的。

/etc/ld.so.cache 是一个非文本的数据文件，不能直接编辑，它是根据 /etc/ld.so.conf 中设置的搜索路径由 /sbin/ldconfig 命令将这些搜索路径下的共享库文件集中在一起而生成的（ldconfig 命令要以 root 权限执行）。因此，为了保证程序执行时对库的定位，在 /etc/ld.so.conf 中进行了库搜索路径的设置之后，还必须要运行 /sbin/ldconfig 命令更新 /etc/ld.so.cache 文件之后才可以。

简单的说，ldconfig 的作用就是将 /etc/ld.so.conf 列出的路径下的库文件缓存到 /etc/ld.so.cache 以供使用。因此当安装完一些库文件（例如刚安装好 glib），或者修改 ld.so.conf 增加新的库路径后，需要运行一下/sbin/ldconfig使所有的库文件都被缓存到 ld.so.cache 中。如果没做，即使库文件明明就在 /usr/lib下的，也是不会被使用的，结果编译过程中抱错，缺少xxx库，去查看发现明明就在那放着，搞的想大骂 computer 蠢猪一个。

除了修改/etc/ld.so.conf，还可以在 /etc/ld.so.conf.d 目录下添加 *.conf 文件，然后往该文件添加搜索路径。同样，需要运行 ldconfig 使之生效。

其它说明
程序运行时加载动态库路径顺序(Linux)
在linux系统中，如果程序需要加载动态库，它会按照一定的顺序（优先级）去查找:
在这里插入图片描述
链接时路径（Link-time path）和运行时路径（Run-time path）不是一回事，当然，当你知道这个概念的时候，你当然明白。

那么自制的库在没有指定运行时库的路径时，一般是不会找到你特定目录的，先来说说链接库的搜寻顺寻：

编译目标代码时指定的动态库搜索路径(指的是用-wl,rpath或-R选项而不是-L)；example: gcc -Wl,-rpath,/home/arc/test,-rpath,/lib/,-rpath,/usr/lib/,-rpath,/usr/local/lib test.c

环境变量LD_LIBRARY_PATH指定的动态库搜索路径；（export LD_LIBRARY_PATH=/root/test/env/lib）

配置文件/etc/ld.so.conf中指定的动态库搜索路径；（更改/etc/ld.so.conf文件后记得一定要执行命令：ldconfig！该命令会将/etc/ld.so.conf文件中所有路径下的库载入内存中）

默认的动态库搜索路径/lib；

默认的动态库搜索路径/usr/lib。

在命令前加环境变量设置，该环境变量只对该命令有效，当该命令执行完成后，该环境变量就无效了。如下述命令：# LD_LIBRARY_PATH=/root/test/env/lib ./pos

下面对编译时库的查找与运行时库的查找做一个简单的比较：

编译时查找的是静态库或动态库，而运行时，查找的只是动态库

编译时可以用-L指定查找路径，或者用环境变量LIBRARY_PATH，而运行时可以用-Wl,rpath或-R选项，或者修改/etc/ld.so.conf文件或者设置环境变量LD_LIBRARY_PATH.

编译时用的链接器是ld，而运行时用的链接器是/lib/ld-linux.so.2.

编译时与运行时都会查找默认路径：/lib /usr/lib

编译时还有一个默认路径：/usr/local/lib，而运行时不会默认找查该路径。

环境变量LIBRARY_PATH可以指定编译时搜索动态库的路径；
环境变量LD_LIBRARY_PATH可以指定程序运行时动态库搜索和加载的路径；

https://blog.csdn.net/chenhongwei610/article/details/134689117
```


# gcc编译路径和运行路径区别
```text
GCC 编译路径与运行路径的主要区别在于它们分别作用于程序构建和程序执行的不同阶段，涉及不同的搜索规则和设置方式。关键区别如下：

🔧 一、核心概念区别

编译路径（Build-time Path）‌

作用阶段‌：程序编译和链接阶段（构建时）。
功能‌：
指导编译器查找静态库（.a）或动态库（.so）文件以完成链接（通过 -L 参数指定）。
影响编译器搜索头文件（.h）的顺序（通过 -I 参数或默认路径）。
特点‌：
仅影响构建过程，不写入最终可执行文件。
若编译路径设置错误，会导致链接失败（报错如 undefined reference）。

运行路径（Run-time Path）‌

作用阶段‌：程序加载执行阶段（运行时）。
功能‌：
指导操作系统加载器查找依赖的动态库（.so）。
通过 RPATH 或 RUNPATH 嵌入到可执行文件中（使用 -Wl,-rpath=<path> 设置），或通过环境变量 LD_LIBRARY_PATH 临时指定。
特点‌：
直接影响程序能否正常运行（若路径错误则报错 cannot open shared object file）。
优先级低于编译路径，但高于系统默认路径（如 /lib、/usr/lib）。
⚙️ 二、设置方式对比
类别‌	‌参数/方法‌	‌作用范围‌	‌示例命令‌
编译路径‌	-L<path>	链接时库文件搜索	gcc main.c -L./lib -lmylib
	-I<path>	预处理时头文件搜索	gcc -I./include main.c
运行路径‌	-Wl,-rpath=<path>	嵌入 RPATH 到可执行文件	gcc -Wl,-rpath=./lib -o main main.c
	LD_LIBRARY_PATH=<path>	临时环境变量	export LD_LIBRARY_PATH=./lib && ./main
🔍 三、路径搜索顺序差异
编译时库搜索顺序‌：
-L 指定路径 → 环境变量 LIBRARY_PATH → 系统默认路径（如 /usr/lib）。
运行时库搜索顺序‌：
RPATH/RUNPATH → LD_LIBRARY_PATH → /etc/ld.so.conf 配置 → 系统默认路径（/lib, /usr/lib）。
💡 四、典型问题与建议
“编译成功但运行失败”‌：编译路径设置正确（-L），但运行路径未指定（缺失 -Wl,-rpath 或 LD_LIBRARY_PATH）。
移植程序报错‌：目标机器缺少依赖库或运行路径与构建环境不一致，建议通过 ldd 检查依赖，并用 patchelf 修改 RPATH。
优先级冲突‌：避免同时使用 -Wl,-rpath 和 LD_LIBRARY_PATH，以防路径搜索顺序混乱。
📌 总结

编译路径解决‌构建期依赖‌（头文件、库链接），运行路径解决‌执行期依赖‌（动态库加载）。两者需独立配置且缺一不可，尤其在跨环境部署时需显式指定运行路径以保证兼容性。
```


# linux查看最近登陆ip
```text
在Linux系统中，查看最近登录IP地址可以通过以下几种常用方法实现：

使用last命令‌
该命令会显示系统登录历史，包括用户名、终端、来源IP和登录时间等信息。常用参数：

bash
Copy Code
last -a -i  # -a显示完整信息，-i将IP显示在最后一列
last -n 5   # 仅显示最近5条记录


输出示例中的FROM列即为登录IP地址。

通过w命令查看当前登录IP‌
该命令会实时显示已登录用户及其来源IP：

bash
Copy Code
w


输出中的FROM字段即为登录IP。

使用lastlog查看所有用户最后登录记录‌
该命令会显示所有用户的最后一次登录信息，包括IP地址：

bash
Copy Code
lastlog


需注意从未登录的用户会标记为Never logged in。

查看系统日志文件‌
登录记录通常存储在以下文件中，可直接用grep过滤IP：

bash
Copy Code
cat /var/log/secure | grep "Accepted password"  # 适用于SSH登录记录
cat /var/log/wtmp   # 二进制文件，需通过last命令解析
```:ml-citation{ref="7,12" data="citationList"}



审计日志工具‌
若系统启用了auditd服务，可通过以下命令查看详细登录审计：

bash
Copy Code
ausearch -m USER_LOGIN --start today
```:ml-citation{ref="7" data="citationList"}  


注意事项‌：

部分命令需要root权限才能查看完整信息。
历史记录可能被轮转或清理，可通过/etc/logrotate.conf配置查看保留周期。
```


```text
在Linux系统中，在大多数情况下，直接通过命令行查看最近登录的IP地址是不可能的，因为登录历史通常不会自动记录在系统日志中。不过，可以通过以下几种方法间接获取或追踪最近登录的IP地址：

1. 查看SSH日志

如果你的系统使用SSH服务，可以查看SSH的日志文件来找到最近的登录尝试。这通常存储在/var/log/auth.log（在某些系统中是/var/log/secure或/var/log/auth.log）。你可以使用grep命令来搜索特定的IP地址或登录尝试。

sudo grep 'Accepted' /var/log/auth.log

或者如果你使用的是较新的系统，可能是：

sudo grep 'Accepted' /var/log/secure
2. 使用last命令

last命令可以显示登录系统的用户列表，包括登录时间、持续时间以及登录的终端或远程IP地址。

last

这将列出所有用户的登录历史，包括IP地址。你可以使用管道和grep来过滤特定的IP地址或用户。

3. 查看who命令的输出

who命令显示当前登录到系统的用户列表，包括它们的IP地址（如果通过SSH）。

who
4. 使用lastlog命令

lastlog命令显示系统中所有用户的最后登录信息，包括IP地址。

sudo lastlog
5. 配置SSH以记录更多信息

如果你发现默认的日志记录不够详细，可以修改SSH配置文件（通常是/etc/ssh/sshd_config），确保以下设置被启用：

LogLevel INFO

然后重启SSH服务：

sudo systemctl restart sshd

或者对于使用SysVinit的系统：

sudo service ssh restart

确保这些设置后，相关的登录信息将更加详细地记录在日志文件中。

注意事项：

确保你有足够的权限（通常需要root权限）来查看这些日志文件。

日志文件的路径可能因Linux发行版而异，常见的有/var/log/auth.log、/var/log/secure等。

某些系统可能会使用不同的日志管理系统（如systemd-journal），在这种情况下，你可以使用journalctl命令来查看SSH登录尝试：

sudo journalctl | grep sshd

或者更具体地：

sudo journalctl -u sshd | grep 'Accepted'

通过以上方法，你应该能够找到或追踪到最近登录的IP地址。
```