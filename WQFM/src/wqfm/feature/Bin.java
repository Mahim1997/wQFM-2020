package wqfm.feature;

import wqfm.configs.Config;
import java.util.ArrayList;
import java.util.List;
import wqfm.main.Main;
import wqfm.configs.DefaultValues;

/**
 *
 * @author mahim
 */
public class Bin {

    public static double proportion_left_thresh;
    public static double proportion_after_thresh_before_1;
    public static double proportion_greater_or_equal_1;
    public static boolean WILL_DO_DYNAMIC = true;

    private double lower_limit;
    private double upper_limit;
    private double frequency; // no need for a map/dictionary

    public Bin(double lower_lim, double upper_lim) {
        this.lower_limit = lower_lim;
        this.upper_limit = upper_lim;
        this.frequency = 0;
    }

    //Factory method to get list of bins [Mahim]
    public static List<Bin> getListOfBins(double lower_limit, double upper_limit, double step_size) {
        List<Bin> bins = new ArrayList<>();
        double lower_iter = lower_limit;
        while (lower_iter < upper_limit) {
            bins.add(new Bin(lower_iter, (lower_iter + step_size)));
            lower_iter += step_size;
        }
        return bins;
    }

    private static boolean does_lie_within(double left, double right, double val) {
        return val >= left && val < right;
    }

    private static boolean is_within_bin(Bin bin, double ratio) {
        return does_lie_within(bin.lower_limit, bin.upper_limit, ratio);
    }

    private double getMidPoint() {
        // may be later can be used as some other way of finding the limiting point instead of direct mid-point.
        return 0.5 * (this.lower_limit + this.upper_limit);
    }

    public static double calculateBinsAndFormScores(List<Double> list_ratios) {

        int cnt_before_thresh = 0;
        int cnt_after_thresh_before_1 = 0;
        int cnt_after_1 = 0;

        //already counts are initialized to 0
        List<Bin> bins = Bin.getListOfBins(0.5, 1.0, Config.STEP_SIZE_BINNING); //initialize bins from [0.5,1.0] in delta = 0.01
//        System.out.println(bins);

        //calculate counts in each bin as well as the proportion-counts
        if (bins.isEmpty()) {
            System.out.println("-->>L 64. Num of bins is empty. Returning default beta = " + DefaultValues.BETA_DEFAULT_VAL);
            return DefaultValues.BETA_DEFAULT_VAL;
        }
        double upper_limit_of_highest_bin = bins.get(bins.size() - 1).upper_limit;

        //proportion-counts and bin-counts for each ratio and each bin.
        for (double ratio : list_ratios) {
            //proportion counts ..
            if (Bin.does_lie_within(0.5, Config.THRESHOLD_BINNING, ratio)) {
                cnt_before_thresh++;
            } else if (Bin.does_lie_within(Config.THRESHOLD_BINNING, upper_limit_of_highest_bin, ratio)) {
                cnt_after_thresh_before_1++;
            } else { // >= 1
                cnt_after_1++;
            }
            //bin's counts
            for (Bin bin : bins) {
                if (Bin.is_within_bin(bin, ratio) == true) {
                    bin.frequency++; //if ratio lies in this bin, increment
                }
            }
        }

        //find-proportions.
        int total_count = cnt_before_thresh + cnt_after_thresh_before_1 + cnt_after_1;

        //base-case if no ratios exist. [should be handled from calling function, but check nonetheless]
        if (total_count == 0) {
            System.out.println("L 97. Bin. Total-Count-Ratios = 0, Use beta default = " + DefaultValues.BETA_DEFAULT_VAL);
            return DefaultValues.BETA_DEFAULT_VAL;
        }
        //set-up the proportions accordingly.
        Bin.proportion_left_thresh = (double) cnt_before_thresh / (double) total_count;
        Bin.proportion_after_thresh_before_1 = (double) cnt_after_thresh_before_1 / (double) total_count;
        Bin.proportion_greater_or_equal_1 = (double) cnt_after_1 / (double) total_count;

//        System.out.printf("L 101. cnt_before_thresh = %d, cnt_after_thresh_before_1 = %d, cnt_after_1 = %d, total_count = %d"
//                , cnt_before_thresh, cnt_after_thresh_before_1, cnt_after_1, total_count);
        
        double weighted_avg_final; //this will be passed as BETA

        if (Bin.proportion_left_thresh >= Config.CUT_OFF_LIMIT_BINNING) { //greater than cut-off so, bin(left, thresh)
            //bin on the left side.
            double cumulative_mid_point_product_counts = 0;
            for (Bin bin : bins) {
                if (bin.lower_limit < Config.THRESHOLD_BINNING) { //Bin from 0.5 upto 0.9 [left-side-bin]
                    //sum the mid-point-of-class * frequency-of-class
                    cumulative_mid_point_product_counts += (bin.getMidPoint() * bin.frequency);
                }
            }
            weighted_avg_final = (cumulative_mid_point_product_counts) / (double) cnt_before_thresh;
        } //bin on the right side.
        else {
            double cumulative_mid_point_product_counts = 0;
            for (Bin bin : bins) {
                if (bin.lower_limit >= Config.THRESHOLD_BINNING) {
                    cumulative_mid_point_product_counts += (bin.getMidPoint() * bin.frequency);
                }
            }
///             compute using both bins.
            if (Config.SET_RIGHT_TO_1 == true) {
                weighted_avg_final = DefaultValues.BETA_DEFAULT_VAL;
            } else {
                weighted_avg_final = (cumulative_mid_point_product_counts + (double) cnt_after_1) / ((double) (cnt_after_thresh_before_1 + cnt_after_1));
            }
            

        }
        bins.clear(); ///clear for gc to collect [WILL have to be more efficient than this]
        return weighted_avg_final;

    }

    @Override
    public String toString() {
        return "Bin(" + this.lower_limit + "," + this.upper_limit + "," + this.frequency + ")";
    }

}


/*
//        bins.add(new Bin(0.5, 0.6));
//        bins.add(new Bin(0.6, 0.7));
//        bins.add(new Bin(0.7, 0.8));

        bins.add(new Bin(0.50, 0.51));
        bins.add(new Bin(0.51, 0.52));
        bins.add(new Bin(0.52, 0.53));
        bins.add(new Bin(0.53, 0.54));
        bins.add(new Bin(0.54, 0.55));
        bins.add(new Bin(0.55, 0.56));
        bins.add(new Bin(0.56, 0.57));
        bins.add(new Bin(0.57, 0.58));
        bins.add(new Bin(0.58, 0.59));
        bins.add(new Bin(0.59, 0.60));
        bins.add(new Bin(0.60, 0.61));
        bins.add(new Bin(0.61, 0.62));
        bins.add(new Bin(0.62, 0.63));
        bins.add(new Bin(0.63, 0.64));
        bins.add(new Bin(0.64, 0.65));
        bins.add(new Bin(0.65, 0.66));
        bins.add(new Bin(0.66, 0.67));
        bins.add(new Bin(0.67, 0.68));
        bins.add(new Bin(0.68, 0.69));
        bins.add(new Bin(0.69, 0.70));
        bins.add(new Bin(0.70, 0.71));
        bins.add(new Bin(0.71, 0.72));
        bins.add(new Bin(0.72, 0.73));
        bins.add(new Bin(0.73, 0.74));
        bins.add(new Bin(0.74, 0.75));
        bins.add(new Bin(0.75, 0.76));
        bins.add(new Bin(0.76, 0.77));
        bins.add(new Bin(0.77, 0.78));
        bins.add(new Bin(0.78, 0.79));
        bins.add(new Bin(0.79, 0.80)); //thresh = 0.80, step-size = 0.01 [7, 7]
        bins.add(new Bin(0.80, 0.81));
        bins.add(new Bin(0.81, 0.82));
        bins.add(new Bin(0.82, 0.83));
        bins.add(new Bin(0.83, 0.84));
        bins.add(new Bin(0.84, 0.85)); //thresh = 0.85, step-size = 0.01 [7, 7]
        bins.add(new Bin(0.85, 0.86));
        bins.add(new Bin(0.86, 0.87));
        bins.add(new Bin(0.87, 0.88));
        bins.add(new Bin(0.88, 0.89));
        bins.add(new Bin(0.89, 0.90)); //thresh = 0.90, step-size = 0.01 [6, 6]
//        bins.add(new Bin(0.91, 0.92));
//        bins.add(new Bin(0.92, 0.93));
//        bins.add(new Bin(0.93, 0.94));
//        bins.add(new Bin(0.94, 0.95));
//        bins.add(new Bin(0.95, 0.96));
//        bins.add(new Bin(0.96, 0.97));
//        bins.add(new Bin(0.97, 0.98));
//        bins.add(new Bin(0.98, 0.99));
//        bins.add(new Bin(0.99, 1.00)); //thresh = 1.0, step-size = 0.01 [7, 7]
 */
