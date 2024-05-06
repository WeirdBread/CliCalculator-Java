package tokenizer;

public class CloseParenthesisToken implements IToken{

    public String getSymbol(){
        return ")";
    }

    public Enums.TokenType getTokenType(){
        return Enums.TokenType.CloseParenthesis;
    }

    @Override
    public String toString(){
        return this.getSymbol();
    }
}
