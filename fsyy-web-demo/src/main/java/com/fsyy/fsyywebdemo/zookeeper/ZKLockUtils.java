package com.fsyy.fsyywebdemo.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKLockUtils implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback, AsyncCallback.Children2Callback, AsyncCallback.StringCallback {

    /**
     * 这里可通过set方法或者构造方法，传入zooKeeper，
     */
    private ZooKeeper zooKeeper;

    /**
     * 当前节点的path
     */
    private String pathName;

    /**
     * 当前线程的名字，便于查看
     */
    private String threadName;

    /**
     * 用于获取不到锁时候阻塞
     */
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * v 1.0
     * 加锁方法
     * 基础版本，功能实现了，后续再进行优化吧
     */
    public  void lock(){
        /**
         * 思路.....
         * 1、在锁目录下创建自己的节点，临时有序节点
         * 2、获取所有的孩子节点、判断自己是不是第一个
         * 3、如果自己是第一个，则加锁成功，执行业务代码
         * 4、如果自己不是第一个，watch自己的前一个节点
         * 5、当第一个节点，也就是获取锁的执行完之后，删除自己的节点
         * 6、第二个就能监听到，从而继续执行获取所有孩子节点，判断自己是不是第一个的操作
         */
        try {
            zooKeeper.create("/lock", "lock".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,this,threadName);
            //当前线程阻塞，进行抢锁
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 解锁方法
     * 执行完业务后，删除掉自己的节点即可 version为-1  忽略数据版本
     */
    public  void ulock(){
        //删除自己的节点
        try {
            zooKeeper.delete(pathName,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }


    /**
     * Children2Callback 接口
     * 获取节点下所有孩子
     * 实现分布式锁的核心点
     * @param rc
     * @param path
     * @param ctx
     * @param children
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        if(children != null && children.size()>0){
            //对节点进行排序
            Collections.sort(children);
            String currentPath = pathName.substring(1);
            //查询自己是第几个
            int index = children.indexOf(currentPath);
            //判断自己是不是第一个
            if(index<1){
                try {
                    //如果自己是第一个，则认为抢到了锁
                    System.out.println(threadName+"抢到锁了..");
                    zooKeeper.setData("/",threadName.getBytes(),-1);
                    countDownLatch.countDown();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                //只监听自己的前一个
                zooKeeper.exists("/"+children.get(index-1),this,this,"abc");
            }
        }
    }

    /**
     * 节点创建成功时的回调
     * @param rc
     * @param path
     * @param ctx
     * @param name
     */
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        pathName = name;
        System.out.println(threadName+"-节点创建成功："+pathName);
        //处的watch为false,表示不需要对根节点下的所有节点进行watch，我们只需要监听自己的前一个即可
        zooKeeper.getChildren("/",false,this,"abc");
    }

    /**
     * DataCallback接口
     * 当getdata有数据时的回调
     * @param rc
     * @param path
     * @param ctx
     * @param data
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        //TODO
    }

    /**
     * StatCallback接口
     * 判断节点是否存在时的回调
     * @param rc
     * @param path
     * @param ctx
     * @param stat
     */
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        //TODO
        /*if(stat != null){
            zooKeeper.getData("/lock",this,this,"abc");
        }*/
    }

    /**
     * Watcher 接口
     * 节点的事件回调
     * @param event
     */
    @Override
    public void process(WatchedEvent event) {
        Event.EventType eventType = event.getType();
        String path = event.getPath();
        switch (eventType) {
            case None:
                break;
            case NodeCreated:
                System.out.println("节点被创建...");
                break;
            case NodeDeleted:
                //当前一个节点被删除，判断自己是不是第一个
                System.out.println(path+"-节点被删除...");
                //执行获取所有孩子节点的操作
                zooKeeper.getChildren("/",false,this,"abc");
                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            /*case PersistentWatchRemoved:
                break;*/
        }
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }


}