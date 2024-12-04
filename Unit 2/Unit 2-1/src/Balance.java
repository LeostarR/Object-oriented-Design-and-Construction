import java.util.ArrayList;
import java.util.HashMap;

public class Balance { //可变托盘 PassengerQueue
    private boolean end = false;

    private final ArrayList<Passenger> list = new ArrayList<>();

    private final HashMap<Integer, ArrayList<Passenger>> table = new HashMap<>();

    private final HashMap<Integer, Integer> occupyMap = new HashMap<>();

    private final HashMap<Integer, Integer> openMap = new HashMap<>();

    public Balance() {
        for (int i = 1;i <= 11;i++) {
            occupyMap.put(i, 0);
            openMap.put(i, 0);
        }
    }

    public HashMap<Integer, Integer> getOccupyMap() {
        //notifyAll();
        return this.occupyMap;
    }

    public HashMap<Integer, Integer> getOpenMap() {
        //notifyAll();
        return this.openMap;
    }

    public synchronized void setOpenMap(Integer i, Integer j) {
        this.openMap.replace(i, j);
        notifyAll();
    }

    public synchronized void setBusy(int index) {
        this.occupyMap.replace(index, 1);
        notifyAll();
    }

    public synchronized void setFree(int index) {
        this.occupyMap.replace(index, 0);
        notifyAll();
    }

    public boolean isEmpty() {
        //notifyAll();
        return this.list.isEmpty();
    }

    public HashMap<Integer, ArrayList<Passenger>> getHash() {
        //notifyAll();
        return this.table;
    }

    public synchronized void setEnd(boolean end) {
        this.end = end;
        notifyAll();
    }

    public boolean isEnd() {
        //notifyAll();
        return this.end;
    }

    public void distribute() { //更新楼层表
        table.clear();
        for (Passenger p : this.list) {
            ArrayList<Passenger> l;
            if (table.containsKey(p.getFromFloor())) {
                l = table.get(p.getFromFloor());
            } else {
                l = new ArrayList<>();
            }
            l.add(p);
            table.put(p.getFromFloor(), l);
        }
    }

    public synchronized void inPassenger(Passenger p) { //进入天平（等候队列）
        this.list.add(p);
        distribute();
        notifyAll();
    }

    public synchronized void outPassenger(Passenger p) { //离开天平，进入电梯
        if (this.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (this.list.isEmpty()) {
            return;
        }
        this.list.remove(p);
        distribute();
        notifyAll();
    }

}
