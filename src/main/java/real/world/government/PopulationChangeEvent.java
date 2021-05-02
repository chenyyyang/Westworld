package real.world.government;

import cn.hutool.log.StaticLog;
import real.world.tools.zkClient.listener.ZKChildDataListener;

import java.util.List;

public class PopulationChangeEvent extends ZKChildDataListener {

    private VillageOfficial villageOfficial;

    public PopulationChangeEvent(VillageOfficial villageOfficial) {
        this.villageOfficial = villageOfficial;
    }

    //会话过期
    @Override
    public void handleSessionExpired(String path, Object data) throws Exception {
        StaticLog.debug("[PopulationChangeEvent]" + path + "  lost connection:" + data);
    }

    //子节点数据发生改变
    @Override
    public void handleChildDataChanged(String path, Object data) throws Exception {
        StaticLog.error("[PopulationChangeEvent]:" + path + " data changed:" + data);
    }

    //子节点数量发生改变
    @Override
    public void handleChildCountChanged(String path, List<String> children) throws Exception {
        StaticLog.error("[PopulationChangeEvent]:" + path + " now population:" + children);
        List<String> nationLand = villageOfficial.listNationLand();
        if (nationLand.size() < 1) {
            StaticLog.error("[PopulationChangeEvent]: no land to assign  ");
            return;
        }
        villageOfficial.reassigning();
    }
}
