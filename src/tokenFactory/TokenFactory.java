package tokenFactory;

import tokenizert.IToken;

import java.util.*;

public class TokenFactory implements ITokenFactory {
    private static TokenFactory instance;
    private final List<ITokenProvider> tokenProviderList = new ArrayList<>();

    private TokenFactory(){
        this.registerProvider(new OperandTokenProvider(), 1)
                .registerProvider(new MathOperatorTokenProvider(), 1)
                .registerProvider(new ParenthesisTokenProvider(), 1)
                .registerProvider(new CommonDiceTokenProvider(), 3)
                .registerProvider(new DiceModificatorTokenProvider(), 1)
                .registerProvider(new ExpressionModificatorTokenProvider(),1)
                .registerProvider(new FudgeDiceTokenProvider(), 2);
    }

    public static synchronized TokenFactory getInstance(){
        if (instance == null){
            instance = new TokenFactory();
        }
        return instance;
    }

    @Override
    public List<IToken> createTokens(String token, Object... tokenParams) {
        Optional<ITokenProvider> optProvider = this.tokenProviderList.stream().
                sorted(Comparator.comparingInt(ITokenProvider::getOrder))
                .filter(x -> x.getPredicate().perform(token))
                .findFirst();

        return optProvider.map(provider -> provider.provide(token, tokenParams)).orElse(Collections.emptyList());
    }

    private TokenFactory registerProvider(ITokenProvider provider, int order){
        provider.setOrder(order);
        this.tokenProviderList.add(provider);
        return this;
    }
}
