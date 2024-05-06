package evaluator;

public class DiceEvaluationResult {

    public DiceEvaluationResult(){
        this.diceRolled = new DiceResultCollection();
    }

    public String expression;

    public int sum;

    public DiceResultCollection diceRolled;

    @Override
    public String toString(){
        return String.format("%s = %d %s", this.expression, this.sum, this.diceRolled.toString());
    }
}

