package Tokenizer;

import Evaluator.DiceEvalutationResult;

import java.util.Random;

public class FudgeDiceToken extends DiceToken{
    public FudgeDiceToken(boolean isSingleDie) {
        super(isSingleDie, true);
    }

    @Override
    public DiceEvalutationResult rollDice(int diceToRoll, int edges){
        var rnd = new Random();
        var result = new DiceEvalutationResult();
        for (var i = 0; i < diceToRoll; i++){
            var rollResult = rnd.nextInt(-1, 2);
            result.diceRolled.add(rollResult);
            result.sum += rollResult;
        }
        result.expression = diceToRoll + "dF";
        return result;
    }
}
