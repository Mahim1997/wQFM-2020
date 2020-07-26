computeRF_ASTRAL_wQMC()
{
	innerFolder_arr=('0.5X-1000-500' '1X-1000-500' '2X-1000-500' '0.5X-1000-true' '1X-1000-true' '2X-1000-true')
	# innerFolder_arr=('0.5X-1000-true' '1X-1000-true' '2X-1000-true')

	# innerFolder_arr=('2X-1000-500')
	for (( iter = 0; iter < ${#innerFolder_arr[*]}; iter++ ))
	do
		folderName=${innerFolder_arr[$iter]}

		for i in {1..20}; do
			# wc -l "$folderName/R$i/weighted_quartets"

			echo -n "$folderName/R$i/wqmc.tre		"
			java -jar phylonet_v2_4.jar rf -m true_tree_without_branch -e "$folderName/R$i/wqmc.tre"


			# echo -n "$folderName/R$i/astral.5.7.3.heuristic.tre		"
			# java -jar astral.5.7.3.jar -i "$folderName/R$i/all_gt.tre" -o "$folderName/R$i/astral.5.7.3.heuristic.tre" -t 0
			# java -jar phylonet_v2_4.jar rf -m true_tree_without_branch -e "$folderName/R$i/astral.5.7.3.heuristic.tre"

		done
	done

	# 
}


func()
{
	innerFolder_arr=('0.5X-1000-500' '1X-1000-500' '2X-1000-500' '0.5X-1000-true' '1X-1000-true' '2X-1000-true')
	# innerFolder_arr=('0.5X-1000-true' '1X-1000-true' '2X-1000-true')

	# innerFolder_arr=('0.5X-1000-500')
	for (( iter = 0; iter < ${#innerFolder_arr[*]}; iter++ ))
	do
		innerFolder=${innerFolder_arr[$iter]}
		folderName="$innerFolder"
		for i in {1..20}; do
			# echo -n "$innerFolder/R$i/wQFM_DYN_NC_2.tre		" >> "RF-AVIAN-wQFM-newcutoff-thresh-0.8.txt"
			# java -jar phylonet_v2_4.jar rf -m true_tree_without_branch -e "$innerFolder/R$i/wQFM_DYN_NC_2.tre" >> "RF-AVIAN-wQFM-newcutoff-thresh-0.8.txt"
			

			# java -jar wQFM-June-30.jar "$folderName/R$i/weighted_quartets" "$folderName/R$i/wQFM-June-30.tre"

			echo -n "$folderName/R$i/wQFM-June-30.tre		"
			java -jar phylonet_v2_4.jar rf -m true_tree_without_branch -e "$folderName/R$i/wQFM-June-30.tre"

		done
	done
}
func

# computeRF_ASTRAL_wQMC











############# PREPROCESSING STEPS #################
###### mkdir "$folderName/R$i"
###### mv "$folderName/R$i.weighted_quartets" "$folderName/R$i/weighted_quartets"