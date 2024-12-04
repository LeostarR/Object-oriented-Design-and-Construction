import java.util.HashMap;
import java.util.Map;

public class Lib {
    private final HashMap<Book, Integer> lib = new HashMap<>();
    private final String schoolName;

    public Lib(String schoolName) {
        this.schoolName = schoolName;
    }

    public void initBooks(Book book, int number) {
        this.lib.put(book, number);
    }

    public boolean queryBooks(String date, Student student, String bookName) {
        //"[YYYY-mm-dd] <学校名称>-<学号> queried <类别号-序列号> from <服务部门>"
        System.out.printf("%s %s-%s queried %s from self-service machine\n",
                date, student.getSchoolName(), student.getStudentName(), bookName);
        //"[YYYY-mm-dd] self-service machine provided information of <类别号-序列号>"
        System.out.printf("%s self-service machine provided information of %s\n", date, bookName);
        for (Map.Entry<Book, Integer> entry: lib.entrySet()) {
            if (entry.getKey().getName().equals(bookName)
                    && entry.getValue() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean haveBook(String bookName) {
        for (Map.Entry<Book, Integer> entry: lib.entrySet()) {
            if (entry.getKey().getName().equals(bookName)
                    && entry.getKey().allowBorrow()
                    && entry.getValue() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean contain(String bookName) {
        for (Map.Entry<Book, Integer> entry: lib.entrySet()) {
            if (entry.getKey().getName().equals(bookName)) {
                return true;
            }
        }
        return false;
    }

    public Book borrowOne(String bookName) { //校内，不考虑权限
        for (Map.Entry<Book, Integer> entry: lib.entrySet()) {
            if (entry.getKey().getName().equals(bookName)
                    && entry.getValue() > 0) {
                int n = entry.getValue();
                entry.setValue(n - 1);
                return entry.getKey();
            }
        }
        return null;
    }

    public Book borrowBookOut(String bookName) { //校际，需考虑权限
        for (Map.Entry<Book, Integer> entry: lib.entrySet()) {
            if (entry.getKey().getName().equals(bookName)
                    && entry.getKey().allowBorrow()
                    && entry.getValue() > 0) {
                int n = entry.getValue();
                entry.setValue(n - 1);
                return entry.getKey();
            }
        }
        return null;
    }

    public HashMap<Book, Integer> getLib() {
        return lib;
    }
}
