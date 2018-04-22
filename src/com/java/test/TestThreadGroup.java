package com.java.test;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 复制group里面的thread信息，activeCount取得线程组里面存活线程的数量 <br>
 * Thread[] threads = new Thread[threadGroup.activeCount()];
 * threadGroup.enumerate(threads);
 * </p>
 * <p>
 * Thread.currentThread().getThreadGroup().getName():获取当前线程组名
 * </p>
 * 
 * @author ChengHan
 * @date 2018年4月22日 下午9:34:28
 */
public class TestThreadGroup {
	public static void main(String[] args) {
		System.out.println("Main Thread start...");
		ThreadGroup threadGroup = new ThreadGroup("Searcher");
		Result result = new Result();
		SearchTask searchTask = new SearchTask(result);
		for (int i = 0; i < 5; i++) {
			Thread thread = new Thread(threadGroup, searchTask);
			thread.start();
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Number of threads: " + threadGroup.activeCount());
		System.out.println("Information about the thread group");
		threadGroup.list();
		System.out.println("---我是分割线---");
		// 复制group里面thread的信息
		Thread[] threads = new Thread[threadGroup.activeCount()];
		threadGroup.enumerate(threads);
		for (int i = 0; i < threadGroup.activeCount(); i++) {
			System.out.println("Thread " + threads[i].getName() + ":" + threads[i].getState());
		}
		waitFinish(threadGroup);
		// 将group里面所有的线程都interrupt
		threadGroup.interrupt();
		System.out.println("Main Thread end...");
	}

	private static void waitFinish(ThreadGroup threadGroup) {
		while (threadGroup.activeCount() > 0) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class SearchTask implements Runnable {

	private Result result;

	public SearchTask(Result result) {
		this.result = result;
	}

	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		System.out.println("Thread start " + name);
		try {
			doTask();
			result.setName(name);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Thread " + name + ":Interrupted");
		}
		System.out.println("Thread end " + name);
	}

	private void doTask() throws InterruptedException {
		Random random = new Random(new Date().getTime());
		int value = (int) (random.nextDouble() * 100);
		System.out.println("Thread " + Thread.currentThread().getName() + ":" + value);
		TimeUnit.SECONDS.sleep(value);
	}
}

class Result {
	private String name;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
