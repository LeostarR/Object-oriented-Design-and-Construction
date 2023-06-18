import java.util.ArrayList;

public class PassengerQueue {
    private final ArrayList<Passenger> queue;
    private boolean isEnd;
    private final int id;
    private int maintainSymbol = 0;
    private int endFlag = 0;

    public PassengerQueue(int id) {
        this.queue = new ArrayList<>();
        this.isEnd = false;
        this.id = id;
    }

    public synchronized void setMaintainSymbol(int symbol) {
        this.maintainSymbol = symbol;
        notifyAll();
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
        notifyAll();
    }

    public synchronized void setEndFlag(int i) {
        this.endFlag = i;
        notifyAll();
    }

    public int getEndFlag() {
        return this.endFlag;
    }

    public boolean isMaintained() {
        return this.maintainSymbol == 1;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public boolean isEmpty() {
        return queue.size() == 0;
    }

    public int getId() {
        return this.id;
    }

    public synchronized void setMain(int i) {
        for (Passenger p: this.queue) {
            p.setMaintain(i);
        }
        notifyAll();
    }

    public synchronized void addPassenger(Passenger passenger) {
        queue.add(passenger);
        notifyAll();
    }

    public synchronized void addMaintainList(ArrayList<Passenger> list) {
        queue.addAll(list);
        notifyAll();
    }

    public synchronized ArrayList<Passenger> getList() {
        notifyAll();
        return this.queue;
    }

    public synchronized void clear() {
        this.queue.clear();
        notifyAll();
    }

    public synchronized void removePassenger(Passenger passenger) {
        queue.remove(passenger);
        notifyAll();
    }

    public synchronized Passenger getOnePassenger() { //按时间先后取出(先来后到)
        if (queue.isEmpty() && !this.isEnd()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (queue.isEmpty()) {
            return null;
        }
        Passenger p = queue.get(0);
        queue.remove(0);
        notifyAll();
        return p;
    }
}
