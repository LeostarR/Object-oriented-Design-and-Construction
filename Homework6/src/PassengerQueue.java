import com.oocourse.elevator2.PersonRequest;
import java.util.ArrayList;

public class PassengerQueue {
    private final ArrayList<PersonRequest> queue;
    private boolean isEnd;
    private final int id;
    private int maintainSymbol = 0;

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

    public synchronized void addPassenger(PersonRequest personRequest) {
        queue.add(personRequest);
        notifyAll();
    }

    public synchronized void addMaintainList(ArrayList<PersonRequest> list) {
        queue.addAll(list);
        notifyAll();
    }

    public synchronized ArrayList<PersonRequest> getList() {
        notifyAll();
        return this.queue;
    }

    public synchronized void clear() {
        this.queue.clear();
        notifyAll();
    }

    public synchronized void removePassenger(PersonRequest p) {
        queue.remove(p);
        notifyAll();
    }

    public synchronized PersonRequest getOnePassenger() { //按时间先后取出(先来后到)
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
        PersonRequest p = queue.get(0);
        queue.remove(0);
        notifyAll();
        return p;
    }
}
