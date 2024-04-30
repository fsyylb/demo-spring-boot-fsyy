https://yangsanity.me/2021/08/10/CLHLock/

AQS 基础--多图详解 CLH 锁的原理与实现
 发表于 2021-08-10   分类于 source code   阅读次数： 33   Disqus： 0 Comments
本文转载自公众号 “源码笔记”。

什么是自旋锁和互斥锁？
由于CLH锁是一种自旋锁，那么我们先来看看自旋锁是什么？

自旋锁说白了也是一种互斥锁，只不过没有抢到锁的线程会一直自旋等待锁的释放，处于busy-waiting的状态，此时等待锁的线程不会进入休眠状态，而是一直忙等待浪费CPU周期。因此自旋锁适用于锁占用时间短的场合。

这里谈到了自旋锁，那么我们也顺便说下互斥锁。这里的互斥锁说的是传统意义的互斥锁，就是多个线程并发竞争锁的时候，没有抢到锁的线程会进入休眠状态即sleep-waiting，当锁被释放的时候，处于休眠状态的一个线程会再次获取到锁。缺点就是这一些列过程需要线程切换，需要执行很多CPU指令，同样需要时间。如果CPU执行线程切换的时间比锁占用的时间还长，那么可能还不如使用自旋锁。因此互斥锁适用于锁占用时间长的场合。

什么是CLH锁？
CLH锁其实就是一种是基于逻辑队列非线程饥饿的一种自旋公平锁，由于是 Craig、Landin 和 Hagersten三位大佬的发明，因此命名为CLH锁。

CLH锁原理如下：

首先有一个尾节点指针，通过这个尾结点指针来构建等待线程的逻辑队列，因此能确保线程线程先到先服务的公平性，因此尾指针可以说是构建逻辑队列的桥梁；此外这个尾节点指针是原子引用类型，避免了多线程并发操作的线程安全性问题；
通过等待锁的每个线程在自己的某个变量上自旋等待，这个变量将由前一个线程写入。由于某个线程获取锁操作时总是通过尾节点指针获取到前一线程写入的变量，而尾节点指针又是原子引用类型，因此确保了这个变量获取出来总是线程安全的。
这么说肯定很抽象，有些小伙伴可能不理解，没关系，我们心中可以有个概念即可，后面我们会一步一图来彻彻底底把CLH锁弄明白。

为什么要学习CLH锁？
好了，前面我们对CLH锁有了一个概念后，那么我们为什么要学习CLH锁呢？

研究过AQS源码的小伙伴们应该知道，AQS是JUC的核心，而CLH锁又是AQS的基础，说核心也不为过，因为AQS就是用了变种的CLH锁。如果要学好Java并发编程，那么必定要学好JUC；学好JUC，必定要先学好AQS；学好AQS，那么必定先学好CLH。因此，这就是我们为什么要学习CLH锁的原因。

CLH锁详解
那么，下面我们先来看CLH锁实现代码，然后通过一步一图来详解CLH锁。

// CLHLock.java

public class CLHLock {
    /**
     * CLH锁节点
     */
    private static class CLHNode {
        // 锁状态：默认为false，表示线程没有获取到锁；true表示线程获取到锁或正在等待
        // 为了保证locked状态是线程间可见的，因此用volatile关键字修饰
        volatile boolean locked = false;
    }
    // 尾结点，总是指向最后一个CLHNode节点
    // 【注意】这里用了java的原子系列之AtomicReference，能保证原子更新
    private final AtomicReference<CLHNode> tailNode;
    // 当前节点的前继节点
    private final ThreadLocal<CLHNode> predNode;
    // 当前节点
    private final ThreadLocal<CLHNode> curNode;

    // CLHLock构造函数，用于新建CLH锁节点时做一些初始化逻辑
    public CLHLock() {
        // 初始化时尾结点指向一个空的CLH节点
        tailNode = new AtomicReference<>(new CLHNode());
        // 初始化当前的CLH节点
        curNode = ThreadLocal.withInitial(CLHNode::new);
        // 初始化前继节点，注意此时前继节点没有存储CLHNode对象，存储的是null
        predNode = new ThreadLocal<>();
    }

    /**
     * 获取锁
     */
    public void lock() {
        // 取出当前线程ThreadLocal存储的当前节点，初始化值总是一个新建的CLHNode，locked状态为false。
        CLHNode currNode = curNode.get();
        // 此时把lock状态置为true，表示一个有效状态，
        // 即获取到了锁或正在等待锁的状态
        currNode.locked = true;
        // 当一个线程到来时，总是将尾结点取出来赋值给当前线程的前继节点；
        // 然后再把当前线程的当前节点赋值给尾节点
        // 【注意】在多线程并发情况下，这里通过AtomicReference类能防止并发问题
        // 【注意】哪个线程先执行到这里就会先执行predNode.set(preNode);语句，因此构建了一条逻辑线程等待链
        // 这条链避免了线程饥饿现象发生
        CLHNode preNode = tailNode.getAndSet(currNode);
        // 将刚获取的尾结点（前一线程的当前节点）赋给当前线程的前继节点ThreadLocal
        // 【思考】这句代码也可以去掉吗，如果去掉有影响吗？
        predNode.set(preNode);
        // 【1】若前继节点的locked状态为false，则表示获取到了锁，不用自旋等待；
        // 【2】若前继节点的locked状态为true，则表示前一线程获取到了锁或者正在等待，自旋等待
        while (preNode.locked) {
            System.out.println("线程 " + Thread.currentThread().getName() + " 没能获取到锁，进行自旋等待。。。");
            Thread.yield();
        }
        // 能执行到这里，说明当前线程获取到了锁
        System.out.println("线程 " + Thread.currentThread().getName() + " 获取到了锁！！！");
    }

    /**
     * 释放锁
     */
    public void unLock() {
        // 获取当前线程的当前节点
        CLHNode node = curNode.get();
        // 进行解锁操作
        // 这里将locked至为false，此时执行了lock方法正在自旋等待的后继节点将会获取到锁
        // 【注意】而不是所有正在自旋等待的线程去并发竞争锁
        node.locked = false;
        System.out.println("线程 " + Thread.currentThread().getName() + " 释放了锁！！！");
        // 小伙伴们可以思考下，下面两句代码的作用是什么？？
        CLHNode newCurNode = new CLHNode();
        curNode.set(newCurNode);

        // 【优化】能提高GC效率和节省内存空间，请思考：这是为什么？
        // curNode.set(predNode.get());
    }
}
CLH锁的初始化逻辑
通过上面代码，我们缕一缕CLH锁的初始化逻辑先：

定义了一个CLHNode节点，里面有一个locked属性，表示线程线程是否获得锁，默认为false。false表示线程没有获取到锁或已经释放锁；true表示线程获取到了锁或者正在自旋等待。
注意，为了保证locked属性线程间可见，该属性被volatile修饰。

CLHLock有三个重要的成员变量尾节点指针tailNode,当前线程的前继节点preNode和当前节点curNode。其中tailNode是AtomicReference类型，目的是为了保证尾节点的线程安全性；此外，preNode和curNode都是ThreadLocal类型即线程本地变量类型，用来保存每个线程的前继CLHNode和当前CLHNode节点。
最重要的是我们新建一把CLHLock对象时，此时会执行构造函数里面的初始化逻辑。此时给尾指针tailNode和当前节点curNode初始化一个locked状态为false的CLHNode节点，此时前继节点preNode存储的是null。
CLH锁的加锁过程
我们再来看看CLH锁的加锁过程，下面再贴一遍加锁lock方法的代码：

// CLHLock.java

/**
 * 获取锁
 */
public void lock() {
    // 取出当前线程ThreadLocal存储的当前节点，初始化值总是一个新建的CLHNode，locked状态为false。
    CLHNode currNode = curNode.get();
    // 此时把lock状态置为true，表示一个有效状态，
    // 即获取到了锁或正在等待锁的状态
    currNode.locked = true;
    // 当一个线程到来时，总是将尾结点取出来赋值给当前线程的前继节点；
    // 然后再把当前线程的当前节点赋值给尾节点
    // 【注意】在多线程并发情况下，这里通过AtomicReference类能防止并发问题
    // 【注意】哪个线程先执行到这里就会先执行predNode.set(preNode);语句，因此构建了一条逻辑线程等待链
    // 这条链避免了线程饥饿现象发生
    CLHNode preNode = tailNode.getAndSet(currNode);
    // 将刚获取的尾结点（前一线程的当前节点）赋给当前线程的前继节点ThreadLocal
    // 【思考】这句代码也可以去掉吗，如果去掉有影响吗？
    predNode.set(preNode);
    // 【1】若前继节点的locked状态为false，则表示获取到了锁，不用自旋等待；
    // 【2】若前继节点的locked状态为true，则表示前一线程获取到了锁或者正在等待，自旋等待
    while (preNode.locked) {
        System.out.println("线程 " + Thread.currentThread().getName() + " 没能获取到锁，进行自旋等待。。。");
        Thread.yield();
    }
    // 能执行到这里，说明当前线程获取到了锁
    System.out.println("线程 " + Thread.currentThread().getName() + " 获取到了锁！！！");
}
虽然代码的注释已经很详细，我们还是缕一缕线程加锁的过程：

首先获得当前线程的当前节点curNode，这里每次获取的CLHNode节点的locked状态都为false；
然后将当前CLHNode节点的locked状态赋值为true，表示当前线程的一种有效状态，即获取到了锁或正在等待锁的状态；
因为尾指针tailNode的总是指向了前一个线程的CLHNode节点，因此这里利用尾指针tailNode取出前一个线程的CLHNode节点，然后赋值给当前线程的前继节点predNode，并且将尾指针重新指向最后一个节点即当前线程的当前CLHNode节点，以便下一个线程到来时使用；
根据前继节点（前一个线程）的locked状态判断，若locked为false，则说明前一个线程释放了锁，当前线程即可获得锁，不用自旋等待；若前继节点的locked状态为true，则表示前一线程获取到了锁或者正在等待，自旋等待。
为了更通俗易懂，我们用一个图来说明。

假如有这么一个场景：有四个并发线程同时启动执行lock操作，假如四个线程的实际执行顺序为：threadA<–threadB<–threadC<–threadD

第一步，线程A过来，执行了lock操作，获得了锁，此时locked状态为true，如下图：

4.2-1

第二步，线程B过来，执行了lock操作，由于线程A还未释放锁，此时自旋等待，locked状态也为true，如下图：

4.2-2

第三步，线程C过来，执行了lock操作，由于线程B处于自旋等待，此时线程C也自旋等待（因此CLH锁是公平锁），locked状态也为true，如下图：

4.2-3

第四步，线程D过来，执行了lock操作，由于线程C处于自旋等待，此时线程D也自旋等待，locked状态也为true，如下图：

4.2-4

这就是多个线程并发加锁的一个过程图解，当前线程只要判断前一线程的locked状态如果是true，那么则说明前一线程要么拿到了锁，要么也处于自旋等待状态，所以自己也要自旋等待。而尾指针tailNode总是指向最后一个线程的CLHNode节点。

CLH锁的释放锁过程
前面用图解结合代码说明了CLH锁的加锁过程，那么，CLH锁的释放锁的过程又是怎样的呢？同样，我们先贴下释放锁的代码：

// CLHLock.java

/**
 * 释放锁
 */
public void unLock() {
    // 获取当前线程的当前节点
    CLHNode node = curNode.get();
    // 进行解锁操作
    // 这里将locked至为false，此时执行了lock方法正在自旋等待的后继节点将会获取到锁
    // 【注意】而不是所有正在自旋等待的线程去并发竞争锁
    node.locked = false;
    System.out.println("线程 " + Thread.currentThread().getName() + " 释放了锁！！！");
    // 小伙伴们可以思考下，下面两句代码的作用是什么？？
    CLHNode newCurNode = new CLHNode();
    curNode.set(newCurNode);

    // 【优化】能提高GC效率和节省内存空间，请思考：这是为什么？
    // curNode.set(predNode.get());
}
可以看到释放CLH锁的过程代码比加锁简单多了，下面同样缕一缕：

首先从当前线程的线程本地变量中获取出当前CLHNode节点，同时这个CLHNode节点被后面一个线程的preNode变量指向着；
然后将locked状态置为false即释放了锁；
注意：locked因为被volitile关键字修饰，此时后面自旋等待的线程的局部变量preNode.locked也为false，因此后面自旋等待的线程结束while循环即结束自旋等待，此时也获取到了锁。这一步骤也在异步进行着。

然后给当前线程的表示当前节点的线程本地变量重新赋值为一个新的CLHNode。
思考：这一步看上去是多余的，其实并不是。请思考下为什么这么做？我们后续会继续深入讲解。

我们还是用一个图来说说明CLH锁释放锁的场景，接着前面四个线程加锁的场景，假如这四个线程加锁后，线程A开始释放锁，此时线程B获取到锁，结束自旋等待，然后线程C和线程D仍然自旋等待，如下图：

4.3-1

以此类推，线程B释放锁的过程也跟上图类似，这里不再赘述。

同个线程加锁释放锁再次正常获取锁
在前面4.3小节讲到释放锁unLock方法中有下面两句代码：

CLHNode newCurNode = new CLHNode();
curNode.set(newCurNode);
这两句代码的作用是什么？这里先直接说结果：若没有这两句代码，若同个线程加锁释放锁后，然后再次执行加锁操作，这个线程就会陷入自旋等待的状态。这是为啥，可能有些下伙伴也没明白，劲越也是搞了蛮久才搞明白，嘿嘿。

下面我们同样通过一步一图的形式来分析这两句代码的作用。假如有下面这样一个场景：线程A获取到了锁，然后释放锁，然后再次获取锁。

第一步： 线程A执行了lock操作，获取到了锁，如下图：

4.4-1

上图的加锁操作中，线程A的当前CLHNode节点的locked状态被置为true；然后tailNode指针指向了当前线程的当前节点；最后因为前继节点的locked状态为false，不用自旋等待，因此获得了锁。

第二步： 线程A执行了unLock操作，释放了锁，如下图：

4.4-2

上图的释放锁操作中，线程A的当前CLHNode节点的locked状态被置为false，表示释放了锁；然后新建了一个新的CLHNode节点newCurNode，线程A的当前节点线程本地变量值重新指向了newCurNode节点对象。

第三步： 线程A再次执行lock操作，重新获得锁，如下图：

4.4-3

上图的再次获取锁操作中，首先将线程A的当前CLHNode节点的locked状态置为true；然后首先通过tailNode尾指针获取到前继节点即第一，二步中的curNode对象，然后线程A的前继节点线程本地变量的值重新指向了重新指向了curNode对象；然后tailNode尾指针重新指向了新创建的CLHNode节点newCurNode对象。最后因为前继节点的locked状态为false，不用自旋等待，因此获得了锁。

扩展： 注意到以上图片的preNode对象此时没有任何引用，所以当下一次会被GC掉。前面是通过每次执行unLock操作都新建一个新的CLHNode节点对象newCurNode，然后让线程A的当前节点线程本地变量值重新指向newCurNode。因此这里完全不用重新创建新的CLHNode节点对象，可以通过curNode.set(predNode.get());这句代码进行优化，提高GC效率和节省内存空间。

考虑同个线程加锁释放锁再次获取锁异常的情况
现在我们把unLock方法的CLHNode newCurNode = new CLHNode();和curNode.set(newCurNode);这两句代码注释掉，变成了下面这样：

// CLHLock.java

public void unLock() {
    CLHNode node = curNode.get();
    node.locked = false;
    System.out.println("线程 " + Thread.currentThread().getName() + " 释放了锁！！！");
    /*CLHNode newCurNode = new CLHNode();
    curNode.set(newCurNode);*/
}
那么结果就是线程A通过加锁，释放锁后，再次获取锁时就会陷入自旋等待的状态，这又是为什么呢？我们下面来详细分析。

第一步： 线程A执行了lock操作，获取到了锁，如下图：

4.5-1

上图的加锁操作中，线程A的当前CLHNode节点的locked状态被置为true；然后tailNode指针指向了当前线程的当前节点；最后因为前继节点的locked状态为false，不用自旋等待，因此获得了锁。这一步没有什么异常。

第二步： 线程A执行了unLock操作，释放了锁，如下图：

4.5-2

现在已经把unLock方法的CLHNode newCurNode = new CLHNode();和curNode.set(newCurNode);这两句代码注释掉了，因此上图的变化就是线程A的当前CLHNode节点的locked状态置为false即可。

第三步： 线程A再次执行lock操作，此时会陷入一直自旋等待的状态，如下图：

4.5-3

通过上图对线程A再次获取锁的lock方法的每一句代码进行分析，得知虽然第二步中将线程A的当前CLHNode的locked状态置为false了，但是在第三步线程A再次获取锁的过程中，将当前CLHNode的locked状态又置为true了，且尾指针tailNode指向的依然还是线程A的当前当前CLHNode节点。又因为每次都是将尾指针tailNode指向的CLHNode节点取出来给当前线程的前继CLHNode节点，之后执行while(predNode.locked) {}语句时，此时因为predNode.locked = true，因此线程A就永远自旋等待了。

测试CLH锁
下面我们通过一个Demo来测试前面代码实现的CLH锁是否能正常工作，直接上测试代码：

// CLHLockTest.java

/**
 * 测试 CLHLocke
 *
 * 定义一个静态成员变量 cnt，然后开 10 个线程跑起来，看能是否会有线程安全问题
 */
public class CLHLockTest {
    private static int cnt = 0;
    private static final int COUNT = 10;

    public static void main(String[] args) throws Exception {
        final CLHLock lock = new CLHLock();
        CountDownLatch latch = new CountDownLatch(COUNT);

        for (int i = 0; i < COUNT; i++) {
            new Thread(() -> {
                try {
                    lock.lock();
                    cnt++;
                } finally {
                    lock.unLock();
                    latch.countDown();
                }
            }).start();
        }

        latch.await();
        System.out.println("cnt----------->>> " + cnt);
    }
}
下面附运行结果截图：

result

PS： 这里为了截图全面，因此只开了10个线程。经过劲越测试，开100个线程，1000个线程也不会存在线程安全问题。

小结
好了，前面我们通过多图详细说明了CLH锁的原理与实现，那么我们再对前面的知识进行一次小结：

首先我们学习了自旋锁和互斥锁的概念与区别；
然后我们学习了什么是CLH锁以及为什么要学习CLH锁；
最后我们通过图示+代码实现的方式来学习CLH锁的原理，从而为学习后面的AQS打好坚实的基础。
