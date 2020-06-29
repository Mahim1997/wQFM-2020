# wQFM-2020
wQFM implementation in Java 


<!-- Headings -->
# wQFM
<!-- Strong -->
wQFM is a quartet amalgamation method of estimating species tree. It takes set of estimated gene trees as input and generates set of weighted quartets (statistically consistent tree of four taxa) and combine these weighted quartet trees into a tree on the full set of taxa using a heuristic aimed at finding a species tree of minimum distance to the set of weighted quartet trees.

## Background
Species tree estimation from genes sampled from throughout the whole genome is complicated due to the gene tree-species tree discordance. Incomplete Lineage Sorting (ILS) is one of the most frequent causes for this discordance.
Quartet-based summary methods for estimating species trees from a collection of
gene trees are becoming popular due to their high accuracy and statistical guarantee
under ILS.

We present wQFM, a highly accurate method for species tree estimation
from a collection of gene trees by extending the quartet FM (QFM) algorithm to
handle weighted quartets. wQFM was assessed on a collection of simulated and real
biological dataset including the avian phylogeneomic dataset which is one of the
largest phylongenomic dataset to date. We compared wQFM to wQMC, which is
the best alternate method for weighted quartet amalgamation, and with ASTRAL
which is considered to be the most accurate and widely used species tree estimation
method. Our results suggest that wQFM matches or improves upon the accuracies
of wQMC and ASTRAL.

## Execution dependencies
<!-- OL -->
1. Please change the paths in the file "quartet_count.sh".
   The path in "quartet_count.sh" file must be changed to where the tool "triplets.soda2103" is kept.
   <!-- Code Blocks -->
   ```bash
   # Eg. In line 10, the triplets.soda2103 is kept in the directory "/home/mahim/gene-tree-tools/"
   # Change it accordingly into where triplets.soda2103 is kept in your directory structure.
      cat $1| xargs -I@ sh -c 'echo -n "@" >'$tmp'; /home/mahim/gene-tree-tools/triplets.soda2103 printQuartets '$tmp';'|sed 's/.*: //'| sed 's/^/\(\(/'| sed 's/$/\)\)\;/'| sed 's/ | /\),\(/'| sed 's/ /\,/g'
   ```

2. Need to have "lib" folder in same path as jar file. (Check github/astral at https://github.com/smirarab/ASTRAL for more details on lib [uses phylonet package])
(This is needed to reroot the tree with respect to an outgroup node.)

## Command Line Arguments for running jar file.
<!-- OL -->
For running the jar file, use java -jar wQFM.jar <input-file-name> <output-file-name> [ALPHA] [BETA]

<!-- Code Blocks -->
  ```bash
      
    java -jar wQFM.jar "weighted_quartets" "output-file-name" # for using the whole dynamic ratio-feature partition-score calculation.
      
    # for running without dynamic ratio-feature partition-score calculation, and simply using the input values of ALPHA and BETA to calculate partition-score = ALPHA*w[s] - BETA*w[v]
    java -jar wQFM.jar "weighted_quartets" "output-file-name" "1" "0.5" 
 ```

