文末也有参考网址。
Java实现5种限流算法及7种限流方式
其它参考：https://www.jb51.net/article/257674.htm


Java 实现滑动时间窗口限流算法，你见过吗？

在网上搜滑动时间窗口限流算法，大多都太复杂了，本人实现了个简单的，先上代码：

package cn.dijia478.util;
 
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
 
/**
 * 滑动时间窗口限流工具
 * 本限流工具只适用于单机版，如果想要做全局限流，可以按本程序的思想，用redis的List结构去实现
 *
 * @author dijia478
 * @date 2020-10-13 10:53
 */
public class SlideWindow {
 
    /** 队列id和队列的映射关系，队列里面存储的是每一次通过时候的时间戳，这样可以使得程序里有多个限流队列 */
    private volatile static Map<String, List<Long>> MAP = new ConcurrentHashMap<>();
    private SlideWindow() {}
 
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            // 任意10秒内，只允许2次通过
            System.out.println(LocalTime.now().toString() + SlideWindow.isGo("ListId", 2, 10000L));
            // 睡眠0-10秒
            Thread.sleep(1000 * new Random().nextInt(10));
        }
    }
 
    /**
     * 滑动时间窗口限流算法
     * 在指定时间窗口，指定限制次数内，是否允许通过
     *
     * @param listId     队列id
     * @param count      限制次数
     * @param timeWindow 时间窗口大小
     * @return 是否允许通过
     */
    public static synchronized boolean isGo(String listId, int count, long timeWindow) {
        // 获取当前时间
        long nowTime = System.currentTimeMillis();
        // 根据队列id，取出对应的限流队列，若没有则创建
        List<Long> list = MAP.computeIfAbsent(listId, k -> new LinkedList<>());
        // 如果队列还没满，则允许通过，并添加当前时间戳到队列开始位置
        if (list.size() < count) {
            list.add(0, nowTime);
            return true;
        }
 
        // 队列已满（达到限制次数），则获取队列中最早添加的时间戳
        Long farTime = list.get(count - 1);
        // 用当前时间戳 减去 最早添加的时间戳
        if (nowTime - farTime <= timeWindow) {
            // 若结果小于等于timeWindow，则说明在timeWindow内，通过的次数大于count
            // 不允许通过
            return false;
        } else {
            // 若结果大于timeWindow，则说明在timeWindow内，通过的次数小于等于count
            // 允许通过，并删除最早添加的时间戳，将当前时间添加到队列开始位置
            list.remove(count - 1);
            list.add(0, nowTime);
            return true;
        }
    }
 
}


运行可以看到，任意10秒内，通过的次数不超过2次。

或者按照实现原理来说，任意通过2次内的时间差，都不超过10秒：



这里画图做说明，为什么这样可以做到滑动窗口限流，假设10秒内允许通过5次

1.这条线就是队列list，当第一个事件进来，队列大小是0，时间是第1秒：



2.因为size=0，小于5，都没有到限制的次数，完全不用考虑时间窗口，直接把这次事件的时间戳放到0的位置：



3.第2.8秒的时候，第二个事件来了。

因为此时size=1，还是小于5，把这次事件的时间戳放到0的位置，原来第1秒来的事件时间戳会往后移动一格：



4.陆续的又来了3个事件，队列大小变成了5，先来的时间戳依次向后移动。

此时，第6个事件来了，时间是第8秒：



5.因为size=5，不小于5，此时已经达到限制次数，以后都需要考虑时间窗口了。

所以取出位置4的时间（离现在最远的时间），和第6个事件的时间戳做比较：



6.得到的差是7秒，小于时间窗口10秒，说明在10秒内，来的事件个数大于5了，所以本次不允许通过：



7.接下来即便来上100个事件，只要时间差小于等于10秒，都同上，拒绝通过：



8.第11.1秒，第101次事件过来了。

因为size=5，不小于5，所以取出位置4的时间（离现在最远的时间），和第101个事件的时间戳做比较：



9.得到的差是10.1秒，大于时间窗口10秒，说明在10秒内，来的事件个数小于等于5了，所以本次允许通过：



10.删除位置4的时间（离现在最远的时间），把这次事件的时间戳放到0的位置，后面的时间戳依次向后移动：



往后再来其他事件，就是重复4-10的步骤，即可实现，在任意滑动时间窗口内，限制通过的次数

其本质思想是转换概念，将原本问题的确定时间大小，进行次数限制。

转换成确定次数大小，进行时间限制。

参考：https://blog.csdn.net/m0_71777195/article/details/129097463