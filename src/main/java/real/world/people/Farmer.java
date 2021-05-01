package real.world.people;

import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.log.StaticLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import real.world.land.Farmland;

import java.util.concurrent.TimeUnit;

@Data
@EqualsAndHashCode(of = {"farmerName"})
public class Farmer extends Thread {

    String farmerName;

    Farmland farmland;

    public Farmer(String name) {
        super("t-" + name);
        farmerName = name;
    }

    @Override
    public void run() {

        while (true) {

            try {
                TimeUnit.SECONDS.sleep(3600);
            } catch (InterruptedException e) {
                StaticLog.error("Farmer [{}] Interrupted", farmerName);
                break;
            }

        }

        afterWork();

    }

    public void afterWork() {
        SimpleDataSource dataSource = farmland.getDataSource();
        if (dataSource != null) {
            dataSource.close();
        }

    }

    public void prepareWork(Farmland farmland) {
        this.farmland = farmland;
    }
}
