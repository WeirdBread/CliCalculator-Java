package Tokenizer;

public class BinaryOperatorToken implements IOperator{

    public static final String[] operatorSymbols = { "+", "-", "/", "*", "^" };

    public BinaryOperatorToken(String operatorSymbol){
        this._symbol = operatorSymbol;
        this._operatorType = switch (operatorSymbol){
          case "+" -> OperatorType.Sum;
          case "-" -> OperatorType.Subtract;
          case "*" -> OperatorType.Multiply;
          case "/" -> OperatorType.Divide;
          case "^" -> OperatorType.Power;
            default -> throw new IllegalArgumentException();
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
        return switch (operatorType){
            case OperatorType.Sum, OperatorType.Subtract -> 0;
            case OperatorType.Multiply, OperatorType.Divide -> 1;
            case OperatorType.Power -> 3;
            default -> throw new IllegalArgumentException();
        };
    }

    public enum OperatorType{
        Sum,
        Subtract,
        Multiply,
        Divide,
        Power
    }
}
