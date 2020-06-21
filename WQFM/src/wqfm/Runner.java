package wqfm;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javafx.util.Pair;
import wqfm.ds.CustomDS;
import wqfm.ds.Quartet;
import wqfm.testFunctions.TestNormalFunctions;

/**
 *
 * @author mahim
 */
public class Runner {

    //Main method to run all functions ... [ABSTRACT everything from Main class]
    public static void runFunctions() {
        TestNormalFunctions.testTreeMapFromPairIntegers();

//        mainMethod();
    }

    private static void mainMethod() {
        Runner runner = new Runner(); //Create object and handle [IF THREAD used later on]
        CustomDS customDS = null;
        try {
            customDS = runner.readFileAndpopulateCustomTables(Main.INPUT_FILE_NAME); // Initial population of custom-datastructure-tables 
            System.out.println("Reading from file <" + Main.INPUT_FILE_NAME + "> done.\nDone populating initial tables");
        } catch (Exception e) {
            System.out.println("Error reading input from file <" + Main.INPUT_FILE_NAME + "> ... exiting program.");
            System.exit(-1);
        }
//        customDS.printCustomDS(); // TURN OFF COMMENT FOR NOT PRINTING CUSTOM DATASTRUCTURE ... 

        List<String> list_taxa = new ArrayList<>(customDS.map_taxa_relevant_quartet_indices.keySet()); // obtain initial list of taxa
        //Call recursive divide and conquer approach.
        int level = 0;
        runner.recursiveDivideAndConquer(customDS, level,
                list_taxa, customDS.table4_quartetes_indices_list); //call the recursive DNC function

    }

    // ------>>>> Main RECURSIVE function ....
    private String recursiveDivideAndConquer(CustomDS customDS,
            int level, List<String> list_taxa_string,
            List<Pair<Integer, Integer>> list_quartets_indices) {
        System.out.println("-->>Inside recursiveDNC() function ... of Runner.java LINE 57");
        
        List<Integer> initial_logical_partition_list = getInitialBipartition(customDS, level, list_taxa_string, list_quartets_indices);
        System.out.println("Printing Initial Bipartition");
        printBipartition(list_taxa_string, initial_logical_partition_list);
        /*  CustomDS.getDummyTaxonName(level) returns a dummy taxon with this level.
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
            String dummy_Taxon_this_level = CustomDS.getDummyTaxon(level - 1); //one-step before since we have incremented level
            tree_left_rooted = Reroot.rerootTree_python(tree_left, dummy_Taxon_this_level); // left tree will be just as is
            tree_right_rooted = Reroot.rerootTree_python(tree_right, dummy_Taxon_this_level).reverse(); //right tree should be reversed to maintain same face with left rooted tree. 
            merged_tree = merge_by_removing_dummy_and_bracket_balance(tree_left_rooted, tree_right_rooted);
            return merged_tree;
         */
        return null;
    }

    //------------------Initial Bipartition : ZAHIN----------------- [June 21, Morning]
    private void printBipartition(List<String> list_taxa_string, List<Integer> partition_list) {
        System.out.print("LEFT: ");
        for (int i = 0; i < partition_list.size(); i++) {
            if (partition_list.get(i) == Status.LEFT_PARTITION) {
                System.out.print(list_taxa_string.get(i) + "-->");
            }
        }
        System.out.print("\nRIGHT: ");
        for (int i = 0; i < partition_list.size(); i++) {
            if (partition_list.get(i) == Status.RIGHT_PARTITION) {
                System.out.print(list_taxa_string.get(i) + "-->");
            }
        }
        System.out.println("");
    }
    //Function to obtain initial (logical) bipartition 
    private List<Integer> getInitialBipartition(CustomDS customDS,
            int level, List<String> list_taxa_string,
            List<Pair<Integer, Integer>> list_quartets_indices) {
        // partition_list is the list which will the partitions of each taxa
        // Status.LEFT_PARTITION : left , 0 : unassigned , +1 : right
        //initiazing partition_list with 0 (all are unassigned)
        List<Integer> partition_list = new ArrayList<>(Collections.nCopies(list_taxa_string.size(), Status.UNASSIGNED));
        int count_taxa_left_partition = 0;
        int count_taxa_right_partition = 0;

        //Need to create a TreeMap for Pair of list_quartetes_indices...
        for (Double key_weight : customDS.table2_map_weight_indexQuartet.keySet()) {
            int rowIDX = customDS.table2_map_weight_indexQuartet.get(key_weight);
            //System.out.println("Key_WEIGHT: <" + key_weight + ">: Value_rowTable2_IDX: " + rowIDX);
            for (int j = 0; j < customDS.table1_quartets_double_list.get(rowIDX).size(); j++) {
                //thus we can get quartets in sorted order (according to weight)
                //   System.out.print(customDS.table1_quartets_double_list.get(rowIDX).get(j) + "  ");
                Quartet quartet_under_consideration = customDS.table1_quartets_double_list.get(rowIDX).get(j);
                String q1 = quartet_under_consideration.taxa_sisters_left[0];
                String q2 = quartet_under_consideration.taxa_sisters_left[1];
                String q3 = quartet_under_consideration.taxa_sisters_right[0];
                String q4 = quartet_under_consideration.taxa_sisters_right[1];
                //get indices of these taxas
                int idx_q1 = list_taxa_string.indexOf(q1);
                int idx_q2 = list_taxa_string.indexOf(q2);
                int idx_q3 = list_taxa_string.indexOf(q3);
                int idx_q4 = list_taxa_string.indexOf(q4);
//                System.out.println(idx_q1);
//                System.out.println(idx_q2);
//                System.out.println(idx_q3);
//                System.out.println(idx_q4);
                //check status of q1,q2,q3,q4
                int status_q1, status_q2, status_q3, status_q4; //status of q1,q2,q3,q4 respectively
                status_q1 = partition_list.get(idx_q1);
                status_q2 = partition_list.get(idx_q2);
                status_q3 = partition_list.get(idx_q3);
                status_q4 = partition_list.get(idx_q4);

                if (status_q1 == Status.UNASSIGNED && status_q2 == Status.UNASSIGNED /*all taxa of this quartet are unassigned to any bipartition*/
                        && status_q3 == Status.UNASSIGNED && status_q4 == Status.UNASSIGNED) { // assign q1,q2 to left and q3,q4 to right
                    partition_list.set(idx_q1, Status.LEFT_PARTITION);
                    partition_list.set(idx_q2, Status.LEFT_PARTITION);
                    partition_list.set(idx_q3, Status.RIGHT_PARTITION);
                    partition_list.set(idx_q4, Status.RIGHT_PARTITION);
                    count_taxa_left_partition += 2;
                    count_taxa_right_partition += 2;
                    status_q1 = Status.LEFT_PARTITION;
                    status_q2 = Status.LEFT_PARTITION;
                    status_q3 = Status.RIGHT_PARTITION;
                    status_q4 = Status.RIGHT_PARTITION;
                    //System.out.println(partition_list.get(idx_q1));
                } else {
                    if (status_q1 == Status.UNASSIGNED) //q1 not present in any partition
                    {	//if status_q2 is assigned
                        if (status_q2 != Status.UNASSIGNED) { //look for q2's partition. put q1 in there
                            if (status_q2 == Status.LEFT_PARTITION) {
                                status_q1 = Status.LEFT_PARTITION;
                                partition_list.set(idx_q1, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            } else {
                                status_q1 = Status.RIGHT_PARTITION;
                                partition_list.set(idx_q1, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            }
                        } //q3 is assgined
                        else if (status_q3 != Status.UNASSIGNED) {
                            // q3 in left, put q1 in right
                            if (status_q3 == Status.LEFT_PARTITION) {
                                status_q1 = Status.RIGHT_PARTITION;
                                partition_list.set(idx_q1, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } // status_q3 in right,put status_q1 in left
                            else {
                                status_q1 = Status.LEFT_PARTITION;
                                partition_list.set(idx_q1, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        } else if (status_q4 != Status.UNASSIGNED) {
                            // q4 in left, put q1 in right
                            if (status_q4 == Status.LEFT_PARTITION) {
                                status_q1 = Status.RIGHT_PARTITION;
                                partition_list.set(idx_q1, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } //q4 in right,put q1 in left
                            else {
                                status_q1 = Status.LEFT_PARTITION;
                                partition_list.set(idx_q1, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        }

                    }
                    if (status_q2 == Status.UNASSIGNED) {
                        //look for q1's partition, put q2 in there
                        if (status_q1 == Status.LEFT_PARTITION) {
                            status_q2 = Status.LEFT_PARTITION;
                            partition_list.set(idx_q2, Status.LEFT_PARTITION);
                            count_taxa_left_partition++;
                        } else {
                            status_q2 = Status.RIGHT_PARTITION;
                            partition_list.set(idx_q2, Status.RIGHT_PARTITION);
                            count_taxa_right_partition++;
                        }

                    }
                    if (status_q3 == Status.UNASSIGNED) {
                        if (status_q4 != Status.UNASSIGNED) //q4 is assigned, look for q4 and put q3 in there
                        {
                            if (status_q4 == Status.RIGHT_PARTITION) {
                                status_q3 = Status.RIGHT_PARTITION;
                                partition_list.set(idx_q3, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } else {
                                status_q3 = Status.LEFT_PARTITION;
                                partition_list.set(idx_q3, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        } else {
                            if (status_q1 == Status.RIGHT_PARTITION) {
                                status_q3 = Status.LEFT_PARTITION;
                                partition_list.set(idx_q3, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            } else {
                                status_q3 = Status.RIGHT_PARTITION;
                                partition_list.set(idx_q3, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            }
                        }
                    }
                    if (status_q4 == Status.UNASSIGNED) {
                        if (status_q3 == Status.LEFT_PARTITION) {
                            status_q4 = Status.LEFT_PARTITION;
                            partition_list.set(idx_q4, Status.LEFT_PARTITION);
                            count_taxa_left_partition++;
                        } else {
                            status_q4 = Status.RIGHT_PARTITION;
                            partition_list.set(idx_q4, Status.RIGHT_PARTITION);
                            count_taxa_right_partition++;
                        }

                    }
                }

            }
        }//done going through all quartets

        //now assign remaining taxa randomly step4
        int flag = 0;
        for (int i = 0; i < partition_list.size(); i++) {
            if (partition_list.get(i) == 0) {
                if (count_taxa_left_partition < count_taxa_right_partition) {
                    flag = 2;
                } else if (count_taxa_left_partition > count_taxa_right_partition) {
                    flag = 1;
                } else {
                    flag++;
                }

                if (flag % 2 == 0) {
                    partition_list.set(i, Status.LEFT_PARTITION);
                    count_taxa_left_partition++;
                } else {
                    partition_list.set(i, Status.RIGHT_PARTITION);
                    count_taxa_right_partition++;
                }

            }
        }
        return partition_list;

    }

    //Function to read input from file and populate initial tables [map is a treemap so, by default sorting done]
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
            col_list_for_table1_this_weight.add(quartet); // ADD THE QUARTET to Table 1.
            col_idx_table_1 = customDS.table1_quartets_double_list.get(row_idx_table_1).size() - 1; // Current row's --> columns_list.size - 1 

            customDS.table4_quartetes_indices_list.add(new Pair(row_idx_table_1, col_idx_table_1)); // Also put in the <row,col> list of quartets ...
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

}
