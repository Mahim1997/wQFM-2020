package wqfm;

import wqfm.datastructure.Quartet;

/**
 *
 * @author mahim
 */
public class Runner {

    public static void runFunctions() {
        readFile(Main.INPUT_FILE_NAME);
    }

    
    private static void readFile(String inputFileName){
        System.out.println("-->>Test input file name");
    }
    
    
    private static void testNewickQuartet() {
        String newickQuartet = "((Z,B),(D,C)); 41";
        Quartet quartet = new Quartet(newickQuartet);
//        quartet = new Quartet("A", "B", "D", "C", 41);
        quartet.printQuartet();
    }
    
    // Using python3 and dendropy ... this reroot_tree_new.py works
    // Command is: python3 reroot_tree_new.py <tree-newick> <outgroup> DON'T FORGET SEMI-COLON
    private static void testRerootFunction() {
        String newickTree = "((3,(1,2)),((6,5),4));";
        String outGroupNode = "5";
        
        for (int i = 0; i < 1; i++) {
            System.out.print(i + ": ");
            RerootTree.rerootTree_python(newickTree, outGroupNode);
        }

    }
}
