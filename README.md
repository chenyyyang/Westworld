# Westworld

### brief introduction
这是一个分布式延时调度框架  
[掘金原文地址](https://juejin.cn/post/6951905809617911845)

### high light
- 扩展性:  
可以支持不同数据源，运行时动态添加数据源
- 高可用:  
有server宕机的话，该server负责的数据源会自动故障转移给其他server负责（依赖zk）
- 无锁设计:  
数据源的最小单位可以是mysql-schema或者mysql-table，在系统中统一抽象为land,一个land只会分配给一个framer，天然无锁
- 水平扩容:  
理论上数据源和server都可以无限水平扩展（数量多了会降低rebalance效率），并行执行任务
- auto rebalance:  
简单的hash方案，每个server在自己内部通过hash取模运算后，可以得到相同的数据源分配方案，非常快速

### implementation
项目分成了三个领域：  
- 政府 StateGovernment：政府是一个抽象的概念，没有具体的行为，是这个社会的组织者，相当于Application
- 村官 VillageOfficial：每台server启动一个注册成临时节点，并执行监听和计算rebalance后负责的db，拿到db后根据负责的db数量为每1个db创建1个的Farmer线程
可以看出，核心管理工作由他负责
- 农场 Farmland：其实就是各种db的抽象，创建时会保存到zk，然后为每一个农田配一个农民，就像为每一台电脑配一个程序员，是1对1的，土地作为事实存在的固定的资源，
可能增加但是避免减少,由于数据没有冗余，减少后那个db上的数据无法故障转移
- 农民 Farmer：用thread实现，去分配到的农田（db）或者工地上工作，具体工作就是把数据表中的数据扫描出来执行，1个Farmer只负责1个db



### performance
environment：
```
macbook air :i5 8c-16g power supply
```

### Q&A
为什么要这样抽象？  
```
刚学Java的开始就知道Java是面向对象的语言，可以用代码来描写真实世界的行为。
但是MVC（controller service dao）的流行，使得代码不再需要抽象，只需要组织数据，与面向过程编程没啥区别了。
代码的包 分层 也变成了那几层，对象只是数据库表的映射,业务逻辑基本都写在了service层。
后来了解了一下DDD（领域驱动设计），我认为还是一种分类方式，这样的分类方式使得写代码就像写故事一样，
模拟现实的对象和对象的行为，把原本MVC中service层的流水账逻辑分散给不同的领域对象去做，
这样抽象后实现更加简单也更容易被读懂，还能降低耦合避免重复代码，对扩展开放对修改闭合。
```