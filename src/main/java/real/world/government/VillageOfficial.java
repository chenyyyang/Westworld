package real.world.government;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import lombok.Data;
import org.apache.zookeeper.CreateMode;
import real.world.land.Farmland;
import real.world.people.Farmer;
import real.world.tools.CasUtil;
import real.world.tools.zkClient.ZKClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Data
public class VillageOfficial {
    public static String Nation = "/nationName";

    public static String LAND = Nation + "/land";
    public static String OFFICIAL = Nation + "/official";

    public static Map<String, Farmer> localFarmer = new ConcurrentHashMap<>();

    public String name;

    private ZKClient zkClient;

    public VillageOfficial(ZKClient zkClient) {
        this.zkClient = zkClient;
    }

    public void registeSelf() {
        String localhostStr = NetUtil.getLocalhostStr() + "_" + NetUtil.getUsableLocalPort();
        //注册临时节点
        String newPath = this.zkClient.createEphemerale(OFFICIAL + "/1",
                localhostStr, true);
        String[] paths = newPath.split("/");
        name = paths[paths.length - 1];

    }

    public void lookupPopulation() {
        boolean populationExist = this.zkClient.exists(OFFICIAL);
        StaticLog.info("[lookupPopulation] exist:" + populationExist);
        if (!populationExist) {
            this.zkClient.createRecursive(OFFICIAL, name, CreateMode.PERSISTENT);
        }
        this.zkClient.listenChildDataChanges(OFFICIAL, new OfficialChangeEvent(this));

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


    public void reassigning() {
        StaticLog.error("[global rebalance] start waiting");
        if (!CasUtil.tryOnce()) {
            StaticLog.error("[global rebalance] reject ");
            return;
        }
        try {

            //5s拒绝其他 rebalance请求。
            TimeUnit.SECONDS.sleep(5);

            StaticLog.error("[global rebalance] begin");
            List<String> nationLands = listNationLand();
            List<String> villageOfficial = listVillageOfficial();
            Collections.sort(villageOfficial);

            int villageOfficialCount = villageOfficial.size();
            int selfIndex = villageOfficial.indexOf(this.name);

            for (String nationLand : nationLands) {
                int hashValue = nationLand.hashCode() % villageOfficialCount;

                if (hashValue == selfIndex) {
                    StaticLog.error(" {} hashValue[{}] self[{}]Index[{}] ->hireFarmerForLand", nationLand, hashValue, this.name, selfIndex);
                    Farmer farmer = hireFarmerForLand(nationLand);
                    farmer.start();
                    localFarmer.put(nationLand, farmer);
                }
            }

        } catch (Throwable e) {
            StaticLog.error("[reassigning]error:" + e.getCause().getMessage(), ExceptionUtil.stacktraceToString(e));
        } finally {
            CasUtil.reset();
        }

    }

    private Farmer hireFarmerForLand(String nationLand) {
        Object data = zkClient.getData(LAND + "/" + nationLand);
        Farmland.FarmlandInfo info = JSONUtil.toBean(data.toString(), Farmland.FarmlandInfo.class);
        Farmland farmland = new Farmland(info);
        Farmer farmer = new Farmer(nationLand);
        farmer.prepareWork(farmland);
        return farmer;
    }

    public List<String> listVillageOfficial() {
        return zkClient.getChildren(OFFICIAL, false);
    }

    public List<String> listNationLand() {
        return zkClient.getChildren(LAND, false);
    }
}
