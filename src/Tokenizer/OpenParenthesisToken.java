package Tokenizer;

public class OpenParenthesisToken implements IToken{
    public String getSymbol(){
        return "(";
    }

    public Enums.TokenType getTokenType(){
        return Enums.TokenType.OpenParenthesis;
    }

    @Override
    public String toString(){
        return this.getSymbol();
    }
}
