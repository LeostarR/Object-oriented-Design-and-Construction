package expr;

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
            if (this.flag == 1) {
                sb.append("*");
            }
            if (!h1.get(s).equals(BigInteger.valueOf(0))
                    && !h1.get(s).equals(BigInteger.valueOf(1))) {
                sb.append("sin((").append(s).append("))**").append(h1.get(s));
                this.flag = 1;
            } else if (h1.get(s).equals(BigInteger.valueOf(1))) {
                sb.append("sin((").append(s).append("))");
                this.flag = 1;
            }
        }
        HashMap<String, BigInteger> h2 = e.getHashCos();
        for (String s : h2.keySet()) {
            if (this.flag == 1) {
                sb.append("*");
            }
            if (!h2.get(s).equals(BigInteger.valueOf(0))
                    && !h2.get(s).equals(BigInteger.valueOf(1))) {
                sb.append("cos((").append(s).append("))**").append(h2.get(s));
                this.flag = 1;
            } else if (h2.get(s).equals(BigInteger.valueOf(1))) {
                sb.append("cos((").append(s).append("))");
                this.flag = 1;
            }
        }
        return sb.toString();
    }
}
