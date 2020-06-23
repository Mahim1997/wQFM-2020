package wqfm.ds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.util.Pair;
import wqfm.Status;
import wqfm.bip.AggValuesBothBipartitionPerTaxa;
import wqfm.bip.Bipartition_8_values;

/**
 *
 * @author mahim
 */
public class FMComputer {

    private final Bipartition_8_values initialBipartition_8_values;
    private final List<Integer> initial_bipartition_logical_list;
    public List<String> taxa_list;
    public List<Pair<Integer, Integer>> quartets_list_indices;
    private final CustomDS customDS;

//    private List<Integer> bipartition_logical_list_per_pass;
    private List<Boolean> lockedTaxaBooleanList; //true: LOCKED, false: FREE
    private final Map<String, Integer> mapInitialBip;

    //Updated on 23 June, 2020 (Mahim)
    private Map<Double, List<String>> map_hypo_gain_list_taxa; // Map of hypothetical gain vs list of taxa
    private Map<String, AggValuesBothBipartitionPerTaxa> mapAggValsPerTaxaBeforeAndAfterBip;
    
    public FMComputer(CustomDS cDS, List<String> list, List<Pair<Integer, Integer>> qrts, 
            List<Integer> initial_bip, Bipartition_8_values initialBip_8_vals) {
//        this.taxa_list = new ArrayList<>(list); //Copy OR direct assignment ?
        this.taxa_list = list;
        this.quartets_list_indices = qrts;
        this.customDS = cDS;
        this.initial_bipartition_logical_list = initial_bip;
        //Initially all the taxa will be FREE
        this.lockedTaxaBooleanList = new ArrayList<>(Collections.nCopies(this.taxa_list.size(), false));
//        this.bipartition_logical_list_per_pass = new ArrayList<>(this.initial_bipartition_logical_list);
        this.mapInitialBip = new HashMap<>();

        for (int i = 0; i < this.taxa_list.size(); i++) {
            this.mapInitialBip.put(this.taxa_list.get(i), this.initial_bipartition_logical_list.get(i));
        }
//        System.out.println(this.mapOfInitialBipartition);
        this.map_hypo_gain_list_taxa = new TreeMap<>(Collections.reverseOrder());
        this.mapAggValsPerTaxaBeforeAndAfterBip = new HashMap<>();
        this.initialBipartition_8_values = initialBip_8_vals;
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
//                int currentPartitionSide = this.initial_bipartition_logical_list.get(free_taxa_iter);
                
                /*System.out.println("For Taxa i = " + i + " , name = " + this.taxa_list.get(i));
                System.out.println("String taxa = " + this.taxa_list);
                System.out.println("Before bipartition = " + this.initial_bipartition_logical_list);
                System.out.println("After bipartition = " + this.bipartition_logic_list_per_pass + "\n");*/

                //Calculate hypothetical Gain ... [using discussed short-cut]
                List<Pair<Integer, Integer>> relevantQuartetsBeforeMoving = customDS.map_taxa_relevant_quartet_indices.get(taxaToConsider);
                for (int quartets_itr = 0; quartets_itr < relevantQuartetsBeforeMoving.size(); quartets_itr++) {
                    Pair<Integer, Integer> pair = relevantQuartetsBeforeMoving.get(quartets_itr);
                    Quartet quartet = customDS.table1_quartets_double_list.get(pair.getKey()).get(pair.getValue());

                    //int[] left_sisters_before_bipartition = {this.mapInitialBip.get(quartet.taxa_sisters_left[0]), this.mapInitialBip.get(quartet.taxa_sisters_left[1])};
                    //int[] right_sisters_before_bipartition = {this.mapInitialBip.get(quartet.taxa_sisters_right[0]), this.mapInitialBip.get(quartet.taxa_sisters_right[1])};

                    int status_quartet_before_hyp_swap = Utils.findQuartetStatus(this.mapInitialBip.get(quartet.taxa_sisters_left[0]), this.mapInitialBip.get(quartet.taxa_sisters_left[1]), 
                            this.mapInitialBip.get(quartet.taxa_sisters_right[0]), this.mapInitialBip.get(quartet.taxa_sisters_right[1]));
//                    System.out.println("Before hypo swap, tax considered = " + taxaToConsider + " , Qrt = " + quartet.toString() + " , Status = " + Status.PRINT_STATUS_QUARTET(status_quartet_before_hyp_swap));
                }

                
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
