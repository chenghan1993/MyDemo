package com.java.multiThread.frame;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在JDK中，只有DefaultThreadFactory实现了ThreadFactory <br>
 * 线程池中默认的线程工厂实现是很简单的，它做的事就是统一给线程池中的线程设置线程group、统一的线程前缀名以及统一的优先级
 * 
 * @author ChengHan
 * @date 2018年4月23日 上午11:57:12
 */
class DefaultThreadFactory implements ThreadFactory {
	private static final AtomicInteger poolNumber = new AtomicInteger(1);// 原子类，线程池编号
	private final ThreadGroup group;// 线程组
	private final AtomicInteger threadNumber = new AtomicInteger(1);// 线程数目
	private final String namePrefix;// 为每个创建的线程添加的前缀

	DefaultThreadFactory() {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();// 取得线程组
		namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
	}

	public Thread newThread(Runnable r) {
		// 真正创建线程的地方，设置了线程的线程组及线程名
		Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)// 默认是正常优先级
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
}

interface ThreadFactory {
	Thread newThread(Runnable r);
}

public class ThreadFactoryAbout {

}
