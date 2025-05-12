import java.util.ArrayList;

public class Derivative implements Factor {
    private ArrayList<Result> results;

    public Derivative(String expression) {
        Lexer lexer = new Lexer(expression);
        Parser parser = new Parser(lexer);
        Expr expr = parser.parserExpr();
        this.results = Calculation.find_derivative(Operation.deleteResult(expr.getResults()));
    }

    public String toString() {
        return String.valueOf("");
    }

    public ArrayList<Result> getResults() {
        return results;
    }

}
