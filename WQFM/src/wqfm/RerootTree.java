/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 *
 * @author mahim
 */
public class RerootTree {


    public static String rerootTree(String newickTree, String outGroupNode) {
        // http://alvinalexander.com/java/edu/pj/pj010016/
        try {
            String s;
            
            // String cmd = "python3 reroot_tree_new.py '" + newickTree + "' '" + outGroupNode + "'";
            
            String cmd = "python3 reroot_tree_new.py " + newickTree + " " + outGroupNode + "";
            
//            System.out.println(cmd);
            
            Process p = Runtime.getRuntime().exec(cmd);
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            
            // read the output from the command
//            System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                return s;
            }
            
            // read any errors from the attempted command
//            System.out.println("Here is the standard error of the command (if any):\n");
//            while ((s = stdError.readLine()) != null) {
//                System.out.println(s);
//            }
        } catch (IOException ex) {
        }
        System.out.println("--->>ERROR IN REROOT ... check python package ... check if dendropy is installed "
                + " ... use pip3 install dendropy ... use python3 ... ");
        System.exit(-1);
        return "ERROR_IN_TREE";
    }
}
