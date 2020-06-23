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

//    private List<Boolean> lockedTaxaBooleanList; //true: LOCKED, false: FREE
    private Map<String, Integer> initialBipartitionMap;
    private Map<String, Boolean> lockedTaxaBooleanMap; //true: LOCKED, false:FREE

    //Updated on 23 June, 2020 (Mahim)
    private Map<Double, List<String>> mapCandidateGainsPerListTax; // Map of hypothetical gain vs list of taxa
    private Map<String, Bipartition_8_values> mapCandidateTax_vs_8vals; //after hypothetical swap [i.e. IF this is taken as snapshot, no need to recalculate]

    private List<StatsPerPass> listOfPerPassStatistics;

    public FMComputer(CustomInitTables customDS, List<String> list, List<Pair<Integer, Integer>> qrts,
            Map<String, Integer> mapInitialBipartition, Bipartition_8_values initialBip_8_vals) {
//        this.taxa_list = new ArrayList<>(list); //Copy OR direct assignment ?
        this.taxa_list = list;
        this.quartets_list_indices = qrts;
        this.customDS = customDS;
        this.initialBipartitionMap = mapInitialBipartition;
        this.initialBipartition_8_values = initialBip_8_vals;
        //Initially all the taxa will be FREE

        //for one-box/one-pass
        this.mapCandidateGainsPerListTax = new TreeMap<>(Collections.reverseOrder());
        this.mapCandidateTax_vs_8vals = new HashMap<>();

        //for one-iteration/all boxes
        this.listOfPerPassStatistics = new ArrayList<>();

        //initialise the lockMap
        this.lockedTaxaBooleanMap = new HashMap<>();
        for (int i = 0; i < this.taxa_list.size(); i++) {
            this.lockedTaxaBooleanMap.put(this.taxa_list.get(i), Boolean.FALSE);
        }
    }

    public void run_FM_singlepass_hypothetical_swap() {
        //per pass or step [per num taxa of steps].
        //Test hypothetically ...
        for (int taxa_iter = 0; taxa_iter < this.taxa_list.size(); taxa_iter++) { // iterate over whole set of taxa
            String taxToConsider = this.taxa_list.get(taxa_iter); // WHICH taxa to consider for hypothetical move.

            if (this.lockedTaxaBooleanMap.get(taxToConsider) == false) { // this is a free taxon, hypothetically test it ....
                System.out.println("Considring taxa => " + taxToConsider);
                int taxPartValBeforeHypoSwap = this.initialBipartitionMap.get(taxToConsider);
                //First check IF moving this will lead to a singleton bipartition ....
                Map<String, Integer> newMap = new HashMap<>(this.initialBipartitionMap);
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
                    int status_quartet_before_hyp_swap = Utils.findQuartetStatus(initialBipartitionMap.get(quartet.taxa_sisters_left[0]),
                            initialBipartitionMap.get(quartet.taxa_sisters_left[1]), initialBipartitionMap.get(quartet.taxa_sisters_right[0]), initialBipartitionMap.get(quartet.taxa_sisters_right[1]));
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

    public void find_best_taxa_of_single_pass() {
        /*
        1.  Check if mapCandidateGainsPerListTax.size == 0 (any of the two maps) THEN all are singleton ... LOCK all taxaToMove
        2.  OTHERWISE, Use the two maps to find bestTaxaToMove [maxGain OR highestGain_with_max_num_satisfied_qrts]
        3.  LOCK the bestTaxaToMove and put corresponding stats in map
         */
//        System.out.println("-->Inside find_best_taxa_of_single_pass() ... printing mapCandidateTax_vs_8vals: " + mapCandidateTax_vs_8vals);
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
            System.out.println("AFTER choosing highest-taxon, printing lockedMap " + this.lockedTaxaBooleanMap);
            //create new map
            Map<String, Integer> mapAfterMovement = new HashMap<>(this.initialBipartitionMap);
            //reverse the bipartition for THIS taxon
            mapAfterMovement.put(taxonWithTheHighestGainInThisPass, Utils.getOppositePartition(mapAfterMovement.get(taxonWithTheHighestGainInThisPass)));
            StatsPerPass statsForThisPass = new StatsPerPass(taxonWithTheHighestGainInThisPass, highest_gain_value,
                    this.mapCandidateTax_vs_8vals.get(taxonWithTheHighestGainInThisPass), mapAfterMovement);
            this.listOfPerPassStatistics.add(statsForThisPass);
        }
    }

    public void changeParameterValuesForNextPass() {
        //Previous step's chosen-bipartition is THIS step's intiail-bipartition.
        this.initialBipartitionMap.clear();
        StatsPerPass previousPassStats = this.listOfPerPassStatistics.get(this.listOfPerPassStatistics.size() - 1);
        this.initialBipartitionMap = previousPassStats.map_final_bipartition;
        //Previous step's chosen-8Values will be THIS step's chosen-8Values
        this.initialBipartition_8_values.assign(previousPassStats._8_values_chosen_for_this_pass);

        //Clear all the per-pass maps
//        this.mapCandidateGainsPerListTax.clear();
        this.mapCandidateGainsPerListTax = new TreeMap<>(Collections.reverseOrder());
        this.mapCandidateTax_vs_8vals = new HashMap<>();
    }

    public void run_FM_single_iteration() {
        //per iteration ... has many passes. [will have rollback]
        int pass_num = 0;
        double max_hypothetical_gain_of_this_pass = Integer.MIN_VALUE;
        String taxa_with_max_hypothetical_gain = "NONE_CHECK_NONE";

        System.out.println("INSIDE run_FM_single_iteration() ... calling runFMSinglePass()");

        int pass = 0;
        boolean areAllTaxaLocked = false;
        while (areAllTaxaLocked == false && pass < 10) {
            pass++; //for debug printing....

            areAllTaxaLocked = Helper.checkAllValuesIFSame(this.lockedTaxaBooleanMap, true); //if ALL are true, then stop.
            run_FM_singlepass_hypothetical_swap(); //FM hypothetical single swap run
            find_best_taxa_of_single_pass(); //Find the best-taxon for THIS swap

            //Debug printing.
            StatsPerPass last_pass_stat = this.listOfPerPassStatistics.get(this.listOfPerPassStatistics.size() - 1);
            System.out.println("FM-pass = " + pass + " , choosing taxon: " + last_pass_stat.whichTaxaWasPassed + " , Gain = "
                    + last_pass_stat.maxGainOfThisPass);

            changeParameterValuesForNextPass();//Change parameters to maintain consistency wrt next step/box/pass.
        }

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
