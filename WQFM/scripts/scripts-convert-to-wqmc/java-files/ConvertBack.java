

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

/**
 *
 * @author gahab
 */
public class ConvertBack {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String inputFileName = args[0];
        
         String outputFileName = args[1];
        Scanner sc = new Scanner(new File(inputFileName));
        Map<String, String> map = new HashMap<String, String>();
        Properties properties = new Properties();
        properties.load(new FileInputStream("data.properties"));
        PrintWriter outputWriter;

        for (String key : properties.stringPropertyNames()) {
           map.put(properties.get(key).toString(),key);
            //System.out.println(properties.get(key).toString()+" "+key);
        }
        String finalTree ="";
       
        while(sc.hasNext()){
            finalTree = sc.nextLine();
        }
        // System.out.println(finalTree);
        String decodedTree ="";
        for(int i=0;i<finalTree.length();i++){
            char c = finalTree.charAt(i);
            if(c!='('&&c!=')'&&c!=','&&c!=';'){
              String key = "";
              int j;
              for( j=i+1;j<finalTree.length();j++) {
                  char c1 = finalTree.charAt(j);
                  if(c1==')'||c1=='('||c1==','||c1==';') break;
              }
               // System.out.println(j);
              key = finalTree.substring(i, j);
               // System.out.println("i: "+i+ " j: "+j);
               // System.out.println("Key: "+ key);
              String val = map.get(key);
              //System.out.println(val);
              decodedTree += val;
              i+= (j-1-i);
            }
            else  decodedTree += c;
           //  System.out.println(finalTree.charAt(i));
            
        }
       // System.out.println(decodedTree);
         outputWriter = new PrintWriter(new File(outputFileName));
         outputWriter.write(decodedTree + " ");
        outputWriter.flush();
    }
    
}
