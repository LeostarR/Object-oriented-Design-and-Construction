import javafx.util.Pair;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        TreeMap<String, ArrayList<Object>> schools = new TreeMap<>();
        HashMap<String, HashMap<String, Student>> students = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        int t = Integer.parseInt(scanner.nextLine());
        for (int i = 0;i < t;i++) {
            String schoolInfo = scanner.nextLine();
            String[] schoolInfoPart = schoolInfo.split(" ");
            String schoolName = schoolInfoPart[0];
            int n = Integer.parseInt(schoolInfoPart[1]);
            Lib lib = new Lib(schoolName);
            for (int j = 0;j < n;j++) {
                String bookInfo = scanner.nextLine();
                initLib(schoolName, bookInfo, lib);
            }
            ArrayList<Object> departments = new ArrayList<>();
            departments.add(lib);
            initDepartment(departments, schoolName);
            schools.put(schoolName, departments);
            students.put(schoolName, new HashMap<>());
        }
        HashSet<String> arDays = findArDays();
        int m = Integer.parseInt(scanner.nextLine());
        String today = "[2022-12-31]";
        String arrDay = "[2023-01-01]";
        ArrayList<String[]> proList = new ArrayList<>();
        ArrayList<Pair<String[], Book>> transList = new ArrayList<>();
        for (int i = 0;i < m;i++) {
            String info = scanner.nextLine();
            String[] parts = info.split(" ");
            String date = parts[0];
            String identity = parts[1];
            String[] str = identity.split("-");
            String schoolName = str[0];
            String studentName = str[1];
            String operation = parts[2];
            String bookName = parts[3];
            //闭馆后 //开馆前
            if (dayChange(date, today)) {
                //确定这一天结束（仍在昨天）
                process(proList, schools, transList, students);//处理所有没能借成功的信息
                transport(transList, schools, today);
                //清晨
                today = plusDay(today);
                receive(transList, schools, students, today);
                String day = today;
                if (arDays.contains(day)) {
                    //完成新书购置
                    buyBooks(schools, day);
                    System.out.println(day + " arranging librarian arranged all the books");
                    //全部收纳到管理员处并发放
                    arrange(schools, students, day);
                    arrDay = findArrDay(arrDay, arDays);
                }
                while (dayEarly(arrDay, date)) {
                    //完成新书购置
                    buyBooks(schools, arrDay);
                    System.out.println(arrDay + " arranging librarian arranged all the books");
                    //全部收纳到管理员处并发放
                    arrange(schools, students, arrDay);
                    arrDay = findArrDay(arrDay, arDays);
                }
                //切换到此刻
                today = date;
            }
            Lib thisLib = (Lib) schools.get(schoolName).get(0);
            Bar thisBar = (Bar) schools.get(schoolName).get(1);
            Mace thisMace = (Mace) schools.get(schoolName).get(3);
            Rai rai = (Rai) schools.get(schoolName).get(4);
            //处理本次信息
            if (!students.get(schoolName).containsKey(studentName)) {
                students.get(schoolName).put(studentName, new Student(schoolName, studentName));
            }
            Student student = students.get(schoolName).get(studentName);
            switch (operation) {
                case "borrowed":
                    if (thisLib.queryBooks(date, student, bookName)) {
                        if (bookName.charAt(0) == 'B') {
                            thisBar.borrowB(date, student, bookName, thisLib);
                        } else if (bookName.charAt(0) == 'C') {
                            thisMace.borrowC(date, student, bookName, thisLib);
                        }
                    } else { //暂时不可借,是否校际借书,购买还是校内预订需要等这一天过去
                        proList.add(new String[]{date, schoolName, studentName, bookName});
                    }
                    break;
                case "smeared":
                    student.setSmear(bookName);
                    break;
                case "lost":
                    thisBar.punishLost(date, student, bookName);
                    break;
                case "returned":
                    if (bookName.charAt(0) == 'B') {
                        thisBar.returnB(date, student, bookName, rai);
                    } else if (bookName.charAt(0) == 'C') {
                        thisMace.returnC(date, student, bookName, rai);
                    }
                    break;
                default:
                    break;
            }
            //闭馆后（最后一个输入）
            if (i == m - 1) {
                process(proList, schools, transList, students);//处理所有没能借成功的信息
                transport(transList, schools, today);
            }
        }
    }

    public static void initLib(String schoolName, String bookInfo, Lib lib) {
        String[] bookInfoPart = bookInfo.split(" ");
        String bookName = bookInfoPart[0];
        int number = Integer.parseInt(bookInfoPart[1]);
        boolean allowBorrow = Objects.equals(bookInfoPart[2], "Y");
        Book book = new Book(schoolName, bookName, allowBorrow);
        lib.initBooks(book, number);
    }

    public static void initDepartment(ArrayList<Object> departments, String schoolName) {
        Bar bar = new Bar(schoolName);// 1->Bar BorrowingAndReturningLibrarian
        departments.add(bar);
        Odr odr = new Odr(schoolName);// 2->Odr OrderLibrarian
        departments.add(odr);
        Mace mace = new Mace(schoolName);// 3->Mace ServiceMachine
        departments.add(mace);
        Rai rai = new Rai(schoolName);// 4->Rai LogisticsDivision
        departments.add(rai);
        Arg arg = new Arg(schoolName);// 5->Arg ArrangingLibrarian
        departments.add(arg);
        Pcd pcd = new Pcd(schoolName);// 6->Pcd PurchasingDepartment
        departments.add(pcd);
    }

    public static void arrange(TreeMap<String, ArrayList<Object>> schools,
                               HashMap<String, HashMap<String, Student>> students, String date) {
        for (Map.Entry<String, ArrayList<Object>> entry: schools.entrySet()) {
            Bar bar = (Bar) entry.getValue().get(1);
            Odr odr = (Odr) entry.getValue().get(2);
            Mace mace = (Mace) entry.getValue().get(3);
            Rai rai = (Rai) entry.getValue().get(4);
            Arg arg = (Arg) entry.getValue().get(5);
            arg.collectBook(bar.getBarLib());
            arg.collectBook(mace.getMaceLib());
            arg.collectBook(rai.getRaiLib());
            arg.collectBook(odr.getBuyMap());
            odr.collect(arg.getArgLib());
            odr.sendBooks(students, date);
            Lib lib = (Lib) entry.getValue().get(0);
            odr.putBack(lib.getLib());
        }
    }

    public static void transport(ArrayList<Pair<String[], Book>> transList,
                                 TreeMap<String, ArrayList<Object>> schools, String date) {
        //运出信息（借校际书）
        for (Pair<String[], Book> pair: transList) {
            //"[YYYY-mm-dd] <学校名称>-<类别号-序列号> got transported by <服务部门> in <学校名称>"
            String bookSource = pair.getValue().getSource();
            String bookName = pair.getKey()[3];
            System.out.printf("%s %s-%s got transported by purchasing department in %s\n",
                    date, bookSource, bookName, bookSource);
            //(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>
            System.out.printf("(State) %s %s transfers from inLib to onRoad\n", date, bookName);
        }
        //运出信息（还校际书）
        for (Map.Entry<String, ArrayList<Object>> entry: schools.entrySet()) {
            Bar bar = (Bar) entry.getValue().get(1);
            Mace mace = (Mace) entry.getValue().get(3);
            Rai rai = (Rai) entry.getValue().get(4);
            bar.transport(date);
            mace.transport(date);
            rai.transport(date);
        }
    }

    public static void receive(ArrayList<Pair<String[], Book>> transList,
                               TreeMap<String, ArrayList<Object>> schools,
                               HashMap<String, HashMap<String, Student>> students, String date) {
        //运入信息（借校际书）
        for (Pair<String[], Book> pair: transList) {
            //"[YYYY-mm-dd] <学校名称>-<类别号-序列号> got received by <服务部门> in <学校名称>"
            String schoolName = pair.getKey()[1];
            String bookSource = pair.getValue().getSource();
            String bookName = pair.getValue().getName();
            System.out.printf("%s %s-%s got received by purchasing department in %s\n",
                    date, bookSource, bookName, schoolName);
            //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
            System.out.printf("(State) %s %s transfers from onRoad to outSchool\n", date, bookName);
        }
        //运入信息（还校际书），直接放到书架上
        for (Map.Entry<String, ArrayList<Object>> entry: schools.entrySet()) {
            Bar bar = (Bar) entry.getValue().get(1);
            Mace mace = (Mace) entry.getValue().get(3);
            Rai rai = (Rai) entry.getValue().get(4);
            bar.receiveBook(date, schools);
            mace.receiveBook(date, schools);
            rai.receiveBook(date, schools);
        }
        //校际图书发放
        for (Pair<String[], Book> pair: transList) {
            //"[YYYY-mm-dd] <服务部门> lent <学校名称>-<类别号-序列号> to <学校名称>-<学号>"
            //"[YYYY-mm-dd] <学校名称>-<学号> borrowed <学校名称>-<类别号-序列号> from <服务部门>"
            String schoolName = pair.getKey()[1];
            String studentName = pair.getKey()[2];
            String bookSource = pair.getValue().getSource();
            String bookName = pair.getValue().getName();
            Student student = students.get(schoolName).get(studentName);
            System.out.printf("%s purchasing department lent %s-%s to %s-%s\n",
                    date, bookSource, bookName, schoolName, studentName);
            //“(State) [YYYY-mm-dd] <类别号-序列号> transfers from <原状态> to <新状态>”
            System.out.printf("(State) %s %s transfers from inLib to inHands\n", date, bookName);
            System.out.printf("%s %s-%s borrowed %s-%s from purchasing department\n",
                    date, schoolName, studentName, bookSource, bookName);
            Book book = pair.getValue();
            if (bookName.charAt(0) == 'B') {
                student.borrowB(book);
            } else if (bookName.charAt(0) == 'C') {
                student.borrowC(book);
            }
        }
        transList.clear();
    }

    public static void buyBooks(TreeMap<String, ArrayList<Object>> schools, String date) {
        for (Map.Entry<String, ArrayList<Object>> entry: schools.entrySet()) {
            Odr odr = (Odr) entry.getValue().get(2);
            odr.updateMap();
            odr.createBook(date);
        }
    }

    public static void process(ArrayList<String[]> proList,
                               TreeMap<String, ArrayList<Object>> schools,
                               ArrayList<Pair<String[], Book>> transList,
                               HashMap<String, HashMap<String, Student>> students) {
        ArrayList<String[]> deleteList = new ArrayList<>();
        for (String[] need: proList) {
            String schoolName = need[1];
            String studentName = need[2];
            Student student = students.get(schoolName).get(studentName);
            String bookName = need[3];
            for (Map.Entry<String, ArrayList<Object>> entry: schools.entrySet()) {
                Lib lib = (Lib) entry.getValue().get(0);
                if (lib.haveBook(bookName) && !schoolName.equals(entry.getKey())) {
                    //到这一步一定走校际流程，现在只需判断是否合法
                    if (demandOk(transList, student, need)) {
                        Book book = lib.borrowBookOut(bookName);
                        transList.add(new Pair<>(need, book));
                        //所有合法校际借阅保存在transList中，不合法那一部分直接丢弃
                    }
                    deleteList.add(need);
                    break;
                }
            }
        }
        for (String[] delete: deleteList) {
            proList.remove(delete);
        }
        //剩余的proList走校内，再判断预定或购买
        obProcess(proList, schools, students);
    }

    public static boolean demandOk(ArrayList<Pair<String[], Book>> transList,
                                   Student student, String[] need) {
        String bookName = need[3];
        if ((bookName.charAt(0) == 'B' && student.haveBookB()) || student.haveBookC(bookName)) {
            return false;
        }
        for (Pair<String[], Book> info: transList) {
            String schoolName = info.getKey()[1];
            String studentName = info.getKey()[2];
            Book book = info.getValue();
            if (student.getSchoolName().equals(schoolName) &&
                    student.getStudentName().equals(studentName) &&
                    ((book.getName().charAt(0) == 'B' && bookName.charAt(0) == 'B') ||
                            book.getName().equals(bookName))) {
                return false;
            }
        }
        return true;
    }

    public static void obProcess(ArrayList<String[]> proList,
                                 TreeMap<String, ArrayList<Object>> schools,
                                 HashMap<String, HashMap<String, Student>> students) {
        for (Map.Entry<String, ArrayList<Object>> entry: schools.entrySet()) {
            for (String[] need: proList) {
                String schoolName = need[1];
                if (schoolName.equals(entry.getKey())) {
                    String studentName = need[2];
                    Student student = students.get(schoolName).get(studentName);
                    String bookName = need[3];
                    Lib lib = (Lib) schools.get(schoolName).get(0);
                    Odr odr = (Odr) schools.get(schoolName).get(2);
                    if (lib.contain(bookName)) { //校内预定
                        odr.order(need[0], schoolName, studentName, bookName, student);
                    } else { //购书
                        odr.buy(need[0], schoolName, studentName, bookName);
                    }
                }
            }
        }
        proList.clear();
    }

    public static boolean dayChange(String date1, String date2) {
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
        return !dat1.isEqual(dat2);
    }

    public static boolean dayEarly(String date1, String date2) {
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
        return dat1.isBefore(dat2) || dat1.isEqual(dat2);
    }

    public static String plusDay(String date) {
        String s = date.replace("[", "").replace("]", "");
        String[] parts = s.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        return '[' + LocalDate.of(year, month, day).plusDays(1).toString() + ']';
    }

    public static String findArrDay(String date, HashSet<String> datesSet) {
        String s = date.replace("[", "").replace("]", "");
        String[] parts = s.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        LocalDate next = LocalDate.of(year, month, day);
        String str = '[' + next.plusDays(1).toString() + ']';
        if (datesSet.contains(str)) {
            return str;
        }
        str = '[' + next.plusDays(2).toString() + ']';
        if (datesSet.contains(str)) {
            return str;
        }
        return '[' + next.plusDays(3).toString() + ']';
    }

    public static HashSet<String> findArDays() {
        LocalDate initDate = LocalDate.of(2023,1,1);
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        HashSet<String> datesSet = new HashSet<>();
        while (startDate.isBefore(endDate)) {
            if (initDate.until(startDate, ChronoUnit.DAYS) % 3 == 0) {
                datesSet.add('[' + startDate.toString() + ']');
            }
            startDate = startDate.plusDays(1);
        }
        datesSet.add('[' + initDate.toString() + ']');
        return datesSet;
    }
}