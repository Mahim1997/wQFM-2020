package wqfm.algo;

import java.util.List;

/**
 *
 * @author mahim
 */
public class StatsPerPass {
    public String whichTaxaWasPassed;
    public double maxGainOfThisPass;
    public int numSatisfiedQuartetsOfThisPass;
    public List<Integer> list_bipartition_final;

    public StatsPerPass(String whichTaxaWasPassed, double maxGainOfThisPass, int numSatisfiedQuartetsOfThisPass, List<Integer> list_bipartition_final) {
        this.whichTaxaWasPassed = whichTaxaWasPassed;
        this.maxGainOfThisPass = maxGainOfThisPass;
        this.numSatisfiedQuartetsOfThisPass = numSatisfiedQuartetsOfThisPass;
        this.list_bipartition_final = list_bipartition_final;
    }

    @Override
    public String toString() {
        return "PerPassValue{" + "whichTaxaWasPassed=" + whichTaxaWasPassed + ", maxGainOfThisPass=" + maxGainOfThisPass + ", numSatisfiedQuartetsOfThisPass=" + numSatisfiedQuartetsOfThisPass + ", list_bipartition_final=" + list_bipartition_final + '}';
    }
    
    
}
