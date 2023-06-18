import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.MaintainRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;
import com.oocourse.elevator3.TimableOutput;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        TimableOutput.initStartTimestamp();
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        ArrayList<PassengerQueue> elevatorQueues = new ArrayList<>();
        PassengerQueue waitQueue = new PassengerQueue(0);
        PassengerQueue table = new PassengerQueue(-1);
        StateMap stateMap = new StateMap();
        for (int i = 1; i <= 6; i++) {
            PassengerQueue parallelQueue = new PassengerQueue(i);
            elevatorQueues.add(parallelQueue);
            Elevator e = new Elevator(i, 1, 6, 0.4,
                    parallelQueue, waitQueue, table, stateMap);
            e.start();
        }
        Distribute distribute = new Distribute(waitQueue, elevatorQueues, stateMap, table);
        distribute.start();
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                break;
            } else {
                if (request instanceof PersonRequest) {          //新增乘客
                    Passenger passenger = new Passenger(((PersonRequest) request).getFromFloor(),
                            ((PersonRequest) request).getToFloor(),
                            ((PersonRequest) request).getPersonId());
                    waitQueue.addPassenger(passenger);
                    table.addPassenger(passenger);
                } else if (request instanceof ElevatorRequest) { //新增电梯
                    PassengerQueue parallelQueue
                            = new PassengerQueue(((ElevatorRequest) request).getElevatorId());
                    elevatorQueues.add(parallelQueue);
                    stateMap.addAccess(((ElevatorRequest) request).getAccess(),
                            ((ElevatorRequest) request).getElevatorId());
                    Elevator e = new Elevator(((ElevatorRequest) request).getElevatorId(),
                            ((ElevatorRequest) request).getFloor(),
                            ((ElevatorRequest) request).getCapacity(),
                            ((ElevatorRequest) request).getSpeed(),
                            parallelQueue, waitQueue, table, stateMap);
                    e.start();
                } else if (request instanceof MaintainRequest) { //新增维护
                    for (int i = 0;i < elevatorQueues.size();i++) {
                        if (elevatorQueues.get(i).getId()
                                == ((MaintainRequest) request).getElevatorId()) {
                            stateMap.removeAccess(((MaintainRequest) request).getElevatorId());
                            PassengerQueue passengerQueue = elevatorQueues.get(i);
                            passengerQueue.setMaintainSymbol(1);
                            elevatorQueues.remove(passengerQueue);
                            break;
                        }
                    }
                }
            }
        }
        waitQueue.setEndFlag(1);
        elevatorInput.close();
    }
}