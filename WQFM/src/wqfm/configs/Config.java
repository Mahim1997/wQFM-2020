package wqfm.configs;

/**
 *
 * @author mahim
 */
public class Config {
    public static int REROOT_MODE = DefaultValues.REROOT_USING_JAR;
    public static double SMALLEPSILON = 0; //if cumulative gain of iteration < this_num then stop
    
    public static int BIPARTITION_MODE = DefaultValues.BIPARTITION_GREEDY; // BIPARTITION_EXTREME, BIPARTITION_RANDOM, BIPARTITION_GREEDY
    public static double STEP_SIZE_BINNING = 0.01; //always used 0.01 for experiments (default)
    public static double THRESHOLD_BINNING = 0.9; // use 0.9 [default]
    public static boolean SET_RIGHT_TO_1 = false; //false: dual-bin (default), true: right will be set to 1.
    public static boolean DEBUG_DUMMY_NAME = false; //true -> X1, X2 like that & false -> MZCY ... weird name.
    public static int PARTITION_SCORE_MODE = DefaultValues.PARTITION_SCORE_FULL_DYNAMIC; //0->[s]-[v], 1->[s]-0.5[v], 2->[s]-[v]-[d], 3->3[s]-2[v]
    public static boolean BIN_LIMIT_LEVEL_1 = false; // by default: false (bin on all levels); true -> only bin level 1
    public static double CUT_OFF_LIMIT_BINNING = 0.1; // use 0.1 [default]
    
    public static boolean DEBUG_MODE_TESTING = true; // true -> while running from netbeans, false -> run from cmd
    public static boolean NORMALIZE_DUMMY_QUARTETS = true; // true -> divide by count (use mean), false -> simply sum
    public static boolean DEBUG_MODE_PRINTING_GAINS_BIPARTITIONS = false; // printing gains, default: false (otherwise too much cluttered)

    public static int MAX_ITERATIONS_LIMIT = 1000000; //can we keep it as another stopping-criterion ? [100k]

    public static int ANNOTATIONS_LEVEL = DefaultValues.ANNOTATIONS_LEVEL0_NONE;
    public static int QUARTET_SCORE_LEVEL = DefaultValues.QUARTET_SCORE_LEVEL_0_NONE;
    
    public static String INPUT_FILE_NAME = DefaultValues.INPUT_FILE_NAME_WQRTS_DEFAULT; // "input_files/weighted_quartets_avian_biological_dataset";
    public static String OUTPUT_FILE_NAME = DefaultValues.OUTPUT_FILE_NAME_DEFAULT;
    
    public static String INPUT_MODE = DefaultValues.INPUT_MODE_DEFAULT;
    
    
    public static String QUARTET_SCORE_OUTPUT_FILE = "quartet-score-output-file.log";

}
