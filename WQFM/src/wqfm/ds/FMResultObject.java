package wqfm.ds;

import java.util.HashMap;
import java.util.Map;
import wqfm.interfaces.Status;
import wqfm.utils.CustomPair;
import wqfm.utils.Utils;

/**
 *
 * @author mahim
 */
public class FMResultObject {

    public CustomDSPerLevel customDS_left_partition;
    public CustomDSPerLevel customDS_right_partition;

    public final String dummyTaxonThisLevel;
    private final CustomDSPerLevel customDS_initial_this_level;

    private Map<Quartet, CustomPair> map_quartet_of_dummy_with_added_weights_and_partition;

    public FMResultObject(CustomDSPerLevel customDS_this_level, int level) {
        this.customDS_initial_this_level = customDS_this_level;
        //pass the reference of initial table to both left & right partitions.
        this.customDS_left_partition = new CustomDSPerLevel(); //do not initialize tables YET
        this.customDS_right_partition = new CustomDSPerLevel(); //do not initialize tables YET
        this.dummyTaxonThisLevel = Utils.getDummyTaxonName(level); //obtain the dummy node for this level

        this.customDS_left_partition.initial_table1_of_list_of_quartets = new InitialTable(false);
        this.customDS_right_partition.initial_table1_of_list_of_quartets = new InitialTable(false);

        // ------- map for quartets with dummy node ---> will be formed to one node ----------
        this.map_quartet_of_dummy_with_added_weights_and_partition = new HashMap<>();
    }

    public void createFMResultObjects(Map<String, Integer> mapOfBipartition) {
        //Initially just transfer all to P_left and P_right. [Then for quartets-with-dummy, just pass the dummy node] 
        for (String key_taxon : mapOfBipartition.keySet()) {
            if (mapOfBipartition.get(key_taxon) == Status.LEFT_PARTITION) {
                this.customDS_left_partition.taxa_list_string.add(key_taxon);
            } else if (mapOfBipartition.get(key_taxon) == Status.RIGHT_PARTITION) {
                this.customDS_right_partition.taxa_list_string.add(key_taxon);
            }
        }

        //Add dummy taxon to both partitions.
        this.customDS_left_partition.taxa_list_string.add(dummyTaxonThisLevel);
        this.customDS_right_partition.taxa_list_string.add(dummyTaxonThisLevel);

        //1. Traverse each quartet, find the deferred and blank quartets and pass to next.
        for (int itr = 0; itr < this.customDS_initial_this_level.quartet_indices_list_unsorted.size(); itr++) {
            int qrt_idx = this.customDS_initial_this_level.quartet_indices_list_unsorted.get(itr); //add to new lists of customDS
            Quartet quartet_parent = this.customDS_initial_this_level.initial_table1_of_list_of_quartets.get(qrt_idx);
            // find quartet's status.
            int left_1_partition = mapOfBipartition.get(quartet_parent.taxa_sisters_left[0]);
            int left_2_partition = mapOfBipartition.get(quartet_parent.taxa_sisters_left[1]);
            int right_1_partition = mapOfBipartition.get(quartet_parent.taxa_sisters_right[0]);
            int right_2_partition = mapOfBipartition.get(quartet_parent.taxa_sisters_right[1]);

            int quartet_status = Utils.findQuartetStatus(left_1_partition, left_2_partition, right_1_partition, right_2_partition);

            //check if quartet is blank or deferred and only keep those, add dummy taxon ... [add quartet-indices and taxa-set]
            if (quartet_status == Status.BLANK) { // pass THIS quartet, no need to add dummy [all 4 are on same side]
                if (left_1_partition == Status.LEFT_PARTITION) { //all four tax of the parent quartet are in left partition
                    this.customDS_left_partition.quartet_indices_list_unsorted.add(qrt_idx); //add old quartet's index in Q_left

                } else if (left_1_partition == Status.RIGHT_PARTITION) { //all four tax of the parent quartet are in right partition
                    this.customDS_right_partition.quartet_indices_list_unsorted.add(qrt_idx);  //add old quartet's index in Q_right

                }
            } else if (quartet_status == Status.DEFERRED) {
                int[] arr_bipartition = {left_1_partition, left_2_partition, right_1_partition, right_2_partition};
                int commonBipartitionValue = findCommonBipartition(arr_bipartition); //find the common bipartition [i.e. whether q goes to Q_left or Q_right]
//                System.out.println(">> FMResultObject (line 64) parent qrt = " + quartet_parent + " bip = " + mapOfBipartition);
                Quartet newQuartetWithDummy = replaceExistingQuartetWithDummyNode(quartet_parent, arr_bipartition, commonBipartitionValue); //Find the new quartet WITH dummy node [replaces uncommon tax]

                // do not add yet, first put to map with added weight eg. 1,2|5,11 and 1,2|5,15 will be 1,2|5,X with weight = w1+w2
                if (this.map_quartet_of_dummy_with_added_weights_and_partition.containsKey(newQuartetWithDummy) == false) { //this quartet-of-dummy DOESN't exist.
                    this.map_quartet_of_dummy_with_added_weights_and_partition.put(newQuartetWithDummy, new CustomPair(newQuartetWithDummy.weight, commonBipartitionValue)); //initialize with 0 so that next step doesn't have to be if-else
                } else {
                    // else we will add weights for the Pair (value of the map_quartet_of_dummy_with_added_weights_and_partition)
                    CustomPair pair_value_from_map = this.map_quartet_of_dummy_with_added_weights_and_partition.get(newQuartetWithDummy);
                    CustomPair new_pair = new CustomPair((pair_value_from_map.weight_double + newQuartetWithDummy.weight), pair_value_from_map.partition_int);
                    //this will update the added weights while maintaining the same bipartition.
                    this.map_quartet_of_dummy_with_added_weights_and_partition.put(newQuartetWithDummy, new_pair);

                }

                /// for some reason, pair doesn't seem to work hence custom-class [is there a way to do it more efficiently?]
////                this.map_quartet_of_dummy_with_added_weights_and_partition.put(newQuartetWithDummy, new_pair);
            }

        }
        //2. Now keep adding the corrected-weighted-quartets to initial-table
        for (Quartet q_with_dummy : this.map_quartet_of_dummy_with_added_weights_and_partition.keySet()) {
            CustomPair pair_val = this.map_quartet_of_dummy_with_added_weights_and_partition.get(q_with_dummy);
////            Pair<Double, Integer> pair_val = this.map_quartet_of_dummy_with_added_weights_and_partition.get(q_with_dummy);
            Quartet new_quartet = new Quartet(q_with_dummy.taxa_sisters_left[0], q_with_dummy.taxa_sisters_left[1], q_with_dummy.taxa_sisters_right[0],
                    q_with_dummy.taxa_sisters_right[1], pair_val.weight_double);
            //update the weight now.
            //push to initial table.
            this.customDS_initial_this_level.initial_table1_of_list_of_quartets.addToListOfQuartets(new_quartet);
            //obtain the index i.e. size - 1
            int idx_quartet_newly_added = this.customDS_initial_this_level.initial_table1_of_list_of_quartets.sizeTable() - 1;
            //push to which partition depending on the pair_value's bipartition stored.
            int bipartition_val = pair_val.partition_int;
            if (bipartition_val == Status.LEFT_PARTITION) {
                this.customDS_left_partition.quartet_indices_list_unsorted.add(idx_quartet_newly_added);
            } else if (bipartition_val == Status.RIGHT_PARTITION) {
                this.customDS_right_partition.quartet_indices_list_unsorted.add(idx_quartet_newly_added);
            }
        }
        //finally add the references to left and right partitions.
        this.customDS_left_partition.initial_table1_of_list_of_quartets.assignByReference(this.customDS_initial_this_level.initial_table1_of_list_of_quartets);
        this.customDS_right_partition.initial_table1_of_list_of_quartets.assignByReference(this.customDS_initial_this_level.initial_table1_of_list_of_quartets);
    }

    private int findCommonBipartition(int[] arr) {
        //Three will be same, one will be different
        int sum = arr[0] + arr[1] + arr[2] + arr[3];
        if (sum < 0) {
            return Status.LEFT_PARTITION;
        } else {
            return Status.RIGHT_PARTITION;
        }
    }

    private Quartet replaceExistingQuartetWithDummyNode(Quartet quartet, int[] arr, int commonBipartition) {
        Quartet q = new Quartet(quartet);
        int idx = -1;
        //finds which idx contains the uncommon bipartition
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != commonBipartition) {
                idx = i;
            }
        }
        //returns the taxon name of the uncommon bipartition
        switch (idx) {
            case Status.LEFT_SISTER_1_IDX:
                q.taxa_sisters_left[0] = this.dummyTaxonThisLevel;
                break;
            case Status.LEFT_SISTER_2_IDX:
                q.taxa_sisters_left[1] = this.dummyTaxonThisLevel;
                break;
            case Status.RIGHT_SISTER_1_IDX:
                q.taxa_sisters_right[0] = this.dummyTaxonThisLevel;
                break;
            case Status.RIGHT_SISTER_2_IDX:
                q.taxa_sisters_right[1] = this.dummyTaxonThisLevel;
                break;
            default:
                break;
        }
        q.sort_quartet_taxa_names();
        return q;
    }

}
