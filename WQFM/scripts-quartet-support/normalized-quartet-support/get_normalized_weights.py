import sys

dict_4TaxSeq_to_totalWts = {} # Key: 4-tax-sequence, Value: Total-Cumulative-Weight-of-Quartets

def printUsageExit():
    print("Usage:python3 normalize_weights.py <input-file> <output-file> [MODE: Total->default/no args, MAX->any args] \nExiting\n")
    exit()

""" Gets the newick representation
"""
def get_newick(qrt):
    return ("((" + qrt[0] + "," + qrt[1] + "),(" + qrt[2] + "," + qrt[3] + ")); " + str(qrt[4]))


""" Sort the quartets
"""
def sort_4TaxSequence(tax1, tax2, tax3, tax4):
    list_custom = []
    list_custom.append(tax1)
    list_custom.append(tax2)
    list_custom.append(tax3)
    list_custom.append(tax4)
    list_custom.sort()
    # (t1, t2, t3, t4) = list_custom
    return list_custom

""" Gets quartet
"""
def get_quartet(line):
    for ch in ['(',')', ';']: ## remove these characters BRACKETS, SEMI-COLON
        if ch in line:
            line = line.replace(ch, "")
    line = line.replace(" ", ",")
    (tax1, tax2, tax3, tax4, weight) = line.split(",")
    weight = float(weight)
    return (tax1, tax2, tax3, tax4, weight)

""" Appends one single line to dictionary
"""
def append_to_dictionary(line, max_mode):
    (tax1, tax2, tax3, tax4, weight) = get_quartet(line)
    (t1, t2, t3, t4) = sort_4TaxSequence(tax1, tax2, tax3, tax4) # any sorting order

    # print(tax1, tax2, tax3, tax4, weight, t1, t2, t3, t4)
    key_4Tax_seq = (t1, t2, t3, t4)

    if key_4Tax_seq not in dict_4TaxSeq_to_totalWts:
        dict_4TaxSeq_to_totalWts[key_4Tax_seq] = 0 ## initialize as ZERO

    if max_mode == False: ## use sum mode.
        dict_4TaxSeq_to_totalWts[key_4Tax_seq] = dict_4TaxSeq_to_totalWts[key_4Tax_seq] + weight
    else: ## use MAX mode
        dict_4TaxSeq_to_totalWts[key_4Tax_seq] = max(dict_4TaxSeq_to_totalWts[key_4Tax_seq], weight)
    


def runMethod(input_file, output_file, max_mode):
    print(f"max_mode = {max_mode}")

    ## Read each line, pre-process, and push to dictionary of quartets
    with open(input_file, 'r') as fp:
        line = fp.readline()
        while line:
            append_to_dictionary(line, max_mode)
            line = fp.readline()

 

    ## For each key, divide by MAX OR divide by SUM
    with open(output_file, 'w') as fout:
        with open(input_file, 'r') as fp:
            line = fp.readline()
            while line:
                (tax1, tax2, tax3, tax4, weight) = get_quartet(line)
                (t1, t2, t3, t4) = sort_4TaxSequence(tax1, tax2, tax3, tax4)
                key_4Tax_seq = (t1, t2, t3, t4)
                DENOMINATOR = dict_4TaxSeq_to_totalWts[key_4Tax_seq]
                weight = weight / float(DENOMINATOR)
                qrt = (tax1, tax2, tax3, tax4, weight)
                newick = get_newick(qrt)

                fout.write(newick)
                fout.write("\n")

                line = fp.readline()


###############################################################################################

if __name__ == '__main__':
    args = sys.argv
    print(f"args: {args}")
    
    if len(args) == 3:
        max_mode = False ## normalize by sum
    elif len(args) == 4:
        max_mode = True  ## normalize by max
    else:
        printUsageExit()


    runMethod(sys.argv[1], sys.argv[2], max_mode=max_mode)