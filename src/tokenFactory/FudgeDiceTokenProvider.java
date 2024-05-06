package tokenFactory;

import tokenizert.FudgeDiceToken;
import tokenizert.IToken;
import utils.Predicate;

import java.util.Collections;
import java.util.List;

public final class FudgeDiceTokenProvider implements ITokenProvider {
    public Predicate<String> getPredicate() {
        return (x) -> x.contains("dF");
    }

    public List<IToken> provide(String token, Object... args) {
        return Collections.singletonList(new FudgeDiceToken());
    }

    private int order;
    public int getOrder() {
        return this.order;
    }
    public void setOrder(int value) {
        this.order = value;
    }
}
