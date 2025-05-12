import java.math.BigInteger;
import java.util.ArrayList;

public class Num implements Factor {
    private final BigInteger coefficient;
    private final ArrayList<Result> results = new ArrayList<>();

    public Num(String num) {
        coefficient = new BigInteger(num);
        Result result = new Result(coefficient, BigInteger.valueOf(0));
        result.addExp(new ArrayList<>()); //置空
        results.add(result);
    }

    public String toString() {
        return String.valueOf(coefficient);
    }

    public ArrayList<Result> getResults() {
        return results;
    }

}
