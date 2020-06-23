package wqfm.algo;

import wqfm.utils.Utils;
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
import wqfm.ds.CustomInitTables;
import wqfm.ds.FMResultObject;
import wqfm.ds.Quartet;
import wqfm.utils.Helper;
import wqfm.utils.WeightedPartitionScores;

/**
 *
 * @author mahim
 */
public class FMComputer {

    private final Bipartition_8_values initialBipartition_8_values;
//    private final List<Integer> initial_bipartition_logical_list; //USE MAP from 23 June, 2020: Tuesday
    public List<String> taxa_list;
    public List<Pair<Integer, Integer>> quartets_list_indices;
    private final CustomInitTables customDS;

//    private List<Integer> bipartition_logical_list_per_pass;
    private List<Boolean> lockedTaxaBooleanList; //true: LOCKED, false: FREE
    private final Map<String, Integer> mapInitialBip;

    //Updated on 23 June, 2020 (Mahim)
    private Map<Double, List<String>> mapCandidateGainsPerListTax; // Map of hypothetical gain vs list of taxa
    private Map<String, Bipartition_8_values> mapCandidateTax_vs_8vals; //after hypothetical swap [i.e. IF this is taken as snapshot, no need to recalculate]
    private Map<Integer, StatsPerPass> mapOfPerPassValues;

    public FMComputer(CustomInitTables customDS, List<String> list, List<Pair<Integer, Integer>> qrts,
            Map<String, Integer> mapInitialBipartition, Bipartition_8_values initialBip_8_vals) {
//        this.taxa_list = new ArrayList<>(list); //Copy OR direct assignment ?
        this.taxa_list = list;
        this.quartets_list_indices = qrts;
        this.customDS = customDS;
        this.mapInitialBip = mapInitialBipartition;
        //Initially all the taxa will be FREE
        this.lockedTaxaBooleanList = new ArrayList<>(Collections.nCopies(this.taxa_list.size(), false));
        this.mapCandidateGainsPerListTax = new TreeMap<>(Collections.reverseOrder());
        this.mapCandidateTax_vs_8vals = new HashMap<>();
        this.initialBipartition_8_values = initialBip_8_vals;
        this.mapOfPerPassValues = new HashMap<>();
    }

    public void run_FM_singlepass_hypothetical_swap() {
        //per pass or step [per num taxa of steps].
        //Test hypothetically ...
        for (int taxa_iter = 0; taxa_iter < this.taxa_list.size(); taxa_iter++) { // iterate over whole set of taxa

            if (this.lockedTaxaBooleanList.get(taxa_iter) == false) { // this is a free taxon, hypothetically test it ....
                String taxToConsider = this.taxa_list.get(taxa_iter); // WHICH taxa to consider for hypothetical move.
                int taxPartValBeforeHypoSwap = this.mapInitialBip.get(taxToConsider);
                //First check IF moving this will lead to a singleton bipartition ....
                Map<String, Integer> newMap = new HashMap<>(this.mapInitialBip);
                newMap.put(taxToConsider, Utils.getOppositePartition(taxPartValBeforeHypoSwap)); //hypothetically make the swap.
                if (Utils.isThisSingletonBipartition(newMap) == true) {
                    //THIS hypothetical movement of taxToConsider leads to singleton bipartition so, continue ...
                    continue;
                }
                //ELSE: DOESN'T lead to singleton bipartition [add to map, and other datastructures]
                //Calculate hypothetical Gain ... [using discussed short-cut]
                List<Pair<Integer, Integer>> relevantQuartetsBeforeHypoMoving = customDS.map_taxa_relevant_quartet_indices.get(taxToConsider);
//                Bipartition_8_values _8_vals_THIS_TAX_before_swap = Utils.obtain8ValsOfTaxonBeforeSwap(customDS, relevantQuartetsBeforeHypoMoving, 
//                        taxToConsider, this.mapInitialBip);
                Bipartition_8_values _8_vals_THIS_TAX_before_hypo_swap = new Bipartition_8_values(); // all initialized to 0
                Bipartition_8_values _8_vals_THIS_TAX_AFTER_hypo_swap = new Bipartition_8_values(); // all initialized to 0

                List<Pair<Integer, Integer>> deferredQuartetsBeforeHypoMoving = new ArrayList<>(); //keep deferred quartets for later checking ...
                for (int quartets_itr = 0; quartets_itr < relevantQuartetsBeforeHypoMoving.size(); quartets_itr++) {
                    Pair<Integer, Integer> pair = relevantQuartetsBeforeHypoMoving.get(quartets_itr);
                    Quartet quartet = customDS.table1_quartets_double_list.get(pair.getKey()).get(pair.getValue());
                    int status_quartet_before_hyp_swap = Utils.findQuartetStatus(mapInitialBip.get(quartet.taxa_sisters_left[0]),
                            mapInitialBip.get(quartet.taxa_sisters_left[1]), mapInitialBip.get(quartet.taxa_sisters_right[0]), mapInitialBip.get(quartet.taxa_sisters_right[1]));
//                    System.out.println("Before hypo swap, tax considered = " + taxToConsider + " , Qrt = " + quartet.toString() + " , Status = " + Status.PRINT_STATUS_QUARTET(status_quartet_before_hyp_swap));
                    int status_quartet_after_hyp_swap = Utils.findQuartetStatusUsingShortcut(status_quartet_before_hyp_swap);
                    _8_vals_THIS_TAX_before_hypo_swap.addRespectiveValue(quartet.weight, status_quartet_before_hyp_swap);
                    _8_vals_THIS_TAX_AFTER_hypo_swap.addRespectiveValue(quartet.weight, status_quartet_after_hyp_swap); //If status.UNKNOWN, then don't add anything.

                    if (status_quartet_before_hyp_swap == Status.DEFERRED) {
                        deferredQuartetsBeforeHypoMoving.add(pair);
                    }
                } // end for [relevant-quartets-iteration]
                for (int itr_deferred_qrts = 0; itr_deferred_qrts < deferredQuartetsBeforeHypoMoving.size(); itr_deferred_qrts++) {
                    Pair<Integer, Integer> pair = deferredQuartetsBeforeHypoMoving.get(itr_deferred_qrts);
                    Quartet quartet = customDS.table1_quartets_double_list.get(pair.getKey()).get(pair.getValue());
                    int status_after_hypothetical_swap = Utils.findQuartetStatus(newMap.get(quartet.taxa_sisters_left[0]),
                            newMap.get(quartet.taxa_sisters_left[1]), newMap.get(quartet.taxa_sisters_right[0]), newMap.get(quartet.taxa_sisters_right[1]));
                    _8_vals_THIS_TAX_AFTER_hypo_swap.addRespectiveValue(quartet.weight, status_after_hypothetical_swap);
                }
                double ps_before_reduced = WeightedPartitionScores.calculatePartitionScoreReduced(_8_vals_THIS_TAX_before_hypo_swap);
                double ps_after_reduced = WeightedPartitionScores.calculatePartitionScoreReduced(_8_vals_THIS_TAX_AFTER_hypo_swap);
                double gainOfThisTax = ps_after_reduced - ps_before_reduced; //correct calculation
                if (this.mapCandidateGainsPerListTax.containsKey(gainOfThisTax) == false) { // this gain was not contained
                    //initialize the taxon(for this gain-val) list.
                    this.mapCandidateGainsPerListTax.put(gainOfThisTax, new ArrayList<>());
                }//else: simply append to the list.
                this.mapCandidateGainsPerListTax.get(gainOfThisTax).add(taxToConsider); //add gain to map
                Bipartition_8_values _8_values_whole_considering_thisTax_swap = new Bipartition_8_values();
                /*AfterHypoSwap.Whole_8Vals - BeforeHypoSwap.Whole_8Vals = AfterHypoSwap.OneTax.8Vals - BeforeHypoSwap.OneTax.8vals //vector rules of distance addition*/
                //So, AfterHypoSwap.Whole_8Vals = BeforeHypoSwap.Whole_8Vals + AfterHypoSwap.OneTax.8Vals - BeforeHypoSwap.OneTax.8vals
                _8_values_whole_considering_thisTax_swap.addObject(this.initialBipartition_8_values);
                _8_values_whole_considering_thisTax_swap.addObject(_8_vals_THIS_TAX_AFTER_hypo_swap);
                _8_values_whole_considering_thisTax_swap.subtractObject(_8_vals_THIS_TAX_before_hypo_swap);
                this.mapCandidateTax_vs_8vals.put(taxToConsider, _8_values_whole_considering_thisTax_swap);

            } //end if
        }//end outer for

    }

    private void printTwoMaps() {
        for (String tax : this.mapCandidateTax_vs_8vals.keySet()) {
            Bipartition_8_values _8_vals = this.mapCandidateTax_vs_8vals.get(tax);
            System.out.println(tax + ": " + _8_vals.toString());
        }

        for (double gain : this.mapCandidateGainsPerListTax.keySet()) {
            List<String> list_tax_with_this_gain = this.mapCandidateGainsPerListTax.get(gain);
            for (String tax : list_tax_with_this_gain) {
                System.out.println("-->>Gain(" + tax + ") = " + gain);
            }
        }
    }

    public void find_best_taxa_of_single_pass(int pass_num) {
        /*
        1.  Check if mapCandidateGainsPerListTax.size == 0 (any of the two maps) THEN all are singleton ... LOCK all taxaToMove
        2.  OTHERWISE, Use the two maps to find bestTaxaToMove [maxGain OR highestGain_with_max_num_satisfied_qrts]
        3.  LOCK the bestTaxaToMove
         */
        if (this.mapCandidateGainsPerListTax.isEmpty() == true) {
            //ALL LEAD TO SINGLETON BIPARTITION .... [LOCK ALL THE TAXA]
            for (int i = 0; i < this.lockedTaxaBooleanList.size(); i++) {
                this.lockedTaxaBooleanList.set(i, Boolean.TRUE);
            }
        }//do not add the prospective steps thing.
        else {
            //Check if MULTIPLE taxa with same GAIN value. [guaranteed map.len > 1]
//            Map.Entry<Integer,String> entry = this..entrySet().iterator().next();
            Map.Entry<Double, List<String>> firstKeyEntry = this.mapCandidateGainsPerListTax.entrySet().iterator().next();
            double highest_gain_value = firstKeyEntry.getKey();
            List<String> taxaWithHighestGainValues = firstKeyEntry.getValue();
            if(taxaWithHighestGainValues.size() == 1){ // exactly ONE taxon has the highest gain value... choose this
                
                
                
            }
            
//        StatsPerPass statsOfThisPass = new StatsPerPass(whichTaxaWasPassed, pass_num, pass_num, list_bipartition_final)
//        this.mapOfPerPassValues.put(pass_num, statsOfThisPass);
        }
    }

    public void changeParameterValuesForNextPass() {

    }

    public void run_FM_single_iteration() {
        //per iteration ... has many passes. [will have rollback]
        int pass_num = 0;
        double max_hypothetical_gain_of_this_pass = Integer.MIN_VALUE;
        String taxa_with_max_hypothetical_gain = "NONE_CHECK_NONE";

        System.out.println("INSIDE run_FM_single_iteration() ... calling runFMSinglePass()");

        boolean areAllTaxaLocked = false;
        while (areAllTaxaLocked == false) {
            areAllTaxaLocked = Helper.checkAllValuesIFSame(this.lockedTaxaBooleanList, true); //if ALL are true, then stop.
            run_FM_singlepass_hypothetical_swap();
            find_best_taxa_of_single_pass(pass_num);
            changeParameterValuesForNextPass();
            pass_num++;
        }

    }

    public void find_best_pass_of_single_iteration() {

    }

    //Whole FM ALGORITHm
    public FMResultObject run_FM_Algorithm_Whole() {
        //Constructor FMResultObject(List<Integer> logical_bipartition, List<String> taxa_list_initial, List<Pair<Integer, Integer>> quartets_list_initial)
        FMResultObject object = new FMResultObject(null, null, null);

        int iterationsFM = 0;
        while (iterationsFM < 1) { //stopping condition
            run_FM_single_iteration();
            iterationsFM++;
        }

        return object;
    }

}
