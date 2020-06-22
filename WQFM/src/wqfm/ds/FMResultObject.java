package wqfm.ds;

import java.util.List;
import javafx.util.Pair;

/**
 *
 * @author mahim
 */
public class FMResultObject {
    /*Will contain
        GIVEN as INPUT
        1. logical_bipartition
        2. Initial List<String> taxa_list_initial [same length as above]
        3. List<row,col> of all-quartets 
                [NEED to iterate through all to find relevant deferred and blank quartets] to put to Q_left and Q_right]
    
        TO RETURN as OUTPUT
        1. Q_left  <row,col> list
        2. Q_right <row,col> list
        3. P_left  List<String>
        4. P_right List<String>
    */
    
    // Inputs
    private final List<Integer> logical_bipartition;
    private final List<String> taxa_list_initial;
    private final List<Pair<Integer, Integer>> quartets_list_initial;

    //TO return as outputs
    public List<String> taxa_left_partition;
    public List<String> taxa_right_partition;
    public List<Pair<Integer, Integer>> quartets_list_indices_left_partition;
    public List<Pair<Integer, Integer>> quartets_list_indices_right_partition;
    
    
    public FMResultObject(List<Integer> logical_bipartition, List<String> taxa_list_initial, List<Pair<Integer, Integer>> quartets_list_initial) {
        this.logical_bipartition = logical_bipartition;
        this.taxa_list_initial = taxa_list_initial;
        this.quartets_list_initial = quartets_list_initial;
    }
    
    public void createFMResultObject()
    {
        //Use computations to obtian the above outputs.
    }
    

    
}
