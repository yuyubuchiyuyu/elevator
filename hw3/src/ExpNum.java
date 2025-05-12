import java.math.BigInteger;
import java.util.ArrayList;

public class ExpNum implements Factor {
    private final BigInteger coefficient = BigInteger.valueOf(1);
    private final ArrayList<Result> results = new ArrayList<>();

    public ExpNum(String expression) {
        Result result = new Result(BigInteger.valueOf(1), BigInteger.valueOf(0));
        result.addExp(getExp(expression));
        results.add(result);
    }

    public String toString() {
        return String.valueOf(coefficient);
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public ArrayList<Result> getExp(String expression) {
        Lexer expLexer = new Lexer(expression);
        Parser expParser = new Parser(expLexer);
        Expr expr = expParser.parserExpr();
        ArrayList<Result> exp = Operation.deleteResult(expr.getResults());
        return exp;
    }
}
