package com.fsyy.fsyywebdemo.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public class Test {
    /*public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183", 2000, null);
        zooKeeper.create("/hello", "编程易行".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("");
        Thread.sleep(10 * 1000);
        System.out.println(zooKeeper.getState());
        System.out.println(zooKeeper.getSessionTimeout());
        zooKeeper.close();
        Thread.sleep(10 * 1000);
        System.out.println(zooKeeper.getState());
        System.out.println(zooKeeper.getSessionTimeout());
    }*/

    public static void main(String[] args) {
        String zookeeperConnectionString = "zoo1:2181,zoo2:2182,zoo3:2183";
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
        client.start();

        try {
//创建分布式锁, 锁空间的根节点路径为/curator/lock
            InterProcessMutex lock = new InterProcessMutex(client, "/curator/lock");
            if ( lock.acquire(1000, TimeUnit.SECONDS) )
            {
                try
                {
// do some work inside of the critical section here
                    System.out.println("do some work inside of the critical section here");
                }
                finally
                {
//完成业务流程, 释放锁
                    lock.release();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
