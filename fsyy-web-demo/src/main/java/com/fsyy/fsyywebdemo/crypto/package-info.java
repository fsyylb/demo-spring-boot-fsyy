package com.fsyy.fsyywebdemo.crypto;

/**
 * 加密分为三种：
 *
 * 对称加密（symmetric），例如：AES、DES等
 * 非对称加密（asymmetric），例如：RSA、DSA等
 * 摘要加密（digest），例如：MD5、SHA-1、SHA-256、HMAC等
 * hutool-crypto针对这三种加密类型分别封装，并提供常用的大部分加密算法。
 *
 * 对于非对称加密，实现了：
 *
 * RSA
 * DSA
 * 对于对称加密，实现了：
 *
 * AES
 * ARCFOUR
 * Blowfish
 * DES
 * DESede
 * RC2
 * PBEWithMD5AndDES
 * PBEWithSHA1AndDESede
 * PBEWithSHA1AndRC2_40
 * 对于摘要算法实现了：
 *
 * MD2
 * MD5
 * SHA-1
 * SHA-256
 * SHA-384
 * SHA-512
 * HmacMD5
 * HmacSHA1
 * HmacSHA256
 * HmacSHA384
 * HmacSHA512
 * 其中，针对常用到的算法，模块还提供SecureUtil工具类用于快速实现加密。
 */

/**
 * 加密解密工具-SecureUtil
 * 介绍
 * SecureUtil主要针对常用加密算法构建快捷方式，还有提供一些密钥生成的快捷工具方法。
 *
 * #方法介绍
 * #对称加密
 * SecureUtil.aes
 * SecureUtil.des
 * #摘要算法
 * SecureUtil.md5
 * SecureUtil.sha1
 * SecureUtil.hmac
 * SecureUtil.hmacMd5
 * SecureUtil.hmacSha1
 * #非对称加密
 * SecureUtil.rsa
 * SecureUtil.dsa
 * #UUID
 * SecureUtil.simpleUUID 方法提供无“-”的UUID
 * #密钥生成
 * SecureUtil.generateKey 针对对称加密生成密钥
 * SecureUtil.generateKeyPair 生成密钥对（用于非对称加密）
 * SecureUtil.generateSignature 生成签名（用于非对称加密）
 * 其它方法为针对特定加密方法的一些密钥生成和签名相关方法，详细请参阅API文档。
 */

/**
 * 国密算法工具-SmUtil
 * 介绍
 * Hutool针对Bouncy Castle做了简化包装，用于实现国密算法中的SM2、SM3、SM4。
 *
 * 国密算法工具封装包括：
 *
 * 非对称加密和签名：SM2
 * 摘要签名算法：SM3
 * 对称加密：SM4
 * 国密算法需要引入Bouncy Castle库的依赖。
 *
 * #使用
 * #引入Bouncy Castle依赖
 * <dependency>
 *   <groupId>org.bouncycastle</groupId>
 *   <artifactId>bcprov-jdk15to18</artifactId>
 *   <version>1.69</version>
 * </dependency>
 * 说明 bcprov-jdk15to18的版本请前往Maven中央库搜索，查找对应JDK的最新版本。
 *
 * #非对称加密SM2
 * 使用随机生成的密钥对加密或解密
 * String text = "我是一段测试aaaa";
 *
 * SM2 sm2 = SmUtil.sm2();
 * // 公钥加密，私钥解密
 * String encryptStr = sm2.encryptBcd(text, KeyType.PublicKey);
 * String decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd(encryptStr, KeyType.PrivateKey));
 * 使用自定义密钥对加密或解密
 * String text = "我是一段测试aaaa";
 *
 * KeyPair pair = SecureUtil.generateKeyPair("SM2");
 * byte[] privateKey = pair.getPrivate().getEncoded();
 * byte[] publicKey = pair.getPublic().getEncoded();
 *
 * SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
 * // 公钥加密，私钥解密
 * String encryptStr = sm2.encryptBcd(text, KeyType.PublicKey);
 * String decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd(encryptStr, KeyType.PrivateKey));
 * SM2签名和验签
 * String content = "我是Hanley.";
 * final SM2 sm2 = SmUtil.sm2();
 * String sign = sm2.signHex(HexUtil.encodeHexStr(content));
 *
 * // true
 * boolean verify = sm2.verifyHex(HexUtil.encodeHexStr(content), sign);
 * 当然，也可以自定义密钥对：
 *
 * String content = "我是Hanley.";
 * KeyPair pair = SecureUtil.generateKeyPair("SM2");
 * final SM2 sm2 = new SM2(pair.getPrivate(), pair.getPublic());
 *
 * byte[] sign = sm2.sign(content.getBytes());
 *
 * // true
 * boolean verify = sm2.verify(content.getBytes(), sign);
 * 使用SM2曲线点构建SM2
 * 使用曲线点构建中的点生成和验证见：https://i.goto327.top/CryptTools/SM2.aspx?tdsourcetag=s_pctim_aiomsg(opens new window)
 *
 * String privateKeyHex = "FAB8BBE670FAE338C9E9382B9FB6485225C11A3ECB84C938F10F20A93B6215F0";
 * String x = "9EF573019D9A03B16B0BE44FC8A5B4E8E098F56034C97B312282DD0B4810AFC3";
 * String y = "CC759673ED0FC9B9DC7E6FA38F0E2B121E02654BF37EA6B63FAF2A0D6013EADF";
 *
 * // 数据和ID此处使用16进制表示
 * String data = "434477813974bf58f94bcf760833c2b40f77a5fc360485b0b9ed1bd9682edb45";
 * String id = "31323334353637383132333435363738";
 *
 * final SM2 sm2 = new SM2(privateKeyHex, x, y);
 * // 生成的签名是64位
 * sm2.usePlainEncoding();
 *
 * final String sign = sm2.signHex(data, id);
 * // true
 * boolean verify = sm2.verifyHex(data, sign)
 * 使用私钥D值签名
 * //需要签名的明文,得到明文对应的字节数组
 * byte[] dataBytes = "我是一段测试aaaa".getBytes();
 * //指定的私钥
 * String privateKeyHex = "1ebf8b341c695ee456fd1a41b82645724bc25d79935437d30e7e4b0a554baa5e";
 *
 * // 此构造从5.5.9开始可使用
 * final SM2 sm2 = new SM2(privateKeyHex, null, null);
 * sm2.usePlainEncoding();
 * byte[] sign = sm2.sign(dataBytes, null);
 * 使用公钥Q值验证签名
 * //指定的公钥
 * String publicKeyHex ="04db9629dd33ba568e9507add5df6587a0998361a03d3321948b448c653c2c1b7056434884ab6f3d1c529501f166a336e86f045cea10dffe58aa82ea13d725363";
 * //需要加密的明文,得到明文对应的字节数组
 * byte[] dataBytes = "我是一段测试aaaa".getBytes();
 * //签名值
 * String signHex ="2881346e038d2ed706ccdd025f2b1dafa7377d5cf090134b98756fafe084dddbcdba0ab00b5348ed48025195af3f1dda29e819bb66aa9d4d088050ff148482a";
 *
 * final SM2 sm2 = new SM2(null, ECKeyUtil.toSm2PublicParams(publicKeyHex));
 * sm2.usePlainEncoding();
 *
 * // true
 * boolean verify = sm2.verify(dataBytes, HexUtil.decodeHex(signHex));
 * 其他格式的密钥
 * 在SM2算法中，密钥的格式分以下几种：
 *
 * 私钥：
 *
 * D值 一般为硬件直接生成的值
 * PKCS#8 JDK默认生成的私钥格式
 * PKCS#1 一般为OpenSSL生成的的EC密钥格式
 * 公钥：
 *
 * Q值 一般为硬件直接生成的值
 * X.509 JDK默认生成的公钥格式
 * PKCS#1 一般为OpenSSL生成的的EC密钥格式
 * 在新版本的Hutool中，SM2的构造方法对这几类的密钥都做了兼容，即用户无需关注密钥类型：
 *
 *
 * 摘要加密算法SM3
 * //结果为：136ce3c86e4ed909b76082055a61586af20b4dab674732ebd4b599eef080c9be
 * String digestHex = SmUtil.sm3("aaaaa");
 * #对称加密SM4
 * String content = "test中文";
 * SymmetricCrypto sm4 = SmUtil.sm4();
 *
 * String encryptHex = sm4.encryptHex(content);
 * String decryptStr = sm4.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
 */

/**
 * 对称加密-SymmetricCrypto
 * 对称加密(也叫私钥加密)指加密和解密使用相同密钥的加密算法。有时又叫传统密码算法，就是加密密钥能够从解密密钥中推算出来，同时解密密钥也可以从加密密钥中推算出来。
 * 而在大多数的对称算法中，加密密钥和解密密钥是相同的，所以也称这种加密算法为秘密密钥算法或单密钥算法。它要求发送方和接收方在安全通信之前，商定一个密钥。
 * 对称算法的安全性依赖于密钥，泄漏密钥就意味着任何人都可以对他们发送或接收的消息解密，所以密钥的保密性对通信的安全性至关重要。
 *
 * 对于对称加密，封装了JDK的，具体介绍见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyGenerator (opens new window)：
 *
 * AES (默认AES/ECB/PKCS5Padding)
 * ARCFOUR
 * Blowfish
 * DES (默认DES/ECB/PKCS5Padding)
 * DESede
 * RC2
 * PBEWithMD5AndDES
 * PBEWithSHA1AndDESede
 * PBEWithSHA1AndRC2_40
 */

/**
 * MD5 + 盐是一种密码存储的安全机制。MD5，全称为Message Digest Algorithm 5，是一种常用的哈希函数。在密码存储中，通常会使用盐（salt）来增加密码的安全性。
 *
 * 使用MD5 + 盐的密码存储过程如下：
 * 1. 随机生成一个盐值，将其与用户的密码拼接在一起。
 * 2. 使用MD5函数对拼接后的字符串进行哈希计算，生成一个固定长度的哈希值。
 * 3. 将盐值和哈希值一起存储在数据库中，以备后续验证使用。
 *
 * 在密码验证过程中，使用相同的步骤生成哈希值，并将其与数据库中存储的哈希值进行比对。如果相同，则说明密码正确。
 *
 * 使用盐值可以增加密码的安全性，即使两个用户使用相同的密码，由于盐值的不同，其生成的哈希值也会不同，从而防止彩虹表等常见的攻击手段。
 * 但需要注意的是，单独使用MD5已不再被视为安全的方式，推荐使用更强大的哈希函数，如SHA-256等。同时，还可以与盐值结合使用其他的密码加密技术，以增加密码的安全性。
 */

/**
 * 在分组加密中，常常需要一个IV。
 * 这时由于分组加密是将明文切成一小块一小块来加密，每一小块的大小就跟密钥长度一样，但是由于每个小块的密钥是一样的，
 * 如果直接对一小块一小块明文进行加密的话，会发现相同明文相同密钥则密文相同（ECB模块），很容易被分析攻击。所以分组加密需要块一小块一小块的分组加密进行连接，
 * 并设计让相同的明文和相同的密钥下得到不同的密文，所以需要要明文和密钥运算中加入一个干扰项，这个干扰项就可以用上一块的密文，
 * 这时候就出现一个问题，第0块明文块加密没有上一块的密文来干扰，就得引入一个IV值。CBC、CFB、OFB差异就只是具体连接的方式不同而已，但实际工程应用最多的是CBC方式。
 * 有时为了方便也会用ECB，但一般用来加密存储的多一些。
 *
 */

/**
 * DES封装
 * DES全称为Data Encryption Standard，即数据加密标准，是一种使用密钥加密的块算法，Java中默认实现为：DES/CBC/PKCS5Padding
 *
 * DES使用方法与AES一致，构建方法为：
 *
 * 快速构建
 * byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.DES.getValue()).getEncoded();
 * DES des = SecureUtil.des(key);
 * 自定义模式和偏移
 * DES des = new DES(Mode.CTS, Padding.PKCS5Padding, "0CoJUm6Qyw8W8jud".getBytes(), "01020304".getBytes());
 */


/**
 * 在4.2.1之后，Hutool借助Bouncy Castle库可以支持国密算法，以SM4为例：
 *
 * 我们首先需要引入Bouncy Castle库：
 *
 * <dependency>
 *   <groupId>org.bouncycastle</groupId>
 *   <artifactId>bcpkix-jdk15on</artifactId>
 *   <version>1.60</version>
 * </dependency>
 * 然后可以调用SM4算法，调用方法与其它算法一致：
 *
 * String content = "test中文";
 * SymmetricCrypto sm4 = new SymmetricCrypto("SM4");
 *
 * String encryptHex = sm4.encryptHex(content);
 * String decryptStr = sm4.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);//test中文
 * 同样我们可以指定加密模式和偏移：
 *
 * String content = "test中文";
 * SymmetricCrypto sm4 = new SymmetricCrypto("SM4/ECB/PKCS5Padding");
 *
 * String encryptHex = sm4.encryptHex(content);
 * String decryptStr = sm4.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);//test中文
 */

//********************************************************************************************************************

/**
 * 摘要加密-Digester
 * 摘要算法介绍
 * 摘要算法是一种能产生特殊输出格式的算法，这种算法的特点是：无论用户输入什么长度的原始数据，经过计算后输出的密文都是固定长度的，
 * 这种算法的原理是根据一定的运算规则对原数据进行某种形式的提取，这种提取就是摘要，被摘要的数据内容与原数据有密切联系，只要原数据稍有改变，
 * 输出的“摘要”便完全不同，因此，基于这种原理的算法便能对数据完整性提供较为健全的保障。
 *
 * 但是，由于输出的密文是提取原数据经过处理的定长值，所以它已经不能还原为原数据，即消息摘要算法是不可逆的，理论上无法通过反向运算取得原数据内容，
 * 因此它通常只能被用来做数据完整性验证。
 *
 * 在不引入第三方库的情况下，JDK支持有限的摘要算法：
 *
 * 详细见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest(opens new window)
 *
 * #摘要算法
 * MD2
 * MD5
 * SHA-1
 * SHA-256
 * SHA-384
 * SHA-512
 */

/**
 * Digester
 * 以MD5为例：
 *
 * Digester md5 = new Digester(DigestAlgorithm.MD5);
 *
 * // 5393554e94bf0eb6436f240a4fd71282
 * String digestHex = md5.digestHex(testStr);
 * 当然，做为最为常用的方法，MD5等方法被封装为工具方法在DigestUtil中，以上代码可以进一步简化为：
 *
 * // 5393554e94bf0eb6436f240a4fd71282
 * String md5Hex1 = DigestUtil.md5Hex(testStr);
 *
 * 更多摘要算法
 * #SM3
 * 在4.2.1之后，Hutool借助Bouncy Castle库可以支持国密算法，以SM3为例：
 *
 * 我们首先需要引入Bouncy Castle库：
 *
 * <dependency>
 *   <groupId>org.bouncycastle</groupId>
 *   <artifactId>bcprov-jdk15to18</artifactId>
 *   <version>1.66</version>
 * </dependency>
 * 然后可以调用SM3算法，调用方法与其它摘要算法一致：
 *
 * Digester digester = DigestUtil.digester("sm3");
 *
 * //136ce3c86e4ed909b76082055a61586af20b4dab674732ebd4b599eef080c9be
 * String digestHex = digester.digestHex("aaaaa");
 * Java标准库的java.security包提供了一种标准机制，允许第三方提供商无缝接入。当引入Bouncy Castle库的jar后，Hutool会自动检测并接入。
 * 具体方法可见SecureUtil.createMessageDigest
 */


/**
 * 消息认证码算法-HMac
 * HMAC介绍
 * HMAC，全称为“Hash Message Authentication Code”，中文名“散列消息鉴别码”，主要是利用哈希算法，以一个密钥和一个消息为输入，生成一个消息摘要作为输出。一般的，消息鉴别码用于验证传输于两个共同享有一个密钥的单位之间的消息。HMAC 可以与任何迭代散列函数捆绑使用。MD5 和 SHA-1 就是这种散列函数。HMAC 还可以使用一个用于计算和确认消息鉴别值的密钥。
 *
 * #Hutool支持的算法类型
 * #Hmac算法
 * 在不引入第三方库的情况下，JDK支持有限的摘要算法：
 *
 * HmacMD5
 * HmacSHA1
 * HmacSHA256
 * HmacSHA384
 * HmacSHA512
 *
 *
 * 使用
 * HMac
 * 以HmacMD5为例：
 *
 * String testStr = "test中文";
 *
 * // 此处密钥如果有非ASCII字符，考虑编码
 * byte[] key = "password".getBytes();
 * HMac mac = new HMac(HmacAlgorithm.HmacMD5, key);
 *
 * // b977f4b13f93f549e06140971bded384
 * String macHex1 = mac.digestHex(testStr);
 * #更多HMac算法
 * 与摘要算法类似，通过加入Bouncy Castle库可以调用更多算法，使用也类似：
 *
 * HMac mac = new HMac("XXXX", key);
 */


/**
 * MAC
 * 在现代的网络中，身份认证是一个经常会用到的功能，在身份认证过程中，有很多种方式可以保证用户信息的安全，而MAC(message authentication code)就是一种常用的方法。
 *
 * 消息认证码是对消息进行认证并确认其完整性的技术。通过使用发送者和接收者之间共享的密钥，就可以识别出是否存在伪装和篡改行为。
 *
 * MAC是通过MAC算法+密钥+要加密的信息一起计算得出的。
 *
 * 同hash算法（消息摘要）相比，消息摘要只能保证消息的完整性，即该消息摘要B是这个消息A生成的。而MAC算法能够保证消息的正确性，即判断确实发的是消息A而不是消息C。
 *
 * 同公私钥体系相比，因为MAC的密钥在发送方和接收方是一样的，所以发送方和接收方都可以来生成MAC，而公私钥体系因为将公钥和私钥分开，所以增加了不可抵赖性。
 *
 * MAC有很多实现方式，比较通用的是基于hash算法的MAC，比如今天我们要讲的HMAC。还有一种是基于分组密码的实现，比如(OMAC, CBC-MAC and PMAC)。
 *
 * HMAC
 * HMAC 是Keyed-Hashing for Message Authentication的缩写。HMAC的MAC算法是hash算法，它可以是MD5, SHA-1或者 SHA-256，他们分别被称为HMAC-MD5，HMAC-SHA1， HMAC-SHA256。
 *
 * HMAC用公式表示：
 *
 * H(K XOR opad, H(K XOR ipad, text))
 *
 * 其中 H：hash算法，比如（MD5，SHA-1，SHA-256） B：块字节的长度，块是hash操作的基本单位。这里B=64。 L：hash算法计算出来的字节长度。(L=16 for MD5, L=20 for SHA-1)。 K：共享密钥，K的长度可以是任意的，但是为了安全考虑，还是推荐K的长度>B。当K长度大于B时候，会先在K上面执行hash算法，将得到的L长度结果作为新的共享密钥。 如果K的长度<B, 那么会在K后面填充0x00一直到等于长度B。 text： 要加密的内容 opad：外部填充常量，是 0x5C 重复B次。 ipad： 内部填充常量，是0x36 重复B次。 XOR： 异或运算。
 *
 * 计算步骤如下： 1. 将0x00填充到K的后面，直到其长度等于B。 2. 将步骤1的结果跟 ipad做异或。 3. 将要加密的信息附在步骤2的结果后面。 4. 调用H方法。 5. 将步骤1的结果跟opad做异或。 6. 将步骤4的结果附在步骤5的结果后面。 7. 调用H方法。
 *
 * HMAC的应用
 * hmac主要应用在身份验证中，如下是它的使用过程： 1. 客户端发出登录请求（假设是浏览器的GET请求） 2. 服务器返回一个随机值，并在会话中记录这个随机值 3. 客户端将该随机值作为密钥，用户密码进行hmac运算，然后提交给服务器 4. 服务器读取用户数据库中的用户密码和步骤2中发送的随机值做与客户端一样的hmac运算，然后与用户发送的结果比较，如果结果一致则验证用户合法。
 *
 * 在这个过程中，可能遭到安全攻击的是服务器发送的随机值和用户发送的hmac结果，而对于截获了这两个值的黑客而言这两个值是没有意义的，绝无获取用户密码的可能性，随机值的引入使hmac只在当前会话中有效，大大增强了安全性和实用性。
 */


/**
 * 签名和验证-Sign
 * Hutool针对java.security.Signature做了简化包装，包装类为：Sign，用于生成签名和签名验证。
 *
 * 对于签名算法，Hutool封装了JDK的Signature，具体介绍见：https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Signature (opens new window)：
 *
 * // The RSA signature algorithm
 * NONEwithRSA
 *
 * // The MD2/MD5 with RSA Encryption signature algorithm
 * MD2withRSA
 * MD5withRSA
 *
 * // The signature algorithm with SHA-* and the RSA
 * SHA1withRSA
 * SHA256withRSA
 * SHA384withRSA
 * SHA512withRSA
 *
 * // The Digital Signature Algorithm
 * NONEwithDSA
 *
 * // The DSA with SHA-1 signature algorithm
 * SHA1withDSA
 *
 * // The ECDSA signature algorithms
 * NONEwithECDSA
 * SHA1withECDSA
 * SHA256withECDSA
 * SHA384withECDSA
 * SHA512withECDSA
 * #使用
 * byte[] data = "我是一段测试字符串".getBytes();
 * Sign sign = SecureUtil.sign(SignAlgorithm.MD5withRSA);
 * //签名
 * byte[] signed = sign.sign(data);
 * //验证签名
 * boolean verify = sign.verify(data, signed);
 */


/**
 * 非对称加密-AsymmetricCrypto
 * 对于非对称加密，最常用的就是RSA和DSA，在Hutool中使用AsymmetricCrypto对象来负责加密解密。
 *
 * 非对称加密有公钥和私钥两个概念，私钥自己拥有，不能给别人，公钥公开。根据应用的不同，我们可以选择使用不同的密钥加密：
 *
 * 签名：使用私钥加密，公钥解密。用于让所有公钥所有者验证私钥所有者的身份并且用来防止私钥所有者发布的内容被篡改，但是不用来保证内容不被他人获得。
 *
 * 加密：用公钥加密，私钥解密。用于向公钥所有者发布信息,这个信息可能被他人篡改,但是无法被他人获得。
 *
 * Hutool封装了JDK的，详细见https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator (opens new window)：
 *
 * RSA
 * RSA_ECB_PKCS1（RSA/ECB/PKCS1Padding）
 * RSA_None（RSA/None/NoPadding）
 * ECIES（需要Bouncy Castle库）
 */


/**
 * 使用
 * 在非对称加密中，我们可以通过AsymmetricCrypto(AsymmetricAlgorithm algorithm)构造方法，通过传入不同的算法枚举，获得其加密解密器。
 *
 * 当然，为了方便，我们针对最常用的RSA算法构建了单独的对象：RSA。
 *
 * #基本使用
 * 我们以RSA为例，介绍使用RSA加密和解密 在构建RSA对象时，可以传入公钥或私钥，当使用无参构造方法时，Hutool将自动生成随机的公钥私钥密钥对：
 *
 * RSA rsa = new RSA();
 *
 * //获得私钥
 * rsa.getPrivateKey();
 * rsa.getPrivateKeyBase64();
 * //获得公钥
 * rsa.getPublicKey();
 * rsa.getPublicKeyBase64();
 *
 * //公钥加密，私钥解密
 * byte[] encrypt = rsa.encrypt(StrUtil.bytes("我是一段测试aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
 * byte[] decrypt = rsa.decrypt(encrypt, KeyType.PrivateKey);
 *
 * //Junit单元测试
 * //Assert.assertEquals("我是一段测试aaaa", StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));
 *
 * //私钥加密，公钥解密
 * byte[] encrypt2 = rsa.encrypt(StrUtil.bytes("我是一段测试aaaa", CharsetUtil.CHARSET_UTF_8), KeyType.PrivateKey);
 * byte[] decrypt2 = rsa.decrypt(encrypt2, KeyType.PublicKey);
 *
 * //Junit单元测试
 * //Assert.assertEquals("我是一段测试aaaa", StrUtil.str(decrypt2, CharsetUtil.CHARSET_UTF_8));
 * 对于加密和解密可以完全分开，对于RSA对象，如果只使用公钥或私钥，另一个参数可以为null
 *
 * #自助生成密钥对
 * 有时候我们想自助生成密钥对可以：
 *
 * KeyPair pair = SecureUtil.generateKeyPair("RSA");
 * pair.getPrivate();
 * pair.getPublic();
 * 自助生成的密钥对是byte[]形式，我们可以使用Base64.encode方法转为Base64，便于存储为文本。
 *
 * 当然，如果使用RSA对象，也可以使用encryptStr和decryptStr加密解密为字符串。
 *
 *
 * 案例一：
 * 已知私钥和密文，如何解密密文？
 *
 * String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIL7pbQ+5KKGYRhw7jE31hmA"
 * 		+ "f8Q60ybd+xZuRmuO5kOFBRqXGxKTQ9TfQI+aMW+0lw/kibKzaD/EKV91107xE384qOy6IcuBfaR5lv39OcoqNZ"
 * 		+ "5l+Dah5ABGnVkBP9fKOFhPgghBknTRo0/rZFGI6Q1UHXb+4atP++LNFlDymJcPAgMBAAECgYBammGb1alndta"
 * 		+ "xBmTtLLdveoBmp14p04D8mhkiC33iFKBcLUvvxGg2Vpuc+cbagyu/NZG+R/WDrlgEDUp6861M5BeFN0L9O4hz"
 * 		+ "GAEn8xyTE96f8sh4VlRmBOvVdwZqRO+ilkOM96+KL88A9RKdp8V2tna7TM6oI3LHDyf/JBoXaQJBAMcVN7fKlYP"
 * 		+ "Skzfh/yZzW2fmC0ZNg/qaW8Oa/wfDxlWjgnS0p/EKWZ8BxjR/d199L3i/KMaGdfpaWbYZLvYENqUCQQCobjsuCW"
 * 		+ "nlZhcWajjzpsSuy8/bICVEpUax1fUZ58Mq69CQXfaZemD9Ar4omzuEAAs2/uee3kt3AvCBaeq05NyjAkBme8SwB0iK"
 * 		+ "kLcaeGuJlq7CQIkjSrobIqUEf+CzVZPe+AorG+isS+Cw2w/2bHu+G0p5xSYvdH59P0+ZT0N+f9LFAkA6v3Ae56OrI"
 * 		+ "wfMhrJksfeKbIaMjNLS9b8JynIaXg9iCiyOHmgkMl5gAbPoH/ULXqSKwzBw5mJ2GW1gBlyaSfV3AkA/RJC+adIjsRGg"
 * 		+ "JOkiRjSmPpGv3FOhl9fsBPjupZBEIuoMWOC8GXK/73DHxwmfNmN7C9+sIi4RBcjEeQ5F5FHZ";
 *
 * RSA rsa = new RSA(PRIVATE_KEY, null);
 *
 * String a = "2707F9FD4288CEF302C972058712F24A5F3EC62C5A14AD2FC59DAB93503AA0FA17113A020EE4EA35EB53F"
 * 		+ "75F36564BA1DABAA20F3B90FD39315C30E68FE8A1803B36C29029B23EB612C06ACF3A34BE815074F5EB5AA3A"
 * 		+ "C0C8832EC42DA725B4E1C38EF4EA1B85904F8B10B2D62EA782B813229F9090E6F7394E42E6F44494BB8";
 *
 * byte[] aByte = HexUtil.decodeHex(a);
 * byte[] decrypt = rsa.decrypt(aByte, KeyType.PrivateKey);
 *
 * //Junit单元测试
 * //Assert.assertEquals("虎头闯杭州,多抬头看天,切勿只管种地", StrUtil.str(decrypt, CharsetUtil.CHARSET_UTF_8));
 * #其它算法
 * #ECIES
 * ECIES全称集成加密方案（elliptic curve integrate encrypt scheme）
 *
 * Hutool借助Bouncy Castle库可以支持ECIES算法：
 *
 * 我们首先需要引入Bouncy Castle库：
 *
 * <dependency>
 *   <groupId>org.bouncycastle</groupId>
 *   <artifactId>bcprov-jdk15to18</artifactId>
 *   <version>1.66</version>
 * </dependency>
 * final ECIES ecies = new ECIES();
 * String textBase = "我是一段特别长的测试";
 * StringBuilder text = new StringBuilder();
 * for (int i = 0; i < 10; i++) {
 * 	text.append(textBase);
 * }
 *
 * // 公钥加密，私钥解密
 * String encryptStr = ecies.encryptBase64(text.toString(), KeyType.PublicKey);
 * String decryptStr = StrUtil.utf8Str(ecies.decrypt(encryptStr, KeyType.PrivateKey));
 *
 */