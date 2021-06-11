package wqfm.ds;

import java.util.Map;
import wqfm.bip.Bipartition_8_values;

/**
 *
 * @author mahim
 */
public class StatsPerPass {

    public int whichTaxaWasPassed;
    public double maxGainOfThisPass;
    public Bipartition_8_values _8_values_chosen_for_this_pass;
    public final Map<Integer, Integer> map_final_bipartition;

    public StatsPerPass(int whichTaxaWasPassed, double maxGainOfThisPass,
            Bipartition_8_values _8_vals,
            Map<Integer, Integer> map) {
        this.whichTaxaWasPassed = whichTaxaWasPassed;
        this.maxGainOfThisPass = maxGainOfThisPass;
        this._8_values_chosen_for_this_pass = new Bipartition_8_values(_8_vals);
        this.map_final_bipartition = map; // this also works.
//        this.map_final_bipartition = new HashMap<>(map);

    }

    @Override
    public String toString() {
        return "StatsPerPass{" + "whichTaxaWasPassed=" + whichTaxaWasPassed + ", maxGainOfThisPass=" + maxGainOfThisPass + ", _8_values_chosen_for_this_pass=" + _8_values_chosen_for_this_pass + ", map_final_bipartition=" + map_final_bipartition + '}';
    }

}
