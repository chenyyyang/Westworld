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
- 国家：主要是利用zk做一些监听管理工作，由official负责具体是执行rebalance
- 国土：有农田和工地，为每一个农田配一个农民，就像为每一台电脑配一个程序员
- 国人：有农民和工人，去国家分配的农田或者工地上工作，具体工作就是把数据表中的数据扫描出来执行

### performance
environment：
```
macbook air :i5 8c-16g power supply
```