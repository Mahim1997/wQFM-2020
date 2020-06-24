/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.util.Pair;
import wqfm.Status;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.Quartet;

/**
 *
 * @author mahim
 */
public class InitialBipartition {
    //Function to obtain initial (logical) bipartition 

    public static void printBipartition(List<String> list_taxa_string, Map<String, Integer> map) {
        System.out.print("LEFT: ");
        for (String key : map.keySet()) { // print left
            if (map.get(key) == Status.LEFT_PARTITION) {
                System.out.print(key + ", ");
            }
        }
        System.out.print("\nRIGHT: ");
        for (String key : map.keySet()) { // print left
            if (map.get(key) == Status.RIGHT_PARTITION) {
                System.out.print(key + ", ");
            }
        }
        System.out.println("");
    }

    public Map<String, Integer> getInitialBipartitionMap(CustomDSPerLevel customDS) {
        // partition_list is the list which will the partitions of each taxa
        // Status.LEFT_PARTITION : left , 0 : unassigned , +1 : right
//        initiazing partition_list with 0 (all are unassigned)
        Map<String, Integer> map_partition = new HashMap<>();
        for (String tax : customDS.list_taxa_string) {
            map_partition.put(tax, Status.UNASSIGNED);
        }
        int count_taxa_left_partition = 0;
        int count_taxa_right_partition = 0;

        for (double weight_key : customDS.sorted_quartets_weight_list_indices_map.keySet()) { //Mahim
            List<Integer> list_quartets_with_this_weight = customDS.sorted_quartets_weight_list_indices_map.get(weight_key); //Mahim
            for (int j = 0; j < list_quartets_with_this_weight.size(); j++) { //Mahim
                int quartet_index = list_quartets_with_this_weight.get(j);//Mahim
                Quartet quartet_under_consideration = customDS.table1_initial_table_of_quartets.get(quartet_index);//Mahim

                String q1 = quartet_under_consideration.taxa_sisters_left[0];
                String q2 = quartet_under_consideration.taxa_sisters_left[1];
                String q3 = quartet_under_consideration.taxa_sisters_right[0];
                String q4 = quartet_under_consideration.taxa_sisters_right[1];
                
                int status_q1, status_q2, status_q3, status_q4; //status of q1,q2,q3,q4 respectively
                status_q1 = map_partition.get(q1);
                status_q2 = map_partition.get(q2);
                status_q3 = map_partition.get(q3);
                status_q4 = map_partition.get(q4);

                if (status_q1 == Status.UNASSIGNED && status_q2 == Status.UNASSIGNED /*all taxa of this quartet are unassigned to any bipartition*/
                        && status_q3 == Status.UNASSIGNED && status_q4 == Status.UNASSIGNED) { // assign q1,q2 to left and q3,q4 to right
                    map_partition.put(q1, Status.LEFT_PARTITION);
                    map_partition.put(q2, Status.LEFT_PARTITION);
                    map_partition.put(q3, Status.RIGHT_PARTITION);
                    map_partition.put(q4, Status.RIGHT_PARTITION);
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
                                map_partition.put(q1, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            } else {
                                status_q1 = Status.RIGHT_PARTITION;
                                map_partition.put(q1, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            }
                        } //q3 is assgined
                        else if (status_q3 != Status.UNASSIGNED) {
                            // q3 in left, put q1 in right
                            if (status_q3 == Status.LEFT_PARTITION) {
                                status_q1 = Status.RIGHT_PARTITION;
                                map_partition.put(q1, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } // status_q3 in right,put status_q1 in left
                            else {
                                status_q1 = Status.LEFT_PARTITION;
                                map_partition.put(q1, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        } else if (status_q4 != Status.UNASSIGNED) {
                            // q4 in left, put q1 in right
                            if (status_q4 == Status.LEFT_PARTITION) {
                                status_q1 = Status.RIGHT_PARTITION;
                                map_partition.put(q1, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } //q4 in right,put q1 in left
                            else {
                                status_q1 = Status.LEFT_PARTITION;
                                map_partition.put(q1, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        }

                    }
                    if (status_q2 == Status.UNASSIGNED) {
                        //look for q1's partition, put q2 in there
                        if (status_q1 == Status.LEFT_PARTITION) {
                            status_q2 = Status.LEFT_PARTITION;
                            map_partition.put(q2, Status.LEFT_PARTITION);
                            count_taxa_left_partition++;
                        } else {
                            status_q2 = Status.RIGHT_PARTITION;
                            map_partition.put(q2, Status.RIGHT_PARTITION);
                            count_taxa_right_partition++;
                        }

                    }
                    if (status_q3 == Status.UNASSIGNED) {
                        if (status_q4 != Status.UNASSIGNED) //q4 is assigned, look for q4 and put q3 in there
                        {
                            if (status_q4 == Status.RIGHT_PARTITION) {
                                status_q3 = Status.RIGHT_PARTITION;
                                map_partition.put(q3, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } else {
                                status_q3 = Status.LEFT_PARTITION;
                                map_partition.put(q3, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        } else {
                            if (status_q1 == Status.RIGHT_PARTITION) {
                                status_q3 = Status.LEFT_PARTITION;
                                map_partition.put(q3, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            } else {
                                status_q3 = Status.RIGHT_PARTITION;
                                map_partition.put(q3, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            }
                        }
                    }
                    if (status_q4 == Status.UNASSIGNED) {
                        if (status_q3 == Status.LEFT_PARTITION) {
                            status_q4 = Status.LEFT_PARTITION;
                            map_partition.put(q4, Status.LEFT_PARTITION);
                            count_taxa_left_partition++;
                        } else {
                            status_q4 = Status.RIGHT_PARTITION;
                            map_partition.put(q4, Status.RIGHT_PARTITION);
                            count_taxa_right_partition++;
                        }

                    }
                }

            }
        }//done going through all quartets

        //now assign remaining taxa randomly step4
        int flag_for_random_assignment = 0;
        for (String key_tax : map_partition.keySet()) {
            if (map_partition.get(key_tax) == Status.UNASSIGNED) {
                if (count_taxa_left_partition < count_taxa_right_partition) {
                    flag_for_random_assignment = 2;
                } else if (count_taxa_left_partition > count_taxa_right_partition) {
                    flag_for_random_assignment = 1;
                } else {
                    flag_for_random_assignment++;
                }
                if (flag_for_random_assignment % 2 == 0) {
                    map_partition.put(key_tax, Status.LEFT_PARTITION);
                    count_taxa_left_partition++;
                } else {
                    map_partition.put(key_tax, Status.RIGHT_PARTITION);
                    count_taxa_right_partition++;
                }
            }
        }

        return map_partition;

    }
}
