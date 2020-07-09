/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thread_objects;

import wqfm.bip.Bipartition_8_values;

/**
 *
 * @author Zahin
 */
public class HypotheticalGain_Object {

    public int taxToConsider;
    public Bipartition_8_values _8_values_whole_considering_thisTax_swap;
    public double Gain;

    public HypotheticalGain_Object(int taxToConsider, Bipartition_8_values _8_values_whole_considering_thisTax_swap, double Gain) {
        this.taxToConsider = taxToConsider;
        this._8_values_whole_considering_thisTax_swap = _8_values_whole_considering_thisTax_swap;
        this.Gain = Gain;
    }

}