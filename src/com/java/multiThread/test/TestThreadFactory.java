package com.java.multiThread.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * 
 * @author ChengHan
 * @date 2018年4月23日 上午11:29:04
 */
public class TestThreadFactory {
	public static void main(String[] args) {
		System.out.println("Main thread start");
		MyThreadFactory factory = new MyThreadFactory("MyThreadFactory");
		for (int i = 0; i < 10; i++) {
			Thread thread = factory.newThread(new MyTask(i));
			thread.start();
		}
		System.out.println("Factory stats: " + factory.getStats());
		System.out.println("Main thread end");
	}
}

class MyTask implements Runnable {
	private int num;

	public MyTask(int num) {
		this.num = num;
	}

	@Override
	public void run() {
		System.out.println("Task" + num + " is running");
		try {
			Thread.sleep(2 * 10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class MyThreadFactory implements ThreadFactory {
	private int count;
	private String name;
	private List<String> stats;

	public MyThreadFactory(String name) {
		count = 0;
		this.name = name;
		stats = new ArrayList<>();
	}

	@Override
	public Thread newThread(Runnable run) {
		Thread t = new Thread(run, name + "-Thread-" + count);
		count++;
		stats.add(String.format("Create thread" + t.getId() + " with name " + t.getName() + "on...", new Date()));
		return t;
	}

	public String getStats() {
		StringBuffer buffer = new StringBuffer();
		Iterator<String> it = stats.iterator();
		while (it.hasNext()) {
			buffer.append(it.next() + "\n");
		}
		return buffer.toString();
	}
}
