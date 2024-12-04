package expr;

import java.math.BigInteger;
import java.util.ArrayList;
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
                newList.add(e);
            }
        }
        this.exprList.clear();
        this.exprList.addAll(newList);
        this.merge();
        this.sort();
    }

    public void merge() { //合并当前List中所有同类项
        ArrayList<Ele> copy = new ArrayList<>();
        ArrayList<Ele> save = new ArrayList<>(this.exprList);
        for (int i = 0;i < this.exprList.size();i++) {
            for (int j = i + 1;j < save.size();j++) {
                if (i != j && this.exprList.get(i).canMerge(save.get(j))) {
                    Ele ele = new Ele();
                    ele.setCoe(save.get(i).getCoe().add(save.get(j).getCoe()));
                    ele.initHashVar(this.exprList.get(i).getHashVar());
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
                    return Integer.compare(0, o1.getHashVar().get("z")
                            .compareTo(o2.getHashVar().get("z")));
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
}
