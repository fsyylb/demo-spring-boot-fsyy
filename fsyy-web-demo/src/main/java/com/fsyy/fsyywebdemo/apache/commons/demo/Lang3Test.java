package com.fsyy.fsyywebdemo.apache.commons.demo;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class Lang3Test {
    public static void main(String[] args) {

        /**
         * StringUtils.isEmpty(CharSequence str)
         *
         * 解释： 检查字符串是否为空（null或长度为0）
         */
        System.out.println(StringUtils.isEmpty(" "));  // 长度为1的" "空格字符返回false

        /**
         * StringUtils.isNotBlank(CharSequence str)
         *
         * 解释： 检查字符串是否非空且不仅包含空格
         */
        System.out.println(StringUtils.isNotBlank(" "));  // false

        /**
         * StringUtils.defaultIfEmpty(String str, String defaultStr)
         *
         * 解释： 如果字符串为空，则返回默认字符串
         */
        System.out.println(StringUtils.defaultIfEmpty("", "test")); // 注意empty和blank的区别

        /**
         * StringUtils.strip(String str)
         *
         * 解释： 去除字符串两端的空格
         */
        System.out.println(StringUtils.strip("  test  "));

        /**
         * StringUtils.substring(String str, int start, int end)
         *
         * 解释： 截取字符串的子串
         */
        System.out.println(StringUtils.substring("abcdefg", 1, 3)); // start从0开始，不包括end；  bc

        /**
         * StringUtils.join(Iterable<?> iterable, String separator)
         *
         * 解释： 将可迭代对象的元素连接成一个字符串，使用指定分隔符分隔
         */
        System.out.println(StringUtils.join(Arrays.asList("a", "b", "c"), "--"));  // a--b--c

        /**
         * StringUtils.replace(String text, String searchString, String replacement)
         *
         * 解释： 替换字符串中的指定子字符串
         */
        System.out.println(StringUtils.replace("test", "t", "=="));   // ==es==

        /**
         * StringUtils.capitalize(String str)
         *
         * 解释： 将字符串的第一个字符转为大写
         */
        System.out.println(StringUtils.capitalize("abc"));  // Abc

        /**
         * StringUtils.uncapitalize(String str)
         *
         * 解释： 将字符串的第一个字符转为小写
         */
        System.out.println(StringUtils.uncapitalize("Abc")); // abc
    }
}
