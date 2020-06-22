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
        for(int i=0; i<arr.length; i++){
            sum += arr[i];
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

    
}
