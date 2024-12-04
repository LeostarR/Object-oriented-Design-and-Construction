import java.util.HashSet;

public class Student {
    private final String id;
    private final HashSet<String> bookB = new HashSet<>();
    private final HashSet<String> bookC = new HashSet<>();
    private final HashSet<String> smearedBooks = new HashSet<>();

    public Student(String id) {
        this.id = id;
    }

    public void borrowBookB(String category) {
        this.bookB.add(category);
    }

    public void returnBookB(String category) {
        this.bookB.remove(category);
    }

    public boolean notHaveBookB() {
        return this.bookB.isEmpty();
    }

    public void borrowBookC(String category) {
        this.bookC.add(category);
    }

    public void returnBookC(String category) {
        this.bookC.remove(category);
    }

    public boolean notHaveThisBookC(String category) {
        return !this.bookC.contains(category);
    }

    public void getOrderBook(String category) {
        if (category.charAt(0) == 'B') {
            this.borrowBookB(category);
        } else if (category.charAt(0) == 'C') {
            this.borrowBookC(category);
        }
    }

    public void lostBook(String category) {
        if (category.charAt(0) == 'B') {
            this.bookB.remove(category);
        } else if (category.charAt(0) == 'C') {
            this.bookC.remove(category);
        }
    }

    public void smearBook(String category) {
        this.smearedBooks.add(category);
    }

    public boolean isSmeared(String category) {
        return this.smearedBooks.contains(category);
    }

    public void removeSmeared(String category) {
        this.smearedBooks.remove(category);
    }
}
