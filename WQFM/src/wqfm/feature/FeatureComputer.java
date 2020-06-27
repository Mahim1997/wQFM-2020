/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wqfm.ds.Bin;
import wqfm.ds.InitialTable;
import wqfm.ds.Quartet;

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
    static HashMap<List<String>, List<Quartet>> dictiory_3Tax_sequence = new HashMap<List<String>, List<Quartet>>();
    static HashMap<List<String>, List<Double>> dictiory_3Tax_sequence_weight = new HashMap<List<String>, List<Double>>();

    public static List<String> SortTaxaWithinQuartets(String tax1, String tax2, String tax3, String tax4) {
        List<String> temp = new ArrayList<String>(4);
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

    public static void Compute_Feature(List<Quartet> quartets_list) {

        makeDictionary(quartets_list);
        int features = 5;
        //    double[},{] results_table= new double[dictiory_3Tax_sequence.keySet().size()},{features];

        // int row = 0;
        List<Double> list_ratios = new ArrayList<Double>();
        int num_four_tax_seq_with_3_qrts = 0;
        for (List<String> threeTax : dictiory_3Tax_sequence_weight.keySet()) {
          //  System.out.println("Key: " + threeTax.get(0) + "," + threeTax.get(1) + ","
               //     + threeTax.get(2) + "," + threeTax.get(3));
            List<Double> weights_under_this_3_tax_seq = dictiory_3Tax_sequence_weight.get(threeTax);
            Collections.sort(weights_under_this_3_tax_seq, Collections.reverseOrder());
//             for(int i=0;i<weights_under_this_3_tax_seq.size();i++){
//                 System.out.print(weights_under_this_3_tax_seq.get(i)+" ");
//             }
            if (weights_under_this_3_tax_seq.size() == 3) {
                list_ratios.add(weights_under_this_3_tax_seq.get(0) / (weights_under_this_3_tax_seq.get(1) + weights_under_this_3_tax_seq.get(2)));
                num_four_tax_seq_with_3_qrts++;
            }
            //System.out.println("");
            //  row++;

        }
        double weighted_avg_bin_ratio = findInBins(list_ratios);
        System.out.println("Ratio (beta): "+weighted_avg_bin_ratio);

    }

    public static double findInBins(List<Double> list_ratios) {
        double highest_ratio = Collections.max(list_ratios);
        // double [][] bins = {{0.5,0.6},{0.6,0.7},{0.7,0.8},{0.8,0.9},{1,highest_ratio}} ;
        List<Bin> bins = new ArrayList<Bin>();
        bins.add(new Bin(0.5, 0.6));
        bins.add(new Bin(0.6, 0.7));
        bins.add(new Bin(0.7, 0.8));
        bins.add(new Bin(0.8, 0.9));
        bins.add(new Bin(1, highest_ratio));

        HashMap<Bin, Integer> dictionary_bins = new HashMap<Bin, Integer>();
        for (int i = 0; i < bins.size(); i++) {
            dictionary_bins.put(bins.get(i), 0);
        }
        for (double ratio : list_ratios) {
            for (Bin _bin : bins) {
                double lower_lim = _bin.lower_lim;
                double upper_lim = _bin.upper_lim;
                if (ratio >= lower_lim && ratio < upper_lim) {
                    int counter = dictionary_bins.get(_bin);
                    counter++;
                    dictionary_bins.put(_bin, counter);
                }

            }
        }
        int idx_bin = 0;
        double total_weights_to_divide_without_1 = 0;
        double cumulative_weighted_mid_ratio = 0;
        for (Bin _bin : bins) {
            double lower_lim = _bin.lower_lim;
            double upper_lim = _bin.upper_lim;
            if (lower_lim != 1.0) {
                double mid_point = 0.5 * (lower_lim + upper_lim);
                double weighted_mid_ratio = mid_point * dictionary_bins.get(_bin);
                total_weights_to_divide_without_1 += dictionary_bins.get(_bin);
                cumulative_weighted_mid_ratio += weighted_mid_ratio;
            }
        }
        double weighted_avg_bin_ratio = cumulative_weighted_mid_ratio / total_weights_to_divide_without_1;
      //  double proportion_with_greater_than_or_equal_to_1 = dictionary_bins.get(_bin) / (total_weights_to_divide_without_1 + dictionary_bins.get(_bin));
      return weighted_avg_bin_ratio;

    }

    public static void printDictionary() {
        for (List<String> i : dictiory_3Tax_sequence.keySet()) {
            System.out.println("Key: " + i.get(0) + " " + i.get(1) + " " + i.get(2) + " " + i.get(3));
            System.out.println(dictiory_3Tax_sequence.get(i));
            System.out.println(dictiory_3Tax_sequence_weight.get(i));
        }
    }

    public static void makeDictionary(List<Quartet> quartets_list) {

        for (int i = 0; i < quartets_list.size(); i++) {
            Quartet q = quartets_list.get(i);
            List<String> three_tax_sequence = SortTaxaWithinQuartets(q.taxa_sisters_left[0], q.taxa_sisters_left[1], q.taxa_sisters_right[0], q.taxa_sisters_right[1]);
            if (dictiory_3Tax_sequence.get(three_tax_sequence) == null) {
                List<Quartet> temp_list = new ArrayList<Quartet>();
                temp_list.add(q);
                dictiory_3Tax_sequence.put(three_tax_sequence, temp_list);
                List<Double> temp_list_2 = new ArrayList<Double>();
                temp_list_2.add(q.weight);
                dictiory_3Tax_sequence_weight.put(three_tax_sequence, temp_list_2);
            } else {
                List<Quartet> temp_list = dictiory_3Tax_sequence.get(three_tax_sequence);
                temp_list.add(q);
                dictiory_3Tax_sequence.put(three_tax_sequence, temp_list);
                List<Double> temp_list_2 = dictiory_3Tax_sequence_weight.get(three_tax_sequence);
                temp_list_2.add(q.weight);
                dictiory_3Tax_sequence_weight.put(three_tax_sequence, temp_list_2);
            }
        }
        //   printDictionary();
    }

}
