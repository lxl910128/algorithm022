# 算法实现
1. 两数之和(1)
2. 有效的字母异位词(242)
4. N 叉树的前序遍历(589)
5. N 叉树的后序遍历(590)
6. 前 K 个高频元素(347)
7. 字母异位词分组(49)

# java HashMap源码阅读
## 类注解
1. 基于hash表的Map接口实现
2. 线程不安全
3. 允许null值和null键
4. key不插入顺序无关
5. initial capacity（初始容量） 和 load factor（负载因子）十分影响HashMap效率，默认16
6. capacity（容量）是指hash表容量，初始容量是创建map时初始化的容量
7.  load factor（负载因子）是在自动增加其哈希表容量的阈值，小数表示百分比，默认0.75
8. 达到负载因子的阈值将会扩容
   1. 数据重新hash
   2. 扩大1倍

## 重要参数

```java
 /**
     * 默认初始map大小16
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * 最大容量2^30
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * 负载因子 默认0.75
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

// 基本结构 node，重写了 hashcode 和 equals
static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
  // 链表
        Node<K,V> next;
						………………
    }
    /**
     * node 数组
     */
    transient Node<K,V>[] table;

    /**
     * node 的set数组
     */
    transient Set<Map.Entry<K,V>> entrySet;

    /**
     * map 大小
     */
    transient int size;

    /**
     * map结构修改的次数，包括增删以及重新索引
     */
    transient int modCount;

    /**
     * 下次需要重新hash的阈值大小
     */
    int threshold;

    /**
     * 实际 负载因子
     */
    final float loadFactor;


```

## get方法

```java
// gat实际调用的方法 hash = hash（key）
final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
  			// map非空 
        if ((tab = table) != null && (n = tab.length) > 0 &&
            // (n - 1) & hash 算出该key在node长度范围的hash值，即这个key在Node数组中的位置
            // 因为node数组长度为2的N次方，n-1 必都是1 ,如（8-1）=0111
            // 与key hash值算同或得到在node数组长度范围内的hash值
            // 查看key是否有值
            (first = tab[(n - 1) & hash]) != null) {
          // 检测 hash表获得的 node的第一个元素的hash与传入hash是否一样，该处比较的Object.hash计算的hash
            if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
          // 如果不是第一元素，则查找其child(链表)，直到找到为止
            if ((e = first.next) != null) {
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
/* 
* 扰动函数（ Perturbation function ）
	解决问题：object.hash 的hash范围有 40亿过大，需要将hash表范围缩小且还能保证分散
	意义：混合高16位和低16位的值，得到一个更加散列的低16位的Hash值
	即：新低16位是  高16与原16位的异或，该值将作为hash值使用
  方法：（key的通用hash值） 异或 key哈希值右移16位 
*/
static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

## put方法

```java
// hash为 hashMap定义的hash算法生成的hash值
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)
          	// 第一次插入，调用resize()初始化
          	// n为node数组长度
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)
          // HashMap的hash还没被占用，直接在对应位置放上新的node
            tab[i] = newNode(hash, key, value, null);
        else {
          // 该hash值已经有了，则在链表后增加本次添加的值
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
              // 首节点 实际hash 与此次hash相同
                e = p;
            else if (p instanceof TreeNode)
              // TreeNode的情况
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                for (int binCount = 0; ; ++binCount) {
                  // 找到链表尾部添加节点
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
          
            if (e != null) { // existing mapping for key
              // node中已存在与本次插入的keyhash相同的节点
              // 更新value
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
              // linkedHashMap使用的方法
                afterNodeAccess(e);
                return oldValue;
            }
        }
  			// map修改次数+1
        ++modCount;
        if (++size > threshold)
          // 达到上限扩容
            resize();
        afterNodeInsertion(evict);
        return null;
    }
// 初始化 及 2倍扩容函数
final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
```

