package com.java.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class TestFuture {

	// 使用CompletionService，先完成则先返回
	public static void main5(String[] args) throws InterruptedException, ExecutionException {
		long time1 = System.currentTimeMillis();
		ExecutorService executorService = Executors.newCachedThreadPool();
		ExecutorCompletionService<Integer> executorCompletionService = new ExecutorCompletionService<>(executorService);
		for (int i = 0; i < 10; i++) {
			executorCompletionService.submit(new HandleFuture(i));
		}
		executorService.shutdown();
		for (int i = 0; i < 10; i++) {
			// take()方法其实就是Producer-Consumer中的Consumer
			System.out.println("返回结果：" + executorCompletionService.take().get());
			// System.out.println("返回结果：" + executorCompletionService.poll().get());
		}
		long time2 = System.currentTimeMillis();
		System.out.println("执行时间是：" + (time2 - time1));
	}

	// 未使用CompletionService，按加入线程池的顺序返回
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		long time1 = System.currentTimeMillis();
		ExecutorService executorService = Executors.newCachedThreadPool();
		List<Future<Integer>> results = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Future<Integer> result = executorService.submit(new HandleFuture(i));
			results.add(result);
		}
		executorService.shutdown();
		for (Future<Integer> future : results) {
			System.out.println("返回结果：" + future.get());
		}
		long time2 = System.currentTimeMillis();
		System.out.println("执行时间是：" + (time2 - time1));
	}

	// 使用FutureTask，通过线程池
	public static void main3(String[] args) {
		System.out.println("Main Thread begin at " + System.currentTimeMillis());
		ExecutorService executorService = Executors.newCachedThreadPool();
		HandleCallable task1 = new HandleCallable("1");
		HandleCallable task2 = new HandleCallable("2");
		FutureTask<Integer> result1 = new FutureTask<Integer>(task1);
		FutureTask<Integer> result2 = new FutureTask<Integer>(task2);
		executorService.execute(result1);
		executorService.execute(result2);
		Future<?> submit1 = executorService.submit(result1); // 结果是null
		Future<?> submit2 = executorService.submit(result2); // 结果是null
		executorService.shutdown();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("task1运行结果是:" + result1.get());
			System.out.println("task2运行结果是:" + result2.get());
			System.out.println("task1运行结果是:" + submit1.get());
			System.out.println("task2运行结果是:" + submit2.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("Main Thread end at " + System.currentTimeMillis());
	}

	// 使用FutureTask，直接启动线程的方法
	public static void main2(String[] args) {
		System.out.println("Main Thread begin at " + System.currentTimeMillis());
		HandleCallable task1 = new HandleCallable("1");
		FutureTask<Integer> result1 = new FutureTask<Integer>(task1);
		Thread thread1 = new Thread(result1);
		thread1.start();
		HandleCallable task2 = new HandleCallable("2");
		FutureTask<Integer> result2 = new FutureTask<Integer>(task2);
		Thread thread2 = new Thread(result2);
		thread2.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("task1运行结果是:" + result1.get());
			System.out.println("task2运行结果是:" + result2.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("Main Thread finish at " + System.currentTimeMillis());
	}

	// 使用Future
	public static void main1(String[] args) {
		System.out.println("Main Thread begin at " + System.currentTimeMillis());
		HandleCallable task1 = new HandleCallable("1");
		HandleCallable task2 = new HandleCallable("2");
		HandleCallable task3 = new HandleCallable("3");
		ExecutorService executorService = Executors.newCachedThreadPool();
		Future<Integer> result1 = executorService.submit(task1);
		Future<Integer> result2 = executorService.submit(task2);
		Future<Integer> result3 = executorService.submit(task3);
		executorService.shutdown();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			System.out.println("task1运行结果是:" + result1.get());
			System.out.println("task2运行结果是:" + result2.get());
			System.out.println("task3运行结果是:" + result3.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("Main Thread finish at " + System.currentTimeMillis());
	}
}

class HandleFuture implements Callable<Integer> {

	private int num;

	public HandleFuture(int num) {
		this.num = num;
	}

	@Override
	public Integer call() throws Exception {
		Thread.sleep(3 * 100);
		System.out.println(Thread.currentThread().getName());
		return num;
	}

}

class HandleCallable implements Callable<Integer> {

	private String name;

	public HandleCallable(String name) {
		this.name = name;
	}

	@Override
	public Integer call() throws Exception {
		System.out.println("task" + name + "开始进行计算");
		Thread.sleep(3000);
		int sum = new Random().nextInt(300);
		int result = 0;
		for (int i = 0; i < sum; i++) {
			result += i;
		}
		return result;
	}

}
