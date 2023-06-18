import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;
import com.oocourse.elevator1.TimableOutput;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        TimableOutput.initStartTimestamp();
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        PassengerQueue waitQueue = new PassengerQueue();
        ArrayList<PassengerQueue> elevatorQueues = new ArrayList<>();
        //Balance balance = new Balance();
        for (int i = 1; i <= 6; i++) {
            PassengerQueue parallelQueue = new PassengerQueue();
            elevatorQueues.add(parallelQueue);
            Elevator e = new Elevator(i, parallelQueue);
            e.start();
        }
        Controller controller = new Controller(waitQueue, elevatorQueues);
        controller.start();
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            if (request == null) {
                break;
            } else {
                Passenger passenger = new Passenger(request.getPersonId(),
                        request.getFromFloor(), request.getToFloor());
                waitQueue.addPassenger(passenger);
            }
        }
        waitQueue.setEnd(true);
        elevatorInput.close();
    }
}