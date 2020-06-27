/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import wqfm.bip.Bipartition_8_values;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.Quartet;
import wqfm.interfaces.Status;
import wqfm.utils.Utils;
import wqfm.utils.WeightedPartitionScores;

/**
 *
 * @author Zahin
 */


public class HypotheticalGainCalcuator implements Callable<HypotheticalGain_Object> {

    private String taxToConsider;
    private Bipartition_8_values initialBipartition_8_values;
    private final CustomDSPerLevel customDS;
    private Map<String, Integer> bipartitionMap;

    public HypotheticalGainCalcuator(String taxToConsider, Bipartition_8_values initialBipartition_8_values, CustomDSPerLevel customDS, Map<String, Integer> bipartitionMap) {
        this.taxToConsider = taxToConsider;
        this.initialBipartition_8_values = initialBipartition_8_values;
        this.customDS = customDS;
        this.bipartitionMap = bipartitionMap;
    }

    @Override
    public HypotheticalGain_Object call() throws Exception {

        int taxPartValBeforeHypoSwap = this.bipartitionMap.get(taxToConsider);
        //First check IF moving this will lead to a singleton bipartition ....
        Map<String, Integer> newMap = new HashMap<>(this.bipartitionMap);
        newMap.put(taxToConsider, Utils.getOppositePartition(taxPartValBeforeHypoSwap)); //hypothetically make the swap.

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

        return new HypotheticalGain_Object(taxToConsider, _8_values_whole_considering_thisTax_swap, gainOfThisTax);

    }
}
