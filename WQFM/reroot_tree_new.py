#! /usr/bin/env python

import sys
import dendropy
# tree_str = "[&R] (A, (B, (C, (D, E))));"
# tree_str = "((1,2), (X,3));"
# tree_str = "((4,X), (5,6));"


# tree_str = "((3,(1,2)), ((6,5),4));"
# OUTGROUP_TAXON = "3"


tree_str = "((3,(1,2)),((6,5),4));"
OUTGROUP_TAXON = "4"





############### main ###################
tree_str = sys.argv[1]
OUTGROUP_TAXON = sys.argv[2]

# print(f"tree_str = {tree_str} and OUTGROUP_TAXON = {OUTGROUP_TAXON}")


tree = dendropy.Tree.get_from_string(tree_str, "newick")
# print("Before:")
# print(tree.as_string('newick'))
# print(tree.as_ascii_plot())

outgroup_node = tree.find_node_with_taxon_label(OUTGROUP_TAXON)


# tree.to_outgroup_position(outgroup_node, update_splits=False)
tree.to_outgroup_position(outgroup_node)


# print("After:")

print(tree.as_string('newick'))
