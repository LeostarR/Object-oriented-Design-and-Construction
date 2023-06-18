import com.oocourse.spec1.exceptions.PersonIdNotFoundException;

import java.util.HashMap;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private static int sum = 0;
    private static final HashMap<Integer, Integer> INTEGER_HASH_MAP = new HashMap<>();
    private final int id;

    public MyPersonIdNotFoundException(int id) {
        sum++;
        this.id = id;
        if (INTEGER_HASH_MAP.containsKey(id)) {
            int n = INTEGER_HASH_MAP.get(id);
            INTEGER_HASH_MAP.put(id, n + 1);
        } else {
            INTEGER_HASH_MAP.put(id, 1);
        }
    }

    @Override
    public void print() {
        System.out.println("pinf-" + sum + ", " + id + "-" + INTEGER_HASH_MAP.get(id));
    }
}
