# 操作步骤

## 注意，只需要开放server端到agent端的6443端口访问权限，而无需反过来

## server端
```text
安装
curl -sfL https://rancher-mirror.rancher.cn/k3s/k3s-install.sh | INSTALL_K3S_MIRROR=cn sh -

复制配置文件
cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
赋权
chown $(id -u):$(id -g) $HOME/.kube/config

镜像源设置
vim /etc/rancher/k3s/registries.yaml
mirrors:
  "docker.io":
    endpoint:
      - "https://dockerpull.cn"
      - "https://ccr.ccs.tencentyun.com"
  "k8s.gcr.io":
    endpoint:
      - "https://registry.aliyuncs.com/google_containers"  # 阿里云替代源

因为云服务器有内网ip和公网ip，为了让配置为公网ip，能被访问，需对服务进行设置
vim /etc/systemd/system/k3s.service
原先是
ExecStart=/usr/local/bin/k3s \
    server \

添加一行
ExecStart=/usr/local/bin/k3s \
    server \
    --tls-san 110.42.212.69
然后
systemctl daemon-reload
systemctl restart k3s

```

## agent端
```text
获取server端的token
在server的目录下 cat /var/lib/rancher/k3s/server/node-token

安装agent
curl -sfL https://rancher-mirror.rancher.cn/k3s/k3s-install.sh | INSTALL_K3S_MIRROR=cn K3S_URL=https://110.42.212.69:6443 K3S_TOKEN=K101e3243cf9cac566e51d287b8f9e44270ccf18026da92c1f4b8cd5c196de22507::server:8be33cb51156bb248892424c670a7f34 sh -
其中，K3S_URL是server端的ip和端口，此处用公网ip和默认的6443端口
K3S_TOKEN即是server端的token

同样的，也在agent端配置镜像源，k3s目录要新建，mkdir /etc/rancher/k3s
vim /etc/rancher/k3s/registries.yaml
mirrors:
  "docker.io":
    endpoint:
      - "https://dockerpull.cn"
      - "https://ccr.ccs.tencentyun.com"
  "k8s.gcr.io":
    endpoint:
      - "https://registry.aliyuncs.com/google_containers"  # 阿里云替代源

有些时候安装卡住，有可能是前期在server端加入过本节点
在server端 kubectl get nodes 查看下，若有（根据hostname来判断是否为同一个节点）
则 kubectl delete node xxx(你的节点）

安装完成后，在agent端使用kubectl命令时，可能报localhost相关错误
此时是agent端kubectl客户端命令配置有问题
需要把服务端的/etc/rancher/k3s/k3s.yaml复制到～/.kube目录下，并修改为config文件名
另外，文件的内容中，
server: https://127.0.0.1:6443
需要修改为
server: https://110.42.212.69:6443
其中110.42.212.69是server端的公网ip
---------------------------------------------------------
(base) [root@fsyy ~]# cat ~/.kube/config
apiVersion: v1
clusters:
- cluster:
    certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUJkakNDQVIyZ0F3SUJBZ0lCQURBS0JnZ3Foa2pPUFFRREFqQWpNU0V3SHdZRFZRUUREQmhyTTNNdGMyVnkKZG1WeUxXTmhRREUzTkRnek16WXhNVGN3SGhjTk1qVXdOVEkzTURnMU5URTNXaGNOTXpVd05USTFNRGcxTlRFMwpXakFqTVNFd0h3WURWUVFEREJock0zTXRjMlZ5ZG1WeUxXTmhRREUzTkRnek16WXhNVGN3V1RBVEJnY3Foa2pPClBRSUJCZ2dxaGtqT1BRTUJCd05DQUFURzZYcEdNQ3VYUDJoanhlaDl2VnJ4aXFQdVFqVmxHMXJmK3l4dHdhNXUKS1NNbWhMeGNJeElhUzBFMjhtdlVIMzBDU1FHZWZReFdKdEwvbGZhWlRTMmpvMEl3UURBT0JnTlZIUThCQWY4RQpCQU1DQXFRd0R3WURWUjBUQVFIL0JBVXdBd0VCL3pBZEJnTlZIUTRFRmdRVVBUTTdvTXE5N1JvZnVOUUxzcko5CnQ1M1QwLzR3Q2dZSUtvWkl6ajBFQXdJRFJ3QXdSQUlnSWhSaVZINk1PUEZYYytHcWh6eGpIRmhIVitkZUxwWVEKWFNNMDNjZ0RFNmdDSURUY2tkanl5b21rVlJWeDgzbGRZZ25lU3FIQlhaL2pPY2NycVB6amRlVGMKLS0tLS1FTkQgQ0VSVElGSUNBVEUtLS0tLQo=
    server: https://110.42.212.69:6443
  name: default
contexts:
- context:
    cluster: default
    user: default
  name: default
current-context: default
kind: Config
preferences: {}
users:
- name: default
  user:
    client-certificate-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUJrakNDQVRlZ0F3SUJBZ0lJRmRzMVBXRGVxTnd3Q2dZSUtvWkl6ajBFQXdJd0l6RWhNQjhHQTFVRUF3d1kKYXpOekxXTnNhV1Z1ZEMxallVQXhOelE0TXpNMk1URTNNQjRYRFRJMU1EVXlOekE0TlRVeE4xb1hEVEkyTURVeQpOekE0TlRVeE4xb3dNREVYTUJVR0ExVUVDaE1PYzNsemRHVnRPbTFoYzNSbGNuTXhGVEFUQmdOVkJBTVRESE41CmMzUmxiVHBoWkcxcGJqQlpNQk1HQnlxR1NNNDlBZ0VHQ0NxR1NNNDlBd0VIQTBJQUJKQTBQL2Vrb0Frc0NodjEKaXp1Ryt0MldCOThaL3FhdngxekxxSGYyb1JEZGFzWDhlSDNBRjlUT29UWjk5RkpOdElHWnAzVnIrT2FYR1hvSgpRMHhzRi9DalNEQkdNQTRHQTFVZER3RUIvd1FFQXdJRm9EQVRCZ05WSFNVRUREQUtCZ2dyQmdFRkJRY0RBakFmCkJnTlZIU01FR0RBV2dCVDJqVEk1MXdLZDQwdlZmR3Z0aGhiYjFnT2JwVEFLQmdncWhrak9QUVFEQWdOSkFEQkcKQWlFQXNPbFJQREJZb05HdzliYmk2R0diYkVaWlIzbUErT1F0VXlhd1pQdDVWSzRDSVFEQWgvVDJyc3pqMkpqWApYb2hsTmZiWXZKQnEzMzg2NE9BSUxVMVlXVE1ITkE9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCi0tLS0tQkVHSU4gQ0VSVElGSUNBVEUtLS0tLQpNSUlCZURDQ0FSMmdBd0lCQWdJQkFEQUtCZ2dxaGtqT1BRUURBakFqTVNFd0h3WURWUVFEREJock0zTXRZMnhwClpXNTBMV05oUURFM05EZ3pNell4TVRjd0hoY05NalV3TlRJM01EZzFOVEUzV2hjTk16VXdOVEkxTURnMU5URTMKV2pBak1TRXdId1lEVlFRRERCaHJNM010WTJ4cFpXNTBMV05oUURFM05EZ3pNell4TVRjd1dUQVRCZ2NxaGtqTwpQUUlCQmdncWhrak9QUU1CQndOQ0FBU1E0WTJlOFpQbDJubjVGNjJTcUdqY0s2eEZnT1h1cmVhcGhlTmF6YW9vCldwMmhSUHo5Y1JDZWVxRk1kNk1JTjk2eGRLUWZUNENOdmN0YmRaWm1WQ3libzBJd1FEQU9CZ05WSFE4QkFmOEUKQkFNQ0FxUXdEd1lEVlIwVEFRSC9CQVV3QXdFQi96QWRCZ05WSFE0RUZnUVU5bzB5T2RjQ25lTkwxWHhyN1lZVwoyOVlEbTZVd0NnWUlLb1pJemowRUF3SURTUUF3UmdJaEFKWmM2Wk14MEVmRVdaSFFPY1RVVmxSOVFmSVJTZDRHCmFxbmRVV3EyZjNEbkFpRUFtUCtQUFNOTWxXWmh1TmJhZDl6aEJWQ1lTbjVqem81cExkTXlHTmFyb1dzPQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==
    client-key-data: LS0tLS1CRUdJTiBFQyBQUklWQVRFIEtFWS0tLS0tCk1IY0NBUUVFSUUxRy82WUMvbTBWeXRRQkdKMXNjeGlJKyszRWsxVld5YmRVdk5pdFA1RmNvQW9HQ0NxR1NNNDkKQXdFSG9VUURRZ0FFa0RRLzk2U2dDU3dLRy9XTE80YjYzWllIM3huK3BxL0hYTXVvZC9haEVOMXF4Zng0ZmNBWAoxTTZoTm4zMFVrMjBnWm1uZFd2NDVwY1plZ2xEVEd3WDhBPT0KLS0tLS1FTkQgRUMgUFJJVkFURSBLRVktLS0tLQo=
---------------------------------------------------------

```