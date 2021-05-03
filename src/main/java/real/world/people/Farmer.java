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
        super("t_workFor_" + name);
        farmerName = "workFor_" + name;
    }

    @Override
    public void run() {
        StaticLog.info("Farmer [{}] start success~gogogo", farmerName);

        for (int i = 0; ; i++) {
            StaticLog.error("Farmer [{}] isInterrupted[{}]", farmerName, isInterrupted());
            if (isInterrupted()) {
                StaticLog.error("Farmer [{}] Interrupted", farmerName);
                break;
            }
            long beginTime = System.currentTimeMillis();

            doWork();

            long costTime = System.currentTimeMillis() - beginTime;
            StaticLog.error("Farmer [{}] workLoop[{}] Cost {} ms ", farmerName, i, costTime);
            try {
                TimeUnit.MILLISECONDS.sleep(10 * 1000 - costTime);
            } catch (InterruptedException e) {
                StaticLog.error("Farmer [{}] Interrupted", farmerName);
                break;
            }
        }

        afterWork();

    }

    private void doWork() {
        try {
            TimeUnit.MILLISECONDS.sleep(1000);

        } catch (Throwable throwable) {
        }

    }

    public void afterWork() {
        StaticLog.error("Farmer [{}] off work", farmerName);
        SimpleDataSource dataSource = farmland.getDataSource();
        if (dataSource != null) {
            dataSource.close();
        }

    }

    public void prepareWork(Farmland farmland) {
        this.farmland = farmland;
    }
}
