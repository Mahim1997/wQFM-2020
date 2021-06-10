package wqfm.main;

import wqfm.configs.Config;
import wqfm.feature.Bin;

/**
 *
 * @author mahim
 */
public class Debug {
    
    public static void main(String[] args) {
        Bin.WILL_DO_DYNAMIC = false;
        
        Config.INPUT_FILE_NAME = "input_files/DEBUG_WQRTS";
        Config.OUTPUT_FILE_NAME = "OUTPUT_DEBUG_TREE";

        Main.SPECIES_TREE_FILE_NAME = Config.OUTPUT_FILE_NAME;
        Main.runwQFM();
    }
}
