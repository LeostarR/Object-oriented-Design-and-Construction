import expr.Ele;
import expr.Expr;
import expr.Factor;

import java.math.BigInteger;
import java.util.Objects;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Factor parseExpr() {
        Expr expr = new Expr();//全部集中到这个对象里
        if (Objects.equals(lexer.read(), "+") || Objects.equals(lexer.read(), "-")) {
            //表达式的第一个项可以有符号（+，-）
            expr = (Expr) dealEx(expr);
        } else {
            //lexer.next();
            Expr newExpr = (Expr) parseTerm();
            expr.combTerm(newExpr.getExprList(), "+");
            //lexer.next();
        }
        while (Objects.equals(lexer.read(), "+") || Objects.equals(lexer.read(), "-")) {
            expr = (Expr) dealEx(expr);
        }
        return expr;
    }

    public Factor dealEx(Expr expr) {
        String sy = lexer.read();
        lexer.next();
        Expr newExpr = (Expr) parseTerm();
        expr.combTerm(newExpr.getExprList(), sy);
        return expr;
    }

    public Factor parseTerm() {
        Expr expr = new Expr();
        if (Objects.equals(lexer.read(), "+") || Objects.equals(lexer.read(), "-")) {
            //项的第一部分可能带符号（+，-）
            int symbol = 0;
            if (Objects.equals(lexer.read(), "+")) {
                symbol = 1;
            } else if (Objects.equals(lexer.read(), "-")) {
                symbol = -1;
            }
            lexer.next();
            expr = (Expr) parseFactor();
            if (symbol == -1) {
                expr.reverse();
            }
        } else {
            expr = (Expr) parseFactor();
        }
        while (Objects.equals(lexer.read(), "*")) {
            lexer.next();
            Expr newExp = (Expr) parseFactor();
            expr.mulTerm(newExp.getExprList());
        }
        return expr;
    }

    public Factor parseFactor() {
        Ele ele = new Ele();
        if (lexer.read().equals("(")) { //表达式因子，函数表达式（已在PreStr中套上了一层括号，因此也可以当成表达式因子处理）
            lexer.next();
            Expr expr = (Expr) parseExpr();
            lexer.next(); //'^'
            if (Objects.equals(lexer.read(), "^")) {
                lexer.next();
                BigInteger num = new BigInteger(lexer.read());
                Expr var = new Expr();
                var.initExprList(expr.getExprList());
                for (BigInteger i = BigInteger.valueOf(1);
                     i.compareTo(num) < 0;i = i.add(BigInteger.valueOf(1))) {
                    expr.mulTerm(var.getExprList());
                }
                if (num.equals(BigInteger.valueOf(0))) {
                    expr = new Expr();
                    Ele e = new Ele();
                    expr.initExpr(e);
                }
                lexer.next();
            }
            return expr;
        } else if (lexer.read().equals("+") || lexer.read().equals("-")) { //不是第一个带符号的因子，则一定为有符号常数
            int symbol = 0;
            if (Objects.equals(lexer.read(), "+")) {
                symbol = 1;
            } else if (Objects.equals(lexer.read(), "-")) {
                symbol = -1;
            }
            lexer.next();
            BigInteger num = new BigInteger(lexer.read()).multiply(BigInteger.valueOf(symbol));
            ele.setCoe(num);
            Expr expr = new Expr();
            expr.initExpr(ele);
            lexer.next();
            return expr;
        } else if (Character.isDigit(lexer.read().charAt(0))) { //处理无符号整数
            BigInteger num = new BigInteger(lexer.read());
            ele.setCoe(num);
            Expr expr = new Expr();
            expr.initExpr(ele);
            lexer.next();
            return expr;
        } else if (Objects.equals(lexer.read(), "sin")
                || Objects.equals(lexer.read(), "cos")) { //处理三角函数
            return parseTri();
        } else { //处理幂函数
            return  parsePow();
        }
    }

    public Expr parseTri() {
        Ele ele = new Ele();
        String str = lexer.read();
        lexer.next(); //'('
        lexer.next();
        Expr factor = (Expr) parseFactor();
        factor.simplify();
        lexer.next();
        if (Objects.equals(lexer.read(), "^")) {
            lexer.next(); //已删除'^'后的’+‘
            BigInteger num = new BigInteger(lexer.read());
            if (Objects.equals(str, "sin")) {
                ele.setHashSin(factor.toString(), num);
            } else {
                ele.setHashCos(factor.toString(), num);
            }
            lexer.next();
        } else {
            if (Objects.equals(str, "sin")) {
                ele.setHashSin(factor.toString(), BigInteger.valueOf(1));
            } else {
                ele.setHashCos(factor.toString(), BigInteger.valueOf(1));
            }
        }
        Expr expr = new Expr();
        expr.initExpr(ele);
        return expr;
    }

    public Expr parsePow() {
        Ele e = new Ele();
        String str = lexer.read();
        lexer.next();//"^"
        if (Objects.equals(lexer.read(), "^")) {
            lexer.next();
            BigInteger num = new BigInteger(lexer.read());
            e.setHashVar(str, num);
            lexer.next();
        } else {
            e.setHashVar(str, BigInteger.valueOf(1));
        }
        Expr expr = new Expr();
        expr.initExpr(e);
        return  expr;
    }
}
