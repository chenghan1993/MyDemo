package com.java.multiThread.frame;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * BlockingQueue是一个阻塞队列，用于高并发场景中，在线程池中，
 * 当运行线程数目大于核心线程数目时，会尝试把新加入的线程放到一个BlockingQueue中去，队列的特性就是先进先出
 * 其最常用到的实现类是ArrayBlockingQueue、LinkedBlockingQueue及SynchronousQueue这三种
 * <p>
 * ArrayBlockingQueue和LinkedBlockingQueue的区别
 * ArrayBlockingQueue数据是放在一个数组中；LinkedBlockingQueue是放在一个Node节点中，构成一个链接
 * ArrayBlockingQueue取元素和放元素都是同一个锁；而LinkedBlockingQueue有两个锁，一个放入锁，一个取得锁，
 * 分别对应放入元素和取得元素时的操作。这是由链表的结构所确定的，但是删除一个元素时，要同时获得放入锁和取得锁。
 * </p>
 * <p>
 * SynchronousQueue的特点<br>
 * 1.容量为0，无论何时 size方法总是返回0<br>
 * 2.put操作阻塞， 直到另外一个线程取走队列的元素<br>
 * 3.take操作阻塞，直到另外的线程put某个元素到队列中<br>
 * 4.任何线程只能取得其他线程put进去的元素，而不会取到自己put进去的元素
 * </p>
 * <p>
 * public SynchronousQueue(boolean fair) {
 *       transferer = fair ? new TransferQueue<E>() : new TransferStack<E>();
 *   }
 * SynchronousQueue的构造方法，接收boolean参数，表示这是一个公平的基于队列的排队模式，
 * 还是一个非公平的基于栈的排队模式
 * </p>
 * 
 * @author ChengHan
 * @date 2018年4月25日 下午5:05:40
 */

interface BlockingQueue<E> extends Queue<E> {

	// 队列没满的话，放入成功。否则抛出异常
	boolean add(E e);

	// 表示如果可能的话,将object加到BlockingQueue里,即如果BlockingQueue可以容纳,则返回true,否则返回false.（本方法不阻塞当前执行方法的线程）
	boolean offer(E e);

	// 把object加到BlockingQueue里,如果BlockQueue没有空间,则调用此方法的线程阻塞。直到BlockingQueue里面有空间再继续
	void put(E e) throws InterruptedException;

	// 可以设定等待的时间，如果在指定的时间内，还不能往队列中加入BlockingQueue，则返回失败
	boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException;

	// 取走BlockingQueue里排在首位的对象,若BlockingQueue为空,阻断进入等待状态直到BlockingQueue有新的数据被加入
	E take() throws InterruptedException;

	// 取走BlockingQueue里排在首位的对象,若不能立即取出,则可以等time参数规定的时间,取不到时返回null
	// 在JDK的concurrent包里未找到
	E pool(long time);

	// 从BlockingQueue取出排在首位的对象，如果在指定时间内，队列一旦有数据可取，则立即返回队列中的数据，如果直到时间超时还没有数据可取，返回失败
	E poll(long timeout, TimeUnit unit) throws InterruptedException;

	int remainingCapacity();

	boolean remove(Object o);

	public boolean contains(Object o);

	// 一次性从BlockingQueue获取所有可用的数据对象（还可以指定获取数据的个数），通过该方法，可以提升获取数据效率；不需要多次分批加锁或释放锁
	int drainTo(Collection<? super E> c);

	int drainTo(Collection<? super E> c, int maxElements);
}

public class BlockingQueueAbout {

}
