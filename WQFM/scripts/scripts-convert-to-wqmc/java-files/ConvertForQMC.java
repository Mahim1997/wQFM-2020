

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 *
 * @author mahim
 */
public class ConvertForQMC {

    /**
     * @param args the command line arguments
     */
    
    static List<Quartet> listQuartets = new ArrayList<>();
    
    static String inputFileName;//= "weighted_quartets";
    static String outputFileName;// = "weighted_quartets_forQMC";

    static PrintWriter outputWriter;
    static int count = 1;
    static Properties properties = new Properties();
    
    static HashMap<String, String> map = new HashMap<>(); 
    static boolean FLAG = false;
    public static void main(String[] args) throws FileNotFoundException, IOException {
    inputFileName = args[0];
    outputFileName = args[1];
    System.out.println("ARGS.len = " + args.length);
    if(args.length > 2){
        FLAG = true;
    }
        try {
            outputWriter = new PrintWriter(new File(outputFileName));
        } catch (FileNotFoundException ex) {
            System.out.println("EXCEPTION in output file stream");
        }
        
        run(inputFileName);
        for (Map.Entry<String,String> entry : map.entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }

     properties.store(new FileOutputStream("data.properties"), null);
        
    }

    static void run(String inputFile) {
        listQuartets.clear();
        int numLines = 0;
        try {
            Scanner sc = new Scanner(new File(inputFile));
            
            while(sc.hasNext()){
                // System.out.println("Next Line<" + sc.nextLine() + ">");
                process(sc.nextLine());
                numLines++;
            }
            
        } catch (FileNotFoundException ex) {
            System.out.println("EXCEPTION .... ");
        }
       // System.out.println("Total lines = " + numLines);
    }

    private static void process(String nextLine) {
        if(nextLine.trim().equals("")==true){
            return;
        }
        
        if(FLAG)
        {
            nextLine = nextLine.replace("; ", ";");
        }


        Quartet quartet = new Quartet();
        String[]sArr = nextLine.split(" ");
        quartet.weight = Double.parseDouble(sArr[1].trim()); //Set weight
        
        String line1 = sArr[0].substring(0, sArr[0].length() - 1); //Upto last - 1 i.e. remove the ';' mark
        line1 = line1.substring(1, line1.length() - 1); //Remove the first '(' and the last ')'
        
        //Remove the brackets
        line1 = line1.replace("(", "");
        line1 = line1.replace(")", ""); 
        
        String[] arr2 = line1.split(",");
        if(map.get(arr2[0])==null){
            map.put(arr2[0],Integer.toString(count) );
            count++;
          //  System.out.println(arr2[0]+" not found");
            
        }
        if(map.get(arr2[1])==null){
            map.put(arr2[1],Integer.toString(count) );
            count++;
           // System.out.println(arr2[1]+" not found");
            
        }
        if(map.get(arr2[2])==null){
            map.put(arr2[2],Integer.toString(count) );
            count++;
           // System.out.println(arr2[2]+" not found");
            
        }
         if(map.get(arr2[3])==null){
            map.put(arr2[3],Integer.toString(count) );
            count++;
          //  System.out.println(arr2[3]+" not found");
            
        }

        
        
        quartet.q1 = map.get(arr2[0]);
        quartet.q2 = map.get(arr2[1]);
        quartet.q3 = map.get(arr2[2]);
        quartet.q4 = map.get(arr2[3]);
        outputToFile(quartet.toQMCFormat());
//        System.out.println(quartet.toQMCFormat());
//        System.out.println(quartet.toString());
    }
    static int num = 0;
    private static void outputToFile(String stringToPrint) {
        //System.out.println(num + " -> " + stringToPrint);
        num++;
        outputWriter.write(stringToPrint + " ");
        outputWriter.flush();
    }
    
}

class Quartet
{
    String q1, q2, q3, q4;
    double weight;

    @Override
    public String toString() {
        return "Quartet{" + "q1=" + q1 + ", q2=" + q2 + ", q3=" + q3 + ", q4=" + q4 + ", weight=" + weight + '}';
    }
    
    public String toQMCFormat()
    {
        String s = "";
        
        s += (q1 + "," + q2);
        s += ("|");
        s += (q3 + "," + q4);
        s += ":";
        s += String.valueOf(weight);
//        s += " ";
        return s;
    }
}