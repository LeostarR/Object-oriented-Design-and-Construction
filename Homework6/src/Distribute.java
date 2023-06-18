import com.oocourse.elevator2.PersonRequest;

import java.util.ArrayList;

public class Distribute extends Thread {
    private final PassengerQueue waitQueue;
    private final ArrayList<PassengerQueue> elevatorQueues;
    private final PassengerQueue table;

    public Distribute(PassengerQueue waitQueue,
                      ArrayList<PassengerQueue> elevatorQueues, PassengerQueue table) {
        this.waitQueue = waitQueue;
        this.elevatorQueues = elevatorQueues;
        this.table = table;
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            //TimableOutput.println("In**********");
            if (waitQueue.isEnd() && waitQueue.isEmpty()) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (!waitQueue.isEmpty()) {
                    continue;
                }
                for (PassengerQueue elevatorQueue : elevatorQueues) {
                    elevatorQueue.setEnd(true);
                }
                return;
            }
            PersonRequest person = waitQueue.getOnePassenger();
            if (person == null) {
                continue;
            }
            //TimableOutput.println("Out**********");
            if (elevatorQueues.size() <= i) {
                i = (i + 1) % elevatorQueues.size();
            }
            elevatorQueues.get(i).addPassenger(person);
            i = (i + 1) % elevatorQueues.size();
        }
    }

}
