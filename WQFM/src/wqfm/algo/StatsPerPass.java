package wqfm.algo;

import java.util.List;
import java.util.Map;

/**
 *
 * @author mahim
 */
public class StatsPerPass {
    public String whichTaxaWasPassed;
    public double maxGainOfThisPass;
    public int numSatisfiedQuartetsOfThisPass;
    public Map<String, Integer> map_final_bipartition;

    public StatsPerPass(String whichTaxaWasPassed, double maxGainOfThisPass, int numSatisfiedQuartetsOfThisPass, 
            Map<String, Integer> map) {
        this.whichTaxaWasPassed = whichTaxaWasPassed;
        this.maxGainOfThisPass = maxGainOfThisPass;
        this.numSatisfiedQuartetsOfThisPass = numSatisfiedQuartetsOfThisPass;
        this.map_final_bipartition = map;
    }

    @Override
    public String toString() {
        return "StatsPerPass{" + "taxPassed=" + whichTaxaWasPassed + ", maxG=" + maxGainOfThisPass + ", ns=" + numSatisfiedQuartetsOfThisPass + ", bipartition_final=" + map_final_bipartition + '}';
    }

    
    
    
}
