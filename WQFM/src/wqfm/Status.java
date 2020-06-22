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
    
    //Free or locked [arbitrary values]
    public static int FREE = 22;
    public static int LOCKED = 31;
    
    // FOR NOW NOT NEEDED ...
    public static int SATISFIED_LEFT_BIPARTITION = 11;
    public static int SATISFIED_RIGHT_BIPARTITION = 12;

    public static int VIOLATED_LEFT_BIPARTITION = 13;
    public static int VIOLATED_RIGHT_BIPARTITION = 14;

    public static int DEFERRED_LEFT_BIPARTITION = 15;
    public static int DEFERRED_RIGHT_BIPARTITION = 16;
}
