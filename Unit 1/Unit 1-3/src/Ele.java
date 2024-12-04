import java.math.BigInteger;
import java.util.HashMap;

public class Ele implements Comparable {
    private BigInteger coe;
    private final HashMap<String, BigInteger> hashVar = new HashMap<>();
    private final HashMap<String, BigInteger> hashSin = new HashMap<>();
    private final HashMap<String, BigInteger> hashCos = new HashMap<>();

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

    public void setHashSin(String s, BigInteger bigInteger) {
        this.hashSin.put(s, bigInteger);
    }

    public void initHashSin(HashMap<String, BigInteger> hash) {
        this.hashSin.clear();
        this.hashSin.putAll(hash);//clear?
    }

    public void putHashSin(HashMap<String, BigInteger> hash) {
        for (String s : hash.keySet()) {
            if (this.hashSin.containsKey(s)) {
                this.hashSin.replace(s, this.hashSin.get(s).add(hash.get(s)));
            } else {
                this.hashSin.put(s, hash.get(s));
            }
        }
    }

    public void setHashCos(String s, BigInteger bigInteger) {
        this.hashCos.put(s, bigInteger);
    }

    public void initHashCos(HashMap<String, BigInteger> hash) {
        this.hashCos.clear();
        this.hashCos.putAll(hash);
    }

    public void putHashCos(HashMap<String, BigInteger> hash) {
        for (String s : hash.keySet()) {
            if (this.hashCos.containsKey(s)) {
                this.hashCos.replace(s, this.hashCos.get(s).add(hash.get(s)));
            } else {
                this.hashCos.put(s, hash.get(s));
            }
        }
    }

    public BigInteger getCoe() {
        return this.coe;
    }

    public HashMap<String, BigInteger> getHashVar() {
        return hashVar;
    }

    public HashMap<String, BigInteger> getHashSin() {
        return hashSin;
    }

    public HashMap<String, BigInteger> getHashCos() {
        return hashCos;
    }

    public boolean canMerge(Ele ele) {
        int flag1 = 0;
        int flag2 = 0;
        if (this.hashVar.get("x").equals(ele.getHashVar().get("x"))
                && this.hashVar.get("y").equals(ele.getHashVar().get("y"))
                && this.hashVar.get("z").equals(ele.getHashVar().get("z"))) {
            flag1 = 1;
        }
        if (this.hashSin.equals(ele.getHashSin()) && this.hashCos.equals(ele.getHashCos())) {
            flag2 = 1;
        }
        int flag = flag1 & flag2;
        return flag == 1;
    }

    public boolean isOne() {
        int flag1 = 0;
        int flag2 = 0;
        int flag3 = 0;
        if (this.coe.equals(BigInteger.valueOf(1)) || this.coe.equals(BigInteger.valueOf(-1))) {
            flag1 = 1;
        }
        if (this.getHashVar().get("x").equals(BigInteger.valueOf(0))
                && this.getHashVar().get("y").equals(BigInteger.valueOf(0))
                && this.getHashVar().get("z").equals(BigInteger.valueOf(0))) {
            flag2 = 1;
        }
        //在Expr中实现simplify
        if (this.getHashSin().isEmpty() && this.getHashCos().isEmpty()) {
            flag3 = 1;
        }
        int flag = flag1 & flag2 & flag3;
        return flag == 1;
    }

    public boolean isZero() {
        return this.getCoe().equals(BigInteger.valueOf(0));
    }

    public boolean varContain(String s) {
        int flag = 0;
        if (! this.getHashVar().get(s).equals(BigInteger.valueOf(0))) {
            flag = 1;
        }
        return flag == 1;
    }

    public boolean sinContain(String s) {
        int flag = 0;
        for (String key : this.getHashSin().keySet()) {
            if (key.contains(s)) {
                flag = 1;
                break;
            }
        }
        return flag == 1;
    }

    public boolean cosContain(String s) {
        int flag = 0;
        for (String key : this.getHashCos().keySet()) {
            if (key.contains(s)) {
                flag = 1;
                break;
            }
        }
        return flag == 1;
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
                if (this.hashVar.get("z").compareTo(ele.hashVar.get("z")) > 0) {
                    return 1;
                } else if (this.hashVar.get("z").compareTo(ele.hashVar.get("z")) < 0) {
                    return -1;
                } else {
                    if (this.getHashSin().size() > ((Ele) o).getHashSin().size()) {
                        return 1;
                    } else if (this.getHashSin().size() < ((Ele) o).getHashSin().size()) {
                        return -1;
                    } else if (!this.getHashSin().equals(((Ele) o).getHashSin())) {
                        return 1;
                    } else {
                        if (this.getHashCos().size() > ((Ele) o).getHashCos().size()) {
                            return 1;
                        } else if (this.getHashCos().size() < ((Ele) o).getHashCos().size()) {
                            return -1;
                        } else if (!this.getHashCos().equals(((Ele) o).getHashCos())) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                }
            }
        }
    }
}
