```text
go get命令详解
go get

下载导入路径指定的包及其依赖项，然后安装命名包，即执行go install命令。（推荐：go语言教程）

用法：go get [-d] [-f] [-t] [-u] [-fix] [-insecure] [build flags] [packages]

标记名称 描述
-d 让命令程序只执行下载动作，而不执行安装动作。
-f 仅在使用-u标记时才有效。该标记会让命令程序忽略掉对已下载代码包的导入路径的检查。如果下载并安装的代码包所属的项目是你从别人那里Fork过来的，那么这样做就尤为重要了。
-fix 让命令程序在下载代码包后先执行修正动作，而后再进行编译和安装。
-insecure 允许命令程序使用非安全的scheme（如HTTP）去下载指定的代码包。如果你用的代码仓库（如公司内部的Gitlab）没有HTTPS支持，可以添加此标记。请在确定安全的情况下使用它。
-t 让命令程序同时下载并安装指定的代码包中的测试源码文件中依赖的代码包。
-u 让命令利用网络来更新已有代码包及其依赖包。默认情况下，该命令只会从网络上下载本地不存在的代码包，而不会更新已有的代码包。
-v 打印出被构建的代码包的名字
-x 打印出用到的命令
go install

使用：go install [-i] [build flags] [packages]。

和go build命令比较相似，go build命令会编译包及其依赖，生成的文件存放在当前目录下。而且go build只对main包有效，其他包不起作用。而go install对于非main包会生成静态文件放在$GOPATH/pkg目录下，文件扩展名为a。如果为main包，则会在$GOPATH/bin下生成一个和给定包名相同的可执行二进制文件。

综上： go get命令会下载指定的包，并将下载的包进行编译，然后安装到特定目录。
```

```text
Go 1.16 中关于 go get 和 go install 你需要注意的地方
 2020.12.19  1515  4 分钟
目录
概览
Go 1.16 中已解决的工具安装问题
关于不带 @version 的 go install
关于 go get 和 go.mod
go.mod 如何编辑
从 1.15 升级需要注意什么？
总结
Go (golang) 已于 18 日发布了 1.16 beta1 版本，至此其主体功能已经基本确定。我看大多数人都在关注 Go 在苹果(Apple) M1 上的支持，甚至 Go 官方博客中也有一篇专门的说明 Go on ARM and Beyond ，来介绍 Go 在此方面的支持。

我就不凑热闹了，我来聊聊 Go 1.16 中关于 go get 和 go install 你需要注意的地方。

目前 Docker 官方镜像尚未发布，我是本地构建了个镜像来使用。

(MoeLove) ➜  go version
go version go1.16beta1 linux/amd64
概览
Go 1.16 中包含着大量的 Modules 相关的更新，详细内容可直接查看其 Release Note。整体而言，包含以下要点：

GO111MODULE 默认为 on ，如果要恢复到之前的行为，则需要将 GO111MODULE 设置为 auto ，这样差不多意味着 GOPATH 模式要逐步淡出人们的视野了；
go install 命令可以接受一个版本后缀了，（例如，go install sigs.k8s.io/kind@v0.9.0），并且它是在模块感知的模式下运行，可忽略当前目录或上层目录的 go.mod 文件。这对于在不影响主模块依赖的情况下，安装二进制很方便；
在将来，go install 被设计为“用于构建和安装二进制文件”， go get 则被设计为 “用于编辑 go.mod 变更依赖”，并且使用时，应该与 -d 参数共用，在将来版本中 -d 可能会默认启用；
go build 和 go test 默认情况下不再修改 go.mod 和 go.sum。可通过 go mod tidy ，go get 或者手动完成；
总结而言，关于 go install 和 go get 必须要注意的是：

基本上 go install <package>@<version> 是用于命令的全局安装：

例如：go install sigs.k8s.io/kind@v0.9.0;
go get 安装二进制的功能，后续版本将会删除；

go get 主要被设计为修改 go.mod 追加依赖之类的，但还存在类似 go mod tidy 之类的命令，所以使用频率可能不会很高；

Go 1.16 中已解决的工具安装问题
到目前为止，Go 一直使用 go get 命令，将我们需要的工具安装到 $GOPATH/bin 目录下，但这种方式存在一个很严重的问题。go get 由于具备更改 go.mod 文件的能力，因此我们 必须要避免执行 go get 命令时，让它接触到我们的 go.mod 文件 ，否则它会将我们安装的工具作为一个依赖。

目前的解决方案通常是：

(MoeLove) ➜  cd $(mktemp -d); GO111MODULE=on go get sigs.k8s.io/kind@v0.9.0
自 1.16 开始，我们可以直接使用下面的方式：

(MoeLove) ➜  go install sigs.k8s.io/kind@v0.9.0
非常的简单直观。需要注意的是 go install <package>@<version> 是从 1.16 开始增加的，无论你当前是否在一个模块下，此命令都会在 $GOPATH/bin 下安装指定版本的工具。

此外由于 Go 1.16 中 GO111MODULE 默认是打开的，go install 不会修改 go.mod 之类的文件，不会造成任何意外。

注意：

@version 只能安装主软件包。非主程序包不受此格式约束。

关于不带 @version 的 go install
在模块外，不带 @version 是无法安装的，会有如下错误:
(MoeLove) ➜  go install  -v sigs.k8s.io/kind
go install: version is required when current directory is not in a module
        Try 'go install sigs.k8s.io/kind@latest' to install the latest version
如果你在模块目录中，并且你不带 @version 执行安装的话，只能安装 go.mod 中已经包含的版本。并且不能安装未出现在 go.mod 中的包。
(MoeLove) ➜  mkdir -p /go/src/github.com/moelove/iris
(MoeLove) ➜  cd /go/src/github.com/moelove/iris
# 初始化模块
(MoeLove) ➜  /go/src/github.com/moelove/iris go mod init
go: creating new go.mod: module github.com/moelove/iris
(MoeLove) ➜  /go/src/github.com/moelove/iris cat go.mod 
module github.com/moelove/iris

go 1.16


# 不带 @version 无法安装
(MoeLove) ➜  /go/src/github.com/moelove/iris go install -v sigs.k8s.io/kind
no required module provides package sigs.k8s.io/kind; try 'go get -d sigs.k8s.io/kind' to add it

# 用 go get -d 下载
(MoeLove) ➜  /go/src/github.com/moelove/iris go get -d sigs.k8s.io/kind
go get: added sigs.k8s.io/kind v0.9.0

# 可以看到已经被添加到了模块依赖中
(MoeLove) ➜  /go/src/github.com/moelove/iris cat go.mod 
module github.com/moelove/iris

go 1.16

require sigs.k8s.io/kind v0.9.0 // indirect

# 删除本地的 kind 工具
(MoeLove) ➜  /go/src/github.com/moelove/iris which kind
/go/bin/kind
(MoeLove) ➜  /go/src/github.com/moelove/iris rm /go/bin/kind
(MoeLove) ➜  /go/src/github.com/moelove/iris which kind

# 不带 @version 进行安装
(MoeLove) ➜  /go/src/github.com/moelove/iris go install -v sigs.k8s.io/kind
(MoeLove) ➜  /go/src/github.com/moelove/iris which kind
/go/bin/kind
(MoeLove) ➜  /go/src/github.com/moelove/iris kind version
kind v0.9.0 go1.16beta1 linux/amd64
关于 go get 和 go.mod
go get 将二进制安装相关的功能都转移到了 go install, 仅作为用于编辑 go.mod 文件的命令存在。在后续版本（计划是 Go 1.17）中删掉 go get 安装二进制的功能，接下来 go get 的行为就等同于我们现在执行 go get -d 命令了，仅需下载源码，并将依赖添加至 go.mod 即可。

go.mod 如何编辑
在 Go 1.16 中，另一个行为变更是 go build 和 go test 不会自动编辑 go.mod 了，基于以上信息，Go 1.16 中将进行如下处理：

通过在代码中修改 import 语句，来修改 go.mod：

go get 可用于添加新模块；
go mod tidy 删除掉无用的模块；
将未导入的模块写入 go.mod:

go get <package>[@<version>];
go mod tidy 也可以；
手动编辑；
从 1.15 升级需要注意什么？
由于 go build 和 go test 不会自动编辑 go.mod 了，所以可以将原本的行为通过 go mod tidy 共同处理。

总结
Go 1.16 中 go install 和 go get 方面有些不兼容的变更，但是 1.16 中模块更加简洁，减少了使用时的心智负担，我还是很期待这个版本的。

欢迎订阅我的文章公众号【MoeLove】

TheMoeLove

作者：张晋涛
链接：https://moelove.info/2020/12/19/Go-1.16-中关于-go-get-和-go-install-你需要注意的地方/
许可：CC BY-NC-SA 4.0

```


### 有些版本 go get命令不会更新go.mod文件，需要go get -d

```text

golang: go mod命令的常见子命令
2023-09-04
560
阅读3分钟

参考网址
blog.csdn.net/qq_48480384…

go mod命令的常用子命令
go mod命令的子命令有如下几个：

go mod download    下载依赖的module到本地cache（默认为$GOPATH/pkg/mod目录）
go mod edit        编辑go.mod文件
go mod graph       打印模块依赖图
go mod init        初始化当前文件夹, 创建go.mod文件
go mod tidy        增加缺少的module，删除无用的module
go mod vendor      将依赖复制到vendor下
go mod verify      校验依赖
go mod why         解释为什么需要依赖
go mod init project_name
`go mod init` 命令用于初始化一个新的 Go 模块。
它会在当前目录下创建一个新的 `go.mod` 文件，该文件包含了模块的名称、版本等信息，以及该模块所依赖的其他模块的信息。
在使用 `go mod` 管理依赖关系时，`go.mod` 文件是必须的。

go mod init project_name，该命令是给你的项目（应用）创建一个go.mod文件。
go.mod文件中存放的是：你的项目（应用）中所依赖的其他的项目（应用）
你的项目中 import 的的东西，就是你所依赖的东西(不用重复造轮子，直接用别人的，这就是依赖)
go mod tidy
go mod tidy 命令用于整理和清理项目的 go.mod 文件和依赖项。
它的主要作用是移除未使用的依赖项，更新依赖项的版本，以及将 go.mod 文件中的依赖项列表与实际使用的依赖项列表保持一致。

当我们在开发 Go 项目时，通常会使用第三方包或库。
这些依赖项会被记录在项目的 go.mod 文件中。
但是，有时候我们可能会添加一些依赖项，但最终却没有使用它们。这些未使用的依赖项将会增加项目的体积和构建时间，因此需要将其移除。
此外，当我们使用的依赖项有新的版本时，我们也需要更新依赖项的版本以获得更好的性能和安全性。 

go mod tidy 命令会自动分析项目代码和依赖项，找出未使用的依赖项并将其移除，同时更新依赖项的版本。它还会检查 go.mod 文件中的依赖项列表是否与实际使用的依赖项列表一致，如果不一致，会自动更新 go.mod 文件中的依赖项列表。这样可以确保项目的依赖项列表始终保持最新和准确。
go build project_name
go build 可以让我们创建一个二进制的程序，我们在新的环境中即使没有GO的环境下也能使用这个包。
但是在不同操作系统下这个二进制的程序也是不同的
go get
在项目中执行go get命令可以下载依赖包，并且还可以指定下载的版本。

运行go get -u将会升级到最新的次要版本或者修订版本(x.y.z, z是修订版本号， y是次要版本号)
运行go get -u=patch将会升级到最新的修订版本
运行go get package@version将会升级到指定的版本号version

如果下载所有依赖可以使用go mod download命令。
go mod edit
格式化
因为我们可以手动修改go.mod文件，所以有些时候需要格式化该文件。Go提供了一下命令：
go mod edit -fmt

添加依赖项
go mod edit -require=golang.org/x/text

移除依赖项
如果只是想修改go.mod文件中的内容，那么可以运行go mod edit -droprequire=package path，比如要在go.mod中移除golang.org/x/text包，可以使用如下命令：

go mod edit -droprequire=golang.org/x/text
关于go mod edit的更多用法可以通过go help mod edit查看。

```

```text
【go语言】Go基础知识：go.mod文件、go mod命令、私有仓库、导入版本管理、Vendor目录详解
go.mod 文件是Go语言中的模块文件，用于管理项目的依赖关系和版本信息。go.mod 文件通常位于项目的根目录下，用于定义模块的名称、依赖关系和版本信息。

 

一、go.mod文件及go mod命令
1、go.mod 文件的基本结构
 View Code
module: 定义模块的名称，通常是项目的导入路径。该路径也用于唯一标识模块。
go: 指定项目的最小Go语言版本，确保项目在指定版本及以上的Go环境中可以正确构建。
require: 列出模块的直接依赖关系及其版本。这里 github.com/example/package 是一个示例依赖，指定了版本 v1.2.3。
replace: 指定替代或替换规则，用于本地调试或使用自定义的包。
2、初始化模块
使用 go mod init 命令初始化一个新的模块。这会创建一个 go.mod 文件，记录项目的模块路径和依赖项。

 View Code
这将创建一个 go.mod 文件，其中包含模块声明：

 View Code
3、添加依赖项
使用 go get 命令添加新的依赖项，并自动更新 go.mod 文件。例如，go get github.com/example/package@v1.2.3 将添加 github.com/example/package 作为依赖，并指定版本为 v1.2.3。

 View Code
这会在 go.mod 中添加如下条目：

 View Code
4、下载依赖项
使用 go mod download 命令下载模块的所有依赖项，但不安装它们。这个命令通常在构建之前执行，以确保所有依赖项都已下载。

 View Code
5、查看依赖项
使用 go list -m all 命令查看项目的所有依赖项及其版本。这有助于了解项目的整体依赖关系。

go list -m all
6、更新依赖项
使用 go get -u 命令更新项目的依赖项。例如，go get -u 将更新所有依赖项至其最新版本。

 View Code
7、清理不使用的依赖项
使用 go mod tidy 命令清理未使用的依赖项。这有助于保持 go.mod 文件的简洁性，只保留项目实际使用的依赖项。

 View Code
8、编辑 go.mod 文件
手动编辑 go.mod 文件允许添加、删除或更改依赖项。但通常推荐使用 go get 或其他 go mod 命令来管理依赖，以确保文件的正确性。

9、Vendor 目录
Go 模块支持 vendor 目录，可以用于存放项目的依赖项的本地拷贝。这有助于项目在不同环境中保持一致性。

 View Code
10、替代模块
使用 replace 指令可以指定替代或替换规则，例如使用本地代码替代远程库。这对于本地开发和调试非常有用。

 View Code
11、替换依赖项
使用 go mod edit 命令替换特定的依赖项。

 View Code
12、私有仓库
对于私有仓库，可以使用 GOPRIVATE 环境变量来设置私有仓库的规则，以确保 go get 等命令可以正确访问。

 View Code
13、导入版本管理
Go 模块支持语义版本管理，可以通过 vX.Y.Z 形式来指定版本。这确保了在依赖项更新时项目的稳定性。

14、不使用模块
如果你的项目不使用模块，可以通过设置环境变量 GO111MODULE=off 来禁用模块。这使得项目回退到传统的 GOPATH 模式。

 View Code
15、帮助文档
运行 go help mod 可以查看关于 Go 模块的详细帮助文档，其中包含了更多高级的使用和配置选项。

16、总结
go.mod 文件是Go语言模块的核心配置文件，通过其中的命令和规则，开发者可以方便地初始化、添加、更新、删除依赖项，以及进行版本管理。这有助于项目的可维护性和依赖管理的精确性。

二、Vendor目录详解
在Go语言中，vendor 目录是用于存放项目的依赖项的本地拷贝的目录。使用 vendor 目录的主要目的是为了在项目中固定依赖项的版本，以确保在不同环境中构建时使用相同的代码。

1、Vendor 目录的作用

版本控制： 通过将依赖项的源代码复制到项目的 vendor 目录中，可以确保项目使用的是特定版本的依赖项。这有助于避免由于外部依赖项的更改而导致的不稳定性。
离线构建： vendor 目录使得项目在没有网络连接的情况下仍然能够构建。所有必需的依赖项都在本地，无需依赖远程仓库。
2、使用 Vendor 目录

自动生成： Go语言在构建项目时会自动生成 vendor 目录，其中包含项目的所有依赖项的本地拷贝。可以通过 go build 或 go install 等命令来触发自动生成。
导入路径： vendor 目录中的代码使用和其他外部依赖项相同的导入路径。这意味着项目中的代码无需修改，即可引用 vendor 目录中的依赖项。
3、更新 Vendor 目录
手动更新： 可以通过运行 go mod vendor 命令手动更新 vendor 目录。这将根据当前的 go.mod 文件中的依赖关系重新生成 vendor 目录。
 View Code
 

4、Vendor 目录的使用示例
初始化模块：

go mod init example.com/mymodule
添加依赖项：

go get github.com/example/package@v1.2.3
生成 vendor 目录：

go mod vendor
构建项目：

go build
5、注意事项
优先级： 在使用 vendor 目录的情况下，Go 会优先使用 vendor 目录中的依赖项，而不是从远程下载。这确保了版本的稳定性。
推荐使用： 尽管 Go 模块已经成为推荐的依赖管理方式，但 vendor 目录仍然是一种可行的方式，特别是对于需要离线构建或者对特定版本有要求的项目。
6、总结
不推荐修改： 通常情况下，不建议手动修改 vendor 目录中的代码。修改应当通过修改依赖项的源代码或者更新依赖项版本来进行。
总体来说，vendor 目录在一些情况下是一个有用的工具，可以确保项目的依赖项版本的一致性，尤其是在一些需要离线构建或者在不同环境中部署的场景中。
三、替代模块replace详解
在Go语言中，replace 是一种在 go.mod 文件中用于指定替代或替换规则的指令。通过 replace，你可以用本地文件系统中的代码替换远程版本控制系统（例如Git、Mercurial等）中的模块，或者使用其他特定版本的模块。这对于本地开发、调试和测试非常有用。

以下是关于 replace 的详细讲解：

1、replace 的基本结构
在 go.mod 文件中，replace 的基本结构如下：

 View Code
其中，old/module 是原始的导入路径，new/module 是替代的导入路径。可以使用相对路径或绝对路径来指定替代的位置。


2、使用本地代码替代远程库的示例
 View Code
这个示例中，项目中原本依赖于 github.com/example/package 的代码将被替换为本地路径 ../local/package 下的代码。

 
3、替代规则的应用场景
本地开发： 允许在本地修改和测试依赖项的代码，而不必在每次更改后提交并推送到远程仓库。
使用不同的分支或版本： 可以将依赖项指定为不同的分支或特定的提交哈希，以测试或使用不同的代码状态。
使用其他来源的模块： 允许将依赖项替换为其他来源，例如自定义的修复或调试版本。
 

 

4、使用替代规则的步骤
1、在 go.mod 文件中添加 replace 规则。

replace (
github.com/example/package => ../local/package
)

2、运行 go mod tidy 命令。

go mod tidy

这会更新 go.mod 文件并应用替代规则。

 

3、运行 go build 或其他构建命令。

go build
Go 将使用替代规则指定的本地代码或其他来源的模块。

 

5、替代规则的注意事项
谨慎使用： 替代规则应当小心使用，确保在生产环境之前，所有替代都经过适当的测试。
相对路径： 使用相对路径时，确保路径的结构正确，并且可以正确找到替代的代码。
版本管理： 替代规则可能会绕过Go模块的版本管理机制，因此使用时要确保明白替代的影响。
6、清除替代规则
如果需要清除替代规则，可以直接从 go.mod 文件中删除相关的 replace 指令，然后运行 go mod tidy。
 

7、替代规则的实际应用场景
 View Code
在这个示例中，通过 replace 指定了 golang.org/x/net 模块的特定版本，以确保项目使用的是指定的代码状态。

 
8、总结
总体来说，replace 提供了一种灵活的方式，使得在开发过程中能够方便地替代依赖项，从而更容易进行本地调试和测试。然而，在生产环境中，建议小心使用，确保在正式发布之前清理和验证替代规则。

 

四、私有仓库详解
在Go语言中，使用私有仓库是常见的需求，尤其是当你的项目依赖于不希望公开的代码时。私有仓库可以是自己搭建的Git服务器、GitLab、Bitbucket等，而Go语言本身对私有仓库的支持较好。

1、导入路径与私有仓库
Go语言中，导入路径是用于标识和定位代码包的唯一标识符。对于私有仓库，导入路径通常包含用户名或组织名以及仓库名。

例如，私有仓库的导入路径可能类似于：

 View Code
2、使用私有仓库的步骤
1、认证访问： 当你使用私有仓库时，通常需要进行认证。这可以通过SSH密钥、HTTP基本认证或访问令牌等方式实现。

2、配置 go get： Go工具链提供了 go get 命令用于获取远程包，但默认情况下，它要求导入路径是公共的。对于私有仓库，你可以使用SSH或HTTPS协议，并提供认证信息。


使用SSH：

go get gitlab.com/username/myprivateproject
使用HTTPS：

go get https://gitlab.com/username/myprivateproject
 

3、认证配置： 如果使用SSH，确保你的SSH密钥正确配置并且已经添加到你的Git服务器。如果使用HTTPS，可能需要提供用户名和密码或者访问令牌。

 

3、在 go.mod 文件中使用私有仓库
如果你使用Go模块（Go Modules），你可以在 go.mod 文件中直接指定私有仓库的版本。

 View Code
4、使用 ~/.netrc 文件进行认证
对于HTTPS协议，你可以在 ~/.netrc 文件中配置认证信息，以避免在命令行中暴露敏感信息。

 View Code
请注意，这种方式并不安全，因为 ~/.netrc 文件中的密码是明文存储的。更安全的方式是使用访问令牌或其他认证机制。

5、使用私有模块代理
你也可以设置私有的模块代理（例如，搭建自己的Go Proxy），将私有仓库的代码缓存到本地，从而在构建时无需直接访问私有仓库。这有助于提高构建速度，同时保护私有代码的安全性。

6、私有仓库的注意事项
认证安全性： 确保你的认证信息安全。不要直接在代码或配置文件中硬编码敏感信息。
版本管理： 对于私有仓库，推荐使用Go模块进行版本管理，确保项目的稳定性。
代理： 考虑使用模块代理来加速构建，并减少对私有仓库的直接依赖。
 

 

五、导入版本管理详解
在Go语言中，导入版本管理主要依赖于模块化系统，Go Modules 是自Go 1.11版本开始引入的官方版本管理工具。通过 Go Modules，开发者可以更好地管理和维护项目所依赖的第三方包，确保项目在不同环境中能够稳定地构建和运行。

1、Go Modules 简介
模块初始化： 可以通过命令 go mod init 初始化一个新的模块。这将创建一个 go.mod 文件，记录了模块的名称和依赖项信息。
　　go mod init example.com/myproject
依赖解析： 当你使用 go get 或其他命令获取包时，Go Modules 会更新 go.mod 文件，记录依赖关系及其版本。
 

2、go.mod 文件结构
模块声明： go.mod 文件中的第一行包含了模块的声明，指定了模块的名称和路径。

　　module example.com/myproject
依赖声明： 在 go.mod 文件中，会列出项目的依赖项及其版本信息。

require (
github.com/pkg1 v1.2.3
github.com/pkg2 v0.4.1
)

3、版本管理操作
添加依赖： 可以使用 go get 命令来添加依赖项，同时更新 go.mod 文件。

go get github.com/pkg1@v1.2.3

更新依赖： 通过使用 go get -u 命令来更新依赖项的版本。

go get -u github.com/pkg1
 

4、版本约束和语义化版本控制

版本约束： 在 go.mod 文件中，可以通过 go get 命令来约束依赖项的版本，例如 @v1.2.3。

语义化版本控制： Go语言鼓励使用语义化版本控制（Semantic Versioning），即 MAJOR.MINOR.PATCH 的版本号格式，用于明确表达依赖项的向后兼容性。

 

5、间接依赖项管理
Vendor 目录： 通过 go mod vendor 命令将依赖项下载至 vendor 目录中，以便项目可以在不同环境中进行构建和部署。

不直接修改 Vendor 目录： 建议不要直接修改 vendor 目录中的代码，而是应该通过版本管理工具来管理依赖项。

 

6、并发版本管理
并发安全： Go Modules 通过并发来管理和解析依赖项，可以更快速地解析模块的依赖关系。

 

7、私有仓库和代理支持
私有仓库： Go Modules 支持与私有仓库的交互，可以通过合适的认证方式访问私有仓库。

代理支持： 可以配置代理来缓存下载的模块，加速构建过程并减少对外部网络的依赖。

 

8、版本管理的最佳实践
稳定版本： 尽量使用稳定的版本来确保项目的稳定性。
定期更新： 定期更新依赖项的版本，保持项目的安全性和最新性。
语义化版本控制： 遵循语义化版本规范，清晰地表达依赖项的向后兼容性。
持续集成（CI）： 在持续集成流程中集成依赖项的管理，确保项目构建的可重复性和稳定性。
```


```text
go mod使用 | 全网最详细
Golang发烧友
Golang发烧友
来自专栏 · Golang发烧友
99 人赞同了该文章
一个包管理工具应该有以下功能：

基本功能

依赖管理
依赖包版本控制
对应的包管理平台
可以私有化部署
加分：

代码包是否可以复用
构建，测试,打包
发布上线
对比上面几点：来谈谈今天主角是go mod。



使用go path问题

代码开发必须在go path src目录下，不然，就有问题。
依赖手动管理
依赖包没有版本可言
从这个看， go path不算包管理工具

govendor

解决了包依赖，一个配置文件就管理
依赖包全都下载到项目vendor下，每个项目都把有一份。拉取项目时,开始怀疑人生。


go mod介绍

go modules 是 golang 1.11 新加的特性。现在1.12 已经发布了，是时候用起来了。Modules官方定义为：

模块是相关Go包的集合。modules是源代码交换和版本控制的单元。go命令直接支持使用modules，包括记录和解析对其他模块的依赖性。modules替换旧的基于GOPATH的方法来指定在给定构建中使用哪些源文件。


如何使用go mod

首先，必须升级go到1.11,目前版本是1.14，下面我以我自己升级演示：

### 卸载旧版本，删除对应文件
brew uninstall -f go

### 更新一下brew

brew update


### 安装go
brew install go
上面升级完了，使用 go version看下版本

go version go1.14.1 darwin/amd64
下面设置go mod和go proxy

go env -w GOBIN=/Users/youdi/go/bin
go env -w GO111MODULE=on
go env -w GOPROXY=https://goproxy.cn,direct // 使用七牛云的
注意：go env -w会将配置写到 GOENV="/Users/youdi/Library/Application Support/go/env"

下面看下我的配置

GO111MODULE="on"
GOARCH="amd64"
GOBIN="/Users/youdi/go/bin"
GOCACHE="/Users/youdi/Library/Caches/go-build"
GOENV="/Users/youdi/Library/Application Support/go/env"
GOEXE=""
GOFLAGS=""
GOHOSTARCH="amd64"
GOHOSTOS="darwin"
GOINSECURE=""
GONOPROXY=""
GONOSUMDB=""
GOOS="darwin"
GOPATH="/Users/youdi/go"
GOPRIVATE=""
GOPROXY="https://goproxy.cn,direct"
GOROOT="/usr/local/go"
GOSUMDB="off"
GOTMPDIR=""
GOTOOLDIR="/usr/local/go/pkg/tool/darwin_amd64"
GCCGO="gccgo"
AR="ar"
CC="clang"
CXX="clang++"
CGO_ENABLED="1"
GOMOD="/dev/null"
CGO_CFLAGS="-g -O2"
CGO_CPPFLAGS=""
CGO_CXXFLAGS="-g -O2"
CGO_FFLAGS="-g -O2"
CGO_LDFLAGS="-g -O2"
PKG_CONFIG="pkg-config"
GOGCCFLAGS="-fPIC -m64 -pthread -fno-caret-diagnostics -Qunused-arguments -fmessage-length=0 -fdebug-prefix-map=/var/folders/8m/v_1j4dgs7rzgqq4p_4_8k_nr0000gn/T/go-build221113671=/tmp/go-build -gno-record-gcc-switches -fno-common"
我们看一下，我修改的内容

cat /Users/youdi/Library/Application Support/go/env

GO111MODULE=on
GOBIN=/Users/youdi/go/bin
GOPROXY=https://goproxy.cn,direct
GOSUMDB=off
GO111MODULE

GO111MODULE 有三个值：off, on和auto（默认值）。

GO111MODULE=off，go命令行将不会支持module功能，寻找依赖包的方式将会沿用旧版本那种通过vendor目录或者GOPATH模式来查找。

GO111MODULE=on，go命令行会使用modules，而一点也不会去GOPATH目录下查找。

GO111MODULE=auto，默认值，go命令行将会根据当前目录来决定是否启用module功能。这种情况下可以分为两种情形：

当前目录在GOPATH/src之外且该目录包含go.mod文件
当前文件在包含go.mod文件的目录下面。
当modules功能启用时，依赖包的存放位置变更为$GOPATH/pkg，允许同一个package多个版本并存，且多个项目可以共享缓存的 module

我们看下目录：

cd /Users/youdi/go/pkg

├── darwin_amd64
│   ├── github.com
│   ├── go.etcd.io
│   ├── golang
│   ├── golang.org
│   ├── gopkg.in
│   ├── quickstart
│   └── uc.a
├── mod
│   ├── cache
│   ├── github.com
│   ├── golang.org
│   ├── google.golang.org
│   └── gopkg.in
└── sumdb
    └── sum.golang.org
go mod命令

golang 提供了 go mod命令来管理包。

go help mod

Go mod provides access to operations on modules.

Note that support for modules is built into all the go commands,
not just 'go mod'. For example, day-to-day adding, removing, upgrading,
and downgrading of dependencies should be done using 'go get'.
See 'go help modules' for an overview of module functionality.

Usage:

    go mod <command> [arguments]

The commands are:

    download    download modules to local cache
    edit        edit go.mod from tools or scripts
    graph       print module requirement graph
    init        initialize new module in current directory
    tidy        add missing and remove unused modules
    vendor      make vendored copy of dependencies
    verify      verify dependencies have expected content
    why         explain why packages or modules are needed

Use "go help mod <command>" for more information about a command.
go mod 有以下命令：


比较常用的是 init,tidy, edit



使用go mod管理一个新项目

初始化项目
可以随便找一个目录创建项目，我使用习惯用IDEA进行创建

mkdir Gone
cd Gone
go mod init Gone
查看一下 go.mod文件

module Gone

go 1.14
go.mod文件一旦创建后，它的内容将会被go toolchain全面掌控。go toolchain会在各类命令执行时，比如go get、go build、go mod等修改和维护go.mod文件。

go.mod 提供了module, require、replace和exclude 四个命令

module 语句指定包的名字（路径）
require 语句指定的依赖项模块
replace 语句可以替换依赖项模块
exclude 语句可以忽略依赖项模块


2、添加依赖

创建 main.go文件

package main

import (
    "github.com/gin-gonic/gin"
)

func main() {
    r := gin.Default()
    r.GET("/ping", func(c *gin.Context) {
        c.JSON(200, gin.H{
            "message": "pong",
        })
    })
    r.Run() // listen and serve on 0.0.0.0:8080 (for windows "localhost:8080")
}
执行 go run main.go 运行代码会发现 go mod 会自动查找依赖自动下载,再查看 go.mod

module Gone

go 1.14

require github.com/gin-gonic/gin v1.6.3
go module 安装 package 的原則是先拉最新的 release tag，若无tag则拉最新的commit

go 会自动生成一个 go.sum 文件来记录 dependency tree


再次执行脚本 go run main.go发现跳过了检查并安装依赖的步骤。

可以使用命令 go list -m -u all 来检查可以升级的package，使用go get -u need-upgrade-package 升级后会将新的依赖版本更新到go.mod * 也可以使用 go get -u 升级所有依赖

去mod包缓存下看看

/Users/youdi/go/pkg/mod/github.com/gin-gonic/gin@v1.6.3
go get升级

运行 go get -u 将会升级到最新的次要版本或者修订版本(x.y.z, z是修订版本号， y是次要版本号)
运行 go get -u=patch 将会升级到最新的修订版本
运行 go get package@version 将会升级到指定的版本号version
运行go get如果有版本的更改，那么go.mod文件也会更改
使用replace替换无法直接获取的package

由于某些已知的原因，并不是所有的package都能成功下载，比如：http://golang.org下的包。

modules 可以通过在 go.mod 文件中使用 replace 指令替换成github上对应的库，比如：

replace (
    golang.org/x/crypto v0.0.0-20190313024323-a1f597ede03a => github.com/golang/crypto v0.0.0-20190313024323-a1f597ede03a
)
go mod发布和使用

参考Roberto Selbach写的go mod入门文章，文末，我给出链接

Creating a Module

如果你设置好go mod了，那你就可以在任何目录下随便创建

$mkdir gomodone
$cd gomodone
在这个目录下创建一个文件say.go

package gomodone

import "fmt" 

// say Hi to someone
func SayHi(name string) string {
   return fmt.Sprintf("Hi, %s", name)
}
初始化一个 go.mod文件

$ go mod init github.com/jacksonyoudi/gomodone
go: creating new go.mod: module github.com/jacksonyoudi/gomodone
查看 go.mod内容如下：

github.com/jacksonyoudi/gomodone
go 1.14
下面我们要将这个module发布到github上，然后在另外一个程序使用

$git init
$vim .gitiiignore
$git commit -am "init"
// github创建对应的repo
$git remote add origin git@github.com:jacksonyoudi/gomodone.git
$git push -u origin master
执行完，上面我们就相当于发布完了。

如果有人需要使用，就可以使用

go get github.com/jacksonyoudi/gomodone
这个时候没有加tag，所以，没有版本的控制。默认是v0.0.0后面接上时间和commitid。如下：

gomodone@v0.0.0-20200517004046-ee882713fd1e
官方不建议这样做，没有进行版本控制管理。

module versioning

使用tag，进行版本控制

making a release

git tag v1.0.0
git push --tags
操作完，我们的module就发布了一个v1.0.0的版本了。

推荐在这个状态下，再切出一个分支，用于后续v1.0.0的修复推送,不要直接在master分支修复

$git checkout -b v1
$git push -u origin v1
use our module

上面已经发布了一个v1.0.0的版本，我们可以在另一个项目中使用，创建一个go的项目

$mkdir Gone
$cd Gone
$vim main.go

package main

import (
    "fmt"
    "github.com/jacksonyoudi/gomodone"
)

func main() {
    fmt.Println(gomodone.SayHi("Roberto"))
}
代码写好了，我们生成 go mod文件

go mod init Gone
上面命令执行完，会生成 go mod文件 看下mod文件：

module Gone

go 1.14

require (
    github.com/jacksonyoudi/gomodone v1.0.0
)

$go mod tidy
go: finding module for package github.com/jacksonyoudi/gomodone
go: found github.com/jacksonyoudi/gomodone in github.com/jacksonyoudi/gomodone v1.0.0
同时还生成了go.sum, 其中包含软件包的哈希值，以确保我们具有正确的版本和文件。

github.com/jacksonyoudi/gomodone v1.0.1 h1:jFd+qZlAB0R3zqrC9kwO8IgPrAdayMUS0rSHMDc/uG8=
github.com/jacksonyoudi/gomodone v1.0.1/go.mod h1:XWi+BLbuiuC2YM8Qz4yQzTSPtHt3T3hrlNN2pNlyA94=
github.com/jacksonyoudi/gomodone/v2 v2.0.0 h1:GpzGeXCx/Xv2ueiZJ8hEhFwLu7xjxLBjkOYSmg8Ya/w=
github.com/jacksonyoudi/gomodone/v2 v2.0.0/go.mod h1:L8uFPSZNHoAhpaePWUfKmGinjufYdw9c2i70xtBorSw=
这个内容是下面的，需要操作执行的结果

go run main.go就可以运行了

Making a bugfix release

假如fix一个bug,我们在v1版本上进行修复

修改代码如下：

// say Hi to someone
func SayHi(name string) string {
-       return fmt.Sprintf("Hi, %s", name)
+       return fmt.Sprintf("Hi, %s!", name)
}
修复好，我们开始push

$ git commit -m "Emphasize our friendliness" say.go
$ git tag v1.0.1
$ git push --tags origin v1
Updating modules

刚才fix bug，所以要在我们使用项目中更新

这个需要我们手动执行更新module操作

我们通过使用我们的好朋友来做到这一点go get：

运行go get -u以使用最新的 minor 版本或修补程序版本（即它将从1.0.0更新到例如1.0.1，或者，如果可用，则更新为1.1.0）
运行 go get -u=patch 以使用最新的 修补程序 版本（即，将更新为1.0.1但不更新 为1.1.0）
运行go get package@version 以更新到特定版本（例如http://github.com/jacksonyoudi/gomodone@v1.0.1）
目前module最新的也是v1.0.1

// 更新最新
$go get -u
$go get -u=patch
//指定包，指定版本
$go get github.com/jacksonyoudi/gomodone@v1.0.1
操作完，go.mod文件会修改如下:

module Gone

go 1.14

require (
    github.com/jacksonyoudi/gomodone v1.0.1
)
Major versions

根据语义版本语义，主要版本与次要版本 不同。主要版本可能会破坏向后兼容性。从Go模块的角度来看，主要版本是 完全不同的软件包。乍一看这听起来很奇怪，但这是有道理的：两个不兼容的库版本是两个不同的库。比如下面修改，完全破坏了兼容性。

package gomodone

import (
    "errors"
    "fmt"
)

// Hi returns a friendly greeting
// Hi returns a friendly greeting in language lang
func SayHi(name, lang string) (string, error) {
    switch lang {
    case "en":
        return fmt.Sprintf("Hi, %s!", name), nil
    case "pt":
        return fmt.Sprintf("Oi, %s!", name), nil
    case "es":
        return fmt.Sprintf("¡Hola, %s!", name), nil
    case "fr":
        return fmt.Sprintf("Bonjour, %s!", name), nil
    default:
        return "", errors.New("unknown language")
    }
}
如上，我们需要不同的大版本，这种情况下

修改 go.mod如下

module github.com/jacksonyoudi/gomodone/v2

go 1.14
然后，重新tag，push

$ git commit say.go -m "Change Hi to allow multilang"
$ git checkout -b v2 # 用于v2版本，后续修复v2
$ git commit go.mod -m "Bump version to v2"
$ git tag v2.0.0
$ git push --tags origin v2 
Updating to a major version

即使发布了库的新不兼容版本，现有软件 也不会中断，因为它将继续使用现有版本1.0.1。go get -u 将不会获得版本2.0.0。如果想使用v2.0.0,代码改成如下：

package main

import (
    "fmt"
    "github.com/jacksonyoudi/gomodone/v2"
)

func main() {
    g, err := gomodone.SayHi("Roberto", "pt")
    if err != nil {
        panic(err)
    }
    fmt.Println(g)
}
执行 go mod tidy

go: finding module for package github.com/jacksonyoudi/gomodone/v2
go: downloading github.com/jacksonyoudi/gomodone/v2 v2.0.0
go: found github.com/jacksonyoudi/gomodone/v2 in github.com/jacksonyoudi/gomodone/v2 v2.0.0
当然，两个版本都可以同时使用, 使用别名 如下：

package main

import (
    "fmt"
    "github.com/jacksonyoudi/gomodone"
    mv2 "github.com/jacksonyoudi/gomodone/v2"
)

func main() {
    g, err := mv2.SayHi("Roberto", "pt")
    if err != nil {
        panic(err)
    }
    fmt.Println(g)

    fmt.Println(gomodone.SayHi("Roberto"))
}
执行一下 go mod tidy

Vendoring

默认是忽略vendor的，如果想在项目目录下有vendor可以执行下面命令

$go vendor
当然，如果构建程序的时候，希望使用vendor中的依赖，

$ go build -mod vendor
IDEA下开发GO

1、创建go项目


2、创建完项目，会自动生成go mod文件 如果需要修改，可以手动修改，加入git等操作

3、写业务逻辑代码


4、解决依赖，更新go.mod


5、go build

文章转自：https://www.jianshu.com/p/760c97ff644c

```


```text
GO-GRPC使用教程
且听风吟
且听风吟
程序爱好者,懂点深度学习,玩点后端工程。
36 人赞同了该文章
GO-GRPC使用教程

gRPC 是一个高性能、通用的开源 RPC 框架，其由 Google 主要面向移动应用开发并基于 HTTP/2 协议标准而设计，基于 ProtoBuf(Protocol Buffers) 序列化协议开发，且支持众多开发语言。

本教程提供了 Go 使用 gRPC 的基础教程

在教程中你将会学到如何：

在.proto 文件中定义一个服务。
使用 protocol buffer 编译器生成客户端和服务端代码。
使用 gRPC 的 Go API 为你的服务写一个客户端和服务器。
gRPC的几种服务类型。
继续之前，请确保你已经对 gRPC 概念有所了解，并且熟悉 protocol buffer。可以参照前面的 <<grpc简介一章>>。

安装相关工具和插件
安装 grpc 包
首先需要安装 gRPC golang版本的软件包。 安装官方安装命令：

go get google.golang.org/grpc
是安装不起的，会报：

package google.golang.org/grpc: unrecognized import path "google.golang.org/grpc"(https fetch: Get https://google.golang.org/grpc?go-get=1: dial tcp 216.239.37.1:443: i/o timeout)
原因是这个代码已经转移到github上面了，但是代码里面的包依赖还是没有修改，还是http://google.golang.org这种， 所以不能使用go get的方式安装，经本人从头实践，正确的安装方式：

# 下载grpc-go
git clone https://github.com/grpc/grpc-go.git $GOPATH/src/google.golang.org/grpc
# 下载golang/net
git clone https://github.com/golang/net.git $GOPATH/src/golang.org/x/net
# 下载golang/text
git clone https://github.com/golang/text.git $GOPATH/src/golang.org/x/text
# 下载sys
git clone https://github.com/golang/sys.git $GOPATH/src/golang.org/x/sys
# 下载go-genproto
git clone https://github.com/google/go-genproto.git $GOPATH/src/google.golang.org/genproto
# 下载go-protobuf
git clone https://github.com/protocolbuffers/protobuf-go.git  $GOPATH/src/google.golang.org/protobuf
cd $GOPATH/src/
go install google.golang.org/grpc
安装protoc编译器
protoc 用于编译 protocolbuf (.proto文件) 和 protobuf 运行时。

❝ protoc 是 C++ 写的，其可以将proto文件翻译为指定语言的代码。**比较简单的安装方式是直接下载编译好的二进制文件。**go get http://google.golang.org/protobuf/cmd/protoc-gen-gogo get http://google.golang.org/protobuf/cmd/protoc-gen-go
下载
安装编译器最简单的方式是去protobuf仓库地址下载预编译好的 protoc 二进制文件，仓库中可以找到每个平台对应的编译器二进制文件。




❝ 下载操作系统对应版本然后解压然后配置一下环境变量即可。
比如windows就下载protoc-3.18.0-win64.zip, 然后把解压后的xxx\protoc-3.18.0-win64\bin配置到环境变量。linux则下载protoc-3.18.0-linux-x86_64.zip

这里我们以linux为例，从 https://github.com/protocolbuffers/protobuf/releases下载protoc-3.18.0-linux-x86_64.zip并解压文件。

❝ unzip protoc-3.18.0-linux-x86_64.zip -d protoc-3.18.0-linux-x86_64

配置环境变量
更新 PATH 系统变量，或者确保 protoc 放在了 PATH 包含的目录中了。

新增环境变量
$ vim /etc/profile 
增加以下内容
#记得改成自己的路径
export PATH=$PATH:/data/jbchen5/src/protoc-3.18.0-linux-x86_64/bin
使其生效
$ source /etc/profile
查看是否成功
$ protoc --version
libprotoc 3.18.0
安装Go Plugins
除了安装 protoc 之外还需要安装各个语言对应的编译插件，这里我们用的Go 语言，所以还需要安装一个 Go 语言的编译插件。

git clone https://github.com/golang/protobuf.git  $GOPATH/src/github.com/golang/protobuf
cd $GOPATH/src/
go install github.com/golang/protobuf/protoc-gen-go/
❝ **编译器插件 protoc-gen-go 将安装在默认位于
下
面
，
请
务
必
将
GOPATH/bin/protoc-gen-go拷贝到/usr/bin下面，或者添加
真
实
路
径
到
PATH中(
可
能
配
置
多
个
，
请
指
定
一
个
GOPATH)。
DEMO
gRPC主要有4种请求和响应模式，分别是简单模式(Simple RPC)、服务端流式（Server-side streaming RPC）、客户端流式（Client-side streaming RPC）、和双向流式（Bidirectional streaming RPC）。

简单模式(Simple RPC)：客户端发起请求并等待服务端响应。
服务端流式（Server-side streaming RPC）：客户端发送请求到服务器，拿到一个流去读取返回的消息序列。 客户端读取返回的流，直到里面没有任何消息。
客户端流式（Client-side streaming RPC）：与服务端数据流模式相反，这次是客户端源源不断的向服务端发送数据流，而在发送结束后，由服务端返回一个响应。
双向流式（Bidirectional streaming RPC）：双方使用读写流去发送一个消息序列，两个流独立操作，双方可以同时发送和同时接收。
本节先介绍简单模式。

本demo项目结构如下:

helloworld/
├── client.go - 客户端代码
├── go.mod  - go模块配置文件
├── proto     - 协议目录
│   ├── helloworld.pb.go - rpc协议go版本代码
│   └── helloworld.proto - rpc协议文件
└── server.go  - rpc服务端代码
初始化命令如下：

# 创建项目目录
mkdir helloworld
# 切换到项目目录
cd helloworld
# 创建RPC协议目录
mkdir proto
# 初始化go模块配置，用来管理第三方依赖
go mod init 
定义服务
其实就是通过protobuf语法定义语言平台无关的接口。 文件: helloworld/proto/helloworld.proto

syntax = "proto3";

//option go_package = "path;name";
//path 表示生成的go文件的存放地址，会自动生成目录的。
//name 表示生成的go文件所属的包名
option go_package="./;proto";
// 定义包名
package proto;

// 定义Greeter服务
service Greeter {
  // 定义SayHello方法，接受HelloRequest消息， 并返回HelloReply消息
  rpc SayHello (HelloRequest) returns (HelloReply) {}
}

// 定义HelloRequest消息
message HelloRequest {
  // name字段
  string name = 1;
}

// 定义HelloReply消息
message HelloReply {
  // message字段
  string message = 1;
}
编译命令
$ protoc --proto_path=IMPORT_PATH  --go_out=OUT_DIR  --go_opt=paths=source_relative path/to/file.proto
这里简单介绍一下 golang 的编译姿势:

proto_path或者-I ：指定 import 路径，可以指定多个参数，编译时按顺序查找，不指定时默认查找当前目录。
proto 文件中也可以引入其他 .proto 文件，这里主要用于指定被引入文件的位置。

go_out：golang编译支持，指定输出文件路径
go_opt：指定参数，比如--go_opt=paths=source_relative就是表明生成文件输出使用相对路径。
path/to/file.proto ：被编译的 .proto 文件放在最后面
上面通过proto定义的接口，没法直接在代码中使用，因此需要通过protoc编译器，将proto协议文件，编译成go语言代码。 在我们的demo中,按如下命令进行编译:

# 切换到helloworld项目根目录，执行命令
$ protoc -I proto/ --go_out=plugins=grpc:proto proto/helloworld.proto
protoc命令参数说明:

-I 指定代码输出目录，忽略服务定义的包名，否则会根据包名创建目录
--go_out 指定代码输出目录，格式：--go_out=plugins=grpc:目录名
命令最后面的参数是proto协议文件 编译成功后在proto目录生成了helloworld.pb.go文件，里面包含了，我们的服务和接口定义。
实现服务端代码
文件:helloworld/server.go

package main

import (
 "log"
 "net"

 "golang.org/x/net/context"
 // 导入grpc包
 "google.golang.org/grpc"
 // 导入刚才我们生成的代码所在的proto包。
  pb "helloworld/proto"
 "google.golang.org/grpc/reflection"
)


// 定义server，用来实现proto文件，里面实现的Greeter服务里面的接口
type server struct{}

// 实现SayHello接口
// 第一个参数是上下文参数，所有接口默认都要必填
// 第二个参数是我们定义的HelloRequest消息
// 返回值是我们定义的HelloReply消息，error返回值也是必须的。
func (s *server) SayHello(ctx context.Context, in *pb.HelloRequest) (*pb.HelloReply, error) {
 // 创建一个HelloReply消息，设置Message字段，然后直接返回。
 return &pb.HelloReply{Message: "Hello " + in.Name}, nil
}

func main() {
 // 监听127.0.0.1:50051地址
 lis, err := net.Listen("tcp", "127.0.0.1:50051")
 if err != nil {
  log.Fatalf("failed to listen: %v", err)
 }

 // 实例化grpc服务端
 s := grpc.NewServer()

        // 注册Greeter服务
 pb.RegisterGreeterServer(s, &server{})

 // 往grpc服务端注册反射服务
 reflection.Register(s)

        // 启动grpc服务
 if err := s.Serve(lis); err != nil {
     log.Fatalf("failed to serve: %v", err)
 }
}
运行:

# 切换到项目根目录，运行命令
go run server.go
客户端代码
文件：helloworld/client.go

package main

import (
 "log"
 "os"
 "time"

 "golang.org/x/net/context"
 // 导入grpc包
 "google.golang.org/grpc"
 // 导入刚才我们生成的代码所在的proto包。
  pb "helloworld/proto"
)

const (
 defaultName = "world"
)

func main() {
 // 连接grpc服务器
 conn, err := grpc.Dial("localhost:50051", grpc.WithInsecure())
 if err != nil {
  log.Fatalf("did not connect: %v", err)
 }
 // 延迟关闭连接
 defer conn.Close()

 // 初始化Greeter服务客户端
 c := pb.NewGreeterClient(conn)

 // 初始化上下文，设置请求超时时间为1秒
 ctx, cancel := context.WithTimeout(context.Background(), time.Second)
 // 延迟关闭请求会话
 defer cancel()

 // 调用SayHello接口，发送一条消息
 r, err := c.SayHello(ctx, &pb.HelloRequest{Name: "world"})
 if err != nil {
  log.Fatalf("could not greet: %v", err)
 }

 // 打印服务的返回的消息
 log.Printf("Greeting: %s", r.Message)
}
运行:

# 切换到项目根目录，运行命令
go run client.go
服务端流式RPC
上面的DEMO介绍了简单模式RPC，当数据量大或者需要不断传输数据时候，我们应该使用流式RPC，它允许我们边处理边传输数据。本节先介绍服务端流式RPC。

服务端流式RPC：客户端发送请求到服务器，拿到一个流去读取返回的消息序列。 客户端读取返回的流，直到里面没有任何消息。

情景模拟：实时获取股票走势。

客户端要获取某原油股的实时走势，客户端发送一个请求
服务端实时返回该股票的走势
新建proto文件
新建server_stream.proto文件

// 定义发送请求信息
message SimpleRequest{
    // 定义发送的参数，采用驼峰命名方式，小写加下划线，如：student_name
    // 请求参数
    string data = 1;
}

// 定义流式响应信息
message StreamResponse{
    // 流式响应数据
    string stream_value = 1;
}

//服务端流式rpc，只要在响应数据前添加stream即可
// 定义我们的服务（可定义多个服务,每个服务可定义多个接口）
service StreamServer{
    // 服务端流式rpc，在响应数据前添加stream
    rpc ListValue(SimpleRequest)returns(stream StreamResponse){};
}
编译参考demo部分的编译命令

创建server端
定义我们的服务，并实现ListValue方法
// SimpleService 定义我们的服务
type StreamService struct{}
// ListValue 实现ListValue方法
func (s *StreamService) ListValue(req *pb.SimpleRequest, srv pb.StreamServer_ListValueServer) error {
 for n := 0; n < 5; n++ {
  // 向流中发送消息， 默认每次send送消息最大长度为`math.MaxInt32`bytes
  err := srv.Send(&pb.StreamResponse{
   StreamValue: req.Data + strconv.Itoa(n),
  })
  if err != nil {
   return err
  }
 }
 return nil
}
启动gRPC服务器
const (
 // Address 监听地址
 Address string = ":8000"
 // Network 网络通信协议
 Network string = "tcp"
)

func main() {
 // 监听本地端口
 listener, err := net.Listen(Network, Address)
 if err != nil {
  log.Fatalf("net.Listen err: %v", err)
 }
 log.Println(Address + " net.Listing...")
 // 新建gRPC服务器实例
 // 默认单次接收最大消息长度为`1024*1024*4`bytes(4M)，单次发送消息最大长度为`math.MaxInt32`bytes
 // grpcServer := grpc.NewServer(grpc.MaxRecvMsgSize(1024*1024*4), grpc.MaxSendMsgSize(math.MaxInt32))
 grpcServer := grpc.NewServer()
 // 在gRPC服务器注册我们的服务
 pb.RegisterStreamServerServer(grpcServer, &StreamService{})

 //用服务器 Serve() 方法以及我们的端口信息区实现阻塞等待，直到进程被杀死或者 Stop() 被调用
 err = grpcServer.Serve(listener)
 if err != nil {
  log.Fatalf("grpcServer.Serve err: %v", err)
 }
}
运行
go run server.go
:8000 net.Listing...
创建client端
创建调用服务端ListValue方法
// listValue 调用服务端的ListValue方法
func listValue() {
 // 创建发送结构体
 req := pb.SimpleRequest{
  Data: "stream server grpc ",
 }
 // 调用我们的服务(ListValue方法)
 stream, err := grpcClient.ListValue(context.Background(), &req)
 if err != nil {
  log.Fatalf("Call ListStr err: %v", err)
 }
 for {
  //Recv() 方法接收服务端消息，默认每次Recv()最大消息长度为`1024*1024*4`bytes(4M)
  res, err := stream.Recv()
  // 判断消息流是否已经结束
  if err == io.EOF {
   break
  }
  if err != nil {
   log.Fatalf("ListStr get stream err: %v", err)
  }
  // 打印返回值
  log.Println(res.StreamValue)
 }
}
启动gRPC客户端
// Address 连接地址
const Address string = ":8000"

var grpcClient pb.StreamServerClient

func main() {
 // 连接服务器
 conn, err := grpc.Dial(Address, grpc.WithInsecure())
 if err != nil {
  log.Fatalf("net.Connect err: %v", err)
 }
 defer conn.Close()

 // 建立gRPC连接
 grpcClient = pb.NewStreamServerClient(conn)
 route()
 listValue()
}
运行客户端
go run client.go
stream server grpc 0
stream server grpc 1
stream server grpc 2
stream server grpc 3
stream server grpc 4
❝ 客户端不断从服务端获取数据
客户端流式RPC
上一节介绍了服务端流式RPC，客户端发送请求到服务器，拿到一个流去读取返回的消息序列。 客户端读取返回的流的数据。本节将介绍客户端流式RPC。

客户端流式RPC：与服务端流式RPC相反，客户端不断的向服务端发送数据流，而在发送结束后，由服务端返回一个响应。

情景模拟：客户端大量数据上传到服务端。

新建proto文件
新建client_stream.proto文件

// 定义流式请求信息
message StreamRequest{
    //流式请求参数
    string stream_data = 1;
}

// 定义响应信息
message SimpleResponse{
    //响应码
    int32 code = 1;
    //响应值
    string value = 2;
}


//客户端流式rpc，只要在请求的参数前添加stream即可
service StreamClient{
    // 客户端流式rpc，在请求的参数前添加stream
    rpc RouteList (stream StreamRequest) returns (SimpleResponse){};
}
参照demo进行编译。

创建Server端
定义我们的服务，并实现RouteList方法
// SimpleService 定义我们的服务
type SimpleService struct{}
// RouteList 实现RouteList方法
func (s *SimpleService) RouteList(srv pb.StreamClient_RouteListServer) error {
 for {
  //从流中获取消息
  res, err := srv.Recv()
  if err == io.EOF {
   //发送结果，并关闭
   return srv.SendAndClose(&pb.SimpleResponse{Value: "ok"})
  }
  if err != nil {
   return err
  }
  log.Println(res.StreamData)
 }
}
启动gRPC服务器
const (
 // Address 监听地址
 Address string = ":8000"
 // Network 网络通信协议
 Network string = "tcp"
)

func main() {
 // 监听本地端口
 listener, err := net.Listen(Network, Address)
 if err != nil {
  log.Fatalf("net.Listen err: %v", err)
 }
 log.Println(Address + " net.Listing...")
 // 新建gRPC服务器实例
 grpcServer := grpc.NewServer()
 // 在gRPC服务器注册我们的服务
 pb.RegisterStreamClientServer(grpcServer, &SimpleService{})

 //用服务器 Serve() 方法以及我们的端口信息区实现阻塞等待，直到进程被杀死或者 Stop() 被调用
 err = grpcServer.Serve(listener)
 if err != nil {
  log.Fatalf("grpcServer.Serve err: %v", err)
 }
}
运行服务端
go run server.go
:8000 net.Listing...
创建客户端
创建调用服务端RouteList方法
// routeList 调用服务端RouteList方法
func routeList() {
 //调用服务端RouteList方法，获流
 stream, err := streamClient.RouteList(context.Background())
 if err != nil {
  log.Fatalf("Upload list err: %v", err)
 }
 for n := 0; n < 5; n++ {
  //向流中发送消息
  err := stream.Send(&pb.StreamRequest{StreamData: "stream client rpc " + strconv.Itoa(n)})
  if err != nil {
   log.Fatalf("stream request err: %v", err)
  }
 }
 //关闭流并获取返回的消息
 res, err := stream.CloseAndRecv()
 if err != nil {
  log.Fatalf("RouteList get response err: %v", err)
 }
 log.Println(res)
}
启动gRPC客户端
// Address 连接地址
const Address string = ":8000"

var streamClient pb.StreamClientClient

func main() {
 // 连接服务器
 conn, err := grpc.Dial(Address, grpc.WithInsecure())
 if err != nil {
  log.Fatalf("net.Connect err: %v", err)
 }
 defer conn.Close()

 // 建立gRPC连接
 streamClient = pb.NewStreamClientClient(conn)
 routeList()
}
运行客户端
go run client.go
code:200 value:"hello grpc"
value:"ok"
服务端不断从客户端获取到数据
stream client rpc 0
stream client rpc 1
stream client rpc 2
stream client rpc 3
stream client rpc 4
双向流式RPC
上一节介绍了客户端流式RPC，客户端不断的向服务端发送数据流，在发送结束或流关闭后，由服务端返回一个响应。本节将介绍双向流式RPC。

双向流式RPC：客户端和服务端双方使用读写流去发送一个消息序列，两个流独立操作，双方可以同时发送和同时接收。

情景模拟：双方对话（可以一问一答、一问多答、多问一答，形式灵活）。

新建proto文件
新建both_stream.proto文件

// 定义流式请求信息
message StreamRequest{
    //流请求参数
    string question = 1;
}

// 定义流式响应信息
message StreamResponse{
    //流响应数据
    string answer = 1;
}


//双向流式rpc，只要在请求的参数前和响应参数前都添加stream即可
service Stream{
    // 双向流式rpc，同时在请求参数前和响应参数前加上stream
    rpc Conversations(stream StreamRequest) returns(stream StreamResponse){};
}
编译参照demo部分编译即可。

创建Server端
定义我们的服务，并实现RouteList方法 这里简单实现对话中一问一答的形式
// StreamService 定义我们的服务
type StreamService struct{}
// Conversations 实现Conversations方法
func (s *StreamService) Conversations(srv pb.Stream_ConversationsServer) error {
 n := 1
 for {
  req, err := srv.Recv()
  if err == io.EOF {
   return nil
  }
  if err != nil {
   return err
  }
  err = srv.Send(&pb.StreamResponse{
   Answer: "from stream server answer: the " + strconv.Itoa(n) + " question is " + req.Question,
  })
  if err != nil {
   return err
  }
  n++
  log.Printf("from stream client question: %s", req.Question)
 }
}
启动gRPC服务器
const (
 // Address 监听地址
 Address string = ":8000"
 // Network 网络通信协议
 Network string = "tcp"
)

func main() {
 // 监听本地端口
 listener, err := net.Listen(Network, Address)
 if err != nil {
  log.Fatalf("net.Listen err: %v", err)
 }
 log.Println(Address + " net.Listing...")
 // 新建gRPC服务器实例
 grpcServer := grpc.NewServer()
 // 在gRPC服务器注册我们的服务
 pb.RegisterStreamServer(grpcServer, &StreamService{})

 //用服务器 Serve() 方法以及我们的端口信息区实现阻塞等待，直到进程被杀死或者 Stop() 被调用
 err = grpcServer.Serve(listener)
 if err != nil {
  log.Fatalf("grpcServer.Serve err: %v", err)
 }
}
运行服务端
go run server.go
:8000 net.Listing...
创建Client端
创建调用服务端Conversations方法
// conversations 调用服务端的Conversations方法
func conversations() {
 //调用服务端的Conversations方法，获取流
 stream, err := streamClient.Conversations(context.Background())
 if err != nil {
  log.Fatalf("get conversations stream err: %v", err)
 }
 for n := 0; n < 5; n++ {
  err := stream.Send(&pb.StreamRequest{Question: "stream client rpc " + strconv.Itoa(n)})
  if err != nil {
   log.Fatalf("stream request err: %v", err)
  }
  res, err := stream.Recv()
  if err == io.EOF {
   break
  }
  if err != nil {
   log.Fatalf("Conversations get stream err: %v", err)
  }
  // 打印返回值
  log.Println(res.Answer)
 }
 //最后关闭流
 err = stream.CloseSend()
 if err != nil {
  log.Fatalf("Conversations close stream err: %v", err)
 }
}
启动gRPC客户端
// Address 连接地址
const Address string = ":8000"

var streamClient pb.StreamClient

func main() {
 // 连接服务器
 conn, err := grpc.Dial(Address, grpc.WithInsecure())
 if err != nil {
  log.Fatalf("net.Connect err: %v", err)
 }
 defer conn.Close()

 // 建立gRPC连接
 streamClient = pb.NewStreamClient(conn)
 conversations()
}
运行客户端，获取到服务端的应答
go run client.go
from stream server answer: the 1 question is stream client rpc 0
from stream server answer: the 2 question is stream client rpc 1
from stream server answer: the 3 question is stream client rpc 2
from stream server answer: the 4 question is stream client rpc 3
from stream server answer: the 5 question is stream client rpc 4
服务端获取到来自客户端的提问
from stream client question: stream client rpc 0
from stream client question: stream client rpc 1
from stream client question: stream client rpc 2
from stream client question: stream client rpc 3
from stream client question: stream client rpc 4
发布于 2021-09-17 11:26
RPC 框架
gRPC
Go 语言
​赞同 36​
​11 条评论
​分享
​喜欢
​收藏
​申请转载
​
写下你的评论...

11 条评论
默认
最新
Joe
Joe
## protoc buffer写法需要注意，文件里的go_package需要带'/'，否则protoc-gen-go会报错；比如：
option go_package = "./;proto";

2023-03-16
​回复
​2
真的喜欢吃西瓜的
真的喜欢吃西瓜的
感谢！

2023-09-22
​回复
​喜欢
Joe
Joe
参考：github.com/techschool/p

2023-03-16
​回复
​喜欢
爱骑车的码农
爱骑车的码农
写的很好，学到了

2024-07-07
​回复
​喜欢
pcc
pcc
小写加下划线的命名方式是蛇形命名不是驼峰

2023-01-03
​回复
​喜欢
tower
tower
总是出现这些错，楼主有遇到吗

/usr/local/go/src/generated/hello_grpc.pb.go:26:36: undefined: HelloRequest
/usr/local/go/src/generated/hello_grpc.pb.go:26:77: undefined: HelloReply
/usr/local/go/src/generated/hello_grpc.pb.go:37:59: undefined: HelloRequest
/usr/local/go/src/generated/hello_grpc.pb.go:37:100: undefined: HelloReply
/usr/local/go/src/generated/hello_grpc.pb.go:38:13: undefined: HelloReply
/usr/local/go/src/generated/hello_grpc.pb.go:51:29: undefined: HelloRequest
/usr/local/go/src/generated/hello_grpc.pb.go:51:45: undefined: HelloReply
/usr/local/go/src/generated/hello_grpc.pb.go:59:62: undefined: HelloRequest
/usr/local/go/src/generated/hello_grpc.pb.go:59:78: undefined: HelloReply
/usr/local/go/src/generated/hello_grpc.pb.go:76:12: undefined: HelloRequest
/usr/local/go/src/generated/hello_grpc.pb.go:76:12: too many errors

2022-12-06
​回复
​喜欢
木呆呆的小烧杯
木呆呆的小烧杯
试了下，简单模式可以通过，双向流式就报get conversations stream err错误
2021-11-23
​回复
​喜欢
木呆呆的小烧杯
木呆呆的小烧杯
通过了，因为proxy代理的问题，unset就可以解决
2021-11-23
​回复
​1
真的喜欢吃西瓜的
真的喜欢吃西瓜的
木呆呆的小烧杯
感谢！

2023-09-22
​回复
​喜欢
泰宝
泰宝
route() 是那里定义的方法? [发呆]

2022-10-15
​回复
​喜欢
氟替卡松
氟替卡松
一个文件夹下多个package啊?
```

### https://github.com/goxuetang/ 

```text
go mod 安装依赖 unkown revision问题解决
发布于 2020-03-20 12:10:52
30.6K00
代码可运行
举报
文章被收录于专栏：
不想当开发的产品不是好测试
关联问题
换一批
Go mod安装依赖unknown revision是什么错误？
如何解决Go mod安装依赖时的unknown revision问题？
Go mod依赖管理中unknown revision错误的原因是什么？
背景

公司一个golang的项目，使用到了公司的私有仓库，去执行go mod tidy（下载依赖）的时候，到download公司私有库的时候就报错，报错信息也不明显，只是提示找不到影响版本unkown revision

小知识
go mod
golang用来管理用来的，类似java的maven（但肯定没有maven这么好用）
go mod tidy ，下载更新依赖
go install这种下载依赖的方式其实是通过go get的方式去下载的
go insall -x 加上-x命令，可以查看更多的错误信息

golang环境配置
linux

代码语言：javascript代码运行次数：0
运行
AI代码解释
安装go >= 1.12.x
vi /etc/profile
export GOROOT=/usr/local/go (go语言安装路径)
export GOPATH=/data/go (go业务代码环境路径)
export GO111MODULE=on (启用Mod依赖)
export GOPORT=8082 (启动端口，默认8080)
export GIN_MODE=debug (gin环境模式，线上用release)
export GOPRIVATE=gitlab.XXXX.com/XXX/* (公司私有私有库)
source /etc/profile
可惜我的环境是windows上的，索性也折腾了一下

代码语言：javascript代码运行次数：0
运行
AI代码解释
使用的是windows的powershell
ls env:  显示环境变量
$env:GOPRIVATE="gitlab.XXXX.com/XXX/*"   设置环境变量


当然你可以直接通过图形化界面去配置，原理一致

git 配置
git的默认配置在当前用户目录的.gitconfig文件中，不管是windows还是linux

代码语言：javascript代码运行次数：0
运行
AI代码解释
git config -l 查看git的所有配置
git config --global user.name="jwen" 设置global配置
踩坑
golang版本问题
电脑上本来使用的是go1.12，看代码里面给出的说是大于1.12就行了的，但知道golang的1.12和1.13版本有比较大区别，因为我就强行升级了一波，直接来到了golang1.14版本
在1.12版本的时候，要手动打开GO111MODULE ，就是导入这个环境变量GO111MODULE=on
而在1.13版本的话，就跟根据目录是否有go.mod文件自动打开的
所以建议升级就golang版本到1.13以上版本
**
 

git版本问题
这个是万万没想到的，说是go mod调用链中会用到一些git指令，当git版本比较旧时，调用失败产生错误，并给出歧义的提示信息，提示unknown revision
解决方法：更新git
参考文档：https://blog.csdn.net/test1280/article/details/87868613

gitlab请求问题
公司的gitlab仓库请求的是http请求，而不是https请求，这个需要根据公司实际情况来看

解决办法
确认golang，git版本，建议升级到最新版本；
新增golang的环境变量，新增GOPRIVATE，value配置为自己需求的私有仓库，让golang可以下载私有库的东西
代码语言：javascript代码运行次数：0
运行
AI代码解释
建议直接golang设置
golang设置
go env -w GOPRIVATE="gitlab.xxx.com/xxxx/*"

linux配置
export GOPRIVATE=gitlab.xxx.com/xxxx/*

windows配置
$env:GOPRIVATE="gitlab.XXXX.com/XXX/*" 
修改git配置，因为go install/mod tidy 去下载依赖其实是通过git命令去下载的，而且默认是http协议去下载的，建议是修改为ssh协议去获取
方法一：通过修改文件方式，去到当前用户目录修改.gitconfig文件，新增如下，注意私有库是http还是https

代码语言：javascript代码运行次数：0
运行
AI代码解释
[url "git@gitlab.xxxx.com:"]
    insteadOf = http://gitlab.xxxxx.com/
方法二：通过命令行形式，直接执行命令如下：

代码语言：javascript代码运行次数：0
运行
AI代码解释
git config --global url."git@gitlab.xxxx.com:".insteadOf "http://gitlab.xxxx.com/"
然后执行go install 或者 go mod tidy确认是否可以正常下载依赖
参考文档
https://zhuanlan.zhihu.com/p/65916369
https://blog.csdn.net/test1280/article/details/87868613
https://blog.csdn.net/weixin_41571449/article/details/78966862
https://blog.csdn.net/u011078141/article/details/95217973


```