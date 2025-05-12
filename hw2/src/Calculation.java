import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

public class Calculation {
    public static int judgeEqual(ArrayList<Result> exp1, ArrayList<Result> exp2) {
        if (exp1.size() != exp2.size()) {
            return 0;
        } else {
            for (int i = 0; i < exp1.size(); i++) {
                if (exp1.get(i).equal(exp2.get(i)) == 0) {
                    return 0;
                }
            }
            return 1;
        }
    }

    public static Integer searchIndex(BigInteger index,
                                      ArrayList<Result> exp, ArrayList<Result> results) {
        for (int i = 0; i < results.size(); i++) {
            if (judgeEqual(results.get(i).getExp(), exp) == 1
                    && Objects.equals(results.get(i).getIndex(), index)) {
                return i;
            }
        }
        return -1;
    }

    public static Term Mul(ArrayList<Factor> factors) {
        Term term = new Term(factors);
        term.setResults(factors.get(0).getResults());
        for (int i = 1; i < factors.size(); i++) {
            Factor now = factors.get(i);
            ArrayList<Result> resultsTemp = new ArrayList<>();
            for (int j = 0; j < term.getResults().size(); j++) {
                for (int k = 0; k < now.getResults().size(); k++) {
                    BigInteger lastCoefficient = term.getResults().get(j).getCoefficient();
                    BigInteger nowCoefficient = now.getResults().get(k).getCoefficient();

                    BigInteger lastIndex = term.getResults().get(j).getIndex();
                    BigInteger nowIndex = now.getResults().get(k).getIndex();

                    ArrayList<Result> lastExp = term.getResults().get(j).getExp();
                    ArrayList<Result> nowExp = now.getResults().get(k).getExp();

                    int sign = searchIndex(
                            lastIndex.add(nowIndex), mulExp(lastExp, nowExp), resultsTemp);

                    if (sign == -1) {
                        Result result = new Result(lastCoefficient.multiply(nowCoefficient),
                                lastIndex.add(nowIndex));
                        result.addExp(mulExp(lastExp, nowExp));
                        resultsTemp.add(result);
                    } else {
                        Result result = new Result(resultsTemp.get(sign).getCoefficient()
                                .add(lastCoefficient.multiply(nowCoefficient)),
                                lastIndex.add(nowIndex));
                        result.addExp(mulExp(lastExp, nowExp));
                        resultsTemp.set(sign, result);
                    }
                }
            }
            term.setResults(resultsTemp);
        }
        return term;
    }

    public static Expr Add(ArrayList<Term> terms, ArrayList<Token> ops) {
        Expr expr = new Expr(terms, ops);
        expr.setResults(terms.get(0).getResults());
        for (int i = 1; i < terms.size(); i++) {
            Term now = terms.get(i);

            if (ops.get(i - 1).getType() == Token.Type.Add) {
                for (int j = 0; j < now.getResults().size(); j++) {
                    BigInteger nowCoefficient = now.getResults().get(j).getCoefficient();
                    BigInteger nowIndex = now.getResults().get(j).getIndex();
                    ArrayList<Result> nowExp = now.getResults().get(j).getExp();

                    int sign = searchIndex(nowIndex, nowExp, expr.getResults());
                    if (sign == -1) {
                        Result result = new Result(nowCoefficient, nowIndex);
                        result.addExp(nowExp);
                        expr.getResults().add(result);
                    } else {
                        BigInteger lastCoefficient = expr.getResults().get(sign).getCoefficient();
                        Result result = new Result(lastCoefficient
                                .add(nowCoefficient), nowIndex);
                        result.addExp(nowExp);
                        expr.getResults().set(sign, result);
                    }
                }
            } else {
                for (int j = 0; j < now.getResults().size(); j++) {
                    BigInteger nowCoefficient = now.getResults().get(j).getCoefficient();
                    BigInteger nowIndex = now.getResults().get(j).getIndex();
                    ArrayList<Result> nowExp = now.getResults().get(j).getExp();

                    int sign = searchIndex(nowIndex, nowExp, expr.getResults());
                    if (sign == -1) {
                        Result result = new Result(
                                BigInteger.valueOf(0).subtract(nowCoefficient), nowIndex);
                        result.addExp(nowExp);
                        expr.getResults().add(result);
                    } else {
                        BigInteger lastCoefficient = expr.getResults().get(sign).getCoefficient();
                        Result result = new Result(lastCoefficient
                                .subtract(nowCoefficient), nowIndex);
                        result.addExp(nowExp);
                        expr.getResults().set(sign, result);
                    }
                }
            }
        }
        return expr;
    }

    public static ArrayList<Result> mulExp(
            ArrayList<Result> exp1, ArrayList<Result> exp2) {
        ArrayList<Result> newExp = new ArrayList<>();
        for (int i = 0; i < exp1.size(); i++) {
            Result result = new Result(exp1.get(i).getCoefficient(), exp1.get(i).getIndex());
            result.addExp(exp1.get(i).getExp());
            newExp.add(result);
        }
        for (int i = 0; i < exp2.size(); i++) {
            int sign = searchIndex(exp2.get(i).getIndex(), exp2.get(i).getExp(), newExp);
            if (sign == -1) {
                Result result = new Result(exp2.get(i).getCoefficient(), exp2.get(i).getIndex());
                result.addExp(exp2.get(i).getExp());
                newExp.add(result);
            } else {
                newExp.get(sign).changeCoefficient(
                        newExp.get(sign).getCoefficient().add(exp2.get(i).getCoefficient()));
            }
        }
        return newExp;
    }
}
