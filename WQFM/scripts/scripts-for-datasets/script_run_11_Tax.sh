#!/bin/bash

mode=1; # mode == 1, estimated || else mode == 2, simulated

run()
{
	if [[ $mode -eq 1 ]]; then
		mode_str="ESTIMATED"
		outerFolder="estimated_Xgenes_strongILS"
		innerFolder_arr=('estimated_5genes_strongILS' 'estimated_15genes_strongILS' 'estimated_25genes_strongILS' 'estimated_50genes_strongILS' 'estimated_100genes_strongILS')
		trueTreeName="true_tree_trimmed"
	else
		mode_str="SIMULATED"
		outerFolder="simulated_Xgenes_strongILS"
		innerFolder_arr=('simulated_5genes_strongILS' 'simulated_15genes_strongILS' 'simulated_25genes_strongILS' 'simulated_50genes_strongILS' 'simulated_100genes_strongILS')
		trueTreeName="model_tree"
	fi
	subscript_arr=('5genes' '15genes' '25genes' '50genes' '100genes')

	for (( iter = 0; iter < ${#innerFolder_arr[*]}; iter++ ))
	do
		folderName_init=${innerFolder_arr[$iter]}

		folderName="$outerFolder/$folderName_init"
		subscript_name=${subscript_arr[$iter]}
		
		# touch "$folderName/RF_ASTRAL.txt"
		# java ComputeAverage "$folderName/RF_ASTRAL""_""$mode_str"".txt" "TEMP_OUTPUT.txt" "11"

		for (( i = 1; i <= 20; i++ )); do
			# Rep1_5genes.astral
			wt_file_name="$folderName/R$i/weighted_quartets"
			
			# echo -n "$folderName_init/R$i/wQFM_DYN_NC.3.tre		"
			# java -jar phylonet_v2_4.jar rf -m "$trueTreeName" -e "$folderName/R$i/wQFM_DYN_NC.3.tre"

			# BETA=`python3 bin_dual_include_above_1_cutoff_0_1.py "$folderName/R$i/weighted_quartets" "0.9"`
			# java -jar wQFM.jar "$folderName/R$i/weighted_quartets" "$folderName/R$i/wQFM_DYN_NC.4.tre" "1" "$BETA"

			# echo -n "$folderName_init/R$i/wqrts	"
			# BETA=`python3 bin_two_thresh_0.8_step_0.01_F3_give_alpha_beta.py "$folderName/R$i/weighted_quartets"`

			# java -jar wQFM-left-bin-right-set-to-1-thresh-0.9.jar "$folderName/R$i/weighted_quartets" "$folderName/R$i/wqfm-checking-left-bin-right-set-1.tre"

			# echo -n "$folderName_init/R$i/wqfm-checking-left-bin-right-set-1.tre		"
			# java -jar phylonet_v2_4.jar rf -m "$trueTreeName" -e "$folderName/R$i/wqfm-checking-left-bin-right-set-1.tre"

			# java -jar wQFM-June-30.jar "$folderName/R$i/weighted_quartets" "$folderName/R$i/wQFM-June-30.tre"			

			echo -n "$folderName_init/R$i/wQFM-June-30.tre		"
			java -jar phylonet_v2_4.jar rf -m "$trueTreeName" -e "$folderName/R$i/wQFM-June-30.tre"

		done
		# echo ""

	done
}
# wQFM-June-30

# wQFM-June-30.jar

mode=2 # first simulated
run


mode=1 # second estimated
run
