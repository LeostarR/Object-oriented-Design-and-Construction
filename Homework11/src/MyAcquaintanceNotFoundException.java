import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;

import java.util.HashMap;

public class MyAcquaintanceNotFoundException extends AcquaintanceNotFoundException {
    private static int sum = 0;
    private final int id;
    private static final HashMap<Integer, Integer> INTEGER_HASH_MAP = new HashMap<>();

    public MyAcquaintanceNotFoundException(int id) {
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
        System.out.println("anf-" + sum + ", " + id + "-" + INTEGER_HASH_MAP.get(id));
    }
}