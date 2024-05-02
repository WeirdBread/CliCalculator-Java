package Tokenizer;

import Evaluator.DiceEvalutationResult;

import java.util.Random;

public class FudgeDiceToken extends DiceToken{
    public FudgeDiceToken(boolean isSingleDie) {
        super(isSingleDie, true);
    }

    @Override
    public DiceEvalutationResult rollDice(int diceToRoll, int edges){
        Random rnd = new Random();
        DiceEvalutationResult result = new DiceEvalutationResult();
        for (int i = 0; i < diceToRoll; i++){
            int rollResult = rnd.nextInt(3) - 1;
            result.diceRolled.add(rollResult);
            result.sum += rollResult;
        }
        result.expression = diceToRoll + "dF";
        return result;
    }
}
