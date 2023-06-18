import java.util.ArrayList;

public class Controller extends Thread {
    private final PassengerQueue waitQueue;
    private final ArrayList<PassengerQueue> elevatorQueues;

    public Controller(PassengerQueue waitQueue, ArrayList<PassengerQueue> elevatorQueues) {
        this.waitQueue = waitQueue;
        this.elevatorQueues = elevatorQueues;
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            if (waitQueue.isEmpty() && waitQueue.isEnd()) {
                for (PassengerQueue elevatorQueue : elevatorQueues) {
                    elevatorQueue.setEnd(true);
                }
                return;
            }
            Passenger passenger = waitQueue.getOnePassenger();
            if (passenger == null) {
                continue;
            }
            elevatorQueues.get(i).addPassenger(passenger);
            i = (i + 1) % 6;
        }
    }
}
