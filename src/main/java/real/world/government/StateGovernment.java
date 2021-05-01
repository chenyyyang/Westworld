package real.world.government;

import real.world.tools.zkClient.ZKClient;
import real.world.tools.zkClient.ZKClientBuilder;

public class StateGovernment {

    public Official official;

    public String name = this.getClass().getSimpleName();


    public StateGovernment() {
        String address = "localhost:2181";
        ZKClient zkClient = ZKClientBuilder.newZKClient(address)
                .sessionTimeout(2000)
                .eventThreadPoolSize(1)
                .retryTimeout(1000 * 60)
                .connectionTimeout(Integer.MAX_VALUE)
                .build();

        official = new Official(zkClient);

        buildNation(zkClient);

    }

    public void buildNation(ZKClient zkClient) {

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
