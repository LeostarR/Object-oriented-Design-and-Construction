import java.util.HashMap;

public class ServiceMachine {
    private final HashMap<String, Integer> machineCounter = new HashMap<>();

    public ServiceMachine() {
    }

    public boolean queryBook(String date, String id, String category,
                             HashMap<String, Integer> lib) {
        System.out.println(date + " " +  id + " queried " + category +
                " from self-service machine");
        return lib.containsKey(category) && lib.get(category) > 0;
    }

    public void borrowBookC(String date, String id, String category,
                            Student student, HashMap<String, Integer> lib) {
        lib.computeIfPresent(category, (k, v) -> v - 1);
        if (student.notHaveThisBookC(category)) {
            student.borrowBookC(category);
            System.out.println(date + " " +  id + " borrowed " + category +
                    " from self-service machine");
        } else {
            machineCounter.compute(category, (k, v) -> v == null ? 1 : v + 1);
        }
    }

    public void returnBookC(String date, String id, String category,
                            Student student, LogisticsDivision ldRepair) {
        student.returnBookC(category);
        if (student.isSmeared(category)) {
            student.removeSmeared(category);
            System.out.println(date + " " +  id +
                    " got punished by borrowing and returning librarian");
            System.out.println(date + " " +  id + " returned " +
                    category + " to self-service machine");
            ldRepair.repairBook(date, category);
        } else {
            System.out.println(date + " " +  id + " returned " +
                    category + " to self-service machine");
            machineCounter.compute(category, (k, v) -> v == null ? 1 : v + 1);
        }
    }

    public HashMap<String, Integer> getMachineCounter() {
        return this.machineCounter;
    }
}
