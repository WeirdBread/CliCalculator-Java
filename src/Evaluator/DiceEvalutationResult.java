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
        var diceRolledString = "";
        for (var item : this.diceRolled){
            diceRolledString += String.format("[%s]", String.join(", ", item.stream().map(Object::toString).toList()));
        }
        return String.format("%s = %d %s", this.expression, this.sum, diceRolledString);
    }
}

