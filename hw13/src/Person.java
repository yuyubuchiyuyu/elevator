import java.util.HashSet;

public class Person {
    private String studentId;
    private int numB; // 所持有的B类书数量
    private HashSet<String> uidC; // 所持有的C类书的uid

    public Person(String studentId) {
        this.studentId = studentId;
        this.numB = 0;
        this.uidC = new HashSet<>();
    }

    public int getNumB() {
        return numB;
    }

    public boolean judgeHasBook(String uid) {
        return uidC.contains(uid);
    }

    public void addBookC(String uid) {
        this.uidC.add(uid);
    }

    public void deleteBookC(String uid) {
        uidC.remove(uid);
    }

    public void addNumB() {
        numB++;
    }

    public void subNumB() {
        numB--;
    }

    public String getId() {
        return studentId;
    }
}
