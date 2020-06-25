package wqfm.utils;

import java.util.List;
import java.util.Map;
import wqfm.Status;

/**
 *
 * @author mahim
 */
public class Utils {

    public static String getDummyTaxonName(int level) {
//        String dummyTax = "DUMMY_MZCR_" + String.valueOf(level); //arbitrary names so as to not get mixed up with actual names
        String dummyTax = "X" + String.valueOf(level); //debug
        return dummyTax;
    }

    //Returns true if there is 1 taxa on either side, OR zero taxa on either side.[for pairwise swapping maybe needed]
    public static boolean isThisSingletonBipartition(List<Integer> logical_bipartition) { //true if this bipartition is a singleton bipartition
        int len = logical_bipartition.size();
        int sum = Helper.sumList(logical_bipartition);
        return (Math.abs(sum) == (len - 2)) || (Math.abs(sum) == len);
        //eg. -1,+1, +1,+1,+1,+1  --> so, two terms will lead to 0, rest sum will be length - 2
    }

    public static boolean isThisSingletonBipartition(Map<String, Integer> mapInitialBip) {
        int len = mapInitialBip.keySet().size();
        int sum = Helper.sumMapValuesInteger(mapInitialBip);

        return (Math.abs(sum) == (len - 2)) || (Math.abs(sum) == len);
    }

    public static int findQuartetStatus(int left_sis1_bip, int left_sis2_bip, int right_sis1_bip, int right_sis2_bip) {
        int[] four_bipartitions = {left_sis1_bip, left_sis2_bip, right_sis1_bip, right_sis2_bip};

        int sum_four_bipartitions = Helper.sumArray(four_bipartitions);
        //Blank check: Easier to check if blank quartet (all four are same) [priority wise first]
//        if ((left_sisters_bip[0] == left_sisters_bip[1]) && (right_sisters_bip[0] == right_sisters_bip[1]) && (left_sisters_bip[0] == right_sisters_bip[0])) {

        if (Math.abs(sum_four_bipartitions) == 4) { // -1,-1,-1,-1 or +1,+1,+1,+1 all will lead to sum == 4
            return Status.BLANK;
        }
        //Deferred Check: sum == 2 check [otherwise, permutations will be huge]
        if (Math.abs(sum_four_bipartitions) == 2) { //-1,+1 ,+1,+1  => +2 or +1,-1 , -1,-1 => -2 
            return Status.DEFERRED;
        }
        //Satisfied check: left are equal, right are equal AND left(any one) != right(any one)
        if ((left_sis1_bip == left_sis2_bip) && (right_sis1_bip == right_sis2_bip) && (left_sis1_bip != right_sis1_bip)) {
            return Status.SATISFIED;
        }
        //All check fails, Violated quartet
        return Status.VIOLATED;
    }

    public static int findQuartetStatus(int[] arr) { //call the above function
        return findQuartetStatus(arr[0], arr[1], arr[2], arr[3]);
    }

    public static int getOppositePartition(int partition) {
        switch (partition) {
            case Status.LEFT_PARTITION:
                return Status.RIGHT_PARTITION;
            case Status.RIGHT_PARTITION:
                return Status.LEFT_PARTITION;
            default:
                return Status.UNASSIGNED_PARTITION;
        }
    }


    /*public static Bipartition_8_values obtain8ValsOfTaxonBeforeSwap(CustomDSPerLevel customDS, List<Pair<Integer, Integer>> relevantQuartetsBeforeHypoMoving, String taxToConsider, Map<String, Integer> mapInitialBip) {
        //Consider each quartet. Using that set accordingly.
        Bipartition_8_values _8_vals = new Bipartition_8_values();
        return _8_vals;
    }*/
    public static int findQuartetStatusUsingShortcut(int status_quartet_before_hyp_swap) {
        if (status_quartet_before_hyp_swap == Status.DEFERRED) {
            return Status.UNKNOWN; //only if deferred, next calculations are necessary
        }
        return Status.DEFERRED; //s->d, v->d, b->d
    }

}

//----------------------------------------------------------- NOT USED FOR NOW ---------------------------------------------------------
/*public static int findQuartetStatus(int[] left_sisters_bip, int[] right_sisters_bip) {
        int[] four_bipartitions = {left_sisters_bip[0], left_sisters_bip[1], right_sisters_bip[0], right_sisters_bip[1]};

        int sum_four_bipartitions = Helper.sumArray(four_bipartitions);
        //Blank check: Easier to check if blank quartet (all four are same) [priority wise first]
//        if ((left_sisters_bip[0] == left_sisters_bip[1]) && (right_sisters_bip[0] == right_sisters_bip[1]) && (left_sisters_bip[0] == right_sisters_bip[0])) {

        if (Math.abs(sum_four_bipartitions) == 4) { // -1,-1,-1,-1 or +1,+1,+1,+1 all will lead to sum == 4
            return Status.BLANK;
        }
        //Deferred Check: sum == 2 check [otherwise, permutations will be huge]
        if (Math.abs(sum_four_bipartitions) == 2) { //-1,+1 ,+1,+1  => +2 or +1,-1 , -1,-1 => -2 
            return Status.DEFERRED;
        }
        //Satisfied check: left are equal, right are equal AND left(any one) != right(any one)
        if ((left_sisters_bip[0] == left_sisters_bip[1]) && (right_sisters_bip[0] == right_sisters_bip[1]) && (left_sisters_bip[0] != right_sisters_bip[0])) {
            return Status.SATISFIED;
        }
        //All check fails, Violated quartet
        return Status.VIOLATED;
    }*/
