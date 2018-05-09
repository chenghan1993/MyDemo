package com.java.multiThread.test;

class Thread3 implements Runnable {

	private String name;
	private Object prev;
	private Object self;

	public Thread3(String name, Object prev, Object self) {
		this.name = name;
		this.prev = prev;
		this.self = self;
	}

	@Override
	public void run() {
		int count = 10;
		while (count > 0) {
			synchronized (prev) {
				synchronized (self) {
					System.out.println(name);
					count--;
					self.notify();
				}
				try {
					prev.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

class Thread2 implements Runnable {
	private String name;

	public Thread2(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		for (int i = 0; i < 5; i++) {
			System.out.println("Thread" + name + " running... :" + i);
			try {
				Thread.sleep((long) Math.random() * 10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

class Thread1 extends Thread {
	private String name;

	public Thread1(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		for (int i = 0; i < 50; i++) {
			if (i == 30) {
				this.yield();
				System.out.println("Thread" + name + " yield...");
			}
			System.out.println("Thread" + name + " running... :" + i);
			try {
				sleep((long) Math.random() * 10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

public class TestMultiThread {
	public static void main(String[] args) throws Exception {
		System.out.println("Main thread start...");

		Object a = new Object();
		Object b = new Object();
		Object c = new Object();
		Object d = new Object();
		Object e = new Object();
		Thread3 ta = new Thread3("A", e, a);
		Thread3 tb = new Thread3("B", a, b);
		Thread3 tc = new Thread3("C", b, c);
		Thread3 td = new Thread3("D", c, d);
		Thread3 te = new Thread3("E", d, e);
		// 行不通
		// ExecutorService exec = Executors.newSingleThreadExecutor();
		// Thread3[] objArr = new Thread3[] { ta, tb, tc, td, te };
		// for (int i = 0; i < objArr.length; i++) {
		// exec.execute(objArr[i]);
		// }
		// exec.shutdown();

		new Thread(ta).start();
		Thread.sleep(100);
		new Thread(tb).start();
		Thread.sleep(100);
		new Thread(tc).start();
		Thread.sleep(100);
		new Thread(td).start();
		Thread.sleep(100);
		new Thread(te).start();
		Thread.sleep(100);

		// Thread1 th1 = new Thread1("A");
		// Thread1 th2 = new Thread1("B");
		// th1.start();
		// th2.start();
		System.out.println("Main thread end...");
	}
}
