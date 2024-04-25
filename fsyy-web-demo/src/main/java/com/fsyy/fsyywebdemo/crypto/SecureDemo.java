package com.fsyy.fsyywebdemo.crypto;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

public class SecureDemo {
    public static void main(String[] args) {

        //*****************************************AES****************************************
        String content = "test中文";
        //随机生成密钥
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        //构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);
        //加密
        byte[] encrypt = aes.encrypt(content);
        //解密
        byte[] decrypt = aes.decrypt(encrypt);
        //加密为16进制表示
        String encryptHex = aes.encryptHex(content);
        //解密为字符串
        String decryptStr = aes.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);


        //*****************************************AES****************************************
        /*
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
         */

    }
}
