package tokenFactory;

import tokenizert.CommonDiceToken;
import tokenizert.DiceModificatorToken;
import tokenizert.IToken;
import utils.Predicate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class DiceModificatorTokenProvider implements ITokenProvider {
    public Predicate<String> getPredicate() {
        return (x) -> CommonDiceToken.availableModifiersTags.entrySet().stream().anyMatch(k -> x.startsWith(k.getKey()));
    }

    public List<IToken> provide(String token, Object... args) {
        List<String> mods = CommonDiceToken.parseDiceMods(token);
        if (mods.isEmpty()){
            return Collections.emptyList();
        }
        return mods.stream().map(DiceModificatorToken::new).collect(Collectors.toList());
    }

    private int order;
    public int getOrder() {
        return this.order;
    }
    public void setOrder(int value) {
        this.order = value;
    }
}
