package tokenizer;

import tokenFactory.ITokenFactory;

import java.util.*;

public class Tokenizer implements ITokenizer {
    private final String inputExpression;
    private final ITokenFactory tokenFactory;

    public Tokenizer(String inputExpression, ITokenFactory tokenFactory){
        this.inputExpression = inputExpression;
        this.tokenFactory = tokenFactory;
    }

    public TokenCollection generateTokens() {
        TokenCollection result = new TokenCollection();
        List<String> splitedExpression = this.splitByTokens();
        for (String s : splitedExpression) {
            for (IToken token : this.tokenFactory.createTokens(s)){
                if (token instanceof MathOperatorToken) {
                    convertToUnaryIfNeeded(result, (MathOperatorToken)token);
                }
                result.add(token);
            }
        }
        return result;
    }

    /**
     * Преобразует математический оператор в унарный при необходимости.
     */
    private static void convertToUnaryIfNeeded(TokenCollection tokens, MathOperatorToken mathToken){
        // Унарным может быть только минус и плюс.
        if (mathToken.getOperatorType() != MathOperatorToken.OperatorType.Subtract
                && mathToken.getOperatorType() != MathOperatorToken.OperatorType.Sum){
            return;
        }
        if (tokens.isEmpty()) {
            mathToken.convertToUnary();
            return;
        }
        IToken prevToken = tokens.get(tokens.size() - 1);
        if (prevToken instanceof MathOperatorToken) {
            if (((MathOperatorToken)prevToken).isUnary()){
                throw new IllegalArgumentException(); // Два унарных оператора не могут идти подряд.
            }
            mathToken.convertToUnary();
        }
        if (prevToken instanceof OpenParenthesisToken) {
            mathToken.convertToUnary();
        }
    }

    private List<String> splitByTokens(){
        ArrayList<String> tokens = new ArrayList<>();
        Buffer buffer = new Buffer();
        for (int i = 0; i < this.inputExpression.length(); i++){
            resolveChar(this.inputExpression.charAt(i), tokens, buffer);
        }

        buffer.popIntoList(tokens);
        return tokens;
    }

    private static void resolveChar(char ch, List<String> tokens, Buffer buffer){
        if (Character.isWhitespace(ch)){
            buffer.popIntoList(tokens);
            return;
        }

        boolean charIsDigit = Character.isDigit(ch);
        boolean charIsLetter = Character.isLetter(ch);
        boolean charIsPoint = ch == '.';

        if (charIsDigit || charIsLetter || charIsPoint){
            if (buffer.bufferString == null){
                buffer.bufferString = String.valueOf(ch);
                buffer.isNumber = charIsDigit;
            } else if ((buffer.isNumber && (charIsDigit || charIsPoint)) || (!buffer.isNumber && charIsLetter)) {
                buffer.bufferString += String.valueOf(ch);
            } else {
                buffer.popIntoList(tokens);
                buffer.bufferString = String.valueOf(ch);
                buffer.isNumber = charIsDigit;
            }
            return;
        }

        buffer.popIntoList(tokens);
        tokens.add(String.valueOf(ch));
    }

    private static class Buffer{
        public String bufferString = null;
        public boolean isNumber = false;

        public void popIntoList(List<String> list){
            if (bufferString != null){
                list.add(this.toStringAndClear());
            }
        }

        public String toStringAndClear(){
            String result = this.bufferString;
            this.bufferString = null;
            this.isNumber = false;
            return result;
        }
    }
}
