import java.math.BigInteger;
import java.util.Map;

public interface Factor {
    String toString();

    void addResult(Integer index, BigInteger coefficient);

    Map<Integer, BigInteger> getResults();

    void changeMaxIndex(Integer num);

    Integer getMaxIndex();

    BigInteger getCoefficient(Integer index);

}