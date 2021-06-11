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
def compute_satisfied_wqrts_one_branch(bipartition, dict_quartets, dictionary_quartets_satisfied, taxon_namespace):
    map_bipartition = getPartitionMap(str(bipartition), taxon_namespace)
    print(map_bipartition)

    for quartet in dict_quartets:
        (t1, t2, t3, t4) = quartet
        if isQuartetSatisfied(map_bipartition, quartet):
            dictionary_quartets_satisfied[dictionary_quartets_satisfied] = dict_quartets[quartet]


"""Compute the satisfied quartets for all branches of the tree"""
def compute_satisfied_wqrts(tree, dict_quartets):
    dictionary_quartets_satisfied = {}

    tree.encode_bipartitions() ## encode bipartitions to bitmaps
    support_values = {} ## Map for support values

    for nd in tree:
        if nd.bipartition.is_trivial() == False:

            # print(f"\nBip = {nd.bipartition.leafset_as_newick_string(taxon_namespace=tree.taxon_namespace)}, \
                # is_trivial = {nd.bipartition.is_trivial()}")

            compute_satisfied_wqrts_one_branch(nd.bipartition, dict_quartets, dictionary_quartets_satisfied, tree.taxon_namespace)

            # support_values[nd.bipartition] = get_support_value(nd.bipartition) if nd.label is not None else 1.0
            # support_values[nd.bipartition] = get_support_value(nd.bipartition, tree.taxon_namespace, list_quartets) if nd.label is None else -1.0

    # tree.encode_bipartitions()

    # TOTAL_SATISFIED_QUARTETS = -1

    # for nd in tree:
    #     # nd.label = support_values.get(nd.bipartition, float(TOTAL_SATISFIED_QUARTETS))
    #     nd.label = support_values.get(nd.bipartition, "")
    # return tree


def main(input_file_wqrts, stree_file, qscore_level, qscore_output_file=None):
    dict_quartets, total_weight_wqrts = get_input_wqrts(input_file_wqrts)

    tree = read_tree(stree_file)

    compute_satisfied_wqrts(tree, dict_quartets)


if __name__ == '__main__':
    
    lens = len(sys.argv)

    if (lens == 4) or (lens == 5):
        input_file_wqrts = sys.argv[1]
        stree_file = sys.argv[2]
        qscore_level = int(sys.argv[3])
        
        if lens == 5:
            qscore_output_file = sys.argv[4]
            main(input_file_wqrts, stree_file, qscore_level, qscore_output_file)
        else:
            main(input_file_wqrts, stree_file, qscore_level)

    else:
        printUsageExit()