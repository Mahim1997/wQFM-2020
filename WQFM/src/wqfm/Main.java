package wqfm;

/**
 *
 * @author mahim
 */
public class Main {

//    public static String INPUT_FILE_NAME = "wqrts_avian_2X_R1";
    public static String INPUT_FILE_NAME = "wqrts_11Tax_est_5G_R1";
    
    public static void main(String[] args) {

        long time_1 = System.currentTimeMillis();

        Runner.runFunctions();
        
        
        long time_del = System.currentTimeMillis() - time_1;
        System.out.println("Time (ms) = " + time_del);

    }


    

}
