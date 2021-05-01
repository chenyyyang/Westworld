package real.world.government;

import lombok.Data;
import real.world.tools.zkClient.ZKClient;
import real.world.tools.zkClient.ZKClientBuilder;

@Data
public class StateGovernment {

    public Official official;

    public String name = this.getClass().getSimpleName();

    public StateGovernment() {
        String address = "localhost:2181";
        ZKClient zkClient = ZKClientBuilder.newZKClient(address)
                .sessionTimeout(10000)
                .eventThreadPoolSize(1)
                .retryTimeout(1000 * 60)
                .connectionTimeout(Integer.MAX_VALUE)
                .build();

        official = new Official(zkClient);

        buildNation(zkClient);

    }

    public void buildNation(ZKClient zkClient) {
        //这里本可以合并成一个方法，通过不同的参数来lookup，但是为了单一
        official.lookupNationLand();

        official.lookupPopulation();

    }

    public static volatile StateGovernment instance;

    public synchronized static StateGovernment get() {
        if (instance == null) {
            instance = new StateGovernment();
        }
        return instance;
    }

}
