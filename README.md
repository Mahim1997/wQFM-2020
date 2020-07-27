# wQFM-2020
wQFM (Implementation in Java) 


<!-- Headings -->
# wQFM
<!-- Strong -->
wQFM is a quartet amalgamation method of estimating species tree. It takes set of estimated gene trees as input and generates set of weighted quartets (statistically consistent tree of four taxa) and combines these weighted quartet trees into a tree on the full set of taxa using a heuristic aimed at finding a species tree of minimum distance to the set of weighted quartet trees.

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
1. The tool "triplets.soda2103" must be in the same directory as "quartet-controller.sh".
    
    Other scripts such as "quartet_count.sh" and "summarize-quartet-counts.py" should be in the same directory as "quartet-controller.sh" and "triplets.soda2103".
    
    If you want to keep it in another path, please change to absolute path of "triplets.soda2103" in "quartet_count.sh".
    
    <!-- Code Blocks -->
    ```bash
        # we can also use absolute path eg. /home/gene-trees/triplets.soda2103 instead of keeping the tool in the same directory.
        cat $1| xargs -I@ sh -c 'echo -n "@" >'$tmp'; ./triplets.soda2103 printQuartets '$tmp';'|sed 's/.*: //'| sed 's/^/\(\(/'| sed 's/$/\)\)\;/'| sed 's/ | /\),\(/'| sed 's/ /\,/g'
    ```

2. Need to have "lib" folder in same path as jar file. 
    (Check [ASTRAL's github repo](https://github.com/smirarab/ASTRAL) for more details on lib [uses PhyloNet package])
    (This is needed to reroot the tree with respect to an outgroup node.)


## Running the application.
<!-- OL -->
1.  For generating embedded weighted quartets, use the "quartet-controller.sh" as discussed above.
    
    Make sure "triplets.soda2103" is in the same path (or you have added correct absolute paths) in the "quartet_count.sh" file.

    <!-- Code Blocks -->
    ```bash
      ./quartet-controller.sh "input-gene-tree-file-name" "output-quartet-file-name"
    ``` 

2. For running the jar file, use java -jar wQFM.jar "input-file-name" "output-file-name" [ALPHA] [BETA]

    For running using whole dynamic bin-ratio-heuristic partition-score calculation
    <!-- Code Blocks -->
      ```bash
          java -jar wQFM.jar "weighted_quartets" "output-file-name" 
      ```
    For running using fixed partition-score (input params: ALPHA, BETA) where partition-score = ALPHA.w[s] - BETA.w[v]
  
    <!-- Code Blocks -->
      ```bash
          # Uses input values of ALPHA and BETA to calculate partition-score = ALPHA*w[s] - BETA*w[v]

          ## Example, partition-score = 1*w[s] - 0.5*w[v] i.e. ALPHA = 1, BETA = 0.5
          java -jar wQFM.jar "weighted_quartets" "output-file-name" "1" "0.5"
     ```

3. For large number of taxa, increasing the memory available to Java is recommended. 

    **You should give Java only as much free available memory as you have in your machine.** 

    Suppose you have 8GB of free memory, do use the following command to make all the 8GB available to Java:

    <!-- Code Blocks -->
    ```bash
      java -Xmx8000M -jar wQFM.jar "weighted_quartets" "output-file-name" ## dynamic ratio-based partition-score

      java -Xmx8000M -jar wQFM.jar "weighted_quartets" "output-file-name" "1" "0.5" ## fixed partition-score
    ```

4. For now, wQFM cannot handle **stars** which is induced due to polytomy in gene trees.
  
    So, if you do provide stars in input quartet-file, wQFM will terminate (by giving a prompt).


## Acknowledgement
wQFM uses some methods of the PhyloNet package for rerooting of unrooted trees with respect to an outgroup.
