import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class Student {
    private final String schoolName;
    private final String studentName;
    private final HashSet<Book> bs = new HashSet<>();
    private final HashSet<Book> cs = new HashSet<>();
    private final HashSet<Book> smear = new HashSet<>();
    private final HashMap<String, String> borrowDate = new HashMap<>();

    public Student(String schoolName, String studentName) {
        this.schoolName = schoolName;
        this.studentName = studentName;
    }

    public boolean haveBookB() {
        return !this.bs.isEmpty();
    }

    public void borrowB(Book book, String date) {
        this.bs.add(book);
        this.borrowDate.put(book.getName(), date);
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
        this.borrowDate.remove(bookName);
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

    public void borrowC(Book book, String date) {
        this.cs.add(book);
        this.borrowDate.put(book.getName(), date);
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
        this.borrowDate.remove(bookName);
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
        this.borrowDate.remove(bookName);
    }

    public String getDate(String bookName) {
        return this.borrowDate.get(bookName);
    }
}
