package tokenizer;

import evaluator.IEvaluator;

public interface IOperator extends IToken {
    int getPriority();
    void doOperation(IEvaluator evaluator);
}
