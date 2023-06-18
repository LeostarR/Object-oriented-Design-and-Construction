import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Objects;

public class OrderLibrarian {
    private final ArrayList<String[]> orderList = new ArrayList<>();//String[]={date, id, category}
    private final HashMap<String, Integer> orderCounter = new HashMap<>();

    public OrderLibrarian() {
    }

    public void order(String date, String id, String category, Student student) {
        int sum = 0;
        for (String[] parts : orderList) {
            if (Objects.equals(parts[1], id) && Objects.equals(parts[2], category)) {
                return;
            }
            if (Objects.equals(parts[0], date) && Objects.equals(parts[1], id)) {
                sum++;
            }
        }
        if (sum >= 3) {
            return;
        }
        boolean accept = (category.charAt(0) == 'A') ||
                (category.charAt(0) == 'B' && student.notHaveBookB()) ||
                (category.charAt(0) == 'C' && student.notHaveThisBookC(category));
        if (accept) {
            orderList.add(new String[]{date, id, category});
            System.out.println(date + " " +  id + " ordered " +
                    category + " from ordering librarian");
        }
    }

    public void getBooks(HashMap<String, Integer> arCounter) {
        for (String[] info: orderList) {
            String category = info[2];
            if (arCounter.containsKey(category) && arCounter.get(category) > 0) {
                this.orderCounter.compute(category, (k, v) -> v == null ? 1 : v + 1);
                arCounter.computeIfPresent(category, (k, v) -> v - 1);
            }
        }
    }

    public void sendBookToStudents(HashMap<String, Student> students, String date) {
        ArrayList<String[]> deleteList = new ArrayList<>();
        for (String[] parts: orderList) {
            String category = parts[2];
            if (this.orderCounter.containsKey(category) && this.orderCounter.get(category) > 0
                    && !deleteList.contains(parts)) {
                String id = parts[1];
                Student student = students.get(id);
                if (category.charAt(0) != 'A') {
                    student.getOrderBook(category);
                    System.out.println(date + " " + id + " borrowed " +
                            category + " from ordering librarian");
                    this.orderCounter.computeIfPresent(category, (k, v) -> v - 1);
                    if (category.charAt(0) == 'B') {
                        this.cancelOrderB(id, category, deleteList);
                    }
                }
                deleteList.add(parts);
            }
        }
        this.removeOrder(deleteList);
    }

    public void cancelOrderB(String id, String category, ArrayList<String[]> deleteList) {
        ListIterator<String[]> iterator = this.orderList.listIterator();
        while (iterator.hasNext()) {
            String[] element = iterator.next();
            String id1 = element[1];
            String category1 = element[2];
            if (Objects.equals(id1, id)
                    && category1.charAt(0) == 'B'
                    && !Objects.equals(category1, category)) {
                deleteList.add(element);
            }
        }
    }

    public void cancelAllOrderB(String id) {
        ListIterator<String[]> iterator = this.orderList.listIterator();
        while (iterator.hasNext()) {
            String[] element = iterator.next();
            String id1 = element[1];
            String category1 = element[2];
            if (Objects.equals(id1, id)
                    && category1.charAt(0) == 'B') {
                iterator.remove();
            }
        }
    }

    public void removeOrder(ArrayList<String[]> deleteList) {
        for (String[] parts: deleteList) {
            this.orderList.remove(parts);
        }
    }

    public void sendRemainingBookBack(ArrangingLibrarian arLibrarian) {
        arLibrarian.collectBooks(this.orderCounter);
    }
}
