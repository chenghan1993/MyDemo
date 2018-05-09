package com.java.multiThread.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * public ThreadPoolExecutor(int corePoolSize, //核心线程大小 <br>
 * 							 int maximumPoolSize, //最大线程大小 <br>
 * 							 long keepAliveTime, //线程缓存时间 <br>
 * 							 TimeUnit unit, //时间单位<br>
 * 							 BlockingQueue<Runnable> workQueue, //缓存队列 <br>
 * 							 ThreadFactory threadFactory, //线程工厂<br>
 * 							 RejectedExecutionHandler handler) //拒绝策略
 * </p>
 * <p>
 * BlockingQueue有以下几种实现 <br>
 * 1.ArrayBlockingQueue:有界的数组队列 <br>
 * 2.LinkedBlockingQueue:可支持有界/无界的队列，使用链表实现
 * 3.PriorityBlockingQueue:优先队列，可以针对任务排序<br>
 * 4.SynchronousQueue:队列长度为1的队列，和Array有点区别就是：client thread提交到block queue会是一个阻塞过程，
 * 		直到有一个worker thread连接上来poll task
 * </p>
 * <p>
 * 拒绝策略通常是以下几种:
 * ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常
 * ThreadPoolExecutor.DiscardPolicy:也是丢弃任务，但是不抛出异常
 * ThreadPoolExecutor.DiscardOldestPolicy:丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
 * ThreadPoolExecutor.CallerRunsPolicy:由调用线程处理该任务
 * </p>
 * 
 * @author ChengHan
 * @date 2018年4月22日 上午12:03:41
 */
public class TestThreadPoolExecutor {
	private static int produceTaskSleepTime = 2;
	private static int produceTaskNumber = 10;

	public static void main(String[] args) {
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 4, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(3), Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		for(int i = 0; i < produceTaskNumber; i++) {
			String task = "task@" + i;
			System.out.println("put " + task);
			threadPool.execute(new ThreadPoolTask(task));
			try {
				Thread.sleep(produceTaskSleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main2(String[] args) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		List<Future<String>> resultList = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			// 使用ExecutorService执行Callable类型的任务，并将结果保存在future中
			Future<String> future = executorService.submit(new TaskWithResult(i));
			resultList.add(future);
		}
		// 启动一次顺序关闭，执行以前提交的任务，但不接受新任务。如果已经关闭，则调用没有其他作用
		executorService.shutdown();
		for (Future<String> fs : resultList) {
			try {
				System.out.println(fs.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
/**
 * 自定义线程池
 * @author ChengHan
 * @date 2018年4月22日 上午12:34:54
 */
class ThreadPoolTask implements Runnable, Serializable {

	private static final long serialVersionUID = 1L;
	private static int consumeTaskSleepTime = 2000;
	private Object threadPoolTaskData;

	public ThreadPoolTask(Object threadPoolTaskData) {
		this.threadPoolTaskData = threadPoolTaskData;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName());
		System.out.println("start..." + threadPoolTaskData);
		try {
			Thread.sleep(consumeTaskSleepTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		threadPoolTaskData = null;
	}

	public Object getTask() {
		return this.threadPoolTaskData;
	}
}

/**
 * submit()测试
 * 
 * @author ChengHan
 * @date 2018年4月21日 上午3:57:15
 */
class TaskWithResult implements Callable<String> {

	private int id;

	public TaskWithResult(int id) {
		this.id = id;
	}

	@Override
	/*
	 * 任务的具体过程，一旦任务传递给ExecutorService的submit()，则该方法自动在一个线程上执行
	 */
	public String call() throws Exception {
		System.out.println("call()被自动调用" + Thread.currentThread().getName());
		// 模拟耗时操作
		for (int i = 999999; i > 0; i--)
			;
		return "call()被自动调用，任务结果是：" + id + " " + Thread.currentThread().getName();
	}

}
