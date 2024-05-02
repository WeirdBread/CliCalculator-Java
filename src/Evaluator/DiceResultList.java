package Evaluator;

import java.util.ArrayList;

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
        return this.getLast();
    }

    public ArrayList<Integer> addGroup(DiceResultList diceResult){
        this.addAll(diceResult);
        return this.getLastGroup();
    }

    public void add(int item){
        this.getLastGroup().add(item);
    }
}
