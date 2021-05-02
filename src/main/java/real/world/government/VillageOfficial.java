package real.world.government;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import lombok.Data;
import org.apache.zookeeper.CreateMode;
import real.world.land.Farmland;
import real.world.people.Farmer;
import real.world.tools.zkClient.ZKClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class VillageOfficial {
    public static String Nation = "/nationName";

    public static String LAND = Nation + "/land";
    public static String PEOPLE = Nation + "/official";

    public static Map<String, Farmer> localPeople = new ConcurrentHashMap<>();

    public String name;

    private ZKClient zkClient;

    public VillageOfficial(ZKClient zkClient) {
        String localhostStr = NetUtil.getLocalhostStr() + "_" + NetUtil.getUsableLocalPort();
        this.zkClient = zkClient;
        name = "official" + localhostStr;
        //注册临时节点
        this.zkClient.createRecursive(PEOPLE + "/" + name,
                localhostStr,
                CreateMode.EPHEMERAL);

    }

    public void lookupPopulation() {
        boolean populationExist = this.zkClient.exists(PEOPLE);
        StaticLog.info("[lookupPopulation] exist:" + populationExist);
        if (!populationExist) {
            this.zkClient.createRecursive(PEOPLE, name, CreateMode.PERSISTENT);
        }
        this.zkClient.listenChildDataChanges(PEOPLE, new PopulationChangeEvent(this));

    }

    public void lookupNationLand() {
        boolean landExist = this.zkClient.exists(LAND);
        StaticLog.info("[lookupNationLand] exist:" + landExist);
        if (!landExist) {
            this.zkClient.createRecursive(LAND, name, CreateMode.PERSISTENT);
        }
        this.zkClient.listenChildDataChanges(LAND, new NationLandChangeEvent(this));
    }

    public Boolean registeFarmland(Farmland.FarmlandInfo farmlandInfo) {

        String farmlandName = farmlandInfo.getFarmlandName();

        boolean landExist = this.zkClient.exists(LAND + "/" + farmlandName);

        StaticLog.info("[registeFarmland] " + farmlandName + " exist:" + landExist);

        if (!landExist) {
            this.zkClient.createRecursive(LAND + "/" + farmlandName,
                    JSONUtil.toJsonStr(farmlandInfo),
                    CreateMode.PERSISTENT);
            return true;
        }
        return false;
    }

   /* private volatile int value;

    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                    (Official.class.getDeclaredField("value"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }*/

    public void reassigning() {
        try {
            StaticLog.error("[global rebalance] start...");

            List<String> nationLands = listNationLand();
            List<String> people = listPeople();

            if (people.size() < nationLands.size()) {
                StaticLog.error("[underpopulation error]");
            }
            for (Map.Entry<String, Farmer> entry : localPeople.entrySet()) {
                String farmerName = entry.getKey();
                int index = people.indexOf(farmerName);
                if (index > nationLands.size() - 1) {
                    //现在没活可干了
                    continue;
                }

                String nationLand = nationLands.get(index);
                Object data = zkClient.getData(LAND + "/" + nationLand);

                Farmland.FarmlandInfo info = JSONUtil.toBean(data.toString(), Farmland.FarmlandInfo.class);
                Farmland farmland = new Farmland(info);

                Farmer farmer = entry.getValue();
                farmer.prepareWork(farmland);
                farmer.start();
                localPeople.put(farmerName, farmer);
            }

        } catch (Throwable e) {
            StaticLog.error("[reassigning]error:", ExceptionUtil.stacktraceToString(e));
        } finally {
        }

    }


    public List<String> listPeople() {
        return zkClient.getChildren(PEOPLE, false);
    }

    public List<String> listNationLand() {
        return zkClient.getChildren(LAND, false);
    }
}
