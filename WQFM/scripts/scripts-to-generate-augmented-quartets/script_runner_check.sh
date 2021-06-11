# python3 generate_wqrts_augmentation.py wqrts wqrts_augmented
# java -jar wQFM.jar wqrts_augmented test.augmented.wQFM.tre
# java -jar phylonet_v2_4.jar rf -m astral-July26.5.7.3.tre -e test.augmented.wQFM.tre

folderName="small-tree"

input_sTree="$folderName/aminota-aa-wqfm.tre"


wqrts_induced="$folderName/weighted_quartets"

### wqrts_augmented="$folderName/wqrts_augmented"
wqrts_dominant="$folderName/wqrts_dominant"
wqrts_all="$folderName/wqrts_all"

##
wQFM_dominant="$folderName/wQFM.dominant.tre"
log_dominant="$folderName/log.dominant.txt"

wQFM_all="$folderName/wQFM.all.tre"
log_all="$folderName/log.all.txt"


## Induce quartets.
./quartet-controller.sh "$input_sTree" "$wqrts_induced"

## Augment weights.
python3 generate_wqrts_augmentation.py "$wqrts_induced" "$wqrts_all"


## Keep only best.
java -jar generateBestWQrts.jar "$wqrts_all" "$wqrts_dominant"


## Run wQFM
java -jar wQFM-printing-more-info.jar "$wqrts_dominant" "$wQFM_dominant" > "$log_dominant"

java -jar wQFM-printing-more-info.jar "$wqrts_all" "$wQFM_all"  "1" "0" > "$log_all"

## Check phylonet jar
echo "Checking $wQFM_dominant"
java -jar phylonet_v2_4.jar rf -m "$input_sTree" -e "$wQFM_dominant"

echo "Checking $wQFM_all"
java -jar phylonet_v2_4.jar rf -m "$input_sTree" -e "$wQFM_all"
