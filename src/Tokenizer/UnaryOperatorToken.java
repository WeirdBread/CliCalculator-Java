package Tokenizer;

public class UnaryOperatorToken implements IOperator{
    public Enums.TokenType getTokenType(){
        return Enums.TokenType.UnaryOperator;
    }

    public String getSymbol(){
        return "u-";
    }

    public int getPriority(){
        return 4;
    }

    @Override
    public String toString(){
        return getSymbol();
    }
}
