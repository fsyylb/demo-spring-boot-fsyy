```text
Go 日常开发常备第三方库和工具
2021-11-02
44,704
阅读9分钟
专栏： 
crossoverJie的内卷小课堂

不知不觉写 Go 已经快一年了，上线了大大小小好几个项目；心态也经历了几轮变化。

因为我个人大概前五年时间写的是 Java ，中途写过一年多的 Python，所以刚接触到 Go 时的感觉如下图： 

既没有 Java 的生态，也没有 Python 这么多语法糖。

写到现在的感觉就是： 

这里就不讨论这几门语言谁强谁弱了；重点和大家分享下我们日常开发中所使用到的一些第三方库与工具。

这里我主要将这些库分为两类：

业务开发
基础工具开发
业务开发
首先是业务开发，主要包含了 web、数据库、Redis 等。

Gin ⭐️⭐️⭐️⭐️⭐️
首先是 Gin，一款 HTTP 框架，使用简单、性能优秀、资料众多；你还在犹豫选择哪款框架时，那就选择它吧，基本没错。

当然和它配套的 github.com/swaggo/gin-… swagger 工具也是刚需；利用它可以生成 swagger 文档。

GORM ⭐️⭐️⭐️⭐️⭐️
GORM 也没啥好说的，如果你喜欢 orm 的方式操作数据库，那就选它吧；同样的也是使用简单、资料较多。

如果有读写分离需求，也可以使用 GORM 官方提供的插件 github.com/go-gorm/dbr… ，配合 GORM 使用也是非常简单。

errors ⭐️⭐️⭐️⭐️⭐️
Go 语言自身提供的错误处理比较简单，github.com/pkg/errors 提供了更强大的功能，比如：

包装异常
包装堆栈等。
常用的有以下 API：

// WithMessagef annotates err with the format specifier.
func WithMessagef(err error, format string, args ...interface{}) error

// WithStack annotates err with a stack trace at the point WithStack was called.
func WithStack(err error) error
zorolog ⭐️⭐️⭐️⭐️⭐️
Go 里的日志打印库非常多，日志在日常开发中最好就是存在感低；也就是说性能强（不能影响到业务代码）、使用 API 简单。

"github.com/rs/zerolog/log"
log.Debug().Msgf("OrderID :%s", "12121")
excelize
github.com/qax-os/exce…是一个读写 Excel 的库，基本上你能遇到的 Excel 操作它都能实现。

now ⭐️⭐️⭐️⭐️
github.com/jinzhu/now 是一个时间工具库：

获取当前的年月日、时分秒。
不同时区支持。
最后一周、最后一个月等。
import "github.com/jinzhu/now"

time.Now() // 2013-11-18 17:51:49.123456789 Mon

now.BeginningOfMinute()        // 2013-11-18 17:51:00 Mon
now.BeginningOfHour()          // 2013-11-18 17:00:00 Mon
now.BeginningOfDay()           // 2013-11-18 00:00:00 Mon
now.BeginningOfWeek()          // 2013-11-17 00:00:00 Sun
now.BeginningOfMonth()         // 2013-11-01 00:00:00 Fri
now.BeginningOfQuarter()       // 2013-10-01 00:00:00 Tue
now.BeginningOfYear()          // 2013-01-01 00:00:00 Tue

now.EndOfMinute()              // 2013-11-18 17:51:59.999999999 Mon
now.EndOfHour()                // 2013-11-18 17:59:59.999999999 Mon
now.EndOfDay()                 // 2013-11-18 23:59:59.999999999 Mon
now.EndOfWeek()                // 2013-11-23 23:59:59.999999999 Sat
now.EndOfMonth()               // 2013-11-30 23:59:59.999999999 Sat
now.EndOfQuarter()             // 2013-12-31 23:59:59.999999999 Tue
now.EndOfYear()                // 2013-12-31 23:59:59.999999999 Tue

now.WeekStartDay = time.Monday // Set Monday as first day, default is Sunday
now.EndOfWeek()                // 2013-11-24 23:59:59.999999999 Sun
Decimal ⭐️⭐️⭐️⭐️
当业务上需要精度计算时 github.com/shopspring/… 可以帮忙。

import (
	"fmt"
	"github.com/shopspring/decimal"
)

func main() {
	price, err := decimal.NewFromString("136.02")

	quantity := decimal.NewFromInt(3)
	fee, _ := decimal.NewFromString(".035")
	taxRate, _ := decimal.NewFromString(".08875")

	subtotal := price.Mul(quantity)

	preTax := subtotal.Mul(fee.Add(decimal.NewFromFloat(1)))

	total := preTax.Mul(taxRate.Add(decimal.NewFromFloat(1)))

	fmt.Println("Subtotal:", subtotal)                      // Subtotal: 408.06
	fmt.Println("Pre-tax:", preTax)                         // Pre-tax: 422.3421
	fmt.Println("Taxes:", total.Sub(preTax))                // Taxes: 37.482861375
	fmt.Println("Total:", total)                            // Total: 459.824961375
	fmt.Println("Tax rate:", total.Sub(preTax).Div(preTax)) // Tax rate: 0.08875
}
基本上你能想到的精度转换它都能做到；配合上 GORM 也可以将 model 字段声明为 decimal 的类型，数据库对应的也是 decimal ，这样使用起来时会更方便。

Amount decimal.Decimal `gorm:"column:amout;default:0.0000;NOT NULL" json:"amout"` 
configor ⭐️⭐️⭐️⭐️
github.com/jinzhu/conf… 是一个配置文件读取库，支持 YAML/JSON/TOML 等格式。

go-cache ⭐️⭐️⭐️
github.com/patrickmn/g… 是一个类似于 Java 中的 Guava cache，线程安全，使用简单；不需要分布式缓存的简单场景可以考虑。

	c := cache.New(5*time.Minute, 10*time.Minute)
	// Set the value of the key "foo" to "bar", with the default expiration time
	c.Set("foo", "bar", cache.DefaultExpiration)
copier ⭐️⭐️⭐️
github.com/jinzhu/copi… 看名字就知道这是一个数据复制的库，与 Java 中的 BeanUtils.copy() 类似；可以将两个字段相同但对象不同的 struct 进行数据复制，也支持深拷贝。

func Copy(toValue interface{}, fromValue interface{}) (err error) 
在我们需要一个临时 struct 来存放数据时很有用，特别是一个 struct 中字段非常多时，一个个来回赋值确实有点费手指。

但也要注意不要什么情况都使用，会带来一些弊端：

当删除字段时，不能利用编译器提示。
当一些字段需要额外人工处理时，代码不易阅读。
反射赋值，有一定性能损耗。
总之在业务开发时，还是建议人工编写，毕竟代码是给人看的。

env ⭐️⭐️⭐️
github.com/caarlos0/en… 这个库可以将我们的环境变量转换为一个 struct.

type config struct {
	Home string `env:"HOME"`
}

func main() {
	cfg := config{}
	if err := env.Parse(&cfg); err != nil {
		fmt.Printf("%+v\n", err)
	}

	fmt.Printf("%+v\n", cfg)
}
这个在我们打包代码到不同的运行环境时非常有用，利用它可以方便的获取不同环境变量。

user_agent ⭐️⭐️⭐️
github.com/mssola/user… 是一个格式化 user-agent 的小工具。

当我们需要在服务端收集 user-agen 时可以更快的读取数据。

func main() {
    ua := user_agent.New("Mozilla/5.0 (Linux; U; Android 2.3.7; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")

    fmt.Printf("%v\n", ua.Mobile())   // => true
    fmt.Printf("%v\n", ua.Bot())      // => false
    fmt.Printf("%v\n", ua.Mozilla())  // => "5.0"
    fmt.Printf("%v\n", ua.Model())    // => "Nexus One"
    fmt.Printf("%v\n", ua.Platform()) // => "Linux"
    fmt.Printf("%v\n", ua.OS()) 
    }
phonenumbers ⭐️⭐️⭐️
github.com/nyaruka/pho… 手机号码验证库，可以不用自己写正则表达式了。

// parse our phone number
num, err := phonenumbers.Parse("6502530000", "US")
基础工具
接下来是一些基础工具库，包含一些主流的存储的客户端、中间件等。

gomonkey ⭐️⭐️⭐️⭐️⭐️
github.com/agiledragon… 是一个 mock 打桩工具，当我们写单元测试时，需要对一些非接口函数进行 mock 会比较困难，这时就需要用到它了。

由于它是修改了调用对应函数时机器跳转指令，而 CPU 架构的不同对应的指令也不同，所以在我们使用时还不兼容苹果的 M1 芯片，不过目前应该已经兼容了，大家可以试试。

goconvey ⭐️⭐️⭐️⭐️⭐️
github.com/smartystree… 也是配合单元测试的库，可以兼容 go test 命令。

提供可视化 web UI。
与 IDE 集成显示单元覆盖率。 
dig ⭐️⭐️⭐️⭐️⭐️
github.com/uber-go/dig 这是一个依赖注入库，我们这里暂不讨论是否应该使用依赖注入，至少目前我们使用下来还是有几个好处：

所有的对象都是单例。
有一个统一的地方管理对象。
使用时直接传递对象当做参数进来即可（容器会自动注入）。
当然也有一些不太方便的地方：

不熟悉时，一个对象是如何创建的不清楚。
代码不是很好理解。
我们内部有自己开发一个业务框架，其中所有的对象都交由 dig 进行管理，使用起来倒也是比较方便。

cobra ⭐️⭐️⭐️⭐️
github.com/spf13/cobra是一个功能强大的命令行工具库，我们用它来实现内部的命令行工具，同时也推荐使用 github.com/urfave/cli/ 我个人会更习惯用后者，要简洁一些。

BloomRPC ⭐️⭐️⭐️⭐️
github.com/uw-labs/blo… 一个 gRPC 可视化工具，比起自己写 gRPC 客户端的代码那确实是要简单许多。

 但也有些小问题，比如精度。如果是 int64 超过了 2^56 服务端拿到的值会发生错误，这点目前还未解决。

redis ⭐️⭐️⭐️⭐️
github.com/go-redis/re… Redis 客户端，没有太多可说的；发展了许多年，该有的的功能都有了。

elastic ⭐️⭐️⭐️⭐️
github.com/olivere/ela… 这也是一个非常成熟的 elasticsearch 库。

resty ⭐️⭐️⭐️⭐️
github.com/go-resty/re… 一个 http client, 使用起来非常简单：

// Create a Resty Client
client := resty.New()
resp, err := client.R().
    EnableTrace().
    Get("https://httpbin.org/get")
有点 Python requests 包那味了。

pulsar-client-go ⭐️⭐️⭐️
Pulsar 官方出品的 go 语言客户端，相对于 Java 来说其他语言的客户端几乎都是后娘养的；功能会比较少，同时更新也没那么积极；但却没得选。

go-grpc-middleware ⭐️⭐️⭐️
github.com/grpc-ecosys… 官方提供的 gRPC 中间件，可以自己实现内部的一些鉴权、元数据、日志等功能。

go-pilosa ⭐️⭐️⭐️
github.com/pilosa/go-p… 是一个位图数据库的客户端，位图数据库的场景应用比较有限，通常是有标签需求时才会用到；比如求 N 个标签的交并补集；数据有一定规模后运营一定会提相关需求；可以备着以备不时之需。

pb ⭐️⭐️⭐️
github.com/cheggaaa/pb 一个命令行工具进度条，编写命令行工具时使用它交互会更优雅。



总结
最后我汇总了一个表格，方便查看：

名称	类型	功能	星级
Gin	业务开发	HTTP 框架	⭐️⭐️⭐️⭐️⭐️
GORM	业务开发	ORM 框架	⭐️⭐️⭐️⭐️⭐️
errors	业务开发	异常处理库	⭐️⭐️⭐️⭐️⭐️
zorolog	业务开发	日志库	⭐️⭐️⭐️⭐️⭐️
excelize	业务开发	Excel相关需求	⭐️⭐️⭐️⭐️⭐️
now	业务开发	时间处理	⭐️⭐️⭐️⭐️️
Decimal	业务开发	精度处理	⭐️⭐️⭐️⭐️️
configor	业务开发	配置文件	⭐️⭐️⭐️⭐️️
go-cache	业务开发	本地缓存	⭐️⭐️⭐️
copier	业务开发	数据复制	⭐️⭐️⭐️️️
env	业务开发	环境变量	⭐️⭐️⭐️️️
user_agent	业务开发	读取 user-agent	⭐️⭐️⭐️️️
phonenumbers	业务开发	手机号码验证	⭐️⭐️⭐️️️
gomonkey	基础工具	mock工具	⭐️⭐️⭐️⭐️⭐
goconvey	基础工具	单测覆盖率	⭐️⭐️⭐️⭐️⭐
dig	基础工具	依赖注入	⭐️⭐️⭐️⭐️⭐
cobra	基础工具	命令行工具	⭐️⭐️⭐️⭐
cli	基础工具	命令行工具	⭐️⭐️⭐️⭐
BloomRPC	基础工具	gRPC 调试客户端	⭐️⭐️⭐️⭐
redis	基础工具	Redis 客户端	⭐️⭐️⭐️⭐
elastic	基础工具	elasticsearch 客户端	⭐️⭐️⭐️⭐
resty	基础工具	http 客户端	⭐️⭐️⭐️⭐
pulsar-client-go	基础工具	Pulsar 客户端	⭐️⭐️⭐️
go-grpc-middleware	基础工具	gRPC 中间件	⭐️⭐️⭐
go-pilosa	基础工具	pilosa 客户端	⭐️⭐️⭐️
pb	基础工具	命令行工具进度条	⭐️⭐️⭐️
星级评分的规则主要是看实际使用的频次。

最后夹带一点私货（其实也谈不上） 文中提到了我们内部有基于以上库整合了一个业务开发框架；也基于该框架上线了大大小小10几个项目，改进空间依然不少，目前还是在快速迭代中。

大概的用法，入口 main.go:   最后截取我在内部的分享就概括了整体的思想--引用自公司一司姓同事。

也许我们内部经过多次迭代，觉得有能力开放出来给社区带来一些帮助时也会尝试开源；现阶段就不嫌丑了。

这些库都是我们日常开发最常用到的，也欢迎大家在评论区留下你们常用的库与工具。


```

# https://github.com/bloomrpc/bloomrpc
# https://github.com/spf13/viper


# 禁止调试？！阻止用户打开开发者工具三大方法
# https://juejin.cn/post/7294260754916392969

# disabledevtool如何破解
# 爬虫必看！开发者工具被禁用打不开？右键菜单被禁用? 禁止F12快捷键?一招教你绕过 disable-devtool
# 绕过网站的检测DevTools
# https://blog.csdn.net/fj_changing/article/details/123816522?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-0-123816522-blog-143977503.235^v43^pc_blog_bottom_relevance_base7&spm=1001.2101.3001.4242.1&utm_relevant_index=2
# https://docs.npmjs.com/
# go操作docker
```text
go 操作docker/client
2022-03-29
2,739
阅读3分钟

使用 Golang 玩转 Docker API
Docker 提供了一个与 Docker 守护进程交互的 API (称为Docker Engine API)，我们可以使用官方提供的 Go 语言的 SDK 进行构建和扩展 Docker 应用程序和解决方案。

安装 SDK

通过下面的命令就可以安装 SDK 了：

go get github.com/docker/docker/client
管理本地的 Docker

该部分会介绍如何使用 Golang + Docker API 进行管理本地的 Docker。

运行容器

第一个例子将展示如何运行容器，相当于 docker run docker.io/library/alpine echo "hello world"：

package main

import (
    "context"
    "io"

    "github.com/docker/docker/api/types"
    "github.com/docker/docker/api/types/container"
    "github.com/docker/docker/client"
    "github.com/docker/docker/pkg/stdcopy"
)

func main() {
    ctx := context.Background()
    cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
    if err != nil {
        panic(err)
    }

    reader, err := cli.ImagePull(ctx, "docker.io/library/alpine", types.ImagePullOptions{})
    if err != nil {
        panic(err)
    }
    io.Copy(os.Stdout, reader)

    resp, err := cli.ContainerCreate(ctx, &container.Config{
        Image: "alpine",
        Cmd:   []string{"echo", "hello world"},
    }, nil, nil, "")
    if err != nil {
        panic(err)
    }

    if err := cli.ContainerStart(ctx, resp.ID, types.ContainerStartOptions{}); err != nil {
        panic(err)
    }


    statusCh, errCh := cli.ContainerWait(ctx, resp.ID, container.WaitConditionNotRunning)
    select {
    case err := <-errCh:
        if err != nil {
            panic(err)
        }
    case <-statusCh:
    }

    out, err := cli.ContainerLogs(ctx, resp.ID, types.ContainerLogsOptions{ShowStdout: true})
    if err != nil {
        panic(err)
    }

    stdcopy.StdCopy(os.Stdout, os.Stderr, out)
}
后台运行容器

还可以在后台运行容器，相当于 docker run -d bfirsh/reticulate-splines：

package main

import (
    "context"
    "fmt"
    "io"
    "os"

    "github.com/docker/docker/api/types"
    "github.com/docker/docker/api/types/container"
    "github.com/docker/docker/client"
)

func main() {
    ctx := context.Background()
    cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
    if err != nil {
        panic(err)
    }

    imageName := "bfirsh/reticulate-splines"

    out, err := cli.ImagePull(ctx, imageName, types.ImagePullOptions{})
    if err != nil {
        panic(err)
    }
    io.Copy(os.Stdout, out)

    resp, err := cli.ContainerCreate(ctx, &container.Config{
        Image: imageName,
    }, nil, nil, "")
    if err != nil {
        panic(err)
    }

    if err := cli.ContainerStart(ctx, resp.ID, types.ContainerStartOptions{}); err != nil {
        panic(err)
    }

    fmt.Println(resp.ID)
}
查看容器列表

列出正在运行的容器，就像使用 docker ps 一样：

package main

import (
    "context"
    "fmt"

    "github.com/docker/docker/api/types"
    "github.com/docker/docker/client"
)

func main() {
    ctx := context.Background()
    cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
    if err != nil {
        panic(err)
    }

    containers, err := cli.ContainerList(ctx, types.ContainerListOptions{})
    if err != nil {
        panic(err)
    }

    for _, container := range containers {
        fmt.Println(container.ID)
    }
}
如果是 docker ps -a，我们可以通过修改 types.ContainerListOptions 中的 All 属性达到这个目的：

// type ContainerListOptions struct {
//     Quiet   bool
//     Size    bool
//     All     bool
//     Latest  bool
//     Since   string
//     Before  string
//     Limit   int
//     Filters filters.Args
// }

options := types.ContainerListOptions{
    All: true,
}
containers, err := cli.ContainerList(ctx, options)
if err != nil {
    panic(err)
}
停止所有运行中的容器

通过上面的例子，我们可以获取容器的列表，所以在这个案例中，我们可以去停止所有正在运行的容器。

注意：不要在生产服务器上运行下面的代码。

package main

import (
    "context"
    "fmt"

    "github.com/docker/docker/api/types"
    "github.com/docker/docker/client"
)

func main() {
    ctx := context.Background()
    cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
    if err != nil {
        panic(err)
    }

    containers, err := cli.ContainerList(ctx, types.ContainerListOptions{})
    if err != nil {
        panic(err)
    }

    for _, container := range containers {
        fmt.Print("Stopping container ", container.ID[:10], "... ")
        if err := cli.ContainerStop(ctx, container.ID, nil); err != nil {
            panic(err)
        }
        fmt.Println("Success")
    }
}
获取指定容器的日志

通过指定容器的 ID，我们可以获取对应 ID 的容器的日志：

package main

import (
    "context"
    "io"
    "os"

    "github.com/docker/docker/api/types"
    "github.com/docker/docker/client"
)

func main() {
    ctx := context.Background()
    cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
    if err != nil {
        panic(err)
    }

    options := types.ContainerLogsOptions{ShowStdout: true}

    out, err := cli.ContainerLogs(ctx, "f1064a8a4c82", options)
    if err != nil {
        panic(err)
    }

    io.Copy(os.Stdout, out)
}
查看镜像列表

获取本地所有的镜像，相当于 docker image ls 或 docker images：

package main

import (
    "context"
    "fmt"

    "github.com/docker/docker/api/types"
    "github.com/docker/docker/client"
)

func main() {
    ctx := context.Background()
    cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
    if err != nil {
        panic(err)
    }

    images, err := cli.ImageList(ctx, types.ImageListOptions{})
    if err != nil {
        panic(err)
    }

    for _, image := range images {
        fmt.Println(image.ID)
    }
}
拉取镜像

拉取指定镜像，相当于 docker pull alpine：

package main

import (
    "context"
    "io"
    "os"

    "github.com/docker/docker/api/types"
    "github.com/docker/docker/client"
)

func main() {
    ctx := context.Background()
    cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
    if err != nil {
        panic(err)
    }

    out, err := cli.ImagePull(ctx, "alpine", types.ImagePullOptions{})
    if err != nil {
        panic(err)
    }

    defer out.Close()

    io.Copy(os.Stdout, out)
}
拉取私有镜像

除了公开的镜像，我们平时还会用到一些私有镜像，可以是 DockerHub 上私有镜像，也可以是自托管的镜像仓库，比如 harbor。这个时候，我们需要提供对应的凭证才可以拉取镜像。

值得注意的是：在使用 Docker API 的 Go SDK 时，凭证是以明文的方式进行传输的，所以如果是自建的镜像仓库，请务必使用 HTTPS！

package main

import (
    "context"
    "encoding/base64"
    "encoding/json"
    "io"
    "os"

    "github.com/docker/docker/api/types"
    "github.com/docker/docker/client"
)

func main() {
    ctx := context.Background()
    cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
    if err != nil {
        panic(err)
    }

    authConfig := types.AuthConfig{
        Username: "username",
        Password: "password",
    }
    encodedJSON, err := json.Marshal(authConfig)
    if err != nil {
        panic(err)
    }
    authStr := base64.URLEncoding.EncodeToString(encodedJSON)

    out, err := cli.ImagePull(ctx, "alpine", types.ImagePullOptions{RegistryAuth: authStr})
    if err != nil {
        panic(err)
    }

    defer out.Close()
    io.Copy(os.Stdout, out)
}
保存容器成镜像

我们可以将一个已有的容器通过 commit 保存成一个镜像：

package main

import (
    "context"
    "fmt"

    "github.com/docker/docker/api/types"
    "github.com/docker/docker/api/types/container"
    "github.com/docker/docker/client"
)

func main() {
    ctx := context.Background()
    cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
    if err != nil {
        panic(err)
    }

    createResp, err := cli.ContainerCreate(ctx, &container.Config{
        Image: "alpine",
        Cmd:   []string{"touch", "/helloworld"},
    }, nil, nil, "")
    if err != nil {
        panic(err)
    }

    if err := cli.ContainerStart(ctx, createResp.ID, types.ContainerStartOptions{}); err != nil {
        panic(err)
    }

    statusCh, errCh := cli.ContainerWait(ctx, createResp.ID, container.WaitConditionNotRunning)
    select {
    case err := <-errCh:
        if err != nil {
            panic(err)
        }
    case <-statusCh:
    }

    commitResp, err := cli.ContainerCommit(ctx, createResp.ID, types.ContainerCommitOptions{Reference: "helloworld"})
    if err != nil {
        panic(err)
    }

    fmt.Println(commitResp.ID)
}
管理远程的 Docker

当然，除了可以管理本地的 Docker， 我们同样也可以通过使用 Golang + Docker API 管理远程的 Docker。

远程连接

默认 Docker 是通过非网络的 Unix 套接字运行的，只能够进行本地通信（/var/run/docker.sock），是不能够直接远程连接 Docker 的。

我们需要编辑配置文件 /etc/docker/daemon.json，并修改以下内容（把 192.168.59.3 改成你自己的 IP 地址），然后重启 Docker：

# vi /etc/docker/daemon.json
{
  "hosts": [
    "tcp://192.168.59.3:2375",
    "unix:///var/run/docker.sock"
  ]
}

systemctl restart docker
修改 client

创建 client 的时候需要指定远程 Docker 的地址，这样就可以像管理本地 Docker 一样管理远程的 Docker 了：

cli, err = client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation(),
    client.WithHost("tcp://192.168.59.3:2375"))
```

# go语言 undefined types.ContainerListOptions
```text
Docker go SDK版本问题解决

croder

于 2020-11-18 13:47:15 发布

阅读量905
 收藏 1

点赞数
分类专栏： Docker Go 文章标签： go docker
版权

Docker
同时被 2 个专栏收录
6 篇文章
订阅专栏

Go
4 篇文章
订阅专栏
Docker go SDK版本问题解决
使用go modules,在go.mod文件中添加如下内容：

module docker_lear
go 1.15

require (
	github.com/docker/docker v19.03.11
)
1
2
3
4
5
6
然后在项目根目录下执行：

go mod tidy

go.mod文件内容会变成下面这样(是正常的)：

module docker_lear

go 1.15

require (
	github.com/Azure/go-ansiterm v0.0.0-20170929234023-d6e3b3328b78 // indirect
	github.com/Microsoft/go-winio v0.4.14 // indirect
	github.com/containerd/containerd v1.3.4 // indirect
	github.com/docker/distribution v2.7.1+incompatible // indirect
	github.com/docker/docker v17.12.0-ce-rc1.0.20200531234253-77e06fda0c94+incompatible
	github.com/docker/go-connections v0.4.0 // indirect
	github.com/docker/go-units v0.4.0 // indirect
	github.com/gogo/protobuf v1.3.1 // indirect
	github.com/google/go-cmp v0.4.1 // indirect
	github.com/gorilla/mux v1.7.4 // indirect
	github.com/morikuni/aec v1.0.0 // indirect
	github.com/opencontainers/go-digest v1.0.0 // indirect
	github.com/opencontainers/image-spec v1.0.1 // indirect
	github.com/pkg/errors v0.9.1 // indirect
	github.com/sirupsen/logrus v1.6.0 // indirect
	golang.org/x/sys v0.0.0-20200602225109-6fdc65e7d980 // indirect
	golang.org/x/time v0.0.0-20200416051211-89c76fbcd5d1 // indirect
	google.golang.org/grpc v1.29.1 // indirect
	gotest.tools v2.2.0+incompatible // indirect
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
然后就可以调用sdk的api进行操作了。

官方demo

package main

import (
	"context"
	"fmt"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/client"
)

func main() {
	ctx := context.Background()
	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		panic(err)
	}

	containers, err := cli.ContainerList(ctx, types.ContainerListOptions{})
	if err != nil {
		panic(err)
	}

	for _, container := range containers {
		fmt.Println(container.ID)
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
更新：20210722
最新引入方式如下：
github.com/docker/docker v20.10.7+incompatible
执行go mod tidy
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/daxiang52/article/details/109772015
```

# go mod tidy作用
```text
深入理解 Go 模块管理：探讨 `go mod tidy` 和 `go mod vendor`

爱吃胡萝卜的代码兔

于 2023-11-02 21:35:50 发布

阅读量2.7k
 收藏 1

点赞数
文章标签： golang 开发语言 后端
版权
 本文详细解释了Go语言的gomodtidy和gomodvendor命令，前者用于保持依赖整洁，后者管理Vendor目录。介绍了它们的内部工作原理及常见使用场景，如依赖清理、网络隔离构建等，强调了在Go项目管理中的重要性。
摘要生成于 C知道 ，由 DeepSeek-R1 满血版支持， 前往体验 >

文章目录
`go mod tidy`：保持依赖整洁
内部机制
使用场景
`go mod vendor`：管理 Vendor 目录
内部机制
使用场景
总结

Go 语言作为现代编程语言的佼佼者，自身的模块管理功能 go mod 为依赖管理带来了巨大便利。在日常开发中， go mod tidy 和 go mod vendor 这两个命令极大地简化了依赖的整理和管理工作。今天，我们就来深入探讨这两个命令的内部机制，使用场景，以及它们如何优化我们的 Go 项目。
go mod tidy：保持依赖整洁
go mod tidy 命令的作用是清理未使用的依赖，并更新 go.mod 以及 go.sum 文件。

内部机制
当你执行 go mod tidy 时，Go 工具链会进行如下操作：

扫描你的项目中所有的 Go 文件，分析其中的 import 声明。
确定哪些模块是项目真正依赖的，即哪些模块是被直接或间接引用的。
任何 go.mod 文件中存在但项目中未引用的依赖将会被移除。
对于缺失的依赖（即代码中引用但未在 go.mod 文件声明的），它会尝试添加适当的版本。
更新 go.sum 文件，包含所有依赖项的预期加密校验和。
使用场景
依赖清理：在你删除了代码中的依赖，但是 go.mod 文件还包含这些依赖时。
升级和降级：在你需要确保 go.mod 文件反映实际使用的依赖版本时。
持续集成：在自动化构建和部署流程中，确保依赖清单的准确性。
go mod vendor：管理 Vendor 目录
go mod vendor 命令用于创建一个 vendor 目录，该目录包含项目依赖的精确版本的副本。

内部机制
执行 go mod vendor 时，Go 会进行以下操作：

根据 go.mod 文件中的依赖项，复制依赖的精确版本到项目的 vendor 目录下。
依赖项的源代码将被拷贝过来，确保无网络环境下也能构建项目。
生成一个 modules.txt 文件在 vendor 目录下，列出所有 vendored 的依赖和它们的版本信息。
使用场景
网络隔离构建：在某些环境下无法直接连接到网络，需要提前下载所有依赖。
确保依赖的一致性：防止因为依赖仓库的不稳定导致的构建失败。
支持旧的构建系统：一些旧的 Go 构建系统或工具可能还不支持模块，需要 vendor 目录。
总结
在现代 Go 项目开发流程中，go mod tidy 和 go mod vendor 两个命令是维护项目依赖不可或缺的工具。go mod tidy 确保了项目的 go.mod 文件精简且准确，而 go mod vendor 则为项目提供了一种可靠的依赖备份机制。合理使用这两个命令，可以让我们的 Go 项目在依赖管理上更加稳健和可维护。随着 Go 语言在工程领域的不断深入，理解和掌握这些工具
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/ABV09876543210/article/details/134191803
```

# https://pkg.go.dev/github.com/docker/docker/client#section-readme

# panic用法 go
```text
1分钟学会go语言中panic处理套路
2023-01-08
1,170
阅读2分钟
专栏： 
go进阶

go 提供一种 panic-recover 模式用于处理程序的异常，这和其他语言的采用的 Exception 机制类似。但也不太一样，使用时需要小心。

核心点：panic 是基于goroutine级别的异常机制，我们只要理解了这句话的思想，就能很好的使用它。

即是说，如果你程序里的 panic 的产生跨多个 goroutine 时，那么此时你想 catch 住该 panic ，一定在 panic 产生的同一个 goroutine 里进行捕获，否则无效。

同一协程内
如果 panic 都是在同一个goroutine里，那么直接可以在根函数进行捕获即可：

func func3() {
   panic("func3 panic")
}

func func2() {
   func3()
}

func func1() {
   defer func() {
      if err := recover(); err != nil {
         fmt.Println(err)
      }
   }()

   func2()
}
如上的 func1、func2、func3 调用链关系：

func1
  └── func2
        └── func3
由于三个函数都在同一个协程中，所以我们只要在根函数 func1 中即可捕获所有的后代子函数产生的 panic。

不同协程
这种情况尤为要注意，否则容易掉坑。还是上面的例子：

func func3() {
   panic("func3 panic")
}

func func2() {
   go func3()
}

func func1() {
   defer func() {
      if err := recover(); err != nil {
         fmt.Println(err)
      }
   }()

   go func2()
}
调用链关系未变，但 func2、func3 在不同的 goroutine 中，那么这时候在 func1 中是无法捕获到后续子协程的 panic 异常，而且，这种子协程的 panic 如果未有效拦截，也会导致主进程崩溃，所以不得不重视它。

所以，但凡你要新启一个 goroutine，而你又不完全确定是否它会产生 panic ，那么保险的做法在启动的时候进行拦截，做到 "谁创建，谁负责" 的原则。

func func3() {
   panic("func3 panic")
}

func func2() {
   go func() {
      defer func() {
         if err := recover(); err != nil {
            fmt.Println(err)
         }
      }()
      
      func3()
   }()
}

func func1() {
   go func() {
      defer func() {
         if err := recover(); err != nil {
            fmt.Println(err)
         }
      }()
      
      func2()
   }()
}
如此，我们便能处理好每一个 goroutine 所产生的异常问题。

封装
其实前面的总结，我们已经掌握了 panic 的处理原则，但是针对新启 goroutine 时的异常处理，代码相当冗余，所以我们打算封装一个启动新协成的函数，抛弃 go func 原生的启动方式（完整源文件：cxy.im/s.40tCUop ）：

func Go(f func(), opts ...Option) {
   g := &gr{}
   for _, opt := range opts {
      opt(g)
   }

   go func() {
      defer func() {
         if err := recover(); err != nil {
            if g.errCallback != nil {
               g.errCallback(err)
            }
         }
      }()

      f()
   }()
}
使用
我们使用 Go() 替代官方的提供的 go func() 函数：

// 基本使用
r.Go(func() {
    // some code
})

// 带 panic err 回调函数
r.Go(func() {
    // some code
}, r.WithErrCallbackOpt(func(err any) {
   // 记录 panic err 错误
    app.Log().Err(err)
}))
```

# goroutine
```text
Go语言的协程——Goroutine
进程(Process)，线程(Thread)，协程(Coroutine，也叫轻量级线程)

进程进程是一个程序在一个数据集中的一次动态执行过程，可以简单理解为“正在执行的程序”，它是CPU资源分配和调度的独立单位。 进程一般由程序、数据集、进程控制块三部分组成。我们编写的程序用来描述进程要完成哪些功能以及如何完成；数据集则是程序在执行过程中所需要使用的资源；进程控制块用来记录进程的外部特征，描述进程的执行变化过程，系统可以利用它来控制和管理进程，它是系统感知进程存在的唯一标志。 进程的局限是创建、撤销和切换的开销比较大。



线程线程是在进程之后发展出来的概念。 线程也叫轻量级进程，它是一个基本的CPU执行单元，也是程序执行过程中的最小单元，由线程ID、程序计数器、寄存器集合和堆栈共同组成。一个进程可以包含多个线程。 线程的优点是减小了程序并发执行时的开销，提高了操作系统的并发性能，缺点是线程没有自己的系统资源，只拥有在运行时必不可少的资源，但同一进程的各线程可以共享进程所拥有的系统资源，如果把进程比作一个车间，那么线程就好比是车间里面的工人。不过对于某些独占性资源存在锁机制，处理不当可能会产生“死锁”。



协程协程是一种用户态的轻量级线程，又称微线程，英文名Coroutine，协程的调度完全由用户控制。人们通常将协程和子程序（函数）比较着理解。 子程序调用总是一个入口，一次返回，一旦退出即完成了子程序的执行。

与传统的系统级线程和进程相比，协程的最大优势在于其"轻量级"，可以轻松创建上百万个而不会导致系统资源衰竭，而线程和进程通常最多也不能超过1万的。这也是协程也叫轻量级线程的原因。

协程的特点在于是一个线程执行，与多线程相比，其优势体现在：协程的执行效率极高。因为子程序切换不是线程切换，而是由程序自身控制，因此，没有线程切换的开销，和多线程比，线程数量越多，协程的性能优势就越明显。
Goroutine
1.1 什么是Goroutine
go中使用Goroutine来实现并发concurrently。

Goroutine是Go语言特有的名词。区别于进程Process，线程Thread，协程Coroutine，因为Go语言的创造者们觉得和他们是有所区别的，所以专门创造了Goroutine。

Goroutine是与其他函数或方法同时运行的函数或方法。Goroutines可以被认为是轻量级的线程。与线程相比，创建Goroutine的成本很小，它就是一段代码，一个函数入口。以及在堆上为其分配的一个堆栈（初始大小为4K，会随着程序的执行自动增长删除）。因此它非常廉价，Go应用程序可以并发运行数千个Goroutines。

Goroutines在线程上的优势。
与线程相比，Goroutines非常便宜。它们只是堆栈大小的几个kb，堆栈可以根据应用程序的需要增长和收缩，而在线程的情况下，堆栈大小必须指定并且是固定的
Goroutines被多路复用到较少的OS线程。在一个程序中可能只有一个线程与数千个Goroutines。如果线程中的任何Goroutine都表示等待用户输入，则会创建另一个OS线程，剩下的Goroutines被转移到新的OS线程。所有这些都由运行时进行处理，我们作为程序员从这些复杂的细节中抽象出来，并得到了一个与并发工作相关的干净的API。
当使用Goroutines访问共享内存时，通过设计的通道可以防止竞态条件发生。通道可以被认为是Goroutines通信的管道。


1.2 主goroutine
封装main函数的goroutine称为主goroutine。

主goroutine所做的事情并不是执行main函数那么简单。它首先要做的是：设定每一个goroutine所能申请的栈空间的最大尺寸。在32位的计算机系统中此最大尺寸为250MB，而在64位的计算机系统中此尺寸为1GB。如果有某个goroutine的栈空间尺寸大于这个限制，那么运行时系统就会引发一个栈溢出(stack overflow)的运行时恐慌。随后，这个go程序的运行也会终止。

此后，主goroutine会进行一系列的初始化工作，涉及的工作内容大致如下：

创建一个特殊的defer语句，用于在主goroutine退出时做必要的善后处理。因为主goroutine也可能非正常的结束
启动专用于在后台清扫内存垃圾的goroutine，并设置GC可用的标识
执行mian包中的init函数
执行main函数
执行完main函数后，它还会检查主goroutine是否引发了运行时恐慌，并进行必要的处理。最后主goroutine会结束自己以及当前进程的运行。
1.3 如何使用Goroutines
在函数或方法调用前面加上关键字go，您将会同时运行一个新的Goroutine。

实例代码：

package main
​
import (  
    "fmt"
)
​
func hello() {  
    fmt.Println("Hello world goroutine")
}
func main() {  
    go hello()
    fmt.Println("main function")
}
运行结果：可能会值输出“main function”。


我们开始的Goroutine怎么样了?我们需要了解Goroutine的规则

当新的Goroutine开始时，Goroutine调用立即返回。与函数不同，go不等待Goroutine执行结束。当Goroutine调用，并且Goroutine的任何返回值被忽略之后，go立即执行到下一行代码。
main的Goroutine应该为其他的Goroutines执行。如果main的Goroutine终止了，程序将被终止，而其他Goroutine将不会运行。
修改以上代码：

package main
​
import (  
    "fmt"
    "time"
)
​
func hello() {  
    fmt.Println("Hello world goroutine")
}
func main() {  
    go hello()
    time.Sleep(1 * time.Second)
    fmt.Println("main function")
}
运行结果：


在上面的程序中，我们已经调用了时间包的Sleep方法，它会在执行过程中睡觉。在这种情况下，main的goroutine被用来睡觉1秒。现在调用go hello()有足够的时间在main Goroutine终止之前执行。这个程序首先打印Hello world goroutine，等待1秒，然后打印main函数。



1.4 启动多个Goroutines
示例代码：

package main
​
import (  
    "fmt"
    "time"
)
​
func numbers() {  
    for i := 1; i <= 5; i++ {
        time.Sleep(250 * time.Millisecond)
        fmt.Printf("%d ", i)
    }
}
func alphabets() {  
    for i := 'a'; i <= 'e'; i++ {
        time.Sleep(400 * time.Millisecond)
        fmt.Printf("%c ", i)
    }
}
func main() {  
    go numbers()
    go alphabets()
    time.Sleep(3000 * time.Millisecond)
    fmt.Println("main terminated")
}
运行结果：

1 a 2 3 b 4 c 5 d e main terminated  
时间轴分析：


发布于 2019-08-08 14:42
Go 语言
Go 编程
编程语言
​赞同 58​
​2 条评论
​分享
​喜欢
​收藏
​申请转载
​
写下你的评论...

2 条评论
默认
最新
tearshark
tearshark
你前面谈协程，并没有特指是go的协程。而是并列的将协程，线程，进程一起谈论的。因此，我认为你是在说泛指的协程，即不考虑操作系统，不考虑语言的概念。毕竟，进程，线程也是一个泛概念。

那么，泛指的协程，并不是你描述的那样的特征。

一、协程首先不是轻量级线程，协程出现得比线程早，所以轻线程可以直接否了。

二、协程不是可以都可以开上很多的，Windows上的系统API提供的协程，默认1M的栈空间，基本跟线程一样的开销。

三、协程的切换开销也要看实现，还是有开销基本等同线程的实现：栈切换，缓存丢失等等。好处是可以不进内核状态。



不要拿go的协程的特征来衡量协程。也不要以为go的协程的特征就是独一无二的。go协程的特征在其他语言里都找得到对应的实现，其某个特性也不见得是最优的。go的协程最优秀的地方是好用，毕竟专攻这块。然后现在生态发展得好。

2019-08-09
​回复
​43
可以在你身边
可以在你身边
最后的图好评！！！！[爱]
```

```text
Golang panic用法
白鲸鱼
白鲸鱼
公众号： 科研收录 、柚子编程俱乐部
来自专栏 · golang编程指北
26 人赞同了该文章
本文使用 Zhihu On VSCode 创作并发布


Golang panic用法

Go语言追求简洁优雅，所以，Go语言不支持传统的 try…catch…finally 这种异常，因为Go语言的设计者们认为，将异常与控制结构混在一起会很容易使得代码变得混乱。因为开发者很容易滥用异常，甚至一个小小的错误都抛出一个异常。在Go语言中，使用多值返回来返回错误。不要用异常代替错误，更不要用来控制流程。在极个别的情况下，也就是说，遇到真正的异常的情况下（比如除数为 0了）。才使用Go中引入的Exception处理：defer, panic, recover。

这几个异常的使用场景可以这么简单描述：Go中可以抛出一个panic的异常，然后在defer中通过recover捕获这个异常，然后正常处理。

package main

import "fmt"

func main(){
    fmt.Println("c")
     defer func(){ // 必须要先声明defer，否则不能捕获到panic异常
        fmt.Println("d")
        if err:=recover();err!=nil{
            fmt.Println(err) // 这里的err其实就是panic传入的内容，55
        }
        fmt.Println("e")
    }()

    f() //开始调用f
    fmt.Println("f") //这里开始下面代码不会再执行
}

func f(){
    fmt.Println("a")
    panic("异常信息")
    fmt.Println("b") //这里开始下面代码不会再执行
    fmt.Println("f")
}


输出结果：

c
a
d
异常信息
e
内建函数
假如函数F中书写了panic语句，会终止其后要执行的代码，在panic所在函数F内如果存在要执行的defer函数列表，按照defer的逆序执行
比如panic函数内有：
    defer   函数1
    defer   函数2
    defer   函数3
那么执行顺序就是：
    函数3
    函数2
    函数1
返回函数F的调用者G，在G中，调用函数F语句之后的代码不会执行，假如函数G中存在要执行的defer函数列表，按照defer的逆序执行，这里的defer 有点类似 try-catch-finally 中的 finally
到goroutine整个退出，并报告错误
recover：
内建函数
用来控制一个goroutine的panicking行为，捕获panic，从而影响应用的行为
一般的调用建议(如上面的例子)
a). 在defer函数中，通过recever来终止一个gojroutine的panicking过程，从而恢复正常代码的执行
b). 可以获取通过panic传递的error
简单来讲：go中可以抛出一个panic的异常，然后在defer中通过recover捕获这个异常，然后正常处理。
注意：利用recover处理panic指令，defer必须在panic之前声明，否则当panic时，recover无法捕获到panic．

应用示例：
比如go编译器的源码

注释: 编译器是构建正在运行的二进制文件的编译器工具链的名称。已知的工具链为：
gc Also known as cmd/compile.
gccgo The gccgo front end, part of the GCC compiler suite.





介绍 错误和异常 (errors and exceptions)
大多数编程语言支持 exception 作为处理 error 的标准方式，比如 Java，Python。虽然方便，但也会带来许多问题，这就是为什么他们不喜欢其他语言或者风格。对 exception 的主要吐槽点是它们为控制流引入了 "side channel"，当阅读代码的时候，你必须时刻记住这个 exception 引发的流程控制方向。这也导致某些代码阅读起来比较困难[1]。

让我们开始具体谈谈 Go 中的错误处理。我假定你知道 Go 中错误处理的“标准”方式。下面如何打开文件的代码：

f, err := os.Open("file.txt")
if err != nil {
    // handle error here
}
// do stuff with f here

如果文件不存在， os.Open() 函数将返回一个非空 error ，在其他语言中这样的错误处理是完全不同的，比如 Python 中的内建 open() 函数将在错误发生时抛出异常。

try:
    with open("file.txt") as f:
        # do stuff with f here
except OSError as err:
    # handle exception err here
Python 始终坚持通过 exception 来处理 error。因为这种无处不在的错误处理方式导致经常被吐槽。甚至利用 exception 作为序列结束的信号。到底 exception 的真正含义是什么？以下是来自 Rob Pike 在邮件中 对此的贴切阐述，其中塑造了现有的 Go panic/recover 机制雏形。

这正是提案试图避免的那种事情。 Panic 和 recover 不是通常意义的异常机制。通常的方式是将 exception 和一个控制结构相关联，鼓励细粒度的 exception 处理，导致代码往往不易阅读。在 error 和调用一个 panic 之间确实存在差异，而且我们希望这个差异很重要。在 Java 中打开一个文件会抛出异常。在我的经验中，打开文件失败是最平常不过的事。而且还需要我写许多代码来处理这样的 exception。
客观的讲。exception 的支持者嘲笑 Go 的这种过于明确的 error 处理有多方面的原因。首先，请注意上面两个例子中代码的顺序。 在 Python 中，程序的主要流程紧跟在 open 调用之后，并且错误处理被委托给后一阶段（更不用说在许多情况下，异常将被堆栈中更上一级的函数捕获到而不是在此函数中）。 另一方面，在 Go 中，立刻处理错误这种方式，可能会使主程序流程混淆。 此外，Go 的错误处理非常冗长 - 这是该语言的主要吐槽点之一。 我将在后面提到一种可能的方法来解决这个问题。

除了上面 Rob 的引用之外，在 FAQ 中总结了 Go 的 exception 哲学。

我们认为将异常耦合到控制结构（如 try-catch-finally 惯用语）会导致代码错综复杂。 它还倾向于鼓励程序员标记太多普通错误，例如打开文件失败。
然而，在某些情况下，具有类似异常的机制实际上是有用的 ; 像 Go 这样的高级语言甚至是必不可少的。 这就是存在 panic 和 recover 的原因。

偶尔的 panic 是必要的
Go 是一种安全的语言，运行时检查一些严重的编程错误。例如在你访问超出 slice 边界的元素时，这种行为是未定义的，因此 Go 会在运行时 panic。例如下面的小程序。

package main

import (
    "fmt"
)

func main() {
    s := make([]string, 3)
    fmt.Println(s[5])
}
程序将终止于一个运行时 error。

panic: runtime error: index out of range

goroutine 1 [running]:
main.main()
    /tmp/sandbox209906601/main.go:9 +0x40
其他一些会引发 panic 的就是通过值为 nil 的指针访问结构体的字段，关闭已经关闭的 channel 等。怎样选择性的 panic ？可以通过访问 slice 时返回 result，error 两个值的方式实现。也可以将 slice 的元素赋值给一个可能返回 error 的函数，但是这样会将代码变复杂。想象一下，写一个小片段，foo，bar，baz 都只是一个字符串的一个 slice，实现片段之间的拼接。

foo[i] = bar[i] + baz[i]

就会变成下面这样冗长的代码：

br, err := bar[i]
if err != nil {
    return err
}
bz, err := baz[i]
if err != nil {
    return err
}
err := assign_slice_element(foo, i, br + bz)
if err != nil {
    return err
 }
这不是开玩笑，不同语言处理这样的方式是不一样的。如果 slices/lists/arrays 的指针 i 越界了，在 Python 和 Java 中就会抛出异常。C 中没有越界检查，所以你就可以尽情的蹂躏边界外的内存空间，最后将导致程序崩溃或者暴露安全漏洞。C++ 中将采用折中的处理方式。性能优先的模块采用这种不安全的 C 模式，其他模块（比如 std::vector::at）采用抛出异常的方式。

因为上面重写的小片段变得如此冗长是不可接受的。Go 选择了 panic ，这是一种类似异常的机制，在代码中保留了像 bugs 这样最原始的异常条件。

这不只是内建代码能够这样用，自定义代码也可以在任何需要的地方调用 panic。在有些可能导致可怕错误的地方还鼓励使用 panic 抛出 error，比如 bug 或者一些关键因素被违反的时候。比如在 swich 的某个 case 在当前上下文中是不可能发生的，在这种 case 中只有一个 panic 函数。这无形中等价于 Python 中的 raise 或者 C++ 中的 throw。这也强有力的证明了在捕获异常方面 Go 的异常处理的特殊之处。


Image


Go 中 panic 恢复的限制条件
讨论
一般就HTTP服务的全局做一个recover,然后任何地方panic都能全局捕获相应一个错误









正确使用 Panic
[禁止] 使用 panic 用于正常的错误处理。
应该使用 error 和多个返回值。
panic 只适合用于那些严重影响程序运行的错误。参考这里


拓展阅读
On the uses and misuses of panics in Go

https://zhuanlan.zhihu.com/p/222367644

https://www.golangprograms.com/go-language/panic-and-recover.html

编辑于 2022-09-06 10:21
```

# go func参数


```text
国内隐私计算的开源框架介绍

沙子可可

于 2025-04-01 12:09:46 发布

阅读量1.3k
 收藏 19

点赞数 33
文章标签： 开源 安全架构
版权
在中国，隐私计算领域的发展迅速，多个开源框架被广泛应用于金融、医疗、政务等领域。

开源框架
以下是使用较多的隐私计算开源框架及其特点：

1. FATE（联邦学习框架）
开发者：微众银行（WeBank）
技术方向：联邦学习（Federated Learning）
特点：
支持横向联邦、纵向联邦和迁移联邦学习。
提供可视化工具（FATE Board）和工业级部署方案。
兼容主流机器学习框架（如TensorFlow、PyTorch）。
应用场景：金融风控、医疗数据联合建模等。
开源地址：GitHub - FATE
2. 隐语（SecretFlow）
开发者：蚂蚁集团
技术方向：多方安全计算（MPC）、联邦学习、可信执行环境（TEE）融合。
特点：
整合了多种隐私计算技术（如SPU安全计算单元）。
支持大规模分布式计算，适用于复杂业务场景。
提供Python友好的API，易与现有AI框架集成。
应用场景：广告推荐、联合风控等。
开源地址：GitHub - SecretFlow
3. Rosetta
开发者：矩阵元（JUZIX）
技术方向：基于MPC的隐私计算（兼容深度学习）。
特点：
基于TensorFlow扩展，支持隐私保护的深度学习模型训练。
提供MPC协议（如SecureNN、Helix）的封装。
适合已有TensorFlow生态的用户迁移。
应用场景：联合数据分析、模型训练。
开源地址：GitHub - Rosetta
4. PaddleFL
开发者：百度（PaddlePaddle生态）
技术方向：联邦学习
特点：
基于百度飞桨（PaddlePaddle）深度学习框架。
支持多种联邦学习策略和自定义算法。
适合需要与PaddlePaddle深度集成的场景。
应用场景：自然语言处理、图像识别等AI模型的联邦训练。
开源地址：GitHub - PaddleFL
5. PrimiHub
开发者：原语科技
技术方向：多方安全计算（MPC）、联邦学习、隐私求交（PSI）等。
特点：
模块化设计，支持灵活扩展。
提供数据隐私保护的全流程工具链。
应用场景：金融数据合作、政务数据共享。
开源地址：GitHub - PrimiHub
6. FederatedScope
开发者：阿里巴巴、浙江大学
技术方向：联邦学习
特点：
轻量级、模块化设计，支持快速实验。
支持跨设备、跨组织的联邦学习场景。
应用场景：学术研究、轻量级业务部署。
开源地址：GitHub - FederatedScope
选择建议
联邦学习优先：FATE、PaddleFL、FederatedScope。
多方安全计算（MPC）：隐语（SecretFlow）、Rosetta、PrimiHub。
企业级支持：FATE（微众）、隐语（蚂蚁）适合需要工业级稳定性的场景。
学术研究：FederatedScope提供灵活的模块化设计。
这些框架多数由中国顶尖科技企业或学术机构主导，兼顾技术创新和实际落地需求，可根据具体场景（数据规模、安全要求、技术栈兼容性）选择合适的工具。

隐语（SecretFlow）框架
隐语网址

开源可信隐私计算框架——隐语 SecretFlow，以安全、开放为核心设计理念，支持安全多方计算/联邦学习/同态加密/TEE 等主流隐私计算技术。 积极推进产学研生态共建，助力行业标杆场景应用与数据要素市场发展。


良好的分层设计可以提高开发效率和可维护性，满足不同用户的需求。隐语从上到下一共分为六层。

产品层：通过白屏化产品提供隐语整体隐私计算能力的输出，让用户简单直观体验隐私计算，屏蔽隐私计算底层细节，节约开发成本；
调度层：提供隐私计算任务的编排与资源调度能力；
AI & BI 算法层：屏蔽隐私计算技术细节，提供一些通用的算法能力，比如 MPC 的 LR/XGB/NN，联邦学习算法等；
设备层：提供了统一的可编程设备抽象，将 MPC、HE 等隐私计算技术抽象为密态设备，将单方本地计算抽象为明文设备；
安全协议层：提供密码原语能力，支持高安全、高性能的协议；
硬件层：支持TEE可信硬件。
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY 版权协议，转载请附上原文出处链接和本声明。
                        
原文链接：https://blog.csdn.net/desert_fish1976/article/details/146908097
```

#  https://blog.csdn.net/desert_fish1976?type=blog
# 深入学习LLM开发 第零章：简单说明
# https://blog.csdn.net/desert_fish1976/article/details/145908761?spm=1001.2014.3001.5502

# 隐语框架介绍
# https://www.yuque.com/secret-flow/admin/tox42l
# https://pkg.go.dev/os@go1.24.2#Open

# linux让进程或线程独占CPU 
# https://www.cnblogs.com/zuofaqi/articles/10438632.html

# linux单独给进程设置cpu
# 
