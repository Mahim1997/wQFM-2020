package wqfm.ds;

import java.util.Arrays;
import wqfm.utils.Helper;

/**
 *
 * @author mahim
 */
public class Quartet {

    public static int NUM_TAXA_PER_PARTITION = 2;
    public static int TEMP_TAX_TO_SWAP;

    public int[] taxa_sisters_left;// = new String[NUM_TAXA_PER_PARTITION];
    public int[] taxa_sisters_right;// = new String[NUM_TAXA_PER_PARTITION];
    public double weight;

    public Quartet() {
        this.weight = 1.0;
    }


    public String getNamedQuartet() {
        StringBuilder builder = new StringBuilder();
        builder.append("((");
        builder.append(Helper.getStringMappedName(this.taxa_sisters_left[0]));
        builder.append(",");
        builder.append(Helper.getStringMappedName(this.taxa_sisters_left[1]));
        builder.append("),(");
        builder.append(Helper.getStringMappedName(this.taxa_sisters_right[0]));
        builder.append(",");
        builder.append(Helper.getStringMappedName(this.taxa_sisters_right[1]));
        builder.append(")); ");
        builder.append(Double.toString(this.weight));
        return builder.toString();
    }

    /*
        Keep a,b|c,d as the quartet.
        Keep the minimum of (a,b) in taxa_sisters_left i.e. taxa_sisters_left[0] = min, taxa_sisters_left[1] = max
        Same with (c,d) for taxa_sisters_right.
        FOR NOW, NOT DOING ABOVE THING
     */
    public final void initialiseQuartet(int a, int b, int c, int d, double w) {
        //sorting.

        this.taxa_sisters_left = new int[NUM_TAXA_PER_PARTITION];
        this.taxa_sisters_right = new int[NUM_TAXA_PER_PARTITION];

        this.taxa_sisters_left[0] = a;
        this.taxa_sisters_left[1] = b;
        this.taxa_sisters_right[0] = c;
        this.taxa_sisters_right[1] = d;
        this.weight = w;

        this.sort_quartet_taxa_names();

    }

    public Quartet(Quartet q) {
        initialiseQuartet(q.taxa_sisters_left[0], q.taxa_sisters_left[1],
                q.taxa_sisters_right[0], q.taxa_sisters_right[1], q.weight);
    }

    public Quartet(int a, int b, int c, int d, double w) {
        initialiseQuartet(a, b, c, d, w);
    }

    public Quartet(String s) {
        //ADDITIONALLY append to the map and reverse map.
        s = s.replace(" ", "");
        s = s.replace(";", ",");
        s = s.replace("(", "");
        s = s.replace(")", ""); // Finally end up with A,B,C,D,41.0
        String[] arr = s.split(",");
        int a, b, c, d;
        if (InitialTable.map_of_str_vs_int_tax_list.containsKey(arr[0]) == true) {
            a = InitialTable.map_of_str_vs_int_tax_list.get(arr[0]);
        } else { //THIS taxon doesn't exist.
            a = InitialTable.TAXA_COUNTER;
            InitialTable.TAXA_COUNTER++;
            InitialTable.map_of_str_vs_int_tax_list.put(arr[0], a);
            InitialTable.map_of_int_vs_str_tax_list.put(a, arr[0]);
        }

        if (InitialTable.map_of_str_vs_int_tax_list.containsKey(arr[1]) == true) {
            b = InitialTable.map_of_str_vs_int_tax_list.get(arr[1]);
        } else { //THIS taxon doesn't exist.
            b = InitialTable.TAXA_COUNTER;
            InitialTable.TAXA_COUNTER++;
            InitialTable.map_of_str_vs_int_tax_list.put(arr[1], b);
            InitialTable.map_of_int_vs_str_tax_list.put(b, arr[1]);
        }
        if (InitialTable.map_of_str_vs_int_tax_list.containsKey(arr[2]) == true) {
            c = InitialTable.map_of_str_vs_int_tax_list.get(arr[2]);
        } else { //THIS taxon doesn't exist.
            c = InitialTable.TAXA_COUNTER;
            InitialTable.TAXA_COUNTER++;
            InitialTable.map_of_str_vs_int_tax_list.put(arr[2], c);
            InitialTable.map_of_int_vs_str_tax_list.put(c, arr[2]);
        }
        if (InitialTable.map_of_str_vs_int_tax_list.containsKey(arr[3]) == true) {
            d = InitialTable.map_of_str_vs_int_tax_list.get(arr[3]);
        } else { //THIS taxon doesn't exist.
            d = InitialTable.TAXA_COUNTER;
            InitialTable.TAXA_COUNTER++;
            InitialTable.map_of_str_vs_int_tax_list.put(arr[3], d);
            InitialTable.map_of_int_vs_str_tax_list.put(d, arr[3]);
        }

        initialiseQuartet(a, b, c, d, Double.parseDouble(arr[4]));

//        initialiseQuartet(arr[0], arr[1], arr[2], arr[3], Double.parseDouble(arr[4]));
    }

    @Override
    public String toString() {
        String s = "((" + this.taxa_sisters_left[0] + "," + this.taxa_sisters_left[1] + "),(" + this.taxa_sisters_right[0] + "," + this.taxa_sisters_right[1] + ")); " + String.valueOf(this.weight);
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
//        String[] left = {this.taxa_sisters_left[0], this.taxa_sisters_left[1]};
//        String[] right = {this.taxa_sisters_right[0], this.taxa_sisters_right[1]};

        Arrays.sort(this.taxa_sisters_left);
        Arrays.sort(this.taxa_sisters_right);

        if (this.taxa_sisters_left[0] < this.taxa_sisters_right[0]) { //don't swap two sides
            //no need to swap
        } else {  // swap two sides
            for (int i = 0; i < Quartet.NUM_TAXA_PER_PARTITION; i++) {
                Quartet.TEMP_TAX_TO_SWAP = this.taxa_sisters_left[i];
                this.taxa_sisters_left[i] = this.taxa_sisters_right[i];
                this.taxa_sisters_right[i] = Quartet.TEMP_TAX_TO_SWAP;
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Arrays.hashCode(this.taxa_sisters_left);
        hash = 97 * hash + Arrays.hashCode(this.taxa_sisters_right);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
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

        if (!Arrays.equals(this.taxa_sisters_left, other.taxa_sisters_left)) {
            return false;
        }
        if (!Arrays.equals(this.taxa_sisters_right, other.taxa_sisters_right)) {
            return false;
        }
        return true;
    }

}
