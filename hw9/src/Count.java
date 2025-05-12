import java.util.HashMap;
import java.util.Objects;

public class Count {
    private String type;
    private final HashMap<Integer, Integer> counter = new HashMap<>();
    private int sum = 0;

    public Count(String type) {
        this.type = type;
    }

    public int getSum() {
        return sum;
    }

    public int getIdSum(Integer id) {
        return counter.get(id);
    }

    public void process(int id1, int id2) {
        if (counter.containsKey(id1)) {
            Integer idSumTemp = counter.get(id1);
            counter.replace(id1, idSumTemp + 1);
        } else {
            counter.put(id1, 1);
        }
        if ((Objects.equals(type, "er") || Objects.equals(type, "rnf")) && id1 != id2) {
            if (counter.containsKey(id2)) {
                Integer idSumTemp = counter.get(id2);
                counter.replace(id2, idSumTemp + 1);
            } else {
                counter.put(id2, 1);
            }
        }
        sum++;
    }

}
