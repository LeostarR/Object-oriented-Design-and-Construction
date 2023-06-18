import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class Odr {
    private final ArrayList<String[]> orderList = new ArrayList<>();
    //date, schoolName, studentName, bookName
    private final ArrayList<String[]> buyList = new ArrayList<>();
    //date, schoolName, studentName, bookName
    private final ArrayList<Pair<String[], Character>> list = new ArrayList<>();
    private final HashMap<Book, Integer> buyMap = new HashMap<>();
    private final HashMap<Book, Integer> counter = new HashMap<>();
    private final String schoolName;

    public Odr(String schoolName) {
        this.schoolName = schoolName;
    }

    public HashMap<Book, Integer> getCounter() {
        return counter;
    }

    public void collect(HashMap<Book, Integer> map) {
        for (Map.Entry<Book, Integer> entry: map.entrySet()) {
            Book book = entry.getKey();
            int num = entry.getValue();
            this.counter.compute(book, (k, v) -> v == null ? num : v + num);
        }
        map.clear();
    }

    public void putBack(HashMap<Book, Integer> lib) {
        for (Map.Entry<Book, Integer> entry: counter.entrySet()) {
            Book book = entry.getKey();
            int num = entry.getValue();
            lib.compute(book, (k, v) -> v == null ? num : v + num);
        }
        counter.clear();
    }

    public void sendBooks(HashMap<String, HashMap<String, Student>> students, String date) {
        ArrayList<Pair<String[], Character>> deleteList = new ArrayList<>();
        for (Pair<String[], Character> pair: list) {
            String[] need = pair.getKey();
            String schoolName = need[1];
            String studentName = need[2];
            String bookName = need[3];
            Student student = students.get(schoolName).get(studentName);
            if (haveBook(bookName)) {
                if (bookName.charAt(0) != 'A') {
                    Book book = findBook(bookName);
                    if (bookName.charAt(0) == 'B' && !student.haveBookB()) {
                        student.borrowB(book, date);
                        this.counter.computeIfPresent(book, (k, v) -> v - 1);
                        //"[YYYY-mm-dd] <服务部门> lent <学校名称>-<类别号-序列号> to <学校名称>-<学号>"
                        System.out.printf("%s ordering librarian lent %s-%s to %s-%s\n",
                                date, schoolName, book.getName(), schoolName, studentName);
                        //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
                        System.out.printf("(State) %s %s transfers from inLib to inHands\n",
                                date, bookName);
                        // (Sequence) [YYYY-mm-dd] <消息发送者> sends a message to <消息接收者>
                        System.out.printf("(Sequence) %s Odr sends a message to Student\n", date);
                        //"[YYYY-mm-dd] <学校名称>-<学号> borrowed <学校名称>-<类别号-序列号> from <服务部门>"
                        System.out.printf("%s %s-%s borrowed %s-%s from ordering librarian\n",
                                date, schoolName, studentName, schoolName, bookName);
                    } else if (bookName.charAt(0) == 'C' && !student.haveBookC(bookName)) {
                        student.borrowC(book, date);
                        this.counter.computeIfPresent(book, (k, v) -> v - 1);
                        //"[YYYY-mm-dd] <服务部门> lent <学校名称>-<类别号-序列号> to <学校名称>-<学号>"
                        System.out.printf("%s ordering librarian lent %s-%s to %s-%s\n",
                                date, schoolName, book.getName(), schoolName, studentName);
                        //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
                        System.out.printf("(State) %s %s transfers from inLib to inHands\n",
                                date, bookName);
                        //(Sequence) [YYYY-mm-dd] <消息发送者> sends a message to <消息接收者>
                        System.out.printf("(Sequence) %s Odr sends a message to Student\n", date);
                        //"[YYYY-mm-dd] <学校名称>-<学号> borrowed <学校名称>-<类别号-序列号> from <服务部门>"
                        System.out.printf("%s %s-%s borrowed %s-%s from ordering librarian\n",
                                date, schoolName, studentName, schoolName, bookName);
                    }
                    deleteList.add(pair);
                }
            }
        }
        for (Pair<String[], Character> pair: deleteList) {
            list.remove(pair);
            if (pair.getValue() == 'O') {
                orderList.remove(pair.getKey());
            } else if (pair.getValue() == 'B') {
                buyList.remove(pair.getKey());
            }
        }
    }

    public boolean haveBook(String bookName) {
        for (Map.Entry<Book, Integer> entry: counter.entrySet()) {
            if (Objects.equals(entry.getKey().getName(), bookName) && entry.getValue() > 0) {
                return true;
            }
        }
        return false;
    }

    public Book findBook(String bookName) {
        for (Map.Entry<Book, Integer> entry: counter.entrySet()) {
            if (Objects.equals(entry.getKey().getName(), bookName) && entry.getValue() > 0) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void order(String date, String schoolName,
                      String studentName, String bookName, Student student) {
        int sum = 0;
        for (String[] parts : orderList) {
            if (Objects.equals(parts[1], schoolName) &&
                    Objects.equals(parts[2], studentName) &&
                    Objects.equals(parts[3], bookName)) {
                return;
            }
            if (Objects.equals(parts[0], date) &&
                    Objects.equals(parts[1], schoolName) &&
                    Objects.equals(parts[2], studentName)) {
                sum++;
            }
        }
        if (sum >= 3) {
            return;
        }
        boolean accept = (bookName.charAt(0) == 'A') ||
                (bookName.charAt(0) == 'B' && !student.haveBookB()) ||
                (bookName.charAt(0) == 'C' && !student.haveBookC(bookName));
        if (accept) {
            String[] need = new String[]{date, schoolName, studentName, bookName};
            orderList.add(need);
            list.add(new Pair<>(need, 'O'));
            //"[YYYY-mm-dd] <学校名称>-<学号> ordered <学校名称>-<类别号-序列号> from <服务部门>"
            System.out.printf("%s %s-%s ordered %s-%s from ordering librarian\n",
                    date, schoolName, studentName, schoolName, bookName);
            //"[YYYY-mm-dd] ordering librarian recorded <学校名称>-<学号>'s order of <学校名称>-<类别号-序列号>"
            System.out.printf("%s ordering librarian recorded %s-%s's order of %s-%s\n",
                    date, schoolName, studentName, schoolName, bookName);
            //(Sequence) [YYYY-mm-dd] <消息发送者> sends a message to <消息接收者>
            System.out.printf("(Sequence) %s Student sends a message to Odr\n", date);
        }
    }

    public void buy(String date, String schoolName, String studentName, String bookName) {
        String[] need = new String[]{date, schoolName, studentName, bookName};
        Book book = new Book(schoolName, bookName, true);
        buyList.add(need);
        buyMap.compute(book, (k, v) -> v == null ? 1 : v + 1);
        list.add(new Pair<>(need, 'B'));
        //"[YYYY-mm-dd] <学校名称>-<学号> ordered <学校名称>-<类别号-序列号> from <服务部门>"
        System.out.printf("%s %s-%s ordered %s-%s from ordering librarian\n",
                date, schoolName, studentName, schoolName, bookName);
        //"[YYYY-mm-dd] ordering librarian recorded <学校名称>-<学号>'s order of <学校名称>-<类别号-序列号>"
        System.out.printf("%s ordering librarian recorded %s-%s's order of %s-%s\n",
                date, schoolName, studentName, schoolName, bookName);
        //(Sequence) [YYYY-mm-dd] <消息发送者> sends a message to <消息接收者>
        System.out.printf("(Sequence) %s Student sends a message to Odr\n", date);
    }

    public void updateMap() {
        for (Map.Entry<Book, Integer> entry: buyMap.entrySet()) {
            if (entry.getValue() < 3) {
                entry.setValue(3);
            }
        }
    }

    public void createBook(String date) {
        HashSet<String> name = new HashSet<>();
        for (String[] need: buyList) {
            String schoolName = this.schoolName;
            String bookName = need[3];
            if (name.contains(bookName)) {
                continue;
            }
            //Book book = new Book(schoolName, bookName, true);
            //buyMap.compute(book, (k, v) -> v == null ? 1 : v + 1);
            //"[YYYY-mm-dd] <学校名称>-<类别号-序列号> got purchased by <服务部门> in <学校名称>"
            System.out.printf("%s %s-%s got purchased by purchasing department in %s\n",
                    date, schoolName, bookName, schoolName);
            name.add(bookName);
        }
    }

    public HashMap<Book, Integer> getBuyMap() {
        return buyMap;
    }

    public void orderNewBook() {

    }
}
