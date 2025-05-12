import java.math.BigInteger;
import java.util.ArrayList;

public class PowerNum implements Factor {
    private final BigInteger coefficient;
    private final ArrayList<Result> results = new ArrayList<>();

    public PowerNum(String str) {
        coefficient = new BigInteger(str);
        Result result = new Result(coefficient, BigInteger.valueOf(1));
        result.addExp(new ArrayList<>()); //置空
        results.add(result);
    }

    public String toString() {
        return String.valueOf(coefficient) + 'x';
    }

    public ArrayList<Result> getResults() {
        return results;
    }

}
