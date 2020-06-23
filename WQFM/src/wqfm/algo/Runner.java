package wqfm.algo;

import wqfm.algo.FMComputer;
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
import wqfm.ds.CustomInitTables;
import wqfm.ds.Quartet;
import wqfm.main.Main;

/**
 *
 * @author mahim
 */
public class Runner {

    //Main method to run all functions ... [ABSTRACT everything from Main class]
    public static void runFunctions() {
//        TestNormalFunctions.testDoubleListSort();
//        TestNormalFunctions.testListCopy();
//        TestNormalFunctions.testListIsInFunction();

        mainMethod();

    }

    private static void mainMethod() {
        Runner runner = new Runner();
        CustomInitTables customDS = runner.readFileAndPopulateInitialTables(Main.INPUT_FILE_NAME);
        System.out.println("Reading from file <" + Main.INPUT_FILE_NAME + "> done.\nDone populating & sorting initial tables.");
        customDS.sortTable1();
        customDS.fillRelevantQuartetsMap();
        customDS.updateTable2Map(); // IS IT actually NEEDED ??
//        customDS.printCustomDS();
        ///// Now pass to recursive divide and conquer function.
        List<String> list_taxa = new ArrayList<>(customDS.map_taxa_relevant_quartet_indices.keySet()); // obtain initial list of taxa
        int level = 0;
        
        Map<Pair<Integer, Integer>, Boolean> map_quartets_indices = new HashMap<>();
        for(int row=0; row<customDS.table1_quartets_double_list.size(); row++){
            for(int col=0; col<customDS.table1_quartets_double_list.get(row).size(); col++){
                map_quartets_indices.put(new Pair(row, col), Boolean.FALSE); //dummy boolean value
            }
        }
        
        /*List<Pair<Integer, Integer>> list_quartets_as_pair = new ArrayList<>();
        for (int rowIdx = 0; rowIdx < customDS.table1_quartets_double_list.size(); rowIdx++) {
            List<Quartet> quartets_cols = customDS.table1_quartets_double_list.get(rowIdx);
            for (int colIdx = 0; colIdx < quartets_cols.size(); colIdx++) {
                list_quartets_as_pair.add(new Pair(rowIdx, colIdx));
            }
        }*/
        
        runner.recursiveDivideAndConquer(customDS, level,
                list_taxa, map_quartets_indices); //call the recursive DNC function
    }

    // ------>>>> Main RECURSIVE function ....
    private String recursiveDivideAndConquer(CustomInitTables customDS,
            int level, List<String> list_taxa_string,
            Map<Pair<Integer, Integer>, Boolean> map_quartet_indices) {
//        System.out.println("-->>Inside recursiveDNC() function ... of Runner.java LINE 57");
//
//        List<Integer> initial_logical_partition_list = getInitialBipartitionMap(customDS, level, list_taxa_string, list_quartets_indices);
        InitialBipartition initialBip = new InitialBipartition();
        Map<String, Integer> mapInitialBipartition = initialBip.getInitialBipartitionMap(customDS, 
                list_taxa_string, map_quartet_indices);

        System.out.println("Printing Initial Bipartition");
        InitialBipartition.printBipartition(list_taxa_string, mapInitialBipartition);

        //Debugging ... for singleton bipartition list ... [TO DO]
        Bipartition_8_values initialBip_8_vals = new Bipartition_8_values();
        initialBip_8_vals.compute8ValuesUsingAllQuartets(customDS, list_taxa_string, map_quartet_indices, mapInitialBipartition);
//        System.out.println("Printing initial_bipartitions_8values:\n" + initialBip_8_vals.toString());

        FMComputer fmComputerObject = new FMComputer(customDS, list_taxa_string, map_quartet_indices,
                mapInitialBipartition, initialBip_8_vals, level);
        fmComputerObject.run_FM_Algorithm_Whole();
        level++; // ????

        /*  CustomInitTables.getDummyTaxonName(level) returns a dummy taxon with this level.
            TO DO HERE .... recursive-DNC function
            1. SET INTIIAL RETURN CONDITIONS ...
            2. Initial bipartition should return a logical bipartition i.e. list_integer -1:left, 0:unassigned, +1:right
            3. FM-iteration algorithm ->    should return a logical bipartition i.e. the above list AND
                                            a map of LEFT_OR_RIGHT_INTEGER[0/1]: list< pair<int,int> >
                                            i.e. key: 0/1 [left/right] AND value: list of <r,c> i.e. list<quartets> 
                                            i.e. for each pair in map[0] will be list<(r,c)> for left_bipartition_quartets 
                                                and map[1] will have list<(r,c)> for right_bipartition_quartets
        ***** In FM-iteration algorithm, dummy taxa WILL be added before returning the quartets by passing the level parameter
        eg. FM-iteration(customDS, List<Integer> initial_bipartition_logical, List<Pair<int,int>>list_quartets, int level); can be the signature    
            4. Use P_left, Q_left and P_right, Q_right to recursively call the function be adjusting params eg. level++, etc.
            level++;
            tree_left = recursiveDivideAndConquer(customDS, level, taxa_left_partition, list_quartetes_left_partition)
            tree_right = recursiveDivideAndConquer(customDS, level, taxa_right_partition, list_quartetes_right_partition)
            String dummy_Taxon_this_level = CustomInitTables.getDummyTaxon(level - 1); //one-step before since we have incremented level
            tree_left_rooted = Reroot.rerootTree_python(tree_left, dummy_Taxon_this_level); // left tree will be just as is
            tree_right_rooted = Reroot.rerootTree_python(tree_right, dummy_Taxon_this_level).reverse(); //right tree should be reversed to maintain same face with left rooted tree. 
            merged_tree = merge_by_removing_dummy_and_bracket_balance(tree_left_rooted, tree_right_rooted);
            return merged_tree;
         */
        return null;
    }

    //------------------Initial Bipartition : ZAHIN----------------- [June 21, Morning]
    private void populatePerInputLine(CustomInitTables customDS, String line) {
        // Only populate table 1 [WILL SORT IT LATER]
//        System.out.println("-->>Populating for line = " + line + " customDS = " + customDS.toString());
        Quartet quartet = new Quartet(line);
        int row_idx_table_1; //Use this as marker to represent current quartet ... put in map for <taxa, list(r,c)> of relevant_quartets_per_taxa_table3
        //Populate table 2 <USE MAP> [ weight,index_of_table_1_weight ]
        if (customDS.table2_map_weight_indexQuartet.containsKey(quartet.weight) == false) { //First time, THIS weight value is found ...
            row_idx_table_1 = customDS.table1_quartets_double_list.size(); // AT THIS moment, no need to subtract 1, since this is BEFORE putting new array_list for columns
            customDS.table2_map_weight_indexQuartet.put(quartet.weight, row_idx_table_1); //Key doesn't exist, so, THIS ROW (i.e. weight) in table 1 DOES NOT exist, insert in map
            //Also, initialize table 1's columns' list for THIS weight value (this row)
            customDS.table1_quartets_double_list.add(new ArrayList<>()); // initialize this [since this is the first time THIS weight is found]

        } else { //NOT the first time for THIS weight, THIS weight EXISTS ...  // No need to initalize column's list of table 1.
            row_idx_table_1 = customDS.table2_map_weight_indexQuartet.get(quartet.weight);
        }
        List<Quartet> col_list_for_table1_this_weight = customDS.table1_quartets_double_list.get(row_idx_table_1); // obtain the list.
        col_list_for_table1_this_weight.add(quartet); // ADD THE QUARTET to Table 1.
    }

    //BufferedReader is used here since BufferedReader is faster than Scanner.readLine
    public CustomInitTables readFileAndPopulateInitialTables(String inputFileName) {
        // https://stackoverflow.com/questions/5868369/how-can-i-read-a-large-text-file-line-by-line-using-java
        CustomInitTables customDS = new CustomInitTables();
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

        return customDS;
    }

    //-------------------------------------------------------------------------------------------------------------
}
