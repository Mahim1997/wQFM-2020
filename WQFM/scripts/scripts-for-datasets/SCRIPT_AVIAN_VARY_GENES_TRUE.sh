#!/bin/bash


# num_genes_arr=('25')

num_genes_arr=('25' '50' '100' '250' '500')
numGenes=25



form_gene_trees()
{
	# for (( iter = 0; iter < ${#num_genes_arr[*]}; iter++ ))
	# do
	# 	numGenes=${num_genes_arr[$iter]}
		numGenes=200
		########### form directories ############
		# mkdir "1X-""$numGenes""-true"

		# for i in {1..20};
		# do
		# 	mkdir "1X-""$numGenes""-true/R$i"
		# done
		# # ###### directories formation done #######

		# # ########## copy num lines ##############

		# for i in {1..20};
		# do
		# 	head -"$numGenes" "R$i/all_gt.tre" > "1X-""$numGenes""-true""/R$i/all_gt.tre"
		# done

		for i in {1..20};
		do
			wc -l "1X-""$numGenes""-true""/R$i/all_gt.tre"
		done

	# done
}


run()
{
	innerFolder_arr=('1X-25-true' '1X-50-true' '1X-100-true' '1X-250-true' '1X-500-true') # '1X-200-true'
	# innerFolder_arr=('2X-1000-true')
	
	# innerFolder_arr=('0.5X-1000-true')
	for (( iter = 0; iter < ${#innerFolder_arr[*]}; iter++ ))
	do
		folderName=${innerFolder_arr[$iter]}

		for i in {1..20}; do
			# wc -l "$folderName/R$i/all_gt.tre"

			echo "Doing for $folderName/R$i/all_gt.tre"
			
			# run astral
			java -jar astral.5.7.3.jar -i "$folderName/R$i/all_gt.tre" -o "$folderName/R$i/astral.5.7.3.tre" -t 0 

			# run wQFM
			java -jar wQFM-June-30.jar "$folderName/R$i/weighted_quartets" "$folderName/R$i/wQFM-June-30.tre"

			# run wQMC
			java -jar convertForWQMC.jar "$folderName/R$i/weighted_quartets" "TEMP_INPUT_WQMC"
			./max-cut-tree qrtt="TEMP_INPUT_WQMC" weights=on otre="TEMP_OUTPUT_WQMC"
			java -jar convertBack.jar "TEMP_OUTPUT_WQMC" "$folderName/R$i/wqmc.tre"


			# ./quartet-controller.sh "$folderName/R$i/all_gt.tre" "$folderName/R$i/weighted_quartets"

			
			# java -jar wQFM.jar "$folderName/R$i/weighted_quartets" "$folderName/R$i/wQFM_JAR_DYNAMIC_5.tre" "1" "$betaValue"

			# echo -n "$folderName/R$i/wQFM_JAR_DYNAMIC_2		"
			# java -jar phylonet_v2_4.jar rf -m true_tree_without_branch -e "$folderName/R$i/wQFM_JAR_DYNAMIC_2"

		done
	done
}

generateWqrts()
{
	folderName="1X-200-true"
	for i in {1..20};
	do
		./quartet-controller.sh "$folderName/R$i/all_gt.tre" "$folderName/R$i/weighted_quartets"
		# run wQMC
		java -jar astral.5.7.3.jar -i "$folderName/R$i/all_gt.tre" -o "$folderName/R$i/astral.5.7.3.tre" -t 0
		# run wQFM
		java -jar wQFM-June-30.jar "$folderName/R$i/weighted_quartets" "$folderName/R$i/wQFM-June-30.tre"
		# run wQMC
		java -jar convertForWQMC.jar "$folderName/R$i/weighted_quartets" "TEMP_INPUT_WQMC"
		./max-cut-tree qrtt="TEMP_INPUT_WQMC" weights=on otre="TEMP_OUTPUT_WQMC"
		java -jar convertBack.jar "TEMP_OUTPUT_WQMC" "$folderName/R$i/wqmc.tre"
	done
}

computeRF()
{
	innerFolder_arr=('1X-25-true' '1X-50-true' '1X-100-true' '1X-200-true' '1X-250-true' '1X-500-true')
	# innerFolder_arr=('2X-1000-true')
	
	# innerFolder_arr=('0.5X-1000-true')
	for (( iter = 0; iter < ${#innerFolder_arr[*]}; iter++ ))
	do
		folderName=${innerFolder_arr[$iter]}

		for i in {1..20}; do
			# echo -n "$folderName/R$i/astral.5.7.3.tre		" >> "RF-AVIAN-TRUE-numGenes-ASTRAL.5.7.3.txt"
			# java -jar phylonet_v2_4.jar rf -m "true_tree_without_branch" -e "$folderName/R$i/astral.5.7.3.tre" >> "RF-AVIAN-TRUE-numGenes-ASTRAL.5.7.3.txt"

			# echo -n "$folderName/R$i/wqmc.tre		" >> "RF-AVIAN-TRUE-numGenes-wQMC.txt"
			# java -jar phylonet_v2_4.jar rf -m "true_tree_without_branch" -e "$folderName/R$i/wqmc.tre" >> "RF-AVIAN-TRUE-numGenes-wQMC.txt"

			echo -n "$folderName/R$i/wQFM-June-30.tre		" >> "RF-AVIAN-TRUE-numGenes-wQFM-June-30.txt"
			java -jar phylonet_v2_4.jar rf -m "true_tree_without_branch" -e "$folderName/R$i/wQFM-June-30.tre" >> "RF-AVIAN-TRUE-numGenes-wQFM-June-30.txt"

		done
	done
}
# form_gene_trees
# run
# generateWqrts

computeRF