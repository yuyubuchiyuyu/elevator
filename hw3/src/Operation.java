import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operation {

    public static String Replace(String input) {
        String str = input.replace(" ", "");
        str = str.replace("\t", "");
        Pattern pattern1 = Pattern.compile("\\+\\+");
        Pattern pattern2 = Pattern.compile("--");
        Pattern pattern3 = Pattern.compile("\\+-");
        Pattern pattern4 = Pattern.compile("-\\+");
        Pattern pattern5 = Pattern.compile("\\^\\+");
        Pattern pattern6 = Pattern.compile("\\*\\+");
        Matcher matcher1 = pattern1.matcher(str);
        Matcher matcher2 = pattern2.matcher(str);
        Matcher matcher3 = pattern3.matcher(str);
        Matcher matcher4 = pattern4.matcher(str);
        Matcher matcher5 = pattern5.matcher(str);
        Matcher matcher6 = pattern6.matcher(str);
        while (matcher1.find() || matcher2.find() || matcher3.find()
                || matcher4.find() || matcher5.find() || matcher6.find()) {
            str = str.replace("++", "+");
            str = str.replace("--", "+");
            str = str.replace("+-", "-");
            str = str.replace("-+", "-");
            str = str.replace("^+", "^");
            str = str.replace("*+", "*");
        }
        return str;
    }

    public static String DeleteZero(String input) {
        String str = new String();
        int pos = 0;
        int flag0 = 1; //接下来最新出现的数字是否为前导
        while (pos < input.length()) {
            if (input.charAt(pos) == '0' && pos == 0 && pos + 1 < input.length() &&
                    input.charAt(pos + 1) >= '0' && input.charAt(pos + 1) <= '9') {
                pos++;
            } //是前导零（首位为0且后有数字）
            else if (flag0 == 1 && input.charAt(pos) == '0' &&
                    pos < input.length() - 1 && pos + 1 < input.length() &&
                    input.charAt(pos + 1) >= '0' && input.charAt(pos + 1) <= '9') {
                pos++;
            } //是前导零(前无数字后有数字)
            else {
                if (input.charAt(pos) >= '0' && input.charAt(pos) <= '9') {
                    flag0 = 0;
                } else {
                    flag0 = 1;
                }
                str = str + input.charAt(pos);
                pos++;
            }
        }
        return str;
    }

    public static ArrayList<Result> deleteResult(ArrayList<Result> results) { //删去系数为0的项
        for (int i = 0; i < results.size(); i++) {
            if (Objects.equals(results.get(i).getCoefficient(), BigInteger.valueOf(0))) {
                results.remove(i);
                i--;
            }
        }
        return results;
    }

    public static String giveResult(ArrayList<Result> results) {
        String str = "";
        // 先输出正系数项
        int firstChar = 1;
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getCoefficient().signum() == 1) {
                // 输出系数的符号
                if (firstChar == 1) {
                    firstChar = 0;
                } else {
                    str = str + "+";
                }
                Result r = results.get(i);
                str = str + givePositiveNum(r.getIndex(), r.getCoefficient(), r.getExp())
                        + givePowerNum(r.getIndex(), r.getCoefficient(), r.getExp())
                        + giveExpNum(r.getIndex(), r.getCoefficient(), r.getExp());
            }
        }
        // 再输出负系数项
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getCoefficient().signum() == -1) {
                Result r = results.get(i);
                str = str + giveNegativeNum(r.getIndex(), r.getCoefficient(), r.getExp())
                        + givePowerNum(r.getIndex(), r.getCoefficient(), r.getExp())
                        + giveExpNum(r.getIndex(), r.getCoefficient(), r.getExp());
            }
        }
        if (str.equals("")) {
            str = "0";
        }
        return str;
    }

    public static int judgeExp(ArrayList<Result> exp) {
        for (int i = 0; i < exp.size(); i++) {
            if (!Objects.equals(exp.get(i).getCoefficient(), BigInteger.valueOf(0))) {
                return 1;
            }
        }
        return 0;
    }

    public static String givePositiveNum(
            BigInteger index, BigInteger coefficient, ArrayList<Result> exp) { //此函数已默认系数大于0
        String str = "";
        // 系数
        if (coefficient.equals(BigInteger.valueOf(1))) {
            if (Objects.equals(index, BigInteger.valueOf(0)) && judgeExp(exp) == 0) {
                str = str + "1";
            }
        } else {
            str = str + coefficient;
        }
        return str;
    }

    public static String giveNegativeNum(
            BigInteger index, BigInteger coefficient, ArrayList<Result> exp) {
        String str = "";
        // 系数
        if (coefficient.equals(BigInteger.valueOf(-1))) {
            str = str + '-';
            if (Objects.equals(index, BigInteger.valueOf(0)) && judgeExp(exp) == 0) {
                str = str + "1";
            }
        } else {
            str = str + coefficient;
        }
        return str;
    }

    public static String givePowerNum(
            BigInteger index, BigInteger coefficient, ArrayList<Result> exp) {
        String str = "";
        // x前的*
        if (!coefficient.equals(BigInteger.valueOf(1))
                && !coefficient.equals(BigInteger.valueOf(-1))
                && !Objects.equals(index, BigInteger.valueOf(0))) {
            str = str + "*";
        }
        // x 系数不为0且指数不为0时输出x
        if ((!Objects.equals(coefficient, BigInteger.valueOf(0))
                && (!Objects.equals(index, BigInteger.valueOf(0))))) {
            str = str + "x";
        }
        // x的指数 指数不为1且不为0，系数不为0时输出指数
        if (!Objects.equals(index, BigInteger.valueOf(1))
                && !Objects.equals(index, BigInteger.valueOf(0)) &&
                (!Objects.equals(coefficient, BigInteger.valueOf(0)))) {
            str = str + "^" + index;
        }
        return str;
    }

    public static String giveExpString(String str, BigInteger factor, ArrayList<Result> exp) {
        ArrayList<Result> newExp = new ArrayList<>();
        for (int i = 0; i < exp.size(); i++) {
            Result result = new Result(
                    exp.get(i).getCoefficient().divide(factor), exp.get(i).getIndex());
            result.addExp(exp.get(i).getExp());
            newExp.add(result);
        }
        String resultExp = giveResult(newExp);
        if (!resultExp.contains("x")) { //常数项
            return str + "exp(" + resultExp + ")^" + factor;
        } else if (deleteExp(newExp).size() == 1 && Objects.equals(
                newExp.get(0).getCoefficient(), BigInteger.valueOf(1))
                && !resultExp.contains("*")) { //仅有一项且系数为1且该项无乘号
            return str + "exp(" + giveResult(newExp) + ")^" + factor;
        } else {
            return str + "exp((" + giveResult(newExp) + "))^" + factor;
        }

    }

    public static ArrayList<Result> deleteExp(ArrayList<Result> exp) {
        ArrayList<Result> newExp = new ArrayList<>();
        for (int i = 0; i < exp.size(); i++) {
            if (!Objects.equals(exp.get(i).getCoefficient(), BigInteger.valueOf(0))) {
                Result result = new Result(exp.get(i).getCoefficient(), exp.get(i).getIndex());
                result.addExp(exp.get(i).getExp());
                newExp.add(result);
            }
        }
        return newExp;
    }

    public static String giveExpNum(
            BigInteger index, BigInteger coefficient, ArrayList<Result> exp) { //默认系数不为0
        String str = "";
        String resultExp = giveResult(exp);
        if (Objects.equals(resultExp, "0") || deleteExp(exp).isEmpty()) {
            return str;
        } else {
            // exp前的*
            if (!Objects.equals(index, BigInteger.valueOf(0))) {
                str = str + "*";
            } else if (index.equals(BigInteger.valueOf(0))
                    && !Objects.equals(coefficient, BigInteger.valueOf(1))
                    && !Objects.equals(coefficient, BigInteger.valueOf(-1))) {
                str = str + "*";
            }
            String way1;
            if (!resultExp.contains("x")) { //只有常数项
                way1 = str + "exp(" + resultExp + ")";
            } else if (deleteExp(exp).size() == 1 && Objects.equals(
                    exp.get(0).getCoefficient(), BigInteger.valueOf(1)) &&
                    !resultExp.contains("*")) { //仅有一项且系数为1且该项无乘号
                way1 = str + "exp(" + resultExp + ")";
            } else {
                way1 = str + "exp((" + resultExp + "))";
            }
            // 最大公因数作为指数
            BigInteger commonFactor = exp.get(0).getCoefficient().abs();
            for (int i = 1; i < exp.size(); i++) {
                commonFactor = commonFactor.gcd(exp.get(i).getCoefficient()).abs();
            }
            String way2 = giveExpString(str, commonFactor, exp);
            // 列举1-10之间的因子作为指数
            if (exp.size() > 1) {
                for (BigInteger i = BigInteger.valueOf(2);
                     i.compareTo(BigInteger.valueOf(10)) < 0 && i.compareTo(commonFactor) < 0;
                     i = i.add(BigInteger.valueOf(1))) {
                    if (commonFactor.mod(i).equals(BigInteger.valueOf(0))) {
                        //对gcd的个位数因子i逐个将gcd/i作为指数
                        String tempWay = giveExpString(str, commonFactor.divide(i), exp);
                        // 判断
                        if (tempWay.length() < way2.length()) {
                            way2 = tempWay;
                        }
                    }
                }
            }
            // 判断
            if (way1.length() < way2.length()) {
                return way1; //无指数输出
            } else {
                return way2; //含指数的最短输出
            }
        }
    }
}
