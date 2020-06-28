package wqfm.ds;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mahim
 */
//Only store the List<Quartets> initially. [will be modified by individual threads/classes/objects]
public class InitialTable { //STORED separately to have the synchronized add function

    private List<Quartet> list_quartets;

    public InitialTable(boolean flag) {
        // do not initialize. [to pass as reference]

    }

    public InitialTable() {
        this.list_quartets = new ArrayList<>();
    }

    public List<Quartet> get_QuartetList() {
        return list_quartets;
    }

    @Override
    public String toString() {
        return "InitialTable{" + "list_quartets=" + list_quartets + '}';
    }

    public Quartet get(int idx) {
        return list_quartets.get(idx);
    }

    public int sizeTable() {
        return list_quartets.size();
    }

    public void addToListOfQuartets(Quartet q) { //maybe make this method sync ? [NO NEED]
//        System.out.println("InitTable. Quartet is added." + q);
        this.list_quartets.add(q);
    }

    public void printQuartetList() {
        for (int i = 0; i < this.list_quartets.size(); i++) {
            System.out.println(i + ":-> " + this.list_quartets.get(i).toString());
        }
    }

    public void assignByReference(InitialTable initialTable) {
        this.list_quartets = initialTable.list_quartets; //assign be reference
    }

}
