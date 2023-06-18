import java.util.ArrayList;
import java.util.LinkedList;

public class Distribute extends Thread {
    private final PassengerQueue waitQueue;
    private final ArrayList<PassengerQueue> elevatorQueues;
    private final StateMap stateMap;
    private final PassengerQueue table;

    public Distribute(PassengerQueue waitQueue, ArrayList<PassengerQueue> elevatorQueues
            , StateMap stateMap, PassengerQueue table) {
        this.waitQueue = waitQueue;
        this.elevatorQueues = elevatorQueues;
        this.stateMap = stateMap;
        this.table = table;
    }

    @Override
    public void run() {
        int i = 0;
        while (true) {
            if (waitQueue.isEnd() && waitQueue.isEmpty() && table.isEmpty()) {
                for (PassengerQueue elevatorQueue : elevatorQueues) {
                    elevatorQueue.setEnd(true);
                }
                return;
            }
            Passenger passenger = waitQueue.getOnePassenger();
            if (passenger == null) {
                continue;
            }
            LinkedList<Integer> demand = stateMap.findPath(passenger);
            passenger.updateDemand(demand);
            do {
                i = (i + 1) % elevatorQueues.size();
            } while (!(stateMap.canAccess(elevatorQueues.get(i).getId(),  passenger.getDemand()) &&
                    stateMap.canAccess(elevatorQueues.get(i).getId(), passenger.getFromFloor())));
            elevatorQueues.get(i).addPassenger(passenger);
        }
    }

}
