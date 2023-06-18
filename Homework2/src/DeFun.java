import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class DeFun {
    private final HashMap<String, ArrayList<String>> hashMap = new HashMap<>();

    public void readFun(String s) {
        String key = Character.toString(s.charAt(0));
        ArrayList<String> arrayList = new ArrayList<>();
        int i;
        for (i = 2;i < s.length();i++) {
            if ((s.charAt(i) == 'x') || (s.charAt(i) == 'y') || (s.charAt(i) == 'z')) {
                arrayList.add(String.valueOf(Character.toUpperCase(s.charAt(i))));
            }
            if (s.charAt(i) == '=') {
                break;
            }
        }
        String funExpr = s.substring(i + 1).replace("x", "X").replace("y", "Y").replace("z", "Z");
        arrayList.add(funExpr);
        hashMap.put(key, arrayList);
    }

    public String process(String s) {
        String expr = s;
        while (expr.contains("f(") || expr.contains("g(") || expr.contains("h(")) {
            int posF = 0;
            int posG = 0;
            int posH = 0;
            if (expr.contains("f(")) {
                posF = expr.lastIndexOf("f(");
            }
            if (expr.contains("g(")) {
                posG = expr.lastIndexOf("g(");
            }
            if (expr.contains("h(")) {
                posH = expr.lastIndexOf("h(");
            }
            int begin = max(max(posF, posG), posH);
            int end = begin + 2;
            LinkedList<String> stack = new LinkedList<>();
            int pos = 0;
            while (end < expr.length()) {
                if (expr.charAt(end) == '(') {
                    stack.add("(");
                    pos++;
                    end++;
                }
                else if (expr.charAt(end) == ')') {
                    if (pos == 0) {
                        break;
                    } else if (Objects.equals(stack.getLast(), "(")) {
                        stack.removeLast();
                        pos--;
                        end++;
                    }
                } else {
                    end++;
                }
            }
            String subString = expr.substring(begin, end + 1);
            String key = Character.toString(subString.charAt(0));
            String[] sub =
                    subString.substring(2, subString.length() - 1).split(",");//分割成几个”自变量“取代x,y,z
            String stand = hashMap.get(key).get(hashMap.get(key).size() - 1);//定义的函数表达式
            //expr = hashMap.get(key).get(hashMap.get(key).size() - 1);
            for (int i = 0;i < sub.length;i++) {
                stand = stand.replace(hashMap.get(key).get(i), "(" + sub[i] + ")");
            }
            stand = "(" + stand + ")";
            expr = expr.replace(subString, stand);
        }
        expr = "(" + expr + ")";
        return expr;
    }

    private int max(int indexOf, int indexOf1) {
        return Math.max(indexOf, indexOf1);
    }
}
