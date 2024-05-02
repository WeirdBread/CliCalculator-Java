package Evaluator;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DiceResultList extends ArrayList<ArrayList<Integer>> {
    public DiceResultList(){
        this.add(new ArrayList<Integer>());
        this.primaryGroupIndex = 0;
    }

    public int primaryGroupIndex;

    public ArrayList<Integer> getPrimaryGroup() {
        return this.get(this.primaryGroupIndex);
    }

    public ArrayList<Integer> getLastGroup(){
        return this.get(this.size() - 1);
    }

    public ArrayList<Integer> addGroup(DiceResultList diceResult){
        this.addAll(diceResult);
        return this.getLastGroup();
    }

    public void add(int item){
        this.getLastGroup().add(item);
    }

    @Override
    public String toString(){
        StringBuilder diceRolledString = new StringBuilder();
        for (ArrayList<Integer> item : this){
            diceRolledString.append(String.format("[%s]", String.join(", ", item.stream().map(Object::toString).collect(Collectors.toList()))));
        }
        return diceRolledString.toString();
    }
}
