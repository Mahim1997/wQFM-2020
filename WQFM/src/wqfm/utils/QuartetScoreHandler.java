package wqfm.utils;

import wqfm.configs.Config;
import wqfm.configs.DefaultValues;
import wqfm.configs.Scripts;
import wqfm.main.Main;

/**
 *
 * @author mahim
 */
public class QuartetScoreHandler {

    private static String getCommand(String speciesTreeFileName, int quartetScoreLevel, String quartetScoreOutputFile) {
        StringBuilder bld = new StringBuilder();

        bld.append(Main.PYTHON_ENGINE) // python3/pyhton
                .append(" ")
                .append(Scripts.QUARTET_SCORE_COMPUTER) // compute_quartet_score.py
                .append(" ")
                .append(Config.INPUT_FILE_NAME)
                .append(" ")
                .append(speciesTreeFileName)
                .append(" ")
                .append(quartetScoreLevel);

        if (quartetScoreOutputFile != null) {
            bld.append(" ") // if not null, pass the file name ... to be handled in the script.
                    .append(quartetScoreOutputFile);
        }

        return bld.toString();
    }

    public static void handleQuartetScore(String speciesTreeFileName, int quartetScoreLevel, String quartetScoreOutputFile) {
        if (quartetScoreLevel == DefaultValues.QUARTET_SCORE_LEVEL_0_NONE) {
            return; // do not show anything.
        }

        String cmd = getCommand(speciesTreeFileName, quartetScoreLevel, quartetScoreOutputFile);

        System.out.println(cmd);
        Helper.runSystemCommand(cmd);

    }

}
