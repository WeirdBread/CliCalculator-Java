package Tokenizer;

public class BinaryOperatorToken implements IOperator{

    public static final String[] operatorSymbols = { "+", "-", "/", "*", "^" };

    public BinaryOperatorToken(String operatorSymbol){
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
        };
        this._priority = calcPriority(this._operatorType);
    }

    private final OperatorType _operatorType;
    public OperatorType getOperatorType(){
        return this._operatorType;
    }

    public Enums.TokenType getTokenType(){
        return Enums.TokenType.BinaryOperator;
    }

    private final String _symbol;
    public String getSymbol(){
        return this._symbol;
    }

    private final int _priority;
    public int getPriority(){
        return this._priority;
    }

    @Override
    public String toString(){
        return getSymbol();
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
                return 3;
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
