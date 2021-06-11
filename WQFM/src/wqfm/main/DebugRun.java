package wqfm.main;

import wqfm.configs.Config;
import wqfm.configs.DefaultValues;
import wqfm.feature.Bin;

/**
 *
 * @author mahim
 */
public class DebugRun {

    public static void main(String[] args) {
        Config.INPUT_FILE_NAME = "input_files/weighted_quartets_avian_biological_dataset";
        Config.OUTPUT_FILE_NAME = "test-output.tre";
        
        Main.SPECIES_TREE_FILE_NAME = Config.OUTPUT_FILE_NAME; // initially set as same.
        Config.PARTITION_SCORE_MODE = DefaultValues.PARITTION_SCORE_COMMAND_LINE;
        
        Bin.WILL_DO_DYNAMIC = false;
        
        String tree = Main.runwQFM();
        System.out.println(tree);
    }
}
