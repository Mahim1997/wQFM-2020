# wQFM-2020
**wQFM (Version 1.2)**


<!-- Headings -->
# wQFM
<!-- Strong -->
wQFM is a quartet amalgamation method. <!--for estimating species trees.--> 
<!--It takes a set of estimated gene trees as input and generates a set of weighted quartets and combines these weighted quartet trees into a tree on the full set of taxa using a heuristic aimed at finding a species tree of minimum distance to the set of weighted quartet trees.
-->

<!--
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
-->

## Execution dependencies
<!-- OL -->
1. The tool "triplets.soda2103" must be in the same directory as "quartet-controller.sh". Helper scripts such as "quartet_count.sh", "summarize-quartet-counts.py" should be in the same directory as "quartet-controller.sh" and "triplets.soda2103".

2. Need to have "lib" folder in same path as jar file. (This uses some bytecode from [PhyloNet](https://bioinfocs.rice.edu/phylonet) package by Luay Nakhleh)
    <!--(Check [ASTRAL's github repo](https://github.com/smirarab/ASTRAL) for more details on lib [uses PhyloNet package])-->
    (This is needed to reroot the tree with respect to an outgroup.)
    
3. Need to have the python scripts "annotate_branches.py", "normalize_weights.py" in the same directory as the jar file.


## Installation
<!-- UL -->
* There is no installation required to run wQFM-v1.jar

* To download, use either of the following approach:

    * You can clone/download the whole repository and use required scripts and wQFM-v1.jar to run the application.

    * Alternatively, you can also download and extract the "wQFM-v1.2.zip" file which contains relevant scripts for generating embedded-weighted-quartets and running wQFM-v1.2.jar

* wQFM is a java-based application, and hence should run in any environment (Windows, Linux, Mac, etc.) as long as java is installed.

* To generate embedded-weighted-quartets, **Python** and some modules such as **pandas** and **numpy** need to be installed.

* Linux O.S. is required for using the tool "triplets.soda2103" to generate embedded-weighted-quartets.

* To use branch annotations, the script "annotate_branches.py" uses **DendroPy**. If you would like to use branch annotations, do setup **Python** and **DendroPy**.

## Input and Output formats for wQFM

### Input
wQFM takes as input a **set of weighted quartets** in **Newick format**. Each line contains one quartet, followed by its weight.

	((A,B),(C,D)); 34
	((A,C),(B,D)); 125
	((B,C),(D,E)); 431
	((A,B),(E,F)); 256
	((C,D),(E,F)); 992
	((A,C),(E,F)); 121	

### Output
A **newick tree** with or without **branch support** (multiple annotation levels as described below)

	- Without branch support (annotation level 0)
	((D,(B,(A,C))),(E,F));
	
	- With branch support measured as the average no of quartets that agree with a branch (annotation level 1)
	((D,(B,(A,C)123.0)269.3333333333333)456.3333333333333,(E,F)456.3333333333333);

	- With branch support measured as the proportion of quartets in your gene trees that agree with a branch (annotation level 2)
	((D,(B,(A,C)0.8930817610062893)1.0)1.0,(E,F)1.0);
	
	- With branch support measured as the proportion of quartets in gene trees, weights being normalized by the most dominant topology's weight (annotation level 3)
	((D,(B,(A,C)1.0)1.0)1.0,(E,F)1.0);


## Running the application.
<!-- OL -->
1.  For generating embedded weighted quartets, use the "quartet-controller.sh" as discussed above.
    
    Make sure "triplets.soda2103" is in the same path (or you have added correct absolute paths) in the "quartet_count.sh" file.

<!-- Code Blocks -->
```bash
  ./quartet-controller.sh "input-gene-tree-file-name" "output-quartet-file-name"
``` 

2. (**Default Mode**) For running the jar file, use the flags -i for input file containing weighted quartets, and -o for the output file name.

<!-- Code Blocks -->
  ```bash
      # Default mode, uses [s] - [v] as partition score.
      java -jar wQFM-v1.2.jar -i "input-file-name" -o "output-file-name"
  ```

3. **To infer branch supports**

wQFM can annotate the branches in the output tree with the quartet support which is defined as the number of quartets in the input set of gene trees that agree with a branch.

* Annotating the output species tree with -t flag 
```bash
# Annotate branches with average quartet support (the average weights of quartets in your gene trees that agree with a branch)
java -jar wQFM-v1.2.jar -i "input-file-name" -o "output-file-name" -t 1 

# Annotate branches with normalized average quartet support (the proportion of quartets in your gene trees that agree with a branch)
java -jar wQFM-v1.2.jar -i "input-file-name" -o "output-file-name" -t 2 

# Annotate branches with normalized average quartet support (normalized over the most dominant topology's weight, so this option will provide highest possible branch support if all dominant quartets agree with a branch)
java -jar wQFM-v1.2.jar -i "input-file-name" -o "output-file-name" -t 3

## Eg. if you have python setup instead of python3. Now, the scripts will be called using "python ..."
java -jar wQFM-v1.2.jar -i "input-file-name" -o "output-file-name" -t 1 -pe python
```
    
* If you want to annotate the branches of a given species tree with quartet support with respect to a set of weighted quartets
```bash
# eg. using annotations level of 1 (use -pe python if you have python setup instead of python3)
java -jar wQFM-v1.2.jar -i "input-file-weighted-quartets" -st "species-tree-without-annotations" -o "species-tree-with-annotations" -t 1
```

4. For large number of taxa, increasing the memory available to Java is recommended. 
```bash
    # Example: To supply 8GB of free memory.
    
    java -Xmx8000M -jar wQFM-v1.2.jar -i "input-file-name" -o "output-file-name" 
```

5. *Relevant Multiple Options*

```bash
-i, --input_file=<inputFileNameWeightedQuartets>
	The input file name/path for weighted quartets
	
-o, --output_file=<outputFileNameSpeciesTree>
	The output file name/path for (estimated) species tree

-t, --annotations_level=<annotationsLevel>
	t=0 for none (default)
	t=1 for annotations using quartet support
	t=2 for annotations using quartet support normalized by sum
	t=3 for annotations using quartet support nomralized by max

-st, --species_tree=<speciesTreeFileName>
	If given, will run annotations and provide to output file
	(will NOT run wQFM)

-beta, --partition_score_beta=<beta>
	(default) then beta = 1, hence [s]-[v] used
	beta=<BETA> for 1[ws] - <BETA>[wv] partition score
	beta="dyanmic" then dynamic bin heuristic is used.

-h, --help      Show this help message and exit.

-pe, --python_engine=<pythonEngine>
	(default) python3
	(otherwise) python
	i.e. If you have "python" setup in your O.S., then scripts will be run using "python <script.py>"

-V, --version   Print version information and exit.
```


6. For now, wQFM cannot handle **stars** which is induced due to polytomy in gene trees.
  
    So, if you do provide stars in input quartet-file, wQFM will terminate (after giving a prompt).



## Datasets
The simulated datasets investigated in this study are available [here](https://sites.google.com/eng.ucsd.edu/datasets/home?authuser=0)

The input gene trees, corresponding weighted quartets and the estimated species trees are available here [link](https://drive.google.com/drive/folders/1IYKYWG81Sld8QwzZNO5D71mOulGVd7ax?usp=sharing).


As of July 29, 2020, the datasets (used for analysis) contain the following:

| Simulated Datasets  |      Biological Datasets  |
|---------------------|:-----------------------------:|
| 11-taxon     |aminota-aa                 |
| 15-taxon     |aminota-nt                 |
| 37-taxon     |mamalian                   |
| 48-taxon     |angiosperm                 |
| 101-taxon    |avian                      |

The following files are available for each aforementioned datasets.

| File Name			  |      Description		      |
|---------------------|:-----------------------------:|
| all_gt.tre		  |Estimated/True Gene trees (depending on model condition)|
| weighted_quartets   |Embedded weighted-quartets generated on "all_gt.tre"    |
| wQFM-26-July.tre    |Species tree generated by wQFM (bin-ratio heuristic on all levels) run on "weighted_quartets"|
| wqmc-26-July.tre    |Species tree generated by wQMC run on "weighted_quartets"|
| astral-July26.5.7.3.tre    |Species tree generated by ASTRAL-5.7.3 run on "all_gt.tre"|
| model_tree/true_tree_trimmed |Model Tree **only for simulated datasets**|




## Acknowledgements
- wQFM uses some methods in the [PhyloNet](https://bioinfocs.rice.edu/phylonet) package for rerooting a tree with respect to an outgroup.
    
    C. Than, D. Ruths, L. Nakhleh (2008) PhyloNet: A software package for analyzing and reconstructing reticulate evolutionary histories, BMC Bioinformatics 9:322.

- For generating embedded weighted quartets, the tool **triplets.soda2103** is used.

	Gerth Stølting Brodal, Rolf Fagerberg, Thomas Mailund, Christian N. S. Pedersen, and Andreas Sand. 2013. Efficient algorithms for computing the triplet and quartet distance between trees of arbitrary degree. In Proceedings of the twenty-fourth annual ACM-SIAM symposium on Discrete algorithms (SODA '13). Society for Industrial and Applied Mathematics, USA, 1814–1832.

- Certain additional scripts (branch-support calculation, bootstrap support calculation) use [DendroPy](http://dendropy.org/).
    
    Sukumaran, J. and Mark T. Holder. The DendroPy Phylogenetic Computing Library Documentation. Retrieved 01/02/2021, from http://dendropy.org/.
    
- Arguments parsing is done using **picocli**.
[![picocli](https://img.shields.io/badge/picocli-4.6.1-green.svg)](https://github.com/remkop/picocli)


## Bug Report
We are always looking to improve our codebase. 

For any bugs, please post on [wQFM-2020 issues page](https://github.com/Mahim1997/wQFM-2020/issues).

Alternatively, you can email at ``mahim.mahbub.97@gmail.com`` or ``zahinwahab@gmail.com``.
