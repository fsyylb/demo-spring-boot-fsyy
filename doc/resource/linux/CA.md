# CA.sh
```shell script
#!/bin/bash

######################################
# Author: fsyy@fsyy.com
# Date: 2025/06/09
# ref: https://gist.github.com/liuguangw/4d4b87b750be8edb700ff94c783b1dd4
######################################


PRIVATE_KEY_LENGTH=4096
# ref: https://www.cnblogs.com/f-ck-need-u/p/6091027.html
CERT_CONFIG_DEMO='cert.conf'
CERT_SIGN_CONFIG_DEMO='cert.txt'

# ca
CA_PRIVATE_KEY_FILE='myCA.key'
CA_CERTIFICATE_FILE='myCA.crt'
CA_CERTIFICATE_PEM_FILE='myCA.pem'
CA_CERTIFICATE_DAYS=7300

# domain
DOMAIN_NAME='PartyC'
DOMAIN_CERTIFICATE_DAYS=3650

function initCA()
{
  # generate ca private key
  if [ ! -f $CA_PRIVATE_KEY_FILE ]
  then
    openssl genrsa -des3 -out $CA_PRIVATE_KEY_FILE $PRIVATE_KEY_LENGTH
  else
    echo "CA private key exist: $CA_PRIVATE_KEY_FILE"
  fi 

  # generate ca certificaopenssl
  openssl req -x509 -new -nodes -key $CA_PRIVATE_KEY_FILE -sha256 -days $CA_CERTIFICATE_DAYS -out $CA_CERTIFICATE_FILE
  openssl x509 -in $CA_CERTIFICATE_FILE -out $CA_CERTIFICATE_PEM_FILE
}

function initCert()
{
  if [ ! -f $CERT_CONFIG_DEMO ]; then
    echo "Err: $CERT_CONFIG_DEMO for cert init is not exist!"
  fi 
  
  if [ ! -f $DOMAIN_CERTIFICATE_CONFIG ]; then
    cp $CERT_CONFIG_DEMO $DOMAIN_CERTIFICATE_CONFIG
  fi
  
  # new private key for ssl cert
  # TODO: for intermediate-certificate, use 'openssl genrsa -des3'
  openssl genrsa -out $DOMAIN_PRIVATE_KEY_FILE $PRIVATE_KEY_LENGTH
  
  # new ssl certificate by private key
  openssl req -new -key $DOMAIN_PRIVATE_KEY_FILE -out $DOMAIN_CERTIFICATE_FILE -config $DOMAIN_CERTIFICATE_CONFIG
}

function signCert()
{
  if [ ! -f $CERT_SIGN_CONFIG_DEMO ]; then
      echo "Err: $CERT_SIGN_CONFIG_DEMO for cert signature is not exist!"
  fi

  if [ ! -f $DOMAIN_SIGN_CONFIG ]; then
    cp $CERT_SIGN_CONFIG_DEMO $DOMAIN_SIGN_CONFIG
  fi 
  
  openssl x509 -req -in $DOMAIN_CERTIFICATE_FILE -out $DOMAIN_SIGNED_CERT_FILE -days $DOMAIN_CERTIFICATE_DAYS \
    -CAcreateserial -CA $CA_CERTIFICATE_FILE -CAkey $CA_PRIVATE_KEY_FILE \
    -CAserial serial -extfile $DOMAIN_SIGN_CONFIG
}

function checkCert()
{
  openssl x509 -in $DOMAIN_SIGNED_CERT_FILE -noout -text
  if [ -e $DOMAIN_SIGNED_CERT_FILE ];
  then
    openssl verify -CAfile $CA_CERTIFICATE_FILE $DOMAIN_SIGNED_CERT_FILE
  else
    openssl verify -CAfile $DOMAIN_NAME
  fi
}

function packCerts()
{
  rm -fr $DOMAIN_NAME
  mkdir -p $DOMAIN_NAME/client
  mkdir -p $DOMAIN_NAME/CA
  
  cp $CA_CERTIFICATE_PEM_FILE $DOMAIN_NAME/CA
  mv $DOMAIN_SIGNED_CERT_FILE $DOMAIN_NAME/client/$MODULE_NAME.crt
  mv $DOMAIN_PRIVATE_KEY_FILE $DOMAIN_NAME/client/$MODULE_NAME.key
  
  openssl pkcs12 -export -out $DOMAIN_NAME/client/$MODULE_NAME.pfx \
    -inkey $DOMAIN_NAME/client/$MODULE_NAME.key \
    -in $DOMAIN_NAME/client/$MODULE_NAME.crt \
    -certfile $CA_CERTIFICATE_PEM_FILE -password ncl:fsyy

  rm $DOMAIN_SIGN_CONFIG
  rm $DOMAIN_CERTIFICATE_CONFIG
  rm $DOMAIN_CERTIFICATE_FILE
}

function usage()
{
  echo "Script tool for CA key/cert and Domain key/cert generation/signature"
  echo ""
  echo "Attention: "
  echo -e "\tfor action about DOMAIN, edit cert.conf & cert.txt first"
  echo -e ""
  echo " bash ./CA.sh"
  echo -e "\t-h --help"
  echo -e "\t-d --domain_name=DOMAIN_NAME"
  echo -e "\t-a --actions=<action><,action2><...>"
  echo -e ""
  echo -e "available action list:"
  echo -e "\t - all"
  echo -e "\t - domain, include initCert/signCert/checkCert"
  echo -e "\t - initCA"
  echo -e "\t - initCert"
  echo -e "\t - signCert"
  echo -e "\t - checkCert"
}

echo "---- FsyyPrivate CA Tools ----"

if [ "$1" == "" ]; then
    usage
    exit
fi

MODULE_NAME="mdl"
while [ "$1" != "" ]; do
    PARAM=`echo $1 | awk -F= '{print $1}'`
    VALUE=`echo $1 | awk -F= '{print $2}'`
    case $PARAM in
      -h | --help)
        usage
        exit
        ;;
      -d | --domain_name)
        DOMAIN_NAME=$VALUE
        ;;
      -a | --actions)
        ACTIONS=$VALUE
        ;;
      -m | --module)
        MODULE_NAME="mdl"
        ;;
      *)
        echo "ERROR: unknown parameter \"$PARAM\""
        usage
        exit 1
        ;;
    esac
    shift
done

DOMAIN_PRIVATE_KEY_FILE=$DOMAIN_NAME'.key'
DOMAIN_CERTIFICATE_FILE=$DOMAIN_NAME'.csr'
DOMAIN_SIGN_CONFIG=$DOMAIN_NAME'.txt'
DOMAIN_CERTIFICATE_CONFIG=$DOMAIN_NAME'.conf'
DOMAIN_SIGNED_CERT_FILE=$DOMAIN_NAME'.crt'

IFS=', ' read -r -a actionList <<< "$ACTIONS"
for action in "${actionList[@]}"
do
    echo "> Execute action-$action"
    case $action in
      all)
        initCA
        initCert
        signCert
        checkCert
        exit
        ;;
      domain)
        initCert
        signCert
        checkCert
        packCerts
        exit
        ;;
      initCA)
        initCA
        ;;
      initCert)
        initCert
        ;;
      signCert)
        signCert
        ;;
      checkCert)
        checkCert
        ;;
      *)
        echo "ERROR: unknown action \"$action\""
        usage
        exit 1
        ;;
    esac
done
```


# cert.conf
```text
[ req ]
default_bits        = 2048
default_keyfile     = keyfile.pem
distinguished_name  = req_distinguished_name
prompt              = no

[ req_distinguished_name ]
C                   = CN
ST                  = Beijing
L                   = Beijing
O                   = fsyy
OU                  = fsyy unit
CN                  = fsyy
emailAddress        = fsyy@fsyy.com

```


# cert.txt
```text
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names

[alt_names]
DNS.1 = localhost
DNS.2 = fsyy.com
DNS.3 = *.fsyy.com
DNS.4 = test.com
DNS.5 = *.test.com
IP.1 = 127.0.0.1
IP.2 = 10.10.10.10
```


# distrib_cert.sh
```shell script
#!/bin/bash
####################################
# Author: fsyy
# Usage: ./distrib_cert.sh -t /home/ubuntu/giftzyx -h sd-cus-sandbox -f store-prod-vl/JdTusita •m mdl
####################################
SSH_HOST=''
CERT_FROM=''
CERT_TO=''
MODULE_TYPE='mdl'

while getopts ":h:t:f:m:" arg; do 
  case $arg in 
    h)
      SSH_HOST=${OPTARG}
      ;;
    f)
      CERT_FROM=${OPTARG}
      ;;
    t)
      CERT_TO=${OPTARG}
      ;;
    m)
      MODULE_TYPE=${OPTARG}
      ;;
    \?)
      echo "Invalid option: -${OPTARG}" >&2
      exit 2
      ;;
    *)
      echo "Invalid option: -${OPTARG}" >&2
      exit 2
      ;;
  esac
done
flag=''
if [[ -z "${SSH_HOST}" ]]; then 
  echo "-h SSH_HOST should not be empty"
  flag='SSH_HOST'
fi
if [[ -z "${CERT_FROM}" ]]; then
  echo "-f CERT_FROM should not be empty"
  flag='CERT_FROM'
fi
if [[ -z "${CERT_TO}" ]]; then
  echo "-t CERT_TO should not be empty"
  flag='CERT_TO'
fi

if [[ ! -z "${flag}" ]]; then
  exit 2
fi

CERT_DIR="$( cd "$( dirname "${CERT_FROM}.crt" )" >/dev/null 2>&1 && pwd )"
echo "$SSH_HOST, $CERT_FROM, $CERT_TO, $MODULE_TYPE, $CERT_DIR"

ssh $SSH_HOST "cd $CERT_TO && cp -r ssl_cert ssl_cert_old && rm -fr ssl_cert/* && mkdir -p ssl_cert/CA && mkdir -p ssl_cert/client"
scp $CERT_FROM.crt $SSH_HOST:$CERT_TO/ssl_cert/client/$MODULE_TYPE.crt
scp $CERT_FROM.key $SSH_HOST:$CERT_TO/ssl_cert/client/$MODULE_TYPE.key
scp $CERT_DIR/myCA.crt $SSH_HOST:$CERT_TO/ssl_cert/CA/myCA.crt
scp $CERT_DIR/myCA.pem $SSH_HOST:$CERT_TO/ssl_cert/CA/myCA.pem
```