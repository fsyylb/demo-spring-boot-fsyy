```text
go get报错解决办法：go.mod at revision v0.0.0: unknown revision v0.0.0

超级西红柿

于 2024-05-26 14:10:35 发布

阅读量869
 收藏 1

点赞数 11
文章标签： golang 开发语言 后端
版权

GitCode 开源社区
文章已被社区收录
加入社区
go get报错解决办法：go.mod at revision v0.0.0: unknown revision v0.0.0
文章目录
go get报错解决办法：go.mod at revision v0.0.0: unknown revision v0.0.0
一、现象描述
二、问题排查
1、cache
2、GOPROXY
一、现象描述
使用go get给项目添加依赖的时候，报错出现go.mod at revision v0.0.0: unknown revision v0.0.0

二、问题排查
看了一下go.mod，里边确实有个依赖声明的版本是v0.0.0，查阅后得知这样声明没有问题，所以排除项目本身的问题，将问题定位到go库或者go env配置的问题。最后排除了cache的问题，发现是GOPROXXY环境变量设置导致的。

1、cache
在使用go get添加其他的依赖比如gin官方的依赖，仍然报告上一个v0.0.0的依赖的错误，所以怀疑是cache的问题。在网上查到以下解决方案:

删除文件夹 ($GOPATH)/pkg/mod/cache
执行 go mod why
1
2
($GOPATH)/pkg/mod/cache这个目录存储了Go模块下载的源代码缓存，删除后，Go在下次需要依赖时会重新下载。这样可以解决缓存的代码文件损坏或不完整、依赖的版本标签在仓库中被移除、网络问题比如代理设置或防火墙规则变化这三个问题。

go mod why命令用于显示一个模块为何是项目中的依赖。Go会重新解析整个模块依赖树，对缺失或过期模块重新下载，如果在解析过程中发现依赖不完整或有误，会尝试修复这些问题。

执行后发现问题依然存在，但解决了执行go get时会报别的依赖的错误的问题。

2、GOPROXY
这才是本次问题的原因所在，GOPROXY设置失败导致go的解析出现问题。应该配置为：

go env -w GOPROXY=https://goproxy.cn
1
AI写代码
再次执行go run main.go，问题解决。
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/qq_43271717/article/details/139214132
```


```text
手把手教你如何创建及使用Go module
2022-03-30
4,414
阅读12分钟
专栏： 
Go实战

Go module是从Go 1.11版本才引入的新功能。其目标是取代旧的的基于GOPATH方法来指定在工程中使用哪些源文件或导入包。本文首先分析Go引入module之前管理依赖的优缺点，然后针对这些缺点，看module是如何解决的。

一、传统的包管理方式-package

在Go1.11之前，如果想要编写Go代码以及引入第三方包，则需要将源代码写在GOPATH/src目录下。即开发者只能将研发的项目放到GOPATH目录下。同时，将引入的第三方包会下载到GOPATH/pkg目录下**。**我们先来看下在这种包管理模式下，使用go get是如何安装依赖包的，然后再分析这种包管理的不足。

1.1 go get的工作流程

我们以在项目中引入github.com/go-redis/redis包为例。在项目中使用import导入该包：

import "github.com/go-redis/redis"
然后我们需要使用go get命令将该包下载下来：

go get github.com/go-redis/redis
运行go get命令后，Go会访问 github.com/go-redis/re… 并下载该包。一旦下载完成，该包就会被保存到 $GOPATH/pkg/github.com/go-redis/redis 目录下。

那么从执行go get命令到包被保存到对应的目录期间，go get都经历了哪些过程呢？

首先，Go会将包拼接成https协议的URL地址。这里是 github.com/go-redis/re… 。 Go的第三方包是存储在像GIT或SVN这样的在线版本控制管理系统上的。Go目前支持的在线版本管理类型如下：

Bazaar        .bzr
Fossil        .fossil
Git           .git
Mercurial     .hg
Subversion    .svn
所以，在示例中，Go首先会解析github.com/go-redis/redis.git （模板格式：github.com/go-redis/redis{.type}）。

其次，根据支持的协议依次尝试clone该包。若该在线版本管理系统支持多种协议，那么Go会依次尝试。例如，Git支持 https:// 和 git+ssh:// 协议 ， 那么Go会依次使用对应的协议进行解析该包。如果Go成功解析了对应的URL地址，那么该包将会被clone并保存到$GOPATH/pkg目录下。

最后，若版本管理系统不是Go所支持的，则尝试查找META信息。在这种场景下，Go也会试图使用https或http协议拼装成的URL地址去解析。并从返回的HTML代码中查找META信息：

<meta name="go-import" content="import-prefix type repo-root">
import-prefix: 这是模块所导入的路径。在我们的示例中是github.com/go-redis/go
**type：**在线版本管理系统的类型。可以是上面我们提到的Go支持的类型之一。在我们的示例中是git。
repo-root： 代码仓库在版本控制系统中的根URL地址。例如，在我们的示例中，应该是 github.com/go-redis/re…
根据读取到的meta信息，Go就可以从 github.com/go-redis/re… 中克隆该项目代码，并将其保存到本地的$GOPATH/src目录下的github.com/go-redis/redis中。

01-git-import-redis.png

到此，我们已经了解了传统的包管理的工作方式了。下面我们来看看这种管理方式有哪些缺点。

1.2 传统包管理方式的不足

首先，所有的项目都必须在GOPATH/src指向的目录下，或者必须更改GOPATH环境变量所指向的目录。

我们以两个项目A、B来举例说明。假设当前的GOPATH=/usr/local/goworkspace/。如果保持GOPATH不变的话，那么A、B两个项目的源代码都必须要放到GOPATH的目录下，即/usr/local/goworkspace/src目录下。同时，A和B项目引入的第三方包都会在GOPATH/pkg目录下。这样两个项目其实就是混合在一起。

如果不想混合在一起怎么办呢？那就只能更改GOPATH的目录。假设我们现在在研发A项目，并将其工作目录放在/usr/local/goworkspace/a目录下，GOPATH=/usr/local/goworkspace/a。但是在开发B项目时，更改GOPATH的指向，例如我们这里使用/usr/local/goworkspace/b目录下。这样两个项目的源代码以及依赖的第三方包就在各自项目下了。 但同时如果想继续修改A项目的代码时，就需要再将GOPATH目录更改到指向A项目的目录中，即GOPATH=/usr/local/goworkspace/src目录。

其次，对于依赖的同一个包只能从master分支上导入最新的提交，且不能导入包的指定的版本。

假设我们有一个第三方包redis，项目A首次引入该包时，使用go get命令从代码库的master分支下载当前最新的代码，并将该包保存在本地的GOPATH/pkg目录下。之后redis包有了新的提交，但同时也引入了一个bug。如果项目A升级或重新安装该包时，使用go get命令并没有指定特定版本的参数，还是从该包的代码库的master分支中下载该包，也就造成了向后不兼容。另外，升级或重新安装的包也会被安装到GOPATH/pkg下的相同目录，因为没有版本的管理，所以会覆盖之前。

好了，以上就是在传统的包管理方式中的两大主要不足之处。那么针对这些不足，我们来看看Go的module是如何解决的。

二、现代包管理方式-module

2.1 什么是module

一个module就是一个包含多个package的目录，即一个package的集合。 其要实现的目标如下：

首先，研发者应该能够在任何目录下工作，而不仅仅是在GOPATH指定的目录。
可以安装依赖包的指定版本，而不是只能从master分支安装最新的版本。
可以导入同一个依赖包的多个版本。当我们老项目使用老版本，新项目使用新版本时会非常有用。
要有一个能够罗列当前项目所依赖包的列表。这个的好处是当我们发布项目时不用同时发布所依赖的包。Go能够根据该文件自动下载对应的包。
一个module也是可以像package一样共享的。因此，module也必须是一个git仓库或其他Go可支持的代码控制系统。因此，Go的建议是：

一个module必须是一个代码控制系统的仓库，并且一个仓库应该只能包含一个module。
一个module应该包含一个或多个package。
一个包应该在同一个目录下包含一个或多个go文件
2.2 如何创建module

第一，我们在GOPATH之外的任何位置创建一个目录。

这里我们使用encodex，该encodex包含一些对字符串的编码功能函数，例如md5，sha1等。如下图：

02-创建module示例-01.png

根据上面所讨论的，一个Go module应该是一个版本控制系统上的代码仓库。所以我们在github上创建一个git的代码仓库，如下图：

03-创建module示例-02-git的创建.png

第二，在本地的目录下执行go mod init 命令来初始化Go module。

go mod init github.com/goxuetang/encodex
该命令会在encodex的根目录下创建go.mod文件，go.mod文件会包含我们定义的module的导入路径和依赖的包及对应的版本。如下所示：

04-显示go-mod-init.png

由上图可知，在生成的go.mod文件中显示了该module可被导入的路径以及Go的版本。因为目前还没有导入任何其他依赖包，所以没有显示导入包的信息。好，现在我们把该目录同时提交到git上。

git init

git remote add origin https://github.com/goxuetang/encodex.git
第三，我们在encodex的hash包中添加如下代码：

05-hash包截图.png

好了，到这里我们就可以发布我们的包。但在发布之前我们先来看下语义化的版本。

语义化的版本是一种通用的版本格式。其格式如下：

vMajor.Minor.Patch
该格式以固定的字母 v 开头，Major代表主版本，Minor代表次版本，Patch代表不定版本。只有在版本不兼容之前的版本时，才会改动主版本Major。当做了向下兼容的功能时会改动Minor。当对次版本Minor做了问题修正时会改动Patch。详细的语义化版本可参考语义化版本官方文档进一步阅读。

Go语言指出，当一个module的新老版本不兼容时，新版本应该发布一个新的主版本。同时，Go会认为这是一个独立的module，和之前的老版本没有任何关系。

Git的分支本质上是一个历史提交的记录。对于每一次提交都有一个唯一的标识对应。对于每一个唯一标识，我们还可以给一个语义化的版本别名，也就是我们所说的tag。

最后，我们可以给我们的module打一个tag了。

因为是第一个版本，所以我们使用版本v1.0.0，如下：

git tag v1.0.0

git push --tags
到此，我们的module已经发布了，并由一个v1.0.0的tag版本。接下来，我们看看在项目中如何使用该module

2.4 如何使用第三方module

我们在新建的main module中创建了一个main.go文件，在该module下要想使用encodex模块下的包，则需要引入和安装两个步骤。在文件中使用import语句引入包，如下图： 06-main中引入encodex.png

第一步，使用import引入模块下具体的包。因为在encodex的module中，我们设置的引入路径是github.com/goxuetang/encodex， 即go.mod文件的第一行。hash包是encodex模块下的一个包。所以我们引入的完整路径是：

import "github.com/goxuetang/encodex/hash"
第二步，使用go get命令安装引入的包。使用go get命令时，可以指定包的具体版本，如下：

go get github.com/goxuetang/encodex/hash@v1.0.0
也可以不指定版本，这时go get命令会自动的查找最近的版本，如下：

go get github.com/goxuetang/encodex/hash

go get:added github.com/goxuetang/encodex v1.0.0
如图所示：

07-main中使用go-get自动获取版本.png

同时，go get会将引入的包加在go.mod文件中。require中不仅有包名，还有对应的版本号。如下图所示： 08-go-get的mod文件.png

好，我们现在来看另外一个问题，下载下来的包存在哪里了。

2.5 module存储在哪里

当go get将包下载下来后，会将其存储到GOPATH/pkg/mod目录下。通过go env可以查看GOPATH环境变量的具体指向目录，我的环境下的GOPATH=/Users/YuYang/go，如下是上节中引入的encodex模块。如下图所示：

09-module存储未知.png

我们发现encodex模块的目录是带版本号的，这也是Go module能够支持多版本的原因。

三、如何升级版本

在上面我们有讲到module使用的是vX.X.X格式的语义化版本。那么在日常的研发中又是如何对这三个版本号进行升级的呢。

3.1 如何升级module的小版本和补丁版本

随着时间的推移，发布的包肯定会有新的提交，比如修复了一个bug，则patch版本号会升级，添加了一个新功能，则小版本号会升级。做了一项大的改动，和前一个版本不兼容了，那么主版本号就会升级。接下来我们看看在已引入的包后，如何升级对应的版本。

如果我们只想升级补丁版本patch，那么可以使用如下命令：

go get -u=patch
如果想更新同一个大版本下的小版本，那么可以使用如下命令：

go get -u
该命令是如果小版本有更新，则升级小版本。如果只有补丁版本有更新，则会升级补丁版本。

如果想升级到指定的版本，则使用指定版本的命令：

go get module@version
例如，要将encodex模块升级到v1.1.3版本，则使用如下命令：

go get github.com/goxuetang/encodex@v1.1.3
3.2 如何升级module的大版本

如果想要升级大版本则需要重新安装大版本，因为在上面我们有提到，在Go中，会将一个大版本视为一个全新的模块。因此，需要使用go get安装该大版本的模块，同时在对应的文件中通过import引入该包。例如encodex模块升级到了v2版本，那么就需要在encodex模块的go.mod中将导入路径更改为v2。如下：

github.com/goxuetang/encodex/v2
然后就可以在工程中引用该v2版本的模块了。如下：

import newHash github.com/goxuetang/encodex/v2/hash
同时使用go get命令下载并安装该模块：

go get github.com/goxuetang/encodex/v2
四、间接依赖

一个工程所依赖的模块可分为直接依赖和**间接依赖。**直接依赖就是我们的工程文件中使用import语句导入的模块。而间接依赖就是我们直接依赖的模块所依赖的。如下图： 12-直接依赖和间接依赖.png

现在我们在main模块中引入github.com/go-redis/redis 模块，然后查看go.mod文件，发现有如下间接的依赖模块，这里的模块正是在github.com/go-redis/redis 中引入的模块，可以查看github.com/go-redis/redis 模块的go.mod文件以确认。

10-间接依赖.png

在上图中，我们还发现redis的模块后面的版本是 v6.15.9+incompatible。这个代表什么意思呢？这个代表的是引入的模块的最新版本是v5.15.9，但同时具有不兼容的风险。 为什么呢？因为在redis模块中未使用规范的导入名称。例如，规范的模块命名应该是在模块的版本大于1的时候，导入名称就需要增加主版本信息。例如，当该模块是第一个版本时，其对应的go.mod文件如下：

module github.com/go-redis/redis
当主版本升级到2时，则go.mod中的模块导入名称应该为：

module github.com/go-redis/redis/v2
如果不增加v2这个标识，那么当使用go get github.com/go-redis/redis 下载包的时候，go会找到模块名称没有使用主版本标识的最新的版本。我们通过查看该模块在git上的6.15.9的版本源码，发现其源码中并没有go.mod文件。

所以，当模块的go.mod文件中的导入路径没有版本后缀（例如v2）的情况下，默认是v1版本，因此在使用go get获取这样的模块时，默认会获取v1.x.x的最新版本。

五、 小版本的选择

我们已经知道了Go可以同时导入主版本不同的module。那么，如果只有小版本或补丁版本不同，那么Go该如何选择呢？ 假设工程项目直接依赖于两个module：A和B。同时A依赖于MODULE 1 的v1.0.1版本，但B依赖于MODULE 1的v1.0.2版本。如下图所示： 13-间接依赖多个版本.png

那么，在工程项目模块（PROJECT MODULE）中需要间接依赖MODULE 1的哪个版本呢？如果我们使用v1.0.1，那么MODULE B有可能会产生异常。在语义化版本中，我们知道小版本或补丁版本应该向后兼容，即v1.0.2是兼容v1.0.1的，所以在PROJECZT MODULE中应该选择MODULE 1的v1.0.2版本。

14-最终间接版本选择.png

总结

Go module不仅解决了项目代码不再依赖于GOPATH路径，而且还解决了相同module的多版本引入问题。通过本篇文章，相信您对module的创建、发布、版本管理、依赖关系都会有了一个清晰的认识。


```


```text
GO111MODULE作用

Aring88

于 2021-03-24 00:44:52 发布

阅读量7.1k
 收藏 10

点赞数 3
分类专栏： Go 文章标签： go git
版权

Go
专栏收录该内容
4 篇文章
订阅专栏
一、
再没有 GO111MODULE时， go编译程序的查找依赖的顺序 go path > goroot , 有了GO111MODULE后，会读取当前项目的go.mod.文件， 在go.mod文件中会记录有哪些依赖

Go Modules 是 Go 语言的一种依赖管理方式，该 feature 是在 Go 1.11 版本中出现的，由于最近在做的项目中，团队都开始使用 go module 来替代以前的 Godep，Kubernetes 也从 v1.15 开始采用 go module 来进行包管理，所以有必要了解一下 go module。

go module 相比于原来的 Godep，go module 在打包、编译等多个环节上有着明显的速度优势，并且能够在任意操作系统上方便的复现依赖包，更重要的是 go module 本身的设计使得自身被其他项目引用变得更加容易，这也是 Kubernetes 项目向框架化演进的又一个重要体现。

二、
使用 go module 管理依赖后会在项目根目录下生成两个文件 go.mod 和 go.sum。
go.mod 中会记录当前项目的所依赖，文件格式如下所示：
module github.com/gosoon/audit-webhook
go 1.12
require (
github.com/elastic/go-elasticsearch v0.0.0
github.com/gorilla/mux v1.7.2
github.com/gosoon/glog v0.0.0-20180521124921-a5fbfb162a81
)

1
2
3
4
5
6
7
8
go.sum记录每个依赖库的版本和哈希值，文件格式如下所示：
github.com/elastic/go-elasticsearch v0.0.0 h1:Pd5fqOuBxKxv83b0+xOAJDAkziWYwFinWnBO0y+TZaA=
github.com/elastic/go-elasticsearch v0.0.0/go.mod h1:TkBSJBuTyFdBnrNqoPc54FN0vKf5c04IdM4zuStJ7xg=
github.com/gorilla/mux v1.7.2 h1:zoNxOV7WjqXptQOVngLmcSQgXmgk4NMz1HibBchjl/I=
github.com/gorilla/mux v1.7.2/go.mod h1:1lud6UwP+6orDFRuTfBEV8e9/aOM/c4fVVCaMa2zaAs=
github.com/gosoon/glog v0.0.0-20180521124921-a5fbfb162a81 h1:JP0LU0ajeawW2xySrbhDqtSUfVWohZ505Q4LXo+hCmg=
github.com/gosoon/glog v0.0.0-20180521124921-a5fbfb162a81/go.mod h1:1e0N9vBl2wPF6qYa+JCRNIZnhxSkXkOJfD2iFw3eOfg=
1
2
3
4
5
6
三、如何启用 go module 功能
(1) go 版本 >= v1.11
(2) 设置GO111MODULE环境变量
要使用go module 首先要设置GO111MODULE=on，GO111MODULE 有三个值，off、on、auto，off 和 on 即关闭和开启，auto 则会根据当前目录下是否有 go.mod 文件来判断是否使用 modules 功能。

无论使用哪种模式，module 功能默认不在 GOPATH 目录下查找依赖文件，所以使用 modules 功能时请设置好代理。

在使用 go module 时，将 GO111MODULE 全局环境变量设置为 off，在需要使用的时候再开启，避免在已有项目中意外引入 go module。

download download modules to local cache (下载依赖的module到本地cache))
edit edit go.mod from tools or scripts (编辑go.mod文件)
graph print module requirement graph (打印模块依赖图))
init initialize new module in current directory (在当前文件夹下初始化一个新的module, 创建go.mod文件))
tidy add missing and remove unused modules (增加丢失的module，去掉未使用的module)
vendor make vendored copy of dependencies (将依赖复制到vendor下)
verify verify dependencies have expected content (校验依赖)
why explain why packages or modules are needed (解释为什么需要依赖)
1
2
3
4
5
6
7
8
四、使用 go module 功能
1， 对于新建项目使用 go module：
$ export GO111MODULE=on
$ go mod init github.com/you/hello
…
// go build 会将项目的依赖添加到 go.mod 中
$ go build
2，对于已有项目要改为使用 go module：
$ export GO111MODULE=on
// 创建一个空的 go.mod 文件
$ go mod init .

// 查找依赖并记录在 go.mod 文件中
$ go get ./…
go.mod 文件必须要提交到 git 仓库，但 go.sum 文件可以不用提交到 git 仓库(gi t忽略文件 .gitignore 中设置一下)。

五、项目的打包
首先需要使用 go mod vendor 将项目所有的依赖下载到本地 vendor 目录中然后进行编译，下面是一个参考：
go mod vendor
go build -ldflags “-s -w” -a -installsuffix cgo -o audit-webhook .
1
2
六、注意事项
1、依赖下载
go module 默认不在 GOPATH 目录下查找依赖文件，其首先会在G O P A T H / p k g / m o d 中 查 找 有 没 有 所 需 要 的 依 赖 ， 没 有 的 直 接 会 进 行 下 载 。
可 以 使 用 g o m o d d o w n l o a d 下 载 好 所 需 要 的 依 赖 ， 依 赖 默 认 会 下 载 到 GOPATH/pkg/mod中查找有没有所需要的依赖，没有的直接会进行下载。
可以使用 go mod download下载好所需要的依赖，依赖默认会下载到GOPATH/pkg/mod中查找有没有所需要的依赖，没有的直接会进行下载。
可以使用gomoddownload下载好所需要的依赖，依赖默认会下载到GOPATH/pkg/mod中，其他项目也会使用缓存的 module。
```

```text
GO111MODULE 是个啥？（转载）
2022-06-25
545
阅读2分钟
专栏： 
go

GO111MODULE 是个啥？
程序员奇点

程序员奇点

公众号:程序员奇点

12 人赞同了该文章

GO111MODULE 是啥？
GO111MODULE 是个环境变量，可以在使用 Go 或者更改 Go 导入包的方式时候设置。

要注意的是，这个变量在不同 Go 版本有不同的语义

没有包管理阶段
一开始go发布的时候是没有包管理的
go get命令会根据路径，把相应的模块获取并保存在$GOPATH/src
也没有版本的概念，master 就代表稳定的版本
首先，让我们谈谈 GOPATH。当 Go 在 2009 年首次推出时，它并没有随包管理器一起提供。取而代之的是 go get，通过使用它们的导入路径来获取所有源并将其存储在 $GOPATH/src 中。没有版本控制并且『master』分支表示该软件包的稳定版本。

Go 1.11 引入了 Go 模块。 Go Modules 不使用 GOPATH 存储每个软件包的单个 git checkout，而是存储带有 go.mod 标记版本的标记版本，并跟踪每个软件包的版本。

什么时候用 GOPATH, 什么时候用 GOMODULE ？ 这是个问题，需要通过 GO111MODULE 来解决。

Go 1.11 和 1.12 阶段
即使项目在您的 GOPATH 中，GO111MODULE = on 仍将强制使用 Go 模块。仍然需要 go.mod 才能正常工作。

GO111MODULE = off
强制 Go 表现出 GOPATH 方式，即使你的项目不在 GOPATH 目录里。

. GO111MODULE = auto 是默认模式。

在这种模式下，Go 会表现： 当项目路径在 GOPATH 目录外部时， 设置为 GO111MODULE = on 当项目路径位于 GOPATH 内部时，即使存在 go.mod, 设置为 GO111MODULE = off。

Go 1.13 阶段
在 Go 1.13 下， GO111MODULE 的默认行为 (auto) 语义变了。

当存在 go.mod 文件时或处于 GOPATH 外， 其行为均会等同于 GO111MODULE=on。相当于 Go 1.13 下你可以将所有的代码仓库均不存储在 GOPATH 下。
当项目目录处于 GOPATH 内，且没有 go.mod 文件存在时其行为会等同于 GO111MODULE=off。
Go Modules 的使用说明
使用 go get 同样会更新你的 go.mod
go get 通常它是用于提供一个安装或下载包的功能。但如果使用了 Go modules，当你在一个有着 go.mod 文件存在的仓库下使用这个命令会将你所下载或安装的包静默记录于 go.mod 文件中。

Go Modules 依赖项的存储在哪个目录？
使用 Go Modules 时，在 go build 期间使用的包存储在 $GOPATH/pkg/mod 中。

在尝试在开发工具中的import时，你可能最终使用的包是 GOPATH 中的版本，而不是编译期间使用的 pkg/mod。

当让项目固定指向一个依赖项时，可以使用 vendor 目录

解决方法 1: 使用 go mod vendor + go build -mod=vendor。这将强制 go 使用 vendor/files 而不是 $GOPATH/pkg/mod 中的一个。该选项还解决了 开发工具 不能打开包文件的正确版本的问题。
解决方法 2: 在 go.mod 末尾添加 replace 行:
use replace github.com/maelvls/beers => ../beers
欢迎关注：程序员开发者社区
参考资料
learnku.com/go/t/39086
blog.csdn.net/waltonhuang…
发布于 2021-05-22 14:26
```

## https://go.dev/doc/tutorial/getting-started
## https://go.dev/doc/modules/managing-dependencies#naming_module
## https://go.dev/doc/
## https://go.dev/learn/
## https://pkg.go.dev/std
## https://go.dev/doc/install
## https://go.dev/

```text
Spring使用event-stream进行数据推送
 更新时间：2024年03月25日 09:31:06   作者：zyydd_  
这篇文章主要介绍了Spring使用event-stream进行数据推送,前端使用EventSource方式向后台发送请求,后端接收到之后使用event-stream方式流式返回,文中有相关的代码示例供大家参考,需要的朋友可以参考下
Java技术迷

前端使用EventSource方式向后台发送请求，后端接收到之后使用event-stream方式流式返回。可以应用在时钟、逐字聊天等场景。

前端js示例代码（向后台请求数据，并展示到“id=date”的div上）

后端java示例

除了是这个方法，我们也可以使用Spring的定时器定时推送数据

定时器编写

1.在配置文件里添加相关配置

上面的代码设置的是每5秒执行一次appInfoAdd方法，cron字段表示的是时间的设置，分为：秒，分，时，日，月，周，年。

2.定时方法的编写

这个过程包括读取配置文件里的key，从数据库读取数据（根据数据的字段判断是否需要推送），使用GSON把数据拼装成上一篇文章里的data的形式data={"esealList":[{},{},{}]},使用HttpClient执行带参数的POST方法，访问POST接口，根据返回信息判断状态。

3.HttpClient里的带参POST方法

4.辅助类

以上就是Spring使用event-stream进行数据推送的详细内容，更多关于Spring event-stream数据推送的资料请关注脚本之家其它相关文章！

您可能感兴趣的文章:
SpringBoot使用异步线程池实现生产环境批量数据推送
vue使用websocket实现实时数据推送功能
python 监听salt job状态,并任务数据推送到redis中的方法
Node.js实现数据推送
JavaScript数据推送Comet技术详解

```

## https://github.com/XiaoQingGG/gpt4free-ts

```text
redission Lettuce 原创
mob649e815e9bc92024-10-14 05:15:20

文章标签Redis客户端redis文章分类Redis数据库阅读数14

现在的通义灵码不但全面支持 Qwen3，还支持配置自己的 MCP 工具，还没体验过的小伙伴，马上配置起来啦~

 https://click.aliyun.com/m/1000403618/

Redisson与Lettuce：两种优秀的Redis客户端
在现代应用程序的开发中，Redis扮演着越来越重要的角色。作为一个开源的内存数据结构存储系统，它被广泛用于缓存、消息代理、排行榜等场景。而在Java开发中，Redisson和Lettuce是两款常用的Redis客户端。本文将通过对这两者的对比、使用场景及代码示例向您介绍它们的特点。

Redisson与Lettuce的对比
1. Redisson
Redisson是一个功能全面的Redis客户端，提供了对Redis操作的高级抽象和功能，它不仅支持连接Redis，还引入了多种分布式数据结构和工具，协助开发者更方便地管理分布式系统。Redisson还提供了丰富的功能，包括分布式锁、分布式集合、限流等，适用于需要复杂数据结构和分布式功能的场合。

2. Lettuce
Lettuce是一个简单且高效的Redis客户端，其核心优势在于反应式编程和异步处理。它使用Netty作为网络传输层，支持高并发的连接，同时提供同步和异步的API，适合需要高性能和低延迟的场景。

状态图
在使用Redisson和Lettuce时，我们可以通过状态图来理解其状态变化和操作顺序。以下是一个简单的状态图：

Idle
Connecting
Connected
Working
Disconnecting
这个状态图描述了Redis客户端在不同操作下的状态变化，包括从空闲状态到连接状态，再到工作状态，最后回到空闲状态。

旅行图
在实际使用中，Redisson与Lettuce的使用流程也可以用旅行图表示：

Redis
客户端
用户
连接Redis
用户输入连接信息
客户端建立连接
执行命令
用户发起GET命令
客户端发送GET命令
Redis返回结果
断开连接
用户请求关闭连接
客户端关闭连接
Redis 客户端使用流程
代码示例
接下来，我们分别通过代码示例来演示这两种客户端的基本用法。

使用 Redisson
以下是一个使用Redisson连接Redis并执行基本操作的示例：

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonExample {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);

        // 操作Redis
        String bucket = redisson.getBucket("myBucket");
        bucket.set("Hello, Redisson!");

        // 获取值
        String value = bucket.get();
        System.out.println("Stored value: " + value);

        // 关闭连接
        redisson.shutdown();
    }
}
1.
2.
3.
4.
5.
6.
7.
8.
9.
10.
11.
12.
13.
14.
15.
16.
17.
18.
19.
20.
21.
22.
在这个示例中，我们创建了一个Redisson客户端实例，连接到了本地的Redis服务器，然后通过getBucket方法存储和获取一个值。

使用 Lettuce
接下来是Lettuce的示例代码：

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.SyncCommands;

public class LettuceExample {
    public static void main(String[] args) {
        RedisClient client = RedisClient.create("redis://127.0.0.1:6379");
        StatefulRedisConnection<String, String> connection = client.connect();
        SyncCommands<String, String> syncCommands = connection.sync();

        // 操作Redis
        syncCommands.set("myKey", "Hello, Lettuce!");
        String value = syncCommands.get("myKey");
        System.out.println("Stored value: " + value);

        // 关闭连接
        connection.close();
        client.shutdown();
    }
}
1.
2.
3.
4.
5.
6.
7.
8.
9.
10.
11.
12.
13.
14.
15.
16.
17.
18.
19.
20.
在这个示例中，我们同样连接到了本地的Redis服务器，并使用同步命令存储和获取一个键值对。

结尾
综上所述，Redisson和Lettuce都是功能强大的Redis客户端，各自有其独特的优势。Redisson更适合需要复杂功能和数据结构的应用程序，而Lettuce则适合高性能、低延迟的场景。在选择合适的Redis客户端时，开发者应根据具体需求和环境进行评估。希望本文能够帮助您更好地理解和使用这两款客户端。

现在的通义灵码不但全面支持 Qwen3，还支持配置自己的 MCP 工具，还没体验过的小伙伴，马上配置起来啦~

 https://click.aliyun.com/m/1000403618/
```

```text
Springboot + redis操作多种实现（以及Jedis,Redisson,Lettuce的区别比较）

Tonels

于 2019-10-16 20:27:07 发布

阅读量1.8w
 收藏 108

点赞数 13
分类专栏： Redis Spring 文章标签： 分布式锁 Redis Radisson集成Springboot Radisson配置 Radisson分布式锁
版权

GitCode 开源社区
文章已被社区收录
加入社区

Spring
同时被 2 个专栏收录
8 篇文章
订阅专栏

Redis
4 篇文章
订阅专栏
Springboot + redis 操作多种实现
一、Jedis,Redisson,Lettuce三者的区别
二、Jedis
三、RedisTemplate
3.1、使用配置
3.2、使用示例
3.3、扩展
3.3.1、spring-boot-starter-data-redis的依赖包
3.3.2、stringRedisTemplate API（部分展示）
3.3.3 StringRedisTemplate默认序列化机制
四、RedissonClient 操作示例
4.1 基本配置
4.1.1、Maven pom 引入
4.1.2、添加配置文件Yaml或者json格式
4.1.3、读取配置
4.2 使用示例
4.3 扩展
4.3.1 丰富的jar支持，尤其是对 Netty NIO框架
4.3.2 丰富的配置机制选择，[这里是详细的配置说明](https://github.com/redisson/redisson/wiki/2.-Configuration)
4.3.3 API支持（部分展示），具体的 Redis --> RedissonClient ,[可查看这里](https://github.com/redisson/redisson/wiki/11.-Redis-commands-mapping)
4.3.4 轻便的丰富的锁机制的实现
4.3.4.1 Lock
4.3.4.2 Fair Lock
4.3.4.3 MultiLock
4.3.4.4 RedLock
4.3.4.5 ReadWriteLock
4.3.4.6 Semaphore
4.3.4.7 PermitExpirableSemaphore
4.3.4.8 CountDownLatch
五、基于注解实现的Redis缓存
5.1 Maven 和 YML配置
5.2 使用示例
5.3 扩展
一、Jedis,Redisson,Lettuce三者的区别
共同点：都提供了基于Redis操作的Java API，只是封装程度，具体实现稍有不同。

不同点：

1.1、Jedis
是Redis的Java实现的客户端。支持基本的数据类型如：String、Hash、List、Set、Sorted Set。

特点：使用阻塞的I/O，方法调用同步，程序流需要等到socket处理完I/O才能执行，不支持异步操作。Jedis客户端实例不是线程安全的，需要通过连接池来使用Jedis。

1.1、Redisson
优点点： 分布式锁，分布式集合，可通过Redis支持延迟队列。

1.3、 Lettuce
用于线程安全同步，异步和响应使用，支持集群，Sentinel，管道和编码器。

基于Netty框架的事件驱动的通信层，其方法调用是异步的。Lettuce的API是线程安全的，所以可以操作单个Lettuce连接来完成各种操作。

二、Jedis
三、RedisTemplate
3.1、使用配置
maven配置引入，（要加上版本号，我这里是因为Parent已声明）

   <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

1
2
3
4
5
application-dev.yml

spring:
  redis:
    host: 192.168.1.140
    port: 6379
    password:
    database: 15 # 指定redis的分库（共16个0到15）
1
2
3
4
5
6
3.2、使用示例
 @Resource
 private StringRedisTemplate stringRedisTemplate;
 
    @Override
    public CustomersEntity findById(Integer id) {
        // 需要缓存
        // 所有涉及的缓存都需要删除，或者更新
        try {
            String toString = stringRedisTemplate.opsForHash().get(REDIS_CUSTOMERS_ONE, id + "").toString();
            if (toString != null) {
                return JSONUtil.toBean(toString, CustomersEntity.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 缓存为空的时候，先查,然后缓存redis
        Optional<CustomersEntity> byId = customerRepo.findById(id);
        if (byId.isPresent()) {
            CustomersEntity customersEntity = byId.get();
            try {
                stringRedisTemplate.opsForHash().put(REDIS_CUSTOMERS_ONE, id + "", JSONUtil.toJsonStr(customersEntity));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return customersEntity;
        }
        return null;
    }

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
16
17
18
19
20
21
22
23
24
25
26
27
28
3.3、扩展
3.3.1、spring-boot-starter-data-redis的依赖包


3.3.2、stringRedisTemplate API（部分展示）
opsForHash --> hash操作
opsForList --> list操作
opsForSet --> set操作
opsForValue --> string操作
opsForZSet --> Zset操作


3.3.3 StringRedisTemplate默认序列化机制
public class StringRedisTemplate extends RedisTemplate<String, String> {

	/**
	 * Constructs a new <code>StringRedisTemplate</code> instance. {@link #setConnectionFactory(RedisConnectionFactory)}
	 * and {@link #afterPropertiesSet()} still need to be called.
	 */
	public StringRedisTemplate() {
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		setKeySerializer(stringSerializer);
		setValueSerializer(stringSerializer);
		setHashKeySerializer(stringSerializer);
		setHashValueSerializer(stringSerializer);
	}
	}
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
四、RedissonClient 操作示例
4.1 基本配置
4.1.1、Maven pom 引入
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
            <version>3.8.2</version>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
            <version>LATEST</version>
        </dependency>
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
4.1.2、添加配置文件Yaml或者json格式
redisson-config.yml

# Redisson 配置
singleServerConfig:
  address: "redis://192.168.1.140:6379"
  password: null
  clientName: null
  database: 15 #选择使用哪个数据库0~15
  idleConnectionTimeout: 10000
  pingTimeout: 1000
  connectTimeout: 10000
  timeout: 3000
  retryAttempts: 3
  retryInterval: 1500
  reconnectionTimeout: 3000
  failedAttempts: 3
  subscriptionsPerConnection: 5
  subscriptionConnectionMinimumIdleSize: 1
  subscriptionConnectionPoolSize: 50
  connectionMinimumIdleSize: 32
  connectionPoolSize: 64
  dnsMonitoringInterval: 5000
  #dnsMonitoring: false

threads: 0
nettyThreads: 0
codec:
  class: "org.redisson.codec.JsonJacksonCodec"
transportMode: "NIO"


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
16
17
18
19
20
21
22
23
24
25
26
27
28
或者，配置 redisson-config.json

{
  "singleServerConfig": {
    "idleConnectionTimeout": 10000,
    "pingTimeout": 1000,
    "connectTimeout": 10000,
    "timeout": 3000,
    "retryAttempts": 3,
    "retryInterval": 1500,
    "reconnectionTimeout": 3000,
    "failedAttempts": 3,
    "password": null,
    "subscriptionsPerConnection": 5,
    "clientName": null,
    "address": "redis://192.168.1.140:6379",
    "subscriptionConnectionMinimumIdleSize": 1,
    "subscriptionConnectionPoolSize": 50,
    "connectionMinimumIdleSize": 10,
    "connectionPoolSize": 64,
    "database": 0,
    "dnsMonitoring": false,
    "dnsMonitoringInterval": 5000
  },
  "threads": 0,
  "nettyThreads": 0,
  "codec": null,
  "useLinuxNativeEpoll": false
}


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
16
17
18
19
20
21
22
23
24
25
26
27
28
4.1.3、读取配置
新建读取配置类

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redisson() throws IOException {

        // 两种读取方式，Config.fromYAML 和 Config.fromJSON
//        Config config = Config.fromJSON(RedissonConfig.class.getClassLoader().getResource("redisson-config.json"));
        Config config = Config.fromYAML(RedissonConfig.class.getClassLoader().getResource("redisson-config.yml"));
        return Redisson.create(config);
    }
}
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
或者，在 application.yml中配置如下

spring:
  redis:
    redisson:
      config: classpath:redisson-config.yaml
1
2
3
4
4.2 使用示例

@RestController
@RequestMapping("/")
public class TeController {

    @Autowired
    private RedissonClient redissonClient;

    static long i = 20;
    static long sum = 300;

//    ========================== String =======================
    @GetMapping("/set/{key}")
    public String s1(@PathVariable String key) {
        // 设置字符串
        RBucket<String> keyObj = redissonClient.getBucket(key);
        keyObj.set(key + "1-v1");
        return key;
    }

    @GetMapping("/get/{key}")
    public String g1(@PathVariable String key) {
        // 设置字符串
        RBucket<String> keyObj = redissonClient.getBucket(key);
        String s = keyObj.get();
        return s;
    }

    //    ========================== hash =======================-=

    @GetMapping("/hset/{key}")
    public String h1(@PathVariable String key) {

        Ur ur = new Ur();
        ur.setId(MathUtil.randomLong(1,20));
        ur.setName(key);
      // 存放 Hash
        RMap<String, Ur> ss = redissonClient.getMap("UR");
        ss.put(ur.getId().toString(), ur);
        return ur.toString();
    }

    @GetMapping("/hget/{id}")
    public String h2(@PathVariable String id) {
        // hash 查询
        RMap<String, Ur> ss = redissonClient.getMap("UR");
        Ur ur = ss.get(id);
        return ur.toString();
    }

    // 查询所有的 keys
    @GetMapping("/all")
    public String all(){
        RKeys keys = redissonClient.getKeys();
        Iterable<String> keys1 = keys.getKeys();
        keys1.forEach(System.out::println);
        return keys.toString();
    }

    // ================== ==============读写锁测试 =============================

    @GetMapping("/rw/set/{key}")
    public void rw_set(){
//        RedissonLock.
        RBucket<String> ls_count = redissonClient.getBucket("LS_COUNT");
        ls_count.set("300",360000000l, TimeUnit.SECONDS);
    }

    // 减法运算
    @GetMapping("/jf")
    public void jf(){

        String key = "S_COUNT";

//        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
//        atomicLong.set(sum);
//        long l = atomicLong.decrementAndGet();
//        System.out.println(l);

        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        if (!atomicLong.isExists()) {
            atomicLong.set(300l);
        }

        while (i == 0) {
            if (atomicLong.get() > 0) {
                long l = atomicLong.getAndDecrement();
                        try {
                            Thread.sleep(1000l);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                i --;
                System.out.println(Thread.currentThread().getName() + "->" + i + "->" + l);
            }
        }


    }

    @GetMapping("/rw/get")
    public String rw_get(){

        String key = "S_COUNT";
        Runnable r = new Runnable() {
            @Override
            public void run() {
                RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
                if (!atomicLong.isExists()) {
                    atomicLong.set(300l);
                }
                if (atomicLong.get() > 0) {
                    long l = atomicLong.getAndDecrement();
                    i --;
                    System.out.println(Thread.currentThread().getName() + "->" + i + "->" + l);
                }
            }
        };

        while (i != 0) {
            new Thread(r).start();
//            new Thread(r).run();
//            new Thread(r).run();
//            new Thread(r).run();
//            new Thread(r).run();
        }


        RBucket<String> bucket = redissonClient.getBucket(key);
        String s = bucket.get();
        System.out.println("================线程已结束================================" + s);

        return s;
    }

}


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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87
88
89
90
91
92
93
94
95
96
97
98
99
100
101
102
103
104
105
106
107
108
109
110
111
112
113
114
115
116
117
118
119
120
121
122
123
124
125
126
127
128
129
130
131
132
133
134
135
136
137
4.3 扩展
4.3.1 丰富的jar支持，尤其是对 Netty NIO框架
4.3.2 丰富的配置机制选择，这里是详细的配置说明
关于序列化机制中，就有很多




4.3.3 API支持（部分展示），具体的 Redis --> RedissonClient ,可查看这里


4.3.4 轻便的丰富的锁机制的实现
4.3.4.1 Lock
4.3.4.2 Fair Lock
4.3.4.3 MultiLock
4.3.4.4 RedLock
4.3.4.5 ReadWriteLock
4.3.4.6 Semaphore
4.3.4.7 PermitExpirableSemaphore
4.3.4.8 CountDownLatch
五、基于注解实现的Redis缓存
5.1 Maven 和 YML配置
参考 RedisTemplate 配置

另外，还需要额外的配置类

// todo 定义序列化，解决乱码问题
@EnableCaching
@Configuration
@ConfigurationProperties(prefix = "spring.cache.redis")
public class RedisCacheConfig {

    private Duration timeToLive = Duration.ZERO;

    public void setTimeToLive(Duration timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        // 解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(timeToLive)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
        return cacheManager;
    }

}

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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
5.2 使用示例
@Transactional
@Service
public class ReImpl implements RedisService {

    @Resource
    private CustomerRepo customerRepo;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public static final String REDIS_CUSTOMERS_ONE = "Customers";

    public static final String REDIS_CUSTOMERS_ALL = "allList";

    // =====================================================================使用Spring cahce 注解方式实现缓存
    // ==================================单个操作

    @Override
    @Cacheable(value = "cache:customer", unless = "null == #result",key = "#id")
    public CustomersEntity cacheOne(Integer id) {
        final Optional<CustomersEntity> byId = customerRepo.findById(id);
        return byId.isPresent() ? byId.get() : null;
    }

    @Override
    @Cacheable(value = "cache:customer", unless = "null == #result", key = "#id")
    public CustomersEntity cacheOne2(Integer id) {
        final Optional<CustomersEntity> byId = customerRepo.findById(id);
        return byId.isPresent() ? byId.get() : null;
    }

     // todo 自定义redis缓存的key,
    @Override
    @Cacheable(value = "cache:customer", unless = "null == #result", key = "#root.methodName + '.' + #id")
    public CustomersEntity cacheOne3(Integer id) {
        final Optional<CustomersEntity> byId = customerRepo.findById(id);
        return byId.isPresent() ? byId.get() : null;
    }

    // todo 这里缓存到redis，还有响应页面是String（加了很多转义符\,），不是Json格式
    @Override
    @Cacheable(value = "cache:customer", unless = "null == #result", key = "#root.methodName + '.' + #id")
    public String cacheOne4(Integer id) {
        final Optional<CustomersEntity> byId = customerRepo.findById(id);
        return byId.map(JSONUtil::toJsonStr).orElse(null);
    }

     // todo 缓存json，不乱码已处理好,调整序列化和反序列化
    @Override
    @Cacheable(value = "cache:customer", unless = "null == #result", key = "#root.methodName + '.' + #id")
    public CustomersEntity cacheOne5(Integer id) {
        Optional<CustomersEntity> byId = customerRepo.findById(id);
        return byId.filter(obj -> !StrUtil.isBlankIfStr(obj)).orElse(null);
    }



    // ==================================删除缓存
    @Override
    @CacheEvict(value = "cache:customer", key = "'cacheOne5' + '.' + #id")
    public Object del(Integer id) {
        // 删除缓存后的逻辑
        return null;
    }

    @Override
    @CacheEvict(value = "cache:customer",allEntries = true)
    public void del() {

    }

    @CacheEvict(value = "cache:all",allEntries = true)
    public void delall() {

    }
    // ==================List操作

    @Override
    @Cacheable(value = "cache:all")
    public List<CustomersEntity> cacheList() {
        List<CustomersEntity> all = customerRepo.findAll();
        return all;
    }

    // todo 先查询缓存，再校验是否一致，然后更新操作，比较实用，要清楚缓存的数据格式（明确业务和缓存模型数据）
    @Override
    @CachePut(value = "cache:all",unless = "null == #result",key = "#root.methodName")
    public List<CustomersEntity> cacheList2() {
        List<CustomersEntity> all = customerRepo.findAll();
        return all;
    }

}



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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87
88
89
90
91
92
93
94
5.3 扩展
基于spring缓存实现

————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/qq_42105629/article/details/102589319/
```

```text
Redis学习手册18—Sentinel

Hiro · ZHAO

于 2020-06-16 17:02:22 发布

阅读量1.1k
 收藏

点赞数
分类专栏： # Redis 文章标签： redis Redis Sentinel Redis哨兵模式
版权

Redis
专栏收录该内容
20 篇文章
订阅专栏
故障转移
因为Redis支持主从复制，并且主从服务器的数据完全一致。因此，当主服务器因故障下线时，将它的其中一台从服务器转换为主服务器，并使用新的主服务器继续处理命令请求，这样整个系统就可以继续运行，不比仅因为主服务器下线而停机。这种使用正常服务器替换下线服务器以维持系统正常运转的操作，一般被称为故障转移（failover）。

手动故障转移
因为Redis支持主从复制特性，所以我们同样可以对下线的Redis主服务器实施故障转移，假设现在有一个主服务器 127.0.0.1:6379（简称 6379），它有两个从服务器 127.0.0.1:6380（简称 6380） 和 127.0.0.1:6381（简称 6381），对主服务器实施故障转移的步骤如下：

先向 6380 发送命令 REPLICAOF no one，将它转换为主服务器；
然后向另一个从服务器6381发送命令 REPLICAOF 127.0.0.1 6380，让它去复制新的主服务器 6380；
具体如下图所示：



Sentinel（哨兵）
当Redis主从复制结构比较复杂，比如一主多从的情况下，监视多台Redis服务器，并在有需要的时候对下线的主服务器实施故障转移，并不是一件容易的事情。为此，Redis服务器提供了自动化的故障转移功能，提高主从服务器的可用性，Redis为用户提供了 Redis Sentinel 工具，Sentinel（哨兵） 可以通过心跳检测的方式监视多个主服务器以及它们下属的所有从服务器，并在某个主服务器下线的时候自动对其实施故障转移。

启动Sentinel
在了解了Redis Sentinel的基本作用之后，现在让我们来学习一下如何启动Redis Sentinel并让它去监视指定的主服务器。

Redis Sentinel的程序文件名为 redis-sentinel，它通常和普通的Redis服务器 redis-server 位于同一个文件夹。因为用户需要在配置文件中指定想要被 Sentinel 监视的主服务器，并且 Sentinel 也需要在配置文件中写入信息以及记录主从服务器的状态，所以用户在启动 Sentinel 的时候必须传入一个 可写 的配置文件作为参数，如下：

redis-sentinel /etc/sentinel.conf

如果用户给定的配置文件是不可写的，那么 Sentinel 将放弃启动并报告一个错误

一个 Sentinel 配置文件至少需要包含以下选项，用于指定 Sentinel 要监视的主服务器：

sentinel monitor <master-name> <ip> <port> <quorum>

其中 <master-name> 参数用于指定主服务器的名字，这个名字在执行各种 Sentinel 操作的时候经常会用到；ip 参数和 port 参数用于指定主服务器的IP地址和端口号；而 quorum 参数则用于指定判断这个主服务器下线所需要的 Sentinel 数量。

sentinel monitor mymaster 127.0.0.1 6379 1
1
Sentinel 在开始监视一个主服务器之后，就会获取被监视主服务器的从服务器名单，并根据名单对各个从服务器实施监视，整个过程完全自动，所以用户只需要输入待监视的主服务器的地址就可以了，并不需要输入从服务器的地址。

redis-sentinel和redis-server之间的关系

因为Redis Sentinel实际上就是一个运行与特殊模式下的Redis服务器，所以用户也可以使用命令 redis-server sentinel.conf --sentinel 去启动一个Sentinel：这里的 --sentinel 参数用于指示Redis服务器进入Sentinel模式，从而变成一个Redis Sentinel而不是普通的Redis服务器。
同时监视多个主服务器

一个Sentinel可以监视任意数量的主服务器，而不是仅仅监视一个主服务器。如果用户想要使用Sentinel去监视多个主服务器，那么只需要在配置文件中指定多个sentinel monitor选项即可。
处理重上线的旧主服务器

Sentinel在对下线的主服务器实施故障转移之后，仍然会继续对它进行心跳检测，当这个服务器重新上线的时候，Sentinel将把它转换为当前主服务器的从服务器。
设置从服务器优先级

用户可以通过 replica-priority 配置选项来设置各个从服务器的优先级，优先级高的从服务器在 Sentinel 选择新主服务器的时候会优先被选择。replica-priority的默认值为100，这个值越小，从服务器的优先级越高。replica-priority值为0的从无服务器永远不会被选为主服务器，用户可以通过这一设置将不适合用作主服务器的从服务器排除在新主服务器的候选名单之外。
新主服务器的挑选规则

当Sentinel需要在多个从服务器中选择一个作为新的主服务器时，首先会根据以下规则从候选名单中剔除不符合条件的从服务器：
1) 否决所有已经下线以及长时间没有回复心跳检测的疑似已下线的从服务器。
2) 否决所有长时间没有与主服务器通信的，数据状态过时的从服务器。
3) 否决所有优先级为0的从服务器。
然后根据以下规则，在剩余的候选从服务器中选出新的主服务器：
1) 优先级最高的从服务器获胜。
2) 如果优先级最高的从服务器有两个或以上，那么复制偏移量最大的那个从服务器获胜。
3) 如果符合上述两个条件的从服务器有两个或以上，那么选出它们当中运行ID最小的那一个。
Sentinel网络
在实际应用中，只使用单个 Sentinel 监视主从服务器并不合适，因为：

单个 Sentinel 可能会形成单点故障，当唯一的 Sentinel 出现故障时，针对主从服务器的自动故障转移将无法实施。
单个 Sentinel 可能会因为网络故障而无法获得主服务器的相关信息，并因此错误的将主服务器判断为下线，继而执行实际上并无必要的故障转移操作。
为了避免 Sentinel 单点故障，并能够给出真实有效的判断结果，我们可以使用多个 Sentinel 组建一个分布式 Sentinel 网络，网络中的各个 Sentinel可以通过互通消息来更加准确地判断服务器的状态。

当 Sentinel 网络中的其中一个 Sentinel 认为某个主服务器已经下线时，它会将这个主服务器标记为主观下线，然后询问网络中的其他 Sentinel，是否也认为该服务器已下线。当同意主服务器下线的 Sentinel 数量达到 sentinel monitor 配置选项中 quorum 参数所指定的数量时，Sentinel 就会将相应的主服务器标记为客观下线，然后开始对其进行故障转移。

Sentinel管理命令
Redis为 Sentinel 提供了相应的管理命令，用于对 Sentinel 执行各种各样的管理操作。

Sentinel masters：获取所有被监视的主服务器的信息
通过向 Sentinel 发送以下命令，用户可以获得 Sentinel 正在监视的所有主服务器的相关信息：

Sentinel masters

命令返回的各个字段的及其意义：



Sentinel master：获取指定被监视主服务器的信息
如果想要获取特定主服务器的信息而不是主服务器的信息，可以使用以下命令代替 Sentinel masters命令：

Sentinel master <master-name>

这个命令只会返回用户指定的主服务器的相关信息。

Sentinel slaves：获取被监视主服务器的从服务器信息
通过使用以下命令，可以让 Sentinel 返回指定的主服务器属下所有从服务器的相关信息：

Sentinel slaves <master-name>

命令返回的字段及其意义：



Sentinel sentinels：获取其他Sentinel的相关信息
用户可以通过执行以下命令，获取监视同一个主服务器的其他所有Sentinel的相关信息：

Sentinel sentinels <master-name>

命令返回的字段及其含义：


Sentinel get-master-addr-by-name：获取给定的主服务器的IP地址和端口号
用户可以通过以下命令，通过给定的主服务器的名字来获取该服务器的IP地址和端口号：

Sentinel get-master-addr-by-name <master-name>

Sentinel reset：重置主服务器状态
Sentinel reset 命令接受一个glob风格的模式作为参数，接收到该命令的 Sentinel 将重置所有与给定模式相匹配的主服务器：

Sentinel reset <pattern>

命令将返回被重置主服务器的数量作为返回值。接收到Sentinel reset 命令的 Sentinel 除了会清理被匹配主服务器的相关信息之外，还会遗忘被匹配主服务器目前已有的所有从服务器，以及正在监视被匹配主服务器的所有其他 Sentinel。然后，这个 Sentinel将会重新搜索正在监视被匹配主服务器的其他Sentinel，以及该服务器属下的各个从服务器，并与它们重新建立连接。

Sentinel failover：强制执行故障转移
通过执行以下命令，可以强制对指定的主服务器实施故障转移：

Sentinel failover <master-name>

接收到这一命令的Sentinel 会直接对主服务器执行故障转移操作，而不会像平时那样，先在Sentinel网络中投票，然后再根据投票结果决定是否执行故障转移操作。

Sentinel ckquorum：检查可用的Sentinel数量
通过以下命令，检查Sentinel 网络中当前可用的Sentinel数量是否达到了判断主服务器客观下线并实施故障转移所需的数量：

Sentinel ckquorum <master-name>

Sentinel flushconfig：强制写入配置文件
用户可以通过向 Sentinel 发送以下命令，让 Sentinel 将它的配置文件重新写入硬盘中：

Sentinel flushconfig

因为Sentinel在被监视服务器的状态发生变化时就会自动重写配置文件，所以这个命令的作用就是在配置文件基于某些原因或错误而丢失时，立即生成一个新的配置文件。

在线配置 Sentinel
从Redis 2.8.4版本开始为 Sentinel 命令添加了一组子命令，这些子命令可以在线修改Sentinel对于监视主服务器的配置选项，并把修改后的配置选项保存到配置文件中，整个过程完全不需要停止 Sentinel，也不需要手动修改配置文件。

Sentinel monitor：监视给定的主服务器
通过以下命令，用户可以让 Sentinel 开始监视一个新的主服务器：

Sentinel monitor <master-name> <ip> <port> <quorum>

Sentinel monitor命令本质上就是 sentinel monitor配置选项的命令版本，当我们想要让 Sentinel 监视一个新的主服务器，但是又不想重启 Sentinel 并手动修改配置文件时，就可以使用该命令。

Sentinel remove：取消给定主服务器的监视
当用户想要在线取消Sentinel对某个主服务器的监视时，可以使用以下命令：

Sentinel remove <master-name>

接收到这个命令的Sentinel会停止对给定主服务器的监视，并删除Sentinel内部以及Sentinel配置文件中与给定主服务器有关的信息，然后返回OK，表示执行成功。

Sentinel set：修改Sentinel配置选项的值
通过使用以下命令，用户可以在线修改Sentinel配置文件中与主服务器有关的配置选项：

Sentinel set <master-name> <option> <value>

只要是Sentinel配置文件中与主服务器有关的配置选项，都可以使用 Sentinel set 命令在线进行配置，命令在执行成功后返回OK表示成功。

在线配置命令的注意事项
需要注意的是，以上介绍的各个在线配置命令只会对接收到命令的单个Sentinel起效，并不会对同一个Sentinel网络的其他Sentinel产生影响。
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/lingxi0726/article/details/106786628
```


```text
Linux中部署Redis主从复制，主从复制原理
2023-03-13
2,698
阅读8分钟
专栏： 
Redis 系列文章

主从复制
在上一篇文章中谈了单机的 Redis 部署，你觉得单机部署的架构有什么问题吗？主从复制能够解决全部问题吗？

前文：Linux 安装 Redis 单机，详细图解

后文：Linux安装Redis（三）哨兵模式，分析流程和原理，详细图解

但我们还是回到本文的内容吧

本文谈到的内容主要是以下几点：

如何部署Redis主从复制（一主二从）
一些关于主从的问题模拟和思考
主从复制原理和工作流程
前文
所谓主从复制，就是以其中一台机器作为 master，并且以写为主，其他从服务器（Slave）则是以读为主，达到读写分离的效果，以来提高系统性能。从服务器的数据全部从主服务中复制同步而来。

当 master 数据变化的时候，自动将新的数据异步同步到其他Slave数据库

redis 官方文档：redis.io

主从复制它解决了什么问题？

其实这很好说吗，读写分离、容灾备份、数据备份、水平扩容支撑高并发等等，其实也蛮多的，但是也有一些不足之处，后面就知道啦

一、相关命令
其实主从分离是真的很简单，就改几个配置项就完事啦，和单机的没啥区别

也就这一句话，配置从库，不配置主库，从库变更一下配置文件，或者是输入几行命令就可以，主库则不需要动，和单机一样。

关于权限：

如果主库设置了密码，则从机需要验证密码
master 如果配置了 requirepass 参数，需要密码登陆
那么slave就要配置masterauth 来设置校验密码，否则的话，master会拒绝slave的访问请求
基本操作命令

info replication : 可以查看复制节点的主从关系和配置信息
replicaof 主库IP 主库端口 ：一般写入redis.conf配置文件内
slaveof 主库IP 主库端口：每次与 master 断开之后，都需要重新连接，除非你配置进redis.conf 文件。
在运行期间修改slave节点的信息，如果该数据库已经是某个主数据库的从数据库，那么会停止和原主数据的同步关系转而和新的主数据库库同步，重新建立联系。
slaveof no one：使当前数据库停止与其他数据库的同步，转为主数据库.
二、部署架构
我有三台虚拟机，分别是

192.168.208.128

192.168.208.129

192.168.208.130

准备部署的架构图为：

image.png

三、开始部署
1、前期准备：
三台虚拟机上都已经安装完Redis服务，这点不会可以去考古-->

三台虚拟机能够相互ping通，且开放相关的 redis 服务使用的端口或者是关闭防火墙。

关闭防火墙：

systemctl stop firewalld #三台都要关 或者开发相关的端口
配置主从复制，我们只需要修改从库的配置，不需要修改主机的配置。

replicaof 主库IP 主库端口 ：一般写入redis.conf配置文件内
slaveof 主库IP 主库端口：每次与 master 断开之后，都需要重新连接，除非你配置进redis.conf 文件。
在运行期间修改slave节点的信息，如果该数据库已经是某个主数据库的从数据库，那么会停止和原主数据的同步关系转而和新的主数据库库同步，重新建立联系。
slaveof no one：使当前数据库停止与其他数据库的同步，转为主数据库，自立为王。
2、修改配置文件：
我们重新cp 一份未动过的配置文件，一步一步来看，到底那些要进行配置

补充：从机就比主机多了第十步的一个操作，其余均相同，其他更详细的配置还是需要大家在生产环境中配置，例如aof的开启和关闭，同步时间是多长等等

image.png

大致步骤：

image.png

1、修改 daemonize 为yes

image.png

2、注释掉 bind 127.0.0.1

image.png

3、修改 protected-mode 为 no

image.png

4、指定 port 端口

image.png

其实很多时候在线的 redis 服务并不会开放默认的端口，而是会换成其他不常用的端口，来求得一定的安全性

5、指定工作目录

image.png

6、指定 pidfile 文件的名称

image.png

可改可不改，知道存在，万一要看了，知道在哪里可以看到

7、指定 logfile 文件名称

image.png

我这里指定的是我的地址哈~

8、指定 dump.rdb 文件名称（dbfilename）

image.png

9、requirepass 设置密码

image.png

主机配到这里就可以啦，下面的这个是从机才需要配置的。

10、配置访问主机的密码 masterauth 、 masterip、masterport

image.png修改完后就是如下：

image.png

第二台从机也是一样的配置。

四、启动测试
先启动master，再陆续启动两台slave.

image.png

确定主机和从机都起来之后。

我们在主机上set 一个值，我们看有没有复制过去

image.png

image.png

image.png

另外也可以看一下从机的log日志，这边的主机我忘记配了，就没啦~ 感兴趣的话，自己去看一下主机的日志

image.png

查看服务器状态：

image.png

五、思考问题
你觉得上述这样的架构还存有一些什么问题吗？

1、当主机宕机之后，从机会自动变成主机吗？

答案是不会，这里木有人来操作啊~
2、从机宕机之后，再启动是从机还是主机？

如果是写入配置的方式，从机宕机后重启还是从机，如果是使用命令的方式，来建立主从服务的话，重启后则是主机。
3、主机宕机之后，从机可不可以成为另一台机器的从机？

可以，但需要人工干预，来输入相关命令，让该从机成为其他机器的从机。
4、主机宕机之后，我们可以让剩下的其中一台成为主机吗？

可以，但需要人工干预，在客户端执行slave no one命令
六、薪火相传
如下图部署方式：

image.png

是什么意思呢？

就是当主机宕机的时候，192.168.208.129这台从机，在人工干预的情况下可以立马升级为主机，它下面跟随着两台从机，也可以继续提供读服务，而无需重新改向新的主机。

如果是按照之前的方式部署，如果主机宕机了，那么两台从机都只能提供读服务，需要人工干预将其中一台从机升级为主机，再手动将另外一台的从机改从向新的主机进行复制。（注意：如果要改变跟随的主机，是需要将slave的数据全部清除掉，然后再从新跟随的主机，重新开始复制，数据量比较大的情况，需要一定的时间）

配置是一样的，就是一台跟着一台，只是把跟随的主机变更了。

七、反客为主
我们之前就谈到了，如果主机宕机了，我们是可以手动让从机变更为主服务的，发送slave no one即可。

八、主从复制原理和工作流程
借鉴的一张图：

image.png

来源 Redis主从复制原理

slave 第一次启动成功连接到 master 后会发送一个 sync 命令，首次全新连接 master，会自动进行一次完全同步（全量复制），slave自身原有数据会被 master 数据覆盖清除。

master 节点收到 sync 命令后会开始在后台保存快照（即RDB持久化，主从复制时会触发RDB）同时收集所有接收到的用于修改数据集命令缓存起来，master 节点执行RDB持久化完后，master将rdb快照文件和所有缓存的命令发送到所有slave，以完成一次完全同步。

而slave服务在接收到数据库文件数据之后，将其存盘并加载到内存中，从而完成复制初始化。

在正常运行的过程中，主从双方要保持通信，主机会向从机发送心跳检测，心跳检测的默认时间为10秒，repl-ping-replica-period 10

增量复制，除去第一次连接是全量复制以外，之后 master 继续将新的所有收集到的修改命令自动依次传给slave，完成同步。

从机下线，重连续传；

master会检查backlog里面的offset，master和slave都会保存一个复制的offset还有一个masterId,

offset是保存在backlog中的。master只会把已经复制的offset后面的数据复制给slave，类似断点续传。

缺点
1、复制具有延时性，并且因为所有的写操作都是现在Master 上操作，然后同步更新到 Slave 上，所以从 Master 同步到 Slave 机器上有一定的延迟，当系统很繁忙的时候，延迟问题会更严重，另外就是当 Slave 机器数量变多，复制次数变多也会使这个问题更加严重。

2、当 master 挂了的时候，并不会在 slave 节点中自动选举一个节点成为Master节点，从而导致redis服务只能读而不能写。

同时也意味着出现事故的时候，必须要有人工干预。

后文
如果仔细想想，主从复制的架构其实还是有很多问题的，比如不能够自动的进行故障转移，必须要人工干预，又或者是复制的延迟性等。

但不得不说，部署是极为简单的，机器资源要求也不高，在很多时候还是非常实用的，比如做容灾备份，数据备份等。

对啦，想不如做，看也不如做，感兴趣的话，我还是建议可以抽空试一试~

写于 2023年3月11日，作者：宁在春

本文正在参加「金石计划」 
```


## https://redis.io/docs/latest/operate/oss_and_stack/management/sentinel/
```text
Redis哨兵模式部署，哨兵模式原理分析以及图解
2023-03-13
4,456
阅读14分钟
专栏： 
Redis 系列文章

前文：

1、Linux 安装 Redis （一）单机模式，详细图解

2、Linux 安装 Redis（二）主从复制，分析流程和原理，详细图解

本文大致会说到的一些内容：

redis哨兵模式的部署
模拟故障转移
查看详细日志信息
关于一些问题的思考
关于哨兵模式的运行流程详解
哨兵模式选举原理
哨兵模式
哨兵就如这个名词一般，就是负责监控和巡查~，哨兵会监控master主机是否故障，如果故障了根据投票数自动将某一个从库转换为新主库，继续对外服务，也就是能够弥补上我们之前谈到的主从复制中需要人工干预的缺点。

官网理论：redis.io/docs/manage…

哨兵模式的优点：

1、主从监控，监控主从redis库运行是否正常

2、消息通知，哨兵可以将故障转移的结果发送给客户端，

3、故障转移，如果master宕机，哨兵会主动进行主从切换，在其中的slaves中选择一个slave成为新的master。

4、配置中心，客户端通过连接哨兵来获得当前redis服务的主节点地址（应用程序端不再直接连接redis主从节点，而是连接哨兵集群)。

一、部署架构图
三个哨兵，自动监控和维护集群，不存放数据，只做监控；一主二从：用户存放数据和读取数据的redis.

哨兵本质上也是一个redis服务，只不过它不再直接存放数据了~

应用程序不再直接连接redis主从服务，而是转而直接连接哨兵集群，以达到无感知的主从切换。

image.png

二、开始部署
1、备份 sentinel.conf
在我们原本解压的目录下，可以看到有一个默认的sentinel.conf 文件，我们copy一份到我自己定义的/myredis目录下

image.png

2、常见和重点配置项
记得看看默认的sentinel的文件有那些内容，不然只光会部署，意义好像也不太大~

此处只贴出了一些常见参数的说明和一些重点配置项

bind 服务监听地址，用户客户端连接，默认本机地址
daemonize 是否以后台daemonize 方式运行
protected-mode 安全保护模式
port 端口
logfile 日志文件路径
pidfile pid文件路径
dir 工作目录
哨兵模式需要配置的几个重要参数：

sentinel monitor <master-name> <ip> <redis-port> <quorum>
#设置要监控的master 服务器，quorum 表示最少有几个哨兵认可客观下线，同意故障迁移的法定票数
sentinel auth-pass <master-name> <password> 
# master 设置了密码，连接master服务的密码
上述命令中的 quorum 就是表示最少有几个哨兵认可客观下线，同意故障迁移的法定票数。后续在运行流程会再谈到

我们知道，网络是不可靠的，有时候一个sentinel 会因为网络堵塞而误以为一个master redis 已经挂掉了，在sentinel集群环境下需要多给sentinel互相沟通来确认某个master是否真的死了，quorum这个参数是进行客观下线一个依据，意思是至少有quorum个sentinel认为这个master有故障，才会对这个master进行下线以及故障转移。

通俗点说，你一个人认为它挂了，这是不准确的，哪里能这么霸道呢，要公平的询问大家的信息，如果达到指定票数的sentinel都认为该master有问题，才会进行下一步操作。

另外这还有几个进阶的参数，后续用到了再说吧，本文没有过多涉及。

sentinel down-after-milliseconds <master-name> <milliseconds>
指定多少毫秒之后，主节点没有应答哨兵，此时哨兵主观上认为该主节点下线（这也是后续会说到的主观下线，默认是30000毫秒）
​
sentinel parallel-syncs <master-name> <nums>
表示允许并行同步的 salve 个数，当master挂了后，哨兵会选出新的master，此时，剩余的slave会跟随新的matser，重新开始复制数据
​
sentinel failover-timeout <master-name> <milliseconds>
故障转移的超时时间，进行故障转移时，如果超过设置的毫秒，表示故障转移失败
​
sentinel notification-script <master-name> <script-path>
配置当某一事件发生时所需要执行的脚本
​
sentinel client-reconfig-scipr <master-name> <script-path>
客户端重新配置主节点参数脚本
3、本次案例哨兵sentinel文件配置
为了更好的观察配置文件的变化，我这边是将配置文件中所有无关的文字都删除掉了。

默认配置文件总共为 342行，删除无关注释后的默认配置如下：

protected-mode no
port 26379
daemonize no
pidfile /var/run/redis-sentinel.pid
logfile ""
dir /tmp
sentinel monitor mymaster 127.0.0.1 6379 2
sentinel down-after-milliseconds mymaster 30000
acllog-max-len 128
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 180000
sentinel deny-scripts-reconfig yes
SENTINEL resolve-hostnames no
SENTINEL announce-hostnames no
SENTINEL master-reboot-down-after-period mymaster 0
我这三台sentinel的通用配置文件如下：# 三台的端口分别是 26379、26380、26381,相关的文件名称也改一下，我这不赘述了。

protected-mode no 
port 26379 
daemonize yes  
pidfile /var/run/redis-sentinel26379.pid 
logfile "/myredis/sentinel26379.log"
dir /myredis
sentinel monitor mymaster 192.168.208.128 6379 2
sentinel auth-pass mymaster 123456 
sentinel down-after-milliseconds mymaster 30000
​
acllog-max-len 128
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 180000
sentinel deny-scripts-reconfig yes
SENTINEL resolve-hostnames no
SENTINEL announce-hostnames no
SENTINEL master-reboot-down-after-period mymaster 0
我这里是做的是简单的测试，其他相关的配置，是需要根据生产环境，业务大小来设置，光说没啥用~ 详情还是需要看redis官方文档

三份配置文件配完就是如下：

image.png

4、启动一主二从redis，测试
启动一主二从，进行测试

在主机的配置中也要加上:masterauth "123456"

image.png

因为如果主机宕机后，再次恢复上线可能会变成从机，所以需要配置上密码。

我们在启动观察一下配置文件的最末端是什么内容，稍后会有新的发现哦

image.png

主从测试：

image.png

image.png

image.png查看三台机器目前的状态信息

info replication
image.png

假如我们现在手动模拟主机宕机，关停 master 主机，他们会自动切换吗？答案是并不会，感兴趣的同学可以自己试一下~

在确定正常后，我现在直接启动哨兵集群啦~

5、启动主从后，启动哨兵集群做测试
启动哨兵集群

redis-sentinel sentinel26379.conf --sentinel
redis-sentinel sentinel26380.conf --sentinel
redis-sentinel sentinel26381.conf --sentinel
image.png

通过ps -ef | grep redis 的结果可以看到，我们已经成功启动了哨兵集群，接下来，我们再进行一遍redis的主从测试。

另外我们查看一下三台sentinel的日志信息

image.png

也没标记啥啦，后期拿来对比会看的更明显

6、模拟 master 挂了后的场景，查看相关日志信息
现在我们手动模拟master主机宕机，看看会出现什么样的情况？

image.png

image.png

image.png

从上面的结果中，我们可以看到，在哨兵模式下，我们已经成功的实现主从机之间故障转移，并且无需我们人工干预。

7、哨兵到底做了什么？
那哨兵到底做了一些什么呢？怎么就达到了主从机之间故障转移呢？

我们来看看哨兵的日志信息：

image.png其他两台也是类似的~ 就不重复贴出来啦~

刚刚我在上图中谈到了重写覆盖配置，我们一起来看看到底发生了什么吧

对比配置文件

在 129 的从机上（已成为主机），我们会看到在配置文件末尾添加了一段新的配置

image.png

并且我们之前配置的跟随主机的那段配置也被删除了

image.png

我们可以看到哨兵模式在故障转移的时候，帮助我们重写了覆盖配置文件。

那么自然另外一个从机的配置文件也大差不差啦~

跟随的主机的配置，变成了 129 机器

image.png

同样在最后也添加了一些配置~

image.png

8、宕机的master恢复了，会发生什么？
假如我之前宕机的master，在一段时间被恢复了，那么它还会是主机吗？？

答案肯定是不会啦，因为你想想啊，原来从机中的配置文件都已经被改了，它怎么可能还会是主机呢？

再想想，我们没有修改挂掉的主机的配置文件（即没有添加 replicaof 主机IP、主机port ），它重启还会加入到这个主从集群中吗？

想不如做，自己亲自动手去看看，远比看到我这个答案有趣的多。

答案是会，会变成当前主机下的从机。

image.png

问题又来啦，难道哨兵模式在我重启之后，帮助我们重写了配置文件吗？

恭喜你，答对啦

image.png

这个过程是在重新启动之前就指定了的，只不过可能我们看日志看的没有那么仔细。

我把之前那张故障切换的截图拿过来你们就懂了。

image.png

看到这里，我再提个小问题？你觉得哨兵的配置文件被覆盖重写了吗？

我们直接打开看一看你就知道

image.png

9、一些思考
诸如上述问题，在看完下一章节，便会一一了解。

我们通过上面观察到的信息，可以看出哨兵模式下redis集群，它是会被sentinel进行动态更改的。

哨兵如何知道所有从库地址呢？

哨兵是如何帮助我们更改配置的呢？

哨兵也是实例，如果挂了怎么办？

先思考思考吧，有些后面有答案，有些没写，可以自己去看看其他文章。

三、哨兵的运行流程和选举原理
当一个主从配置中的master故障宕机之后，sentinel可以选举出一个新的master出来，自动接替原master的工作，即我们常说的自动实现主从切换，故障转移。主从配置中的其他redis服务器自动指向新的master同步数据。

一般建议sentinel部署奇数台，方便投票，哈哈，同时也是防止某一台sentinel无法连接到master导致误切换。

先说说大概的故障转移流程

image.png

3.1、主观下线
SDowm（主观不可用）是单个sentinel自己主观下检测到的关于Master的状态，从sentinel的角度来看，如果发送了 PING 心跳后，在一定时候内没有收到合法的回复，那么这个Sentinel会主观的（单方面的）认为这个Master不可用了。

Sentinel配置文件中的 down-after-milliseconds设置了判断主观下线的时间长度，默认是 30000 毫秒

sentinel down-after-milliseconds mymaster 30000
3.2、客观下线
客观下线，就是集群中的多个sentinel都判定该master出现故障了，这里的多个就是我们上文谈到过的 quorum,

sentinel monitor <master-name> <ip> <redis-port> <quorum>
#设置要监控的master 服务器，quorum 表示最少有几个哨兵认可客观下线，同意故障迁移的法定票数
上述命令中的 quorum 就是表示最少有几个哨兵认可客观下线，同意故障迁移的法定票数。

通俗点说，你一个人认为它挂了，这是不准确的，哪里能这么霸道呢，要公平的询问大家的信息，如果达到指定票数的sentinel都认为该master有问题，才会进行下一步操作。

思考一下：如果是哨兵集群中的哨兵数量为1，而此处的quorum的值为2,还能够正常进行故障转移吗？那如果哨兵集群是两台呢？可以吗？

答案见文末，先想想吧。

3.3、选择哨兵Leader:
当主节点被判断客观下线之后，各个哨兵节点会进行协商，先选举出一个领导者哨兵节点（Leader）并由该领导者节点，也即被选举出来的Leader 节点来进行 failover(故障转移，从存活中的节点中选一个来转变为主服务)。

又回到之前谈到的问题，那哨兵领导者（Leader）是如何被选出来的呢?

这牵扯到 Raft 算法，一个分布式协议算法，详细的可以看看这篇文章：深度解析 Raft 分布式一致性协议

监视该主节点的所有哨兵节点都有可能被选为领导者，选举使用的算法是 Raft 算法： Raft 算法的基本思路是先到先得：

即在一轮选举中，哨兵A向B发送成为领导者的申请，如果B没有同意过其他哨兵，则会同意A成为领导者。

image.png

3.4、选择新Master
选出新master的规则，剩余slave节点健康前提下：

redis.conf文件中，优先级 replica-priority 最高的从节点(数字越小，优先级越高，默认是100)
复制偏移量位置offset最大的从节点
最小的Run ID 的从节点
image.png

3.5、小结
故障恢复：

1、从下线的主服务器下的所有从机里挑选一个从服务，将其转为主机。

选择条件依次为：

选择优先级靠前的，优先级在redis.conf中的 salve-priority 100
选择偏移量最大的，偏移量是指获得原主数据最多的
选择runid最小的，每个redis实例启动后都会获得一个随机生成的40位的runid，越小的启动的越早。
2、挑选出新的主服务后，sentinel向原主服务的从服务发送slaveof新主服务命令，复制新的master。

执行 slaveof no one 命令让选出来的从节点成为新的主节点，并通过slave of 命令让其他节点成为其从节点，
sentinel leader 会对选举出的新master执行slave no one 操作，将其提升为 master 节点
sentinel leader向其他 slave 发送命令，让剩下的 slave节点成为新的master节点的从节点。
3、当已下线的服务重新上线时，sentinel会向其发送slave的命令，让其成为新主的从服务。

注意点
哨兵节点的数量应为多个（奇数更佳），哨兵本身也应为集群，保证高可用。
各个哨兵节点的配置应为一致（这在诸多集群服务中都是如此）
在本地虚拟机自己玩的时候，一定要记得关闭防火墙或者是开放使用的端口。
哨兵集群+主从复制，并不能保证数据零丢失（比如主机接收到了命令还没同步到从机就挂了，还有主从切换期间，服务的不可用）
之前思考的答案：一台机器是不可以的，两台机器是可以的。

简单说一下原因：哨兵集群中机器数量为3时，quorum设置为2，这肯定是可行的，因为会有2票的产生。当机器为2时，quorum设置为2时，也是可以进行故障转移的。一台机器时设置quorum 为2，那自然是不行的哈。

后文
其实在蛮久以前，就知道这些了，但是太久不用，没去搭建，很多东西都给忘记了。

很多事情，想不如做，就像一些问题的答案，都是自己一步一步尝试出来的。

在此之前，你要是来问我哨兵集群中只有一台机器或两台机器，quorum设置为2，可不可以进行故障转移，我肯定会迟疑，因为我没有试过，所以我不确定。

想要得到一个确切的答案，那么就只能是亲自去躺坑。

谢谢阅读，希望看完的你有所收获，祝你生活愉快！

写于2023年3月11日，作者：宁在春

参考文章
www.jianshu.com/p/06ab9daf9…

www.cnblogs.com/conefirst/a…

zhuanlan.zhihu.com/p/296271483

字节一面：Redis主节点宕机，如何处理？


```

```text
Redis——Lettuce的主从哨兵模式

FlyLikeButterfly

于 2022-05-01 11:00:00 发布

阅读量3k
 收藏 2

点赞数
分类专栏： redis 文章标签： redis 数据库 database Lettuce
版权

redis
专栏收录该内容
18 篇文章
订阅专栏
 该博客详细介绍了如何使用Lettuce库连接Redis的主从服务器和哨兵系统。通过示例代码展示了连接过程、哨兵信息获取、主从切换以及设置读取策略等操作，强调了MasterReplica.connect()和client.connectSentinel()的区别。此外，还提及了ReadFrom枚举类型用于设置读取数据的来源。
摘要生成于 C知道 ，由 DeepSeek-R1 满血版支持， 前往体验 >

Lettuce操作主从服务器使用的是MasterReplica.connect()方法创建的主从专用StatefulRedisMasterReplicaConnection，跟普通的通过client创建有点区别，Lettuce连接哨兵则使用client的connectSentinel()方法，创建的是哨兵专用的StatefulRedisSentinelConnection；

另外，主从模式可以设置ReadFrom读取数据来源：（还是挺方便的）

MASTER：只从主服务读取数据；
MASTER_PREFERRED：优先从主服务读取数据，主服务不可用时再从从服务读取；
UPSTREAM：同MASTER；
UPSTREAM_PREFERRED：同MASTER_PREFERRED；
REPLICA_PREFERRED：优先从从服务读取数据，没有可用从服务时再从主服务读取；
REPLICA：只从从服务读取数据；
LOWEST_LATENCY：使用延迟低的读取；
ANY：可从任何节点读取；
ANY_REPLICA：从任何从服务读取；
测试Demo：

使用的redis版本6.2.6，Lettuce版本6.1.8；

主从redis：192.168.1.31:6379、192.168.1.31:6380、192.168.1.31:6381；

哨兵：192.168.1.31:26379；

/**
 * 2022年4月29日下午2:46:43
 */
package testlettuce;
 
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
 
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import io.lettuce.core.sentinel.api.sync.RedisSentinelCommands;
 
/**
 * @author XWF
 *
 */
public class TestLettuceMasterReplicaAndSentinel {
 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//sentinel
		RedisURI sentinelUri = RedisURI.builder()
				.withSentinel("192.168.1.31", 26379, "111111")	//哨兵地址和密码
				.withSentinelMasterId("mymaster")
				.withPassword("123456".toCharArray())
				.build();
		RedisClient sentinelClient = RedisClient.create(sentinelUri);
		StatefulRedisSentinelConnection<String, String> sentinelConn = sentinelClient.connectSentinel();
		RedisSentinelCommands<String, String> sentinelCmd = sentinelConn.sync();
		System.out.println(sentinelCmd.info("sentinel"));
		List<Map<String, String>> masters = sentinelCmd.masters();
		System.out.println(masters);
		Map<String, String> mymaster = sentinelCmd.master("mymaster");
		System.out.println(mymaster.get("name") + "," + mymaster.get("ip") + "," + mymaster.get("port"));
		List<Map<String, String>> slaves = sentinelCmd.slaves("mymaster");
		slaves.forEach(x -> {
			System.out.println(x.get("ip") + ":" + x.get("port"));
		});
		
		StatefulRedisPubSubConnection<String, String> sentinelPubSubConn = sentinelClient.connectPubSub();//需要设置主从密码
		sentinelPubSubConn.addListener(new RedisPubSubListener<String, String>() {
			@Override
			public void unsubscribed(String channel, long count) {
				System.out.println(String.format("unsubscribed:%s", channel));
			}
			@Override
			public void subscribed(String channel, long count) {
				System.out.println(String.format("subscribed:%s", channel));
			}
			@Override
			public void punsubscribed(String pattern, long count) {
			}
			@Override
			public void psubscribed(String pattern, long count) {
			}
			@Override
			public void message(String pattern, String channel, String message) {
			}
			@Override
			public void message(String channel, String message) {
				System.out.println(String.format("message:%s -> %s", channel, message));
			}
		});
		RedisPubSubCommands<String, String> sentinelPubSubCmd = sentinelPubSubConn.sync();
		sentinelPubSubCmd.subscribe("__sentinel__:hello");
		
		try {Thread.sleep(10000);} catch (InterruptedException e) {e.printStackTrace();}
		
		sentinelPubSubCmd.unsubscribe("__sentinel__:hello");
		
		//MasterReplica
		RedisClient client = RedisClient.create();
		ClientOptions options = ClientOptions.builder()
				.autoReconnect(true)	//是否自动重连
				.pingBeforeActivateConnection(true)	//连接激活之前是否执行PING命令
				.build();
		client.setOptions(options);
		StatefulRedisMasterReplicaConnection<String, String> conn = MasterReplica.connect(client, StringCodec.UTF8, sentinelUri);
		conn.setReadFrom(ReadFrom.ANY);		//设置可从任何一个服务读数据
		for (int i = 0; i < 5; i++) {
			try {
				RedisCommands<String, String> cmd = conn.sync();
				cmd.set("aa", String.valueOf(i));
				System.out.print(cmd.get("aa") + "--");
				Stream.of(cmd.info("server").split("\n")).filter(x -> x.startsWith("tcp_port")).forEach(System.out::print);
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}
 
}

运行结果：


————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/FlyLikeButterfly/article/details/124496285
```


```text
程序员自由之路
博客园首页标签归档联系订阅管理
Redis 客户端 Jedis、lettuce 和 Redisson 对比
Redis 支持多种语言的客户端，下面列举了部分 Redis 支持的客户端语言，大家可以通过官网查看 Redis 支持的客户端详情。

C语言
C++
C#
Java
Python
Node.js
PHP
Redis 是用单线程来处理多个客户端的访问，因此作为 Redis 的开发和运维人员需要了解 Redis 服务端和客户端的通信协议，以及主流编程语言的 Redis 客户端使用方法，同时还需要了解客户端管理的相应 API 以及开发运维中可能遇到的问题。

Redis 客户端通信协议#
Redis制定了RESP（Redis Serialization Protocol，Redis序列化协议）实现客户端与服务端的正常交互，这种协议简单高效，既能够被机器解析，又容易被人类识别。

RESP可以序列化不同的数据类型，如整型、字符串、数组还有一种特殊的Error类型。需要执行的Redis命令会封装为类似于字符串数组的请求然后通过Redis客户端发送到Redis服务端。Redis服务端会基于特定的命令类型选择对应的一种数据类型进行回复。

1. RESP 发送命令格式

在RESP中，发送的数据类型取决于数据报的第一个字节：

单行字符串的第一个字节为+。
错误消息的第一个字节为-。
整型数字的第一个字节为:。
定长字符串的第一个字节为$。
RESP数组的第一个字节为*。
数据类型	本文翻译名称	基本特征	例子
Simple String	单行字符串	第一个字节是+，最后两个字节是\r\n，其他字节是字符串内容	+OK\r\n
Error	错误消息	第一个字节是-，最后两个字节是\r\n，其他字节是异常消息的文本内容	-ERR\r\n
Integer	整型数字	第一个字节是:，最后两个字节是\r\n，其他字节是数字的文本内容	:100\r\n
Bulk String	定长字符串	第一个字节是$，紧接着的字节是内容字符串长度\r\n，最后两个字节是\r\n，其他字节是字符串内容	$4\r\ndoge\r\n
Array	RESP数组	第一个字节是*，紧接着的字节是元素个数\r\n，最后两个字节是\r\n，其他字节是各个元素的内容，每个元素可以是任意一种数据类型	*2\r\n:100\r\n$4\r\ndoge\r\n
发送的命令格式如下，CRLF代表"\r\n":

*<参数数量> CRLF
$<参数1的字节数量> CRLF
<参数1> CRLF
...
$<参数N的字节数量> CRLF
<参数N> CRLF
以set hello world这个命令为例，发送的内容就是这样的：

*3
$3
SET
$5
hello
$5
world
第一行*3表示有3个参数，
3
表
示
接
下
来
的
一
个
参
数
有
3
个
字
节
，
接
下
来
是
参
数
，
5表示下一个参数有5个字节，接下来是参数，$5表示下一个参数有5个字节，接下来是参数。

所以set hello world最终发送给redis服务器的命令是：

*3\r\n$3\r\nSET\r\n$5\r\nhello\r\n$5\r\nworld\r\n
2. RESP 响应内容

Redis的返回结果类型分为以下五种：
        正确回复：在RESP中第一个字节为"+"
        错误回复：在RESP中第一个字节为"-"
        整数回复：在RESP中第一个字节为":"
        字符串回复：在RESP中第一个字节为"$"
        多条字符串回复：在RESP中第一个字节为"*"

(+) 表示一个正确的状态信息，具体信息是当前行+后面的字符。
(-)  表示一个错误信息，具体信息是当前行－后面的字符。
(*) 表示消息体总共有多少行，不包括当前行,*后面是具体的行数。
($) 表示下一行数据长度，不包括换行符长度\r\n,$后面则是对应的长度的数据。
(:) 表示返回一个数值，：后面是相应的数字节符。




有了这个协议，我们就可以编写程序来和 Redis 服务端进行通信。由于 Redis 的流行，已经存在了很多流行的开源客户端。本文主要选择 Java 领域 Redis 官方推荐的客户端进行介绍。

Redis 的 Java 客户端#
Redis 官方推荐的 Java 客户端有Jedis、lettuce 和 Redisson。

1. Jedis

Jedis 是老牌的 Redis 的 Java 实现客户端，提供了比较全面的 Redis 命令的支持，其官方网址是：http://tool.oschina.net/uploads/apidocs/redis/clients/jedis/Jedis.html。

优点：

支持全面的 Redis 操作特性（可以理解为API比较全面）。
缺点：

使用阻塞的 I/O，且其方法调用都是同步的，程序流需要等到 sockets 处理完 I/O 才能执行，不支持异步；
Jedis 客户端实例不是线程安全的，所以需要通过连接池来使用 Jedis。
2. lettuce

lettuce （[ˈletɪs]），是一种可扩展的线程安全的 Redis 客户端，支持异步模式。如果避免阻塞和事务操作，如BLPOP和MULTI/EXEC，多个线程就可以共享一个连接。lettuce 底层基于 Netty，支持高级的 Redis 特性，比如哨兵，集群，管道，自动重新连接和Redis数据模型。lettuce 的官网地址是：https://lettuce.io/

优点：

支持同步异步通信模式；
Lettuce 的 API 是线程安全的，如果不是执行阻塞和事务操作，如BLPOP和MULTI/EXEC，多个线程就可以共享一个连接。
3. Redisson

Redisson 是一个在 Redis 的基础上实现的 Java 驻内存数据网格（In-Memory Data Grid）。它不仅提供了一系列的分布式的 Java 常用对象，还提供了许多分布式服务。其中包括( BitSet, Set, Multimap, SortedSet, Map, List, Queue, BlockingQueue, Deque, BlockingDeque, Semaphore, Lock, AtomicLong, CountDownLatch, Publish / Subscribe, Bloom filter, Remote service, Spring cache, Executor service, Live Object service, Scheduler service) Redisson 提供了使用Redis 的最简单和最便捷的方法。Redisson 的宗旨是促进使用者对Redis的关注分离（Separation of Concern），从而让使用者能够将精力更集中地放在处理业务逻辑上。Redisson的官方网址是：https://redisson.org/

优点：

使用者对 Redis 的关注分离，可以类比 Spring 框架，这些框架搭建了应用程序的基础框架和功能，提升开发效率，让开发者有更多的时间来关注业务逻辑；
提供很多分布式相关操作服务，例如，分布式锁，分布式集合，可通过Redis支持延迟队列等。
缺点：

Redisson 对字符串的操作支持比较差。
4. 使用建议

结论：lettuce + Redisson

Jedis 和 lettuce 是比较纯粹的 Redis 客户端，几乎没提供什么高级功能。Jedis 的性能比较差，所以如果你不需要使用 Redis 的高级功能的话，优先推荐使用 lettuce。

Redisson 的优势是提供了很多开箱即用的 Redis 高级功能，如果你的应用中需要使用到 Redis 的高级功能，建议使用 Redisson。具体 Redisson 的高级功能可以参考：https://redisson.org/

参考#
RESP协议1：https://www.cnblogs.com/4a8a08f09d37b73795649038408b5f33/p/9998245.html

RESP协议2：https://my.oschina.net/u/2474629/blog/913805

RESP协议3：https://www.cnblogs.com/throwable/p/11644790.html

Redis的三个框架：Jedis,Redisson,Lettuce：https://www.cnblogs.com/williamjie/p/11287292.html

redis客户端选型-Jedis、lettuce、Redisson：https://blog.csdn.net/a5569449/article/details/106891111/

作者：程序员自由之路

出处：https://www.cnblogs.com/54chensongxia/p/13815761.html

版权：本作品采用「署名-非商业性使用-相同方式共享 4.0 国际」许可协议进行许可。

```

```text
java 服务基于lettuce实现redis sentinel/cluster的高可用连接
分布式缓存服务Redis版
2023-04-26 22:49:20
216
0
背景
基于redis sentinel和cluster的高可用能力，和redis lettuce 驱动的高可用连接，实现java 服务对redis 异常的自恢复能力并测试

对lettuce的初步了解可以看文章《初探 Redis 客户端 Lettuce》

本次是基于lettuce   5.3.7.RELEASE 版本进行实现，其他版本可能存在实现差异

        <dependency>
            <groupId>io.lettuce</groupId>
            <artifactId>lettuce-core</artifactId>
            <version>5.3.7.RELEASE</version>
        </dependency>
目的
使用的lettuce Redis client实现高可用链接，保证lettuce Redis client配合Redis Server（sentinel/cluster）高可用能力，具备故障恢复能力，并得出故障恢复时长。

测试场景
准备redis cluster 和sentinel 环境，可以基于redis 4.0 进行部署，cluster最小部署需要三主三从，sentinel可以三个哨兵节点+1主1从节点

高可用测试场景:

1:Redis Cluster的主动切换主，加片，减片，主动kill 主，的故障恢复时间

2:Redis Sentinel的主动切换主，主动kill 主的故障恢复时间

高可用连接实现:
sentinel
核心部分在

SENTINEL分支，需要构建 StatefulRedisMasterReplicaConnection 链接
    /**
     * 客户端
     */
    protected RedisClient singleClient;

    /**
     * 同步操作
     */
    protected RedisCommands sync;

    /**
     * 异步，不自动提交连接
     */
    protected RedisAsyncCommands<String, String> pipleLine;

    private String type = SINGLE;

/**
     * 生成哨兵的URI
     *
     * @param nodes
     * @param password
     * @param timeOut
     * @param masterId
     * @return
     */
    protected RedisURI getSentinelURI(String nodes, String password, Integer timeOut, String masterId) {
        if (StringUtils.isEmpty(nodes)) {
            logger.error("lettuce nodes config is empty.");
            return null;
        }

        if (timeOut == null || timeOut < 500) {
            timeOut = 2000;
        }
        Duration timeout = Duration.ofMillis(timeOut.longValue());

        String[] ipPortArr = nodes.split(",", -1);
        RedisURI.Builder builder = null;
        for (String host : ipPortArr) {
            if (StringUtils.isEmpty(host)) {
                logger.error("lettuce config is wrong：" + host);
                return null;
            }
            final String[] ipPort = host.trim().split(":");
            //2个以上
            if (ipPort.length >= 2) {
                if (builder == null) {
                    builder = RedisURI.Builder.sentinel(ipPort[0], Integer.parseInt(ipPort[1]), masterId, password);
                } else {
                    builder.withSentinel(ipPort[0], Integer.parseInt(ipPort[1]), password);
                }
                //sentinel的情况，不需要build 出uri
                continue;
            }
        }
        builder.withTimeout(timeout);
        return builder.build();
    }

   protected void initSingle(String nodes, String password, Integer redisTimeout, String type, String masterId) {
        if (StringUtils.isBlank(nodes)) {
            logger.error("configMap is blank!");
            return;
        }
        if (redisTimeout == null || redisTimeout < 500) {
            redisTimeout = 2000;
        }
        if (init) {
            logger.info("redis config changged by " + nodes);
        }

        //lettuce 链接可以复用，如果不是做事务操作，可以都用一个连接客户端

        //以下取了2个连接
        //同步,异步，不自动提交，pipleLine
        //https://github.com/lettuce-io/lettuce-core/wiki/Master-Replica
        RedisURI redisURI;
        if (SENTINEL.equals(type)) {
            redisURI = getSentinelURI(nodes, password, redisTimeout, masterId);
            //同步链接
            StatefulRedisMasterReplicaConnection<String, String> masterReplicaConnect = MasterReplica.connect(RedisClient.create(), StringCodec.UTF8, redisURI);
            masterReplicaConnect.setReadFrom(ReadFrom.MASTER_PREFERRED);

            sync = masterReplicaConnect.sync();
            //异步的链接
            StatefulRedisMasterReplicaConnection<String, String> masterReplicaAsyncConnect = MasterReplica.connect(RedisClient.create(), StringCodec.UTF8, redisURI);
            masterReplicaAsyncConnect.setReadFrom(ReadFrom.MASTER_PREFERRED);
            pipleLine = masterReplicaAsyncConnect.async();
            //手工提交
            pipleLine.setAutoFlushCommands(false);
            //用以上方式链接，就不需要使用以下 client方式，满提供一下
            singleClient = (RedisClient) initClient(nodes, password, redisTimeout, type, masterId);
        } 
        init = true;

        RedisClient asyncClient = RedisClient.create();
      
    }
cluster
protected void initCluser(String nodes, String password,Integer redisTimeout)
    {
        if(StringUtils.isBlank(nodes))
        {
            logger.error("configMap is blank!");
            return ;
        }
        if(init)
        {
            logger.info("redis config changged by " + nodes);
        }
        //lettuce 链接可以复用，如果不是做事务操作，可以都用一个连接
        //客户端
        clusterClient = (RedisClusterClient)initClient(nodes,password,redisTimeout,type,null);
        //以下取了2个连接
        //同步
        sync = clusterClient.connect().sync();
        //异步，不自动提交，pipleLine
        //异步的一定要和同步链接区分，因为 tAutoFlushCommands 是共享的
        pipleLine = clusterClient.connect().async();
        pipleLine.setAutoFlushCommands(false);
        init = true;
    }

 /**
     * 初始化连接
     * 如果有密码，
     * 1：如果都一样，那么直接一个统一参数放入
     * 2：如果每个节点都不一样，那么放在端口后面，:隔开
     * redisNodes = 10.123.6.100:7000,10.123.6.100:7001,10.123.6.100:7002,10.123.6.100:7003,10.123.6.100:7004,10.123.6.100:7005
     * timeOut = 1500
     * maxRedirects = 3
     * 哨兵
     * 10.212.22.82:26379,10.212.22.83:26379,10.212.22.87:26379
     * masterId:要获取的redis的master
     */
    protected AbstractRedisClient initClient(String nodes, String password, Integer timeOut, String type, String masterId) {
        if (StringUtils.isEmpty(nodes)) {
            logger.error("lettuce nodes config is empty.");
            return null;
        }

        String[] ipPortArr = nodes.split(",", -1);
        List<RedisURI> redisURIs = new ArrayList<>();
        RedisURI.Builder builder = null;
        if (timeOut == null || timeOut < 500) {
            timeOut = 2000;
        }
        Duration timeout = Duration.ofMillis(timeOut.longValue());
        for (String host : ipPortArr) {
            if (StringUtils.isEmpty(host)) {
                logger.error("lettuce config is wrong：" + host);
                return null;
            }
            final String[] ipPort = host.trim().split(":");
            //2个以上
            if (ipPort.length >= 2) {
                if (SENTINEL.equals(type)) {
                    if (builder == null) {
                        builder = RedisURI.Builder.sentinel(ipPort[0], Integer.parseInt(ipPort[1]), masterId, password);
                    } else {
                        builder.withSentinel(ipPort[0], Integer.parseInt(ipPort[1]), password);
                    }
                    //sentinel的情况，不需要build 出uri
                    continue;
                } else {
                    builder = RedisURI.Builder.redis(ipPort[0], Integer.parseInt(ipPort[1]));
                }
            }

            if (StringUtils.isNotBlank(password)) {
                builder.withPassword(password);
            }
            builder.withTimeout(timeout);
            redisURIs.add(builder.build());
        }

        //哨兵的情况
        if (redisURIs.size() == 0) {
            redisURIs.add(builder.build());
        }

        AbstractRedisClient client = null;

        //集群
        if (redisURIs.size() > 1) {
            client = getClusterClient(redisURIs);
        }
        //单节点也有代理的cluster,比如腾讯云的集群版本,还有哨兵
        else {
            if (CLUSTER.equals(type)) {
                client = getClusterClient(redisURIs);
            } else {
                //哨兵也可以用这个方式获取master
                client = RedisClient.create(redisURIs.get(0));
            }
        }

        client.setDefaultTimeout(timeout);
        return client;
    }


private RedisClusterClient getClusterClient(List<RedisURI> redisURIs) {
        //cluster 节点变动自动刷新拓扑 https://blog.csdn.net/u010046887/article/details/106948341
        //开启 自适应集群拓扑刷新和周期拓扑刷新
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                // 开启全部自适应刷新
                .enableAllAdaptiveRefreshTriggers() // 开启自适应刷新,自适应刷新不开启,Redis集群变更时将会导致连接异常
                // 自适应刷新超时时间(默认30秒) 适配cluster 异常恢复时间 15秒
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(30)) //默认关闭开启后时间为30秒
                // 开周期刷新，因为server的 主kill 后，需要最块15秒恢复，这里采样刷新频率是 信号的2倍即可，<7.5秒一次
                .enablePeriodicRefresh(Duration.ofSeconds(7))  // 默认关闭开启后时间为60秒 ClusterTopologyRefreshOptions.DEFAULT_REFRESH_PERIOD 60  .enablePeriodicRefresh(Duration.ofSeconds(2)) = .enablePeriodicRefresh().refreshPeriod(Duration.ofSeconds(2))
                .build();
        ClusterClientOptions clientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions)
//                .maxRedirects(5) //默认就是5次
                .build();

        RedisClusterClient client = RedisClusterClient.create(redisURIs);
        client.setOptions(clientOptions);
        return client;
    }
 核心部分在 getClusterClient 方法里，需要 使用 ClusterTopologyRefreshOptions 配置自适应刷新cluster的拓扑信息，构建 RedisClusterClient。

测试代码
1：客户端初始化

600ms 超时,如果有密码，自行配置上

private String sentinelNodes = "ip1:port1,ip2:port2,ip3:port3";
private String sentinelMaster = "masterName";

private String clusterNodes = "ip1:port1,ip2:port2,ip3:port3,ip4:port4,ip5:port5,ip6:port6";
@Bean
public RedisCluster initCluserClient() {
return new RedisCluster(clusterNodes,600); }
/**
* 初始化一个Sentinel 的master client * @return
*/
@Bean
public RedisSentinel initSentinelClient() {
return new RedisSentinel(sentinelNodes,sentinelMaster,600); }
2：测试逻辑

开启2个线程，分别使用cluster和sentinel无限循环while(true)执行读写动作，其中cluster的key是随机生成,其中taskScheduler 是spring 的定时任务线程池。

@PostConstruct
    public void initService() {
        LOGGER.info("initService hello"); //10秒触发一次
         taskScheduler.schedule(new Runnable() {
        @Override
        public void run() {
            ApiAppBaseInfoVo baseInfoVo = new ApiAppBaseInfoVo(); baseInfoVo.setAppCode("testCode"); baseInfoVo.setName("testName"); baseInfoVo.setAppKey("key"+RandomUtils.nextInt()); baseInfoVo.setDescription("testAAA");
            while(true) {
                try {
                    long start = System.currentTimeMillis();
                    String Key = (RandomUtils.nextInt() % 100) + "testCluster";
                    String clusterRe = redisCluster.set(Key,baseInfoVo,10L);
                    LOGGER.info("while redisCluser set :"+clusterRe + " use:"+ (System.currentTimeMillis()-start)+" ms"); ApiAppBaseInfoVo clusterVAlue = redisCluster.get(Key,ApiAppBaseInfoVo.class);
                    start = System.currentTimeMillis();
                    LOGGER.info("while redisCluser get use:"+ (System.currentTimeMillis()-start)+" ms, value:" + clusterVAlue);
                }catch (Exception e) {
                    LOGGER.error("redisCluser test error:"+e.getMessage()); }
            } }
        },new Date(System.currentTimeMillis() + 10000));
        //10秒触发一次
       taskScheduler.schedule(new Runnable() {
        @Override
        public void run() {
            while(true) {
                try {
                    long start = System.currentTimeMillis();
                    String clusterRe = redisSentinel.getCMD().set("testSentinel", RandomUtils.nextInt()+"-hello",
                            SetArgs.Builder.ex(10));
                    LOGGER.info("while redisSentinel set :"+clusterRe + " use:"+ (System.currentTimeMillis()-start)+" ms");
                    start = System.currentTimeMillis();
                    String sentinelVAlue = redisSentinel.get("testSentinel");
                    LOGGER.info("while redisSentinel get use:"+ (System.currentTimeMillis()-start)+" ms, value:" + sentinelVAlue);
                }catch (Exception e) {
                    LOGGER.error("redisSentinel test error:" + e.getMessage()); }
                }
            }
        },new Date(System.currentTimeMillis() + 10000));
    }
正常运行，截取1秒内日志简单分析，哨兵是 1302 QPS，集群是 1372 QPS



模拟redis异常
一：redis cluster
1：切换主从

业务运行无异常，QPS正常

2：加片减片

    加片/减片操作 需要人工分钟级操作，但是业务端没有发现错误日志，都正常读写

3：突发异常场景
把redis cluster 直接kill -9其中一个主节点，(15~25秒恢复)

这里依赖redis cluster serve的主从异常超时设置，这里设置的15秒，说明server端最快15秒发现主异常，进行切换。client端最快就是15秒自动恢复



二：redis sentinel
1：主动维护场景

人工操作切换主从：从日志出现异常到恢复，8秒多



2：突发异常场景

kill -9 Redis 主节点，需要25秒恢复

这里依赖redis sentinel的主从异常超时设置，这里设置的20秒，说明server端最快20秒发现主异常，进行切换。client端最快就是20秒自动 恢复



 小结
redis cluster的高可用自恢复能力更好

 	redis cluster（超时配置15秒）	redis sentinel（超时配置20秒）
人工维护:主从切换	无异常	8秒+
人工维护:加片减片	无异常	不涉及
突发异常:kill -9 主

15~25秒（取决于redis cluster 超时配置和client刷新拓扑间隔时间配置）	20~25秒（取决于redis sentinel 超时配置和client 刷新间隔时间配置）
版权声明：本文内容系天翼云实
```

```text

redis 哨兵是客户端如何连接
飞飞 1年前 其他 150
回复 共3条回复我来回复
worktile的头像
worktile
Worktile官方账号
Redis 哨兵系统是用于监控和管理 Redis 主从复制架构的解决方案。哨兵系统由一个或多个哨兵节点组成，这些节点负责监控 Redis 主节点和从节点的状态，并在主节点故障时自动进行故障转移。

那么，客户端如何连接 Redis 哨兵系统呢？

配置哨兵节点信息
首先，客户端需要配置连接哨兵节点的信息。在连接之前，我们需要知道哨兵节点的 IP 地址和端口号。一般情况下，哨兵节点的默认端口号是26379，但也可以根据实际情况进行配置。

创建 Redis 哨兵连接
客户端可以使用 Redis 客户端库来创建与哨兵系统的连接。大多数编程语言都提供了 Redis 客户端库，如 Redis-Py（Python）、Jedis（Java）等。

在创建连接时，客户端需要传入哨兵节点的 IP 地址和端口号，并指定要连接的 Redis 哨兵名称。

获取 Redis 主节点信息
客户端与 Redis 哨兵系统建立连接后，可以通过发送命令获取 Redis 主节点的信息。客户端可以发送 "SENTINEL get-master-addr-by-name " 命令给哨兵节点，其中 为 Redis 主节点的名称。响应结果会返回 Redis 主节点的 IP 地址和端口号。

连接 Redis 主节点
客户端通过获取到的主节点 IP 地址和端口号，可以使用 Redis 客户端库创建与主节点的连接。这样客户端就可以像普通的 Redis 客户端一样与主节点进行通信了。

需要注意的是，由于哨兵系统在主从切换时会通知客户端新的主节点信息，所以客户端需要具备重新连接和更新主节点信息的能力。

总之，客户端连接 Redis 哨兵系统的过程主要包括配置哨兵节点信息、创建连接、获取主节点信息和连接主节点。通过这些步骤，客户端可以实现与 Redis 哨兵系统的连接，并进行数据操作。

赞同

1年前
0条评论
飞飞的头像
飞飞
Worktile&PingCode市场小伙伴
当使用Redis Sentinel实现哨兵模式时，客户端如何连接Redis实例主要有两种方式：直接连接和使用哨兵连接。

直接连接：
客户端可以通过直接连接到Redis实例来进行读写操作，这种方式适用于单机部署的情况，不需要使用哨兵模式进行故障转移和自动切换。
示例代码（使用Redis的Python客户端redis-py）:

import redis

redis_client = redis.Redis(host='localhost', port=6379, db=0)
在这种方式下，客户端需要明确指定Redis实例的地址和端口进行连接。

使用哨兵连接：
当使用哨兵模式部署Redis实例时，客户端可以利用哨兵进行连接和故障切换。通过哨兵连接，客户端可以自动获取当前Redis实例的主节点地址，并且在主节点故障时自动切换到备用节点。
示例代码（使用redis-py）：

import redis.sentinel

sentinel = redis.sentinel.Sentinel([('sentinel1.example.com', 26379), ('sentinel2.example.com', 26379), ('sentinel3.example.com', 26379)], socket_timeout=0.1)
redis_master = sentinel.master_for('mymaster', socket_timeout=0.1)

# 通过redis_master进行读写操作
redis_master.set('key', 'value')
value = redis_master.get('key')
在上述代码中，我们通过Sentinel实例(sentinel)来获取Redis实例的主节点(redis_master)，然后就可以使用redis_master对象进行读写操作。

需要注意的是，在使用哨兵连接时，我们需要提供一个包含哨兵节点地址的列表（比如sentinel1.example.com:26379），以及指定连接的master的名字。

总结起来，客户端可以通过直接连接或使用哨兵连接来连接Redis实例，直接连接适用于单机部署，而使用哨兵连接适用于哨兵模式的多节点部署，可以实现故障转移和自动切换。

赞同

1年前
0条评论
不及物动词的头像
不及物动词
这个人很懒，什么都没有留下～
评论
Redis Sentinel（哨兵）是一个用于高可用性场景的 Redis 系统，它是一个分布式系统，用于监控和管理 Redis 主从节点的状态。它可以自动进行故障检测、故障转移和配置更新等操作。在客户端连接 Redis Sentinel 时，需要遵循以下步骤：

获取 Redis Sentinel 的地址：
在 Redis 哨兵模式下，通常有多个 Sentinel 实例组成一个 Sentinel 集群。客户端需要获取 Sentinel 集群中任一 Sentinel 节点的地址。

连接 Sentinel 节点：
客户端使用 Redis 客户端库来连接 Sentinel 节点，比如 Redis-Py、Jedis 等。客户端通过提供 Sentinel 节点的地址和端口来建立连接。

获取 Redis 主节点信息：
客户端调用 Sentinel 提供的命令，比如 SENTINEL get-master-addr-by-name，来获取 Redis 主节点的地址和端口。

连接 Redis 主节点：
客户端使用 Redis 客户端库来连接 Redis 主节点，通过提供主节点的地址和端口来建立连接。

发送命令和接收响应：
客户端通过与 Redis 主节点建立的连接来发送命令和接收响应。常见的命令包括 GET、SET、DEL 等。

处理故障转移：
如果 Sentinel 检测到主节点宕机或不可用，会自动选举一个新的主节点，并将该信息广播给客户端。客户端需要重新连接新的主节点，以继续执行命令。

断开连接：
当客户端不需要再与 Redis 服务器进行通信时，可以关闭连接，以释放资源。

需要注意的是，客户端需要定时查询 Sentinel 来获取主节点的地址信息，以保持对 Redis 集群的连接。此外，当 Sentinel 集群发生故障、配置更新等情况时，客户端需要相应地处理并重新连接。

```

```text
kubernetes 集群离线部署原创
发布于 2022-11-16 15:59:00
4K12
代码可运行
举报
文章被收录于专栏：
奶盖笔记
关联问题
换一批
 Kubernetes集群离线部署有哪些关键步骤？
 离线部署Kubernetes集群时如何处理镜像拉取问题？
 Kubernetes集群离线部署中如何配置网络插件？
      本文主要阐述在生产环境不可连接互联网的情况下如何离线搭建K8S 集群。

1.离线安装包准备
    下载kubelet，kubectl ，kubeadm 安装包
    在可联网的linux 环境执行以下命令，查看可下载的kubelet版本

代码语言：javascript代码运行次数：0
运行
AI代码解释
yum list kubelet --showduplicates | sort -r    
以下载1.18.8 版本为例

代码语言：javascript代码运行次数：0
运行
AI代码解释
yum install --downloadonly --downloaddir=/home/centos/k8s kubelet-1.18.8 kubeadm-1.18.8 kubectl-1.18.8
获取镜像列表
代码语言：javascript代码运行次数：0
运行
AI代码解释
kubeadm config images list
代码语言：javascript代码运行次数：0
运行
AI代码解释
k8s.gcr.io/kube-apiserver:v1.18.8
k8s.gcr.io/kube-controller-manager:v1.18.8
k8s.gcr.io/kube-scheduler:v1.18.8
k8s.gcr.io/kube-proxy:v1.18.8
k8s.gcr.io/pause:3.2
k8s.gcr.io/etcd:3.4.3-0
k8s.gcr.io/coredns:1.6.7
通过外部联网环境拉去docker 镜像,下载示例

代码语言：javascript代码运行次数：0
运行
AI代码解释
docker pull k8s.gcr.io/kube-apiserver:v1.18.8
保存镜像

代码语言：javascript代码运行次数：0
运行
AI代码解释
docker save -o  kube-apiserver.tar k8s.gcr.io/kube-apiserver:v1.18.8
镜像传输到离线环境

代码语言：javascript代码运行次数：0
运行
AI代码解释
docker load -i kube-apiserver.tar
下载docker 安装包
下载离线安装包https://download.docker.com/linux/centos/7/x86_64/stable/Packages/

或者使用命令下载rpm 包

代码语言：javascript代码运行次数：0
运行
AI代码解释
yum install --downloadonly --downloaddir=./  docker-ce docker-ce-cli-y
    下载docker 私有仓库register
1.在有外网环境的docker中下载镜像

代码语言：javascript代码运行次数：0
运行
AI代码解释
docker pull registry:2
2.从image导出镜像

代码语言：javascript代码运行次数：0
运行
AI代码解释
docker save -o registry.tar registry:2
3.上传registry.tar到离线服务器，导入

代码语言：javascript代码运行次数：0
运行
AI代码解释
docker load -i registry.tar
2.离线包安装
docker 安装
docker-ce-cli-18.09.7-3.el7.x86_64.rpm

docker-ce-18.09.7-3.el7.x86_64.rpm

container-selinux-2.107-1.el7_6.noarch.rpm

containerd.io-1.2.2-3.el7.x86_64.rpm

上传到指定服务器，安装命令  

代码语言：javascript代码运行次数：0
运行
AI代码解释
rpm -ivh *.rpm
docker info 查看docker信息

修改cgoupdriver为systemd与k8b保持一致，

代码语言：javascript代码运行次数：0
运行
AI代码解释
vim /etc/docker/daemon.json
进行以下修改

代码语言：javascript代码运行次数：0
运行
AI代码解释
{

  "registry-mirrors": ["https://registry.docker-cn.com"],

  "exec-opts": ["native.cgroupdriver=systemd"]

}
代码语言：javascript代码运行次数：0
运行
AI代码解释
systemctl  daemon-reload
启动docker

代码语言：javascript代码运行次数：0
运行
AI代码解释
systemctl  restart docker
设置开机启动  

代码语言：javascript代码运行次数：0
运行
AI代码解释
systemctl enabel docker
docker 仓库安装
启动register

代码语言：javascript代码运行次数：0
运行
AI代码解释
docker run -d -v /registry:/var/lib/registry -p 5000:5000 --restart=always --name registry registry:2
修改k8s集群节点的dokcer daemon.json  支持https

代码语言：javascript代码运行次数：0
运行
AI代码解释
{
  "registry-mirrors": ["https://registry.docker-cn.com"],
  "exec-opts": ["native.cgroupdriver=systemd"],
  "insecure-registries": ["10.209.68.12:5000"]
}
重新加载配置

代码语言：javascript代码运行次数：0
运行
AI代码解释
systemctl  daemon-reload
代码语言：javascript代码运行次数：0
运行
AI代码解释
systemctl  restart docker
安装K8S
环境准备
关闭防火墙
代码语言：javascript代码运行次数：0
运行
AI代码解释
systemctl disable firewalld.service
systemctl stop firewalld.service
设置时间同步 
代码语言：javascript代码运行次数：0
运行
AI代码解释
yum install ntpdate ‐y 
ntpdate time.windows.com
关闭swap
代码语言：javascript代码运行次数：0
运行
AI代码解释
swapoff -a  #临时关闭
代码语言：javascript代码运行次数：0
运行
AI代码解释
vim /etc/fstab   #永久关闭 
#注释掉swap这行

# /dev/mapper/centos‐swap swap swap defaults 0 0

systemctl reboot#重启生效

free ‐m  #查看下swap交换区是否都为0，如果都为0则swap关闭成功

关闭selinux
代码语言：javascript代码运行次数：0
运行
AI代码解释
sed ‐i "s/enforcing/disabled/" /etc/selinux/config  #永久关闭
允许 iptables 检查桥接流量
确保 br_netfilter 模块被加载。这一操作可以通过运行 lsmod | grep br_netfilter 来完成。若要显式加载该模块，可执行 sudo modprobe br_netfilter。

为了让你的 Linux 节点上的 iptables 能够正确地查看桥接流量，你需要确保在你的 sysctl 配置中将 net.bridge.bridge-nf-call-iptables 设置为 1。例如：

代码语言：javascript代码运行次数：0
运行
AI代码解释
cat <<EOF | sudo tee /etc/modules-load.d/k8s.conf
br_netfilter
EOF

cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sudo sysctl --system
安装 kubeadm、kubelet 和 kubectl 本地安装包
代码语言：javascript代码运行次数：0
运行
AI代码解释
yum localinstall *.rpm
初始化控制平面节点（mater）
代码语言：javascript代码运行次数：0
运行
AI代码解释
kubeadm init <args>

--kubernetes-version v1.18.8 指定版本
--apiserver-advertise-address 为通告给其它组件的IP，一般应为master节点的IP地址
--service-cidr 指定service网络，不能和node网络冲突
--pod-network-cidr 指定pod网络，不能和node网络、service网络冲突
--image-repository registry.aliyuncs.com/google_containers 指定镜像源，由于默认拉取镜像地址k8s.gcr.io国内无法访问，这里指定阿里云镜像仓库地址。
代码语言：javascript代码运行次数：0
运行
AI代码解释
kubeadm init --apiserver-advertise-address=192.168.17.149--image-repository registry.aliyuncs.com/google_containers --kubernetes-version v1.18.8--pod-network-cidr=10.10.0.0/16--service-cidr=10.20.0.0/16
要使非 root 用户可以运行 kubectl，请运行以下命令， 它们也是 kubeadm init 输出的一部分：

代码语言：javascript代码运行次数：0
运行
AI代码解释
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
或者，如果你是 root 用户，则可以运行：

代码语言：javascript代码运行次数：0
运行
AI代码解释
export KUBECONFIG=/etc/kubernetes/admin.conf
#查看kubectl是否能正常使用

代码语言：javascript代码运行次数：0
运行
AI代码解释
kubectl get nodes
安装 Pod 网络附加组件（master）
每个集群只能安装一个 Pod 网络。

安装 Pod 网络后，您可以通过在 kubectl get pods --all-namespaces 输出中检查 CoreDNS Pod 是否 Running 来确认其是否正常运行。 一旦 CoreDNS Pod 启用并运行，你就可以继续加入节点。

安装POD network Flannel(DamonSet)

代码语言：javascript代码运行次数：0
运行
AI代码解释
wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml

复制

修改kube-flannel.yml

更改 128行的网络配置，要和 pod-network-cidr保持一致

cat kube-flannel.yml | grep Network

代码语言：javascript代码运行次数：0
运行
AI代码解释
kubectl  apply -f kube-flannel.yaml
如果您的网络无法正常工作或 CoreDNS 不在“运行中”状态，请查看kubeadm的故障排除指南。

代码语言：javascript代码运行次数：0
运行
AI代码解释
kubectl -n kube-system get pods -o wide   #查看是否创建成功
加入节点（NODE执行）
NODE 节点也需要安装 kubeadm、kubelet 和 kubectl 

代码语言：javascript代码运行次数：0
运行
AI代码解释
kubeadm join 192.168.17.149:6443--token gfs8qb.shcvgzxrcflaw19m \     --discovery-token-ca-cert-hash sha256:a47db38d409c8135969aeee18b1371230dfabfd8fed860691b02f717543a8f85 

```

```text
Kubernetes(一) 跟着官方文档从零搭建K8S
2019-09-17
70,250
阅读4分钟

Kubernetes教程之跟着官方文档从零搭建K8S

文章地址: blog.piaoruiqing.com/2019/09/17/…

前言
本文将带领读者一起, 参照着Kubernetes官方文档, 对其安装部署进行讲解. Kubernetes更新迭代很快, 书上、网上等教程可能并不能适用于新版本, 但官方文档能.

阅读这篇文章你能收获到:

如何阅读Kubernetes官方安装指南并搭建一个Kubernetes环境.
Kubernetes安装过程中的注意事项.
避过常见的坑.
阅读本文你需要:

熟悉Linux命令.
知道Kubernetes是用来干什么的 (不然装它干啥(ಥ_ಥ)).
知道Docker
器材准备
文档链接: Before you begin

序号	名称	数量	备注
1	服务器	2	操作系统: Linux(centos7, 其它操作系统也可, 安装过程类似, 可参考官方文档)
机器配置: CPU >= 2, 内存 >= 2G
从官网找到kubeadm安装文档入口, 文档很详细. 英文阅读没有障碍的读者推荐直接查看英文文档, 中文文档不全且更新不及时安装时可能存在问题.


前期准备
笔者已经预先安装好了两台虚拟机, centos7(CPUx2, 内存2.5G). 并在路由器上固定了这两个虚拟机的IP地址.


修改hostname
[root@k8s-master ~]$ vim /etc/hostname # 修改hostname
[root@k8s-master ~]$ vim /etc/hosts	# 将本机IP指向hostname
[root@k8s-master ~]$ reboot -h 		# 重启(可以做完全部前期准备后再重启)
修改后, 两台虚拟机的配置如下:

# in k8s-master
[root@k8s-master ~]$ cat /etc/hostname 
k8s-master
[root@k8s-master ~]$ cat /etc/hosts | grep k8s
10.33.30.92 k8s-master
10.33.30.91 k8s-worker

# in k8s-worker
[root@k8s-worker ~]$ cat /etc/hostname 
k8s-worker
[root@k8s-worker ~]$ cat /etc/hosts | grep k8s
10.33.30.92 k8s-master
10.33.30.91 k8s-worker
确认MAC和product_uuid的唯一性
文档链接: Verify the MAC address and product_uuid are unique for every node

[root@k8s-master ~]$ ifconfig -a    # 查看MAC
[root@k8s-master ~]$ cat /sys/class/dmi/id/product_uuid	# 查看product_uuid
注: 如果你的centos7没有ifconfig命令, 可以执行yum install net-tools进行安装.

配置防火墙
文档链接: Check required ports

由于是本地内网测试环境, 笔者图方便, 直接关闭了防火墙. 若安全要求较高, 可以参考官方文档放行必要端口.

[root@k8s-master ~]$ systemctl stop firewalld	# 关闭服务
[root@k8s-master ~]$ systemctl disable firewalld	# 禁用服务
禁用SELinux
文档链接: coredns pods have CrashLoopBackOff or Error state

修改/etc/selinux/config, 设置SELINUX=disabled. 重启机器.

[root@k8s-master ~]$ sestatus	# 查看SELinux状态
SELinux status: disabled
禁用交换分区
文档链接: Before you begin

Swap disabled. You MUST disable swap in order for the kubelet to work properly.

编辑/etc/fstab, 将swap注释掉. 重启机器.

[root@k8s-master ~]$ vim /etc/fstab 
#/dev/mapper/cl-swap     swap                    swap    defaults        0 0
安装Docker
文档链接: Get Docker Engine - Community for CentOS

Docker官方文档对安装步骤描述已经足够详细, 过程并不复杂, 本文便不再赘述.

Docker请使用18.09, k8s暂不支持Docker最新版19.x, 安装时请按照文档描述的方式明确指定版本号yum install docker-ce-18.09.9-3.el7 docker-ce-cli-18.09.9-3.el7 containerd.io.

若网络不好, 可换用国内源, 阿里云、中科大等都可. 此处附上阿里云源docker安装文档地址: 容器镜像服务.

安装完毕后, 建议将docker源替换为国内. 推荐阿里云镜像加速, 有阿里云账号即可免费使用.阿里云 -> 容器镜像服务 -> 镜像中心 -> 镜像加速

配置Docker

文档地址: Container runtimes

修改/etc/docker/daemon.json为如下内容:

{
  "registry-mirrors": ["https://xxxxxxxx.mirror.aliyuncs.com"],
  "exec-opts": ["native.cgroupdriver=systemd"],
  "log-driver": "json-file",
  "log-opts": {
    "max-size": "100m"
  },
  "storage-driver": "overlay2"
}
其中https://xxxxxxxx.mirror.aliyuncs.com为阿里云镜像加速地址, xxxxxxxx需要替换为自己账户中的地址. 如图:

安装配置完毕后执行:

[root@k8s-master ~]$ systemctl enable docker
[root@k8s-master ~]$ systemctl start docker
安装Kubernetes
文档地址: Installing kubeadm, kubelet and kubectl

添加源
由于国内网络原因, 官方文档中的地址不可用, 本文替换为阿里云镜像地址, 执行以下代码即可:

cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=http://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=http://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg http://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
exclude=kube*
EOF
安装
[root@k8s-master ~]$ yum install -y kubelet kubeadm kubectl --disableexcludes=kubernetes
[root@k8s-master ~]$ systemctl enable kubelet && systemctl start kubelet
修改网络配置
cat <<EOF >  /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sysctl --system
注意: 至此, 以上的全部操作, 在Worker机器上也需要执行. 注意hostname等不要相同.

初始化Master
生成初始化文件
[root@k8s-master ~]$ kubeadm config print init-defaults > kubeadm-init.yaml
该文件有两处需要修改:

将advertiseAddress: 1.2.3.4修改为本机地址
将imageRepository: k8s.gcr.io修改为imageRepository: registry.cn-hangzhou.aliyuncs.com/google_containers
修改完毕后文件如下:

apiVersion: kubeadm.k8s.io/v1beta2
bootstrapTokens:
- groups:
  - system:bootstrappers:kubeadm:default-node-token
  token: abcdef.0123456789abcdef
  ttl: 24h0m0s
  usages:
  - signing
  - authentication
kind: InitConfiguration
localAPIEndpoint:
  advertiseAddress: 10.33.30.92
  bindPort: 6443
nodeRegistration:
  criSocket: /var/run/dockershim.sock
  name: k8s-master
  taints:
  - effect: NoSchedule
    key: node-role.kubernetes.io/master
---
apiServer:
  timeoutForControlPlane: 4m0s
apiVersion: kubeadm.k8s.io/v1beta2
certificatesDir: /etc/kubernetes/pki
clusterName: kubernetes
controllerManager: {}
dns:
  type: CoreDNS
etcd:
  local:
    dataDir: /var/lib/etcd
imageRepository: registry.cn-hangzhou.aliyuncs.com/google_containers
kind: ClusterConfiguration
kubernetesVersion: v1.15.0
networking:
  dnsDomain: cluster.local
  serviceSubnet: 10.96.0.0/12
scheduler: {}
下载镜像
[root@k8s-master ~]$ kubeadm config images pull --config kubeadm-init.yaml
[版权声明]
本文发布于朴瑞卿的博客, 允许非商业用途转载, 但转载必须保留原作者朴瑞卿 及链接:blog.piaoruiqing.com. 如有授权方面的协商或合作, 请联系邮箱: piaoruiqing@gmail.com.
执行初始化
[root@k8s-master ~]$ kubeadm init --config kubeadm-init.yaml
等待执行完毕后, 会输出如下内容:

...
Your Kubernetes control-plane has initialized successfully!
...
Then you can join any number of worker nodes by running the following on each as root:

kubeadm join 10.33.30.92:6443 --token abcdef.0123456789abcdef \
    --discovery-token-ca-cert-hash sha256:2883b1961db36593fb67ab5cd024f451b934fc0e72e2fa3858dda3ad3b225837 
最后两行需要保存下来, kubeadm join ...是worker节点加入所需要执行的命令.

接下来配置环境, 让当前用户可以执行kubectl命令:

mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
测试一下: 此处的NotReady是因为网络还没配置.

[root@k8s-master kubernetes]$ kubectl get node
NAME         STATUS     ROLES    AGE     VERSION
k8s-master   NotReady   master   3m25s   v1.15.3
配置网络
文档地址: Instructions

下载描述文件

[root@k8s-master ~]$ wget https://docs.projectcalico.org/v3.8/manifests/calico.yaml
[root@k8s-master ~]$ cat kubeadm-init.yaml | grep serviceSubnet:
serviceSubnet: 10.96.0.0/12
打开calico.yaml, 将192.168.0.0/16修改为10.96.0.0/12

需要注意的是, calico.yaml中的IP和kubeadm-init.yaml需要保持一致, 要么初始化前修改kubeadm-init.yaml, 要么初始化后修改calico.yaml.

执行kubectl apply -f calico.yaml初始化网络.

此时查看node信息, master的状态已经是Ready了.

[root@k8s-master ~]$ kubectl get node
NAME         STATUS   ROLES    AGE   VERSION
k8s-master   Ready    master   15m   v1.15.3
安装Dashboard
文档地址: Web UI (Dashboard)

部署Dashboard
文档地址: Deploying the Dashboard UI

[root@k8s-master ~]$ wget https://raw.githubusercontent.com/kubernetes/dashboard/v2.0.0-beta4/aio/deploy/recommended.yaml
[root@k8s-master ~]$ kubectl apply -f recommended.yaml 
部署完毕后, 执行kubectl get pods --all-namespaces查看pods状态

[root@k8s-master kubernetes]$ kubectl get pods --all-namespaces | grep dashboard
NAMESPACE              NAME                                        READY   STATUS   
kubernetes-dashboard   dashboard-metrics-scraper-fb986f88d-m9d8z   1/1     Running
kubernetes-dashboard   kubernetes-dashboard-6bb65fcc49-7s85s       1/1     Running 
创建用户
文档地址: Creating sample user

创建一个用于登录Dashboard的用户. 创建文件dashboard-adminuser.yaml内容如下:

apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: admin-user
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: admin-user
  namespace: kube-system
执行命令kubectl apply -f dashboard-adminuser.yaml.

生成证书
文档地址: Accessing Dashboard 1.7.X and above

官方文档中提供了登录1.7.X以上版本的登录方式, 但并不清晰, 笔者没有完全按照该文档的方式进行操作.

[root@k8s-master ~]$ grep 'client-certificate-data' ~/.kube/config | head -n 1 | awk '{print $2}' | base64 -d >> kubecfg.crt
[root@k8s-master ~]$ grep 'client-key-data' ~/.kube/config | head -n 1 | awk '{print $2}' | base64 -d >> kubecfg.key
[root@k8s-master ~]$ openssl pkcs12 -export -clcerts -inkey kubecfg.key -in kubecfg.crt -out kubecfg.p12 -name "kubernetes-client"
第三条命令生成证书时会提示输入密码, 可以直接两次回车跳过.

kubecfg.p12即需要导入客户端机器的证书. 将证书拷贝到客户端机器上, 导入即可.

~$ scp root@10.33.30.92:/root/.kube/kubecfg.p12 ./
需要注意的是: 若生成证书时跳过了密码, 导入时提示填写密码直接回车即可, 不要纠结密码哪来的 (ﾟ▽ﾟ)/
此时我们可以登录面板了, 访问地址: https://{k8s-master-ip}:6443/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/#/login, 登录时会提示选择证书, 确认后会提示输入当前用户名密码(注意是电脑的用户名密码).



登录Dashboard
文档地址:Bearer Token

执行kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | grep admin-user | awk '{print $1}'), 获取Token.

[root@k8s-master .kube]$ kubectl -n kube-system describe secret $(kubectl -n kube-system get secret | grep admin-user | awk '{print $1}')
Name:         admin-user-token-dhhkb
Namespace:    kube-system
Labels:       <none>
Annotations:  kubernetes.io/service-account.name: admin-user
              kubernetes.io/service-account.uid: b20d1143-ce94-4379-9e14-8f80f06d8479

Type:  kubernetes.io/service-account-token

Data
====
ca.crt:     1025 bytes
namespace:  11 bytes
token:      eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJhZG1pbi11c2VyLXRva2VuLWRoaGtiIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImFkbWluLXVzZXIiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiJiMjBkMTE0My1jZTk0LTQzNzktOWUxNC04ZjgwZjA2ZDg0NzkiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06YWRtaW4tdXNlciJ9.f6IbPGwIdFZWStzBj8_vmF01oWW5ccaCpPuVQNLSK1pgEqn0kNVK_x0RYSuKEnujObzpQQdFiRYcI6ITHja2PIVc5Nv83VCn5IaLvZdYuGZWUYRw0efJUBMA4J4N8-pRkiw6fYAuWLeGYghLNXL_nDdC_JkG75ASqrr3U1MVaikOcfrEPaI-T_AJ3TMYhI8aFoKiERpumu5W1K6Jl80Am9pWDX0Ywis5SSUP1VYfu-coI48EXSptcaxEyv58PrHUd6t_oMVV9rpqSxrNtMZvMeXqe8Hnl21vR7ls5yTZegYtHXSc3PKvCaIalKhYXAuhogNcIXHaMzvLSbf-DSQkVw
复制该Token到登录页, 点击登录即可, 效果如下:


添加Worker节点
重复执行 前期准备-修改hostname ~ 安装Kubernetes-修改网络配置的全部操作, 初始化一个Worker机器.

执行如下命令将Worker加入集群:

kubeadm join 10.33.30.92:6443 --token abcdef.0123456789abcdef \
    --discovery-token-ca-cert-hash sha256:2883b1961db36593fb67ab5cd024f451b934fc0e72e2fa3858dda3ad3b225837 
注意: 此处的秘钥是初始化Master后生成的, 参考前文.
添加完毕后, 在Master上查看节点状态:

[root@k8s-master ~]$ kubectl get node
NAME         STATUS   ROLES    AGE   VERSION
k8s-master   Ready    master   10h   v1.15.3
k8s-worker   Ready    <none>   96s   v1.15.3
在面板上也可查看:


如果这篇文章对您有帮助，请点个赞吧 (￣▽￣)"

参考文献
kubernetes.io
github.com/kubernetes/…
系列文章
Kubernetes(一) 跟着官方文档从零搭建K8S
Kubernetes(二) 应用部署
Kubernetes(三) 如何从外部访问服务
欢迎关注公众号(代码如诗):


[版权声明]
本文发布于朴瑞卿的博客, 允许非商业用途转载, 但转载必须保留原作者朴瑞卿 及链接:blog.piaoruiqing.com. 如有授权方面的协商或合作, 请联系邮箱: piaoruiqing@gmail.com.
标签：


```


```text
Redis哨兵模式（sentinel）学习总结及部署记录（主从复制、读写分离、主从切换）
 

Redis的集群方案大致有三种：1）redis cluster集群方案；2）master/slave主从方案；3）哨兵模式来进行主从替换以及故障恢复。

一、sentinel哨兵模式介绍
Sentinel(哨兵)是用于监控redis集群中Master状态的工具，是Redis 的高可用性解决方案，sentinel哨兵模式已经被集成在redis2.4之后的版本中。sentinel是redis高可用的解决方案，sentinel系统可以监视一个或者多个redis master服务，以及这些master服务的所有从服务；当某个master服务下线时，自动将该master下的某个从服务升级为master服务替代已下线的master服务继续处理请求。

sentinel可以让redis实现主从复制，当一个集群中的master失效之后，sentinel可以选举出一个新的master用于自动接替master的工作，集群中的其他redis服务器自动指向新的master同步数据。一般建议sentinel采取奇数台，防止某一台sentinel无法连接到master导致误切换。其结构如下:



Redis-Sentinel是Redis官方推荐的高可用性(HA)解决方案，当用Redis做Master-slave的高可用方案时，假如master宕机了，Redis本身(包括它的很多客户端)都没有实现自动进行主备切换，而Redis-sentinel本身也是一个独立运行的进程，它能监控多个master-slave集群，发现master宕机后能进行自动切换。Sentinel由一个或多个Sentinel 实例 组成的Sentinel 系统可以监视任意多个主服务器，以及这些主服务器属下的所有从服务器，并在被监视的主服务器进入下线状态时，自动将下线主服务器属下的某个从服务器升级为新的主服务器。

例如下图所示：



在Server1 掉线后：



升级Server2 为新的主服务器：



Sentinel版本
Sentinel当前最新的稳定版本称为Sentinel 2(与之前的Sentinel 1区分开来）。随着redis2.8的安装包一起发行。安装完Redis2.8后，可以在redis2.8/src/里面找到Redis-sentinel的启动程序。
强烈建议：如果你使用的是redis2.6(sentinel版本为sentinel 1)，你最好应该使用redis2.8版本的sentinel 2，因为sentinel 1有很多的Bug，已经被官方弃用，所以强烈建议使用redis2.8以及sentinel 2。

Sentinel状态持久化
snetinel的状态会被持久化地写入sentinel的配置文件中。每次当收到一个新的配置时，或者新创建一个配置时，配置会被持久化到硬盘中，并带上配置的版本戳。这意味着，可以安全的停止和重启sentinel进程。

Sentinel作用： 
1）Master状态检测 
2）如果Master异常，则会进行Master-Slave切换，将其中一个Slave作为Master，将之前的Master作为Slave。 
3）Master-Slave切换后，master_redis.conf、slave_redis.conf和sentinel.conf的内容都会发生改变，即master_redis.conf中会多一行slaveof的配置，sentinel.conf的监控目标会随之调换。

Sentinel工作方式（每个Sentinel实例都执行的定时任务）
1）每个Sentinel以每秒钟一次的频率向它所知的Master，Slave以及其他 Sentinel 实例发送一个PING命令。
2）如果一个实例（instance）距离最后一次有效回复PING命令的时间超过 own-after-milliseconds 选项所指定的值，则这个实例会被Sentinel标记为主观下线。 
3）如果一个Master被标记为主观下线，则正在监视这个Master的所有 Sentinel 要以每秒一次的频率确认Master的确进入了主观下线状态。 
4）当有足够数量的Sentinel（大于等于配置文件指定的值）在指定的时间范围内确认Master的确进入了主观下线状态，则Master会被标记为客观下线。
5）在一般情况下，每个Sentinel 会以每10秒一次的频率向它已知的所有Master，Slave发送 INFO 命令。
6）当Master被Sentinel标记为客观下线时，Sentinel 向下线的 Master 的所有Slave发送 INFO命令的频率会从10秒一次改为每秒一次。 
7）若没有足够数量的Sentinel同意Master已经下线，Master的客观下线状态就会被移除。 若 Master重新向Sentinel 的PING命令返回有效回复，Master的主观下线状态就会被移除。

三个定时任务
sentinel在内部有3个定时任务
1）每10秒每个sentinel会对master和slave执行info命令，这个任务达到两个目的：
a）发现slave节点
b）确认主从关系
2）每2秒每个sentinel通过master节点的channel交换信息（pub/sub）。master节点上有一个发布订阅的频道(__sentinel__:hello)。sentinel节点通过__sentinel__:hello频道进行信息交换(对节点的"看法"和自身的信息)，达成共识。
3）每1秒每个sentinel对其他sentinel和redis节点执行ping操作（相互监控），这个其实是一个心跳检测，是失败判定的依据。

主观下线
所谓主观下线（Subjectively Down， 简称 SDOWN）指的是单个Sentinel实例对服务器做出的下线判断，即单个sentinel认为某个服务下线（有可能是接收不到订阅，之间的网络不通等等原因）。
主观下线就是说如果服务器在down-after-milliseconds给定的毫秒数之内， 没有返回 Sentinel 发送的 PING 命令的回复， 或者返回一个错误， 那么 Sentinel 将这个服务器标记为主观下线（SDOWN ）。
sentinel会以每秒一次的频率向所有与其建立了命令连接的实例（master，从服务，其他sentinel）发ping命令，通过判断ping回复是有效回复，还是无效回复来判断实例时候在线（对该sentinel来说是“主观在线”）。
sentinel配置文件中的down-after-milliseconds设置了判断主观下线的时间长度，如果实例在down-after-milliseconds毫秒内，返回的都是无效回复，那么sentinel回认为该实例已（主观）下线，修改其flags状态为SRI_S_DOWN。如果多个sentinel监视一个服务，有可能存在多个sentinel的down-after-milliseconds配置不同，这个在实际生产中要注意。

客观下线
客观下线（Objectively Down， 简称 ODOWN）指的是多个 Sentinel 实例在对同一个服务器做出 SDOWN 判断， 并且通过 SENTINEL is-master-down-by-addr 命令互相交流之后， 得出的服务器下线判断，然后开启failover。
客观下线就是说只有在足够数量的 Sentinel 都将一个服务器标记为主观下线之后， 服务器才会被标记为客观下线（ODOWN）。
只有当master被认定为客观下线时，才会发生故障迁移。
当sentinel监视的某个服务主观下线后，sentinel会询问其它监视该服务的sentinel，看它们是否也认为该服务主观下线，接收到足够数量（这个值可以配置）的sentinel判断为主观下线，既任务该服务客观下线，并对其做故障转移操作。
sentinel通过发送 SENTINEL is-master-down-by-addr ip port current_epoch runid，（ip：主观下线的服务id，port：主观下线的服务端口，current_epoch：sentinel的纪元，runid：*表示检测服务下线状态，如果是sentinel 运行id，表示用来选举领头sentinel）来询问其它sentinel是否同意服务下线。
一个sentinel接收另一个sentinel发来的is-master-down-by-addr后，提取参数，根据ip和端口，检测该服务时候在该sentinel主观下线，并且回复is-master-down-by-addr，回复包含三个参数：down_state（1表示已下线，0表示未下线），leader_runid（领头sentinal id），leader_epoch（领头sentinel纪元）。
sentinel接收到回复后，根据配置设置的下线最小数量，达到这个值，既认为该服务客观下线。
客观下线条件只适用于主服务器： 对于任何其他类型的 Redis 实例， Sentinel 在将它们判断为下线前不需要进行协商， 所以从服务器或者其他 Sentinel 永远不会达到客观下线条件。只要一个 Sentinel 发现某个主服务器进入了客观下线状态， 这个 Sentinel 就可能会被其他 Sentinel 推选出， 并对失效的主服务器执行自动故障迁移操作。

在redis-sentinel的conf文件里有这么两个配置：
1）sentinel monitor <masterName> <ip> <port> <quorum>

四个参数含义：
masterName这个是对某个master+slave组合的一个区分标识（一套sentinel是可以监听多套master+slave这样的组合的）。
ip 和 port 就是master节点的 ip 和 端口号。
quorum这个参数是进行客观下线的一个依据，意思是至少有 quorum 个sentinel主观的认为这个master有故障，才会对这个master进行下线以及故障转移。因为有的时候，某个sentinel节点可能因为自身网络原因，导致无法连接master，而此时master并没有出现故障，所以这就需要多个sentinel都一致认为该master有问题，才可以进行下一步操作，这就保证了公平性和高可用。

2）sentinel down-after-milliseconds <masterName> <timeout>
这个配置其实就是进行主观下线的一个依据，masterName这个参数不用说了，timeout是一个毫秒值，表示：如果这台sentinel超过timeout这个时间都无法连通master包括slave（slave不需要客观下线，因为不需要故障转移）的话，就会主观认为该master已经下线（实际下线需要客观下线的判断通过才会下线）

那么，多个sentinel之间是如何达到共识的呢？
这就是依赖于前面说的第二个定时任务，某个sentinel先将master节点进行一个主观下线，然后会将这个判定通过sentinel is-master-down-by-addr这个命令问对应的节点是否也同样认为该addr的master节点要做客观下线。最后当达成这一共识的sentinel个数达到前面说的quorum设置的这个值时，就会对该master节点下线进行故障转移。quorum的值一般设置为sentinel个数的二分之一加1，例如3个sentinel就设置2。

主观下线（SDOWN）和客观下线（ODOWN）的更多细节
sentinel对于不可用有两种不同的看法，一个叫主观不可用(SDOWN),另外一个叫客观不可用(ODOWN)。SDOWN是sentinel自己主观上检测到的关于master的状态，ODOWN需要一定数量的sentinel达成一致意见才能认为一个master客观上已经宕掉，各个sentinel之间通过命令SENTINEL is_master_down_by_addr来获得其它sentinel对master的检测结果。
从sentinel的角度来看，如果发送了PING心跳后，在一定时间内没有收到合法的回复，就达到了SDOWN的条件。这个时间在配置中通过is-master-down-after-milliseconds参数配置。
当sentinel发送PING后，以下回复之一都被认为是合法的：
PING replied with +PONG.
PING replied with -LOADING error.
PING replied with -MASTERDOWN error.
其它任何回复（或者根本没有回复）都是不合法的。

从SDOWN切换到ODOWN不需要任何一致性算法，只需要一个gossip协议：如果一个sentinel收到了足够多的sentinel发来消息告诉它某个master已经down掉了，SDOWN状态就会变成ODOWN状态。如果之后master可用了，这个状态就会相应地被清理掉。
正如之前已经解释过了，真正进行failover需要一个授权的过程，但是所有的failover都开始于一个ODOWN状态。
ODOWN状态只适用于master，对于不是master的redis节点sentinel之间不需要任何协商，slaves和sentinel不会有ODOWN状态。

配置版本号
为什么要先获得大多数sentinel的认可时才能真正去执行failover呢？
当一个sentinel被授权后，它将会获得宕掉的master的一份最新配置版本号，当failover执行结束以后，这个版本号将会被用于最新的配置。因为大多数sentinel都已经知道该版本号已经被要执行failover的sentinel拿走了，所以其他的sentinel都不能再去使用这个版本号。这意味着，每次failover都会附带有一个独一无二的版本号。我们将会看到这样做的重要性。而且，sentinel集群都遵守一个规则：如果sentinel A推荐sentinel B去执行failover，B会等待一段时间后，自行再次去对同一个master执行failover，这个等待的时间是通过failover-timeout配置项去配置的。从这个规则可以看出，sentinel集群中的sentinel不会再同一时刻并发去failover同一个master，第一个进行failover的sentinel如果失败了，另外一个将会在一定时间内进行重新进行failover，以此类推。
redis sentinel保证了活跃性：如果大多数sentinel能够互相通信，最终将会有一个被授权去进行failover.
redis sentinel也保证了安全性：每个试图去failover同一个master的sentinel都会得到一个独一无二的版本号。

配置传播
一旦一个sentinel成功地对一个master进行了failover，它将会把关于master的最新配置通过广播形式通知其它sentinel，其它的sentinel则更新对应master的配置。
一个faiover要想被成功实行，sentinel必须能够向选为master的slave发送SLAVEOF NO ONE命令，然后能够通过INFO命令看到新master的配置信息。
当将一个slave选举为master并发送SLAVEOF NO ONE后，即使其它的slave还没针对新master重新配置自己，failover也被认为是成功了的，然后所有sentinels将会发布新的配置信息。
新配在集群中相互传播的方式，就是为什么我们需要当一个sentinel进行failover时必须被授权一个版本号的原因。
每个sentinel使用##发布/订阅##的方式持续地传播master的配置版本信息，配置传播的##发布/订阅##管道是：__sentinel__:hello。
因为每一个配置都有一个版本号，所以以版本号最大的那个为标准。

举个例子：
假设有一个名为mymaster的地址为192.168.10.202:6379。一开始，集群中所有的sentinel都知道这个地址，于是为mymaster的配置打上版本号1。一段时候后mymaster死了，有一个sentinel被授权用版本号2对其进行failover。如果failover成功了，假设地址改为了192.168.10.202:9000，此时配置的版本号为2，进行failover的sentinel会将新配置广播给其他的sentinel，由于其他sentinel维护的版本号为1，发现新配置的版本号为2时，版本号变大了，说明配置更新了，于是就会采用最新的版本号为2的配置。
这意味着sentinel集群保证了第二种活跃性：一个能够互相通信的sentinel集群最终会采用版本号最高且相同的配置。

sentinel的"仲裁会"
前面我们谈到，当一个master被sentinel集群监控时，需要为它指定一个参数，这个参数指定了当需要判决master为不可用，并且进行failover时，所需要的sentinel数量，可以称这个参数为票数

不过，当failover主备切换真正被触发后，failover并不会马上进行，还需要sentinel中的大多数sentinel授权后才可以进行failover。
当ODOWN时，failover被触发。failover一旦被触发，尝试去进行failover的sentinel会去获得“大多数”sentinel的授权（如果票数比大多数还要大的时候，则询问更多的sentinel)
这个区别看起来很微妙，但是很容易理解和使用。例如，集群中有5个sentinel，票数被设置为2，当2个sentinel认为一个master已经不可用了以后，将会触发failover，但是，进行failover的那个sentinel必须先获得至少3个sentinel的授权才可以实行failover。
如果票数被设置为5，要达到ODOWN状态，必须所有5个sentinel都主观认为master为不可用，要进行failover，那么得获得所有5个sentinel的授权。

选举领头sentinel（即领导者选举）
一个redis服务被判断为客观下线时，多个监视该服务的sentinel协商，选举一个领头sentinel，对该redis服务进行故障转移操作。选举领头sentinel遵循以下规则：
1）所有的sentinel都有公平被选举成领头的资格。
2）所有的sentinel都有且只有一次将某个sentinel选举成领头的机会（在一轮选举中），一旦选举某个sentinel为领头，不能更改。
3）sentinel设置领头sentinel是先到先得，一旦当前sentinel设置了领头sentinel，以后要求设置sentinel为领头请求都会被拒绝。
4）每个发现服务客观下线的sentinel，都会要求其他sentinel将自己设置成领头。
5）当一个sentinel（源sentinel）向另一个sentinel（目sentinel）发送is-master-down-by-addr ip port current_epoch runid命令的时候，runid参数不是*，而是sentinel运行id，就表示源sentinel要求目标sentinel选举其为领头。
6）源sentinel会检查目标sentinel对其要求设置成领头的回复，如果回复的leader_runid和leader_epoch为源sentinel，表示目标sentinel同意将源sentinel设置成领头。
7）如果某个sentinel被半数以上的sentinel设置成领头，那么该sentinel既为领头。
8）如果在限定时间内，没有选举出领头sentinel，暂定一段时间，再选举。

为什么要选领导者？
简单来说，就是因为只能有一个sentinel节点去完成故障转移。
sentinel is-master-down-by-addr这个命令有两个作用，一是确认下线判定，二是进行领导者选举。
选举过程：
1）每个做主观下线的sentinel节点向其他sentinel节点发送上面那条命令，要求将它设置为领导者。
2）收到命令的sentinel节点如果还没有同意过其他的sentinel发送的命令（还未投过票），那么就会同意，否则拒绝。
3）如果该sentinel节点发现自己的票数已经过半且达到了quorum的值，就会成为领导者
4）如果这个过程出现多个sentinel成为领导者，则会等待一段时间重新选举。

Redis Sentinel的主从切换方案
Redis 2.8版开始正式提供名为Sentinel的主从切换方案，通俗的来讲，Sentinel可以用来管理多个Redis服务器实例，可以实现一个功能上实现HA的集群，Sentinel主要负责三个方面的任务：
1）监控（Monitoring）： Sentinel 会不断地检查你的主服务器和从服务器是否运作正常。
2）提醒（Notification）： 当被监控的某个 Redis 服务器出现问题时， Sentinel 可以通过 API 向管理员或者其他应用程序发送通知。
3）自动故障迁移（Automatic failover）： 当一个主服务器不能正常工作时， Sentinel 会开始一次自动故障迁移操作， 它会将失效主服务器的其中一个从服务器升级为新的主服务器， 并让失效主服务器的其他从服务器改为复制新的主服务器； 当客户端试图连接失效的主服务器时， 集群也会向客户端返回新主服务器的地址， 使得集群可以使用新主服务器代替失效服务器。

Redis Sentinel 是一个分布式系统， 可以在一个架构中运行多个 Sentinel 进程（progress）， 这些进程使用流言协议（gossip protocols)来接收关于主服务器是否下线的信息， 并使用投票协议（agreement protocols）来决定是否执行自动故障迁移， 以及选择哪个从服务器作为新的主服务器。
一个简单的主从结构加sentinel集群的架构图如下：



上图是一主一从节点，加上两个部署了sentinel的集群，sentinel集群之间会互相通信，沟通交流redis节点的状态，做出相应的判断并进行处理，这里的主观下线状态和客观下线状态是比较重要的状态，它们决定了是否进行故障转移
可以 通过订阅指定的频道信息，当服务器出现故障得时候通知管理员
客户端可以将 Sentinel 看作是一个只提供了订阅功能的 Redis 服务器，你不可以使用 PUBLISH 命令向这个服务器发送信息，但你可以用 SUBSCRIBE 命令或者 PSUBSCRIBE 命令， 通过订阅给定的频道来获取相应的事件提醒。 一个频道能够接收和这个频道的名字相同的事件。 比如说， 名为 +sdown 的频道就可以接收所有实例进入主观下线（SDOWN）状态的事件。

个人认为，Sentinel实现的最主要的一个功能就是能做到自动故障迁移，即当某一个master挂了的时候，可以自动的将某一个slave提升为新的master，且原master的所有slave也都自动的将自己的master改为新提升的master，这样我们的程序的可用性大大提高了。只要redis安装完成，Sentinel就安装完成了，Sentinel集成在redis里了。

Sentinel支持集群（可以部署在多台机器上，也可以在一台物理机上通过多端口实现伪集群部署）
很显然，只使用单个sentinel进程来监控redis集群是不可靠的，当sentinel进程宕掉后(sentinel本身也有单点问题，single-point-of-failure)整个集群系统将无法按照预期的方式运行。所以有必要将sentinel集群，这样有几个好处：
1）即使有一些sentinel进程宕掉了，依然可以进行redis集群的主备切换；
2）如果只有一个sentinel进程，如果这个进程运行出错，或者是网络堵塞，那么将无法实现redis集群的主备切换（单点问题）;
3）如果有多个sentinel，redis的客户端可以随意地连接任意一个sentinel来获得关于redis集群中的信息。

sentinel集群注意事项
1）只有Sentinel 集群中大多数服务器认定master主观下线时master才会被认定为客观下线，才可以进行故障迁移，也就是说，即使不管我们在sentinel monitor中设置的数是多少，就算是满足了该值，只要达不到大多数，就不会发生故障迁移。
2）官方建议sentinel至少部署三台，且分布在不同机器。这里主要考虑到sentinel的可用性，假如我们只部署了两台sentinel，且quorum设置为1，也可以实现自动故障迁移，但假如其中一台sentinel挂了，就永远不会触发自动故障迁移，因为永远达不到大多数sentinel认定master主观下线了。
3）sentinel monitor配置中的master IP尽量不要写127.0.0.1或localhost，因为客户端，如jedis获取master是根据这个获取的，若这样配置，jedis获取的ip则是127.0.0.1，这样就可能导致程序连接不上master
4）当sentinel 启动后会自动的修改sentinel.conf文件，如已发现的master的slave信息，和集群中其它sentinel 的信息等,这样即使重启sentinel也能保持原来的状态。注意，当集群服务器调整时，如更换sentinel的机器，或者新配置一个sentinel，请不要直接复制原来运行过得sentinel配置文件，因为其里面自动生成了以上说的那些信息，我们应该复制一个新的配置文件或者把自动生成的信息给删掉。
5）当发生故障迁移的时候，master的变更记录与slave更换master的修改会自动同步到redis的配置文件，这样即使重启redis也能保持变更后的状态。

每个 Sentinel 都需要定期执行的任务
每个 Sentinel 以每秒钟一次的频率向它所知的主服务器、从服务器以及其他 Sentinel 实例发送一个 PING 命令。
如果一个实例（instance）距离最后一次有效回复 PING 命令的时间超过 down-after-milliseconds 选项所指定的值， 那么这个实例会被 Sentinel 标记为主观下线。 一个有效回复可以是： +PONG 、 -LOADING 或者 -MASTERDOWN 。
如果一个主服务器被标记为主观下线， 那么正在监视这个主服务器的所有 Sentinel 要以每秒一次的频率确认主服务器的确进入了主观下线状态。
如果一个主服务器被标记为主观下线， 并且有足够数量的 Sentinel （至少要达到配置文件指定的数量）在指定的时间范围内同意这一判断， 那么这个主服务器被标记为客观下线。
在一般情况下， 每个 Sentinel 会以每 10 秒一次的频率向它已知的所有主服务器和从服务器发送 INFO 命令。 当一个主服务器被 Sentinel 标记为客观下线时， Sentinel 向下线主服务器的所有从服务器发送 INFO 命令的频率会从 10 秒一次改为每秒一次。
当没有足够数量的 Sentinel 同意主服务器已经下线， 主服务器的客观下线状态就会被移除。 当主服务器重新向 Sentinel 的PING 命令返回有效回复时， 主服务器的主管下线状态就会被移除。

Sentinel之间和Slaves之间的自动发现机制
虽然sentinel集群中各个sentinel都互相连接彼此来检查对方的可用性以及互相发送消息。但是你不用在任何一个sentinel配置任何其它的sentinel的节点。因为sentinel利用了master的发布/订阅机制去自动发现其它也监控了统一master的sentinel节点。
通过向名为__sentinel__:hello的管道中发送消息来实现。
同样，你也不需要在sentinel中配置某个master的所有slave的地址，sentinel会通过询问master来得到这些slave的地址的。
每个sentinel通过向每个master和slave的发布/订阅频道__sentinel__:hello每秒发送一次消息，来宣布它的存在。
每个sentinel也订阅了每个master和slave的频道__sentinel__:hello的内容，来发现未知的sentinel，当检测到了新的sentinel，则将其加入到自身维护的master监控列表中。
每个sentinel发送的消息中也包含了其当前维护的最新的master配置。如果某个sentinel发现
自己的配置版本低于接收到的配置版本，则会用新的配置更新自己的master配置。
在为一个master添加一个新的sentinel前，sentinel总是检查是否已经有sentinel与新的sentinel的进程号或者是地址是一样的。如果是那样，这个sentinel将会被删除，而把新的sentinel添加上去。

sentinel和redis身份验证
当一个master配置为需要密码才能连接时，客户端和slave在连接时都需要提供密码。
master通过requirepass设置自身的密码，不提供密码无法连接到这个master。
slave通过masterauth来设置访问master时的密码。
但是当使用了sentinel时，由于一个master可能会变成一个slave，一个slave也可能会变成master，所以需要同时设置上述两个配置项。

Sentinel API
在默认情况下， Sentinel 使用 TCP 端口 26379 （普通 Redis 服务器使用的是 6379 ）。Sentinel 接受 Redis 协议格式的命令请求， 所以你可以使用 redis-cli 或者任何其他 Redis 客户端来与 Sentinel 进行通讯。有两种方式可以和 Sentinel 进行通讯：
1）是通过直接发送命令来查询被监视 Redis 服务器的当前状态， 以及 Sentinel 所知道的关于其他 Sentinel 的信息， 诸如此类。
2）是使用发布与订阅功能， 通过接收 Sentinel 发送的通知： 当执行故障转移操作， 或者某个被监视的服务器被判断为主观下线或者客观下线时， Sentinel 就会发送相应的信息。

Sentinel命令（即登录到sentinel节点后执行的命令，比如执行"redis-cli -h 192.168.10.203 -p 26379"命令后，才可以执行下面命令）
PING ：返回 PONG 。
SENTINEL masters ：列出所有被监视的主服务器，以及这些主服务器的当前状态；
SENTINEL slaves <master name> ：列出给定主服务器的所有从服务器，以及这些从服务器的当前状态；
SENTINEL get-master-addr-by-name <master name> ： 返回给定名字的主服务器的 IP 地址和端口号。 如果这个主服务器正在执行故障转移操作， 或者针对这个主服务器的故障转移操作已经完成， 那么这个命令返回新的主服务器的 IP 地址和端口号；
SENTINEL reset <pattern> ： 重置所有名字和给定模式 pattern 相匹配的主服务器。 pattern 参数是一个 Glob 风格的模式。 重置操作清楚主服务器目前的所有状态， 包括正在执行中的故障转移， 并移除目前已经发现和关联的， 主服务器的所有从服务器和 Sentinel ；
SENTINEL failover <master name> ： 当主服务器失效时， 在不询问其他 Sentinel 意见的情况下， 强制开始一次自动故障迁移。 （不过发起故障转移的 Sentinel 会向其他 Sentinel 发送一个新的配置，其他 Sentinel 会根据这个配置进行相应的更新）

SENTINEL MONITOR <name> <ip> <port> <quorum> 这个命令告诉sentinel去监听一个新的master
SENTINEL REMOVE <name> 命令sentinel放弃对某个master的监听
SENTINEL SET <name> <option> <value> 这个命令很像Redis的CONFIG SET命令，用来改变指定master的配置。支持多个<option><value>。例如以下实例：SENTINEL SET objects-cache-master down-after-milliseconds 1000
只要是配置文件中存在的配置项，都可以用SENTINEL SET命令来设置。这个还可以用来设置master的属性，比如说quorum(票数)，而不需要先删除master，再重新添加master。例如：SENTINEL SET objects-cache-master quorum 5

客户端可以通过SENTINEL get-master-addr-by-name <master name>获取当前的主服务器IP地址和端口号，以及SENTINEL slaves <master name>获取所有的Slaves信息。

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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
[root@redis-master ~]# redis-cli -h 192.168.10.202 -p 6379 INFO|grep role
role:slave
[root@redis-master ~]# redis-cli -h 192.168.10.203 -p 6379 INFO|grep role
role:slave
[root@redis-master ~]# redis-cli -h 192.168.10.205 -p 6379 INFO|grep role
role:master
 
登录任意一个节点的sentinel，进行相关命令的操作（下面命令例子中的redisMaster是sentinel监控redis主从状态时定义的master名称）
 
1）sentinel masters   罗列所有sentinel 监视相关的master
[root@redis-master ~]# redis-cli -h 192.168.10.205 -p 26379
192.168.10.205:26379> sentinel masters
 
2）sentinel master masterName   列出一个master相关的的信息
[root@redis-master ~]# redis-cli -h 192.168.10.205 -p 26379
192.168.10.205:26379> sentinel master redisMaster
 
3）sentinel slaves masterName   列出一个master相应的slave组相关的数据
[root@redis-master ~]# redis-cli -h 192.168.10.205 -p 26379
192.168.10.205:26379> sentinel slaves redisMaster
 
4）sentinel sentinels masterName   列出master相关的sentinels组其他相关的信息
[root@redis-master ~]# redis-cli -h 192.168.10.205 -p 26379
192.168.10.205:26379> sentinel sentinels redisMaster
 
5）sentinel get-master-addr-by-name masterName   获取master-name相关的 ip addr 的信息
[root@redis-master ~]# redis-cli -h 192.168.10.205 -p 26379
192.168.10.205:26379> sentinel get-master-addr-by-name redisMaster
1) "192.168.10.205"
2) "6379"
增加或删除Sentinel
由于有sentinel自动发现机制，所以添加一个sentinel到你的集群中非常容易，你所需要做的只是监控到某个Master上，然后新添加的sentinel就能获得其他sentinel的信息以及master所有的slaves。
如果你需要添加多个sentinel，建议你一个接着一个添加，这样可以预防网络隔离带来的问题。你可以每个30秒添加一个sentinel。最后你可以用SENTINEL MASTER mastername来检查一下是否所有的sentinel都已经监控到了master。
删除一个sentinel显得有点复杂：因为sentinel永远不会删除一个已经存在过的sentinel，即使它已经与组织失去联系很久了。
要想删除一个sentinel，应该遵循如下步骤：
1）停止所要删除的sentinel
2）发送一个SENTINEL RESET * 命令给所有其它的sentinel实例，如果你想要重置指定master上面的sentinel，只需要把*号改为特定的名字，注意，需要一个接一个发，每次发送的间隔不低于30秒。
3）检查一下所有的sentinels是否都有一致的当前sentinel数。使用SENTINEL MASTER mastername 来查询。

删除旧master或者不可达slave
sentinel永远会记录好一个Master的slaves，即使slave已经与组织失联好久了。这是很有用的，因为sentinel集群必须有能力把一个恢复可用的slave进行重新配置。
并且，failover后，失效的master将会被标记为新master的一个slave，这样的话，当它变得可用时，就会从新master上复制数据。
然后，有时候你想要永久地删除掉一个slave(有可能它曾经是个master)，你只需要发送一个SENTINEL RESET master命令给所有的sentinels，它们将会更新列表里能够正确地复制master数据的slave。

发布与订阅信息（sentinel的日志文件里可以看到这些信息）
客户端可以将 Sentinel 看作是一个只提供了订阅功能的 Redis 服务器： 你不可以使用 PUBLISH 命令向这个服务器发送信息， 但你可以用 SUBSCRIBE 命令或者 PSUBSCRIBE 命令， 通过订阅给定的频道来获取相应的事件提醒。

一个频道能够接收和这个频道的名字相同的事件。 比如说， 名为 +sdown 的频道就可以接收所有实例进入主观下线（SDOWN）状态的事件。

通过执行 "PSUBSCRIBE * "命令可以接收所有事件信息（即订阅所有消息）。

以下列出的是客户端可以通过订阅来获得的频道和信息的格式： 第一个英文单词是频道/事件的名字， 其余的是数据的格式。

注意， 当格式中包含 instance details 字样时， 表示频道所返回的信息中包含了以下用于识别目标实例的内容.

以下是所有可以收到的消息的消息格式，如果你订阅了所有消息的话。第一个单词是频道的名字，其它是数据的格式。
注意：以下的instance details的格式是：
<instance-type> <name> <ip> <port> @ <master-name> <master-ip> <master-port>
如果这个redis实例是一个master，那么@之后的消息就不会显示。

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
16
17
18
19
20
21
22
23
24
25
+reset-master <instance details> -- 当master被重置时.
    +slave <instance details> -- 当检测到一个slave并添加进slave列表时.
    +failover-state-reconf-slaves <instance details> -- Failover状态变为reconf-slaves状态时
    +failover-detected <instance details> -- 当failover发生时
    +slave-reconf-sent <instance details> -- sentinel发送SLAVEOF命令把它重新配置时
    +slave-reconf-inprog <instance details> -- slave被重新配置为另外一个master的slave，但数据复制还未发生时。
    +slave-reconf-done <instance details> -- slave被重新配置为另外一个master的slave并且数据复制已经与master同步时。
    -dup-sentinel <instance details> -- 删除指定master上的冗余sentinel时 (当一个sentinel重新启动时，可能会发生这个事件).
    +sentinel <instance details> -- 当master增加了一个sentinel时。
    +sdown <instance details> -- 进入SDOWN状态时;
    -sdown <instance details> -- 离开SDOWN状态时。
    +odown <instance details> -- 进入ODOWN状态时。
    -odown <instance details> -- 离开ODOWN状态时。
    +new-epoch <instance details> -- 当前配置版本被更新时。
    +try-failover <instance details> -- 达到failover条件，正等待其他sentinel的选举。
    +elected-leader <instance details> -- 被选举为去执行failover的时候。
    +failover-state-select-slave <instance details> -- 开始要选择一个slave当选新master时。
    no-good-slave <instance details> -- 没有合适的slave来担当新master
    selected-slave <instance details> -- 找到了一个适合的slave来担当新master
    failover-state-send-slaveof-noone <instance details> -- 当把选择为新master的slave的身份进行切换的时候。
    failover-end-for-timeout <instance details> -- failover由于超时而失败时。
    failover-end <instance details> -- failover成功完成时。
    switch-master <master name> <oldip> <oldport> <newip> <newport> -- 当master的地址发生变化时。通常这是客户端最感兴趣的消息了。
    +tilt -- 进入Tilt模式。
    -tilt -- 退出Tilt模式。
可以看出，使用Sentinel命令和发布订阅两种机制就能很好的实现和客户端的集成整合：
使用get-master-addr-by-name和slaves指令可以获取当前的Master和Slaves的地址和信息；而当发生故障转移时，即Master发生切换，可以通过订阅的+switch-master事件获得最新的Master信息。

sentinel.conf中的notification-script
在sentinel.conf中可以配置多个sentinel notification-script <master name> <shell script-path>, 如sentinel notification-script mymaster ./check.sh
这个是在群集failover时会触发执行指定的脚本。脚本的执行结果若为1，即稍后重试（最大重试次数为10）；若为2，则执行结束。并且脚本最大执行时间为60秒，超时会被终止执行。

目前会存在该脚本被执行多次的问题，网上查找资料获得的解释是：脚本分为两个级别， SENTINEL_LEADER 和 SENTINEL_OBSERVER ，前者仅由领头 Sentinel 执行（一个 Sentinel），而后者由监视同一个 master 的所有 Sentinel 执行（多个 Sentinel）。

无failover时的配置纠正
即使当前没有failover正在进行，sentinel依然会使用当前配置去设置监控的master。特别是：
1）根据最新配置确认为slaves的节点却声称自己是master(上文例子中被网络隔离后的的redis3)，这时它们会被重新配置为当前master的slave。
2）如果slaves连接了一个错误的master，将会被改正过来，连接到正确的master。

Slave选举与优先级
当一个sentinel准备好了要进行failover，并且收到了其他sentinel的授权，那么就需要选举出一个合适的slave来做为新的master。

slave的选举主要会评估slave的以下几个方面：
1）与master断开连接的次数
2）Slave的优先级
3）数据复制的下标(用来评估slave当前拥有多少master的数据)
4）进程ID

如果一个slave与master失去联系超过10次，并且每次都超过了配置的最大失联时间(down-after-milliseconds)，如果sentinel在进行failover时发现slave失联，那么这个slave就会被sentinel认为不适合用来做新master的。
更严格的定义是，如果一个slave持续断开连接的时间超过
(down-after-milliseconds * 10) + milliseconds_since_master_is_in_SDOWN_state
就会被认为失去选举资格。

符合上述条件的slave才会被列入master候选人列表，并根据以下顺序来进行排序：
1）sentinel首先会根据slaves的优先级来进行排序，优先级越小排名越靠前。
2）如果优先级相同，则查看复制的下标，哪个从master接收的复制数据多，哪个就靠前。
3）如果优先级和下标都相同，就选择进程ID较小的那个。

一个redis无论是master还是slave，都必须在配置中指定一个slave优先级。要注意到master也是有可能通过failover变成slave的。
如果一个redis的slave优先级配置为0，那么它将永远不会被选为master。但是它依然会从master哪里复制数据。

故障转移
所谓故障转移就是当master宕机，选一个合适的slave来晋升为master的操作，redis-sentinel会自动完成这个，不需要我们手动来实现。

一次故障转移操作大致分为以下流程：
发现主服务器已经进入客观下线状态。
对我们的当前集群进行自增， 并尝试在这个集群中当选。
如果当选失败， 那么在设定的故障迁移超时时间的两倍之后， 重新尝试当选。 如果当选成功， 那么执行以下步骤：
选出一个从服务器，并将它升级为主服务器。
向被选中的从服务器发送 SLAVEOF NO ONE 命令，让它转变为主服务器。
通过发布与订阅功能， 将更新后的配置传播给所有其他 Sentinel ， 其他 Sentinel 对它们自己的配置进行更新。
向已下线主服务器的从服务器发送 SLAVEOF 命令， 让它们去复制新的主服务器。
当所有从服务器都已经开始复制新的主服务器时， 领头 Sentinel 终止这次故障迁移操作。
每当一个 Redis 实例被重新配置（reconfigured） —— 无论是被设置成主服务器、从服务器、又或者被设置成其他主服务器的从服务器 —— Sentinel 都会向被重新配置的实例发送一个 CONFIG REWRITE 命令， 从而确保这些配置会持久化在硬盘里。

Sentinel 使用以下规则来选择新的主服务器：
在失效主服务器属下的从服务器当中， 那些被标记为主观下线、已断线、或者最后一次回复 PING 命令的时间大于五秒钟的从服务器都会被淘汰。
在失效主服务器属下的从服务器当中， 那些与失效主服务器连接断开的时长超过 down-after 选项指定的时长十倍的从服务器都会被淘汰。
在经历了以上两轮淘汰之后剩下来的从服务器中， 我们选出复制偏移量（replication offset）最大的那个从服务器作为新的主服务器； 如果复制偏移量不可用， 或者从服务器的复制偏移量相同， 那么带有最小运行 ID 的那个从服务器成为新的主服务器。

Sentinel 自动故障迁移的一致性特质
Sentinel 自动故障迁移使用 Raft 算法来选举领头（leader） Sentinel ， 从而确保在一个给定的纪元（epoch）里， 只有一个领头产生。

这表示在同一个纪元中， 不会有两个 Sentinel 同时被选中为领头， 并且各个 Sentinel 在同一个纪元中只会对一个领头进行投票。

更高的配置纪元总是优于较低的纪元， 因此每个 Sentinel 都会主动使用更新的纪元来代替自己的配置。

简单来说， 可以将 Sentinel 配置看作是一个带有版本号的状态。 一个状态会以最后写入者胜出（last-write-wins）的方式（也即是，最新的配置总是胜出）传播至所有其他 Sentinel 。

举个例子， 当出现网络分割（network partitions）时， 一个 Sentinel 可能会包含了较旧的配置， 而当这个 Sentinel 接到其他 Sentinel 发来的版本更新的配置时， Sentinel 就会对自己的配置进行更新。

如果要在网络分割出现的情况下仍然保持一致性， 那么应该使用 min-slaves-to-write 选项， 让主服务器在连接的从实例少于给定数量时停止执行写操作， 与此同时， 应该在每个运行 Redis 主服务器或从服务器的机器上运行 Redis Sentinel 进程。

Sentinel 状态的持久化
Sentinel 的状态会被持久化在 Sentinel 配置文件里面。每当 Sentinel 接收到一个新的配置， 或者当领头 Sentinel 为主服务器创建一个新的配置时， 这个配置会与配置纪元一起被保存到磁盘里面。这意味着停止和重启 Sentinel 进程都是安全的。

Sentinel 在非故障迁移的情况下对实例进行重新配置
即使没有自动故障迁移操作在进行， Sentinel 总会尝试将当前的配置设置到被监视的实例上面。 特别是：

根据当前的配置， 如果一个从服务器被宣告为主服务器， 那么它会代替原有的主服务器， 成为新的主服务器， 并且成为原有主服务器的所有从服务器的复制对象。
那些连接了错误主服务器的从服务器会被重新配置， 使得这些从服务器会去复制正确的主服务器。
不过， 在以上这些条件满足之后， Sentinel 在对实例进行重新配置之前仍然会等待一段足够长的时间， 确保可以接收到其他 Sentinel 发来的配置更新， 从而避免自身因为保存了过期的配置而对实例进行了不必要的重新配置。

总结来说，故障转移分为三个步骤：

1）从下线的主服务的所有从服务里面挑选一个从服务，将其转成主服务
sentinel状态数据结构中保存了主服务的所有从服务信息，领头sentinel按照如下的规则从从服务列表中挑选出新的主服务；
删除列表中处于下线状态的从服务；
删除最近5秒没有回复过领头sentinel info信息的从服务；
删除与已下线的主服务断开连接时间超过 down-after-milliseconds*10毫秒的从服务，这样就能保留从的数据比较新（没有过早的与主断开连接）；
领头sentinel从剩下的从列表中选择优先级高的，如果优先级一样，选择偏移量最大的（偏移量大说明复制的数据比较新），如果偏移量一样，选择运行id最小的从服务。

2）已下线主服务的所有从服务改为复制新的主服务
挑选出新的主服务之后，领头sentinel 向原主服务的从服务发送 slaveof 新主服务 的命令，复制新master。

3）将已下线的主服务设置成新的主服务的从服务，当其回复正常时，复制新的主服务，变成新的主服务的从服务
同理，当已下线的服务重新上线时，sentinel会向其发送slaveof命令，让其成为新主的从。

温馨提示：还可以向任意sentinel发生sentinel failover <masterName> 进行手动故障转移，这样就不需要经过上述主客观和选举的过程。

sentinel.conf文件配置参数解释

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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
1）sentinel monitor mymaster 192.168.10.202 6379 2
Sentine监听的maste地址，第一个参数是给master起的名字，第二个参数为master IP，第三个为master端口，第四个为当该master挂了的时候，若想将该master判为失效，
在Sentine集群中必须至少2个Sentine同意才行，只要该数量不达标，则就不会发生故障迁移。也就是说只要有2个sentinel认为master下线，就认为该master客观下线，
启动failover并选举产生新的master。通常最后一个参数不能多于启动的sentinel实例数。
 
这个配置是sentinel需要监控的master/slaver信息，格式为sentinel monitor <mastername> <masterIP> <masterPort> <quorum> 
其中<quorum>应该小于集群中slave的个数，当失效的节点数超过了<quorum>,则认为整个体系结构失效
 
不过要注意， 无论你设置要多少个 Sentinel 同意才能判断一个服务器失效， 一个 Sentinel 都需要获得系统中多数（majority） Sentinel 的支持， 才能发起一次自动故障迁移，
并预留一个给定的配置纪元 （configuration Epoch ，一个配置纪元就是一个新主服务器配置的版本号）。
  
换句话说， 在只有少数（minority） Sentinel 进程正常运作的情况下， Sentinel 是不能执行自动故障迁移的。
-----------------------------------------------------------------------------------------------
2）sentinel down-after-milliseconds mymaster 30000
表示master被当前sentinel实例认定为失效的间隔时间。
master在多长时间内一直没有给Sentine返回有效信息，则认定该master主观下线。也就是说如果多久没联系上redis-servevr，认为这个redis-server进入到失效（SDOWN）状态。
  
如果服务器在给定的毫秒数之内， 没有返回 Sentinel 发送的 PING 命令的回复， 或者返回一个错误， 那么 Sentinel 将这个服务器标记为主观下线（subjectively down，简称 SDOWN ）。
不过只有一个 Sentinel 将服务器标记为主观下线并不一定会引起服务器的自动故障迁移： 只有在足够数量的 Sentinel 都将一个服务器标记为主观下线之后， 服务器才会被标记为客观下线
（objectively down， 简称 ODOWN ）， 这时自动故障迁移才会执行。
将服务器标记为客观下线所需的 Sentinel 数量由对主服务器的配置决定。
-----------------------------------------------------------------------------------------------
3）sentinel parallel-syncs mymaster 2
当在执行故障转移时，设置几个slave同时进行切换master，该值越大，则可能就有越多的slave在切换master时不可用，可以将该值设置为1，即一个一个来，这样在某个
slave进行切换master同步数据时，其余的slave还能正常工作，以此保证每次只有一个从服务器处于不能处理命令请求的状态。
  
parallel-syncs 选项指定了在执行故障转移时， 最多可以有多少个从服务器同时对新的主服务器进行同步， 这个数字越小， 完成故障转移所需的时间就越长。
  
如果从服务器被设置为允许使用过期数据集（参见对 redis.conf 文件中对 slave-serve-stale-data 选项的说明）， 那么你可能不希望所有从服务器都在同一时间向新的主服务器发送同步请求，
因为尽管复制过程的绝大部分步骤都不会阻塞从服务器， 但从服务器在载入主服务器发来的 RDB 文件时， 仍然会造成从服务器在一段时间内不能处理命令请求： 如果全部从服务器一起对新的主
服务器进行同步， 那么就可能会造成所有从服务器在短时间内全部不可用的情况出现。
 
当新master产生时，同时进行"slaveof"到新master并进行"SYNC"的slave个数。 
默认为1,建议保持默认值 
在salve执行salveof与同步时，将会终止客户端请求。 
此值较大，意味着"集群"终止客户端请求的时间总和和较大。 
此值较小,意味着"集群"在故障转移期间，多个salve向客户端提供服务时仍然使用旧数据。 
-----------------------------------------------------------------------------------------------
4）sentinel can-failover mymaster yes
在sentinel检测到O_DOWN后，是否对这台redis启动failover机制
-----------------------------------------------------------------------------------------------
5）sentinel auth-pass mymaster 20180408
设置sentinel连接的master和slave的密码，这个需要和redis.conf文件中设置的密码一样
-----------------------------------------------------------------------------------------------
6）sentinel failover-timeout mymaster 180000
failover过期时间，当failover开始后，在此时间内仍然没有触发任何failover操作，当前sentinel将会认为此次failoer失败。 
执行故障迁移超时时间，即在指定时间内没有大多数的sentinel 反馈master下线，该故障迁移计划则失效
-----------------------------------------------------------------------------------------------
7）sentinel config-epoch mymaster 0
选项指定了在执行故障转移时， 最多可以有多少个从服务器同时对新的主服务器进行同步。这个数字越小， 完成故障转移所需的时间就越长。
-----------------------------------------------------------------------------------------------
8）sentinel notification-script mymaster /var/redis/notify.sh
当failover时，可以指定一个"通知"脚本用来告知当前集群的情况。
脚本被允许执行的最大时间为60秒，如果超时，脚本将会被终止(KILL)
-----------------------------------------------------------------------------------------------
9）sentinel leader-epoch mymaster 0
同时一时间最多0个slave可同时更新配置,建议数字不要太大,以免影响正常对外提供服务。
基于以上细节知识梳理，总结出sentinel的工作原理

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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
首先要能理解SDOWN和ODOWN这两个词的含义，上面已经详细介绍了它们俩。在此再提一下：
SDOWN:subjectively down,直接翻译的为"主观"失效,即当前sentinel实例认为某个redis服务为"不可用"状态.
ODOWN:objectively down,直接翻译为"客观"失效,即多个sentinel实例都认为master处于"SDOWN"状态,那么此时master将处于ODOWN,ODOWN可以简单理解为master已经被集群确定
为"不可用",将会开启failover.
 
SDOWN适合于master和slave,但是ODOWN只会使用于master;当slave失效超过"down-after-milliseconds"后,那么所有sentinel实例都会将其标记为"SDOWN"。
 
1) SDOWN与ODOWN转换过程:
每个sentinel实例在启动后,都会和已知的slaves/master以及其他sentinels建立TCP连接,并周期性发送PING(默认为1秒)
在交互中,如果redis-server无法在"down-after-milliseconds"时间内响应或者响应错误信息,都会被认为此redis-server处于SDOWN状态。
如果SDOWN的server为master,那么此时sentinel实例将会向其他sentinel间歇性(一秒)发送"is-master-down-by-addr <ip> <port>"指令并获取响应信息,如果足够多的
sentinel实例检测到master处于SDOWN,那么此时当前sentinel实例标记master为ODOWN...其他sentinel实例做同样的交互操作。
配置项"sentinel monitor <mastername> <masterip> <masterport> <quorum>",如果检测到master处于SDOWN状态的slave个数达到<quorum>,那么此时此sentinel实例将会认为
master处于ODOWN。每个sentinel实例将会间歇性(10秒)向master和slaves发送"INFO"指令,如果master失效且没有新master选出时,每1秒发送一次"INFO";"INFO"的主要目的就是
获取并确认当前集群环境中slaves和master的存活情况。
 
经过上述过程后,所有的sentinel对master失效达成一致后,开始failover.
 
2) Sentinel与slaves"自动发现"机制:
在sentinel的配置文件中(local-sentinel.conf),都指定了port,此port就是sentinel实例侦听其他sentinel实例建立链接的端口.在集群稳定后,最终会每个sentinel实例之间都
会建立一个tcp链接,此链接中发送"PING"以及类似于"is-master-down-by-addr"指令集,可用用来检测其他sentinel实例的有效性以及"ODOWN"和"failover"过程中信息的交互.
 
在sentinel之间建立连接之前,sentinel将会尽力和配置文件中指定的master建立连接.sentinel与master的连接中的通信主要是基于pub/sub来发布和接收信息,发布的信息内容包
括当前sentinel实例的侦听端口:
+sentinel sentinel 127.0.0.1:26579 127.0.0.1 26579 .... 
 
发布的主题名称为"__sentinel__:hello";同时sentinel实例也是"订阅"此主题,以获得其他sentinel实例的信息.由此可见,环境首次构建时,在默认master存活的情况下,所有的
sentinel实例可以通过pub/sub即可获得所有的sentinel信息,此后每个sentinel实例即可以根据+sentinel信息中的"ip+port"和其他sentinel逐个建立tcp连接即可.不过需要提醒
的是,每个sentinel实例均会间歇性(5秒)向"__sentinel__:hello"主题中发布自己的ip+port,目的就是让后续加入集群的sentinel实例也能或得到自己的信息。
根据上文,我们知道在master有效的情况下,即可通过"INFO"指令获得当前master中已有的slave列表;此后任何slave加入集群,master都会向"主题中"发布"+slave 127.0.0.1:6579 ..",
那么所有的sentinel也将立即获得slave信息,并和slave建立链接并通过PING检测其存活性.
 
补充一下,每个sentinel实例都会保存其他sentinel实例的列表以及现存的master/slaves列表,各自的列表中不会有重复的信息(不可能出现多个tcp连接),对于sentinel将使用ip+port
做唯一性标记,
对于master/slaver将使用runid做唯一性标记,其中redis-server的runid在每次启动时都不同.
 
3) Leader选举:
其实在sentinels故障转移中，仍然需要一个"Leader"来调度整个过程：master的选举以及slave的重配置和同步。当集群中有多个sentinel实例时，如何选举其中一个sentinel为leader呢？
 
在配置文件中"can-failover""quorum"参数，以及"is-master-down-by-addr"指令配合来完成整个过程。
A) "can-failover"用来表明当前sentinel是否可以参与"failover"过程，如果为"YES"则表明它将有能力参与"Leader"的选举，否则它将作为"Observer"，observer参与leader选举投票但
不能被选举；
B) "quorum"不仅用来控制master ODOWN状态确认，同时还用来选举leader时最小"赞同票"数；
C) "is-master-down-by-addr"，它可以用来检测"ip + port"的master是否已经处于SDOWN状态，不过此指令不仅能够获得master是否处于SDOWN，同时它还额外的返回当前sentinel
本地"投票选举"的Leader信息(runid);
 
每个sentinel实例都持有其他的sentinels信息，在Leader选举过程中(当为leader的sentinel实例失效时，有可能master server并没失效，注意分开理解)，sentinel实例将从所有的
sentinels集合中去除"can-failover = no"和状态为SDOWN的sentinels，在剩余的sentinels列表中按照runid按照"字典"顺序排序后，取出runid最小的sentinel实例，并将它"投票选举"
为Leader，并在其他sentinel发送的"is-master-down-by-addr"指令时将推选的runid追加到响应中。每个sentinel实例都会检测"is-master-down-by-addr"的响应结果，如果"投票选举"的
leader为自己，且状态正常的sentinels实例中，"赞同者"的自己的sentinel个数不小于(>=) 50% + 1,且不小与<quorum>，那么此sentinel就会认为选举成功且leader为自己。
在sentinel.conf文件中，我们期望有足够多的sentinel实例配置"can-failover yes"，这样能够确保当leader失效时，能够选举某个sentinel为leader，以便进行failover。如果leader无法产生，
比如较少的sentinels实例有效，那么failover过程将无法继续.
 
4) failover过程:
在Leader触发failover之前，首先wait数秒(随即0~5)，以便让其他sentinel实例准备和调整(有可能多个leader??),如果一切正常，那么leader就需要开始将一个salve提升为master，此slave
必须为状态良好(不能处于SDOWN/ODOWN状态)且权重值最低(redis.conf中)的，当master身份被确认后，开始failover
A）"+failover-triggered": Leader开始进行failover，此后紧跟着"+failover-state-wait-start"，wait数秒。
B）"+failover-state-select-slave": Leader开始查找合适的slave
C）"+selected-slave": 已经找到合适的slave
D） "+failover-state-sen-slaveof-noone": Leader向slave发送"slaveof no one"指令，此时slave已经完成角色转换，此slave即为master
E） "+failover-state-wait-promotition": 等待其他sentinel确认slave
F）"+promoted-slave"：确认成功
G）"+failover-state-reconf-slaves": 开始对slaves进行reconfig操作。
H）"+slave-reconf-sent":向指定的slave发送"slaveof"指令，告知此slave跟随新的master
I）"+slave-reconf-inprog": 此slave正在执行slaveof + SYNC过程，如过slave收到"+slave-reconf-sent"之后将会执行slaveof操作。
J）"+slave-reconf-done": 此slave同步完成，此后leader可以继续下一个slave的reconfig操作。循环G）
K）"+failover-end": 故障转移结束
L）"+switch-master"：故障转移成功后，各个sentinel实例开始监控新的master。
二、redis sentinel 主从切换(failover)的容灾环境部署记录

redis主从复制简单来说：
A）Redis的复制功能是支持多个数据库之间的数据同步。一类是主数据库（master）一类是从数据库（slave），主数据库可以进行读写操作，当发生写操作的时候自动将数据同步到从数据库，而从数据库一般是只读的，并接收主数据库同步过来的数据，一个主数据库可以有多个从数据库，而一个从数据库只能有一个主数据库。
B）通过redis的复制功能可以很好的实现数据库的读写分离，提高服务器的负载能力。主数据库主要进行写操作，而从数据库负责读操作。

Redis主从复制流程简图



redis主从复制的大致过程：
1）当一个从数据库启动时，会向主数据库发送sync命令，
2）主数据库接收到sync命令后会开始在后台保存快照（执行rdb操作），并将保存期间接收到的命令缓存起来
3）当快照完成后，redis会将快照文件和所有缓存的命令发送给从数据库。
4）从数据库收到后，会载入快照文件并执行收到的缓存的命令。

注意：redis2.8之前的版本：当主从数据库同步的时候从数据库因为网络原因断开重连后会重新执行上述操作，不支持断点续传。redis2.8之后支持断点续传。

0）Redis主从结构支持一主多从+n个sentinel模式，信息如下：

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
192.168.10.202   redis-master    redis（6379）、sentinel（26379）
192.168.10.203   redis-slave01   redis（6379）、sentinel（26379）
192.168.10.205   redis-slave02   redis（6379）、sentinel（26379） 
  
关闭三个节点机器的iptables和selinux（所有节点机器上都要操作）
[root@redis-master ~]# /etc/init.d/iptables stop
[root@redis-master ~]# vim /etc/sysconfig/selinux
......
SELINUX=disabled
[root@redis-master ~]# setenforce 0
[root@redis-master ~]# getenforce
Permissive
  
注意：本案例采用1主2从+3 sentinel的集群模式，所有从节点的配置都一样。
a）redis服务器上各自存在一个Sentinel，监控本机redis的运行情况，并通知给闭路环上其它的redis节点；
b）当master发生异常（例如：宕机和断电等）导致不可运行时，Sentinel将通知给其它节点，而剩余节点上的Sentinel将重新选举出新的master，而原来的master重新恢复正常后，则一直扮演slave角色；
c）规定整个架构体系中，master提供读写服务，而slave只提供读取服务。





1）redis一键安装（三个节点上都要操作）

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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
[root@redis-master ~]# cd /usr/local/src/
[root@redis-master src]# vim install_redis.sh
#!/usr/bin/env bash
# It's Used to be install redis.
# Created on 2018/04/08 11:18.
# @author: wangshibo.
# Version: 1.0
   
function install_redis () {
#################################################################################################
        cd /usr/local/src
        if [ ! -f " redis-4.0.1.tar.gz" ]; then
           wget http://download.redis.io/releases/redis-4.0.1.tar.gz
        fi
        cd /usr/local/src
        tar -zxvf /usr/local/src/redis-4.0.1.tar.gz
        cd redis-4.0.1
        make PREFIX=/usr/local/redis install
        mkdir -p /usr/local/redis/{etc,var}
        rsync -avz redis.conf  /usr/local/redis/etc/
        sed -i 's@pidfile.*@pidfile /var/run/redis-server.pid@' /usr/local/redis/etc/redis.conf
        sed -i "s@logfile.*@logfile /usr/local/redis/var/redis.log@" /usr/local/redis/etc/redis.conf
        sed -i "s@^dir.*@dir /usr/local/redis/var@" /usr/local/redis/etc/redis.conf
        sed -i 's/daemonize no/daemonize yes/g' /usr/local/redis/etc/redis.conf
        sed -i 's/^# bind 127.0.0.1/bind 0.0.0.0/g' /usr/local/redis/etc/redis.conf
 #################################################################################################
}
   
install_redis
[root@redis-master src]# chmod 755 install_redis.sh
[root@redis-master src]# sh -x install_redis.sh
2）redis启停脚本（三个节点上都要操作）

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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87
88
89
90
91
92
93
94
95
96
97
98
99
100
101
102
103
104
105
106
[root@redis-master src]# vim /etc/init.d/redis-server
#!/bin/bash
#
# redis - this script starts and stops the redis-server daemon
#
# chkconfig:   - 85 15
# description:  Redis is a persistent key-value database
# processname: redis-server
# config:      /usr/local/redis/etc/redis.conf
# config:      /etc/sysconfig/redis
# pidfile:     /usr/local/redis/var/redis-server.pid
  
# Source function library.
. /etc/rc.d/init.d/functions
  
# Source networking configuration.
. /etc/sysconfig/network
  
# Check that networking is up.
[ "$NETWORKING" = "no" ] && exit 0
  
redis="/usr/local/redis/bin/redis-server"
prog=$(basename $redis)
  
REDIS_CONF_FILE="/usr/local/redis/etc/redis.conf"
  
[ -f /etc/sysconfig/redis ] && . /etc/sysconfig/redis
  
lockfile=/var/lock/subsys/redis-server
  
start() {
    [ -x $redis ] || exit 5
    [ -f $REDIS_CONF_FILE ] || exit 6
    echo -n $"Starting $prog: "
    daemon $redis $REDIS_CONF_FILE
    retval=$?
    echo
    [ $retval -eq 0 ] && touch $lockfile
    return $retval
}
  
stop() {
    echo -n $"Stopping $prog: "
    killproc $prog
    retval=$?
    echo
    [ $retval -eq 0 ] && rm -f $lockfile
    return $retval
}
  
restart() {
    stop
    start
}
  
reload() {
    echo -n $"Reloading $prog: "
    killproc $redis -HUP
    RETVAL=$?
    echo
}
  
force_reload() {
    restart
}
  
rh_status() {
    status $prog
}
  
rh_status_q() {
    rh_status >/dev/null 2>&1
}
  
case "$1" in
    start)
        rh_status_q && exit 0
        $1
        ;;
    stop)
        rh_status_q || exit 0
        $1
        ;;
    restart)
        $1
        ;;
    reload)
        rh_status_q || exit 7
        $1
        ;;
    force-reload)
        force_reload
        ;;
    status)
        rh_status
        ;;
    condrestart|try-restart)
        rh_status_q || exit 0
            ;;
    *)
        echo $"Usage: $0 {start|stop|status|restart|condrestart|try-restart|reload|force-reload}"
        exit 2
esac
 
执行权限
[root@redis-master src]# chmod 755 /etc/init.d/redis-server
3）redis-sentinel启停脚本示例（三个节点上都要操作）

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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87
88
89
90
91
92
93
94
95
96
97
98
99
100
101
102
103
104
105
106
[root@redis-master src]# vim /etc/init.d/redis-sentinel
#!/bin/bash
#
# redis-sentinel - this script starts and stops the redis-server sentinel daemon
#
# chkconfig:   - 85 15
# description:  Redis sentinel
# processname: redis-server
# config:      /usr/local/redis/etc/sentinel.conf
# config:      /etc/sysconfig/redis
# pidfile:     /usr/local/redis/var/redis-sentinel.pid
  
# Source function library.
. /etc/rc.d/init.d/functions
  
# Source networking configuration.
. /etc/sysconfig/network
  
# Check that networking is up.
[ "$NETWORKING" = "no" ] && exit 0
  
redis="/usr/local/redis/bin/redis-sentinel"
prog=$(basename $redis)
  
REDIS_CONF_FILE="/usr/local/redis/etc/sentinel.conf"
  
[ -f /etc/sysconfig/redis ] && . /etc/sysconfig/redis
  
lockfile=/var/lock/subsys/redis-sentinel
  
start() {
    [ -x $redis ] || exit 5
    [ -f $REDIS_CONF_FILE ] || exit 6
    echo -n $"Starting $prog: "
    daemon $redis $REDIS_CONF_FILE --sentinel
    retval=$?
    echo
    [ $retval -eq 0 ] && touch $lockfile
    return $retval
}
  
stop() {
    echo -n $"Stopping $prog: "
    killproc $prog
    retval=$?
    echo
    [ $retval -eq 0 ] && rm -f $lockfile
    return $retval
}
  
restart() {
    stop
    start
}
  
reload() {
    echo -n $"Reloading $prog: "
    killproc $redis -HUP
    RETVAL=$?
    echo
}
  
force_reload() {
    restart
}
  
rh_status() {
    status $prog
}
  
rh_status_q() {
    rh_status >/dev/null 2>&1
}
  
case "$1" in
    start)
        rh_status_q && exit 0
        $1
        ;;
    stop)
        rh_status_q || exit 0
        $1
        ;;
    restart)
        $1
        ;;
    reload)
        rh_status_q || exit 7
        $1
        ;;
    force-reload)
        force_reload
        ;;
    status)
        rh_status
        ;;
    condrestart|try-restart)
        rh_status_q || exit 0
            ;;
    *)
        echo $"Usage: $0 {start|stop|status|restart|condrestart|try-restart|reload|force-reload}"
        exit 2
esac
 
执行权限：
[root@redis-master src]# chmod 755 /etc/init.d/redis-sentinel
4）配置redis.conf

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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87
88
89
90
91
92
93
94
95
96
97
98
99
100
101
102
103
104
105
106
107
108
109
110
111
112
113
114
115
116
117
a）编辑redis-master主节点的redis.conf文件
[root@redis-master src]# mkdir -p /usr/local/redis/data/redis
[root@redis-master src]# cp /usr/local/redis/etc/redis.conf /usr/local/redis/etc/redis.conf.bak
[root@redis-master src]# vim /usr/local/redis/etc/redis.conf
bind 0.0.0.0
daemonize yes
pidfile "/usr/local/redis/var/redis-server.pid"
port 6379
tcp-backlog 128
timeout 0
tcp-keepalive 0
loglevel notice
logfile "/usr/local/redis/var/redis-server.log"
databases 16
save 900 1  
save 300 10
save 60 10000
stop-writes-on-bgsave-error yes
rdbcompression yes
rdbchecksum yes
dbfilename dump.rdb
dir "/usr/local/redis/data/redis"
#masterauth "20180408"                        #master设置密码保护，即slave连接master时的密码
#requirepass "20180408"                       #设置Redis连接密码，如果配置了连接密码，客户端在连接Redis时需要通过AUTH <password>命令提供密码，默认关闭
slave-serve-stale-data yes
slave-read-only yes
repl-diskless-sync no
repl-diskless-sync-delay 5
repl-disable-tcp-nodelay no
slave-priority 100
appendonly yes                                #打开aof持久化
appendfilename "appendonly.aof"
appendfsync everysec                          # 每秒一次aof写
no-appendfsync-on-rewrite no
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
aof-load-truncated yes
lua-time-limit 5000
slowlog-log-slower-than 10000
slowlog-max-len 128
latency-monitor-threshold 0
notify-keyspace-events ""
hash-max-ziplist-entries 512
hash-max-ziplist-value 64
list-max-ziplist-entries 512
list-max-ziplist-value 64
set-max-intset-entries 512
zset-max-ziplist-entries 128
zset-max-ziplist-value 64
hll-sparse-max-bytes 3000
activerehashing yes
client-output-buffer-limit normal 0 0 0
client-output-buffer-limit slave 256mb 64mb 60
client-output-buffer-limit pubsub 32mb 8mb 60
hz 10
aof-rewrite-incremental-fsync yes
 
注意：
上面配置中masterauth和requirepass表示设置密码保护，如果设置了密码，则连接redis后需要执行"auth 20180408"密码后才能操作其他命令。这里我不设置密码。
 
b）编辑redis-slave01和redis-slave02两个从节点的redis.conf文件
[root@redis-slave01 src]# mkdir -p /usr/local/redis/data/redis
[root@redis-slave01 src]# cp /usr/local/redis/etc/redis.conf /usr/local/redis/etc/redis.conf.bak
[root@redis-slave01 src]# vim /usr/local/redis/etc/redis.conf
bind 0.0.0.0
daemonize yes
pidfile "/usr/local/redis/var/redis-server.pid"
port 6379
tcp-backlog 128
timeout 0
tcp-keepalive 0
loglevel notice
logfile "/usr/local/redis/var/redis-server.log"
databases 16
save 900 1  
save 300 10
save 60 10000
stop-writes-on-bgsave-error yes
rdbcompression yes
rdbchecksum yes
dbfilename dump.rdb
dir "/usr/local/redis/data/redis"
#masterauth "20180408"               
#requirepass "20180408"      
slaveof 192.168.10.202 6379                  #相对主redis配置，多添加了此行       
slave-serve-stale-data yes
slave-read-only yes                          #从节点只读，不能写入
repl-diskless-sync no
repl-diskless-sync-delay 5
repl-disable-tcp-nodelay no
slave-priority 100
appendonly yes                           
appendfilename "appendonly.aof"
appendfsync everysec                        
no-appendfsync-on-rewrite no
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb
aof-load-truncated yes
lua-time-limit 5000
slowlog-log-slower-than 10000
slowlog-max-len 128
latency-monitor-threshold 0
notify-keyspace-events ""
hash-max-ziplist-entries 512
hash-max-ziplist-value 64
list-max-ziplist-entries 512
list-max-ziplist-value 64
set-max-intset-entries 512
zset-max-ziplist-entries 128
zset-max-ziplist-value 64
hll-sparse-max-bytes 3000
activerehashing yes
client-output-buffer-limit normal 0 0 0
client-output-buffer-limit slave 256mb 64mb 60
client-output-buffer-limit pubsub 32mb 8mb 60
hz 10
aof-rewrite-incremental-fsync yes
5）配置sentinel.conf（这个默认没有，需要自建）。三个节点的配置一样。

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
[root@redis-master src]# mkdir -p /usr/local/redis/data/sentinel
[root@redis-master src]# vim /usr/local/redis/etc/sentinel.conf
port 26379
pidfile "/usr/local/redis/var/redis-sentinel.pid"
dir "/usr/local/redis/data/sentinel"
daemonize yes
protected-mode no
logfile "/usr/local/redis/var/redis-sentinel.log"
sentinel monitor redisMaster 192.168.10.202 6379 2 
sentinel down-after-milliseconds redisMaster 10000 
sentinel parallel-syncs redisMaster 1
sentinel failover-timeout redisMaster 60000 
6）启动redis和sentinel（三个节点都要操作）

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
16
17
18
19
20
21
22
设置系统变量
[root@redis-slave02 src]# vim /etc/profile
.......
export PATH=$PATH:/usr/local/redis/bin
[root@redis-slave02 src]# source /etc/profile
 
启动redis和sentinel
[root@redis-master src]# /etc/init.d/redis-server start
Starting redis-server:                                     [  OK  ]
[root@redis-master src]# /etc/init.d/redis-sentinel start
Starting redis-sentinel:                                   [  OK  ]
[root@redis-master src]# lsof -i:6379
COMMAND    PID USER   FD   TYPE  DEVICE SIZE/OFF NODE NAME
redis-ser 2297 root    6u  IPv4 7819726      0t0  TCP *:6379 (LISTEN)
redis-ser 2297 root    8u  IPv4 7819778      0t0  TCP 192.168.10.202:6379->192.168.10.202:56226 (ESTABLISHED)
redis-ser 2297 root    9u  IPv4 7819780      0t0  TCP 192.168.10.202:6379->192.168.10.202:56228 (ESTABLISHED)
redis-sen 2315 root    8u  IPv4 7819777      0t0  TCP 192.168.10.202:56226->192.168.10.202:6379 (ESTABLISHED)
redis-sen 2315 root    9u  IPv4 7819779      0t0  TCP 192.168.10.202:56228->192.168.10.202:6379 (ESTABLISHED)
[root@redis-master src]# lsof -i:26379
COMMAND    PID USER   FD   TYPE  DEVICE SIZE/OFF NODE NAME
redis-sen 2315 root    6u  IPv6 7819772      0t0  TCP *:26379 (LISTEN)
redis-sen 2315 root    7u  IPv4 7819773      0t0  TCP *:26379 (LISTEN)
7）查看redis和sentinel信息

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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87
88
89
90
91
92
93
94
95
96
97
98
99
100
101
102
103
104
105
106
107
108
109
110
111
112
113
114
115
116
117
118
119
120
121
122
123
124
125
126
127
128
129
130
131
132
133
134
135
136
137
138
139
140
141
142
143
144
145
146
147
148
149
150
151
152
153
154
155
156
157
158
159
160
161
162
163
1）查看三个节点的redis的主从关系
[root@redis-master src]# redis-cli -h 192.168.10.202 -p 6379 INFO|grep role
role:master
[root@redis-master src]# redis-cli -h 192.168.10.203 -p 6379 INFO|grep role
role:slave
[root@redis-master src]# redis-cli -h 192.168.10.205 -p 6379 INFO|grep role
role:slave
  
从上面信息可以看出，192.168.10.202是master，192.168.10.203和192.168.10.205是slave
  
2）查看Master节点信息：
[root@redis-master src]# redis-cli -h 192.168.10.202 -p 6379 info Replication
# Replication
role:master
connected_slaves:2
slave0:ip=192.168.10.203,port=6379,state=online,offset=61480,lag=0
slave1:ip=192.168.10.205,port=6379,state=online,offset=61480,lag=0
master_replid:96a1fd63d0ad9e7903851a82382e32d690667bcc
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:61626
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:61626
  
从上面信息看出，此时192.168.10.202的角色为master，有两个slave（203和205）被连接成功.
  
此时打开master的sentinel.conf，在末尾可看到如下自动写入的内容：
[root@redis-master src]# cat /usr/local/redis/etc/sentinel.conf
port 26379
pidfile "/usr/local/redis/var/redis-sentinel.pid"
dir "/usr/local/redis/data/sentinel"
daemonize yes
protected-mode no
logfile "/usr/local/redis/var/redis-sentinel.log"
sentinel myid c165761901b5ea3cd2d622bbf13f4c99eb73c1bc
sentinel monitor redisMaster 192.168.10.202 6379 2
sentinel down-after-milliseconds redisMaster 10000
sentinel failover-timeout redisMaster 60000
# Generated by CONFIG REWRITE
sentinel config-epoch redisMaster 0
sentinel leader-epoch redisMaster 0
sentinel known-slave redisMaster 192.168.10.203 6379
sentinel known-slave redisMaster 192.168.10.205 6379
sentinel known-sentinel redisMaster 192.168.10.205 26379 cc25d5f0e37803e888732d63deae3761c9f91e1d
sentinel known-sentinel redisMaster 192.168.10.203 26379 e1505ffc65f787871febfde2f27b762f70cddd71
sentinel current-epoch 0
  
[root@redis-master ~]# redis-cli -h 192.168.10.205 -p 26379 info Sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=redisMaster,status=ok,address=192.168.10.202:6379,slaves=2,sentinels=3
 
3）查看Slave节点信息：
  
先查看salve01节点信息
[root@redis-slave01 src]# redis-cli -h 192.168.10.203 -p 6379 info Replication
# Replication
role:slave
master_host:192.168.10.202
master_port:6379
master_link_status:up
master_last_io_seconds_ago:0
master_sync_in_progress:0
slave_repl_offset:96744
slave_priority:100
slave_read_only:1
connected_slaves:0
master_replid:96a1fd63d0ad9e7903851a82382e32d690667bcc
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:96744
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:96744
  
此时192.168.10.203的角色为slave，它们所属的master为220。
此时打开slave的sentinel.conf，在末尾可看到如下自动写入的内容：
[root@redis-slave01 src]# cat /usr/local/redis/etc/sentinel.conf
port 26379
pidfile "/usr/local/redis/var/redis-sentinel.pid"
dir "/usr/local/redis/data/sentinel"
daemonize yes
protected-mode no
logfile "/usr/local/redis/var/redis-sentinel.log"
sentinel myid e1505ffc65f787871febfde2f27b762f70cddd71
sentinel monitor redisMaster 192.168.10.202 6379 2
sentinel down-after-milliseconds redisMaster 10000
sentinel failover-timeout redisMaster 60000
# Generated by CONFIG REWRITE
sentinel config-epoch redisMaster 0
sentinel leader-epoch redisMaster 0
sentinel known-slave redisMaster 192.168.10.203 6379
sentinel known-slave redisMaster 192.168.10.205 6379
sentinel known-sentinel redisMaster 192.168.10.205 26379 cc25d5f0e37803e888732d63deae3761c9f91e1d
sentinel known-sentinel redisMaster 192.168.10.202 26379 c165761901b5ea3cd2d622bbf13f4c99eb73c1bc
sentinel current-epoch 0
 
[root@redis-slave01 ~]# redis-cli -h 192.168.10.203 -p 26379 info Sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=redisMaster,status=ok,address=192.168.10.202:6379,slaves=2,sentinels=3
 
同样查看slave02节点的信息
[root@redis-slave02 src]# redis-cli -h 192.168.10.205 -p 6379 info Replication
# Replication
role:slave
master_host:192.168.10.202
master_port:6379
master_link_status:up
master_last_io_seconds_ago:0
master_sync_in_progress:0
slave_repl_offset:99678
slave_priority:100
slave_read_only:1
connected_slaves:0
master_replid:96a1fd63d0ad9e7903851a82382e32d690667bcc
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:99678
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:170
repl_backlog_histlen:99509
  
[root@redis-slave02 src]# cat /usr/local/redis/etc/sentinel.conf
port 26379
pidfile "/usr/local/redis/var/redis-sentinel.pid"
dir "/usr/local/redis/data/sentinel"
daemonize yes
protected-mode no
logfile "/usr/local/redis/var/redis-sentinel.log"
sentinel myid cc25d5f0e37803e888732d63deae3761c9f91e1d
sentinel monitor redisMaster 192.168.10.202 6379 2
sentinel down-after-milliseconds redisMaster 10000
sentinel failover-timeout redisMaster 60000
# Generated by CONFIG REWRITE
sentinel config-epoch redisMaster 0
sentinel leader-epoch redisMaster 0
sentinel known-slave redisMaster 192.168.10.205 6379
sentinel known-slave redisMaster 192.168.10.203 6379
sentinel known-sentinel redisMaster 192.168.10.203 26379 e1505ffc65f787871febfde2f27b762f70cddd71
sentinel known-sentinel redisMaster 192.168.10.202 26379 c165761901b5ea3cd2d622bbf13f4c99eb73c1bc
sentinel current-epoch 0
 
[root@redis-slave02 ~]# redis-cli -h 192.168.10.205 -p 26379 info Sentinel
# Sentinel
sentinel_masters:1
sentinel_tilt:0
sentinel_running_scripts:0
sentinel_scripts_queue_length:0
sentinel_simulate_failure_flags:0
master0:name=redisMaster,status=ok,address=192.168.10.202:6379,slaves=2,sentinels=3
8）客户端写入测试数据

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
16
17
18
19
20
21
22
23
客户端连接master节点，写入一条数据
[root@redis-master src]# redis-cli  -h 192.168.10.202 -p 6379
192.168.10.202:6379> set name kevin;
OK
192.168.10.202:6379> get name;
"kevin";
 
然后客户端再连接任意slave节点，通过get获取上面的那条数据
[root@redis-master src]# redis-cli  -h 192.168.10.203 -p 6379
192.168.10.203:6379> get name
"kevin;"
192.168.10.203:6379> set name grace;
(error) READONLY You can't write against a read only slave.
192.168.10.203:6379>
 
[root@redis-master src]# redis-cli  -h 192.168.10.205 -p 6379
192.168.10.205:6379> get name
"kevin;"
192.168.10.205:6379> set name grace;
(error) READONLY You can't write against a read only slave.
192.168.10.205:6379>
 
由上面测试信息可知，master节点可以写入，可以读取；而slave节点默认只能读取，不能写入！这就实现了主从复制，读写分离了！
9）模拟故障（通过sentinel实现主从切换，sentinel也要部署多台，即集群模式，防止单台sentinel挂掉情况）

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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87
88
89
90
91
92
93
94
95
96
97
98
99
100
101
102
103
104
105
106
107
108
109
110
111
112
113
114
115
116
117
118
119
120
121
122
123
124
125
126
127
128
129
130
131
132
133
134
135
136
137
138
139
140
141
142
143
144
145
146
147
148
149
150
151
152
153
154
155
156
157
158
159
160
161
162
163
164
165
166
167
168
169
170
171
172
173
174
175
176
177
178
179
180
181
182
183
184
185
186
187
188
189
190
191
192
193
194
195
196
197
198
199
1）关掉任意一个slave节点（比如关闭掉slave01节点），所有节点的sentinel都可以检测到，出现如下示例信息：
[root@redis-master src]# redis-cli  -h 192.168.10.203 -p 6379
192.168.10.203:6379> get name
"kevin;"
192.168.10.203:6379> set name grace;
(error) READONLY You can't write against a read only slave.
192.168.10.203:6379> shutdown                             
not connected>
 
说明：shutdown命令表示关闭redis
 
从上可看出203被sentinel检测到已处于关闭状态，此时再来查看剩余节点的主从信息，它们的角色不会发生变化，只是master上的connected_slaves变为了1。
[root@redis-master src]# redis-cli  -h 192.168.10.202 -p 6379 info replication
# Replication
role:master
connected_slaves:1
slave0:ip=192.168.10.205,port=6379,state=online,offset=219376,lag=1
master_replid:96a1fd63d0ad9e7903851a82382e32d690667bcc
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:219376
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:219376
 
查看sentinel日志（任意节点上查看），发现203节点已经进入"+sdown"状态
[root@redis-master src]# tail -f /usr/local/redis/var/redis-sentinel.log
2315:X 08 May 18:49:51.429 * Increased maximum number of open files to 10032 (it was originally set to 1024).
2315:X 08 May 18:49:51.431 * Running mode=sentinel, port=26379.
2315:X 08 May 18:49:51.431 # WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
2315:X 08 May 18:49:51.463 # Sentinel ID is c165761901b5ea3cd2d622bbf13f4c99eb73c1bc
2315:X 08 May 18:49:51.463 # +monitor master redisMaster 192.168.10.202 6379 quorum 2
2315:X 08 May 18:50:11.544 * +slave slave 192.168.10.203:6379 192.168.10.203 6379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 18:50:11.574 * +slave slave 192.168.10.205:6379 192.168.10.205 6379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 18:50:15.088 * +sentinel sentinel e1505ffc65f787871febfde2f27b762f70cddd71 192.168.10.203 26379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 18:50:16.075 * +sentinel sentinel cc25d5f0e37803e888732d63deae3761c9f91e1d 192.168.10.205 26379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 19:06:07.669 # +sdown slave 192.168.10.203:6379 192.168.10.203 6379 @ redisMaster 192.168.10.202 6379
 
然后重启上面被关闭的slave节点（即192.168.10.203），所有节点的sentinel都可以检测到，可看出221又被sentinel检测到已处于可用状态，此时再来查看节点的主从信息，
它们的角色仍然不会发生变化，master上的connected_slaves又变为了2
[root@redis-slave01 src]# /etc/init.d/redis-server restart
Stopping redis-server:                                     [FAILED]
Starting redis-server:                                     [  OK  ]
[root@redis-slave01 src]# /etc/init.d/redis-server restart
Stopping redis-server:                                     [  OK  ]
Starting redis-server:                                     [  OK  ]
 
[root@redis-master src]# redis-cli  -h 192.168.10.202 -p 6379 info replication
# Replication
role:master
connected_slaves:2
slave0:ip=192.168.10.205,port=6379,state=online,offset=268216,lag=0
slave1:ip=192.168.10.203,port=6379,state=online,offset=268070,lag=1
master_replid:96a1fd63d0ad9e7903851a82382e32d690667bcc
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:268216
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:268216
 
查看sentinel日志（任意节点上查看），发现203节点已经进入"-sdown"状态
[root@redis-master src]# tail -f /usr/local/redis/var/redis-sentinel.log
2315:X 08 May 18:49:51.429 * Increased maximum number of open files to 10032 (it was originally set to 1024).
2315:X 08 May 18:49:51.431 * Running mode=sentinel, port=26379.
2315:X 08 May 18:49:51.431 # WARNING: The TCP backlog setting of 511 cannot be enforced because /proc/sys/net/core/somaxconn is set to the lower value of 128.
2315:X 08 May 18:49:51.463 # Sentinel ID is c165761901b5ea3cd2d622bbf13f4c99eb73c1bc
2315:X 08 May 18:49:51.463 # +monitor master redisMaster 192.168.10.202 6379 quorum 2
2315:X 08 May 18:50:11.544 * +slave slave 192.168.10.203:6379 192.168.10.203 6379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 18:50:11.574 * +slave slave 192.168.10.205:6379 192.168.10.205 6379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 18:50:15.088 * +sentinel sentinel e1505ffc65f787871febfde2f27b762f70cddd71 192.168.10.203 26379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 18:50:16.075 * +sentinel sentinel cc25d5f0e37803e888732d63deae3761c9f91e1d 192.168.10.205 26379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 19:06:07.669 # +sdown slave 192.168.10.203:6379 192.168.10.203 6379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 19:10:14.965 * +reboot slave 192.168.10.203:6379 192.168.10.203 6379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 19:10:15.020 # -sdown slave 192.168.10.203:6379 192.168.10.203 6379 @ redisMaster 192.168.10.202 6379
 
=======================================================================================================
2）关掉master节点（即192.168.10.202），待所有节点的sentinel都检测到后（稍等一会，2-3秒钟时间），再来查看两个Slave节点的主从信息，发现其中一个节点的角色通过选举后会成为
master节点了！
[root@redis-master src]# redis-cli  -h 192.168.10.202 -p 6379
192.168.10.202:6379> shutdown
not connected>
 
查看sentinel日志（任意节点上查看），发现202节点已经进入"+sdown"状态
[root@redis-master src]# tail -f /usr/local/redis/var/redis-sentinel.log
2315:X 08 May 19:17:03.722 # +failover-state-reconf-slaves master redisMaster 192.168.10.202 6379
2315:X 08 May 19:17:03.760 * +slave-reconf-sent slave 192.168.10.203:6379 192.168.10.203 6379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 19:17:04.015 * +slave-reconf-inprog slave 192.168.10.203:6379 192.168.10.203 6379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 19:17:04.459 # -odown master redisMaster 192.168.10.202 6379
2315:X 08 May 19:17:05.047 * +slave-reconf-done slave 192.168.10.203:6379 192.168.10.203 6379 @ redisMaster 192.168.10.202 6379
2315:X 08 May 19:17:05.131 # +failover-end master redisMaster 192.168.10.202 6379
2315:X 08 May 19:17:05.131 # +switch-master redisMaster 192.168.10.202 6379 192.168.10.205 6379
2315:X 08 May 19:17:05.131 * +slave slave 192.168.10.203:6379 192.168.10.203 6379 @ redisMaster 192.168.10.205 6379
2315:X 08 May 19:17:05.131 * +slave slave 192.168.10.202:6379 192.168.10.202 6379 @ redisMaster 192.168.10.205 6379
2315:X 08 May 19:17:15.170 # +sdown slave 192.168.10.202:6379 192.168.10.202 6379 @ redisMaster 192.168.10.205 6379
 
[root@redis-slave01 src]# redis-cli -h 192.168.10.203 -p 6379 INFO|grep role
role:slave
[root@redis-slave01 src]# redis-cli -h 192.168.10.205 -p 6379 INFO|grep role     
role:master
 
[root@redis-slave01 src]# redis-cli  -h 192.168.10.205 -p 6379 info replication
# Replication
role:master
connected_slaves:1
slave0:ip=192.168.10.203,port=6379,state=online,offset=348860,lag=1
master_replid:a51f87958cea0b1e8fe2c83542ca6cebace53bf7
master_replid2:96a1fd63d0ad9e7903851a82382e32d690667bcc
master_repl_offset:349152
second_repl_offset:342824
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:170
repl_backlog_histlen:348983
 
[root@redis-slave01 src]# redis-cli  -h 192.168.10.203 -p 6379 info replication
# Replication
role:slave
master_host:192.168.10.205
master_port:6379
master_link_status:up
master_last_io_seconds_ago:0
master_sync_in_progress:0
slave_repl_offset:347546
slave_priority:100
slave_read_only:1
connected_slaves:0
master_replid:a51f87958cea0b1e8fe2c83542ca6cebace53bf7
master_replid2:96a1fd63d0ad9e7903851a82382e32d690667bcc
master_repl_offset:347546
second_repl_offset:342824
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:283207
repl_backlog_histlen:64340
 
由上可知，当master节点（即192.168.10.202）的redis关闭后，slave02节点（即192.168.10.205）变成了新的master节点，而slave01（即192.168.10.203）成为
了slave02的从节点！
 
192.168.10.205节点此时被选举为master，此时打开的205节点的redis.conf文件，slaveof配置项已被自动删除了。
而203从节点的redis.conf文件中slaveof配置项的值被自动修改为192.168.10.205 6379。
[root@redis-slave02 src]# cat /usr/local/redis/etc/redis.conf|grep slaveof
[root@redis-slave01 src]# cat /usr/local/redis/etc/redis.conf|grep slaveof
slaveof 192.168.10.205 6379
 
在这个新master上（即192.168.10.205）执行诸如set这样的写入操作将被成功执行
[root@redis-slave01 src]# redis-cli  -h 192.168.10.205 -p 6379
192.168.10.205:6379> set name beijing;
OK
 
[root@redis-master src]# redis-cli  -h 192.168.10.203 -p 6379
192.168.10.203:6379> get name
"beijing;"
192.168.10.203:6379> set name tianjin;
(error) READONLY You can't write against a read only slave.
192.168.10.203:6379>
 
重启192.168.10.202节点的redis，待所有节点的sentinel都检测到后，再来查看所有节点的主从信息，此时192.168.10.205节点的master角色不会被重新抢占，
而192.168.10.202节点的角色会从原来的master变为了slave。
[root@redis-master src]# /etc/init.d/redis-server restart
Stopping redis-server:                                     [FAILED]
Starting redis-server:                                     [  OK  ]
[root@redis-master src]# /etc/init.d/redis-server restart
Stopping redis-server:                                     [  OK  ]
Starting redis-server:                                     [  OK  ]
 
[root@redis-master src]# redis-cli -h 192.168.10.202 -p 6379 INFO|grep role
role:slave
[root@redis-master src]# redis-cli -h 192.168.10.203 -p 6379 INFO|grep role
role:slave
[root@redis-master src]# redis-cli -h 192.168.10.205 -p 6379 INFO|grep role
role:master
[root@redis-master src]# redis-cli  -h 192.168.10.205 -p 6379 info replication
# Replication
role:master
connected_slaves:2
slave0:ip=192.168.10.203,port=6379,state=online,offset=545410,lag=1
slave1:ip=192.168.10.202,port=6379,state=online,offset=545410,lag=1
master_replid:a51f87958cea0b1e8fe2c83542ca6cebace53bf7
master_replid2:96a1fd63d0ad9e7903851a82382e32d690667bcc
master_repl_offset:545556
second_repl_offset:342824
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:170
repl_backlog_histlen:545387
 
[root@redis-master src]# redis-cli  -h 192.168.10.202 -p 6379
192.168.10.202:6379> get name
"beijing;"
 
此时登录192.168.10.202节点的redis，执行"get name"得到的值为beijing，而不是原来的kevin，因为192.168.10.202节点的redis重启后会自动从新的master中同步
数据。此时打开192.168.10.202节点的redis.conf文件，会在末尾找到如下信息：
[root@redis-master src]# cat /usr/local/redis/etc/redis.conf|grep slaveof
slaveof 192.168.10.205 6379
 
到此，已经验证出了redis sentinel可以自行实现主从的故障切换了！
10）客户端如何连接redis sentinel？

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
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
54
55
56
57
58
59
60
61
62
63
64
65
66
67
68
69
70
71
72
73
74
75
76
77
78
79
80
81
82
83
84
85
86
87
88
89
90
91
92
93
94
95
96
97
98
99
100
101
102
103
104
105
106
107
客户端配置连接的是sentinel信息，比如连接sentinel.conf文件中定义的master名称。在sentinel监听时，当master节点挂了，它会在slave节点中自动选举出新
的master节点，而当挂了的老master节点重新恢复后就会成为新的slave节点。对于客户端来说，redis主从切换后它不需要修改连接配置。
 
下面列出几个客户端连接redis sentinel的例子
 
1）java客户端在jedis中使用redis sentinel哨兵方式连接
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans">
    <bean id="jedisPoolConfig" class="redis.clients.jedis.JedisPoolConfig">
         <property name="maxTotal" value="1000"/>
         <property name="maxIdle" value="10"/>
         <property name="minIdle" value="1"/>
         <property name="maxWaitMillis" value="30000"/>
         <property name="testOnBorrow" value="true"/>
         <property name="testOnReturn" value="true"/>
         <property name="testWhileIdle" value="true"/>
    </bean>
 
    <bean id="cacheService" class="sentinel.CacheServiceImpl" destroy-method="destroy">
        <property name="jedisSentinlePool">
            <bean class="redis.clients.jedis.JedisSentinelPool">
                 <constructor-arg index="0" value="mymaster" />
                 <constructor-arg index="1">
                     <set>
                         <value>192.168.10.202:26379</value>
                         <value>192.168.10.203:26379</value>
                         <value>192.168.10.205:26379</value>
                     </set>
                 </constructor-arg>
                 <constructor-arg index="2" ref="jedisPoolConfig" />
            </bean>
        </property>
    </bean>
</beans>
 
2）python连接redis sentinel集群（需要安装python redis客户端，即执行"pip install redis"）
#!/usr/bin/env python
# -*- coding:utf-8 -*-
 
import redis
from redis.sentinel import Sentinel
 
# 连接哨兵服务器(主机名也可以用域名)
sentinel = Sentinel([('192.168.10.202', 26379),
                     ('192.168.10.203', 26379),
                     ('192.168.10.205', 26379)
             ],
                    socket_timeout=0.5)
 
# 获取主服务器地址
master = sentinel.discover_master('mymaster')
print(master)
# 输出：('192.168.10.202', 26379)
 
# 获取从服务器地址
slave = sentinel.discover_slaves('mymaster')
print(slave)
# 输出：[('192.168.10.203', 26379), ('192.168.10.205', 26379), ('172.31.0.5', 26379)]
 
# 获取主服务器进行写入
master = sentinel.master_for('mymaster', socket_timeout=0.5, password='redis_auth_pass', db=15)
w_ret = master.set('foo', 'bar')
# 输出：True
 
# # 获取从服务器进行读取（默认是round-roubin）
slave = sentinel.slave_for('mymaster', socket_timeout=0.5, password='redis_auth_pass', db=15)
r_ret = slave.get('foo')
print(r_ret)
# # 输出：bar
 
3）Java客户端连接Redis（单sentinel）.
下面例子中好的redisMaster和20180408是在sentinel.conf中定义的master名称和连接密码
package com.hiifit.cloudplatform.gaia.test;
import java.util.HashSet;
import java.util.Set;
 
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
 
public class RedisSentinelTest {
 
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
 
        Set<String> sentinels = new HashSet<String>();
        String hostAndPort1 = "192.168.10.205:26379";
        sentinels.add(hostAndPort1);
 
        String clusterName = "redisMaster";
        String password = "20180408";
 
        JedisSentinelPool redisSentinelJedisPool = new JedisSentinelPool(clusterName,sentinels,password);
 
        Jedis jedis = null;
        try {
            jedis = redisSentinelJedisPool.getResource();
            jedis.set("key", "value");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            redisSentinelJedisPool.returnBrokenResource(jedis);
        }
 
        redisSentinelJedisPool.close();
    }
 
}
*************** 当你发现自己的才华撑不起野心时，就请安静下来学习吧！***************
```

```text
Linux三剑客Awk、Sed、Grep 命令详解
头像
民工哥
2020-11-21
阅读 7 分钟
20
Linux三剑客Awk命令详解
命令名称

Awk  pattern scanning and processing language
命令作用

对文本和数据进行处理

详细说明

awk 是一种编程语言，用于在linux/unix下对文本和数据进行处理。数据可以来自标准输(stdin)、一个或多个文件，或其它命令的输出。它在命令行中使用，但更多是作为脚本来使用。awk有很多内建的功能，比如数组、函数等，这是它和C语言的相同之处，灵活性是awk最大的优势。

语法格式

awk [options] 'scripts' var=value filename
常用参数

-F 指定分隔符（可以是字符串或正则表达式）
-f 从脚本文件中读取awk命令
-v var=value 赋值变量，将外部变量传递给awk
脚本基本结构

 awk 'BEGIN{ print "start" } pattern{ commands } END{ print "end" }' filename
一个awk脚本通常由BEGIN语句+模式匹配+END语句三部分组成,这三部分都是可选项

工作原理:

第一步执行BEGIN 语句
第二步从文件或标准输入读取一行，然后再执行pattern语句，逐行扫描文件到文件全部被读取
第三步执行END语句
实例展示:

echo "hello " | awk 'BEGIN{ print "welcome" } END{ print "2017-08-08" }'
welcome
2017-08-08

echo -e "hello" | awk 'BEGIN{ print "welcome" } {print} END{ print "2017-08-08" }'
welcome
hello
2017-08-08
#不加print参数时默认只打印当前的行

echo|awk '{ a="hello"; b="nihao"; c="mingongge"; print a,b,c; }'
hello nihao mingongge
#使用print以逗号分隔时，打印则是以空格分界

echo|awk '{ a="mgg"; b="mingg"; c="mingongge"; print a" is "b" or "c; }'
mgg is mingg or mingongge
#awk的print语句中双引号其实就是个拼接作用
Awk的变量

内置变量

$0   #当前记录
$1~$n #当前记录的第N个字段
FS   #输入字段分隔符（-F相同作用）默认空格
RS   #输入记录分割符，默认换行符
NF   #字段个数就是列
NR   #记录数，就是行号，默认从1开始
OFS  #输出字段分隔符，默认空格
ORS  #输出记录分割符，默认换行符  
外部变量

[mingongge@ ~]#a=100
[mingongge@ ~]#b=100
[mingongge@ ~]#echo |awk '{print v1*v2 }' v1=$a v2=$b
10000
Awk运算与判断

算术运算符

 加减
/ & 乘 除 求余
^ *  求幂
++ -- 增加或减少，作为前缀或后缀
[mingongge@ ~]#awk 'BEGIN{a="b";print a,a++,a--,++a;}'
b 0 1 1

[mingongge@ ~]#awk 'BEGIN{a="0";print a,a++,a--,++a;}'
0 0 1 1

[mingongge@ ~]#awk 'BEGIN{a="0";print a,a++,--a,++a;}'
0 0 0 1

#和其它编程语言一样，所有用作算术运算符进行操作，操作数自动转为数值，所有非数值都变为0
赋值运算符

= += -= *= /= %= ^= **=
正则运算符

~ !~  匹配正则表达式/不匹配正则表达式
逻辑运算符

||  &&  逻辑或  逻辑与
关系运算符

< <= > >= != = 
其它运算符

$     #字段引用 
空格  #字符串链接符
?:    #三目运算符
ln    #数组中是否存在某键值
Awk正则

^    行首定位符
$    行尾定位符
.    匹配任意单个字符
*    匹配0个或多个前导字符（包括回车）
+    匹配1个或多个前导字符
?    匹配0个或1个前导字符 
[]   匹配指定字符组内的任意一个字符/^[ab]
[^]  匹配不在指定字符组内的任意一个字符
()   子表达式
|    或者
\    转义符
~,!~ 匹配或不匹配的条件语句
x{m} x字符重复m次
x{m,} x字符至少重复m次
X{m,n} x字符至少重复m次但不起过n次（需指定参数-posix或--re-interval）
Linux三剑客Sed命令详解
命令名称

Sed 一个强大的流式文本编辑器

详细说明

sed是一种流编辑器，也是文本处理中非常好的工具，配合正则使用更强大处理时，把当前处理的行存储在临时缓冲区中，称为“模式空间”，接着用sed命令处理缓冲区的内容，完成后输出到屏幕，接着处理下一行.

命令格式

 sed [options] 'command' file(s)

 sed [options] -f scriptfile file(s)
常用参数

-e #以指定的指令来处理输入的文本文件
-n  #取消默认输出（如果和p命令同时使用只会打印发生改变的行**）
-h #帮助
-V #显示版本信息
常用命令

a #在当前行下面插入文本 

i #在当前行上面插入文本 

c #把选定的行改为新的文本 

d #删除，删除选择的行 

D #删除模板块的第一行 

s #替换指定字符 

h #拷贝模板块的内容到内存中的缓冲区

H #追加模板块的内容到内存中的缓冲区 

g #获得内存缓冲区的内容，并替代当前模板块中的文本 

G #获得内存缓冲区的内容，并追加到当前模板块文本的后面 

l #列表不能打印字符的清单 

n #读取下一个输入行，用下一个命令处理新的行而不是用第一个命令 

N #追加下一个输入行到模板块后面并在二者间嵌入一个新行，改变当前行号码 

p  #打印匹配的行 

P #(大写)打印模板的第一行

q  #退出Sed 

b  #lable 分支到脚本中带有标记的地方，如果分支不存在则分支到脚本的末尾 

r #file 从file中读行 

t #label if分支，从最后一行开始，条件一旦满足或者T，t命令，将导致分支到带有标号的命令处，或者到脚本的末尾 

T #label 错误分支，从最后一行开始，一旦发生错误或者T，t命令，将导致分支到带有标号的命令处，或者到脚本的末尾 

w #file 写并追加模板块到file末尾

W #file 写并追加模板块的第一行到file末尾

!  #表示后面的命令对所有没有被选定的行发生作用 

=  #打印当前行号码

# #把注释扩展到下一个换行符以前
Sed替换命令

g #表示行内全面替换（全局替换配合s命令使用）

p #表示打印行 

w  #表示把行写入一个文件 

x #表示互换模板块中的文本和缓冲区中的文本 

y #表示把一个字符翻译为另外的字符（但是不用于正则表达式） 

1  #子串匹配标记 

& #已匹配字符串标记
Sed正则

^ #匹配行开始 

$ #匹配行结束

. #匹配一个非换行符的任意字符

 #匹配0个或多个字符

[] #匹配一个指定范围内的字符

[^]  #匹配一个不在指定范围内的字符 

(..) #匹配子串

&  #保存搜索字符用来替换其他字符

< #匹配单词的开始

>  #匹配单词的结束

x{m} #重复字符x，m次

x{m,} #重复字符x，至少m次 

x{m,n} #重复字符x，至少m次，不多于n次
Sed常用实例

1、替换操作

echo "hello world" |sed 's/ /-/1g'
hello-world 
#从第一个空格开始全局替换成-，只不过文本中只有一个空格
2、删除操作

sed '/^$/d' filename #删除空白行

sed '2d' filename #删除第二行

sed '2，$d' filename #删除第二直到未尾所有行

sed '$d' filename #删除最后一行

sed '/^test/'d filename #删除以test开头行
3、匹配替换

echo "hello world" |sed 's/w+/[&]/g'

[hello] [world]

echo "hello world" |sed 's/w+/"&"/g'

"hello" "world"

#w+匹配每一个单词，&表示匹配到的字符串

echo AAA bbb |sed 's/([A-Z]+) ([a-z]+)/[2] [1]/'

[bbb] [AAA]

#子串匹配替换
4、选定范围

sed -n '/= 0/,/max/p' svnserve.conf

#min-encryption = 0

#max-encryption = 256

#所有在=0到max范围内的行都会被打印出来
5、sed多点编辑功能（-e）

[root@centos001 ~]#cat -n test 
1  this is a test file
2  welcome
3  to
4  here
5  hello WORLD
6
7  linux centos6.8
8  redhat

sed -e '2,6d' -e 's/linux centos6.8/Linux Centos6.8/' test
this is a test file
Linux Centos6.8
redhat
#如果两条命令功能一样，那么就需要用到下面的参数

sed --expression='s/linux centos6.8/Linux Centos6.8/' --expression='s/to/TO/' test**
this is a test file
welcome
TO
here
hello WORLD
Linux CenTOs6.8
redhat
6、读入与写入

[root@centos001 ~]#cat test1
welcom 
to 
here

[root@centos001 ~]#sed '/here/r test1' test
this is a test file
welcome
to
here
#welcom
to
here#
hello WORLD
linux centos6.8
redhat

#将test1的文件内容读取显示所有匹配here行的后面
sed -n '/centos6.8/w test2' test

[root@centos001 ~]#cat test2
linux centos6.8

#将test文件匹配到centos6.8的所有行都写入到test2文件中，文件可以不存在.
#如果文件存在，就会被重定向不是追加
7、追加与插入

[root@centos001 ~]#sed '/^l/a2017-08-08' test2
linux centos6.8
2017-08-08
#在匹配以l开头的行的后面追加2017-08-08

[root@centos001 ~]#sed '1a2017-08-08' test2
linux centos6.8
2017-08-08
#在第一行的后面追加2017-08-08

[root@centos001 ~]#sed '/^l/i2017-08-08' test2
2017-08-08
linux centos6.8
#在匹配以l开头的行的前面插入2017-08-08
#######以上操作是不会改变文件内容################

[root@centos001 ~]#sed -i '/^l/i2017-08-08' test2
[root@centos001 ~]#cat test2
2017-08-08
linux centos6.8
8、其它命令实例

[root@centos001 ~]#cat -n test2
 1 2017-08-08
 2 linux centos6.8
 3 08
 4
 5 test

[root@centos001 ~]#**sed '/08/{ n; s/l/L/; }' test2
2017-08-08
Linux centos6.8
08
test
#如果08匹配到就跳到下一行，将小写l替换成大写，注意到第三行也是被匹配到
#但是后面的条件不满足，所有没有被替换

[root@centos001 ~]#sed '1,4y/8/9/' test2
2017-09-09
linux centos6.9
09
test
#将1至4行所有的数字8替换成9

[root@centos001 ~]#**sed '1q' test2**
2017-08-08
#打印第一行内容后退出
9、打印奇数或公偶数行

[root@centos001 ~]#sed -n 'p;n' test2
20170808
08

[root@centos001 ~]#sed -n 'n;p' test2
linux centos6.8
test

[root@centos001 ~]#sed -n '1~2p' test2
20170808
08

[root@centos001 ~]#sed -n '2~2p' test2
linux centos6.8
test
10、打印匹配字符串行的下一行

[root@centos001 ~]#sed -n '/linux/{n;p}' test2
08

[root@centos001 ~]#awk '/linux/{getline; print}' test2
08
Linux三剑客Grep 命令详解
命令名称：
grep

命令作用：

文本查找或搜索工具

详细说明：

同样可以配合正则表达式来搜索文本，并将匹配的行打印输出,也可用于过滤与搜索特定字符串，使用十分灵活

常用参数：

-a  #不要忽略二进制数据

-A  #除了显示符合范本样式的那一行之外，并显示该行之后的内容

-b  #在显示符合范本样式的那一行之外，并显示该行之前的内容

-B  #除了显示符合样式的那一行之外，并显示该行之前的内容

-c  #计算符合范本样式的列数

-C  #除了显示符合范本样式的那一列之外，并显示该列之前后的内容

-d  #当指定要查找的是目录而非文件时，必须使用这项参数，否则grep命令将回报信息并停止动作

-e  #指定字符串作为查找文件内容的范本样式

-E  #将范本样式为延伸的普通表示法来使用，意味着使用能使用扩展正则表达式

-f  #指定范本文件，其内容有一个或多个范本样式，让grep查找符合范本条件的文件内容，格式为每一列的范本样式

-F  #将范本样式视为固定字符串的列表

-G  #将范本样式视为普通的表示法来使用

-h  #在显示符合范本样式的那一列之前，不标示该列所属的文件名称

-H  #在显示符合范本样式的那一列之前，标示该列的文件名称

-i  #忽略字符大小写的差别

-l  #列出文件内容符合指定的范本样式的文件名称

-L  #列出文件内容不符合指定的范本样式的文件名称

-n  #在显示符合范本样式的那一列之前，标示出该列的编号

-q  #不显示任何信息

-R/-r #此参数的效果和指定“-d recurse”参数相同

-s  #不显示错误信息

-v  #反转查找

-V  #显示版本信息 

-w  #只显示全字符合的列

-x  #只显示全列符合的列

-y  #此参数效果跟“-i”相同

-o  #只输出文件中匹配到的部分
正则表达式

^  #匹配以XX开头的行

$  #匹配以XX结尾的行
常用实例：

1、在多个文件中查找：

grep "file" file_1 file_2 file_3 
2、输出除之外的所有行 -v 选项：

grep -v "file" file_name
3、标记匹配颜色 --color=auto 选项：

grep "file" file_name --color=auto
4、使用正则表达式 -E 选项：

grep -E "[1-9]+"

egrep "[1-9]+"
5、只输出文件中匹配到的部分 -o 选项：

echo this is a test line. | grep -o -E "[a-z]+."

line.

echo this is a test line. | egrep -o "[a-z]+."

line.
6、统计文件或者文本中包含匹配字符串的行数-c 选项：

grep -c "text" file_name

2
7、输出包含匹配字符串的行数 -n 选项：

grep "text" -n file_name

或

cat file_name | grep "text" -n
8、多个文件

grep "text" -n file_1 file_2
9、搜索多个文件并查找匹配文本在哪些文件中：

grep -l "text" file1 file2 file3...
10、grep递归搜索文件

在多级目录中对文本进行递归搜索：

grep "text" . -r -n
11、忽略匹配样式中的字符大小写：

echo "hello world" | grep -i "HELLO"

hello
12、选项 -e 指定多个匹配样式：

echo this is a text line | grep -e "is" -e "line" -o

is

line
13、也可以使用 -f 选项来匹配多个样式，在样式文件中逐行写出需要匹配的字符。

cat patfile

aaa

bbb

echo aaa bbb ccc ddd eee | grep -f patfile -o
14、在grep搜索结果中包括或者排除指定文件：

只在目录中所有的.php和.html文件中递归搜索字符"main()"

grep "main()" . -r --include *.{php,html}
15、在搜索结果中排除所有README文件

grep "main()" . -r --exclude "README"
16、在搜索结果中排除filelist文件列表里的文件

grep "main()" . -r --exclude-from filelist
Linux三剑客Awk、Sed、Grep 命令详解,到这里就介绍完了。欢迎评论、点赞、转发分享支持。

注：未经授权，禁止任何方式的转载。
```

```text
Linux 文本处理三剑客：grep、sed 和 awk
Beritra
Beritra
软件
来自专栏 · Java学习笔记
98 人赞同了该文章
awk、grep、sed是linux操作文本的三大利器，合称文本三剑客，也是必须掌握的linux命令之一。三者的功能都是处理文本，但侧重点各不相同，其中属awk功能最强大，但也最复杂。grep更适合单纯的查找或匹配文本，sed更适合编辑匹配到的文本，awk更适合格式化文本，对文本进行较复杂格式处理。

grep
Linux 系统中 grep 命令是一种强大的文本搜索工具，它能使用正则表达式搜索文本，并把匹配的行打印出来。grep全称是 Global Regular Expression Print，表示全局正则表达式版本，它的使用权限是所有用户。

grep可用于shell脚本，因为grep通过返回一个状态值来说明搜索的状态，如果模板搜索成功，则返回0，如果搜索不成功，则返回1，如果搜索的文件不存在，则返回2。我们利用这些返回值就可进行一些自动化的文本处理工作。

命令的基本格式：

grep [option] pattern file
即便不熟悉这个命令，应该大多数同学也用过查询进程的命令：

ps -ef|grep xxxx
这就是 grep 的一个基本用法，从所有进程中搜索某个进程。

grep 常用的参数如下：

-A<行数 x>：除了显示符合范本样式的那一列之外，并显示该行之后的 x 行内容。
-B<行数 x>：除了显示符合样式的那一行之外，并显示该行之前的 x 行内容。
-C<行数 x>：除了显示符合样式的那一行之外，并显示该行之前后的 x 行内容。
-c：统计匹配的行数
-e ：实现多个选项间的逻辑or 关系
-E：扩展的正则表达式
-f 文件名：从文件获取 PATTERN 匹配
-F ：相当于fgrep
-i --ignore-case #忽略字符大小写的差别。
-n：显示匹配的行号
-o：仅显示匹配到的字符串
-q： 静默模式，不输出任何信息
-s：不显示错误信息。
-v：显示不被 pattern 匹配到的行，相当于[^] 反向匹配
-w ：匹配 整个单词
前三个 A、B、C 参数很容易理解，举个栗子，假设我们有一个文件，文件名是 test，内容是从 1 到 9，每个数字一行：

➜ grep -A2 7 test
7
8
9
-A2 7 的效果就是找到 7 ，然后输出 7 后面两行。

同理，-B2 7和-C2 7就是找到 7 ，然后分别输出 7 前面两行和前后两行：

➜ grep -B2 7 test
5
6
7

➜ grep -C2 7 test
5
6
7
8
9
继续，假设我们有个名叫 test 的文件内容如下：

➜ cat test
aaaa
bbbbbb
AAAaaa
BBBBASDABBDA
grep -c命令的作用就是输出匹配到的行数，比如我们想找包含aaa的有几行，一眼就能看出来有两行，第一行和第三行都包含：

➜ grep -c aaa test
2
grep -e命令是实现多个匹配之间的或关系，比如我们想找包含aaaa或者bbbb的，显然应该返回第一行和第二行：

➜ grep -e aaaa -e bbbb test
aaaa
bbbbbb
grep -F相当于fgrep命令，就是将pattern视为固定字符串。比如搜索'aa*'不带-F和带上，区别如下：

➜ grep 'aa*' test
aaaa
AAAaaa

➜ grep -F 'aa*' test
可以看到第二次就找不到了，因为搜索的是 aa*这个字符串，而不是正则表达式。

grep -f 文件名的使用方法是把后面这个文件里的内容当做pattern。比如我们有个文件，名字是 grep.txt，然后内容是aa*，使用方法如下：

➜ grep -f grep.txt test
aaaa
AAAaaa
实际上等同于grep 'aa*' test

grep -i --ignore-case作用是忽略大小写。

grep -n显示匹配的行号，就是多显示了个行号，不用细说。

grep -o仅显示匹配到的字符串，还是用刚才的aa*距离，之前显示的都是匹配到的字符所在的整行，这个命令是只显示匹配到的字符：

➜ grep -o 'aa*' test
aaaa
aaa
grep -q不打印匹配结果。刚看到这个我疑惑了半天，让你搜索字符串，你不给我结果那有啥用？然后发现还有一条很多教程没说：如果有匹配的内容则立即返回状态值 0。所以一般用在shell脚本中，在 if 判断里面。

grep -s不显示错误信息，不解释。

grep -v显示不被匹配到的行，相当于[^]反向匹配，最常见的还是用在查找线程的命令里，有时候会打印grep线程，可以再加上这么一个去除自己：

➜ ps -ef|grep Typora
  501 91616     1   0 五11上午 ??        13:39.32 /Applications/Typora.app/Contents/MacOS/Typora
  501 14814 93748   0  5:33下午 ttys002    0:00.00 grep --color=auto --exclude-dir=.bzr --exclude-dir=CVS --exclude-dir=.git --exclude-dir=.hg --exclude-dir=.svn Typora

➜ ps -ef|grep Typora|grep -v grep
  501 91616     1   0 五11上午 ??        13:39.32 /Applications/Typora.app/Contents/MacOS/Typora
可以看到第二次就没有打印grep线程自身

grep -w匹配整个单词，只有完全符合pattern的单次才会匹配到：

➜ grep aaa test
aaaa
AAAaaa

➜ grep -w aaa test
可以看到第二次结果为空，因为没有aaa这个单词。

关于正则的高级用法就不再深入研究了，改日再统一整理。

sed
sed 命令的作用是利用脚本来处理文本文件。使用方法：

sed [-hnV][-e<script>][-f<script文件>][文本文件]
参数说明：

-e<script>或--expression=<script> 以选项中指定的 script 来处理输入的文本文件，这个-e可以省略，直接写表达式。
-f<script文件>或--file=<script文件>以选项中指定的 script 文件来处理输入的文本文件。
-h或--help显示帮助。
-n 或 --quiet 或 --silent 仅显示 script 处理后的结果。
-V 或 --version 显示版本信息。
动作说明：

a：新增， a 的后面可以接字串，而这些字串会在新的一行出现(目前的下一行)～
c：取代， c 的后面可以接字串，这些字串可以取代 n1,n2 之间的行！
d：删除，因为是删除啊，所以 d 后面通常不接任何咚咚；
i：插入， i 的后面可以接字串，而这些字串会在新的一行出现(目前的上一行)；
p：打印，亦即将某个选择的数据印出。通常 p 会与参数 sed -n 一起运行～
s：取代，通常这个 s 的动作可以搭配正规表示法，例如 1,20s/old/new/g 。
我们先准备一个文件，名为test做测试，内容如下：

➜ cat test 
HELLO LINUX!  
Linux is a free unix-type opterating system.  
This is a linux testfile!  
Linux test
增加内容
使用命令sed -e 3a\newLine testfile这个命令的意思就是，在第三行后面追加newLine这么一行字符，字符前面要用反斜线作区分。执行完毕之后可以看到结果：

➜ sed -e 3a\newline test  
HELLO LINUX!  
Linux is a free unix-type opterating system.  
This is a linux testfile!  
newline
Linux test
但是注意，这个只是将文字处理了，没有写入到文件里，文件里还是之前的内容。

其实 a 前面是可以匹配字符串，比如我们只想在出现 Linux 的行后面追加，就可以：sed -e /Linux/a\newline test 两个斜线之间的内容是需要匹配的内容。可以看出，只有第二、第四行有Linux，所以结果如下：

➜ sed -e /Linux/a\newline test 
HELLO LINUX!  
Linux is a free unix-type opterating system.  
newline
This is a linux testfile!  
Linux test 
newline
这里用双引号把整个表达式括起来也可以，还方便处理带空格的字符。

sed -e /Linux/a\newline test等效于sed "/Linux/a newline" test

插入内容
跟 a 类似，sed 3i\newline test是在第三行前面插入newline:

➜ sed 3i\newline test
HELLO LINUX!  
Linux is a free unix-type opterating system.  
newline
This is a linux testfile!  
Linux test
sed /Linux/i\newline test是在所有匹配到Linux的行前面插入：

➜ sed /Linux/i\newline test
HELLO LINUX!  
newline
Linux is a free unix-type opterating system.  
This is a linux testfile!  
newline
Linux test
可以看出插入的用法和增加很相似。

删除
删除的字符是d，用法跟前面也很相似，就不赘述，例子如下：

➜ sed '/Linux/d' test      
HELLO LINUX!  
This is a linux testfile!
可以看到删除了匹配到的两行。

替换
替换也是一样，字符是c。举个栗子：

➜ sed '/Linux/c\Windows' test                   
HELLO LINUX!  
Windows
This is a linux testfile!  
Windows
替换还有个字符是 s，但是用法由不太一样了，最常见的用法：sed 's/old/new/g'其中old代表想要匹配的字符，new是想要替换的字符，比如：

➜ sed 's/Linux/Windows/g' test
HELLO LINUX!  
Windows is a free unix-type opterating system.  
This is a linux testfile!  
Windows test
这里的/g的意思是一行中的每一次匹配，因为一行中可能匹配到很多次。我们拿一个新的文本文件做例子：

➜ cat test2
aaaaaaaaaaa
bbbbbabbbbb
cccccaacccc
假设我们想把一行中的第三次及以后出现的a变成大写A，那应该这么写：

➜ sed 's/a/A/3g' test2
aaAAAAAAAAA
bbbbbabbbbb
cccccaacccc
可以看出只有第一行的有的改了，因为第二第三行没有这么多a出现。

关于s还有很多用法，还是回到第一个文件，比如可以用/^/和/$/分别代表行首和行尾：

➜ sed 's/^/###/g' test
###HELLO LINUX!  
###Linux is a free unix-type opterating system.  
###This is a linux testfile!  
###Linux test 

➜ sed 's/$/---/g' test
HELLO LINUX!  ---
Linux is a free unix-type opterating system.  ---
This is a linux testfile!  ---
Linux test ---
这个其实就是正则表达式的语法，其他类似语法还有：

^ 表示一行的开头。如：/^#/ 以#开头的匹配。
$ 表示一行的结尾。如：/}$/ 以}结尾的匹配。
\< 表示词首。 如：`\ 表示以 abc 为首的詞。
\> 表示词尾。 如：abc\> 表示以 abc 結尾的詞。
. 表示任何单个字符。
* 表示某个字符出现了0次或多次。
[ ] 字符集合。 如：[abc] 表示匹配a或b或c，还有 [a-zA-Z] 表示匹配所有的26个字符。如果其中有^表示反，如 [^a] 表示非a的字符
以上的所有用法，还可以在字符前面增加行号或者匹配。什么意思呐？比如你想在第一和第二行后面增加一行内容newline，就是：

➜ sed '1,2a\newline' test
HELLO LINUX!  
newline
Linux is a free unix-type opterating system.  
newline
This is a linux testfile!  
Linux test
其他操作同理。不止可以用数字来限定范围，还可以用匹配来限定，只需要用//括起来：

➜ sed '/LINUX/,/linux/i\test' test
test
HELLO LINUX!  
test
Linux is a free unix-type opterating system.  
test
This is a linux testfile!  
Linux test
这里的意思是，从匹配到LINUX的那一行，到匹配到linux的那一行，也就是 123 这三行

，都做插入操作。

多个匹配
用-e命令可以执行多次匹配，相当于顺序依次执行两个sed命令：

➜ sed -e 's/Linux/Windows/g' -e 's/Windows/Mac OS/g' test
HELLO LINUX!  
Mac OS is a free unix-type opterating system.  
This is a linux testfile!  
Mac OS test
这个命令其实就是先把Linux替换成Windows，再把Windows替换成Mac OS。

写入文件
上面介绍的所有文件操作都支持在缓存中处理然后打印到控制台，实际上没有对文件修改。想要保存到原文件的话可以用> file或者-i来保存到文件

awk
awk是一个强大的文本分析工具，相对于grep的查找，sed的编辑，awk在其对数据分析并生成报告时，显得尤为强大。简单来说awk就是把文件逐行的读入，以空格为默认分隔符将每行切片，切开的部分再进行各种分析处理。

语法
awk [选项参数] 'script' var=value file(s)
或
awk [选项参数] -f scriptfile var=value file(s)
参数说明：

-F fs or --field-separator fs 指定输入文件折分隔符，fs是一个字符串或者是一个正则表达式，如-F:。
-v var=value or --asign var=value 赋值一个用户定义变量。
-f scripfile or --file scriptfile 从脚本文件中读取awk命令。
基本用法
最基本的用法是awk 动作 文件名。我们先准备一个文件test：

➜ cat test 
2 this is a test
3 Are you like awk
This's a test
10 There are orange,apple,mongo
然后输入awk '{print $1,$4}' test就可以看到：

2 a
3 like
This's 
10 orange,apple,mongo
对比可以很清楚的发现，这行语句的作用是打印每行的第一个和第四个单词。这里如果是$0的话就是把整行都输出出来。

awk -F命令可以指定使用哪个分隔符，默认是空格或者 tab 键：

➜ awk -F, '{print $2}' test



apple
可以看出只有最后一行有输出，因为用逗号做分割，之后最后一行被分成了10 There are orange、apple和mongo三项，然后我们要的是第二项。

还可以同时使用多个分隔符：

➜ awk -F '[ ,]'  '{print $1,$2,$5}' test 
2 this test
3 Are awk
This's a 
10 There apple
这个例子便是使用空格和逗号两个分隔符。

匹配项中可以用正则表达式，比如：

➜ awk '/^This/' test
This's a test
匹配的就是严格以This开头的内容。

还可以取反：

➜ awk '$0 !~ /is/' test 
3 Are you like awk
10 There are orange,apple,mongo
这一个的结果就是去掉带有is的行，只显示其余部分。

从文件中读取：awk -f {awk脚本} {文件名}，这个很好理解，就不再做解释。

变量
awk中有不少内置的变量，比如$NF代表的是分割后的字段数量，相当于取最后一个。

➜ awk '{print $NF}' test            
test
awk
test
orange,apple,mongo
可以看出都是每行的最后一项。

其他的内置变量还有：

FILENAME：当前文件名
FS：字段分隔符，默认是空格和制表符。
RS：行分隔符，用于分割每一行，默认是换行符。
OFS：输出字段的分隔符，用于打印时分隔字段，默认为空格。
ORS：输出记录的分隔符，用于打印时分隔记录，默认为换行符。
OFMT：数字输出的格式，默认为％.6g。
函数
awk还提供了一些内置函数，方便对原始数据的处理。主要如下：

toupper()：字符转为大写。
tolower()：字符转为小写。
length()：返回字符串长度。
substr()：返回子字符串。
sin()：正弦。
cos()：余弦。
sqrt()：平方根。
rand()：随机数。
条件
awk允许指定输出条件，只输出符合条件的行。输出条件要写在动作的前面：

awk '条件 动作' 文件名
还是刚才的例子，用逗号分隔之后有好几个空白行，我们加上限制条件，匹配后为空的不显示：

➜ awk -F, '$2!="" {print $2}' test
apple
可以看到就只剩下apple了。

if 语句
awk提供了if结构，用于编写复杂的条件。比如：

➜ awk '{if ($2 > "t") print $1}' test
2
这一句的完整含义应该是：把每一行按照空格分割之后，如果第二个单词大于t，就输出第一个单词。这里对字符的大小判断应该是基于字符长度和 unicode 编码。

以上这些只是三剑客的基础用法，包括正则表达式也有很多技巧，更多扩展内容网上也很多了，可以自行搜索，或者翻阅下面的参考文章。

参考文章

SED 简明教程

Linux sed 命令

Linux awk 命令

awk 入门教程
```

