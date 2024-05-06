package tokenFactory;

import tokenizer.IToken;
import tokenizer.OperandToken;
import utils.Helper;
import utils.Predicate;

import java.util.Collections;
import java.util.List;

public final class OperandTokenProvider implements ITokenProvider{
    private double _value;

    public Predicate<String> getPredicate() {
        return (x) -> {
            Double value = Helper.tryParseDouble(x);
            if (value == null){
                return false;
            }
            this._value = value;
            return true;
        };
    }

    public List<IToken> provide(String token, Object... args) {
        return Collections.singletonList(new OperandToken(this._value));
    }

    private int order;
    public int getOrder() {
        return this.order;
    }
    public void setOrder(int value) {
        this.order = value;
    }
}
