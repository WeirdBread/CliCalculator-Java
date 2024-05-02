package ReversePolishNotation;

import Tokenizer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static Tokenizer.Enums.TokenType.Dice;

public class PolishNotator {

    public static ArrayList<IToken> PolandizeTokens(List<IToken> tokens){
        ArrayList<IToken> result = new ArrayList<IToken>();
        Stack<IToken> stack = new Stack<IToken>();

        for (IToken token : tokens) {
            switch (token.getTokenType()) {
                case Operand: result.add(token);
                break;
                case UnaryOperator: stack.push(token);
                break;
                case Dice:
                case BinaryOperator:
                    IOperator operatorToken = (IOperator)token;
                    while (!stack.isEmpty()){
                        IToken stackToken = stack.peek();
                        if (stackToken instanceof IOperator && ((IOperator) stackToken).getPriority() >= operatorToken.getPriority()
                            || stackToken instanceof UnaryOperatorToken && operatorToken.getPriority() > 0){
                            result.add(stack.pop());
                            continue;
                        }
                        break;
                    }
                    stack.push(operatorToken);
                break;
                case OpenParenthesis: stack.push(token);
                break;
                case CloseParenthesis:
                    while (!stack.isEmpty() && !(stack.peek() instanceof OpenParenthesisToken)){
                        result.add(stack.pop());
                    }
                    stack.pop();
                break;
            }
        }

        while (!stack.isEmpty()){
            result.add(stack.pop());
        }

        return result;
    }
}


