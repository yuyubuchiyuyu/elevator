import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String funcF = "";
        String funcG = "";
        String funcH = "";
        int n = Integer.parseInt(scanner.nextLine());
        for (int i = 0; i < n; i++) {
            String temp = Operation.DeleteZero(Operation.Replace(scanner.nextLine())); //输入函数并格式化
            temp = Func.funcDefine(temp,funcF,funcG,funcH);
            if (temp.contains("f")) {
                funcF = temp;
            } else if (temp.contains("g")) {
                funcG = temp;
            } else if (temp.contains("h")) {
                funcH = temp;
            }
        }
        String str = Operation.DeleteZero(Operation.Replace(scanner.nextLine())); //输入表达式并格式化;
        str = Operation.DeleteZero(Operation.Replace(
                Func.replace(str, funcF, funcG, funcH)));//进行函数替换
        Lexer lexer = new Lexer(str);
        Parser parser = new Parser(lexer);
        Expr expr = parser.parserExpr();
        System.out.print(Operation.giveResult(Operation.deleteResult(expr.getResults())));
    }
}

