package real.world.land;

import cn.hutool.db.ds.simple.SimpleDataSource;
import lombok.Data;

import javax.sql.DataSource;

@Data
public class Farmland implements NationLand {

    String type = "Mysql-database";

    DataSource dataSource;

    public Farmland(FarmlandInfo info) {
        SimpleDataSource simpleDataSource = new SimpleDataSource();
        simpleDataSource.init(info.getUrl(), info.getUsername(), info.getPassword());
        dataSource = simpleDataSource;
    }

    /*保存一些元数据到zk*/
    @Data
    public class FarmlandInfo {
        private String farmlandName = "laoxiangji_park";
        private String url = "jdbc:mysql://localhost:3306/westworld";
        private String username = "root";
        private String password = "12345678";
    }
}
