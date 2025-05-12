import com.oocourse.spec1.exceptions.EqualRelationException;

public class MyEqualRelationException extends EqualRelationException {
    private static final Count count = new Count("er");
    private final int min;
    private final int max;

    public MyEqualRelationException(int id1, int id2) {
        count.process(id1, id2);
        if (id1 < id2) {
            min = id1;
            max = id2;
        } else {
            min = id2;
            max = id1;
        }
    }

    public void print() {
        System.out.println("er-" + count.getSum() +
                ", " + min + "-" + count.getIdSum(min) + ", " + max + "-" + count.getIdSum(max));
    }
}