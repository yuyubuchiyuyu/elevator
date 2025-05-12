import com.oocourse.spec1.exceptions.EqualPersonIdException;

public class MyEqualPersonIdException extends EqualPersonIdException {
    private static final Count count = new Count("epi");
    private final int id;

    public MyEqualPersonIdException(int id) {
        this.id = id;
        count.process(id, -1);
    }

    public void print() {
        System.out.println("epi-" + count.getSum() + ", " + id + "-" + count.getIdSum(id));
    }
}
