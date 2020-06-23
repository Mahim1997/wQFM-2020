package wqfm;

/**
 *
 * @author mahim
 */
public interface Status {

    // Use this for logical biparititoning
    public static int LEFT_PARTITION = -1;
    public static int UNASSIGNED = 0;
    public static int RIGHT_PARTITION = +1;

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

    //Partition score modes ... 1->[s]-[v], 2->[s]-0.5[v], 3->[s]-[v]-[d]
    public static int PARTITION_SCORE_MODE_1 = 51;
    public static int PARTITION_SCORE_MODE_2 = 52;
    public static int PARTITION_SCORE_MODE_3 = 53;

    public static String PRINT_STATUS_QUARTET(int status) {
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
