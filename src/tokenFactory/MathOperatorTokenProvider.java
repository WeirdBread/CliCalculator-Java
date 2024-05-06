package tokenFactory;

import tokenizert.IToken;
import tokenizert.MathOperatorToken;
import utils.Predicate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MathOperatorTokenProvider implements ITokenProvider{

    public Predicate<String> getPredicate() {
        return (x) -> x.length() == 1 && Arrays.asList(MathOperatorToken.operatorSymbols).contains(x);
    }

    public List<IToken> provide(String token, Object... args) {
        return Collections.singletonList(new MathOperatorToken(token));
    }

    private int order;
    public int getOrder() {
        return this.order;
    }
    public void setOrder(int value) {
        this.order = value;
    }
}
