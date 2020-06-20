package wqfm;

/**
 *
 * @author mahim
 */
public class WQFM {

    public static void main(String[] args) {


        long time_1 = System.currentTimeMillis();

        String newickTree = "((3,(1,2)),((6,5),4));";
        String outGroupNode = "5";
        
        for(int i=0; i<1; i++){
            System.out.print(i + ": ");
            RerootTree.rerootTree(newickTree, outGroupNode);
        }


        
        
        
        long time_del = System.currentTimeMillis() - time_1;
        System.out.println("Time (ms) = " + time_del);

    }

}
