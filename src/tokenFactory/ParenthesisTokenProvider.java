package tokenFactory;

import tokenizert.CloseParenthesisToken;
import tokenizert.IToken;
import tokenizert.OpenParenthesisToken;
import utils.Predicate;

import java.util.Collections;
import java.util.List;

public final class ParenthesisTokenProvider implements ITokenProvider{
    public Predicate<String> getPredicate() {
        return (x) -> x.equals("(") || x.equals(")");
    }

    public List<IToken> provide(String token, Object... args) {
        return Collections.singletonList(token.equals("(") ? new OpenParenthesisToken() : new CloseParenthesisToken());
    }

    private int order;
    public int getOrder() {
        return this.order;
    }
    public void setOrder(int value) {
        this.order = value;
    }
}
