```text
go实现gRPC服务

automan02

于 2024-08-12 09:19:49 发布

阅读量1.2k
 收藏 4

点赞数
文章标签： golang 开发语言 后端
版权
 来源：来源
本文介绍如何使用go语言搭建简单的gRPC服务，内容比较基础，记录自己的踩坑过程。

1. 背景知识
gRPC是一种高效的远程过程调用（RPC）框架，用于不同计算机之间的通信和远程服务调用。gRPC的目标是让客户端向调用本地方法一样调用其他机器上的服务端应用程序提供的方法，能够帮助开发者更方便高效的构建分布式应用程序和服务。

protocol buffers是实现gRPC服务的基础，gRPC使用protocol buffers提供了一种简洁而强大的接口定义语言（IDL），gRPC通过.proto文件定义服务和消息类型，由于protocol buffers是一种用于序列化结构化数据的与语言无关的格式，因此，使用.proto文件定义的服务可以使用任意语言编写业务逻辑，从而实现该服务。

2. gRPC服务实现过程
实现一个gRPC服务主要包括以下步骤：

定义服务接口：定义服务，服务的方法，接收和返回的消息类型

编译proto文件：使用protoc编译器编译生成服务相关的go文件

开发业务逻辑：实现proto中定义的方法

创建gRPC服务：创建gRPC服务器，并绑定业务逻辑

3. 基础环境配置
3.1 go get配置代理
golang默认的代理 https://proxy.golang.org 在国内下载速度很慢，使用以下命令设置golang代理为 https://goproxy.cn

go env -w GOPROXY=https://goproxy.cn,direct
参考：Go 国内加速：Go 国内加速镜像 | Go 技术论坛 (learnku.com)

3.2. protoc安装
protoc是一款命令行工具，用于自动根据proto后缀的protobuf文件生成相关的go文件，从以下github仓库中下载可执行文件

Releases · protocolbuffers/protobuf · GitHub

img

将下载完成的可执行文件放入%GOPATH%/bin目录下（也可以放入其他目录并添加环境变量）

img

在命令行中运行 protoc --version 成功显示版本号则说明安装成功

img

3.3 protocol compiler plugin安装
使用一下命令安装相关的插件，go install命令会下载依赖源码并进行编译，编译完成的可执行文件位于GOPATH下。

$ go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
$ go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest
安装完成后，能够在%GOPATH%/bin目录下找到刚刚安装完成的插件可执行文件

img

后续的自动生成proto代码时，会调用到当前目录下的可执行文件。一般go安装时会自动将GOPATH添加到环境变量，如果没有，则需要手动添加。

img

参考：Quick start | Go | gRPC

2. 定义服务
2.1 编写proto代码
定义服务接口、接收和响应的消息类型均在.proto文件中定义，以下代码中，定义了一个服务Greeter，服务中定义了一个rpc方法SayHello，该方法接收一个HelloRequest类型的消息，并返回一个HelloReply类型的响应。

syntax = "proto3";    // 指定protoc版本
 
option go_package = "gRPC_demo/protos/helloworld";   // 指定自动生成go代码时的包名
 
package helloworld;   // 指定包名，防止命名冲突
 
// 定义Greeter服务
service Greeter {
  // 定义SayHello方法
  rpc SayHello (HelloRequest) returns (HelloReply) {}
}
 
// 定义HelloRequest消息，消息中包括一个string类型的name字段
message HelloRequest {
  string name = 1;    // 1代表该字段的排序编号，如该消息还有第2个字段pass,则定义为 string pass = 2;
}
 
// 定义HelloReply消息，消息中包括一个string类型的message字段
message HelloReply {
  string message = 1;
}
}

2.2. 编译.proto文件
protocol buffers是一种与语言无关的接口定义语言，在使用不同的语言实现时，都可以通过对应的插件将.proto定义代码编译成对应语言的代码。为了适应开发过程中.proto文件的频繁变更，可以将编译命令写入gen.bat文件，方便后续发生变更时快速重新生成代码，注意将.proto文件和.bat文件放在同一层目录，并cd到bat文件所在目录执行bat文件，防止代码生成到其他目录造成目录结构混乱

// gen.bat
protoc --go_out=. --go_opt=paths=source_relative --go-grpc_out=. --go grpc_opt=paths=source_relative .helloworld.proto
执行bat文件后，将自动生成了两个go文件，**.grpc.pb.go和**.pb.go，它们包含的内容如下：

**.grpc.pb.go：该文件包含gRPC服务端和客户端的代码框架，后续将基于该框架编写服务的业务逻辑，实现.proto文件中定义的服务。

**.pb.go: 该文件保险消息类型的定义和相关的序列化/反序列化方法，通信过程中，将调用这些方法对消息进行处理。

img

2.3 同步依赖
生成的代码由于依赖未同步，可能存在大范围飘红，在命令行执行go mod tidy自动同步依赖即可

img

3. 实现业务逻辑
打开helloworld_grpc.pb.go，可以看到SayHello方法定义如下，会返回一个SayHello方法没有实现的错误，我们可以在SayHello中编写业务逻辑，但是由于helloworld_grpc.pb.go是自动生成的代码，在后续发生变更时，重新编译会覆盖我们编写的业务逻辑代码，因此强烈不建议在helloworld_grpc.pb.go中直接编写业务逻辑代码。

func (UnimplementedGreeterServer) SayHello(context.Context, *HelloRequest) (*HelloReply, error) {
	return nil, status.Errorf(codes.Unimplemented, "method SayHello not implemented")
} nil
}
推荐的方法是在项目根目录下创建一个services/helloworld目录，在目录中创建一个helloworld.go，新建一个结构体Server，继承UnimplementedGreeterServer结构体中的所有方法，并重写UnimplementedGreeterServer.SayHello方法，实现业务逻辑。

package helloworld
 
import (
    "context"
    pb "gRPC_demo/protos/helloworld"
    "log"
)
 
// 创建Server结构体，将SayHello方法注册为它的成员函数
type Server struct {
    pb.UnimplementedGreeterServer    // Server结构体继承了pb.UnimplementedGreeterServer结构体的所有方法
}
 
// 重写pb.GreeterServer.SayHello方法，实现业务逻辑
func (s *Server) SayHello(ctx context.Context, hellorequest *pb.HelloRequest) (*pb.HelloReply, error) {
    log.Printf("Received message from %v", hellorequest.GetName())
    return &pb.HelloReply{Message: "Hello " + hellorequest.GetName()}, nil
}
 

项目目录结构如下：

img

4. 编写main函数
在项目根目录编写以下main函数，新建一个gRPC服务，将gRPC服务和我们实现的Server结构体中的业务逻辑绑定到Greeter服务上，并开启监听。

package main
 
import (
    "fmt"
    pb "gRPC_demo/protos/helloworld"
    "gRPC_demo/services/helloworld"
    "log"
    "net"
    "google.golang.org/grpc"
)
 
func main() {
    // 绑定地址和端口
    grpcAddress := "0.0.0.0"
    grpcPort := 8090
    lis, err := net.Listen("tcp", fmt.Sprintf("%s:%d", grpcAddress, grpcPort))
    if err != nil {
        log.Fatalf("failed to listen: %v", err)
    }
    // 初始化gRPC服务
    s := grpc.NewServer()
    // 将gRPC服务和自定义的业务逻辑注册到Greeter服务中
    pb.RegisterGreeterServer(s, &helloworld.Server{})
    log.Printf("serving gRPC on %v", lis.Addr())
    // 将gRPC服务绑定在上面创建的tcp端口上，并开启监听
    err = s.Serve(lis)
    if err != nil {
        log.Fatalf("启动gRPC服务失败(%v)", err)
    }
}
 

5. 运行gRPC服务
使用一下命令启动gRPC服务

go run main.go
img

7. 调用测试
将proto文件导入apifox后调用SayHello方法

img

服务端也打印了调用的日志信息
```

```text
go安装依赖包（go get, go module）

正则化

于 2019-12-11 23:46:49 发布

阅读量6w
 收藏 58

点赞数 21
分类专栏： 编程语言学习笔记
版权

GitCode 开源社区
文章已被社区收录
加入社区

编程语言学习笔记
专栏收录该内容
33 篇文章
订阅专栏
安装完golang后，输入go env可以看到

GOPATH="/home/zhongzhanhui/go"
GOROOT="/usr/local/go"
GOBIN=""
1
2
3
go get
参考http://c.biancheng.net/view/123.html

go get 命令可以借助代码管理工具通过远程拉取或更新代码包及其依赖包，并自动完成编译和安装。整个过程就像安装一个 App 一样简单。这个命令在内部实际上分成了两步操作：第一步是下载源码包，第二步是执行 go install。

默认情况下，go get 可以直接使用。例如，想获取 go 的源码并编译，使用下面的命令行即可：

go get github.com/davyxu/cellnet
1
go get下载的依赖包会放到GOPATH 目录下，因此获取前需要确保 GOPATH 已经设置。Go 1.8 版本之后，GOPATH 默认在用户目录的 go 文件夹下。

go module
参考https://www.cnblogs.com/chnmig/p/11806609.html

https://www.jianshu.com/p/bbed916d16ea

https://www.cnblogs.com/klsw/p/11537850.html

这个很详细：goproxy和go modules的初步使用

go module是go官方自带的go依赖管理库。go module可以将某个项目(文件夹)下的所有依赖整理成一个 go.mod 文件,里面写入了依赖的版本等，使用go module之后我们可不需要关心GOPATH，也不用将代码放置在src下了。

GO111MODULE=off: 不使用 modules 功能。
GO111MODULE=on: 使用 modules 功能，不会去 GOPATH 下面查找依赖包。
GO111MODULE=auto: Golang 自己检测是不是使用 modules 功能。
推荐使用 Go 模块时将 GO111MODULE 设置为 on而不是atuo，将以下语句添加进 ~/bashrc中，然后重开Terminal

gedit ~/.bashrc
添加 export GO111MODULE=on
1
2
初始化
项目第一次使用 GO MODULE(项目中还没有go.mod文件) ，cd进入项目文件夹，初始化 MODULE

cd /home/zhongzhanhui/GoProject/Seckill   
go mod init Seckill  	#Seckill是项目名
1
2
此时项目根目录会出现一个 go.mod 文件，此时的 go.mod 文件只标识了项目名和go的版本,这是正常的,因为只是初始化了。 go.mod 文件内容如下：

module SecKill

go 1.13
1
2
3
检测依赖
go mod tidy
1
tidy会检测该文件夹目录下所有引入的依赖,写入 go.mod 文件，写入后会发现 go.mod 文件有所变动：

module SecKill

go 1.13

require (
	github.com/gin-contrib/sessions v0.0.1
	github.com/gin-gonic/gin v1.5.0
	github.com/jinzhu/gorm v1.9.11
	github.com/kr/pretty v0.1.0 // indirect
	gopkg.in/yaml.v2 v2.2.2
)
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
此时依赖还是没有下载的。

下载依赖
我们需要将依赖下载至本地，但不使用 go get，而是使用以下命令

go mod download
1
然而如果你没有设置 GOPROXY 为国内镜像,这步百分百会卡死。

设置镜像的语句（最好把他们写进 ~/.bashrc 中，不然每次打开Terminal都要执行一次）：

export GO111MODULE=on
export GOPROXY=https://goproxy.io
1
2
参考https://blog.csdn.net/mrtwenty/article/details/98451005

此时会将依赖全部下载至 GOPATH 下的pkg/mod文件夹中，比如此处会下载到/home/zhongzhanhui/go/pkg/mod中，同时会在项目根目录下生成 go.sum 文件, 该文件是依赖的详细依赖。但是我们开头说了,我们的项目是没有放到 GOPATH 下的,那么我们下载至 GOPATH 下是无用的,照样找不到这些包

似乎项目是可以找到放在GOPATH 下的依赖包的。

导入依赖
go mod vendor
1
执行此命令,会将刚才下载至 GOPATH 下的依赖转移至该项目根目录下的 vendor(自动新建) 文件夹下，此时我们就可以使用这些依赖了。然而实际不导入也是完全ok的。导入了反而更麻烦。

在协作中使用 GOMODULE时要注意的是, 在项目管理中,如使用git,请将 vendor 文件夹放入白名单,不然项目中带上包体积会很大。

git设置白名单方式为在git托管的项目根目录新建 .gitignore 文件



设置忽略即可。但是 go.mod 和 go.sum 不要忽略，另一人clone项目后在本地进行依赖更新(同上方依赖更新)即可。

GOLAND设置开启 GO MODULE
这部分看goproxy和go modules的初步使用 更详细。

可能是因为 GO MODULE 功能还需完善,GOLAND默认是关闭该功能的,我们需要手动打开。proxy应该填https://goproxy.io



在GoLand的setting中设置好GOPATH，然后在代码的import的依赖包处按 alt+enter ，同步依赖包即可，可以看到依赖包名会由红色变绿色。

依赖更新
这里的更新不是指版本的更新,而是指引入新依赖，不使用 go get ,我怎么在项目中加新包呢?

直接项目中 import 这个包,之后更新依赖即可依赖更新请从检测依赖部分一直执行即可,即

go mod tidy
go mod download
go mod vendor
1
2
3
GOMODULE常用命令
go mod init  # 初始化go.mod
go mod tidy  # 更新依赖文件
go mod download  # 下载依赖文件
go mod vendor  # 将依赖转移至本地的vendor文件
go mod edit  # 手动修改依赖文件
go mod graph  # 打印依赖图
go mod verify  # 校验依赖
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/weixin_41519463/article/details/103501485
```


```text
golang 下载依赖太慢 go get 太慢解决办法（goproxy 国内代理）

天天向上qw

于 2022-02-24 23:53:00 发布

阅读量3.1k
 收藏 3

点赞数 2
文章标签： python docker 人工智能 pip go
版权
用过go的同学都知道go get …… 是一个漫长的过程，既然python 有pip国内镜像，node 也有国内镜像，那go是否有类似的镜像呢？确实有go mod + goproxy 可参考 http://mirrors.aliyun.com/goproxy/ ，具体怎实现呢？

在环境变量中加入GOPROXY值为 https://mirrors.aliyun.com/goproxy/

新建一个文件夹test

在test下执行go mod init test

go get -u -v github.com/gin-gonic/gin
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/u010055810/article/details/123450000
```

```text
go查看当前项目安装了哪些包命令

rolling_kitten

已于 2022-08-13 16:43:16 修改

阅读量4.6k
 收藏

点赞数 1
分类专栏： golang 文章标签： golang 开发语言 后端
版权

golang
专栏收录该内容
4 篇文章
订阅专栏
 本文介绍如何使用golist-mall检查项目依赖，golist-uneed-upgrade-package进行升级，以及各种定制格式命令来查看软件包和依赖关系。详细讲解了golang包管理工具的实用技巧。
摘要生成于 C知道 ，由 DeepSeek-R1 满血版支持， 前往体验 >

在当前项目的终端里，注意需要这个项目下有mod文件，使用这个命令查看：

go list -m all
1
可以使用命令go list -m -u all来检查可以升级的 package，
使用go get -u need-upgrade-package升级后会将新的依赖版本更新到 go.mod *
也可以使用go get -u升级所有依赖。

更多关于go list命令，参见：
https://codingdict.com/questions/62564

如果要列出已安装的软件包，可以使用以下go list命令：

列表包
要列出工作空间中的软件包，请转到工作空间文件夹并运行以下命令：

go list ./...
./告诉从当前文件夹开始，…告诉递归向下。当然，这不仅适用于go工作区中的任何其他文件夹（但通常这是您感兴趣的）。

列出所有包裹
执行中

go list ...
在任何文件夹中列出所有软件包，包括标准库的软件包，然后是go工作区中的外部库。

软件包及其依赖性
如果您还想查看每个软件包导入的软件包，则可以尝试以下自定义格式：

go list -f "{{.ImportPath}} {{.Imports}}" ./...
-f使用package的语法为列表指定替代格式template。可以引用该go help list命令的字段可以引用其字段的结构。

如果要 递归 查看所有依赖项（ 递归 查看导入包的依赖项），可以使用以下自定义格式：

go list -f "{{.ImportPath}} {{.Deps}}" ./...
但是通常这是一个很长的列表，只是"{{.Imports}}"每个软件包的单个import（）就是您想要的。
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/rolling_kitten/article/details/121199701
```

```text

Go引入包报错 package ... is not in GOROOT
配置GOPATH

GOPATH是一个环境变量，是GO项目的存放路径
GOROOT为go安装路径

添加环境变量

vim ~/.bash_profile
 

vim编辑器按 i 进入编辑模式



 

按Esc退出编辑模式，:wq 报错退出

环境变量添加保存之后，执行以下命令让配置生效

source ~/.bash_profile
查看go环境变量配置

go env
配置后还是报相同的错，编译器没有去gopath下找包，查了一下原因是GO111MODULE没有关， gomod 和 gopath 两个包管理方案，并且相互不兼容，在 gopath 查找包，按照 goroot 和多 gopath 目录下 src/xxx 依次查找。在 gomod 下查找包，解析 go.mod 文件查找包，mod 包名就是包的前缀，里面的目录就后续路径了。在 gomod 模式下，查找包就不会去 gopath 查找，只是 gomod 包缓存在 gopath/pkg/mod 里面。

解决方法：把GO111MODULE置为off，打开终端输入即可

go env -w GO111MODULE=off

```

```text
undefined: beego.Run
go build没有找到安装的包

https://github.com/go-errors/errors

```


```text
Go 应用构建 Docker 镜像 - 解决依赖下载慢、失败问题

呜呜呜啦啦啦

于 2019-05-27 13:58:55 发布

阅读量9.6k
 收藏 4

点赞数 3
分类专栏： Go Docker 文章标签： Go Docker
版权

GitCode 开源社区
文章已被社区收录
加入社区

Docker
同时被 2 个专栏收录
5 篇文章
订阅专栏

Go
2 篇文章
订阅专栏
Go 应用构建 Docker 镜像 - 解决依赖下载慢、失败问题
在使用 Docker 构建镜像时，发现依赖下载失败，应用安装很慢的问题，通过修改镜像源，使用代理解决了这个问题

原 Dockerfile
FROM golang:1.12.3-alpine3.9
RUN mkdir /app
ADD . /app/
WORKDIR /app
RUN apk add git
RUN go build -o main endgame/main.go
CMD ["/app/main"]
1
2
3
4
5
6
7
这个 Dockerfile 中，将当前的源码目录挂载到容器中，使用 alpine 镜像，安装 git 后下载依赖并编译；
但是在构建过程中，出现安装 Git 很慢，build 时依赖下载失败

解决 Git 安装慢的问题
替换镜像源
使用阿里云的镜像，替换掉 Linux 默认的镜像，达到加速的效果

添加 RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories ，将镜像源替换为阿里云

FROM golang:1.12.3-alpine3.9
RUN mkdir /app
ADD . /app/
WORKDIR /app
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk add git
RUN go build -o main endgame/main.go
CMD ["/app/main"]
1
2
3
4
5
6
7
8
再次编译，出现 Git 下载依赖很慢，给Git 使用添加代理(前提是本地要有 SS 或者 privoxy 等其他的代理)

解决 Git 下载依赖慢的问题
给 Git 指定代理，在 Dockerfile 中添加以下内容

RUN git config --global https.proxy http://127.0.0.1:1080
RUN git config --global https.proxy https://127.0.0.1:1080
1
2
FROM golang:1.12.3-alpine3.9
RUN mkdir /app
ADD . /app/
WORKDIR /app
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk add git
RUN git config --global https.proxy http://127.0.0.1:1080
RUN git config --global https.proxy https://127.0.0.1:1080
RUN go build -o main endgame/main.go
CMD ["/app/main"]
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
此外，构建镜像的命令也需要指定使用 host 模式进行构建

docker build -t endgame . --network host
1
再次构建，发现 golang.org 的依赖下载超时；因为 GFW 导致无法访问 golang.org，尽管已经将其加入 PAC 名单，但是依然访问不了

解决 golang.org 依赖下载失败的问题
使用Golang的代理
从 1.11 开始，go 支持使用代理访问镜像；可以参考 https://goproxy.io/
修改Dockerfile中的编译命令 RUN GOPROXY="https://goproxy.io" go build -o main endgame/main.go

FROM golang:1.12.3-alpine3.9
RUN mkdir /app
ADD . /app/
WORKDIR /app
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk add git
RUN git config --global https.proxy http://127.0.0.1:1080
RUN git config --global https.proxy https://127.0.0.1:1080
RUN GOPROXY="https://goproxy.io" go build -o main endgame/main.go
CMD ["/app/main"]
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
再次尝试，依然失败，发现依然失败，goproxy.io无法访问，将其替换为另一个代理gocenter.io

最终的 Dockerfile
FROM golang:1.12.3-alpine3.9
RUN mkdir /app
ADD . /app/
WORKDIR /app
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories
RUN apk add git
RUN git config --global https.proxy http://127.0.0.1:8118
RUN git config --global https.proxy https://127.0.0.1:8118
RUN GOPROXY="https://gocenter.io" GO111MODULE=on go build -o main endgame/main.go
CMD ["/app/main"]
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/u013360850/article/details/90602149


```

```text
https://github.com/grpc-up-and-running/samples/tree/master/ch02/productinfo/go

https://protobuf.dev/downloads/
https://grpc.io/docs/languages/go/quickstart/
https://github.com/grpc/grpc-go/tree/master/examples/helloworld
https://pkg.go.dev/google.golang.org/grpc/cmd/protoc-gen-go-grpc
https://protobuf.dev/installation/
https://github.com/protocolbuffers/protobuf/releases
ctx, cancel := context.WithTimeout(context.Background(), time.Second)

```

```text

go context之WithTimeout的使用

YZF_Kevin

于 2020-07-11 22:39:09 发布

阅读量3.3w
 收藏 29

点赞数 14
分类专栏： Go语言 文章标签： go WithTimeout
版权

Go语言
专栏收录该内容
56 篇文章
订阅专栏
 本文详细解析了Go语言中context包的WithTimeout()函数如何在协程中实现超时控制。通过创建子Context并监控其Done通道，演示了在超时或手动调用cancel函数时，如何使协程优雅地退出。
摘要生成于 C知道 ，由 DeepSeek-R1 满血版支持， 前往体验 >

简言

1. context包的WithTimeout()函数接受一个 Context 和超时时间作为参数，返回其子Context和取消函数cancel

2. 新创建协程中传入子Context做参数，且需监控子Context的Done通道，若收到消息，则退出

3. 需要新协程结束时，在外面调用 cancel 函数，即会往子Context的Done通道发送消息

4. 若不调用cancel函数，到了原先创建Contetx时的超时时间，它也会自动调用cancel()函数，即会往子Context的Done通道发送消息
实验步骤

1. 利用根Context创建一个父Context，使用父Context创建2个协程，超时时间设为3秒

2. 等待8秒钟，再调用cancel函数，其实这个时候会发现在3秒钟的时候两个协程已经收到退出信号了
实验结果如下图



完整代码

package main
 
import (
	"context"
	"fmt"
	"time"
)
 
func main() {
	// 创建一个子节点的context,3秒后自动超时
	ctx, cancel := context.WithTimeout(context.Background(), time.Second*3)
 
	go watch(ctx, "监控1")
	go watch(ctx, "监控2")
 
	fmt.Println("现在开始等待8秒,time=", time.Now().Unix())
	time.Sleep(8 * time.Second)
 
	fmt.Println("等待8秒结束,准备调用cancel()函数，发现两个子协程已经结束了，time=", time.Now().Unix())
	cancel()
}
 
// 单独的监控协程
func watch(ctx context.Context, name string) {
	for {
		select {
		case <-ctx.Done():
			fmt.Println(name, "收到信号，监控退出,time=", time.Now().Unix())
			return
		default:
			fmt.Println(name, "goroutine监控中,time=", time.Now().Unix())
			time.Sleep(1 * time.Second)
		}
	}
}

————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/yzf279533105/article/details/107292247

```

```text
Go Context backgroup应用场景
go编程进阶实战 pdf
https://zhuanlan.zhihu.com/p/485698548
https://github.com/golang-china/awesome-go-zh
https://tour.go-zh.org/welcome/1

go编程进阶实战 开发命令行应用、http应用和grpc应用 pdf
```

```text
golang context 上下文管理 context.Background() 介绍
go context 上下文简单介绍和使用

        //context.WithDeadline() // 指定一个终止时间 time
	//context.WithTimeout() // 自定义超时时间 time
	//context.WithValue() // 自定义一个键值对 取值 map[key]value
	//ctx,cancel:=context.WithCancel(context.Background()) // 获取一个终止函数 

        // 设置一个50毫秒的超时
	ctx, cancel := context.WithTimeout(context.Background(), time.Millisecond*50)

        key := TraceCode("TRACE_CODE")
	traceCode, ok := ctx.Value(key).(string) // 在子goroutine中获取trace code
	if !ok {
		fmt.Println("invalid trace code")
	}

        // 设置一个50毫秒的超时
	ctx, cancel := context.WithTimeout(context.Background(), time.Millisecond*50)
	// 在系统的入口中设置trace code传递给后续启动的goroutine实现日志数据聚合
	ctx = context.WithValue(ctx, TraceCode("TRACE_CODE"), "12512312234")

        d := time.Now().Add(50 * time.Millisecond)
	ctx, cancel := context.WithDeadline(context.Background(), d)

	// 尽管ctx会过期，但在任何情况下调用它的cancel函数都是很好的实践。
	// 如果不这样做，可能会使上下文及其父类存活的时间超过必要的时间。
	defer cancel()

	select {
	case <-time.After(1 * time.Second):
		fmt.Println("overslept")
	case <-ctx.Done():
		fmt.Println(ctx.Err())
	}

context.Context是一个接口，该接口定义了四个需要实现的方法。具体签名如下：

type Context interface {
    Deadline() (deadline time.Time, ok bool)
    Done() <-chan struct{}
    Err() error
    Value(key interface{}) interface{}
}
其中：
Deadline方法需要返回当前Context被取消的时间，也就是完成工作的截止时间（deadline）；
Done方法需要返回一个Channel，这个Channel会在当前工作完成或者上下文被取消之后关闭，多次调用Done方法会返回同一个Channel；
Err方法会返回当前Context结束的原因，它只会在Done返回的Channel被关闭时才会返回非空的值；
如果当前Context被取消就会返回Canceled错误；
如果当前Context超时就会返回DeadlineExceeded错误；
Value方法会从Context中返回键对应的值，对于同一个上下文来说，多次调用Value 并传入相同的Key会返回相同的结果，该方法仅用于传递跨API和进程间跟请求域的数据；
Background()和TODO()
Go内置两个函数：Background()和TODO()，这两个函数分别返回一个实现了Context接口的background和todo。我们代码中最开始都是以这两个内置的上下文对象作为最顶层的partent context，衍生出更多的子上下文对象。

Background()主要用于main函数、初始化以及测试代码中，作为Context这个树结构的最顶层的Context，也就是根Context。

TODO()，它目前还不知道具体的使用场景，如果我们不知道该使用什么Context的时候，可以使用这个。

background和todo本质上都是emptyCtx结构体类型，是一个不可取消，没有设置截止时间，没有携带任何值的Context。

官方案例
var (
	ctx    context.Context
	cancel context.CancelFunc
)

func handleSearch(w http.ResponseWriter, req *http.Request) {
	// ctx 上下文管理 简单案例演示
	timeout, err := time.ParseDuration(req.FormValue("timeout"))
	if err == nil {
		// 初始化 上下文并获取对象
		ctx, cancel = context.WithTimeout(context.Background(), timeout)
	} else {
		ctx, cancel = context.WithCancel(context.Background())
	}
	// 延时关闭
	defer cancel()
}
demo代码演示二：


var wg sync.WaitGroup

func handleSearch(ctx context.Context) {

	for {
		time.Sleep(time.Second)
		fmt.Println("working……")
		select {
		case <-ctx.Done():
			return
		default:
		}
	}
}

func main() {
	ctx, cancel := context.WithCancel(context.Background())
	wg.Add(1) //添加

	// 如何实现go 子程序优雅的退出
	go handleSearch(ctx)
	// 案例监听5秒
	time.Sleep(time.Second * 5)
	// 连续5秒都没完成则自动退出
	cancel()  // 退出
	wg.Wait() // 等待结束
	fmt.Println("over")
}
推荐以参数的方式显示传递Context
以Context作为参数的函数方法，应该把Context作为第一个参数。
给一个函数方法传递Context的时候，不要传递nil，如果不知道传递什么，就使用context.TODO()
Context的Value相关方法应该传递请求域的必要数据，不应该用于传递可选参数
Context是线程安全的，可以放心的在多个goroutine中传递

```

```text
一文搞懂Golang中的接口
发布于 2023-10-16 12:23:28
1.8K00
代码可运行
举报
文章被收录于专栏：
周小末天天开心
关联问题
换一批
Golang中的接口是什么？
Golang接口如何定义？
Golang接口如何实现？
接口
接口介绍
Go语言中的接口（interface）是一组方法签名的集合，是一种抽象类型。接口定义了方法，但没有实现，而是由具体的类型（struct）实现这些方法，因此接口是一种实现多态的机制。

接口定义
Go语言中的接口定义语法如下：

代码语言：javascript代码运行次数：0
运行
AI代码解释
type 接口名 interface {
    方法名1(参数1 类型1, 参数2 类型2) 返回值类型1
    方法名2(参数3 类型3) 返回值类型2
    ...
}
其中，接口名是一个标识符，方法名是一个标识符，参数和返回值都是类型。一个接口可以包含多个方法，每个方法的返回值类型可以是单个值、多个值、无返回值（void）或者是另一个接口类型。

实现接口
代码语言：javascript代码运行次数：0
运行
AI代码解释
package main

import "fmt"

type animal interface {
    sound() string
}

type cat struct {
    name string
}

func (c cat) sound() string {
    return "meow"
}

type dog struct {
    name string
}

func (d dog) sound() string {
    return "woof"
}

func makeSound(a animal) {
    fmt.Println(a.sound())
}

func main() {
    c := cat{"Tom"}
    d := dog{"Fido"}

    makeSound(c)
    makeSound(d)
}
定义了一个 animal 接口，它包含了一个 sound() 方法，没有任何参数，返回一个字符串。然后我们定义了两个类型，一个是 cat，一个是 dog，它们都实现了 sound() 方法。最后我们定义了一个 makeSound() 函数，它接收一个实现了 animal 接口的对象，然后调用它的 sound() 方法打印出声音。在 main() 函数中，我们创建了一个 cat 和一个 dog 对象，然后分别传递给 makeSound() 函数进行测试。

实现接口的关键在于方法名和参数列表的匹配。只有当一个类型实现了一个接口中所有的方法，才能称之为这个接口的实现类型。在上面的例子中，cat 和 dog 都实现了 animal 接口中的 sound() 方法，因此它们都是 animal 接口的实现类型。

需要注意的是，Go语言中的接口是隐式实现的，也就是说，只要一个类型实现了接口中定义的所有方法，它就自动成为这个接口的实现类型，不需要显式地声明实现了哪个接口。这使得Go语言中的接口更加灵活，也更加容易使用。

空接口
在 Go 语言中，空接口是指一个没有任何方法的接口。空接口不仅没有方法，也没有任何需要实现的约束条件，因此可以被任何类型实现。因此，空接口也被称为万能接口。

空接口在 Go 语言中的作用非常广泛，可以用来表示任何类型的数据。在许多场景中，我们可能需要处理不同类型的数据，但是又不想为每个类型都定义一个新的接口，这时候就可以使用空接口。

实现空接口
代码语言：javascript代码运行次数：0
运行
AI代码解释
package main

import (
	"fmt"
)

// 空接口
type EmptyInterface interface {
}

func main() {
	// 可以用空接口表示任何类型的数据
	var data1 EmptyInterface = 10
	var data2 EmptyInterface = "hello"
	var data3 EmptyInterface = []int{1, 2, 3}

	fmt.Println(data1)
	fmt.Println(data2)
	fmt.Println(data3)
}
在上面的示例中，定义了一个名为 EmptyInterface 的空接口。接着，在 main 函数中定义了三个变量 data1、data2 和 data3，它们都是 EmptyInterface 类型的变量，并分别赋值为整型、字符串和整型切片。可以看到，在输出这三个变量时，都直接使用了空接口类型的变量。

类型断言
在Go语言中，可以使用类型断言（type assertion）来判断一个接口实例的底层值是什么类型，并将其转换成对应的类型。类型断言的语法如下：

代码语言：javascript代码运行次数：0
运行
AI代码解释
value, ok := interfaceVar.(Type)
其中，interfaceVar 是一个接口变量，Type 是一个具体的类型。如果 interfaceVar 的底层值是 Type 类型，则类型断言返回 interfaceVar 的底层值和 true；否则返回零值和 false。

使用类型断言
代码语言：javascript代码运行次数：0
运行
AI代码解释
package main

import (
	"fmt"
)

func main() {
	var i interface{}
	i = "hello"

	// 使用类型断言判断 i 的底层值是否为字符串类型
	if s, ok := i.(string); ok {
		fmt.Printf("i is a string: %s\n", s)
	} else {
		fmt.Println("i is not a string")
	}

	// 使用类型断言判断 i 的底层值是否为整数类型
	if n, ok := i.(int); ok {
		fmt.Printf("i is an integer: %d\n", n)
	} else {
		fmt.Println("i is not an integer")
	}
}
在上面的示例程序中，首先定义了一个空接口变量 i，并将其赋值为字符串 "hello"。然后，通过两次类型断言分别判断 i 的底层值是否为字符串类型和整数类型，最终输出判断结果。

输出结果为：

代码语言：javascript代码运行次数：0
运行
AI代码解释
i is a string: hello
i is not an integer
需要注意的是，在使用类型断言时，如果底层值不是指定类型，则会触发运行时错误。因此，在使用类型断言时，通常会将其与条件语句配合使用，以避免出现运行时错误。而且类型断言一般使用在switch语句中。

结构体实现多接口
在Go语言中，结构体可以实现一个或多个接口，这使得结构体可以具备多个不同的行为。

首先定义一个结构体类型：

代码语言：javascript代码运行次数：0
运行
AI代码解释
type Person struct {
    name string
    age  int
}
接着定义两个接口类型：

代码语言：javascript代码运行次数：0
运行
AI代码解释
type Talker interface {
    Talk() string
}

type Runner interface {
    Run() string
}
Person结构体要实现这两个接口，需要实现它们的方法：

代码语言：javascript代码运行次数：0
运行
AI代码解释
func (p Person) Talk() string {
    return "Hi, my name is " + p.name
}

func (p Person) Run() string {
    return "I can run " + strconv.Itoa(p.age) + " miles per hour"
}
这里的Talk()方法返回一个字符串，表示Person的自我介绍。Run()方法返回一个字符串，表示Person的奔跑速度。

最后可以创建一个Person实例，并将其赋值给Talker和Runner类型的变量：

代码语言：javascript代码运行次数：0
运行
AI代码解释
func main() {
    p := Person{"Tom", 20}
    var t Talker = p
    var r Runner = p
    fmt.Println(t.Talk())
    fmt.Println(r.Run())
}
在这个示例中，我们将p赋值给t和r。t是Talker类型，因此它只能访问Talk()方法；r是Runner类型，因此它只能访问Run()方法。但是，由于Person实现了这两个接口，t和r都可以访问Talk()和Run()方法。

接口嵌套
Go语言中的接口嵌套是一种将多个接口组合成一个新接口的方法。它可以让程序员更灵活地组织和复用代码。

接口嵌套的语法格式为：一个接口类型可以嵌套多个接口类型，也可以嵌套一个包含多个接口类型的接口。

嵌套接口的语法格式为：

代码语言：javascript代码运行次数：0
运行
AI代码解释
type Embed interface {
    I1
    I2
}
其中，I1和I2是已经定义好的接口类型。Embed接口嵌套了I1和I2两个接口类型，也就是说，Embed接口继承了I1和I2两个接口的所有方法。这样，实现了Embed接口的类型也必须实现I1和I2接口的所有方法。

接口嵌套可以实现代码的复用和组合。通过嵌套多个接口类型，可以定义一个新的接口类型，这个新的接口类型拥有多个接口类型的方法集合，可以更方便地调用这些方法。同时，由于接口类型的动态特性，我们可以在运行时动态地组合不同的接口类型，进一步实现代码的复用和扩展。

结构体值接收者和指针接收者实现接口的区别
1.在 Go 语言中，结构体可以通过实现接口来满足某个接口的约束条件。在实现接口时，可以使用值接收者或指针接收者。它们的主要区别在于如何处理结构体的拷贝和指针。

2.值接收者方法接收一个结构体值的副本作为接收者，而指针接收者方法接收一个结构体指针作为接收者。具体来说，值接收者方法会将结构体值拷贝一份，然后对副本进行操作，而指针接收者方法则直接操作原始结构体指针所指向的对象。

3.在实现接口时，使用值接收者方法和指针接收者方法的区别在于，使用值接收者方法实现接口时，只有结构体的值可以被传递给接口，而指针接收者方法实现接口时，可以传递结构体的指针或者任何实现了该结构体指针类型的类型。这是因为在 Go 语言中，可以通过对指针类型进行间接引用来访问结构体的字段。

4.指针接收者方法还具有一些其他的优点。例如，使用指针接收者可以避免在方法中对结构体进行拷贝，从而提高程序的性能。此外，指针接收者还可以用于修改结构体中的字段值。

5.在实现接口时，应根据实际情况选择值接收者方法或指针接收者方法。如果不需要修改结构体的字段，并且希望方法能够被传递给值类型的变量，那么使用值接收者方法就可以了。如果需要修改结构体的字段，或者希望方法能够被传递给指针类型的变量，那么就应该使用指针接收者方法。

代码案例
当使用值接收者（value receiver）实现接口时，接口会拷贝值接收者的值。而当使用指针接收者（pointer receiver）实现接口时，接口会拷贝指向值接收者的指针。这两种实现方式的主要区别在于对结构体的修改是否会影响接口中对应的值。

定义了一个接口 Geometry，并分别使用值接收者和指针接收者实现了两个结构体 Rectangle 和 Circle：

代码语言：javascript代码运行次数：0
运行
AI代码解释
package main

import (
	"fmt"
	"math"
)

type Geometry interface {
	area() float64
	perimeter() float64
}

type Rectangle struct {
	width, height float64
}

func (r Rectangle) area() float64 {
	return r.width * r.height
}

func (r Rectangle) perimeter() float64 {
	return 2*r.width + 2*r.height
}

type Circle struct {
	radius float64
}

func (c *Circle) area() float64 {
	return math.Pi * c.radius * c.radius
}

func (c *Circle) perimeter() float64 {
	return 2 * math.Pi * c.radius
}

func main() {
	r := Rectangle{width: 3, height: 4}
	c := Circle{radius: 5}

	fmt.Println("Rectangle")
	fmt.Println("Area: ", r.area())
	fmt.Println("Perimeter: ", r.perimeter())

	fmt.Println("\nCircle")
	fmt.Println("Area: ", c.area())
	fmt.Println("Perimeter: ", c.perimeter())

	// 值接收者实现的结构体修改不影响接口中对应的值
	r.width = 6
	fmt.Println("\nRectangle after modification")
	fmt.Println("Area: ", r.area())
	fmt.Println("Perimeter: ", r.perimeter())

	// 指针接收者实现的结构体修改会影响接口中对应的值
	c.radius = 7
	fmt.Println("\nCircle after modification")
	fmt.Println("Area: ", c.area())
	fmt.Println("Perimeter: ", c.perimeter())
}
输出结果为：

代码语言：javascript代码运行次数：0
运行
AI代码解释
Rectangle
Area:  12
Perimeter:  14

Circle
Area:  78.53981633974483
Perimeter:  31.41592653589793

Rectangle after modification
Area:  24
Perimeter:  14

Circle after modification
Area:  153.93804002589985
Perimeter:  43.982297150257104
可以看到，当使用值接收者实现 Rectangle 结构体时，结构体中的修改不会影响接口中对应的值；而使用指针接收者实现 Circle 结构体时，结构体中的修改会影响接口中对应的值。

```

# golang返回值是interface{}
```text
Golang interface{}
2025-04-26
20
阅读2分钟
专栏： 
golang

一、什么是 interface{}？
在 Go 语言中，interface{} 是一种空接口（empty interface） ，它表示任意类型。因为它没有定义任何方法，所以 所有类型都实现了它。

定义形式：
interface{}
等价于：

type interface{} interface {}
二、interface{} 有什么特别的？
✅ 特点：
所有类型都实现了 interface{}。
可以用来存储任意类型的值。
是 Go 的万能类型容器，类似于其他语言里的 Object 或 any。
三、使用示例
1. 存储任意类型的值：
func printAnything(v interface{}) {
    fmt.Println("值是：", v)
}
​
func main() {
    printAnything(123)
    printAnything("hello")
    printAnything([]int{1, 2, 3})
}
输出：

值是：123
值是：hello
值是：[1 2 3]
四、底层原理：interface{} 是怎么存值的？
Go 编译器将 interface{} 实际存储为两部分：

type eface struct {
    _type *_type      // 真实类型信息
    data  unsafe.Pointer  // 指向实际数据的指针
}
比如：

var a interface{} = 123
这时候：

_type：指向 int 的类型描述符；
data：指向 123 这个 int 的值。
五、怎么取出 interface{} 中的值？
1. 类型断言（Type Assertion）：
var a interface{} = "hello"
​
str, ok := a.(string)
if ok {
    fmt.Println("转换成功：", str)
} else {
    fmt.Println("转换失败")
}
2. 使用类型分支（Type Switch）：
var a interface{} = 3.14
​
switch v := a.(type) {
case int:
    fmt.Println("是 int：", v)
case float64:
    fmt.Println("是 float64：", v)
case string:
    fmt.Println("是 string：", v)
default:
    fmt.Println("未知类型")
}
六、常见使用场景
场景	描述
JSON 解析	map[string]interface{} 可以存储任意结构
fmt.Println	接收的是 ...interface{} 参数
任意类型传参	写通用工具函数，允许接收任意类型
空值或未知类型变量	当不知道变量类型时，先存为 interface{}
七、注意事项 ⚠️
interface{} 存进去什么类型，取出来的时候必须断言正确，否则运行时报错。
interface{} 本身不能直接做加减乘除等运算，必须先类型断言。
不等于 JavaScript 的 any，Go 仍然是强类型语言，类型断言很重要。
interface{} 不能直接比较，除非内部类型支持 ==。
八、面试常考问答 💡
Q：interface{} 是不是万能的？

A：它可以存储任何类型的值，但你不能随便操作这些值，除非你知道它的真实类型，并且使用类型断言来还原它。

Q：interface{} 和 interface 的区别？

A：interface{} 是一种接口类型，没有定义任何方法；而普通的接口比如 Writer interface { Write(p []byte) (n int, err error) } 定义了方法，只有实现这些方法的类型才能赋值给该接口。

九、总结一句话：
interface{} 是 Go 中可以表示任意类型的“空接口”，是实现泛型编程的基础工具之一。

👉 立即点击链接，开启你的全栈开发之路：Golang全栈开发完整课程
```

# atomicgo.dev/schedule
# https://github.com/atomicgo/schedule
# https://hostlocvps.com/2025/02/27/%E8%A7%A3%E5%86%B3go%E7%89%88%E6%9C%AC%E9%94%99%E8%AF%AF%EF%BC%9Ainvalid-go-version-must-match-format-1-23%E8%AF%A6%E8%A7%A3/
# invalid go version '1.21.1': must match format 1.23
# https://blog.csdn.net/m0_61078826/article/details/135186219
```text
项目中go mod版本与本地go版本不一致，invalid go version ‘1.20.1‘: must match format 1.23

上上谦*

于 2023-12-24 20:23:53 发布

阅读量1.1w
 收藏 13

点赞数 17
文章标签： golang 开发语言 后端
版权

GitCode 开源社区
文章已被社区收录
加入社区
版本号应该要是x.y格式的

改为下面的即可


————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/m0_61078826/article/details/135186219

```

```text
解决go版本错误：invalid go version must match format 1.23详解
 admin
 2025-2-27
 分享


解决 Go 版本错误：invalid go version: must match format 1.23 详解
1. 引言
在 Go 语言开发过程中，开发者可能会遇到形如 invalid go version: must match format 1.23 的错误。该错误通常出现在项目依赖管理、构建或运行阶段，指示 Go 工具链无法识别或处理项目所声明的 Go 版本。 此错误的出现可能中断开发流程，影响项目的构建和部署。

本文旨在深入分析该错误的成因，提供多种场景下的解决方案，并探讨其背后的 Go 版本管理机制。 通过对错误根源的剖析和解决方案的详细阐述，开发者能够更有效地解决该问题，并对 Go 的版本管理有更深入的理解。

2. 错误成因分析
invalid go version: must match format 1.23 错误的核心在于 Go 工具链无法解析项目所声明的 Go 版本字符串。 错误信息中的 "1.23" 代表期望的 Go 版本格式。 导致该错误的原因主要有以下几种：

2.1. go.mod 文件中的 go 指令版本声明错误

go.mod 文件是 Go 模块的描述文件，其中 go 指令用于声明项目所需的最低 Go 版本。如果 go 指令后的版本字符串格式不正确，Go 工具链将无法解析，从而触发该错误。

格式不规范: 版本号必须遵循 主版本号.次版本号 的格式，例如 1.18，1.20。 常见的错误包括：

缺少次版本号: 例如 go 1
包含额外的信息: 例如 go 1.18.2 (补丁版本号不应出现在 go.mod 中)
使用了非数字字符: 例如 go 1.x
使用了前导零: 例如 go 01.23
使用了多个点: 例如 go 1.23.1
版本号过低: 如果 go.mod 中声明的 Go 版本低于当前安装的 Go 工具链版本，且项目中使用了更高版本 Go 的特性，也可能间接导致类似问题。

2.2. 环境中 GOROOT 或 GOTOOLCHAIN 设置问题

Go 工具链的安装路径由 GOROOT 环境变量指定。而从 Go 1.21 开始，引入了 GOTOOLCHAIN 环境变量来管理多个 Go 工具链版本。这些环境变量的错误配置也会引发版本问题。

GOROOT 指向错误: GOROOT 环境变量应该指向 Go 的安装目录。如果指向了不存在的目录、错误的目录，或者没有设置 GOROOT，Go 工具链可能无法正常工作。
GOTOOLCHAIN 设置不当: 当使用 GOTOOLCHAIN 管理多个 Go 版本时，如果设置了无效的值，或者与 go.mod 中的版本声明冲突，也会导致版本识别问题。
GOTOOLCHAIN 的几个关键设置：
local:默认值，使用 go.mod 里面定义的版本
path/version: 下载对应版本的go,并且使用这个下载的版本
version:使用环境变量已经存在的go 版本
2.3. 使用了不受支持的 Go 版本

虽然 Go 团队通常会提供较长时间的支持，但过于老旧的 Go 版本可能不再被当前工具链支持。试图使用这些过时版本可能会导致版本解析错误。

2.4. 第三方工具或脚本干扰

某些第三方工具或自定义构建脚本可能会修改 Go 版本相关的环境变量或文件，导致 Go 工具链无法正确识别版本。

2.5. Go 工具链安装或更新不完整

在极少数情况下，Go 工具链本身的安装或更新过程可能出现问题，导致相关文件损坏或缺失，进而影响版本识别。

3. 错误场景与解决方案
针对上述错误成因，本节将详细描述不同场景下的解决方案。

3.1. 场景一：go.mod 文件中的 go 指令错误

这是最常见的错误场景。

3.1.1. 问题描述:

go.mod 文件中的 go 指令存在格式错误，例如：

```
module mymodule

go 1 // 错误：缺少次版本号
// 或
go 1.18.2 // 错误：包含了补丁版本号
// 或
go 1.x // 错误：使用了非数字字符
```

3.1.2. 解决方案:

修改 go.mod 文件，确保 go 指令后的版本字符串符合 主版本号.次版本号 的格式。

正确示例:

```
module mymodule

go 1.18 // 正确
// 或
go 1.20 // 正确
```

操作步骤:

使用文本编辑器打开 go.mod 文件。
找到 go 指令所在的行。
修正版本号，确保其格式正确。
保存 go.mod 文件。
重新运行 Go 命令（如 go build，go run）。
3.2. 场景二：GOROOT 或 GOTOOLCHAIN 设置问题

3.2.1. 问题描述:

GOROOT 环境变量未设置、指向错误目录，或 GOTOOLCHAIN 设置不当，导致 Go 工具链无法找到正确的版本。

3.2.2. 解决方案:

检查并修正 GOROOT 和 GOTOOLCHAIN 环境变量。

操作步骤:

检查 GOROOT:

在命令行中执行 echo $GOROOT (Linux/macOS) 或 echo %GOROOT% (Windows) 查看 GOROOT 的值。
确保 GOROOT 指向 Go 的安装目录。通常情况下，Go 的安装程序会自动设置 GOROOT。 如果未设置或指向错误，需要手动设置。
手动设置 GOROOT (以 Linux/macOS 为例，假设 Go 安装在 /usr/local/go):
bash
export GOROOT=/usr/local/go
export PATH=$GOROOT/bin:$PATH
(将上述命令添加到 shell 配置文件，如 .bashrc 或 .zshrc，以使其永久生效)
检查 GOTOOLCHAIN:

在命令行中执行 go env GOTOOLCHAIN 查看 GOTOOLCHAIN 的值。
如果不使用 GOTOOLCHAIN，可以不设置。
如果使用 GOTOOLCHAIN，确保其值与 go.mod 中的版本声明一致，或者设置为 auto。
手动设置 GOTOOLCHAIN (以设置为 auto 为例):
bash
go env -w GOTOOLCHAIN=auto
或者也可以设置成local，含义和auto有区别
如果不确定GOTOOLCHAIN的设置，可以使用go env -u GOTOOLCHAIN恢复默认值
重新打开命令行窗口或终端，使环境变量生效。

验证设置:
执行 go version 命令，确认 Go 版本信息是否正确显示。

3.3. 场景三：使用了不受支持的 Go 版本

3.3.1. 问题描述:

项目使用的 Go 版本过于陈旧，当前工具链不再支持。

3.3.2. 解决方案:

升级项目使用的 Go 版本。

操作步骤:

确定可用的 Go 版本: 访问 Go 官方网站 (golang.org) 或使用 go tool dist list 命令查看可用的 Go 版本。
修改 go.mod 文件: 将 go 指令后的版本号更新为受支持的较新版本。
更新代码 (如有必要): 如果新版本引入了不兼容的更改，可能需要更新项目代码以适应新版本。
运行 go mod tidy: 清理不再需要的依赖项，并下载新版本所需的依赖项。
测试: 彻底测试项目，确保在新版本下运行正常。
3.4. 场景四：第三方工具或脚本干扰

3.4.1. 问题描述:

第三方工具或自定义构建脚本修改了 Go 版本相关的环境变量或文件。

3.4.2. 解决方案:

检查并修正第三方工具或脚本的行为。

操作步骤:

审查第三方工具的文档: 了解其是否会修改 Go 版本相关的环境变量或文件。
检查自定义构建脚本: 查看脚本中是否有修改 GOROOT、GOTOOLCHAIN 或 go.mod 文件的代码。
修正或禁用有问题的工具或脚本: 如果发现问题，修正工具或脚本的行为，或者暂时禁用它们，以排除干扰。
3.5. 场景五：Go 工具链安装或更新不完整

3.5.1. 问题描述:

Go 工具链安装或更新过程出错，导致文件损坏或缺失。

3.5.2. 解决方案:

重新安装 Go 工具链。

操作步骤:

卸载现有的 Go 工具链:
Windows: 在“控制面板”的“程序和功能”中卸载 Go。
Linux/macOS: 删除 Go 的安装目录 (通常是 /usr/local/go)。
下载最新版本的 Go 安装包: 从 Go 官方网站 (golang.org) 下载适用于操作系统的最新版本的 Go 安装包。
重新安装 Go 工具链: 按照官方安装指南进行安装。
验证安装: 执行 go version 命令，确认 Go 版本信息是否正确显示。
4. Go 版本管理机制探讨
理解 invalid go version 错误背后的 Go 版本管理机制，有助于更好地预防和解决此类问题。

4.1. go.mod 文件与 go 指令

go.mod 文件是 Go 模块的基石，其中的 go 指令声明了项目所需的最低 Go 版本。Go 工具链会读取该指令，并据此选择合适的工具链版本。 这确保了项目在不同 Go 版本环境下构建和运行的一致性。

4.2. GOROOT 与 GOTOOLCHAIN

GOROOT 环境变量指定 Go 的安装目录，是 Go 工具链运行的基础。

GOTOOLCHAIN 环境变量 (从 Go 1.21 开始引入) 提供了更灵活的 Go 工具链管理方式。 它允许开发者在同一系统中安装多个 Go 版本，并通过 GOTOOLCHAIN 的值来选择使用哪个版本。

GOTOOLCHAIN 的优先级高于 GOROOT。 当设置了 GOTOOLCHAIN 时，Go 工具链会优先使用 GOTOOLCHAIN 指定的工具链，而忽略 GOROOT。

4.3. 版本选择逻辑

Go 工具链在选择版本时，遵循以下逻辑：

检查 GOTOOLCHAIN: 如果设置了 GOTOOLCHAIN，则使用其指定的工具链。
读取 go.mod: 如果未设置 GOTOOLCHAIN，则读取 go.mod 文件中的 go 指令，获取项目所需的最低 Go 版本。
匹配工具链: 在已安装的 Go 工具链中，选择满足 go.mod 版本要求的最新版本。
使用默认工具链: 如果没有找到匹配的工具链，则使用默认的 Go 工具链 (通常是 GOROOT 指向的工具链)。
4.4. 版本兼容性

Go 团队致力于保持向后兼容性。这意味着，使用较新版本的 Go 工具链构建的项目，通常可以在较旧版本的 Go 环境中运行 (只要没有使用新版本特有的功能)。

然而，Go 也会引入一些不兼容的更改。这些更改通常会在发布说明中详细说明。因此，在升级 Go 版本时，建议仔细阅读发布说明，并进行充分的测试。

5. 替代方案及比较
除了前文提到的方法之外，还有一些其他方式也可以影响Go的版本，这里进行集中对比说明

5.1 GVM (Go Version Manager)
GVM是一个第三方的Go版本管理器，可以很方便地安装、切换和管理多个Go版本。

优点:
安装和切换Go版本非常简单。
可以为每个项目设置独立的Go版本。
支持安装多个Go版本。
缺点:
需要安装额外的工具。
可能与其他Go工具链管理方式冲突。
安装方法 (以Linux/macOS为例):
bash
bash < <(curl -s -S -L https://raw.githubusercontent.com/moovweb/gvm/master/binscripts/gvm-installer)
安装成功之后，就可以使用gvm install 版本号进行版本安装了
5.2 go install
go install 命令也可以用来下载和安装指定版本的Go工具链。

优点:

Go自带的工具，无需额外安装。
可以下载和安装特定版本的Go工具链。
缺点:

不如GVM等工具方便。
需要手动管理多个Go工具链。
使用示例：
bash
# 默认安装
go install golang.org/dl/go版本号@latest
# 下载安装包
go版本号 download

5.3. 对比说明

下面从不同维度对这几种方式进行直观比较：

| 维度 | go.mod/GOTOOLCHAIN | GVM | go install |
| ------------ | ------------------ | -------------------------------------- | --------------------------------------- |
| 易用性 | 中等 | 高 | 中等 |
| 灵活性 | 高 | 高 | 中等 |
| 额外工具 | 无 | 需要安装 GVM | 无 |
| 版本隔离 | 通过 GOTOOLCHAIN 实现 | 每个项目可设置独立版本 | 需要手动管理 |
| 官方支持 | 是 | 否 | 是 |
| 适用场景 | 所有场景 | 需要频繁切换 Go 版本的场景 | 需要安装特定版本的 Go 工具链的场景 |
| 学习曲线 | 中等| 低 | 中等|
| 是否需要额外安装 | 否| 是 | 否 |

说明：
* 易用性: GVM由于使用方便，更胜一筹。
* 灵活性：go.mod/GOTOOLCHAIN 和 GVM 都支持灵活的版本选择，go install 需要手动管理，略逊。
* 是否需要额外安装：GVM需要，另外两者不需要
* 官方支持：go.mod/GOTOOLCHAIN和go install是官方方案

6. 深入案例分析
为了更深入地理解该错误及其解决方案，本节将分析一个更复杂的案例。

6.1. 案例描述

一个大型项目，包含多个子模块，每个子模块可能有不同的 Go 版本要求。 项目使用自定义构建脚本，并且依赖了一些第三方工具。

6.2. 问题排查

检查顶层 go.mod 文件: 确认 go 指令的版本号是否正确。
检查子模块的 go.mod 文件: 确认每个子模块的 go 指令版本号是否正确，以及是否存在版本冲突。
检查自定义构建脚本: 查看脚本中是否有修改 GOROOT、GOTOOLCHAIN 或 go.mod 文件的代码。
检查第三方工具: 确认第三方工具是否会影响 Go 版本相关的环境变量或文件。
检查 GOTOOLCHAIN 环境变量: 确认其值是否与项目的 Go 版本要求一致。
逐步排除: 尝试禁用自定义构建脚本和第三方工具，逐步缩小问题范围。
查看详细的错误信息: 使用 -v 或 -x 参数运行 Go 命令，获取更详细的错误输出，以帮助定位问题。
6.3. 解决方案

根据排查结果，采取相应的解决方案：

如果 go.mod 文件存在问题，修正版本号。
如果自定义构建脚本或第三方工具有问题，修正或禁用它们。
如果 GOTOOLCHAIN 设置不当，调整其值。
如果存在版本冲突，协商并统一各子模块的 Go 版本要求。
如果问题仍然存在，考虑重新安装 Go 工具链。
6.4. 最佳实践

在项目根目录的 go.mod 文件中明确声明项目所需的最低 Go 版本。
尽量使用 GOTOOLCHAIN 管理多个 Go 版本，避免直接修改 GOROOT。
定期更新 Go 工具链到最新的稳定版本。
仔细审查第三方工具和自定义构建脚本，确保它们不会干扰 Go 版本管理。
在升级 Go 版本时，仔细阅读发布说明，并进行充分的测试。
如果项目复杂，可以使用类似于“go work”的工作区模式进行管理
7. 案例分析总结
大型项目结构复杂，出现问题的可能性更多
需要对各种因素进行仔细的排查
结合多种手段，逐步缩小范围，最终定位问题
通过这个例子，应该对整个流程有更深刻的认识
8. 预防措施与最佳实践
为了避免 invalid go version 错误的发生，以下是一些预防措施和最佳实践：

规范 go.mod 文件:

始终在 go.mod 文件中明确声明项目所需的最低 Go 版本。
确保版本号格式正确 (主版本号.次版本号)。
定期运行 go mod tidy 清理不再需要的依赖项。
合理使用 GOTOOLCHAIN:

使用 GOTOOLCHAIN 管理多个 Go 版本，避免直接修改 GOROOT。
将 GOTOOLCHAIN 设置为 auto 或与 go.mod 中的版本声明一致。
保持 Go 工具链更新:

定期更新 Go 工具链到最新的稳定版本。
在升级 Go 版本时，仔细阅读发布说明，并进行充分的测试。
谨慎使用第三方工具:

仔细审查第三方工具，了解其是否会修改 Go 版本相关的环境变量或文件。
尽量使用官方推荐的工具。
规范构建流程:

使用标准化的构建流程，避免自定义脚本对 Go 版本管理的干扰。
在 CI/CD 环境中，明确指定 Go 版本。
代码审查:

在代码审查过程中，检查 go.mod 文件的正确性。
关注是否有代码依赖于特定的 Go 版本特性。
文档记录:

在项目文档中记录项目所使用的 Go 版本。
记录任何与 Go 版本相关的特殊配置。
统一版本：

尽量在团队和项目内部统一Go版本
9. 知识扩展：Go 版本发布策略
Go 团队采用了一种相对稳定的版本发布策略：

大版本 (Major Version): 通常每年发布一个大版本 (例如 Go 1.18, Go 1.19, Go 1.20)。 大版本可能会引入一些不兼容的更改，但 Go 团队会尽量保持向后兼容性。
小版本 (Minor Version): 每个大版本会包含多个小版本 (例如 Go 1.18.1, Go 1.18.2)。 小版本通常包含错误修复和性能改进，不会引入不兼容的更改。
发布周期: Go 团队通常每六个月发布一个大版本。
支持周期: 每个大版本通常会得到至少两个大版本的支持 (例如 Go 1.18 会得到 Go 1.19 和 Go 1.20 的支持)。 这意味着在发布新版本后，旧版本仍然会得到一段时间的维护和安全更新。
了解 Go 的版本发布策略，有助于开发者选择合适的 Go 版本，并规划项目的升级计划。

10. 工具推荐
Go 官方工具: Go 工具链本身提供了丰富的命令和选项，用于管理 Go 版本和依赖项。
GVM (Go Version Manager): 第三方 Go 版本管理器，方便安装、切换和管理多个 Go 版本。
VS Code Go 插件: 提供了强大的 Go 开发支持，包括版本提示、自动补全、调试等功能。
Goland: JetBrains 出品的 Go IDE，提供了全面的 Go 开发功能，包括版本管理、依赖管理、代码分析等。
11. 常见问题解答 (FAQ)
问：我可以使用比 go.mod 中声明的 Go 版本更高的版本吗？

答：通常情况下，可以使用更高的 Go 版本。Go 团队致力于保持向后兼容性。但是，如果项目中使用了新版本特有的功能，则必须使用相应的或更高的 Go 版本。

问：我可以使用比 go.mod 中声明的 Go 版本更低的版本吗？

答：如果项目中没有使用新版本特有的功能，则可以使用更低的版本。但是，不建议这样做，因为较新的版本通常包含错误修复和性能改进。

问：go.mod 文件中的 go 指令是否必须？

答：是的，go.mod 文件中的 go 指令是必须的。它告诉 Go 工具链项目所需的最低 Go 版本。

问：我应该使用 GOROOT 还是 GOTOOLCHAIN？

答：建议使用 GOTOOLCHAIN 管理多个 Go 版本。GOTOOLCHAIN 提供了更灵活的版本管理方式。

问：如何查看已安装的 Go 版本？

答：在命令行中执行 go version 命令可以查看当前使用的 Go 版本。 执行 go tool dist list 可以查看所有可安装的 Go 版本。

12. 未来展望
Go 语言的版本管理机制在不断演进。 随着 Go 语言的发展，未来可能会出现新的版本管理工具和方法。 开发者应持续关注 Go 语言的最新动态，以便更好地利用 Go 语言的特性和工具。

13. 要点回顾
invalid go version: must match format 1.23 错误的核心原因是 Go 工具链无法解析项目声明的 Go 版本。
错误通常由 go.mod 文件中的 go 指令错误、GOROOT 或 GOTOOLCHAIN 设置问题、使用了不受支持的 Go 版本、第三方工具干扰或 Go 工具链安装问题导致。
通过修正 go.mod 文件、调整环境变量、升级 Go 版本、检查第三方工具或重新安装 Go 工具链等方法可以解决该错误。
理解 Go 的版本管理机制，包括 go.mod 文件、GOROOT、GOTOOLCHAIN 和版本选择逻辑，有助于预防和解决此类问题。
遵循最佳实践，如规范 go.mod 文件、合理使用 GOTOOLCHAIN、保持 Go 工具链更新、谨慎使用第三方工具等，可以有效避免该错误的发生。
可以采用多种方式进行版本管理，包括go.mod配合GOTOOLCHAIN、gvm、go install等
版权声明：
作者：admin
链接：https://hostlocvps.com/2025/02/27/%e8%a7%a3%e5%86%b3go%e7%89%88%e6%9c%ac%e9%94%99%e8%af%af%ef%bc%9ainvalid-go-version-must-match-format-1-23%e8%af%a6%e8%a7%a3/
文章版权归作者所有，未经允许请勿转载。

```

```text
GO: 快速升级Go版本

IChen.

于 2024-03-08 10:46:28 发布

阅读量1.2w
 收藏 14

点赞数 10
文章标签： golang 开发语言 后端
版权

GitCode 开源社区
文章已被社区收录
加入社区
由于底层依赖升级了，那我们也要跟着升，go老版本已经不足满足需求了，必须要将版本升级到1.22.0以上

查看当前Go版本
命令查看go版本
go version
1
[root@localhost local]# go version
go version go1.21.4 linux/amd64
[root@localhost local]# 
1
2
3
下载高版本Go
登录 Go官网

下载对应的版本即可！




升级Go版本
保险起见还是备份好旧版本，防止有需要的情况

1.命令行查看旧版本go存放路径

[root@localhost local]# which go
/usr/local/go/bin/go
[root@localhost local]# 
1
2
3
2.备份

cd /usr/local
mv go go-1.21.4
1
2
部署新版本
1.上传版本包

go1.22.0.linux-amd64.tar.gz

2.将新版本Go压缩包解压到当前路径：/usr/local

tar zxf go1.22.0.linux-amd64.tar.gz
1
查看go目录
ll go
1

4. 验证go版本

go version
1
[root@localhost local]# go version
go version go1.22.0 linux/amd64
[root@localhost local]#
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/ichen820/article/details/136554968
```

# https://golang.google.cn/doc/install
# https://golang.google.cn/dl/

```text
grpc教程（golang版）

os-lee

已于 2024-10-17 16:46:24 修改

阅读量5.3k
 收藏 54

点赞数 63
分类专栏： Golang 文章标签： rpc grpc go
版权

Golang
专栏收录该内容
8 篇文章
订阅专栏
目录

一、介绍

二、环境准备

三、Golang中使用grpc

1.编写protobuf文件

2.服务端

3.客户端

四、proto文件详解

1.proto语法

2.数据类型 

基本数据类型

数组类型

map类型

嵌套类型

编写风格

3.多服务 

4.多个proto文件

五、流式传输

1.普通rpc

2.服务器流式

3.客户端流式 

4.双向流

六、配套代码

一、介绍
RPC是Remote Procedure Call的简称，中文叫远程过程调用，简单的说，就是调用远程方法和调用本地方法一样

那么grpc就是由 google开发的一个高性能、通用的开源RPC框架

官网地址：Introduction to gRPC | gRPC

是一个高性能，开源和通用的RPC框架，基于Protobuf序列化协议开发，且支持众多开发语言。
面向服务端和协议端，基于http/2设计，带来诸如双向流，流控，头部压缩，单TCP连接上的多路复用请求等特性。这些特性使得其在移动设备上表现的更好，更省电和节省空间。
在gPRC里客户端可以向调用本地对象一样直接调用另一台不同机器上服务端应用的方法，使得您能够更容易地创建分布式应用和服务。
与许多RPC系统类似，gRPC也是基于以下理念：定义一个服务，指定其能够被远程调用的方法（包含参数和返回类型）。在服务端实现这个接口。并运行一个gRPC服务器来处理客户端调用。在客户端拥有一个存根能够向服务端一样的方法。
grpc和http的区别
传输协议：HTTP使用文本基础的协议，而gRPC使用的是二进制协议，这意味着gRPC数据包更小，传输效率更高。另外，gRPC使用HTTP/2协议，支持多路复用，从而可以更好地处理并发请求。
性能差异：gRPC在性能方面优于HTTP。由于使用了二进制格式，因此gRPC传输速度更快、更稳定。而且gRPC通过使用连接池，实现客户端与服务端的长连接机制，使得延迟较低，在高网络带宽时表现更好。
使用场景：HTTP主要用于Web中浏览器和服务器之间的交互，在Web应用程序中非常常见。而gRPC通常用于服务之间的通信，特别是分布式系统中，例如微服务框架等。gRPC具有高性能、低延迟、易扩展等特点，是一个理想的分布式系统通信协议。
序列化和反序列化：HTTP在传输过程中使用JSON或XML格式来表示数据，需要进行序列化和反序列化。而gRPC则使用了更为高效的protobuf格式，同时也支持JSON等格式。
综上所述，gRPC和HTTP在不同的场景下各有其优势，使用时应根据具体情况来进行选择。如果需要高性能的服务之间通信，则可以选择gRPC，而在Web浏览器与服务器之间传输数据时则更适合使用HTTP。

二、环境准备
以Golang使用为例，只需要在windows上安装protoc转换工具

官网地址：Go | gRPC

# 安装protoc，这是通用的，所有语言都需要

​# 下载网址：

https://github.com/protocolbuffers/protobuf/releases/download/v3.9.0/protoc-3.9.0-win64.zip

解压后将将protoc的bin目录添加到环境变量中

# 安装protoc-gen-go，这是针对go语言的

# 创建一个go项目安装grpc相关依赖

go env -w GOPROXY=https://goproxy.io,direct （配置国内代理）
go get github.com/golang/protobuf/proto
go get google.golang.org/grpc
go install github.com/golang/protobuf/protoc-gen-go

将protoc-gen-go.exe的目录添加到环境变量中

 刚刚添加之后，可能需要重启电脑或者重启goland，才能在goland的terminal中使用



三、Golang中使用grpc
整体结构如图：



1.编写protobuf文件
现在还没有学过怎么编写，不用担心，先复制粘贴就行了，主要是用于测试环境是否正常

创建文件夹 /grpc_proto 在该文件夹中创建文件 hello.proto ，编写内容如下：

syntax = "proto3"; // 指定proto版本
package hello_grpc;     // 指定默认包名
 
// 指定golang包名
option go_package = "/hello_grpc";
 
//定义rpc服务
service HelloService {
  // 定义函数
  rpc SayHello (HelloRequest) returns (HelloResponse) {}
}
 
// HelloRequest 请求内容
message HelloRequest {
  string name = 1;
  string message = 2;
}
 
// HelloResponse 响应内容
message HelloResponse{
  string name = 1;
  string message = 2;
}

指令 

在文件夹 /grpc_proto 中执行命令：protoc -I . --go_out=plugins=grpc:. .\hello.proto
 
或者编写set.bat批处理文件，方便使用，内容如下：
 
protoc -I . --go_out=plugins=grpc:.\grpc_proto .\grpc_proto\hello.proto
2.服务端
package main
 
import (
	"context"
	"fmt"
	"google.golang.org/grpc"
	"google.golang.org/grpc/grpclog"
	"net"
	"oslee/grpc_study/1base/grpc_proto/hello_grpc"
)
 
// HelloServer 得有一个结构体，需要实现这个服务的全部方法,叫什么名字不重要
type HelloServer struct {
}
 
func (HelloServer) SayHello(ctx context.Context, request *hello_grpc.HelloRequest) (*hello_grpc.HelloResponse, error) {
	fmt.Println("入参：", request.Name, request.Message)
	return &hello_grpc.HelloResponse{
		Name:    "server",
		Message: "hello " + request.Name,
	}, nil
}
 
func main() {
	// 监听端口
	listen, err := net.Listen("tcp", ":8080")
	if err != nil {
		grpclog.Fatalf("Failed to listen: %v", err)
	}
 
	// 创建一个gRPC服务器实例。
	s := grpc.NewServer()
	server := HelloServer{}
	// 将server结构体注册为gRPC服务。
	hello_grpc.RegisterHelloServiceServer(s, &server)
	fmt.Println("grpc server running :8080")
	// 开始处理客户端请求。
	err = s.Serve(listen)
}

3.客户端
package main
 
import (
	"context"
	"fmt"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"log"
	"oslee/grpc_study/1base/grpc_proto/hello_grpc"
)
 
func main() {
	addr := ":8080"
	// 使用 grpc.Dial 创建一个到指定地址的 gRPC 连接。
	// 此处使用不安全的证书来实现 SSL/TLS 连接
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf(fmt.Sprintf("grpc connect addr [%s] 连接失败 %s", addr, err))
	}
	defer conn.Close()
	// 初始化客户端
	client := hello_grpc.NewHelloServiceClient(conn)
	result, err := client.SayHello(context.Background(), &hello_grpc.HelloRequest{
		Name:    "client",
		Message: "hello",
	})
	fmt.Println(result, err)
}

调用结果如下：



四、proto文件详解
1.proto语法
service 对应的就是go里面的接口，可以作为服务端，客户端
rpc 对应的就是结构体中的方法
message对应的也是结构体
2.数据类型 
基本数据类型
message Request {
  double a1 = 1;
  float a2 = 2;
  int32 a3 = 3;
  uint32 a4 = 4;
  uint64 a5 = 5;
  sint32 a6 = 6;
  sint64 a7 = 7;
  fixed32 a8 = 8;
  fixed64 a9 = 9;
  sfixed32 a10 = 10;
  sfixed64 a11 = 11;
  bool a12 = 12;
  string a13 = 13;
  bytes a14 = 14;
}

对应go类型

type Request struct {
  state         protoimpl.MessageState
  sizeCache     protoimpl.SizeCache
  unknownFields protoimpl.UnknownFields

  A1  float64 `protobuf:"fixed64,1,opt,name=a1,proto3" json:"a1,omitempty"`
  A2  float32 `protobuf:"fixed32,2,opt,name=a2,proto3" json:"a2,omitempty"`
  A3  int32   `protobuf:"varint,3,opt,name=a3,proto3" json:"a3,omitempty"`
  A4  uint32  `protobuf:"varint,4,opt,name=a4,proto3" json:"a4,omitempty"`
  A5  uint64  `protobuf:"varint,5,opt,name=a5,proto3" json:"a5,omitempty"`
  A6  int32   `protobuf:"zigzag32,6,opt,name=a6,proto3" json:"a6,omitempty"`
  A7  int64   `protobuf:"zigzag64,7,opt,name=a7,proto3" json:"a7,omitempty"`
  A8  uint32  `protobuf:"fixed32,8,opt,name=a8,proto3" json:"a8,omitempty"`
  A9  uint64  `protobuf:"fixed64,9,opt,name=a9,proto3" json:"a9,omitempty"`
  A10 int32   `protobuf:"fixed32,10,opt,name=a10,proto3" json:"a10,omitempty"`
  A11 int64   `protobuf:"fixed64,11,opt,name=a11,proto3" json:"a11,omitempty"`
  A12 bool    `protobuf:"varint,12,opt,name=a12,proto3" json:"a12,omitempty"`
  A13 string  `protobuf:"bytes,13,opt,name=a13,proto3" json:"a13,omitempty"`
  A14 []byte  `protobuf:"bytes,14,opt,name=a14,proto3" json:"a14,omitempty"`
}

标量类型

.proto Type	解释	Go Type
double		float64
float		float32
int32	使用变长编码，对于负值的效率很低，如果你的域有可能有负值，请使用sint64替代	int32
uint32	使用变长编码	uint32
uint64	使用变长编码	uint64
sint32	使用变长编码，这些编码在负值时比int32高效的多	int32
sint64	使用变长编码，有符号的整型值。编码时比通常的int64高效	int64
fixed32	总是4个字节，如果数值总是比总是比228大的话，这个类型会比uint32高效。	uint32
fixed64	总是8个字节，如果数值总是比总是比256大的话，这个类型会比uint64高效。	uint64
sfixed32	总是4个字节	int32
sfixed64	总是8个字节	int64
bool		bool
string	一个字符串必须是UTF-8编码或者7-bit ASCII编码的文本	string
bytes	可能包含任意顺序的字节数据	[]byte
标量类型如果没有被赋值，则不会被序列化，解析时，会赋予默认值

strings：空字符串
bytes：空序列
bools：false
数值类型：0
数组类型
message ArrayRequest {
  repeated int64 a1 = 1;
  repeated string a2 = 2;
  repeated Request request_list = 3;
}

对应go类型 

type ArrayRequest struct {
  A1          []int64 
  A2          []string   
  RequestList []*Request
}

map类型
键只能是基本类型

message MapRequest {
  map<int64, string> m_i_s = 1;
  map<string, bool> m_i_b = 2;
  map<string, ArrayRequest> m_i_arr = 3;
}

对应go类型 

type MapRequest struct {

  MIS   map[int64]string
  MIB   map[string]bool
  MIArr map[string]*ArrayRequest
}

嵌套类型
message Q1 {
  message Q2{
    string name2 = 2;
  }
  string name1 = 1;
  Q2 q2 = 2;
}

对应go类型

type Q1 struct {
  state         protoimpl.MessageState
  sizeCache     protoimpl.SizeCache
  unknownFields protoimpl.UnknownFields
  Name1 string `protobuf:"bytes,1,opt,name=name1,proto3" json:"name1,omitempty"`
  Q2    *Q1_Q2 `protobuf:"bytes,2,opt,name=q2,proto3" json:"q2,omitempty"`
}

个人习惯不嵌套，分开写

编写风格
文件名建议下划线，例如：my_student.proto
包名和目录名对应
服务名、方法名、消息名均为大驼峰
字段名为下划线
3.多服务 
proto文件

syntax = "proto3"; // 指定proto版本
// 指定golang包名
option go_package = "/duo_server";

service VideoService {
  rpc Look(Request)returns(Response){}
}

service OrderService {
  rpc Buy(Request)returns(Response){}
}

message Request{
  string name = 1;
}
message Response{
  string name = 1;
}
服务端

package main
 
import (
	"context"
	"fmt"
	"google.golang.org/grpc"
	"log"
	"net"
	"oslee/grpc_study/2duo_server/grpc_proto/duo_server"
)
 
type VideoServer struct {
}
 
func (VideoServer) Look(ctx context.Context, request *duo_server.Request) (res *duo_server.Response, err error) {
	fmt.Println("video:", request)
	return &duo_server.Response{
		Name: "server",
	}, nil
}
 
type OrderServer struct {
}
 
func (OrderServer) Buy(ctx context.Context, request *duo_server.Request) (res *duo_server.Response, err error) {
	fmt.Println("order:", request)
	return &duo_server.Response{
		Name: "server",
	}, nil
}
 
func main() {
	listen, err := net.Listen("tcp", ":8080")
	if err != nil {
		log.Fatal(err)
	}
	s := grpc.NewServer()
	duo_server.RegisterVideoServiceServer(s, &VideoServer{})
	duo_server.RegisterOrderServiceServer(s, &OrderServer{})
	fmt.Println("grpc server程序运行在：8080")
	err = s.Serve(listen)
}

客户端

package main
 
import (
	"context"
	"fmt"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"log"
	"oslee/grpc_study/2duo_server/grpc_proto/duo_server"
)
 
func main() {
	addr := ":8080"
	// 使用 grpc.Dial 创建一个到指定地址的 gRPC 连接。
	// 此处使用不安全的证书来实现 SSL/TLS 连接
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf(fmt.Sprintf("grpc connect addr [%s] 连接失败 %s", addr, err))
	}
	defer conn.Close()
 
	orderClient := duo_server.NewOrderServiceClient(conn)
	res, err := orderClient.Buy(context.Background(), &duo_server.Request{
		Name: "client",
	})
	fmt.Println(res, err)
 
	videoClient := duo_server.NewVideoServiceClient(conn)
	res, err = videoClient.Look(context.Background(), &duo_server.Request{
		Name: "client",
	})
	fmt.Println(res, err)
 
}

4.多个proto文件
当项目大起来之后，会有很多个service，rpc，message

我们会将不同服务放在不同的proto文件中，还可以放一些公共的proto文件

本质就是生成go文件，需要在一个包内

proto文件 

common.proto

syntax = "proto3";
package proto;
option go_package = "/proto";


message Request{
  string name = 1;
}
message Response{
  string name = 1;
}
order.proto

syntax = "proto3";
package proto;
option go_package = "/proto";
import "common.proto";

service OrderService {
  rpc Look(Request)returns(Response){}
}
video.proto

syntax = "proto3";
package proto;
option go_package = "/proto";
import "common.proto";

service VideoService {
  rpc Look(Request)returns(Response){}
}
服务端

package main
 
import (
	"context"
	"fmt"
	"google.golang.org/grpc"
	"log"
	"net"
	"oslee/grpc_study/3duo_proto/grpc_proto/proto"
)
 
type VideoServer struct {
}
 
func (VideoServer) Look(ctx context.Context, request *proto.Request) (res *proto.Response, err error) {
	fmt.Println("video:", request)
	return &proto.Response{
		Name: "server",
	}, nil
}
 
type OrderServer struct {
}
 
func (OrderServer) Look(ctx context.Context, request *proto.Request) (res *proto.Response, err error) {
	fmt.Println("order:", request)
	return &proto.Response{
		Name: "server",
	}, nil
}
 
func main() {
	listen, err := net.Listen("tcp", ":8080")
	if err != nil {
		log.Fatal(err)
	}
	s := grpc.NewServer()
	proto.RegisterVideoServiceServer(s, &VideoServer{})
	proto.RegisterOrderServiceServer(s, &OrderServer{})
	fmt.Println("grpc server程序运行在：8080")
	err = s.Serve(listen)
}

客户端

package main
 
import (
	"context"
	"fmt"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"log"
	"oslee/grpc_study/3duo_proto/grpc_proto/proto"
)
 
func main() {
	addr := ":8080"
	// 使用 grpc.Dial 创建一个到指定地址的 gRPC 连接。
	// 此处使用不安全的证书来实现 SSL/TLS 连接
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf(fmt.Sprintf("grpc connect addr [%s] 连接失败 %s", addr, err))
	}
	defer conn.Close()
 
	orderClient := proto.NewOrderServiceClient(conn)
	res, err := orderClient.Look(context.Background(), &proto.Request{
		Name: "client",
	})
	fmt.Println(res, err)
 
	videoClient := proto.NewVideoServiceClient(conn)
	res, err = videoClient.Look(context.Background(), &proto.Request{
		Name: "client",
	})
	fmt.Println(res, err)
}

五、流式传输
1.普通rpc
一问一答式，文章前面已讲

2.服务器流式
建立连接后，不知道服务端什么时候发送完毕，使用场景：下载文件 

 proto文件

syntax = "proto3";
option go_package = "/proto";
 
message Request {
  string name = 1;
}
 
message FileResponse{
  string file_name = 1;
  bytes content = 2;
}
service ServiceStream{
  rpc DownLoadFile(Request)returns(stream FileResponse){}
}
服务端

package main
 
import (
	"fmt"
	"google.golang.org/grpc"
	"io"
	"log"
	"net"
	"os"
	"oslee/grpc_study/4download/grpc_proto/proto"
)
 
type ServiceStream struct{}
 
func (ServiceStream) DownLoadFile(request *proto.Request, stream proto.ServiceStream_DownLoadFileServer) error {
	fmt.Println(request)
	file, err := os.Open("F:\\yckj\\workspace_gitee\\1own\\os_lee\\go_grpc_study\\res\\protoc-3.9.0-win64.zip")
	if err != nil {
		return err
	}
	defer file.Close()
 
	for {
		buf := make([]byte, 2048)
		_, err = file.Read(buf)
		if err == io.EOF {
			break
		}
		if err != nil {
			break
		}
		stream.Send(&proto.FileResponse{
			Content: buf,
		})
	}
	return nil
}
 
func main() {
	listen, err := net.Listen("tcp", ":8080")
	if err != nil {
		log.Fatal(err)
	}
	server := grpc.NewServer()
	proto.RegisterServiceStreamServer(server, &ServiceStream{})
 
	server.Serve(listen)
}

 客户端

package main
 
import (
	"bufio"
	"context"
	"fmt"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"io"
	"log"
	"os"
	"oslee/grpc_study/4download/grpc_proto/proto"
)
 
func main() {
	addr := ":8080"
	// 使用 grpc.Dial 创建一个到指定地址的 gRPC 连接。
	// 此处使用不安全的证书来实现 SSL/TLS 连接
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf(fmt.Sprintf("grpc connect addr [%s] 连接失败 %s", addr, err))
	}
	defer conn.Close()
	// 初始化客户端
	client := proto.NewServiceStreamClient(conn)
 
	stream, err := client.DownLoadFile(context.Background(), &proto.Request{
		Name: "张三",
	})
 
	file, err := os.OpenFile("F:\\yckj\\workspace_gitee\\1own\\os_lee\\go_grpc_study\\res\\protoc-3.9.0-win64_down.zip", os.O_CREATE|os.O_WRONLY, 0600)
	if err != nil {
		log.Fatalln(err)
	}
	defer file.Close()
 
	writer := bufio.NewWriter(file)
	var index int
	for {
		index++
		response, err := stream.Recv()
		if err == io.EOF {
			break
		}
		fmt.Printf("第%d 次， 写入 %d 数据\n", index, len(response.Content))
		writer.Write(response.Content)
	}
	writer.Flush()
}

3.客户端流式 
建立连接后，不知道客户端什么时候发送完毕，使用场景：上传文件

proto文件

syntax = "proto3";
option go_package = "/proto";
message Response {
  string Text = 1;
}
message FileRequest{
  string file_name = 1;
  bytes content = 2;
}
service ClientStream{
  rpc UploadFile(stream FileRequest)returns(Response){}
}
服务端

package main
 
import (
	"bufio"
	"fmt"
	"google.golang.org/grpc"
	"io"
	"log"
	"net"
	"os"
	"oslee/grpc_study/5upload/grpc_proto/proto"
)
 
type ClientStream struct{}
 
func (ClientStream) UploadFile(stream proto.ClientStream_UploadFileServer) error {
 
	file, err := os.OpenFile("F:\\yckj\\workspace_gitee\\1own\\os_lee\\go_grpc_study\\res\\protoc-3.9.0-win64_up.zip", os.O_CREATE|os.O_WRONLY, 0600)
	if err != nil {
		log.Fatalln(err)
	}
	defer file.Close()
 
	writer := bufio.NewWriter(file)
	var index int
	for {
		index++
		response, err := stream.Recv()
		if err == io.EOF {
			break
		}
		writer.Write(response.Content)
		fmt.Printf("第%d次", index)
	}
	writer.Flush()
	stream.SendAndClose(&proto.Response{Text: "完毕了"})
	return nil
}
 
func main() {
	listen, err := net.Listen("tcp", ":8080")
	if err != nil {
		log.Fatal(err)
	}
	server := grpc.NewServer()
	proto.RegisterClientStreamServer(server, &ClientStream{})
 
	server.Serve(listen)
}

客户端

package main
 
import (
	"context"
	"fmt"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"io"
	"log"
	"os"
	"oslee/grpc_study/5upload/grpc_proto/proto"
)
 
func main() {
	addr := ":8080"
	// 使用 grpc.Dial 创建一个到指定地址的 gRPC 连接。
	// 此处使用不安全的证书来实现 SSL/TLS 连接
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf(fmt.Sprintf("grpc connect addr [%s] 连接失败 %s", addr, err))
	}
	defer conn.Close()
	// 初始化客户端
	client := proto.NewClientStreamClient(conn)
	stream, err := client.UploadFile(context.Background())
 
	file, err := os.Open("F:\\yckj\\workspace_gitee\\1own\\os_lee\\go_grpc_study\\res\\protoc-3.9.0-win64.zip")
	if err != nil {
		log.Fatalln(err)
	}
	defer file.Close()
 
	for {
		buf := make([]byte, 2048)
		_, err = file.Read(buf)
		if err == io.EOF {
			break
		}
		if err != nil {
			break
		}
		stream.Send(&proto.FileRequest{
			FileName: "x.png",
			Content:  buf,
		})
	}
	response, err := stream.CloseAndRecv()
	fmt.Println(response, err)
}

4.双向流
使用场景：聊天室

proto文件

syntax = "proto3";
option go_package = "/proto";
 
message Request {
  string name = 1;
}
message Response {
  string Text = 1;
}
 
service BothStream{
  rpc Chat(stream Request)returns(stream Response){}
}
服务端

package main
 
import (
	"fmt"
	"google.golang.org/grpc"
	"log"
	"net"
	"oslee/grpc_study/6chat/grpc_proto/proto"
)
 
type BothStream struct{}
 
func (BothStream) Chat(stream proto.BothStream_ChatServer) error {
	for i := 0; i < 10; i++ {
		request, _ := stream.Recv()
		fmt.Println(request)
		stream.Send(&proto.Response{
			Text: "你好",
		})
	}
	return nil
}
 
func main() {
	listen, err := net.Listen("tcp", ":8080")
	if err != nil {
		log.Fatal(err)
	}
	server := grpc.NewServer()
	proto.RegisterBothStreamServer(server, &BothStream{})
 
	server.Serve(listen)
}

客户端 

package main
 
import (
	"context"
	"fmt"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"log"
	"oslee/grpc_study/6chat/grpc_proto/proto"
)
 
func main() {
	addr := ":8080"
	// 使用 grpc.Dial 创建一个到指定地址的 gRPC 连接。
	// 此处使用不安全的证书来实现 SSL/TLS 连接
	conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf(fmt.Sprintf("grpc connect addr [%s] 连接失败 %s", addr, err))
	}
	defer conn.Close()
	// 初始化客户端
	client := proto.NewBothStreamClient(conn)
	stream, err := client.Chat(context.Background())
 
	for i := 0; i < 10; i++ {
		stream.Send(&proto.Request{
			Name: fmt.Sprintf("第%d次", i),
		})
		response, err := stream.Recv()
		fmt.Println(response, err)
	}
}

六、配套代码
代码下载地址：

go_grpc_study: Golang使用grpc教程
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/qq_22321199/article/details/137551376

```


# go install和go get区别
```text
go install和go get的区别

catmes

已于 2022-11-28 16:17:27 修改

阅读量5.8k
 收藏 35

点赞数 37
分类专栏： Go语言学习 文章标签： golang 服务器 后端
版权

Go语言学习
专栏收录该内容
10 篇文章
订阅专栏
go install和go get的区别
go get 和 go install 的区别
go get命变更
go get命令变更的原因
GOMODULE常用命令
go get 和 go install 的区别
先看结论:

go get: 对 go mod 项目，添加，更新，删除 go.mod 文件的依赖项(仅源码)。不执行编译。侧重应用依赖项管理。
go install: 在操作系统中安装 Go 生态的第三方命令行应用。不更改项目 go.mod 文件。侧重可执行文件的编译和安装。
之前网上乱传的 go get 命令要被弃用是错的。正确说法是，go 1.17后，go get 命令的使用方式发生了改变.

具体什么改变呢？请看官方说明:

Starting in Go 1.17, installing executables with go get is deprecated. go install may be used instead.
In Go 1.18, go get will no longer build packages; it will only be used to add, update, or remove dependencies in go.mod.
Specifically, go get will always act as if the -d flag were enabled.

大概表达3个意思:

自 Go 1.17 起, 弃用 go get 命令安装可执行文件，使用 go install 命令替代.
自Go 1.18起，go get 命令不再有编译包的功能。将只有添加，更新，移除 go.mod 文件中的依赖项的功能。
go get 命令将默认启用 -d 选项。
go get命变更
Go 1.17 之前：go get 通过远程拉取或更新代码包及其依赖包，并自动完成编译和安装。实际分成两步操作：1. 下载源码包，2. 执行 go install。

Go 1.17 之后: 弃用go get命令的编译和安装功能

go get命令变更的原因
由于 go 1.11 之后 go mod modules特性的引入，使得go get 命令，既可以安装第三方命令，又可以从 go.mod 文件自动更新项目依赖。但多数情况下，开发者只想做二者之一。

自 go 1.16 起，go install 命令，可以忽略当前目录的 go.mod文件(如果存在)，直接安装指定版本的命令行应用。

go get 命令的编译和安装功能，因为和 go install 命令的功能重复，故被弃用。由于弃用了编译和安装功能，go get 命令将获得更高的执行效率, 也不会在更新包的时候，再出现编译失败的报错。

Since modules were introduced, the go get command has been used both to update dependencies in go.mod and to install commands.
This combination is frequently confusing and inconvenient: in most cases, developers want to update a dependency or install a command but not both at the same time.

Since Go 1.16, go install can install a command at a version specified on the command line while ignoring the go.mod file in the current directory (if one exists).
go install should now be used to install commands in most cases.

go get’s ability to build and install commands is now deprecated, since that functionality is redundant with go install.
Removing this functionality will make go get faster, since it won’t compile or link packages by default.
go get also won’t report an error when updating a package that can’t be built for the current platform.

go get 由于具备更改 go.mod 文件的能力，因此我们 必须要避免执行 go get 命令时，让它接触到我们的 go.mod 文件 ，否则它会将我们安装的工具作为一个依赖。

所以，如果不是为了更新项目依赖，而是安装可执行命令，请使用 go install

GOMODULE常用命令
go mod init  # 初始化go.mod
go mod tidy  # 直接从源代码中获取依赖关系，更新依赖文件。可删掉go.mod中无用的依赖。
go mod download  # 下载依赖文件
go mod vendor  # 将依赖转移至本地的vendor文件
go mod edit  # 手动修改依赖文件
go mod graph  # 打印依赖图
go mod verify  # 校验依赖
1
2
3
4
5
6
7
在项目源码中使用 import 语句，导入新的依赖模块前，可用 go get 命令，先下载新模块。

go instsll 应该在module外部使用 https://github.com/golang/go/issues/40276
弃用go get命令安装可执行文件 https://go.dev/doc/go-get-install-deprecation
Go 1.16 中关于 go get 和 go install https://cloud.tencent.com/developer/article/1766820
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/WHQ78164/article/details/128081577
```

```text
Protobuf生成Go代码指南

kevin_tech

于 2019-09-18 00:00:00 发布

阅读量3.7k
 收藏 4

点赞数 1
版权
这个教程中将会描述protocol buffer编译器通过给定的.proto会编译生成什么Go代码。教程针对的是proto3版本的protobuf。在阅读之前确保你已经阅读过Protobuf语言指南。

编译器调用
Protobuf核心的工具集是C++语言开发的，官方的protoc编译器中并不支持Go语言，需要安装一个插件才能生成Go代码。用如下命令安装：

$ go get github.com/golang/protobuf/protoc-gen-go
提供了一个protoc-gen-go二进制文件，当编译器调用时传递了--go_out命令行标志时protoc就会使用它。--go_out告诉编译器把Go源代码写到哪里。编译器会为每个.proto文件生成一个单独的源代码文件。

输出文件的名称是通过获取.proto文件的名称并进行两处更改来计算的：

生成文件的扩展名是.pb.go。比如说player_record.proto编译后会得到player_record.pb.go。
proto路径（使用--proto_path或-I命令行标志指定）将替换为输出路径（使用--go_out标志指定）。
当你运行如下编译命令时：

protoc --proto_path=src --go_out=build/gen src/foo.proto src/bar/baz.proto
编译器会读取文件src/foo.proto和src/bar/baz.proto，这将会生成两个输出文件build/gen/foo.pb.go和build/gen/bar/baz.pb.go

如果有必要，编译器会自动生成build/gen/bar目录，但是他不能创建build或者build/gen目录，这两个必须是已经存在的目录。

包
如果一个.proto文件中有包声明，生成的源代码将会使用它来作为Go的包名，如果.proto的包名中有. 在Go包名中会将.转换为_。举例来说proto包名example.high_score将会生成Go包名example_high_score。

在.proto文件中可以使用option go_package指令来覆盖上面默认生成Go包名的规则。比如说包含如下指令的一个.proto文件

package example.high_score;
option go_package = "hs";
生成的Go源代码的包名是hs。

如果一个.proto文件中不包含package声明，生成的源代码将会使用.proto文件的文件名(去掉扩展名)作为Go包名，.会被首先转换为_。举例来说一个名为high.score.proto不包含pack声明的文件将会生成文件high.score.pb.go，他的Go包名是high_score。

消息
一个简单的消息声明：

message Foo {}
protocol buffer编译器将会生成一个名为Foo的结构体，实现了proto.Message接口的Foo类型的指针

type Foo struct {
}
 
// 重置proto为默认值
func (m *Foo) Reset()         { *m = Foo{} }
 
// String 返回proto的字符串表示
func (m *Foo) String() string { return proto.CompactTextString(m) }
 
// ProtoMessage作为一个tag 确保其他人不会意外的实现
// proto.Message 接口.
func (*Foo) ProtoMessage()    {}
内嵌的消息
一个message可以声明在其他message的内部。比如说：

message Foo {
  message Bar {
  }
}
这种情况，编译器会生成两个结构体：Foo和Foo_Bar。

预定义消息类型
Protobufs带有一组预定义的消息，称为众所周知的类型（WKT）。这些类型可以用于与其他服务的互操作性，或者仅仅因为它们简洁地表示了常见的有用模式。例如，Struct消息表示任意C样式结构的格式。

WKT的预生成Go代码作为Go protobuf库的一部分进行分发，如果message中使用了WKT，则生成的消息的Go代码会引用此代码。例如，给出如下消息：

import "google/protobuf/struct.proto"
import "google/protobuf/timestamp.proto"
 
message NamedStruct {
  string name = 1;
  google.protobuf.Struct definition = 2;
  google.protobuf.Timestamp last_modified = 3;
}
生成的Go代码将会像下面这样：

import google_protobuf "github.com/golang/protobuf/ptypes/struct"
import google_protobuf1 "github.com/golang/protobuf/ptypes/timestamp"
 
...
 
type NamedStruct struct {
   Name         string
   Definition   *google_protobuf.Struct
   LastModified *google_protobuf1.Timestamp
}
一般来说，您不需要将这些类型直接导入代码中。但是，如果需要直接引用其中一种类型，只需导入github.com/golang/protobuf/ptypes/[TYPE]包，并正常使用该类型。

字段
编译器会为每个在message中定义的字段生成一个Go结构体的字段，字段的确切性质取决于它的类型以及它是singular，repeated，map还是oneof字段。

注意生成的Go结构体的字段将始终使用驼峰命名，即使在.proto文件中消息字段用的是小写加下划线（应该这样）。大小写转换的原理如下：

首字母会大些，如果message中字段的第一个字符是_，它将被替换为X。
如果内部下划线后跟小写字母，则删除下划线，并将后面跟随的字母大写。
因此，proto字段foo_bar_baz在Go中变成FooBarBaz， _my_field_name_2变为XMyFieldName_2。

单一标量字段
对于字段定义：

int32 foo = 1;
编译器将生成一个带有名为Foo的int32字段和一个访问器方法GetFoo（）的结构，该方法返回Foo中的int32值或该字段的零值（如果字段未设置（数值型零值为0，字符串为空字符串））。

单一message字段
给出如下消息类型

message Bar {}
对于一个有Bar类型字段的消息：

// proto3
message Baz {
  Bar foo = 1;
}
编译器将会生成一个Go结构体

type Baz struct {
        Foo *Bar
}
消息类型的字段可以设置为nil，这意味着该字段未设置，有效清除该字段。这不等同于将值设置为消息结构体的“空”实例。

编译器还生成一个func（m * Baz）GetFoo（）* Bar辅助函数。这让不在中间检查nil值进行链式调用成为可能。

可重复字段
每个重复的字段在Go中的结构中生成一个T类型的slice，其中T是字段的元素类型。对于带有重复字段的此消息：

message Baz {
  repeated Bar foo = 1;
}
编译器会生成如下结构体：

type Baz struct {
        Foo  []*Bar
}
同样，对于字段定义repeated bytes foo = 1;编译器将会生成一个带有类型为[][]byte名为Foo的字段的Go结构体。对于可重复的枚举repeated MyEnum bar = 2;，编译器会生成带有类型为[]MyEnum名为Bar的字段的Go结构体。

映射字段
每个映射字段会在Go的结构体中生成一个map[TKey]TValue类型的字段，其中TKey是字段的键类型TValue是字段的值类型。对于下面这个消息定义：

message Bar {}
 
message Baz {
  map<string, Bar> foo = 1;
}
编译器生成Go结构体

type Baz struct {
        Foo map[string]*Bar
}
枚举
给出如下枚举

message SearchRequest {
  enum Corpus {
    UNIVERSAL = 0;
    WEB = 1;
    IMAGES = 2;
    LOCAL = 3;
    NEWS = 4;
    PRODUCTS = 5;
    VIDEO = 6;
  }
  Corpus corpus = 1;
  ...
}
编译器将会生成一个枚举类型和一系列该类型的常量。

对于消息中的枚举（像上面那样），类型名字以消息名开头

type SearchRequest_Corpus int32
对于包级别的枚举：

// .proto
enum Foo {
  DEFAULT_BAR = 0;
  BAR_BELLS = 1;
  BAR_B_CUE = 2;
}
Go 中的类型不会对proto中的枚举名称进行修改：

type Foo int32
此类型具有String()方法，该方法返回给定值的名称。

Enum()方法使用给定值初始化新分配的内存并返回相应的指针：

func (Foo) Enum() *Foo
编译器为枚举中的每个值生成一个常量。对于消息中的枚举，常量以消息的名称开头：

const (
        SearchRequest_UNIVERSAL SearchRequest_Corpus = 0
        SearchRequest_WEB       SearchRequest_Corpus = 1
        SearchRequest_IMAGES    SearchRequest_Corpus = 2
        SearchRequest_LOCAL     SearchRequest_Corpus = 3
        SearchRequest_NEWS      SearchRequest_Corpus = 4
        SearchRequest_PRODUCTS  SearchRequest_Corpus = 5
        SearchRequest_VIDEO     SearchRequest_Corpus = 6
)
对于包级别的枚举，常量以枚举名称开头:

const (
        Foo_DEFAULT_BAR Foo = 0
        Foo_BAR_BELLS   Foo = 1
        Foo_BAR_B_CUE   Foo = 2
)
protobuf编译器还生成从整数值到字符串名称的映射以及从名称到值的映射：

var Foo_name = map[int32]string{
        0: "DEFAULT_BAR",
        1: "BAR_BELLS",
        2: "BAR_B_CUE",
}
var Foo_value = map[string]int32{
        "DEFAULT_BAR": 0,
        "BAR_BELLS":   1,
        "BAR_B_CUE":   2,
}
请注意，.proto语言允许多个枚举符号具有相同的数值。具有相同数值的符号是同义词。这些在Go中以完全相同的方式表示，多个名称对应于相同的数值。反向映射包含数字值的单个条目，数值映射到出现在proto文件中首先出现的名称。

服务
默认情况下，Go代码生成器不会为服务生成输出。如果您启用gRPC插件（请参阅gRPC Go快速入门指南），则会生成代码以支持gRPC。
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/kevin_tech/article/details/104093925
```

```text
Protobuf 的 import 功能在 Go 项目中的实践
头像
Xavier
2019-12-31
阅读 4 分钟
18
业务场景
我们会有这样的需求：在不同的文件夹中定义了不同的 proto 文件，这些不同的文件夹可能是一些不同的 gRPC 服务。因为不想重复定义某一个 message，所以其中一个服务可能会用到其他服务中定义的 message，那么这个时候就需要使用到 proto 文件的 import 功能。

接下来说说我在 Go 项目中使用 protobuf 的 import 时所遇到的坑。

案例
首先，我们来创建一个实验项目作为案例，便以说明，结构如下：

image.png

文件 go.mod 中声明了该项目模块名 module github.com/xvrzhao/pb-demo，proto 文件夹中含有两个 gRPC 服务，分别为 article 和 user，我们在这两个文件夹中定义各自所需要的 messages 和 services。

一般情况下，我们会将编译生成的 pb.go 文件生成在与 proto 文件相同的目录，这样我们就不需要再创建相同的目录层级结构来存放 pb.go 文件了。由于同一文件夹下的 pb.go 文件同属于一个 package，所以在定义 proto 文件的时候，相同文件夹下的 proto 文件也应声明为同一的 package，并且和文件夹同名，这是因为生成的 pb.go 文件的 package 是取自 proto package 的。

同属于一个包内的 proto 文件之间的引用也需要声明 import ，因为每个 proto 文件都是相互独立的，这点不像 Go（包内所有定义均可见）。我们的项目 user 模块下 service.proto 就需要用到 message.proto 中的 message 定义，代码是这样写的：

user/service.proto:

syntax = "proto3";  
package user;  // 声明所在包
option go_package = "github.com/xvrzhao/pb-demo/proto/user";  // 声明生成的 go 文件所属的包
  
import "proto/user/message.proto";  // 导入同包内的其他 proto 文件
import "proto/article/message.proto";  // 导入其他包的 proto 文件
  
service User {  
    rpc GetUserInfo (UserID) returns (UserInfo);  
    rpc GetUserFavArticle (UserID) returns (article.Articles.Article);  
}
user/message.proto:

syntax = "proto3";  
package user;  
option go_package = "github.com/xvrzhao/pb-demo/proto/user";  
  
message UserID {  
    int64 ID = 1;  
}  
  
message UserInfo {  
    int64 ID = 1;  
    string Name = 2;  
    int32 Age = 3;  
    gender Gender = 4;  
    enum gender {  
        MALE = 0;  
        FEMALE = 1;  
    }  
}
可以看到，我们在每个 proto 文件中都声明了 package 和 option go_package，这两个声明都是包声明，到底两者有什么关系，这也是我开始比较迷惑的。

我是这样理解的，package 属于 proto 文件自身的范围定义，与生成的 go 代码无关，它不知道 go 代码的存在（但 go 代码的 package 名往往会取自它）。这个 proto 的 package 的存在是为了避免当导入其他 proto 文件时导致的文件内的命名冲突。所以，当导入非本包的 message 时，需要加 package 前缀，如 service.proto 文件中引用的 Article.Articles，点号选择符前为 package，后为 message。同包内的引用不需要加包名前缀。

article/message.proto:

syntax = "proto3";  
package article;  
option go_package = "github.com/xvrzhao/pb-demo/proto/article";  
  
message Articles {  
    repeated Article Articles = 1;  
    message Article {  
        int64 ID = 1;  
        string Title = 2;  
    }  
}
而 option go_package 的声明就和生成的 go 代码相关了，它定义了生成的 go 文件所属包的完整包名，所谓完整，是指相对于该项目的完整的包路径，应以项目的 Module Name 为前缀。如果不声明这一项会怎么样？最开始我是没有加这项声明的，后来发现 依赖这个文件的 其他包的 proto 文件 所生成的 go 代码 中（注意断句，已用斜体和正体标示），引入本文件所生成的 go 包时，import 的路径并不是基于项目 Module 的完整路径，而是在执行 protoc 命令时相对于 --proto_path 的包路径，这在 go build 时是找不到要导入的包的。这里听起来可能有点绕，建议大家亲自尝试一下。

protoc 命令
另外，我们说说编译 proto 文件时的命令参数。

首先 protoc 编译生成 go 代码所用的插件 protoc-gen-go 是不支持多包同时编译的，执行一次命令只能同时编译一个包，关于该讨论可以查看该项目的 issue#39。

接下来讲讲我遇到的另外一个坑。通常情况下我们编译命令是这样的（基于本项目来说，执行命令的 pwd 为项目根目录）：

$ protoc --proto_path=. --go_out=. ./proto/user/*.proto # 编译 user 路径下所有 proto 文件
其中，--proto_path 或者 -I 参数用以指定所编译源码（包括直接编译的和被导入的 proto 文件）的搜索路径，proto 文件中使用 import 关键字导入的路径一定是要基于 --proto_path 参数所指定的路径的。该参数如果不指定，默认为 pwd ，也可以指定多个以包含所有所需文件。

其中，--go_out 参数是用来指定 protoc-gen-go 插件的工作方式 和 go 代码目录架构的生成位置，可以向 --go_out 传递很多参数，见 golang/protobuf 文档 。主要的两个参数为 plugins 和 paths ，代表 生成 go 代码所使用的插件 和 生成的 go 代码的目录怎样架构。--go_out 参数的写法是，参数之间用逗号隔开，最后加上冒号来指定代码目录架构的生成位置，例如：--go_out=plugins=grpc,paths=import:. 。paths 参数有两个选项，import 和 source_relative 。默认为 import ，代表按照生成的 go 代码的包的全路径去创建目录层级，source_relative 代表按照 proto 源文件的目录层级去创建 go 代码的目录层级，如果目录已存在则不用创建。

在上面的示例命令中，--go_out 默认使用了 paths=import 所以，我的 go 文件都被编译到了 ./github.com/xvrzhao/pb-demo/proto/user/ 下，后来阅读 文档 才发现:

However, the output directory is selected in one of two ways. Let us say we have inputs/x.proto with a go_package option of github.com/golang/protobuf/p . The corresponding output file may be:

Relative to the import path:
$ protoc --go_out=. inputs/x.proto
# writes ./github.com/golang/protobuf/p/x.pb.go
( This can work well with --go_out=$GOPATH )

Relative to the input file:
$ protoc --go_out=paths=source_relative:. inputs/x.proto
# generate ./inputs/x.pb.go
所以，我们应该将 --go_out 参数改为 --go_out=paths=source_relative:. 。

请切记 option go_package 声明和 --go_out=paths=source_relative:. 命令行参数缺一不可 。

option go_package 声明 是为了让生成的其他 go 包（依赖方）可以正确 import 到本包（被依赖方）
--go_out=paths=source_relative:. 参数 是为了让加了 option go_package 声明的 proto 文件可以将 go 代码编译到与其同目录。
一般用法
为了统一性，我会将所有 proto 文件中的 import 路径写为相对于项目根目录的路径，然后 protoc 的执行总是在项目根目录下进行：

$ protoc --go_out=plugins=grpc,paths=source_relative:. ./proto/user/*.proto 
$ protoc --go_out=plugins=grpc,paths=source_relative:. ./proto/article/*.proto
如果你觉得每个包都需要单独编译，有些麻烦，可以执行脚本（ **/* 代表递归获取当前目录下所有的文件和文件夹）：

pb-demo 下执行：

$ for x in **/*.proto; do protoc --go_out=plugins=grpc,paths=source_relative:. $x; done
循环依赖
注意，不同包之间的 proto 文件不可以循环依赖，这会导致生成的 go 包之间也存在循环依赖，导致 go 代码编译不通过。

总结
感觉 protobuf 的使用非常繁杂，文档散落在各处（ protobuf 官方文档 / golang protobuf 文档 / grpc 文档 ），要注意的细节也很多，需要多加实践，多加总结。
```

# grpc_proto/hello_grpc" is not a package path; see 'go help packages'

```text
protocol buffer中的一些点（pacakge、go_package、proto依赖等）
protoc --proto_path=$GOPATH/src --proto_path=. --go_out=. ./*.proto
a. 上面的句编译语句中，--proto_path用于表示要编译的proto文件所依赖的其他proto文件的查找位置，可以使用-I来替代。如果没有指定则从当前目录中查找。
b. --go_out有两层含义，一层是输出的是go语言对应的文件；一层是指定生成的go文件的存放位置。
c. --go_out=plugins=grpc:helloworld，这里使用了grpc插件。如果proto文件想在rpc中使用，可以在proto中定义接口如下：
service SearchService {
  rpc Search(SearchRequest) returns (SearchResponse);
}
helloworld表示生成的文件存放地址。
protoc --go_out=plugins=grpc:. --go_opt=paths=source_relative ./update.proto
a. --go_opt表示生成go文件时候的目录选项，如上面写时表示生成的文件与proto在同一目录。
import、go_package、package
a. package主要是用于避免命名冲突的，不同的项目（project）需要指定不同的package。
b. import，如果proto文件需要使用在其他proto文件中已经定义的结构，可以使用import引入。
c. option go_package = "github.com/protocolbuffers/protobuf/examples/go/tutorialpb"; go_packge有两层意思，一层是表明如果要引用这个proto生成的文件的时候import后面的路径；一层是如果不指定--go_opt（默认值），生成的go文件存放的路径。
d. 需要注意的是package和go_package的含义。在官方给的文档中，package和go_package的最后一个单词不一样：

他们的含义分别是：package用于防止不同project之间定义了同名message结构的冲突，因为package名的一个作用是用于init方法中的注册：

而当go_package存在时，其最后一个单词是生成的go文件的package名字：

而当go_package不存在时，go文件件的package名字就变成了proto中package指定的名字了。

```

```text
如何使用go module导入本地包
发布于2020/02/27 ,更新于2020/02/27 23:35:34| golang|总阅读量：22019次
承蒙大家厚爱，我的《Go语言之路》的纸质版图书已经上架京东，有需要的朋友请点击 此链接 购买。

go module是Go1.11版本之后官方推出的版本管理工具，并且从Go1.13版本开始，go module将是Go语言默认的依赖管理工具。到今天Go1.14版本推出之后Go modules 功能已经被正式推荐在生产环境下使用了。

这几天已经有很多教程讲解如何使用go module，以及如何使用go module导入gitlab私有仓库，我这里就不再啰嗦了。但是最近我发现很多小伙伴在群里问如何使用go module导入本地包，作为初学者大家刚开始接触package的时候肯定都是先在本地创建一个包，然后本地调用一下，然后就被卡住了。。。

这里就详细介绍下如何使用go module导入本地包。

前提
假设我们现在有moduledemo和mypackage两个包，其中moduledemo包中会导入mypackage包并使用它的New方法。

mypackage/mypackage.go内容如下：

package mypackage

import "fmt"

func New(){
	fmt.Println("mypackage.New")
}
我们现在分两种情况讨论：

在同一个项目下
注意：在一个项目（project）下我们是可以定义多个包（package）的。

目录结构
现在的情况是，我们在moduledemo/main.go中调用了mypackage这个包。

moduledemo
├── go.mod
├── main.go
└── mypackage
    └── mypackage.go
导入包
这个时候，我们需要在moduledemo/go.mod中按如下定义：

module moduledemo

go 1.14
然后在moduledemo/main.go中按如下方式导入mypackage

package main

import (
	"fmt"
	"moduledemo/mypackage"  // 导入同一项目下的mypackage包
)
func main() {
	mypackage.New()
	fmt.Println("main")
}
举个例子
举一反三，假设我们现在有文件目录结构如下：

└── bubble
    ├── dao
    │   └── mysql.go
    ├── go.mod
    └── main.go
其中bubble/go.mod内容如下：

module github.com/q1mi/bubble

go 1.14
bubble/dao/mysql.go内容如下：

package dao

import "fmt"

func New(){
	fmt.Println("mypackage.New")
}
bubble/main.go内容如下：

package main

import (
	"fmt"
	"github.com/q1mi/bubble/dao"
)
func main() {
	dao.New()
	fmt.Println("main")
}
不在同一个项目下
目录结构
├── moduledemo
│   ├── go.mod
│   └── main.go
└── mypackage
    ├── go.mod
    └── mypackage.go
导入包
这个时候，mypackage也需要进行module初始化，即拥有一个属于自己的go.mod文件，内容如下：

module mypackage

go 1.14
然后我们在moduledemo/main.go中按如下方式导入：

import (
	"fmt"
	"mypackage"
)
func main() {
	mypackage.New()
	fmt.Println("main")
}
因为这两个包不在同一个项目路径下，你想要导入本地包，并且这些包也没有发布到远程的github或其他代码仓库地址。这个时候我们就需要在go.mod文件中使用replace指令。

在调用方也就是moduledemo/go.mod中按如下方式指定使用相对路径来寻找mypackage这个包。

module moduledemo

go 1.14


require "mypackage" v0.0.0
replace "mypackage" => "../mypackage"
举个例子
最后我们再举个例子巩固下上面的内容。

我们现在有文件目录结构如下：

├── p1
│   ├── go.mod
│   └── main.go
└── p2
    ├── go.mod
    └── p2.go
p1/main.go中想要导入p2.go中定义的函数。

p2/go.mod内容如下：

module liwenzhou.com/q1mi/p2

go 1.14
p1/main.go中按如下方式导入

import (
	"fmt"
	"liwenzhou.com/q1mi/p2"
)
func main() {
	p2.New()
	fmt.Println("main")
}
因为我并没有把liwenzhou.com/q1mi/p2这个包上传到liwenzhou.com这个网站，我们只是想导入本地的包，这个时候就需要用到replace这个指令了。

p1/go.mod内容如下：

module github.com/q1mi/p1

go 1.14


require "liwenzhou.com/q1mi/p2" v0.0.0
replace "liwenzhou.com/q1mi/p2" => "../p2"
此时，我们就可以正常编译p1这个项目了。

说再多也没用，自己动手试试吧。
```

# https://github.com/henrygd/beszel/blob/main/beszel/migrations/collections_snapshot_0_10_2.go
# https://pkg.go.dev/golang.org/x/crypto#section-readme
# golang.org/x/crypto库
# https://github.com/go-shiori/shiori/blob/master/go.mod
# go 短声明
```text
Go语言中不可不知的语法糖，使得代码更加简洁、高效
今天，我们来聊聊Go语言中的一些语法糖。说到“语法糖”，你是不是会觉得有点抽象？简单来说，就是那些让你觉得编写代码更轻松、优雅、且高效的功能——就像糖一样，给编程加点“甜头”。如果你用 Go 写过程序，可能已经体验到这些便利了，今天我就从一个程序员的角度，给大家分享一些我常用的 Go 语言语法糖，这些技巧简直让我的编码之旅顺畅不少！让我们来看看。

1. 短变量声明 (:=)
这可是 Go 中的一大语法糖。你还记得在其他语言中，声明一个变量需要先写类型再赋值？在 Go 中，使用 := 直接完成声明和赋值，简直是懒人福音！🍬

package main

import "fmt"

func main() {
    x := 10 // 自动声明并赋值
    fmt.Println(x)
}
这个 := 省去了你手动声明类型的麻烦，编译器会自动推断出 x 是 int 类型。这种语法糖让代码看起来更加简洁、清爽。

2. 匿名函数 (Anonymous Functions)
匿名函数是另一个高效又简洁的语法糖。顾名思义，它就是没有名字的函数。你可以随时创建一个匿名函数，并立即调用它，甚至作为参数传递给其他函数。比如，你需要传递一个简单的操作：

package main

import "fmt"

func main() {
    // 直接声明并调用匿名函数
    func(a, b int) {
        fmt.Println(a + b)
    }(3, 5)
}
这种方法非常适用于那些临时的、一次性的功能，你根本不需要额外为它定义一个名字，让代码更加精简。

3. 函数作为参数 (First-Class Functions)
Go 语言允许将函数作为参数传递给其他函数。这一点从 Python、JavaScript 中的高阶函数可以看到影子，Go 也完美实现了这一点。你可以传递函数来简化一些复杂的逻辑操作，直接将它当作参数传递，代码显得简洁又有趣。

package main

import "fmt"

// 定义一个接收函数的函数
func applyOperation(x, y int, op func(int, int)) {
    op(x, y)
}

func main() {
    add := func(a, b int) {
        fmt.Println(a + b)
    }

    applyOperation(5, 7, add)  // 将 add 函数传递给 applyOperation
}
这样，不需要每次都写重复的逻辑，函数也可以被动态传递，代码显得非常灵活！

4. 空白标识符 (_)
有时候你会遇到这样的情况：某个函数返回多个值，但你并不关心其中某些值。此时，Go 的空白标识符 _ 就派上用场了。你可以用它丢弃不需要的返回值，避免编译器抱怨。

package main

import "fmt"

func getData() (string, int) {
    return "Hello", 42
}

func main() {
    name, _ := getData() // 忽略第二个返回值
    fmt.Println(name)     // 输出：Hello
}
这个 _ 用法简洁高效，特别是在你只关心函数的部分返回值时。

5. 延迟执行 (defer)
说到 Go 语法糖，defer 绝对算一个“金手指”！它用于在函数结束时执行一些操作，比如资源清理。常见的场景就是关闭文件、网络连接或者解锁 Mutex。你将 defer 放在函数开始的位置，它会等到函数执行完毕后再执行。

package main

import "fmt"

func main() {
    defer fmt.Println("函数结束时执行")
    fmt.Println("程序执行中")
}
输出：

程序执行中
函数结束时执行
你看到没有？defer 会确保 "函数结束时执行" 被打印出来，即使函数中间发生了异常。

6. 可变参数 (...)
Go 允许你在函数中使用可变参数，这对于接收不确定数量的参数非常有用。比起传统语言的 varargs，Go 采用的是 ... 语法，让你可以接收任意数量的参数。

package main

import "fmt"

func sum(nums ...int) {
    total := 0
    for _, num := range nums {
        total += num
    }
    fmt.Println("Total:", total)
}

func main() {
    sum(1, 2, 3, 4, 5)
}
通过 ...，我们可以很方便地将任意数量的整数传递给函数，代码简洁又灵活。

7. 命名返回值 (Named Return Values)
Go 支持命名返回值，即在函数签名中为返回值命名，这样可以省去在 return 语句中显式列出返回值的麻烦。

package main

import "fmt"

func getValues() (x int, y int) {
    x = 5
    y = 10
    return
}

func main() {
    a, b := getValues()
    fmt.Println(a, b) // 输出：5 10
}
你看到的 return 语句没有参数，Go 会自动返回之前定义好的 x 和 y。这种写法不仅简洁，而且能显得更直观。

8. 多重赋值 (Multiple Assignment)
多重赋值是 Go 语法糖中的另一个亮点，它允许你一次性给多个变量赋值，甚至能交换两个变量的值。

package main

import "fmt"

func main() {
    a, b := 1, 2
    a, b = b, a // 交换 a 和 b
    fmt.Println(a, b) // 输出：2 1
}
一次赋值，多个变量搞定，这比你每个都写一行代码赋值要干净多了吧。

9. 条件变量赋值
Go 允许你在 if 语句中同时进行条件判断和变量赋值。这样，你可以将变量的赋值和条件判断结合在一起，避免额外的代码。

package main

import "fmt"

func main() {
    if x := 5; x > 3 {
        fmt.Println("x is greater than 3") // 输出：x is greater than 3
    }
}
在 if 语句里，我们直接定义并赋值了变量 x，然后进行条件判断。

10. 切片扩展和数组切片 (Slicing)
Go 中的切片（slice）是一个非常强大的数据结构，它可以让你方便地从数组中截取子数组，甚至能动态扩展。

package main

import "fmt"

func main() {
    arr := []int{1, 2, 3, 4, 5}
    slice := arr[1:4] // 截取数组的部分
    fmt.Println(slice) // 输出：[2 3 4]
}
通过 [:] 语法，你可以灵活地获取切片的子集，极大地方便了数据处理。

11. map 的零值 (Zero Value)
Go 中的 map 是引用类型，未初始化的 map 默认是 nil，不能直接写入。你需要使用 make 来初始化它。

package main

import "fmt"

func main() {
    var m map[string]int // 默认是 nil
    fmt.Println(m == nil) // 输出：true

    m = make(map[string]int) // 初始化
    m["a"] = 1
    fmt.Println(m) // 输出：map[a:1]
}
12. select 语句 (多路复用)
Go 的 select 语句让你可以同时处理多个 channel 的读写，类似于 switch，非常适合并发编程。

package main

import "fmt"

func main() {
    ch1 := make(chan int)
    ch2 := make(chan int)

    go func() { ch1 <- 1 }()
    go func() { ch2 <- 2 }()

    select {
    case msg1 := <-ch1:
        fmt.Println("Received from ch1:", msg1)
    case msg2 := <-ch2:
        fmt.Println("Received from ch2:", msg2)
    }
}
通过 select，你能优雅地同时处理多个 channel，代码简洁且高效。

总结
Go 语言通过这些语法糖，真正做到了让我们的代码更加简洁、高效。
```


# go make(map[string]int)解析
```text
Go map的概念及三种使用方法

NGC_2070

于 2020-07-01 12:37:58 发布

阅读量3.6k
 收藏 5

点赞数 2
分类专栏： Golang基础 文章标签： go golang
版权

Golang基础
专栏收录该内容
119 篇文章
订阅专栏
 本文深入讲解Golang中map数据结构的使用方法，包括其概念、基本语法、声明与初始化过程，以及通过多个案例演示如何在实际编程中运用map。
摘要生成于 C知道 ，由 DeepSeek-R1 满血版支持， 前往体验 >

map的概念
map 的基本介绍
map 是 key-value 数据结构，又称为字段或者关联数组。类似其它编程语言的集合，
基本语法
var map 变量名  map[keytype]valuetype
key 可以是什么类型
golang 中的 map，的 key 可以是很多种类型，比如 bool, 数字，string, 指针, channel , 还可以是只包含前面几个类型的 接口, 结构体, 数组
通常 key 为 int 、string
注意:
slice， map 还有 function 不可以，因为这几个没法用 == 来判断
valuetype 可以是什么类型
valuetype 的类型和 key 基本一样
通常为: 数字(整数,浮点数)，string,，map，struct
map 声明的举例：
var a map[string]string
var a map[string]int
var a map[int]string
var a map[string]map[string]string
注意：声明是不会分配内存的，初始化需要 make ，分配内存后才能赋值和使用。
案例演示：
//map的声明和注意事项 
var a map[string]string
//在使用map前，需要先make , make的作用就是给map分配数据空间
a = make(map[string]string, 10)
a["no1"] = "宋江" //ok?
a["no2"] = "吴用" //ok?
a["no1"] = "武松" //ok?
a["no3"] = "吴用" //ok?
fmt.Println(a)
输出结果：


对上面代码的说明
map 在使用前一定要 make
map 的 key 是不能重复，如果重复了，则以最后这个 key-value 为准
map 的 value 是可以相同的. 
map 的 key-value 是无序
make 内置函数数目


map的三种使用方法
方式 1
--map的声明
var a map[string]string
-- 在使用map前，需要先make , make的作用就是给map分配数据空间
a = make(map[string]string, 10)
方式 2
cities := make(map[string]string)
cities["no1"] = "北京"
cities["no2"] = "天津"
cities["no3"] = "上海"
fmt.Println(cities)
输出：


方式 3
heroes := map[string]string{
	"hero1" : "小卤蛋",
	"hero2" : "瑶瑶公主",
	"hero3" : "吉吉国王",
}
heroes["hero4"] = "高地虎"
fmt.Println("heroes=", heroes)
输出：


应用案例：
比如：我们要存放 3 个学生信息, 每个学生有 name 和 sex 信息
studentMap := make(map[string]map[string]string)
 
studentMap["stu01"] =  make(map[string]string, 3)
studentMap["stu01"]["name"] = "tom"
studentMap["stu01"]["sex"] = "男"
studentMap["stu01"]["address"] = "北京长安街"
 
studentMap["stu02"] =  make(map[string]string, 3) //这句话不能少!!
studentMap["stu02"]["name"] = "mary"
studentMap["stu02"]["sex"] = "女"
studentMap["stu02"]["address"] = "上海黄浦江"
 
fmt.Println(studentMap)
fmt.Println(studentMap["stu02"])
输出：

————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/baidu_41388533/article/details/107061572
```

# https://zhuanlan.zhihu.com/p/87565681/
# 移除pagefile.sys和hiberfil.sys获取C盘空间

