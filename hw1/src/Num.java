import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Num implements Factor {
    private final BigInteger coefficient;
    private final Map<Integer, BigInteger> results = new HashMap<>();
    private Integer maxIndex = 0;

    public Num(String num) {
        coefficient = new BigInteger(num);
        results.put(0, coefficient);
        results.put(1, BigInteger.valueOf(0));
    }

    public String toString() {
        return String.valueOf(coefficient);
    }

    public void addResult(Integer index, BigInteger coefficient) {
        results.replace(index, results.get(index).add(coefficient));
    }

    public Map<Integer, BigInteger> getResults() {
        return results;
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
}
