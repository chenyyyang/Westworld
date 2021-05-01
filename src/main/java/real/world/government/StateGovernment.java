package real.world.government;

import real.world.tools.zkClient.ZKClient;
import real.world.tools.zkClient.ZKClientBuilder;

public class StateGovernment {

    private ZKClient zkClient;

    public Official official;

    public String name = this.getClass().getSimpleName();

    public StateGovernment(ZKClient zkClient) {
        this.zkClient = zkClient;
    }

    public StateGovernment() {
        String address = "localhost:2181";
        ZKClient zkClient = ZKClientBuilder.newZKClient(address)
                .sessionTimeout(2000)
                .eventThreadPoolSize(1)
                .retryTimeout(1000 * 60)
                .connectionTimeout(Integer.MAX_VALUE)
                .build();
        this.zkClient = zkClient;

        official = new Official(zkClient);

        buildNation(zkClient);

        instance = this;
    }

    public void buildNation(ZKClient zkClient) {

        official.lookupNationLand();

        official.lookupPopulation();

    }

    public static StateGovernment instance;

}
