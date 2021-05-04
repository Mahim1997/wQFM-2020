package wqfm.bip;

import wqfm.configs.Config;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.Quartet;
import wqfm.main.Main;
import wqfm.configs.DefaultValues;

/**
 *
 * @author mahim
 */
public class InitialBipartition {
    //Function to obtain initial (logical) bipartition 

    private static String getCommaSeparatedEqualValue(Map<String, Integer> map, int value) {
        return map.keySet()
                .stream()
                .map(x -> map.get(x))
                .filter(x -> (x == value))
                .map(x -> String.valueOf(x))
                .collect(Collectors.joining(", "));
    }

    public static void printBipartition(Map<String, Integer> map) {
        System.out.print("LEFT: ");
        System.out.println(InitialBipartition.getCommaSeparatedEqualValue(map, DefaultValues.LEFT_PARTITION));

        System.out.print("RIGHT: ");
        System.out.println(InitialBipartition.getCommaSeparatedEqualValue(map, DefaultValues.RIGHT_PARTITION));
    }

    public Map<Integer, Integer> getInitialBipartitionMap(CustomDSPerLevel customDS) {
        // Factory level choice.
        switch (Config.BIPARTITION_MODE) {

            case DefaultValues.BIPARTITION_GREEDY:
                return this.getInitialBipartitionGreedy(customDS);

            case DefaultValues.BIPARTITION_EXTREME:
                return this.getInitialBipartitionExtreme(customDS);

            case DefaultValues.BIPARTITION_RANDOM:
                return this.getInitialBipartitionRandom(customDS);

            default:
                return this.getInitialBipartitionGreedy(customDS); // by default

        }

    }

    private Map<Integer, Integer> getInitialBipartitionExtreme(CustomDSPerLevel customDS) {
        // one on the left, all the others on the right.
        Map<Integer, Integer> map_partition = new HashMap<>();

        // initially put all taxa to the right.
        customDS.taxa_list_int.forEach((t) -> {
            map_partition.put(t, DefaultValues.RIGHT_PARTITION);
        });

        // take the first taxa put into left.
        map_partition.put(customDS.taxa_list_int.get(0), DefaultValues.LEFT_PARTITION);

        return map_partition;
    }

    private Map<Integer, Integer> getInitialBipartitionRandom(CustomDSPerLevel customDS) {
        Map<Integer, Integer> map_partition = new HashMap<>();

        // randomly assign partitions.
        customDS.taxa_list_int.forEach((t) -> {
            int partition = (Math.random() > 0.5) ? DefaultValues.LEFT_PARTITION : DefaultValues.RIGHT_PARTITION;
            map_partition.put(t, partition);
        });

        // check if any side is empty, then place there.
        boolean has_left = false, has_right = false;

        for (int key : map_partition.keySet()) {
            int val = map_partition.get(key);
            if (val == DefaultValues.LEFT_PARTITION) {
                has_left = true;
            }
            if (val == DefaultValues.RIGHT_PARTITION) {
                has_right = true;
            }
        }

        if (!has_left) {
            // assign first element.
            map_partition.put(customDS.taxa_list_int.get(0), DefaultValues.LEFT_PARTITION);
        }
        if (!has_right) {
            // assign first element.
            map_partition.put(customDS.taxa_list_int.get(0), DefaultValues.RIGHT_PARTITION);
        }

        return map_partition;
    }

    private Map<Integer, Integer> getInitialBipartitionGreedy(CustomDSPerLevel customDS) {

        Map<Integer, Integer> map_partition = new HashMap<>(); //return this map

        for (int tax : customDS.taxa_list_int) { //initially assign all as 0/unassigned
            map_partition.put(tax, DefaultValues.UNASSIGNED_PARTITION);
        }

        int count_taxa_left_partition = 0;
        int count_taxa_right_partition = 0;

        for (double weight_key : customDS.sorted_quartets_weight_list_indices_map.keySet()) { //Mahim
            List<Integer> list_quartets_with_this_weight = customDS.sorted_quartets_weight_list_indices_map.get(weight_key); //Mahim
            for (int j = 0; j < list_quartets_with_this_weight.size(); j++) { //Mahim
                int quartet_index = list_quartets_with_this_weight.get(j);//Mahim
                Quartet quartet_under_consideration = customDS.initial_table1_of_list_of_quartets.get(quartet_index);//Mahim

                int q1 = quartet_under_consideration.taxa_sisters_left[0];
                int q2 = quartet_under_consideration.taxa_sisters_left[1];
                int q3 = quartet_under_consideration.taxa_sisters_right[0];
                int q4 = quartet_under_consideration.taxa_sisters_right[1];

                int status_q1, status_q2, status_q3, status_q4; //status of q1,q2,q3,q4 respectively
                status_q1 = map_partition.get(q1);
                status_q2 = map_partition.get(q2);
                status_q3 = map_partition.get(q3);
                status_q4 = map_partition.get(q4);

                if (status_q1 == DefaultValues.UNASSIGNED_PARTITION && status_q2 == DefaultValues.UNASSIGNED_PARTITION /*all taxa of this quartet are unassigned to any bipartition*/
                        && status_q3 == DefaultValues.UNASSIGNED_PARTITION && status_q4 == DefaultValues.UNASSIGNED_PARTITION) { // assign q1,q2 to left and q3,q4 to right
                    map_partition.put(q1, DefaultValues.LEFT_PARTITION);
                    map_partition.put(q2, DefaultValues.LEFT_PARTITION);
                    map_partition.put(q3, DefaultValues.RIGHT_PARTITION);
                    map_partition.put(q4, DefaultValues.RIGHT_PARTITION);
                    count_taxa_left_partition += 2;
                    count_taxa_right_partition += 2;
                    status_q1 = DefaultValues.LEFT_PARTITION;
                    status_q2 = DefaultValues.LEFT_PARTITION;
                    status_q3 = DefaultValues.RIGHT_PARTITION;
                    status_q4 = DefaultValues.RIGHT_PARTITION;
                    //System.out.println(partition_list.get(idx_q1));
                } else {
                    if (status_q1 == DefaultValues.UNASSIGNED_PARTITION) //q1 not present in any partition
                    {	//if status_q2 is assigned
                        if (status_q2 != DefaultValues.UNASSIGNED_PARTITION) { //look for q2's partition. put q1 in there
                            if (status_q2 == DefaultValues.LEFT_PARTITION) {
                                status_q1 = DefaultValues.LEFT_PARTITION;
                                map_partition.put(q1, DefaultValues.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            } else {
                                status_q1 = DefaultValues.RIGHT_PARTITION;
                                map_partition.put(q1, DefaultValues.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            }
                        } //q3 is assgined
                        else if (status_q3 != DefaultValues.UNASSIGNED_PARTITION) {
                            // q3 in left, put q1 in right
                            if (status_q3 == DefaultValues.LEFT_PARTITION) {
                                status_q1 = DefaultValues.RIGHT_PARTITION;
                                map_partition.put(q1, DefaultValues.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } // status_q3 in right,put status_q1 in left
                            else {
                                status_q1 = DefaultValues.LEFT_PARTITION;
                                map_partition.put(q1, DefaultValues.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        } else if (status_q4 != DefaultValues.UNASSIGNED_PARTITION) {
                            // q4 in left, put q1 in right
                            if (status_q4 == DefaultValues.LEFT_PARTITION) {
                                status_q1 = DefaultValues.RIGHT_PARTITION;
                                map_partition.put(q1, DefaultValues.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } //q4 in right,put q1 in left
                            else {
                                status_q1 = DefaultValues.LEFT_PARTITION;
                                map_partition.put(q1, DefaultValues.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        }

                    }
                    if (status_q2 == DefaultValues.UNASSIGNED_PARTITION) {
                        //look for q1's partition, put q2 in there
                        if (status_q1 == DefaultValues.LEFT_PARTITION) {
                            status_q2 = DefaultValues.LEFT_PARTITION;
                            map_partition.put(q2, DefaultValues.LEFT_PARTITION);
                            count_taxa_left_partition++;
                        } else {
                            status_q2 = DefaultValues.RIGHT_PARTITION;
                            map_partition.put(q2, DefaultValues.RIGHT_PARTITION);
                            count_taxa_right_partition++;
                        }

                    }
                    if (status_q3 == DefaultValues.UNASSIGNED_PARTITION) {
                        if (status_q4 != DefaultValues.UNASSIGNED_PARTITION) //q4 is assigned, look for q4 and put q3 in there
                        {
                            if (status_q4 == DefaultValues.RIGHT_PARTITION) {
                                status_q3 = DefaultValues.RIGHT_PARTITION;
                                map_partition.put(q3, DefaultValues.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            } else {
                                status_q3 = DefaultValues.LEFT_PARTITION;
                                map_partition.put(q3, DefaultValues.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            }
                        } else {
                            if (status_q1 == DefaultValues.RIGHT_PARTITION) {
                                status_q3 = DefaultValues.LEFT_PARTITION;
                                map_partition.put(q3, DefaultValues.LEFT_PARTITION);
                                count_taxa_left_partition++;
                            } else {
                                status_q3 = DefaultValues.RIGHT_PARTITION;
                                map_partition.put(q3, DefaultValues.RIGHT_PARTITION);
                                count_taxa_right_partition++;
                            }
                        }
                    }
                    if (status_q4 == DefaultValues.UNASSIGNED_PARTITION) {
                        if (status_q3 == DefaultValues.LEFT_PARTITION) {
                            status_q4 = DefaultValues.LEFT_PARTITION;
                            map_partition.put(q4, DefaultValues.LEFT_PARTITION);
                            count_taxa_left_partition++;
                        } else {
                            status_q4 = DefaultValues.RIGHT_PARTITION;
                            map_partition.put(q4, DefaultValues.RIGHT_PARTITION);
                            count_taxa_right_partition++;
                        }

                    }
                }

            }
        }//done going through all quartets

        //now assign remaining taxa randomly step4
        int flag_for_random_assignment = 0;
        for (int key_tax : map_partition.keySet()) {
            if (map_partition.get(key_tax) == DefaultValues.UNASSIGNED_PARTITION) {
                if (count_taxa_left_partition < count_taxa_right_partition) {
                    flag_for_random_assignment = 2;
                } else if (count_taxa_left_partition > count_taxa_right_partition) {
                    flag_for_random_assignment = 1;
                } else {
                    flag_for_random_assignment++;
                }
                if (flag_for_random_assignment % 2 == 0) {
                    map_partition.put(key_tax, DefaultValues.LEFT_PARTITION);
                    count_taxa_left_partition++;
                } else {
                    map_partition.put(key_tax, DefaultValues.RIGHT_PARTITION);
                    count_taxa_right_partition++;
                }
            }
        }

        return map_partition;

    }

}
