# wQFM
#### wQFM (version 1.3)
This repository contains the official implementation of <!--code and helper scripts of--> our paper [**"wQFM: Highly Accurate Genome-scale Species Tree Estimation from Weighted Quartets"**](https://academic.oup.com/bioinformatics/advance-article-abstract/doi/10.1093/bioinformatics/btab428/6292084) accepted in ***Bioinformatics, 2021***.

## Notice to all the users
- Codebase has been slightly updated **(on June 12, 2021)** to fix some precision related bugs.
- New version (i.e. the current stable version **v-1.3**) has it fixed. Please use the current version if you have used the jar file/codebase before the aforementioned date.
- New version also includes quartet score outputs (please see below **"To infer quartet scores"** section).

## Short Description

**wQFM** is a quartet amalgamation technique for estimating species trees. 

wQFM combines a set of weighted quartets into a tree on the full set of taxa using a heuristic aimed at finding a species tree of minimum distance to the set of weighted quartets


<!--wQFM uses a two-step technique in which we first use the input set of estimated gene trees to produce a set of weighted four-taxon trees (*weighted quartets*).-->


## Execution dependencies

### Packages, Programming Languages and Operating Systems Requirements
- Java (required to run the main wQFM application).

- Python, Pandas, NumPy, Linux O.S. required to generate weighted quartets. This is done by using the combination of the helper scripts **quartet-controller.sh**, **quartet_count.sh**, **summarize-quartet-counts.py**, **generate_wqrts.py** and the tool **triplets.soda2103** (requires Linux O.S.)

- Python, DendroPy needed for branch annotations while using the helper script **annotate_branches.py**.

### Files Structure

- #### If you download the wQFM-v1.3.zip and extract the contents, all the files will be present in the required structure (described below)

	1. The tool **triplets.soda2103** must be in the same directory as the helper scripts **quartet-controller.sh**, **quartet_count.sh**, **summarize-quartet-counts.py** and **generate_wqrts.py**. Make sure the scripts have executable permission.

	2. Need to have **lib** folder (contains **PhyloNet jar** and **Picocli jar**) in same path as the **wQFM-v1.3.jar** file.

	3. Need to have the python scripts **annotate_branches.py**, **normalize_weights.py**, **compute_quartet_score.py** in the same directory as the jar file.
	

## Input and Output formats for wQFM

### Input

#### Input file containing weighted quartets (default)
<!--**wQFM** takes as input a **set of weighted quartets** in **Newick format**. Each line contains one quartet, followed by its weight.-->
The input is a **set of weighted quartets** in **Newick format**. Each line contains one quartet, followed by its weight.

	((A,B),(C,D)); 34
	((A,C),(B,D)); 125
	((B,C),(D,E)); 431
	((A,B),(E,F)); 256
	((C,D),(E,F)); 992
	((A,C),(E,F)); 121	


#### Input file containing gene trees (optional). See *-im/--input_mode* option as described below.
The input is a **set of gene trees** in **Newick format**. Each line contains one gene tree.

	((3,((11,(10,((5,6),(9,(7,8))))),4)),2,1);
	(3,(2,(4,(11,(9,((7,8),((5,6),10)))))),1);
	(((2,(11,((5,6),((7,8),(10,9))))),3),4,1);
	(2,((11,(10,((8,7),((6,5),9)))),(4,3)),1);
	((11,((10,((7,6),(9,8))),5)),((4,2),3),1);

Weighted quartets (with frequency as weight) will be generated automatically in the file named **input-wqrts-for-jar.wqrts** and wQFM will be run on this weighted quartets file.


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
####  For generating embedded weighted quartets, use the script "quartet-controller.sh".
   
<!-- Code Blocks -->
```bash
./quartet-controller.sh "input-gene-tree-file-name" "output-quartet-file-name"
``` 

<!--### (**Default Mode**) For running the jar file, use the flags -i for input file containing weighted quartets, and -o for the output file name.-->
#### To run using weighted quartets as input file, simply use -i and -o flags

<!-- Code Blocks -->
  ```bash
# Default mode, uses [s] - [v] as partition score.
java -jar wQFM-v1.3.jar -i "input-file-name" -o "output-file-name"
  ```

#### To run directly using gene trees, use -im/--input_mode argument.
  ```bash
# Uses the -im/--input_mode as gene-trees (see Relevant Multiple Options below for details).
java -jar wQFM-v1.3.jar -i "input-file-gene-trees" -o "output-file-name" -im gene-trees
  ```


### To infer branch supports

wQFM can annotate the branches in the output tree with the quartet support which is defined as the number of quartets in the input set of gene trees that agree with a branch.

* Annotating the output species tree with **-t** flag 
```bash
# Annotate branches with average quartet support (the average weights of quartets in your gene trees that agree with a branch)
java -jar wQFM-v1.3.jar -i "input-file-name" -o "output-file-name" -t 1 

# Annotate branches with normalized average quartet support (the proportion of quartets in your gene trees that agree with a branch)
java -jar wQFM-v1.3.jar -i "input-file-name" -o "output-file-name" -t 2 

# Annotate branches with normalized average quartet support (normalized over the most dominant topology's weight, so this option will provide highest possible branch support if all dominant quartets agree with a branch)
java -jar wQFM-v1.3.jar -i "input-file-name" -o "output-file-name" -t 3

## Eg. if you have python setup instead of python3. Now, the scripts will be called using "python <script-name.py> [args]"
java -jar wQFM-v1.3.jar -i "input-file-name" -o "output-file-name" -t 1 -pe python
```
    
* If you want to annotate the branches of a given species tree with quartet support with respect to a set of weighted quartets
```bash
# eg. using annotations level of 1 (use -pe python if you have python setup instead of python3)
java -jar wQFM-v1.3.jar -i "input-file-weighted-quartets" -st "species-tree-without-annotations" -o "species-tree-with-annotations" -t 1
```

### To infer quartet scores
We can use the jar file to compute quartet scores of a reference species tree with respect to a set of weighted quartets. (See Relevant Multiple Options below for details)

* Quartet Scores with **-q** flag to control level of verbosity, and **-qo** to indicate the file path where the quartet scores will be written to (tab separated)
```bash

# Use q = 1 to get only the total weight of quartets that are satisfied by the given produced species tree
# Use q = 2 to get total weight of satisfied quartets, total weight of quartets present in the input wqrts file, and proportion of quartets satisfied.

# Run wQFM input and output using quartet scores
java -jar wQFM-v1.3.jar -i "input-file-name" -o "output-file-name" -q 1 # just prints quartet score details on console.
java -jar wQFM-v1.3.jar -i "input-file-name" -o "output-file-name" -q 1 -qo "qscore-details.txt" # for convenience, dumps to a file.

## Eg. if you have python setup instead of python3. Now, the scripts will be called using "python <script-name.py> [args]"
java -jar wQFM-v1.3.jar -i "input-file-name" -o "output-file-name" -q 2 -pe python
```
    
* If you want to find the quartet score without generating species tree (i.e. without running wQFM algorithm)
```bash
# Just to get the quartet scores without generating any estimated species tree

java -jar wQFM-v1.3.jar -i "input-file-weighted-quartets" -st "species-tree-file" -q 2 # to print on console
java -jar wQFM-v1.3.jar -i "input-file-weighted-quartets" -st "species-tree-file" -q 2 -qo "qscore-details.txt" # to dump to a file
```

#### Relevant Multiple Options

```bash
-i, --input_file=<inputFileName>
	The input file name/path
	(default: for weighted quartets, see option -im/--input_mode for details)
	
-o, --output_file=<outputFileNameSpeciesTree>
	The output file name/path for (estimated) species tree

-im, --input_mode=<inputFileMode>
                  im=<weighted-quartets> (default)
                  im=<gene-trees> when input file consists of gene trees

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

-q, --quartet_score_level=<quartetScoreLevel>
	q=0: do not show quartet score(default)
	q=1: show quartet score only
	q=2: show quartet score, total weight of quartets, proportion of quartets satisfied

-qo, --quartet_score_output_file=<quartetScoreOutputFile>
    (default) null
    If given, quartet scores will be output here.

-pe, --python_engine=<pythonEngine>
	(default) python3
	(otherwise) python
	i.e. If you have "python" setup in your O.S., then scripts will be run using "python <script.py> [args]"


-h, --help      Show this help message and exit.

-V, --version   Print version information and exit.
```


#### For large number of taxa, increasing the memory available to Java is recommended. 
```bash
# Example: To supply 8GB of free memory.

java -Xmx8000M -jar wQFM-v1.3.jar -i "input-file-name" -o "output-file-name" 
```

#### For now, wQFM cannot handle **stars** which is induced due to polytomy in gene trees.
  
So, if you do provide stars in input quartet-file, wQFM will terminate (after giving a prompt).

	eg. (a,b,c,d); 10
	This will be produced as a "quartet" if a star is present in the initial gene tree.
	If wQFM is run and the input weighted quartets file contains such a star, then wQFM will terminate giving a prompt.

## Datasets
The simulated datasets investigated in this study are available [here](https://sites.google.com/eng.ucsd.edu/datasets/home?authuser=0)

The input gene trees, corresponding weighted quartets and the estimated species trees are available [here](https://drive.google.com/drive/folders/1IYKYWG81Sld8QwzZNO5D71mOulGVd7ax?usp=sharing).


As of May 1, 2021, the datasets (used for analysis) contain the following:

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
| model_tree/true_tree_trimmed |Model Tree **only for simulated datasets**|
| wQFM-v1.2-all.tre    |Species tree generated by wQFM-v1.2 (and wQFM-v1.3) run on "weighted_quartets"|
| wqmc-26-July.tre    |Species tree generated by wQMC run on "weighted_quartets"|
| astral-July26.5.7.3.tre    |Species tree generated by ASTRAL-5.7.3 run on "all_gt.tre"|
| wQFM-v1.2-best.tre    |Species tree generated by wQFM-v1.2 (and wQFM-v1.3) run on "best_weighted_quartets" i.e. dominant quartets|
| qfm-best.tre    |Species tree generated by QFM run on "best_weighted_quartets" with weights as 1|
| wQFM-26-July.tre    |Species tree generated by wQFM-v1.1 (bin-ratio heuristic on all levels) run on "weighted_quartets"|


Additionally, the folder **Bootstrapped-Biological-Datasets** provides the species trees estimated by all three methods (wQFM, ASTRAL, wqmc),
as well as includes the bootstrap supports on the species trees estimated by bestML gene trees.


## Acknowledgements
- wQFM uses some methods in the [PhyloNet](https://bioinfocs.rice.edu/phylonet) package for rerooting a tree with respect to an outgroup.
    
    C. Than, D. Ruths, L. Nakhleh (2008) PhyloNet: A software package for analyzing and reconstructing reticulate evolutionary histories, BMC Bioinformatics 9:322.

- For generating embedded weighted quartets, the tool **triplets.soda2103** is used.

	Gerth Stølting Brodal, Rolf Fagerberg, Thomas Mailund, Christian N. S. Pedersen, and Andreas Sand. 2013. Efficient algorithms for computing the triplet and quartet distance between trees of arbitrary degree. In Proceedings of the twenty-fourth annual ACM-SIAM symposium on Discrete algorithms (SODA '13). Society for Industrial and Applied Mathematics, USA, 1814–1832.

- Certain additional scripts (branch-support calculation, bootstrap support calculation) use [DendroPy](http://dendropy.org/).
    
    Sukumaran, J. and Mark T. Holder. The DendroPy Phylogenetic Computing Library Documentation. Retrieved 01/02/2021, from http://dendropy.org/.
    
- Arguments parsing is done using **picocli**.
[![picocli](https://img.shields.io/badge/picocli-4.6.1-green.svg)](https://github.com/remkop/picocli)

## License
The contents of this repository are licensed under the **Apache License, Version 2.0**.

See [LICENSE.md](https://github.com/Mahim1997/wQFM-2020/blob/master/LICENSE.md) for the full license text.

## Citation (BibTeX)
If you wish to use any part of this repository, please do cite our paper.

```
@article{10.1093/bioinformatics/btab428,
    author = {Mahbub, Mahim and Wahab, Zahin and Reaz, Rezwana and Rahman, M Saifur and Bayzid, Md. Shamsuzzoha},
    title = "{wQFM: Highly Accurate Genome-scale Species Tree Estimation from Weighted Quartets}",
    journal = {Bioinformatics},
    year = {2021},
    month = {06},
    abstract = "{Species tree estimation from genes sampled from throughout the whole genome is complicated due to the gene tree-species tree discordance. Incomplete lineage sorting (ILS) is one of the most frequent causes for this discordance, where alleles can coexist in populations for periods that may span several speciation events. Quartet-based summary methods for estimating species trees from a collection of gene trees are becoming popular due to their high accuracy and statistical guarantee under ILS. Generating quartets with appropriate weights, where weights correspond to the relative importance of quartets, and subsequently amalgamating the weighted quartets to infer a single coherent species tree can allow for a statistically consistent way of estimating species trees. However, handling weighted quartets is challenging.We propose wQFM, a highly accurate method for species tree estimation from multi-locus data, by extending the quartet FM (QFM) algorithm to a weighted setting. wQFM was assessed on a collection of simulated and real biological datasets, including the avian phylogenomic dataset which is one of the largest phylogenomic datasets to date. We compared wQFM with wQMC, which is the best alternate method for weighted quartet amalgamation, and with ASTRAL, which is one of the most accurate and widely used coalescent-based species tree estimation methods. Our results suggest that wQFM matches or improves upon the accuracy of wQMC and ASTRAL.wQFM is available in open source form at https://github.com/Mahim1997/wQFM-2020Supplementary data are available at Bioinformatics online.}",
    issn = {1367-4803},
    doi = {10.1093/bioinformatics/btab428},
    url = {https://doi.org/10.1093/bioinformatics/btab428},
    note = {btab428},
    eprint = {https://academic.oup.com/bioinformatics/advance-article-pdf/doi/10.1093/bioinformatics/btab428/38489242/btab428.pdf},
}
```

## Bug Report
We are always looking to improve our codebase. 

For any bugs, please post on [wQFM-2020 issues page](https://github.com/Mahim1997/wQFM-2020/issues).

Alternatively, you can email at ``mahim.mahbub.97@gmail.com`` or ``zahinwahab@gmail.com``.
