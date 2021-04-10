package wqfm.algo;

import thread_objects.HypotheticalGainCalcuator;
import wqfm.ds.StatsPerPass;
import wqfm.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import thread_objects.HypotheticalGain_Object;
import wqfm.interfaces.Status;
import wqfm.bip.Bipartition_8_values;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.FMResultObject;
import wqfm.ds.Quartet;
import wqfm.main.Main;
import wqfm.utils.Helper;
import wqfm.bip.WeightedPartitionScores;
import wqfm.ds.InitialTable;

/**
 *
 * @author mahim
 */
public class FMComputer {

    private final Map<Integer, Integer> initial_bipartition_map_this_level_FIXED;
    private final Bipartition_8_values initial_bipartition_8_values_FIXED;
    
    
    public int level;
    private Bipartition_8_values initialBipartition_8_values;
    private final CustomDSPerLevel customDS;
    private Map<Integer, Integer> bipartitionMap;
    private final Map<Integer, Boolean> lockedTaxaBooleanMap; //true: LOCKED, false:FREE
    private Map<Double, List<Integer>> mapCandidateGainsPerListTax; // Map of hypothetical gain vs list of taxa
    private Map<Integer, Bipartition_8_values> mapCandidateTax_vs_8vals; //after hypothetical swap [i.e. IF this is taken as snapshot, no need to recalculate]
    private final List<StatsPerPass> listOfPerPassStatistics;

    public FMComputer(CustomDSPerLevel customDS,
            Map<Integer, Integer> mapInitialBipartition,
            Bipartition_8_values initialBip_8_vals, int level) {
        
        // Final values are initialized here.
        this.initial_bipartition_map_this_level_FIXED = new HashMap<>(mapInitialBipartition);
        this.initial_bipartition_8_values_FIXED = new Bipartition_8_values(initialBip_8_vals);
        
        // Regular values initialized.
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

        for (int tax : this.customDS.taxa_list_int) {
            this.lockedTaxaBooleanMap.put(tax, Boolean.FALSE);
        }
    }

    public void run_FM_singlepass_hypothetical_swap_threaded_version() throws InterruptedException, ExecutionException {//per pass or step [per num taxa of steps].
        //Test hypothetically ...
        List<Integer> freeTaxList = new ArrayList<>();
        for (int taxToConsider : this.customDS.taxa_list_int) {
            if (this.lockedTaxaBooleanMap.get(taxToConsider) == false) { // this is a free taxon, hypothetically test it ....
//                System.out.println("Line 65. Inside runFMSinglePassHypoSwap() .. taxaToConsider = " + taxToConsider);

                int taxPartValBeforeHypoSwap = this.bipartitionMap.get(taxToConsider);
                //First check IF moving this will lead to a singleton bipartition ....
                Map<Integer, Integer> newMap = new HashMap<>(this.bipartitionMap);
                newMap.put(taxToConsider, Utils.getOppositePartition(taxPartValBeforeHypoSwap)); //hypothetically make the swap.
                if (Utils.isThisSingletonBipartition(newMap) == true) {
                    //THIS hypothetical movement of taxToConsider leads to singleton bipartition so, continue ...
                    continue;
                } //ELSE: DOESN'T lead to singleton bipartition [add to map, and other datastructures]
                //Calculate hypothetical Gain ... [using discussed short-cut]
                freeTaxList.add(taxToConsider);

            } //end if
        }//end outer for
        int totalThreads = Status.TOTAL_THREADS;
        int totalLoops = (int) Math.floor(freeTaxList.size() / totalThreads);
        //System.out.println("totalLoops: " + totalLoops);
        //System.out.println("freetaxList: " + freeTaxList.size());
        int remainingTax = freeTaxList.size() - totalLoops * totalThreads;
        for (int j = 0; j < totalLoops; j++) {
            ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);

            List<Callable<HypotheticalGain_Object>> list = new ArrayList<>();

            int initialCounter = j * totalThreads;
            int finalCounter = initialCounter + totalThreads;
            for (int i = initialCounter; i < finalCounter; i++) {
                list.add(new HypotheticalGainCalcuator(freeTaxList.get(i), initialBipartition_8_values, customDS, bipartitionMap));

            }

            List<Future<HypotheticalGain_Object>> tasks = executorService.invokeAll(list);

            for (Future<HypotheticalGain_Object> task : tasks) {
                HypotheticalGain_Object hypotheticGain_Object = task.get();
                if (this.mapCandidateGainsPerListTax.containsKey(hypotheticGain_Object.Gain) == false) { // this gain was not contained
                    //initialize the taxon(for this gain-val) list.
                    this.mapCandidateGainsPerListTax.put(hypotheticGain_Object.Gain, new ArrayList<>());
                }//else: simply append to the list.
                this.mapCandidateGainsPerListTax.get(hypotheticGain_Object.Gain).add(hypotheticGain_Object.taxToConsider);
                this.mapCandidateTax_vs_8vals.put(hypotheticGain_Object.taxToConsider, hypotheticGain_Object._8_values_whole_considering_thisTax_swap);

            }
            executorService.shutdown();

        }
        //System.out.println("Remaining Tax:"+remainingTax);
        if (remainingTax > 0) {
            ExecutorService executorService = Executors.newFixedThreadPool(remainingTax);

            List<Callable<HypotheticalGain_Object>> list = new ArrayList<>();
            for (int i = freeTaxList.size() - remainingTax; i < freeTaxList.size(); i++) {

                list.add(new HypotheticalGainCalcuator(freeTaxList.get(i), initialBipartition_8_values, customDS, bipartitionMap));

            }

            List<Future<HypotheticalGain_Object>> tasks = executorService.invokeAll(list);
            //  System.out.println(tasks.size() + " Responses recieved.\n");
            for (Future<HypotheticalGain_Object> task : tasks) {
                HypotheticalGain_Object hypotheticGain_Object = task.get();
                if (this.mapCandidateGainsPerListTax.containsKey(hypotheticGain_Object.Gain) == false) { // this gain was not contained
                    //initialize the taxon(for this gain-val) list.
                    this.mapCandidateGainsPerListTax.put(hypotheticGain_Object.Gain, new ArrayList<>());
                }//else: simply append to the list.
                this.mapCandidateGainsPerListTax.get(hypotheticGain_Object.Gain).add(hypotheticGain_Object.taxToConsider);
                this.mapCandidateTax_vs_8vals.put(hypotheticGain_Object.taxToConsider, hypotheticGain_Object._8_values_whole_considering_thisTax_swap);

            }
            executorService.shutdown();
        }
    }

    public void run_FM_singlepass_hypothetical_swap() {//per pass or step [per num taxa of steps].
        //Test hypothetically ...
        for (int taxToConsider : this.customDS.taxa_list_int) {
            if (this.lockedTaxaBooleanMap.get(taxToConsider) == false) { // this is a free taxon, hypothetically test it ....
//                System.out.println("Line 65. Inside runFMSinglePassHypoSwap() .. taxaToConsider = " + taxToConsider);
                int taxPartValBeforeHypoSwap = this.bipartitionMap.get(taxToConsider);
                //First check IF moving this will lead to a singleton bipartition ....
                Map<Integer, Integer> newMap = new HashMap<>(this.bipartitionMap);
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

                for (int itr = 0; itr < relevantQuartetsBeforeHypoMoving.size(); itr++) {
                    int idx_relevant_qrt = relevantQuartetsBeforeHypoMoving.get(itr);
                    //No need explicit checking as customDS will be changed after every level
                    Quartet quartet = customDS.initial_table1_of_list_of_quartets.get(idx_relevant_qrt);

                    int status_quartet_before_hyp_swap = Utils.findQuartetStatus(bipartitionMap.get(quartet.taxa_sisters_left[0]),
                            bipartitionMap.get(quartet.taxa_sisters_left[1]), bipartitionMap.get(quartet.taxa_sisters_right[0]), bipartitionMap.get(quartet.taxa_sisters_right[1]));
                    //                    System.out.println("Before hypo swap, tax considered = " + taxToConsider + " , Qrt = " + quartet.toString() + " , Status = " + Status.PRINT_STATUS_QUARTET(status_quartet_before_hyp_swap));
                    int status_quartet_after_hyp_swap = Utils.findQuartetStatusUsingShortcut(status_quartet_before_hyp_swap); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
                    _8_vals_THIS_TAX_before_hypo_swap.addRespectiveValue(quartet.weight, status_quartet_before_hyp_swap); //_8values include ns, nv, nd, nb, ws, wv, wd, wb
                    _8_vals_THIS_TAX_AFTER_hypo_swap.addRespectiveValue(quartet.weight, status_quartet_after_hyp_swap); //If status.UNKNOWN, then don't add anything.

                    if (status_quartet_before_hyp_swap == Status.DEFERRED) {
                        deferredQuartetsBeforeHypoMoving.add(idx_relevant_qrt);
                    }

                } // end for [relevant-quartets-iteration]
                for (int itr_deferred_qrts = 0; itr_deferred_qrts < deferredQuartetsBeforeHypoMoving.size(); itr_deferred_qrts++) {
                    int qrt_idx_deferred_relevant_quartets_after_hypo_swap = deferredQuartetsBeforeHypoMoving.get(itr_deferred_qrts);
                    Quartet quartet = customDS.initial_table1_of_list_of_quartets.get(qrt_idx_deferred_relevant_quartets_after_hypo_swap);
                    int status_after_hypothetical_swap = Utils.findQuartetStatus(newMap.get(quartet.taxa_sisters_left[0]),
                            newMap.get(quartet.taxa_sisters_left[1]), newMap.get(quartet.taxa_sisters_right[0]), newMap.get(quartet.taxa_sisters_right[1]));
                    _8_vals_THIS_TAX_AFTER_hypo_swap.addRespectiveValue(quartet.weight, status_after_hypothetical_swap);
                }
                double ps_before_reduced = WeightedPartitionScores.calculatePartitionScoreReduced(_8_vals_THIS_TAX_before_hypo_swap);
                double ps_after_reduced = WeightedPartitionScores.calculatePartitionScoreReduced(_8_vals_THIS_TAX_AFTER_hypo_swap);
                double gainOfThisTax = ps_after_reduced - ps_before_reduced; //correct calculation

                Bipartition_8_values _8_values_whole_considering_thisTax_swap = new Bipartition_8_values();
                /*AfterHypoSwap.Whole_8Vals - BeforeHypoSwap.Whole_8Vals = AfterHypoSwap.OneTax.8Vals - BeforeHypoSwap.OneTax.8vals //vector rules of distance addition*/
                //So, AfterHypoSwap.Whole_8Vals = BeforeHypoSwap.Whole_8Vals + AfterHypoSwap.OneTax.8Vals - BeforeHypoSwap.OneTax.8vals
                _8_values_whole_considering_thisTax_swap.addObject(this.initialBipartition_8_values);
                _8_values_whole_considering_thisTax_swap.addObject(_8_vals_THIS_TAX_AFTER_hypo_swap);
                _8_values_whole_considering_thisTax_swap.subtractObject(_8_vals_THIS_TAX_before_hypo_swap);

                if (this.mapCandidateGainsPerListTax.containsKey(gainOfThisTax) == false) { // this gain was not contained
                    //initialize the taxon(for this gain-val) list.
                    this.mapCandidateGainsPerListTax.put(gainOfThisTax, new ArrayList<>());
                }//else: simply append to the list.
                this.mapCandidateGainsPerListTax.get(gainOfThisTax).add(taxToConsider); //add gain to map
                this.mapCandidateTax_vs_8vals.put(taxToConsider, _8_values_whole_considering_thisTax_swap);

            } //end if
        }//end outer for

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

    public void find_best_taxa_of_single_pass() {
        /*
        1.  Check if mapCandidateGainsPerListTax.size == 0 (any of the two maps) THEN all are singleton ... LOCK all taxaToMove
        2.  OTHERWISE, Use the two maps to find bestTaxaToMove [maxGain OR highestGain_with_max_num_satisfied_qrts]
        3.  LOCK the bestTaxaToMove and put corresponding stats in map
         */
        if (this.mapCandidateGainsPerListTax.isEmpty() == true) {
            for (int key : this.lockedTaxaBooleanMap.keySet()) {
                this.lockedTaxaBooleanMap.put(key, Boolean.TRUE); //ALL LEAD TO SINGLETON BIPARTITION .... [LOCK ALL THE TAXA]
            }
        }//do not add the prospective steps thing.
        else {

            Map.Entry<Double, List<Integer>> firstKeyEntry = this.mapCandidateGainsPerListTax.entrySet().iterator().next();
            double highest_gain_value = firstKeyEntry.getKey();
            List<Integer> list_taxaWithHighestGainValues = firstKeyEntry.getValue();
            int taxonWithTheHighestGainInThisPass;
            // exactly ONE taxon has the highest gain value... choose this
            if (list_taxaWithHighestGainValues.size() == 1) {
                //lock this taxon and put stats values for this taxon.
                taxonWithTheHighestGainInThisPass = list_taxaWithHighestGainValues.get(0);

            } else { // MORE than one taxon with same GAIN value .. select MAX count-satisfied-quartets one
                //create TreeMap<ns,tax> in descending order and take the first one.
                TreeMap<Integer, Integer> treeMap = new TreeMap<>(Collections.reverseOrder());
                for (int i = 0; i < list_taxaWithHighestGainValues.size(); i++) {
                    int taxChecking = list_taxaWithHighestGainValues.get(i);
                    treeMap.put(this.mapCandidateTax_vs_8vals.get(taxChecking).numSatisfied, taxChecking);
                }
                Map.Entry<Integer, Integer> highestNumSatTaxEntry = treeMap.entrySet().iterator().next();
                taxonWithTheHighestGainInThisPass = highestNumSatTaxEntry.getValue();
            }

            //lock and put stats values for this taxon in corresponding maps
            this.lockedTaxaBooleanMap.put(taxonWithTheHighestGainInThisPass, Boolean.TRUE);
            //create new map
            Map<Integer, Integer> mapAfterMovement = new HashMap<>(this.bipartitionMap);

            //reverse the bipartition for THIS taxon
            mapAfterMovement.put(taxonWithTheHighestGainInThisPass, Utils.getOppositePartition(mapAfterMovement.get(taxonWithTheHighestGainInThisPass)));

            StatsPerPass statsForThisPass = new StatsPerPass(taxonWithTheHighestGainInThisPass, highest_gain_value,
                    this.mapCandidateTax_vs_8vals.get(taxonWithTheHighestGainInThisPass), mapAfterMovement);

            this.listOfPerPassStatistics.add(statsForThisPass);
        }
    }

    public void run_FM_single_iteration() throws InterruptedException {
        int pass = 0; //to print while debugging.
        boolean areAllTaxaLocked = false; //initially this condition is false.
        while (areAllTaxaLocked == false) {
            pass++; //for debug printing....

            //Either do threaded or single-thread calculation for hypothetical gain calculation
            if (Status.THREADED_GAIN_CALCULATION_MODE == false) { //for now it is false.
                run_FM_singlepass_hypothetical_swap();
            } else {
                try {
                    //    //FM hypothetical single swap run
                    run_FM_singlepass_hypothetical_swap_threaded_version();
                } catch (ExecutionException ex) {
                    System.out.println("-->>(L 286.) Exception while running threads in run_FM_single_iteration()");

                }
            }

            find_best_taxa_of_single_pass(); //Find the best-taxon for THIS swap

            //Debug printing.
            if (listOfPerPassStatistics.isEmpty() == false) { //AT LEAST ONE per-pass val exists.
                StatsPerPass last_pass_stat = this.listOfPerPassStatistics.get(this.listOfPerPassStatistics.size() - 1);

                System.out.println("[FMComputer L 310]. FM-pass(box) = " + pass + " , best-taxon: " 
                        + Helper.getStringMappedName(last_pass_stat.whichTaxaWasPassed )
                        + " , MaxGain = " + last_pass_stat.maxGainOfThisPass);
                
                changeParameterValuesForNextPass();//Change parameters to maintain consistency wrt next step/box/pass.
            }
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

        System.out.println("[FMComputer L 341] Cumulative gain (max) = " + max_cumulative_gain_of_current_iteration
                + " , for pass = " + (pass_index_with_max_cumulative_gain + 1)
                + " , Tax Passed = " + Helper.getStringMappedName(statOfMaxCumulativeGainBox.whichTaxaWasPassed));
        
//                + " map_final_bipartition = \n" + Helper.getReadableMap(statOfMaxCumulativeGainBox.map_final_bipartition));
        //Initial bipartitions and ALL maps //Now change parameters accordingly for next FM iteration.
        //only when max-cumulative-gain is GREATER than zero, we will change, otherwise return the initial bipartition of this iteration
        
        
        if (max_cumulative_gain_of_current_iteration > Main.SMALLEPSILON) {
            this.bipartitionMap = new HashMap<>(statOfMaxCumulativeGainBox.map_final_bipartition);
            this.initialBipartition_8_values = statOfMaxCumulativeGainBox._8_values_chosen_for_this_pass;
            this.listOfPerPassStatistics.clear();
            this.mapCandidateGainsPerListTax = new TreeMap<>(Collections.reverseOrder());
            this.mapCandidateTax_vs_8vals = new HashMap<>();
            for (int tax : this.customDS.taxa_list_int) {
                this.lockedTaxaBooleanMap.put(tax, Boolean.FALSE);
            }
            return true;
        }
        //Set initial map to list's 1st item's map.
        return false;
    }

    //Whole FM ALGORITHm
    public FMResultObject run_FM_Algorithm_Whole() {
        Map<Integer, Integer> map_previous_iteration;//= new HashMap<>();
        boolean willIterateMore = true;
        int iterationsFM = 0; //can have stopping criterion for 10k iterations ?
        while (true) { //stopping condition
            if (iterationsFM > Main.MAX_ITERATIONS_LIMIT) { //another stopping criterion.
                System.out.println("[FMComputer L258.] Thread (" + Thread.currentThread().getName()
                        + ", " + Thread.currentThread().getId() + ") MAX_ITERATIONS_LIMIT = "
                        + Main.MAX_ITERATIONS_LIMIT + " is reached for level = " + this.level);
                break;
            }
            iterationsFM++;
//            System.out.println("---------------- LEVEL " + level + ", Iteration " + iterationsFM + " ----------------");
            map_previous_iteration = new HashMap<>(this.bipartitionMap); // always store this
            try {
                run_FM_single_iteration();
            } catch (InterruptedException ex) {
                Logger.getLogger(FMComputer.class.getName()).log(Level.SEVERE, null, ex);
            }
            willIterateMore = changeAndCheckAfterFMSingleIteration();
            if (willIterateMore == false) {
                this.bipartitionMap = map_previous_iteration; // just change as previous map
            }

//            System.out.println("End of Iteration " + iterationsFMprintPartitions
//                    + " new bipartition =>> \n"
//                    + Helper.getReadableMap(bipartitionMap));
//            System.out.println("================================================================");
            if (willIterateMore == false) {
                break;
            }
        }

        FMResultObject object = new FMResultObject(this.customDS, this.level); //pass the parent's customDS as reference
        
        /////////////////////////////////////// DEBUGGING \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        System.out.println("");
        if(this.bipartitionMap.equals(this.initial_bipartition_map_this_level_FIXED)){
            System.out.println(">>>>>>>>>>>>>>>> INITIAL MAP SAME AS RETURNED MAP OF FM PASS this level = " + this.level);
            
        }else{
            System.out.println(">>>><<<<<-------- NOT EQUAL MAP AS RETURNED MAP OF FM PASS this level = " + this.level);
        }
        
        System.out.println("INITIAL  ==> " + this.initial_bipartition_8_values_FIXED.toString());
        Helper.printPartition(this.initial_bipartition_map_this_level_FIXED, Status.LEFT_PARTITION, Status.RIGHT_PARTITION, InitialTable.map_of_int_vs_str_tax_list);
        
        System.out.println("RETURNED ==> " + this.initialBipartition_8_values.toString());
        Helper.printPartition(bipartitionMap, Status.LEFT_PARTITION, Status.RIGHT_PARTITION, InitialTable.map_of_int_vs_str_tax_list);
        
        
        
        
        //////////////////////////////////// Create results and return \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        object.createFMResultObjects(this.bipartitionMap); //pass THIS level's final-bipartition to get P_left,Q_left,P_right,Q_right
        return object;
    }

    ///////////////////////////// FOR DEBUGGING \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private void printTwoMaps() {
        for (int tax : this.mapCandidateTax_vs_8vals.keySet()) {
            Bipartition_8_values _8_vals = this.mapCandidateTax_vs_8vals.get(tax);
            System.out.println(tax + ": " + _8_vals.toString());
        }

        for (double gain : this.mapCandidateGainsPerListTax.keySet()) {
            List<Integer> list_tax_with_this_gain = this.mapCandidateGainsPerListTax.get(gain);
            for (int tax : list_tax_with_this_gain) {
                System.out.println("-->>Gain(" + tax + ") = " + gain);
            }
        }
    }
}
