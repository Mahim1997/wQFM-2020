package wqfm.main;

import wqfm.interfaces.Status;
import wqfm.algo.FMRunner;
import wqfm.feature.Bin;
import wqfm.testFunctions.TestNormalFunctions;
//import wqfm.testFunctions.TestNormalFunctions;
import wqfm.utils.Helper;
import wqfm.utils.TreeHandler;

/**
 *
 * @author mahim
 */
public class Main {
    
    public static int BIPARTITION_MODE = Status.BIPARTITION_GREEDY; // BIPARTITION_EXTREME, BIPARTITION_RANDOM, BIPARTITION_GREEDY

    public static boolean DEBUG_DUMMY_NAME = false; //true -> X1, X2 like that & false -> MZCY ... weird name.
    public static int REROOT_MODE = Status.REROOT_USING_JAR;
    public static int PARTITION_SCORE_MODE = Status.PARTITION_SCORE_FULL_DYNAMIC; //0->[s]-[v], 1->[s]-0.5[v], 2->[s]-[v]-[d], 3->3[s]-2[v]

    public static double CUT_OFF_LIMIT_BINNING = 0.1; // use 0.1 [default]
    public static double THRESHOLD_BINNING = 0.9; // use 0.9 [default]

//    public static String INPUT_FILE_NAME = "input_files/best_weighted_quartets_avian_2X_R1_est";
    
//    public static String INPUT_FILE_NAME = "input_files/weighted_quartets_avian_biological_dataset";
    public static String INPUT_FILE_NAME = "input_files/wqrts_11Tax_est_5G_R1";
//   public static String INPUT_FILE_NAME = "input_files/wqrts_15G_100g100b_R1";
//    public static String INPUT_FILE_NAME = "input_files/wqrts_11Tax_est_5G_R1";
//    public static String INPUT_FILE_NAME = "input_files/wqrts_toy_dataset_QFM_paper";
//        public static String INPUT_FILE_NAME = "input_files/weird_dataset_1";

    public static String OUTPUT_FILE_NAME = "test-output-file-wqfm-java.tre";

    public static boolean DEBUG_MODE_TESTING = true;
    public static double SMALLEPSILON = 0.000001; //if cumulative gain of iteration < this_num then stop
    public static int MAX_ITERATIONS_LIMIT = 1000000; //can we keep it as another stopping-criterion ? [100k]
    public static double STEP_SIZE_BINNING = 0.01; //always used 0.01 for experiments (default)
    public static boolean SET_RIGHT_TO_1 = false; //false: dual-bin, true: right will be set to 1.

    public static void main(String[] args) {
        System.out.println("================= **** ======================== **** ====================");
        Helper.findOptionsUsingCommandLineArgs(args); //initial arguments processing
        long time_1 = System.currentTimeMillis(); //calculate starting time
        Bin.WILL_DO_DYNAMIC = true; //set to dynamic=true //SHOULD BE KEPT TRUE.
        Main.testIfRerootWorks();

        FMRunner.runFunctions(); //main functions for wQFM
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
