#! /usr/bin/env python
import dendropy
import fileinput
# from random import randrange, uniform
import random
import sys

random.seed(2231)

def usageAndExit():
    print("USAGE: python3 removerTaxaRandom.py <input-file> <output-file> <min-percent> <max-percent>")
    exit()

####### STEPS #######
# Take input tree
# Take output file name
# Remove taxa set [random percent as inputs]
# Print pruned tree in the output file


def readInputs(fileName):
	# read inputs from file , return the set of gene trees as list.
    list_gts = []
    with open(fileName) as fp:
    	cnt = 1
    	line = fp.readline()
    	while line:
    		list_gts.append(line)
    		# print("Line {}: {}".format(cnt, line.strip()))
    		line = fp.readline()
    		cnt = cnt + 1
    return list_gts


def getNumTaxa(list):
    gt = list[0][:]
    gt = gt.replace(';', '')
    gt = gt.replace('(', '')
    gt = gt.replace(')', '')
    gt = gt.replace(',', ' ')
    gt = gt.replace('\n', "")
    arr = gt.split(' ')
    num_taxa = len(arr)


    return num_taxa

def getTaxaList(gt):
    gt = gt.replace(';', '')
    gt = gt.replace('(', '')
    gt = gt.replace(')', '')
    gt = gt.replace(',', ' ')
    gt = gt.replace('\n', "")
    arr = gt.split(' ')
    return arr

def getPrunedTaxaList(items, n):
    data = items
    random.shuffle(data)
    data = data[:len(data) - n]
    return data
    # to_delete = set(random.sample(range(len(items)),n))
    # return [x for i,x in enumerate(items) if not i in to_delete]


def printList(list_to_print):
    s = ""
    for x in list_to_print:
    	s = s + str(x)
    	s = s + " "
	# s = s + "\n"
	# print("Len = " + str(len(list_to_print)) + " <" +  s + ">")

def getPrunedTree(pruned_taxa_list, tree_str):
    tree = dendropy.Tree.get_from_string(tree_str, "newick")
    # tree.retain_taxa_with_labels(["A", "C", "G"])
    
    # print("Inside getPrunedTree(), pruned_taxa_list = ", pruned_taxa_list)
    # print("tree = ", tree)

    tree.retain_taxa_with_labels(pruned_taxa_list)
    new_tree_str = tree.as_string('newick')
    return new_tree_str

def runMain(fileName, outputFileName, min_percent, max_percent):
    # min_percent = 10
    # max_percent = 20
    gt_list = readInputs(fileName)
    num_taxa = getNumTaxa(gt_list)
    min_remove_tax_num = (int)(min_percent * 0.01 * num_taxa)
    max_remove_tax_num = (int)(max_percent * 0.01 * num_taxa)
    # print("min_remove_tax_num = " + str(min_remove_tax_num) + " , max_remove_tax_num = " + str(max_remove_tax_num))
    cnt = 1
    list_new_trees = []
    for gt in gt_list:
        # irand = randrange(min_remove_tax_num, max_remove_tax_num)
        ### A random integer in range [start, end] including the end points.
        irand = random.randint(min_remove_tax_num, max_remove_tax_num)
        # print("Rand num, irand = ", irand)
        all_taxa_list = getTaxaList(gt)
        # print("Whole Taxa list = ", all_taxa_list)
        pruned_taxa_list = getPrunedTaxaList(all_taxa_list, irand)
        # print("Pruned taxa list = ", pruned_taxa_list)
        new_tree = getPrunedTree(pruned_taxa_list, gt)
        # print("New tree = ", new_tree, "\n")
        list_new_trees.append(new_tree)
        # print("Cnt = " + str(cnt) + " >> " + new_tree)
        cnt = cnt + 1

    f = open(outputFileName, "w")
    for gt in list_new_trees:
    	f.write(gt)
    f.close()

# ----------------------------------- main ---------------------------------------------



if __name__ == '__main__':
    # print("This is the name of the script: ", sys.argv[0])
    print("Number of arguments: ", len(sys.argv))
    print("The arguments are: " , str(sys.argv))
    if len(sys.argv) != (4 + 1):
    	usageAndExit()

    inputFileName = sys.argv[1]
    outputFileName = sys.argv[2]
    min_percent = (float)(sys.argv[3])
    max_percent = (float)(sys.argv[4])

    # runMain("all_gt.tre")
    runMain(inputFileName, outputFileName, min_percent, max_percent)














