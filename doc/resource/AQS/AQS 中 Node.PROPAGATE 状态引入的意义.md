https://yangsanity.me/2022/06/11/AQS-PROPAGATE/

AQS 中 Node.PROPAGATE 状态引入的意义
 发表于 2022-06-11   分类于 source code   阅读次数： 340   Disqus： 1 Comment
前言
关于 AQS 中的 Node.PROPAGATE 状态，源码中是这么说的：

PROPAGATE 状态表明下一次 acquireShared 应无条件传播。
releaseShared 方法应该传播到其他节点，该状态在 doReleaseShared 方法中设置（仅适用于头节点）以确保传播继续，即使其它操作已经介入。

但光看这些晦涩的文字，还是很难很好的理解它存在的意义，为什么要引入它呢？

通过查找资料发现，其实，PROPAGATE 状态的引入是为了解决 AQS 的一个 bug。

bug: https://bugs.openjdk.java.net/browse/JDK-6801020
fix: https://github.com/openjdk/jdk8u/commit/b63d6d68d93ebc34f8b4091a752eba86ff575fc2

这个 bug 是一个关于 Semaphore 的 case。

在 AQS 引入 PROPAGATE 状态前，并发调用 Semaphore 的 release 方法，某些情况下同步队列中排队的线程仍不会被唤醒。

这个 case 的完整代码如下：

import java.util.concurrent.Semaphore;

public class TestSemaphore {

    /**
     * Semaphore 初始状态为 0
     */
    private static final Semaphore SEM = new Semaphore(0);

    private static class Thread1 extends Thread {
        
        public void run() {
            // 获取 1 个许可，会阻塞等待其他线程释放许可，可被中断
            SEM.acquireUninterruptibly();
        }
    }

    private static class Thread2 extends Thread {
        
        public void run() {
            // 释放 1 个许可
            SEM.release();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10000000; i++) {
            Thread t1 = new Thread1();
            Thread t2 = new Thread1();
            Thread t3 = new Thread2();
            Thread t4 = new Thread2();
            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t1.join();
            t2.join();
            t3.join();
            t4.join();
            System.out.println(i);
        }
    }
}
引入 PROPAGATE 状态前
我们来分析一下引入 PROPAGATE 状态前这段代码会有什么问题。

首先，我们看看当时版本的 AQS 源码是怎样的，这里我们只看和 PROPAGATE 状态有关的 setHeadAndPropagate 和 releaseShared 方法即可。当时还没有引入 doReleaseShared 方法，该方法是后来解决这个 bug 时引入的。

private void setHeadAndPropagate(Node node, int propagate) {
    setHead(node);
    if (propagate > 0 && node.waitStatus != 0) {
        Node s = node.next;
        if (s == null || s.isShared())
            unparkSuccessor(node);
    }
}

public final boolean releaseShared(long arg) {
    if (tryReleaseShared(arg)) {
        Node h = head;
        if (h != null && h.waitStatus != 0)
            unparkSuccessor(h);
        return true;
    }
    return false;
}
接下来，我们分析下这个 bug 是如何产生的。

在 TestSemaphore 中，Semaphore 初始许可为 0，同时运行 4 个子线程，2 个子线程（t1，t2）同时获取 1 个许可，另外 2 个子线程（t3，t4）同时释放 1 个许可，每次循环主线程都会等待所有子线程运行完毕。

我们假设 t1 和 t2 先获取许可，因为初始许可为 0，所以 t1 和 t2 入同步队列，假设此刻的同步队列是这样的：

head <=> node1(t1) <=> node2(t2 tail)

此时 head.waitStatus 为 SIGNAL。接下来，t3 先释放，t4 后释放：

t3 调用 tryReleaseShared 方法释放 1 个许可，然后调用 unparkSuccessor 方法将 head.waitStatus 由 SIGNAL 改为 0，并唤醒后继节点 t1 后退出
t1 被 t3 唤醒，调用 tryAcquireShared 方法获取到许可并返回 0（此时还未调用 setHeadAndPropagate 方法中的 setHead 方法将自己设置为新 head）
t4 调用 tryReleaseShared 方法释放 1 个许可，因为 head 未改变，因此 head.waitStatus 仍为 0，这导致 t4 退出，不会继续调用 unparkSuccessor 方法唤醒后继节点 t2
t1 继续调用 setHeadAndPropagate 方法，首先将自己设置为新 head，然后因为 tryAcquireShared 方法返回 0 导致 t1 退出，不会继续调用 unparkSuccessor 方法唤醒后继节点 t2
至此，t2 永远不会被唤醒，问题产生。

引入 PROPAGATE 状态后
接下来我们再来看看引入 PROPAGATE 状态后这个问题如何解决。

同样先看下引入 PROPAGATE 状态后的 AQS 源码：

private void setHeadAndPropagate(Node node, int propagate) {
    Node h = head;
    setHead(node);
    if (propagate > 0 || h == null || h.waitStatus < 0) {
        Node s = node.next;
        if (s == null || s.isShared())
            doReleaseShared();
    }
}

public final boolean releaseShared(long arg) {
    if (tryReleaseShared(arg)) {
        doReleaseShared();
        return true;
    }
    return false;
}

private void doReleaseShared() {
    for (;;) {
        Node h = head;
        if (h != null && h != tail) {
            int ws = h.waitStatus;
            if (ws == Node.SIGNAL) {
                if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                    continue;
                unparkSuccessor(h);
            }
            else if (ws == 0 &&
                     !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                continue;
        }
        if (h == head)
            break;
    }
}
同样的例子：

t3 调用 tryReleaseShared 方法释放 1 个许可，然后调用 doReleaseShared 方法将 head.waitStatus 由 SIGNAL 改为 0，并唤醒后继节点 t1 后退出
t1 被 t3 唤醒，调用 tryAcquireShared 方法获取到许可并返回 0（此时还未调用 setHeadAndPropagate 方法中的 setHead 方法将自己设置为新 head）
t4 调用 tryReleaseShared 方法释放 1 个许可，因为 head 未改变，因此 head.waitStatus 仍为 0，然后调用 doReleaseShared 方法将 head.waitStatus 由 0 改为 PROPAGATE 后 t4 退出
t1 继续调用 setHeadAndPropagate 方法，首先将自己设置为新 head，因为此时旧 head.waitStatus 为 PROPAGATE 且同步队列中 t1 还有后继节点 t2，所以继续调用 doReleaseShared 方法，将 head.waitStatus 由 SIGNAL 改为 0，并唤醒后继节点 t2 后退出
后继节点 t2 被唤醒，问题解决。

个人见解与思考
其实，setHeadAndPropagate 方法逻辑改成如下也可以解决这个 bug，甚至都不需要引入 PROPAGATE 状态。

（基本思路是：head.waitStatus 为 0 是多线程下可能出现的中间状态，既然 head.waitStatus 在多线程下遇 0 要变 PROPAGATE，那在 setHeadAndPropagate 方法中判断头节点时加上 0 就行了）

private void setHeadAndPropagate(Node node, int propagate) {
    setHead(node);
    if (propagate > 0 || node.waitStatus <= 0) { // 修改这里
        Node s = node.next;
        if (s == null || s.isShared())
            unparkSuccessor(node);
    }
}
所以，这就产生了一个新问题：引入 PROPAGATE 状态可以解决这个 bug，但是解决这个 bug 并不一定非要引入 PROPAGATE 状态，那为什么最终还是引入了呢？

查了一些资料后也无果，下面有一些自己的思考，欢迎交流指正。

解决 bug
引入 PROPAGATE 状态的第一个好处：解决这个 bug。

这个 bug 产生的原因，就是因为共享锁的获取和释放在同一时刻很可能会有多条线程并发执行，这就导致在这个过程中可能会产生这种 waitStatus 为 0 的中间状态，可以通过引入 PROPAGATE 状态来解决这个问题。

语意更清晰
引入 PROPAGATE 状态的第二个好处：语意更清晰。

我们可以再深入思考下，既然 head.waitStatus 由 0 变 PROPAGATE，那 head.waitStatus 什么时候是 0？

因为 doReleaseShared 方法只有 releaseShared 和 setHeadAndPropagate 方法调用，所以从排列组合来说，无非是以下四种情况：
假设有两个线程（或多个线程）

两个同时调用 releaseShared，一个先将 head.waitStatus 由 SIGNAL 改为 0
两个同时调用 setHeadAndPropagate，一个先将 head.waitStatus 由 SIGNAL 改为 0
一个先调 releaseShared 将 head.waitStatus 由 SIGNAL 改为 0，另一个再调 setHeadAndPropagate
一个先调 setHeadAndPropagate 将 head.waitStatus 由 SIGNAL 改为 0，另一个再调 releaseShared
下面具体看下每种情况：（node1(t1 0) 表示 node1.thread 为 t1，node1.waitStatus 为 0）

情况 1：

head(-1) <=> node1(t1 0)

线程 A 调用 releaseShared 方法将 head.waitStatus 由 SIGNAL 改为 0 并唤醒 t1 后退出。此时 head(0) <=> node1(t1 0)
线程 B 调用 releaseShared 方法时发现 head.waitStatus 为 0
情况 2：

head(-1) <=> node1(t1 -1) <=> node2(t2 -1) <=> node3(t3 0)

线程 A 调用 releaseShared 方法将 head.waitStatus 由 SIGNAL 改为 0 并唤醒 t1 后退出。此时 head(0) <=> node1(t1 -1) <=> node2(t2 -1) <=> node3(t3 0)
t1 获取到锁成为头节点，此时 head.waitStatus 为 SIGNAL，调用 doReleaseShared 方法。此时 head(node1 -1) <=> node2(t2 -1) <=> node3(t3 0)
线程 B 调用 releaseShared 方法将 head.waitStatus 由 SIGNAL 改为 0 并唤醒 t2 后退出。此时 head(node1 0) <=> node2(t2 -1) <=> node3(t3 0)
t2 获取到锁成为头节点，此时 head.waitStatus 为 SIGNAL，调用 doReleaseShared 方法。此时 head(node2 -1) <=> node3(t3 0)
t1 将 head.waitStatus 由 SIGNAL 改为 0 并去唤醒 t3。此时 head(node2 0) -> node3(t3 0)
t2 发现 head.waitStatus 为 0
情况 3：

head(-1) <=> node1(t1 -1) <=> node2(t2 0)

线程 A 调用 releaseShared 方法将 head.waitStatus 由 SIGNAL 改为 0 并唤醒 t1 后退出。此时 head(0) <=> node1(t1 -1) <=> node2(t2 0)
t1 获取到锁成为头节点，此时 head.waitStatus 为 SIGNAL，调用 doReleaseShared 方法。此时 head(node1 -1) <=> node2(t2 0)
线程 B 调用 releaseShared 方法将 head.waitStatus 由 SIGNAL 改为 0 并去唤醒 t2 后退出。此时 head(node1 0) <=> node2(t2 0)
t1 发现 head.waitStatus 为 0
情况 4：

head(-1) <=> node1(t1 -1) <=> node2(t2 0)

线程 A 调用 releaseShared 方法将 head.waitStatus 由 SIGNAL 改为 0 并唤醒 t1 后退出。此时 head(0) <=> node1(t1 -1) <=> node2(t2 0)
t1 获取到锁成为头节点，此时 head.waitStatus 为 SIGNAL，调用 doReleaseShared 方法。此时 head(node1 -1) <=> node2(t2 0)
t1 将 head.waitStatus 由 SIGNAL 改为 0 并唤醒 t2 后退出。此时 head(node1 0) <=> node2(t2 0)
线程 B 调用 releaseShared 方法，发现 head.waitStatus 为 0
我们知道，head.waitStatus 为 0 代表 head 是刚成为头节点的，即 head 刚初始化，或 tail 获取到锁后成为新 head，导致队列中只剩下 head（在这个前提下，后续节点可能正在加入，也可能刚加入还没来得及将 head.waitStatus 改为 SIGNAL，但这不重要）。

从上述情况中可以发现：head.waitStatus 为 0 还可以短暂代表共享模式下有线程正在调用 unparkSuccessor 方法去唤醒后继节点（其实就是这种情况被标识为了 PROPAGATE）。

所以，引入 PROPAGATE 状态后，head.waitStatus 为 0 和 PROPAGATE 就分别代表不同的情况，否则就要揉在一起，不好理解。

加速传播
引入 PROPAGATE 状态的第三个好处：加速唤醒后继节点

doReleaseShared 方法中有这个条件判断：

if (ws == 0 && !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
    continue;
如果没有 PROPAGATE 状态，当多条线程同时运行到这里后，可能就直接退出了，虽然这时有个线程正在调用 unparkSuccessor 方法去唤醒后继节点，但唤醒后的线程也需要等到获取到锁且成为头节点后才能调用 doReleaseShared 方法再去唤醒后继节点。

当并发大时，在这个过程中很有可能会有新节点入队并满足唤醒条件，所以有了 PROPAGATE 状态，当多条线程同时运行到这里后，CAS 失败后的线程可以再次去循环判断能否唤醒后继节点，如果满足唤醒条件就去唤醒。

毕竟，调用 doReleaseShared 方法越多、越早就越有可能更快的唤醒后继节点。

总结
因此，bug 解决的更优雅且可以带来不错的收益也许才是最终引入 PROPAGATE 状态的原因吧，欢迎交流指正。

本文作者： jjiey
本文链接： http://yangsanity.me/2022/06/11/AQS-PROPAGATE/
版权声明： 本博客所有文章除特别声明外，均采用 BY-NC-SA 许可协议。转载请注明出处！