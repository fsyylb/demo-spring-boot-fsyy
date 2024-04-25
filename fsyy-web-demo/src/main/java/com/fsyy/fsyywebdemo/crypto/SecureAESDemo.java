package com.fsyy.fsyywebdemo.crypto;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

/**
 * AES全称高级加密标准（英语：Advanced Encryption Standard，缩写：AES），在密码学中又称Rijndael加密法。
 *
 * 对于Java中AES的默认模式是：AES/ECB/PKCS5Padding，如果使用CryptoJS，请调整为：padding: CryptoJS.pad.Pkcs7
 *
 * 自定义内置模式和偏移
 * AES aes = new AES(Mode.CTS, Padding.PKCS5Padding, "0CoJUm6Qyw8W8jud".getBytes(), "0102030405060708".getBytes());
 *
 * PKCS7Padding模式
 * 由于IOS等移动端对AES加密有要求，必须为PKCS7Padding模式，但JDK本身并不提供这种模式，因此想要支持必须做一些工作。
 *
 * 首先引入bc库：
 *
 * <dependency>
 * 	<groupId>org.bouncycastle</groupId>
 * 	<artifactId>bcprov-jdk15to18</artifactId>
 * 	<version>1.68</version>
 * </dependency>
 * AES aes = new AES("CBC", "PKCS7Padding",
 *   // 密钥，可以自定义
 *   "0123456789ABHAEQ".getBytes(),
 *   // iv加盐，按照实际需求添加
 *   "DYgjCEIMVrj2W9xN".getBytes());
 *
 * // 加密为16进制表示
 * String encryptHex = aes.encryptHex(content);
 * // 解密
 * String decryptStr = aes.decryptStr(encryptHex);
 */
public class SecureAESDemo {
    public static void main(String[] args) {
        String content = "test中文";
        // 随机生成密钥
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();

        // 构建
        AES aes = SecureUtil.aes(key);

        // 加密
        byte[] encrypt = aes.encrypt(content);
        // 解密
        byte[] decrypt = aes.decrypt(encrypt);

        // 加密为16进制表示
        String encryptHex = aes.encryptHex(content);
        // 解密为字符串
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
    }
}
