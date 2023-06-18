import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Bar {
    private final String schoolName;
    private final HashMap<Book, Integer> barLib = new HashMap<>();
    private final ArrayList<Book> outBooks = new ArrayList<>();
    private int smearFlagB = 0;
    private boolean flagB = false;

    public Bar(String schoolName) {
        this.schoolName = schoolName;
    }

    public void borrowB(String date, Student student, String bookName, Lib lib) { //校内借阅
        Book book = lib.borrowOne(bookName);
        flagB = student.haveBookB();
        if (flagB) {
            this.barLib.compute(book, (k, v) -> v == null ? 1 : v + 1);
            //"[YYYY-mm-dd] <服务部门> refused lending <学校名称>-<类别号-序列号> to <学校名称>-<学号>"
            System.out.
                    printf("%s borrowing and returning librarian refused lending %s-%s to %s-%s\n"
                            , date, schoolName, bookName, schoolName, student.getStudentName());
            //(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>
            System.out.printf("(State) %s %s transfers from inLib to inArrange\n", date, bookName);
            //(Sequence) [YYYY-mm-dd] <消息发送者> sends a message to <消息接收者>
            System.out.printf("(Sequence) %s Bar sends a message to Student\n", date);
        } else {
            student.borrowB(book, date);
            //"[YYYY-mm-dd] <服务部门> lent <学校名称>-<类别号-序列号> to <学校名称>-<学号>"
            System.out.printf("%s borrowing and returning librarian lent %s-%s to %s-%s\n"
                    , date, schoolName, bookName, schoolName, student.getStudentName());
            //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
            System.out.printf("(State) %s %s transfers from inLib to inHands\n", date, bookName);
            //(Sequence) [YYYY-mm-dd] <消息发送者> sends a message to <消息接收者>
            System.out.printf("(Sequence) %s Bar sends a message to Student\n", date);
            //"[YYYY-mm-dd] <学校名称>-<学号> borrowed <学校名称>-<类别号-序列号> from <服务部门>"
            System.out.printf("%s %s-%s borrowed %s-%s from borrowing and returning librarian\n"
                    , date, schoolName, student.getStudentName(), schoolName, bookName);
        }
    }

    public void returnB(String date, Student student, String bookName, Rai rai) {
        String day = student.getDate(bookName);
        Book book = student.returnB(bookName);
        boolean flag1 = student.querySmear(book);
        boolean flag2 = over30Days(day, date);
        if (flag1) {
            smearFlagB = 1;
        } else if (flag2) {
            smearFlagB = -1;
        } else {
            smearFlagB = 0;
        }
        if (smearFlagB != 0) {
            student.removeSmear(book);
            //"[YYYY-mm-dd] <学校名称>-<学号> got punished by <服务部门>"
            System.out.printf("%s %s-%s got punished by borrowing and returning librarian\n",
                    date, schoolName, student.getStudentName());
            //"[YYYY-mm-dd] borrowing and returning librarian received <学校名称>-<学号>'s fine"
            System.out.printf("%s borrowing and returning librarian received %s-%s's fine\n",
                    date, schoolName, student.getStudentName());
            //"[YYYY-mm-dd] <学校名称>-<学号> returned <学校名称>-<类别号-序列号> to <服务部门>"
            System.out.printf("%s %s-%s returned %s-%s to borrowing and returning librarian\n",
                    date, schoolName, student.getStudentName(), book.getSource(), bookName);
            //"[YYYY-mm-dd] <服务部门> collected <学校名称>-<类别号-序列号> from <学校名称>-<学号>"
            System.out.printf("%s borrowing and returning librarian collected %s-%s from %s-%s\n",
                    date, book.getSource(), bookName, schoolName, student.getStudentName());
            if (smearFlagB == 1) {
                //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
                System.out.
                        printf("(State) %s %s transfers from inHands to smear\n", date, bookName);
                rai.repair(date, book);
            } else if (smearFlagB == -1) {
                //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
                System.out.printf("(State) %s %s transfers from inHands to inArrange\n",
                        date, bookName);
                if (book.getSource().equals(schoolName)) {
                    this.barLib.compute(book, (k, v) -> v == null ? 1 : v + 1);
                } else {
                    outBooks.add(book);
                }
            }
        } else {
            //"[YYYY-mm-dd] <学校名称>-<学号> returned <学校名称>-<类别号-序列号> to <服务部门>"
            System.out.printf("%s %s-%s returned %s-%s to borrowing and returning librarian\n",
                    date, schoolName, student.getStudentName(), book.getSource(), bookName);
            //"[YYYY-mm-dd] <服务部门> collected <学校名称>-<类别号-序列号> from <学校名称>-<学号>"
            System.out.printf("%s borrowing and returning librarian collected %s-%s from %s-%s\n",
                    date, book.getSource(), bookName, schoolName, student.getStudentName());
            //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
            System.out.printf("(State) %s %s transfers from inHands to inArrange\n",
                    date, bookName);
            if (book.getSource().equals(schoolName)) {
                this.barLib.compute(book, (k, v) -> v == null ? 1 : v + 1);
            } else {
                outBooks.add(book);
            }
        }
    }

    public void punishLost(String date, Student student, String bookName) {
        student.lost(bookName);
        //"[YYYY-mm-dd] <学校名称>-<学号> got punished by <服务部门>"
        System.out.printf("%s %s-%s got punished by borrowing and returning librarian\n",
                date, schoolName, student.getStudentName());
        //"[YYYY-mm-dd] borrowing and returning librarian received <学校名称>-<学号>'s fine"
        System.out.printf("%s borrowing and returning librarian received %s-%s's fine\n",
                date, schoolName, student.getStudentName());
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

    public HashMap<Book, Integer> getBarLib() {
        return barLib;
    }

    public boolean over30Days(String date1, String date2) {
        String s1 = date1.replace("[", "").replace("]", "");
        String s2 = date2.replace("[", "").replace("]", "");
        String[] parts1 = s1.split("-");
        int year1 = Integer.parseInt(parts1[0]);
        int month1 = Integer.parseInt(parts1[1]);
        int day1 = Integer.parseInt(parts1[2]);
        String[] parts2 = s2.split("-");
        int year2 = Integer.parseInt(parts2[0]);
        int month2 = Integer.parseInt(parts2[1]);
        int day2 = Integer.parseInt(parts2[2]);
        LocalDate dat1 = LocalDate.of(year1, month1, day1);
        LocalDate dat2 = LocalDate.of(year2, month2, day2);
        long daysBetween = ChronoUnit.DAYS.between(dat1, dat2);
        return Math.abs(daysBetween) > 30;
    }
}
