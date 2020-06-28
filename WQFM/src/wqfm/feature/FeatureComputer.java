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

    public static void computeBinningFeature(HashMap<List<String>, List<Quartet>> dictionary_4Tax_sequence, HashMap<List<String>, List<Double>> dictionary_4Tax_sequence_weight) {
        List<Double> list_ratios = new ArrayList<>();
        int num_four_tax_seq_with_3_qrts = 0;
        for (List<String> threeTax : dictionary_4Tax_sequence_weight.keySet()) {
            List<Double> weights_under_this_3_tax_seq = dictionary_4Tax_sequence_weight.get(threeTax);
            Collections.sort(weights_under_this_3_tax_seq, Collections.reverseOrder());
            if (weights_under_this_3_tax_seq.size() == 3) {
                list_ratios.add(weights_under_this_3_tax_seq.get(0) / (weights_under_this_3_tax_seq.get(1) + weights_under_this_3_tax_seq.get(2)));
                num_four_tax_seq_with_3_qrts++;
            }
        }

        if (list_ratios.isEmpty()) {
            System.out.println("Empty 4-tax-seq (default ratio): 1");
            WeightedPartitionScores.ALPHA_PARTITION_SCORE = Status.ALPHA_DEFAULT_VAL;
            WeightedPartitionScores.BETA_PARTITION_SCORE = 1;
        } else {
            double weighted_avg_bin_ratio = Bin.findInBins(list_ratios);
            
            WeightedPartitionScores.ALPHA_PARTITION_SCORE = 1;
            WeightedPartitionScores.BETA_PARTITION_SCORE = weighted_avg_bin_ratio;
            System.out.println("Prop(0.5,thresh) = " + Bin.proportion_left_thresh + ", Prop (thresh,1) = "
                    + Bin.proportion_after_thresh_before_1 + ", Prop(>=1) = " + Bin.proportion_greater_or_equal_1);
            System.out.println("Ratio (beta) = " + weighted_avg_bin_ratio);
        }

    }

    public static void printDictionary(HashMap<List<String>, List<Quartet>> dictionary_4Tax_sequence, HashMap<List<String>, List<Double>> dictionary_4Tax_sequence_weight) {
        for (List<String> i : dictionary_4Tax_sequence.keySet()) {
            System.out.print("Key: " + i.get(0) + " " + i.get(1) + " " + i.get(2) + " " + i.get(3) + " --> ");
            System.out.print("Size: " + dictionary_4Tax_sequence.get(i).size() + "---> " + dictionary_4Tax_sequence.get(i));
            System.out.println("\n----------------------------------------------------------------------");
            //   System.out.println(dictionary_4Tax_sequence_weight.get(i));
        }
    }

    public static void makeDictionary(Quartet q, HashMap<List<String>, List<Quartet>> dictionary_4Tax_sequence, HashMap<List<String>, List<Double>> dictionary_4Tax_sequence_weight) {

        List<String> four_tax_sequence = sortTaxaWithinQuartets(q.taxa_sisters_left[0], q.taxa_sisters_left[1], q.taxa_sisters_right[0], q.taxa_sisters_right[1]);
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
