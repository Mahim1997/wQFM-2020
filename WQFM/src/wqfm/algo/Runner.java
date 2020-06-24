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
import wqfm.ds.InitialTable;
import wqfm.ds.Quartet;
import wqfm.main.Main;

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
        runner.recursiveDivideAndConquer(customDS, level); //customDS will have (P, Q, Q_relevant etc) all the params needed.
    }

    // ------>>>> Main RECURSIVE function ....
    private String recursiveDivideAndConquer(CustomDSPerLevel customDS, int level) {
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
        fmComputerObject.run_FM_Algorithm_Whole();
//        level++; // ????

        /*  CustomDSPerLevel.getDummyTaxonName(level) returns a dummy taxon with this level.
            TO DO HERE .... recursive-DNC function
        ***** In FM-iteration algorithm, dummy taxa WILL be added before returning the quartets by passing the level parameter
        eg. FM-iteration(customDS, List<Integer> initial_bipartition_logical, List<Pair<int,int>>list_quartets, int level); can be the signature    
            4. Use P_left, Q_left and P_right, Q_right to recursively call the function be adjusting params eg. level++, etc.
            level++;
            tree_left = recursiveDivideAndConquer(customDS, level, taxa_left_partition, list_quartetes_left_partition)
            tree_right = recursiveDivideAndConquer(customDS, level, taxa_right_partition, list_quartetes_right_partition)
            String dummy_Taxon_this_level = CustomDSPerLevel.getDummyTaxon(level - 1); //one-step before since we have incremented level
            tree_left_rooted = Reroot.rerootTree_python(tree_left, dummy_Taxon_this_level); // left tree will be just as is
            tree_right_rooted = Reroot.rerootTree_python(tree_right, dummy_Taxon_this_level).reverse(); //right tree should be reversed to maintain same face with left rooted tree. 
            merged_tree = merge_by_removing_dummy_and_bracket_balance(tree_left_rooted, tree_right_rooted);
            return merged_tree;
         */
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
