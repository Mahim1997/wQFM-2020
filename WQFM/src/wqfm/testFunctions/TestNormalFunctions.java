/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm.testFunctions;

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
import java.util.function.BiFunction;
import javafx.util.Pair;
import wqfm.main.Main;
import wqfm.utils.TreeHandler;
import wqfm.interfaces.Status;
import wqfm.ds.CustomDSPerLevel;
import wqfm.ds.Quartet;
import wqfm.utils.Helper;

/**
 *
 * @author mahim
 */
public class TestNormalFunctions {

    public static void testMyPairClass() {
        MyPair p1, p2;
        Quartet q1, q2, q3, q4, q5, q6, q7;
        Map<Quartet, MyPair> map = new HashMap<>();

        q1 = new Quartet("((5,8),(X1,10));2.0");
        q2 = new Quartet("((10,X1),(5,8));2.0");

        q1.printQuartet();
        q2.printQuartet();
        System.out.println(q1.equals(q2));

        p1 = new MyPair(-100, -100);
        p2 = new MyPair(7777, 7777);

        map.put(q1, p1);
        System.out.println(map);
        System.out.println(map.containsKey(q2));

        map.get(q2).v1 = 77;
        map.get(q2).v2 = 77;

        System.out.println(map);
    }

    static void compareQuartets(Quartet q1, Quartet q2) {
        System.out.println("Comparing " + q1.toString() + " , " + q2.toString() + " = " + q1.equals(q2));
    }

    public static void testMapAddQuartet() {

        Quartet q1, q2, q3, q4, q5, q6, q7;

        q1 = new Quartet("M", "Z", "A", "B", 22);
        q3 = new Quartet("M", "Z", "A", "B", 31);
        q2 = new Quartet("A", "B", "M", "Z", 22);
        q4 = new Quartet("M", "Z", "B", "A", 10);
        q5 = new Quartet("Z", "M", "B", "A", 100);
        q7 = new Quartet("A", "M", "B", "Z", 22);

        System.out.println("-------------------------------------------------------");

        Map<Quartet, Pair<Double, Integer>> map_cuml_weight_quartets = new HashMap<>();
        map_cuml_weight_quartets.put(q1, new Pair(q1.weight, -1));
        System.out.println(map_cuml_weight_quartets);
        System.out.println(map_cuml_weight_quartets.containsKey(q2));
        System.out.println(map_cuml_weight_quartets.get(q2));
        map_cuml_weight_quartets.put(q2, new Pair(1000, 1000));
        System.out.println(map_cuml_weight_quartets);

        System.out.println("*****************************************************************");
        q1 = new Quartet("((7,8),(X2,9));4.0");
        q2 = new Quartet("((7,8),(9,X2));0.0");

        System.out.println(q1.equals(q2));
        map_cuml_weight_quartets.clear();
        System.out.println(map_cuml_weight_quartets);

        map_cuml_weight_quartets.put(q1, new Pair(q1.weight, -100));
        System.out.println(map_cuml_weight_quartets);
        System.out.println(map_cuml_weight_quartets.containsKey(q2));
        map_cuml_weight_quartets.put(q2, new Pair(7777, 7777));

        /*
        Comparing ((M,Z),(A,B));22.0 , ((Z,M),(B,A));100.0 = true
        Comparing ((M,Z),(A,B));22.0 , ((A,M),(B,Z));22.0 = false
         */
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
        Main.REROOT_MODE = Status.REROOT_USING_PERL;
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

//            System.out.print(i + ": ");
        Main.REROOT_MODE = Status.REROOT_USING_PYTHON;
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

    public static void testStarTree() {
        List<String> list1 = new ArrayList<>(Arrays.asList("Mahim", "Ronaldo"));
        List<String> list2 = new ArrayList<>(Arrays.asList("Mahim"));
        List<String> list3 = new ArrayList<>(Arrays.asList("Mahim", "Ronaldo", "CR7"));
        List<String> list4 = new ArrayList<>();
        System.out.println("List 1 ->" + TreeHandler.getStarTree(list1));
        System.out.println("List 2 ->" + TreeHandler.getStarTree(list2));
        System.out.println("List 3 ->" + TreeHandler.getStarTree(list3));
        System.out.println("List 4 ->" + TreeHandler.getStarTree(list4));
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

    public static void testDoubleListSort() {
        List<List<Quartet>> double_list = new ArrayList<>();

        // List<Integer> list_1 = new ArrayList<>(Arrays.asList(22,5,31));
        List<Quartet> list_1 = new ArrayList<>(Arrays.asList(new Quartet("((a,b),(c,d));31"), new Quartet("((a,f),(g,h));31"), new Quartet("((g,z),(m,n));31")));
        List<Quartet> list_2 = new ArrayList<>(Arrays.asList(new Quartet("((a,c),(f,l));200"), new Quartet("((p,q),(r,s));200")));
        List<Quartet> list_3 = new ArrayList<>(Arrays.asList(new Quartet("((i,j),(a,k));22")));

        double_list.add(list_1);
        double_list.add(list_2);
        double_list.add(list_3);

        for (int i = 0; i < double_list.size(); i++) {
            List<Quartet> list = double_list.get(i);
            System.out.print("Row " + i + " : ");
            for (Quartet q : list) {
                System.out.print(q.toString() + "  ");
            }
            System.out.println("");
        }

        double_list.sort((List<Quartet> list1, List<Quartet> list2) -> {
            return (int) (list2.get(0).weight - list1.get(0).weight); //To change body of generated lambdas, choose Tools | Templates.
        });

        System.out.println("-----------------------------------------");

        for (int i = 0; i < double_list.size(); i++) {
            List<Quartet> list = double_list.get(i);
            System.out.print("Row " + i + " : ");
            for (Quartet q : list) {
                System.out.print(q.toString() + "  ");
            }
            System.out.println("");
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
