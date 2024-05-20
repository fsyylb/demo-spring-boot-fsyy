# Centos 配置swap
https://zhuanlan.zhihu.com/p/632185795

1、检查是否有交换分区了，如果有输出交换分区，关闭

sudo swapon --show
sudo swapoff -a

swapon -a是启动
2、创建16G的交换分区

# 方式一
sudo fallocate -l 16G /swapfile
# 方式二，如果是centos 7 并且是xfs文件格式，使用dd
sudo dd if=/dev/zero of=/swapfile bs=1G count=16
3、设置交换分区

sudo chmod 600 /swapfile
sudo mkswap /swapfile
4、启用交换分区

sudo swapon /swapfile
5、配置系统启动时自动挂载分区

echo "/swapfile   swap    swap    defaults   0   0 " >> /etc/fstab
6、重启系统

sudo reboot
要注意的就是第二步，如果不是centos 6/7 或者是ext文件系统，还是建议用fallocate

参考文档：Unable to create swap file with error "swapon failed: Invalid argument" - Red Hat Customer Portal


# 如何在CentOS 8添加交换空间
很多认为swap是物理RAM内存已满时才使用swap。 这是一个错误的认知，内核会将非活动页面将从内存移动到交换空间swap
如你需要了解更多关于这方面知识，请阅读我们的教程：Linux性能：为什么你应该添加交换空间swap。

交换空间swap可以采用专用交换分区或交换文件的形式创建。通常，在虚拟机运行CentOS 8时，不存在交换分区，唯一的选择是创建交换文件。

交换空间swap不应视为物理内存的替代。由于交换空间是硬盘驱动器的一部分，因此它的访问速度比物理内存慢。

至于交互空间如何改善Linux系统的性能，我们在教程Linux性能：为什么你应该添加交换空间swap有详细的说明。

创建交换空间文件
在此教程中，我们将创建2 GB大小的交换空间文件。如果要添加更多交换空间，请将2G替换为你所需交换空间的大小。

创建一个指定大小文件的方式在CentOS 8中有两种，分别fallocate和dd命令。但你也可以使用你喜欢的方式创建交换空间文件，

fallocate和dd命令都可以帮助快速创建指定大小的文件，fallocate比dd命令更易于理解。具体选择取决于你。

如果您的系统上没有fallocate命令，或者您收到一条错误消息，提示fallocate failed: Operation not supported操作不支持。

你可使用dd命令创建swap交换空间文件。以下fallocate和dd命令将会在你CentOS 8的根目录中创建交换空间文件/swapfile，大小是2G。

sudo fallocate -l 2G /swapfile

sudo dd if=/dev/zero of=/swapfile bs=1024 count=2097152
默认的交换空间文件的权限只有root用户才能写入和读取交换文件的数据。因此我们需要修改交换空间文件的权限为600。

在Linux中可使用chmod修改文件权限，除此之外你还需要格式化交换空间的文件。交换空间文件所使用的文件系统与普通文件系统不一样，
交换空间它有自己的文件系统格式和专用个格式化工具mkswap。以下命令将修改交换空间文件的权限为600，并使用mkswap格式化文件。

sudo chmod 600 /swapfile
sudo mkswap /swapfile
启用交换空间
当你格式化完成后，为了让系统识别交换空间文件，因此还需要手动启用交换空间文件。

在CentOS 8中你可以使用swapon命令启用交换空间文件，它将会在自动挂载到系统中。运行命令sudo swapon /swapfile。

swapon命令启用交换空间仅当前会话可用，重启后将不会自动挂载。为了让交换空间永久启用，并在开机启动时自动挂载。

sudo swapon /swapfile
要在Linux系统启动时自动挂载分区，你需要在/etc/fstab文件中定义挂载配置选项。

/etc/fstab文件存储着文件系统的静态挂载信息，可用于定义磁盘分区，各种其他块设备或远程文件系统，告知Linux内核如何在挂载这些设备文件。

以下命令使用echo，tee命令以及管道追加行/swapfile swap swap defaults 0 0到将/etc/fstab文件文件。
当计算机在重启时，交换空间将会自动启用。可以运行命令swapon或free命令验证交换空间是否处于活动状态。

echo "/swapfile swap swap defaults 0 0" | sudo tee -a /etc/fstab

sudo swapon --show
sudo free -h

Swappiness
Swappiness是一个Linux内核属性，用于定义系统使用交换空间的频率。Swappiness可以是0到100之间的值。

swappiness=0的时候表示最大限度使用物理内存，然后才是交换空间，swappiness＝100的时候表示积极的使用交换空间。


如果你不能确定该值大小，请参考我们的教程：Linux性能：为什么你应该添加交换空间swap和空闲内存与可用内存的区别。

Linux的初始默认设置为60，你可以运行命令cat /proc/sys/vm/swappiness命令查看当前swappiness值的大小。

如果你需要对swappiness的值作出更改，请运行sudo sysctl -w vm.swappiness=10命令。

sysctl命令用于在运行时配置Linux内核的参数，更改仅在当前会话中可用，即重启会恢复为默认值。

为让swappiness的值持久化，则需要将值写入到/etc/sysctl.conf文件中。sysctl.conf是Linux内核的配置文件。在Linux内核启动是将会次配置文件的参数。

运行以下命令持久化Linux内核参数swappiness的值。

echo "/swapfile swap swap defaults 0 0" | sudo tee -a /etc/sysctl.conf
删除交换空间
如果你因某些原因需要关闭并且删除交换文件。你必须首先运行命令swapoff关闭交换空间。

如果你的交换空间文件还启用fstab方式的自动挂载，你还需要删除/etc/fstab文件中定义的自动挂载配置。

接着，使用你喜欢的编辑器打开文件/etc/fstab，并移除行定义交换空间自动挂载的行。在本教程中，我们将使用vim命令打开文件。

最后保存并退出vim。运行rm命令删除交换空间文件。

sudo swapoff -v /swapfile
sudo vim /etc/fstab
sudo rm /swapfile
结论
我们向您展示了如何在CentOS 8 创建交换空间文件以及激活和配置交换空间。如果您遇到问题或有反馈，请在下面发表评论。

https://www.myfreax.com/how-to-add-swap-space-on-centos-8/

