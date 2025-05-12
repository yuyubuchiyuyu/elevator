import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private static final ArrayList<Token> tokens = new ArrayList<>();

    public static String Replace(String input) {
        String str = input.replace(" ", "");
        str = str.replace("\t", "");
        Pattern pattern1 = Pattern.compile("\\+\\+");
        Pattern pattern2 = Pattern.compile("--");
        Pattern pattern3 = Pattern.compile("\\+-");
        Pattern pattern4 = Pattern.compile("-\\+");
        Pattern pattern5 = Pattern.compile("\\^\\+");
        Pattern pattern6 = Pattern.compile("\\*\\+");
        Pattern pattern7 = Pattern.compile("\\*\\*");
        Matcher matcher1 = pattern1.matcher(str);
        Matcher matcher2 = pattern2.matcher(str);
        Matcher matcher3 = pattern3.matcher(str);
        Matcher matcher4 = pattern4.matcher(str);
        Matcher matcher5 = pattern5.matcher(str);
        Matcher matcher6 = pattern6.matcher(str);
        Matcher matcher7 = pattern7.matcher(str);
        while (matcher1.find() || matcher2.find() || matcher3.find()
                || matcher4.find() || matcher5.find() || matcher6.find() || matcher7.find()) {
            str = str.replace("++", "+");
            str = str.replace("--", "+");
            str = str.replace("+-", "-");
            str = str.replace("-+", "-");
            str = str.replace("^+", "^");
            str = str.replace("*+", "*");
            str = str.replace("**", "^");
        }
        return str;
    }

    public static String DeleteZero(String input) {
        String str = new String();
        int pos = 0;
        int flag0 = 1; //接下来最新出现的数字是否为前导
        while (pos < input.length()) {
            if (input.charAt(pos) == '0' && pos == 0 && pos + 1 < input.length() &&
                    input.charAt(pos + 1) >= '0' && input.charAt(pos + 1) <= '9') {
                pos++;
            } //是前导零（首位为0且后有数字）
            else if (flag0 == 1 && input.charAt(pos) == '0' && pos + 1 < input.length() &&
                    pos < input.length() - 1 &&
                    input.charAt(pos + 1) >= '0' && input.charAt(pos + 1) <= '9') {
                pos++;
            } //是前导零(前无数字后有数字)
            else {
                if (input.charAt(pos) >= '0' && input.charAt(pos) <= '9') {
                    flag0 = 0;
                } else {
                    flag0 = 1;
                }
                str = str + input.charAt(pos);
                pos++;
            }
        }
        return str;
    }

    public Lexer(String input) {
        int pos = 0;
        char lastChar = 0;
        while (pos < input.length()) {
            if (input.charAt(pos) == '-' &&
                    (lastChar == '*' || lastChar == 0 || lastChar == '(')) { // "*-" "(-"
                tokens.add(new Token(Token.Type.Num, "-1"));
                tokens.add(new Token(Token.Type.Mul, "*"));
                lastChar = '*';
            } else if (input.charAt(pos) == '+' &&
                    (lastChar == 0 || lastChar == '(')) {
                tokens.add(new Token(Token.Type.Num, "0"));
                tokens.add(new Token(Token.Type.Add, "+"));
                lastChar = '+';
            } else {
                if (input.charAt(pos) == '(') {
                    tokens.add(new Token(Token.Type.Open_paren, "("));
                } else if (input.charAt(pos) == ')') {
                    tokens.add(new Token(Token.Type.Close_paren, ")"));
                } else if (input.charAt(pos) == '+') {
                    tokens.add(new Token(Token.Type.Add, "+"));
                } else if (input.charAt(pos) == '-') {
                    tokens.add(new Token(Token.Type.Sub, "-"));
                } else if (input.charAt(pos) == '*') {
                    tokens.add(new Token(Token.Type.Mul, "*"));
                } else if (input.charAt(pos) == '^') {
                    tokens.add(new Token(Token.Type.Power, "^"));
                } else if (input.charAt(pos) == 'x') {
                    tokens.add(new Token(Token.Type.PowerNum, "x"));
                } else {
                    char now = input.charAt(pos);
                    StringBuilder sb = new StringBuilder();
                    while (now >= '0' && now <= '9') {
                        sb.append(now);
                        pos++;
                        if (pos >= input.length()) {
                            break;
                        }
                        now = input.charAt(pos);
                    }
                    tokens.add(new Token(Token.Type.Num, sb.toString()));
                    pos--;
                }
                lastChar = input.charAt(pos);
            }
            pos++;
        }
    }

    private int sign = 0;

    public void move() {
        sign++;
    }

    public Token now() {
        return tokens.get(sign);
    }

    public boolean notEnd() {
        return sign < tokens.size();
    }

}
