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
            pos++;
            nowRead = String.valueOf(c);
        }
    }

    public String read() {
        return this.nowRead;
    }
}
