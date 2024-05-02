package Tokenizer;

import Evaluator.DiceEvalutationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiceToken implements IOperator{
    public  static final String[] availableModifiersTags = { "rkh", "rkl", "kh", "kl", "!", "<", ">" };

    public DiceToken(boolean isSingleDie, boolean hasStaticEdges) {
        this.modificators = new ArrayList<DiceModificator>();
        this._isSingleDie = isSingleDie;
        this._hasStaticEdges = hasStaticEdges;
    }

    private final boolean _isSingleDie;
    public boolean getIsSingleDie(){
        return  this._isSingleDie;
    }

    private final boolean _hasStaticEdges;
    public boolean getHasStaticEdges() {
        return this._hasStaticEdges;
    }

    public List<DiceModificator> modificators;

    public Enums.TokenType getTokenType(){
        return Enums.TokenType.Dice;
    }

    public String getSymbol(){
        return "d";
    }

    public int getPriority(){
        return 4;
    }

    @Override
    public String toString(){
        return this.getSymbol();
    }

    public DiceEvalutationResult rollDice(int diceToRoll, int edges){
        Random rnd = new Random();
        DiceEvalutationResult result = new DiceEvalutationResult();
        for (int i = 0; i < diceToRoll; i++){
            int rollResult = rnd.nextInt(edges) + 1;
            result.diceRolled.add(rollResult);
            result.sum += rollResult;
        }
        result.expression = diceToRoll + "d" + edges;
        return result;
    }

    public DiceModificator addModificator(String modificator){
        return this.addModificator(modificator, null);
    }

    public DiceModificator addModificator(String modificator, Integer param){
        DiceModificator newModificator = null;
        switch (modificator){
            case "kh": newModificator = new DiceModificator(DiceModificatorType.KeepHigh, param, false);
            break;
            case "kl": newModificator = new DiceModificator(DiceModificatorType.KeepLow, param, false);
            break;
            case "rkh": newModificator = new DiceModificator(DiceModificatorType.RerollKeepHigh, param, true);
            break;
            case "rkl": newModificator = new DiceModificator(DiceModificatorType.RerollKeepLow, param, true);
            break;
            case "!": newModificator = new DiceModificator(DiceModificatorType.Explosive, param, true);
            break;
            case "<": newModificator = new DiceModificator(DiceModificatorType.LessThan, param, false);
            break;
            case ">": newModificator = new DiceModificator(DiceModificatorType.MoreThan, param, false);
            break;
        };

        if (newModificator != null) {
            DiceModificator finalNewModificator = newModificator;
            if (this.modificators.stream().anyMatch(x -> x.getPriority() == finalNewModificator.getPriority())){
                return null;
            }
            this.modificators.add(newModificator);
        }
        return newModificator;
    }

    public class DiceModificator{
        public DiceModificator(DiceModificatorType type, Integer param, Boolean isLeftOriented){
            this._type = type;
            this.param = param;
            this._priority = calcPriority(type);
            this._isLeftOriented = isLeftOriented;
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

        private final boolean _isLeftOriented;
        public boolean getIsLeftOriented(){
            return this._isLeftOriented;
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
        None,
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
}
