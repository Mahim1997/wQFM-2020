import dendropy

## https://dendropy.org/primer/bipartitions.html

## ((10,((6,5),(9,(7,8)))),(11,(4,(2,(3,1)))));

## ((5,6),((1,2),(3,4)));
## (5,(6,((1,2),(3,4))));

taxa = dendropy.TaxonNamespace()
tree = dendropy.Tree.get_from_path(
    "wQFM-best-25Nov.tre",
    "newick",
    taxon_namespace=taxa)


print("Tree: ", tree, "\n\n")


bipartitions = tree.encode_bipartitions()

# print(taxa)

for bip in bipartitions:
    # print(bip)

    if not bip.is_trivial():
        print(bip.leafset_as_newick_string(taxon_namespace=taxa))
        # print(bip.split_as_newick_string(taxon_namespace=taxa, preserve_spaces=True, quote_underscores=False))
    # print(bip.leafset_taxa(taxon_namespace=taxa))


# edges = tree.edges()

# for edge in edges:
#     print(edge.head_node, edge.length, edge.label, edge.is_internal(), edge.is_leaf())