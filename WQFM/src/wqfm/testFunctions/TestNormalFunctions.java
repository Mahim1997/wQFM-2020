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
import javafx.util.Pair;
import wqfm.RerootTree;
import wqfm.Runner;
import wqfm.ds.CustomDS;
import wqfm.ds.Quartet;

/**
 *
 * @author mahim
 */
public class TestNormalFunctions {

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

}
