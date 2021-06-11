import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
/**
 *
 * @author gahab
 */
public class BranchLengthRemover {
    public static void main(String[] args) throws Exception {
       
        if(args.length != 2){
            System.out.println("-->>Usage BranchLengthRemover <inputFileName> <outputFileName>");
            System.exit(-1);
        }
        String inputFileName = args[0];
        String outputFileName = args[1];

        FileWriter fileWriter = new FileWriter(outputFileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
//        printWriter.print(fileContent);



        Scanner sc = new Scanner(new File(inputFileName));
        String trueTree ="";
       
        while(sc.hasNext()){
            trueTree = sc.nextLine();
		
	        trueTree = trueTree.replaceAll(":[0-9]*.[0-9]+", "");
		trueTree = trueTree.replaceAll(":[0-9]+", "");
trueTree = trueTree.replaceAll("e[0-9]+", "");
		trueTree = trueTree.replaceAll("e-[0-9]+", "");
	        //System.out.println(trueTree);
            printWriter.print(trueTree);
            printWriter.print("\n");  
        }

        printWriter.close();
    }
    
}
