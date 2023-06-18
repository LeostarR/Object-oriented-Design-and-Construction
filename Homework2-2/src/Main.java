import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.ElevatorRequest;
import com.oocourse.elevator2.MaintainRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        TimableOutput.initStartTimestamp();
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        ArrayList<PassengerQueue> elevatorQueues = new ArrayList<>();
        PassengerQueue waitQueue = new PassengerQueue(0);
        PassengerQueue table = new PassengerQueue(-1);
        for (int i = 1; i <= 6; i++) {
            PassengerQueue parallelQueue = new PassengerQueue(i);
            elevatorQueues.add(parallelQueue);
            Elevator e = new Elevator(i, 1, 6, 0.4, parallelQueue, waitQueue, table);
            e.start();
        }
        Distribute distribute = new Distribute(waitQueue, elevatorQueues, table);
        distribute.start();
        while (true) {
            //TimableOutput.println("loop");
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                break;
            } else {
                if (request instanceof PersonRequest) {          //新增乘客
                    waitQueue.addPassenger((PersonRequest) request);
                    table.addPassenger((PersonRequest) request);
                } else if (request instanceof ElevatorRequest) { //新增电梯
                    PassengerQueue parallelQueue
                            = new PassengerQueue(((ElevatorRequest) request).getElevatorId());
                    elevatorQueues.add(parallelQueue);
                    Elevator e = new Elevator(((ElevatorRequest) request).getElevatorId(),
                                              ((ElevatorRequest) request).getFloor(),
                                              ((ElevatorRequest) request).getCapacity(),
                                              ((ElevatorRequest) request).getSpeed(),
                                              parallelQueue, waitQueue, table);
                    e.start();
                } else if (request instanceof MaintainRequest) { //新增维护
                    for (int i = 0;i < elevatorQueues.size();i++) {
                        if (elevatorQueues.get(i).getId()
                                == ((MaintainRequest) request).getElevatorId()) {
                            PassengerQueue passengerQueue = elevatorQueues.get(i);
                            passengerQueue.setMaintainSymbol(1);
                            elevatorQueues.remove(passengerQueue);
                            break;
                        }
                    }
                    //distribute.removeElevatorQueues(((MaintainRequest) request).getElevatorId());
                }
            }
        }
        waitQueue.setEnd(true);
        elevatorInput.close();
    }
}