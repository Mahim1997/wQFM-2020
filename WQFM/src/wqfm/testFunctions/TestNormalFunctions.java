/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm.testFunctions;

import wqfm.configs.Config;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javafx.util.Pair;
import wqfm.feature.Bin;
import wqfm.main.Main;
import wqfm.utils.TreeHandler;
import wqfm.ds.Quartet;
import wqfm.utils.Helper;
import wqfm.configs.DefaultValues;

/**
 *
 * @author mahim
 */
public class TestNormalFunctions {

    public static void testInitialBipartitionFunctions() {
        Map<Integer, Integer> map_partition = new HashMap<>();

        Integer[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        List<Integer> taxa_list_int = new ArrayList<>(Arrays.asList(arr));
        // initially put all taxa to the right.
        taxa_list_int.forEach((t) -> {
            int partition = (Math.random() > 0.5) ? DefaultValues.LEFT_PARTITION : DefaultValues.RIGHT_PARTITION;
            map_partition.put(t, partition);
        });

        System.out.println(map_partition);
    }

    private static List<Bin> getListOfBins(double lower_limit, double upper_limit, double step_size) {
        List<Bin> bins = new ArrayList<>();
        double lower_iter = lower_limit;
        while (lower_iter < upper_limit) {
            bins.add(new Bin(lower_iter, (lower_iter + step_size)));
            lower_iter += step_size;
        }
        return bins;
    }

    public static void testBin() {
        List<Bin> bins = getListOfBins(0.5, 1.0, 0.01);
        System.out.println(
                bins.stream()
                        .map(Bin::toString)
                        .collect(Collectors.joining("\n"))
        );
    }

    public static void testMyPairClass() {
        for (int i = 5; i <= 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.println("bins.add(new Bin(0." + i + j + "," + "0." + i + (j + 1) + "));");
            }
            System.out.println("bins.add(new Bin(0." + i + "9," + "0." + (i + 1) + "0));");
        }

    }

    static void compareQuartets(Quartet q1, Quartet q2) {
        System.out.println("Comparing " + q1.toString() + " , " + q2.toString() + " = " + q1.equals(q2));
    }

    public static boolean checkAllValuesIFSame(List<Boolean> list, boolean val) {
        return list.stream().noneMatch((x) -> (x != val)); //if at least one is different wrt val, then return false
    }

    public static void testCheckValuesFunction() {
        List<Boolean> list1 = new ArrayList<>(Arrays.asList(true, true, true));
        List<Boolean> list2 = new ArrayList<>(Arrays.asList(false, true, true));
        List<Boolean> list3 = new ArrayList<>(Arrays.asList(false, false, false));
        List<Boolean> list4 = new ArrayList<>(Arrays.asList());
        System.out.println(checkAllValuesIFSame(list1, true));
        System.out.println(checkAllValuesIFSame(list2, true));
        System.out.println(checkAllValuesIFSame(list3, true));
        System.out.println(checkAllValuesIFSame(list4, true));
    }

    //Fail khay somehow ??? [PERL use korle fail khay]
    public static void testPerlReroot(int num) {
        String newickTree = "((3,(1,2)),((6,5),4));";
        String outGroupNode = "5";
        int i = 0;
//            System.out.print(i + ": ");
        Config.REROOT_MODE = DefaultValues.REROOT_USING_PERL;
        String rerootTree = TreeHandler.rerootTree(newickTree, outGroupNode);
        System.out.println(i + ": ->" + rerootTree);
//        for (int i = 0; i < num; i++) {
//            String rerootTree = TreeHandler.rerootTree(newickTree, outGroupNode);
//            System.out.println(i + ": ->" + rerootTree);
//        }
    }

// --------------------------------------- TEST METHODS ----------------------------------
    // READ and populate tables using the same function ABOVE... SO don't use this
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

    // Using python3 and dendropy ... this reroot_tree_new.py works
    // Command is: python3 reroot_tree_new.py <tree-newick> <outgroup> DON'T FORGET SEMI-COLON
    public static void testRerootFunction() {
        String newickTree = "((3,(1,2)),((6,5),4));";
        String outGroupNode = "5";

//            System.out.print(i + ": ");
        Config.REROOT_MODE = DefaultValues.REROOT_USING_PYTHON;
        TreeHandler.rerootTree(newickTree, outGroupNode);

    }

    public static void testHashMapValuesSumTime(int num) {
        HashMap<String, Integer> map = new HashMap<>();

        map.put("A", 20);
        map.put("B", 100);
        map.put("Mahim", 22);
        map.put("Zahin", 31);
        map.put("Ronaldo", 7);

        for (int i = 0; i < num; i++) {
            List<Integer> list = new ArrayList<>(map.values());
            int sum = Helper.sumList(list);
//            int sum = 0;
//            sum = map.keySet().stream().map((key) -> map.get(key)).reduce(sum, Integer::sum);
            System.out.println(i + ": " + sum);
        }
    }

    public static void testHashMapNormalSumTime(int num) {
        HashMap<String, Integer> map = new HashMap<>();

        map.put("A", 20);
        map.put("B", 100);
        map.put("Mahim", 22);
        map.put("Zahin", 31);
        map.put("Ronaldo", 7);

        for (int i = 0; i < num; i++) {
            int sum = 0;
            sum = map.keySet().stream().map((key) -> map.get(key)).reduce(sum, Integer::sum);
            System.out.println(i + ": " + sum);
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

    public static void testTreeMapFromPairIntegers() {
        List<Pair<Integer, Integer>> list_pairs = new ArrayList<>();
        list_pairs.add(new Pair(22, 1));
        list_pairs.add(new Pair(31, 2));
        list_pairs.add(new Pair(43, 3));
        list_pairs.add(new Pair(5, 4));
        list_pairs.add(new Pair(10, 5));

        for (Pair<Integer, Integer> pair : list_pairs) {
            System.out.println(pair);
        }

        Map<Integer, Integer> treeMap = new TreeMap<>(Collections.reverseOrder());
        for (Pair<Integer, Integer> pair : list_pairs) {
            treeMap.put(pair.getKey(), pair.getValue());
        }
        System.out.println("---------------------------------------");
        for (Integer key : treeMap.keySet()) {
            int val = treeMap.get(key);
            System.out.println(key + "," + val);
        }
    }
    //https://www.baeldung.com/java-hashmap-sort
    //https://stackoverflow.com/questions/30842966/how-to-sort-a-hash-map-using-key-descending-order

    public static void testListCopy() {
        List<String> list = new ArrayList<>(Arrays.asList("Mahim", "CR7", "RONALDO"));
        List<String> list2 = new ArrayList<>(list);
//        List<String> list2 = list;
        list2.add("Zahin");

        System.out.println(list);
        System.out.println(list2);
    }

    public static void testListIsInFunction() {
        /*for (int i = 0; i < 1000; i++) {
            List<String> list = new ArrayList<>(Arrays.asList("Mahim", "CR7", "RONALDO", "Zahin", "Alvi", "Papan", "Hridoy"));
            int indexOf = list.indexOf("Mahim");
            System.out.println(indexOf);
        }*/
        List<String> list = new ArrayList<>(Arrays.asList("Mahim", "CR7", "RONALDO", "Zahin", "Alvi", "Papan", "Hridoy"));
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i), i);
        }
        List<Integer> list_values = new ArrayList<>(map.values());
        System.out.println(list_values);
//            System.out.println(map.get("Mahim"));
//            System.out.println(list.indexOf("Mahim"));

    }

    public static void testDuplicateMapEntry() {
        Map<String, Integer> map = new HashMap<>();
        map.put("Mahim", 1);
        map.put("Mahim", 22);
        System.out.println(map);
    }

    public static void testHashMapInitializer(int num) {
        Map<String, Integer> mapFirst = new HashMap<>();
        System.out.println("Length of no entries map = " + mapFirst.size());

        List<String> list = new ArrayList<>(Arrays.asList("Mahim", "CR7", "RONALDO", "Zahin", "Alvi", "Papan", "Hridoy"));
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i), i);
        }
        int i = 0;
        Map<String, Integer> newMap = new HashMap<>(map);
        newMap.put("Mahim", 22);
        for (i = 0; i < num; i++) {
            System.out.println(i + ": " + map);
            System.out.println(i + ": " + newMap);
        }
    }

    public static void testIfAllFalse(int num) {
        List<Boolean> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(Boolean.TRUE);
        }
        System.out.println(list);
    }

    public static void testMapFirstKeyValues() {
        List<String> list = new ArrayList<>(Arrays.asList("Mahim", "CR7", "RONALDO", "Zahin", "Alvi", "Papan", "Hridoy"));
        Map<Integer, String> map = new TreeMap<>(Collections.reverseOrder());
        for (int i = 0; i < list.size(); i++) {
            map.put(i, list.get(i));
        }
        System.out.println(map);
        Map.Entry<Integer, String> entry = map.entrySet().iterator().next();
        System.out.println(entry + " , " + entry.getKey() + " , " + entry.getValue());
    }

    public static void testReverseMap() {
        Map<String, String> map_of_int_vs_str = new HashMap<>();
        List<Integer> list_int = new ArrayList<>(Arrays.asList(22, 5, 43, 10, 31));
//        List<String> list_str = new ArrayList<>(Arrays.asList("MAHIM", "ALVI", "PAPAN", "HRIDOY", "ZAHIN"));
        List<String> list_str = new ArrayList<>(Arrays.asList("1", "2", "11", "12", "5"));
        for (int i = 0; i < list_int.size(); i++) {
            map_of_int_vs_str.put(String.valueOf(list_int.get(i)), list_str.get(i));
        }
        System.out.println(map_of_int_vs_str);
//        String s = "(((MAHIM,ZAHIN),ALVI),(HRIDOY,PAPAN));";
        String finalTree = "(((22,31),5),(10,43));";
        System.out.println(finalTree);

        String decodedTree = "";
        for (int i = 0; i < finalTree.length(); i++) {
            char c = finalTree.charAt(i);
            if (c != '(' && c != ')' && c != ',' && c != ';') {
                String key = "";
                int j;
                for (j = i + 1; j < finalTree.length(); j++) {
                    char c1 = finalTree.charAt(j);
                    if (c1 == ')' || c1 == '(' || c1 == ',' || c1 == ';') {
                        break;
                    }
                }
                // System.out.println(j);
                key = finalTree.substring(i, j);
                // System.out.println("i: "+i+ " j: "+j);
                // System.out.println("Key: "+ key);
                String val = map_of_int_vs_str.get(key);
                //System.out.println(val);
                decodedTree += val;
                i += (j - 1 - i);
            } else {
                decodedTree += c;
            }
            //  System.out.println(finalTree.charAt(i));

        }
//        for(int key: map_of_int_vs_str.keySet()){
//            System.out.println("<<REPLACING key=" + key + ", with val=" + map_of_int_vs_str.get(key) + ">>");
//            replaced = replaced.replace(String.valueOf(key), map_of_int_vs_str.get(key));
//        }
        System.out.println(decodedTree);

    }

    private TreeMap<Double, Integer> sortMap(Map<Double, Integer> map) {
//        TreeMap<Double, Integer> sorted = new TreeMap<>(map);
        TreeMap<Double, Integer> sorted = new TreeMap<>(Collections.reverseOrder());
        sorted.putAll(map);
        return sorted;
    }

    public static void testSortPair() { //takes more time than map putting...
        List<Pair<Integer, Integer>> list_pairs = new ArrayList<>();
        list_pairs.add(new Pair(22, 1));
        list_pairs.add(new Pair(31, 2));
        list_pairs.add(new Pair(43, 3));
        list_pairs.add(new Pair(5, 4));
        list_pairs.add(new Pair(10, 5));

        for (Pair<Integer, Integer> pair : list_pairs) {
            System.out.println(pair);
        }
        System.out.println("---------------------------------------");

        list_pairs.sort((o1, o2) -> {
            return o2.getKey() - o1.getKey(); //To change body of generated lambdas, choose Tools | Templates.
        });
        for (Pair<Integer, Integer> pair : list_pairs) {
            System.out.println(pair);
        }
    }

    public static void testRerootJarFunctions(int num) {
//        String newickTree = "((3,(1,2)),((6,5),4));";
//        String outGroupNode = "5";
//        String newickTree = "((ALVI,(MAHIM,ZAHIN)),((PAPAN,HRIDOY),BRISTY));";
//        String outGroupNode = "HRIDOY";
//        String rootedTree = TreeHandler.rerootTree_JAR(newickTree, outGroupNode);

        String leftTree = "((1,2),(3,Y));";
        String rightTree = "((5,6),(4,Y));";
        String outgroup = "Y";

//        String leftTree = "(1,2,X);";
//        String rightTree = "(3,4,X);";
//        String outgroup = "X";
        for (int i = 0; i < num; i++) {
//            System.out.println(i + "::::::: Rooted Tree" + "->> " + rootedTree);
            System.out.println("Left tree =-> " + leftTree + "\nRight Tree => " + rightTree);
//            String leftRTree = TreeHandler.rerootTree_JAR(leftTree, outgroup);
//            String rightRTree = TreeHandler.rerootTree_JAR(rightTree, outgroup);
//            System.out.println("Rerooted LTree: " + leftRTree + "\nRerooted RTree: " + rightRTree);
            String mergedTree = TreeHandler.mergeUnrootedTrees(leftTree, rightTree, outgroup);
            System.out.println("Merged Tree with dummy as " + outgroup + ":>> " + mergedTree);
        }

    }

}

class MyPair {

    double v1;
    int v2;

    public MyPair(double v1, int v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public String toString() {
        return "MyPair{" + "v1=" + v1 + ", v2=" + v2 + '}';
    }

}
