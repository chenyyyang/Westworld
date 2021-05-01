package real.world.government;

import cn.hutool.log.StaticLog;
import org.apache.zookeeper.CreateMode;
import real.world.land.NationLand;
import real.world.people.Human;
import real.world.tools.zkClient.ZKClient;
import real.world.tools.zkClient.ZKClientBuilder;

import java.util.ArrayList;
import java.util.List;

public class StateGovernment {

    public static String Nation = "/nationName";
    public static String LAND = Nation + "/land";
    public static String PEOPLE = Nation + "/people";
    public static List<NationLand> landNodeCache = new ArrayList<>();
    public static List<Human> humanNodeCache = new ArrayList<>();

    private ZKClient zkClient;
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
        buildNation();
    }

    private void buildNation() {

        lookupNationLand();

        lookupPopulation();
        
    }

    private void lookupPopulation() {
        boolean populationExist = this.zkClient.exists(PEOPLE);
        StaticLog.info("[lookupPopulation] exist:" + populationExist);
        if (!populationExist) {
            this.zkClient.createRecursive(PEOPLE, name, CreateMode.PERSISTENT);
        }
        this.zkClient.listenChildDataChanges(PEOPLE, new PopulationChangeEvent());

    }

    private void lookupNationLand() {
        boolean landExist = this.zkClient.exists(LAND);
        StaticLog.info("[lookupNationLand] exist:" + landExist);
        if (!landExist) {
            this.zkClient.createRecursive(LAND, name, CreateMode.PERSISTENT);
        }
        this.zkClient.listenChildDataChanges(LAND, new NationLandChangeEvent());
    }
}
