package evaluator;

import tokenizert.IToken;
import tokenizert.TokenCollection;

import javax.naming.LimitExceededException;
import java.util.Stack;

public interface IEvaluator {
    TokenCollection getTokens();
    Stack<IToken> getEvaluationStack();
    IEvaluationLogger getLogger();
    void setLogger(IEvaluationLogger logger);
    double evaluate() throws LimitExceededException;
}
