# pip install pycrypto(废弃)
# pip install pycryptodome

import base64
from Crypto import Random
from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_v1_5 as PKCS1_cipher


def rsa_key_generate():
    random_generator = Random.new().read
    rsa = RSA.generate(2048, random_generator)
    private_pem = rsa.exportKey().decode()
    public_pem = rsa.publickey().exportKey().decode()
    with open('private_key.pem', "w") as f:
        f.write(private_pem)
    with open('public_key.pem', "w") as f:
        f.write(public_pem)
    return private_pem, public_pem

# rsa_key_generate()

def encrypt_data(public_key, msg):
    cipher = PKCS1_cipher.new(RSA.importKey(public_key))
    encrypt_text = base64.b64encode(cipher.encrypt(bytes(msg.encode("utf8"))))
    return encrypt_text.decode('utf-8')

# public_key = b''
# with open('public_key.pem', 'rb') as f:
#   public_key = f.read()
# msg = 'hello'
# en_msg = encrypt_data(public_key, msg)

def pwdecrypt(private_key, encrypt_msg):
    try:
        cipher = PKCS1_cipher.new(RSA.importKey(private_key))
        back_text = cipher.decrypt(base64.b64decode(encrypt_msg), 0)
        return back_text.decode('utf-8')
    except Exception as e:
        raise RuntimeError(f"passwd decrypt failed: {e}")

# private_key = b''
# with open('private_key.pem', 'rb') as f:
#   private_key = f.read()
# de_msg = pwdecrypt(private_key, en_msg)


'''
1 概述
RSA加密算法是一种非对称加密算法。RSA 是1977年由罗纳德·李维斯特（Ron Rivest）、阿迪·萨莫尔（Adi Shamir）和伦纳德·阿德曼（Leonard Adleman）一起提出的。RSA就是他们三人姓氏开头字母拼在一起组成的 。

RSA加密解密，需要一对秘钥，一个是私钥，一个是公钥。使用公钥加密后，可以用私钥来解密，但使用私钥加密的数据，不能用公钥解密，只能用公钥验证加密后的数据是否被篡改。

2 openssl工具生成RSA公钥私钥
2.1 生成私钥
openssl genrsa -out rsa_private_key.pem 1024

2.2 生成公钥
openssl rsa -in rsa_private_key.pem -pubout -out rsa_public_key.pem


生成的内容类似下面的字符串
-----BEGIN RSA PRIVATE KEY-----
MIICWwIBAAKBgQCgh1mzJaDj2nxRhNa0lT6TZKE5ucGqHqgSVKKX5mQLnp1v3HtW
gytGTWQizCoLE5qxLWJk9rBDBl14ay9B6zpkFmxv32Dvc1591tRTzp4LA32tbABy
DOsLhr3MoeEsTOkFd9OIfdxRpjJqFaLcFxyHuun9UIYzPrrk38CMoRQMxwIDAQAB
AoGAQaC0Kkpi6WREWOtu/6rYR1fnlfr8Uvlvivbw9hrpodp50OaZwfekNHylSJih
Q6ADKRY92LsFupEam4Vub4ukdereweVVSxJvwNAV5MH4JlLGU9rWA4gBSFDh+8/g
gcJ+g9bnG0XYSulaBvGaCibXHb3O1ZjcZ6QZvqdJD9azmwkCQQDRxNwEbpTRwm5o
Xpbv1mYSa/H1zFJu/j03W3l02UOf+goXCuihbCT0rBXi6kS5pkXlJveVBLQHv25M
FWKXvARVAkEAw+hcY6AX7DbzH1IjabdU4h6TztqLWxxMppjKMOXDhGPhhNpzsKS3
6Mo2ZBJIrti7dTwAJVWFcIu99cAsg3CIqwJARcvIoTjiD4EwF04Rzq95uvtrwKtB
amjxmW+4U+bOl3Ys0Unx6XQAP+DaT2w/fb4TV5HFozGzbTiGzI35Bh+rGQJAKYen
lkMI7z1S2s/py+5eYzUCNnaj1iOwqBx5uFXs1bLqyuQYZFj217WORDaQC+jMDHU0
vrVbfVlSYrdjeFWx0wJAUH26P+L4cu4mBEX1alE4OGGS/LK/N3soDeW2VL8FMkru
GnKuJAnbW6G0PE/TH7AFNAFaZrTYgeGrUH0C96aXZw==
-----END RSA PRIVATE KEY-----

3 使用 Crypto 库
Crypto库的安装比较费心，因为有好几种库可以安装，功能几乎一样，最终都叫Crypto，不同系统下可安装的库的名称还不一致，但总结下来，下面的安装方法在任何平台都可用

3.1 安装库
pip install pycryptodome

3.2 生成私钥和公钥

from Crypto import Random
from Crypto.PublicKey import RSA


random_generator = Random.new().read
rsa = RSA.generate(2048, random_generator)
# 生成私钥
private_key = rsa.exportKey()
print(private_key.decode('utf-8'))
# 生成公钥
public_key = rsa.publickey().exportKey()
print(public_key.decode('utf-8'))

with open('private_key.pem', 'wb') as f:
    f.write(private_key)

with open('public_key.pem', 'wb') as f:
    f.write(public_key)


生成的秘钥，需要写入到文件中，这样便于保存，毕竟，公钥是需要交给其他人使用，你自己手里同时拿着私钥和公钥是没有意义的，和你进行数据交互的人需要公钥来验证数据是否安全。

3.3 公钥加密，私钥解密
使用公钥加密的数据，可以用私钥进行解密


import base64
from Crypto.PublicKey import RSA
from Crypto.Hash import SHA
from Crypto.Signature import PKCS1_v1_5 as PKCS1_signature
from Crypto.Cipher import PKCS1_v1_5 as PKCS1_cipher

def get_key(key_file):
    with open(key_file) as f:
        data = f.read()
        key = RSA.importKey(data)

    return key

def encrypt_data(msg):
    public_key = get_key('public_key.pem')
    cipher = PKCS1_cipher.new(public_key)
    encrypt_text = base64.b64encode(cipher.encrypt(bytes(msg.encode("utf8"))))
    return encrypt_text.decode('utf-8')


def decrypt_data(encrypt_msg):
    private_key = get_key('private_key.pem')
    cipher = PKCS1_cipher.new(private_key)
    back_text = cipher.decrypt(base64.b64decode(encrypt_msg), 0)
    return back_text.decode('utf-8')

def test_encrypt_decrypt():
    msg = "coolpython.net"
    encrypt_text = encrypt_data(msg)
    decrypt_text = decrypt_data(encrypt_text)
    print(msg == decrypt_text)

test_encrypt_decrypt()     # True

公钥加密的数据，只有私钥才可以解密

3.4 私钥制作签名，公钥验证签名
使用私钥生成签名，并没有对数据进行加密，另一方在获取数据后，可以利用签名进行验证，如果数据传输的过程中被篡改了，那么签名验证就会失败。你可能已经想到了一种可能性，原始数据被篡改了，签名也同时被篡改了，这样验证就通过了。如果这种情况真的发生了，就说明篡改数据的人已经获取了私钥并利用私钥对篡改后的数据生成新的签名，否则，绝没有可能在没有私钥的情况下准确的篡改签名还能通过验证。如果篡改者能够获取私钥这种高度机密的信息，那么，防篡改已经没有意义了，因为人家已经彻底攻破了你的系统。


import base64
from Crypto.PublicKey import RSA
from Crypto.Hash import SHA
from Crypto.Signature import PKCS1_v1_5 as PKCS1_signature
from Crypto.Cipher import PKCS1_v1_5 as PKCS1_cipher

def get_key(key_file):
    with open(key_file) as f:
        data = f.read()
        key = RSA.importKey(data)

    return key

def rsa_private_sign(data):
    private_key = get_key('private_key.pem')
    signer = PKCS1_signature.new(private_key)
    digest = SHA.new()
    digest.update(data.encode("utf8"))
    sign = signer.sign(digest)
    signature = base64.b64encode(sign)
    signature = signature.decode('utf-8')
    return signature


def rsa_public_check_sign(text, sign):
    publick_key = get_key('public_key.pem')
    verifier = PKCS1_signature.new(publick_key)
    digest = SHA.new()
    digest.update(text.encode("utf8"))
    return verifier.verify(digest, base64.b64decode(sign))


def test_sign():
    msg = 'coolpython.net'
    sign = rsa_private_sign(msg)
    print(rsa_public_check_sign(msg, sign))    # True

if __name__ == '__main__':
    test_sign()


'''


import base64
from Crypto.PublicKey import RSA
from Crypto.Hash import SHA
from Crypto.Signature import PKCS1_v1_5 as PKCS1_signature
from Crypto.Cipher import PKCS1_v1_5 as PKCS1_cipher

def rsa_private_sign(private_key, data):
    signer = PKCS1_signature.new(private_key)
    digest = SHA.new()
    digest.update(data.encode("utf-8"))
    sign = signer.sign(digest)
    signature = base64.b64encode(sign).decode("utf-8")
    return signature

def rsa_public_check_sign(public_key, text, sign):
    verifier = PKCS1_signature.new(public_key)
    digest = SHA.new()
    digest.update(text.encode("utf-8"))
    return verifier.verify(digest, base64.b64decode(sign))

private_key = None
public_key = None
with open('public_key.pem', 'rb') as f:
    public_key = RSA.importKey(f.read())

with open('private_key.pem', 'rb') as f:
    private_key = RSA.importKey(f.read())

data = 'hello'
signature = rsa_private_sign(private_key, data)
checksign = rsa_public_check_sign(public_key, data, signature)




#### https://blog.csdn.net/kobe_okok/article/details/124925803
#### https://blog.csdn.net/weixin_42323041/article/details/105832325
#### https://www.cnblogs.com/qxh-beijing2016/p/15249911.html

#### (r ** e % n * m) ** d % n = (r * (m ** d % n) ) % n