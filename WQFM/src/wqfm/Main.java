package wqfm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import phylonet.tree.io.ParseException;
import phylonet.tree.util.Trees;
import phylonet.tree.model.sti.STITree;
import wqfm.testFunctions.TestNormalFunctions;

/**
 *
 * @author mahim
 */
public class Main {

//    public static String INPUT_FILE_NAME = "wqrts_avian_2X_R1";
//    public static String INPUT_FILE_NAME = "wqrts_15G_100g100b_R1";
//    public static String INPUT_FILE_NAME = "wqrts_11Tax_est_5G_R1";
    public static String INPUT_FILE_NAME = "wqrts_toy_dataset_QFM_paper";

    public static void main(String[] args) {

        long time_1 = System.currentTimeMillis();

        for (int i = 0; i < 89; i++) {
            String newickTree = "((3,(1,2)),((6,5),4));";
            String outGroupNode = "5";
            System.out.println(i + "->> " + RerootTree.rerootTree_JAR(newickTree, outGroupNode));
        }
//        Runner.runFunctions();

        long time_del = System.currentTimeMillis() - time_1;
        System.out.println("\n\n\nTime (ms) = " + time_del);

    }

}
