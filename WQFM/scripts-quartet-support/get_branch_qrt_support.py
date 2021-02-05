import dendropy
import sys


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
    2. Form a list of taxa
    3. Form a Map of <taxa: [list of qrt indices]>
"""
def processQuartets(inputQrtFile):
    list_quartets = []
    map_taxa_quartet = {}

    with open(inputQrtFile, 'r') as fin:
        line = fin.readline()
        while line:
            quartet = getSortedQuartet(line) # (t0, t1, t2, t3, w) = getSortedQuartet(line)
            # print(quartet)
            list_quartets.append(quartet)
            (t0, t1, t2, t3, w) = quartet
            for i in range(0, 4):
                if quartet[i] not in map_taxa_quartet:
                    map_taxa_quartet[quartet[i]] = [] ## initialize a list for the taxon NOT in map.
                idx_quartet = len(list_quartets) - 1
                ## Append this INDEX for each taxon.
                map_taxa_quartet[quartet[i]].append(idx_quartet)


            line = fin.readline()

    return list_quartets, map_taxa_quartet

""" Read tree from the Species Tree file
"""
def read_tree(inputStreeFile):
    taxa = dendropy.TaxonNamespace()
    tree = dendropy.Tree.get_from_path(inputStreeFile, "newick", taxon_namespace=taxa, rooting="force-rooted") ## rooting should be done
    return tree


""" Function to compute Quartet Support and return new tree with quartet support.
"""
def compute_tree_QSupport(tree, list_quartets, map_taxa_quartet):
    benc = tree.encode_bipartitions()
    support_values = {}

    for nd in tree:
        if nd.bipartition.is_trivial() == False:
            # print(f"Bipartition: {nd.bipartition}, bip.is_trivial() = {nd.bipartition.is_trivial()}")
            print(f"Bip = {nd.bipartition.leafset_as_newick_string(taxon_namespace=tree.taxon_namespace)}, is_trivial = {nd.bipartition.is_trivial()}")

            support_values[nd.bipartition] = float(7) if nd.label is not None else 1.0 # get_support_value(nd.bipartition)
            ## support_values[nd.bipartition] = float(nd.label) if nd.label is not None else 1.0

    # outgroup_node = tree.find_node_with_taxon_label("X")
    # outgroup_node = tree.find_node_with_taxon_label(OUTGROUP_NODE)

    # new_root = outgroup_node.parent_node
    # tree.reseed_at(new_root)

    tree.encode_bipartitions()
    for nd in tree:
        nd.label = support_values.get(nd.bipartition, 77)
    return tree




""" Main function
"""
def run(inputQrtFile, inputStreeFile, outputFile):

    list_quartets, map_taxa_quartet = processQuartets(inputQrtFile)
    
    tree = read_tree(inputStreeFile)
    treeStr = tree.as_string("newick").strip()
    
    print(treeStr, "\n")

    output_tree = compute_tree_QSupport(tree, list_quartets, map_taxa_quartet)
    output_tree = output_tree.as_string("newick").strip()

    print("\n")
    print(output_tree)
    # print(f"\n In run(), output_tree = {output_tree}")

#####################################################################################################################

def printUsageExit():
    print(f"Usage: python3 {sys.argv[0]} <input-qrt-file> <input-STree-file> <output-file>")
    exit()




if __name__ == '__main__':
    if len(sys.argv) != 4:
        printUsageExit()
    
    inputQrtFile = sys.argv[1]
    inputStreeFile = sys.argv[2]
    outputFile = sys.argv[3]

    run(inputQrtFile, inputStreeFile, outputFile)

















