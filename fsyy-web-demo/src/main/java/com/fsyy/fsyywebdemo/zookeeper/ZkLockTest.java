package com.fsyy.fsyywebdemo.zookeeper;

import org.apache.zookeeper.ZooKeeper;

public class ZkLockTest {

    public static void main(String[] args)throws Exception {
        ZooKeeper zooKeeper = ZKUtils.getZooKeeper();
        //模拟多线程请求
        for (int i = 0; i < 5; i++) {
            String threadName = "LockThread-"+i;
            new Thread(()->{
                ZKLockUtils lockUtils = new ZKLockUtils();
                lockUtils.setZooKeeper(zooKeeper);
                lockUtils.setThreadName(threadName);
                //加锁
                lockUtils.lock();
                System.out.println(Thread.currentThread().getName()+"正在执行任务");
                //模拟执行任务
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //解锁
                System.out.println(Thread.currentThread().getName()+"执行完成");
                lockUtils.ulock();
            },threadName).start();
        }
    }
}
