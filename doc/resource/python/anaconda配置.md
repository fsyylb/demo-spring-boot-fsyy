```text
一、安装 Anaconda
1. 下载 Anaconda 安装包
首先，前往 Anaconda 官网 下载适用于 Linux 的安装包。

wget https://repo.anaconda.com/archive/Anaconda3-2023.07-Linux-x86_64.sh
1
2. 安装 Anaconda
下载完成后，运行以下命令开始安装：

bash Anaconda3-2023.07-Linux-x86_64.sh
1
安装过程中会出现许可协议提示，按 Enter 键查看协议内容，按 Q 键退出查看，然后输入 yes 同意协议。

安装路径建议使用默认路径（按 Enter 键确认）。

3. 配置环境变量
安装完成后，建议添加 Anaconda 到系统环境变量中。在终端中运行以下命令：

source ~/.bashrc
1
或者手动编辑 .bashrc 文件，在文件末尾添加以下内容：

export PATH="$HOME/anaconda3/bin:$PATH"
1
保存文件后，运行：

source ~/.bashrc
1
二、基本使用
1. 验证安装
在终端中输入以下命令验证是否安装成功：

conda --version
1
如果返回 conda 的版本号，则表示安装成功。

2. 更新 conda
安装成功后，建议更新 conda 到最新版本：

conda update conda
1
3. 创建和管理环境
使用 Anaconda 可以轻松创建和管理虚拟环境。例如，创建一个名为 myenv 的新环境，并安装指定版本的 Python：

conda create -n myenv python=3.8
1
激活环境：

conda activate myenv
1
安装常用包（如 numpy 和 pandas）：

conda install numpy pandas
1
列出所有环境：

conda env list
1
删除环境：

conda remove -n myenv --all
1
4. 使用 Jupyter Notebook
在 Anaconda 环境中使用 Jupyter Notebook 是非常方便的。首先，确保 Jupyter 已安装：

conda install jupyter
1
然后，在终端中运行：

jupyter notebook
1
浏览器将自动打开 Jupyter Notebook 的界面。

三、高级使用
1. 配置镜像源
为了提高下载速度，可以配置国内镜像源。例如，配置清华大学的镜像源：

conda config --add channels https://mirrors.tuna.tsinghua.edu.cn/anaconda/pkgs/free/
conda config --add channels https://mirrors.tuna.tsinghua.edu.cn/anaconda/pkgs/main/
conda config --set show_channel_urls yes
1
2
3
2. 导出和导入环境
导出环境配置：

conda env export > environment.yml
1
根据配置文件创建环境：

conda env create -f environment.yml
1
四、常见问题
1. conda 命令未找到
如果在终端中遇到 conda: command not found 的错误，确保已正确配置环境变量，并重新加载 .bashrc 文件。

source ~/.bashrc
1
2. 解决依赖冲突
在安装包时可能会遇到依赖冲突的情况。可以尝试使用 conda-forge 仓库：

conda install -c conda-forge <package_name>
   
原文链接：https://blog.csdn.net/mieshizhishou/article/details/140269614
```

##实际安装命令
```text
官网下载页面：https://www.anaconda.com/download#
https://www.anaconda.com/download/success

删除和卸载指南：https://www.anaconda.com/docs/getting-started/anaconda/uninstall#what-are-conda-initialization-scripts


mkdir /myconda
cd /myconda
wget https://repo.anaconda.com/archive/Anaconda3-2024.10-1-Linux-x86_64.sh
bash Anaconda3-2024.10-1-Linux-x86_64.sh

Anaconda3 will now be installed into this location:
/root/anaconda3

  - Press ENTER to confirm the location
  - Press CTRL-C to abort the installation
  - Or specify a different location below
```
