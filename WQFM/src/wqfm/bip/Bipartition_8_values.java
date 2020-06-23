/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm.bip;

import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import wqfm.Status;
import wqfm.ds.CustomInitTables;
import wqfm.ds.Quartet;
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

    public void compute8ValuesUsingAllQuartets(CustomInitTables customDS, List<String> list_taxa_string,
            List<Pair<Integer, Integer>> list_quartets_indices, Map<String, Integer> map_bipartitions) {

//        Map<String, Integer> map_bipartitions = Utils.obtainBipartitionMap(list_taxa_string, bipartitions_list);
        for (int i = 0; i < list_quartets_indices.size(); i++) {
            Pair<Integer, Integer> pair = list_quartets_indices.get(i);
            int row = pair.getKey(); //obtain row idx
            int col = pair.getValue(); //obtian col idx
            Quartet quartet = customDS.table1_quartets_double_list.get(row).get(col); //obtain actual quartet

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
