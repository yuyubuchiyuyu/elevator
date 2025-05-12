import java.util.ArrayList;

public class Term {
    private final ArrayList<Factor> factors = new ArrayList<>();

    private ArrayList<Result> results = new ArrayList<>();

    public Term(ArrayList<Factor> factors) {
        this.factors.addAll(factors);
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> resultsTemp) {
        this.results = resultsTemp;
    }

}
