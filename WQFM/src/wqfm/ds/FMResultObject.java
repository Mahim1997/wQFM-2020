package wqfm.ds;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    
    private Set<String> left_partition_taxa_set;
    private Set<String> right_partition_taxa_set;

    public FMResultObject(CustomDSPerLevel customDS_this_level, int level) {
        this.customDS_initial_this_level = customDS_this_level;
        //pass the reference of initial table to both left & right partitions.
        this.customDS_left_partition = new CustomDSPerLevel(customDS_this_level.table1_initial_table_of_quartets);
        this.customDS_right_partition = new CustomDSPerLevel(customDS_this_level.table1_initial_table_of_quartets);
        this.dummyTaxonThisLevel = Utils.getDummyTaxonName(level); //obtain the dummy node for this level
        
        //hashset [efficient checking for does set.contain]
        this.left_partition_taxa_set = new HashSet<>();
        this.right_partition_taxa_set = new HashSet<>();
    }

    public void createFMResultObjects(Map<String, Integer> mapOfBipartition) {
        //1. Traverse each quartet, find the deferred and blank quartets and pass to next.
        for (int i = 0; i < this.customDS_initial_this_level.quartet_indices_list_unsorted.size(); i++) {
            int qrt_idx = this.customDS_initial_this_level.quartet_indices_list_unsorted.get(i); //add to new lists of customDS
            Quartet q = this.customDS_initial_this_level.table1_initial_table_of_quartets.get(qrt_idx);
            // find quartet's status.
            int left_1_partition = mapOfBipartition.get(q.taxa_sisters_left[0]);
            int left_2_partition = mapOfBipartition.get(q.taxa_sisters_left[1]);
            int right_1_partition = mapOfBipartition.get(q.taxa_sisters_right[0]);
            int right_2_partition = mapOfBipartition.get(q.taxa_sisters_right[1]);

            int quartet_status = Utils.findQuartetStatus(left_1_partition, left_2_partition, right_1_partition, right_2_partition);

            //check if quartet is blank or deferred and only keep those, add dummy taxon ... [just add quartets-indices and add dummy-quartet to initialTable]
            //FMRunner handles the rest things such as finding taxa_list, sorting quartets wrt weights and finding relevant quartets per taxa
            if (quartet_status == Status.BLANK) { // pass THIS quartet, no need to add dummy [all 4 are on same side]
                if (left_1_partition == Status.LEFT_PARTITION) {
                    this.customDS_left_partition.quartet_indices_list_unsorted.add(qrt_idx);
                } else if (left_1_partition == Status.RIGHT_PARTITION) {
                    this.customDS_right_partition.quartet_indices_list_unsorted.add(qrt_idx);
                }
            } else if (quartet_status == Status.DEFERRED) {
                int[] arr_bipartition = {left_1_partition, left_2_partition, right_1_partition, right_2_partition};
                int commonBipartitionValue = findCommonBipartition(arr_bipartition); //find the common bipartition
                //essentially finding the uncommon taxon which is to be replaced by the dummy node.
                Quartet newQuartetWithDummy = replaceExistingQuartetWithDummyNode(q, arr_bipartition, commonBipartitionValue);
                this.customDS_initial_this_level.table1_initial_table_of_quartets.addToListOfQuartets(newQuartetWithDummy); //Add the INITIAL TABLE ...
                int index_of_new_qrt = this.customDS_initial_this_level.table1_initial_table_of_quartets.sizeTable() - 1; //obtain latest index
                //check on which Q subset to add i.e. Q_left or Q_right
                if (commonBipartitionValue == Status.LEFT_PARTITION) {
                    this.customDS_left_partition.quartet_indices_list_unsorted.add(index_of_new_qrt);
                } else if (commonBipartitionValue == Status.RIGHT_PARTITION) {
                    this.customDS_right_partition.quartet_indices_list_unsorted.add(index_of_new_qrt);
                }
            }
        }
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
