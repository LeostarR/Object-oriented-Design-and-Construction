import java.util.HashMap;

public class OkTest {
    private final int id1;
    private final int id2;
    private final int value;
    private final HashMap<Integer, HashMap<Integer, Integer>> beforeData;
    private final HashMap<Integer, HashMap<Integer, Integer>> afterData;

    public OkTest(int id1, int id2, int value,
                  HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                  HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        this.id1 = id1;
        this.id2 = id2;
        this.value = value;
        this.beforeData = beforeData;
        this.afterData = afterData;
    }

    public int test() {
        if (beforeData.containsKey(id1) && beforeData.containsKey(id2)
                && id1 != id2 && beforeData.get(id1).containsKey(id2)
                && beforeData.get(id2).containsKey(id1)
                && beforeData.get(id1).get(id2) + value > 0) {
            return testCon1();
        } else if (beforeData.containsKey(id1) && beforeData.containsKey(id2)
                && id1 != id2 && beforeData.get(id1).containsKey(id2)
                && beforeData.get(id2).containsKey(id1)
                && beforeData.get(id1).get(id2) + value <= 0) {
            return testCon2();
        } else {
            if (!beforeData.equals(afterData)) {
                return -1;
            }
        }
        return 0;
    }

    public int testCon1() {
        if (beforeData.size() != afterData.size()) {
            return 1;
        }
        if (!beforeData.keySet().equals(afterData.keySet())) {
            return 2;
        }
        HashMap<Integer, HashMap<Integer, Integer>> before = new HashMap<>(beforeData);
        before.remove(id1);
        before.remove(id2);
        HashMap<Integer, HashMap<Integer, Integer>> after = new HashMap<>(afterData);
        after.remove(id1);
        after.remove(id2);
        if (!before.equals(after)) {
            return 3;
        }
        if (!afterData.get(id1).containsKey(id2) || !afterData.get(id2).containsKey(id1)) {
            return 4;
        }
        if (afterData.get(id1).get(id2) != beforeData.get(id1).get(id2) + value) {
            return 5;
        }
        if (afterData.get(id2).get(id1) != beforeData.get(id2).get(id1) + value) {
            return 6;
        }
        if (afterData.get(id1).size() != beforeData.get(id1).size()) {
            return 7;
        }
        if (afterData.get(id2).size() != beforeData.get(id2).size()) {
            return 8;
        }
        if (!afterData.get(id1).keySet().equals(beforeData.get(id1).keySet())) {
            return 9;
        }
        if (!afterData.get(id2).keySet().equals(beforeData.get(id2).keySet())) {
            return 10;
        }
        HashMap<Integer, Integer> map1 = new HashMap<>(beforeData.get(id1));
        map1.remove(id2);
        HashMap<Integer, Integer> map2 = new HashMap<>(afterData.get(id1));
        map2.remove(id2);
        if (!map1.equals(map2)) {
            return 11;
        }
        HashMap<Integer, Integer> map3 = new HashMap<>(beforeData.get(id2));
        map3.remove(id1);
        HashMap<Integer, Integer> map4 = new HashMap<>(afterData.get(id2));
        map4.remove(id1);
        if (!map3.equals(map4)) {
            return 12;
        }
        if (afterData.get(id1).keySet().size() != afterData.get(id1).values().size()) {
            return 13;
        }
        if (afterData.get(id2).keySet().size() != afterData.get(id2).values().size()) {
            return 14;
        }
        return 0;
    }

    public int testCon2() {
        if (beforeData.size() != afterData.size()) {
            return 1;
        }
        if (!beforeData.keySet().equals(afterData.keySet())) {
            return 2;
        }
        HashMap<Integer, HashMap<Integer, Integer>> before = new HashMap<>(beforeData);
        before.remove(id1);
        before.remove(id2);
        HashMap<Integer, HashMap<Integer, Integer>> after = new HashMap<>(afterData);
        after.remove(id1);
        after.remove(id2);
        if (!before.equals(after)) {
            return 3;
        }
        if (afterData.get(id1).containsKey(id2) || afterData.get(id2).containsKey(id1)) {
            return 15;
        }
        if (beforeData.get(id1).values().size() != afterData.get(id1).keySet().size() + 1) {
            return 16;
        }
        if (beforeData.get(id2).values().size() != afterData.get(id2).keySet().size() + 1) {
            return 17;
        }
        if (afterData.get(id1).values().size() != afterData.get(id1).keySet().size()) {
            return 18;
        }
        if (afterData.get(id2).values().size() != afterData.get(id2).keySet().size()) {
            return 19;
        }
        HashMap<Integer, Integer> map1 = new HashMap<>(beforeData.get(id1));
        map1.remove(id2);
        if (!afterData.get(id1).equals(map1)) {
            return 20;
        }
        HashMap<Integer, Integer> map2 = new HashMap<>(beforeData.get(id2));
        map2.remove(id1);
        if (!afterData.get(id2).equals(map2)) {
            return 21;
        }
        return 0;
    }
}
