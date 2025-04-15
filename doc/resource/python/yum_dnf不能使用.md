### centos 下载如下安装包并且安装
wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/dnf-4.7.0-4.el8.noarch.rpm
wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/python3-dnf-4.7.0-4.el8.noarch.rpm
rpm -ivh --force *.rpm

# 示例：阿里云镜像站（需替换实际版本号）
wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/libdnf-0.63.0-3.el8.x86_64.rpm
wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/python3-libdnf-0.63.0-3.el8.x86_64.rpm

# 强制安装
rpm -ivh --force *.rpm

wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/rpm-4.14.3-19.el8.x86_64.rpm
wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/python3-rpm-4.14.3-19.el8.x86_64.rpm

wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/python3-hawkey-0.63.0-3.el8.x86_64.rpm

wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/libcomps-0.1.16-2.el8.x86_64.rpm
wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/python3-libcomps-0.1.16-2.el8.x86_64.rpm

wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/gpgme-1.13.1-9.el8.x86_64.rpm
wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/gpgmepp-1.13.1-9.el8.x86_64.rpm
wget https://mirrors.aliyun.com/centos-vault/8.5.2111/BaseOS/x86_64/os/Packages/python3-gpg-1.13.1-9.el8.x86_64.rpm


### centos
CentOS Vault 是 CentOS 的归档站点，用于存储旧版本的 CentOS 镜像和软件包，而 CentOS 是当前支持的版本。
具体区别如下：‌

‌功能定位‌

‌CentOS‌: 是一个基于Red Hat Enterprise Linux源代码编译的社区企业操作系统，主要用于服务器环境，强调稳定性和兼容性。
‌CentOS Vault‌: 是 CentOS 的归档站点，专门用于存储旧版本的 CentOS 镜像和软件包，方便用户下载和使用不再官方支持的版本。
‌版本支持‌

‌CentOS‌: 提供当前支持的版本，用户可以从官方网站或镜像站点下载最新的 ISO 镜像文件。
‌CentOS Vault‌: 提供历史版本的镜像和软件包，例如 CentOS 6.x 或 CentOS 7.x 的旧版本，适合需要特定版本的用户。
‌使用场景‌

‌CentOS‌: 适用于需要最新功能和安全性更新的生产环境。
‌CentOS Vault‌: 适用于需要兼容旧软件或特定版本的环境，例如某些企业应用可能依赖于旧版本的 CentOS。
‌获取方式‌

‌CentOS‌: 用户可以通过官方网站或镜像站点（如中科大镜像站）下载最新版本的 ISO 文件。
‌CentOS Vault‌: 用户可以通过访问vault.centos.org获取旧版本的镜像和软件包。
总结
‌CentOS 是当前支持的版本，适合大多数用户；而 CentOS Vault 是归档站点，主要用于获取旧版本资源，适合有特定需求的用户。


### 
ModuleNotFoundError: No module named 'dnf'

sudo rpm -ivh https://repo.centos.org/centos-8/BaseOS/x86_64/os/Packages/dnf-4.4.2-6.el8.noarch.rpm

https://update.cs2c.com.cn/centos-8/BaseOS/x86_64/os/Packages/dnf-4.4.2-6.el8.noarch.rpm

http://mirrors.aliyun.com/repo/centos-8/BaseOS/x86_64/os/Packages/

http://mirrors.aliyun.com/centos/8/BaseOS/x86_64/os/Packages/dnf-4.7.0-4.el8.noarch.rpm

### rpm find
https://www.rpmfind.net/

https://www.cnblogs.com/chenwolong/p/15907329.html

### ModuleNotFoundError: No module named 'dnf' when running yum or dnf
$ rpm -ql python3-3.7.0-9.h4.eulerosv2r8.aarch64> py.log
$ while read -r line;do dirname $line |xargs -I {} ssh root@$remoteip "mkdir -p {}" ;scp $line root@$remoteip:$line  ;done<py.log

$ rpm -ql python3-libs-3.7.0-9.h4.eulerosv2r8.aarch64 >pylib.log
$ while read -r line;do dirname $line |xargs -I {} ssh root@$remoteip "mkdir -p {}" ;scp $line root@$remoteip:$line  ;done<pylib.log

scp -r /usr/lib/python3.7/site-packages root@$remoteip:/usr/lib/python3.7/

import os
import requests
from bs4 import BeautifulSoup

# 要爬取的 URL
url = "http://mirrors.aliyun.com/centos/8/BaseOS/x86_64/os/Packages/"

# 创建一个用于保存下载文件的目录
download_dir = "py36_rpm"
os.makedirs(download_dir, exist_ok=True)

# 发送请求并获取页面内容
response = requests.get(url)
response.raise_for_status()  # 确保请求成功

# 解析页面内容
soup = BeautifulSoup(response.text, 'html.parser')

# 查找所有以 'python3' 开头的 rpm 包链接
for link in soup.find_all('a'):
    href = link.get('href')
    if href and href.startswith('python3') and href.endswith('.rpm'):
        # 构建完整的下载链接
        download_url = url + href
        # 下载文件并保存
        print(f"Downloading {href}...")
        rpm_response = requests.get(download_url)
        with open(os.path.join(download_dir, href), 'wb') as f:
            f.write(rpm_response.content)

print("All files downloaded.")

https://www.cnblogs.com/neozheng/p/18387080


【Centos】绕开报错ModuleNotFoundError: No module named ‘dnf‘
https://update.cs2c.com.cn/NS/V10/V10SP3-2403/os/adv/lic/base/aarch64/Packages/
