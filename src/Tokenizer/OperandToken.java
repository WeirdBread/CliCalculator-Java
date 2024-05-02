package Tokenizer;

public class OperandToken implements IOperator{
    public OperandToken(double d) {
        this._number = d;
        this._symbol = String.valueOf(d);
    }

    private double _number;
    public double getNumber(){
        return this._number;
    }

    public Enums.TokenType getTokenType(){
        return Enums.TokenType.Operand;
    }

    private String _symbol;
    public String getSymbol(){
        return this._symbol;
    }

    public int getPriority(){
        return 4;
    }

    @Override
    public String toString(){
        return this._symbol;
    }

    public void inverse(){
        this._number = -this._number;
        this._symbol = this._symbol.startsWith("-")
                ? this._symbol.substring(1)
                : "-" + this._symbol;
    }
}
