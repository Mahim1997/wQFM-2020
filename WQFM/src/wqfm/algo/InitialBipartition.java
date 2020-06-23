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
import wqfm.ds.CustomInitTables;
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

    public Map<String, Integer> getInitialBipartitionMap(CustomInitTables customDS,
            List<String> list_taxa_string,
            List<Pair<Integer, Integer>> list_quartets_indices) {
        // partition_list is the list which will the partitions of each taxa
        // Status.LEFT_PARTITION : left , 0 : unassigned , +1 : right
//        initiazing partition_list with 0 (all are unassigned)
        Map<String, Integer> map = new HashMap<>();
        for (String tax : list_taxa_string) {
            map.put(tax, Status.UNASSIGNED);
        }
        List<String> partition_list = new ArrayList<>();
        int count_taxa_left_partition = 0;
        int count_taxa_right_partition = 0;

        //Need to create a TreeMap for Pair of list_quartetes_indices...
        Map<Integer, List<Integer>> mapOfRowAndColumns = new TreeMap<>(); //implicit sorting of List<row,col> wrt row [remember, unique row <-> unique weight] via TreeMap
        for (int i = 0; i < list_quartets_indices.size(); i++) {
            Pair<Integer, Integer> pair = list_quartets_indices.get(i); // obtain this pair (i.e. quartet)
            if (mapOfRowAndColumns.containsKey(pair.getKey()) == false) { // map DOESN'T CONTAIN THIS ROW ... intiialize the array list
                mapOfRowAndColumns.put(pair.getKey(), new ArrayList<>());
            }
            mapOfRowAndColumns.get(pair.getKey()).add(pair.getValue()); //now append to the array list of map[row]. THIS column
        }

        for (int rowIDX : mapOfRowAndColumns.keySet()) { //Mahim
            List<Integer> columns_list_quartets_this_row = mapOfRowAndColumns.get(rowIDX); //Mahim
            for (int j = 0; j < columns_list_quartets_this_row.size(); j++) { //Mahim
                int columnIDX = columns_list_quartets_this_row.get(j);//Mahim
                Quartet quartet_under_consideration = customDS.table1_quartets_double_list.get(rowIDX).get(columnIDX);//Mahim
                String q1 = quartet_under_consideration.taxa_sisters_left[0];
                String q2 = quartet_under_consideration.taxa_sisters_left[1];
                String q3 = quartet_under_consideration.taxa_sisters_right[0];
                String q4 = quartet_under_consideration.taxa_sisters_right[1];
                //check status of q1,q2,q3,q4 [the four taxa of THIS quartet]
                int status_q1, status_q2, status_q3, status_q4; //status of q1,q2,q3,q4 respectively
                status_q1 = map.get(q1);
                status_q2 = map.get(q2);
                status_q3 = map.get(q3);
                status_q4 = map.get(q4);

                if (status_q1 == Status.UNASSIGNED && status_q2 == Status.UNASSIGNED /*all taxa of this quartet are unassigned to any bipartition*/
                        && status_q3 == Status.UNASSIGNED && status_q4 == Status.UNASSIGNED) { // assign q1,q2 to left and q3,q4 to right
                    map.put(q1, Status.LEFT_PARTITION);
                    map.put(q2, Status.LEFT_PARTITION);
                    map.put(q3, Status.RIGHT_PARTITION);
                    map.put(q4, Status.RIGHT_PARTITION);
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
                                map.put(q1, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            } else {
                                status_q1 = Status.RIGHT_PARTITION;
                                map.put(q1, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            }
                        } //q3 is assgined
                        else if (status_q3 != Status.UNASSIGNED) {
                            // q3 in left, put q1 in right
                            if (status_q3 == Status.LEFT_PARTITION) {
                                status_q1 = Status.RIGHT_PARTITION;
                                map.put(q1, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } // status_q3 in right,put status_q1 in left
                            else {
                                status_q1 = Status.LEFT_PARTITION;
                                map.put(q1, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        } else if (status_q4 != Status.UNASSIGNED) {
                            // q4 in left, put q1 in right
                            if (status_q4 == Status.LEFT_PARTITION) {
                                status_q1 = Status.RIGHT_PARTITION;
                                map.put(q1, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } //q4 in right,put q1 in left
                            else {
                                status_q1 = Status.LEFT_PARTITION;
                                map.put(q1, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        }

                    }
                    if (status_q2 == Status.UNASSIGNED) {
                        //look for q1's partition, put q2 in there
                        if (status_q1 == Status.LEFT_PARTITION) {
                            status_q2 = Status.LEFT_PARTITION;
                            map.put(q2, Status.LEFT_PARTITION);
                            count_taxa_left_partition++;
                        } else {
                            status_q2 = Status.RIGHT_PARTITION;
                            map.put(q2, Status.RIGHT_PARTITION);
                            count_taxa_right_partition++;
                        }

                    }
                    if (status_q3 == Status.UNASSIGNED) {
                        if (status_q4 != Status.UNASSIGNED) //q4 is assigned, look for q4 and put q3 in there
                        {
                            if (status_q4 == Status.RIGHT_PARTITION) {
                                status_q3 = Status.RIGHT_PARTITION;
                                map.put(q3, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } else {
                                status_q3 = Status.LEFT_PARTITION;
                                map.put(q3, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        } else {
                            if (status_q1 == Status.RIGHT_PARTITION) {
                                status_q3 = Status.LEFT_PARTITION;
                                map.put(q3, Status.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            } else {
                                status_q3 = Status.RIGHT_PARTITION;
                                map.put(q3, Status.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            }
                        }
                    }
                    if (status_q4 == Status.UNASSIGNED) {
                        if (status_q3 == Status.LEFT_PARTITION) {
                            status_q4 = Status.LEFT_PARTITION;
                            map.put(q4, Status.LEFT_PARTITION);
                            count_taxa_left_partition++;
                        } else {
                            status_q4 = Status.RIGHT_PARTITION;
                            map.put(q4, Status.RIGHT_PARTITION);
                            count_taxa_right_partition++;
                        }

                    }
                }

            }
        }//done going through all quartets

        //now assign remaining taxa randomly step4
        int flag = 0;
        for (String key_tax : map.keySet()) {
            if (map.get(key_tax) == Status.UNASSIGNED) {
                if (count_taxa_left_partition < count_taxa_right_partition) {
                    flag = 2;
                } else if (count_taxa_left_partition > count_taxa_right_partition) {
                    flag = 1;
                } else {
                    flag++;
                }
                if (flag % 2 == 0) {
                    map.put(key_tax, Status.LEFT_PARTITION);
                    count_taxa_left_partition++;
                } else {
                    map.put(key_tax, Status.RIGHT_PARTITION);
                    count_taxa_right_partition++;
                }
            }
        }

        return map;

    }
}
