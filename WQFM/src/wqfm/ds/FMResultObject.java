package wqfm.ds;

import java.util.Map;
import wqfm.Status;
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

    private boolean is_dummy_added_to_left = false; // for faster checking
    private boolean is_dummy_added_to_right = false; //for faster checking

    public FMResultObject(CustomDSPerLevel customDS_this_level, int level) {
        this.customDS_initial_this_level = customDS_this_level;
        //pass the reference of initial table to both left & right partitions.
        this.customDS_left_partition = new CustomDSPerLevel(); //do not initialize tables YET
        this.customDS_right_partition = new CustomDSPerLevel(); //do not initialize tables YET
        this.dummyTaxonThisLevel = Utils.getDummyTaxonName(level); //obtain the dummy node for this level
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

        //1. Traverse each quartet, find the deferred and blank quartets and pass to next.
        for (int itr_for_quartet_indices = 0; itr_for_quartet_indices < this.customDS_initial_this_level.quartet_indices_list_unsorted.size(); itr_for_quartet_indices++) {
            int qrt_idx = this.customDS_initial_this_level.quartet_indices_list_unsorted.get(itr_for_quartet_indices); //add to new lists of customDS
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
                System.out.println(">> FMResultObject (line 64) parent qrt = " + quartet_parent + " bip = " + mapOfBipartition);
                Quartet newQuartetWithDummy = replaceExistingQuartetWithDummyNode(quartet_parent, arr_bipartition, commonBipartitionValue); //Find the new quartet WITH dummy node [replaces uncommon tax]
                this.customDS_initial_this_level.initial_table1_of_list_of_quartets.addToListOfQuartets(newQuartetWithDummy); //Add the INITIAL TABLE ...
                int idx_new_quartet_with_dummy = this.customDS_initial_this_level.initial_table1_of_list_of_quartets.sizeTable() - 1; //obtain latest index
                //check on which Q subset to add i.e. Q_left or Q_right
                if (commonBipartitionValue == Status.LEFT_PARTITION) {
                    this.customDS_left_partition.quartet_indices_list_unsorted.add(idx_new_quartet_with_dummy); //add new quartet's index in Q_left
                    addDummyToLeftPartition(); //only add the dummy node to P_left as all the others were added before.
                } else if (commonBipartitionValue == Status.RIGHT_PARTITION) {
                    this.customDS_right_partition.quartet_indices_list_unsorted.add(idx_new_quartet_with_dummy); //add new quartet's index in Q_right
                    addDummyToRightPartition(); //only add the dummy node to P_right as all the others were added before.
                }
            }

        }
        //finally add the references to left and right partitions.
        this.customDS_left_partition.initial_table1_of_list_of_quartets = this.customDS_initial_this_level.initial_table1_of_list_of_quartets;
        this.customDS_right_partition.initial_table1_of_list_of_quartets = this.customDS_initial_this_level.initial_table1_of_list_of_quartets;
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

        return q;
    }

    private void addDummyToLeftPartition() {
        if (this.is_dummy_added_to_left == false) {
            this.customDS_left_partition.taxa_list_string.add(dummyTaxonThisLevel);
            this.is_dummy_added_to_left = true; //no need to add again.
        }
    }

    private void addDummyToRightPartition() {
        if (this.is_dummy_added_to_right == false) {
            this.customDS_right_partition.taxa_list_string.add(dummyTaxonThisLevel);
            this.is_dummy_added_to_right = true; //no need to add again.
        }
    }

}

/*private String findNameOfUncommonTaxon(Quartet q, int[] arr, int commonBipartition) {
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
                return q.taxa_sisters_left[0];
            case Status.LEFT_SISTER_2_IDX:
                return q.taxa_sisters_left[1];
            case Status.RIGHT_SISTER_1_IDX:
                return q.taxa_sisters_right[0];
            case Status.RIGHT_SISTER_2_IDX:
                return q.taxa_sisters_right[1];
        }
        return "NONE";
    }
 */
 /*
    //find a more efficient implementation
    private int findIndexOfUncommonTaxon(int left_1_partition, int left_2_partition, int right_1_partition, int right_2_partition, int mostCommonPartitionValue) {
        int[] arr = {left_1_partition, left_2_partition, right_1_partition, right_2_partition};
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != mostCommonPartitionValue) {
                return i;
            }
        }
        return Status.UNDEFINED; //undefined
    }
 */
