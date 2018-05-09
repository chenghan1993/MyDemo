package com.java.multiThread.book.JavaConcurrencyInPractice;

import org.junit.Test;

/**
 * 第二章 线程安全性<br>
 * 在线程安全性的定义中，最核心的概念就是正确性，正确性的含义是，某个类的行为与其规范完全一致<br>
 * 无状态对象一定是线程安全的，大多数Servlet都是无状态的<br>
 * 竞态条件：在并发编程中，由于不恰当的执行时序，而出现不正确结果的情况，就是竞态条件<br>
 * 最常见的竞态条件是"先检查后执行(Check-Then_Action)"和"读取-修改-写入"等情况，这两种操作是复合操作<br>
 * 为了确保线程安全性，以上常见的两种竞态条件的操作必须是原子性的，这就要通过加锁来实现<br>
 * 
 * 加锁机制<br>
 * 内置锁：每个Java对象都可以用做一个同步的锁，这些锁被称为内置锁(Intrinsic Lock)或监视锁(Monitor Lock)，
 * 		Java的内置锁是一种互斥锁，这意味着最多只有一个线程能持有这个锁。
 * 重入：内置锁是可重入的，如果某个线程试图获取一个自己已持有的锁，那么这个请求就可以成功，重入避免了死锁的发生。<br>
 * <p>@GuardedBy(lock)</p>注解，用在类中的共享状态上，表示在访问该共享状态时，必须先拿到lock<br>
 * 
 * 用锁来保护状态<br>
 * 每个共享的和可变的状态都应该只由一个锁来保护，从而使维护人员知道是哪一个锁<br>
 * 对于包含多个变量的不变性条件，其中涉及的所有变量都需要用同一个锁来保护<br>
 * 当执行时间较长的计算或者可能无法快速完成的操作时，如网络I/O或者控制台I/O，一定不要持有锁<br>
 * @author ChengHan
 * @date 2018年5月8日 上午11:17:43
 */
public class Chapter2 {

	/**
	 * 内置锁和重入
	 * @author ChengHan
	 * @date 2018年5月8日 上午11:54:20
	 */
	@Test
	public void test01() {
		//如果内置锁是不可重入的，那么这段代码将发生死锁
		LoggingWidget loggingWidget = new LoggingWidget();
		loggingWidget.doSomething();
	}
}

class Widget {
	public synchronized void doSomething() {
		System.out.println("Widget class...");
	}
}

class LoggingWidget extends Widget {
	@Override
	public synchronized void doSomething() {
		super.doSomething();
		System.out.println("LoggingWidget class...");
	}
}
