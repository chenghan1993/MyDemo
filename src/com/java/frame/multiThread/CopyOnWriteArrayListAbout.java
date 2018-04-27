package com.java.frame.multiThread;

/**
 * CopyOnWriteArrayList在java的并发场景中用得其实并不多，因为它并不能完全保证读取数据的正确性<br>
 * 其主要有以下的一些特点：<br>
 * 1.适合读多写少的场景<br>
 * 2.不能保证读取数据一定是正确 的，因为get时是不加锁的<br>
 * 3.add、remove会加锁再来操作
 * 
 * @author ChengHan
 * @date 2018年4月26日 上午10:32:55
 */
public class CopyOnWriteArrayListAbout {

}
