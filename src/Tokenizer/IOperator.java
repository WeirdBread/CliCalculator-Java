package tokenizer;

import evaluator.IEvaluator;

import javax.naming.LimitExceededException;

public interface IOperator extends IToken {
    int getPriority();
    void doOperation(IEvaluator evaluator) throws LimitExceededException;
}
