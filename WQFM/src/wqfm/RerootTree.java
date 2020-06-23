package wqfm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import phylonet.tree.io.ParseException;
import phylonet.tree.model.sti.STITree;

/**
 *
 * @author mahim
 */

public class RerootTree {
    
    //This function uses phylonet's main.jar jar file ... STITree and Node are from 
    public static String rerootTree_JAR(String newickTree, String outGroupNode)
    {
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
    
    //For now, use python3 and dendropy
    // Command is: python3 reroot_tree_new.py <tree-newick> <outgroup> DON'T FORGET SEMI-COLON
    // External commands using java => http://alvinalexander.com/java/edu/pj/pj010016/
    public static String rerootTree_python(String newickTree, String outGroupNode) {
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
}
