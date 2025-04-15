https://blog.csdn.net/weixin_42336774/article/details/86268018

转载自：https://blog.csdn.net/shuaigexiaobo/article/details/78030008

环境：CentOS 6.4
    今天小编将给大家详细讲解查看CentOS版本信息的命令。

    (一) 查看已经安装的CentOS版本信息

    1.cat /etc/issue 查看版本


    cat  缩写concatenate  cat命令可以用来显示、合并文件。

    CentOS release 6.6 (Final)

    CentOS 发行版6.6 

    etc  初期etc的英文名字缩写为etcetera ，后来大家更习惯称为 Editable Text Configuration。ETC为系统配置文件目录，该目录包含系统启动脚本、启动配置文件、用户登陆配置文件、网络配置文件、httpd 配置文件、IPSec 配置文件和其他文件等。

    2. cat /etc/redhat-release 查看CentOS版本

    

     3. cat /proc/version

  

   proc 为process的缩写，里面存放与内核相关的文件。

    显示结果：
    Linux version 2.6.32-504.12.2.el6.x86_64 (mockbuild@c6b9.bsys.dev.centos.org) (gcc version 4.4.7 20120313 (Red Hat 4.4.7-11) (GCC) ) #1 SMP Wed Mar 11 22:03:14 UTC 2015  

    我们可以看到该系统使用的是Linux 2.6.32 内核的64为操作系统。GCC 为GUN 编译器集合，采用4.4.5版本。

    4.uname -a 显示如下

    

    Linux 主机192-168-14-166
    Linux 版本2.6.32-504.12.2.el6.x86_64 64位

    5 uname -r

    显示结果：2.6.32-504.12.2.el6.x86_64

    分析结果：Linux 版本2.6.32-504.12.2.el6.x86_64 64位

  （二）查看系统是32位或者64位的方法
  1. getconf LONG_BIT or getconf WORD_BIT
  输入：getconf LONG_BIT
  返回结果：64
  输入：getconf WORD_BIT
  返回结果：32
  分析：32位的系统中int类型和long类型一般都是4字节，64位的系统中int类型还是4字节的，但是long已变成了8字节inux系统中可用”getconf WORD_BIT”和”getconf        LONG_BIT”获得word和long的位数。64位系统中应该分别得到32和64。
  所以该系统为64为Linux系统。

  2. file /bin/ls
  /bin/ls: ELF 64-bit LSB executable, x86-64, version 1 (SYSV), dynamically linked (uses shared libs), for GNU/Linux 2.6.18, stripped

   可以看到 ELF 64-bit LSB 所以该系统为64位