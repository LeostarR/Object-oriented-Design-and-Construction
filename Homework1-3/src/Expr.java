import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Expr implements Factor {
    private final ArrayList<Ele> exprList = new ArrayList<>();

    private int flag;

    public void initExprList(ArrayList<Ele> list) {
        this.exprList.clear();
        this.exprList.addAll(list);
    }

    public void initExpr(Ele e) {
        this.exprList.clear();
        this.exprList.add(e);
    }

    public ArrayList<Ele> getExprList() {
        return this.exprList;
    }

    public void combTerm(ArrayList<Ele> list, String s) { //加法等价于 list添加到this.exprList末尾 再合并同类项
        ArrayList<Ele> arrayList = new ArrayList<>(list);
        if (Objects.equals(s, "-")) {
            arrayList = this.reverseList(list); //先变负
        }
        this.exprList.addAll(arrayList);
        this.merge();
        this.sort();
    }

    public ArrayList<Ele> reverseList(ArrayList<Ele> list) { //将变量list取反
        for (Ele e : list) {
            e.setCoe(e.getCoe().multiply(BigInteger.valueOf(-1)));
        }
        return list;
    }

    public void reverse() { //将成员list取反
        for (Ele e : this.exprList) {
            e.setCoe(e.getCoe().multiply(BigInteger.valueOf(-1)));
        }
    }

    public void derExpr(char v) {
        Expr der = new Expr();
        String s = Character.toString(v);
        for (int i = 0;i < this.exprList.size();i++) { //循环对每一项求导
            if (!(this.exprList.get(i).varContain(s)
                    || this.exprList.get(i).sinContain(s)
                    || this.exprList.get(i).cosContain(s))) { //表达式不含该变量
                Ele e = new Ele();
                e.zero();
                Expr expr = new Expr();
                expr.initExpr(e);
                der.combTerm(expr.getExprList(), "+");
            } else {
                if (this.exprList.get(i).varContain(s)) { //x幂次不为0(大于0)
                    der.combTerm(this.derVar(i, s), "+");
                }
                if (this.exprList.get(i).sinContain(s)) {
                    der.combTerm(this.derSin(i, s), "+");
                }
                if (this.exprList.get(i).cosContain(s)) {
                    der.combTerm(this.derCos(i, s), "+");
                }
            }
        }
        this.exprList.clear();
        this.exprList.addAll(der.exprList);
        this.merge();
    }

    public ArrayList<Ele> derVar(int i, String s) {
        Ele e = new Ele();
        BigInteger big = this.exprList.get(i).getHashVar().get(s);
        e.setCoe(this.exprList.get(i).getCoe().multiply(big));//原系数乘以幂次
        e.initHashVar(this.exprList.get(i).getHashVar());
        e.setHashVar(s, big.subtract(BigInteger.valueOf(1)));//幂次减1
        e.initHashSin(this.exprList.get(i).getHashSin());//其他项直接不动代入
        e.initHashCos(this.exprList.get(i).getHashCos());
        Expr expr = new Expr();
        expr.initExpr(e);
        return expr.getExprList();
    }

    public ArrayList<Ele> derSin(int i, String s) {
        Ele e = new Ele();
        Expr expr = new Expr();
        Expr sin = new Expr();
        if (this.exprList.get(i).sinContain(s)) {
            for (String key : this.exprList.get(i).getHashSin().keySet()) {
                if (key.contains(s) &&
                        !this.exprList.get(i).getHashSin().get(key).equals(BigInteger.valueOf(0))) {
                    e.setCoe(this.exprList.get(i).
                            getCoe().multiply(this.exprList.get(i).getHashSin().get(key)));
                    e.initHashVar(this.exprList.get(i).getHashVar());
                    e.initHashSin(this.exprList.get(i).getHashSin());
                    e.initHashCos(this.exprList.get(i).getHashCos());
                    //内层函数求导 sin变cos
                    HashMap<String, BigInteger> hash = new HashMap<>();
                    hash.put(key, BigInteger.valueOf(1));
                    e.putHashCos(hash);
                    //外层函数幂次减1
                    e.setHashSin(key, this.exprList.get(i).
                            getHashSin().get(key).subtract(BigInteger.valueOf(1)));
                    expr.initExpr(e);
                    PreStr prestr = new PreStr();
                    String input = prestr.pre(key);
                    Lexer lexer = new Lexer(input);
                    Parser parser = new Parser(lexer);
                    Expr inDer = (Expr) parser.parseExpr();
                    inDer.derExpr(s.charAt(0));
                    //链式法则
                    expr.mulTerm(inDer.getExprList());
                    sin.combTerm(expr.getExprList(), "+");
                }
            }
        }
        return sin.getExprList();
    }

    public ArrayList<Ele> derCos(int i, String s) {
        Ele e = new Ele();
        Expr expr = new Expr();
        Expr cos = new Expr();
        if (this.exprList.get(i).cosContain(s)) {
            for (String key : this.exprList.get(i).getHashCos().keySet()) {
                if (key.contains(s) &&
                        !this.exprList.get(i).getHashCos().get(key).equals(BigInteger.valueOf(0))) {
                    e.setCoe(this.exprList.get(i)
                            .getCoe().multiply(this.exprList.get(i).getHashCos().get(key)));
                    e.initHashVar(this.exprList.get(i).getHashVar());
                    e.initHashSin(this.exprList.get(i).getHashSin());
                    e.initHashCos(this.exprList.get(i).getHashCos());
                    //内层函数求导 cos变sin,由于存在可合并的可能因此用put
                    HashMap<String, BigInteger> hash = new HashMap<>();
                    hash.put(key, BigInteger.valueOf(1));
                    e.putHashSin(hash);
                    //外层幂次减一，直接替换
                    e.setHashCos(key, this.exprList.get(i).
                            getHashCos().get(key).subtract(BigInteger.valueOf(1)));
                    expr.initExpr(e);
                    PreStr prestr = new PreStr();
                    String input = prestr.pre(key);
                    Lexer lexer = new Lexer(input);
                    Parser parser = new Parser(lexer);
                    Expr inDer = (Expr) parser.parseExpr();
                    inDer.derExpr(s.charAt(0));
                    //链式法则
                    expr.mulTerm(inDer.getExprList());
                    cos.combTerm(expr.getExprList(), "-");
                }
            }
        }
        return cos.getExprList();
    }

    public void mulTerm(ArrayList<Ele> list) { //实现乘法
        ArrayList<Ele> newList = new ArrayList<>(); //多次加法再合并
        for (Ele e1 : list) {
            for (Ele e2 : this.exprList) {
                Ele e = new Ele();
                e.setCoe(e1.getCoe().multiply(e2.getCoe()));
                e.setHashVar("x", e1.getHashVar().get("x").add(e2.getHashVar().get("x")));
                e.setHashVar("y", e1.getHashVar().get("y").add(e2.getHashVar().get("y")));
                e.setHashVar("z", e1.getHashVar().get("z").add(e2.getHashVar().get("z")));
                e.putHashSin(e1.getHashSin());
                e.putHashSin(e2.getHashSin());
                e.putHashCos(e1.getHashCos());
                e.putHashCos(e2.getHashCos());
                newList.add(e);
            }
        }
        this.exprList.clear();
        this.exprList.addAll(newList);
        this.merge();
        this.sort();
    }

    public void merge() { //合并当前List中所有同类项
        this.simplify();
        ArrayList<Ele> copy = new ArrayList<>();
        ArrayList<Ele> save = new ArrayList<>(this.exprList);
        for (int i = 0;i < this.exprList.size();i++) {
            for (int j = i + 1;j < save.size();j++) {
                if (i != j && this.exprList.get(i).canMerge(save.get(j))) {
                    Ele ele = new Ele();
                    ele.setCoe(save.get(i).getCoe().add(save.get(j).getCoe()));
                    ele.initHashVar(this.exprList.get(i).getHashVar());
                    ele.initHashSin(this.exprList.get(i).getHashSin());
                    ele.initHashCos(this.exprList.get(i).getHashCos());
                    save.set(j, ele);
                    save.set(i, new Ele().zero());
                    break;
                }
            }
        }
        for (Ele ele : save) {
            if (!ele.getCoe().equals(BigInteger.valueOf(0))) {
                copy.add(ele);
            }
        }
        this.exprList.clear();
        this.exprList.addAll(copy);
    }

    public void simplify() {
        ArrayList<Ele> copy = new ArrayList<>();
        for (Ele ele : this.exprList) {
            Ele e = new Ele();
            if (ele.getHashSin().containsKey("0")
                    && !ele.getHashSin().get("0").equals(BigInteger.valueOf(0))) {
                e.zero();
                copy.add(e);
                continue;
            }
            e.setCoe(ele.getCoe());
            e.initHashVar(ele.getHashVar());
            e.initHashSin(ele.getHashSin());
            e.initHashCos(ele.getHashCos());
            e.getHashSin().keySet()
                    .removeIf(s -> e.getHashSin().get(s).equals(BigInteger.valueOf(0)));
            e.getHashCos().keySet()
                    .removeIf(s -> e.getHashCos().get(s).equals(BigInteger.valueOf(0)));
            e.getHashCos().remove("0");
            copy.add(e);
        }
        this.exprList.clear();
        this.exprList.addAll(copy);
    }

    public void sort() {
        this.exprList.sort((o1, o2) -> {
            if (o1.getHashVar().get("x").compareTo(o2.getHashVar().get("x")) > 0) {
                return -1;
            } else if (o1.getHashVar().get("x").compareTo(o2.getHashVar().get("x")) < 0) {
                return 1;
            } else {
                if (o1.getHashVar().get("y").compareTo(o2.getHashVar().get("y")) > 0) {
                    return -1;
                } else if (o1.getHashVar().get("y").compareTo(o2.getHashVar().get("y")) < 0) {
                    return 1;
                } else {
                    if (o1.getHashVar().get("z").compareTo(o2.getHashVar().get("z")) > 0) {
                        return -1;
                    } else if (o1.getHashVar().get("z").compareTo(o2.getHashVar().get("z")) < 0) {
                        return 1;
                    } else {
                        if (o1.getHashSin().size() > o2.getHashSin().size()) {
                            return -1;
                        } else if (o1.getHashSin().size() < o2.getHashSin().size()) {
                            return 1;
                        } else if (!o1.getHashSin().equals(o2.getHashSin())) {
                            return -1;
                        } else {
                            if (o1.getHashCos().size() > o2.getHashCos().size()) {
                                return -1;
                            } else if (o1.getHashCos().size() < o2.getHashCos().size()) {
                                return 1;
                            } else if (!o1.getHashCos().equals(o2.getHashCos())) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    }
                }
            }
        });
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i < this.exprList.size();i++) {
            Ele e = this.exprList.get(i);
            if (i != 0 && !e.isZero() && sb.length() > 0
                    && e.getCoe().compareTo(BigInteger.valueOf(0)) > 0) {
                sb.append('+');
            }
            if (e.isOne()) {
                sb.append(e.getCoe());
            } else if (! e.isZero()) {
                this.flag = 0;
                if (!e.getCoe().equals(BigInteger.valueOf(1))) {
                    if (e.getCoe().equals(BigInteger.valueOf(-1))) {
                        sb.append("-");
                    } else {
                        sb.append(e.getCoe());
                        this.flag = 1;
                    }
                }
                sb.append(varStr(e));
                sb.append(triStr(e));
            }
        }
        String str = sb.toString();
        if (str.length() == 0) {
            str = "0";
        }
        return str;
    }

    public String varStr(Ele e) {
        StringBuilder sb = new StringBuilder();
        //int flag1 = flag;
        if (!e.getHashVar().get("x").equals(BigInteger.valueOf(0))
                && !e.getHashVar().get("x").equals(BigInteger.valueOf(1))) {
            if (this.flag == 1) {
                sb.append("*");
            }
            sb.append("x**").append(e.getHashVar().get("x").toString());
            this.flag = 1;
        }
        if (e.getHashVar().get("x").equals(BigInteger.valueOf(1))) {
            if (this.flag == 1) {
                sb.append("*");
            }
            sb.append("x");
            this.flag = 1;
        }
        if (!e.getHashVar().get("y").equals(BigInteger.valueOf(0))
                && !e.getHashVar().get("y").equals(BigInteger.valueOf(1))) {
            if (this.flag == 1) {
                sb.append("*");
            }
            sb.append("y**").append(e.getHashVar().get("y").toString());
            this.flag = 1;
        }
        if (e.getHashVar().get("y").equals(BigInteger.valueOf(1))) {
            if (this.flag == 1) {
                sb.append("*");
            }
            sb.append("y");
            this.flag = 1;
        }
        if (!e.getHashVar().get("z").equals(BigInteger.valueOf(0))
                && !e.getHashVar().get("z").equals(BigInteger.valueOf(1))) {
            if (this.flag == 1) {
                sb.append("*");
            }
            sb.append("z**").append(e.getHashVar().get("z").toString());
            this.flag = 1;
        }
        if (e.getHashVar().get("z").equals(BigInteger.valueOf(1))) {
            if (this.flag == 1) {
                sb.append("*");
            }
            sb.append("z");
            this.flag = 1;
        }
        return sb.toString();
    }

    public String triStr(Ele e) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, BigInteger> h1 = e.getHashSin();
        for (String s : h1.keySet()) {
            if (!h1.get(s).equals(BigInteger.valueOf(0))
                    && !h1.get(s).equals(BigInteger.valueOf(1))) {
                if (this.flag == 1) {
                    sb.append("*");
                }
                sb.append("sin((").append(s).append("))**").append(h1.get(s));
                this.flag = 1;
            } else if (h1.get(s).equals(BigInteger.valueOf(1))) {
                if (this.flag == 1) {
                    sb.append("*");
                }
                sb.append("sin((").append(s).append("))");
                this.flag = 1;
            }
        }
        HashMap<String, BigInteger> h2 = e.getHashCos();
        for (String s : h2.keySet()) {
            if (!h2.get(s).equals(BigInteger.valueOf(0))
                    && !h2.get(s).equals(BigInteger.valueOf(1))) {
                if (this.flag == 1) {
                    sb.append("*");
                }
                sb.append("cos((").append(s).append("))**").append(h2.get(s));
                this.flag = 1;
            } else if (h2.get(s).equals(BigInteger.valueOf(1))) {
                if (this.flag == 1) {
                    sb.append("*");
                }
                sb.append("cos((").append(s).append("))");
                this.flag = 1;
            }
        }
        return sb.toString();
    }
}
