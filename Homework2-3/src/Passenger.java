import java.util.LinkedList;

public class Passenger {
    private int fromFloor;
    private final int toFloor;
    private final int personId;
    private final LinkedList<Integer> demand = new LinkedList<>();
    private int maintain = 0;

    public Passenger(int fromFloor, int toFloor, int personId) {
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
        this.personId = personId;
    }

    public int getPersonId() {
        return this.personId;
    }

    public int getFromFloor() {
        return this.fromFloor;
    }

    public int getToFloor() {
        return this.toFloor;
    }

    public int getMaintain() {
        return this.maintain;
    }

    public void setMaintain(int i) {
        this.maintain = i;
    }

    public void setFromFloor(int floor) {
        this.fromFloor = floor;
    }

    public void updateDemand(LinkedList<Integer> list) {
        this.demand.clear();
        this.demand.addAll(list);
    }

    public int getDemand() {
        return this.demand.getFirst();
    }

    public void setDemand() {
        demand.removeFirst();
    }

    public boolean isEmptyDe() {
        return this.demand.isEmpty();
    }
}
