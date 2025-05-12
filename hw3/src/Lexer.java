import java.util.ArrayList;

public class Lexer {
    private final ArrayList<Token> tokens = new ArrayList<>();

    public Lexer(String input) {
        int pos = 0;
        char lastChar = 0;
        while (pos < input.length()) {
            if (input.charAt(pos) == '-' && (lastChar == '*' || lastChar == 0 || lastChar == '(')) {
                tokens.add(new Token(Token.Type.Num, "-1"));
                tokens.add(new Token(Token.Type.Mul, "*"));
                lastChar = '*';
            } else if (input.charAt(pos) == '+' && (lastChar == 0 || lastChar == '(')) {
                tokens.add(new Token(Token.Type.Num, "0"));
                tokens.add(new Token(Token.Type.Add, "+"));
                lastChar = '+';
            } else {
                pos = specialLexer(input, pos);
                lastChar = input.charAt(pos);
            }
            pos++;
        }
    }

    public int specialLexer(String input, int sign) {
        int pos = sign;
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
        } else if (input.charAt(pos) == 'd') { //pos指向'd'
            pos = pos + 2; //pos+1指向'x'，pos+2指向'('
            String expression = "";
            int left = 1;
            int right = 0;
            while (left != right) {
                expression = expression + input.charAt(pos);
                pos++;
                if (input.charAt(pos) == '(') { left++; }
                else if (input.charAt(pos) == ')') { right++; }
            } //此时pos指向dx()的')'
            expression = expression + input.charAt(pos);
            tokens.add(new Token(Token.Type.Derivative, expression));
        } else if (input.charAt(pos) == 'e') {
            pos = pos + 4;//+1是"x",+2是"p",+3是"("
            int left = 1;
            int right = 0;
            String expression = "";
            while (left != right) {
                if (input.charAt(pos) == '(') { left++; }
                else if (input.charAt(pos) == ')') {
                    right++;
                    if (left == right) {
                        break;
                    }
                }
                expression = expression + input.charAt(pos);
                pos++;
            } //此时pos指向')'
            tokens.add(new Token(Token.Type.Exp, expression));
        } else {
            char now = input.charAt(pos);
            StringBuilder sb = new StringBuilder();
            while (now >= '0' && now <= '9') {
                sb.append(now);
                pos++;
                if (pos >= input.length()) { break; }
                now = input.charAt(pos);
            }
            tokens.add(new Token(Token.Type.Num, sb.toString()));
            pos--;
        }
        return pos;
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
