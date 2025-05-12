import com.oocourse.spec1.exceptions.RelationNotFoundException;

public class MyRelationNotFoundException extends RelationNotFoundException {
    private static final Count count = new Count("rnf");
    private final int min;
    private final int max;

    public MyRelationNotFoundException(int id1, int id2) {
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
        System.out.println("rnf-" + count.getSum() +
                ", " + min + "-" + count.getIdSum(min) + ", " + max + "-" + count.getIdSum(max));
    }
}