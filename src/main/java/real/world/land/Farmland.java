package real.world.land;

import cn.hutool.db.ds.simple.SimpleDataSource;
import lombok.Data;

@Data
public class Farmland implements NationLand {

    String type = "Mysql-database";

    SimpleDataSource dataSource;

    FarmlandInfo info;

    public Farmland(FarmlandInfo info) {
        this.info = info;
        SimpleDataSource simpleDataSource = new SimpleDataSource(info.getUrl(), info.getUsername(), info.getPassword());
        dataSource = simpleDataSource;
    }

    /*保存一些元数据到zk*/
    @Data
    public static class FarmlandInfo {
        private String farmlandName = "laoxiangji_park";
        private String url = "jdbc:mysql://localhost:3306/westworld?useUnicode=true&characterEncoding=UTF-8";
        private String username = "root";
        private String password = "12345678";
    }
}
