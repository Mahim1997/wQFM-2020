package wqfm.bip;

import wqfm.interfaces.Status;
import wqfm.main.Main;

/**
 *
 * @author mahim
 */
public class WeightedPartitionScores {

    public static double ALPHA_PARTITION_SCORE = 1;
    public static double BETA_PARTITION_SCORE = 1;
    public static boolean USE_WEIGHTS_PARTITION_SCORE = true;

    private static double calculatePScoreDefault(Bipartition_8_values bip_8_vals) {
        return (bip_8_vals.wtSatisfied - bip_8_vals.wtViolated); //alpha = 1, beta = 1, ws-wv
    }

    private static double calculatePScoreCommandLine(Bipartition_8_values bip_8_vals) {
        if (WeightedPartitionScores.USE_WEIGHTS_PARTITION_SCORE == true) {
            return (WeightedPartitionScores.ALPHA_PARTITION_SCORE * bip_8_vals.wtSatisfied
                    - WeightedPartitionScores.BETA_PARTITION_SCORE * bip_8_vals.wtViolated);
        } else {
            return (WeightedPartitionScores.ALPHA_PARTITION_SCORE * bip_8_vals.numSatisfied
                    - WeightedPartitionScores.BETA_PARTITION_SCORE * bip_8_vals.numViolated);
        }

    }

    private static double calculatePScoreFullDynamic(Bipartition_8_values bip_8_vals) {
        if (WeightedPartitionScores.USE_WEIGHTS_PARTITION_SCORE == true) {
            return (WeightedPartitionScores.ALPHA_PARTITION_SCORE * bip_8_vals.wtSatisfied
                    - WeightedPartitionScores.BETA_PARTITION_SCORE * bip_8_vals.wtViolated);
        } else {
            return (WeightedPartitionScores.ALPHA_PARTITION_SCORE * bip_8_vals.numSatisfied
                    - WeightedPartitionScores.BETA_PARTITION_SCORE * bip_8_vals.numViolated);
        }
    }

    public static double calculatePartitionScoreReduced(Bipartition_8_values bip_8_vals) {

        switch (Main.PARTITION_SCORE_MODE) {
            case Status.PARITTION_SCORE_COMMAND_LINE:
                return WeightedPartitionScores.calculatePScoreCommandLine(bip_8_vals);
            case Status.PARTITION_SCORE_FULL_DYNAMIC:
                return WeightedPartitionScores.calculatePScoreFullDynamic(bip_8_vals);
            default:
                return WeightedPartitionScores.calculatePScoreDefault(bip_8_vals); //[s]-[v] is default.
        }

    }
}
