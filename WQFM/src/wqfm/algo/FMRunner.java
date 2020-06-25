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
        CustomDSPerLevel customDS = new CustomDSPerLevel();
        runner.readFileAndPopulateInitialTables(Main.INPUT_FILE_NAME, customDS, initialTable);
        System.out.println("Reading from file <" + Main.INPUT_FILE_NAME + "> done.\nDone populating & sorting initial tables.");
        //sort and populate in divide-and-conquer function so that it will keep on happening on each input recieved.

        /*customDS.sortQuartetIndicesMap();
        customDS.fillRelevantQuartetsMap();
        customDS.fillUpTaxaList();*/
//        customDS.printCustomDS(); //PRINTING FOR DEBUG
        int level = 0;
//        TreeHandler treeHandler = new TreeHandler(); // maybe static utilites functions won't cause problems
        String final_tree = runner.recursiveDivideAndConquer(customDS, level, initialTable); //customDS will have (P, Q, Q_relevant etc) all the params needed.

        System.out.println("\n\n------- Line 49 of FMRunner.java final tree return -----------");
        System.out.println(final_tree);
        System.out.println("---------------------------------------------------------------");
    }

    // ------>>>> Main RECURSIVE function ....
    private String recursiveDivideAndConquer(CustomDSPerLevel customDS_this_level, int level, InitialTable initialTable) {
        /*So that when customDS is passed subsequently, automatic sorting will be done. No need to do it somewhere else*/
        if (level == 0) { //only do this during level 0 [at the START]
            customDS_this_level.setInitialTableReference(initialTable); //change reference of initial table.
        }
        customDS_this_level.sortQuartetIndicesMap(); //sort the quartet-index map for initial-bipartition-computation
        customDS_this_level.fillRelevantQuartetsMap(); //fill-up the relevant quartets per taxa map
        if (level == 0) { //only do it for the initial step, other levels will be passed as parameters
            customDS_this_level.fillUpTaxaList(); //fill-up the taxa list
        }
//        System.out.println("==== ------ ====== ------ Starting DNC level = " + level);
//        System.out.println(">>>> Map-relevant-qrts = " + customDS_this_level.map_taxa_relevant_quartet_indices);
//        System.out.println(">>>> List<Integer> qrts-indices = " + customDS_this_level.quartet_indices_list_unsorted);
//        System.out.println(">>> TAXA LIST = " + customDS_this_level.taxa_list_string);

        /////////////////// TERMINATING CONDITIONS \\\\\\\\\\\\\\\\\\\\\\\\
        // |P| <= 3 OR |Q|.isEmpty() ... return star over taxa list{P}
        if ((customDS_this_level.taxa_list_string.size() <= 3) || (customDS_this_level.quartet_indices_list_unsorted.isEmpty())) {
            //static method ... [utility method, so maybe threads won't create an issue here] [if issue created, just pass an object]
            String starTree = TreeHandler.getStarTree(customDS_this_level.taxa_list_string);
//            System.out.println("-->>RETURNING level = " + level + " , tree = " + starTree);
            return starTree;
        }

        level++; // For dummy node finding.
        InitialBipartition initialBip = new InitialBipartition();
        Map<String, Integer> mapInitialBipartition = initialBip.getInitialBipartitionMap(customDS_this_level);

//        System.out.println("Printing Initial Bipartition for level " + level);
//        InitialBipartition.printBipartition(mapInitialBipartition);
        Bipartition_8_values initialBip_8_vals = new Bipartition_8_values();
        initialBip_8_vals.compute8ValuesUsingAllQuartets(customDS_this_level, mapInitialBipartition);

        FMComputer fmComputerObject = new FMComputer(customDS_this_level, mapInitialBipartition, initialBip_8_vals, level);
        FMResultObject fmResultObject = fmComputerObject.run_FM_Algorithm_Whole();

        CustomDSPerLevel customDS_left = fmResultObject.customDS_left_partition;
        CustomDSPerLevel customDS_right = fmResultObject.customDS_right_partition;

        //Debug printing begin
        //        System.out.println("-------------- After Level " + level + " LEFT Quartets -------------------- ");
        //        System.out.println(customDS_left.onlyQuartetIndices());
        //        System.out.println(customDS_left.taxa_list_string);
        //        System.out.println("============== After Level " + level + " RIGHT Quartets ==================== ");
        //        System.out.println(customDS_right.onlyQuartetIndices());
        //        System.out.println(customDS_right.taxa_list_string);
        //        System.out.println("++++++++++++++ After Level " + level + " Quartet lists ++++++++++++++++++++ ");
        //        customDS_this_level.initial_table1_of_list_of_quartets.printQuartetList();
        //Debug printing end
        /////////////////// Beginning of Recursion \\\\\\\\\\\\\\\\\\\\\\\\\\\
        String dummyTaxon = fmResultObject.dummyTaxonThisLevel;
        String left_tree_unrooted = recursiveDivideAndConquer(customDS_left, level, initialTable);
        String right_tree_unrooted = recursiveDivideAndConquer(customDS_right, level, initialTable);
        String merged_tree = TreeHandler.mergeUnrootedTrees(left_tree_unrooted, right_tree_unrooted, dummyTaxon);
        return merged_tree;
    }

    //------------------Initial Bipartition --------------------
    private void populatePerInputLine(CustomDSPerLevel customDS, InitialTable initialTable, String line) {
        Quartet quartet = new Quartet(line);
        initialTable.addToListOfQuartets(quartet); //add to initial-quartets-single-list
        int idx_qrt_in_table_1 = initialTable.sizeTable() - 1; //size - 1 is the last index
        customDS.quartet_indices_list_unsorted.add(idx_qrt_in_table_1);
    }

    //BufferedReader is used here since BufferedReader is faster than Scanner.readLine
    public void readFileAndPopulateInitialTables(String inputFileName, CustomDSPerLevel customDS, InitialTable initialTable) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(inputFileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
//                    System.out.println(line);
                    populatePerInputLine(customDS, initialTable, line);
                }
            }
        } catch (IOException ex) {
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
