# Westworld

### brief introduction
这是一个分布式延时调度框架  

### high light
- 扩展性好 
- 高可用
- 无锁设计
- 水平扩容性能无上限
- auto rebalance

### implementation
项目分成了三个领域：  
- 国家领域：主要是利用zk做一些监听管理工作，由official负责具体是执行
- 土地领域：有农田和工地，是可扩展的存储容器，为每一个农田配一个农民，为每一个工地，配一个工人
- 人民领域：有农民和工人，去国家分配的农田或者工地上工作，具体工作就是把数据表中的数据扫描出来执行

### performance
environment：
```
macbook air :i5 8c-16g power supply
```