package wqfm.algo;

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
public class Runner {

    //Main method to run all functions ... [ABSTRACT everything from Main class]
    public static void runFunctions() {
        mainMethod();
    }

    private static void mainMethod() {
        Runner runner = new Runner();
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
        System.out.println(final_tree);
    }

    // ------>>>> Main RECURSIVE function ....
    private String recursiveDivideAndConquer(CustomDSPerLevel customDS, int level,
            TreeHandler treeHandler) {
        
        /*
            Handle terminating conditions.
        */
        if(level == 1){ //level 1 for initial checking.
            return "NOT_RETURING_ANYTHING_NOW";
        }


        //Sort things and fill up relevant quartets map and taxa list and other required things.
        /*So that when customDS is passed subsequently, automatic sorting will be done. No need to do it somewhere else*/
        customDS.sortQuartetIndicesMap(); //sprt the quartet-index map for initial-bipartition-computation
        customDS.fillRelevantQuartetsMap(); //fill-up the relevant quartets per taxa map
        customDS.fillUpTaxaList(); //fill-up the taxa list (using the above map)
        
        InitialBipartition initialBip = new InitialBipartition();
        Map<String, Integer> mapInitialBipartition = initialBip.getInitialBipartitionMap(customDS);

        System.out.println("Printing Initial Bipartition");
        InitialBipartition.printBipartition(customDS.list_taxa_string, mapInitialBipartition);

        Bipartition_8_values initialBip_8_vals = new Bipartition_8_values();
        initialBip_8_vals.compute8ValuesUsingAllQuartets(customDS, mapInitialBipartition);
////        System.out.println("Printing initial_bipartitions_8values:\n" + initialBip_8_vals.toString());
        FMComputer fmComputerObject = new FMComputer(customDS, mapInitialBipartition, initialBip_8_vals, level);
        FMResultObject resultObject = fmComputerObject.run_FM_Algorithm_Whole();
        level++; // ????
        
        
        CustomDSPerLevel customDS_left = resultObject.customDS_left_partition;
        CustomDSPerLevel customDS_right = resultObject.customDS_right_partition;
        String left_tree_unrooted = recursiveDivideAndConquer(customDS_left, level, treeHandler);
        String right_tree_unrooted = recursiveDivideAndConquer(customDS_right, level, treeHandler);
        String dummyTaxon = Utils.getDummyTaxonName(level);
        String merged_tree = TreeHandler.mergeUnrootedTrees(left_tree_unrooted, right_tree_unrooted,
                dummyTaxon);
        return null;
    }

    //------------------Initial Bipartition : ZAHIN----------------- [June 21, Morning]
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
