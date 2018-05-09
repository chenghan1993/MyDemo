package com.java.multiThread.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class Handle2 implements Runnable {

	@Override
	public void run() {
		System.out.println(System.currentTimeMillis());
		try {
			Thread.sleep(1 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

/**
 * newFixedThreadPool 创建一个固定长度的线程池，当到达线程最大数量时，线程池的规模将不再变化
 * <p>
 * newCachedThreadPool 创建一个可缓存的线程池，如果当前线程池的规模超出了处理需求，将回收空的线程；
 * 当需求增加时，会增加线程数量；线程池规模无限制
 * </p>
 * newSingleThreadPoolExecutor 创建一个单线程的Executor，确保任务对了，串行执行
 * newScheduledThreadPool 创建一个固定长度的线程池，而且以延迟或者定时的方式来执行，类似Timer
 * 
 * @author ChengHan
 * @date 2018年4月20日 下午4:26:22
 */
class Handle implements Runnable {

	private String name;

	public Handle(String name) {
		this.name = "Thread" + name;
	}

	@Override
	public void run() {
		System.out.println(name + " Start. Time = " + new Date());
		processCommand();
		System.out.println(name + " End. Time = " + new Date());
	}

	private void processCommand() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		return this.name;
	}

}

public class TestThreadPool {
	public static void main(String[] args) {
		// executeFixedRate();
		// executorFixedDelay();
		executeEightAtNightPerDay();
	}

	/**
	 * 按指定频率周期执行任务，间隔指的是两次任务开始执行时间的间隔
	 * 当执行任务所需时间>间隔时间，并不会开辟新线程并发的执行任务，而是等待该线程执行完毕后，立即执行
	 * 
	 * @author ChengHan
	 * @date 2018年4月20日 下午5:47:48
	 */
	private static void executeFixedRate() {
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(10);
		exec.scheduleAtFixedRate(new Handle2(), 0, 2000, TimeUnit.MILLISECONDS);
	}

	/**
	 * 按指定频率间隔执行某个任务，间隔值得是上次执行完成和下次执行开始时间的间隔
	 * 
	 * @author ChengHan
	 * @date 2018年4月20日 下午6:03:42
	 */
	private static void executorFixedDelay() {
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(10);
		exec.scheduleWithFixedDelay(new Handle2(), 0, 2000, TimeUnit.MILLISECONDS);
	}

	/**
	 * 周期性的执行任务，下面方法设定每天固定9点执行一次任务
	 * 
	 * @author ChengHan
	 * @date 2018年4月20日 下午6:23:43
	 */
	private static void executeEightAtNightPerDay() {
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
		long oneDay = 24 * 60 * 60 * 1000;
		long initDelay = getTimeMillis("18:00:00") - System.currentTimeMillis();
		initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
		exec.scheduleAtFixedRate(new Handle2(), initDelay, oneDay, TimeUnit.MILLISECONDS);
	}

	/**
	 * 获取指定时间对应的毫秒数
	 * 
	 * @author ChengHan
	 * @date 2018年4月20日 下午6:08:41
	 * @param time
	 * @return
	 */
	private static long getTimeMillis(String time) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
			Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
			return curDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void main2(String[] args) {
		System.out.println("Main Thread start at: " + new Date());
		ExecutorService exec1 = Executors.newCachedThreadPool();
		ExecutorService exec2 = Executors.newFixedThreadPool(5);
		// 等价于ExecutorService exec3 = Executors.newFixedThreadPool(1);
		ExecutorService exec3 = Executors.newSingleThreadExecutor();
		ScheduledThreadPoolExecutor exec4 = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
		for (int i = 0; i < 10; i++) {
			// exec1.execute(new Handle(String.valueOf(i)));
			exec4.schedule(new Handle(String.valueOf(i)), 0, TimeUnit.SECONDS);
		}
		exec4.shutdown(); // 并不会马上关闭线程池
		// exec1.shutdownNow() //立即关闭线程池
		while (!exec4.isTerminated()) {
			System.out.println("All tasks to finish...");
		}
		System.out.println("Main Thread end at: " + new Date());
	}
}
