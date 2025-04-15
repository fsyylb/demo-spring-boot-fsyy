# Yum 安装 Node.js

yum安装设置Node.js v16版本
curl --silent --location https://rpm.nodesource.com/setup_16.x | sudo bash
(setup_16里16是版本号，可根据自己需要修改)
yum方式安装
sudo yum -y install nodejs
其它参考：
以上命令安装不成功可执行：
sudo yum clean all
若本地存在多个nodesoucre，可以执行以下命令，在重新yum安装命令

安装cnpm国内访问速度快:
npm install -g cnpm --registry=https://registry.npm.taobao.org



1、删除旧版本nodejs npm
yum remove nodejs npm -y
2、删除旧的node的可执行文件和npm
cd /usr/local/bin
rm -rf node
rm -rf npm
3、删除旧的配置目录
删除: rm -rf /root/.npm
4、yum安装设置Node.js v18版本
curl --silent --location https://rpm.nodesource.com/setup_18.x | bash
5、清空缓存
yum clean all
6、yum方式安装，如果未成功按照提示添加相关参数
yum -y install nodejs
7、配置软链接
ln -s /usr/bin/node /usr/bin/local/node
ln -s /usr/bin/npm /usr/bin/local/npm
8、验证
node -v
npm -v
9、安装相关组件
比如npm升级
npm install wscat等
然后wscat就能使用了，wscat -c http://localhost:port
原先wscat不能使用是node版本过低导致