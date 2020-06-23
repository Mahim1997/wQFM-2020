package wqfm.algo;

import java.util.Map;
import wqfm.bip.Bipartition_8_values;

/**
 *
 * @author mahim
 */
public class StatsPerPass {
    public String whichTaxaWasPassed;
    public double maxGainOfThisPass;
    public Bipartition_8_values _8_values_chosen_for_this_pass;
    public Map<String, Integer> map_final_bipartition;

    public StatsPerPass(String whichTaxaWasPassed, double maxGainOfThisPass, 
            Bipartition_8_values _8_vals, 
            Map<String, Integer> map) {
        this.whichTaxaWasPassed = whichTaxaWasPassed;
        this.maxGainOfThisPass = maxGainOfThisPass;
        this._8_values_chosen_for_this_pass.assign(_8_vals);
        this.map_final_bipartition = map;
    }

    @Override
    public String toString() {
        return "StatsPerPass{" + "taxPassed=" + whichTaxaWasPassed + ", maxG=" + maxGainOfThisPass + ", ns=" + this._8_values_chosen_for_this_pass.numSatisfied + ", bipartition_final=" + map_final_bipartition + '}';
    }

    
    
    
}
