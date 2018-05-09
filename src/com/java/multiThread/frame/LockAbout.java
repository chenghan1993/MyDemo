package com.java.multiThread.frame;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * 
 * @author ChengHan
 * @date 2018年4月26日 上午11:49:23
 */

class ReentrantLock implements Lock, Serializable {

	private static final long serialVersionUID = 1L;
	// private final Sync sync; // ReentrantLock的抽象静态内部类

	// 公平锁的意思是先等待的线程先获得锁，后等待的线程后获得锁
	// 非公平锁则由操作系统的调度来决定，有不确定性，一般设置成非公平锁性能更佳
	public ReentrantLock() {
		// sync = new NonfairSync(); // 默认非公平锁
	}

	public ReentrantLock(boolean fair) {
		// sync = (fair) ? new FairSync() : new NonfairSync();// 公平锁
	}

	@Override
	public void lock() {
		// TODO Auto-generated method stub

	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unlock() {
		// TODO Auto-generated method stub

	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}

interface Lock {

	// 取得锁，但是要注意lock()忽视interrupt(), 拿不到锁就 一直阻塞
	void lock();

	// 同样也是取得锁，但是lockInterruptibly()会响应interrupt()并catch到InterruptedException，从而跳出阻塞
	void lockInterruptibly() throws InterruptedException;

	// 尝试取得锁，成功返回true
	boolean tryLock();

	// 在规定的时间里，如果取得锁就返回true
	boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

	// 释放锁
	void unlock();

	// 条件状态，非常有用，BlockingQueue阻塞队列就是用到它了
	Condition newCondition();
}

public class LockAbout {

}
