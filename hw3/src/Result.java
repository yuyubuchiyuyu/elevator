import java.math.BigInteger;
import java.util.ArrayList;

public class Result {

    // a*x^b*exp()
    private BigInteger coefficient;//x的系数
    private final BigInteger index;//x的指数
    private ArrayList<Result> exp = new ArrayList<>();//指数部分

    public Result(BigInteger coefficient, BigInteger index) {
        this.coefficient = coefficient;
        this.index = index;
    }

    public BigInteger getIndex() {
        return index;
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    public void addExp(ArrayList<Result> exp) {
        this.exp = exp;
    }

    public ArrayList<Result> getExp() {
        return exp;
    }

    public void changeCoefficient(BigInteger coefficient) {
        this.coefficient = coefficient;
    }

    public int equal(Result exp) {
        if (this.getExp().size() != exp.getExp().size() ||
                !this.getIndex().equals(exp.getIndex()) ||
                !this.getCoefficient().equals(exp.getCoefficient())) {
            return 0;
        } else {
            for (int i = 0; i < this.getExp().size(); i++) {
                if (this.getExp().get(i).equal(exp.getExp().get(i)) == 0) {
                    return 0;
                }
            }
            return 1;
        }
    }
}
