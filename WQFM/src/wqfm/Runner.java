package wqfm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import javafx.util.Pair;
import wqfm.ds.CustomDS;
import wqfm.ds.Quartet;

/**
 *
 * @author mahim
 */
public class Runner {

    //Main method to run all functions ... [ABSTRACT everything from Main class]
    public static void runFunctions() {
        testSortHashMap();
//        mainMethod();
//        testCustomDS();

    }

    private static void mainMethod() {
        List<String> lines = readFile(Main.INPUT_FILE_NAME);
        System.out.println("Reading from file <" + Main.INPUT_FILE_NAME + "> done");

        Runner runner = new Runner(); //Create object and handle [IF THREAD used later on]
        CustomDS customDS = null;
        try {
            customDS = runner.readFileAndpopulateCustomTables(Main.INPUT_FILE_NAME); // Initial population of custom-datastructure-tables 
        } catch (Exception e) {
            System.out.println("Error reading input from file <" + Main.INPUT_FILE_NAME + "> ... exiting program.");
            System.exit(-1);
        }
        customDS.printCustomDS();
    }


    private CustomDS readFileAndpopulateCustomTables(String inputFileName) throws Exception {
        CustomDS customDS = new CustomDS();
        Scanner sc = new Scanner(new FileInputStream(inputFileName));
        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            Quartet quartet = new Quartet(line);
//            System.out.print(i + ":" + line + "  ");
//            quartet.printQuartet();

            int row_idx_table_1, col_idx_table_1; //Use this as marker to represent current quartet ... put in map for <taxa, list(r,c)> of relevant_quartets_per_taxa_table3
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
            col_list_for_table1_this_weight.add(quartet);
            col_idx_table_1 = customDS.table1_quartets_double_list.get(row_idx_table_1).size() - 1; // Current row's --> columns_list.size - 1 

            //Put in hashMap(relevant_quartetes_per_taxa) for each taxa in the quartet.
            //For left sisters
            for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) {
                String taxa = quartet.taxa_sisters_left[i];
                if (customDS.map_taxa_relevant_quartet_indices.containsKey(taxa) == false) { // doesn't contain THIS taxa in map, so initialize list and put this taxa as key to map.
                    customDS.map_taxa_relevant_quartet_indices.put(taxa, new ArrayList<>()); // initialize the list
                }
                List<Pair<Integer, Integer>> list_indices_of_quartets = customDS.map_taxa_relevant_quartet_indices.get(taxa); // now will exist [since we have initialized previously]
                list_indices_of_quartets.add(new Pair(row_idx_table_1, col_idx_table_1));
            }

            //For right sisters
            for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) {
                String taxa = quartet.taxa_sisters_right[i];
                if (customDS.map_taxa_relevant_quartet_indices.containsKey(taxa) == false) { // doesn't contain THIS taxa in map, so initialize list and put this taxa as key to map.
                    customDS.map_taxa_relevant_quartet_indices.put(taxa, new ArrayList<>()); // initialize the list
                }
                List<Pair<Integer, Integer>> list_indices_of_quartets = customDS.map_taxa_relevant_quartet_indices.get(taxa); // now will exist [since we have initialized previously]
                list_indices_of_quartets.add(new Pair(row_idx_table_1, col_idx_table_1));
            }
        }

        return customDS;
    }
    
// --------------------------------------- TEST METHODS ----------------------------------
    // READ and populate tables at the same function ABOVE...
    private static List<String> readFile(String inputFileName) {
        List<String> lines = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new FileInputStream(inputFileName));
            String s;
            while (sc.hasNextLine()) {
                s = sc.nextLine();
                lines.add(s);
            }
            return lines;
        } catch (FileNotFoundException ex) {
            System.out.println("-->>Exception in reading input-file <" + inputFileName + "> ... exiting");
            System.exit(-1);
        }
        return null;
    }

    private static void testPairDS() {
        Pair<Integer, Integer> pair;
        pair = new Pair(2, 3);
        System.out.println(pair.getKey() + " -> " + pair.getValue());
    }

    private static void testNewickQuartet() {
        String newickQuartet = "((Z,B),(D,C)); 41";
        Quartet quartet = new Quartet(newickQuartet);
//        quartet = new Quartet("A", "B", "D", "C", 41);
        quartet.printQuartet();
    }

    // Using python3 and dendropy ... this reroot_tree_new.py works
    // Command is: python3 reroot_tree_new.py <tree-newick> <outgroup> DON'T FORGET SEMI-COLON
    public static void testRerootFunction() {
        String newickTree = "((3,(1,2)),((6,5),4));";
        String outGroupNode = "5";

        for (int i = 0; i < 1; i++) {
            System.out.print(i + ": ");
            RerootTree.rerootTree_python(newickTree, outGroupNode);
        }

    }

    private static void testHashtableAndHashMap() {
        Hashtable<String, Integer> table = new Hashtable<>();
        HashMap<String, Integer> map = new HashMap<>();

        table.put("Mahim", 22);
        table.put("Zahin", 31);
        table.put("Ronaldo", 7);

        int roll = table.get("Mahim");
        System.out.println(roll);

        map.put("Mahim", 22);
        map.put("Zahin", 31);
        map.put("Ronaldo", 7);

        roll = map.get("Mahim");
        System.out.println(roll);

    }

    private static void testCustomDS() {
        CustomDS customDS = new CustomDS();
        customDS.table1_quartets_double_list.add(new ArrayList<>());
        List<Quartet> get = customDS.table1_quartets_double_list.get(0);
        get.add(new Quartet("A", "B", "C", "D", 10));
        customDS.printCustomDS();

    }
    
    
    //https://www.baeldung.com/java-hashmap-sort
    //https://stackoverflow.com/questions/30842966/how-to-sort-a-hash-map-using-key-descending-order
    private TreeMap<Double,Integer> sortMap(Map<Double, Integer> map){
//        TreeMap<Double, Integer> sorted = new TreeMap<>(map);
        TreeMap<Double, Integer> sorted = new TreeMap<>(Collections.reverseOrder());
        sorted.putAll(map);
        return sorted;
    }
    
    private static void testSortHashMap() {
        Map<Double, Integer> map_to_test = new HashMap<>();
        
        map_to_test.put(22.0, 1);
        map_to_test.put(31.0, 2);
        map_to_test.put(7.0, 3);
        map_to_test.put(100.0, 4);
        map_to_test.put(15.0, 5);
        
        System.out.println("-------- Before Sorting ---------");
        for(double key: map_to_test.keySet()){
            int val = map_to_test.get(key);
            System.out.println("Key = " + key + " , val = " + val);
        }
        Runner runner = new Runner();
        TreeMap<Double, Integer> sortMap = runner.sortMap(map_to_test);
        
        System.out.println("=========== AFTER SORTING ==============");
        for(double key: sortMap.keySet()){
            int val = sortMap.get(key);
            System.out.println("Key = " + key + " , val = " + val);
        }
        
    }

}
