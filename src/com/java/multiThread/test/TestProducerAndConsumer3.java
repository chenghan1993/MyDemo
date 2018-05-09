package com.java.multiThread.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消费者生产者模式是Java并发编程中一个很好的应用实例，一般要求如下： <br>
 * 1.生产者仅在仓储未满时候生产，仓满则停止生产 <br>
 * 2.消费者仅在仓储有产品时候才能消费，仓空则等待 <br>
 * 3.当消费者发现仓储没产品可消费时候会通知生产者生产 <br>
 * 4.生产者在生产出可消费产品时，应该通知等待的消费者去消费。<br>
 * 
 * 第三种方式：BlockingQueue实现
 * 
 * @author ChengHan
 * @date 2018年4月27日 上午10:16:24
 */
public class TestProducerAndConsumer3 {

	public static void main(String[] args) {
		// 仓库对象
		Storehouse3 storage = new Storehouse3(1000);

		// 生产者对象
		ProducerThread3 p1 = new ProducerThread3(storage, 200);
		ProducerThread3 p2 = new ProducerThread3(storage, 200);
		ProducerThread3 p3 = new ProducerThread3(storage, 100);
		ProducerThread3 p4 = new ProducerThread3(storage, 300);
		ProducerThread3 p5 = new ProducerThread3(storage, 400);
		ProducerThread3 p6 = new ProducerThread3(storage, 200);
		ProducerThread3 p7 = new ProducerThread3(storage, 500);

		// 消费者对象
		ConsumerThread3 c1 = new ConsumerThread3(storage, 500);
		ConsumerThread3 c2 = new ConsumerThread3(storage, 200);
		ConsumerThread3 c3 = new ConsumerThread3(storage, 800);

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

class Storehouse3 {
	private int capacity;

	private BlockingQueue<Object> blockingQueue;

	private AtomicInteger curNum = new AtomicInteger(0);

	public Storehouse3(int capacity) {
		this.capacity = capacity;
		this.blockingQueue = new ArrayBlockingQueue<Object>(capacity);
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public void producer(int num) throws InterruptedException {
		while (num + curNum.get() > capacity) {
			System.out.println("仓库已无法再生产：" + num + "个产品，当前仓库产品数量：" + curNum.get());
		}

		System.out.println("仓库还未满，生产：" + num + "个产品没有问题，当前仓库产品数量：" + blockingQueue.size());
		for (int i = 0; i < num; i++) {
			blockingQueue.add(new Object());
			curNum.incrementAndGet();
		}
	}

	public void consumer(int num) throws InterruptedException {
		while (num > curNum.get()) {
			System.out.println("【仓库没有：" + num + "个产品可消费】" + "当前仓库产品数量：" + blockingQueue.size());
		}

		System.out.println("【仓库有：" + num + "个产品可消费】" + "当前仓库产品数量：" + blockingQueue.size());
		for (int i = 0; i < num; i++) {
			try {
				blockingQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			curNum.decrementAndGet();
		}
	}
}

class ProducerThread3 extends Thread {
	private int num;
	private Storehouse3 storehouse;

	public ProducerThread3(Storehouse3 storehouse, int num) {
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

class ConsumerThread3 extends Thread {
	private int num;
	private Storehouse3 storehouse;

	public ConsumerThread3(Storehouse3 storehouse, int num) {
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
