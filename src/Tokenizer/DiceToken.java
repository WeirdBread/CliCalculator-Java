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
        var rnd = new Random();
        var result = new DiceEvalutationResult();
        for (var i = 0; i < diceToRoll; i++){
            var rollResult = rnd.nextInt(1, edges+1);
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
        var newModificator = switch (modificator){
            case "kh" -> new DiceModificator(DiceModificatorType.KeepHigh, param, false);
            case "kl" -> new DiceModificator(DiceModificatorType.KeepLow, param, false);
            case "rkh" -> new DiceModificator(DiceModificatorType.RerollKeepHigh, param, true);
            case "rkl" -> new DiceModificator(DiceModificatorType.RerollKeepLow, param, true);
            case "!" -> new DiceModificator(DiceModificatorType.Explosive, param, true);
            case "<" -> new DiceModificator(DiceModificatorType.LessThan, param, false);
            case ">" -> new DiceModificator(DiceModificatorType.MoreThan, param, false);
            default -> null;
        };

        if (newModificator != null) {
            if (this.modificators.stream().anyMatch(x -> x.getPriority() == newModificator.getPriority())){
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

        private static int calcPriority(DiceModificatorType type){
            return switch (type){
                case DiceModificatorType.MoreThan, DiceModificatorType.LessThan -> 0;
                case DiceModificatorType.KeepHigh, DiceModificatorType.KeepLow -> 1;
                case DiceModificatorType.RerollKeepHigh, DiceModificatorType.RerollKeepLow -> 2;
                case DiceModificatorType.Explosive -> 3;
                default -> -1;
            };
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
        return switch (type) {
            case KeepHigh -> "kh";
            case KeepLow -> "kl";
            case LessThan -> "<";
            case MoreThan -> ">";
            case Explosive -> "!";
            case RerollKeepLow -> "rkl";
            case RerollKeepHigh -> "rkh";
            default -> null;
        };
    }
}
