package wqfm.algo;

import wqfm.ds.StatsPerPass;
import wqfm.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javafx.util.Pair;
import wqfm.Status;
import wqfm.bip.Bipartition_8_values;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.FMResultObject;
import wqfm.ds.Quartet;
import wqfm.main.Main;
import wqfm.utils.Helper;
import wqfm.utils.WeightedPartitionScores;

/**
 *
 * @author mahim
 */
public class FMComputer {

    public int level;
    private Bipartition_8_values initialBipartition_8_values;
    private final CustomDSPerLevel customDS;
    private Map<String, Integer> bipartitionMap;
    private final Map<String, Boolean> lockedTaxaBooleanMap; //true: LOCKED, false:FREE
    private Map<Double, List<String>> mapCandidateGainsPerListTax; // Map of hypothetical gain vs list of taxa
    private Map<String, Bipartition_8_values> mapCandidateTax_vs_8vals; //after hypothetical swap [i.e. IF this is taken as snapshot, no need to recalculate]
    private final List<StatsPerPass> listOfPerPassStatistics;
    private final Set<String> taxa_set;

    public FMComputer(CustomDSPerLevel customDS,
            Map<String, Integer> mapInitialBipartition,
            Bipartition_8_values initialBip_8_vals, int level) {
        this.level = level;
        this.customDS = customDS;
        //Initially all the taxa will be FREE
        this.bipartitionMap = mapInitialBipartition;
        this.initialBipartition_8_values = initialBip_8_vals;
        //for one-box/one-pass
        this.mapCandidateGainsPerListTax = new TreeMap<>(Collections.reverseOrder());
        this.mapCandidateTax_vs_8vals = new HashMap<>();

        //for one-iteration/all boxes
        this.listOfPerPassStatistics = new ArrayList<>();
        //initialise the lockMap
        this.lockedTaxaBooleanMap = new HashMap<>();
        //obtain set of taxa
        this.taxa_set = customDS.set_taxa_string;
        for (String tax : this.taxa_set) {
            this.lockedTaxaBooleanMap.put(tax, Boolean.FALSE);
        }
    }

    public void run_FM_singlepass_hypothetical_swap() {//per pass or step [per num taxa of steps].
        //Test hypothetically ...
        for(String taxToConsider: this.taxa_set){
            if (this.lockedTaxaBooleanMap.get(taxToConsider) == false) { // this is a free taxon, hypothetically test it ....
                int taxPartValBeforeHypoSwap = this.bipartitionMap.get(taxToConsider);
                //First check IF moving this will lead to a singleton bipartition ....
                Map<String, Integer> newMap = new HashMap<>(this.bipartitionMap);
                newMap.put(taxToConsider, Utils.getOppositePartition(taxPartValBeforeHypoSwap)); //hypothetically make the swap.
                if (Utils.isThisSingletonBipartition(newMap) == true) {
                    //THIS hypothetical movement of taxToConsider leads to singleton bipartition so, continue ...
                    continue;
                } //ELSE: DOESN'T lead to singleton bipartition [add to map, and other datastructures]
                //Calculate hypothetical Gain ... [using discussed short-cut]
                List<Integer> relevantQuartetsBeforeHypoMoving = customDS.map_taxa_relevant_quartet_indices.get(taxToConsider);
                Bipartition_8_values _8_vals_THIS_TAX_before_hypo_swap = new Bipartition_8_values(); // all initialized to 0
                Bipartition_8_values _8_vals_THIS_TAX_AFTER_hypo_swap = new Bipartition_8_values(); // all initialized to 0

                List<Integer> deferredQuartetsBeforeHypoMoving = new ArrayList<>(); //keep deferred quartets for later checking ...
                //For each quartet, find status, compute previous-hypothetical-swap-values, and using short-cuts (excluding deferred), compute after-hypothetical-swap-values

                for (int quartets_itr = 0; quartets_itr < relevantQuartetsBeforeHypoMoving.size(); quartets_itr++) {
                    int qrt_index_relevant_quartets = relevantQuartetsBeforeHypoMoving.get(quartets_itr);
                    //No need explicit checking as customDS will be changed after every level
                    Quartet quartet = customDS.table1_initial_table_of_quartets.get(qrt_index_relevant_quartets);
                    int status_quartet_before_hyp_swap = Utils.findQuartetStatus(bipartitionMap.get(quartet.taxa_sisters_left[0]),
                            bipartitionMap.get(quartet.taxa_sisters_left[1]), bipartitionMap.get(quartet.taxa_sisters_right[0]), bipartitionMap.get(quartet.taxa_sisters_right[1]));
                    //                    System.out.println("Before hypo swap, tax considered = " + taxToConsider + " , Qrt = " + quartet.toString() + " , Status = " + Status.PRINT_STATUS_QUARTET(status_quartet_before_hyp_swap));
                    int status_quartet_after_hyp_swap = Utils.findQuartetStatusUsingShortcut(status_quartet_before_hyp_swap); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
                    _8_vals_THIS_TAX_before_hypo_swap.addRespectiveValue(quartet.weight, status_quartet_before_hyp_swap); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
                    _8_vals_THIS_TAX_AFTER_hypo_swap.addRespectiveValue(quartet.weight, status_quartet_after_hyp_swap); //If status.UNKNOWN, then don't add anything.

                    if (status_quartet_before_hyp_swap == Status.DEFERRED) {
                        deferredQuartetsBeforeHypoMoving.add(qrt_index_relevant_quartets);
                    }

                } // end for [relevant-quartets-iteration]
                for (int itr_deferred_qrts = 0; itr_deferred_qrts < deferredQuartetsBeforeHypoMoving.size(); itr_deferred_qrts++) {
                    int qrt_idx_deferred_relevant_quartets_after_hypo_swap = deferredQuartetsBeforeHypoMoving.get(itr_deferred_qrts);
                    Quartet quartet = customDS.table1_initial_table_of_quartets.get(qrt_idx_deferred_relevant_quartets_after_hypo_swap);
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

    public void find_best_taxa_of_single_pass() {
        /*
        1.  Check if mapCandidateGainsPerListTax.size == 0 (any of the two maps) THEN all are singleton ... LOCK all taxaToMove
        2.  OTHERWISE, Use the two maps to find bestTaxaToMove [maxGain OR highestGain_with_max_num_satisfied_qrts]
        3.  LOCK the bestTaxaToMove and put corresponding stats in map
         */
        if (this.mapCandidateGainsPerListTax.isEmpty() == true) {
            //ALL LEAD TO SINGLETON BIPARTITION .... [LOCK ALL THE TAXA]
            for (String key : this.lockedTaxaBooleanMap.keySet()) {
                this.lockedTaxaBooleanMap.put(key, Boolean.TRUE);
            }
        }//do not add the prospective steps thing.
        else {

            Map.Entry<Double, List<String>> firstKeyEntry = this.mapCandidateGainsPerListTax.entrySet().iterator().next();
            double highest_gain_value = firstKeyEntry.getKey();
            List<String> list_taxaWithHighestGainValues = firstKeyEntry.getValue();
            String taxonWithTheHighestGainInThisPass;
            // exactly ONE taxon has the highest gain value... choose this
            if (list_taxaWithHighestGainValues.size() == 1) {
                //lock this taxon and put stats values for this taxon.
                taxonWithTheHighestGainInThisPass = list_taxaWithHighestGainValues.get(0);

            } else { // MORE than one taxon with same GAIN value .. select MAX count-satisfied-quartets one
                //create TreeMap<ns,tax> in descending order and take the first one.
                TreeMap<Integer, String> treeMap = new TreeMap<>(Collections.reverseOrder());
                for (int i = 0; i < list_taxaWithHighestGainValues.size(); i++) {
                    String taxChecking = list_taxaWithHighestGainValues.get(i);
                    treeMap.put(this.mapCandidateTax_vs_8vals.get(taxChecking).numSatisfied, taxChecking);
                }
                Map.Entry<Integer, String> highestNumSatTaxEntry = treeMap.entrySet().iterator().next();
                taxonWithTheHighestGainInThisPass = highestNumSatTaxEntry.getValue();
            }

            //lock and put stats values for this taxon in corresponding maps
            this.lockedTaxaBooleanMap.put(taxonWithTheHighestGainInThisPass, Boolean.TRUE);
            //create new map
            Map<String, Integer> mapAfterMovement = new HashMap<>(this.bipartitionMap);

            //reverse the bipartition for THIS taxon
            mapAfterMovement.put(taxonWithTheHighestGainInThisPass, Utils.getOppositePartition(mapAfterMovement.get(taxonWithTheHighestGainInThisPass)));

            StatsPerPass statsForThisPass = new StatsPerPass(taxonWithTheHighestGainInThisPass, highest_gain_value,
                    this.mapCandidateTax_vs_8vals.get(taxonWithTheHighestGainInThisPass), mapAfterMovement);

            this.listOfPerPassStatistics.add(statsForThisPass);
        }
    }

    public void changeParameterValuesForNextPass() {
        //Previous step's chosen-bipartition is THIS step's intiail-bipartition.
        this.bipartitionMap.clear();
        StatsPerPass previousPassStats = this.listOfPerPassStatistics.get(this.listOfPerPassStatistics.size() - 1);
        this.bipartitionMap = new HashMap<>(previousPassStats.map_final_bipartition); //NEED TO COPY here.
        //Previous step's chosen-8Values will be THIS step's chosen-8Values
        this.initialBipartition_8_values = new Bipartition_8_values(previousPassStats._8_values_chosen_for_this_pass);
        //Clear all the per-pass maps
        this.mapCandidateGainsPerListTax = new TreeMap<>(Collections.reverseOrder());
        this.mapCandidateTax_vs_8vals = new HashMap<>();
    }

    public void run_FM_single_iteration() {
        //per iteration ... has many passes. [will have rollback]
        int pass = 0;
        boolean areAllTaxaLocked = false;
        while (areAllTaxaLocked == false) {
            pass++; //for debug printing....

            run_FM_singlepass_hypothetical_swap(); //FM hypothetical single swap run
            find_best_taxa_of_single_pass(); //Find the best-taxon for THIS swap

            //Debug printing.
            StatsPerPass last_pass_stat = this.listOfPerPassStatistics.get(this.listOfPerPassStatistics.size() - 1);
            System.out.println("[Line 200]. FM-pass(box) = " + pass + " , best-taxon: " + last_pass_stat.whichTaxaWasPassed + " , MaxGain = "
                    + last_pass_stat.maxGainOfThisPass);

            changeParameterValuesForNextPass();//Change parameters to maintain consistency wrt next step/box/pass.
            areAllTaxaLocked = Helper.checkAllValuesIFSame(this.lockedTaxaBooleanMap, true); //if ALL are true, then stop.
        }

    }

    public boolean changeAndCheckAfterFMSingleIteration() {
//        if list size == 0 .... return initial bipartition and initial stats. //NEED TO CHECK.
        if (this.listOfPerPassStatistics.isEmpty()) {
            return false;
        }

        //iterate over statsPerPass list...
        double max_cumulative_gain_of_current_iteration = Integer.MIN_VALUE;
        double cumulative_gain = 0; //will keep on adding with max-gain from each pass.
        int pass_index_with_max_cumulative_gain = 0; // to store the MAX cumulative gain index

        for (int i = 0; i < this.listOfPerPassStatistics.size(); i++) {
            double currentPassMaxGain = this.listOfPerPassStatistics.get(i).maxGainOfThisPass;
            cumulative_gain += currentPassMaxGain;
            if (cumulative_gain > max_cumulative_gain_of_current_iteration) {
                max_cumulative_gain_of_current_iteration = cumulative_gain; //max_cumulative_gain stores the MAX CGain
                pass_index_with_max_cumulative_gain = i; //stores the pass ... i.e. THIS snapshot
            }
        }
        //Retrieve the stat's bipartition.
        StatsPerPass statOfMaxCumulativeGainBox = this.listOfPerPassStatistics.get(pass_index_with_max_cumulative_gain);

//        System.out.println("[L 236] Cumulative gain (max) = " + max_cumulative_gain_of_current_iteration
//                + " , for pass = " + (pass_index_with_max_cumulative_gain + 1)
//                + " , Tax = " + statOfMaxCumulativeGainBox.whichTaxaWasPassed);
//                + " map_final_bipartition = \n" + Helper.getGoodMap(statOfMaxCumulativeGainBox.map_final_bipartition));
        //Initial bipartitions and ALL maps //Now change parameters accordingly for next FM iteration.
        //only when max-cumulative-gain is GREATER than zero, we will change, otherwise return the initial bipartition of this iteration
        if (max_cumulative_gain_of_current_iteration > Main.SMALLEPSILON) {
            this.bipartitionMap = new HashMap<>(statOfMaxCumulativeGainBox.map_final_bipartition);
            this.initialBipartition_8_values = statOfMaxCumulativeGainBox._8_values_chosen_for_this_pass;
            this.listOfPerPassStatistics.clear();
            this.mapCandidateGainsPerListTax = new TreeMap<>(Collections.reverseOrder());
            this.mapCandidateTax_vs_8vals = new HashMap<>();
            for(String tax: this.taxa_set){
                this.lockedTaxaBooleanMap.put(tax, Boolean.FALSE);
            }
            return true;
        }
        //Set initial map to list's 1st item's map.
        return false;
    }

    //Whole FM ALGORITHm
    public FMResultObject run_FM_Algorithm_Whole() {
        Map<String, Integer> map_previous_iteration;//= new HashMap<>();
        boolean willIterateMore = true;
        int iterationsFM = 0; //can have stopping criterion for 10k iterations ?
        while (iterationsFM <= Main.MAX_ITERATIONS_LIMIT) { //stopping condition
            iterationsFM++;
            System.out.println("---------------- Iteration " + iterationsFM + " ----------------");
            map_previous_iteration = new HashMap<>(this.bipartitionMap); // always store this
            run_FM_single_iteration();
            willIterateMore = changeAndCheckAfterFMSingleIteration();
            if (willIterateMore == false) {
                this.bipartitionMap = map_previous_iteration; // just change as previous map
            }
            System.out.println("End of Iteration " + iterationsFM
                    + " new bipartition =>> \n"
                    + Helper.getGoodMap(bipartitionMap));
            System.out.println("================================================================");
            if (willIterateMore == false) {
                break;
            }
        }

        System.out.println("-->>Returning from one fm-iteration level = " + level);

        FMResultObject object = new FMResultObject(this.customDS, this.level); //pass the parent's customDS as reference
        object.createFMResultObjects(this.bipartitionMap); //pass THIS level's final-bipartition to get P_left,Q_left,P_right,Q_right
        return object;
    }

    //--------------------------------------- For debugging -------------------------------
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
}
