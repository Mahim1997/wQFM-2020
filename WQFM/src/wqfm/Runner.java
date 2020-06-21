package wqfm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
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

        mainMethod();
//        testCustomDS();

    }

    static void testCustomDS() {
        CustomDS customDS = new CustomDS();
        customDS.table1_quartets_double_list.add(new ArrayList<>());
        List<Quartet> get = customDS.table1_quartets_double_list.get(0);
        get.add(new Quartet("A", "B", "C", "D", 10));
        customDS.printCustomDS();

    }

    private static void mainMethod() {
        List<String> lines = readFile(Main.INPUT_FILE_NAME);
        System.out.println("Reading from file <" + Main.INPUT_FILE_NAME + "> done");

        Runner runner = new Runner(); //Create object and handle [IF THREAD used later on]
        CustomDS customDS = runner.populateCustomTables(lines);
        customDS.printCustomDS();
    }

    private CustomDS populateCustomTables(List<String> lines) {
        CustomDS customDS = new CustomDS();
        String line;
        for (int iter_lines = 0; iter_lines < lines.size(); iter_lines++) {
            line = lines.get(iter_lines);
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
                    customDS.map_taxa_relevant_quartet_indices.put(taxa, new ArrayList<>());
                }
                List<Pair<Integer, Integer>> list_indices_of_quartets = customDS.map_taxa_relevant_quartet_indices.get(taxa); // now will exist [since we have initialized previously]
                list_indices_of_quartets.add(new Pair(row_idx_table_1, col_idx_table_1));
            }

            //For right sisters
            for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) {
                String taxa = quartet.taxa_sisters_right[i];
                if (customDS.map_taxa_relevant_quartet_indices.containsKey(taxa) == false) { // doesn't contain THIS taxa in map, so initialize list and put this taxa as key to map.
                    customDS.map_taxa_relevant_quartet_indices.put(taxa, new ArrayList<>());
                }
                List<Pair<Integer, Integer>> list_indices_of_quartets = customDS.map_taxa_relevant_quartet_indices.get(taxa); // now will exist [since we have initialized previously]
                list_indices_of_quartets.add(new Pair(row_idx_table_1, col_idx_table_1));
            }
        }

        return customDS;
    }

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
    private static void testRerootFunction() {
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
}
