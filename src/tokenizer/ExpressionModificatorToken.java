package tokenizer;

import evaluator.DebugEvaluationLogger;
import evaluator.IEvaluator;

import java.util.*;

public class ExpressionModificatorToken implements IToken {
    public static Map<String, ExpressionModificatorType> availableModifiers = new HashMap<String, ExpressionModificatorType>(){
        {
            put("Debug", ExpressionModificatorType.Debug);
            put("Max", ExpressionModificatorType.Max);
            put("Min", ExpressionModificatorType.Min);
        }
    };

    public ExpressionModificatorToken(String token){
        this._symbol = token;
        this._modificatorType = availableModifiers.get(token);
    }

    private final String _symbol;
    public String getSymbol(){
        return this._symbol;
    }

    public Enums.TokenType getTokenType() {
        return Enums.TokenType.ExpressionModificator;
    }

    private final ExpressionModificatorType _modificatorType;
    public ExpressionModificatorType getModificatorType(){
        return this._modificatorType;
    }

    public void applyExpressionMod(IEvaluator evaluator){
        TokenCollection tokens = evaluator.getTokens();
        switch (this.getModificatorType()){
            case Debug:
                evaluator.setLogger(new DebugEvaluationLogger());
                break;
            case Max:
                tokens.stream().filter(x -> x instanceof CommonDiceToken).forEach(x -> ((CommonDiceToken) x).addModificator("max"));
                break;
            case Min:
                tokens.stream().filter(x -> x instanceof CommonDiceToken).forEach(x -> ((CommonDiceToken) x).addModificator("min"));
                break;
        }
    }

    public static ArrayList<String> parseExpressionMods(String input){
        ArrayList<String> result = new ArrayList<>();
        Optional<Map.Entry<String, ExpressionModificatorType>> mod = ExpressionModificatorToken.availableModifiers.entrySet().stream().filter((k) -> input.startsWith(k.getKey())).findFirst();
        if (!mod.isPresent()){
            return result;
        }
        String key = mod.get().getKey();
        result.add(key);
        String restOfInput = input.substring(key.length());
        if (!restOfInput.isEmpty()){
            result.addAll(parseExpressionMods(restOfInput));
        }
        return result;
    }

    public enum ExpressionModificatorType {
        Debug,
        Max,
        Min
    }

    @Override
    public String toString(){
        return this._symbol;
    }
}
