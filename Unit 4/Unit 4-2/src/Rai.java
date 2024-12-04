import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Rai {
    private final String schoolName;
    private final HashMap<Book, Integer> raiLib = new HashMap<>();
    private final ArrayList<Book> outBooks = new ArrayList<>();

    public Rai(String schoolName) {
        this.schoolName = schoolName;
    }

    public void repair(String date, Book book) {
        //"[YYYY-mm-dd] <学校名称>-<类别号-序列号> got repaired by <服务部门> in <学校名称>"
        System.out.printf("%s %s-%s got repaired by logistics division in %s\n",
                date, book.getSource(), book.getName(), schoolName);
        //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
        System.out.printf("(State) %s %s transfers from smear to inArrange\n",
                date, book.getName());
        if (book.getSource().equals(schoolName)) {
            raiLib.compute(book, (k, v) -> v == null ? 1 : v + 1);
        } else {
            outBooks.add(book);
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

    public HashMap<Book, Integer> getRaiLib() {
        return raiLib;
    }
}
