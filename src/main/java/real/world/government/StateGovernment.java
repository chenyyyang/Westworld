package real.world.government;

import lombok.Data;
import real.world.tools.zkClient.ZKClient;
import real.world.tools.zkClient.ZKClientBuilder;

@Data
public class StateGovernment {

    public VillageOfficial villageOfficial;

    public String name = this.getClass().getSimpleName();

    public StateGovernment() {
        String address = "localhost:2181";
        ZKClient zkClient = ZKClientBuilder.newZKClient(address)
                .sessionTimeout(30000)
                .eventThreadPoolSize(1)
                .retryTimeout(1000 * 60)
                .connectionTimeout(Integer.MAX_VALUE)
                .eventThreadPoolSize(10)//处理事件的线程数，可选，默认值为1
                .build();

        villageOfficial = new VillageOfficial(zkClient);

        buildNation();

    }

    public void buildNation() {
        //这里本可以合并成一个方法，通过不同的参数来lookup，但是为了单一职责还是分开
        villageOfficial.lookupNationLand();
        villageOfficial.lookupPopulation();
        villageOfficial.registeSelf();
    }

    public static volatile StateGovernment instance;

    public synchronized static StateGovernment get() {
        if (instance == null) {
            instance = new StateGovernment();
        }
        return instance;
    }

}
