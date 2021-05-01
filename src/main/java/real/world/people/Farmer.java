package real.world.people;

import lombok.Data;
import real.world.land.Farmland;

import java.util.concurrent.TimeUnit;

@Data
public class Farmer implements Human {

    String farmerName;

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
