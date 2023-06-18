public class Lexer {
    private final String input;
    private int pos = 0;
    private String nowRead;

    public Lexer(String s) {
        this.input = s;
        this.next();
    }

    private String getNumber() {
        StringBuilder sb = new StringBuilder();
        while (pos < this.input.length() && Character.isDigit(this.input.charAt(pos))) {
            sb.append(this.input.charAt(pos));
            ++pos;
        }
        return sb.toString();
    }

    public void next() {
        if (pos == input.length()) {
            return;
        }
        char c = input.charAt(pos);
        if (Character.isDigit(c)) {
            nowRead = getNumber();
        } else if ("()+-*^xyz".indexOf(c) != -1) {
            nowRead = String.valueOf(c);
            pos++;
        } else if (c == 's') {
            nowRead = "sin";
            pos += 3;
        } else if (c == 'c') {
            nowRead = "cos";
            pos += 3;
        } else if (c == 'd') { //新增对求导因子的解析
            StringBuilder sb = new StringBuilder();
            sb.append('d');
            pos++;
            sb.append(input.charAt(pos));
            nowRead = sb.toString();
            pos++;
        }
    }

    public String read() {
        return this.nowRead;
    }
}
