import java.util.HashMap;

public class LogisticsDivision {
    private final HashMap<String, Integer> repairCounter = new HashMap<>();

    public LogisticsDivision() {
    }

    public void repairBook(String date, String category) {
        repairCounter.compute(category, (k, v) -> v == null ? 1 : v + 1);
        System.out.println(date + " " + category + " got repaired by logistics division");
    }

    public HashMap<String, Integer> getRepairCounter() {
        return this.repairCounter;
    }
}
