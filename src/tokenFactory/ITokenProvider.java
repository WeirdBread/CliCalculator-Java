package tokenFactory;

import tokenizer.IToken;
import utils.Predicate;

import java.util.List;

public interface ITokenProvider {
    Predicate<String> getPredicate();
    int getOrder();
    void setOrder(int value);
    List<IToken> provide(String token, Object... args);
}
