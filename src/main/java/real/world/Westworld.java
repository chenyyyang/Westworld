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
        //2.创建农场，建立db链接
        Farmland farmland = new Farmland(info);
        //3.创建政府，开始监听和管理
        StateGovernment government = StateGovernment.get();
        //4.登记农场,触发rebalance，但是啥也做不了
        government.getVillageOfficial().registeFarmland(info);
        //5.自动开始rebalance，启动Farmer去干活了。。。

        //6.运行时创建第二个农场
        Farmland.FarmlandInfo farmlandInfo = new Farmland.FarmlandInfo();
        farmlandInfo.setUrl("");
        farmlandInfo.setFarmlandName("");
        government.getVillageOfficial().registeFarmland(farmlandInfo);

    }
}
