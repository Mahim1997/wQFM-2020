package wqfm.algo;

import wqfm.bip.InitialBipartition;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import wqfm.bip.Bipartition_8_values;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.FMResultObject;
import wqfm.ds.InitialTable;
import wqfm.ds.Quartet;
import wqfm.main.Main;
import wqfm.utils.TreeHandler;
import wqfm.utils.Utils;

/**
 *
 * @author mahim
 */
public class FMRunner {

    //Main method to run all functions ... [ABSTRACT everything from Main class]
    public static void runFunctions() {
        mainMethod();
    }

    private static void mainMethod() {
        FMRunner runner = new FMRunner();
        InitialTable initialTable = new InitialTable();
        CustomDSPerLevel customDS = new CustomDSPerLevel(initialTable);
        runner.readFileAndPopulateInitialTables(Main.INPUT_FILE_NAME, customDS);
        System.out.println("Reading from file <" + Main.INPUT_FILE_NAME + "> done.\nDone populating & sorting initial tables.");
        //sort and populate in divide-and-conquer function so that it will keep on happening on each input recieved.

        /*customDS.sortQuartetIndicesMap();
        customDS.fillRelevantQuartetsMap();
        customDS.fillUpTaxaList();*/
//        customDS.printCustomDS(); //PRINTING FOR DEBUG
        int level = 0;
        TreeHandler treeHandler = new TreeHandler(); // everything object so that multi-threading can be done
        String final_tree = runner.recursiveDivideAndConquer(customDS, level, treeHandler); //customDS will have (P, Q, Q_relevant etc) all the params needed.

        System.out.println("\n\n------- Line 49 of FMRunner.java final tree return -----------");
        System.out.println(final_tree);
        System.out.println("---------------------------------------------------------------");
    }

    // ------>>>> Main RECURSIVE function ....
    private String recursiveDivideAndConquer(CustomDSPerLevel customDS, int level,
            TreeHandler treeHandler) {

        //Sort things and fill up relevant quartets map and taxa list and other required things.
        /*So that when customDS is passed subsequently, automatic sorting will be done. No need to do it somewhere else*/
        customDS.sortQuartetIndicesMap(); //sort the quartet-index map for initial-bipartition-computation
        customDS.fillRelevantQuartetsMap(); //fill-up the relevant quartets per taxa map

        if(level == 0){ //only do it for the initial step, other levels will be passed as parameters
            customDS.fillUpTaxaList(); //fill-up the taxa list
        }
//        (THIS should be called from outside as we can have |Q| = 0 but |P| > 0)
        /*
            Handle terminating conditions.
            1. |P| == 0 then return "()"
            2. |P| <= 3, then return a star/depth-one-tree over set of taxon.
            3. |Q| = empty, then return a star/depth-one-tree over set of taxon.
         */
        //if |P| == 0
        
        if(customDS.quartet_indices_list_unsorted.isEmpty()){ // if |Q| == 0
//            return treeHandler.getDepthOneTree(customDS)
        }
        if (level == 1) { //level 1 for initial checking.
            return "NOT_RETURING_ANYTHING_NOW";
        }

        InitialBipartition initialBip = new InitialBipartition();
        Map<String, Integer> mapInitialBipartition = initialBip.getInitialBipartitionMap(customDS);

        System.out.println("Printing Initial Bipartition");
        InitialBipartition.printBipartition(mapInitialBipartition);

        Bipartition_8_values initialBip_8_vals = new Bipartition_8_values();
        initialBip_8_vals.compute8ValuesUsingAllQuartets(customDS, mapInitialBipartition);
////        System.out.println("Printing initial_bipartitions_8values:\n" + initialBip_8_vals.toString());
        FMComputer fmComputerObject = new FMComputer(customDS, mapInitialBipartition, initialBip_8_vals, level);
        FMResultObject fmResultObject = fmComputerObject.run_FM_Algorithm_Whole();

        CustomDSPerLevel customDS_left = fmResultObject.customDS_left_partition;
        CustomDSPerLevel customDS_right = fmResultObject.customDS_right_partition;

        System.out.println("-------------- After Level " + level + " LEFT Quartets -------------------- ");
        System.out.println(customDS_left.onlyQuartetIndices());
        System.out.println(customDS_left.set_taxa_string);
        System.out.println("============== After Level " + level + " RIGHT Quartets ==================== ");
        System.out.println(customDS_right.onlyQuartetIndices());
        System.out.println(customDS_right.set_taxa_string);
        System.out.println("++++++++++++++ After Level " + level + " Quartet lists ++++++++++++++++++++ ");
        customDS.table1_initial_table_of_quartets.printQuartetList();
        level++; // ????

        //////////////////// Beginning of Recursion \\\\\\\\\\\\\\\\\\\\\\\\\\\
//        String dummyTaxon = resultObject.dummyTaxonThisLevel;
//        String left_tree_unrooted = recursiveDivideAndConquer(customDS_left, level, treeHandler);
//        String right_tree_unrooted = recursiveDivideAndConquer(customDS_right, level, treeHandler);
//        String dummyTaxon = Utils.getDummyTaxonName(level);
//        String merged_tree = TreeHandler.mergeUnrootedTrees(left_tree_unrooted, right_tree_unrooted,
//                dummyTaxon)
        return null;
    }

    //------------------Initial Bipartition --------------------
    private void populatePerInputLine(CustomDSPerLevel customDS, String line) {
        Quartet quartet = new Quartet(line);
        customDS.table1_initial_table_of_quartets.addToListOfQuartets(quartet); //add to initial-quartets-single-list
        int idx_qrt_in_table_1 = customDS.table1_initial_table_of_quartets.sizeTable() - 1; //size - 1 is the last index
        customDS.quartet_indices_list_unsorted.add(idx_qrt_in_table_1);
    }

    //BufferedReader is used here since BufferedReader is faster than Scanner.readLine
    public void readFileAndPopulateInitialTables(String inputFileName, CustomDSPerLevel customDS) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(inputFileName);
            //specify UTF-8 encoding explicitly
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
//                    System.out.println(line);
                    populatePerInputLine(customDS, line);
                }
            }

        } catch (Exception ex) {
            System.out.println("ERROR READING FILE <" + inputFileName + ">. EXITING SYSTEM");
            System.exit(-1);

        } finally {
            try {
                fileInputStream.close();
            } catch (IOException ex) {
                System.out.println("ERROR IN CLOSING fileInputStream while reading file. Exiting");
                System.exit(-1);
            }
        }

    }

    //-------------------------------------------------------------------------------------------------------------
}
