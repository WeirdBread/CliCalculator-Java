package Evaluator;

import Tokenizer.*;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.List;
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

    public double EvaluateExpression() throws OperationNotSupportedException {
        var stack = new Stack<IToken>();

        for (var token: tokens) {
            switch (token.getTokenType()){
                case Enums.TokenType.Operand -> stack.push(token);
                case Enums.TokenType.UnaryOperator -> {
                    var operand = (OperandToken) stack.pop();
                    operand.inverse();
                    stack.push(operand);
                }
                case Enums.TokenType.BinaryOperator -> {
                    var rightOperand = (OperandToken) stack.pop();
                    var leftOperand = (OperandToken) stack.pop();
                    switch (((BinaryOperatorToken)token).getOperatorType()){
                        case Sum -> stack.push(new OperandToken(leftOperand.getNumber() + rightOperand.getNumber()));
                        case Subtract -> stack.push(new OperandToken(leftOperand.getNumber() - rightOperand.getNumber()));
                        case Multiply -> stack.push(new OperandToken(leftOperand.getNumber() * rightOperand.getNumber()));
                        case Divide -> stack.push(new OperandToken(leftOperand.getNumber() / rightOperand.getNumber()));
                        case Power -> stack.push(new OperandToken(Math.pow(leftOperand.getNumber(), rightOperand.getNumber())));
                    }
                }
                case Enums.TokenType.Dice -> {
                    var rightOperand = ((DiceToken)token).getHasStaticEdges() ? null :(OperandToken) stack.pop();
                    var leftOperand = ((DiceToken)token).getIsSingleDie() ? null : (OperandToken) stack.pop();

                    var diceResult = DiceEvaluator.evaluateDice((DiceToken) token, leftOperand, rightOperand);
                    this._diceResults.add(diceResult);
                    stack.push(new OperandToken(diceResult.sum));
                }
            }
        }

        var result = stack.pop();
        if (result instanceof OperandToken){
            return ((OperandToken) result).getNumber();
        }

        throw new OperationNotSupportedException();
    }
}
