package wqfm.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import wqfm.Status;
import wqfm.utils.Utils;
/**
 *
 * @author mahim
 */
public class FMResultObject {

    public CustomDSPerLevel customDS_left_partition;
    public CustomDSPerLevel customDS_right_partition;
    
    private final int level;
    
    private final CustomDSPerLevel initial_customDS_this_level;
    
    public FMResultObject(CustomDSPerLevel customDS_this_level, int level) {
        this.level = level;
        //pass the reference of initial table to both left & right partitions.
        this.customDS_left_partition = new CustomDSPerLevel(customDS_this_level.table1_initial_table_of_quartets);
        this.customDS_right_partition = new CustomDSPerLevel(customDS_this_level.table1_initial_table_of_quartets);
        this.initial_customDS_this_level = customDS_this_level;
    }

    public void createFMResultObjects(Map<String, Integer> mapOfBipartition) {

        System.out.println("-->In createFMResultObject.... bipartition is " + mapOfBipartition);
        
        
        
        
        //1. Traverse each quartet, find the deferred and blank quartets and pass to next.
        for(int i=0; i<this.initial_customDS_this_level.quartet_indices_list_unsorted.size(); i++){
            int qrt_idx = this.initial_customDS_this_level.quartet_indices_list_unsorted.get(i); //add to new lists of customDS
            Quartet q = this.initial_customDS_this_level.table1_initial_table_of_quartets.get(qrt_idx);
        
            
        }
        
        
        
    }


}
