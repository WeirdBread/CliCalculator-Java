package tokenFactory;

import tokenizert.IToken;

import java.util.List;

public interface ITokenFactory {
    List<IToken> createTokens(String token, Object... tokenParams);
}
