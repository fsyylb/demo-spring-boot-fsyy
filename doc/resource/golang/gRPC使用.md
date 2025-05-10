# gRPC教程
## quickstart官网
https://grpc.io/docs/languages/go/quickstart/

## protoc安装
### 帮助网址
https://protobuf.dev/installation/

### 安装步骤
1、从release页面下载zip包
PB_REL="https://github.com/protocolbuffers/protobuf/releases"
curl -LO $PB_REL/download/v30.2/protoc-30.2-linux-x86_64.zip

2、解压（目录可选， -d 命令后面即是目录）
unzip protoc-30.2-linux-x86_64.zip -d $HOME/.local

3、更新环境变量（注意，目录需要和步骤2对应）
export PATH="$PATH:$HOME/.local/bin"

ps：如果不更新PATH环境变量，那么也可以使用./executable（可执行文件）代替

当然，也可以在anaconda中安装protobuf实现protoc安装
conda install protobuf


## go插件安装
go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest
然后更新环境变量
export PATH="$PATH:$(go env GOPATH)/bin"

## 案例网址
https://github.com/grpc/grpc-go
其下有examples/helloworld目录

# 示例

helloworld.proto
```text
// Copyright 2015 gRPC authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

syntax = "proto3";

option go_package = "/";
option java_multiple_files = true;
option java_package = "io.grpc.examples.helloworld";
option java_outer_classname = "HelloWorldProto";

package helloworld;

// The greeting service definition.
service Greeter {
  // Sends a greeting
  rpc SayHello (HelloRequest) returns (HelloReply) {}
}

// The request message containing the user's name.
message HelloRequest {
  string name = 1;
}

// The response message containing the greetings
message HelloReply {
  string message = 1;
}
```

示例代码库结构

example/
├── go.mod
├── go.sum
├── main.go
├── proto
│   ├── helloworld_grpc.pb.go
│   ├── helloworld.pb.go
│   └── helloworld.proto
└── server.go

然后根据helloworld.proto生成pb和grpc pb文件
```shell script
./protoc --go_out=../example/proto/ --go-grpc_out=../example/proto/ -I ../example/proto/ ../example/proto/helloworld.proto
```
相关目录结构如下，执行目录问bin目录，目录下有可执行文件protoc
.
├── bin
│   └── protoc
├── example
│   ├── go.mod
│   ├── go.sum
│   ├── main.go
│   ├── proto
│   │   ├── helloworld_grpc.pb.go
│   │   ├── helloworld.pb.go
│   │   └── helloworld.proto
│   └── server.go

然后在example目录下
go mod init example
然后新建main.go，此文件是client端，替换了import中的pb "example/proto"

main.go
```text
/*
 *
 * Copyright 2015 gRPC authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

// Package main implements a client for Greeter service.
package main

import (
	"context"
	"flag"
	"log"
	"time"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	pb "example/proto"
)

const (
	defaultName = "world"
)

var (
	addr = flag.String("addr", "localhost:50051", "the address to connect to")
	name = flag.String("name", defaultName, "Name to greet")
)

func main() {
	flag.Parse()
	// Set up a connection to the server.
	conn, err := grpc.NewClient(*addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("did not connect: %v", err)
	}
	defer conn.Close()
	c := pb.NewGreeterClient(conn)

	// Contact the server and print out its response.
	ctx, cancel := context.WithTimeout(context.Background(), time.Second)
	defer cancel()
	r, err := c.SayHello(ctx, &pb.HelloRequest{Name: *name})
	if err != nil {
		log.Fatalf("could not greet: %v", err)
	}
	log.Printf("Greeting: %s", r.GetMessage())
}
```


同样的，server.go是服务端代码，也替换了import中的pb "example/proto"

server.go
```text
/*
 *
 * Copyright 2015 gRPC authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

// Package main implements a server for Greeter service.
package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"net"

	"google.golang.org/grpc"
	pb "example/proto"
)

var (
	port = flag.Int("port", 50051, "The server port")
)

// server is used to implement helloworld.GreeterServer.
type server struct {
	pb.UnimplementedGreeterServer
}

// SayHello implements helloworld.GreeterServer
func (s *server) SayHello(_ context.Context, in *pb.HelloRequest) (*pb.HelloReply, error) {
	log.Printf("Received: %v", in.GetName())
	return &pb.HelloReply{Message: "Hello " + in.GetName()}, nil
}

func main() {
	flag.Parse()
	lis, err := net.Listen("tcp", fmt.Sprintf(":%d", *port))
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	s := grpc.NewServer()
	pb.RegisterGreeterServer(s, &server{})
	log.Printf("server listening at %v", lis.Addr())
	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}
```

然后
go mod tidy
下载相关依赖
最后
go run server.go
新开一个终端
go run main.go

执行结束
