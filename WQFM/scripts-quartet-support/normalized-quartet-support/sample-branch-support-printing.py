import dendropy

## https://dendropy.org/primer/bipartitions.html

## ((10,((6,5),(9,(7,8)))),(11,(4,(2,(3,1)))));

## ((5,6),((1,2),(3,4)));
## (5,(6,((1,2),(3,4))));
## ((E,F),((A,B),(C,D)));

taxa = dendropy.TaxonNamespace()
tree = dendropy.Tree.get_from_path(
    "wQFM-best-25Nov.tre",
    "newick",
    taxon_namespace=taxa)


print("Tree: ", tree, "\n\n")


bipartitions = tree.encode_bipartitions()

# print(taxa)

""" Function, given a bipartition bitmap, and also list of taxa, i will find <left> <right> where |left| <= |right|.

"""
def getBipartition(bipartitionMap, listTaxa): ## bipartitionMap -> nd.bipartition
    mapPartition = {0: -1, 1: 1} # -1 -> left, 1 -> right    

    return mapPartition

""" Given a bipartition list <left>, <right>, and quartet, find the status of quartet.
"""
def isSatisfiedQuartet(mapPartition, quartet): # quartet -> [t0,t1,t2,t3,w], 
    cond1 = (mapPartition[t0] == mapPartition[t1])
    cond2 = (mapPartition[t2] == mapPartition[t3])
    cond3 = (mapPartition[t0] != mapPartition[t2])
    return (cond1 and cond2 and cond3)

####################################################################################################################################

print("Taxon Namespace:",tree.taxon_namespace)

for bip in bipartitions:
    # print(bip)
    if not bip.is_trivial():
        
        bip_tax = bip.leafset_as_newick_string(taxon_namespace=taxa)
        print(bip_tax, type(bip_tax), bip)

        print(type(bip))
        # print(bip[0])

        # print(bip.split_as_newick_string(taxon_namespace=taxa, preserve_spaces=True, quote_underscores=False))
    # print(bip.leafset_taxa(taxon_namespace=taxa))


# edges = tree.edges()

# for edge in edges:
#     print(edge.head_node, edge.length, edge.label, edge.is_internal(), edge.is_leaf())