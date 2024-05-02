package Evaluator;

import Tokenizer.DiceToken;
import Tokenizer.OperandToken;

import java.util.Comparator;
import java.util.List;

public class DiceEvaluator {

    public static DiceEvalutationResult evaluateDice(DiceToken diceToken, OperandToken leftOperand, OperandToken rightOperand){
        return evaluateDice(diceToken, leftOperand == null ? 1 : (int) leftOperand.getNumber(), rightOperand == null ? 1 : (int) rightOperand.getNumber(), diceToken.modificators);
    }

    private static DiceEvalutationResult evaluateDice(
            DiceToken diceToken,
            int diceToRoll,
            int edges,
            List<DiceToken.DiceModificator> modificators){
        var rollResult = diceToken.rollDice(diceToRoll, edges);

        var orderedModificators = modificators.stream().sorted((x, y) -> y.getPriority() - x.getPriority()).toList();

        for (var mod : orderedModificators){
            switch (mod.getType()){
                case KeepHigh -> {
                    var diceRolledOrdered = rollResult.diceRolled.getPrimaryGroup().stream().sorted(Comparator.reverseOrder()).toList();
                    rollResult.sum = diceRolledOrdered.subList(0, mod.param == null ? 1 : mod.param).stream().mapToInt(a -> a).sum();
                }
                case KeepLow -> {
                    var diceRolledOrdered = rollResult.diceRolled.getPrimaryGroup().stream().sorted().toList();
                    rollResult.sum = diceRolledOrdered.subList(0, mod.param == null ? 1 : mod.param).stream().mapToInt(a -> a).sum();
                }
                case RerollKeepHigh -> {
                    var count = 1;
                    while (count < (mod.param == null ? 2 : mod.param)){
                        count++;
                        var newRoll = evaluateDice(diceToken, diceToRoll, edges, modificators.stream().filter(x -> x.getPriority() > mod.getPriority()).toList());
                        rollResult.diceRolled.addGroup(newRoll.diceRolled);
                        if (newRoll.sum > rollResult.sum){
                            rollResult.sum = newRoll.sum;
                            rollResult.diceRolled.primaryGroupIndex = rollResult.diceRolled.size() - 1;
                        }
                    }
                }
                case RerollKeepLow -> {
                    var count = 1;
                    while (count < (mod.param == null ? 2 : mod.param)){
                        count++;
                        var newRoll = evaluateDice(diceToken, diceToRoll, edges, modificators.stream().filter(x -> x.getPriority() > mod.getPriority()).toList());
                        rollResult.diceRolled.addGroup(newRoll.diceRolled);
                        if (newRoll.sum < rollResult.sum){
                            rollResult.sum = newRoll.sum;
                            rollResult.diceRolled.primaryGroupIndex = rollResult.diceRolled.size() - 1;
                        }
                    }
                }
                case MoreThan -> rollResult.sum = (int) rollResult.diceRolled.getPrimaryGroup().stream().filter(x -> x >= (mod.param == null ? 1 : mod.param)).count();
                case LessThan -> rollResult.sum = (int) rollResult.diceRolled.getPrimaryGroup().stream().filter(x -> x < (mod.param == null ? 1 : mod.param)).count();
                case Explosive -> {
                    if (edges <= 1) {
                        break;
                    }
                    var dicesToExplode = (int) rollResult.diceRolled.getPrimaryGroup().stream().filter(x -> x == edges).count();
                    while (dicesToExplode > 0){
                        var newRoll = diceToken.rollDice(dicesToExplode, edges);
                        dicesToExplode = (int) newRoll.diceRolled.getPrimaryGroup().stream().filter(x -> x == edges).count();
                        rollResult.diceRolled.getPrimaryGroup().addAll(newRoll.diceRolled.getPrimaryGroup());
                        rollResult.sum += newRoll.sum;
                    }
                }
            }

            rollResult.expression += mod.getIsLeftOriented()
                    ? " " + (mod.param == null ? "" : mod.param) + DiceToken.getDiceModificatorDescription(mod.getType())
                    : DiceToken.getDiceModificatorDescription(mod.getType()) + (mod.param == null ? "" : mod.param);
        }

        return rollResult;
    }
}

