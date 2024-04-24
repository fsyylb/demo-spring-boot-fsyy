
JCE（Java Cryptography Extension）是一组包，它们提供用于加密、密钥生成和协商以及 Message Authentication Code（MAC）算法的框架和实现。

Apache Commons Crypto 是Apache Commons下的子项目，是一个使用 AES-NI（高级加密标准新指令）优化的加密库。Commons Crypto 在密码级别和 Java 流级别提供 Java API。开发人员可以以最少的编码和工作量实现高性能 AES 加密/解密。请注意，Commons Crypto 没有直接实现 AES 等加密算法，它封装了 OpenSSL 和 JCE，以下几个方面为它的特征：
参考：https://www.chendd.cn/blog/article/1538521527048265729.html

Commons codec，是项目中用来处理常用的编码方法的工具类包，例如DES、SHA1、MD5、Base64，URL，Soundx等等。不仅是编码，也可用于解码。
在实际的应用中，我们经常需要对字符串进行编解码，Apache Commons家族中的Commons Codec就提供了一些公共的编解码实现，比如Base64, Hex, MD5,Phonetic and URLs等等。
参考：https://www.cnblogs.com/LiuChunfu/p/5621703.html

一、JAVA的密码算法库 
 1、标准库： 
 (1) JCA/JCE: 提供了密码算法接口/最基本的密码算法实现  
 JCA:interface 
 (2)JSSE: SSL/TLS协议在JAVA内的实现 
 (3)JAAS:JAVA的授权和认证服务 
 2、专用的JAVA语言密码学算法库： 
 Brouncy Castle 


java常用的加解密库
常用的Java加解密库，包括Bouncy Castle、Jasypt和Java Cryptography Extension（JCE）
参考：https://blog.51cto.com/u_16213420/7666923

Jasypt：
Digester仅提供两个setter，setAlgorithm用户设置算法类型，支持的算法类型有：MD2, MD5, SHA, SHA-224, SHA-256, SHA-384, SHA-512, SHA-512/224, SHA-512/256, SHA3-224, SHA3-256, SHA3-384, SHA3-512，setProvider，设置算法提供的厂商Provider，基本不会用到，缺省即可。
参考：https://blog.csdn.net/u012247462/article/details/129594133



推荐10个加解密开源项目
其中：ClassFinal - Java 字节码加密工具
参考：https://zhuanlan.zhihu.com/p/465460235



Java各种方式加解密工具类
参考：https://blog.csdn.net/u011943534/article/details/129217179


使用hutool非对称性RSA加密、解密
package yc_hutool;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

// #MAIN 使用hutool非对称性加密、解密
public class Main {

    public static void main(String[] args) {
        // 1. 利用空构造器的RSA获取Base64位的publicKey, privateKey
        RSA rsa = new RSA();
        String privateKeyBase64 = rsa.getPrivateKeyBase64();
        String publicKeyBase64 = rsa.getPublicKeyBase64();

        // 2. 根据公钥生成密文
        RSA rsa1 = new RSA(null, publicKeyBase64);
        byte[] encrypt = rsa1.encrypt(StrUtil.bytes("我是一段测试aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
        String hexStr = HexUtil.encodeHexStr(encrypt);

        // 3. 根据私钥解密密文
        RSA rsa2 = new RSA(privateKeyBase64, null);
        byte[] decodeHex = HexUtil.decodeHex(hexStr);
        byte[] decrypt1 = rsa2.decrypt(decodeHex, KeyType.PrivateKey);
        System.out.println(StrUtil.str(decrypt1, CharsetUtil.CHARSET_UTF_8));
    }
}



摘要算法、对称加密、非对称加密、数字签名、数字证书浅析
参考：https://zhibo.blog.csdn.net/article/details/79465163