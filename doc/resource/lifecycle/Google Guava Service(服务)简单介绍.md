Guava包里的Service框架可以帮助我们把异步操作封装成一个Service服务。让这个服务有了运行状态(我们也可以理解成生命周期)，这样我们可以实时了解当前服务的运行状态。同时我们还可以添加监听器来监听服务运行状态之间的变化。

       Guava里面的服务有五种状态，如下所示：

Service.State.NEW: 服务创建状态
Service.State.STARTING: 服务启动中
Service.State.RUNNING：服务启动完成，正在运行中
Service.State.STOPPING: 服务停止中
Service.State.TERMINATED: 服务停止完成，结束
       所有的服务都需要实现Service接口，里面包括了服务需要实现的一些基本方法，所以我先对Service的方法做一个基本的介绍．

public interface Service {
	/**
	 * 启动当前服务
	 * 只有当服务的状态是NEW的情况下才可以启动，否则抛出IllegalStateException异常
	 */
	@CanIgnoreReturnValue
	Service startAsync();

	/**
	 * 判断当前服务是否处在运行状态 (RUNNING)
	 */
	boolean isRunning();

	/**
	 * 获取当前服务的状态
	 */
	Service.State state();


	/**
	 * 停止当前服务
	 */
	@CanIgnoreReturnValue
	Service stopAsync();

	/**
	 * 等待当前服务到达RUNNING状态
	 */
	void awaitRunning();

	/**
	 * 在指定的时间内等待当前服务到达RUNNING状态
	 * 如果在指定时间没有达到则抛出TimeoutException
	 */
	void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException;

	/**
	 * 等待当期服务到达TERMINATED状态
	 */
	void awaitTerminated();

	/**
	 * 在指定的时间内等待当前服务达到TERMINATED状态，
	 */
	void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException;

	/**
	 * 获取服务器失败的原因
	 * 在服务是FAILED的状态的时候调用该函数，否则抛出IllegalStateException异常
	 */
	Throwable failureCause();

	/**
	 * 监听当前服务的状态改变，
	 * executor参数表示，监听回调函数在哪里执行
	 */
	void addListener(Service.Listener listener, Executor executor);
}


       那咱们应该怎么来使用Guava里面的Servic．怎么把我们需要实现的异步逻辑包装成服务呢．Guava里面已经给提供了三个基础实现类：AbstractService,AbstractExecutionThreadService,AbstractScheduledService．这里咱们简单的介绍下
AbstractExecutionThreadService和AbstractScheduledService的使用．如果想深入的理解具体的实现逻辑，强烈建议大家去看看这三个类的源码实现逻辑．很有必要，因为你只有充分的了解了这三个类额实现逻辑之后，你才可以根据不同的业务场景自定义自己的服务实现类．

AbstractExecutionThreadService
       AbstractExecutionThreadService可以帮助我们把一个具体的异步操作封装成Service服务。说白了就是把咱们之前在线程的实现逻辑封装成服务．把之前线程的具体实现逻辑搬到AbstractExecutionThreadService的实现方法run()方法去执行。

AbstractExecutionThreadService常用方法介绍
       我们对AbstractExecutionThreadService常用方法做一个简单的解释，首先AbstractExecutionThreadService实现了Service，Service的方法在AbstractExecutionThreadService里面都有，关于这些方法我们就不重复介绍．我们介绍下AbstractExecutionThreadService新加的一些其他的方法。如下所示：

public class AbstractExecutionThreadService {

	...
	
	/**
	 * 开始执行我们服务逻辑的时候会调用，我们可以在里面做一些初始化的操作
	 */
	protected void startUp() throws Exception;

	/**
	 * 我们当前服务需要执行的具体逻辑
	 */
	protected abstract void run() throws Exception;

	/**
	 * 服务停止之后会调用的函数，我们可以在里面做 一些释放资源的处理
	 */
	protected void shutDown() throws Exception {}

	/**
	 * 比如在我们run方法里面有一个无线循环，可以在这个方法里面置状态，退出无线循环，让服务真正停止
	 * 调stopAsync函数的时候，会调用该方法
	 */
	protected void triggerShutdown() {}
	
	...
}


       AbstractExecutionThreadService类里面最重要的就是run()方法了，这个方法是我们服务需要具体实现的方法，服务需要处理的具体逻辑在这个方法里面做。

AbstractExecutionThreadService的使用
       我们用一个简单的实例来说明。我们自定义一个AbstractExecutionThreadServiceImpl实现AbstractExecutionThreadService，把我们线程的操作的具体逻辑搬到AbstractExecutionThreadServiceImpl里面去做。

public class AbstractExecutionThreadServiceImpl extends AbstractExecutionThreadService {

	private volatile boolean running = true; //声明一个状态

	@Override
	protected void startUp() {
		//TODO: 做一些初始化操作
	}

	@Override
	public void run() {
		// 具体需要实现的业务逻辑，会在线程中执行
		while (running) {
			try {
				// 等待2s
				Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
				System.out.println("do our work.....");
			} catch (Exception e) {
				//TODO: 处理异常，这里如果抛出异常，会使服务状态变为failed同时导致任务终止。
			}
		}
	}

	@Override
	protected void triggerShutdown() {
		//TODO: 如果我们的run方法中有无限循环啥的，可以在这里置状态，让退出无限循环，，stopAsync()里面会调用到该方法
		running = false; //这里我们改变状态值，run方法中就能够得到响应。=
	}

	@Override
	protected void shutDown() throws Exception {
		//TODO: 可以做一些清理操作，比如关闭连接啥的。shutDown() 是在线程的具体实现里面调用的
	}

}

       针对AbstractExecutionThreadServiceImpl我们写个测试，看下效果。

	@Test
	public void abstractExecutionThreadServiceTest() {
		// 定义我们自定义的AbstractExecutionThreadServiceImpl的类对象
		AbstractExecutionThreadServiceImpl service = new AbstractExecutionThreadServiceImpl();
		// 添加状态监听
		service.addListener(new Service.Listener() {
			@Override
			public void starting() {
				System.out.println("服务开始启动");
			}

			@Override
			public void running() {
				System.out.println("服务开始运行");
			}

			@Override
			public void stopping(Service.State from) {
				System.out.println("服务关闭中");
			}

			@Override
			public void terminated(Service.State from) {
				System.out.println("服务终止");
			}

			@Override
			public void failed(Service.State from, Throwable failure) {
				System.out.println("失败，cause：" + failure.getCause());
			}
		}, MoreExecutors.directExecutor());
		// 启动服务
		service.startAsync().awaitRunning();
		System.out.println("服务状态为:" + service.state());
		// 等待30s
		Uninterruptibles.sleepUninterruptibly(30, TimeUnit.SECONDS);
		// 停止服务
		service.stopAsync().awaitTerminated();

		System.out.println("服务状态为:" + service.state());
	}


AbstractScheduledService
       AbstractScheduledService可以帮助我们把周期性的任务封装成一个服务。
咱们线程池不是也有一个周期性的线程池么，两者是一一对应的．

AbstractScheduledService方法介绍
       AbstractScheduledService也是一个服务所以，Service里面的方法AbstractScheduledService也都有，这些方法上面已经提到了．这里着重介绍其他的一些方法．

public class AbstractScheduledService {

	...

	/**
	 * 周期任务的具体逻辑在这个里面实现
	 */
	protected abstract void runOneIteration() throws Exception;

	/**
	 * 启动周期任务之前调用，我们可以在里面做一些初始化的操作
	 */
	protected void startUp() throws Exception;


	/**
	 * 周期任务停止之后调用，我们可以在里面做 一些释放资源的处理
	 */
	protected void shutDown() throws Exception {}


	/**
	 * 指定当前周期任务在哪个ScheduledExecutorService里面调用
	 * Scheduler.newFixedDelaySchedule()
	 */
	protected abstract Scheduler scheduler();

	...
}


AbstractScheduledService的使用
       用一个简单的实例来说明下AbstractScheduledService的使用．自定义一个类继承AbstractScheduledService．实现一个非常简单的周期性任务．

public class AbstractScheduledServiceImpl extends AbstractScheduledService {


	@Override
	protected void startUp() throws Exception {
		//TODO: 做一些初始化操作
	}

	@Override
	protected void shutDown() throws Exception {
		//TODO: 可以做一些清理操作，比如关闭连接啥的。shutDown() 是在线程的具体实现里面调用的
	}

	@Override
	protected void runOneIteration() throws Exception {
		// 每次周期任务的执行逻辑
		try {
			System.out.println("do work....");
		} catch (Exception e) {
			//TODO: 处理异常，这里如果抛出异常，会使服务状态变为failed同时导致任务终止。
		}
	}

	@Override
	protected Scheduler scheduler() {
		// 5s执行一次的Scheduler
		return Scheduler.newFixedDelaySchedule(1, 5, TimeUnit.SECONDS);
	}
}


       单元测试，测试下．

	@Test
	public void abstractScheduledServiceImplTest() {
		// 定义AbstractScheduledServiceImpl对象
		AbstractScheduledServiceImpl service = new AbstractScheduledServiceImpl();
		// 添加状态监听器
		service.addListener(new Service.Listener() {
			@Override
			public void starting() {
				System.out.println("服务开始启动.....");
			}

			@Override
			public void running() {
				System.out.println("服务开始运行");
			}

			@Override
			public void stopping(Service.State from) {
				System.out.println("服务关闭中");
			}

			@Override
			public void terminated(Service.State from) {
				System.out.println("服务终止");
			}

			@Override
			public void failed(Service.State from, Throwable failure) {
				System.out.println("失败，cause：" + failure.getCause());
			}
		}, MoreExecutors.directExecutor());
		// 启动任务
		service.startAsync().awaitRunning();
		System.out.println("服务状态为:" + service.state());

		// 等待30s
		Uninterruptibles.sleepUninterruptibly(30, TimeUnit.SECONDS);

		// 关闭任务
		service.stopAsync().awaitTerminated();
		System.out.println("服务状态为:" + service.state());
	}


ServiceManager
       ServiceManager是用来管理多个服务的，让对多个服务的操作变的更加荣一，比如咱们可以同时去启动多个服务，同时去停止多个服务等等．

ServiceManager常用方法介绍
public class ServiceManager {

	/**
	 * 构造函数，管理多个Service服务
	 */
	public ServiceManager(Iterable<? extends Service> services);

	/**
	 * 给ServiceManager增加状态监听器
	 */
	public void addListener(Listener listener, Executor executor);
	public void addListener(Listener listener);

	/**
	 * 开始启动ServiceManager里面所有Service服务
	 */
	public ServiceManager startAsync();

	/**
	 * 等待ServiceManager里面所有Service服务达到Running状态
	 */
	public void awaitHealthy();
	public void awaitHealthy(long timeout, TimeUnit unit) throws TimeoutException;

	/**
	 * 停止ServiceManager里面所有Service服务
	 */
	public ServiceManager stopAsync();

	/**
	 * 等待ServiceManager里面所有Service服务达到终止状态
	 */
	public void awaitStopped();
	public void awaitStopped(long timeout, TimeUnit unit) throws TimeoutException;

	/**
	 * ServiceManager里面所有Service服务是否都达到了Running状态
	 */
	public boolean isHealthy();

	/**
	 * 以状态为索引返回当前所有服务的快照
	 */
	public ImmutableMultimap<State, Service> servicesByState();

	/**
	 * 返回一个Map对象，记录被管理的服务启动的耗时、以毫秒为单位，同时Map默认按启动时间排序
	 */
	public ImmutableMap<Service, Long> startupTimes();

}

ServiceManager的使用
       我们用一个简单的实例，来管理咱们上面实现的AbstractExecutionThreadServiceImpl和AbstractScheduledServiceImpl类．代码如下．

    @Test
    public void serviceManagerTest() {
        // 定义两个服务
        AbstractExecutionThreadServiceImpl service0 = new AbstractExecutionThreadServiceImpl();
        AbstractScheduledServiceImpl service1 = new AbstractScheduledServiceImpl();
        List<Service> serviceList = Lists.newArrayList(service0, service1);
        // ServiceManager里面管理这两个服务
        ServiceManager serviceManager = new ServiceManager(serviceList);
        // 添加监听
        serviceManager.addListener(new ServiceManager.Listener() {
            @Override
            public void healthy() {
                super.healthy();
                System.out.println("healthy");
            }

            @Override
            public void stopped() {
                super.stopped();
                System.out.println("stopped");
            }

            @Override
            public void failure(Service service) {
                super.failure(service);
                System.out.println("failure");
            }
        });
        // 启动服务，等待所有的服务都达到running状态
        serviceManager.startAsync().awaitHealthy();
        // 等待30s
        Uninterruptibles.sleepUninterruptibly(30, TimeUnit.SECONDS);
        // 停止服务
        serviceManager.stopAsync().awaitStopped();
    }

https://blog.csdn.net/wuyuxing24/article/details/94278403