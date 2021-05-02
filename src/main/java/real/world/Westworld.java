package real.world;

import real.world.government.StateGovernment;
import real.world.land.Farmland;

import java.util.concurrent.CountDownLatch;

public class Westworld {

    public static void main(String[] args) throws InterruptedException {
        case1();
        new CountDownLatch(1).await();
    }

    public static void case1() {
        //1。拿到农场的配置，这里使用默认配置
        Farmland.FarmlandInfo info = new Farmland.FarmlandInfo();
        //2.创建政府
        StateGovernment government = StateGovernment.get();
        //4.登记农场,触发rebalance，但是啥也做不了
        government.getVillageOfficial().registeFarmland(info);
        //5.自动开始rebalance，启动Farmer去干活了。。。

        //6.运行时创建第二个农场
        Farmland.FarmlandInfo farmlandInfo = new Farmland.FarmlandInfo();
        farmlandInfo.setUrl("jdbc:mysql://localhost:3306/newland");
        farmlandInfo.setFarmlandName("feidong_laomuji");
        government.getVillageOfficial().registeFarmland(farmlandInfo);
        //7.自动重平衡
        //所有数据都是空的情况下，测试成功
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
}
