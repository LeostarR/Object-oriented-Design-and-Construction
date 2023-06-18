import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class DeFun {
    private final HashMap<String, ArrayList<String>> hashMap = new HashMap<>();

    public void readFun(String s) {
        final String key = Character.toString(s.charAt(0));
        ArrayList<String> arrayList = new ArrayList<>();
        int i;
        for (i = 2;i < s.length();i++) {
            if ((s.charAt(i) == 'x') || (s.charAt(i) == 'y') || (s.charAt(i) == 'z')) {
                arrayList.add(String.valueOf(Character.toUpperCase(s.charAt(i))));//确定形参
            }
            if (s.charAt(i) == '=') {
                break;
            }
        }
        String sim = this.process(s.substring(i + 1));//sim是等号右边并且替换已经定义过的函数
        if (sim.contains("d")) { //求导因子只出现一次
            int begin = sim.indexOf("d");
            //subString = dx(表达式) | dy(表达式) | dz(表达式)，是求导因子
            String subString = sim.substring(begin, findRi(begin + 3, sim) + 1);
            String derFact = subString.substring(2); //derFact是需要求导的表达式
            PreStr prestr = new PreStr();
            String input = prestr.pre(derFact);
            Lexer lexer = new Lexer(input);
            Parser parser = new Parser(lexer);
            Expr expr = (Expr) parser.parseExpr();//预处理，解析，返回一个Expr类
            expr.derExpr(subString.charAt(1));//括号内的表达式进行求导能够返回一个Expr类
            sim = sim.replace(subString, "(" + expr + ")"); //将整个求导因子剔除（替换）
        }
        //funExpr一定为正常的（不含求导因子，不含其他自定义函数）的表达式
        String funExpr = sim.replace("x", "X").replace("y", "Y").replace("z", "Z");
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
            String subString = expr.substring(begin, findRi(begin + 2, expr) + 1);
            String key = Character.toString(subString.charAt(0));
            String[] sub =
                    subString.substring(2, subString.length() - 1).split(",");//分割成几个”自变量“取代x,y,z
            String stand = hashMap.get(key).get(hashMap.get(key).size() - 1);//定义的函数表达式
            for (int i = 0;i < sub.length;i++) {
                stand = stand.replace(hashMap.get(key).get(i), "(" + sub[i] + ")");
            }
            stand = "(" + stand + ")";
            expr = expr.replace(subString, stand);
        }
        expr = "(" + expr + ")";
        return expr;
    }

    public int findRi(int e, String expr) {
        LinkedList<String> stack = new LinkedList<>();
        int end = e;
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
        return end;
    }

    private int max(int indexOf, int indexOf1) {
        return Math.max(indexOf, indexOf1);
    }
}
