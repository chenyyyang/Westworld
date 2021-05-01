package real.world.government;

import cn.hutool.log.StaticLog;
import org.apache.zookeeper.CreateMode;
import real.world.land.NationLand;
import real.world.people.Human;
import real.world.tools.zkClient.ZKClient;

import java.util.ArrayList;
import java.util.List;

public class Official {

    public static String Nation = "/nationName";
    public static String LAND = Nation + "/land";
    public static String PEOPLE = Nation + "/people";

    public static List<NationLand> landNodeCache = new ArrayList<>();
    public static List<Human> humanNodeCache = new ArrayList<>();

    public String name = "Official.Zhangs";

    private ZKClient zkClient;

    public Official(ZKClient zkClient) {
        this.zkClient = zkClient;
    }

    public void lookupPopulation() {
        boolean populationExist = this.zkClient.exists(PEOPLE);
        StaticLog.info("[lookupPopulation] exist:" + populationExist);
        if (!populationExist) {
            this.zkClient.createRecursive(PEOPLE, name, CreateMode.PERSISTENT);
        }
        this.zkClient.listenChildDataChanges(PEOPLE, new PopulationChangeEvent());

    }

    public void lookupNationLand() {
        boolean landExist = this.zkClient.exists(LAND);
        StaticLog.info("[lookupNationLand] exist:" + landExist);
        if (!landExist) {
            this.zkClient.createRecursive(LAND, name, CreateMode.PERSISTENT);
        }
        this.zkClient.listenChildDataChanges(LAND, new NationLandChangeEvent());
    }

}
