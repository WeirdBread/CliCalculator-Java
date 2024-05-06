import evaluator.EvaluationLogger;
import evaluator.IEvaluationLogger;
import evaluator.TokenEvaluator;
import tokenFactory.TokenFactory;
import tokenizert.TokenCollection;
import tokenizert.Tokenizer;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
            if (input.isEmpty()){
                return;
            }

            IEvaluationLogger evaluationLogger = new EvaluationLogger();

            Tokenizer tokenizer = new Tokenizer(input, TokenFactory.getInstance());
            TokenCollection tokens = tokenizer.generateTokens();
            System.out.println(tokens);

            try {
                TokenEvaluator evaluator = new TokenEvaluator(tokenizer, evaluationLogger);
                System.out.println(evaluator.getTokens());

                DecimalFormat format = new DecimalFormat("0.#####", new DecimalFormatSymbols(Locale.US));
                System.out.println(format.format(evaluator.evaluate()));

                System.out.println(evaluator.getLogger());

            } catch (Exception ex)  {
                System.out.println(ex.getMessage());
            }
        }
    }
}