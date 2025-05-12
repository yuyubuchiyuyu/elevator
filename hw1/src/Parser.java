import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parserExpr() {
        ArrayList<Term> terms = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        terms.add(parserTerm());
        while (lexer.notEnd() && (lexer.now().getType() ==
                Token.Type.Add
                || lexer.now().getType() == Token.Type.Sub)) {
            ops.add(lexer.now());
            lexer.move();
            terms.add(parserTerm());
        }
        Expr expr = new Expr(terms, ops);
        for (int i = 0; i < terms.size(); i++) {
            Term now = terms.get(i);
            expr.changeMaxIndex(now.getMaxIndex());
            expr.initializeResults();
            if (i == 0) {
                expr.setResults(now.getResults());
            } else {
                if (ops.get(i - 1).getType() == Token.Type.Add) {
                    for (int j = 0; j <= now.getMaxIndex(); j++) {
                        expr.addResult(j, now.getCoefficient(j));
                    }
                } else {
                    for (int j = 0; j <= now.getMaxIndex(); j++) {
                        expr.addResult(j, BigInteger.valueOf(0).subtract(now.getCoefficient(j)));
                    }
                }
            }
        }
        return expr;
    }

    public Term parserTerm() {
        ArrayList<Factor> factors = new ArrayList<>();
        Factor lastFactor = parserFactor(); //记录上一个因子
        factors.add(lastFactor);
        while (lexer.notEnd() && (lexer.now().getType() ==
                Token.Type.Mul || lexer.now().getType() ==
                Token.Type.Power)) {
            if (lexer.now().getType() == Token.Type.Mul) {
                lexer.move();//移除*
                lastFactor = parserFactor();
                factors.add(lastFactor);
            } else {
                lexer.move(); //移除^
                String numString = parserFactor().toString();
                int num = 0;
                for (int i = 0; i < numString.length(); i++) {
                    num = num * 10 + numString.charAt(i) - '0';
                }
                if (num == 0) {
                    factors.remove(factors.size() - 1);
                    Num one = new Num("1");
                    factors.add(one);
                } else {
                    for (int i = 0; i < num - 1; i++) {
                        factors.add(lastFactor);
                    }
                }
            }
        }
        Term term = new Term(factors);
        for (int i = 0; i < factors.size(); i++) {
            Factor now = factors.get(i);
            if (i == 0) {
                term.setResults(now.getResults());
                term.changeMaxIndex(now.getMaxIndex());
                term.initializeResults();
            } else {
                Map<Integer, BigInteger> resultsTemp = new HashMap<>();
                for (int j = 0; j <= term.getMaxIndex(); j++) {
                    for (int k = 0; k <= now.getMaxIndex(); k++) {
                        if (resultsTemp.get(j + k) == null) {
                            resultsTemp.put(j + k,
                                    term.getCoefficient(j).multiply(now.getCoefficient(k)));
                        } else {
                            resultsTemp.put(j + k, resultsTemp.get(j + k)
                                    .add(term.getCoefficient(j).multiply(now.getCoefficient(k))));
                        }
                    }
                }
                term.setResults(resultsTemp);
                term.changeMaxIndex(term.getMaxIndex() + now.getMaxIndex());
                term.initializeResults();
            }
        }
        return term;
    }

    public Factor parserFactor() {
        if (lexer.now().getType() == Token.Type.Num) {
            Num num = new Num(lexer.now().getContent());
            lexer.move();
            return num;
        } else if (lexer.now().getType() == Token.Type.PowerNum) {
            PowerNum powernum = new PowerNum("1");
            lexer.move();
            return powernum;
        } else {
            lexer.move(); // 这里调用move之前lexer.now()是(
            Expr expr = parserExpr();
            lexer.move(); // 这里调用move之前lexer.now()是)
            return expr; // 调用上面的move之后刚好保证表达式因子的全部成分被跳过。
        }
    }
}
