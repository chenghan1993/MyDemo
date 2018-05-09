package com.java.multiThread.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ThreadFactory与ThreadGroup还有点关联。ThreadLocal基本上和这两个没什么联系的
 * 在高并发场景下，如果只考虑线程安全而不考虑延迟性、数据共享的话，那么使用ThreadLocal会是一个非常不错的选择
 * 
 * 当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
 * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本
 * 
 * ThreadLocal 不是用于解决共享变量的问题的，不是为了协调线程同步而存在，
 * 而是为了方便每个线程处理自己的状态而引入的一个机制，理解这点对正确使用ThreadLocal至关重要
 * 
 * @author ChengHan
 * @date 2018年4月23日 下午1:57:23
 */
public class TestThreadLocal {
	public static void main(String[] args) {
		System.out.println("Main Thread start..");
		ExecutorService executor = Executors.newCachedThreadPool();
		for (int i = 1; i <= 5; i++) {
			executor.execute(new Task(i));
		}
		executor.shutdown();
		System.out.println("Main Thread end..");
	}

	static final ThreadLocal<Integer> local = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
		}
	};

	static class Task implements Runnable {
		private int num;

		public Task(int num) {
			this.num = num;
		}

		@Override
		public void run() {
			Integer i = local.get();
			while (++i < 10)
				;
			System.out.println("Task" + num + " local num result is " + i);
		}

	}
}
