import dendropy
import sys

from normalize_weights import normalize_quartets_weights

global LEFT, RIGHT

LEFT  = -1
RIGHT = 1

#####################################################################################################################



""" Get sorted quartets a,b|c,d: w
"""
def getSortedQuartet(line):
    stringsReplace = ["\n", ";", "(", ")"]
    for s in stringsReplace:
        line = line.replace(s, "") ## remove these chars
        
    line = line.replace(" ", ",")  ## replace WHITESPACE with COMMA    
    arr = line.split(",") ## split by COMMA

    # arr[0:1].sort() ## Maybe not necessary
    # arr[2:3].sort() ## Maybe not necessary

    return (arr[0], arr[1], arr[2], arr[3], float(arr[4]))


""" Process quartets
    1. Form a list of quartets
"""
def processQuartets(inputQrtFile):
    list_quartets = []

    with open(inputQrtFile, 'r') as fin:
        line = fin.readline()
        while line:
            quartet = getSortedQuartet(line) # (t0, t1, t2, t3, w) = getSortedQuartet(line)
            list_quartets.append(quartet)
            line = fin.readline()

    return list_quartets

""" Read tree from the Species Tree file
"""
def read_tree(inputStreeFile):
    taxa = dendropy.TaxonNamespace()
    tree = dendropy.Tree.get_from_path(inputStreeFile, "newick", taxon_namespace=taxa, rooting="force-rooted") ## rooting should be done
    return tree


""" Function to return as a map, the bitmap of bipartition
"""
def getPartitionMap(bipartition, taxon_namespace):
    map_bipartition = {}
    for i in range(0, len(taxon_namespace)):
        bit_idx = len(taxon_namespace) - 1 - i
        partition = LEFT if (bipartition[bit_idx] == '0') else RIGHT
        # map_bipartition[taxon_namespace[i]] = partition
        map_bipartition[str(taxon_namespace[i]).replace("'", "")] = partition

    return map_bipartition

""" Function to check if given quartet is satisfied or not.
"""
def isQuartetSatisfied(map_bipartition, quartet, includes_weight=True):
    if includes_weight == False:
        (t0, t1, t2, t3) = quartet
    else: # by default
        (t0, t1, t2, t3, _) = quartet ## unwrap the quartet
    cond1 = map_bipartition[t0] == map_bipartition[t1] ## check if sisters 1 are in same side
    cond2 = map_bipartition[t2] == map_bipartition[t3] ## check if sisters 2 are in same side
    cond3 = map_bipartition[t0] != map_bipartition[t2] ## check if both sisters are in opposite sides.
    return (cond1 and cond2 and cond3)

""" Function to compute Quartet Support for EACH bipartition
"""
def get_support_value(bipartition, taxon_namespace, list_quartets):
    bipartitionSTR = str(bipartition)
    map_bipartition = getPartitionMap(bipartitionSTR, taxon_namespace)
    # checked_quartets_indices = [False for i in range(0, len(list_quartets))] ## initialize as False since no quartet is checked

    ## Iterating through all quartets everytime. [Can't think of a more efficient approach.]
    numSatisfiedQrts = 0 ## num satisfied quartets
    cumlWtSatisfiedQrts = 0 ## total weight satisfied quartets

    for quartet in list_quartets:
        (t0, t1, t2, t3, w) = quartet
        if isQuartetSatisfied(map_bipartition, quartet):
            cumlWtSatisfiedQrts += w
            numSatisfiedQrts += 1

    normWtSatisfiedQrts = cumlWtSatisfiedQrts/float(numSatisfiedQrts)

    return str(float(normWtSatisfiedQrts)) # ":"+


""" Function to compute Quartet Support and return new tree with quartet support.
"""
def compute_tree_QSupport(tree, list_quartets):
    tree.encode_bipartitions() ## encode bipartitions to bitmaps
    support_values = {} ## Map for support values

    for nd in tree:
        if nd.bipartition.is_trivial() == False:
            # print(f"\nBip = {nd.bipartition.leafset_as_newick_string(taxon_namespace=tree.taxon_namespace)}, is_trivial = {nd.bipartition.is_trivial()}")

            # support_values[nd.bipartition] = get_support_value(nd.bipartition) if nd.label is not None else 1.0
            support_values[nd.bipartition] = get_support_value(nd.bipartition, tree.taxon_namespace, list_quartets) if nd.label is None else -1.0

    tree.encode_bipartitions()

    TOTAL_SATISFIED_QUARTETS = -1

    for nd in tree:
        # nd.label = support_values.get(nd.bipartition, float(TOTAL_SATISFIED_QUARTETS))
        nd.label = support_values.get(nd.bipartition, "")
    return tree


""" Write to output file
"""
def write_output(output_tree, outputFile):
    print(output_tree)
    with open(outputFile, 'w') as fout:
        fout.write(output_tree)
        fout.write("\n")


""" Main function
"""
def run(inputQrtFile, inputStreeFile, outputFile, normalize_mode=None):

    list_quartets = processQuartets(inputQrtFile)
    
    # print(list_quartets[0:10])
    
    if normalize_mode == 'sum':
        # print("Normalizing by sum")
        list_quartets = normalize_quartets_weights(list_quartets, max_mode=False)
    elif normalize_mode == 'max':
        # print("Normalizing by max")
        list_quartets = normalize_quartets_weights(list_quartets, max_mode=True)

    # print(list_quartets[0:10])
    
    tree = read_tree(inputStreeFile)
    treeStr = tree.as_string("newick").strip()
    
    # print(treeStr, "\n")

    output_tree = compute_tree_QSupport(tree, list_quartets)
    output_tree = output_tree.as_string("newick").strip()

    output_tree = output_tree.replace("[&R] ", "") ## remove this sign
    output_tree = output_tree.replace("'", "")
    write_output(output_tree, outputFile)
    # print(f"\n In run(), output_tree = {output_tree}")

#####################################################################################################################



if __name__ == '__main__':
    
    # input wqrt file, input stree file, [sum/max]
    
    inputQrtFile = sys.argv[1]
    inputStreeFile = sys.argv[2]
    outputFile = sys.argv[3]
    
    normalize_mode = None
    
    if len(sys.argv) == 5:
        normalize_mode = sys.argv[4] # sum/max
        
    run(inputQrtFile, inputStreeFile, outputFile, normalize_mode)

















