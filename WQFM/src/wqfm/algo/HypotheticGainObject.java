/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wqfm.algo;

import wqfm.bip.Bipartition_8_values;

/**
 *
 * @author Zahin
 */
class HypotheticalGain_Object {

    public String taxToConsider;
    public Bipartition_8_values _8_values_whole_considering_thisTax_swap;
    public double Gain;

    public HypotheticalGain_Object(String taxToConsider, Bipartition_8_values _8_values_whole_considering_thisTax_swap, double Gain) {
        this.taxToConsider = taxToConsider;
        this._8_values_whole_considering_thisTax_swap = _8_values_whole_considering_thisTax_swap;
        this.Gain = Gain;
    }

}