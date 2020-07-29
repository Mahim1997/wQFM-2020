# wQFM-2020
wQFM (Implementation in Java) 


<!-- Headings -->
# wQFM
<!-- Strong -->
wQFM is a quartet amalgamation method of estimating species tree. It takes a set of estimated gene trees as input and generates a set of weighted quartets and combines these weighted quartet trees into a tree on the full set of taxa using a heuristic aimed at finding a species tree of minimum distance to the set of weighted quartet trees.

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

2. Need to have "lib" folder in same path as jar file. (This uses some bytecode from [PhyloNet](https://bioinfocs.rice.edu/phylonet) package by Luay Nakhleh)
    <!--(Check [ASTRAL's github repo](https://github.com/smirarab/ASTRAL) for more details on lib [uses PhyloNet package])-->
    (This is needed to reroot the tree with respect to an outgroup node.)

## Installation
<!-- UL -->
* There is no installation required to run wQFM.jar

* To download, use either of the following approach:

    * You can clone/download the whole repository and use required scripts and wQFM.jar to run the application.

    * Alternatively, you can also download and extract the "wQFM-package.zip" file which contains relevant scripts for generating embedded-weighted-quartets and running wQFM.jar

* wQFM is a java-based application, and hence should run in any environment (Windows, Linux, Mac, etc.) as long as java is installed.

* To generate embedded-weighted-quartets, python needs to be installed. Also, Linux is required for using the tool "triplets.soda2103".

## Running the application.
<!-- OL -->
1.  For generating embedded weighted quartets, use the "quartet-controller.sh" as discussed above.
    
    Make sure "triplets.soda2103" is in the same path (or you have added correct absolute paths) in the "quartet_count.sh" file.

<!-- Code Blocks -->
```bash
  ./quartet-controller.sh "input-gene-tree-file-name" "output-quartet-file-name"
``` 

2. (**Default Mode**) For running the jar file, use java -jar wQFM.jar "input-file-name" "output-file-name"

<!-- Code Blocks -->
  ```bash
      # Default mode, uses bin-ratio heuristic on all levels.
      java -jar wQFM.jar "weighted_quartets" "output-file-name" 
  ```

3. *Additionally*, for running using fixed partition-score (optional input parameters: ALPHA, BETA).

<!-- Code Blocks -->
```bash
  # Uses input values of ALPHA and BETA to calculate partition-score = ALPHA*w[s] - BETA*w[v]

  ## Example, partition-score = 1*w[s] - 0.5*w[v] i.e. ALPHA = 1, BETA = 0.5
  java -jar wQFM.jar "weighted_quartets" "output-file-name" "1" "0.5"
```

4. For large number of taxa, increasing the memory available to Java is recommended. 

<!--**You should give Java only as much free available memory as you have in your machine.** -->

<!--Suppose you have 8GB of free memory, do use the following command to make all the 8GB available to Java:-->

<!-- Code Blocks -->
```bash
  java -Xmx8000M -jar wQFM.jar "weighted_quartets" "output-file-name" ## dynamic ratio-based partition-score

  java -Xmx8000M -jar wQFM.jar "weighted_quartets" "output-file-name" "1" "0.5" ## fixed partition-score
```

5. For now, wQFM cannot handle **stars** which is induced due to polytomy in gene trees.
  
    So, if you do provide stars in input quartet-file, wQFM will terminate (by giving a prompt).

## Datasets
The simulated datasets investigated in this study are found [here](https://sites.google.com/eng.ucsd.edu/datasets/home?authuser=0)

For now, the gene-trees, weighted-quartets and estimated species trees used for analysis are kept [here](https://drive.google.com/drive/folders/1IYKYWG81Sld8QwzZNO5D71mOulGVd7ax?usp=sharing).


As of July 29, 2020, the datasets (used for analysis) contain the following:

| File Name			  |      Description		      |
|---------------------|:-----------------------------:|
| all_gt.tre		  |Estimated/True Gene trees (depending on model condition)|
| weighted_quartets   |Embedded weighted-quartets generated on "all_gt.tre"    |
| wQFM-26-July.tre    |Species tree generated by wQFM (bin-ratio heuristic on all levels) run on "weighted_quartets"|
| wqmc-26-July.tre    |Species tree generated by wQMC run on "weighted_quartets"|
| astral-July26.5.7.3.tre    |Species tree generated by ASTRAL-5.7.3 run on "all_gt.tre"|
| model_tree/true_tree_trimmed |Model Tree **only for simulated datasets**|

Both simulated and biological datasets are present.
| Simulated Datasets (Folder Names)  |      Biological Datasets (Folder Names)     |
|---------------------|:-----------------------------:|
| 11-taxon     |aminota-aa                 |
| 15-taxon     |aminota-nt                 |
| 37-taxon     |mamalian                   |
| 48-taxon     |angiosperm                 |
| 101-taxon    |avian                      |

## Acknowledgement
wQFM uses some methods of the [PhyloNet](https://bioinfocs.rice.edu/phylonet) package for rerooting of unrooted trees with respect to an outgroup.

## Bug Report
We are always looking to improve our codebase. 

For any bugs, please post on [wQFM-2020 issues page](https://github.com/Mahim1997/wQFM-2020/issues).

Alternatively, you can email at ``mahim.mahbub.97@gmail.com`` or ``zahinwahab@gmail.com``.
