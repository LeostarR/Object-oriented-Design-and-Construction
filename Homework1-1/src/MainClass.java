import expr.Expr;

import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        PreStr prestr = new PreStr();
        input = prestr.pre(input);
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Expr expr = (Expr) parser.parseExpr();
        System.out.println(expr.toString());
    }
}