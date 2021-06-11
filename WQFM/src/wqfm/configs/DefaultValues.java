package wqfm.configs;

/**
 *
 * @author mahim
 */
public interface DefaultValues {


//    public static String SCRIPT_BRANCH_ANNOTATIONS_QUARTET_SUPPORT = "src/wqfm/scripts/annotate_branches.py";

    // Use this for logical biparititoning
    public static int LEFT_PARTITION = -1; //-1 : left
    public static int UNASSIGNED_PARTITION = 0;
    public static int RIGHT_PARTITION = 1; //+1 : right

    // ALL ARE ARBITRARY VALUES FOR NOW ...
    public static int UNKNOWN = 75;
    public static int SATISFIED = 73;
    public static int VIOLATED = 72;
    public static int DEFERRED = 71;
    public static int BLANK = 70;

    //Free or locked [arbitrary values]
    public static int FREE = 21;
    public static int LOCKED = 22;

    // reroot mode [jar, python, to add perl]
    public static int REROOT_USING_JAR = 41;
    public static int REROOT_USING_PYTHON = 42;
    public static int REROOT_USING_PERL = 43;

    //Any undefined values used throughout
    public static int UNDEFINED = -1000;

    //Quartet's each taxon indices
    public static int LEFT_SISTER_1_IDX = 0;
    public static int LEFT_SISTER_2_IDX = 1;
    public static int RIGHT_SISTER_1_IDX = 2;
    public static int RIGHT_SISTER_2_IDX = 3;

    //Partition score modes ... 0->[s]-[v], 1->[s]-0.5[v], 2->[s]-[v]-[d], 3->3[s]-2[v]
    public static int PARTITION_SCORE_MODE_0 = 0;
    public static int PARTITION_SCORE_MODE_1 = 1;
    public static int PARTITION_SCORE_MODE_2 = 2;
    public static int PARTITION_SCORE_MODE_3 = 3;
    public static int PARTITION_SCORE_MODE_4 = 4;
    public static int PARTITION_SCORE_MODE_5 = 5;
    public static int PARTITION_SCORE_MODE_6 = 6;
    public static int PARTITION_SCORE_MODE_7 = 7;
    public static int PARITTION_SCORE_COMMAND_LINE = 8;
    public static int PARTITION_SCORE_FULL_DYNAMIC = 9;

    public static int TOTAL_THREADS = 4;

    public static boolean THREADED_GAIN_CALCULATION_MODE = false;

    // default values for partition-scores
    public static double ALPHA_DEFAULT_VAL = 1.0;
    public static double BETA_DEFAULT_VAL = 1.0;

    // for bipartition modes
    public static int BIPARTITION_GREEDY = 1;
    public static int BIPARTITION_EXTREME = 2;
    public static int BIPARTITION_RANDOM = 3;

    // not important.
//    public static int EARLY_STOP_NUM_TAXA = 5; //just experimentation purposes.
    // for annotations (branch supports)
    public static int ANNOTATIONS_LEVEL0_NONE = 0;
    public static int ANNOTAIONS_LEVEL1_QUARTET_SUPPORT = 1;
    public static int ANNOTATIONS_LEVEL2_QUARTET_SUPPORT_NORMALIZED_SUM = 2;
    public static int ANNOTATIONS_LEVEL3_QUARTET_SUPPORT_NORMALIZED_MAX = 3;

    public static String NULL = "NULL";
    public static String ON = "on";
    public static String OFF = "off";
    
    // for mode of input file
    public static String INPUT_MODE_WEIGHTED_QUARTETS = "weighted-quartets";
    public static String INPUT_MODE_GENE_TREES = "gene-trees";
    public static String INPUT_MODE_UNWEIGHTED_QUARTETS = "unweighted-quartets";
    public static String INPUT_MODE_SEQUENCES = "sequences";
    public static String INPUT_MODE_DEFAULT = INPUT_MODE_WEIGHTED_QUARTETS;
    public static String TEMP_WEIGHTED_QUARTETS_FILE_TO_REMOVE = "TEMP_FILE_PYTHON_FOR_EMBEDDED_QUARTETS";
    
    // default file names
    public static String INPUT_FILE_NAME_WQRTS_DEFAULT = "input-wqrts-for-jar.wqrts";
    public static String OUTPUT_FILE_NAME_DEFAULT = "test-output-file-wqfm-java.tre";
    
    // quartet score level.
    public static int QUARTET_SCORE_LEVEL_0_NONE = 0;
    public static int QUARTET_SCORE_LEVEL_1_WQRTS = 1;
    public static int QUARTET_SCORE_LEVEL_2_WQRTS_PERCENT_TOTAL_QUARTETS = 2;

    
    //Helper method for printing quartet's status
    public static String GET_QUARTET_STATUS(int status) {
        switch (status) {
            case SATISFIED:
                return "SATISFIED";
            case VIOLATED:
                return "VIOLATED";
            case DEFERRED:
                return "DEFERRED";
            case BLANK:
                return "BLANK";
            default:
                break;
        }
        return "NULL";
    }

    

}
