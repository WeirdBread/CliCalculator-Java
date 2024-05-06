package tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class ExpressionModificatorToken implements IToken {
    public  static final String[] availableModifiersTags = { "debug" };

    public ExpressionModificatorToken(String token){
        this._symbol = token;
    }

    private final String _symbol;
    public String getSymbol(){
        return this._symbol;
    }


    public Enums.TokenType getTokenType() {
        return Enums.TokenType.ExpressionModificator;
    }

    public static ArrayList<String> parseExpressionMods(String input){
        ArrayList<String> result = new ArrayList<>();
        Optional<String> mod = Arrays.stream(ExpressionModificatorToken.availableModifiersTags).filter(input::contains).findFirst();
        if (!mod.isPresent()){
            return result;
        }
        result.add(mod.get());
        String restOfInput = input.substring(mod.get().length());
        if (!restOfInput.isEmpty()){
            result.addAll(parseExpressionMods(restOfInput));
        }
        return result;
    }
}
