package real.world.land;

import cn.hutool.db.ds.simple.SimpleDataSource;
import lombok.Data;

import javax.sql.DataSource;

public class Farmland implements NationLand {

    String type = "Mysql-database";

    DataSource dataSource;

    public Farmland(FarmlandInfo info) {

        SimpleDataSource simpleDataSource = new SimpleDataSource();
        simpleDataSource.init(info.url, info.username, info.password);
        dataSource = simpleDataSource;
    }

    /*保存一些元数据到zk*/
    @Data
    public class FarmlandInfo {
        String farmlandName = "laoxiangji_park";
        String url = "jdbc:mysql://localhost:3306/westworld";
        String username = "root";
        String password = "12345678";
    }
}
