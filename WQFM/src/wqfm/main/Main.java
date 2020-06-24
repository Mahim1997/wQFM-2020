package wqfm.main;

import wqfm.Status;
import wqfm.algo.FMRunner;
import wqfm.testFunctions.TestNormalFunctions;
import wqfm.testFunctions.TestThreadFunctions;

/**
 *
 * @author mahim
 */
public class Main {
//    public static String INPUT_FILE_NAME = "wqrts_toy_dataset_QFM_paper_dummy_weights";
    public static int REROOT_MODE = Status.REROOT_USING_JAR;
    public static int PARTITION_SCORE_MODE = Status.PARTITION_SCORE_MODE_1; //1->[s]-[v], 2->[s]-0.5[v], 3->[s]-[v]-[d]
   
    public static String INPUT_FILE_NAME = "input_files/wqrts_avian_2X_R1";
//    public static String INPUT_FILE_NAME = "input_files/wqrts_37Tax_noscale_800g_500b";
//    public static String INPUT_FILE_NAME = "input_files/wqrts_15G_100g100b_R1";
//    public static String INPUT_FILE_NAME = "input_files/wqrts_11Tax_est_5G_R1";
//    public static String INPUT_FILE_NAME = "input_files/wqrts_toy_dataset_QFM_paper";

    
    public static double SMALLEPSILON = 0.00001; //if cumulative gain of iteration < this_num then stop
    public static int MAX_ITERATIONS_LIMIT = 10000000; //can we keep it as another stopping-criterion ?

    public static void main(String[] args) {

        long time_1 = System.currentTimeMillis();
        
        
        FMRunner.runFunctions(); //main functions for wQFM
//        TestThreadFunctions.testFunction();

        long time_del = System.currentTimeMillis() - time_1;
        System.out.println("\n\n\nTime (ms) = " + time_del);

    }

}
