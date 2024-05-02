import Evaluator.Evaluator;
import ReversePolishNotation.PolishNotator;
import Tokenizer.Tokenizer;

import javax.naming.OperationNotSupportedException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws OperationNotSupportedException {
        var scanner = new Scanner(System.in);

        var input = scanner.nextLine();

        if (input == null){
            return;
        }

        var tokenizer = new Tokenizer(input);
        var tokens = tokenizer.getResult();
        System.out.println(String.join(", ", tokens.stream().map(Object::toString).toList()));

        var polishTokens = PolishNotator.PolandizeTokens(tokens);
        System.out.println(String.join(", ", polishTokens.stream().map(Object::toString).toList()));

        var evaluator = new Evaluator(polishTokens);
        var format = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.US));
        System.out.println(format.format(evaluator.EvaluateExpression()));

        System.out.println(String.join("; ", evaluator.getDiceResults().stream().map(Object::toString).toList()));
    }
}