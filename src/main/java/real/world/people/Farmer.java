package real.world.people;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.log.StaticLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import real.world.land.Farmland;
import real.world.performance.PerformanceTestingConsole;

import java.util.Date;
import java.util.List;
import java.util.Set;
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

            doWork(new Date(beginTime));

            long costTime = System.currentTimeMillis() - beginTime;
            StaticLog.error("Farmer [{}] workLoop[{}] Cost {} ms ", farmerName, i, costTime);
            try {
                TimeUnit.MILLISECONDS.sleep(10 * 60 * 1000 - costTime);
            } catch (InterruptedException e) {
                StaticLog.error("Farmer [{}] Interrupted", farmerName);
                break;
            }
        }

        afterWork();

    }

    private HashedWheelTimer wheelTimer;

    private void doWork(Date date) {
        try {
            SimpleDataSource dataSource = farmland.getDataSource();

            long beginTime = DateUtil.offsetMinute(date, -10).getTime() / 1000;
            long endTime = DateUtil.offsetMinute(date, 10).getTime() / 1000;

            List<Entity> findResult = Db.use(dataSource)
                    .query("select * from farmland where status =0 and fire_time > ? and fire_time < ?",
                            beginTime, endTime);

            StaticLog.info("Farmer [{}] query[{}~{}] count:[{}]  ", farmerName, beginTime, endTime, findResult.size());

            for (Entity entity : findResult) {
                Long fire_time = entity.getLong("fire_time");

                if (fire_time - System.currentTimeMillis() / 1000 < 2) {
                    trigger(entity);
                    continue;
                }
                wheelTimer.newTimeout((Timeout timeout) -> {
                    trigger(entity);
                }, fire_time * 1000 - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            }

        } catch (Throwable e) {
            StaticLog.error("Farmer [{}] work[{}] error : " + ExceptionUtil.stacktraceToString(e), farmerName, date);
        }

    }

    PerformanceTestingConsole performanceTestingConsole;

    private void trigger(Entity entity) {
        performanceTestingConsole.trigger(entity);
    }

    public void afterWork() {
        SimpleDataSource dataSource = farmland.getDataSource();
        if (dataSource != null) {
            dataSource.close();
        }
        Set<Timeout> unprocessedTimeouts = wheelTimer.stop();

        StaticLog.error("Farmer [{}] off work,unprocessedTimeoutsTask[{}]", farmerName, unprocessedTimeouts);
    }

    public void prepareWork(Farmland farmland) {
        this.farmland = farmland;

        this.wheelTimer = new HashedWheelTimer(
                new NamedThreadFactory("Timer_" + farmerName, false),
                200,
                TimeUnit.MILLISECONDS,
                5 * 600
        );
        this.performanceTestingConsole = new PerformanceTestingConsole(farmland.getDataSource());
    }
}
