import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Mace {
    private final String schoolName;
    private final HashMap<Book, Integer> maceLib = new HashMap<>();
    private final ArrayList<Book> outBooks = new ArrayList<>();
    private boolean smearFlagC = false;
    private boolean flagC = false;

    public Mace(String schoolName) {
        this.schoolName = schoolName;
    }

    public void borrowC(String date, Student student, String bookName, Lib lib) { //校内借阅
        Book book = lib.borrowOne(bookName);
        flagC = student.haveBookC(bookName);
        if (flagC) {
            this.maceLib.compute(book, (k, v) -> v == null ? 1 : v + 1);
            //"[YYYY-mm-dd] <服务部门> refused lending <学校名称>-<类别号-序列号> to <学校名称>-<学号>"
            System.out.printf("%s self-service machine refused lending %s-%s to %s-%s\n"
                            , date, schoolName, bookName, schoolName, student.getStudentName());
            //(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>
            System.out.printf("(State) %s %s transfers from inLib to inArrange\n", date, bookName);
        } else {
            student.borrowC(book);
            //"[YYYY-mm-dd] <服务部门> lent <学校名称>-<类别号-序列号> to <学校名称>-<学号>"
            System.out.printf("%s self-service machine lent %s-%s to %s-%s\n"
                    , date, schoolName, bookName, schoolName, student.getStudentName());
            //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
            System.out.printf("(State) %s %s transfers from inLib to inHands\n", date, bookName);
            //"[YYYY-mm-dd] <学校名称>-<学号> borrowed <学校名称>-<类别号-序列号> from <服务部门>"
            System.out.printf("%s %s-%s borrowed %s-%s from self-service machine\n"
                    , date, schoolName, student.getStudentName(), schoolName, bookName);
        }
    }

    public void returnC(String date, Student student, String bookName, Rai rai) {
        Book book = student.returnC(bookName);
        smearFlagC = student.querySmear(book);
        if (smearFlagC) {
            student.removeSmear(book);
            //"[YYYY-mm-dd] <学校名称>-<学号> got punished by <服务部门>"
            System.out.printf("%s %s-%s got punished by borrowing and returning librarian\n",
                    date, schoolName, student.getStudentName());
            //"[YYYY-mm-dd] borrowing and returning librarian received <学校名称>-<学号>'s fine"
            System.out.printf("%s borrowing and returning librarian received %s-%s's fine\n",
                    date, schoolName, student.getStudentName());
            //"[YYYY-mm-dd] <学校名称>-<学号> returned <学校名称>-<类别号-序列号> to <服务部门>"
            System.out.printf("%s %s-%s returned %s-%s to self-service machine\n",
                    date, schoolName, student.getStudentName(), book.getSource(), bookName);
            //"[YYYY-mm-dd] <服务部门> collected <学校名称>-<类别号-序列号> from <学校名称>-<学号>"
            System.out.printf("%s self-service machine collected %s-%s from %s-%s\n",
                    date, book.getSource(), bookName, schoolName, student.getStudentName());
            //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
            System.out.printf("(State) %s %s transfers from inHands to smear\n", date, bookName);
            rai.repair(date, book);
        } else {
            //"[YYYY-mm-dd] <学校名称>-<学号> returned <学校名称>-<类别号-序列号> to <服务部门>"
            System.out.printf("%s %s-%s returned %s-%s to self-service machine\n",
                    date, schoolName, student.getStudentName(), book.getSource(), bookName);
            //"[YYYY-mm-dd] <服务部门> collected <学校名称>-<类别号-序列号> from <学校名称>-<学号>"
            System.out.printf("%s self-service machine collected %s-%s from %s-%s\n",
                    date, book.getSource(), bookName, schoolName, student.getStudentName());
            //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
            System.out.printf("(State) %s %s transfers from inHands to inArrange\n",
                    date, bookName);
            if (book.getSource().equals(schoolName)) {
                this.maceLib.compute(book, (k, v) -> v == null ? 1 : v + 1);
            } else {
                outBooks.add(book);
            }
        }
    }

    public void transport(String date) {
        for (Book book: outBooks) {
            String schoolName = book.getSource();
            String bookName = book.getName();
            //"[YYYY-mm-dd] <学校名称>-<类别号-序列号> got transported by <服务部门> in <学校名称>"
            System.out.printf("%s %s-%s got transported by purchasing department in %s\n",
                    date, schoolName, bookName, this.schoolName);
            //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
            System.out.printf("(State) %s %s transfers from outSchool to onRoad\n", date, bookName);
        }
    }

    public void receiveBook(String date, TreeMap<String, ArrayList<Object>> schools) {
        for (Book book: outBooks) {
            String schoolName = book.getSource();
            String bookName = book.getName();
            //"[YYYY-mm-dd] <学校名称>-<类别号-序列号> got received by <服务部门> in <学校名称>"
            System.out.printf("%s %s-%s got received by purchasing department in %s\n",
                    date, schoolName, bookName, schoolName);
            //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
            System.out.printf("(State) %s %s transfers from onRoad to inArrange\n", date, bookName);
            Odr source = (Odr) schools.get(schoolName).get(2);
            source.getCounter().compute(book, (k, v) -> v == null ? 1 : v + 1);
        }
        this.outBooks.clear();
    }

    public HashMap<Book, Integer> getMaceLib() {
        return maceLib;
    }
}
