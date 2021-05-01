package real.world;

import cn.hutool.core.util.RandomUtil;
import real.world.government.StateGovernment;
import real.world.land.Farmland;
import real.world.people.Farmer;

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
        government.getOfficial().registeFarmland(info);
        //5.初始化一个农民
        Farmer farmer = new Farmer("Farmer_" + RandomUtil.randomLong());
        government.getOfficial().registeFarmer(farmer);

    }
}
