package com.fsyy.fsyywebdemo.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

//使用Curator框架实现分布式锁
public class CuratorLockTest {
    private CuratorFramework curatorFramework;

    //定义锁节点的路径
    private String node = "/locks";
    //设置zookeeper连接
    private final String connectString = "zoo1:2181,zoo2:2181,zoo3:2181";
    //设置超时时间
    private final int sessionTimeout = 2000;
    //设置超时时间
    private final int connectionTimeout = 3000;

    public static void main(String[] args) {
        new CuratorLockTest().test();
    }

    private void test() {
        //创建分布式锁1
        final InterProcessLock lock1 = new InterProcessMutex(getCuratorFramework(), node);

        //创建分布式锁2
        // final InterProcessLock lock2 = new InterProcessMutex(getCuratorFramework(), node);
        final InterProcessLock lock2 = lock1;

        /*//创建线程
        //线程1
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //线程加锁
                    lock1.acquire();
                    System.out.println("线程1获取锁");

                    //线程沉睡
                    Thread.sleep(5*1000);

                    //线程解锁
                    lock1.release();
                    System.out.println("线程1释放了锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

        //线程2
        new Thread(new Runnable() {
            @Override
            public void run() {
                //线程加锁
                try {
                    lock2.acquire();
                    System.out.println("线程2获取到锁");

                    //线程沉睡
                    Thread.sleep(5*1000);

                    lock2.release();
                    System.out.println("线程2释放锁");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();*/

        for(int i = 0; i < 20; i++){
            int j = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //线程加锁
                        lock1.acquire();
                        System.out.println("线程" + j + "获取锁");

                        //线程沉睡
                        Thread.sleep(1*1000);
                        try {
                            lock1.acquire();
                            System.out.println("重入线程" + j + "获取锁");

                            //线程沉睡
                            Thread.sleep(1*1000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            //线程解锁
                            lock1.release();
                            System.out.println("重入线程" + j + "释放了锁");
                        }

                        //线程解锁
                        lock1.release();
                        System.out.println("线程" + j + "释放了锁");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    //对分布式锁进行初始化
    private CuratorFramework getCuratorFramework() {
        synchronized (this){
            if(curatorFramework == null){
                //重试策略,定义初试时间3s,重试3次
                ExponentialBackoffRetry exponentialBackoffRetry = new ExponentialBackoffRetry(3000, 3);

                //初始化客户端
                CuratorFramework client = CuratorFrameworkFactory.builder()
                        .connectString(connectString)
                        .sessionTimeoutMs(sessionTimeout)
                        .connectionTimeoutMs(connectionTimeout)
                        .retryPolicy(exponentialBackoffRetry)
                        .build();

                //开启连接
                client.start();
                System.out.println("zookeeper 初始化完成...");
                curatorFramework = client;
            }
        }

        return curatorFramework;
    }
}

