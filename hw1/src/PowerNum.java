import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class PowerNum implements Factor {
    private final BigInteger coefficient;
    private final Map<Integer, BigInteger> results = new HashMap<>();
    private Integer maxIndex = 1;

    public PowerNum(String str) {
        coefficient = new BigInteger(str);
        results.put(1, coefficient);
        results.put(0, BigInteger.valueOf(0));
    }

    public String toString() {
        return String.valueOf(coefficient) + 'x';
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
