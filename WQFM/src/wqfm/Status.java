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
    public static int SATISFIED = 73;
    public static int VIOLATED = 72;
    public static int DEFERRED = 71;
    public static int BLANK = 70;

    //Free or locked [arbitrary values]
    public static int FREE = 21;
    public static int LOCKED = 22;

    // FOR NOW NOT NEEDED ...
    public static int SATISFIED_LEFT_BIPARTITION = 11;
    public static int SATISFIED_RIGHT_BIPARTITION = 12;

    public static int VIOLATED_LEFT_BIPARTITION = 13;
    public static int VIOLATED_RIGHT_BIPARTITION = 14;

    public static int DEFERRED_LEFT_BIPARTITION = 15;
    public static int DEFERRED_RIGHT_BIPARTITION = 16;

    // reroot mode [jar, python, to add perl]
    public static int REROOT_USING_JAR = 41;
    public static int REROOT_USING_PYTHON = 42;
    public static int REROOT_USING_PERL = 43;
    
    
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
