package wqfm.main;

import wqfm.configs.Config;
import wqfm.algo.FMRunner;
import wqfm.feature.Bin;
import wqfm.testFunctions.TestNormalFunctions;
//import wqfm.testFunctions.TestNormalFunctions;
import wqfm.utils.Helper;
import wqfm.utils.TreeHandler;
import wqfm.configs.DefaultValues;
import wqfm.utils.AnnotationsHandler;

/**
 *
 * @author mahim
 */
public class Main {

    public static String PYTHON_ENGINE = "python"; // or python3
    public static String SPECIES_TREE_FILE_NAME = Config.OUTPUT_FILE_NAME;

    public static void main(String[] args) {
        Main.runwQFM();
    }

    private static void runwQFM() {
        System.out.println("================= **** ======================== **** ====================");
        long time_1 = System.currentTimeMillis(); //calculate starting time

        Bin.WILL_DO_DYNAMIC = false; //set to dynamic=true //SHOULD BE KEPT TRUE.
        Config.PARTITION_SCORE_MODE = DefaultValues.PARITTION_SCORE_COMMAND_LINE;
        Config.ANNOTATIONS_LEVEL = DefaultValues.ANNOTATIONS_LEVEL0_NONE;

        Main.testIfRerootWorks();

        String tree = FMRunner.runFunctions(); //main functions for wQFM
        AnnotationsHandler.handleAnnotations(tree);
//        TestNormalFunctions.testInitialBipartitionFunctions();

        long time_del = System.currentTimeMillis() - time_1;
        long minutes = (time_del / 1000) / 60;
        long seconds = (time_del / 1000) % 60;
        System.out.format("\nTime taken = %d ms ==> %d minutes and %d seconds.\n", time_del, minutes, seconds);
        System.out.println("================= **** ======================== **** ====================");
    }

    private static void testIfRerootWorks() {
        try {
            //Test a dummy reroot function. To check if "lib" is in correct folder.
            String newickTree = "((3,(1,2)),((6,5),4));";
            String outGroupNode = "5";
            String rerootTree = TreeHandler.rerootTree(newickTree, outGroupNode);
        } catch (Exception e) {
            System.out.println("Reroot not working, check if lib is in correct folder. Exiting.");
            System.exit(-1);
        }
    }

}
