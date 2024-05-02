package Evaluator;

import Tokenizer.DiceToken;
import Tokenizer.OperandToken;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DiceEvaluator {

    public static DiceEvalutationResult evaluateDice(DiceToken diceToken, OperandToken leftOperand, OperandToken rightOperand){
        return evaluateDice(diceToken, leftOperand == null ? 1 : (int) leftOperand.getNumber(), rightOperand == null ? 1 : (int) rightOperand.getNumber(), diceToken.modificators);
    }

    private static DiceEvalutationResult evaluateDice(
            DiceToken diceToken,
            int diceToRoll,
            int edges,
            List<DiceToken.DiceModificator> modificators){
        DiceEvalutationResult rollResult = diceToken.rollDice(diceToRoll, edges);

        List<DiceToken.DiceModificator> orderedModificators = modificators.stream().sorted((x, y) -> y.getPriority() - x.getPriority()).collect(Collectors.toList());

        for (DiceToken.DiceModificator mod : orderedModificators){
            switch (mod.getType()){
                case KeepHigh:
                    List<Integer> diceRolledOrdered = rollResult.diceRolled.getPrimaryGroup().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                    rollResult.sum = diceRolledOrdered.subList(0, mod.param == null ? 1 : mod.param).stream().mapToInt(a -> a).sum();
                break;
                case KeepLow:
                    diceRolledOrdered = rollResult.diceRolled.getPrimaryGroup().stream().sorted().collect(Collectors.toList());
                    rollResult.sum = diceRolledOrdered.subList(0, mod.param == null ? 1 : mod.param).stream().mapToInt(a -> a).sum();
                break;
                case RerollKeepHigh:
                    int count = 1;
                    while (count < (mod.param == null ? 2 : mod.param)){
                        count++;
                        DiceEvalutationResult newRoll = evaluateDice(diceToken, diceToRoll, edges, modificators.stream().filter(x -> x.getPriority() > mod.getPriority()).collect(Collectors.toList()));
                        rollResult.diceRolled.addGroup(newRoll.diceRolled);
                        if (newRoll.sum > rollResult.sum){
                            rollResult.sum = newRoll.sum;
                            rollResult.diceRolled.primaryGroupIndex = rollResult.diceRolled.size() - 1;
                        }
                    }
                break;
                case RerollKeepLow:
                    count = 1;
                    while (count < (mod.param == null ? 2 : mod.param)){
                        count++;
                        DiceEvalutationResult newRoll = evaluateDice(diceToken, diceToRoll, edges, modificators.stream().filter(x -> x.getPriority() > mod.getPriority()).collect(Collectors.toList()));
                        rollResult.diceRolled.addGroup(newRoll.diceRolled);
                        if (newRoll.sum < rollResult.sum){
                            rollResult.sum = newRoll.sum;
                            rollResult.diceRolled.primaryGroupIndex = rollResult.diceRolled.size() - 1;
                        }
                    }
                break;
                case MoreThan:
                    rollResult.sum = (int) rollResult.diceRolled.getPrimaryGroup().stream().filter(x -> x >= (mod.param == null ? 1 : mod.param)).count();
                break;
                case LessThan:
                    rollResult.sum = (int) rollResult.diceRolled.getPrimaryGroup().stream().filter(x -> x < (mod.param == null ? 1 : mod.param)).count();
                break;
                case Explosive:
                    if (edges <= 1) {
                        break;
                    }
                    int dicesToExplode = (int) rollResult.diceRolled.getPrimaryGroup().stream().filter(x -> x == edges).count();
                    while (dicesToExplode > 0){
                        DiceEvalutationResult newRoll = diceToken.rollDice(dicesToExplode, edges);
                        dicesToExplode = (int) newRoll.diceRolled.getPrimaryGroup().stream().filter(x -> x == edges).count();
                        rollResult.diceRolled.getPrimaryGroup().addAll(newRoll.diceRolled.getPrimaryGroup());
                        rollResult.sum += newRoll.sum;
                    }
                break;
            }

            rollResult.expression += mod.getIsLeftOriented()
                    ? " " + (mod.param == null ? "" : mod.param) + DiceToken.getDiceModificatorDescription(mod.getType())
                    : DiceToken.getDiceModificatorDescription(mod.getType()) + (mod.param == null ? "" : mod.param);
        }

        return rollResult;
    }
}

