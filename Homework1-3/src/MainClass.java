import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = 0;
        if (scanner.hasNextInt()) {
            n = Integer.parseInt(scanner.nextLine());//读入第一个整数
        }
        DeFun defun = new DeFun();
        for (int i = 0;i < n;i++) {
            String str = scanner.nextLine();
            str = str.replace("\\s*", "");//去空格
            defun.readFun(str);//读入函数定义，存入Hashmap
        }
        String input = scanner.nextLine();
        input = defun.process(input);//替换自定义函数
        PreStr prestr = new PreStr();
        input = prestr.pre(input);
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Expr expr = (Expr) parser.parseExpr();
        System.out.println(expr);
    }
}