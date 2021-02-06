import sys



dictionary_quartets = {} # Key: 4-tax-sequence, Value: List<quartets>

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

""" Appends one single line to dictionary
"""
def append_to_dictionary(line):
    for ch in ['(',')', ';']: ## remove these characters BRACKETS, SEMI-COLON
        if ch in line:
            line = line.replace(ch, "")
    line = line.replace(" ", ",")
    (tax1, tax2, tax3, tax4, weight) = line.split(",")
    weight = float(weight)

    (t1, t2, t3, t4) = sort_4TaxSequence(tax1, tax2, tax3, tax4) # any sorting order

    # print(tax1, tax2, tax3, tax4, weight, t1, t2, t3, t4)
    key_4Tax_seq = (t1, t2, t3, t4)

    if key_4Tax_seq not in dictionary_quartets: # Initialize as list
        dictionary_quartets[key_4Tax_seq] = []

    dictionary_quartets[key_4Tax_seq].append([tax1, tax2, tax3, tax4, weight]) # append to the list


def runMethod(input_file, output_file, max_mode=False):
    
    ## Read each line, pre-process, and push to dictionary of quartets
    with open(input_file, 'r') as fp:
        line = fp.readline()
        while line:
            append_to_dictionary(line, max_mode)
            line = fp.readline()

    ## For each key, divide by MAX OR divide by SUM
    for key_4Tax_seq in dictionary_quartets:
        list_quartets = dictionary_quartets[key_4Tax_seq]
        DENOMINATOR = 0.0 ## Denotes MAX or SUM

        for qrt in list_quartets: ## Retrieve the max/sum
            (_, _, _, _, w) = qrt
            if max_mode == True:
                DENOMINATOR = max(DENOMINATOR, w) ## DENOMINATOR -> MAX
            else:
                DENOMINATOR += w ## DENOMINATOR -> SUM

        for qrt in list_quartets: ## Divide by MAX/SUM 
            qrt[4] /= DENOMINATOR


        ## Print to outer file.
        with open(output_file, 'w') as fout:
            for key in dictionary_quartets:
                qrts = dictionary_quartets[key]
                for q in qrts:
                    newick = get_newick(q)
                    fout.write(newick)
                    fout.write("\n")





    # print(f"After reading input file, printing dictionary_quartets\n")
    # for key in dictionary_quartets:
    #     print(key, dictionary_quartets[key])


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