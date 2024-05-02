package Evaluator;

public class DiceEvalutationResult{

    public DiceEvalutationResult(){
        this.diceRolled = new DiceResultList();
    }

    public String expression;

    public int sum;

    public DiceResultList diceRolled;

    @Override
    public String toString(){
        return String.format("%s = %d %s", this.expression, this.sum, this.diceRolled.toString());
    }
}

