package com.java.redis.book.theDesignAndImplementationOfRedis;

/**
 * 字典
 * 字典，又称为符号表(symbol table)，关联数组(associative array)，或映射(map)
 * 是一种用于保存键值对(key-value pair)的抽象数据结构
 * Redis的数据库就是使用字典来作为底层实现的，对数据库的增删改查操作也是构建在对字典的操作之上
 * 字典还是哈希键的底层实现之一
 * Redis的字典使用哈希表作为底层实现，一个哈希表里面可以有多个哈希表节点，每个哈希表节点就保存了字典中的一个键值对
 * 
 * 哈希表
 * Redis字典所使用的哈希表由dict.h/dictht结构定义
 * typedef struct dictht {
 *   // 哈希表数组
 *   dictEntry **table;
 *   // 哈希表大小
 *   unsigned long size;
 *   // 哈希表大小掩码，用于计算索引值
 *   // 总是等于 size - 1
 *   unsigned long sizemask;
 *   // 该哈希表已有节点的数量
 *   unsigned long used;
 * } dictht;
 * table属性是一个数组，数组中的每个元素都是一个指向dict.h/dictEntry结构的指针，每个dictEntry结构保存着一个键值对
 * sizemask属性的值总是等于size-1，这个属性和哈希值一起决定一个键应该被放到table数组的哪个索引上面
 * 
 * 哈希表节点
 * 哈希表节点使用dictEntry结构表示，每个dictEntry结构都保存着一个键值对
 * typedef struct dictEntry {
 *   //键
 *   void *key;
 *   //值
 *   union {
 *     void *val;
 *     uint64_t u64;
 *     int64_t s64;
 *   } v;
 *   //指向下个哈希表节点，形成链表
 *   struct dictEntry *next;
 * } dictEntry;
 * 其中，next属性是指向另一个哈希表节点的指针，这个指针可以将多个哈希值相同的键值对连接在一起，形成链表，
 * 以此方式来解决"键冲突(collision)"的问题，这种方法叫做"链地址法(separate chaining)"
 * 因为dictEntry节点组成的链表没有指向表尾的指针，所以为了速度考虑，新节点添加到链表的表头位置，排在其他已有节点的前面
 * 
 * 字典
 * Redis中的字典由dict.h/dict结构表示
 * typedef struct dict {
 *   //类型特定函数
 *   dictType *type;
 *   //私有数据
 *   void *privdata;
 *   //哈希表
 *   dictht ht[2];
 *   //rehash索引，当rehash不在进行时，值为-1
 *   int rehashidx;
 * } dict;
 * type属性是一个指向dictType结构的指针，每个dictType结构保存了一簇用于操作特定类型键值对的函数
 * Redis会为用途不同的字典设置不同的类型特定函数
 * privdata属性则保存了需要传给那些类型特定函数的可选参数
 * ht属性是一个包含两个哈希表的数组，一般情况下，字典只使用ht[0]哈希表，ht[1]哈希表只会在对ht[0]哈希表进行rehash时使用
 * 除了ht[1]之外，另一个和rehash有关的属性就是rehashidx，它记录了rehash目前的进度，如果目前没有在进行rehash，则该值为-1
 * 
 * typedef struct dictType {
 *   //计算哈希值的函数
 *   unsigned int (*hashFunction)(const void *key);
 *   //复制键的函数
 *   void *(*keyDup) (void *privdata, const void *key);
 *   //复制值的函数
 *   void *(*valDup) (void *privdata, const void *key);
 *   //对比键的函数
 *   int (*keyCompare) (void *privdata, const void *key1, const void *key2);
 *   //销毁键的函数
 *   void (*keyDestructor) (void *privdata, void *key);
 *   销毁值的函数
 *   void (*valDestructor) (void *privdata, void *obj);
 * } dictType;
 * 
 * 哈希算法
 * 当要将一个新的键值对(k-v pair)添加到字典(dict.h/dict)里面时，程序需要先根据键(k)计算出哈希值和索引值
 * 然后再根据索引值，将包含新键值对的哈希表节点(dictEntry)放到哈希表数组(dictht.**table)的指定索引上面
 * 使用字典设置的哈希函数(dict.*type)，计算键key的哈希值
 * hash = dict->type->hashFunction(key);
 * 使用哈希表(dictht)的sizemask属性和hash值，计算出索引值
 * index = hash & dict->ht[x].sizemask;
 * 其中，ht[x]可能是ht[0]，也可能是ht[1]，由是否进行rehash决定
 * 当字典被用作数据库的底层实现，或者是哈希键的底层实现时，Redis使用MurmurHash2算法来计算键的哈希值
 * 该算法目前最新版本为MurmurHash3
 * 
 * rehash(重新散列)
 * 随着操作的不断执行，哈希表保存的键值对会逐渐地增多或减少，为了让哈希表的负载因子(load factor)维持在一个合理的范围内
 * 当哈希表保存的键值对数量太多或者太少时，程序需要对哈希表的大小进行相应的扩展或者收缩
 * 扩展和收缩哈希表的工作可以通过执行rehash操作来完成，Redis对字典的哈希表(dict.ht[2])执行rehash的步骤如下
 * 1.为字典的ht[1]哈希表分配空间，这个哈希表空间的大小取决于要执行的操作(扩展或收缩),以及ht[0]当前包含的键值对数量(即ht[0].used属性值)
 *   1.1：如果执行的是扩展操作，那么ht[1]的大小为第一个大于等于ht[0].used*2的2ⁿ(2的n次方幂)
 *   1.2：如果执行的是收缩操作，那么ht[1]的大小为第一个大于等于ht[0].used的2ⁿ
 * 2.将保存在ht[0]中的所有键值对rehash到ht[1]上面：rehash指的是重新计算键的哈希值和索引值，然后将键值对放置到ht[1]哈希表的指定位置上
 * 3.当ht[0]包含的所有键值对都迁移到了ht[1]之后(ht[0]变为空表)，释放ht[0]，将ht[1]设置为ht[0]，并在ht[1]新创建一个空白的哈希表，为下一次rehash做准备
 * 
 * 哈希表的扩展与收缩
 * 当以下条件中任意一个被满足，程序会自动开始对哈希表执行扩展操作
 * 1.服务器没有在执行BGSAVE命令或者BGREWRITEAOF命令，并且哈希表的负载因子大于等于1
 * 2.服务器正在执行BGSAVE命令或者BGREWRITEAOF命令，并且哈希表的负载因子大于等于5
 * 其中，负载因子通过如下方式计算
 * 负载因子 = 哈希表已保存节点数量/哈希表大小
 * load_factor = ht[0].used / ht[0].size
 * 以上可见，当服务器正在执行BGSAVE命令或者BGREWRITEAOF命令时，执行rehash操作的负载因子要求更高
 * 这是因为避免在持久化子进程存在期间进行哈希表扩展操作，这可以避免不必要的内存写入操作，最大限度的节约内存
 * 另一方面，当哈希表的负载因子小于0.1时，程序自动开始对哈希表执行收缩操作
 * 
 * 渐进式rehash
 * 为了避免rehash对服务器性能造成影响，该操作并不是一次性、集中式的完成的，而是分多次、渐进式的完成的
 * 渐进式rehash步骤如下
 * 1.为ht[1]分配空间，让字典同时持有ht[0]和ht[1]两个哈希表
 * 2.在字典中维持一个索引计数器rehashidx，并将它的值设置为0，表示rehash工作正式开始
 * 3.在rehash进行期间，每次都字典执行增删改查时，程序出了执行指定的操作外，还会顺带将ht[0]哈希表在rehashidx索引上的所有键值对rehash到ht[1]
 *   当rehash工作完成之后，将rehashidx值增1
 * 4.随着字典操作的不断执行，最终在某个时间点上，ht[0]的所有键值对都会被rehash至ht[1]，这时程序将rehashidx属性的值设为-1，表示rehash操作完成
 * 渐进式rehash的好处在于它采取分而治之的方式，将rehash键值对所需的计算工作均摊到对字典的每个增删改查操作上，从而避免了集中式rehash而带来的庞大计算量
 * 在渐进式rehash执行期间，字典会同时使用ht[0]和ht[1]两个哈希表，所以字典的删除，查找，更新等操作会在两个哈希表上进行
 * 但是新增键值对的操作，会一律被保存到ht[1]里面，ht[0]不再进行任何添加操作。这一措施保证了ht[0]包含的键值对的数量只减不增。并随着rehash操作的执行而最终变成空表
 * 
 * @author ChengHan
 * @date 2018年6月19日 下午8:45:16
 */
public class Chapter4 {

}
