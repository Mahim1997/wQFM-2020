package wqfm.datastructure;

/**
 *
 * @author mahim
 */
public class Quartet {

    public static int NUM_TAXA_PER_PARTITION = 2;

    public String[] taxa_sisters_left = new String[NUM_TAXA_PER_PARTITION];
    public String[] taxa_sisters_right = new String[NUM_TAXA_PER_PARTITION];
    public double weight;

    /*private void initialiseArray() {
        this.taxa_sisters_left = new String[NUM_TAXA_PER_PARTITION];
        this.taxa_sisters_right = new String[NUM_TAXA_PER_PARTITION];
    }*/
    public Quartet() {
        this.weight = 1.0;
    }

    /*
        Keep a,b|c,d as the quartet.
        Keep the minimum of (a,b) in taxa_sisters_left i.e. taxa_sisters_left[0] = min, taxa_sisters_left[1] = max
        Same with (c,d) for taxa_sisters_right.
     */

    public void initialiseQuartet(String a, String b, String c, String d, double w) {
        if (a.compareTo(b) < 0) {
            this.taxa_sisters_left[0] = a;
            this.taxa_sisters_left[1] = b;
        } else {
            this.taxa_sisters_left[0] = b;
            this.taxa_sisters_left[1] = a;
        }

        if (c.compareTo(d) < 0) {
            this.taxa_sisters_right[0] = c;
            this.taxa_sisters_right[1] = d;
        } else {
            this.taxa_sisters_right[0] = d;
            this.taxa_sisters_right[1] = c;
        }
        this.weight = w;
    }

    public Quartet(String a, String b, String c, String d, double w) {
        initialiseQuartet(a, b, c, d, w);
    }

    public Quartet(String newickQuartet) {
        String s = newickQuartet;
        s = s.replace(" ", "");
        s = s.replace(";", ",");
        s = s.replace("(", "");
        s = s.replace(")", ""); // Finally end up with A,B,C,D,41.0
        String[] arr = s.split(",");
        initialiseQuartet(arr[0], arr[1], arr[2], arr[3], Double.parseDouble(arr[4]));
    }

    @Override
    public String toString() {
        String s = "((" + this.taxa_sisters_left[0] + "," + this.taxa_sisters_left[1] + "),(" + this.taxa_sisters_right[0] + "," + this.taxa_sisters_right[1] + "));" + String.valueOf(this.weight);
        return s;
    }

    public void printQuartet() {
        System.out.println(this.toString());
    }

}
