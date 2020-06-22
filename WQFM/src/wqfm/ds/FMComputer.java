package wqfm.ds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private List<Integer> bipartition_logical_list_per_pass;
    private List<Boolean> lockedTaxaBooleanList; //true: LOCKED, false: FREE

    private final Map<String, Integer> mapOfInitialBipartition;

    public FMComputer(CustomDS cDS, List<String> list, List<Pair<Integer, Integer>> qrts, List<Integer> initial_bip) {
//        this.taxa_list = new ArrayList<>(list); //Copy OR direct assignment ?
        this.taxa_list = list;
        this.quartets_list_indices = qrts;
        this.customDS = cDS;
        this.initial_bipartition_logical_list = initial_bip;
        //Initially all the taxa will be FREE
        this.lockedTaxaBooleanList = new ArrayList<>(Collections.nCopies(this.taxa_list.size(), false));
        this.bipartition_logical_list_per_pass = new ArrayList<>(this.initial_bipartition_logical_list);
        this.mapOfInitialBipartition = new HashMap<>();

        for (int i = 0; i < this.taxa_list.size(); i++) {
            this.mapOfInitialBipartition.put(this.taxa_list.get(i), this.initial_bipartition_logical_list.get(i));
        }
        System.out.println(this.mapOfInitialBipartition);
    }

    private boolean isSingletonBipartition(List<Integer> logical_bipartition) { //true if this bipartition is a singleton bipartition
        int len = logical_bipartition.size();
        int sum = Helper.sumList(logical_bipartition);

        return Math.abs(sum)== (len - 2); //eg. -1,+1, +1,+1,+1,+1  --> so, two terms will lead to 0, rest sum will be length - 2
    }

    private int checkQuartetStatusBeforeAndAfter(int[] left_sisters_bip, int[] right_sisters_bip) {
        int[] four_bipartitions = {left_sisters_bip[0], left_sisters_bip[1], right_sisters_bip[0], right_sisters_bip[1]};

        int sum_four_bipartitions = Helper.sumArray(four_bipartitions);
        //Blank check: Easier to check if blank quartet (all four are same) [priority wise first]
//        if ((left_sisters_bip[0] == left_sisters_bip[1]) && (right_sisters_bip[0] == right_sisters_bip[1]) && (left_sisters_bip[0] == right_sisters_bip[0])) {

        if (Math.abs(sum_four_bipartitions) == 4) { // -1,-1,-1,-1 or +1,+1,+1,+1 all will lead to sum == 4
            return Status.BLANK;
        }
        //Deferred Check: sum == 2 check [otherwise, permutations will be huge]
        if (Math.abs(sum_four_bipartitions) == 2) { //-1,+1 ,+1,+1  => +2 or +1,-1 , -1,-1 => -2 
            return Status.DEFERRED;
        }
        //Satisfied check: left are equal, right are equal AND left(any one) != right(any one)
        if ((left_sisters_bip[0] == left_sisters_bip[1]) && (right_sisters_bip[0] == right_sisters_bip[1]) && (left_sisters_bip[0] != right_sisters_bip[0])) {
            return Status.SATISFIED;
        }
        //All check fails, Violated quartet
        return Status.VIOLATED;
    }

    public void run_FM_single_pass() {
        //per pass or step [per num taxa of steps].
        //Test hypothetically ...
        double max_hypothetical_gain_of_this_pass = Integer.MIN_VALUE;
        String taxa_with_max_hypothetical_gain = "NONE_CHECK_NONE";

        for (int free_taxa_iter = 0; free_taxa_iter < this.lockedTaxaBooleanList.size(); free_taxa_iter++) {
            if (this.lockedTaxaBooleanList.get(free_taxa_iter) == false) { // this is a free taxon, hypothetically test it ....
                //First check IF moving this will lead to a singleton bipartition ....
                String taxaToConsider = this.taxa_list.get(free_taxa_iter);
                int currentPartitionSide = this.initial_bipartition_logical_list.get(free_taxa_iter);
                this.bipartition_logical_list_per_pass.set(free_taxa_iter, Helper.getOppositePartition(currentPartitionSide));  //reverse the bipartition
                /*System.out.println("For Taxa i = " + i + " , name = " + this.taxa_list.get(i));
                System.out.println("String taxa = " + this.taxa_list);
                System.out.println("Before bipartition = " + this.initial_bipartition_logical_list);
                System.out.println("After bipartition = " + this.bipartition_logic_list_per_pass + "\n");*/
                //Calculate hypothetical Gain ... [using discussed short-cut]

                List<Pair<Integer, Integer>> relevantQuartetsBeforeMoving = customDS.map_taxa_relevant_quartet_indices.get(taxaToConsider);
                for (int quartets_itr = 0; quartets_itr < relevantQuartetsBeforeMoving.size(); quartets_itr++) {
                    Pair<Integer, Integer> pair = relevantQuartetsBeforeMoving.get(quartets_itr);
                    Quartet quartet = customDS.table1_quartets_double_list.get(pair.getKey()).get(pair.getValue());

                    int[] left_sisters_before_bipartition = {this.mapOfInitialBipartition.get(quartet.taxa_sisters_left[0]), this.mapOfInitialBipartition.get(quartet.taxa_sisters_left[1])};
                    int[] right_sisters_before_bipartition = {this.mapOfInitialBipartition.get(quartet.taxa_sisters_right[0]), this.mapOfInitialBipartition.get(quartet.taxa_sisters_right[1])};

//                    System.out.println("Taxa_to_consider = " + taxaToConsider + " , Qrt = " + quartet.toString());
                    int status_quartet_before_hypothetical_swap = checkQuartetStatusBeforeAndAfter(left_sisters_before_bipartition, right_sisters_before_bipartition);
                    System.out.println("++>> AFTERWARDS, TAXA-Cons = " + taxaToConsider + " , Qrt = " + quartet.toString() + " , Status = " + Status.PRINT_STATUS_QUARTET(status_quartet_before_hypothetical_swap));
//                    System.out.println("");
                }
                System.out.println("");

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
