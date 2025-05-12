import java.math.BigInteger;
import java.util.Objects;

public class Operation {
    public static void printMath(Expr expr) {
        int flag0 = 1;// 判断系数是否全为0
        int flagNum = 0; //判断是否仅有常数项
        for (int i = expr.getMaxIndex(); i >= 0; i--) {
            if (!Objects.equals(expr.getResults().get(i), BigInteger.valueOf(0))) {
                if (flag0 == 1 && i == 0) {
                    flagNum = 1;
                }
                flag0 = 0;
            }
        }
        if (flag0 == 1) {
            System.out.print("0");
        } else if (flagNum == 1) {
            System.out.print(expr.getResults().get(0));
        } else {
            // 先输出正系数项
            int firstChar = 1;
            for (int i = expr.getMaxIndex(); i >= 0; i--) {
                // 系数的符号
                if (expr.getResults().get(i).signum() == 1) {
                    if (firstChar == 1) {
                        firstChar = 0;
                    } else {
                        System.out.print("+");
                    }
                    printPositiveNum(expr, i);
                }
            }
            // 再输出系数项
            for (int i = expr.getMaxIndex(); i >= 0; i--) {
                if (expr.getResults().get(i).signum() == -1) {
                    printNegativeNum(expr, i);
                }
            }
        }

    }

    public static void printPositiveNum(Expr expr, int i) {
        // 系数
        if (expr.getResults().get(i).equals(BigInteger.valueOf(1))) {
            if (i == 0) {
                System.out.print('1');//输出
            }
        } else if (i == 0) {
            System.out.print(expr.getResults().get(i));//输出
        } else {
            System.out.print(expr.getResults().get(i) + "*");//输出
        }
        // x 系数不为0且指数不为0时输出x
        if ((!Objects.equals(expr.getResults().get(i), BigInteger.valueOf(0)) && i != 0)) {
            System.out.print("x");
        }
        // 指数 指数不为1且不为0，系数不为0时输出指数
        if (i != 1 && i != 0 &&
                (!Objects.equals(expr.getResults().get(i), BigInteger.valueOf(0)))) {
            System.out.print("^" + i);
        }
    }

    public static void printNegativeNum(Expr expr, int i) {
        // 系数
        if (expr.getResults().get(i).equals(BigInteger.valueOf(-1))) {
            System.out.print('-');//输出
            if (i == 0) {
                System.out.print("1");//输出
            }
        } else if (i == 0) {
            System.out.print(expr.getResults().get(i));//输出
        } else {
            System.out.print(expr.getResults().get(i) + "*");//输出
        }
        // x 系数不为0且指数不为0时输出x
        if ((!Objects.equals(expr.getResults().get(i), BigInteger.valueOf(0)) && i != 0)) {
            System.out.print("x");
        }
        // 指数 指数不为1且不为0，系数不为0时输出指数
        if (i != 1 && i != 0 &&
                (!Objects.equals(expr.getResults().get(i), BigInteger.valueOf(0)))) {
            System.out.print("^" + i);
        }
    }
}
