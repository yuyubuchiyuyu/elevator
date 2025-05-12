import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();//输入
        String str = Lexer.Replace(input);
        str = Lexer.DeleteZero(str);
        Lexer lexer = new Lexer(str);
        Parser parser = new Parser(lexer);
        Expr expr = parser.parserExpr();
        Operation.printMath(expr);
    }
}

