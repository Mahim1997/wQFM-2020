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
    public static int SATISFIED = 7;
    public static int VIOLATED = 5;
    public static int DEFERRED = 3;
    
    // FOR NOW NOT NEEDED ...
    public static int SATISFIED_LEFT_BIPARTITION = 1;
    public static int SATISFIED_RIGHT_BIPARTITION = 2;

    public static int VIOLATED_LEFT_BIPARTITION = 3;
    public static int VIOLATED_RIGHT_BIPARTITION = 4;

    public static int DEFERRED_LEFT_BIPARTITION = 5;
    public static int DEFERRED_RIGHT_BIPARTITION = 6;
}
