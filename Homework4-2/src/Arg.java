import java.util.HashMap;
import java.util.Map;

public class Arg {
    private final HashMap<Book, Integer> argLib = new HashMap<>();
    private final String schoolName;

    public Arg(String schoolName) {
        this.schoolName = schoolName;
    }

    public void collectBook(HashMap<Book, Integer> lib) {
        for (Map.Entry<Book, Integer> entry: lib.entrySet()) {
            Book book = entry.getKey();
            int num = entry.getValue();
            argLib.compute(book, (k, v) -> v == null ? num : v + num);
        }
        lib.clear();
    }

    public HashMap<Book, Integer> getArgLib() {
        return argLib;
    }
}
