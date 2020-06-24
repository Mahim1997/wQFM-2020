package wqfm.ds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author mahim
 */
public class CustomDSPerLevel {

    public InitialTable table1_initial_table_of_quartets; //immutable [doesn't change, only as reference, is passed]

    //Will mutate per level
    public List<Integer> quartet_indices_list_unsorted;
    public Map<String, List<Integer>> map_taxa_relevant_quartet_indices; //releveant quartets map, key: taxa & val:list<indices>
    public Map<Double, List<Integer>> sorted_quartets_weight_list_indices_map;
    public final Set<String> set_taxa_string;

    public CustomDSPerLevel(InitialTable initialTable) {
        this.table1_initial_table_of_quartets = initialTable;
        this.quartet_indices_list_unsorted = new ArrayList<>();
        this.map_taxa_relevant_quartet_indices = new HashMap<>();
        this.sorted_quartets_weight_list_indices_map = new TreeMap<>(Collections.reverseOrder());
        this.set_taxa_string = new HashSet<>();
    }

    private void printTable1() {
        System.out.println("----------- Table1 [SINGLE list of quartets indices] ------------------");
        for (int i = 0; i < quartet_indices_list_unsorted.size(); i++) {
            System.out.println(this.quartet_indices_list_unsorted.get(i).toString());
        }
    }

    private void printMap_RelevantQuartetsIndicesPerTaxa() {
        System.out.println("----------- Printing Map <Taxa,RelevantQrtIndex> ---------");
        for (String key_taxa : map_taxa_relevant_quartet_indices.keySet()) {
            List<Integer> list_relevant_qrts_indices = map_taxa_relevant_quartet_indices.get(key_taxa);
            System.out.print("Taxa:<" + key_taxa + ">: Length: " + list_relevant_qrts_indices.size() + "  ==>> ");
            for (int i = 0; i < list_relevant_qrts_indices.size(); i++) {
                System.out.print(list_relevant_qrts_indices.get(i) + ",");
            }
            System.out.println("");
        }
    }

    private void printMap_RelevantQuartetsPeraTaxa() {
        System.out.println("----------- Printing Map <Taxa,RelevantQrts> ---------");
        for (String key_taxa : map_taxa_relevant_quartet_indices.keySet()) {
            List<Integer> list_relevant_qrts_indices = map_taxa_relevant_quartet_indices.get(key_taxa);
            System.out.print("Taxa:<" + key_taxa + ">: Length: " + list_relevant_qrts_indices.size() + "  ==>> ");
            for (int i = 0; i < list_relevant_qrts_indices.size(); i++) {
                System.out.print(this.table1_initial_table_of_quartets.get(list_relevant_qrts_indices.get(i)) + ",");
            }
            System.out.println("");
        }

    }

    public void printCustomDS() {
        this.table1_initial_table_of_quartets.printQuartetList();
        printMap_RelevantQuartetsIndicesPerTaxa();
        System.out.println(this.sorted_quartets_weight_list_indices_map);
    }

    public void sortQuartetIndicesMap() {
        for (int i = 0; i < this.quartet_indices_list_unsorted.size(); i++) {
            int qrt_index = this.quartet_indices_list_unsorted.get(i);
            Quartet q = this.table1_initial_table_of_quartets.get(qrt_index);
            if (this.sorted_quartets_weight_list_indices_map.containsKey(q.weight) == false) { //initialize the list [this weight doesn't exist]
                this.sorted_quartets_weight_list_indices_map.put(q.weight, new ArrayList<>());
            }
            this.sorted_quartets_weight_list_indices_map.get(q.weight).add(qrt_index); //append to list in treeMap
        }
    }

    public void fillRelevantQuartetsMap() {
        //For each quartet
        for (int itr_quartet = 0; itr_quartet < this.quartet_indices_list_unsorted.size(); itr_quartet++) {
            int index_qrt = this.quartet_indices_list_unsorted.get(itr_quartet);
            Quartet q = this.table1_initial_table_of_quartets.get(index_qrt);
            for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) { // Do for left-sisters ... push to map THIS quartet's row,col
                String taxon = q.taxa_sisters_left[i];
                if (this.map_taxa_relevant_quartet_indices.containsKey(taxon) == false) { //map doesn't have an entry yet for THIS taxon
                    this.map_taxa_relevant_quartet_indices.put(taxon, new ArrayList<>()); // initialize for THIS taxon
                }
                this.map_taxa_relevant_quartet_indices.get(taxon).add(itr_quartet);
            }
            for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) { // Repeat the same for right-sisters
                String taxon = q.taxa_sisters_right[i];
                if (this.map_taxa_relevant_quartet_indices.containsKey(taxon) == false) { //map doesn't have an entry yet for THIS taxon
                    this.map_taxa_relevant_quartet_indices.put(taxon, new ArrayList<>()); // initialize for THIS taxon
                }
                this.map_taxa_relevant_quartet_indices.get(taxon).add(itr_quartet);
            }
        }
    }

    public void fillUpTaxaList() {
        this.map_taxa_relevant_quartet_indices.keySet().forEach((tax) -> {
            this.set_taxa_string.add(tax);
        });
    }

    public String onlyQuartetIndices() {
        String s = "";
        s = this.quartet_indices_list_unsorted.stream().map((qrtIndex) -> (String.valueOf(qrtIndex) + ", ")).reduce(s, String::concat);
        return s;
    }

}
