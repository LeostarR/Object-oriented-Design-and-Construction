import java.util.HashMap;

public class BorrowingAndReturningLibrarian {
    private final HashMap<String, Integer> brCounter = new HashMap<>();

    public BorrowingAndReturningLibrarian() {
    }

    public void borrowBookB(String date, String id, String category, Student student,
                            HashMap<String, Integer> lib, OrderLibrarian orLibrarian) {
        lib.computeIfPresent(category, (k, v) -> v - 1);
        if (student.notHaveBookB()) {
            student.borrowBookB(category);
            orLibrarian.cancelAllOrderB(id);
            System.out.println(date + " " +  id + " borrowed " + category
                    + " from borrowing and returning librarian");
        } else {
            this.brCounter.compute(category, (k, v) -> v == null ? 1 : v + 1);
        }
    }

    public void returnBookB(String date, String id, String category,
                            Student student, LogisticsDivision ldRepair) {
        student.returnBookB(category);
        if (student.isSmeared(category)) {
            student.removeSmeared(category);
            System.out.println(date + " " + id +
                    " got punished by borrowing and returning librarian");
            System.out.println(date + " " + id + " returned " + category
                    + " to borrowing and returning librarian");
            ldRepair.repairBook(date, category);
        } else {
            System.out.println(date + " " + id + " returned " + category
                    + " to borrowing and returning librarian");
            brCounter.compute(category, (k, v) -> v == null ? 1 : v + 1);
        }
    }

    public void registerLostBook(String date, String id, String category, Student student) {
        student.lostBook(category);
        System.out.println(date + " " + id + " got punished by borrowing and returning librarian");
    }

    public HashMap<String, Integer> getBrCounter() {
        return this.brCounter;
    }
}
