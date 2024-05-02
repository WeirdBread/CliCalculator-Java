package Evaluator;

import Tokenizer.*;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Evaluator {

    public Evaluator(List<IToken> tokens){
        this.tokens = tokens;
        this._diceResults = new ArrayList<DiceEvalutationResult>();
    }

    private final List<IToken> tokens;

    private List<DiceEvalutationResult> _diceResults;
    public List<DiceEvalutationResult> getDiceResults() {
        return this._diceResults;
    }

    public double EvaluateExpression(Long seed) throws OperationNotSupportedException {
        Stack<IToken> stack = new Stack<IToken>();
        Random rnd = new Random();
        if (seed != null){
            rnd.setSeed(seed);
        }
        DiceEvaluator diceEvaluator = new DiceEvaluator(rnd);
        for (IToken token: tokens) {
            switch (token.getTokenType()){
                case Operand:
                    stack.push(token);
                break;
                case UnaryOperator:
                    OperandToken operand = (OperandToken) stack.pop();
                    operand.inverse();
                    stack.push(operand);
                break;
                case BinaryOperator:
                    OperandToken rightOperand = (OperandToken) stack.pop();
                    OperandToken leftOperand = (OperandToken) stack.pop();
                    switch (((BinaryOperatorToken)token).getOperatorType()){
                        case Sum:
                            stack.push(new OperandToken(leftOperand.getNumber() + rightOperand.getNumber()));
                        break;
                        case Subtract:
                            stack.push(new OperandToken(leftOperand.getNumber() - rightOperand.getNumber()));
                        break;
                        case Multiply:
                            stack.push(new OperandToken(leftOperand.getNumber() * rightOperand.getNumber()));
                        break;
                        case Divide:
                            stack.push(new OperandToken(leftOperand.getNumber() / rightOperand.getNumber()));
                        break;
                        case Power:
                            stack.push(new OperandToken(Math.pow(leftOperand.getNumber(), rightOperand.getNumber())));
                        break;
                    }
                break;
                case Dice:
                    rightOperand = ((DiceToken)token).getHasStaticEdges() ? null :(OperandToken) stack.pop();
                    leftOperand = ((DiceToken)token).getIsSingleDie() ? null : (OperandToken) stack.pop();

                    DiceEvalutationResult diceResult = diceEvaluator.evaluateDice((DiceToken) token, leftOperand, rightOperand);
                    this._diceResults.add(diceResult);
                    stack.push(new OperandToken(diceResult.sum));
                break;
            }
        }

        IToken result = stack.pop();
        if (result instanceof OperandToken){
            return ((OperandToken) result).getNumber();
        }

        throw new OperationNotSupportedException();
    }
}
