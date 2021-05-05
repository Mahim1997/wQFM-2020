package wqfm.utils;

import wqfm.configs.Config;
import wqfm.main.Main;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import phylonet.tree.io.ParseException;
import phylonet.tree.model.sti.STITree;
import wqfm.configs.DefaultValues;

/**
 *
 * @author mahim
 */
public class TreeHandler {

    public static String rerootTree(String newickTree, String outGroupNode) {
        switch (Config.REROOT_MODE) {
            case DefaultValues.REROOT_USING_JAR:
                return TreeHandler.rerootTree_JAR(newickTree, outGroupNode);
            case DefaultValues.REROOT_USING_PYTHON:
                return TreeHandler.rerootTree_python(newickTree, outGroupNode);
            case DefaultValues.REROOT_USING_PERL:
                return TreeHandler.rerootTree_Perl(newickTree, outGroupNode);
            default:
                System.out.println("-->>FOR NOW Reroot only support using jar and python dendropy [to add perl later].");
                return "NULL";
        }
    }

    //This function uses phylonet's main.jar jar file ... STITree and Node are from 
    private static String rerootTree_JAR(String newickTree, String outGroupNode) {
//        String newickTree = "((3,(1,2)),((6,5),4));";
//        String outGroupNode = "5";
        STITree tree = null;
        try {
            tree = new STITree(newickTree);
            tree.rerootTreeAtNode(tree.getNode(outGroupNode));
        } catch (IOException | ParseException ex) {
            System.out.println("Error in rerootTree.JAR ... check if jar main.jar exists. Exiting.");
            System.exit(-1);
        }

        return tree.toNewick();
    }

    //Eg; (X, ((1,2),3)); to give ( ((1,2),3), X) i.e. outgroup is shifted to the right.
    private static String shiftOutgroupToRight(String rootedTree) {
        if (rootedTree.equals("")) {
            return rootedTree;
        }
        rootedTree = rootedTree.replace(";", ""); // remove semi-colon
        rootedTree = rootedTree.substring(1, rootedTree.length() - 1); // remove first and last brackets
        String[] arr = rootedTree.split(",");
        if (arr.length == 0) {
            return rootedTree;
        }
        String outGroup = arr[0];
        rootedTree = rootedTree.replace(outGroup, ""); //remove outGroup Node
        rootedTree = rootedTree.substring(1); //remove first comma.

        //Now we are left with only the right-most subtree WITHOUT the outGroup ... eg. (T) [with brackets]
        //Now we will add new brackets, comma, outGroup AND final semi-colon to right i.e. ( (T), X); 
//        System.out.println(rootedTree);
        String revTree = "(" + rootedTree + "," + outGroup + ");";
        return revTree;
    }

    private static String addBracketsAndSemiColon(String s) {
        return "(" + s + ");";
    }

    private static String removeOutgroupNodeAndBrackets(String tree, String outGroup) {
        tree = tree.replace(";", ""); // remove semi-colon
        tree = tree.substring(1, tree.length() - 1); // remove first and last brackets
        tree = tree.replace(outGroup, ""); //remove outGroup Node
        tree = tree.substring(1); //From left, so remove first comma
        return tree;
    }

    private static String mergeTwoRootedTrees(String treeLeft, String treeRight, String outGroup) {

        String leftTree_outgroupRemoved = removeOutgroupNodeAndBrackets(treeLeft, outGroup);
        String rightTree_outgroupRemoved = removeOutgroupNodeAndBrackets(treeRight, outGroup); //CHECK if from both sides outgroup are in left

        String mergedTree = addBracketsAndSemiColon(leftTree_outgroupRemoved + "," + rightTree_outgroupRemoved);

        return mergedTree;
    }

    public static String mergeUnrootedTrees(String treeLeft, String treeRight, String outGroup) {
        //Check these two conditions
        if (treeLeft.equals("")) {
            return treeRight;
        }
        if (treeRight.equals("")) {
            return treeLeft;
        }

        //1. reroot two trees wrt outGroup
        String rootedTreeLeft = TreeHandler.rerootTree_JAR(treeLeft, outGroup);
        String rootedTreeRight = TreeHandler.rerootTree_JAR(treeRight, outGroup);
        //2. Outgroup will be at the left-most side, so we have to right-shift the leftTree's outgroup
//        String rootedTreeLeft_rightShifted = TreeHandler.shiftOutgroupToRight(rootedTreeLeft);
        String mergedTree = mergeTwoRootedTrees(rootedTreeLeft, rootedTreeRight, outGroup);
        return mergedTree;
    }

    // Command is: python3 reroot_tree_new.py <tree-newick> <outgroup> DON'T FORGET SEMI-COLON [DENDROPY]
    // External commands using java => http://alvinalexander.com/java/edu/pj/pj010016/
    private static String rerootTree_python(String newickTree, String outGroupNode) {
        try {
            String s;
            // String cmd = "python3 reroot_tree_new.py '" + newickTree + "' '" + outGroupNode + "'";
            String cmd = "python3 reroot_tree_new.py " + newickTree + " " + outGroupNode + "";
//            System.out.println(cmd);
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

//            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                return s;
            }
//            System.out.println("Here is the standard error of the command (if any):\n");
//            while ((s = stdError.readLine()) != null) {
//                System.out.println(s);
//            }
        } catch (IOException ex) {
        }
        System.out.println("--->>ERROR IN RerootTree.java ... check python package ... check if dendropy is installed "
                + " ... use pip3 install dendropy ... use python3 ... EXITING JAVA PROGRAM !!! ");
        System.exit(-1);
        return "ERROR_IN_TREE";
    }

    // reroot_tree_new.pl [Using perl reroot ... just to test] {BIOPERL}
    private static String rerootTree_Perl(String newickTree, String outGroupNode) {
        try {
            String s;
            // String cmd = "python3 reroot_tree_new.py '" + newickTree + "' '" + outGroupNode + "'";
            String cmd = "perl reroot_tree_new.pl -t \"" + newickTree + "\" -r \"" + outGroupNode + "\" -o output_file";
            System.out.println(cmd);

            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//
//            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                return s;
            }
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            Scanner sc = new Scanner(new FileInputStream("output_file"));
            if (sc.hasNextLine()) {
                System.out.println(sc.nextLine());
                return sc.nextLine();
            }

        } catch (IOException ex) {
        }

        return "ERROR_IN_TREE";
    }

    //eg: a b c d
    public static String getStarTree(List<Integer> taxa_list_int) {
        if (taxa_list_int.isEmpty()) {
            return "();";
        }
        return taxa_list_int
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "(", ")"));

//        String s = "";
//        s += "(";
//        for (int i = 0; i < taxa_list_int.size(); i++) {
//            s += taxa_list_int.get(i);
//            if (i != taxa_list_int.size() - 1) {
//                s += ","; //do not add comma for the last taxon
//            }
//        }
//        s += ");";
//        return s;
    }
}
