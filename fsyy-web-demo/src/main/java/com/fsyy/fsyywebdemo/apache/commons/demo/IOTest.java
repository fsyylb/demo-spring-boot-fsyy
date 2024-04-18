package com.fsyy.fsyywebdemo.apache.commons.demo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class IOTest {
    public static void main(String[] args) throws IOException {
        /*  FileUtils
        方法名	使用说明
cleanDirectory	清空目录，但不删除目录
contentEquals	比较两个文件的内容是否相同
copyDirectory	将一个目录内容拷贝到另一个目录。可以通过FileFilter过滤需要拷贝的文件
copyFile	将一个文件拷贝到一个新的地址
copyFileToDirectory	将一个文件拷贝到某个目录下
copyInputStreamToFile	将一个输入流中的内容拷贝到某个文件
deleteDirectory	删除目录
deleteQuietly	删除文件
listFiles	列出指定目录下的所有文件
openInputSteam	打开指定文件的输入流
readFileToString	将文件内容作为字符串返回
readLines	将文件内容按行返回到一个字符串数组中
size	返回文件或目录的大小
write	将字符串内容直接写到文件中
writeByteArrayToFile	将字节数组内容写到文件中
writeLines	将容器中的元素的toString方法返回的内容依次写入文件中
writeStringToFile	将字符串内容写到文件中
         */

        /**
         * 读取文件内容，并输出到控制台上
         */
        String content = FileUtils.readFileToString(new File(new ClassPathResource("application.properties").getURI()), "gbk");
        System.out.println(content);

        /**
         * 可以使用FileUtils完成目录拷贝，在拷贝过程中可以通过文件过滤器(FileFilter)选择拷贝内容
         */
        /*FileUtils.copyDirectory(new File(new ClassPathResource("application.properties").getFile().getParent()), new File(new ClassPathResource("application.properties").getFile().getParent() + "_copy"), new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // 使用FileFilter过滤目录和以html结尾的文件
                if (pathname.isDirectory() || pathname.getName().endsWith("html")) {
                    return true;
                } else {
                    return false;
                }
            }
        });*/



        /* IOUtils的妙用
方法名	使用说明
buffer	将传入的流进行包装，变成缓冲流。并可以通过参数指定缓冲大小
closeQueitly	关闭流
contentEquals	比较两个流中的内容是否一致
copy	将输入流中的内容拷贝到输出流中，并可以指定字符编码
copyLarge	将输入流中的内容拷贝到输出流中，适合大于2G内容的拷贝
lineIterator	返回可以迭代每一行内容的迭代器
read	将输入流中的部分内容读入到字节数组中
readFully	将输入流中的所有内容读入到字节数组中
readLine	读入输入流内容中的一行
toBufferedInputStream，toBufferedReader	将输入转为带缓存的输入流
toByteArray，toCharArray	将输入流的内容转为字节数组、字符数组
toString	将输入流或数组中的内容转化为字符串
write	向流里面写入内容
writeLine	向流里面写入一行内容
         */
        content = IOUtils.toString(new ClassPathResource("application.properties").getURI(), StandardCharsets.UTF_8);
        System.out.println(content);
    }
}
