package ReversePolishNotation;

import Tokenizer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PolishNotator {

    public static List<IToken> PolandizeTokens(List<IToken> tokens){
        var result = new ArrayList<IToken>();
        var stack = new Stack<IToken>();

        for (var token : tokens) {
            switch (token.getTokenType()) {
                case Enums.TokenType.Operand -> result.add(token);
                case Enums.TokenType.UnaryOperator -> stack.push(token);
                case Enums.TokenType.BinaryOperator, Enums.TokenType.Dice -> {
                    var operatorToken = (IOperator)token;
                    while (!stack.isEmpty()){
                        var stackToken = stack.peek();
                        if (stackToken instanceof IOperator stackOperatorToken && stackOperatorToken.getPriority() >= operatorToken.getPriority()
                            || stackToken instanceof UnaryOperatorToken && operatorToken.getPriority() > 0){
                            result.add(stack.pop());
                            continue;
                        }
                        break;
                    }
                    stack.push(operatorToken);
                }
                case Enums.TokenType.OpenParenthesis -> stack.push(token);
                case Enums.TokenType.CloseParenthesis -> {
                    while (!stack.isEmpty() && !(stack.peek() instanceof OpenParenthesisToken)){
                        result.add(stack.pop());
                    }
                    stack.pop();
                }
            }
        }

        while (!stack.isEmpty()){
            result.add(stack.pop());
        }

        return result;
    }
}


