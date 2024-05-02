package Tokenizer;

import java.util.ArrayList;
import java.util.Arrays;

public class Tokenizer {
    private final ArrayList<String> tokens = new ArrayList<String>();

    public Tokenizer(String input){
        var buffer = new Buffer();
        var expression = input.toCharArray();
        for (int i = 0; i < expression.length; i++) {
            if (Character.isWhitespace(expression[i])){
                if (buffer.bufferString != null){
                    this.tokens.add(buffer.toStringAndClear());
                }
                continue;
            }

            var charIsDigit = Character.isDigit(expression[i]);
            var charIsLetter = Character.isLetter(expression[i]);
            var charIsPoint = expression[i] == '.';

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
        var result = new ArrayList<IToken>();
        Integer diceModBuffer = null;
        for (int i = 0; i < this.tokens.size(); i++) {
            switch (this.tokens.get(i)){
                case String t when tryParseDouble(t) != null
                        && (result.size() < 2 || !(result.get(result.size() - 2) instanceof DiceToken)):
                    result.add(new OperandToken(Double.parseDouble(t)));
                    break;
                case String t when t.equals("-") && (result.isEmpty()
                        || !(result.getLast() instanceof OperandToken) || !(result.getLast() instanceof CloseParenthesisToken)):
                    result.add(new UnaryOperatorToken());
                    break;
                case String t when Arrays.asList(BinaryOperatorToken.operatorSymbols).contains(t):
                    result.add(new BinaryOperatorToken(t));
                    break;
                case "(":
                    result.add(new OpenParenthesisToken());
                    break;
                case ")":
                    result.add(new CloseParenthesisToken());
                    break;
                case "d":
                    var isSingleDie = result.isEmpty() || !(result.getLast() instanceof OperandToken);
                    result.add(new DiceToken(isSingleDie));
                    break;
                case String t when Arrays.stream(DiceToken.availableModifiersTags).anyMatch(t::contains):
                    var mods = parseDiceMods(t);
                    var prevToken = result.size() < 2 ? null : result.get(result.size() - 2);
                    if (prevToken instanceof DiceToken prevDiceToken){
                        for (var mod : mods){
                            var newMod = prevDiceToken.addModificator(mod);
                            if (newMod != null && newMod.getIsLeftOriented()){
                                newMod.param = diceModBuffer;
                                diceModBuffer = null;
                            }
                        }
                    }
                    break;
                case String t when tryParseInt(t) != null:
                    prevToken = result.size() < 2 ? null : result.get(result.size() - 2);
                    if (prevToken instanceof DiceToken prevDiceToken){
                        var diceMod = prevDiceToken.modificators.isEmpty() ? null : prevDiceToken.modificators.getLast();
                        if (diceMod != null && !diceMod.getIsLeftOriented()){
                            diceMod.param = Integer.parseInt(t);
                            break;
                        }
                        diceModBuffer = Integer.parseInt(t);
                    }
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unexpected token: '%s', pos: %d:%d", this.tokens.get(i), i+1, i+1+this.tokens.get(i).length()));
            }
        }

        return  result;
    }

    private static ArrayList<String> parseDiceMods(String input){
        var result = new ArrayList<String>();
        var mod = Arrays.stream(DiceToken.availableModifiersTags).filter(input::contains).findFirst();
        if (mod.isEmpty()){
            return result;
        }

        result.add(mod.get());
        var restOfInput = input.substring(mod.get().length());
        if (!restOfInput.isEmpty()){
            result.addAll(parseDiceMods(restOfInput));
        }

        return result;
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
            var result = this.bufferString;
            this.bufferString = null;
            this.isNumber = false;
            return result;
        }
    }
}
