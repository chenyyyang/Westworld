package real.world;

import real.world.government.StateGovernment;
import real.world.land.Farmland;

import java.util.concurrent.CountDownLatch;

public class Westworld {

    public static void main(String[] args) throws InterruptedException {
        case2();
        new CountDownLatch(1).await();
    }

    /*
     * 这个用例是再啥也没有的情况下，创建Farmland
     * 与在运行过程中，创建Farmland。
     * 触发rebalance的情况
     * 实际上第二个Farmland触发的rebalance离第一次触发时间间隔较短，会被reject掉。。
     *
     * zk问题：session超时导致临时节点消失，重连后没有自动注册
     * Event处理线程只有1，导致Event阻塞在sleep的地方，无法处理
     * */
    public static void case1() {
        //1。拿到农场的配置，这里使用默认配置
        Farmland.FarmlandInfo info = new Farmland.FarmlandInfo();
        //2.创建政府
        StateGovernment government = StateGovernment.get();
        //4.登记农场,触发rebalance，但是啥也做不了
        government.getVillageOfficial().registeFarmland(info);
        //5.自动开始rebalance，启动Farmer去干活了。。。

        //6.测试运行时创建第二个农场
        Farmland.FarmlandInfo farmlandInfo = new Farmland.FarmlandInfo();
        farmlandInfo.setUrl("jdbc:mysql://localhost:3306/newland");
        farmlandInfo.setFarmlandName("feidong_laomuji");
        government.getVillageOfficial().registeFarmland(farmlandInfo);
        //7.自动重平衡
        //所有数据都是空的情况下，测试ok
//        2021-05-02 23:02:26 INFO  [main] VillageOfficial:49 - [lookupPopulation] exist:false
//                /nationName/official is created with data null mode PERSISTENT
//        2021-05-02 23:02:26 INFO  [main] VillageOfficial:72 - [registeFarmland] laoxiangji_park exist:false
//        2021-05-02 23:02:26 ERROR [ZkClient-EventThread-1] OfficialChangeEvent:31 - [PopulationChangeEvent]:/nationName/official now population:[official127.0.0.1_26742]
//        2021-05-02 23:02:26 ERROR [ZkClient-EventThread-1] VillageOfficial:101 - [global rebalance] start waiting
//  /nationName/land/laoxiangji_park is created with data {"url":"jdbc:mysql://localhost:3306/westworld","farmlandName":"laoxiangji_park","password":"12345678","username":"root"} mode PERSISTENT
//        2021-05-02 23:02:26 INFO  [main] VillageOfficial:72 - [registeFarmland] feidong_laomuji exist:false
//        2021-05-02 23:02:26 ERROR [ZkClient-EventThread-2] NationLandChangeEvent:31 - [NationLandChangeEvent]:/nationName/land  land rise wide now:[laoxiangji_park]
//  /nationName/land/feidong_laomuji is created with data {"url":"jdbc:mysql://localhost:3306/newland","farmlandName":"feidong_laomuji","password":"12345678","username":"root"} mode PERSISTENT
//        2021-05-02 23:02:26 ERROR [ZkClient-EventThread-2] VillageOfficial:101 - [global rebalance] start waiting
//        2021-05-02 23:02:26 ERROR [ZkClient-EventThread-2] VillageOfficial:103 - [global rebalance] reject
//        2021-05-02 23:02:26 ERROR [ZkClient-EventThread-3] NationLandChangeEvent:31 - [NationLandChangeEvent]:/nationName/land  land rise wide now:[laoxiangji_park, feidong_laomuji]
//        2021-05-02 23:02:26 ERROR [ZkClient-EventThread-3] VillageOfficial:101 - [global rebalance] start waiting
//        2021-05-02 23:02:26 ERROR [ZkClient-EventThread-3] VillageOfficial:103 - [global rebalance] reject
//        2021-05-02 23:02:31 ERROR [ZkClient-EventThread-1] VillageOfficial:111 - [global rebalance] begin
//        2021-05-02 23:02:31 ERROR [ZkClient-EventThread-1] VillageOfficial:125 -  laoxiangji_park hashValue[0] selfIndex[0] ->hireFarmerForLand
//        2021-05-02 23:02:31 ERROR [ZkClient-EventThread-1] VillageOfficial:125 -  feidong_laomuji hashValue[0] selfIndex[0] ->hireFarmerForLand
//        2021-05-02 23:02:31 INFO  [t_workFor_laoxiangji_park] Farmer:26 - Farmer [workFor_laoxiangji_park] start success~gogogo
//        2021-05-02 23:02:31 INFO  [t_workFor_feidong_laomuji] Farmer:26 - Farmer [workFor_feidong_laomuji] start success~gogogo
//        2021-05-02 23:02:32 ERROR [t_workFor_laoxiangji_park] Farmer:37 - Farmer [workFor_laoxiangji_park] workLoop[0] Cost 1000 ms
//        2021-05-02 23:02:32 ERROR [t_workFor_feidong_laomuji] Farmer:37 - Farmer [workFor_feidong_laomuji] workLoop[0] Cost 1001 ms
//        2021-05-02 23:02:42 ERROR [t_workFor_laoxiangji_park] Farmer:37 - Farmer [workFor_laoxiangji_park] workLoop[1] Cost 1005 ms
//        2021-05-02 23:02:42 ERROR [t_workFor_feidong_laomuji] Farmer:37 - Farmer [workFor_feidong_laomuji] workLoop[1] Cost 1006 ms
//        2021-05-02 23:02:52 ERROR [t_workFor_laoxiangji_park] Farmer:37 - Farmer [workFor_laoxiangji_park] workLoop[2] Cost 1005 ms
//        2021-05-02 23:02:52 ERROR [t_workFor_feidong_laomuji] Farmer:37 - Farmer [workFor_feidong_laomuji] workLoop[2] Cost 1001 ms

    }

    /*
     * 这个用例是模拟 重启等情况，动态增加和减少offical
     * 启动了多个 offical，看看是多个offical怎么分配 一共2个land
     *
     * idea  --> edit run Configurations -->Allow parallel run
     * */
    public static void case2() {
        StateGovernment government = StateGovernment.get();
        //VillageOfficial启动后自动reblance一次
        government.getVillageOfficial();
        //第一个offical启动。。
//        2021-05-03 08:23:17 ERROR [ZkClient-EventThread-1] OfficialChangeEvent:31 - [PopulationChangeEvent]:/nationName/official now population:[10000000008]
//        2021-05-03 08:23:17 ERROR [ZkClient-EventThread-1] VillageOfficial:92 - [global rebalance]  block waiting to start
        //第一次rebalance后拿到 2个land，开启2个farmer去工作
//        2021-05-03 08:23:18 ERROR [ZkClient-EventThread-1] VillageOfficial:113 -  laoxiangji_park hash:[0] OfficialSequence[10000000008] Index[0] ->hireFarmerForLand
//        2021-05-03 08:23:18 ERROR [ZkClient-EventThread-1] VillageOfficial:113 -  feidong_laomuji hash:[0] OfficialSequence[10000000008] Index[0] ->hireFarmerForLand
//        2021-05-03 08:23:18 INFO  [t_workFor_laoxiangji_park] Farmer:26 - Farmer [workFor_laoxiangji_park] start success~gogogo
//        2021-05-03 08:23:18 ERROR [t_workFor_laoxiangji_park] Farmer:29 - Farmer [workFor_laoxiangji_park] isInterrupted[false]
//        2021-05-03 08:23:18 INFO  [t_workFor_feidong_laomuji] Farmer:26 - Farmer [workFor_feidong_laomuji] start success~gogogo
//        2021-05-03 08:23:18 ERROR [t_workFor_feidong_laomuji] Farmer:29 - Farmer [workFor_feidong_laomuji] isInterrupted[false]
//        2021-05-03 08:23:19 ERROR [t_workFor_laoxiangji_park] Farmer:39 - Farmer [workFor_laoxiangji_park] workLoop[0] Cost 1005 ms
//        2021-05-03 08:23:19 ERROR [t_workFor_feidong_laomuji] Farmer:39 - Farmer [workFor_feidong_laomuji] workLoop[0] Cost 1005 ms
//        2021-05-03 08:23:28 ERROR [t_workFor_laoxiangji_park] Farmer:29 - Farmer [workFor_laoxiangji_park] isInterrupted[false]
//        2021-05-03 08:23:28 ERROR [t_workFor_feidong_laomuji] Farmer:29 - Farmer [workFor_feidong_laomuji] isInterrupted[false]
//        2021-05-03 08:23:29 ERROR [t_workFor_laoxiangji_park] Farmer:39 - Farmer [workFor_laoxiangji_park] workLoop[1] Cost 1004 ms
//        2021-05-03 08:23:29 ERROR [t_workFor_feidong_laomuji] Farmer:39 - Farmer [workFor_feidong_laomuji] workLoop[1] Cost 1005 ms

        //另一个offical上线
//        2021-05-03 08:23:33 ERROR [ZkClient-EventThread-2] OfficialChangeEvent:31 - [PopulationChangeEvent]:/nationName/official now population:[10000000009, 10000000008]
//        2021-05-03 08:23:33 ERROR [ZkClient-EventThread-2] VillageOfficial:92 - [global rebalance]  block waiting to start
//        2021-05-03 08:23:33 ERROR [t_workFor_laoxiangji_park] Farmer:43 - Farmer [workFor_laoxiangji_park] Interrupted
//        2021-05-03 08:23:33 ERROR [t_workFor_feidong_laomuji] Farmer:43 - Farmer [workFor_feidong_laomuji] Interrupted
//        2021-05-03 08:23:33 ERROR [t_workFor_laoxiangji_park] Farmer:62 - Farmer [workFor_laoxiangji_park] off work
//        2021-05-03 08:23:33 ERROR [t_workFor_feidong_laomuji] Farmer:62 - Farmer [workFor_feidong_laomuji] off work
        //之前负责的 land 和对应的 framer结束工作
//        2021-05-03 08:23:33 ERROR [ZkClient-EventThread-2] VillageOfficial:96 - [global rebalance] end waiting, interrupt Farmers [workFor_feidong_laomuji, workFor_laoxiangji_park]
//        2021-05-03 08:23:34 ERROR [ZkClient-EventThread-2] VillageOfficial:113 -  feidong_laomuji hash:[0] OfficialSequence[10000000008] Index[0] ->hireFarmerForLand

        //rebalance后 之前负责2个land ，现在只负责1个land了
//        2021-05-03 08:23:34 INFO  [t_workFor_feidong_laomuji] Farmer:26 - Farmer [workFor_feidong_laomuji] start success~gogogo
//        2021-05-03 08:23:34 ERROR [t_workFor_feidong_laomuji] Farmer:29 - Farmer [workFor_feidong_laomuji] isInterrupted[false]
//        2021-05-03 08:23:35 ERROR [t_workFor_feidong_laomuji] Farmer:39 - Farmer [workFor_feidong_laomuji] workLoop[0] Cost 1002 ms
//        2021-05-03 08:23:44 ERROR [t_workFor_feidong_laomuji] Farmer:29 - Farmer [workFor_feidong_laomuji] isInterrupted[false]
//        2021-05-03 08:23:45 ERROR [t_workFor_feidong_laomuji] Farmer:39 - Farmer [workFor_feidong_laomuji] workLoop[1] Cost 1002 ms
//        2021-05-03 08:23:54 ERROR [t_workFor_feidong_laomuji] Farmer:29 - Farmer [workFor_feidong_laomuji] isInterrupted[false]
//        2021-05-03 08:23:55 ERROR [t_workFor_feidong_laomuji] Farmer:39 - Farmer [workFor_feidong_laomuji] workLoop[2] Cost 1006 ms
//        2021-05-03 08:24:04 ERROR [t_workFor_feidong_laomuji] Farmer:29 - Farmer [workFor_feidong_laomuji] isInterrupted[false]
//        2021-05-03 08:24:05 ERROR [t_workFor_feidong_laomuji] Farmer:39 - Farmer [workFor_feidong_laomuji] workLoop[3] Cost 1004 ms
//        2021-05-03 08:24:14 ERROR [t_workFor_feidong_laomuji] Farmer:29 - Farmer [workFor_feidong_laomuji] isInterrupted[false]
//        2021-05-03 08:24:15 ERROR [t_workFor_feidong_laomuji] Farmer:39 - Farmer [workFor_feidong_laomuji] workLoop[4] Cost 1003 ms
//        2021-05-03 08:24:24 ERROR [t_workFor_feidong_laomuji] Farmer:29 - Farmer [workFor_feidong_laomuji] isInterrupted[false]
//        2021-05-03 08:24:25 ERROR [t_workFor_feidong_laomuji] Farmer:39 - Farmer [workFor_feidong_laomuji] workLoop[5] Cost 1005 ms

        //  另外一个offical下线，触发rebalance
//        2021-05-03 08:24:26 ERROR [ZkClient-EventThread-3] OfficialChangeEvent:31 - [PopulationChangeEvent]:/nationName/official now population:[10000000008]
//        2021-05-03 08:24:26 ERROR [ZkClient-EventThread-3] VillageOfficial:92 - [global rebalance]  block waiting to start
        //关闭当前的framer
//        2021-05-03 08:24:26 ERROR [t_workFor_feidong_laomuji] Farmer:43 - Farmer [workFor_feidong_laomuji] Interrupted
//        2021-05-03 08:24:26 ERROR [ZkClient-EventThread-3] VillageOfficial:96 - [global rebalance] end waiting, interrupt Farmers [workFor_feidong_laomuji]
//        2021-05-03 08:24:26 ERROR [t_workFor_feidong_laomuji] Farmer:62 - Farmer [workFor_feidong_laomuji] off work
        //rebalance时候又拿到了2个land，总共也是2个land
//        2021-05-03 08:24:27 ERROR [ZkClient-EventThread-3] VillageOfficial:113 -  laoxiangji_park hash:[0] OfficialSequence[10000000008] Index[0] ->hireFarmerForLand
//        2021-05-03 08:24:27 ERROR [ZkClient-EventThread-3] VillageOfficial:113 -  feidong_laomuji hash:[0] OfficialSequence[10000000008] Index[0] ->hireFarmerForLand
        //恢复工作去。。
//        2021-05-03 08:24:27 INFO  [t_workFor_laoxiangji_park] Farmer:26 - Farmer [workFor_laoxiangji_park] start success~gogogo
//        2021-05-03 08:24:27 ERROR [t_workFor_laoxiangji_park] Farmer:29 - Farmer [workFor_laoxiangji_park] isInterrupted[false]
//        2021-05-03 08:24:27 INFO  [t_workFor_feidong_laomuji] Farmer:26 - Farmer [workFor_feidong_laomuji] start success~gogogo
//        2021-05-03 08:24:27 ERROR [t_workFor_feidong_laomuji] Farmer:29 - Farmer [workFor_feidong_laomuji] isInterrupted[false]
//        2021-05-03 08:24:28 ERROR [t_workFor_laoxiangji_park] Farmer:39 - Farmer [workFor_laoxiangji_park] workLoop[0] Cost 1004 ms
//        2021-05-03 08:24:28 ERROR [t_workFor_feidong_laomuji] Farmer:39 - Farmer [workFor_feidong_laomuji] workLoop[0] Cost 1003 ms

    }
}