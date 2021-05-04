package real.world.performance;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;
import cn.hutool.log.StaticLog;

import java.util.concurrent.LinkedBlockingQueue;

public class PerformanceTestingConsole {

    LinkedBlockingQueue<Long> linkedBlockingQueue = new LinkedBlockingQueue(1000);

    Thread consumer;

    SimpleDataSource dataSource;

    int totalTask = 0;
    int successCount = 0;
    long totalDelayTimeMs = 0;

    public PerformanceTestingConsole(SimpleDataSource dataSource) {

        this.dataSource = dataSource;
        consumer = new Thread(() -> {
            //消费和记录的线程
            try {
                while (true) {
                    Long id = linkedBlockingQueue.take();
                    if (id != null) {
                        Db.use(dataSource).update(Entity.create().set("status", 1), //修改的数据
                                Entity.create("farmland").set("id", id) //where条件
                        );
                        System.out.println(id + ":update 1");
                    }
                }
            } catch (Exception e) {
                StaticLog.error("consumer error:" + ExceptionUtil.stacktraceToString(e));
            }

        });
        consumer.start();
    }

    public void trigger(Entity entity) {
        totalTask++;
        try {
            Long fire_time = entity.getLong("fire_time");
            Long id = entity.getLong("id");
            linkedBlockingQueue.put(id);
            long abs = Math.abs(fire_time * 1000 - System.currentTimeMillis());
            System.out.println("task id:" + id + " abs:" + abs);
            totalDelayTimeMs += abs;
            successCount++;
        } catch (Throwable e) {
            StaticLog.error(e);
        }
        StaticLog.info("successCount[{}] totalTask[{}] totalDelayTimeMs[{}]", successCount, totalTask, totalDelayTimeMs);
    }
}
