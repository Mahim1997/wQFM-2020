'''
    Prints/Outputs the total satisfied weighted quartets, total quartets of input wqrt file, percent of quartets satisfied
'''

from annotate_branches import getSortedQuartet, read_tree, getPartitionMap, isQuartetSatisfied, LEFT, RIGHT
import sys
import dendropy


def printUsageExit():
    print("python get_quartet_score.py <input-wqrts> <stree-reference> <quartet-score-level> [quartet-score-output-file]")
    sys.exit()


"""Get dictionary of input weighted list_quartets"""
def get_input_wqrts(input_file_wqrts):
    d = {}
    total_weight_wqrts = 0
    with open(input_file_wqrts, mode='r') as fin:
        lines = [l.strip() for l in fin.readlines()]

        for line in lines:
            (t1, t2, t3, t4, w) = getSortedQuartet(line)
            d[(t1, t2, t3, t4)] = w
            total_weight_wqrts += w

    return d, total_weight_wqrts



"""Compute the satisfied quartets for one branch"""
def compute_satisfied_wqrts_one_branch(bipartition, dict_quartets, taxon_namespace):
    map_bipartition = getPartitionMap(str(bipartition), taxon_namespace)
    
    satisfied_wqrts = 0

    # https://stackoverflow.com/questions/11941817/how-to-avoid-runtimeerror-dictionary-changed-size-during-iteration-error
    for quartet in list(dict_quartets):
        if isQuartetSatisfied(map_bipartition, quartet, includes_weight=False):
            satisfied_wqrts += dict_quartets[quartet]
            dict_quartets.pop(quartet, None) # remove from dictionary of quartets

    return satisfied_wqrts

"""Compute the satisfied quartets for all branches of the tree"""
def compute_satisfied_wqrts(tree, dict_quartets):
    tree.encode_bipartitions() ## encode bipartitions to bitmaps
    satisfied_wqrts = 0

    for nd in tree:
        if nd.bipartition.is_trivial() == False:
            # print(f"\nBip = {nd.bipartition.leafset_as_newick_string(taxon_namespace=tree.taxon_namespace)})
            satisfied_wqrts += compute_satisfied_wqrts_one_branch(nd.bipartition, dict_quartets, tree.taxon_namespace)

    return satisfied_wqrts


def get_string_output(qscore_level, satisfied_wqrts, total_weight_wqrts):
    if qscore_level == 1:
        s = str(satisfied_wqrts)
    
    elif qscore_level == 2:
        if total_weight_wqrts == 0:
            percent_satisfied = 0
        else:
            percent_satisfied = float(satisfied_wqrts) / float(total_weight_wqrts)
        
        s = str(satisfied_wqrts) + "\t" + str(total_weight_wqrts) + "\t" + str(percent_satisfied)
    else:
        s = ""
    return s

def main(input_file_wqrts, stree_file, qscore_level, qscore_output_file=None):
    dict_quartets, total_weight_wqrts = get_input_wqrts(input_file_wqrts)

    tree = read_tree(stree_file)

    satisfied_wqrts = compute_satisfied_wqrts(tree, dict_quartets)



    s = get_string_output(qscore_level, satisfied_wqrts, total_weight_wqrts)
    print(s)

    if qscore_output_file != None:
        with open(qscore_output_file, mode='w') as fout:
            fout.write(s)
            fout.write("\n")


#################################################################################################

if __name__ == '__main__':
    
    lens = len(sys.argv)

    if (lens == 4) or (lens == 5):
        input_file_wqrts = sys.argv[1]
        stree_file = sys.argv[2]
        qscore_level = int(sys.argv[3]) # nothing done here.
        
        if lens == 5:
            qscore_output_file = sys.argv[4]
            main(input_file_wqrts, stree_file, qscore_level, qscore_output_file)
        else:
            main(input_file_wqrts, stree_file, qscore_level)

    else:
        printUsageExit()