package tokenizer;

import evaluator.IEvaluator;

public class DiceModificatorToken implements IOperator{

    public DiceModificatorToken(String token){
        this._symbol = token;
    }

    public Integer parameter;

    public Enums.TokenType getTokenType(){
        return Enums.TokenType.DiceModificator;
    }

    private final String _symbol;
    public String getSymbol(){
        return this._symbol;
    }

    @Override
    public String toString(){
        return this.getSymbol();
    }

    public int getPriority() {
        return 6;
    }

    public void doOperation(IEvaluator evaluator) {
        evaluator.getEvaluationStack().push(this);
    }
}
