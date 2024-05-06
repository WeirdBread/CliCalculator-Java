package tokenizert;

import evaluator.DiceEvaluationResult;
import evaluator.IDiceEvaluator;
import evaluator.IEvaluationLogger;
import evaluator.IEvaluator;

import javax.naming.LimitExceededException;
import java.util.*;

public class CommonDiceToken implements IOperator{
    public  static final Map<String, DiceModificatorType> availableModifiersTags = new HashMap<String, DiceModificatorType>(){
        {
            put("rkh", DiceModificatorType.RerollKeepHigh);
            put("rkl", DiceModificatorType.RerollKeepLow);
            put("kh", DiceModificatorType.KeepHigh);
            put("kl", DiceModificatorType.KeepLow);
            put("!", DiceModificatorType.Explosive);
            put("<", DiceModificatorType.LessThan);
            put(">", DiceModificatorType.MoreThan);
            put("max", DiceModificatorType.MaxPossible);
            put("min", DiceModificatorType.MinPossible);
        }
    };

    public boolean isSingleDie;

    public List<DiceModificator> modificators = new ArrayList<>();

    public Enums.TokenType getTokenType(){
        return Enums.TokenType.Dice;
    }

    public String getSymbol(){
        return "d";
    }

    public int getPriority(){
        return 5;
    }

    public void doOperation(IEvaluator evaluator) throws LimitExceededException {
        IDiceEvaluator diceEvaluator = (IDiceEvaluator) evaluator;
        Stack<IToken> stack = evaluator.getEvaluationStack();

        while (stack.peek() instanceof DiceModificatorToken){
            this.addModificator((DiceModificatorToken)stack.pop());
        }

        OperandToken rightOperand = (OperandToken) stack.pop();
        OperandToken leftOperand = this.isSingleDie ? null : (OperandToken) stack.pop();

        DiceEvaluationResult diceResult = diceEvaluator.evaluateDice(this, leftOperand, rightOperand);
        stack.push(new OperandToken(diceResult.sum));
        IEvaluationLogger logger = evaluator.getLogger();
        if (logger != null){
            logger.logEvaluationResult(diceResult.toString());
        }
    }

    @Override
    public String toString(){
        return this.getSymbol();
    }

    public DiceEvaluationResult rollDice(Random rnd, int diceToRoll, int edges){
        DiceEvaluationResult result = new DiceEvaluationResult();
        for (int i = 0; i < diceToRoll; i++){
            int rollResult = rnd.nextInt(edges) + 1;
            result.diceRolled.add(rollResult);
            result.sum += rollResult;
        }
        result.expression = diceToRoll + "d" + edges;
        return result;
    }

    public DiceModificator addModificator(DiceModificatorToken modificator){
        return this.addModificator(modificator.getSymbol(), modificator.parameter);
    }

    public DiceModificator addModificator(String modificator){
        return this.addModificator(modificator, null);
    }

    public DiceModificator addModificator(String modificator, Integer param){
        DiceModificatorType targetType = availableModifiersTags.get(modificator);
        DiceModificator newModificator = new DiceModificator(targetType, param);

        // Не добавляем моды того же приоритета.
        if (this.modificators.stream().anyMatch(x -> x.getPriority() == newModificator.getPriority())){
            return null;
        }

        this.modificators.add(newModificator);
        return newModificator;
    }

    public static class DiceModificator{
        public DiceModificator(DiceModificatorType type, Integer param){
            this._type = type;
            this.param = param;
            this._priority = calcPriority(type);
        }

        private final DiceModificatorType _type;
        public DiceModificatorType getType(){
            return this._type;
        }

        public Integer param;

        private final int _priority;
        public int getPriority(){
            return this._priority;
        }

        private int calcPriority(DiceModificatorType type){
            switch (type){
                case MoreThan:
                case LessThan:
                    return 0;
                case KeepHigh:
                case KeepLow:
                    return 1;
                case RerollKeepHigh:
                case RerollKeepLow:
                    return 2;
                case Explosive:
                case MaxPossible:
                case MinPossible:
                    return 3;
                default: return -1;
            }
        }
    }

    public enum DiceModificatorType{
        KeepHigh,
        KeepLow,
        RerollKeepHigh,
        RerollKeepLow,
        Explosive,
        LessThan,
        MoreThan,
        MaxPossible,
        MinPossible,
    }

    public static ArrayList<String> parseDiceMods(String input){
        ArrayList<String> result = new ArrayList<>();
        Optional<Map.Entry<String, DiceModificatorType>> mod = CommonDiceToken.availableModifiersTags.entrySet().stream().filter((k) -> input.startsWith(k.getKey())).findFirst();
        if (!mod.isPresent()){
            return result;
        }
        String key = mod.get().getKey();
        result.add(key);
        String restOfInput = input.substring(key.length());
        if (!restOfInput.isEmpty()){
            result.addAll(parseDiceMods(restOfInput));
        }
        return result;
    }

    public static String getDiceModificatorDescription(DiceModificatorType modType) {
        return availableModifiersTags.entrySet().stream().filter(x -> x.getValue().equals(modType)).findFirst().get().getKey();
    }
}
