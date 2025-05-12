public class Token {
    public enum Type {
        Add, Sub, Mul, Power, Open_paren, Close_paren, Num, PowerNum, Exp
    }

    private final Type type;
    private final String content;

    public Token(Type type, String content) {
        this.type = type;
        this.content = content;
    }

    public Type getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
