import os

DIRECTORY = os.path.join(os.getcwd(), "nuclear")

folders = os.listdir(DIRECTORY)


def read_file(file):
    with open(file, "r", encoding='utf-8') as fin:
        trees = [line.strip() for line in fin.readlines()]
    return trees

mult_list_trees = []

for folder in folders:

    file = os.path.join(DIRECTORY, folder, "raxmlboot.gtrgamma", "RAxML_bootstrap.all")
    trees = read_file(file)
    mult_list_trees.append(trees.copy())

    # print(file1)
    # print(trees1)


# print(len(mult_list_trees), len(mult_list_trees[0])) ## 310, 200
print(folders)

## Column wise iteration.

for replicate_iter in range(len(mult_list_trees[0])):
    
    replicate_file = os.path.join("bootstrap-replicates", "R" + str((replicate_iter + 1)), "all_gt.tre")
    # print(f"replicate_file = {replicate_file}")
    for gene_iter in range(len(mult_list_trees)):
        gene_tree = mult_list_trees[gene_iter][replicate_iter]

        # with open(replicate_file, 'a', encoding='utf-8') as fout:
        #     fout.write(gene_tree)
        #     fout.write("\n")