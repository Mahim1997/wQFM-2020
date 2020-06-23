package wqfm.main;

import wqfm.Status;
import wqfm.algo.Runner;
import wqfm.testFunctions.TestNormalFunctions;

/**
 *
 * @author mahim
 */
public class Main {

    public static int REROOT_MODE = Status.REROOT_USING_JAR;
    public static int PARTITION_SCORE_MODE = Status.PARTITION_SCORE_MODE_1; //1->[s]-[v], 2->[s]-0.5[v], 3->[s]-[v]-[d]
    
    public static String INPUT_FILE_NAME = "wqrts_avian_2X_R1";
//    public static String INPUT_FILE_NAME = "wqrts_15G_100g100b_R1";
//    public static String INPUT_FILE_NAME = "wqrts_11Tax_est_5G_R1";
//    public static String INPUT_FILE_NAME = "wqrts_toy_dataset_QFM_paper";
//    public static String INPUT_FILE_NAME = "wqrts_toy_dataset_QFM_paper_dummy_weights";
    public static double SMALLEPSILON = 0; //very small number to take the place of 0

    public static void main(String[] args) {

        long time_1 = System.currentTimeMillis();

//        TestNormalFunctions.testMapFirstKeyValues();
        
        
        Runner.runFunctions(); //main functions for wQFM

        long time_del = System.currentTimeMillis() - time_1;
        System.out.println("\n\n\nTime (ms) = " + time_del);

    }

}
