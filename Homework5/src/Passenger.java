public class Passenger {
    private final int id;
    private final int fromFloor;
    private final int toFloor;

    public Passenger(int id, int fromFloor, int toFloor) {
        this.id = id;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }

    public int getId() {
        return this.id;
    }

    public int getFromFloor() {
        return this.fromFloor;
    }

    public int getToFloor() {
        return this.toFloor;
    }
}
