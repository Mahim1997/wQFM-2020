import os
import pandas as pd
import sys
import subprocess
import re
import time
from collections import OrderedDict


def get_dictionary_quartets(inputFile):
    os.system("chmod u+x triplets.soda2103")
    dictionary_line = {} # empty dictionary
    tmp_file_name = "TEMP_FILE_PYTHON_FOR_EMBEDDED_QUARTETS"

    with open(inputFile) as fin:
        for line in fin: # for each gene tree
            line = line.replace("\n", "")
            # print("<", line, ">")
            with open(tmp_file_name, 'w') as f_out_temp:
                f_out_temp.write(line) # write that gene tree to temporary file.

            result = subprocess.run(['./triplets.soda2103', 'printQuartets', tmp_file_name], stdout=subprocess.PIPE)
            results_str = result.stdout.decode('utf-8')
            results_str = results_str.strip() # remove the empty line at the end
            results_str = re.sub(".*: ", "", results_str) # remove alpha,beta,gamma names
            # starting, add (( [two open brackets]
            results_str = re.sub("\n", "));\n((", results_str) # add initial brackets
            results_str = re.sub("^", "((", results_str) # for the very first quartet
            results_str = re.sub("$", "));", results_str) # for the very last quartet

            results_str = re.sub(" ", ",", results_str) # change white space to comma, ((11,9,|,5,6));
            results_str = re.sub(",\|,", "),(", results_str) # change ,|, to ),( to form ((11,9),(5,6));

            results_array = results_str.split("\n") # split to form each quartets

            # print(results_array)

            for line_result in results_array:
                if line_result not in dictionary_line: # THIS line doesn't exist in dictionary
                    dictionary_line[line_result] = 1 # initialize to 1
                else: # THIS line does exist in dictionary, so increment
                    dictionary_line[line_result] += 1

    return dictionary_line


def usage():
    print("Usage: python3 generate-weighted-embedded-quartets.py <input-gene-tree-file-name> <output-file-name>")
    print("***Must have ./triplets.soda2103 in the same directory.")
    exit()

if __name__ == '__main__':
    start_time = time.time()

    inputFile = sys.argv[1] # argv[0] is the script itself.
    outputFile = sys.argv[2]


    if len(sys.argv) != 3:
        usage()


    print(f"inputFile = {inputFile}, outputFile = {outputFile}")

    dictionary_line = get_dictionary_quartets(inputFile)

    sorted_dict = OrderedDict(sorted(dictionary_line.items()))
    # print(sorted_dict)

    # (pd.DataFrame.from_dict(data=dictionary_line, orient='index')
    #    .to_csv(outputFile, header=False, sep=" "))

    (pd.DataFrame.from_dict(data=sorted_dict, orient='index')
       .to_csv(outputFile, header=False, sep=" "))

    end_time = time.time()

    print("--- Time to run => %s seconds ---" % (end_time - start_time))