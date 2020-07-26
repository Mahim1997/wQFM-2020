#!/bin/bash
# outer_directories=('noscale.25g.500b' 'noscale.50g.500b' 'noscale.100g.500b' 'noscale.200g.250b' 'noscale.200g.500b' 'noscale.200g.1000b' 'noscale.200g.1500b' 'noscale.200g.true' 'noscale.400g.500b' 'noscale.400g.1500b' 'noscale.800g.500b' 'scale2d.200g.500b' 'scale2u.200g.500b' 'scale5d.200g.500b')  # 11 Feb 2020
# outer_directories=('scale2d.200g.500b')  # 8 Mar 2020
# outer_directories=('noscale.200g.500b_ACTUALLY_scale2d.200g.500bp_ONADER') # 7 May 2020

# javac *.java

outer_directories=('noscale.100g.500b' 'noscale.200g.1000b' 'noscale.200g.1500b' 'noscale.200g.250b' 'noscale.200g.true' 
'noscale.25g.500b' 'noscale.400g.500b' 'noscale.50g.500b' 'noscale.800g.500b' 'scale2d.200g.500b' 'scale2u.200g.500b' 
'noscale.200g.500b_ACTUALLY_scale2d.200g.500bp_ONADER') # 7 MAY 2020 for astral RF


trueTreeName="true_tree_trimmed"

runAll()
{
	#  for outerFolder in *scale*
	for (( iter = 0; iter < ${#outer_directories[*]}; iter++ ));
	do
		outerFolder=${outer_directories[$iter]}
		folderName="$outerFolder"
		# echo "iter = $iter, Outer folder = $outerFolder"
		for i in {1..20}
		do

			# java -jar wQFM-June-30.jar "$folderName/R$i/weighted_quartets" "$folderName/R$i/wQFM-June-30.tre"

			echo -n "$folderName/R$i/wQFM-June-30.tre		"			
			java -jar phylonet_v2_4.jar rf -m true_tree_trimmed -e "$folderName/R$i/wQFM-June-30.tre"

		done

	done		
}


runAll




