一文带你学会 Docker 导出和导入本地镜像

今天测试一个项目，本地打包了一个 Docker 镜像，需要在另一台环境比较完备的机器上做测试，这时候就需要导出本地镜像，然后在测试机器上导入这个镜像。

先查看下本机要导出的镜像的 ID

# docker images

REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
webconsole          latest              aba01f181a4a        5 seconds ago       593 MB
可以看到要导出的镜像的 ID 为 aba01f181a4a ，将此镜像导出为本地文件

# docker save aba01f181a4a > /opt/webconsole.tar 
将导出的镜像上传到另一台机器，可以通过 scp 命令、rz 命令或者 fpt 工具上传镜像文件。

假设镜像已经上传到了测试机器，在测试机器上面执行导入操作，命令如下：

# docker load < webconsole.tar

040fd7841192: Loading layer [==================================================>] 4.234 MB/4.234 MB
0f20ab684106: Loading layer [==================================================>]   959 kB/959 kB
e8984b733970: Loading layer [==================================================>] 3.072 kB/3.072 kB
61d11dce11a7: Loading layer [==================================================>] 258.1 MB/258.1 MB
f11c0d75456c: Loading layer [==================================================>]  2.56 kB/2.56 kB
46ba3a5cbbcc: Loading layer [==================================================>] 5.632 kB/5.632 kB
185300076fe6: Loading layer [==================================================>] 324.8 MB/324.8 MB
ab113f5d3101: Loading layer [==================================================>] 13.96 MB/13.96 MB
adda2c8b8266: Loading layer [==================================================>] 2.048 kB/2.048 kB
Loaded image ID: sha256:aba01f181a4aa9aa92c27232fd22a1852c1d48f00db85f57d4d3896cd08b7851
然后查看机器上的镜像列表，执行如下命令：

# docker images

REPOSITORY         TAG          IMAGE ID            CREATED             SIZE
<none>             <none>       aba01f181a4a        20 minutes ago      593 MB
可以发现镜像已经导入进来了，但是镜像的仓库名称和 TAG 均为 none，接下来就为镜像打上 tag

# docker tag aba01f181a4a webconsole:latest 
查看下效果

# docker images

REPOSITORY         TAG          IMAGE ID            CREATED             SIZE
webconsole         latest       aba01f181a4a        20 minutes ago      593 MB
可以看出镜像名称和 tag 已经打上了。然后验证镜像是否能够成功运行，执行如下命令观察即可：

# docker run -itd webconsole:latest


https://zhuanlan.zhihu.com/p/602681384