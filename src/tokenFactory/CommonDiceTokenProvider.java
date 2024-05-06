package tokenFactory;

import tokenizert.CommonDiceToken;
import tokenizert.IToken;
import utils.Predicate;

import java.util.Collections;
import java.util.List;

public final class CommonDiceTokenProvider implements ITokenProvider {
    public Predicate<String> getPredicate() {
        return (x) -> x.contains("d");
    }

    public List<IToken> provide(String token, Object... args) {
        return Collections.singletonList(new CommonDiceToken());
    }

    private int order;
    public int getOrder() {
        return this.order;
    }
    public void setOrder(int value) {
        this.order = value;
    }
}
