# centos安装websocat
要在CentOS上安装 websocat，可以使用以下步骤。请注意，CentOS的包管理器是 yum，而某些工具可能需要通过其他方式安装。

1. 安装 EPEL（Extra Packages for Enterprise Linux）
首先，确保安装了EPEL仓库，因为它包含了许多CentOS默认仓库中没有的额外软件包。运行以下命令来启用EPEL：

sudo yum install epel-release -y
1
2. 安装 websocat
websocat 可能不直接在CentOS的默认或EPEL仓库中提供。你可以从 websocat 的GitHub发布页面下载预编译的二进制文件来安装。

步骤：
下载 websocat 二进制文件

前往 websocat的GitHub发布页面 查找最新的版本，然后使用 wget 下载它。这里以 websocat 1.10.0 为例：

wget https://github.com/vi/websocat/releases/download/v1.10.0/websocat.x86_64-unknown-linux-musl
1
重命名并设置权限

将下载的文件重命名为 websocat 并设置执行权限：

mv websocat.x86_64-unknown-linux-musl websocat
chmod +x websocat
1
2
将 websocat 移动到 /usr/local/bin

这样可以使得 websocat 在系统中全局可用：

sudo mv websocat /usr/local/bin/
1
验证安装

运行以下命令来验证安装是否成功：

websocat --version
1
如果显示版本信息，则安装成功。

使用 websocat
一旦安装完成，你可以使用 websocat 发送WebSocket请求。比如，连接到一个WebSocket服务器并发送消息：

websocat ws://your_websocket_server_url
1
你也可以使用不同的选项来更复杂的用法，详情可以参考 websocat 的帮助文档：

websocat --help
1
https://blog.csdn.net/JINXFOREVER/article/details/141647127