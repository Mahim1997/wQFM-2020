package wqfm.bip;

/**
 *
 * @author mahim
 */
public class AggValuesBothBipartitionPerTaxa {

    //FM quartet aggregate values per taxa on both bipartitions for hypothetical gain.
    public Bipartition_8_values left_bipartition_values_obj;
    public Bipartition_8_values right_bipartition_values_obj;
    // ******* REMEMBER above are for PER TAXA [usual case, ONLY in initial bipartition, it is for the whole bipartition]

    public AggValuesBothBipartitionPerTaxa() {

    }

    public void initialiseLeftBipartitionHypotheticalVals(int ns, int nv, int nd, int nb,
            double ws, double wv, double wd, double wb) {
        this.left_bipartition_values_obj = new Bipartition_8_values(ns, nv, nd, nb, ws, wv, wd, wb);
    }

    public void computeRightBipartitionHypotheticalValues(Bipartition_8_values prev, int ns_arg, int nv_arg, int nb_arg,
            double ws_arg, double wv_arg, double wb_arg) {
        //Computation new using short-cut techniques AND new values as given above...
        int ns_new_wrt_1_tax = ns_arg;
        int nv_new_wrt_1_tax = nv_arg;
        int nd_new_wrt_1_tax = prev.numSatisfied + prev.numViolated + prev.numBlank;
        int nb_new_wrt_1_tax = nb_arg;

        double ws_new_wrt_1_tax = ws_arg;
        double wv_new_wrt_1_tax = wv_arg;
        double wd_new_wrt_1_tax = prev.wtSatisfied + prev.wtViolated + prev.wtBlank;
        double wb_new_wrt_1_tax = wb_arg;
        this.right_bipartition_values_obj = new Bipartition_8_values(ns_new_wrt_1_tax, nv_new_wrt_1_tax, nd_new_wrt_1_tax, nb_new_wrt_1_tax,
                ws_new_wrt_1_tax, wv_new_wrt_1_tax, wd_new_wrt_1_tax, wb_new_wrt_1_tax);

    }

}
/*
    Left    Right
    s       d
    v       d
    b       d
    d       s/v/b [depending on]
 */
