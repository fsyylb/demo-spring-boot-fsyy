Dockerfile
```text
FROM python:3-bullseye
MAINTAINER "fsyy"
RUN pip install flask
ADD ./app.py /app.py
EXPOSE 5000
CMD ["python", "/app.py"]
```

myapp_v1.yml
```text
version: "3.9"
services:
  myflask:
    image: myapp:v1
    network_mode: "host"
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - /var/run/docker.sock:/var/run/docker.sock
      - /usr/bin/docker:/usr/bin/docker
      - /usr/bin/docker-compose:/usr/bin/docker-compose
```

docker-compose.yml
```text
#version: "3.9"
services:
  myflask:
    image: python:3-bullseye
    network_mode: "host"
    volumes:
      - /etc/localtime:/etc/localtime:ro
      - /var/run/docker.sock:/var/run/docker.sock
      - /usr/bin:/usr/bin
      - ./app.py:/app.py
    entrypoint: ["bash", "-c", "python", "/app.py"]
```

app.py
```text
# save this as app.py
from flask import Flask, request, jsonify
import os
import subprocess

app = Flask(__name__)

@app.route('/run', methods=['POST'])
def run():
    data = request.get_json()["cmd"]
    result = subprocess.run(data, text = True, capture_output = True)
    #return jsonify({"message": result.stdout}), 200
    return result.stdout, 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
```

## 执行命令
```text
docker build -t myapp:v1 .
docker-compose  -f myapp_v1.yml up -d
docker-compose -f myapp_v1.yml down


curl -X POST -H "Content-Type: application/json" -d '{"cmd": "docker ps"}' http://127.0.0.1:5000/run

curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["docker","ps"]}' http://127.0.0.1:5000/run

curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["ls","-l"]}' http://127.0.0.1:5000/run

```


### tmp
```text
# save this as app.py
from flask import Flask, request, jsonify
import os
import subprocess
 
app = Flask(__name__)
 
@app.route('/reload_nginx', methods=['POST'])
def reload_nginx():
    subprocess.run(['sudo', 'nginx', '-s', 'reload'])
    return jsonify({"message": "Nginx reloaded successfully"}), 200
 
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)



# save this as app.py
from flask import Flask, request, jsonify
import os
import subprocess
 
app = Flask(__name__)
 
@app.route('/run', methods=['POST'])
def run():
	data = request.get_json()["cmd"]
    subprocess.run([data])
    return jsonify({"message": "Nginx reloaded successfully"}), 200
 
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)

curl -X POST -H "Content-Type: application/json" -d '{"cmd": "docker ps"}' http://127.0.0.1:5000/run

curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["docker","ps"]}' http://127.0.0.1:5000/run

curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["ls","-l"]}' http://127.0.0.1:5000/run


version: "3.9"
services:
  myflask:
    image: python:3-bullseye
	network_mode: "host"
	volumes:
		- /etc/localtime:/etc/localtime:ro
		- /var/run/docker.sock:/var/run/docker.sock
		- /usr/bin/:/usr/bin
		- ./app.py:/app.py
    entrypoint: ["bash", "-c", "python", "/app.py"]

FROM python:3-bullseye
MAINTAINER "fsyy"
RUN pip install flask
ADD ./app.py /app.py
EXPOSE 5000
CMD ["python", "/app.py"]

version: "3.9"
services:
  myflask:
    image: python:3-bullseye
	network_mode: "host"
	volumes:
		- /etc/localtime:/etc/localtime:ro
		- /var/run/docker.sock:/var/run/docker.sock
		- /usr/bin:/usr/bin
```



```text
 652  mkdir /myconda
  653  cd /myconda/
  654  wget https://repo.anaconda.com/archive/Anaconda3-2024.10-1-Linux-x86_64.sh
  655  ls -l
  656  bash Anaconda3-2024.10-1-Linux-x86_64.sh
  657  df -h
  658  cd /myconda/
  659  ls -l
  660  bash Anaconda3-2024.10-1-Linux-x86_64.sh
  661  conda
  662  cat ~/.bashrc
  663  cat /etc/bashrc
  664  vim ~/.bashrc
  665  echo $HOME
  666  source ~/.bashrc
  667  conda --version
  668  conda create -n myenv python=3.8
  669  conda activate myenv
  670  conda deactivate
  671  conda init
  672  cat /root/.bashrc
  673  conda env list
  674  cd /myconda/
  675  conda env export > environment.yml
  676  cat environment.yml
  677  pip list
  678  python3
  679  exit;
  680  echo $PYTHONSTART
  681  echo $PYTHONSTARTUP
  682  cd /myconda/
  683  ls -l
  684  vim app.py
  685  python app.py
  686  vim app.py
  687  python app.py
  688  python3
  689  vim app.py
  690  python app.py
  691  python3
  692  vim app.py
  693  python app.py
  694  vim app.py
  695  python app.py
  696  curl -X POST -H "Content-Type: application/json" -d '{"cmd": "docker ps"}' http://127.0.0.1:5000/run
  697  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["docker","ps"]}' http://127.0.0.1:5000/run
  698  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["ls","-l"]}' http://127.0.0.1:5000/run
  699  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["ls","-l", "/"]}' http://127.0.0.1:5000/run
  700  docker images
  701  docker-compose -v
  702  ls -l /var/run/
  703  cd /myconda/
  704  ls -l
  705  mkdir exam
  706  cd exam/
  707  vim docker-compose.yml
  708  cp ../app.py .
  709  ls -l
  710  docker-compose up -d
  711  vim docker-compose.yml
  712  docker-compose up -d
  713  vim docker-compose.yml
  714  docker-compose up -d
  715  docker-compose ps
  716  docker-compose ps -a
  717  docker-compose logs exam-myflask-1
  718  docker ps myflask
  719  docker ps -a
  720  docker logs cb86493dc39a
  721  docker-compose down
  722  docker ps
  723  docker ps -a
  724  vim docker-compose.yml
  725  docker-compose up -d
  726  docker ps
  727  docker ps -a
  728  docker logs 569
  729  docker-compose logs myflask
  730  docker run -it python:3-bullseye
  731  docker ps
  732  docker ps -a
  733  docker-compose down
  734  docker ps -a
  735  docker rm `docker ps -aq`
  736  vim Dockerfile
  737  docker build -t myapp:v1
  738  ls -l
  739  docker build --help
  740  docker build -t myapp:v1 .
  741  vim myapp_v1.yml
  742  docker-compose up -f myapp_v1.yml -d
  743  docker-compose --help
  744  docker-compose  -f myapp_v1.yml up -d
  745  docker ps
  746  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["ls","-l"]}' http://127.0.0.1:5000/run
  747  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["ls","-l"]}' http://127.0.0.1:5000/run -v
  748  docker ps
  749  docker logs 691
  750  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["docker","ps"]}' http://127.0.0.1:5000/run -v
  751  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["docker","ps"]}' http://127.0.0.1:5000/run
  752  ls -l
  753  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["pwd"]}' http://127.0.0.1:5000/run -v
  754  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["pwd"]}' http://127.0.0.1:5000/run
  755  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["ls"]}' http://127.0.0.1:5000/run
  756  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["ll"]}' http://127.0.0.1:5000/run
  757  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["ls", "-l", "/run"]}' http://127.0.0.1:5000/run
  758  docker exec -it 693 bash
  759  docker ps
  760  docker exec -it 691 bash
  761  docker-compose -f myapp_v1.yml down
  762  docker ps
  763  docker ps -a
  764  vim myapp_v1.yml
  765  docker-compose -f myapp_v1.yml up -d
  766  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["ls", "-l"]}' http://127.0.0.1:5000/run
  767  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["docker","ps"]}' http://127.0.0.1:5000/run
  768  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["docker","restart","691"]}' http://127.0.0.1:5000/run
  769  docker ps
  770  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["docker","restart","7b6"]}' http://127.0.0.1:5000/run
  771  curl -X POST -H "Content-Type: application/json" -d '{"cmd": ["docker","restart","7b5"]}' http://127.0.0.1:5000/run
  772  docker ps
  773  cd /myconda/
  774  ls -l
  775  cd exam/
  776  ls -l
  777  docker-compose -f myapp_v1.yml down
  778  cd ../
  779  ls -l
  780  mkdir chapter1
  781  cd chapter1/
  782  conda env list
  783  conda activate myenv
  784  pip install flask
  785  ls -l
  786  vim app.py
  787  flask run
  788  vim app.py
  789  flask run
  790  curl http://localhost:5000
  791  lsof:5000
  792  lsof
  793  lsof -i:5000
  794  cd /myconda/chapter1/
  795  vim app.py
  796  flask run
  797  curl http://localhost:5000/user/fsyy
  798  curl http://localhost:5000/user/fsyy123
  799  curl http://localhost:5000/user/fsyy123<
  800  curl "http://localhost:5000/user/fsyy123<"
  801  curl "http://localhost:5000/test
  802  "
  803  curl "http://localhost:5000/test"
  804  ls -f
  805  ls -al
  806  curl http://localhost:5000/
  807  cd /myconda/chapter1/
  808  flask run
  809  vim app.py
  810  flask run
  811  vim app.py
  812  flask run
  813  vim app.py
  814  flask run
  815  vim app.py
  816  flask run
  817  mkdir templates
  818  vim index.html
  819  ls -l
  820  mv index.html templates/
  821  vim app.py
  822  flask run
  823  flask --help
  824  flask run --help
  825  flask run -p 10240
  826  flask run -p 8080
  827  flask run -h 0.0.0.0 -p 8080
  828  mkdir static
  829  mkdir images
  830  cd images/
  831  wget https://github.com/helloflask/watchlist/commit/e51c579735ae837824f10af5c1b7d454014d3c59#diff-f034f61ca750c9653268603d3c5d7d721b6fbe1ab85557184bd12f38877d923a
  832  ls -l
  833  cd ../
  834  ls -l
  835  git clone https://github.com/helloflask/watchlist.git
  836  git clone http://github.com/helloflask/watchlist.git
  837  git clone https://hub.fastgit.org/helloflask/watchlist.git
  838  git clone git://github.com/helloflask/watchlist.git
  839  ls -l
  840  git clone https://github.com/helloflask/watchlist.git
  841  ls -l
  842  cd watchlist/
  843  ls -l
  844  git branch
  845  git branch --help
  846  git branch list
  847  cd watchlist/
  848  ls -
  849  ls -l
  850  cd static
  851  ls -l
  852  cd images/
  853  ls -l
  854  cp ./* /myconda/chapter1/images/
  855  cd ../
  856  ls -l
  857  cd /myconda/chapter1/
  858  ls -l
  859  cd static/
  860  ls -l
  861  vim style.css
  862  ls -l
  863  cd ../templates/
  864  ls -l
  865  rm -rf index.html
  866  vim index.html
  867  cp ../watchlist/watchlist/static/favicon.ico ../static/
  868  ls -l
  869  cd ../
  870  ls -l
  871  flask run -h 0.0.0.0 -p 8080
  872  ls -l static/
  873  flask run -h 0.0.0.0 -p 8080
  874  ls -l ./static/
  875  cd ../
  876  ls -l
  877  mv chapter1/images chapter1/static/
  878  ls -l chapter1/
  879  ls -l chapter1/static/
  880  ls -l chapter1/static/images/
  881  flask run -h 0.0.0.0 -p 8080
  882  ls -l
  883  cd chapter1/
  884  flask run -h 0.0.0.0 -p 8080
  885  pip list
  886  pip list | grep flask-sql
  887  pip list | grep sql
  888  pip list | grep flask
  889  pip list | grep Flask
  890  conda
  891  conda info
  892  cd /myconda/
  893  ls -l
  894  cd test/
  895  ls -l
  896  cd ../
  897  ls -l
  898  cd test/
  899  ls -l
  900  cd test1/
  901  ls -l
  902  mkdir test2
  903  ls -l
  904  cd ../../
  905  ls -l
  906  mkdir -pv test/test1/test2
  907  ls -l test/*
  908  cd test/
  909  ls -l
  910  cd test1
  911  ls -l
  912  cd ../
  913  ls -l
  914  cd ../
  915  mkdir -pv test/test1/test2
  916  cd test/
  917  ls -l
  918  python
  919  ls -l
  920  cd test1/
  921  ls -l
  922  cd ../
  923  ls -l
  924   ls -l
  925  ls -l
  926  cd ../
  927  ls -l
  928  cd /myconda/
  929  ls -l
  930  mkdir -r test/test1/test2
  931  mkdir --help
  932  mkdir -pv test/test1/test2
  933  python
  934  ls -l
  935  mkdir -pv test/test1/test2
  936  python
  937  ls -l
  938  python
  939  conda env list
  940  conda list
  941  conda --help
  942  conda info
  943  conda env
  944  conda activate myenv
  945  pip install loguru
  946  pip show loguru
  947  python
  948  ls -l *log*
  949  cat error.log
  950  rm -rf error.log
  951  cd /myconda/
  952  ls -l
  953  vim .env
  954  python
  955  cd /myconda/
  956  python
  957  conda activate myevn
  958  conda info --envs
  959  conda activate myenv
  960  pip install tiktoken
  961  python
  962  python
  963  ping arxiv.org
  964  curl http://arxiv.org/abs/cond-mat/0603029v1
  965  python
  966  cd /myconda/
  967  conda activate myenv
  968  pip install arxiv
  969  python
  970  pip install json-repair
  971  python
  972  pip install json5
  973  python
  974  pip install mistune
  975  python
  976  docker ps
  977  docker ps -a
  978  ll
  979  cd /myconda/
  980  ll
  981  cat app.py
  982  conda activate myenv
  983  pip install docker
  984  python
  985  pip install httpx
  986  python
  987  httpx --help
  988  pip install 'httpx[cli]'
  989  httpx --help
  990  httpx http://www.baidu.com
  991  python
  992  conda activate myenv
  993  pip install tornado
  994  python
  995  cd mysql/
  996  cd /myconda/
  997  ll
  998  vim tornado_example.py
  999  python tornado_example.py
 1000  curl http://localhost:8888
 1001  conda activate myenv
 1002  conda install jupyter
 1003  conda info --envs
 1004  cd /myconda/
 1005  ll
 1006  cat exam/
 1007  ll
 1008  cd exam/
 1009  ll
 1010  cat Dockerfile
 1011  cat myapp_v1.yml
 1012  cat docker-compose.yml
 1013  cat app.py
 1014  ll
 1015  history
```