#!/bin/bash
# outerFolder_arr=('100gene-100bp' '100gene-1000bp' '1000gene-100bp' '1000gene-1000bp')
trueTreeName="true_tree_trimmed"
outerFolder_arr=('100gene-100bp/estimated-genetrees' '100gene-1000bp/estimated-genetrees' '1000gene-100bp/estimated-genetrees' '1000gene-1000bp/estimated-genetrees' '100gene-true' '1000gene-true')

# outerFolder_arr=('100gene-100bp/estimated-genetrees' '100gene-1000bp/estimated-genetrees' '1000gene-100bp/estimated-genetrees' '1000gene-1000bp/estimated-genetrees')
num_genes_arr=('100' '100' '1000' '1000' '100' '1000')
# echo -n "" > "RF_15_tax_v2"
# echo -n "" > "RF_15_tax_v3"

# g++ WQFM_Final_newInitial.cpp -o wqfm_new_init
generateWQRTS()
{
	# new_folders=('100gene-100bp/estimated-genetrees' '100gene-1000bp/estimated-genetrees' '1000gene-100bp/estimated-genetrees' '1000gene-1000bp/estimated-genetrees')
	new_folders=('1000gene-true')
	for (( iter = 0; iter < ${#new_folders[*]}; iter++ ))
	do
		folderName=${new_folders[$iter]}
		for i in {1..10}; do
			./quartet-controller.sh "$folderName/R$i/ALL_GT_NEW.tre" "$folderName/R$i/weighted_quartets"
			# rm -f "$folderName/R$i/ALL_GT_NEW.tre"
			# cp "$folderName/R$i/all_gt.tree" "$folderName/R$i/ALL_GT_NEW.tre"
		done
	done	
}

run_WQMC()
{
	javac *.java

	for (( iter = 0; iter < ${#outerFolder_arr[*]}; iter++ ))
	do
		folderName=${outerFolder_arr[$iter]}
		for (( i = 1; i <= 10; i++ )); do
			wt_file_name="$folderName/R$i/weighted_quartets"
			output_file_name="$folderName/R$i/wqmc.tre"

			java ConvertForQMC "$wt_file_name" "TEMP_WQMC_INPUT_FILE"
			./max-cut-tree qrtt="TEMP_WQMC_INPUT_FILE" weights=on otre="TEMP_WQMC_OUTPUT_FILE"
			java ConvertBack "TEMP_WQMC_OUTPUT_FILE" "$output_file_name"

		done
	done
	rm -f *.class
}


computeRF_ASTRAL_June9()
{
	echo -n "" > "RF_15Tax_ASTRAL_June9_EXACT_5.7.3.txt"
	for (( iter = 0; iter < ${#outerFolder_arr[*]}; iter++ ))
	do
		# R1.astral
		folderName=${outerFolder_arr[$iter]}
		for (( i = 1; i <= 10; i++ )); do
			echo "Running for $folderName/R$i"

			echo -n "$folderName/R$i/astral.5.7.3_EXACT.tre		" >> "RF_15Tax_ASTRAL_June9_EXACT_5.7.3.txt"
			java -jar phylonet_v2_4.jar rf -m "$trueTreeName" -e "$folderName/R$i/astral.5.7.3_June9.tre" >> "RF_15Tax_ASTRAL_June9_EXACT_5.7.3.txt"
		done
	done	
}


runWQFM_JAR()
{
	outerFolder_arr=('100gene-100bp/estimated-genetrees' '100gene-1000bp/estimated-genetrees' '1000gene-100bp/estimated-genetrees' '1000gene-1000bp/estimated-genetrees' '100gene-true' '1000gene-true')
	printFolderArr=('100gene-100bp' '100gene-1000bp' '1000gene-100bp' '1000gene-1000bp' '100gene-true' '1000gene-true')

	for (( iter = 0; iter < ${#outerFolder_arr[*]}; iter++ ))
	do
		folder_to_print=${printFolderArr[$iter]}
		folderName=${outerFolder_arr[$iter]}
		for (( i = 1; i <= 10; i++ )); do
			wt_file_name="$folderName/R$i/weighted_quartets"
			
			# echo -n "$folder_to_print/R$i/wQFM_DYN_NC.3.tre		"
			# java -jar phylonet_v2_4.jar rf -m "true_tree_trimmed" -e "$folderName/R$i/wQFM_DYN_NC.3.tre"
			
			# BETA=`python3 bin_dual_include_above_1_cutoff_0_1.py "$wt_file_name" "0.75"`

			# java -jar wQFM-June-30.jar "$folderName/R$i/weighted_quartets" "$folderName/R$i/wQFM-June-30.tre"
			
			echo -n "$folder_to_print/R$i/wQFM-June-30.tre		"
			java -jar phylonet_v2_4.jar rf -m true_tree_trimmed -e "$folderName/R$i/wQFM-June-30.tre"

		done
	done	

}




# --------------------------------------------------------------------------------------

runWQFM_JAR
