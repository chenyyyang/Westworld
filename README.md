# Westworld

### brief introduction
这是一个分布式延时调度框架  
[掘金原文地址](https://juejin.cn/post/6951905809617911845)

### high light
- 扩展性好
- 高可用
- 无锁设计
- 水平扩容
- auto rebalance

### implementation
项目分成了三个领域：  
- 国家：主要是利用zk做一些监听管理工作，由内部的official负责执行监听和rebalance
- 国土：有农田和工地，为每一个农田配一个农民，就像为每一台电脑配一个程序员，土地是比较固定的资源，可能动态增加但是很少会减少
- 国人：有农民和工人，去国家分配的农田或者工地上工作，具体工作就是把数据表中的数据扫描出来执行，rebalance主要调整的就是分配关系

为什么要这样抽象？  
```
刚学Java的开始就知道Java是面向对象的语言，可以用代码来描写真实世界的行为。
但是MVC（controller service dao）的流行，使得代码不再需要抽象，只需要组织数据，与面向过程编程没啥区别了。
代码的包 分层 也变成了那几层，对象只是数据库表的映射。
后来学习了DDD（领域驱动设计），总结起来还是一种分类方式，这样的分类方式使得写代码就像写故事一样，
模拟现实的行为，更容易被读懂
```

### performance
environment：
```
macbook air :i5 8c-16g power supply
```