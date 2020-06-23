package wqfm;

import wqfm.testFunctions.TestNormalFunctions;

/**
 *
 * @author mahim
 */
public class Main {
    public static int REROOT_MODE = Status.REROOT_USING_JAR;
    
//    public static String INPUT_FILE_NAME = "wqrts_avian_2X_R1";
//    public static String INPUT_FILE_NAME = "wqrts_15G_100g100b_R1";
//    public static String INPUT_FILE_NAME = "wqrts_11Tax_est_5G_R1";
    public static String INPUT_FILE_NAME = "wqrts_toy_dataset_QFM_paper";

    public static void main(String[] args) {

        long time_1 = System.currentTimeMillis();

        TestNormalFunctions.testRerootJarFunctions(1); //arg: how many times to check in  loop

//        Runner.runFunctions(); //main functions for wQFM

        long time_del = System.currentTimeMillis() - time_1;
        System.out.println("\n\n\nTime (ms) = " + time_del);

    }

}
