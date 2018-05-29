package com.java.multiThread.book.JavaConcurrencyInPractice;

/**
 * 第三章 对象的共享<br>
 * 
 * 可见性<br>
 * 同步还有另一个重要的方面：内存可见性(Memory Visibility)<br>
 *  我们不仅希望防止某个线程正在使用对象状态而另一个线程在同时修改这个状态，<br>
 *  而且希望确保当一个线程修改了对象状态之后，其他线程能够看到发生的状态变化<br>
 * 重排序<br>
 *  在没有同步的情况下，编译器，处理器，以及运行时等都可能对操作的执行顺序进行一些意想不到的调整<br>
 *  这看上去似乎是一种失败的设计，但却能使JVM充分地利用现代多核处理器的强大性能<br>
 * 失效数据<br>
 *  在访问变量时如果不使用同步，则可能获得该变量的一个失效值<br>
 * 最低安全性<br>
 *  当线程在没有同步的情况下读取变量时，可能会得到一个失效值<br>
 *  但至少这个值是由之前的某个线程设置的值，而不是一个随机值<br>
 *  这种安全性保证被称为"最低安全性(out-of-thin-air-safety)"<br>
 * 最低安全性适用于绝大多数变量，但是存在一个例外：非volatile类型的64位数值变量(long,double类型)<br>
 *  Java内存模型要求，变量的读取操作和写入操作都必须是原子操作，但对于非volatile类型的long和double变量<br>
 *  JVM允许将64位的读操作或写操作分解为两个32位的操作。当读取一个非volatile类型的long变量时，如果对<br>
 *  该变量的读操作和写操作在不同的线程中执行，那么很可能会读取到某个值的高32位和另一个值的低32位。<br>
 *  因此，即使不考虑失效数据的问题，在多线程程序中使用共享且可变的long和double等类型的变量也是不安全的，<br>
 *  除非用volatile来声明它们，或者用锁来保护起来。<br>
 * volatile中文名:易变型变量<br>
 *  volatile是一个类型修饰符(type specifier)，就像大家更熟悉的const一样，它是被设计用来修饰被不同线程访问和修改的变量<br>
 *  volatile的作用是作为指令关键字，确保本条指令不会因编译器的优化而省略，且要求每次直接读值<br>
 *  volatile的变量是说这变量可能会被意想不到地改变，这样，编译器就不会去假设这个变量的值了<br>
 * 加锁与可见性<br>
 *  加锁的含义不仅仅局限于互斥行为，还包括内存可见性。为了确保所有线程都能看到共享变量的最新值<br>
 *  所有执行读操作或者写操作的线程都必须在同一个锁上同步。<br>
 * volatile变量<br>
 *  该变量是Java语言提供的一种稍弱的同步机制，用来确保将变量的更新操作通知到其他线程<br>
 *  当把变量声明为volatile类型后，编译器与运行时都会注意到这个变量是个共享的，因此不会将该变量上的操作与其他内存操作一起重排序。<br>
 *  volatile变量不会被缓存在寄存器或者对其他处理器不可见的地方，因此在读取volatile类型的变量时，总会返回最新写入的值<br>
 *  该变量的一种典型用法：检查某个状态标记以判断是否退出循环<br>
 *  volatile的语义不足以确保递增操作(i++)的原子性。加锁机制既可以确保可见性，又可以确保原子性，而volatile变量只能确保可见性<br>
 * 当且仅当满足以下所有条件是，才应该使用volatile变量<br>
 *  1.对变量的写入操作不依赖变量的当前值，或者你能确保只有单个线程更新变量的值<br>
 *  2.该变量不会与其他状态变量一起纳入不变性条件中<br>
 *  3.在访问变量时不需要加锁<br>
 * 发布与逸出<br>
 *  发布(Publish)一个对象的意思是指，使对象能够在当前作用域之外的代码中使用<br>
 *  逸出(Escape)是当某个不应该发布的对象被发布时的情况，就叫逸出<br>
 *  注意：发布内部状态可能会破坏封装性，并使得程序难以维持不变性条件<br>
 * 
 * @author ChengHan
 * @date 2018年5月9日 下午2:20:09
 */
public class Chapter3 {
	/**
	 * 没有在同步的情况下共享变量(不要这么做)
	 * 
	 * @author ChengHan
	 * @date 2018年5月9日 下午2:57:47
	 * @param args
	 */
	public static void main(String[] args) {
		new NoVisibility.ReadyThread().start();
		NoVisibility.number = 42;
		NoVisibility.ready = true;
	}
}

/**
 * 使用工厂方法来放置this引用在构造过程中逸出，这样做是正确的<br>
 * 只有当构造函数返回时，this引用才应该从线程中逸出<br>
 * 构造函数可以将this引用保存在某个地方，只要其他线程不会再构造函数完成之前使用它即可<br>
 * @author ChengHan
 * @date 2018年5月15日 下午5:21:03
 */
class SafeListener {
	private final EventListener listener;
	
	private SafeListener() {
		listener = new EventListener() {
			public void onEvent(Event e) {
				doSomething();
			}
			private void doSomething() {}
		};
	}
	
	public static SafeListener newInstance(EventSource source) {
		SafeListener safe = new SafeListener();
		source.registListener(safe.listener);
		return safe;
	}
}

/**
 * 该类隐式地使this应用逸出，不应该这么做<br>
 * 当ThisEscape发布EventListener时，也隐含地发布了ThisEscape实例本身<br>
 * 因为在这个内部类的实例中包含了对ThisEscape实例的隐含引用<br>
 * 这是逸出的一种特殊情况，即this引用在构造函数中逸出<br>
 * 当且仅当对象的构造函数返回时，对象才处于可预测的和一致的状态<br>
 * 因此，当从对象的构造函数中发布对象时，只是发布了一个尚未构造完成的对象，即使发布对象的语句位于构造函数的最后一行也是如此<br>
 * 如果this引用在构造函数中逸出，那么这种对象都被认为是不正确的构造<br>
 * <br>
 * 在构造函数中使用this引用逸出的一个常见错误是，在构造函数中启动一个线程<br>
 * 因为在构造函数中启动线程，this引用会被新创建的线程共享，在对象尚未完全构造之前，新的线程就可以看见它，这是不正确的<br>
 * 在构造函数中创建线程并没有错误，但最好不要立刻启动，可以通过一个start或者initialize方法来启动它。<br>
 * <br>
 * 在构造函数中调用一个可改写的实例方法(既不是私有方法，可不是最终方法)时，同样会导致this引用在构造过程中逸出<br>
 * @author ChengHan
 * @date 2018年5月15日 下午4:45:19
 */
class ThisEscape {
	public ThisEscape() {}
	public ThisEscape(EventSource source) {
		source.registListener(new EventListener() {
			public void onEvent(Event e) {
				doSomething();
			}
			private void doSomething() {}
		});
	}
}

class EventListener {}

class Event {}

class EventSource {
	public void registListener(EventListener eventListener) {}
}

/**
 * 在该类中，通过从非私有方法中返回states的引用，发布了states变量，但是该变量是类内部的私有变量，私有变量是不该被发布的<br>
 * 造成了内部可变状态的逸出，这样的做法是不对的
 * @author ChengHan
 * @date 2018年5月15日 下午4:28:12
 */
class UnsafeStates {
	private String[] states = new String[]{"AK","AL"};
	public String[] getStates() {
		return states;
	}
}

class NoVisibility {
	public static boolean ready;
	public static int number;

	static class ReadyThread extends Thread {
		@Override
		public void run() {
			while (!ready)
				Thread.yield();
			System.out.println(number);
		}
	}
}

/**
 * 非线程安全的可变整数类
 * 
 * @author ChengHan
 * @date 2018年5月9日 下午3:13:22
 */
class MutableInteger {
	private int value;

	public int get() {
		return value;
	}

	public void set(int value) {
		this.value = value;
	}
}

/**
 * 线程安全的可变整数类
 * 仅仅对set方法进行同步是不够的，调用get的线程仍然会看见失效值
 * @author ChengHan
 * @date 2018年5月9日 下午4:07:14
 */
class SynchronizedInteger {
	private int value;

	public synchronized int get() {
		return value;
	}

	public synchronized void set(int value) {
		this.value = value;
	}
}
