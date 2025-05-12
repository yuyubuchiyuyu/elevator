import com.oocourse.spec1.exceptions.PersonIdNotFoundException;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private static final Count count = new Count("pinf");
    private final int id;

    public MyPersonIdNotFoundException(int id) {
        this.id = id;
        count.process(id, -1);
    }

    public void print() {
        System.out.println("pinf-" + count.getSum() + ", " + id + "-" + count.getIdSum(id));
    }
}