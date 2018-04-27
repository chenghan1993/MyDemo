package com.java.test.multiThread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestReentrantLock {
	static class NumberWrapper {
		public int value = 1;
	}

	public static void main(String[] args) {
		final Lock lock = new ReentrantLock();
		final Condition reachThreeCondition = lock.newCondition();
		final Condition reachSixCondition = lock.newCondition();
		final NumberWrapper num = new NumberWrapper();
		Thread threadA = new Thread(new Runnable() {

			@Override
			public void run() {
				lock.lock();
				try {
					System.out.println("threadA start write");
					while (num.value <= 3) {
						System.out.println(num.value);
						num.value++;
					}
					reachThreeCondition.signal();
				} finally {
					lock.unlock();
				}
				lock.lock();
				try {
					reachSixCondition.await();
					System.out.println("threadA start write");
					while (num.value <= 9) {
						System.out.println(num.value);
						num.value++;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		});
		Thread threadB = new Thread(new Runnable() {

			@Override
			public void run() {
				lock.lock();
				try {
					while (num.value <= 3) {
						reachThreeCondition.await();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
				lock.lock();
				try {
					System.out.println("threadB start write");
					while (num.value <= 6) {
						System.out.println(num.value);
						num.value++;
					}
					reachSixCondition.signal();
				} finally {
					lock.unlock();
				}
			}

		});
		threadA.start();
		threadB.start();
	}
}
