public class PreStr {
    public String pre(String s) {
        String str = s.replaceAll(" ", "");
        str = str.replaceAll("\t", "");
        str = str.replace("**", "^");
        if (str.charAt(0) == '+' || str.charAt(0) == '-') {
            str = '0' + str;
        }
        while (str.contains("--")) {
            str = str.replace("--", "+");
        }
        while (str.contains("++")) {
            str = str.replace("++", "+");
        }
        str = str.replaceAll("\\^\\+", "^");
        str = str.replaceAll("\\+--","+");
        str = str.replaceAll("\\++-","-");
        str = str.replaceAll("\\+-+","-");
        str = str.replaceAll("-\\+\\+","-");
        str = str.replaceAll("--\\+","+");
        str = str.replaceAll("-\\+-","+");
        str = str.replaceAll("\\+-","-");
        str = str.replaceAll("-\\+","-");
        while (str.contains("--")) {
            str = str.replace("--", "+");
        }
        while (str.contains("++")) {
            str = str.replace("++", "+");
        }
        return str;
    }
}
