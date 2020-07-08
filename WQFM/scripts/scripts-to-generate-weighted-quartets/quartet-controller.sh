#Author: Md. Shamsuzzoha Bayzid
#!/bin/bash

# sh quartet_count.sh $1 | perl summarize_quartets_stdin.pl > $2


# ------------------------------------- [EDITED] Below uses relative paths ------------------------------

usage ()
{
  echo 'Usage : quartet-controller.sh <input-genetree-file> <output-triplet_list>'
  exit
}

if [ "$#" -ne 2 ]
then
  usage
fi

rm -f $2 	#To remove the output file just in case

start=`date +%s`

# sh quartet_count.sh $1 | perl summarize_quartets_stdin.pl > $2 	#To pass the PATH as parameter to the shell script quartet_count.sh

./quartet_count.sh $1 | python3 summarize-quartet-counts.py $2 	#To pass the PATH as parameter to the shell script quartet_count.sh

end=`date +%s`

runtime=$((end-start))

echo "Run time took $runtime seconds"