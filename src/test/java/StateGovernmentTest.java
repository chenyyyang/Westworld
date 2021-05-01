import cn.hutool.log.LogFactory;
import cn.hutool.log.dialect.log4j.Log4jLogFactory;
import real.world.government.StateGovernment;

import java.util.concurrent.CountDownLatch;

public class StateGovernmentTest {

    public static void main(String[] args) throws InterruptedException {

        LogFactory.setCurrentLogFactory(new Log4jLogFactory());

        StateGovernment government = new StateGovernment();

        new CountDownLatch(1).await();
    }
}
