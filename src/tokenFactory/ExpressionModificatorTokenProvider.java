package tokenFactory;

import tokenizert.ExpressionModificatorToken;
import tokenizert.IToken;
import utils.Predicate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ExpressionModificatorTokenProvider implements ITokenProvider{
    public Predicate<String> getPredicate() {
        return (x) -> ExpressionModificatorToken.availableModifiers.entrySet().stream().anyMatch(k -> x.startsWith(k.getKey()));
    }

    public List<IToken> provide(String token, Object... args) {
        List<String> mods = ExpressionModificatorToken.parseExpressionMods(token);
        if (mods.isEmpty()){
            return Collections.emptyList();
        }
        return mods.stream().map(ExpressionModificatorToken::new).collect(Collectors.toList());
    }

    private int order;
    public int getOrder() {
        return this.order;
    }
    public void setOrder(int value) {
        this.order = value;
    }
}
