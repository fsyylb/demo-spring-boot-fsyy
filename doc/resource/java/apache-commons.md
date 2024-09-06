# commons-compress
```text
archivers：归档
changes：变化
compressors：压缩
parallel：并行
harmony：pack算法抽离
utils：工具

Apache Commons Compress提供了许多编解码相关的工具类。Compress目前最新版本是1.21，最低要求Java8以上。
maven坐标如下：
xml 代码解读复制代码<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-compress</artifactId>
    <version>1.21</version>
</dependency>


以下为整体结构：
org.apache.commons.compress
org.apache.commons.compress.archivers
org.apache.commons.compress.changes
org.apache.commons.compress.compressors
org.apache.commons.compress.parallel
org.apache.commons.compress.utils
org.apache.commons.compress.harmony

01. 压缩
压缩：按某种算法减小文件所占用空间的大小
解压：按对应的逆向算法恢复文件
Compress自带了很多压缩相关的类，主要以下几个
**GzipCompressorOutputStream：**压缩"*.gz"文件
GzipCompressorInputStream：解压"*.gz"文件
BZip2CompressorOutputStream：压缩"*.bz2"文件
BZip2CompressorInputStream：解压"*.bz2"文件
XZCompressorOutputStream：压缩"*.xz"文件
XZCompressorInputStream：解压"*.xz"文件
FramedLZ4CompressorOutputStream：压缩"*.lz4"文件
FramedLZ4CompressorInputStream：解压"*.lz4"文件
BlockLZ4CompressorOutputStream：压缩"*.block_lz4"文件
BlockLZ4CompressorInputStream：解压"*.block_lz4"文件
Pack200CompressorOutputStream：压缩"*.pack"文件
Pack200CompressorInputStream：解压"*.pack"文件
DeflateCompressorOutputStream：压缩"*.deflate"文件
DeflateCompressorInputStream：解压"*.deflate"文件
LZMACompressorOutputStream：压缩"*.lzma"文件
LZMACompressorInputStream：解压"*.lzma"文件
FramedSnappyCompressorOutputStream：压缩"*.sz"文件
FramedSnappyCompressorInputStream：解压"*.sz"文件
ZCompressorInputStream：解压"*.Z"文件
下面简单看看例子
1. gzip
gzip是Unix，Linux上常用的压缩工具，也是当今的WEB站点上非常流行的压缩技术。其有压缩级别等概念，可以通过GzipParameters去设置。JDK8也自带了GZIPInputStream类，用法类似。

// gzip压缩
String file = "/test.js";
GzipParameters parameters = new GzipParameters();
parameters.setCompressionLevel(Deflater.BEST_COMPRESSION);
parameters.setOperatingSystem(3);
parameters.setFilename(FilenameUtils.getName(file));
parameters.setComment("Test file");
parameters.setModificationTime(System.currentTimeMillis());
FileOutputStream fos = new FileOutputStream(file + ".gz");
try (GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(fos, parameters);
    InputStream is = new FileInputStream(file)) {
    IOUtils.copy(is, gzos);
}

// gzip解压
String gzFile = "/test.js.gz";
FileInputStream is = new FileInputStream(gzFile);
try (GzipCompressorInputStream gis = new GzipCompressorInputStream(is)) {
    GzipParameters p = gis.getMetaData();
    File targetFile = new File("/test.js");
    FileUtils.copyToFile(gis, targetFile);
    targetFile.setLastModified(p.getModificationTime());
}

2. bz2
bz2是Linux下常见的压缩文件格式，是由具有高压缩率的压缩工具bzip2生成，以后缀为.bz2结尾的压缩文件。
Java 代码解读复制代码// 压缩bz2
String srcFile = "/test.tar";
String targetFile = "/test.tar.bz2";
FileOutputStream os = new FileOutputStream(targetFile);
try (BZip2CompressorOutputStream bzos = new BZip2CompressorOutputStream(os);
    InputStream is = new FileInputStream(srcFile)) {
    IOUtils.copy(is, bzos);
}

Java 代码解读复制代码// 解压bz2
String bzFile = "/test.tar.bz2";
FileInputStream is = new FileInputStream(bzFile);
try (BZip2CompressorInputStream bzis = new BZip2CompressorInputStream(is)) {
    File targetFile = new File("test.tar");
    FileUtils.copyToFile(bzis, targetFile);
}

其他压缩算法的使用方式和bz2基本一致，这里就不做代码示例了。

02. 归档
归档：将许多零散的文件整理为一个文件，文件总大小基本不变
解包：从归档文件中释放文件
Compress自带了很多归档相关的类，主要以下几个
TarArchiveOutputStream：归档"*.tar"文件
TarArchiveInputStream：解包"*.tar"文件
ZipArchiveOutputStream：归档压缩"*.zip"文件
ZipArchiveInputStream：解包解压"*.zip"文件
JarArchiveOutputStream：归档压缩"*.jar"文件
JarArchiveInputStream：解包解压"*.jar"文件
DumpArchiveOutputStream：归档"*.dump"文件
DumpArchiveInputStream：解包"*.dump"文件
CpioArchiveOutputStream：归档压缩"*.cpio"文件
CpioArchiveInputStream：解包解压"*.cpio"文件
ArArchiveOutputStream：归档压缩"*.ar"文件
ArArchiveInputStream：解包解压"*.ar"文件
ArjArchiveInputStream：解包解压"*.arj"文件
SevenZOutputFile：归档压缩"*.7z"文件
SevenZFile：解包解压"*.7z"文件
其中zip，jar，cpio，ar，7z既支持归档也支持压缩，能在归档的过程中做压缩处理。
由于他们会处理一个个零散的文件，所以会有ArchiveEntry的概念，即一个ArchiveEntry代表归档包内的一个目录或文件，下面简单看看例子
1. tar
tar是Unix和Linux系统上的常用的压缩归档工具，可以将多个文件合并为一个文件，打包后的文件后缀亦为"tar"。
Java 代码解读复制代码// tar压缩
public void tar() throws IOException {
    File srcDir = new File("/test");
    String targetFile = "/test.tar";
    try (TarArchiveOutputStream tos = new TarArchiveOutputStream(
            new FileOutputStream(targetFile))) {
        tarRecursive(tos, srcDir, "");
    }
}
// 递归压缩目录下的文件和目录
private void tarRecursive(TarArchiveOutputStream tos, File srcFile, String basePath) throws IOException {
    if (srcFile.isDirectory()) {
        File[] files = srcFile.listFiles();
        String nextBasePath = basePath + srcFile.getName() + "/";
        if (ArrayUtils.isEmpty(files)) {
            // 空目录
            TarArchiveEntry entry = new TarArchiveEntry(srcFile, nextBasePath);
            tos.putArchiveEntry(entry);
            tos.closeArchiveEntry();
        } else {
            for (File file : files) {
                tarRecursive(tos, file, nextBasePath);
            }
        }
    } else {
        TarArchiveEntry entry = new TarArchiveEntry(srcFile, basePath + srcFile.getName());
        tos.putArchiveEntry(entry);
        FileUtils.copyFile(srcFile, tos);
        tos.closeArchiveEntry();
    }
}

Java 代码解读复制代码// tar解压
public void untar() throws IOException {
    InputStream is = new FileInputStream("/test.tar");
    String outPath = "/test";
    try (TarArchiveInputStream tis = new TarArchiveInputStream(is)) {
        TarArchiveEntry nextEntry;
        while ((nextEntry = tis.getNextTarEntry()) != null) {
            String name = nextEntry.getName();
            File file = new File(outPath, name);
            //如果是目录，创建目录
            if (nextEntry.isDirectory()) {
                file.mkdir();
            } else {
                //文件则写入具体的路径中
                FileUtils.copyToFile(tis, file);
                file.setLastModified(nextEntry.getLastModifiedDate().getTime());
            }
        }
    }
}

2. 7z
7z 是一种全新的压缩格式，它拥有极高的压缩比。
7z 格式的主要特征：


开放的结构


高压缩比


强大的 AES-256 加密


能够兼容任意压缩、转换、加密算法


最高支持 16000000000 GB 的文件压缩


以 Unicode 为标准的文件名


支持固实压缩


支持文件头压缩


Java 代码解读复制代码// 7z压缩
public void _7z() throws IOException {
    try (SevenZOutputFile outputFile = new SevenZOutputFile(new File("/test.7z"))) {
        File srcFile = new File("/test");
        _7zRecursive(outputFile, srcFile, "");
    }
}
// 递归压缩目录下的文件和目录
private void _7zRecursive(SevenZOutputFile _7zFile, File srcFile, String basePath) throws IOException {
    if (srcFile.isDirectory()) {
        File[] files = srcFile.listFiles();
        String nextBasePath = basePath + srcFile.getName() + "/";
        // 空目录
        if (ArrayUtils.isEmpty(files)) {
            SevenZArchiveEntry entry = _7zFile.createArchiveEntry(srcFile, nextBasePath);
            _7zFile.putArchiveEntry(entry);
            _7zFile.closeArchiveEntry();
        } else {
            for (File file : files) {
                _7zRecursive(_7zFile, file, nextBasePath);
            }
        }
    } else {
        SevenZArchiveEntry entry = _7zFile.createArchiveEntry(srcFile, basePath + srcFile.getName());
        _7zFile.putArchiveEntry(entry);
        byte[] bs = FileUtils.readFileToByteArray(srcFile);
        _7zFile.write(bs);
        _7zFile.closeArchiveEntry();
    }
}

Java 代码解读复制代码 // 7z解压
public void un7z() throws IOException {
    String outPath = "/test";
    try (SevenZFile archive = new SevenZFile(new File("test.7z"))) {
        SevenZArchiveEntry entry;
        while ((entry = archive.getNextEntry()) != null) {
            File file = new File(outPath, entry.getName());
            if (entry.isDirectory()) {
                file.mkdirs();
            }
            if (entry.hasStream()) {
                final byte [] buf = new byte [1024];
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (int len = 0; (len = archive.read(buf)) > 0;) {
                    baos.write(buf, 0, len);
                }
                FileUtils.writeByteArrayToFile(file, baos.toByteArray());
            }
        }
    }
}

3.  ar，arj，cpio，dump，zip，jar
这些压缩工具类的使用方式和tar基本类似，就不做示例了
03. 修改归档文件
有时候我们会有修改归档内文件的需求，比如添加、删除一个文件，修改其中的文件内容等，当然我们也可以全部解压出来改完后在压缩回去。这样除了代码量多一些外，归档文件大也会导致操作时间过长。那么有没有办法用代码去动态的修改归档文件里的内容呢？
org.apache.commons.compress.changes包下正好就提供了一些类用于动态的修改归档文件里的内容。下面看一个简单的例子
Java 代码解读复制代码String tarFile = "/test.tar";
InputStream is = new FileInputStream(tarFile);
// 替换后会覆盖原test.tar，如果是windows可能会由于文件被访问而覆盖报错
OutputStream os = new FileOutputStream(tarFile);
try (TarArchiveInputStream tais = new TarArchiveInputStream(is);
     TarArchiveOutputStream taos = new TarArchiveOutputStream(os)) {
    ChangeSet changes = new ChangeSet();
    // 删除"test.tar中"的"dir/1.txt"文件
    changes.delete("dir/1.txt");
    // 删除"test.tar"中的"t"目录
    changes.delete("t");
    // 添加文件，如果已存在则替换
    File addFile = new File("/a.txt");
    ArchiveEntry addEntry = taos.createArchiveEntry(addFile, addFile.getName());
    // add可传第三个参数：true: 已存在则替换(默认值)， false: 不替换
    changes.add(addEntry, new FileInputStream(addFile));
    // 执行修改
    ChangeSetPerformer performer = new ChangeSetPerformer(changes);
    ChangeSetResults result = performer.perform(tais, taos);
}

04. 其他
1. 简单工厂
commons-compress还提供了一些简单的工厂类用户动态的获取压缩流和归档流。
Java 代码解读复制代码// 使用factory动态获取归档流
ArchiveStreamFactory factory = new ArchiveStreamFactory();
String archiveName = ArchiveStreamFactory.TAR;
InputStream is = new FileInputStream("/in.tar");
OutputStream os = new FileOutputStream("/out.tar");
// 动态获取实现类，此时ais实际上是TarArchiveOutPutStream
ArchiveInputStream ais = factory.createArchiveInputStream(archiveName, is);
ArchiveOutputStream aos = factory.createArchiveOutputStream(archiveName, os);
// 其他业务操作

// ------------------------

// 使用factory动态获取压缩流
CompressorStreamFactory factory = new CompressorStreamFactory();
String compressName = CompressorStreamFactory.GZIP;
InputStream is = new FileInputStream("/in.gz");
OutputStream os = new FileOutputStream("/out.gz");
// 动态获取实现类，此时ais实际上是TarArchiveOutPutStream
CompressorInputStream cis = factory.createCompressorInputStream(compressName, is);
CompressorOutputStream cos = factory.createCompressorOutputStream(compressName, os);
// 其他业务操作

2. 同时解压解包
上面说了很多都是单一的操作，那么如果解压"test.tar.gz"这种归档和压缩于一体的文件呢？
其实很简单，我们不需要先解压在解包，可以一步同时完成解压和解包，只需要将对应的流包装一下即可（不得不感叹Java IO的装饰者模式设计真的很巧妙）。下面看代码示例
Java 代码解读复制代码// 解压 解包test.tar.gz文件
String outPath = "/test";
InputStream is = new FileInputStream("/test.tar.gz");
// 先解压，所以需要先用gzip流包装文件流
CompressorInputStream gis = new GzipCompressorInputStream(is);
// 在解包，用tar流包装gzip流
try (ArchiveInputStream tgis = new TarArchiveInputStream(gis)) {
    ArchiveEntry nextEntry;
    while ((nextEntry = tgis.getNextEntry()) != null) {
        String name = nextEntry.getName();
        File file = new File(outPath, name);
        // 如果是目录，创建目录
        if (nextEntry.isDirectory()) {
            file.mkdir();
        } else {
            // 文件则写入具体的路径中
            FileUtils.copyToFile(tgis, file);
            file.setLastModified(nextEntry.getLastModifiedDate().getTime());
        }
    }
}

05. 总结
除了以上介绍的工具类外，还有其他不是很常用的就不多做介绍了。感兴趣的可以自行翻阅源码研究。
后续章节我将继续给大家介绍commons中其他好用的工具类库，期待你的关注。

```

# Apache Commons IO
```text
Apache Commons IO是对java.io的扩展，主要是对Java中的bio封装了一些好用的工具类，nio涉及的较少，关于bio和nio问题我们后续再聊。
Commons IO目前最新版本是2.10.0，最低要求Java8以上。
以下为整体结构：
Java 代码解读复制代码org.apache.commons.ioorg.apache.commons.io.comparator
org.apache.commons.io.file
org.apache.commons.io.filefilter
org.apache.commons.io.function
org.apache.commons.io.inputorg.apache.commons.io.monitor
org.apache.commons.io.outputorg.apache.commons.io.serialization

file：文件
input：输入
output：输出
function：函数
comparator：比较器
filefilter：文件过滤器
monitor：监视器
serialization：序列化

下面只列举其中常用的加以说明，其余感兴趣的可以自行翻阅源码研究。
01.IOUtils
    IOUtils可以说是Commons IO中最常用的了，下面直接看例子。
1. 关闭流
Java 代码解读复制代码InputStream inputStream = new FileInputStream("test.txt");
OutputStream outputStream = new FileOutputStream("test.txt");
// 原生写法
if (inputStream != null) {
    try {
        inputStream.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
if (outputStream != null) {
    try {
        outputStream.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
// commons写法(可以传任意数量的流)
IOUtils.closeQuietly(inputStream, outputStream);

2. 读取流
Java 代码解读复制代码// ==== 输入流转换为byte数组 ====
// 原生写法
InputStream is = new FileInputStream("foo.txt");
byte[] buf = new byte[1024];
int len;
ByteArrayOutputStream out = new ByteArrayOutputStream();
while ((len = is.read(buf)) != -1) {
    out.write(buf, 0, len);
}
byte[] result = out.toByteArray();
// commons写法
byte[] result2 = IOUtils.toByteArray(is);

// ---------------------------------------

// ==== 输入流转换为字符串 ====
// 原生写法
InputStream is = new FileInputStream("foo.txt");
BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
StringBuilder sb = new StringBuilder();
String line;
while ((line = br.readLine()) != null) {
    sb.append(line);
}
String result = sb.toString();
// commons写法
String result2 = IOUtils.toString(is, "UTF-8");

// IOUtils.toString 还有很多重载方法，保证有你想要的
// 将reader转换为字符串
String toString(Reader reader, String charset) throws IOException;
// 将url转换为字符串，也就是可以直接将网络上的内容下载为字符串
String toString(URL url, String charset) throws IOException;

3. 其他
Java 代码解读复制代码// 按照行读取结果
InputStream is = new FileInputStream("test.txt");
List<String> lines = IOUtils.readLines(is, "UTF-8");

// 将行集合写入输出流
OutputStream os = new FileOutputStream("newTest.txt");
IOUtils.writeLines(lines, System.lineSeparator(), os, "UTF-8");

// 拷贝输入流到输出流
InputStream inputStream = new FileInputStream("src.txt");
OutputStream outputStream = new FileOutputStream("dest.txt");
IOUtils.copy(inputStream, outputStream);

02. 文件相关
    文件相关主要有FileUtils：文件工具类，FilenameUtils：文件名工具类，PathUtils：路径工具类（主要是操作JDK7新增的java.nio.file.Path类）
1. 文件读写
Java 代码解读复制代码File readFile = new File("test.txt");
// 读取文件
String str = FileUtils.readFileToString(readFile, "UTF-8");
// 读取文件为字节数组
byte[] bytes = FileUtils.readFileToByteArray(readFile);
// 按行读取文件
List<String> lines =  FileUtils.readLines(readFile, "UTF-8");

File writeFile = new File("out.txt");
// 将字符串写入文件
FileUtils.writeStringToFile(writeFile, "测试文本", "UTF-8");
// 将字节数组写入文件
FileUtils.writeByteArrayToFile(writeFile, bytes);
// 将字符串列表一行一行写入文件
FileUtils.writeLines(writeFile, lines, "UTF-8");

2. 移动和复制
Java 代码解读复制代码File srcFile = new File("src.txt");
File destFile = new File("dest.txt");
File srcDir = new File("/srcDir");
File destDir = new File("/destDir");
// 移动/拷贝文件
FileUtils.moveFile(srcFile, destFile);
FileUtils.copyFile(srcFile, destFile);
// 移动/拷贝文件到目录
FileUtils.moveFileToDirectory(srcFile, destDir, true);
FileUtils.copyFileToDirectory(srcFile, destDir);
// 移动/拷贝目录
FileUtils.moveDirectory(srcDir, destDir);
FileUtils.copyDirectory(srcDir, destDir);
// 拷贝网络资源到文件
FileUtils.copyURLToFile(new URL("http://xx"), destFile);
// 拷贝流到文件
FileUtils.copyInputStreamToFile(new FileInputStream("test.txt"), destFile);
// ... ...

3. 其他文件操作
Java 代码解读复制代码File file = new File("test.txt");
File dir = new File("/test");
// 删除文件
FileUtils.delete(file);
// 删除目录
FileUtils.deleteDirectory(dir);
// 文件大小，如果是目录则递归计算总大小
long s = FileUtils.sizeOf(file);
// 则递归计算目录总大小，参数不是目录会抛出异常
long sd = FileUtils.sizeOfDirectory(dir);
// 递归获取目录下的所有文件
Collection<File> files = FileUtils.listFiles(dir, null, true);
// 获取jvm中的io临时目录
FileUtils.getTempDirectory();
// ... ...

4. 文件名称相关
Java 代码解读复制代码// 获取名称，后缀等
String name = "/home/xxx/test.txt";
FilenameUtils.getName(name); // "test.txt"
FilenameUtils.getBaseName(name); // "test"
FilenameUtils.getExtension(name); // "txt"
FilenameUtils.getPath(name); // "/home/xxx/"

// 将相对路径转换为绝对路径
FilenameUtils.normalize("/foo/bar/.."); // "/foo"

5. JDK7的Path操作
Java 代码解读复制代码// path既可以表示目录也可以表示文件

// 获取当前路径
Path path = PathUtils.current();
// 删除path
PathUtils.delete(path);
// 路径或文件是否为空
PathUtils.isEmpty(path);
// 设置只读
PathUtils.setReadOnly(path, true);
// 复制
PathUtils.copyFileToDirectory(Paths.get("test.txt"), path);
PathUtils.copyDirectory(Paths.get("/srcPath"), Paths.get("/destPath"));
// 统计目录内文件数量
Counters.PathCounters counters = PathUtils.countDirectory(path);
counters.getByteCounter(); // 字节大小
counters.getDirectoryCounter(); // 目录个数
counters.getFileCounter(); // 文件个数
// ... ...

03. 流相关
org.apache.commons.io.input和org.apache.commons.io.output包下有许多好用的过滤流，下面列举几个做下说明
1. 自动关闭的输入流 AutoCloseInputStream
Java 代码解读复制代码/**
 * AutoCloseInputStream是一个过滤流，用来包装其他流，读取完后流会自动关掉
 * 实现原理很简单，当读取完后将底层的流关闭，然后创建一个ClosedInputStream赋值给它包装的输入流。
 * 注：如果输入流没有全部读取是不会关掉底层流的
 */
public void autoCloseDemo() throws Exception {
    InputStream is = new FileInputStream("test.txt");
    AutoCloseInputStream acis = new AutoCloseInputStream(is);
    IOUtils.toByteArray(acis); // 将流全部读完
    // 可以省略关闭流的逻辑了
}

2. 倒序文件读取 ReversedLinesFileReader 
Java 代码解读复制代码// 从后向前按行读取
try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File("test.txt"), Charset.forName("UTF-8"))) {
    String lastLine = reader.readLine(); // 读取最后一行
    List<String> line5 = reader.readLines(5); // 从后再读5行
}

3. 带计数功能的流 CountingInputStream，CountingOutputStream
Java 代码解读复制代码/**
 * 大家都知道只给一个输入流咱们是没办法准确的知道它的大小的，虽说流提供了available()方法
 * 但是这个方法只有在ByteArrayInputStream的情况下拿到的是准确的大小，其他如文件流网络流等都是不准确的
 * （当然用野路子也可以实现，比如写入临时文件通过File.length()方法获取，然后在将文件转换为文件流）
 * 下面这个流可以实现计数功能，当把文件读完大小也就计算出来了
 */
public void countingDemo() {
    InputStream is = new FileInputStream("test.txt");
    try (CountingInputStream cis = new CountingInputStream(is)) {
        String txt = IOUtils.toString(cis, "UTF-8"); // 文件内容
        long size = cis.getByteCount(); // 读取的字节数
    } catch (IOException e) {
        // 异常处理
    }
}

4. 可观察的输入流  ObservableInputStream    
    可观察的输入流（典型的观察者模式），可实现边读取边处理
    比如将某些字节替换为另一个字节，计算md5摘要等
    当然你也可以完全写到文件后在做处理，这样相当于做了两次遍历，性能较差。
    这是一个基类，使用时需要继承它来扩展自己的流，示例如下：
Java 代码解读复制代码private class MyObservableInputStream extends ObservableInputStream {
    class MyObserver extends Observer {
        @Override
        public void data(final int input) throws IOException {
            // 做自定义处理
        }
        @Override
        public void data(final byte[] input, final int offset, final int length) throws IOException {
            // 做自定义处理
        }
    }
    public MyObservableInputStream(InputStream inputStream) {
        super(inputStream);
    }
}

5. 其他


BOMInputStream: 同时读取文本文件的bom头


BoundedInputStream：有界的流，控制只允许读取前x个字节


BrokenInputStream: 一个错误流，永远抛出IOException


CharSequenceInputStream: 支持StringBuilder,StringBuffer等读取


LockableFileWriter: 带锁的Writer，同一个文件同时只允许一个流写入，多余的写入操作会跑出IOException


StringBuilderWriter: StringBuilder的Writer


... ...


04. 文件比较器
    org.apache.commons.io.compare包有很多现成的文件比较器，可以对文件排序的时候直接拿来用。
DefaultFileComparator：默认文件比较器，直接使用File的compare方法。（文件集合排序（  Collections.sort()  ）时传此比较器和不传效果一样）
DirectoryFileComparator：目录排在文件之前
ExtensionFileComparator：扩展名比较器，按照文件的扩展名的ascii顺序排序，无扩展名的始终排在前面
LastModifiedFileComparator：按照文件的最后修改时间排序
NameFileComparator：按照文件名称排序
PathFileComparator：按照路径排序，父目录优先排在前面
SizeFileComparator：按照文件大小排序，小文件排在前面（目录会计算其总大小）
CompositeFileComparator：组合排序，将以上排序规则组合在一起
使用示例如下：
Java 代码解读复制代码List<File> files = Arrays.asList(new File[]{
        new File("/foo/def"),
        new File("/foo/test.txt"),
        new File("/foo/abc"),
        new File("/foo/hh.txt")});
// 排序目录在前
Collections.sort(files, DirectoryFileComparator.DIRECTORY_COMPARATOR); // ["/foo/def", "/foo/abc", "/foo/test.txt", "/foo/hh.txt"]
// 排序目录在后
Collections.sort(files, DirectoryFileComparator.DIRECTORY_REVERSE); // ["/foo/test.txt", "/foo/hh.txt", "/foo/def", "/foo/abc"]
// 组合排序，首先按目录在前排序，其次再按照名称排序
Comparator dirAndNameComp = new CompositeFileComparator(
            DirectoryFileComparator.DIRECTORY_COMPARATOR,
            NameFileComparator.NAME_COMPARATOR);
Collections.sort(files, dirAndNameComp); // ["/foo/abc", "/foo/def", "/foo/hh.txt", "/foo/test.txt"]

05. 文件监视器
org.apache.commons.io.monitor包主要提供对文件的创建、修改、删除的监听操作，下面直接看简单示例。
Java 代码解读复制代码public static void main(String[] args) throws Exception {
    // 监听目录下文件变化。可通过参数控制监听某些文件，默认监听目录所有文件
    FileAlterationObserver observer = new FileAlterationObserver("/foo");
    observer.addListener(new myListener());
    FileAlterationMonitor monitor = new FileAlterationMonitor();
    monitor.addObserver(observer);
    monitor.start(); // 启动监视器
    Thread.currentThread().join(); // 避免主线程退出造成监视器退出
}

private class myListener extends FileAlterationListenerAdaptor {
    @Override
    public void onFileCreate(File file) {
        System.out.println("fileCreated:" + file.getAbsolutePath());
    }
    @Override
    public void onFileChange(File file) {
        System.out.println("fileChanged:" + file.getAbsolutePath());
    }
    @Override
    public void onFileDelete(File file) {
        System.out.println("fileDeleted:" + file.getAbsolutePath());
    }
}

06. 总结
    除了以上介绍的工具类外，还有其他不是很常用的就不多做介绍了。感兴趣的可以自行翻阅源码研究。
    后续章节我将继续给大家介绍commons中其他好用的工具类库，期待你的关注。


```

# Apache Commons Codec
```text
Apache Commons Codec提供了许多编解码相关的工具类。Codec目前最新版本是1.15，最低要求Java7以上。
maven坐标如下：
xml 代码解读复制代码<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.15</version>
</dependency>

以下为整体结构：

language：语言
cli：命令行
digest：摘要
net：网络
binary：二进制

下面只列举其中常用的加以说明，其余感兴趣的可以自行翻阅源码研究。

01. 二进制相关
    二进制包主要提供16进制、Base64、Base32等的编解码工具类。
1. 16进制（Hex类）
    十六进制常用于将二进制以更简短的方式展示，比如MD5是128位，展现起来太长，而转换为16进制后只需要32个字符即可表示出来。示例代码如下
Java 代码解读复制代码// byte数组转为16进制字符串
String hex = Hex.encodeHexString("123".getBytes());
System.out.println(hex);
// 16进制字符串解码
byte[] src = Hex.decodeHex(hex);
System.out.println(new String(src));

2. Base64，Base32，Base16
    Base64是网络上最常见的用于传输二进制数据的编码方式之一，Base64就是一种基于64个可打印字符来表示二进制数据的方法。Base32就是使用32个可打印字符，Base16就是使用16个（实际上相当于16进制）。

























名称编码表字符串位数不足是否会补全base16数字0~9和字母A~F不会，位数刚好是 4 的倍数base32大写字母A~Z和数字2~7会base64Base大写字母A-Z，小写字母a-z，数字0~9以及"+"，"/"会
Java 代码解读复制代码// base64编码
String base64 = Base64.encodeBase64String("测试".getBytes());
System.out.println(base64);
// base64解码
byte[] src = Base64.decodeBase64(base64);
System.out.println(new String(src));
// 字符串是否是base64
Base64.isBase64(base64);

// Base32 Base16 同理

Codec还提供了Base系列的流处理，以流的方式去处理Base编解码，示例如下
Java 代码解读复制代码// 以流方式提供Base64编码和解码
// 附："123"的base64编码为"MTIz"

// 对输入流做base64编码
InputStream is = new ByteArrayInputStream("123".getBytes());
Base64InputStream ebis = new Base64InputStream(is, true);
String enc = IOUtils.toString(ebis, "UTF-8"); // MTIz

// 对base64数据流做解码
is = new ByteArrayInputStream(enc.getBytes());
Base64InputStream dbis = new Base64InputStream(is, false);
String dec = IOUtils.toString(dbis, "UTF-8"); // 123

// -----------------------

// 将数据做base64编码写入输出流
final String data = "123";
ByteArrayOutputStream baos = new ByteArrayOutputStream();
Base64OutputStream ebos = new Base64OutputStream(baos, true);
IOUtils.write(data, ebos, "UTF-8");
String enc2 = baos.toString(); // MTIz

// 将base64数据做解码写入输出流
baos = new ByteArrayOutputStream();
Base64OutputStream dbos = new Base64OutputStream(baos, false);
IOUtils.write(data, dbos, "UTF-8");
String dec2 = dbos.toString(); // 123

02. URL相关
URL之所以要进行编码，是因为URL中有些字符会引起歧义。
例如URL参数字符串中使用key=value键值对这样的形式来传参，键值对之间以&符号分隔，如/s?q=abc&ie=utf-8。如果你的value字符串中包含了=或者&，那么势必会造成接收URL的服务器解析错误，因此必须将引起歧义的&和=符号进行转义，也就是对其进行编码。
又如URL的编码格式采用的是ASCII码，而不是Unicode，这也就是说你不能在URL中包含任何非ASCII字符，例如中文。否则如果客户端浏览器和服务端浏览器支持的字符集不同的情况下，中文可能会造成问题。
URL编码的原则就是使用安全的字符（没有特殊用途或者特殊意义的可打印字符）去表示那些不安全的字符。
    编解码示例代码如下
Java 代码解读复制代码URLCodec urlCodec = new URLCodec();
// url编码
String encUrl = urlCodec.encode("http://x.com?f=哈");
System.out.println(encUrl);
// url解码
String decUrl = urlCodec.decode(encUrl);
System.out.println(decUrl);

03. 摘要算法
摘要算法是一种单向的散列算法，它满足以下几个特点。


输入长度是任意的


输出长度是固定的


对每一个给定的输入，计算输出是很容易的


不可逆，无法通过输出推算出原数据


输出不依赖于输入。就是输入数据变动一个字节结果会相差很多


    由于摘要算法以上特点，主要用于数据完整性校验。例如网上的资源一般会提供一个摘要值（一般用MD5算法），用户下载后可以通过工具对资源做MD5后和网上给定的值比较，如果不一致说明文件不完整了，可能是下载过程网络波动内容有丢失，也可能被人篡改过。
    也可以做数据的指纹，比如网盘秒传，就是利用摘要值做判断。客户端上传前先对文件做摘要值，传给服务端，服务端发现有相同摘要的文件说明两个文件内容是一致的，这样就无需上传直接将文件存储路径指向这个文件就可以了，既实现了秒传，还节约了服务器磁盘空间（不同用户相同内容的文件实际上指向的是同一份文件）。
    很多系统也将密码做md5后存储，其中这种方式并不安全。md5已经很很多公开结果了，并且使用彩虹表碰撞也很容易破解了。所以并不建议使用md5存储密码。密码推荐使用BCrypt算法。
    摘要算法主要有以下几个


MD(Message Digest)：消息摘要


SHA(Secure Hash Algorithm)：安全散列


MAC(Message Authentication Code)：消息认证码


1. MD系列
主要有MD2、MD4、MD5，目前一般常用MD5
Java 代码解读复制代码// 如果使用Java自带的api需要十多行才能实现md5算法

// 对数据做md5，参数支持字符串，字节数据，输入流
String md5 = DigestUtils.md5Hex("测试");

2. SHA系列
    SHA系列有SHA-1、SHA-224、SHA-256、SHA-384、SHA-512，SHA3-224、SHA3-256、SHA3-384、SHA3-512等。目前安全起见一般选择256以上，推荐384以上。当然摘要越长则计算耗时也越长，需要根据需求权衡。
Java 代码解读复制代码// 参数支持字符串，字节数据，输入流
String sha1 = DigestUtils.sha1Hex("测试");
String sha256 = DigestUtils.sha256Hex("测试");
String sha384 = DigestUtils.sha384Hex("测试");
String sha512 = DigestUtils.sha512Hex("测试");
String sha3_256 = DigestUtils.sha3_256Hex("测试");
String sha3_384 = DigestUtils.sha3_384Hex("测试");
String sha3_512 = DigestUtils.sha3_512Hex("测试");

3. HMAC系列
HMAC(keyed-Hash Message Authentication Code)系列是包含密钥的散列算法，包含了MD和SHA两个系列的消息摘要算法。融合了MD，SHA：
MD系列：HMacMD2，HMacMD4，HMacMD5
SHA系列：HMacSHA1，HMacSHA224，HMacSHA256，HMacSHA38
，HMacSHA512
Java 代码解读复制代码String key = "asdf3234asdf3234asdf3234asdf3234";
String valueToDigest = "测试数据"; // valueToDigest参数支持字节数据，流，文件等
// 做HMAC-MD5摘要
String hmacMd5 = new HmacUtils(HmacAlgorithms.HMAC_MD5, key).hmacHex(valueToDigest);
// 做HMAC-sha摘要
String hmacSha256 = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmacHex(valueToDigest);
String hmacSha384 = new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key).hmacHex(valueToDigest);
String hmacSha512 = new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key).hmacHex(valueToDigest);

04. 命令行
codec包还提供了一个命令行做摘要算法的入口。
cmd 代码解读复制代码java -cp ./commons-codec-1.15.jar org.apache.commons.codec.cli.Digest MD5 123

05. 总结
    除了以上介绍的工具类外，还有其他不是很常用的就不多做介绍了。感兴趣的可以自行翻阅源码研究。
后续章节我将继续给大家介绍commons中其他好用的工具类库，期待你的关注。

```

# Apache Commons简介
```text
Apache Commons是Apache软件基金会的项目。Commons的目的是提供可重用的、开源的Java代码。
Apache Commons提供了很多工具类库，他们几乎不依赖其他第三方的类库，接口稳定，集成简单，可以大大提高编码效率和代码质量。
以下部分组件节选自官方，详情请参见官网：commons.apache.org/

















































































































































标题BCEL字节码工程库——分析、创建和操作 Java 类字节码工程库——分析、创建和操作 Java 类文文件BeanUtils围绕 Java 反射和内省 API 的易于使用的包装器。CLI命令行参数解析器。Codec通用编码/解码算法（例如语音、base64、URL）。Collections扩展或增强 Java 集合框架。Compress定义用于处理 tar、zip 和 bzip2 文件的 API。Configuration读取各种格式的配置/首选项文件。Crypto使用 AES-NI 包装 Openssl 或 JCE  算法实现优化的加密库。CSV用于读写逗号分隔值文件的组件。Daemonunix-daemon-like java 代码的替代调用机制。DBCP数据库连接池服务。DbUtilsJDBC 帮助程序库。Email用于从 Java 发送电子邮件的库。Exec用于处理 Java 中外部进程执行和环境管理的 API。FileUpload您的 servlet 和 Web 应用程序的文件上传功能。Geometry空间和坐标。Imaging纯 Java 图像库。IOI/O 实用程序的集合。JCIJava 编译器接口JCSJava缓存系统Jelly基于 XML 的脚本和处理引擎。Jexl表达式语言，它扩展了 JSTL 的表达式语言。Lang为 java.lang 中的类提供额外的功能。Logging包装各种日志 API 实现。Math轻量级、自包含的数学和统计组件。Net网络实用程序和协议实现的集合。Numbers数字类型（复数、四元数、分数）和实用程序（数组、组合）。Pool通用对象池组件。RDF可由 JVM 上的系统实现的 RDF 1.1 的通用实现。RNG随机数生成器的实现。TextApache Commons Text  是一个专注于处理字符串的算法的库。Validator在 xml 文件中定义验证器和验证规则的框架。VFS用于将文件、FTP、SMB、ZIP  等视为单个逻辑文件系统的虚拟文件系统组件。Weaver提供一种简单的方法来增强（编织）已编译的字节码。
后续章节我将给大家介绍其中常用的类库，期待你的关注。  


```

# Apache Commons Lang
```text
Apache Commons Lang是对java.lang的扩展，基本上是commons中最常用的工具包。
目前Lang包有两个commons-lang3和commons-lang。
lang最新版本是2.6，最低要求Java1.2以上，目前官方已不在维护。lang3目前最新版本是3.12.0，最低要求Java8以上。相对于lang来说完全支持Java8的特性，废除了一些旧的API。该版本无法兼容旧有版本，于是为了避免冲突改名为lang3。
Java8以上的用户推荐使用lang3代替lang，下面我们主要以lang3 - 3.12.0版本为例做说明。
以下为整体包结构：
arduino 代码解读复制代码org.apache.commons.lang3
org.apache.commons.lang3.builder
org.apache.commons.lang3.concurrent
org.apache.commons.lang3.event
org.apache.commons.lang3.exception
org.apache.commons.lang3.math
org.apache.commons.lang3.mutable
org.apache.commons.lang3.reflect
org.apache.commons.lang3.text
org.apache.commons.lang3.text.translate
org.apache.commons.lang3.time
org.apache.commons.lang3.tuple


下面只列举其中常用的加以说明，其余感兴趣的可以自行翻阅源码研究。
01. 日期相关
    在Java8之前，日期只提供了java.util.Date类和java.util.Calendar类，说实话这些API并不是很好用，而且也存在线程安全的问题，所以Java8推出了新的日期API。如果你还在用旧的日期API，可以使用DateUtils和DateFormatUtils工具类。
1. 字符串转日期
Java 代码解读复制代码final String strDate = "2021-07-04 11:11:11";
final String pattern = "yyyy-MM-dd HH:mm:ss";
// 原生写法
SimpleDateFormat sdf = new SimpleDateFormat(pattern);
Date date1 = sdf.parse(strDate);
// commons写法
Date date2 = DateUtils.parseDate(strDate, pattern);

2. 日期转字符串
Java 代码解读复制代码final Date date = new Date();
final String pattern = "yyyy年MM月dd日";
// 原生写法
SimpleDateFormat sdf = new SimpleDateFormat(pattern);
String strDate = sdf.format(date);
// 使用commons写法
String strDate = DateFormatUtils.format(date, pattern);

3. 日期计算
Java 代码解读复制代码final Date date = new Date();
// 原生写法
Calendar cal = Calendar.getInstance();
cal.setTime(date);
cal.add(Calendar.DATE, 5); // 加5天
cal.add(Calendar.HOUR_OF_DAY, -5); // 减5小时
// 使用commons写法
Date newDate1 = DateUtils.addDays(date, 5); // 加5天
Date newDate2 = DateUtils.addHours(date, -5); // 减5小时
Date newDate3 = DateUtils.truncate(date, Calendar.DATE); // 过滤时分秒
boolean isSameDay = DateUtils.isSameDay(newDate1, newDate2); // 判断是否是同一天

02. 字符串相关
    字符串是Java中最常用的类型，相关的工具类也可以说是最常用的，下面直接看例子
1. 字符串判空
Java 代码解读复制代码String str = "";
// 原生写法
if (str == null || str.length() == 0) {
    // Do something
}
// commons写法
if (StringUtils.isEmpty(str)) {
    // Do something
}  
/* StringUtils.isEmpty(null)      = true
 * StringUtils.isEmpty("")        = true
 * StringUtils.isEmpty(" ")       = false
 * StringUtils.isEmpty("bob")     = false
 * StringUtils.isEmpty("  bob  ") = false
 */

相关方法：
Java 代码解读复制代码// isEmpty取反
StringUtils.isNotEmpty(str);
/* 
 * null, 空串，空格为true
 * StringUtils.isBlank(null)      = true
 * StringUtils.isBlank("")        = true
 * StringUtils.isBlank(" ")       = true
 * StringUtils.isBlank("bob")     = false
 * StringUtils.isBlank("  bob  ") = false
 */
StringUtils.isBlank(str);
// isBlank取反
StringUtils.isNotBlank(str);
// 任意一个参数为空则结果为true
StringUtils.isAnyEmpty(str1, str2, str3);
// 所有参数为空则结果为true
StringUtils.isAllEmpty(str1, str2, str3);

2. 字符串去空格
Java 代码解读复制代码// 去除两端空格，不需要判断null
String newStr = StringUtils.trim(str);
/*
 * 去除两端空格，如果是null则转换为空字符串
 * StringUtils.trimToEmpty(null)          = ""
 * StringUtils.trimToEmpty("")            = ""
 * StringUtils.trimToEmpty("     ")       = ""
 * StringUtils.trimToEmpty("abc")         = "abc"
 * StringUtils.trimToEmpty("    abc    ") = "abc"
 */
newStr = StringUtils.trimToEmpty(str);
/*
 * 去除两端空格，如果结果是空串则转换为null
 * StringUtils.trimToNull(null)          = null
 * StringUtils.trimToNull("")            = null
 * StringUtils.trimToNull("     ")       = null
 * StringUtils.trimToNull("abc")         = "abc"
 * StringUtils.trimToNull("    abc    ") = "abc"
 */
newStr = StringUtils.trimToNull(str);
/*
 * 去两端 给定字符串中任意字符
 * StringUtils.strip(null, *)          = null
 * StringUtils.strip("", *)            = ""
 * StringUtils.strip("abc", null)      = "abc"
 * StringUtils.strip("  abc", null)    = "abc"
 * StringUtils.strip("abc  ", null)    = "abc"
 * StringUtils.strip(" abc ", null)    = "abc"
 * StringUtils.strip("  abcyx", "xyz") = "  abc"
 */
newStr = StringUtils.strip(str, "stripChars");
// 去左端 给定字符串中任意字符
newStr = StringUtils.stripStart(str, "stripChars");
// 去右端 给定字符串中任意字符
newStr = StringUtils.stripEnd(str, "stripChars");

3. 字符串分割
Java 代码解读复制代码/*
 * 按照空格分割字符串 结果为数组
 * StringUtils.split(null)       = null
 * StringUtils.split("")         = []
 * StringUtils.split("abc def")  = ["abc", "def"]
 * StringUtils.split("abc  def") = ["abc", "def"]
 * tringUtils.split(" abc ")    = ["abc"]
 */
 StringUtils.split(str);
 // 按照某些字符分割 结果为数组，自动去除了截取后的空字符串
 StringUtils.split(str, ",");

4. 取子字符串
Java 代码解读复制代码// 获得"ab.cc.txt"中最后一个.之前的字符串
StringUtils.substringBeforeLast("ab.cc.txt", "."); // ab.cc
// 相似方法
// 获得"ab.cc.txt"中最后一个.之后的字符串（常用于获取文件后缀名）
StringUtils.substringAfterLast("ab.cc.txt", "."); // txt
// 获得"ab.cc.txt"中第一个.之前的字符串
StringUtils.substringBefore("ab.cc.txt", "."); // ab
// 获得"ab.cc.txt"中第一个.之后的字符串
StringUtils.substringAfter("ab.cc.txt", "."); // cc.txt
// 获取"ab.cc.txt"中.之间的字符串
StringUtils.substringBetween("ab.cc.txt", "."); // cc
// 看名字和参数应该就知道干什么的了
StringUtils.substringBetween("a(bb)c", "(", ")"); // bb

5.  其他
Java 代码解读复制代码// 首字母大写
StringUtils.capitalize("test"); // Test
// 字符串合并
StringUtils.join(new int[]{1,2,3}, ",");// 1,2,3
// 缩写
StringUtils.abbreviate("abcdefg", 6);// "abc..."
// 判断字符串是否是数字
StringUtils.isNumeric("abc123");// false
// 删除指定字符
StringUtils.remove("abbc", "b"); // ac
// ... ... 还有很多，感兴趣可以自己研究

6. 随机字符串
Java 代码解读复制代码// 随机生成长度为5的字符串
RandomStringUtils.random(5);
// 随机生成长度为5的"只含大小写字母"字符串
RandomStringUtils.randomAlphabetic(5);
// 随机生成长度为5的"只含大小写字母和数字"字符串
RandomStringUtils.randomAlphanumeric(5);
// 随机生成长度为5的"只含数字"字符串
RandomStringUtils.randomNumeric(5);

03. 反射相关
反射是Java中非要重要的特性，原生的反射API代码冗长，Lang包中反射相关的工具类可以很方便的实现反向相关功能，下面看例子
1. 属性操作
Java 代码解读复制代码public class ReflectDemo {
    private static String sAbc = "111";
    private String abc = "123";
    public void fieldDemo() throws Exception {
        ReflectDemo reflectDemo = new ReflectDemo();
        // 反射获取对象实例属性的值
        // 原生写法
        Field abcField = reflectDemo.getClass().getDeclaredField("abc");
        abcField.setAccessible(true);// 设置访问级别，如果private属性不设置则访问会报错
        String value = (String) abcField.get(reflectDemo);// 123
        // commons写法
        String value2 = (String) FieldUtils.readDeclaredField(reflectDemo, "abc", true);//123
        // 方法名如果不含Declared会向父类上一直查找
    }
}

注：方法名含Declared的只会在当前类实例上寻找，不包含Declared的在当前类上找不到则会递归向父类上一直查找。
相关方法：
Java 代码解读复制代码public class ReflectDemo {
    private static String sAbc = "111";
    private String abc = "123";
    public void fieldRelated() throws Exception {
        ReflectDemo reflectDemo = new ReflectDemo();
        // 反射获取对象属性的值
        String value2 = (String) FieldUtils.readField(reflectDemo, "abc", true);//123
        // 反射获取类静态属性的值
        String value3 = (String) FieldUtils.readStaticField(ReflectDemo.class, "sAbc", true);//111
        // 反射设置对象属性值
        FieldUtils.writeField(reflectDemo, "abc", "newValue", true);
        // 反射设置类静态属性的值
        FieldUtils.writeStaticField(ReflectDemo.class, "sAbc", "newStaticValue", true);
    }
}

2. 获取注解方法
Java 代码解读复制代码// 获取被Test注解标识的方法
        
// 原生写法
List<Method> annotatedMethods = new ArrayList<Method>();
for (Method method : ReflectDemo.class.getMethods()) {
    if (method.getAnnotation(Test.class) != null) {
        annotatedMethods.add(method);
    }
}
// commons写法
Method[] methods = MethodUtils.getMethodsWithAnnotation(ReflectDemo.class, Test.class);

3. 方法调用
Java 代码解读复制代码private static void testStaticMethod(String param1) {}
private void testMethod(String param1) {}
  
public void invokeDemo() throws Exception {
    // 调用函数"testMethod"
    ReflectDemo reflectDemo = new ReflectDemo();
    // 原生写法
    Method testMethod = reflectDemo.getClass().getDeclaredMethod("testMethod");
    testMethod.setAccessible(true); // 设置访问级别，如果private函数不设置则调用会报错
    testMethod.invoke(reflectDemo, "testParam");
    // commons写法
    MethodUtils.invokeExactMethod(reflectDemo, "testMethod", "testParam");
    
    // ---------- 类似方法 ----------
    // 调用static方法
    MethodUtils.invokeExactStaticMethod(ReflectDemo.class, "testStaticMethod", "testParam");
    // 调用方法(含继承过来的方法)
    MethodUtils.invokeMethod(reflectDemo, "testMethod", "testParam");
    // 调用static方法(当前不存在则向父类寻找匹配的静态方法)
    MethodUtils.invokeStaticMethod(ReflectDemo.class, "testStaticMethod", "testParam");
}

    其他还有ClassUtils，ConstructorUtils，TypeUtils等不是很常用，有需求的可以现翻看类的源码。
04. 系统相关
主要是获取操作系统和JVM一些信息，下面看例子
Java 代码解读复制代码// 判断操作系统类型
boolean isWin = SystemUtils.IS_OS_WINDOWS;
boolean isWin10 = SystemUtils.IS_OS_WINDOWS_10;
boolean isWin2012 = SystemUtils.IS_OS_WINDOWS_2012;
boolean isMac = SystemUtils.IS_OS_MAC;
boolean isLinux = SystemUtils.IS_OS_LINUX;
boolean isUnix = SystemUtils.IS_OS_UNIX;
boolean isSolaris = SystemUtils.IS_OS_SOLARIS;
// ... ...

// 判断java版本
boolean isJava6 = SystemUtils.IS_JAVA_1_6;
boolean isJava8 = SystemUtils.IS_JAVA_1_8;
boolean isJava11 = SystemUtils.IS_JAVA_11;
boolean isJava14 = SystemUtils.IS_JAVA_14;
// ... ...

// 获取java相关目录
File javaHome = SystemUtils.getJavaHome();
File userHome = SystemUtils.getUserHome();// 操作系统用户目录
File userDir = SystemUtils.getUserDir();// 项目所在路径
File tmpDir = SystemUtils.getJavaIoTmpDir();

05. 总结
    除了以上介绍的工具类外，还有其他不是很常用的就不多做介绍了。感兴趣的可以自行翻阅源码研究。
后续章节我将继续给大家介绍commons中其他好用的工具类库，期待你的关注。

```

# Apache Commons Exec
```text
Apache Commons Exec主要用于执行外部进程的命令。Exec目前最新版本是1.3，最低要求Java5以上。
用Java执行外部进程命令也是比较常见的一种需求，这种操作依赖特定操作系统，需要我们了解特定系统的行为，例如在Windows上使用cmd.exe。想要可靠地执行外部进程还需要在执行命令之前或之后处理环境变量。
Apache Commons Exec就是为了处理上面概述的各种问题。而且代码实现起来也比较简单。
maven坐标如下：
xml 代码解读复制代码<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-exec</artifactId>
    <version>1.3</version>
</dependency>

下面简单介绍一下用法。
01. 同步调用
同步调用系统命令后会阻塞当前线程，直到获取到结果。
1. 使用JDK写法
Java 代码解读复制代码// 不使用工具类的写法
Process process = Runtime.getRuntime().exec("cmd /c ping 192.168.1.10");
int exitCode = process.waitFor(); // 阻塞等待完成
if (exitCode == 0) { // 状态码0表示执行成功
    String result = IOUtils.toString(process.getInputStream()); // "IOUtils" commons io中的工具类，详情可以参见前续文章介绍
    System.out.println(result);
} else {
    String errMsg = IOUtils.toString(process.getErrorStream());
    System.out.println(errMsg);
}

等等，这么写其实有坑。如果执行一个安装脚本会在控制台输出大量内容，这时可能会导致进程卡死（其实是一直阻塞状态）。
这是由于缓冲区满了，无法写入数据，导致线程阻塞，对外现象就是进程无法停止，也不占资源，什么反应也没有。
这种情况可以单独启动一个线程去读取输入流的内容，避免缓冲区占满，示例如下：
Java 代码解读复制代码final Process process = Runtime.getRuntime().exec("cmd /c ping 192.168.1.10");
new Thread(() -> {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = br.readLine()) != null) {
            try {
                process.exitValue();
                break; // exitValue没有异常表示进程执行完成，退出循环
            } catch (IllegalThreadStateException e) {
                // 异常代表进程没有执行完
            }
            //此处只做输出，对结果有其他需求可以在主线程使用其他容器收集此输出
            System.out.println(line);
        }
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}).start();
process.waitFor();

如果是异常信息打印过多则处理process.getErrorStream()。
2. 使用commons写法
commons-exec的command不需要考虑执行环境了，比如windows下不需要添加"cmd /c "的前缀。可以使用自定义的流来接受结果，比如使用文件流将结果保存到文件，使用网络流保存到远程服务器上等。下面的例子为了简单直接使用字节流去接收（如果结果非常大就不要用字节流了，容易内容溢出）。
Java 代码解读复制代码String command = "ping 192.168.1.10";
//接收正常结果流
ByteArrayOutputStream susStream = new ByteArrayOutputStream();
//接收异常结果流
ByteArrayOutputStream errStream = new ByteArrayOutputStream();
CommandLine commandLine = CommandLine.parse(command);
DefaultExecutor exec = new DefaultExecutor();
PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);
exec.setStreamHandler(streamHandler);
int code = exec.execute(commandLine);
System.out.println("result code: " + code);
// 不同操作系统注意编码，否则结果乱码
String suc = susStream.toString("GBK");
String err = errStream.toString("GBK");
System.out.println(suc);
System.out.println(err);

02. 异步调用
1. 使用JDK写法
JDK自带的Runtime的API不支持异步执行，如果要异步拿到执行结果需要自己单独创建线程不断轮询进程状态然后通知主线程，下面看一个例子。例子力求简单，所以很多细节不是很严谨，只看大体思路即可（如果要实现exec方便的API需要更多的代码来实现）。
Java 代码解读复制代码public class RuntimeAsyncDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("1. 开始执行");
        String cmd = "cmd /c ping 192.168.1.11"; // 假设是一个耗时的操作
        execAsync(cmd, processResult -> {
            System.out.println("3. 异步执行完成，success=" + processResult.success + "; msg=" + processResult.result);
            System.exit(0);
        });
        // 做其他操作 ... ...
        System.out.println("2. 做其他操作");
        // 避免主线程退出导致程序退出
        Thread.currentThread().join();
    }
    private static void execAsync(String command, Consumer<ProcessResult> callback) throws IOException {
        final Process process = Runtime.getRuntime().exec(command);
        new Thread(() -> {
            StringBuilder successMsg = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"))) {
                // 存放临时结果
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        successMsg.append(line).append("\r\n");
                        int exitCode = process.exitValue();
                        ProcessResult pr = new ProcessResult();
                        if (exitCode == 0) {
                            pr.success = true;
                            pr.result = successMsg.toString();
                        } else {
                            pr.success = false;
                            pr.result = IOUtils.toString(process.getErrorStream());
                        }
                        callback.accept(pr); // 回调主线程注册的函数
                        break; // exitValue没有异常表示进程执行完成，退出循环
                    } catch (IllegalThreadStateException e) {
                        // 异常代表进程没有执行完
                    }
                    try {
                        // 等待100毫秒在检查是否完成
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static class ProcessResult {
        boolean success;
        String result;
    }
}

2. 使用commons写法
commons-exec原生支持异步调用，下面直接看例子。
Java 代码解读复制代码@Test
public void execAsync() throws IOException, InterruptedException {
    String command = "ping 192.168.1.10";
    //接收正常结果流
    ByteArrayOutputStream susStream = new ByteArrayOutputStream();
    //接收异常结果流
    ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    CommandLine commandLine = CommandLine.parse(command);
    DefaultExecutor exec = new DefaultExecutor();

    PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);
    exec.setStreamHandler(streamHandler);
    ExecuteResultHandler erh = new ExecuteResultHandler() {
        @Override
        public void onProcessComplete(int exitValue) {
            try {
                String suc = susStream.toString("GBK");
                System.out.println(suc);
                System.out.println("3. 异步执行完成");
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }
        }
        @Override
        public void onProcessFailed(ExecuteException e) {
            try {
                String err = errStream.toString("GBK");
                System.out.println(err);
                System.out.println("3. 异步执行出错");
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            }
        }
    };
    System.out.println("1. 开始执行");
    exec.execute(commandLine, erh);
    System.out.println("2. 做其他操作");
    // 避免主线程退出导致程序退出
    Thread.currentThread().join();
}

03. 监控
commons-exec支持监控外部进程的执行状态并做一些操作，如超时，停止等。
在使用Runtime.getRuntime().exec(cmd)执行某些系统命令，如nfs共享的mount时，会由于nfs服务异常等原因导致进程阻塞，使程序没法往下执行，而且也无法捕获到异常，相当于卡死在了。这时如果有超时放弃的功能就好了，当然超时功能可以自己轮询process.exitValue()去实现，稍微麻烦一些，这里就不做示例了。
commons-exec主要通过ExecuteWatchdog类来处理超时，下面看例子
Java 代码解读复制代码String command = "ping 192.168.1.10";
ByteArrayOutputStream susStream = new ByteArrayOutputStream();
ByteArrayOutputStream errStream = new ByteArrayOutputStream();
CommandLine commandLine = CommandLine.parse(command);
DefaultExecutor exec = new DefaultExecutor();
//设置一分钟超时
ExecuteWatchdog watchdog = new ExecuteWatchdog(60*1000);
exec.setWatchdog(watchdog);
PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);
exec.setStreamHandler(streamHandler);
try {
    int code = exec.execute(commandLine);
    System.out.println("result code: " + code);
    // 不同操作系统注意编码，否则结果乱码
    String suc = susStream.toString("GBK");
    String err = errStream.toString("GBK");
    System.out.println(suc+err);
} catch (ExecuteException e) {
    if (watchdog.killedProcess()) {
        // 被watchdog故意杀死
        System.err.println("超时了");
    }
}

ExecuteWatchdog还支持销毁进程，只需调用destroyProcess()，由于ExecuteWatchdog是异步执行的，所以调用后不会马上停止。使用起来也比较简单就不做说明了。
04. 总结
commons-exec屏蔽了不同操作系统的命令差异，解决了Runtime缓冲区问题导致的线程卡死，同时支持超时和等，用来代替JDK的Runtime API是非常不错的选择。
后续章节我将继续给大家介绍commons中其他好用的工具类库，期待你的关注。

```


# Apache Commons Pool
```text
Apache Commons Pool 开源软件库提供了一个对象池 API 和许多对象池实现。
Pool 有两个版本，目前主流的是 Pool2，与 1.x 系列相比，Apache Commons Pool2 重新编写了对象池实现。除了性能和可伸缩性改进之外，版本2还包括健壮的实例跟踪和对象池监控。
后续文章出现的 Commons-Pool 指的就是 Pool2。
Commons-Net目前最新版本是2.9.0，最低要求Java8以上。
maven坐标如下：
XML 代码解读复制代码<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.9.0</version>
</dependency>

包结构如下：
 代码解读复制代码org.apache.commons.pool2org.apache.commons.pool2.implorg.apache.commons.pool2.proxy

下面简单介绍一下其用法。
01. 简介
为什么要有对象池呢，假如一个对象创建耗时 500 毫秒，而我们调用它的方法仅耗时 10 毫秒，这种情况每次使用都 new 的话性价比很低，相当于每次都要耗费 550 毫秒。 对象池就是为了解决此类问题而诞生的，对于这些昂贵的对象来说，提前创建若干个对象用对象池管理起来，用的时候从对象池借来一个，用完后归还 可以大大提升性能。
对象池是一种享元模式的实现，常用于各种连接池的实现。 比如我们常见的数据库连接池 DBCP，Redis 客户端 Jedis 等都依赖 Commons-Pool。
Commons-Pool 主要有三个角色：
PooledObject：池化对象，用于包装实际的对象，提供一些附件的功能。如 Commons-Pool 自带的 DefaultPooledObject 会记录对象的创建时间，借用时间，归还时间，对象状态等，PooledSoftReference 使用 Java 的软引用来持有对象，便于 JVM 内存不够时回收对象。当然我们也可以实现 PooledObject 接口来定义我们自己的对象包装器。
PooledObjectFactory：对象工厂，ObjectPool 对于每个对象的核心操作会代理给 PooledObjectFactory。
需要一个新实例时，就调用 makeObject 方法。
需要借用对象时会调用 activateObject 方法激活对象，并且根据配置情况决定是否验证对象有效性，通过 validateObject 方法验证。
归还对象时会调用 passivateObject 方法钝化对象。
需要销毁对象时候调用 destroyObject 方法。
PooledObjectFactory 必须是线程安全的。
ObjectPool ：对象池接口，用于管理池中的所有对象，对于每个对象的操作会代理给 ObjectFactory。ObjectPool 有多个实现，GenericObjectPool 提供了多种配置选项，包括限制空闲或活动实例的数量、在实例处于池中空闲时将其逐出等。从版本 2 开始，GenericObjectPool 还提供了废弃实例跟踪和删除功能。SoftReferenceObjectPool 可以根据需要增长，但允许垃圾收集器根据需要从池中逐出空闲实例。
以下是部分类图


02. 使用方式
Commons-Pool 用起来很简单，下面我用一个例子简单介绍下其用法
首先我们创建一个对象用于测试，对象构造函数使用随机延迟模拟创建的复杂
Java 代码解读复制代码/**
 * 复杂的对象，创建出来比较耗时间
 */
public class ComplexObject {

    private String name;

    public ComplexObject(String name) {
        try {
            long t1 = System.currentTimeMillis();
            // 模拟创建耗时操作
            ThreadLocalRandom tlr = ThreadLocalRandom.current();
            Thread.sleep(4000 + tlr.nextInt(2000));
            long t2 = System.currentTimeMillis();
            System.out.println(name + " 创建耗时: " + (t2-t1) + "ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

其次创建一个 ComplexPooledObjectFactory 实现。当我们有复杂的操作，比如激活对象，钝化对象，销毁对象（需要释放资源）等就需要实现 PooledObjectFactory 来定制，如果没有这些操作选择继承 BasePooledObjectFactory 抽象类更方便。下面分别给出两种创建的代码示例
1. 继承 BasePooledObjectFactory
Java 代码解读复制代码public class SimplePooledObjectFactory extends BasePooledObjectFactory<ComplexObject> {
    @Override
    public ComplexObject create() {
        // 随机指定一个名称，用于区分ComplexObject
        String name = "test" + ThreadLocalRandom.current().nextInt(100);
        return new ComplexObject(name);
    }
    @Override
    public PooledObject<ComplexObject> wrap(ComplexObject obj) {
        // 使用默认池化对象包装ComplexObject
        return new DefaultPooledObject(obj);
    }
}

2. 实现 PooledObjectFactory
Java 代码解读复制代码public class ComplexPooledObjectFactory implements PooledObjectFactory<ComplexObject> {

    @Override
    public PooledObject<ComplexObject> makeObject() {
        // 随机指定一个名称，用于区分ComplexObject并使用默认池化对象包装ComplexObject
        String name = "test" + ThreadLocalRandom.current().nextInt(100);
        return new DefaultPooledObject<>(new ComplexObject(name));
    }

    @Override
    public void destroyObject(PooledObject<ComplexObject> p) {
        // 销毁对象，当清空，空闲对象大于配置值等会销毁多余对象
        // 此处应释放掉对象占用的资源，如关闭连接，关闭IO等
    }

    @Override
    public boolean validateObject(PooledObject<ComplexObject> p) {
        // 验证对象状态是否正常，是否可用
        return true;
    }

    @Override
    public void activateObject(PooledObject<ComplexObject> p) {
        // 激活对象，使其可用
    }

    @Override
    public void passivateObject(PooledObject<ComplexObject> p) {
        // 钝化对象，使其不可用
    }
}

最后编写测试代码
Java 代码解读复制代码public static void main(String[] args) throws Exception {
    // 创建配置对象
    GenericObjectPoolConfig<ComplexObject> poolConfig = new GenericObjectPoolConfig<>();
    // 最大空闲实例数，空闲超过此值将会被销毁淘汰
    poolConfig.setMaxIdle(5);
    // 最大对象数量，包含借出去的和空闲的
    poolConfig.setMaxTotal(20);
    // 最小空闲实例数，对象池将至少保留2个空闲对象
    poolConfig.setMinIdle(2);
    // 对象池满了，是否阻塞获取（false则借不到直接抛异常）
    poolConfig.setBlockWhenExhausted(true);
    // BlockWhenExhausted为true时生效，对象池满了阻塞获取超时，不设置则阻塞获取不超时，也可在borrowObject方法传递第二个参数指定本次的超时时间
    poolConfig.setMaxWaitMillis(3000);
    // 创建对象后是否验证对象，调用objectFactory#validateObject
    poolConfig.setTestOnCreate(false);
    // 借用对象后是否验证对象 validateObject
    poolConfig.setTestOnBorrow(true);
    // 归还对象后是否验证对象 validateObject
    poolConfig.setTestOnReturn(true);
    // 每30秒定时检查淘汰多余的对象, 启用单独的线程处理
    poolConfig.setTimeBetweenEvictionRunsMillis(1000 * 60 * 30);
    // 每30秒定时检查期间是否验证对象 validateObject
    poolConfig.setTestWhileIdle(false);
    // jmx监控，和springboot自带的jmx冲突，可以选择关闭此配置或关闭springboot的jmx配置
    poolConfig.setJmxEnabled(false);

    ComplexPooledObjectFactory objectFactory = new ComplexPooledObjectFactory();
    GenericObjectPool<ComplexObject> objectPool = new GenericObjectPool<>(objectFactory, poolConfig);
    // 申请对象
    ComplexObject obj1 = objectPool.borrowObject();
    println("第一次申请对象：" + obj1.getName());
    // returnObject应该放在finally中 避免业务异常没有归还对象，demo仅做示例
    objectPool.returnObject(obj1);
    // 申请对象， 由于之前归还了，借用的还是之前的对象
    ComplexObject obj2 = objectPool.borrowObject();
    println("第二次申请对象：" + obj2.getName());
    // 再次申请对象，由于之前没有归还，借用的是新创建的
    ComplexObject obj3 = objectPool.borrowObject();
    println("第三次申请对象：" + obj3.getName());

    // returnObject应该放在finally中 避免业务异常没有归还对象，demo仅做示例
    objectPool.returnObject(obj2);
    objectPool.returnObject(obj3);
}

运行结果如下
 代码解读复制代码test41 创建耗时: 5400ms
第一次申请对象：test41
第二次申请对象：test41
test58 创建耗时: 5349ms
第三次申请对象：test58

当然如果借用次数越多，节省下来的时间就越多。
由于示例比较简单粗暴，在对象池刚刚创建还没提前创建好对象，我们就去使用了，所以效果不是很理想，正常使用效果会比较好。
03. KeyedObjectPool
Commons-Pool 还有 KeyedPooledObjectFactory，KeyedObjectPool 接口，它支持 Key Value 形式。
Java 代码解读复制代码public interface KeyedPooledObjectFactory<K, V> {
    // 通过参数创建对象
    PooledObject<V> makeObject(K key);
    // 通过参数激活对象，使其可用
    void activateObject(K key, PooledObject<V> obj);
    // 通过参数钝化对象，使其不可用
    void passivateObject(K key, PooledObject<V> obj);
    // 通过参数验证对象状态是否正常，是否可用
    boolean validateObject(K key, PooledObject<V> obj);
    // 通过参数销毁对象，当清空，空闲对象大于配置值等会销毁多余对象
    // 此处应释放掉对象占用的资源，如关闭连接，关闭IO等
    void destroyObject(K key, PooledObject<V> obj);
}

具体使用方式就不做介绍了，用法和第二节的类似，区别是对象借用和归还操作需要额外传递自定义的 Key 参数。
04. 总结
Commons-Pool 作为对象池工具包，支持对象的管理、跟踪和监控，并且支持自定义池化对象来扩展对象管理的行为，如果有相关需求可以使用。
后续章节我将继续给大家介绍 commons 中其他好用的工具类库，期待你的关注。

```