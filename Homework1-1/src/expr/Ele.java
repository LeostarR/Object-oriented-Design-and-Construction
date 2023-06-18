package expr;

import java.math.BigInteger;
import java.util.HashMap;

public class Ele implements Comparable {
    private BigInteger coe;
    private final HashMap<String, BigInteger> hashVar = new HashMap<>();
    //private final HashMap<String, BigInteger> hashSin = new HashMap<>();
    //private final HashMap<String, BigInteger> hashCos = new HashMap<>();

    public Ele() {
        coe = BigInteger.valueOf(1);
        hashVar.put("x", BigInteger.valueOf(0));
        hashVar.put("y", BigInteger.valueOf(0));
        hashVar.put("z", BigInteger.valueOf(0));
    }

    public void setCoe(BigInteger coe) {
        this.coe = coe;
    }

    public Ele zero() {
        this.coe = BigInteger.valueOf(0);
        return this;
    }

    public void setHashVar(String s, BigInteger bigInteger) {
        this.hashVar.put(s, bigInteger);//put or replace?
    }

    public void initHashVar(HashMap<String, BigInteger> hash) {
        this.hashVar.clear();
        this.hashVar.putAll(hash);
    }

    public BigInteger getCoe() {
        return this.coe;
    }

    public HashMap<String, BigInteger> getHashVar() {
        return hashVar;
    }

    public boolean canMerge(Ele ele) {
        int flag = 0;
        if (this.hashVar.get("x").equals(ele.getHashVar().get("x"))
                && this.hashVar.get("y").equals(ele.getHashVar().get("y"))
                && this.hashVar.get("z").equals(ele.getHashVar().get("z"))) {
            flag = 1;
        }
        return flag == 1;
    }

    public boolean isOne() {
        int flag1 = 0;
        int flag2 = 0;
        if (this.coe.equals(BigInteger.valueOf(1)) || this.coe.equals(BigInteger.valueOf(-1))) {
            flag1 = 1;
        }
        if (this.getHashVar().get("x").equals(BigInteger.valueOf(0))
                && this.getHashVar().get("y").equals(BigInteger.valueOf(0))
                && this.getHashVar().get("z").equals(BigInteger.valueOf(0))) {
            flag2 = 1;
        }
        //在Expr中实现simplify
        int flag = flag1 & flag2;
        return flag == 1;
    }

    public boolean isZero() {
        return this.getCoe().equals(BigInteger.valueOf(0));
    }

    @Override
    public int compareTo(Object o) {
        Ele ele = (Ele) o;
        if (this.hashVar.get("x").compareTo(ele.hashVar.get("x")) > 0) {
            return 1;
        } else if (this.hashVar.get("x").compareTo(ele.hashVar.get("x")) < 0) {
            return -1;
        } else {
            if (this.hashVar.get("y").compareTo(ele.hashVar.get("y")) > 0) {
                return 1;
            } else if (this.hashVar.get("y").compareTo(ele.hashVar.get("y")) < 0) {
                return -1;
            } else {
                return Integer.compare(this.hashVar.get("z").compareTo(ele.hashVar.get("z")), 0);
            }
        }
    }
}
