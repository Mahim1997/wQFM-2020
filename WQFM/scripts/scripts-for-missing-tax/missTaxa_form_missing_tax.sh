
# innerFolder_arr=('estimated_5genes_strongILS' 'estimated_15genes_strongILS' 'estimated_25genes_strongILS' 'estimated_50genes_strongILS' 'estimated_100genes_strongILS')

# innerFolder_arr=('estimated_5genes_strongILS')

subscript_arr=('5genes' '15genes' '25genes' '50genes' '100genes')

copy_to_all_gt()
{
	if [[ $mode -eq 1 ]]; then
		mode_str="ESTIMATED"
		outerFolder="estimated_Xgenes_strongILS"
		innerFolder_arr=('estimated_5genes_strongILS' 'estimated_15genes_strongILS' 'estimated_25genes_strongILS' 'estimated_50genes_strongILS' 'estimated_100genes_strongILS')
	else
		mode_str="SIMULATED"
		outerFolder="simulated_Xgenes_strongILS"
		innerFolder_arr=('simulated_5genes_strongILS' 'simulated_15genes_strongILS' 'simulated_25genes_strongILS' 'simulated_50genes_strongILS' 'simulated_100genes_strongILS')
	fi
	for (( iter = 0; iter < ${#innerFolder_arr[*]}; iter++ ))
	do
		folderName=${innerFolder_arr[$iter]}
		# folderName="$folderName"
		folderName="$outerFolder/$folderName"
		subscript_name=${subscript_arr[$iter]}

		for i in {1..20}; do
			# gt_fileName="$folderName/Rep$i""_""$subscript_name"
			gt_name_now="$folderName/R$i/Rep$i""_""$subscript_name"
			gt_final_name="$folderName/R$i/all_gt.tre"
			# echo "$gt_name_now"
			# cp src dest
			cp "$gt_name_now" "$gt_final_name"
		done
		echo

	done

}

# mode=1
# copy_to_all_gt

# mode=2
# copy_to_all_gt


### ABOVE function only copies the Rep_names to all_gt.tre


##############################################################################################################################
##############################################################################################################################
##############################################################################################################################

formGTs()
{
	if [[ $mode -eq 1 ]]; then
		mode_str="ESTIMATED"
		outerFolder="estimated_Xgenes_strongILS"
		
		innerFolder_arr=('estimated_5genes_strongILS' 'estimated_15genes_strongILS' 'estimated_25genes_strongILS' 'estimated_50genes_strongILS' 'estimated_100genes_strongILS')
		# innerFolder_arr=('estimated_5genes_strongILS' 'estimated_15genes_strongILS' 'estimated_25genes_strongILS' 'estimated_50genes_strongILS' 'estimated_100genes_strongILS')
		
		true_tree_name='model_tree_trimmed'
	else
		mode_str="SIMULATED"
		outerFolder="simulated_Xgenes_strongILS"
		innerFolder_arr=('simulated_5genes_strongILS' 'simulated_15genes_strongILS' 'simulated_25genes_strongILS' 'simulated_50genes_strongILS' 'simulated_100genes_strongILS')
		true_tree_name='model_tree'
	fi
	for (( iter = 0; iter < ${#innerFolder_arr[*]}; iter++ ))
	do
		folderName=${innerFolder_arr[$iter]}
		folder_to_print="$folderName"
		# folderName="$folderName"
		folderName="$outerFolder/$folderName"
		
		file_name_wqfm_RF="RF_MISSING_WQFM_11Tax"
		file_name_wqmc_RF="RF_MISSING_WQMC_11Tax"

		subscript_name=${subscript_arr[$iter]}

		for i in {1..20}; do
			# gt_fileName="$folderName/Rep$i""_""$subscript_name"
			innerFolder="$folderName/R$i"
			inputGTFileName="$innerFolder/all_gt.tre"
			# echo "$inputGTFileName"
			for (( percent_min = 10; percent_min <= 40; percent_min=percent_min + 10 )); do
				percent_max=$((percent_min + 10))
				gt_file_name="$innerFolder/all_gt_""$percent_min"_"$percent_max.tre"

				python3 removerTaxa.py "$inputGTFileName" "$gt_file_name" "$percent_min" "$percent_max"
				wrt_file_name="$innerFolder/weighted_quartets_""$percent_min"_"$percent_max"

				./quartet-controller.sh "$gt_file_name" "$wrt_file_name"

				output_wqfm_file="$innerFolder/wqfm_""$percent_min"_"$percent_max.tre"
				./wqfm_v1 "$wrt_file_name" "$output_wqfm_file"

				echo "$output_wqfm_file"
				print_folder="$percent_min""_""$percent_max""_""$folder_to_print""/R$i/wqfm_""$percent_min"_"$percent_max.tre"
				echo "$print_folder"
				echo -n "$print_folder""		" >> "$file_name_wqfm_RF"
				java -jar phylonet_v2_4.jar rf -m "$true_tree_name" -e "$output_wqfm_file" >> "$file_name_wqfm_RF"

				
				
				output_wqmc_file="$innerFolder/wqmc_""$percent_min"_"$percent_max.tre"

				java ConvertForQMC "$wrt_file_name" "TEMP_WQMC_INPUT_FILE"
				./max-cut-tree qrtt="TEMP_WQMC_INPUT_FILE" weights=on otre="TEMP_WQMC_OUTPUT_FILE"
				java ConvertBack "TEMP_WQMC_OUTPUT_FILE" "$output_wqmc_file"
				
				
				print_folder="$percent_min""_""$percent_max""_""$folder_to_print""/R$i/wqmc_""$percent_min"_"$percent_max.tre"
				echo -n "$print_folder""		" >> "$file_name_wqmc_RF"
				java -jar phylonet_v2_4.jar rf -m "$true_tree_name" -e "$output_wqmc_file" >> "$file_name_wqmc_RF"
				
				# echo "python3 removerTaxa.py "$inputGTFileName" "$outputFileGTName" "$percent_min" "$percent_max" "
				# python3 removerTaxa.py "$inputGTFileName" "$outputFileGTName" "$percent_min" "$percent_max"
			done
		done
		

	done
	
}




mode=1
formGTs
mode=2
formGTs















