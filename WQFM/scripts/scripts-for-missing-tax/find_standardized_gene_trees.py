import sys

get_num_taxa = lambda line: len(line.replace(";", "").replace("(", "").replace(")", "").split(","))

# input_file: gene trees
def run(input_file):
    # list_num_taxa = []
    dict_num_taxa = {}
    num_gene_trees = 0

    with open(input_file, 'r') as fin:
        line = fin.readline()

        while line:
            num_gene_trees += 1

            line = line.strip()
            num_taxa = get_num_taxa(line)

            if num_taxa in dict_num_taxa:
                dict_num_taxa[num_taxa] += 1
            else:
                dict_num_taxa[num_taxa] = 0

            line = fin.readline()

    # for key in dict_num_taxa:
    #     dict_num_taxa[key] /= num_gene_trees
    # print(dict_num_taxa)
    max_taxa = max(list(dict_num_taxa.keys()))

    for key in sorted(dict_num_taxa.keys(), reverse=True):
        print("{}: {}, {}".format(key, dict_num_taxa[key]/num_gene_trees, dict_num_taxa[key]))
        # print("{},{}: {}, {}".format(key, key/max_taxa, dict_num_taxa[key]/num_gene_trees, dict_num_taxa[key]))

###############################################################################################

if __name__ == '__main__':
    args = sys.argv
    # print(f"args: {args}")
    


    run(sys.argv[1])
