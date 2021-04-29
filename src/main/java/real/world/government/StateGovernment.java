package real.world.government;

import org.apache.zookeeper.CreateMode;
import real.world.tools.zkClient.ZKClient;
import real.world.tools.zkClient.ZKClientBuilder;
import real.world.tools.zkClient.listener.ZKChildDataListener;

import java.util.List;

public class StateGovernment {

    public static String Nation = "/nationName";
    public static String LAND = "/land";
    public static String PEOPLE = "/people";

    private ZKClient zkClient;
    private String name = this.getClass().getSimpleName();

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
        this.zkClient.createRecursive(Nation + LAND, name, CreateMode.PERSISTENT);
        this.zkClient.createRecursive(Nation + PEOPLE, name, CreateMode.PERSISTENT);

        this.zkClient.listenChildDataChanges(Nation + LAND, new ZKChildDataListener() {
            //会话过期
            @Override
            public void handleSessionExpired(String path, Object data) throws Exception {
                System.out.println("children:" + data);
            }

            //子节点数据发生改变
            @Override
            public void handleChildDataChanged(String path, Object data) throws Exception {
                System.out.println("the child data is changed:[path:" + path + ",data:" + data + "]");
            }

            //子节点数量发生改变
            @Override
            public void handleChildCountChanged(String path, List<String> children) throws Exception {
                System.out.println("children:" + children);
            }
        });

        this.zkClient.listenChildDataChanges(Nation + LAND, new ZKChildDataListener() {
            //会话过期
            @Override
            public void handleSessionExpired(String path, Object data) throws Exception {
                System.out.println("children:" + data);
            }

            //子节点数据发生改变
            @Override
            public void handleChildDataChanged(String path, Object data) throws Exception {
                System.out.println("the child data is changed:[path:" + path + ",data:" + data + "]");
            }

            //子节点数量发生改变
            @Override
            public void handleChildCountChanged(String path, List<String> children) throws Exception {
                System.out.println("children:" + children);
            }
        });
    }
}
