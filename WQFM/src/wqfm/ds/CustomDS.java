package wqfm.ds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import javafx.util.Pair;

/**
 *
 * @author mahim
 */

/*
Table 1: List< List<Quartet> > list_of_list_of_quartets;
Table 2: List<Pair<int,int>> for List of <weight_quartet,index_of_table_1> 
        //will only sort this table [table 2]

HashMap [Thread-UNSAFE] so, we can use Hashtable<String, List<Pair>> for taxa_relevant_quartets_indices
BUT FOR NOW, use HashMap [EXACTLY same method signatures for both data-structures]
//i.e. Hashtable/HashMap key: taxa, value: List of indices (row,col) of table 1


 */
public class CustomDS {

    public List<List<Quartet>> table1_quartets_double_list;
    public TreeMap<Double, Integer> table2_map_weight_indexQuartet;
    public HashMap<String, List<Pair<Integer, Integer>>> map_taxa_relevant_quartet_indices;
    public List<Pair<Integer, Integer>> table4_quartetes_indices_list;
    
    
//    public List<Pair<Double, Integer>> table2_weight_indexOfQrt; // Use map as above
    
    public CustomDS() {
        this.table1_quartets_double_list = new ArrayList<>();
//        this.table2_weight_indexOfQrt = new ArrayList<>();
        this.map_taxa_relevant_quartet_indices = new HashMap<>();
        this.table2_map_weight_indexQuartet = new TreeMap<>(Collections.reverseOrder());
        this.table4_quartetes_indices_list = new ArrayList<>();
    }

    private void printTable1() {
        System.out.println("----------- Table1 [Double list of quartets] ------------------");
        for (int i = 0; i < table1_quartets_double_list.size(); i++) {
            System.out.print("Row: " + i + ", size = " +  table1_quartets_double_list.get(i).size() + " ---> ");
            for (int j = 0; j < table1_quartets_double_list.get(i).size(); j++) {
                System.out.print(table1_quartets_double_list.get(i).get(j) + "  ");
            }
            System.out.println("");
        }
    }

    private void printTable2() {
        System.out.println("----------- Table 2 [Weight, Index_Table_1] pair ------------------");
        /*for (int i = 0; i < table2_weight_indexOfQrt.size(); i++) {
            Pair pair = table2_weight_indexOfQrt.get(i);
            System.out.println("i = " + i + " , weight = " + pair.getKey() + " , idx_table_1_ROW_IDX = " + pair.getValue());
        }*/
        for(Double key_weight: table2_map_weight_indexQuartet.keySet()){
            int val_rowIDX_tab2 = table2_map_weight_indexQuartet.get(key_weight);
            System.out.println("Key_WEIGHT: <" + key_weight + ">: Value_rowTable2_IDX: " + val_rowIDX_tab2);
        }
    }

    private void printMap_RelevantQuartetsPerTaxa() {
        System.out.println("----------- Printing Map <Taxa,RelevantQuartet_RowCol> ---------");
        for (String key_taxa : map_taxa_relevant_quartet_indices.keySet()) {
            List<Pair<Integer, Integer>> value_r_c_index_pair = map_taxa_relevant_quartet_indices.get(key_taxa);
            System.out.print("Taxa:<" + key_taxa + ">: Length: " + value_r_c_index_pair.size() + "  ==>> ");
            for (int i = 0; i < value_r_c_index_pair.size(); i++) {
                Pair pair = value_r_c_index_pair.get(i);
                System.out.print("(" + pair.getKey() + "," + pair.getValue() + "), ");
            }
            System.out.println("");
        }

    }

    public void printCustomDS() {
//        printTable1();
        printTable2();
//        printMap_RelevantQuartetsPerTaxa();
    }

    public static String getDummyTaxonName(int level){
        String dummyTax = "DUMMY_MZCR_" + String.valueOf(level); //arbitrary names so as to not get mixed up with actual names
        return dummyTax;
    }
    
}
