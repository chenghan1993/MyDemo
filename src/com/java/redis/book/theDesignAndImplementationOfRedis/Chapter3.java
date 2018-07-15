package com.java.redis.book.theDesignAndImplementationOfRedis;

/**
 * 链表
 * 链表提供了高效的节点重排能力，以及顺序性的节点访问方式，并且可以通过增删节点来灵活的调整链表长度
 * 链表是列表键的底层实现之一
 * 
 * 每个链表节点使用一个adlist.h/listNode结构来表示
 * //双端链表节点
 * typedef struct listNode {
 *   //前置节点
 *   struct listNode *prev;
 *   //后置节点
 *   struct listNode *next;
 *   //节点的值
 *   void *value;
 * }listNode;
 * 多个listNode可以通过prev和next指针组成双端链表
 * 
 * 虽然多个listNode结构就可以组成链表，但是使用adlist.h/list来持有链表的话，更便于操作
 * //双端链表结构
 * typedef struct list {
 *   // 表头节点
 *   listNode *head;
 *   // 表尾节点
 *   listNode *tail;
 *   // 链表所包含的节点数量
 *   unsigned long len;
 *   // 节点值复制函数，该函数用于复制链表节点所保存的值
 *   void *(*dup)(void *ptr);
 *   // 节点值释放函数，该函数用于释放链表节点所保存的值
 *   void (*free)(void *ptr);
 *   // 节点值对比函数，该函数用于对比链表节点所保存的值和另一个输入值是否相等
 *   int (*match)(void *ptr, void *key);
 * } list;
 * 
 * Redis链表(list)的特点
 * 1.双端：链表节点带有prev和next指针，获取某个节点的前置节点和后置节点的复杂度都是O(1)
 * 2.无环：表头节点的prev指针和表尾节点next指针都指向NULL，对链表的访问以NULL为终点
 * 3.带表头指针和表尾指针：通过list的head指针和tail指针，获取链表首尾节点的复杂度为O(1)
 * 4.带链表长度计数器：通过len属性，获取链表中节点数量的复杂度为O(1)
 * 5.多态：链表节点(listNode)使用void*指针来保存节点值，
 *   可以通过list结构的dup、free、match三个函数为节点值设置其他类型的值
 *   所以链表可以用于保存各种不同的值
 * 
 * @author ChengHan
 * @date 2018年6月3日 下午7:14:25
 */
public class Chapter3 {

}
