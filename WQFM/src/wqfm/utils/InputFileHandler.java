package wqfm.utils;


import wqfm.configs.Scripts;

/**
 *
 * @author mahim
 */
public class InputFileHandler {
    
    // handles gene trees by producing weighted quartets
    public static void generateWeightedQuartets(String inputFileName, String outputFileName){
        String cmd = "python3 " + Scripts.GENERATE_WQRTS + " " + inputFileName + " " + outputFileName;
        Helper.runSystemCommand(cmd);
    }
    
    // will add functions to handle sequences
    
}
