/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm.feature;

import wqfm.configs.Config;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wqfm.ds.Quartet;
import wqfm.bip.WeightedPartitionScores;
import wqfm.main.Main;
import wqfm.configs.DefaultValues;

/**
 *
 * @author Zahin
 */
public class FeatureComputer {

    public static List<Integer> sortTaxaWithinQuartets(int tax1, int tax2, int tax3, int tax4) {
//        int[] arr = {tax1, tax2, tax3, tax4};
        List<Integer> list = new ArrayList<>(Arrays.asList(tax1, tax2, tax3, tax4));
        Collections.sort(list);
        return list;
    }

    public static boolean is_within_range(double v1, double v2, double threshold) {
        return (v1 - v2) / ((v1 + v2) / 2) <= threshold;
    }

    public static void computeBinningFeatureUsingLevel1(Map<List<Integer>, List<Double>> dictionary_4Tax_sequence_weight,
            int level) {
        if (level > 1) {
            System.out.println("Bin only level 1, current level = " + level + " , not binning, beta = " + WeightedPartitionScores.BETA_PARTITION_SCORE);
            return;
        }
        // Only do computations for level 1 i.e. if level == 1

        List<Double> list_ratios = new ArrayList<>();

        for (List<Integer> four_tax_seq : dictionary_4Tax_sequence_weight.keySet()) {
            List<Double> weights_under_this_4_tax_seq = dictionary_4Tax_sequence_weight.get(four_tax_seq);
            Collections.sort(weights_under_this_4_tax_seq, Collections.reverseOrder());
            if (weights_under_this_4_tax_seq.size() == 3) {
                list_ratios.add(weights_under_this_4_tax_seq.get(0) / (weights_under_this_4_tax_seq.get(1) + weights_under_this_4_tax_seq.get(2)));
            }
        }
        if (list_ratios.isEmpty()) { // empty ratios -> no 4-tax-seq exists.
            System.out.println("Empty list of 4-tax-seq with 3-quartet-config. (default ratio): " + DefaultValues.BETA_DEFAULT_VAL);
            WeightedPartitionScores.ALPHA_PARTITION_SCORE = DefaultValues.ALPHA_DEFAULT_VAL;
            WeightedPartitionScores.BETA_PARTITION_SCORE = DefaultValues.BETA_DEFAULT_VAL; // beta default = 1
            if (level == 1) {
                System.out.println("ZERO 4-tax-seq exists with 3-qrt-config. DONT'T BIN FURTHER.");
                Bin.WILL_DO_DYNAMIC = false;
            }

        } else { //calculate bins [list-ratios do exist]

            double weighted_avg_bin_ratio = DefaultValues.BETA_DEFAULT_VAL;

            if (Bin.WILL_DO_DYNAMIC == true) { //only compute on Bin.true
                weighted_avg_bin_ratio = Bin.calculateBinsAndFormScores(list_ratios); //forms bins and calculates scores..

                System.out.println("\nNow, P(0.5," + Config.THRESHOLD_BINNING + ") = " + Bin.proportion_left_thresh
                        + ", P(" + Config.THRESHOLD_BINNING + ",1) = "
                        + Bin.proportion_after_thresh_before_1 + ", P(>=1) = " + Bin.proportion_greater_or_equal_1);
                Bin.WILL_DO_DYNAMIC = false;
                WeightedPartitionScores.BETA_PARTITION_SCORE = weighted_avg_bin_ratio;
                System.out.println("Bin only level 1, beta = " + WeightedPartitionScores.BETA_PARTITION_SCORE);

            }

        }

    }

    public static void computeBinningFeatureUsingAllLevels(Map<List<Integer>, List<Double>> dictionary_4Tax_sequence_weight,
            int level) {
        //check on level==0 if is on the right side, don't bin further ...
        List<Double> list_ratios = new ArrayList<>();

        for (List<Integer> four_tax_seq : dictionary_4Tax_sequence_weight.keySet()) {
            List<Double> weights_under_this_4_tax_seq = dictionary_4Tax_sequence_weight.get(four_tax_seq);
            Collections.sort(weights_under_this_4_tax_seq, Collections.reverseOrder());
            if (weights_under_this_4_tax_seq.size() == 3) {
                list_ratios.add(weights_under_this_4_tax_seq.get(0) / (weights_under_this_4_tax_seq.get(1) + weights_under_this_4_tax_seq.get(2)));
            }
            if (weights_under_this_4_tax_seq.size() > 3) {
                System.out.println("-->>L 71. FeatureComputer. ratios.size exceeded 3. Check inputs.");
                System.out.println("Printing this ratios, and sequences." + weights_under_this_4_tax_seq
                        + " , " + (four_tax_seq));
            }
        }

        if (list_ratios.isEmpty()) {
            System.out.println("Empty list of 4-tax-seq with 3-quartet-config. (default ratio): " + DefaultValues.BETA_DEFAULT_VAL);
            WeightedPartitionScores.ALPHA_PARTITION_SCORE = DefaultValues.ALPHA_DEFAULT_VAL;
            WeightedPartitionScores.BETA_PARTITION_SCORE = DefaultValues.BETA_DEFAULT_VAL; // beta default = 1
            if (level == 1) {
                System.out.println("ZERO 4-tax-seq exists with 3-qrt-config. DONT'T BIN FURTHER.");
                Bin.WILL_DO_DYNAMIC = false;
            }

        } else { //calculate bins [list-ratios do exist]

//            System.out.println("List-ratios not empty, printing dictionary.");
//            printDictionary(dictionary_4Tax_sequence, dictionary_4Tax_sequence_weight);
            double weighted_avg_bin_ratio = DefaultValues.BETA_DEFAULT_VAL;

            if (Bin.WILL_DO_DYNAMIC == true) { //only compute on Bin.true
                weighted_avg_bin_ratio = Bin.calculateBinsAndFormScores(list_ratios); //forms bins and calculates scores..

            }

            if (level == 1) { //check on level == 1 and set accordingly.
                if (Bin.proportion_left_thresh < Config.CUT_OFF_LIMIT_BINNING) { //level == 1 has no good distribution ... so do no more.
                    //stop ... don't bin on any levels. set to 1.
                    Bin.WILL_DO_DYNAMIC = false; //DEBUGGING FOR NOW
                    System.out.println("\nNow, P(0.5," + Config.THRESHOLD_BINNING + ") = " + Bin.proportion_left_thresh
                            + ", P(" + Config.THRESHOLD_BINNING + ",1) = "
                            + Bin.proportion_after_thresh_before_1 + ", P(>=1) = " + Bin.proportion_greater_or_equal_1);

                    System.out.println(">> DON'T BIN ON NEXT LEVELS. Level 1 has good distribution above threshold = " + Config.THRESHOLD_BINNING
                            + " set BETA = " + DefaultValues.BETA_DEFAULT_VAL);
                    WeightedPartitionScores.BETA_PARTITION_SCORE = DefaultValues.BETA_DEFAULT_VAL; // set to 1 
                }

            }
            if (Bin.WILL_DO_DYNAMIC == true) { //only bin on true conditions.
                //set p.score as ratio.
                WeightedPartitionScores.BETA_PARTITION_SCORE = weighted_avg_bin_ratio;
                System.out.println("\nP(0.5," + Config.THRESHOLD_BINNING + ") = " + Bin.proportion_left_thresh
                        + ", P(" + Config.THRESHOLD_BINNING + ",1) = "
                        + Bin.proportion_after_thresh_before_1 + ", P(>=1) = " + Bin.proportion_greater_or_equal_1);

                System.out.println("Level" + level + ", Weighted-Avg-Bin-Ratio (to set BETA) = " + weighted_avg_bin_ratio);

            }

        }

    }

    public static void computeBinningFeature(Map<List<Integer>, List<Double>> dictionary_4Tax_sequence_weight,
            int level) {

        if (Config.BIN_LIMIT_LEVEL_1 == true) {
            System.out.println("Binning only at level 1.");
            FeatureComputer.computeBinningFeatureUsingLevel1(dictionary_4Tax_sequence_weight, level);
        } else {
            System.out.println("Binning on all levels.");
            FeatureComputer.computeBinningFeatureUsingAllLevels(dictionary_4Tax_sequence_weight, level);
        }

    }

    public static void printDictionary(HashMap<List<String>, List<Quartet>> dictionary_4Tax_sequence,
            HashMap<List<String>, List<Double>> dictionary_4Tax_sequence_weight) {
        for (List<String> i : dictionary_4Tax_sequence.keySet()) {
            System.out.print("Key: " + i.get(0) + " " + i.get(1) + " " + i.get(2) + " " + i.get(3) + " --> ");
            System.out.print("Size: " + dictionary_4Tax_sequence.get(i).size() + "---> " + dictionary_4Tax_sequence.get(i));
            System.out.println("\n----------------------------------------------------------------------");
            //   System.out.println(dictionary_4Tax_sequence_weight.get(i));
        }
    }

    public static void makeDictionary(Quartet q, Map<List<Integer>, List<Double>> map_weights_four_tax_seq) {
        List<Integer> four_tax_sequence = sortTaxaWithinQuartets(q.taxa_sisters_left[0], q.taxa_sisters_left[1],
                q.taxa_sisters_right[0], q.taxa_sisters_right[1]);

        if (map_weights_four_tax_seq.containsKey(four_tax_sequence) == false) { // this 4-tax-seq has no quartet-weights.
            List<Double> list_weights = new ArrayList<>();
            list_weights.add(q.weight);
            map_weights_four_tax_seq.put(four_tax_sequence, list_weights);
        } else {
            if (map_weights_four_tax_seq.get(four_tax_sequence).size() >= 3) {
                System.out.println("\n\n\n\nL 118. FeatureComputer.java ... ratios-size > 3 for sequence " + four_tax_sequence + " , "
                        + "printing the array-list .. " + map_weights_four_tax_seq.get(four_tax_sequence) + " EXITING.");
                System.exit(-1);
            }
            map_weights_four_tax_seq.get(four_tax_sequence).add(q.weight);
        }

    }

}
