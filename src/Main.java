import Evaluator.Evaluator;
import ReversePolishNotation.PolishNotator;
import Tokenizer.Tokenizer;
import Tokenizer.IToken;

import javax.naming.OperationNotSupportedException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws OperationNotSupportedException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            if (input.isEmpty()){
                return;
            }

            Tokenizer tokenizer = new Tokenizer(input);
            ArrayList<IToken> tokens = tokenizer.getResult();
            System.out.println(String.join(", ", tokens.stream().map(Object::toString).collect(Collectors.toList())));

            ArrayList<IToken> polishTokens = PolishNotator.PolandizeTokens(tokens);
            System.out.println(String.join(", ", polishTokens.stream().map(Object::toString).collect(Collectors.toList())));

            Evaluator evaluator = new Evaluator(polishTokens);
            DecimalFormat format = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.US));
            System.out.println(format.format(evaluator.EvaluateExpression(null)));

            System.out.println(String.join("; ", evaluator.getDiceResults().stream().map(Object::toString).collect(Collectors.toList())));
        }
    }
}