package tokenizer;

import evaluator.DiceEvaluationResult;
import evaluator.IDiceEvaluator;
import evaluator.IEvaluationLogger;
import evaluator.IEvaluator;

import java.util.*;

public class CommonDiceToken implements IOperator{
    public  static final String[] availableModifiersTags = { "rkh", "rkl", "kh", "kl", "!", "<", ">" };

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

    public void doOperation(IEvaluator evaluator) {
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
        DiceModificator newModificator = null;
        switch (modificator){
            case "kh": newModificator = new DiceModificator(DiceModificatorType.KeepHigh, param);
            break;
            case "kl": newModificator = new DiceModificator(DiceModificatorType.KeepLow, param);
            break;
            case "rkh": newModificator = new DiceModificator(DiceModificatorType.RerollKeepHigh, param);
            break;
            case "rkl": newModificator = new DiceModificator(DiceModificatorType.RerollKeepLow, param);
            break;
            case "!": newModificator = new DiceModificator(DiceModificatorType.Explosive, param);
            break;
            case "<": newModificator = new DiceModificator(DiceModificatorType.LessThan, param);
            break;
            case ">": newModificator = new DiceModificator(DiceModificatorType.MoreThan, param);
            break;
        }

        if (newModificator != null) {
            DiceModificator finalNewModificator = newModificator;
            if (this.modificators.stream().anyMatch(x -> x.getPriority() == finalNewModificator.getPriority())){
                return null;
            }
            this.modificators.add(newModificator);
        }
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
        MoreThan
    }

    public static String getDiceModificatorDescription(DiceModificatorType type) {
        switch (type) {
            case KeepHigh: return "kh";
            case KeepLow: return "kl";
            case LessThan: return "<";
            case MoreThan: return ">";
            case Explosive: return "!";
            case RerollKeepLow: return "rkl";
            case RerollKeepHigh: return "rkh";
            default: return null;
        }
    }

    public static ArrayList<String> parseDiceMods(String input){
        ArrayList<String> result = new ArrayList<>();
        Optional<String> mod = Arrays.stream(CommonDiceToken.availableModifiersTags).filter(input::contains).findFirst();
        if (!mod.isPresent()){
            return result;
        }
        result.add(mod.get());
        String restOfInput = input.substring(mod.get().length());
        if (!restOfInput.isEmpty()){
            result.addAll(parseDiceMods(restOfInput));
        }
        return result;
    }
}
