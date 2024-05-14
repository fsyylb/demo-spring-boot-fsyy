# java api获取yarn资源池各节点信息 原创
Java API获取YARN资源池各节点信息
在大数据环境中，YARN（Yet Another Resource Negotiator）是Apache Hadoop的核心组件之一，负责资源管理和作业调度。YARN提供了一种灵活的方式来管理集群中的资源，并使得各种应用能够在共享的资源上运行。

本文将介绍如何使用Java API来获取YARN资源池中各节点的信息。首先我们需要了解YARN中的几个重要概念：

ResourceManager：资源管理器，负责整个集群的资源管理和作业调度。
NodeManager：节点管理器，负责管理集群中的每个节点，监控节点的资源使用情况。
ApplicationMaster：应用程序管理器，每个YARN应用程序都有一个ApplicationMaster来管理该应用程序在集群中的资源。
使用Java API获取节点信息
YARN提供了一组Java API来获取集群中的资源和节点信息。我们可以通过ResourceManager和NodeManager提供的API来获取资源池中各节点的信息。

下面是一个使用Java API获取资源池各节点信息的示例代码：

```java
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.List;

public class YarnNodeInfo {
    public static void main(String[] args) {
        // 创建YarnClient实例
        YarnClient yarnClient = YarnClient.createYarnClient();
        // 初始化YarnClient配置
        yarnClient.init(new YarnConfiguration());
        // 启动YarnClient
        yarnClient.start();
        
        try {
            // 获取资源管理器的节点报告列表
            List<NodeReport> nodeReports = yarnClient.getNodeReports(NodeState.RUNNING);
            
            // 输出节点信息
            for (NodeReport nodeReport : nodeReports) {
                System.out.println("Node ID: " + nodeReport.getNodeId());
                System.out.println("Node Address: " + nodeReport.getHttpAddress());
                System.out.println("Node State: " + nodeReport.getNodeState());
                System.out.println("Node Total Resource: " + nodeReport.getTotalCapability());
                System.out.println("Node Used Resource: " + nodeReport.getUsed());
                // 其他节点信息...
                System.out.println("-----------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭YarnClient
            yarnClient.stop();
        }
    }
}
```
在以上示例代码中，我们使用YarnClient来创建一个YARN客户端实例，并初始化和启动该实例。通过调用yarnClient.getNodeReports(NodeState.RUNNING)方法，我们可以获取资源管理器的节点报告列表。然后，我们可以遍历节点报告列表，获取每个节点的详细信息，如节点ID、地址、状态、总资源和已使用资源等。

节点信息统计与可视化
除了获取节点信息，我们还可以对节点信息进行统计和可视化。下面是一个示例代码，用于统计各个节点的资源使用情况，并使用饼状图进行可视化展示：

```java
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YarnNodeStatistics {
    public static void main(String[] args) {
        // 创建YarnClient实例
        YarnClient yarnClient = YarnClient.createYarnClient();
        // 初始化YarnClient配置
        yarnClient.init(new YarnConfiguration());
        // 启动YarnClient
        yarnClient.start();
        
        try {
            // 获取资源管理器的节点报告列表
            List<NodeReport> nodeReports = yarnClient.getNodeReports(NodeState.RUNNING);
            
            // 节点资源使用统计
            Map<String, Integer> nodeStatistics = new HashMap<>();
            
            // 统计各个节点的资源使用情况
            for (NodeReport nodeReport : nodeReports) {
                Resource usedResource = nodeReport.getUsed();
                int usedMemory = usedResource.getMemory();
                String nodeName = nodeReport.getNodeId().getHost();
                
                nodeStatistics.put(nodeName, usedMemory);
            }
            
            // 输出节点资源使用统计
            for (Map.Entry<String, Integer> entry : nodeStatistics.entrySet()) {
```


# Hbase java API
阅读目录(Content)

一、Java查看zookeeper
二、Hbase Java api
三、Java API操作hbase
getadmin对表操作
getTable对表数据操作
四、HBase BulkLoading

一、Java查看zookeeper
1、使用idea下载zookeeper插件

2、启动集群

3、开始使用查看

```text
二、Hbase Java api
1、新建maven项目命名hbase

2、hbase项目pom文件导包

 <dependencies>
        <dependency>
            <groupId>org.apache.hbase</groupId>
            <artifactId>hbase-client</artifactId>
            <version>1.4.6</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.8.2</version>
        </dependency>
    </dependencies>

3、与hbase创建连接实现建表

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class APIDemo1 {
    public static void main(String[] args) throws IOException {
        //创建配置文件
        Configuration conf = HBaseConfiguration.create();
        //配置zk地址，通过zk可以找到hbase
        conf.set("hbase.zookeeper.quorum","master:2181,node1:2181,node2:2181");
        //创建连接
        Connection conn = ConnectionFactory.createConnection(conf);
        /**
         * 操作表getAdmin
         */
        Admin admin = conn.getAdmin();
        //创建textAPI表，并指定列簇cf1，并将列簇的版本设置为3

        HTableDescriptor textAPI = new HTableDescriptor(TableName.valueOf("textAPI"));
        //创建一个列簇
        HColumnDescriptor cf1 = new HColumnDescriptor("cf1");
        //给列簇进行设置
        cf1.setMaxVersions(3);
        //给textAPI表增加一个列簇
        textAPI.addFamily(cf1);
        //创建表
        admin.createTable(textAPI);
        /**
         * 操作数据getTable
         */

        //关闭连接
        admin.close();
        conn.close();
    }
}


回到顶部(go to top)
三、Java API操作hbase
getadmin对表操作
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class APIDemo2 {
    Connection conn;
    Admin admin;

    @Before
    public void createConn() throws IOException {
        //创建配置文件
        Configuration conf = HBaseConfiguration.create();
        //配置zk地址
        conf.set("hbase.zookeeper.quorum","master:2181,node1:2181,node2:2181");
        //创建连接
        conn= ConnectionFactory.createConnection(conf);
        //创建admin对象
        admin = conn.getAdmin();
    }

    @Test
    /**
     *list
     */
    public void list() throws IOException {
        TableName[] tableNames = admin.listTableNames();
        for (TableName tableName : tableNames) {
            System.out.println(tableName.getNameAsString());
        }
    }

    @Test
    /**
     * create table 建表
     */
    public void createTable() throws IOException {
        HTableDescriptor text = new HTableDescriptor(TableName.valueOf("text"));
        HColumnDescriptor info = new HColumnDescriptor("info");
        text.addFamily(info);
        admin.createTable(text);
    }

    @Test
    /**
     * drop table删表
     */
    public void dropTable() throws IOException {
        //创建需要删除表对象
        TableName text = TableName.valueOf("text");
        //判断是否存在
        if (admin.tableExists(text)){
            admin.disableTableAsync(text);
            admin.deleteTable(text);
        }
    }

    @Test
    /**
     * 修改表结构
     * 针对test表 将其info列簇的ttl设置为10000，并增加一个新的列簇cf1
     */
    public void modifyTable() throws IOException {
        TableName textAPI = TableName.valueOf("text");
        //获取表原有的结构
        HTableDescriptor tableDescriptor = admin.getTableDescriptor(textAPI);
        //在表原有的结构上修改簇的属性
        HColumnDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
        //遍历表中原有的列簇
        for (HColumnDescriptor columnFamily : columnFamilies) {
            //对原有的info列簇进行修改
            if ("info".equals(columnFamily.getNameAsString())){
                columnFamily.setTimeToLive(10000);
            }
        }
        HColumnDescriptor cf1 = new HColumnDescriptor("cf1");
        tableDescriptor.addFamily(cf1);
        admin.modifyTable(textAPI,tableDescriptor);
    }

    @After
    /**
     * 关闭资源
     */
    public void close() throws IOException {
        admin.close();
        conn.close();
    }
}
getTable对表数据操作
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Demo2API {

    Connection conn;
    Admin admin;

    @Before
    public void createConn() throws IOException {
        // 1、创建一个配置文件
        Configuration conf = HBaseConfiguration.create();
        // 配置ZK的地址，通过ZK可以找到HBase
        conf.set("hbase.zookeeper.quorum", "master:2181,node1:2181,node2:2181");

        // 2、创建连接
        conn = ConnectionFactory.createConnection(conf);
        // 3、创建Admin对象
        admin = conn.getAdmin();
    }

    @Test
    /**
     * put 插入一条数据
     */
    public void put() throws IOException {
        Table testAPI = conn.getTable(TableName.valueOf("testAPI"));

        Put put = new Put("0002".getBytes());
        // 相当于插入一列（一个cell）数据
        put.addColumn("cf1".getBytes(), "name".getBytes(), "李四".getBytes());
        put.addColumn("cf1".getBytes(), "age".getBytes(), "23".getBytes());
        put.addColumn("cf1".getBytes(), "phone".getBytes(), "18888887".getBytes());

        testAPI.put(put);
    }

    @Test
    /**
     * get 根据rowkey获取一条数据
     */
    public void get() throws IOException {
        Table testAPI = conn.getTable(TableName.valueOf("testAPI"));

        Get get = new Get("0002".getBytes());

        Result rs = testAPI.get(get);
        // 获取rk
        byte[] rk = rs.getRow();
        System.out.println(rk);
        System.out.println(Bytes.toString(rk));
        // 获取cell
        byte[] name = rs.getValue("cf1".getBytes(), "name".getBytes());
        System.out.println(name);
        System.out.println(Bytes.toString(name));

    }

    @Test
    /**
     * delete 删除数据
     */
    public void deleteLine() throws IOException {
        Delete delete = new Delete("1500100001".getBytes());
        Table student = conn.getTable(TableName.valueOf("student"));
        student.delete(delete);
    }

    @Test
    /**
     * putAll 读取学生信息数据并写入HBase的student表
     */
    public void putAll() throws IOException {
        /**
         * 读取学生信息数据
         */

        // Junit 和 main方法运行时的工作路径不一样
        // 这里传入的相对路径要动态调整
        BufferedReader br = new BufferedReader(new FileReader("data/students.txt"));

        // 与HBase中的student表建立连接
        Table student = conn.getTable(TableName.valueOf("split_table_test"));

        String line = null;

        // 创建Put的集合
        ArrayList<Put> puts = new ArrayList<>();
        int batchSize = 11;
        while ((line = br.readLine()) != null) {
            // 写入HBase
            String[] splits = line.split(",");
            String id = splits[0];
            String name = splits[1];
            String age = splits[2];
            String gender = splits[3];
            String clazz = splits[4];

            Put put = new Put(id.getBytes());
            byte[] info = "cf".getBytes();
            put.addColumn(info, "name".getBytes(), name.getBytes());
            put.addColumn(info, "age".getBytes(), age.getBytes());
            put.addColumn(info, "gender".getBytes(), gender.getBytes());
            put.addColumn(info, "clazz".getBytes(), clazz.getBytes());


            // 每条数据都会执行一次，效率很慢
//            student.put(put);

            // 将每个Put对象加入puts集合
            puts.add(put);
            // 当puts集合的大小同batchSize大小一致时，则调用HTable的put方法进行批量写入
            if (puts.size() == batchSize) {
                student.put(puts);
                // 清空集合
                puts.clear();
            }

        }
        System.out.println(puts.isEmpty());
        System.out.println(puts.size());
        // 当batchSize的大小同数据的条数不成整比的时候 可能会造成最后几条数据未被写入
        // 手动去判断puts集合是否为空，不为空则将其写入HBase
        if (!puts.isEmpty()) {
            student.put(puts);
        }


        br.close();


    }

    @Test
    /**
     * scan 获取一组数据
     * 读取student表
     */
    public void getScan() throws IOException {
        Table student = conn.getTable(TableName.valueOf("student"));

        // scan可以指定rowkey的范围进行查询，或者是限制返回的条数
        Scan scan = new Scan();
        scan.withStartRow("1500100100".getBytes());
        scan.withStopRow("1500100111".getBytes());
        scan.setLimit(10);

        for (Result rs : student.getScanner(scan)) {
            String id = Bytes.toString(rs.getRow());
            String name = Bytes.toString(rs.getValue("info".getBytes(), "name".getBytes()));
            String age = Bytes.toString(rs.getValue("info".getBytes(), "age".getBytes()));
            String gender = Bytes.toString(rs.getValue("info".getBytes(), "gender".getBytes()));
            String clazz = Bytes.toString(rs.getValue("info".getBytes(), "clazz".getBytes()));

            System.out.println(id + "," + name + "," + age + "," + gender + "," + clazz);
        }

    }

    @Test
    /**
     * CellUtil
     */
    public void scanWithCellUtil() throws IOException {
        Table student = conn.getTable(TableName.valueOf("student"));

        // scan可以指定rowkey的范围进行查询，或者是限制返回的条数
        Scan scan = new Scan();
        scan.withStartRow("1500100990".getBytes());
//        scan.withStopRow("1500100111".getBytes());

        for (Result rs : student.getScanner(scan)) {
            String id = Bytes.toString(rs.getRow());
            System.out.print(id + " ");
            // 将一条数据的所有的cell列举出来
            // 使用CellUtil从每一个cell中取出数据
            // 不需要考虑每条数据的结构
            List<Cell> cells = rs.listCells();
            for (Cell cell : cells) {
                String value = Bytes.toString(CellUtil.cloneValue(cell));
                System.out.print(value + " ");
            }
            System.out.println();

        }
    }


    @After
    public void close() throws IOException {
        admin.close();
        conn.close();
    }

}
回到顶部(go to top)
四、HBase BulkLoading
优点
如果我们一次性入库hbase巨量数据，处理速度慢不说，还特别占用Region资源， 一个比较高效便捷的方法就是使用 “Bulk Loading”方法，即HBase提供的HFileOutputFormat类。
它是利用hbase的数据信息按照特定格式存储在hdfs内这一原理，直接生成这种hdfs内存储的数据格式文件，然后上传至合适位置，即完成巨量数据快速入库的办法。配合mapreduce完成，高效便捷，而且不占用region资源，增添负载。
限制
仅适合初次数据导入，即表内数据为空，或者每次入库表内都无数据的情况。
HBase集群与Hadoop集群为同一集群，即HBase所基于的HDFS为生成HFile的MR的集群
代码
package com.shujia;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.KeyValueSortReducer;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.mapreduce.SimpleTotalOrderPartitioner;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class Demo10BulkLoading {
    public static class BulkLoadingMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, KeyValue> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] splits = value.toString().split(",");
            String mdn = splits[0];
            String start_time = splits[1];
            // 经度
            String longitude = splits[4];
            // 维度
            String latitude = splits[5];

            String rowkey = mdn + "_" + start_time;

            KeyValue lg = new KeyValue(rowkey.getBytes(), "info".getBytes(), "lg".getBytes(), longitude.getBytes());
            KeyValue lt = new KeyValue(rowkey.getBytes(), "info".getBytes(), "lt".getBytes(), latitude.getBytes());

            context.write(new ImmutableBytesWritable(rowkey.getBytes()), lg);
            context.write(new ImmutableBytesWritable(rowkey.getBytes()), lt);

        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "master:2181,node1:2181,node2:2181");


        // 创建Job实例
        Job job = Job.getInstance(conf);
        job.setJarByClass(Demo10BulkLoading.class);
        job.setJobName("Demo10BulkLoading");

        // 保证全局有序
        job.setPartitionerClass(SimpleTotalOrderPartitioner.class);

        // 设置reduce个数
        job.setNumReduceTasks(4);
        // 配置map任务
        job.setMapperClass(BulkLoadingMapper.class);

        // 配置reduce任务
        // KeyValueSortReducer 保证在每个Reduce有序
        job.setReducerClass(KeyValueSortReducer.class);

        // 输入输出路径
        FileInputFormat.addInputPath(job, new Path("/data/DIANXIN/"));
        FileOutputFormat.setOutputPath(job, new Path("/data/hfile"));

        // 创建HBase连接
        Connection conn = ConnectionFactory.createConnection(conf);
        // create 'dianxin_bulk','info'
        // 获取dianxin_bulk 表
        Table dianxin_bulk = conn.getTable(TableName.valueOf("dianxin_bulk"));
        // 获取dianxin_bulk 表 region定位器
        RegionLocator regionLocator = conn.getRegionLocator(TableName.valueOf("dianxin_bulk"));
        // 使用HFileOutputFormat2将输出的数据按照HFile的形式格式化
        HFileOutputFormat2.configureIncrementalLoad(job, dianxin_bulk, regionLocator);

        // 等到MapReduce任务执行完成
        job.waitForCompletion(true);

        // 加载HFile到 dianxin_bulk 中
        LoadIncrementalHFiles load = new LoadIncrementalHFiles(conf);
        load.doBulkLoad(new Path("/data/hfile"), conn.getAdmin(), dianxin_bulk, regionLocator);

        /**
         *  create 'dianxin_bulk','info'
         *  hadoop jar HBaseJavaAPI10-1.0-jar-with-dependencies.jar com.shujia.Demo10BulkLoading
         */
    }
}
说明
最终输出结果，无论是map还是reduce，输出部分key和value的类型必须是： < ImmutableBytesWritable, KeyValue>或者< ImmutableBytesWritable, Put>。
最终输出部分，Value类型是KeyValue 或Put，对应的Sorter分别是KeyValueSortReducer或PutSortReducer。
MR例子中HFileOutputFormat2.configureIncrementalLoad(job, dianxin_bulk, regionLocator);自动对job进行配置。SimpleTotalOrderPartitioner是需要先对key进行整体排序，然后划分到每个reduce中，保证每一个reducer中的的key最小最大值区间范围，是不会有交集的。因为入库到HBase的时候，作为一个整体的Region，key是绝对有序的。
MR例子中最后生成HFile存储在HDFS上，输出路径下的子目录是各个列族。如果对HFile进行入库HBase，相当于move HFile到HBase的Region中，HFile子目录的列族内容没有了，但不能直接使用mv命令移动，因为直接移动不能更新HBase的元数据。
HFile入库到HBase通过HBase中 LoadIncrementalHFiles的doBulkLoad方法，对生成的HFile文件入库
```

# Hive 之 API

```text
文章目录
一、Java
1.jdbc：
2.Spark Sql：
二、Scala
1.Spark Sql：
三、踩坑之路
1. Required field 'client_protocol' is unset!
一、Java
1.jdbc：
  JDBC 连接hive2, 程序挺简单, 跟其他数据库查询类似, 连接/执行查询/得到结果：

package com.huiq.bigdata.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ApiQueryTest {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args){
        try {
            Class.forName(driverName);
            Connection con = null;
            con = DriverManager.getConnection("jdbc:hive2://node01:10000/ods_test_schema1", "root", "123456");
            Statement stmt = con.createStatement();
            ResultSet res = null;
            String sql = "select * from t1";
            System.out.println("Running: " + sql);
            res = stmt.executeQuery(sql);
            System.out.println("ok");
            while (res.next()) {
                System.out.println(res.getString(1) + "\t" + res.getString(2));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

Linux执行结果：

[root@node01 huiq]# java -cp hivejdbc-jar-with-dependencies.jar com.huiq.bigdata.test.ApiQueryTest
Running: select * from t1
ok
-103829163_116	116
-1406838352_113	113
-1511909649_130	130
-1789481461_141	141

IDEA在Windows中执行结果：

所需依赖：

        <dependency>
            <groupId>org.apache.hive</groupId>
            <artifactId>hive-jdbc</artifactId>
            <version>3.1.0</version>
        </dependency>

2.Spark Sql：
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.hive.HiveContext;

/**
 * @author: huiq
 * @createTime: 2021/8/27 13:55
 * @description: 连接hive测试
 */
public class SparkSqlHiveTest
{
    public static void main( String[] args )
    {
        SparkConf sparkConf = new SparkConf().setAppName("SparkHive").setMaster("local[2]");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        HiveContext hiveContext = new HiveContext(sc);
        //不要使用SQLContext,部署异常找不到数据库和表
//        SQLContext sqlContext = new SQLContext(sc);
        //查询表前10条数据
//        hiveContext.sql("select * from datawarehouse_ods_db.ods_ct_taglimit 10").show();

		spark.sql("use datawarehouse_dws_db");
		spark.sql("create table if not exists member_reg_num_day_tmp as \nselect \n    to_date(reg_time) date_time,\n    count(1) num \nfrom \n    datawarehouse_dwd_db.dwd_us_user \nwhere \n    reg_time!='' \ngroup by \n    to_date(reg_time)");
		
        sc.stop();
    }
}
所需依赖：

        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-hive_2.11</artifactId>
            <version>2.3.1</version>
        </dependency>

执行命令：

spark-submit --master local[2] --class com.huiq.bigdata.online.SparkSqlHiveTest hbase-jar-with-dependencies.jar

注：SQLContext和HiveContext区别和联系
  Spark SQL程序的主入口是SQLContext类或它的子类。创建一个基本的SQLContext，你只需要SparkContext
  除了基本的SQLContext，也可以创建HiveContext。SQLContext和HiveContext区别与联系为：
  SQLContext现在只支持SQL语法解析器（SQL-92语法）
  HiveContext现在支持SQL语法解析器和HiveSQL语法解析器，默认为HiveSQL语法解析器，用户可以通过配置切换成SQL语法解析器，来运行HiveSQL不支持的语法。
  使用HiveContext可以使用Hive的UDF，读写Hive表数据等Hive操作。SQLContext不可以对Hive进行操作。
  Spark SQL未来的版本会不断丰富SQLContext的功能，做到SQLContext和HiveContext的功能容和，最终可能两者会统一成一个Context
  HiveContext包装了Hive的依赖包，把HiveContext单独拿出来，可以在部署基本的Spark的时候就不需要Hive的依赖包，需要使用HiveContext时再把Hive的各种依赖包加进来。
  SQL的解析器可以通过配置spark.sql.dialect参数进行配置。在SQLContext中只能使用Spark SQL提供的”sql“解析器。在HiveContext中默认解析器为”hiveql“，也支持”sql“解析器。
 

二、Scala
1.Spark Sql：
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * @Auther: huiq
 * @Date: 2021/7/23
 * @Description: 连接hive测试
 */
object SparkSqlHiveTest {

  def main(args: Array[String]): Unit = {

    //初始化spark
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName(this.getClass.getSimpleName)
    val spark: SparkSession = SparkSession.builder().enableHiveSupport().config(sparkConf).getOrCreate()

    //选择Hive库
    spark.sql("use datawarehouse_dws_db");
	spark.sql("create table if not exists member_reg_num_day_tmp as \nselect \n    to_date(reg_time) date_time,\n    count(1) num \nfrom \n    datawarehouse_dwd_db.dwd_us_user \nwhere \n    reg_time!='' \ngroup by \n    to_date(reg_time)");
	
    spark.stop();
  }
}

所需依赖：

        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-hive_2.11</artifactId>
            <version>2.3.1</version>
        </dependency>
执行命令：

spark-submit --master local[2] --class com.huiq.bigdata.online.SparkSqlHiveTest hbase-jar-with-dependencies.jar
注：如果要在IDEA中纯maven项目中执行的话需要把服务器上的hive-site.xml、hdfs-site.xml、core-site.xml文件放到resource目录下；但是在springboot项目下整合却报错：Exception in thread "main" java.lang.NoSuchFieldError: HIVE_STATS_JDBC_TIMEOUT，可能是springboot内部有什么处理导致依赖冲突，可能需要重新编译spark-hive的jar包，思路可参考spark hive java.lang.NoSuchFieldError: HIVE_STATS_JDBC_TIMEOUT

遇到的问题：org.apache.thrift.TApplicationException: Required field 'filesAdded' is unset! Struct:InsertEventRequestData(filesAdded:null)

解决：把sql语句转换成这样的可以成功：spark.sql("select \n to_date(reg_time) date_time,\n count(1) num \nfrom \n datawarehouse_dwd_db.dwd_us_user \nwhere \n reg_time!='' \ngroup by \n to_date(reg_time)").write.mode(SaveMode.Overwrite).saveAsTable("datawarehouse_dws_db.member_reg_num_day_tmp")
  Java代码的话为：hiveContext.sql("select \n to_date(reg_time) date_time,\n count(1) num \nfrom \n rongrong_datawarehouse_dwd_db.dwd_rongrong_us_user \nwhere \n reg_time!='' \ngroup by \n to_date(reg_time)").write().mode(SaveMode.Overwrite).saveAsTable("rongrong_datawarehouse_dws_db.member_reg_num_day_tmp");

注：所遇到的问题上面Spark Sql的java版本同样适用。

三、踩坑之路
1. Required field ‘client_protocol’ is unset!
log4j:WARN No appenders could be found for logger (org.apache.hadoop.util.Shell).
log4j:WARN Please initialize the log4j system properly.
log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
java.sql.SQLException: Could not open client transport with JDBC Uri: jdbc:hive2://node01:10000/heheda_ods_db: Could not establish connection to jdbc:hive2://node01:10000/heheda_ods_db: Required field 'client_protocol' is unset! Struct:TOpenSessionReq(client_protocol:null, configuration:{set:hiveconf:hive.server2.thrift.resultset.default.fetch.size=1000, use:database=heheda_ods_db})
        at org.apache.hive.jdbc.HiveConnection.<init>(HiveConnection.java:252)
        at org.apache.hive.jdbc.HiveDriver.connect(HiveDriver.java:107)
        at java.sql.DriverManager.getConnection(DriverManager.java:664)
        at java.sql.DriverManager.getConnection(DriverManager.java:247)
Caused by: java.sql.SQLException: Could not establish connection to jdbc:hive2://node01:10000/heheda_ods_db: Required field 'client_protocol' is unset! Struct:TOpenSessionReq(client_protocol:null, configuration:{set:hiveconf:hive.server2.thrift.resultset.default.fetch.size=1000, use:database=heheda_ods_db})
        at org.apache.hive.jdbc.HiveConnection.openSession(HiveConnection.java:734)
        at org.apache.hive.jdbc.HiveConnection.<init>(HiveConnection.java:228)
        ... 4 more
Caused by: org.apache.thrift.TApplicationException: Required field 'client_protocol' is unset! Struct:TOpenSessionReq(client_protocol:null, configuration:{set:hiveconf:hive.server2.thrift.resultset.default.fetch.size=1000, use:database=heheda_ods_db})
        at org.apache.thrift.TApplicationException.read(TApplicationException.java:111)
        at org.apache.thrift.TServiceClient.receiveBase(TServiceClient.java:79)
        at org.apache.hive.service.rpc.thrift.TCLIService$Client.recv_OpenSession(TCLIService.java:176)
        at org.apache.hive.service.rpc.thrift.TCLIService$Client.OpenSession(TCLIService.java:163)
        at org.apache.hive.jdbc.HiveConnection.openSession(HiveConnection.java:715)
        ... 5 more

原因：项目的 Pom 文件中 hive-jdbc 版本号没有与 hive Server 的version要保持一致

一开始的配置：
        <dependency>
            <groupId>org.apache.hive</groupId>
            <artifactId>hive-jdbc</artifactId>
            <version>2.1.0</version>
        </dependency>
修改后的配置：
        <dependency>
            <groupId>org.apache.hive</groupId>
            <artifactId>hive-jdbc</artifactId>
            <version>3.1.0</version>
        </dependency>

参考：
hive客户端连接服务端报错“Required field ‘client_protocol’ is unset! ”异常解决
Required field ‘client_protocol‘ is unset 原因探究
https://blog.csdn.net/m0_37739193/article/details/119608467
```

# Hadoop学习总结（使用Java API操作HDFS）
```text
使用Java API操作HDFS，是在安装和配置Maven、IDEA中配置Maven成功情况下进行的，如果Maven安装和配置不完全将不能进行Java API操作HDFS。

      由于Hadoop是使用Java语言编写的，因此可以使用Java API操作Hadoop文件系统。使用HDFS提供的Java API构造一个访问客户端对象，然后通过客户端对象对HDFS上的文件进行操作（增、删、改、查）。

      可以使用单元测试法操作HDFS。这里不使用单元测试法。

一、创建HDFS_CRUD.java文件


二、初始化客户端对象
      通过 main() 方法调用进行HDFS增、删、改、查



public class HDFS_CRUD {
    public static void main(String[] args) throws IOException {
        // 初始化客户端对象
        //构造一个配置对象，设置一个参数：访问的 HDFS 的 URL
        Configuration conf = new Configuration();
        //这里指定使用的是 HDFS
        conf.set("fs.defaultFS", "hdfs://hadoop00:9000");
        //通过如下的方式进行客户端身份的设置
        System.setProperty("HADOOP_USER_NAME", "root");
        //通过 FileSystem 的静态方法获取文件系统客户端对象
        fs = FileSystem.get(conf);  //抛出异常
        System.out.println("hdfs连接成功");
    }
 
}
三、本地上传文件到HDFS
static FileSystem fs = null;
      声明了一个静态的FileSystem对象fs，并将其初始化为null。FileSystem是Java中用于操作Hadoop分布式文件系统(HDFS)的类。通过这个对象，可以执行一些与HDFS相关的操作，如创建文件、删除文件、读取文件等。在这段代码中，fs被声明为静态的，意味着它可以在整个类中被共享和访问。初始值为null，可能是因为在代码的其他部分会对其进行初始化。

      下面对上传功能进行编译



// 完成上传功能
    public static void upload(String path_str,String path_str1) throws IOException {
        //上传文件到HDFS
        //path_str本地文件路径  path_str1是上传到HDFS文件路径
        fs.copyFromLocalFile(new Path(path_str),new Path(path_str1));
        // 关闭资源
        fs.close();
        System.out.println("文件上传成功");
    }
//main()方法中调用
        upload("D:/大数据/word.txt","/input");  //上传
四、从HDFS下载文件到本地


// 完成下载文件
    public static void downloal(String path_str,String path_str1) throws IOException {
        //从 HDFS 下载文件到本地
        //path_str是HDFS文件路径  path_str1本地文件路径
        fs.copyToLocalFile(new Path(path_str),new Path(path_str1));
        // 关闭资源
        fs.close();
        System.out.println("文件下载成功");
    }
​
//main()方法中调用
        downloal("/data.txt","D:/大数据/文件");  //下载
五、创建目录


    // 创建目录
    public static void mkdir(String path_str) throws IOException {
        //path_str所要创建目录路径
        fs.mkdirs(new Path(path_str));
        // 关闭资源
        fs.close();
        System.out.println("创建目录成功");
    }
        //main()方法中调用
        mkdir("/input");  //创建目录
六、重命名文件或文件夹


    // 重命名文件夹
    public static void rename(String old_name,String new_path) throws IOException {
        //old_name原文件名路径  //new_path新文件名路径
        fs.rename(new Path(old_name),new Path(new_path));
        fs.close();
        System.out.println("重命名文件夹成功");
    }
    //main()方法中调用
    rename("/aa","/aa2");  //重命名文件夹
七、删除文件


    // 删除文件 ,如果是非空文件夹,参数2必须给值true
    public static void delete(String path_str) throws IOException {
        //ture表示递归删除 可以用来删除目录 rm -rf
        //false表示非递归删除
        fs.delete(new Path(path_str),true);
        // 关闭资源
        fs.close();
        System.out.println("删除文件夹成功");
    }
        //main()方法中调用
        delete("/aa2");  //删除文件
八、查看文件信息
1、查看文件信息
    // 查看文件信息
    public static void  listFiles(String path_str) throws IOException {
        //获取迭代器对象
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path(path_str),true);
        //遍历
        while (listFiles.hasNext()){
            LocatedFileStatus fileStatus = listFiles.next();
            //打印当前文件名
            System.out.println(fileStatus.getPath().getName());
            //打印当前文件块大小
            System.out.println(fileStatus.getBlockLocations());
            //打印当前文件权限
            System.out.println(fileStatus.getPermission());
            //打印当前文件内容长度
            System.out.println(fileStatus.getLen());
            //获取该文件块信息(包含长度、数据块、datanode的信息)
//            BlockLocation[] blockLocations = fileStatus.getBlockLocations();
//            for (BlockLocation bl : blockLocations){
//                System.out.println("block-length:" + bl.getLength()+"--"+"block-offset:"+bl.getOffset());
//                String[] hosts = bl.getHosts();
//                for (String host : hosts){
//                    System.out.println(host);
//                }
//            }
        }
        System.out.println("--------分割线---------");
        fs.close();
    }

        //main()方法中调用
        listFiles("/data.txt");  //查看文件信息
2、统计目录下所有文件(包括子目录)


    // 1、统计目录下所有文件(包括子目录)
    // 1、统计某个路径(由main方法决定哪个路径),下所有的文件数里,例如：输出：该路径下共有 3 个文件
    public static void count(String path_str) throws IOException {
        //获取迭代器对象
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path(path_str),true);
        //遍历
        int count = 0;
        while (listFiles.hasNext()) {
            LocatedFileStatus fileStatus = listFiles.next();
            count++;
        }
        System.out.println("路径：【"+ path_str +"】下,文件数量为"+count);
        fs.close();
    }
        //main()方法中调用
        count("/");  //统计
 3、列出某个路径下所有的文件数里


    // 2、列出某个路径(由main方法决定哪个路径),下所有的文件数里,例如：文件1,文"路径：【"+ path_str +"】下,文件有："+件2,....
    public static void fileList(String path_str) throws IOException {
        //获取迭代器对象
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path(path_str),true);
        String res = "";
        //遍历
        while (listFiles.hasNext()) {
            LocatedFileStatus fileStatus = listFiles.next();
            res += fileStatus.getPath().getName() + ", ";
        }
        if (res.equals("")){
            res = "没有文件";
        }else {
            res = res.substring(0,res.length() - 2);
        }
        System.out.println("路径：【"+ path_str +"】下的文件：" + res);
//        fs.close();
    }

        //main()方法中调用
          fileList("/"); //查看有什么文件
          fileList("/input"); //查看有什么文件
4、查看所有文件
/*    路径【/】下共有 7 子文件
    文件数量：1,文件列表：data.txt
    目录数量：6,文件列表：a, exp, input, output, test, tmp*/
    public static void list(String path) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(new Path(path));
        String res = "路径【" + path + "】下共有 " + fileStatuses.length + " 子文件";
        int file_num = 0;
        String file_list = "";
        int dir_num = 0;
        String dir_list = "";
        for (FileStatus fileStatus:fileStatuses){
            if (fileStatus.isFile()){
                file_num ++;
                file_list += fileStatus.getPath().getName() + ", ";
            }else {
                dir_num ++;
                dir_list += fileStatus.getPath().getName() + ", ";
            }
        }
        if (file_num != 0) res += "\n\t文件数量：" + file_num + ",文件列表：" + file_list.substring(0,file_list.length()-2);
        if (dir_num != 0) res += "\n\t目录数量：" + dir_num + ",文件列表：" + dir_list.substring(0,dir_list.length()-2);
        System.out.println(res);
    }

        //main()方法中调用
        list("/"); //查看所有
5、判断是文件还是目录


    // 检查路径是目录还是文件
    public static void mulu(String path_str) throws IOException {
        Path path = new Path(path_str);
        // 判断路径是否存在
        if (fs.exists(path)) {
            // 获取指定路径的详细信息
            FileStatus status = fs.getFileStatus(path);
            if (status.isDirectory()) {
                System.out.println(path + "这是一个目录");
            } else if (status.isFile()) {
                System.out.println(path + "这是一个文件");
            } else {
                System.out.println("这是一个未知类型");
            }
        } else {
            System.out.println("路径不存在");
        }
        //关闭资源
        fs.close();
    }

        //main()方法中调用
        mulu("/exp/word.txt"); //检查路径是目录还是文件
九、源代码
package com.itcast.hdfsdemo;
 
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import sun.tracing.dtrace.DTraceProviderFactory;
 
import java.io.IOException;
import java.util.Arrays;
 
public class HDFS_CRUD {
 
    static FileSystem fs = null;
 
    // 完成上传功能
    public static void upload(String path_str,String path_str1) throws IOException {
        //上传文件到HDFS
        //path_str本地文件路径  path_str1是上传到HDFS文件路径
        fs.copyFromLocalFile(new Path(path_str),new Path(path_str1));
        // 关闭资源
        fs.close();
        System.out.println("文件上传成功");
    }
 
    // 完成下载文件
    public static void downloal(String path_str,String path_str1) throws IOException {
        //从 HDFS 下载文件到本地
        //path_str是HDFS文件路径  path_str1本地文件路径
        fs.copyToLocalFile(new Path(path_str),new Path(path_str1));
        // 关闭资源
        fs.close();
        System.out.println("文件下载成功");
    }
 
    // 创建目录
    public static void mkdir(String path_str) throws IOException {
        //path_str所要创建目录路径
        fs.mkdirs(new Path(path_str));
        // 关闭资源
        fs.close();
        System.out.println("创建目录成功");
    }
 
    // 重命名文件夹
    public static void rename(String old_name,String new_path) throws IOException {
        //old_name原文件名路径  //new_path新文件名路径
        fs.rename(new Path(old_name),new Path(new_path));
        // 关闭资源
        fs.close();
        System.out.println("重命名文件夹成功");
    }
    //main()方法中调用
//    rename("/aa","/aa2");  //重命名文件夹
 
    // 删除文件 ,如果是非空文件夹,参数2必须给值true
    public static void delete(String path_str) throws IOException {
        //ture表示递归删除 可以用来删除目录 rm -rf
        //false表示非递归删除
        fs.delete(new Path(path_str),true);
        // 关闭资源
        fs.close();
        System.out.println("删除文件夹成功");
    }
 
    // 查看文件信息
    public static void  listFiles(String path_str) throws IOException {
        //获取迭代器对象
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path(path_str),true);
        //遍历
        while (listFiles.hasNext()){
            LocatedFileStatus fileStatus = listFiles.next();
            //打印当前文件名
            System.out.println(fileStatus.getPath().getName());
            //打印当前文件块大小
            System.out.println(fileStatus.getBlockLocations());
            //打印当前文件权限
            System.out.println(fileStatus.getPermission());
            //打印当前文件内容长度
            System.out.println(fileStatus.getLen());
            //获取该文件块信息(包含长度、数据块、datanode的信息)
//            BlockLocation[] blockLocations = fileStatus.getBlockLocations();
//            for (BlockLocation bl : blockLocations){
//                System.out.println("block-length:" + bl.getLength()+"--"+"block-offset:"+bl.getOffset());
//                String[] hosts = bl.getHosts();
//                for (String host : hosts){
//                    System.out.println(host);
//                }
//            }
        }
        System.out.println("--------分割线---------");
        fs.close();
    }
    //把查看文件信息分解为下面几个方法
    // 1、统计目录下所有文件(包括子目录)
    // 1、统计某个路径(由main方法决定哪个路径),下所有的文件数里,例如：输出：该路径下共有 3 个文件
    public static void count(String path_str) throws IOException {
        //获取迭代器对象
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path(path_str),true);
        //遍历
        int count = 0;
        while (listFiles.hasNext()) {
            LocatedFileStatus fileStatus = listFiles.next();
            count++;
        }
        System.out.println("路径：【"+ path_str +"】下,文件数量为"+count);
        fs.close();
    }
 
    // 2、列出某个路径(由main方法决定哪个路径),下所有的文件数里,例如：文件1,文"路径：【"+ path_str +"】下,文件有："+件2,....
    public static void fileList(String path_str) throws IOException {
        //获取迭代器对象
        RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path(path_str),true);
        String res = "";
        //遍历
        while (listFiles.hasNext()) {
            LocatedFileStatus fileStatus = listFiles.next();
            res += fileStatus.getPath().getName() + ", ";
        }
        if (res.equals("")){
            res = "没有文件";
        }else {
            res = res.substring(0,res.length() - 2);
        }
        System.out.println("路径：【"+ path_str +"】下的文件：" + res);
//        fs.close();
    }
 
/*    路径【/】下共有 7 子文件
    文件数量：1,文件列表：data.txt
    目录数量：6,文件列表：a, exp, input, output, test, tmp*/
    public static void list(String path) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(new Path(path));
        String res = "路径【" + path + "】下共有 " + fileStatuses.length + " 子文件";
        int file_num = 0;
        String file_list = "";
        int dir_num = 0;
        String dir_list = "";
        for (FileStatus fileStatus:fileStatuses){
            if (fileStatus.isFile()){
                file_num ++;
                file_list += fileStatus.getPath().getName() + ", ";
            }else {
                dir_num ++;
                dir_list += fileStatus.getPath().getName() + ", ";
            }
        }
        if (file_num != 0) res += "\n\t文件数量：" + file_num + ",文件列表：" + file_list.substring(0,file_list.length()-2);
        if (dir_num != 0) res += "\n\t目录数量：" + dir_num + ",文件列表：" + dir_list.substring(0,dir_list.length()-2);
        System.out.println(res);
    }
 
    // 检查路径是目录还是文件
    public static void mulu(String path_str) throws IOException {
        Path path = new Path(path_str);
        // 判断路径是否存在
        if (fs.exists(path)) {
            // 获取指定路径的详细信息
            FileStatus status = fs.getFileStatus(path);
            if (status.isDirectory()) {
                System.out.println(path + "这是一个目录");
            } else if (status.isFile()) {
                System.out.println(path + "这是一个文件");
            } else {
                System.out.println("这是一个未知类型");
            }
        } else {
            System.out.println("路径不存在");
        }
        //关闭资源
        fs.close();
    }
 
        //调用
    public static void main(String[] args) throws IOException {
        // 初始化客户端对象
        //构造一个配置对象，设置一个参数：访问的 HDFS 的 URL
        Configuration conf = new Configuration();
        //这里指定使用的是 HDFS
        conf.set("fs.defaultFS","hdfs://hadoop00:9000");
        //通过如下的方式进行客户端身份的设置
        System.setProperty("HADOOP_USER_NAME","root");
        //通过 FileSystem 的静态方法获取文件系统客户端对象
        fs = FileSystem.get(conf);  //抛出异常
        System.out.println("hdfs连接成功");
        
        //main()方法中调用
//        list("/"); //查看所有
        //main()方法中调用
//          fileList("/"); //查看有什么文件
//          fileList("/input"); //查看有什么文件
        //main()方法中调用
//        count("/");  //统计
        //main()方法中调用
//        mulu("/exp/word.txt"); //检查路径是目录还是文件
        //main()方法中调用
//        listFiles("/data.txt");  //查看文件信息
        //main()方法中调用
//        delete("/aa2");  //删除文件
        //main()方法中调用
//        rename("/aa","/aa2");  //重命名文件夹
        //main()方法中调用
//        upload("D:/大数据/word.txt","/input");  //上传
        //main()方法中调用
//        mkdir("/input");  //创建目录
        //main()方法中调用
//        downloal("/data.txt","D:/大数据/文件");  //下载
    }
}
https://blog.csdn.net/2202_75688394/article/details/134332350
```

# Spark3学习【基于Java】3. Spark-Sql常用API
```text
学习一门开源技术一般有两种入门方法，一种是去看官网文档，比如Getting Started - Spark 3.2.0 Documentation (apache.org)，另一种是去看官网的例子，也就是%SPARK_HOME%\examples下面的代码。打开IDEA，选择File-Open...



跟前面文章中方法一样导入jars目录到classpath。

Spark解析json字符串
第一个例子是读取并解析Json。这个例子的结果让我有些震惊，先上代码：

public static void main(String[] args) {
    SparkSession session = SparkSession.builder().master("local[1]").appName("SparkSqlApp").getOrCreate();
 
    Dataset<Row> json = session.read().json("spark-core/src/main/resources/people.json");
    json.show();
}
让我惊讶的是文件的内容。例子里面的文件是三个大括号并列，文件扩展名是.json，由于没有中括号，所以格式是错的：

{"name":"Michael"}
{"name":"Andy", "age":30}
{"name":"Justin", "age":19}
但是spark解析出来了：



于是我把文件改成下面这样向看下结果

[{"name":"Michael"},
{"name":"Andy", "age":30},
{"name":"Justin", "age":19}
]
你猜输出是什么？



显然，spark没有解析出第一行，而且把第4行也解析了。这也说明了为什么样例的文件可以解析：首先跟文件扩展名是没啥关系的，另外spark是按行解析，只要考虑这一行是否符合解析要求就可以，行末可以有逗号。所以把文件改成下面也是可以的

{"name":"Michael"},
{"name":"Andy", "age":30},..
{"name":"Justin", "age":19}
第一行后面有逗号，第二行后面还有两个点。

SQL 查询
在之前的例子中，读取文件返回的是Dataset<String>，因为之前确实是读取的文件内容。现在使用json()方法返回的是DataFrame，数据是经过spark处理过的。

DataFrame提供了一些好用的方法，用的最多的就是show()。它主要用于调试，可以把数据以表格形式打印。spark确实给DataFrame生成了表结构，可以通过printSchema()方法查看



不但有字段名，还有字段类型，还有是否可空（好像都能空）。

DF还提供了类似于sql查询的方法，比如select()/groupBy()，和where类似的filter()等：



这里我们首先给年龄字段+1，并通过别名（相等于SQL里的AS）让他覆盖之前的字段，然后查询比19大的记录，最后根据年龄分组汇总。

如果我们把新字段不覆盖原字段呢？你猜是执行报错还是啥结果？



That's all？当然不是，Spark提供了更强大的SQL操作：视图

View
视图分临时视图和全局视图。临时视图时会话级别的，会话结束了视图就没了；全局视图时应用级别的，只要Spark应用不停，视图就可以跨会话使用。



可见临时视图和全局视图可以叫一样的名字，它们的内容互不干扰。因为要访问全局视图需要通过global_temp库。不信你可以这样试一下

Dataset<Row> group = json.select(col("name"), col("age").plus(1).alias("age1"))
        .filter(col("age").gt(19))
        .groupBy("age1")
        .count();
 
group.createOrReplaceTempView("people");
json.createOrReplaceGlobalTempView("people");
Dataset<Row> temp = session.sql("select * from people");
Dataset<Row> global = session.sql("select * from global_temp.people");
Dataset<Row> global1 = session.newSession().sql("select * from global_temp.people");
temp.show();
global.show();
global1.show();
Dataset
我们已经跟Dataset打过不少交道了，这里再稍晚多说一点点。实际上如果你是自己摸索而不是完全看我写的，下面这些内容估计都已经探索出来了。

1 转换自DF
DF是无类型的，Dataset是有类型的。如果要把无类型的转成有类型的，就需要提供一个类型定义，就像mysql表和Java的PO一样。

先来定义Java类：

public class Person implements Serializable {
  private String name;
  private long age;
 
  public String getName() {
    return name;
  }
 
  public void setName(String name) {
    this.name = name;
  }
 
  public long getAge() {
    return age;
  }
 
  public void setAge(long age) {
    this.age = age;
  }
}
这个类必须实现序列化接口，原因在前面也说过了。

接下来把读入json的DataFrame转成Dataset：



之前都是使用Encoders内置的编码器，这里通过bean()方法生成我们自定义类的编码器，然后传给DF的as()方法就转成了Dataset。

既然转成了强类型的Dataset，那能把每一个对象拿出来吗？给Person类增加toString方法，然后遍历Dataset：



结果报错了竟然：已经生成了集合，却不能访问元素？

报错原因很简单：我们类中的age是原始数据类型，但是实际数据有一个null。把long age改成Long age即可：



但是为什么会这样呢？！~我猜是因为as方法用的编码器（序列化工具）和foreach用到的解码器不匹配，spark的编码器不要求数据符合Java编译规则。

来自Java集合
目前我们掌握了通过读取文件（textFile(path)）、转化其他Dataset（map/flatMap）和转换DF来生成Dataset，如果已经有一堆数据了，也可以直接创建。

SparkSession重载了大量根据数据集生成Dataset和DataFrame的方法，可以自由选择：



所以我们创建一个List来生成，只能是List，不能是Collection



神奇的是原本应该一样的代码，执行的时候有一个报错。这个算Java实现的BUG吧，原因参考Java中普通lambda表达式和方法引用本质上有什么区别？ - RednaxelaFX的回答 - 知乎

https://www.zhihu.com/question/51491241/answer/126232275

转自RDD
RDD 在Java环境下叫JavaRDD。它也是数据集，可以和Dataset/DataFrame互转。这里不说了，有兴趣可以探索。
https://www.cnblogs.com/somefuture/p/15637332.html
系列文章可参考
```

# Parquet文件测试（二）——Java方式对Parquet文件进行文件生成和解析
```text
https://blog.csdn.net/baidu_32377671/article/details/117253718
Java方式对Parquet文件进行文件生成和解析
  此处属于对Parquet文件测试（一）——使用Java方式生成Parqeut格式文件并直接入库的Hive中的补充，因为之前只是写了生成，并没有写如何解析，其次就是弄懂结构定义的问题。最终目的是生成正确的Parquet文件，使用Spark可以正常的读取文件内容（可参考Spark练习测试（二）——定义Parquet文件的字段结构）。

测试准备
  首先定义一个结构，到时候生成的Parquet文件会储存如下结构的内容：

import lombok.Data;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

/**
 * 测试结构
 * 属性为表字段
 */
@Data
public class TestEntity {
    private int intValue;
    private long longValue;
    private double doubleValue;
    private String stringValue;
    private byte[] byteValue;
    private byte[] byteNone;
}

  生成Parquet文件的测试用例代码如下：

import lombok.RequiredArgsConstructor;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.ExampleParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.schema.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TestComponent {

    @Autowired
    @Qualifier("testMessageType")
    private MessageType testMessageType;

    private static final String javaDirPath = "C:\\Users\\Lenovo\\Desktop\\对比\\java\\";

    /**
     * 文件写入parquet
     */
    public void javaWriteToParquet(TestEntity testEntity) throws IOException {
        String filePath = javaDirPath + System.currentTimeMillis() + ".parquet";
        ParquetWriter<Group> parquetWriter = ExampleParquetWriter.builder(new Path(filePath))
                .withWriteMode(ParquetFileWriter.Mode.CREATE)
                .withWriterVersion(ParquetProperties.WriterVersion.PARQUET_1_0)
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .withType(testMessageType).build();
        //写入数据
        SimpleGroupFactory simpleGroupFactory = new SimpleGroupFactory(testMessageType);
        Group group = simpleGroupFactory.newGroup();
        group.add("intValue", testEntity.getIntValue());
        group.add("longValue", testEntity.getLongValue());
        group.add("doubleValue", testEntity.getDoubleValue());
        group.add("stringValue", testEntity.getStringValue());
        group.add("byteValue", Binary.fromConstantByteArray(testEntity.getByteValue()));
        group.add("byteNone", Binary.EMPTY);
        parquetWriter.write(group);
        parquetWriter.close();
    }
}

  ※在配置字段结构的时候会有个问题如何配置字段的重复性。因为代码都是粘过来了一开始让人十分困惑，这玩意有啥作用。先来看下它们的经典说明（百度来的还是很靠谱的）：

方式	说明
REQUIRED	出现 1 次
OPTIONAL	出现 0 次或者 1 次
REPEATED	出现 0 次或者多次
  ※当然最让人困惑的就是什么TMD是1次、什么TMD是0次、什么TMD是多次。

定义结构配置字段属性使用REQUIRED
  此处还是使用SpringBootTest创建测试用例。接下来配置个Parquet的结构：

import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Types;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.*;

@Configuration
public class TestConfiguration {

    @Bean("testMessageType")
    public MessageType testMessageType() {
        Types.MessageTypeBuilder messageTypeBuilder = Types.buildMessage();
        messageTypeBuilder.required(INT32).named("intValue");
        messageTypeBuilder.required(INT64).named("longValue");
        messageTypeBuilder.required(DOUBLE).named("doubleValue");
        messageTypeBuilder.required(BINARY).as(LogicalTypeAnnotation.stringType()).named("stringValue");
        messageTypeBuilder.required(BINARY).as(LogicalTypeAnnotation.bsonType()).named("byteValue");
        messageTypeBuilder.required(BINARY).as(LogicalTypeAnnotation.bsonType()).named("byteNone");
        return messageTypeBuilder.named("test");
    }

}

  接下来执行测试方法生成Parquet文件。

import com.lyan.parquet_convert.test.TestComponent;
import com.lyan.parquet_convert.test.TestEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ParquetConvertApplication.class)
public class TestConvertTest {

    @Resource
    private TestComponent testComponent;

    @Test
    public void startTest() throws IOException {
        TestEntity testEntity = new TestEntity();
        testEntity.setIntValue(100);
        testEntity.setLongValue(200);
        testEntity.setDoubleValue(300);
        testEntity.setStringValue("测试");
        testEntity.setByteValue("不为空的值".getBytes(StandardCharsets.UTF_8));
        testComponent.javaWriteToParquet(testEntity);
    }

  生成Parquet文件的部分日志内容如下。

# Parquet文件结构信息
root
 |-- intValue: integer (nullable = true)
 |-- longValue: long (nullable = true)
 |-- doubleValue: double (nullable = true)
 |-- stringValue: string (nullable = true)
 |-- byteValue: binary (nullable = true)
 |-- byteNone: binary (nullable = true)

# Parquet文件内容信息
+--------+---------+-----------+-----------+--------------------+--------+
|intValue|longValue|doubleValue|stringValue|           byteValue|byteNone|
+--------+---------+-----------+-----------+--------------------+--------+
|     100|      200|      300.0|       测试|[E4 B8 8D E4 B8 B...|      []|
+--------+---------+-----------+-----------+--------------------+--------+

  这里有个疑问，比如byteNone字段就是个空值，怎么能让让展示的是null，当然TestEntity.setByteNone()。是肯定不行的。赋值只能在Group.add()的上入手。那好，干脆就不填这个值了，直接注释：

  运行后结果十分令人满意那就是报错。报错会大致出现两种，具体原因就不分析了。要不是字段顺序的问题，要不就是字段类型的原因导致有的报错是在文件生成时报错，有的是在解析时报错。但已经不重要了。重要的是说明当前定义的结构必须得有内容，也就是REQUIRED修饰的字段内容不能为空。

定义结构配置字段属性使用OPTIONAL
  有了之前的测试结果，这块就好测试了。接着上头继续修改，直接修改结果定义的内容。修改byteNone字段的定义为OPTIONAL。

  执行测试方法生成文件，部分日志内容如下：

# Parquet文件内容信息
+--------+---------+-----------+-----------+--------------------+--------+
|intValue|longValue|doubleValue|stringValue|           byteValue|byteNone|
+--------+---------+-----------+-----------+--------------------+--------+
|     100|      200|      300.0|       测试|[E4 B8 8D E4 B8 B...|    null|
+--------+---------+-----------+-----------+--------------------+--------+


  此处明显可以看到byteNone字段的内容由[ ]变为了null.。所以OPTIONAL修饰的字段内容就是可以为null，也就是0次或多次。

定义结构配置字段属性使用REPEATED
  这个就更容易看出区别了。此处多改几个字段使用REPEATED方式：

  生成文件查看文件结构信息和文件内容信息：

# Parquet文件结构信息
root
 |-- intValue: array (nullable = true)
 |    |-- element: integer (containsNull = true)
 |-- longValue: array (nullable = true)
 |    |-- element: long (containsNull = true)
 |-- doubleValue: array (nullable = true)
 |    |-- element: double (containsNull = true)
 |-- stringValue: string (nullable = true)
 |-- byteValue: binary (nullable = true)
 |-- byteNone: binary (nullable = true)

# Parquet文件内容信息
+--------+---------+-----------+-----------+--------------------+--------+
|intValue|longValue|doubleValue|stringValue|           byteValue|byteNone|
+--------+---------+-----------+-----------+--------------------+--------+
|   [100]|    [200]|    [300.0]|       测试|[E4 B8 8D E4 B8 B...|    null|
+--------+---------+-----------+-----------+--------------------+--------+

  好了到这里就十分清楚了，这就是数组嘛！REPEATED就是数组也就是0次或多次。

解析Parquet文件内容
  解析Parquet文件十分简单代码不多：

import org.apache.hadoop.fs.Path;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;

    public void javaReadParquet(String path) throws IOException {
        GroupReadSupport readSupport = new GroupReadSupport();
        ParquetReader.Builder<Group> reader = ParquetReader.builder(readSupport, new Path(path));
        ParquetReader<Group> build = reader.build();
        Group line;
        while ((line = build.read()) != null) {
            System.out.println(line.getInteger("intValue",0));
            System.out.println(line.getLong("longValue",0));
            System.out.println(line.getDouble("doubleValue",0));
            System.out.println(line.getString("stringValue",0));
            System.out.println(new String(line.getBinary("byteValue",0).getBytes()));
            System.out.println(new String(line.getBinary("byteNone",0).getBytes()));
        }
        build.close();
    }

  配置上之前的文件全路径，解析结果如下：

2021-05-25 14:54:21.721  INFO 4860 --- [           main] o.a.p.h.InternalParquetRecordReader      : RecordReader initialized will read a total of 1 records.
2021-05-25 14:54:21.722  INFO 4860 --- [           main] o.a.p.h.InternalParquetRecordReader      : at row 0. reading next block
2021-05-25 14:54:21.739  INFO 4860 --- [           main] org.apache.hadoop.io.compress.CodecPool  : Got brand-new decompressor [.snappy]
2021-05-25 14:54:21.743  INFO 4860 --- [           main] o.a.p.h.InternalParquetRecordReader      : block read in memory in 21 ms. row count = 1
100
200
300.0
测试
不为空的值
```

# 且谈 Apache Spark 的 API 三剑客：RDD、DataFrame 和 Dataset
```text
本文翻译自 A Tale of Three Apache Spark APIs: RDDs, DataFrames, and Datasets ，翻译已获得原作者 ** Jules S. Damji ** 的授权。

最令开发者们高兴的事莫过于有一组 API，可以大大提高开发者们的工作效率，容易使用、非常直观并且富有表现力。Apache Spark 广受开发者们欢迎的一个重要原因也在于它那些非常容易使用的 API，可以方便地通过多种语言，如 Scala、Java、Python 和 R 等来操作大数据集。

在本文中，我将深入讲讲 Apache Spark 2.2 以及以上版本提供的三种 API——RDD、DataFrame 和 Dataset，在什么情况下你该选用哪一种以及为什么，并概述它们的性能和优化点，列举那些应该使用 DataFrame 和 Dataset 而不是 RDD 的场景。我会更多地关注 DataFrame 和 Dataset，因为在 Apache Spark 2.0 中这两种 API 被整合起来了。

这次整合背后的动机在于我们希望可以让使用 Spark 变得更简单，方法就是减少你需要掌握的概念的数量，以及提供处理结构化数据的办法。在处理结构化数据时，Spark 可以像针对特定领域的语言所提供的能力一样，提供高级抽象和 API。

弹性分布式数据集（Resilient Distributed Dataset，RDD）
从一开始 RDD 就是 Spark 提供的面向用户的主要 API。从根本上来说，一个 RDD 就是你的数据的一个不可变的分布式元素集合，在集群中跨节点分布，可以通过若干提供了转换和处理的底层 API 进行并行处理。

在什么情况下使用 RDD？
下面是使用 RDD 的场景和常见案例：

你希望可以对你的数据集进行最基本的转换、处理和控制；
你的数据是非结构化的，比如流媒体或者字符流；
你想通过函数式编程而不是特定领域内的表达来处理你的数据；
你不希望像进行列式处理一样定义一个模式，通过名字或字段来处理或访问数据属性；
你并不在意通过 DataFrame 和 Dataset 进行结构化和半结构化数据处理所能获得的一些优化和性能上的好处；
Apache Spark 2.0 中的 RDD 有哪些改变？
可能你会问：RDD 是不是快要降级成二等公民了？是不是快要退出历史舞台了？

答案是非常坚决的：不！

而且，接下来你还将了解到，你可以通过简单的 API 方法调用在 DataFrame 或 Dataset 与 RDD 之间进行无缝切换，事实上 DataFrame 和 Dataset 也正是基于 RDD 提供的。

DataFrame
与 RDD 相似， DataFrame 也是数据的一个不可变分布式集合。但与 RDD 不同的是，数据都被组织到有名字的列中，就像关系型数据库中的表一样。设计 DataFrame 的目的就是要让对大型数据集的处理变得更简单，它让开发者可以为分布式的数据集指定一个模式，进行更高层次的抽象。它提供了特定领域内专用的 API 来处理你的分布式数据，并让更多的人可以更方便地使用 Spark，而不仅限于专业的数据工程师。

在我们的 Apache Spark 2.0 网络研讨会以及后续的博客中，我们提到在Spark 2.0 中，DataFrame 和 Dataset 的 API 将融合到一起，完成跨函数库的数据处理能力的整合。在整合完成之后，开发者们就不必再去学习或者记忆那么多的概念了，可以通过一套名为 Dataset 的高级并且类型安全的 API 完成工作。

(点击放大图像)



Dataset
如下面的表格所示，从 Spark 2.0 开始，Dataset 开始具有两种不同类型的 API 特征：有明确类型的 API 和无类型的 API。从概念上来说，你可以把 DataFrame 当作一些通用对象 Dataset[Row] 的集合的一个别名，而一行就是一个通用的无类型的 JVM 对象。与之形成对比，Dataset 就是一些有明确类型定义的 JVM 对象的集合，通过你在 Scala 中定义的 Case Class 或者 Java 中的 Class 来指定。

有类型和无类型的 API
语言

主要抽象

Scala

Dataset[T] & DataFrame (Dataset[Row] 的别名)

Java

Dataset[T]

Python

DataFrame

R

DataFrame

注意：因为 Python 和 R 没有编译时类型安全，所以我们只有称之为 DataFrame 的无类型 API。

Dataset API 的优点
在 Spark 2.0 里，DataFrame 和 Dataset 的统一 API 会为 Spark 开发者们带来许多方面的好处。

1、静态类型与运行时类型安全
从 SQL 的最小约束到 Dataset 的最严格约束，把静态类型和运行时安全想像成一个图谱。比如，如果你用的是 Spark SQL 的查询语句，要直到运行时你才会发现有语法错误（这样做代价很大），而如果你用的是 DataFrame 和 Dataset，你在编译时就可以捕获错误（这样就节省了开发者的时间和整体代价）。也就是说，当你在 DataFrame 中调用了 API 之外的函数时，编译器就可以发现这个错。不过，如果你使用了一个不存在的字段名字，那就要到运行时才能发现错误了。

图谱的另一端是最严格的 Dataset。因为 Dataset API 都是用 lambda 函数和 JVM 类型对象表示的，所有不匹配的类型参数都可以在编译时发现。而且在使用 Dataset 时，你的分析错误也会在编译时被发现，这样就节省了开发者的时间和代价。

所有这些最终都被解释成关于类型安全的图谱，内容就是你的 Spark 代码里的语法和分析错误。在图谱中，Dataset 是最严格的一端，但对于开发者来说也是效率最高的。

(点击放大图像)



2、关于结构化和半结构化数据的高级抽象和定制视图
把 DataFrame 当成 Dataset[Row] 的集合，就可以对你的半结构化数据有了一个结构化的定制视图。比如，假如你有个非常大量的用 JSON 格式表示的物联网设备事件数据集。因为 JSON 是半结构化的格式，那它就非常适合采用 Dataset 来作为强类型化的 Dataset[DeviceIoTData] 的集合。

复制代码
 
{"device_id": 198164, "device_name": "sensor-pad-198164owomcJZ", "ip": "80.55.20.25", "cca2": "PL", "cca3": "POL", "cn": "Poland", "latitude": 53.080000, "longitude": 18.620000, "scale": "Celsius", "temp": 21, "humidity": 65, "battery_level": 8, "c02_level": 1408, "lcd": "red", "timestamp" :1458081226051}
你可以用一个 Scala Case Class 来把每条 JSON 记录都表示为一条 DeviceIoTData，一个定制化的对象。

复制代码
 
case class DeviceIoTData (battery_level: Long, c02_level: Long, cca2: 
String, cca3: String, cn: String, device_id: Long, device_name: String, humidity: 
Long, ip: String, latitude: Double, lcd: String, longitude: Double, scale:String, temp: Long, timestamp: Long)
接下来，我们就可以从一个 JSON 文件中读入数据。

复制代码
 
// read the json file and create the dataset from the 
// case class DeviceIoTData
// ds is now a collection of JVM Scala objects DeviceIoTData
val ds = spark.read.json(“/databricks-public-datasets/data/iot/iot_devices.json”).as[DeviceIoTData]
上面的代码其实可以细分为三步：

Spark 读入 JSON，根据模式创建出一个 DataFrame 的集合；
在这时候，Spark 把你的数据用“DataFrame = Dataset[Row]”进行转换，变成一种通用行对象的集合，因为这时候它还不知道具体的类型；
然后，Spark 就可以按照类 DeviceIoTData 的定义，转换出“Dataset[Row] -> Dataset[DeviceIoTData]”这样特定类型的 Scala JVM 对象了。
许多和结构化数据打过交道的人都习惯于用列的模式查看和处理数据，或者访问对象中的某个特定属性。将 Dataset 作为一个有类型的 Dataset[ElementType] 对象的集合，你就可以非常自然地又得到编译时安全的特性，又为强类型的 JVM 对象获得定制的视图。而且你用上面的代码获得的强类型的 Dataset[T] 也可以非常容易地用高级方法展示或处理。

(点击放大图像)



3、方便易用的结构化 API
虽然结构化可能会限制你的 Spark 程序对数据的控制，但它却提供了丰富的语义，和方便易用的特定领域内的操作，后者可以被表示为高级结构。事实上，用 Dataset 的高级 API 可以完成大多数的计算。比如，它比用 RDD 数据行的数据字段进行 agg、select、sum、avg、map、filter 或 groupBy 等操作简单得多，只需要处理 Dataset 类型的 DeviceIoTData 对象即可。

用一套特定领域内的 API 来表达你的算法，比用 RDD 来进行关系代数运算简单得多。比如，下面的代码将用 filter() 和 map() 来创建另一个不可变 Dataset。

复制代码
 
// Use filter(), map(), groupBy() country, and compute avg() 
// for temperatures and humidity. This operation results in 
// another immutable Dataset. The query is simpler to read, 
// and expressive
val dsAvgTmp = ds.filter(d => {d.temp > 25}).map(d => (d.temp, d.humidity, d.cca3)).groupBy($"_3").avg()
//display the resulting dataset
display(dsAvgTmp)
(点击放大图像)



4、性能与优化
除了上述优点之外，你还要看到使用 DataFrame 和 Dataset API 带来的空间效率和性能提升。原因有如下两点：

首先，因为 DataFrame 和 Dataset API 都是基于 Spark SQL 引擎构建的，它使用 Catalyst 来生成优化后的逻辑和物理查询计划。所有 R、Java、Scala 或 Python 的 DataFrame/Dataset API，所有的关系型查询的底层使用的都是相同的代码优化器，因而会获得空间和速度上的效率。尽管有类型的 Dataset[T] API 是对数据处理任务优化过的，无类型的 Dataset[Row]（别名 DataFrame）却运行得更快，适合交互式分析。

(点击放大图像)



其次， Spark 作为一个编译器，它可以理解 Dataset 类型的 JVM 对象，它会使用编码器来把特定类型的 JVM 对象映射成 Tungsten 的内部内存表示。结果，Tungsten 的编码器就可以非常高效地将 JVM 对象序列化或反序列化，同时生成压缩字节码，这样执行效率就非常高了。

该什么时候使用 DataFrame 或 Dataset 呢？
如果你需要丰富的语义、高级抽象和特定领域专用的 API，那就使用 DataFrame 或 Dataset；
如果你的处理需要对半结构化数据进行高级处理，如 filter、map、aggregation、average、sum、SQL 查询、列式访问或使用 lambda 函数，那就使用 DataFrame 或 Dataset；
如果你想在编译时就有高度的类型安全，想要有类型的 JVM 对象，用上 Catalyst 优化，并得益于 Tungsten 生成的高效代码，那就使用 Dataset；
如果你想在不同的 Spark 库之间使用一致和简化的 API，那就使用 DataFrame 或 Dataset；
如果你是 R 语言使用者，就用 DataFrame；
如果你是 Python 语言使用者，就用 DataFrame，在需要更细致的控制时就退回去使用 RDD；
注意只需要简单地调用一下.rdd，就可以无缝地将 DataFrame 或 Dataset 转换成 RDD。例子如下：

复制代码
 
// select specific fields from the Dataset, apply a predicate
// using the where() method, convert to an RDD, and show first 10
// RDD rows
val deviceEventsDS = ds.select($"device_name", $"cca3", $"c02_level").where($"c02_level" > 1300)
// convert to RDDs and take the first 10 rows
val eventsRDD = deviceEventsDS.rdd.take(10)
(点击放大图像)



总结
总之，在什么时候该选用RDD、DataFrame 或Dataset 看起来好像挺明显。前者可以提供底层的功能和控制，后者支持定制的视图和结构，可以提供高级和特定领域的操作，节约空间并快速运行。

当我们回顾从早期版本的Spark 中获得的经验教训时，我们问自己该如何为开发者简化Spark 呢？该如何优化它，让它性能更高呢？我们决定把底层的RDD API 进行高级抽象，成为DataFrame 和Dataset，用它们在Catalyst 优化器和Tungsten 之上构建跨库的一致数据抽象。

DataFrame 和 Dataset，或 RDD API，按你的实际需要和场景选一个来用吧，当你像大多数开发者一样对数据进行结构化或半结构化的处理时，我不会有丝毫惊讶。
```

# Java api kerberos java api kerberos认证需要什么

```text
用过hbase的朋友可能都有过这样的疑问，我写一个java client，好像就提供了zookeeper quorum地址就连上hbase了，那么是不是存在安全问题？的确是，如何解决？hbase中引入了kerberos认证。我准备用两篇博文介绍hbase + kerberos的相关内容，本篇主要介绍kerberos的配置。

环境准备
这里我准备了三台server，各自安装上centos 6.5 64bit

kb1: kerberos server
kbhbase1: kerberos client, 后续也用于安装运行HBase
kbjavatest1: kerberos client, 后续将在其上部署java程序访问kbhbase1上的hbase数据库
kerberos简介
kerberos简单来说就是一套完全控制机制，它有一个中心服务器（KDC），KDC中有数据库，你可以往里添加各种“人”以及各种“服务”的“身份证”，当某个人要访问某个服务时，他拿着自己的“身份证”联系KDC并告诉KDC他想要访问的服务，KDC经过一系列验证步骤，最终依据验证结果允许/拒绝这个人访问此服务。关于kerberos具体的工作流程，参见文章 《Explain like I’m 5: Kerberos》

kerberos server配置
安装

#yum install krb5-libs krb5-server krb5-workstation

配置

1）#vim /etc/krb5.conf

  View Code

2）#vim /var/kerberos/krb5kdc/kdc.conf

  View Code

kerberos中的realm，即kerberos的域，类似于计算机网络中"domain"的概念。

3）#vim /var/kerberos/krb5kdc/kadm5.acl

这个文件是用来控制哪些人可以使用kadmin工具来管理kerberos数据库，我这里就配了一行：

*/admin@MH.COM *

其中前一个*号是通配符，表示像名为“abc/admin”或“xxx/admin”的人都可以使用此工具（远程或本地）管理kerberos数据库，后一个*跟权限有关，*表示所有权限，还可以进行更细的控制，参见 ACL。

4）#kdb5_util create -s
初始化一个kerberos数据库。

5）现在数据库是空的，想要使用kadmin添加一个人到数据库中，这是需要权限的，那么最开始的那一个人是怎么加到数据库中的？这就需要kadmin.local这个工具，这个工具只能在kerberos server上执行（类似于oracle中的sys用户无密码登录）。
#kadmin.local -q "addprinc admin/admin"
我这里把管理员叫“admin/admin”，你可以叫任何名字，但是因为此前我们在kadm5.acl中的配置，名字必须以/admin结尾。过程中会提示你输入两次密码，记住这个密码，当你在别的机器连接kadmin时，需要这个密码。

启动

#service krb5kdc start
#service kadmin start
#chkconfig krb5kdc on
#chkconfig kadmin on
验证

#kinit admin/admin
如果kinit不带参数，则会默认以当前操作系统用户名，比如root，作为名称。因为root在kerberos的数据库中并没有，所以会提示失败

#klist
正常应该显示：

Ticket cache: FILE:/tmp/krb5cc_0

Default principal: admin/admin@MH.COM

Valid starting     Expires            Service principal

04/10/15 13:03:36  04/11/15 13:03:36  krbtgt/MH.COM@MH.COM

        renew until 04/10/15 13:03:36

以下这两个应该不是必须的，与兼容性有关，ktadd命令会把“身份证”写入到文件（.keytab后缀），可以指定keytab文件，如不指定，默认写入/etc/krb5.keytab

kadmin.local: ktadd kadmin/admin
kadmin.local: ktadd kadmin/changepw
其它一些命令

#kdestroy，退出当前kerberos用户，即你最后使用kinit过的那个用户
kadmin.local>listprincs 列出所有存在数据库中的人或服务
kadmin.local>delprinc zookeeper/kbhbase1.mh.com@MH.COM 删除人或服务
kadmin.local>addprinc admin/admin 添加人或服务
kadmin.local>q 退出kadmin
kadmin:  addprinc -randkey root/kbhbase1.mh@MH.COM
kadmin:  xst -k root.keytab root/kbhbase1.mh.com
# klist -kt root.keytab 列出这个keytab中保存的所有人或服务
一般在实例使用中通过kinit的方式较少，因为每次都要输入密码，所以更经常使用的是keytab文件，相当于为某个人或服务生成一个密码，并放在文件中，程序中则指向这个keytab，不用每次都输入密码。

kerberos client配置
现在，我们到kbhbase1这台机器上，即我们的kerberos 客户端上。kerberos对服务器跟客户端之间的时间有要求，所以一般需要安装ntp来作时间同步，我这里直接手工同步了一下，就不再介绍ntp了。 #yum install krb5-libs krb5-workstation 注意与kerberos server上的yum命令对比。安装完成后把server上的krb5.conf文件拷贝过来覆盖一下即可。注意：

kb1和kbhbase1的/etc/hosts文件都要把各自以及对方添加上去
关闭这两个server上的防火墙
#service iptables stop
#chkconfig iptables off
与在server上类似，使用kinit和klist查看一下。也可以尝试执行kadmin，按提示使用admin/admin并输入密码（你之前创建管理员时输入的密码），正常应该能够连接上服务器，并对数据库进行操作（比如添加或删除人、服务）。

 

最近有这个需求，因为kerberos认证，一直连不上，通过很多大佬的帮忙，终于成功连上，在此分享下自己的一点点收获

话不多说，

1.需要hbase-site.xml这个配置文件，里面有很多配置属性是需要的，需要将这个文件放到根目录下；很多属性因为在这个配置文件里有了，所以不用设置，但是你设置了，会以你设置的为准，这里面的只是初始化。

2.keytab文件

3.krb5.conf文件

private static final String KEYTAB_FILE =CLASS_LOADER.getResource("kerberos/smokeuser.headless.keytab").getPath();
  
 private static final String KERB_CONF =CLASS_LOADER.getResource("kerberos/krb5.conf").getPath();
     private static Connection connection =null;
     private static Admin admin =null;
     public static List<String> list=null;
  
 static {
         System.setProperty("java.security.krb5.conf", KERB_CONF);
         System.setProperty("sun.security.krb5.debug", "true");
  
         final Configuration conf = HBaseConfiguration.create();
         conf.set("hbase.zookeeper.quorum", "zookenode1,zookenode2,zookenode3");
         conf.set("hbase.zookeeper.property.clientPort", "2181");
         conf.set("hadoop.security.authentication", "kerberos");
         conf.set("hbase.security.authentication", "kerberos");
         conf.set("zookeeper.znode.parent", "/hbase-secure");
         conf.set("hbase.master.kerberos.principal", "hbase/xxxx1@xxx.COM");
         UserGroupInformation.setConfiguration(conf);
         try {
             UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI("xxxx2@xxx.COM", KEYTAB_FILE);
             UserGroupInformation.setLoginUser(ugi);
             HBaseAdmin.available(conf);
             connection = ConnectionFactory.createConnection(conf);
             admin = connection.getAdmin();
         } catch (Exception e) {
             e.printStackTrace();
         }
     }
注意 ：principal的值hbase/xxxx1@xxx.COM为主hbase主节点；keytab文件和xxxx2@xxx.COM需要对应；我这里用的hbase不是1.xx版本的，是2.xx版本的，所以是 HBaseAdmin.available(conf);方法；

到此，应该没有多大问题了，可以通过admin和connection愉快的操作hbase了。
 
```

# Kerberos 协议和 KDC 实现 Apache Kerby

https://github.com/apache/directory-kerby?tab=readme-ov-file

https://directory.apache.org/kerby/downloads.html

```text
Apache Kerby 详细介绍

Apache Kerby™ 是 Java Kerberos 绑定，提供了一个丰富，直观和可互操作的实现，库，KDC，各种集成了 PKI，OTP 和令盘（OAuth2）的基础设施。Apache Kerby 提供了现代化环境（云，Hadoop 和移动端）需要的功能。

主要目标：
提供另一个 Java 实现的 Kerberos 服务器
 
提供可以与任意 KDC 服务器交互的客户端 API
 
提供一个可嵌入和独立的 KDC 服务器，支持各种后端
 
使用内存，Mavibot(MVCC BTree), JSON, LDAP 和 Zookeeper 后端来存储数据
 
Embedded KDC server allows easy integration into products for unit testing or production deployment.
 
支持 FAST/Preauthentication 框架
 
支持 PKINIT 机制
 
支持 Token Preauth 机制
 
支持 OTP 机制
 
提供对 JAAS, GSSAPI 和 SASL 框架的支持
 
极小的依赖，核心部分只依赖于 SLF4J
```

# 集成Kerberos的Kudu(1.10.0)访问指南
```text
Kudu作为一款开源的列式存储系统，提供了高性能的随机读/写和快速的分析查询能力。为了增强安全性，许多Kudu部署都集成了Kerberos进行身份验证。本文将指导您如何使用Java集成Kerberos进行Kudu(1.10.0)的访问。

步骤一：Kerberos验证

首先，您需要进行Kerberos验证。这通常涉及到将相应用户的keytab文件引入本地。以下是一个简单的Java代码示例，用于初始化Kerberos环境：

public class KuduKerberosAuth {
    /**
     * 初始化访问Kerberos访问
     * @param debug 是否启用Kerberos的Debug模式
     */
    public static void initKerberosENV(Boolean debug) {
        try {
            System.setProperty("java.security.krb5.conf", "krb5.conf");
            if (debug) {
                System.setProperty("sun.security.krb5.debug", "true");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
在上面的代码中，我们设置了java.security.krb5.conf属性指向krb5.conf文件，该文件包含了Kerberos的配置信息。如果需要启用Debug模式，我们还将sun.security.krb5.debug属性设置为true。

步骤二：Maven依赖

接下来，您需要在项目中引入Kudu和Kerberos的Maven依赖。请确保您的pom.xml文件中包含了以下依赖：

<dependencies>
    <!-- Kudu Java Client -->
    <dependency>
        <groupId>org.apache.kudu</groupId>
        <artifactId>kudu-client</artifactId>
        <version>1.10.0</version>
    </dependency>
    <!-- Kerberos libraries -->
    <dependency>
        <groupId>org.apache.kerby</groupId>
        <artifactId>kerb-core</artifactId>
        <version>1.10.1</version>
    </dependency>
    <dependency>
        <groupId>org.apache.kerby</groupId>
        <artifactId>kerb-client</artifactId>
        <version>1.10.1</version>
    </dependency>
</dependencies>
步骤三：引入Hadoop配置文件

最后，您需要引入Hadoop的配置文件，特别是core-site.xml。您需要将这个文件放到您的项目的resources文件夹中。core-site.xml文件应该包含Kudu和Kerberos的相关配置。以下是一个示例配置：

<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <!-- Kudu settings -->
  <property>
    <name>kudu.master</name>
    <value>kudu_master_host:7051</value>
  </property>
  <!-- Kerberos settings -->
  <property>
    <name>java.security.krb5.conf</name>
    <value>krb5.conf</value>
  </property>
</configuration>
在上面的配置中，我们设置了kudu的master主机地址为kudu_master_host:7051。对于Kerberos的设置，我们指定了Java应该使用krb5.conf文件作为Kerberos配置文件。

完成以上步骤后，您就可以使用Java代码来访问Kudu了。请确保在运行代码之前已经正确地初始化了Kerberos环境，并且已经引入了所有必要的依赖和配置文件。这只是一个基本的指南，实际部署中可能还需要进行其他配置和优化。建议您参考Kudu和Kerberos的官方文档以获取更详细的信息和最佳实践。
```


# 【大数据安全】基于Kerberos的大数据安全方案
https://cloud.tencent.com/developer/article/1360555
```text
1.背景
互联网从来就不是一个安全的地方。很多时候我们过分依赖防火墙来解决安全的问题，不幸的是，防火墙是假设“坏人”是来自外部的，而真正具有破坏性的攻击事件都是往往都是来自于内部的。

近几年，在thehackernews等网站上总会时不时的看到可以看到一些因为数据安全问题被大面积攻击、勒索的事件。在Hadoop1.0.0之前，Hadoop并不提供对安全的支持，默认集群内所有角色都是可靠的。用户访问时不需要进行任何验证,++导致++恶意用户很容易就可以伪装进入集群进行破坏。

不安全的Hadoop集群
不安全的Hadoop集群
要保证Hadoop集群的安全，至少要做到2个A：Authentication(认证)，Authorization(授权)。常见的方案有：

Authentication：
MIT Kerberos, Azure AD, Kerby
Authorization：
Apache Sentry(Cloudera), Apache Ranger(Hortonworks)
Hadoop Cluster Secure
Hadoop Cluster Secure
Hadoop集群对Kerberos的支持
2012年1.0.0版本正式发布后，Hadoop才增加了对Kerberos的支持, 使得集群中的节点是可信任的。

Kerberos可以将认证的密钥在集群部署时事先放到可靠的节点上。集群运行时，集群内的节点使用密钥得到认证，认证通过后的节点才能提供服务。企图冒充的节点由于没有事先得到的密钥信息，无法与集群内部的节点通信。这样就防止了恶意地使用或篡改Hadoop集群的问题，确保了Hadoop集群的可靠性、安全性。

2.Kerberos介绍
Kerberos是种网络身份验证协议,最初设计是用来保护雅典娜工程的网络服务器。Kerberos这个名字源于希腊神话，是一只三头犬的名字，它旨在通过使用密钥加密技术为Client/Server序提供强身份验证。可以用于防止窃听、防止重放攻击、保护数据完整性等场合，是一种应用对称密钥体制进行密钥管理的系统。Kerberos的扩展产品也使用公开密钥加密方法进行认证。

Kerberos目前最新版本是5，1~3版本只在MIT内部发行，因为使用DES加密，早期被美国出口管制局列为军需品禁止出口，直到瑞典皇家工学院实现了Kerberos版本4，KTH-KRB。后续也是这个团队实现了版本5: Heimdal，目前常见的Kerberos5实现之一。

本文中讨论的Kerberos5实现版本为MIT Kerberos，MIT保持的大约半年左右一次的更新速度，目前最新版本是2018-11-01发布的1.16.2版本。

2.1 名词解释
AS（Authentication Server）：认证服务器
KDC（Key Distribution Center）：密钥分发中心
TGT（Ticket Granting Ticket）：票据授权票据，票据的票据
TGS（Ticket Granting Server）：票据授权服务器
SS（Service Server）：特定服务提供端
Principal：被认证的个体
Ticket：票据，客户端用来证明身份真实性。包含：用户名，IP，时间戳，有效期，会话秘钥。
使用Kerberos时，一个客户端需要经过三个步骤来获取服务: 

认证: 客户端向认证服务器发送一条报文，获取一个包含时间戳的TGT。  
授权: 客户端使用TGT向TGS请求指定Service的Ticket。 
服务请求:   客户端向指定的Service出示服务Ticket鉴权通讯。
Kerberos协议在网络通信协定中属于显示层。其通信流程简单地说，用户先用共享密钥从某认证服务器得到一个身份证明。随后，用户使用这个身份证明与SS通信，而不使用共享密钥。

2.2 具体通信流程
①此流程使用了对称加密; ②此流程发生在某一个Kerberos领域中； ③小写字母c,d,e,g是客户端发出的消息，大写字母A,B,E,F,H是各个服务器发回的消息。

首先，用户使用客户端上的程序进行登录：

输入用户ID和密码到客户端（或使用keytab登录）。
客户端程序运行一个单向函数（大多数为Hash）把密码转换成密钥，这个就是客户端的“用户密钥”(user's secret key)。
2.2.1 客户端认证（Kinit）
客户端(Client)从认证服务器(AS)获取票据的票据（TGT）。

客户端认证
客户端认证
Client向AS发送1条明文消息，申请基于该用户所应享有的服务，例如“用户Sunny想请求服务”（Sunny是用户ID）。（注意：用户不向AS发送“用户密钥”(user's secret key)，也不发送密码）该AS能够从本地数据库中查询到该申请用户的密码，并通过相同途径转换成相同的“用户密钥”(user's secret key)。
AS检查该用户ID是否在于本地数据库中，如果用户存在则返回2条消息：
【消息A】：Client/TGS会话密钥(Client/TGS Session Key)（该Session Key用在将来Client与TGS的通信（会话）上），通过 用户密钥(user's secret key) 进行加密。
【消息B】：票据授权票据(TGT)（TGT包括：消息A中的“Client/TGS会话密钥”(Client/TGS Session Key)，用户ID，用户网址，TGT有效期），通过TGS密钥(TGS's secret key) 进行加密。
一旦Client收到消息A和消息B，Client首先尝试用自己的“用户密钥”(user's secret key)解密消息A，如果用户输入的密码与AS数据库中的密码不符，则不能成功解密消息A。输入正确的密码并通过随之生成的"user's secret key"才能解密消息A，从而得到“Client/TGS会话密钥”(Client/TGS Session Key)。（注意：Client不能解密消息B，因为B是用TGS密钥(TGS's secret key)加密的）。拥有了“Client/TGS会话密钥”(Client/TGS Session Key)，Client就足以通过TGS进行认证了。
2.2.2 服务授权
Client从TGS获取票据(client-to-server ticket)。

服务授权
服务授权
当client需要申请特定服务时，其向TGS发送以下2条消息：
【消息c】：即消息B的内容（TGS's secret key加密后的TGT），和想获取的服务的服务ID（注意：不是用户ID）。
【消息d】：认证符(Authenticator)（Authenticator包括：用户ID，时间戳），通过Client/TGS会话密钥(Client/TGS Session Key)进行加密。
收到消息c和消息d后，TGS首先检查KDC数据库中是否存在所需的服务，查找到之后，TGS用自己的“TGS密钥”(TGS's secret key)解密消息c中的消息B（也就是TGT），从而得到之前生成的“Client/TGS会话密钥”(Client/TGS Session Key)。TGS再用这个Session Key解密消息d得到包含用户ID和时间戳的Authenticator，并对TGT和Authenticator进行验证，验证通过之后返回2条消息：
【消息E】：client-server票据(client-to-server ticket)（该ticket包括：Client/SS会话密钥 (Client/Server Session Key），用户ID，用户网址，有效期），通过提供该服务的服务器密钥(service's secret key) 进行加密。
【消息F】：Client/SS会话密钥( Client/Server Session Key) （该Session Key用在将来Client与Server Service的通信（会话）上），通过Client/TGS会话密钥(Client/TGS Session Key) 进行加密。
Client收到这些消息后，用“Client/TGS会话密钥”(Client/TGS Session Key)解密消息F，得到“Client/SS会话密钥”(Client/Server Session Key)。（注意：Client不能解密消息E，因为E是用“服务器密钥”(service's secret key)加密的）。
2.2.3 服务请求
Client从SS获取服务。

当获得“Client/SS会话密钥”(Client/Server Session Key)之后，Client就能够使用服务器提供的服务了。Client向指定服务器SS发出2条消息：
【消息e】：即上一步中的消息E“client-server票据”(client-to-server ticket)，通过服务器密钥(service's secret key) 进行加密
【消息g】：新的Authenticator（包括：用户ID，时间戳），通过Client/SS会话密钥(Client/Server Session Key) 进行加密
SS用自己的密钥(service's secret key)解密消息e从而得到TGS提供的Client/SS会话密钥(Client/Server Session Key)。再用这个会话密钥解密消息g得到Authenticator，（同TGS一样）对Ticket和Authenticator进行验证，验证通过则返回1条消息（确认函：确证身份真实，乐于提供服务）。
【消息H】：新时间戳（新时间戳是：Client发送的时间戳加1，v5已经取消这一做法），通过Client/SS会话密钥(Client/Server Session Key) 进行加密。
Client通过Client/SS会话密钥(Client/Server Session Key)解密消息H，得到新时间戳并验证其是否正确。验证通过的话则客户端可以信赖服务器，并向服务器（SS）发送服务请求。
服务器（SS）向客户端提供相应的服务。
3.Kerberos HA架构
Kerberos支持两种服务器在域内冗余方式：Master/Slave（MIT和Heimdal）和Multimaster结构（Windows Active Directory）。在生产环境中部署Kerberos时，最好使用一主(Master)多从(Slave)的架构，以确保Kerberos服务的高可用性。

Kerberos中每个KDC都包含数据库的副本。主KDC包含域（Realm）数据库的可写副本，它以固定的时间间隔复制到从KDC中。所有数据库更改（例如密码更改）都在主KDC上进行，当主KDC不可用时，从KDC提供Kerberos票据给服务授权，但不提供数据库管理。KDC需要一个Admin来进行日常的管理操作。

Kerberos的同步机制只复制主数据库的内容，但不传递配置文件，以下文件必须手动复制到每个Slave中：

代码语言：txt
复制
- krb5.conf
- kdc.conf
- kadm5.acl
- master key stash file
3.1 HA方案
目前单机房HA方案使用的较多的是Keepalived + Rsync 。Keepalived可以将多个无状态的单点通过虚拟IP(以下称为VIP)漂移的方式搭建成一个高可用服务。

首先，在Master KDC中创建数据库的dump文件(将当前的Kerberos和KADM5数据库转储为ASCII文件)：

代码语言：txt
复制
kdb5_util dump [-b7|-ov|-r13] [-verbose] [-mkey_convert] [-new_mkey_file mkey_file] [-rev] [-recurse] [filename [principals...]]
然后使用Rsync将目录同步到Slave机器的对应目录中，

再导入KDC中：

代码语言：txt
复制
kdb5_util load [-b7|-ov|-r13] [-hash] [-verbose] [-update] filename [dbname]
Hadoop所有请求通过请求内网域名，解析到Keepalived绑定的VIP的方式来使用KDC:

Kerberos HA
Kerberos HA
4. 优化和展望
4.1 优化
（1）用户（Principal）管理
如果团队中已经有一套权限系统，要将现有的身份系统集成到Kerberos中会很困难。

随着业务的飞速增长，服务器规模越来越大，Kerberos Principal手动操作会越来越频繁，手动的增删改查维护会非常痛苦。需要在Kerberos管理系统中规范Principal申请、维护、删除、keytab生成流程。Principal申请和权限管理自动化。

（2）数据同步优化
Kerberos数据同步可以将生成的数据记录同步写入到MySQL中，使用MySQL双主同步方式。在跨机房环境中，KDC数据使用Rsync工具进行增量同步。以A核心机房作为主机房，Rsync Server使用了Keepalived VIP的方式，当Kerberos主机宕机后，VIP漂移到另外一台主机器上，Rsync Client会以VIP所在的KDC主机器为Rsync Server进行数据同步，以保证KDC数据同步的高可用性。

（3）运维
使用进程管理工具对Kerberos相关进程进行存活监控，当发现有进程异常退出时，邮件/微信/钉钉报警，主动再次拉起进程。

4.2 展望
部署过Kerberos的同学都知道，在Hadoop集群部署Kerberos实际是一项非常繁琐的工作。Kerberos本质上是一种协议或安全通道，对于大多数用户或普通用户来说，是有一定学习曲线的，是否有更好的实现能够对普通用户隐藏这些繁琐的细节。

阿里和Intel合作项目Hadoop Authentication Service (HAS) 据称目前已经应用到ApsaraDB for HBase2.0中:

HAS
HAS
HAS方案使用Kerby替代MIT Kerberos服务，利用HAS插件式验证方式建立一套人们习惯的账户密码体系。

目前HAS在Apache Kerby项目has-project分支开发中，未来会作为Kerbby的新feature出现在下一次release中。

Apache Kerby作为Apache Directory的一个子项目，目前关注度并不高，让我们期待它在后续的发展吧。
```