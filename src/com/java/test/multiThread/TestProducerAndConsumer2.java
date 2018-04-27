package com.java.test.multiThread;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 消费者生产者模式是Java并发编程中一个很好的应用实例，一般要求如下： <br>
 * 1.生产者仅在仓储未满时候生产，仓满则停止生产 <br>
 * 2.消费者仅在仓储有产品时候才能消费，仓空则等待 <br>
 * 3.当消费者发现仓储没产品可消费时候会通知生产者生产 <br>
 * 4.生产者在生产出可消费产品时，应该通知等待的消费者去消费。<br>
 * 
 * 第二种方式：通过lock实现
 * 
 * @author ChengHan
 * @date 2018年4月27日 上午10:16:24
 */
public class TestProducerAndConsumer2 {

	public static void main(String[] args) {
		// 仓库对象
		Storehouse2 storage = new Storehouse2(1000);

		// 生产者对象
		ProducerThread2 p1 = new ProducerThread2(storage, 200);
		ProducerThread2 p2 = new ProducerThread2(storage, 200);
		ProducerThread2 p3 = new ProducerThread2(storage, 100);
		ProducerThread2 p4 = new ProducerThread2(storage, 300);
		ProducerThread2 p5 = new ProducerThread2(storage, 400);
		ProducerThread2 p6 = new ProducerThread2(storage, 200);
		ProducerThread2 p7 = new ProducerThread2(storage, 500);

		// 消费者对象
		ConsumerThread2 c1 = new ConsumerThread2(storage, 500);
		ConsumerThread2 c2 = new ConsumerThread2(storage, 200);
		ConsumerThread2 c3 = new ConsumerThread2(storage, 800);

		// 线程开始执行
		c1.start();
		c2.start();
		c3.start();
		p1.start();
		p2.start();
		p3.start();
		p4.start();
		p5.start();
		p6.start();
		p7.start();
	}

}

class Storehouse2 {
	private int capacity;
	private List<Object> list = new LinkedList<>();

	private final Lock lock = new ReentrantLock();

	private final Condition full = lock.newCondition();
	private final Condition empty = lock.newCondition();

	public Storehouse2(int capacity) {
		this.capacity = capacity;
		System.out.println("当前仓库产品数量：" + list.size());
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public void producer(int num) throws InterruptedException {
		try {
			lock.lock();
			while (list.size() + num > capacity) {
				System.out.println("仓库已无法再生产" + num + "个产品，当前仓库产品数量：" + list.size());
				empty.await();
			}
			System.out.println("仓库还未满，生产" + num + "个产品没有问题，当前仓库产品数量：" + list.size());
			for (int i = 0; i < num; i++) {
				list.add(new Object());
			}
			full.signalAll();
			empty.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public void consumer(int num) throws InterruptedException {
		try {
			lock.lock();
			while (list.size() < num) {
				System.out.println("仓库没有：" + num + "个产品可消费，当前仓库产品数量：" + list.size());
				full.await();
			}
			System.out.println("仓库有：" + num + "个产品可消费，当前仓库产品数量：" + list.size());
			for (int i = 0; i < num; i++) {
				list.remove(0);
			}
			empty.signalAll();
			full.signalAll();
		} finally {
			lock.unlock();
		}
	}
}

class ProducerThread2 extends Thread {
	private int num;
	private Storehouse2 storehouse;

	public ProducerThread2(Storehouse2 storehouse, int num) {
		this.storehouse = storehouse;
		this.num = num;
	}

	public void run() {
		try {
			storehouse.producer(num);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class ConsumerThread2 extends Thread {
	private int num;
	private Storehouse2 storehouse;

	public ConsumerThread2(Storehouse2 storehouse, int num) {
		this.storehouse = storehouse;
		this.num = num;
	}

	public void run() {
		try {
			storehouse.consumer(num);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
