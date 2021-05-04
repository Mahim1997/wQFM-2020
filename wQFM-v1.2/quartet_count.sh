#!/bin/bash

# Author: Md Shamsuzzoha Bayzid
# Date: Sep 05, 2014
# it requires 64 bit machine
# usage inputfile outputfile

tmp=`mktemp`

# cat $1| xargs -I@ sh -c 'echo -n "@" >'$tmp'; /home/mahim/gene-tree-tools/triplets.soda2103 printQuartets '$tmp';'|sed 's/.*: //'| sed 's/^/\(\(/'| sed 's/$/\)\)\;/'| sed 's/ | /\),\(/'| sed 's/ /\,/g'


# cat $1| xargs -I@ sh -c 'echo -n "@" >'$tmp'; ./triplets.soda2103 printQuartets '$tmp';'|sed 's/.*: //'| sed 's/^/\(\(/'| sed 's/$/\)\)\;/'| sed 's/ | /\),\(/'| sed 's/ /\,/g'


cat $1| xargs -I@ sh -c 'echo -n "@" >'$tmp'; ./triplets.soda2103 printQuartets '$tmp';'|sed 's/.*: //'| sed 's/^/\(\(/'| sed 's/$/\)\)\;/'| sed 's/ | /\),\(/'| sed 's/ /\,/g'
