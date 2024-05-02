package Tokenizer;

import java.util.*;

public class Tokenizer {
    private final ArrayList<String> tokens = new ArrayList<String>();

    public Tokenizer(String input){
        Buffer buffer = new Buffer();
        char[] expression = input.toCharArray();
        for (int i = 0; i < expression.length; i++) {
            if (Character.isWhitespace(expression[i])){
                if (buffer.bufferString != null){
                    this.tokens.add(buffer.toStringAndClear());
                }
                continue;
            }

            boolean charIsDigit = Character.isDigit(expression[i]);
            boolean charIsLetter = Character.isLetter(expression[i]);
            boolean charIsPoint = expression[i] == '.';

            if ((charIsDigit || charIsLetter || charIsPoint) && expression[i] != 'd'){
                if (buffer.bufferString == null){
                    buffer.bufferString = String.valueOf(expression[i]);
                    buffer.isNumber = charIsDigit;
                    continue;
                } else if ((buffer.isNumber && (charIsDigit || charIsPoint) || (!buffer.isNumber && charIsLetter))) {
                    buffer.bufferString += expression[i];
                    continue;
                }
            }

            if (buffer.bufferString != null){
                this.tokens.add(buffer.toStringAndClear());
                if(charIsDigit || charIsLetter){
                    buffer.bufferString = String.valueOf(expression[i]);
                    buffer.isNumber = charIsDigit;
                } else{
                    tokens.add(String.valueOf(expression[i]));
                }
                continue;
            }

            tokens.add(String.valueOf(expression[i]));
        }

        if (buffer.bufferString != null) {
            this.tokens.add(buffer.toStringAndClear());
        }
    }

    public  ArrayList<IToken> getResult() throws IllegalArgumentException{
        ArrayList<IToken> result = new ArrayList<IToken>();
        Integer diceModBuffer = null;
        for (int i = 0; i < this.tokens.size(); i++) {
            diceModBuffer = fillTokens(result, this.tokens, i, diceModBuffer);
        }
        return  result;
    }

    private Integer fillTokens(ArrayList<IToken> tokens, ArrayList<String> stringTokens, int index, Integer diceModBuffer){
        String t = stringTokens.get(index);
        if (tryParseDouble(t) != null && getDiceTokenIfLast(tokens) == null){
            tokens.add(new OperandToken(Double.parseDouble(t)));
            return diceModBuffer;
        }
        if (t.equals("-") && (tokens.isEmpty()
                || !((tokens.get(tokens.size()-1) instanceof OperandToken) || (tokens.get(tokens.size()-1) instanceof CloseParenthesisToken)))) {
            tokens.add(new UnaryOperatorToken());
            return diceModBuffer;
        }
        if (Arrays.asList(BinaryOperatorToken.operatorSymbols).contains(t)){
            tokens.add(new BinaryOperatorToken(t));
            return diceModBuffer;
        }
        if (t.equals("(")){
            tokens.add(new OpenParenthesisToken());
            return diceModBuffer;
        }
        if (t.equals(")")){
            tokens.add(new CloseParenthesisToken());
            return diceModBuffer;
        }
        if (t.startsWith("dF")){
            boolean isSingleDie = tokens.isEmpty() || !(tokens.get(tokens.size()-1) instanceof OperandToken);
            tokens.add(new FudgeDiceToken(isSingleDie));
            addDiceModificators(t.substring(2), tokens, diceModBuffer);
            return null;
        }
        if (t.equals("d")){
            boolean isSingleDie = tokens.isEmpty() || !(tokens.get(tokens.size()-1) instanceof OperandToken);
            tokens.add(new DiceToken(isSingleDie, false));
            return diceModBuffer;
        }
        if (Arrays.stream(DiceToken.availableModifiersTags).anyMatch(t::contains)){
            addDiceModificators(t, tokens, diceModBuffer);
            return null;
        }
        if (tryParseInt(t) != null) {
            DiceToken prevDiceToken = getDiceTokenIfLast(tokens);
            if (prevDiceToken != null){
                DiceToken.DiceModificator diceMod = prevDiceToken.modificators.isEmpty() ? null : prevDiceToken.modificators.get(prevDiceToken.modificators.size() - 1);
                if (diceMod != null && !diceMod.getIsLeftOriented()){
                    diceMod.param = Integer.parseInt(t);
                    return diceModBuffer;
                }
                return Integer.parseInt(t);
            }
            return diceModBuffer;
        }
        throw new IllegalArgumentException(String.format("Unexpected token: '%s', pos: %d:%d", t, index + 1, index + 1 + t.length()));
    }

    private static ArrayList<String> parseDiceMods(String input){
        ArrayList<String> result = new ArrayList<String>();
        Optional<String> mod = Arrays.stream(DiceToken.availableModifiersTags).filter(input::contains).findFirst();
        if (!mod.isPresent()){
            return result;
        }
        result.add(mod.get());
        String restOfInput = input.substring(mod.get().length());
        if (!restOfInput.isEmpty()){
            result.addAll(parseDiceMods(restOfInput));
        }
        return result;
    }

    private static void addDiceModificators(String input, ArrayList<IToken> tokens, Integer diceModBuffer){
        ArrayList<String> mods = parseDiceMods(input);
        DiceToken prevDiceToken = getDiceTokenIfLast(tokens);
        for (String mod : mods){
            DiceToken.DiceModificator newMod = prevDiceToken.addModificator(mod);
            if (newMod != null && newMod.getIsLeftOriented()){
                newMod.param = diceModBuffer;
                diceModBuffer = null;
            }
        }
    }

    private static DiceToken getDiceTokenIfLast(ArrayList<IToken> tokens){
        if (tokens.isEmpty()){
            return null;
        }
        IToken lastToken = tokens.get(tokens.size()-1);
        if (lastToken instanceof DiceToken && ((DiceToken)lastToken).getHasStaticEdges()) {
            return (DiceToken) lastToken;
        }
        if (tokens.size() > 1){
            IToken secondLastToken = tokens.get(tokens.size() - 2);
            if (secondLastToken instanceof DiceToken && !((DiceToken)secondLastToken).getHasStaticEdges()){
                return (DiceToken) secondLastToken;
            }
        }
        return null;
    }

    private Integer tryParseInt(String string){
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException _) {
            return null;
        }
    }

    private Double tryParseDouble(String string){
        try {
            return Double.parseDouble(string);
        }
        catch (NumberFormatException _) {
            return null;
        }
    }

    private class Buffer{
        public String bufferString = null;
        public boolean isNumber = false;

        public String toStringAndClear(){
            String result = this.bufferString;
            this.bufferString = null;
            this.isNumber = false;
            return result;
        }
    }
}
