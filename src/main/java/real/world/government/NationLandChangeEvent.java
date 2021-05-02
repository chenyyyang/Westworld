package real.world.government;

import cn.hutool.log.StaticLog;
import real.world.tools.zkClient.listener.ZKChildDataListener;

import java.util.List;

public class NationLandChangeEvent extends ZKChildDataListener {

    private VillageOfficial villageOfficial;

    public NationLandChangeEvent(VillageOfficial villageOfficial) {
        this.villageOfficial = villageOfficial;
    }

    //会话过期
    @Override
    public void handleSessionExpired(String path, Object data) throws Exception {
        StaticLog.debug("[NationLandChangeEvent]" + path + "  lost connection:" + data);
    }

    //子节点数据发生改变
    @Override
    public void handleChildDataChanged(String path, Object data) throws Exception {
        StaticLog.error("[NationLandChangeEvent]:" + path + " data changed:" + data);
    }

    //子节点数量发生改变
    @Override
    public void handleChildCountChanged(String path, List<String> children) throws Exception {
        StaticLog.error("[NationLandChangeEvent]:" + path + "  land rise wide now:" + children);
        List<String> villageOfficial = this.villageOfficial.listVillageOfficial();
        if (villageOfficial.size() == 0) {
            return;
        }
        this.villageOfficial.reassigning();
    }
}
