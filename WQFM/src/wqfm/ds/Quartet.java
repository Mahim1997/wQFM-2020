package wqfm.ds;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import javafx.util.Pair;

/**
 *
 * @author mahim
 */
public class Quartet {

    public static int NUM_TAXA_PER_PARTITION = 2;

    public String[] taxa_sisters_left;// = new String[NUM_TAXA_PER_PARTITION];
    public String[] taxa_sisters_right;// = new String[NUM_TAXA_PER_PARTITION];
    public double weight;

    public Quartet() {
        this.weight = 1.0;
    }

    /*
        Keep a,b|c,d as the quartet.
        Keep the minimum of (a,b) in taxa_sisters_left i.e. taxa_sisters_left[0] = min, taxa_sisters_left[1] = max
        Same with (c,d) for taxa_sisters_right.
        FOR NOW, NOT DOING ABOVE THING
     */
    public final void initialiseQuartet(String a, String b, String c, String d, double w) {
        //sorting.

        this.taxa_sisters_left = new String[NUM_TAXA_PER_PARTITION];
        this.taxa_sisters_right = new String[NUM_TAXA_PER_PARTITION];

        String[] left = {a, b};
        String[] right = {c, d};
        Arrays.sort(left);
        Arrays.sort(right);
        for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) {
            this.taxa_sisters_left[i] = left[i];
            this.taxa_sisters_right[i] = right[i];
        }
        this.sort_quartet_taxa_names();

        this.weight = w;
    }

    public Quartet(Quartet q) {
        initialiseQuartet(q.taxa_sisters_left[0], q.taxa_sisters_left[1], q.taxa_sisters_right[0], q.taxa_sisters_right[1], q.weight);
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

    /*
    *********** DO NOT USE WEIGHTS IN equals() method **************
    [Only needed for map check with dummy taxon]
     */
 /*    public boolean equals(Object o) {
        // if()
        Quartet q = (Quartet) o;
        return this.taxa_sisters_left[0].equals(q.taxa_sisters_left[0])
                && this.taxa_sisters_left[1].equals(q.taxa_sisters_left[1])
                && this.taxa_sisters_right[0].equals(q.taxa_sisters_right[0])
                && this.taxa_sisters_right[1].equals(q.taxa_sisters_right[1]) && this.weight == q.weight;
    }
     */
    public void sort_quartet_taxa_names() {
        String[] left = {this.taxa_sisters_left[0], this.taxa_sisters_left[1]};
        String[] right = {this.taxa_sisters_right[0], this.taxa_sisters_right[1]};



        if (left[0].compareTo(right[0]) < 0) {
            for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) {
                this.taxa_sisters_left[i] = left[i];
                this.taxa_sisters_right[i] = right[i];
            }
        } else {
            for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) {
                this.taxa_sisters_left[i] = right[i];
                this.taxa_sisters_right[i] = left[i];
            }
        }

        Arrays.sort(this.taxa_sisters_left);
        Arrays.sort(this.taxa_sisters_right);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        this.sort_quartet_taxa_names();
        hash = 97 * hash + Arrays.deepHashCode(this.taxa_sisters_left);
        hash = 97 * hash + Arrays.deepHashCode(this.taxa_sisters_right);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        this.sort_quartet_taxa_names();
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Quartet other = (Quartet) obj;
        other.sort_quartet_taxa_names();

//        System.out.print("-->>Quartet.equals() + " + this.toString() + " , " + other.toString() + "  ");
//        for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) {
//            if (this.taxa_sisters_left[i].equals(other.taxa_sisters_left[i]) == false) {
//                return false;
//            }
//        }
//        for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) {
//            if (this.taxa_sisters_right[i].equals(other.taxa_sisters_right[i]) == false) {
//                return false;
//            }
//        }
        if (!Arrays.deepEquals(this.taxa_sisters_left, other.taxa_sisters_left)) {
            return false;
        }
        if (!Arrays.deepEquals(this.taxa_sisters_right, other.taxa_sisters_right)) {

            return false;
        }

        return true;
    }
}
