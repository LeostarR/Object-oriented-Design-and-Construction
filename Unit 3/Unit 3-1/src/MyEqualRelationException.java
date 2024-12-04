import com.oocourse.spec1.exceptions.EqualRelationException;

import java.util.HashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MyEqualRelationException extends EqualRelationException {
    private static int sum = 0;
    private static final HashMap<Integer, Integer> INTEGER_HASH_MAP = new HashMap<>();
    private final int id1;
    private final int id2;

    public MyEqualRelationException(int id1, int id2) {
        sum++;
        this.id1 = min(id1, id2);
        this.id2 = max(id1, id2);
        if (id1 == id2) {
            if (INTEGER_HASH_MAP.containsKey(id1)) {
                int n = INTEGER_HASH_MAP.get(id1);
                INTEGER_HASH_MAP.put(id1, n + 1);
            } else {
                INTEGER_HASH_MAP.put(id1, 1);
            }
        } else {
            if (INTEGER_HASH_MAP.containsKey(id1)) {
                int n = INTEGER_HASH_MAP.get(id1);
                INTEGER_HASH_MAP.put(id1, n + 1);
            } else {
                INTEGER_HASH_MAP.put(id1, 1);
            }
            if (INTEGER_HASH_MAP.containsKey(id2)) {
                int n = INTEGER_HASH_MAP.get(id2);
                INTEGER_HASH_MAP.put(id2, n + 1);
            } else {
                INTEGER_HASH_MAP.put(id2, 1);
            }
        }
    }

    @Override
    public void print() {
        System.out.println("er-" + sum + ", " + id1 + "-" + INTEGER_HASH_MAP.get(id1)
                + ", " + id2 + "-" + INTEGER_HASH_MAP.get(id2));
    }
}
