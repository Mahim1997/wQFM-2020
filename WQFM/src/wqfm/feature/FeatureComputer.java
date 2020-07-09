/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import wqfm.ds.Quartet;
import wqfm.bip.WeightedPartitionScores;
import wqfm.interfaces.Status;
import wqfm.main.Main;

/**
 *
 * @author Zahin
 */
public class FeatureComputer {

    Double F1;
    Double F2;
    Double F3;
    Double F4;
    Double F5;

    public static List<String> sortTaxaWithinQuartets(String tax1, String tax2, String tax3, String tax4) {
        List<String> temp = new ArrayList<>(4);
        temp.add(tax1);
        temp.add(tax2);
        temp.add(tax3);
        temp.add(tax4);
        Collections.sort(temp);
        return temp;

    }

    public static boolean is_within_range(double v1, double v2, double threshold) {
        return (v1 - v2) / ((v1 + v2) / 2) <= threshold;
    }

    public static void computeBinningFeature(HashMap<List<String>, List<Double>> dictionary_4Tax_sequence_weight, int level) {
        //check on level==0 if is on the right side, don't bin further ...
        List<Double> list_ratios = new ArrayList<>();
        for (List<String> threeTax : dictionary_4Tax_sequence_weight.keySet()) {
            List<Double> weights_under_this_3_tax_seq = dictionary_4Tax_sequence_weight.get(threeTax);
            Collections.sort(weights_under_this_3_tax_seq, Collections.reverseOrder());
            if (weights_under_this_3_tax_seq.size() == 3) {
                list_ratios.add(weights_under_this_3_tax_seq.get(0) / (weights_under_this_3_tax_seq.get(1) + weights_under_this_3_tax_seq.get(2)));
            }
            if (weights_under_this_3_tax_seq.size() > 3) {
                System.out.println("-->>L 57. FeatureComputer. ratios.size exceeded 3. Check inputs.");
                System.out.println("Printing this ratios, and sequences." + weights_under_this_3_tax_seq + " , " + threeTax);
            }
        }

        if (list_ratios.isEmpty()) {
            System.out.println("Empty list of 4-tax-seq with 3-quartet-config. (default ratio): " + Status.BETA_DEFAULT_VAL);
            WeightedPartitionScores.ALPHA_PARTITION_SCORE = Status.ALPHA_DEFAULT_VAL;
            WeightedPartitionScores.BETA_PARTITION_SCORE = 1;
            if (level == 1) {
                System.out.println("ZERO 4-tax-seq exists with 3-qrt-config. DONT'T BIN FURTHER.");
                Bin.WILL_DO_DYNAMIC = false;
            }

        } else { //calculate bins [list-ratios do exist]

//            System.out.println("List-ratios not empty, printing dictionary.");
//            printDictionary(dictionary_4Tax_sequence, dictionary_4Tax_sequence_weight);
            double weighted_avg_bin_ratio = Status.BETA_DEFAULT_VAL;

            if (Bin.WILL_DO_DYNAMIC == true) { //only compute on Bin.true
                weighted_avg_bin_ratio = Bin.calculateBinsAndFormScores(list_ratios); //forms bins and calculates scores..

            }

            if (level == 1) { //check on level == 1 and set accordingly.
                if (Bin.proportion_left_thresh < Main.CUT_OFF_LIMIT_BINNING) { //level == 1 has no good distribution ... so do no more.
                    //stop ... don't bin on any levels. set to 1.
                    Bin.WILL_DO_DYNAMIC = false; //DEBUGGING FOR NOW
                    System.out.println(">> DON'T BIN ON NEXT LEVELS. Level 1 has good distribution above threshold = " + Main.THRESHOLD_BINNING
                            + " set BETA = " + Status.BETA_DEFAULT_VAL);
                    WeightedPartitionScores.BETA_PARTITION_SCORE = Status.BETA_DEFAULT_VAL; // set to 1 
                }
            }
            if (Bin.WILL_DO_DYNAMIC == true) { //only bin on true conditions.
                //set p.score as ratio.
                WeightedPartitionScores.BETA_PARTITION_SCORE = weighted_avg_bin_ratio;
                System.out.println("\nP(0.5," + Main.THRESHOLD_BINNING + ") = " + Bin.proportion_left_thresh
                        + ", P(" + Main.THRESHOLD_BINNING + ",1) = "
                        + Bin.proportion_after_thresh_before_1 + ", P(>=1) = " + Bin.proportion_greater_or_equal_1);

                System.out.println("Level" + level + ", Weighted-Avg-Bin-Ratio (to set BETA) = " + weighted_avg_bin_ratio);

            }

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

    public static void makeDictionary(Quartet q, HashMap<List<String>, List<Quartet>> dictionary_4Tax_sequence,
            HashMap<List<String>, List<Double>> dictionary_4Tax_sequence_weight) {

        List<String> four_tax_sequence = sortTaxaWithinQuartets(q.taxa_sisters_left[0], q.taxa_sisters_left[1],
                q.taxa_sisters_right[0], q.taxa_sisters_right[1]);

        if (dictionary_4Tax_sequence.get(four_tax_sequence) == null) {
            List<Quartet> temp_list = new ArrayList<>();
            temp_list.add(q);
            dictionary_4Tax_sequence.put(four_tax_sequence, temp_list);
            List<Double> temp_list_2 = new ArrayList<>();
            temp_list_2.add(q.weight);
            dictionary_4Tax_sequence_weight.put(four_tax_sequence, temp_list_2);
        } else {
            List<Quartet> temp_list = dictionary_4Tax_sequence.get(four_tax_sequence);
            temp_list.add(q);
            dictionary_4Tax_sequence.put(four_tax_sequence, temp_list);
            List<Double> temp_list_2 = dictionary_4Tax_sequence_weight.get(four_tax_sequence);
            temp_list_2.add(q.weight);
            dictionary_4Tax_sequence_weight.put(four_tax_sequence, temp_list_2);
        }
    }

}
