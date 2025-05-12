import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Expr implements Factor {
    private final ArrayList<Term> terms = new ArrayList<>();
    private final ArrayList<Token> ops = new ArrayList<>();
    private Map<Integer, BigInteger> results = new HashMap<>();
    private Integer maxIndex = 0;

    public Expr(ArrayList<Term> terms, ArrayList<Token> ops) {
        this.terms.addAll(terms);
        this.ops.addAll(ops);
    }

    public void addResult(Integer index, BigInteger coefficient) {
        results.replace(index, results.get(index).add(coefficient));
    }

    public Map<Integer, BigInteger> getResults() {
        return results;
    }

    public void setResults(Map<Integer, BigInteger> resultsTemp) {
        this.results = resultsTemp;
    }

    public void changeMaxIndex(Integer num) {
        if (num > maxIndex) {
            maxIndex = num;
        }
    }

    public Integer getMaxIndex() {
        return maxIndex;
    }

    public BigInteger getCoefficient(Integer index) {
        return results.get(index);
    }

    public void initializeResults() {
        for (int i = 0; i <= maxIndex; i++) {
            if (this.results.get(i) == null) {
                this.results.put(i, BigInteger.valueOf(0));
            }
        }
    }
}
