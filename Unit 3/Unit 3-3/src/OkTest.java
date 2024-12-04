import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class OkTest {
    private final int limit;
    private final int result;
    private final ArrayList<HashMap<Integer, Integer>> beforeData;
    private final ArrayList<HashMap<Integer, Integer>> afterData;

    public OkTest(int limit, int result,
                  ArrayList<HashMap<Integer, Integer>> beforeData,
                  ArrayList<HashMap<Integer, Integer>> afterData) {
        this.limit = limit;
        this.result = result;
        this.beforeData = beforeData;
        this.afterData = afterData;
    }

    public int test() {
        int idNum = 0;
        for (HashMap.Entry<Integer, Integer> entry: beforeData.get(0).entrySet()) {
            if (entry.getValue() >= limit && !afterData.get(0).containsKey(entry.getKey())) {
                return 1;
            }
            if (entry.getValue() >= limit) {
                idNum++;
            }
        }
        for (HashMap.Entry<Integer, Integer> entry: afterData.get(0).entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            if (!beforeData.get(0).containsKey(key) ||
                    (beforeData.get(0).containsKey(key)
                            && !Objects.equals(beforeData.get(0).get(key), value))) {
                return 2;
            }
        }
        if (afterData.get(0).size() != idNum) {
            return 3;
        }
        if (afterData.get(0).keySet().size() != afterData.get(0).values().size()) {
            return 4;
        }
        int num7 = 0;
        for (HashMap.Entry<Integer, Integer> entry: beforeData.get(1).entrySet()) {
            int key = entry.getKey();
            if (entry.getValue() != null && afterData.get(0).containsKey(entry.getValue()) &&
                    (!afterData.get(1).containsKey(key)
                            || !afterData.get(1).get(key).equals(entry.getValue()))) {
                return 5;
            }
            if (entry.getValue() == null &&
                    (!afterData.get(1).containsKey(key) || afterData.get(1).get(key) != null))  {
                return 6;
            }
            if (entry.getValue() == null ||
                    (afterData.get(0).containsKey(entry.getValue()) &&
                            afterData.get(1).containsValue(entry.getValue()))) {
                num7++;
            }
        }
        if (afterData.get(1).size() != num7) {
            return 7;
        }
        if (result != afterData.get(0).size()) {
            return 8;
        }
        return 0;
    }
}
