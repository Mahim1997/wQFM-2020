#!/bin/bash

inputFileName="$1"
outputFileName="$2"

echo "Input file is $inputFileName, Output file is $outputFileName"

java -jar convertForWQMC_BufferedReader.jar "$inputFileName" "TEMP_WQMC_INPUT_FILE"
# java -jar convertForWQMC.jar "$folderName/R$i/weighted_quartets" "TEMP_WQMC_INPUT_FILE"

echo "Now running max-cut-tree"

./max-cut-tree qrtt="TEMP_WQMC_INPUT_FILE" weights=on otre="TEMP_WQMC_OUTPUT_FILE"

# java -jar convertBack.jar "TEMP_WQMC_OUTPUT_FILE" "$folderName/R$i/wqmc.tre"
java -jar convertBack.jar "TEMP_WQMC_OUTPUT_FILE" "$outputFileName"
			