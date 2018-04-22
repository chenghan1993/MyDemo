package com.java.frame;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * CompletionService<V> 她是一个将线程池执行结果放入到一个BlockingQueue的类<br>
 * 她和Future或FutureTask有什么不同呢？<br>
 * 如果是Future或FutureTask，我们只能通过一个循环，不断的遍历线程池里的线程，取得其执行状态，然后再取结果。
 * 这样效率太低了，有可能发生一条线程执行完毕了，但我们不能立刻知道它处理完成了，还得通过一个循环来判断。
 * 基于上面的问题，所以产生了CompletionService。
 * CompletionService的原理是将一组线程的执行结果放入一个BlockingQueue当中，
 * 线程的执行结果放入到BlockingQueue的顺序只和这个线程的执行时间有关，和它们的启动顺序无关。
 * 并且你无需自己在去写很多判断哪个线程是否执行完成，它里面会去帮你处理。
 * 
 * @author ChengHan
 * @date 2018年4月22日 下午3:39:28
 * @param <V>
 */
interface CompletionService<V> {
	// 提交线程任务
	Future<V> submit(Callable<V> task);

	// 提交线程任务
	Future<V> submit(Runnable task, V result);

	// 阻塞等待
	Future<V> take() throws InterruptedException;

	// 非阻塞等待
	Future<V> poll();

	// 带时间的非阻塞等待
	Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException;
}

public class CompletionServiceAbout {

}
