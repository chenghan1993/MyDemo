package com.java.redis.book.theDesignAndImplementationOfRedis;
/**
 * 对象
 * 前面，我们学习了简单动态字符串，双端链表，字典，跳跃表，压缩列表，整数集合等数据结构
 * 但Redis并没有直接使用这些数据结构来实现键值对数据库，而是基于这些数据结构创建了一个对象系统，这个系统包括
 * 字符串对象、列表对象、哈希对象、集合对象和有序集合对象等五种类型对象
 * 
 * 对象的类型与编码
 * Redis使用对象来表示数据库中的键和值，每当我们在Redis数据库中新创建一个键值对时，我们至少会创建两个对象，
 * 一个是键对象，一个是值对象。Redis中的每个对象都由一个redisObject结构表示
 * typedef struct redisObject {
 *   //类型
 *   unsigned type:4;
 *   //编码
 *   unsigned encoding:4;
 *   //对象最后一次访问的时间，
 *   unsigned lru:REDIS_LRU_BITS;
 *   //引用计数
 *   int refcount;
 *   //指向实际值的指针
 *   void *ptr;
 * } robj;
 * 对象的type属性记录了对象的类型，这个属性的值是"REDIS_STRING"、"REDIS_LIST"、"REDIS_HASH"、"REDIS_SET"或"REDIS_ZSET"五个常量中的一个
 * 对于Redis数据库保存的键值对来说。键总是字符串对象，值则是五个对象中的一个。
 * 通常，我们称一个数据库键为"字符串键"，指的是"这个数据库键所对应的值"为字符串对象
 * 相对应地，当我们对一个数据库键执行"TYPE命令"时，返回的结果为数据库键对应的值对象的类型，而不是键对象的类型
 * 与五个type属性值相对应地，TYPE命令的五个输出结果是"string"、"list"、"hash"、"set"或"zset"等
 * 
 * 编码和底层实现
 * 对象的ptr指针指向对象的底层实现数据结构，而这些数据结构由对象的encoding属性决定
 * encoding属性记录了对象所使用的编码，也即是说这个对象使用了什么数据结构作为对象的底层实现，属性值是以下常量值其中一个
 * 编码常量(encoding值)：REDIS_ENCODING_INT，编码所对应的底层数据结构：long类型的整数，编码所对应的"OBJECT ENCODING命令"的输出为："int"
 * 编码常量(encoding值)：REDIS_ENCODING_EMBSTR，编码所对应的底层数据结构：embstr编码的简单动态字符串，编码所对应的"OBJECT ENCODING命令"的输出为："embstr"
 * 编码常量(encoding值)：REDIS_ENCODING_RAW，编码所对应的底层数据结构：简单动态字符串，编码所对应的"OBJECT ENCODING命令"的输出为："raw"
 * 编码常量(encoding值)：REDIS_ENCODING_HT，编码所对应的底层数据结构：字典，编码所对应的"OBJECT ENCODING命令"的输出为："hashtable"
 * 编码常量(encoding值)：REDIS_ENCODING_LINKEDLIST，编码所对应的底层数据结构：双端链表，编码所对应的"OBJECT ENCODING命令"的输出为："linkedlist"
 * 编码常量(encoding值)：REDIS_ENCODING_ZIPLIST，编码所对应的底层数据结构：压缩列表，编码所对应的"OBJECT ENCODING命令"的输出为："ziplist"
 * 编码常量(encoding值)：REDIS_ENCODING_INTSET，编码所对应的底层数据结构：整数集合，编码所对应的"OBJECT ENCODING命令"的输出为："intset"
 * 编码常量(encoding值)：REDIS_ENCODING_SKIPLIST，编码所对应的底层数据结构：跳跃表和字典，编码所对应的"OBJECT ENCODING命令"的输出为："skiplist"
 * 五大对象中，每种类型的对象都至少使用了两种不同的编码，以下列出了每种对象可以使用的编码
 * 字符串对象：REDIS_ENCODING_INT、REDIS_ENCODING_EMBSTR和REDIS_ENCODING_RAW
 * 链表对象：REDIS_ENCODING_ZIPLIST和REDIS_ENCODING_LINKEDLIST
 * 哈希对象：REDIS_ENCODING_ZIPLIST和REDIS_ENCODING_HT
 * 集合对象：REDIS_ENCODING_INTSET和REDIS_ENCODING_HT
 * 有序集合对象：REDIS_ENCODING_ZIPLIST和REDIS_ENCODING_SKIPLIST
 * 使用"OBJECT ENCODING命令"可以查看一个数据库键的值对象的编码
 * 通过encoding属性来设定对象所使用的编码，而不是为特定类型的对象关联一种固定的编码，极大地提升了Redis的灵活性和效率，
 * 因为Redis可以根据不同的使用场景，来为对象设置不同的编码，从而优化对象在某一场景下的效率
 * 
 * 字符串对象
 * 字符串对象的编码可以是int，raw或者embstr
 * 1.int编码应用场景：如果一个字符串对象保存的是整数值，并且这个整数值可以用long类型表示，那么该值会被保存在字符串对象结构的ptr属性里面(将void*转换成long)，并将字符串对象的编码设置为int
 * 2.raw编码应用场景：如果字符串对象保存的是一个字符串值，并且这个字符串值的长度大于39字节，那么字符串对象将使用一个简单动态字符串(SDS)来保存这个值，并将对象的编码设置为raw
 * 3.embstr编码应用场景：如果字符串对象保存的是一个字符串值，并且这个字符串值的长度小于等于39字节，那么字符串将使用embstr编码的方式来保存这个字符串
 * embstr编码是专门用于保存短字符串的一种优化编码方式，该编码和raw编码一样，都是用redisObject结构和sdshdr结构来表示字符串对象，但raw编码会调用2次内存分配函数，来分别创建
 * redisObject结构和sdshdr结构，而embstr通过调用一次内存分配函数来分配一块连续的空间，空间中依次包含redisObject结构和sdshdr结构
 * embstr编码的字符串对象和raw编码的字符串对象，所执行命令时产生的效果是相同的，使用embstr编码来保存短字符串有以下好处
 * 1.embstr编码将创建字符串对象时，所需的内存分配次数从2次降至1次。
 * 2.embstr编码的字符串对象，将内存释放函数从2次将至1次。
 * 3.embstr编码的字符串对象的所有数据保存在一块连续内存里面，所以能更好的利用缓存带来的优势。
 * 最后，long double类型表示的浮点数在Redis中也是作为字符串值来保存的。
 * 总结，
 * long类型值，编码为int
 * long double类型保存的浮点数，编码为embstr或raw
 * 字符串，和过长的long类型值，和过长的long double类型值，编码为embstr或raw
 * int编码的字符串和embstr编码的字符串在某些情况下，会转换成raw编码的字符串。
 * 1.对于int编码的字符串，如果向该对象执行了一些命令，使得这个对象保存的值不再是整数值，而是一个字符串值，那么该对象的编码将从int变成raw，如执行APPEND命令
 * 2.Redis没有为embstr编码的字符串对象编写任何相关的修改程序，所以embstr编码的字符串对象值只读的，但raw和int则不是。当对embstr编码的字符串对象执行
 * 修改命令时，程序会先将对象的编码从embstr转换成raw，然后再执行修改操作。所以，embstr编码的字符串对象在修改之后，一定是变成raw编码的字符串对象。
 * 字符串对象是Redis五种类型的对象中，唯一会被其他四种对象嵌套的对象
 * 字符串对象操作命令有：SET、GET、APPEND、INCRBYFLOAT、INCRBY、DECRBY、STRLEN、SETRANGE、GETRANGE等
 * 
 * 列表对象
 * 列表对象的编码可以是ziplist或者linkedlist
 * ziplist编码的列表对象使用压缩列表作为底层实现，每个压缩列表节点(entry)保存了一个列表元素。
 * linkedlist编码的列表对象使用双端链表作为底层实现，每个双端链表节点(node)都保存了一个字符串对象，而每个字符串对象都保存了一个列表元素
 * ziplist编码的应用场景：
 * 1.列表对象保存的所有字符串元素的长度都小于64字节
 * 2.列表对象保存的元素数量小于512个
 * 如不能同时满足以上2个条件，则使用linkedlist编码
 * 注：以上两个条件的上限值是可以修改的，位置是配置文件中list-max-ziplist-value(字节)和list-max-ziplist-entries(元素数量)
 * 对于ziplist编码的列表对象来说，一旦两个条件不能同时满足，则对象的编码转换操作会被执行，原本保存在压缩列表里的所有列表对象都会被"转移并保存"到双端链表里面
 * 列表对象操作命令有：LPUSH、RPUSH、LPOP、RPOP、LINDEX、LLEN、LINSERT、LREM、LTRIM、LSET等
 * 
 * 哈希对象
 * 哈希对象的编码可以是ziplist或者hashtable
 * ziplist编码的哈希对象使用压缩列表作为底层实现，每当有新的键值对要加入到哈希对象时，程序会先将保存键的压缩列表节点推入到压缩列表表尾，然后再将保存值的压缩列表节点推入到压缩列表表尾
 * 因此，
 * 1.保存了同一键值对的两个节点总是紧挨在一起，保存键的节点在前，保存值的节点在后
 * 2.先添加到哈希对象中的键值对位于压缩列表的表头方向，后添加到哈希对象中的键值对位于压缩列表的表尾方向
 * hashtable编码的哈希对象使用字典作为底层实现，哈希对象中的每个键值对都使用一个字典键值对来保存
 * 1.字典中的每个键都是一个字符串对象，对象中保存了键值对的键
 * 2.字典中的每个值都是一个字符串对象，对象中保存了键值对的值
 * ziplist编码的应用场景：
 * 1.哈希对象保存的所有键值对的键和值的字符串长度都小于64字节
 * 2.哈希对象保存的键值对数量小于512个
 * 如不能同时满足以上2个条件，则使用hashtable编码
 * 注：以上两个条件的上限值是可以修改的，位置是配置文件中hash-max-ziplist-value(字节)和hash-max-ziplist-entries(键值对数量)
 * 对于ziplist编码的哈希对象来说，一旦两个条件不能同时满足，则对象的编码转换操作会被执行，原本保存在压缩列表里的所有键值对对象都会被"转移并保存"到字典里面
 * 哈希对象操作命令有：HSET、HGET、HEXISTS、HDEL、HLEN、HGETALL等
 * 
 * 集合对象
 * 集合对象的编码可以是intset或者hashtable
 * intset编码的集合对象使用整数集合作为底层实现，集合对象包含的所有元素都被保存在整数集合里面
 * hashtable编码的集合对象使用字典作为底层实现，字典的每个键都是一个字符串对象，每个字符串对象包含了一个集合元素，而字典的值全部被设置成NULL
 * intset编码的应用场景：
 * 1.集合对象保存的所有元素都是整数值
 * 2.集合对象保存的元素数量不超过512个
 * 如不能同时满足以上2个条件，则使用hashtable编码
 * 注：第二个条件的上限值是可以修改的，位置是配置文件中set-max-intset-entries(元素数量)
 * 对于intset编码的集合对象来说，一旦两个条件不能同时满足，则对象的编码转换操作会被执行，原本保存在整数集合里的所有元素都会被"转移并保存"到字典里面
 * 集合对象操作命令有：SADD、SCARD、SISMEMBER、SMEMBERS、SRANDMEMBER、SPOP、SREM等
 * 
 * 有序集合对象
 * 有序集合的编码可以是ziplist或者skiplist
 * ziplist编码的有序集合对象使用压缩列表作为底层实现，每个集合元素使用两个紧挨在一起的压缩列表节点来保存，第一个节点保存元素的成员(member)，第二个节点保存元素的分值(score)
 * 压缩列表内的集合元素按照分值，从小到大进行排序
 * skiplist编码的有序集合对象使用zset结构作为底层实现，一个zset结构同时包含一个跳跃表和一个字典，结构如下
 * typedef struct  zset {
 *   zskiplist *zsl;
 *   dict *dict;
 * } zset;
 * zset结构中的zsl跳跃表按分值从小到大保存了所有集合元素，每个跳跃表节点都保存了一个集合元素：跳跃表节点的object属性保存了元素的成员，跳跃表节点的score属性保存了元素的分值
 * 通过这个跳跃表，程序可以对有序集合进行范围型操作，比如ZRANK、ZRANGE等命令
 * zset结构中的dict字典为有序集合创建了一个从成员到分值的映射，字典中的每个键值对都保存了一个集合元素：字典的键保存了元素的成员，字典的值保存了元素的分值
 * 通过这个字典，程序可以用O(1)复杂度查找给定成员的分值，ZSCORE命令就是据此实现的
 * 有序集合每个元素的成员都是一个字符串对象，每个元素的分值都是double类型的浮点数。虽然zset结构同时使用跳跃表和字典来保存有序集合元素，但这两种数据结构都会通过指针来共享
 * 相同元素的成员和分值，所以同时使用跳跃表和字典来保存集合元素不会产生任何重复成员或者分值，也不会因此而浪费额外的内存
 * 跳跃表擅长范围型操作，字典擅长查找，所以Redis选择同时使用跳跃表和字典两种数据结构来实现有序集合
 * ziplist编码的应用场景：
 * 1.有序集合保存的所有元素成员的长度都小于64字节
 * 2.有序集合保存的元素数量小于128个
 * 如不能同时满足以上2个条件，则使用skiplist编码
 * 住：以上2个条件是可以修改的，位置是配置文件中zset-max-ziplist-value(字节)和zset-max-ziplist-entries(元素数量)
 * 对于使用ziplist编码的有序集合来说，一旦两个条件不能同时满足，则对象的编码转换操作会被执行，原本保存在压缩列表里面的所有集合元素都会"转移并保存"到zset里面
 * 有序集合对象操作命令有：ZADD、ZCARD、ZCOUNT、ZRANGE、ZREVRANGE、ZRANK、ZREVRANK、ZREM、ZSCORE等
 * 
 * 类型检查与命令多态
 * Redis中用于操作键的命令分为两种类型
 * 1.对任何类型的键执行，如DEL、EXPIRE、RENAME、TYPE、OBJECT等命令
 * 2.只能对特定类型的键执行
 * 
 * 类型检查的实现
 * 为了确保只有指定类型的键可以执行某些特定的命令，在执行类型特定命令之前，Redis会先检查输入建的类型是否正确，然后再决定是否执行
 * 类型特定命令所进行的类型检查是通过redisObject结构的type属性来实现的，服务器会先检查"输入数据库键的值对象(注意，检查的是值对象的类型)"是否为执行命令所需的类型，是则执行，否则拒绝
 * 
 * 多态命令的实现
 * Redis除了对值对象进行类型检查之外，还会根据值对象的编码方式，选择正确的命令实现代码来执行命令
 * 
 * 内存回收
 * 对象的整个生命周期可以划分为创建对象、操作对象、释放对象三个阶段
 * C语言不具备自动内存回收功能，所以Redis构建了一个引用计数计数实现的内存回收机制，每个对象的引用计数信息由redisObject结构的refCount属性记录，具体变化规律如下
 * 1.在创建一个对象的时候，引用计数的值会被初始化为1
 * 2.当对象被一个新程序使用时，引用计数值增一
 * 3.当对象不再被一个程序使用时，引用计数值减一
 * 4.当对象的引用计数值变为0时，对象所占用的内存会被释放
 * 修改引用计数的API：incrRefCount(增一)，decrRefCount(减一)，resetRefCount(重置为0，但并不释放对象，该函数多用于需要重新设置引用计数的场景)
 * 
 * 对象共享
 * refCount属性除了用于内存回收之外，还能用于对象共享上
 * 假设键A创建了一个包含整数值100的字符串对象作为值对象，如果这时键B也要创建一个同样保存了整数值100的字符串对象作为值对象，则服务器有一下两种做法
 * 1.为键B新创建一个包含整数值100的字符串对象
 * 2.让键A和键B共享同一个字符串对象
 * 很明显，第二种方法更节约内存，数据库中保存的相同值对象越多，对象共享机制就能节约越多的内存
 * 在Redis中，多个键贡献同一个值对象需要执行以下两个步骤
 * 1.将数据库键的值指针指向一个现有的值对象
 * 2.将被共享的值对象的引用计数增一
 * 可以看到，除了refCount增一之外，其他没有任何变化
 * 目前来说，Redis会在初始化服务器时，创建一万个字符串对象，包含了从0到9999所有的整数值。当服务器需要用到介于其间的值时，均不是新建对象，而是共享对象
 * 创建共享字符串对象的数量可以通过redis.h/REDIS_SHARED_INTEGERS常量来修改
 * OBJECT REFCOUNT A 可以查看键A的值对象的引用计数
 * 另外，这些共享对象不仅只有字符串键可以使用，那些在数据结构中嵌套了字符串对象的对象(linkedlist编码的列表对象，hashtable编码的哈希对象，hashtable编码的集合对象，
 * 以及zset编码的有序集合对象)都可以使用这些共享对象
 * Redis只共享字符串对象，不共享包含字符串对象的对象，因为当服务器考虑将一个共享对象设置为键的值对象时，程序需要先检查该共享对象和键想创建的对象是否完全相同，
 * 只有完全相同的情况下才可共享，而如果共享对象保存的值越复杂，则"检查是否完全相同"所需的复杂度越高，消耗的CPU时间也会越多
 * 因此，尽管共享更复杂的对象可以节约更多内存，但受到CPU时间的限制，Redis只对包含整数值的字符串对象进行共享
 * 
 * 对象的空转时长
 * redisObject结构的lru属性，记录了对象最后一次被命令程序访问的时间
 * OBJECT IDLETIME A 命令可以打印出A键的空转时长，该时长就是通过当前时间减去A键的值对象的lru时间计算得出的
 * 注：OBJECT IDLETIME命令在访问A键的值对象时，不会修改值对象的lru属性
 * 键的空转时长还有另外一项作用。如果服务器打开了maxmemory选项，并且服务器用于回收内存的算法为volatile-lru或者allkeys-lru，那么当服务器占用的内存数超过maxmemory选项
 * 所设置的上限值时，空转时长较高的那部分键会优先被服务器释放，从而回收内存
 * 配置文件的maxmemory(为0表示对内存使用没有限制)选型和maxmemory-policy(内存淘汰策略)选项
 * 
 * @author ChengHan
 * @date 2018年6月24日 下午12:06:48
 */
public class Chapter8 {

}