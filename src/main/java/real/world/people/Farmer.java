package real.world.people;

import real.world.land.Farmland;

import java.util.concurrent.TimeUnit;

public class Farmer implements Human {

    Farmland farmland;

    private void prepare() {

    }

    @Override
    public void run() {

        prepare();

        while (true) {

            try {
                TimeUnit.SECONDS.sleep(3600);
            } catch (InterruptedException e) {
                break;
            }
            
        }

        afterWork();

    }

    private void afterWork() {
    }

}
