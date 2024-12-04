import java.util.HashMap;

public class ArrangingLibrarian {
    private final HashMap<String, Integer> arCounter = new HashMap<>();

    public ArrangingLibrarian() {
    }

    public void arrange(BorrowingAndReturningLibrarian brLibrarian, ServiceMachine machine,
                        LogisticsDivision ldRepair, OrderLibrarian orderLibrarian) {
        this.collectBooks(brLibrarian.getBrCounter());
        this.collectBooks(machine.getMachineCounter());
        this.collectBooks(ldRepair.getRepairCounter());
        orderLibrarian.getBooks(this.arCounter);
    }

    public void collectBooks(HashMap<String, Integer> counter) {
        counter.forEach((key, value) ->
                this.arCounter.compute(key, (k, v) -> v == null ? value : v + value));
        counter.clear();
    }

    public void putBooksToLib(HashMap<String, Integer> lib) {
        this.arCounter.forEach((key, value) ->
                lib.compute(key, (k, v) -> v == null ? value : v + value));
        this.arCounter.clear();
    }
}
