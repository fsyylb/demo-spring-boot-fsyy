package com.fsyy.fsyywebdemo.apache.commons.demo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;

public class Lang3Test {
    public static void main(String[] args) throws ParseException {

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


        /**
         * DateUtils.parseDate(String str, String... parsePatterns)
         *
         * 解释： 将字符串解析为日期对象，支持多种日期格式
         */
        System.out.println(DateUtils.parseDate("2024-04-15", "yyyy-MM-dd"));

        /**
         * DateUtils.addDays(Date date, int amount)
         *
         * 解释： 在日期上添加指定天数
         */
        System.out.println(DateUtils.addDays(
                DateUtils.parseDate("2024-04-15", "yyyy-MM-dd"),
                16
        ));

        /**
         * DateUtils.truncate(Date date, int field)
         *
         * 解释： 截断日期，将其精度调整到指定字段（如年、月、日）
         */
        System.out.println(DateUtils.truncate(
                DateUtils.parseDate("2024-04-15", "yyyy-MM-dd"),
                Calendar.MONTH
        )); // 2024.04.01

        /**
         * DateUtils.isSameDay(Date date1, Date date2)
         *
         * 解释： 检查两个日期是否表示同一天
         */

        System.out.println(DateUtils.isSameDay(
                DateUtils.parseDate("2024-04-15", "yyyy-MM-dd"),
                Calendar.getInstance().getTime()
        ));


        /*
        NumberUtils.isNumber(String str)

解释： 检查字符串是否表示一个数字。
示例：
boolean isNumber = NumberUtils.isNumber(myString);
1
NumberUtils.toInt(String str, int defaultValue)

解释： 将字符串转换为整数，如果无法转换则返回默认值。
示例：
int intValue = NumberUtils.toInt(myString, 0);
1
ArrayUtils.contains(Object[] array, Object objectToFind)

解释： 检查数组中是否包含指定元素。
示例：
boolean contains = ArrayUtils.contains(myArray, myElement);
1
ArrayUtils.isEmpty(Object[] array)

解释： 检查数组是否为空（null或长度为0）。
示例：
if (ArrayUtils.isEmpty(myArray)) {
    // 处理空数组
}
1
2
3
ArrayUtils.removeElement(T[] array, T element)

解释： 从数组中移除指定元素。
示例：
String[] newArray = ArrayUtils.removeElement(myArray, "elementToRemove");
1
ArrayUtils.reverse(T[] array)

解释： 反转数组中的元素顺序。
示例：
ArrayUtils.reverse(myArray);
1
ArrayUtils.toString(Object[] array, String separator)

解释： 将数组转换为字符串，使用指定分隔符。
示例：
String arrayStr = ArrayUtils.toString(myArray, ",");
1
ObjectUtils.defaultIfNull(T object, T defaultValue)

解释： 如果对象为null，返回默认值。
示例：
String result = ObjectUtils.defaultIfNull(myObject, "default");
1
SystemUtils.IS_OS_WINDOWS

解释： 检查操作系统是否为Windows。
示例：
if (SystemUtils.IS_OS_WINDOWS) {
    // 在Windows操作系统上执行特定操作
}
1
2
3
ReflectionToStringBuilder.toString(Object object)

解释： 使用反射将对象转换为字符串表示。
示例：
String objStr = ReflectionToStringBuilder.toString(myObject);
1
WordUtils.capitalize(String str)

解释： 将字符串中的单词的首字母大写。
示例：
String capitalized = WordUtils.capitalize(myString);
1
WordUtils.wrap(String str, int wrapLength)

解释： 将字符串按指定长度包装成多行文本。
示例：
String wrappedText = WordUtils.wrap(myString, 20);
1
StringUtils.contains(CharSequence seq, CharSequence searchSeq)

解释： 检查字符串是否包含指定子字符串。
示例：
boolean contains = StringUtils.contains(myString, "substring");
1
StringUtils.removeStart(String str, String remove)
解释： 从字符串开头移除指定前缀。
示例：
String result = StringUtils.removeStart(myString, "prefix");
1
StringUtils.leftPad(String str, int size, char padChar)
解释： 在字符串左侧填充指定字符，直到达到指定长度。
示例：
String padded = StringUtils.leftPad(myString, 10, '0');
1
StringUtils.getLevenshteinDistance(CharSequence s, CharSequence t)
解释： 计算两个字符串之间的Levenshtein距离，即编辑距离。
示例：
int distance = StringUtils.getLevenshteinDistance("kitten", "sitting");
1
DateUtils.isSameInstant(Date date1, Date date2)
解释： 检查两个日期是否代表相同的瞬间（同一毫秒）。
示例：
boolean sameInstant = DateUtils.isSameInstant(date1, date2);
1
DateUtils.isSameLocalTime(Calendar cal1, Calendar cal2)
解释： 检查两个日历对象是否代表相同的本地时间（不考虑时区）。
示例：
boolean sameLocalTime = DateUtils.isSameLocalTime(calendar1, calendar2);
1
NumberUtils.max(int... array)
解释： 返回一组整数中的最大值。
示例：
int max = NumberUtils.max(5, 8, 2, 10);
1
NumberUtils.min(int... array)
解释： 返回一组整数中的最小值。
示例：
int min = NumberUtils.min(5, 8, 2, 10);
1
ArrayUtils.getLength(Object array)
解释： 获取数组的长度。
示例：
int length = ArrayUtils.getLength(myArray);
1
ArrayUtils.indexOf(Object[] array, Object objectToFind)
解释： 查找数组中指定元素的索引。
示例：
int index = ArrayUtils.indexOf(myArray, myElement);
1
ArrayUtils.containsOnly(T[] array, T... valuesToSearch)
解释： 检查数组是否仅包含指定的值。
示例：
boolean containsOnly = ArrayUtils.containsOnly(myArray, "A", "B", "C");
1
ArrayUtils.addAll(T[] array1, T... array2)
解释： 合并两个数组。
示例：
String[] mergedArray = ArrayUtils.addAll(myArray1, myArray2);
1
ArrayUtils.toMap(Object[] array)
解释： 将数组转换为Map对象，其中数组中的元素被解释为键-值对。
示例：
Map<String, String> map = ArrayUtils.toMap(new String[]{"key1", "value1", "key2", "value2"});
1
ObjectUtils.firstNonNull(T... values)
解释： 返回第一个非null的值，如果没有非null值，则返回null。
示例：
String result = ObjectUtils.firstNonNull(value1, value2, value3);
1
SystemUtils.USER_NAME
解释： 获取当前用户的用户名。
示例：
String userName = SystemUtils.USER_NAME;
1
SystemUtils.JAVA_VERSION
解释： 获取Java运行时的版本。
示例：
String javaVersion = SystemUtils.JAVA_VERSION;
1
ReflectionUtils.getAllFields(Class<?> cls)
解释： 获取类及其超类中的所有字段。
示例：
Field[] fields = ReflectionUtils.getAllFields(myClass);
1
ReflectionUtils.getAllMethods(Class<?> cls)
解释： 获取类及其超类中的所有方法。
示例：
Method[] methods = ReflectionUtils.getAllMethods(myClass);
1
WordUtils.initials(String str)
解释： 获取字符串中每个单词的首字母并连接起来。
示例：
String initials = WordUtils.initials("Commons Lang");
1
WordUtils.abbreviate(String str, int maxWidth)
解释： 将字符串截断为指定宽度，并在末尾添加省略号。
————————————————

                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。

原文链接：https://blog.csdn.net/weixin_53742691/article/details/134123753
         */

        /**
         * NumberUtils.isNumber(String str)
         *
         * 解释： 检查字符串是否表示一个数字
         */

        /**
         * NumberUtils.toInt(String str, int defaultValue)
         *
         * 解释： 将字符串转换为整数，如果无法转换则返回默认值
         */

        /**
         * ArrayUtils.contains(Object[] array, Object objectToFind)
         *
         * 解释： 检查数组中是否包含指定元素
         */

        /**
         * ArrayUtils.isEmpty(Object[] array)
         *
         * 解释： 检查数组是否为空（null或长度为0）
         */

        /**
         * ArrayUtils.removeElement(T[] array, T element)
         *
         * 解释： 从数组中移除指定元素
         */

        /**
         * ArrayUtils.reverse(T[] array)
         *
         * 解释： 反转数组中的元素顺序
         */

        /**
         * ArrayUtils.toString(Object[] array, String separator)
         *
         * 解释： 将数组转换为字符串，使用指定分隔符
         */

        /**
         * ObjectUtils.defaultIfNull(T object, T defaultValue)
         *
         * 解释： 如果对象为null，返回默认值
         */

        /**
         * SystemUtils.IS_OS_WINDOWS
         *
         * 解释： 检查操作系统是否为Windows
         */

        /**
         * ReflectionToStringBuilder.toString(Object object)
         *
         * 解释： 使用反射将对象转换为字符串表示
         */

    }
}
