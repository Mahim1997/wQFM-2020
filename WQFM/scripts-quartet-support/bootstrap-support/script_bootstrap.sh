#!/bin/bash

inputFileNameBootstrapTrees="$1"
targetSpeciesTreeFileName="$2"
outputFileName="$3"


## Find nexus file.
python3 sumtrees.py -t "$targetSpeciesTreeFileName" -o "$outputFileName.nexus" "$inputFileNameBootstrapTrees"

python3 convert_to_newick.py "$outputFileName.nexus" "$outputFileName"