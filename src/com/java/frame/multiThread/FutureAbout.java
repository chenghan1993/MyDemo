package com.java.frame.multiThread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 * 一般情况下，实现Runnable接口、继承Thread类的线程是无法返回结果的。但在某些场合下需要线程返回结果，就要使用Callable、
 * Future、FutureTask、CompletionService这几个类。Callable只能在ExecutorService的线程池中运行，
 * 有返回结果，也可以通过返回的Future对象查询执行状态。Future本身是一种设计模式，它是用来取得异步任务的结果
 * </p>
 * <p>
 * class FutureTask<V> implements RunnableFuture<V> <br>
 * interface RunnableFuture<V> extends Runnable, Future<V> <br>
 * FutureTask类是Future的一个实现，并实现了Runnable，所以可通过Excutor(线程池)
 * 来执行,也可传递给Thread对象执行。如果在主线程中需要执行比较耗时的操作，但又不想阻塞主线程时，可以把这些作业交给Future对象在后台完成，
 * 当主线程将来需要时，就可以通过Future对象获得后台作业的计算结果或者执行状态。
 * Executor框架利用FutureTask来完成异步任务，并可以用来进行任何潜在的耗时的计算。<br>
 * 一般FutureTask多用于耗时的计算， 主线程可以在完成自己的任务后，再去获取结果。FutureTask类既可以使用new
 * Thread(Runnable r)放到一个新线程中跑，也可以使用ExecutorService.submit(Runnable r)<br>
 * 放到线程池中跑， 而且两种方式都可以获取返回结果，但实质是一样的，即如果要有返回结果那么构造函数一定要注入一个Callable对象。
 * </p>
 * 
 * @author ChengHan
 * @date 2018年4月22日 下午2:06:52
 * @param <V>
 */
interface Future<V> {
	// 试图取消对此任务的执行，如果任务已完成、或已取消，或者由于其他原因而无法取消，则此尝试将失败
	// 当调用 cancel()时，如果调用成功，且此任务尚未启动，则此任务将永不运行
	// 如果任务已经启动，则mayInterruptIfRunning参数确定是否应该以试图停止任务的方式来中断执行此任务的线程
	// 此方法返回后，对isDone()的后续调用将始终返回 true
	// 如果此方法返回 true，则对 isCancelled()的后续调用将始终返回 true
	boolean cancel(boolean mayInterruptIfRunning);

	// 如果在任务正常完成前将其取消，则返回 true
	boolean isCancelled();

	// 如果任务已完成，则返回 true；可能由于正常终止、异常或取消而完成，在所有这些情况中，此方法都将返回 true
	boolean isDone();

	// 等待线程结果返回，会阻塞
	V get() throws InterruptedException, ExecutionException;

	// 设置超时时间
	V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
}

public class FutureAbout {

}
