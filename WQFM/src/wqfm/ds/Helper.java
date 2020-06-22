package wqfm.ds;

import java.util.List;
import wqfm.Status;

/**
 *
 * @author mahim
 */
public class Helper {

    public static int sumArray(int[] arr) {
        int sum = 0;
        for (int x : arr) {
            sum += x;
        }
        return sum;
    }

    public static int sumList(List<Integer> list) {
        int sum = 0;
        sum = list.stream().map((x) -> x).reduce(sum, Integer::sum);
        return sum;
    }

    public static int getOppositePartition(int partition) {
        switch (partition) {
            case Status.LEFT_PARTITION:
                return Status.RIGHT_PARTITION;
            case Status.RIGHT_PARTITION:
                return Status.LEFT_PARTITION;
            default:
                return Status.UNASSIGNED;
        }
    }

    public static boolean isSingletonBipartition(List<Integer> bipartition_logic_list_per_pass) {
        int sum = sumList(bipartition_logic_list_per_pass);
        int len = bipartition_logic_list_per_pass.size();

        return Math.abs(sum) == (len - 1); //if ALL (except one of 'em) are -1 or ALL (except one of 'em) are +1 then this bipartition is a singleton bipartition
    }
}
