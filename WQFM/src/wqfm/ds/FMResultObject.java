package wqfm.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import wqfm.Status;
import wqfm.utils.Utils;
/**
 *
 * @author mahim
 */
public class FMResultObject {

    /*Will contain
        GIVEN as INPUT
        1. logical_bipartition
        2. Initial List<String> taxa_list_initial [same length as above]
        3. List<row,col> of all-quartets 
                [NEED to iterate through all to find relevant deferred and blank quartets] to put to Q_left and Q_right]
    
        TO RETURN as OUTPUT
        1. Q_left  <row,col> list
        2. Q_right <row,col> list
        3. P_left  List<String>
        4. P_right List<String>
     */
    // Inputs
//    private Map<String, Integer> logical_bipartition_map;
//    private List<String> taxa_list_initial;
//    private List<Pair<Integer, Integer>> quartets_list_initial;
    //TO return as outputs
    public List<String> taxa_left_partition;
    public List<String> taxa_right_partition;
    public List<Pair<Integer, Integer>> quartets_list_indices_left_partition;
    public List<Pair<Integer, Integer>> quartets_list_indices_right_partition;
    private final int level;
    public CustomInitTables customDS_New;
    //dummy maps
    private final Map<String, Integer> map_left_taxa;
    private final Map<String, Integer> map_right_taxa;
    
    public FMResultObject(int level) {
        this.level = level;
        this.taxa_left_partition = new ArrayList<>();
        this.taxa_right_partition = new ArrayList<>();
        this.quartets_list_indices_left_partition = new ArrayList<>();
        this.quartets_list_indices_right_partition = new ArrayList<>();
        this.map_left_taxa = new HashMap<>();
        this.map_right_taxa = new HashMap<>();
    }

    public void createFMResultObject(CustomInitTables customDS,
            Map<String, Integer> mapOfBipartition,
            List<Pair<Integer, Integer>> quartetsListFromBefore,
            List<String> taxaListFromBefore) {

        //First partition taxa lists
        for (int i = 0; i < taxaListFromBefore.size(); i++) {
            String taxa = taxaListFromBefore.get(i);
            if (mapOfBipartition.get(taxa) == Status.LEFT_PARTITION) {
                this.taxa_left_partition.add(taxa);
                this.map_left_taxa.put(taxa, i); // dummy for next phase quick checking ....
            } else if (mapOfBipartition.get(taxa) == Status.RIGHT_PARTITION) {
                this.taxa_right_partition.add(taxa);
                this.map_right_taxa.put(taxa, i);
            }
        }

        //Create dummy node at this level.
        String dummyNode = Utils.getDummyTaxonName(level);
        System.out.println("--->In FMResultObject.. dummyNode = " + dummyNode);
        
        //Now partition quartet lists ... only send deferred quartets...
        for(int i=0; i<quartetsListFromBefore.size(); i++){
            Pair<Integer, Integer> pair = quartetsListFromBefore.get(i);
            Quartet q = customDS.table1_quartets_double_list.get(pair.getKey()).get(pair.getValue());
            
            //any three must be in EITHER leftMap or rightMap
            
        }
    
        
    }

}
