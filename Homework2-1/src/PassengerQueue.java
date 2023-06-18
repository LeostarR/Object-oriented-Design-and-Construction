import java.util.ArrayList;

public class PassengerQueue {
    private final ArrayList<Passenger> passengers;
    private boolean isEnd;

    public PassengerQueue() {
        passengers = new ArrayList<>();
        this.isEnd = false;
    }

    public synchronized void addPassenger(Passenger p) {
        passengers.add(p);
        notifyAll();
    }

    public synchronized ArrayList<Passenger> getList() {
        notifyAll();
        return this.passengers;
    }

    public synchronized void removePassenger(Passenger p) {
        passengers.remove(p);
        notifyAll();
    }

    public synchronized Passenger getOnePassenger() { //按时间取出
        if (passengers.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (passengers.isEmpty()) {
            return null;
        }
        Passenger p = passengers.get(0);
        passengers.remove(0);
        notifyAll();
        return p;
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
        notifyAll();
    }

    public synchronized boolean isEnd() {
        notifyAll();
        return isEnd;
    }

    public synchronized boolean isEmpty() {
        notifyAll();
        return passengers.isEmpty();
    }
}
