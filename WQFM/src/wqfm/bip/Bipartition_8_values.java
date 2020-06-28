/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm.bip;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import wqfm.interfaces.Status;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.Quartet;
import wqfm.feature.FeatureComputer;
import wqfm.utils.Utils;

/**
 *
 * @author mahim
 */
public class Bipartition_8_values {

    public int numSatisfied;
    public int numViolated;
    public int numDeferred;
    public int numBlank;

    public double wtSatisfied;
    public double wtViolated;
    public double wtDeferred;
    public double wtBlank;

    public Bipartition_8_values(int numSatisfiedQuartets, int numViolatedQuartets, int numDeferredQuartets, int numBlankQuartets, double weightSatisfiedQuartets, double weightViolatedQuartets, double weightDeferredQuartets, double weightBlankQuartets) {
        this.numSatisfied = numSatisfiedQuartets;
        this.numViolated = numViolatedQuartets;
        this.numDeferred = numDeferredQuartets;
        this.numBlank = numBlankQuartets;
        this.wtSatisfied = weightSatisfiedQuartets;
        this.wtViolated = weightViolatedQuartets;
        this.wtDeferred = weightDeferredQuartets;
        this.wtBlank = weightBlankQuartets;
    }

    public Bipartition_8_values() {
        this.numSatisfied = 0;
        this.numViolated = 0;
        this.numDeferred = 0;
        this.numBlank = 0;
        this.wtSatisfied = 0.0;
        this.wtViolated = 0.0;
        this.wtDeferred = 0.0;
        this.wtBlank = 0.0;
    }

    public Bipartition_8_values(Bipartition_8_values obj) {
        this.numSatisfied = obj.numSatisfied;
        this.numViolated = obj.numViolated;
        this.numDeferred = obj.numDeferred;
        this.numBlank = obj.numBlank;
        this.wtSatisfied = obj.wtSatisfied;
        this.wtViolated = obj.wtViolated;
        this.wtDeferred = obj.wtDeferred;
        this.wtBlank = obj.wtBlank;
    }

    public void addRespectiveValue(double weight, int status) {
        switch (status) {
            case Status.SATISFIED:
                this.numSatisfied++;
                this.wtSatisfied += weight;
                break;
            case Status.VIOLATED:
                this.numViolated++;
                this.wtViolated += weight;
                break;
            case Status.DEFERRED:
                this.numDeferred++;
                this.wtDeferred += weight;
                break;
            case Status.BLANK:
                this.numBlank++;
                this.wtBlank += weight;
                break;
            case Status.UNKNOWN: // do nothing for this case
                break;
            default:
                break;
        }
    }

    private void addRespectiveValue(Quartet q, int status) { // not needed for now.
        addRespectiveValue(q.weight, status);
    }

    public void compute8ValuesUsingAllQuartets(CustomDSPerLevel customDS, Map<String, Integer> map_bipartitions) {
        HashMap<List<String>, List<Quartet>> dictiory_4Tax_sequence = new HashMap<List<String>, List<Quartet>>();
        HashMap<List<String>, List<Double>> dictiory_4Tax_sequence_weight = new HashMap<List<String>, List<Double>>();
        System.out.println("bipartition size : "+map_bipartitions.keySet().size());
        System.out.println("Keyset size before populating: "+dictiory_4Tax_sequence.keySet().size());
        HashSet<Quartet> set=new HashSet<Quartet>();  
        for (int idx_quartet : customDS.quartet_indices_list_unsorted) {
            Quartet quartet = customDS.initial_table1_of_list_of_quartets.get(idx_quartet);
           // quartet.printQuartet();
            if(!set.contains(quartet)){
                System.out.println("Hello, new quartet: "+quartet);
                set.add(quartet);

                FeatureComputer.makeDictionary(quartet, dictiory_4Tax_sequence, dictiory_4Tax_sequence_weight);
            }
            //obtain the quartet's taxa's bipartitions
            int left_sis_1_bip_val = map_bipartitions.get(quartet.taxa_sisters_left[0]);
            int left_sis_2_bip_val = map_bipartitions.get(quartet.taxa_sisters_left[1]);
            int right_sis_1_bip_val = map_bipartitions.get(quartet.taxa_sisters_right[0]);
            int right_sis_2_bip_val = map_bipartitions.get(quartet.taxa_sisters_right[1]);

            int status_quartet = Utils.findQuartetStatus(left_sis_1_bip_val, left_sis_2_bip_val, right_sis_1_bip_val, right_sis_2_bip_val); //obtain quartet status
            //compute scores according to status.
            switch (status_quartet) {
                case Status.SATISFIED:
                    this.numSatisfied++;
                    this.wtSatisfied += quartet.weight;
                    break;
                case Status.VIOLATED:
                    this.numViolated++;
                    this.wtViolated += quartet.weight;
                    break;
                case Status.DEFERRED:
                    this.numDeferred++;
                    this.wtDeferred += quartet.weight;
                    break;
                case Status.BLANK:
                    this.numBlank++;
                    this.wtBlank += quartet.weight;
                    break;
                default:
                    break;
            }
        }
        System.out.println("Keyset size after populating: "+dictiory_4Tax_sequence.keySet().size());
        System.out.println("Done making dictionary ... printing ..........");
        FeatureComputer.printDictionary(dictiory_4Tax_sequence, dictiory_4Tax_sequence_weight);
        System.out.println("... DONE PRINTING ..........");
        FeatureComputer.Compute_Feature(dictiory_4Tax_sequence,dictiory_4Tax_sequence_weight);
    }

    @Override
    public String toString() {
        return "_8Vals{" + "ns=" + numSatisfied + ", nv=" + numViolated + ", nd=" + numDeferred + ", nb=" + numBlank + ", ws=" + wtSatisfied + ", wv=" + wtViolated + ", wd=" + wtDeferred + ", wb=" + wtBlank + '}';
    }

    public void addObject(Bipartition_8_values obj) {
        this.numSatisfied += obj.numSatisfied;
        this.numViolated += obj.numViolated;
        this.numDeferred += obj.numDeferred;
        this.numBlank += obj.numBlank;
        this.wtSatisfied += obj.wtSatisfied;
        this.wtViolated += obj.wtViolated;
        this.wtDeferred += obj.wtDeferred;
        this.wtBlank += obj.wtBlank;
    }

    public void subtractObject(Bipartition_8_values obj) {
        this.numSatisfied -= obj.numSatisfied;
        this.numViolated -= obj.numViolated;
        this.numDeferred -= obj.numDeferred;
        this.numBlank -= obj.numBlank;
        this.wtSatisfied -= obj.wtSatisfied;
        this.wtViolated -= obj.wtViolated;
        this.wtDeferred -= obj.wtDeferred;
        this.wtBlank -= obj.wtBlank;
    }

    public void assign(Bipartition_8_values obj) {
        this.numSatisfied = obj.numSatisfied;
        this.numViolated = obj.numViolated;
        this.numDeferred = obj.numDeferred;
        this.numBlank = obj.numBlank;
        this.wtSatisfied = obj.wtSatisfied;
        this.wtViolated = obj.wtViolated;
        this.wtDeferred = obj.wtDeferred;
        this.wtBlank = obj.wtBlank;
    }
}
