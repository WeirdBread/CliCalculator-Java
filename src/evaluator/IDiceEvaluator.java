package evaluator;

import tokenizer.CommonDiceToken;
import tokenizer.OperandToken;

import javax.naming.LimitExceededException;

public interface IDiceEvaluator extends IEvaluator{
    void setSeed(Long seed);
    DiceEvaluationResult evaluateDice(CommonDiceToken diceToken, OperandToken leftOperand, OperandToken rightOperand) throws LimitExceededException;
}
