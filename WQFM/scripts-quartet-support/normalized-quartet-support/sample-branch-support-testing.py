import dendropy

OUTGROUP_NODE = "1"

def get_tree():
    # tree_str = "((C:1.0,D:1.0)10:0.001,(A:1.0, (B:1.0,X:1.0)30:0.01)20:0.1,E:1.0)0:0.0;"
    # tree_str = "((C,D)10,(A,(B,X)30)20,E)0;"
    # tree_str = "((C,D),(A,(B,X)),E);"
    # tree_str = "((5,6),((1,2),(3,4)));"

    tree_str = "((E,F),((A,B),(C,D)));"

    tree = dendropy.Tree.get(
            data=tree_str,
            schema="newick",
            rooting="force-rooted",
            )
    return tree

def naive_method():
    tree = get_tree()
    
    outgroup_node = tree.find_node_with_taxon_label(OUTGROUP_NODE)

    new_root = outgroup_node.parent_node
    tree.reseed_at(new_root)
    # print(tree.as_string("newick").strip())
    return tree

""" Gets bipartition support value.
"""
def get_support_value(bipartition):

    return 0

def support_aware_method():
    tree = get_tree()
    print(f"tree.taxon_namespace = {tree.taxon_namespace}\n")

    benc = tree.encode_bipartitions()
    support_values = {}

    for nd in tree:
        if nd.bipartition.is_trivial() == False:
            print(f"Bipartition: {nd.bipartition}, bip.is_trivial() = {nd.bipartition.is_trivial()}\nbip = {nd.bipartition.leafset_as_newick_string(taxon_namespace=tree.taxon_namespace)}\n")

            support_values[nd.bipartition] = get_support_value(nd.bipartition)
            ## support_values[nd.bipartition] = float(nd.label) if nd.label is not None else 1.0

    # outgroup_node = tree.find_node_with_taxon_label("X")
    # outgroup_node = tree.find_node_with_taxon_label(OUTGROUP_NODE)

    # new_root = outgroup_node.parent_node
    # tree.reseed_at(new_root)

    tree.encode_bipartitions()
    for nd in tree:
        nd.label = support_values.get(nd.bipartition, "X")
    return tree


print("\n[Original Tree:]\n    ")
print(get_tree().as_string("newick").strip())

# print("\n[Naive Tree:]\n    ")
# print(naive_method().as_string("newick").strip())

print("\n[Support-Aware Tree:]\n")
print(support_aware_method().as_string("newick").strip())
print("\n")