package wqfm.ds;

import java.util.List;

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


    
}
