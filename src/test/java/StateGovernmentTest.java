import real.world.government.StateGovernment;

import java.util.concurrent.CountDownLatch;

public class StateGovernmentTest {

    public static void main(String[] args) throws InterruptedException {

        StateGovernment government = StateGovernment.get();

        new CountDownLatch(1).await();
    }
}
