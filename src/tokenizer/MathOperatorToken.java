package tokenizer;

import evaluator.IEvaluationLogger;
import evaluator.IEvaluator;

import java.util.Stack;

public class MathOperatorToken implements IOperator{

    public static final String[] operatorSymbols = { "+", "-", "/", "*", "^" };

    public MathOperatorToken(String operatorSymbol){
        this._symbol = operatorSymbol;
        switch (operatorSymbol){
            case "+": this._operatorType = OperatorType.Sum;
            break;
            case "-": this._operatorType = OperatorType.Subtract;
            break;
            case "*": this._operatorType = OperatorType.Multiply;
            break;
            case "/": this._operatorType = OperatorType.Divide;
            break;
            case "^": this._operatorType = OperatorType.Power;
            break;
            default: throw new IllegalArgumentException();
        }
        this._priority = calcPriority(this._operatorType);
    }

    private final OperatorType _operatorType;
    public OperatorType getOperatorType(){
        return this._operatorType;
    }

    private boolean _isUnary = false;
    public boolean isUnary(){
        return this._isUnary;
    }

    public Enums.TokenType getTokenType(){
        return Enums.TokenType.MathOperator;
    }

    private String _symbol;
    public String getSymbol(){
        return this._symbol;
    }

    private int _priority;
    public int getPriority(){
        return this._priority;
    }

    public void doOperation(IEvaluator evaluator) {
        Stack<IToken> stack = evaluator.getEvaluationStack();
        OperandToken resultOperand;
        OperandToken rightOperand = (OperandToken)stack.pop();
        OperandToken leftOperand = null;
        if (this.isUnary()){
            if (this.getOperatorType() == OperatorType.Subtract){
                resultOperand = new OperandToken(rightOperand.getNumber() * -1);
            } else {
                resultOperand = rightOperand;
            }
        } else {
            leftOperand = (OperandToken)stack.pop();
            switch (this.getOperatorType()) {
                case Sum:
                    assert leftOperand != null;
                    resultOperand = new OperandToken(leftOperand.getNumber() + rightOperand.getNumber());
                break;
                case Subtract: resultOperand = new OperandToken(leftOperand.getNumber() - rightOperand.getNumber());
                break;
                case Multiply: resultOperand = new OperandToken(leftOperand.getNumber() * rightOperand.getNumber());
                break;
                case Divide: resultOperand = new OperandToken(leftOperand.getNumber() / rightOperand.getNumber());
                break;
                case Power: resultOperand = new OperandToken(Math.pow(leftOperand.getNumber(), rightOperand.getNumber()));
                break;
                default: resultOperand = null;
                break;
            }
        }
        if (resultOperand != null){
            stack.push(resultOperand);
            IEvaluationLogger logger = evaluator.getLogger();
            if (logger != null){
                logger.logEvaluationResult(String.format("%s%s %s = %s", leftOperand == null ? "" : leftOperand + " ", this, rightOperand, resultOperand));
            }
        }
    }

    @Override
    public String toString(){
        return getSymbol();
    }

    public void convertToUnary(){
        if (this._isUnary){
            return;
        }
        this._symbol = "u" + this._symbol;
        this._isUnary = true;
        this._priority = 3; // Унарный оператор выполняется перед любым другим оператором, кроме дайсов.
    }

    private static int calcPriority(OperatorType operatorType){
        switch (operatorType){
            case Subtract:
            case Sum:
                return 0;
            case Divide:
            case Multiply:
                return 1;
            case Power:
                return 2;
            default: throw new IllegalArgumentException();
        }
    }

    public enum OperatorType{
        Sum,
        Subtract,
        Multiply,
        Divide,
        Power
    }
}
