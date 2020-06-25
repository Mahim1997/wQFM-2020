package wqfm.main;

import wqfm.interfaces.Status;
import wqfm.algo.FMRunner;
import wqfm.testFunctions.TestNormalFunctions;
import wqfm.testFunctions.TestThreadFunctions;
import wqfm.utils.Helper;

/**
 *
 * @author mahim
 */
public class Main {
//    public static String INPUT_FILE_NAME = "wqrts_toy_dataset_QFM_paper_dummy_weights";

    public static int REROOT_MODE = Status.REROOT_USING_JAR;
    public static int PARTITION_SCORE_MODE = Status.PARTITION_SCORE_MODE_1; //1->[s]-[v], 2->[s]-0.5[v], 3->[s]-[v]-[d]

//    public static String INPUT_FILE_NAME = "input_files/wqrts_avian_2X_R1";
//    public static String INPUT_FILE_NAME = "input_files/wqrts_37Tax_noscale_800g_500b_R1";
    public static String INPUT_FILE_NAME = "input_files/wqrts_15G_100g100b_R1";
//    public static String INPUT_FILE_NAME = "input_files/wqrts_11Tax_est_5G_R1";
//    public static String INPUT_FILE_NAME = "input_files/wqrts_toy_dataset_QFM_paper";

//    public static String INPUT_FILE_NAME = "test-input-file-wqrts-java";
    public static String OUTPUT_FILE_NAME = "test-output-file-wqfm-java.tre";

    public static boolean DEBUG_MODE_TESTING = false;
    public static double SMALLEPSILON = 0.00001; //if cumulative gain of iteration < this_num then stop
    public static int MAX_ITERATIONS_LIMIT = 10000000; //can we keep it as another stopping-criterion ?

    public static void main(String[] args) {
        System.out.println("================= **** ======================== **** ====================");
        readArguments(args); //initial arguments processing
        long time_1 = System.currentTimeMillis(); //calculate starting time
        
        FMRunner.runFunctions(); //main functions for wQFM
        long time_del = System.currentTimeMillis() - time_1;
        long minutes = (time_del / 1000) / 60;
        long seconds = (time_del / 1000) % 60;
        System.out.format("\nTime taken = %d ms ==> %d minutes and %d seconds.\n", time_del ,minutes, seconds);
        System.out.println("================= **** ======================== **** ====================");
    }

    private static void readArguments(String[] args) {
        //For now <input-file> <output-file> <partition-score>
        Helper.findOptionsUsingCommandLineArgs(args);
    }

}
