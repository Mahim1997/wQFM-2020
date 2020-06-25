package wqfm.utils;

import wqfm.interfaces.Status;
import wqfm.bip.Bipartition_8_values;
import wqfm.main.Main;

/**
 *
 * @author mahim
 */
public class WeightedPartitionScores {

    private static double calculatePScore1(Bipartition_8_values vals) {
        return (vals.wtSatisfied - vals.wtViolated);
    }

    private static double calculatePScore2(Bipartition_8_values vals) {
        return (vals.wtSatisfied - 0.5 * vals.wtViolated);
    }

    private static double calculatePScore3(Bipartition_8_values vals) {
        return (vals.wtSatisfied - vals.wtViolated - vals.wtDeferred);
    }

    public static double calculatePartitionScoreReduced(Bipartition_8_values vals) {

        switch (Main.PARTITION_SCORE_MODE) {
            case Status.PARTITION_SCORE_MODE_1:
                return WeightedPartitionScores.calculatePScore1(vals);
            case Status.PARTITION_SCORE_MODE_2:
                return WeightedPartitionScores.calculatePScore2(vals);
            case Status.PARTITION_SCORE_MODE_3:
                return WeightedPartitionScores.calculatePScore3(vals);
            default:
                return WeightedPartitionScores.calculatePScore1(vals); //[s]-[v] is default.
        }

    }
}
