package tokenizert;

import evaluator.DiceEvaluationResult;
import evaluator.IDiceEvaluator;
import evaluator.IEvaluationLogger;
import evaluator.IEvaluator;

import javax.naming.LimitExceededException;
import java.util.Random;
import java.util.Stack;

public class FudgeDiceToken extends CommonDiceToken {

    @Override
    public String getSymbol(){
        return "dF";
    }

    @Override
    public void doOperation(IEvaluator evaluator) throws LimitExceededException {
        IDiceEvaluator diceEvaluator = (IDiceEvaluator) evaluator;
        Stack<IToken> stack = evaluator.getEvaluationStack();

        while (stack.peek() instanceof DiceModificatorToken){
            this.addModificator((DiceModificatorToken)stack.pop());
        }

        OperandToken leftOperand = this.isSingleDie ? null : (OperandToken) stack.pop();

        DiceEvaluationResult diceResult = diceEvaluator.evaluateDice(this, leftOperand, null);
        stack.push(new OperandToken(diceResult.sum));
        IEvaluationLogger logger = evaluator.getLogger();
        if (logger != null){
            logger.logEvaluationResult(diceResult.toString());
        }
    }

    @Override
    public DiceEvaluationResult rollDice(Random rnd, int diceToRoll, int edges){
        DiceEvaluationResult result = new DiceEvaluationResult();
        for (int i = 0; i < diceToRoll; i++){
            int rollResult = rnd.nextInt(3) - 1;
            result.diceRolled.add(rollResult);
            result.sum += rollResult;
        }
        result.expression = diceToRoll + "dF";
        return result;
    }
}
