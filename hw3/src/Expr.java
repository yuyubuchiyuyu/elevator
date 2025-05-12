import java.util.ArrayList;

public class Expr implements Factor {
    private final ArrayList<Term> terms = new ArrayList<>();
    private final ArrayList<Token> ops = new ArrayList<>();
    private ArrayList<Result> results = new ArrayList<>();

    public Expr(ArrayList<Term> terms, ArrayList<Token> ops) {
        this.terms.addAll(terms);
        this.ops.addAll(ops);
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> resultsTemp) {
        this.results = resultsTemp;
    }

}
