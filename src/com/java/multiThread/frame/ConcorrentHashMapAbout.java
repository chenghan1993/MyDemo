package com.java.multiThread.frame;

/**
 * ConcurrentHashMap是java并发包中非常有用的一个类，在高并发场景用得非常多，它是线程安全的。
 * 要注意到虽然Hashtable也是线程安全的，但是它的性能在高并发场景下完全比不上ConcurrentHashMap，
 * 这也是由它们的结构所决定的，可以认为ConcurrentHashMap是Hashtable的加强版，
 * 不过这加强版和原来的HashTable有非常大的区别，不仅是在结构上，而且在方法上也有差别。
 * 
 * HashMap、 Hashtable、ConcurrentHashMap的区别
 * 
 * 1.HashMap是线程不安全的，Hashtable、ConcurrentHashMap都是线程安全的，
 * ConcurrentHashMap、Hashtable不能传入null的key或value，但是HashMap可以。
 * 
 * 2.Hashtable是将数据放入到一个Entry数组或者它Entry数组上一个Entrty的链表节点。
 * 而ConcurrentHashMap是由Segment数组组成，每一个Segment可以看成是一个单独的Map，
 * 然后每个Segment里又有一个HashEntry数组用来存放数据。
 * 
 * 3.Hashtable的get/put/remove方法都是基于同步的synchronized方法，而ConcurrentHashMap
 * 是基于锁的机制，并且每次不是锁全表，而是锁单独的一个Segment。所以ConcurrentHashMap 的性能比HashTable好。
 * 
 * 4.如果不考虑线程安全因素，推荐使用HashMap，因为它性能最好。
 * 
 * @author ChengHan
 * @date 2018年4月25日 下午7:32:40
 */
public class ConcorrentHashMapAbout {

}
