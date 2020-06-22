package wqfm.ds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.util.Pair;
import wqfm.Status;

/**
 *
 * @author mahim
 */
public class FMComputer {

    private final List<Integer> initial_bipartition_logical_list;
    public List<String> taxa_list;
    public List<Pair<Integer, Integer>> quartets_list_indices;
    private final CustomDS customDS;
    
    private List<Integer> bipartition_logic_list_per_pass;
    private List<Boolean> lockedTaxaBooleanList; //true: LOCKED, false: FREE

    public FMComputer(CustomDS cDS, List<String> list, List<Pair<Integer, Integer>> qrts, List<Integer> initial_bip) {
//        this.taxa_list = new ArrayList<>(list); //Copy OR direct assignment ?
        this.taxa_list = list;
        this.quartets_list_indices = qrts;
        this.customDS = cDS;
        this.initial_bipartition_logical_list = initial_bip;
        //Initially all the taxa will be FREE
        this.lockedTaxaBooleanList = new ArrayList<>(Collections.nCopies(this.taxa_list.size(), false));
        this.bipartition_logic_list_per_pass = new ArrayList<>(this.initial_bipartition_logical_list);
    }

    public void run_FM_single_pass() {
        //per pass or step [per num taxa of steps].
        //Test hypothetically ...
        double max_hypothetical_gain_of_this_pass = Integer.MIN_VALUE;
        String taxa_with_max_hypothetical_gain = "NONE_CHECK_NONE";
        
//        String 
        
        for(int i=0; i<this.lockedTaxaBooleanList.size(); i++){
            if(this.lockedTaxaBooleanList.get(i) == false){ // this is a free taxon, hypothetically test it ....
                //First check IF moving this will lead to a singleton bipartition ....
                String taxaToConsider = this.taxa_list.get(i);
                int currentPartitionSide = this.initial_bipartition_logical_list.get(i);
                this.bipartition_logic_list_per_pass.set(i, Helper.getOppositePartition(currentPartitionSide));  //reverse the bipartition
                /*System.out.println("For Taxa i = " + i + " , name = " + this.taxa_list.get(i));
                System.out.println("String taxa = " + this.taxa_list);
                System.out.println("Before bipartition = " + this.initial_bipartition_logical_list);
                System.out.println("After bipartition = " + this.bipartition_logic_list_per_pass + "\n");*/
                //Calculate hypothetical Gain ... [using discussed short-cut]
                
                List<Pair<Integer, Integer>> relevantQuartetsBeforeMoving = customDS.map_taxa_relevant_quartet_indices.get(taxaToConsider);
                for(int quartets_itr=0; quartets_itr<relevantQuartetsBeforeMoving.size(); quartets_itr++){
                    Pair<Integer, Integer> pair = relevantQuartetsBeforeMoving.get(i);
                    Quartet quartet = customDS.table1_quartets_double_list.get(pair.getKey()).get(pair.getValue());
                    
                }
                
//                if(Helper.isSingletonBipartition(this.bipartition_logic_list_per_pass)){ // Singleton TO DO
//                    
//                }
            }
        }
    }

    public void run_FM_single_iteration() {
        //per iteration ... has many passes.

    }

    //Whole FM ALGORITHm
    public FMResultObject run_FM_Algorithm_Whole() {
        //Constructor FMResultObject(List<Integer> logical_bipartition, List<String> taxa_list_initial, List<Pair<Integer, Integer>> quartets_list_initial)
        FMResultObject object = new FMResultObject(null, null, null);

//        System.out.println("-->>Inside FMComputer.run_FM_Algorithm_Whole()");
        System.out.println("TESTING runFMSinglePass()");
        run_FM_single_pass();
        
        return object;
    }
}
