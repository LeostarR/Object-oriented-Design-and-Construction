import java.util.HashSet;
import java.util.Objects;

public class Student {
    private final String schoolName;
    private final String studentName;
    private final HashSet<Book> bs = new HashSet<>();
    private final HashSet<Book> cs = new HashSet<>();
    private final HashSet<Book> smear = new HashSet<>();

    public Student(String schoolName, String studentName) {
        this.schoolName = schoolName;
        this.studentName = studentName;
    }

    public boolean haveBookB() {
        return !this.bs.isEmpty();
    }

    public void borrowB(Book book) {
        this.bs.add(book);
    }

    public Book returnB(String bookName) {
        Book reBook = null;
        for (Book book: bs) {
            if (book.getName().equals(bookName)) {
                reBook = book;
                break;
            }
        }
        this.bs.remove(reBook);
        return reBook;
    }

    public boolean haveBookC(String bookName) {
        for (Book book: cs) {
            if (book.getName().equals(bookName)) {
                return true;
            }
        }
        return false;
    }

    public void borrowC(Book book) {
        this.cs.add(book);
    }

    public Book returnC(String bookName) {
        Book reBook = null;
        for (Book book: cs) {
            if (book.getName().equals(bookName)) {
                reBook = book;
                break;
            }
        }
        this.cs.remove(reBook);
        return reBook;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setSmear(String bookName) {
        for (Book book: bs) {
            if (Objects.equals(book.getName(), bookName)) {
                this.smear.add(book);
                return;
            }
        }
        for (Book book: cs) {
            if (Objects.equals(book.getName(), bookName)) {
                this.smear.add(book);
                return;
            }
        }
    }

    public boolean querySmear(Book book) {
        return this.smear.contains(book);
    }

    public void removeSmear(Book book) {
        this.smear.remove(book);
    }

    public void lost(String bookName) {
        bs.removeIf(book -> book.getName().equals(bookName));
        cs.removeIf(book -> book.getName().equals(bookName));
    }
}
