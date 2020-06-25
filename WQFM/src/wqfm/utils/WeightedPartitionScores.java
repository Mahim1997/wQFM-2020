package wqfm.utils;

import wqfm.interfaces.Status;
import wqfm.bip.Bipartition_8_values;
import wqfm.main.Main;

/**
 *
 * @author mahim
 */
public class WeightedPartitionScores {

    private static double calculatePScore0(Bipartition_8_values bip_8_vals) {
        return (bip_8_vals.wtSatisfied - bip_8_vals.wtViolated);
    }

    private static double calculatePScore1(Bipartition_8_values bip_8_vals) {
        return (bip_8_vals.wtSatisfied - 0.5 * bip_8_vals.wtViolated);
    }

    private static double calculatePScore2(Bipartition_8_values bip_8_vals) {
        return (bip_8_vals.wtSatisfied - bip_8_vals.wtViolated - bip_8_vals.wtDeferred);
    }

    private static double calculatePScore3(Bipartition_8_values bip_8_vals) {
        return (3 * bip_8_vals.wtSatisfied - 2 * bip_8_vals.wtViolated);
    }

    public static double calculatePartitionScoreReduced(Bipartition_8_values bip_8_vals) {

        switch (Main.PARTITION_SCORE_MODE) {
            case Status.PARTITION_SCORE_MODE_0:
                return WeightedPartitionScores.calculatePScore0(bip_8_vals);
            case Status.PARTITION_SCORE_MODE_1:
                return WeightedPartitionScores.calculatePScore1(bip_8_vals);
            case Status.PARTITION_SCORE_MODE_2:
                return WeightedPartitionScores.calculatePScore2(bip_8_vals);
            case Status.PARTITION_SCORE_MODE_3:
                return WeightedPartitionScores.calculatePScore3(bip_8_vals);
            default:
                return WeightedPartitionScores.calculatePScore0(bip_8_vals); //[s]-[v] is default.
        }

    }
}
