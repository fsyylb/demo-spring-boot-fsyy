# 容器部署wordpress

拉取 WordPress 镜像
docker pull wordpress:6.3.1

拉取 Mysql 镜像
docker pull mysql:5.7

docker run --name mysql -d -p 3306:3306 -v /root/mysql:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=root --restart=always mysql:5.7
## run: 启动一个容器
## -d: 启动的容器在后台运行
## --name: 容器名 mysql
## -e MYSQL_ROOT_PASSWORD:  设置 MySQL 的 root 密码
## -p：映射端口，将容器的3306端口映射到主机的3306端口
## mysql：启动的镜像，如果具体版本，这里也要加上具体版本号 mysql:5.7

docker run --name wordpress --link mysql:mysql -p 80:80 -d -v /root/wordpress:/var/www/html wordpress:6.3.1

## --link：将 mysql 容器挂载到 mysql 上，这样 WordPress 才能访问

name参数指定要启动的WordPress实例名称，link参数指定要使用的Docker MySQL实例名称，p参数将Docker内部的80端口映射到本地的8080端口上。


## 1.查看运行的容器
docker ps

## 2.进入mysql容器
docker exec -it mysql bash

## 3.登录mysql
mysql -uroot -proot

## 4.授权root用户在其他机器上运行：host为 % 表示不限制ip，默认的localhost表示本机使用
grant all on *.* to 'root'@'%';

## 5.如果是 mysql8 版本，由于不支持动态修改密码验证，还需要更新root用户密码
alter user 'root'@'%' identified with mysql_native_password by 'root';




alter user 'root'@'localhost' identified by 'root'


## 6.刷新权限
flush privileges;

## 7. 提前创建好数据库，后续wordpress页面输入数据库时需要
create database wordpress;


容器中，wordpress页面填写数据库主机名时，可以直接用mysql


https://article.juejin.cn/post/7247698383379841081



# 【Wordpress+Docker】详细教程，五分钟搭建你自己的个人博客
https://blog.csdn.net/samsara_of_ice/article/details/121977273


# Docker(九)Docker Compose
https://blog.csdn.net/wfs1994/article/details/84965626

# 如何找到wordpress登录网址（4种方法） 
https://www.cnblogs.com/loyseo/p/13625280.html

