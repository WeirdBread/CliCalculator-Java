package evaluator;

import tokenizer.IToken;

import java.util.Stack;

public interface IEvaluator {
    Stack<IToken> getEvaluationStack();
    IEvaluationLogger getLogger();
    double evaluate();
}
