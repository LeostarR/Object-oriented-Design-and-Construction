import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HashMap<String, Student> students = new HashMap<>();
        HashMap<String, Integer> lib = new HashMap<>();
        LogisticsDivision ldRepair = new LogisticsDivision();
        ServiceMachine machine = new ServiceMachine();
        BorrowingAndReturningLibrarian brLibrarian = new BorrowingAndReturningLibrarian();
        OrderLibrarian orderLibrarian = new OrderLibrarian();
        ArrangingLibrarian arLibrarian = new ArrangingLibrarian();
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine());
        for (int i = 0;i < n;i++) {
            String info = scanner.nextLine();
            String[] parts = info.split(" ");
            String category = parts[0];
            int copies = Integer.parseInt(parts[1]);
            lib.put(category, copies);
        } HashSet<String> dateSet = daysDiff();
        String nextDate = "[2023-01-04]";
        int m = Integer.parseInt(scanner.nextLine());
        for (int i = 0;i < m;i++) {
            String info = scanner.nextLine();
            String[] parts = info.split(" ");
            String date = parts[0];
            String id = parts[1];
            String operation = parts[2];
            String category = parts[3];
            if (isEarly(nextDate, date)) {
                arLibrarian.arrange(brLibrarian, machine, ldRepair, orderLibrarian);
                orderLibrarian.sendBookToStudents(students, nextDate);
                orderLibrarian.sendRemainingBookBack(arLibrarian);
                arLibrarian.putBooksToLib(lib);
                nextDate = findNextDate(date, dateSet);
            }
            if (!students.containsKey(id)) {
                students.put(id, new Student(id));
            }
            if (Objects.equals(operation, "borrowed")) {
                if (machine.queryBook(date, id, category, lib)) {
                    if (category.charAt(0) == 'B') {
                        brLibrarian.borrowBookB(date, id, category,
                                students.get(id), lib, orderLibrarian);
                    } else if (category.charAt(0) == 'C') {
                        machine.borrowBookC(date, id, category, students.get(id), lib);
                    }
                } else {
                    orderLibrarian.order(date, id, category, students.get(id));
                }
            } else if (Objects.equals(operation, "smeared")) {
                students.get(id).smearBook(category);
            } else if (Objects.equals(operation, "lost")) {
                brLibrarian.registerLostBook(date, id, category, students.get(id));
            } else if (Objects.equals(operation, "returned")) {
                if (category.charAt(0) == 'B') {
                    brLibrarian.returnBookB(date, id, category, students.get(id), ldRepair);
                } else if (category.charAt(0) == 'C') {
                    machine.returnBookC(date, id, category, students.get(id), ldRepair);
                }
            }
        }
    }

    public static HashSet<String> daysDiff() {
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
        return datesSet;
    }

    public static String findNextDate(String date, HashSet<String> dateSet) {
        String str = date.replace("[", "").replace("]", "");
        String[] parts = str.split("-");
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        String s = '[' + LocalDate.of(2023, month, day).plusDays(1).toString() + ']';
        if (dateSet.contains(s)) {
            return s;
        }
        s = '[' + LocalDate.of(2023, month, day).plusDays(2).toString() + ']';
        if (dateSet.contains(s)) {
            return s;
        }
        s = '[' + LocalDate.of(2023, month, day).plusDays(3).toString() + ']';
        return s;
    }

    public static boolean isEarly(String date1, String date2) {
        String s1 = date1.replace("[", "").replace("]", "");
        String s2 = date2.replace("[", "").replace("]", "");
        String[] parts1 = s1.split("-");
        int month1 = Integer.parseInt(parts1[1]);
        int day1 = Integer.parseInt(parts1[2]);
        String[] parts2 = s2.split("-");
        int month2 = Integer.parseInt(parts2[1]);
        int day2 = Integer.parseInt(parts2[2]);
        LocalDate dat1 = LocalDate.of(2023, month1, day1);
        LocalDate dat2 = LocalDate.of(2023, month2, day2);
        return dat1.isBefore(dat2) || dat1.isEqual(dat2);
    }
}