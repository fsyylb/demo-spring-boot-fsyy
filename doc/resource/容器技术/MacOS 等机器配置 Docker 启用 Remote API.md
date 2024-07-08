Docker 不仅可以通过本地命令行 docker 命令进行调用，还可以通过开启远程控制 API，使用 HTTP 调用接口来进行访问，远程控制 Docker Daemon 来做很多操作。

安装 Docker
如果没有 Docker 可以先进行安装：

# 国外主机
curl -sSL https://get.docker.com | sh

# 国内主机
curl -sSL https://get.daocloud.io/docker | sh
启用 API
Docker 的远程 API 服务默认监听的是 TCP 2375 端口，为了保证安全，Docker 安装后默认不会启用远程 API 服务，因为这个服务默认不做权限认证。

如果你的防火墙或者安全组允许了 2375 端口访问，同时也开启了 Docker 默认的远程 API 服务，那么大几率你的服务器会被拿来挖矿…

所以推荐在内网环境中使用，安全上会有保证，如果是外网生产环境建议做好 iptables 安全加固或用完即焚或使用 TLS 安全认证等等。

下面分别说明 CentOS 以及 MacOS 如何开启远程 API 服务：

CentOS
CentOS 的开启方法比较简单，先修改配置：

vim /usr/lib/systemd/system/docker.service
修改 ExecStart 配置项，默认如下：

ExecStart=/usr/bin/dockerd -H fd:// --containerd=/run/containerd/containerd.sock
增加一个 -H tcp://0.0.0.0:2375 选项

ExecStart=/usr/bin/dockerd -H fd:// -H tcp://0.0.0.0:2375 --containerd=/run/containerd/containerd.sock
如果是内网生产环境，也可以将 0.0.0.0 改为内网 IP。同样的，2375 端口也可以修改。

但是这样可能还有一个问题，无法在命令行使用 docker 命令了，还需要添加 sock 选项：-H unix:///var/run/docker.sock，最后为：

ExecStart=/usr/bin/dockerd -H fd:// -H unix:///var/run/docker.sock -H tcp://10.105.3.115:2375 --containerd=/run/containerd/containerd.sock
修改完配置之后需要重启 Docker 服务：

systemctl daemon-reload
systemctl restart docker
重启完成后，可以使用 netstat 查看端口是否监听来确认是否成功：

[root@VM-3-115-centos ~]# netstat -nutlp | grep 2375
tcp        0      0 10.105.3.115:2375       0.0.0.0:*               LISTEN      32316/dockerd
MacOS
在 Mac 下无法直接修改配置文件来开启远程 API 服务，后来在 docker/for-mac 的 issue 中得到了解决方案。

可以运行一个 socat 容器，将 unix socket 上的 Docker API 转发到 MacOS 上指定的端口中：

docker run -d -v /var/run/docker.sock:/var/run/docker.sock -p 127.0.0.1:2375:2375 bobrik/socat TCP-LISTEN:2375,fork UNIX-CONNECT:/var/run/docker.sock
测试
启用成功后，可以进行一些测试，例如直接使用浏览器访问 info 和 version 等页面获取信息。

http://127.0.0.1:2375/info

http://127.0.0.1:2375/version
这里以 version 为例，访问后可以得到如下内容则表示启用成功：

{
  "Platform": {
    "Name": "Docker Engine - Community"
  },
  "Components": [
    {
      "Name": "Engine",
      "Version": "20.10.7",
      "Details": {
        "ApiVersion": "1.41",
        "Arch": "amd64",
        "BuildTime": "2021-06-02T11:54:58.000000000+00:00",
        "Experimental": "false",
        "GitCommit": "b0f5bc3",
        "GoVersion": "go1.13.15",
        "KernelVersion": "5.10.25-linuxkit",
        "MinAPIVersion": "1.12",
        "Os": "linux"
      }
    },
    {
      "Name": "containerd",
      "Version": "1.4.6",
      "Details": {
        "GitCommit": "d71fcd7d8303cbf684402823e425e9dd2e99285d"
      }
    },
    {
      "Name": "runc",
      "Version": "1.0.0-rc95",
      "Details": {
        "GitCommit": "b9ee9c6314599f1b4a7f497e1f1f856fe433d3b7"
      }
    },
    {
      "Name": "docker-init",
      "Version": "0.19.0",
      "Details": {
        "GitCommit": "de40ad0"
      }
    }
  ],
  "Version": "20.10.7",
  "ApiVersion": "1.41",
  "MinAPIVersion": "1.12",
  "GitCommit": "b0f5bc3",
  "GoVersion": "go1.13.15",
  "Os": "linux",
  "Arch": "amd64",
  "KernelVersion": "5.10.25-linuxkit",
  "BuildTime": "2021-06-02T11:54:58.000000000+00:00"
}
