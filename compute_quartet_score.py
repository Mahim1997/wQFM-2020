'''
    Prints/Outputs the total satisfied weighted quartets, total quartets of input wqrt file, percent of quartets satisfied
'''
import re
import sys
import os
import subprocess

def printUsageExit():
    print("python get_quartet_score.py <input-wqrts> <stree-reference> <quartet-score-level> [quartet-score-output-file]")
    sys.exit()

""" Get sorted quartets a,b|c,d: w ... ALWAYS ascending order """
def getSortedQuartet(line, use_weight=True):
    stringsReplace = ["\n", ";", "(", ")"]
    for s in stringsReplace:
        line = line.replace(s, "") ## remove these chars
        
    line = line.replace(" ", ",")  ## replace WHITESPACE with COMMA    
    arr = line.split(",") ## split by COMMA

    arr[0:2] = sorted(arr[0:2])
    arr[2:4] = sorted(arr[2:4])

    if arr[0] > arr[2]:
        arr[0:2], arr[2:4] = arr[2:4], arr[0:2]

    if use_weight == True:
        return (arr[0], arr[1], arr[2], arr[3], float(arr[4]))
    else:
        return (arr[0], arr[1], arr[2], arr[3])


"""Get dictionary of input weighted list_quartets"""
def get_input_wqrts(input_file_wqrts):
    d = {}
    total_weight_wqrts = 0
    with open(input_file_wqrts, mode='r') as fin:
        lines = [l.strip() for l in fin.readlines()]

        for line in lines:
            (t1, t2, t3, t4, w) = getSortedQuartet(line)
            d[(t1, t2, t3, t4)] = w
            total_weight_wqrts += w

    return d, total_weight_wqrts

def get_dictionary_quartets(inputFile):
    os.system("chmod u+x triplets.soda2103")
    dict_quartets = {} # empty dictionary
    tmp_file_name = "TEMP_FILE_PYTHON_FOR_EMBEDDED_QUARTETS"

    with open(inputFile) as fin:
        for line in fin: # for each gene tree
            line = line.replace("\n", "")
            
            with open(tmp_file_name, 'w') as f_out_temp:
                f_out_temp.write(line) # write that gene tree to temporary file.
            
            result = subprocess.run(['./triplets.soda2103', 'printQuartets', tmp_file_name], stdout=subprocess.PIPE)
            
            results_str = result.stdout.decode('utf-8')
            results_str = results_str.strip() # remove the empty line at the end
            results_str = re.sub(".*: ", "", results_str) # remove alpha,beta,gamma names
            results_str = re.sub("\n", "));\n((", results_str) # add initial brackets
            results_str = re.sub("^", "((", results_str) # for the very first quartet
            results_str = re.sub("$", "));", results_str) # for the very last quartet
            results_str = re.sub(" ", ",", results_str) # change white space to comma, ((11,9,|,5,6));
            results_str = re.sub(",\|,", "),(", results_str) # change ,|, to ),( to form ((11,9),(5,6));
            results_array = results_str.split("\n") # split to form each quartets

            for line_result in results_array:
                quartet = getSortedQuartet(line_result, use_weight=False)
                if line_result not in dict_quartets: # THIS line doesn't exist in dictionary
                    dict_quartets[quartet] = 1 # initialize to 1
                else: # THIS line does exist in dictionary, so increment
                    dict_quartets[quartet] += 1

    os.remove(tmp_file_name) # remove the temp. file
    return dict_quartets

"""Return the total weight of satisfied quartets"""
def get_satisfied_weights(dict_quartets, dict_quartets_stree):
    # dict_quartets may/may not be sorted wqrts
    satisfied_weights = 0

    for quartet in dict_quartets:
        if quartet in dict_quartets_stree:
            satisfied_weights += dict_quartets[quartet]
        
    return satisfied_weights

"""Get required score(s) according to quartet score level"""
def get_string_output(qscore_level, satisfied_wqrts, total_weight_wqrts):
    if qscore_level == 1:
        s = str(satisfied_wqrts)
    
    elif qscore_level == 2:
        if total_weight_wqrts == 0:
            percent_satisfied = 0
        else:
            percent_satisfied = float(satisfied_wqrts) / float(total_weight_wqrts)
        
        s = str(satisfied_wqrts) + "\t" + str(total_weight_wqrts) + "\t" + str(percent_satisfied)
    else:
        s = ""
    return s

""" Main function """
def main(input_file_wqrts, stree_file, qscore_level, qscore_output_file=None):
    dict_quartets, total_weight_wqrts = get_input_wqrts(input_file_wqrts)

    dict_quartets_stree = get_dictionary_quartets(stree_file)

    satisfied_weights = get_satisfied_weights(dict_quartets, dict_quartets_stree)

    s = get_string_output(qscore_level, satisfied_weights, total_weight_wqrts)
    print(s)

    if qscore_output_file != None:
        with open(qscore_output_file, mode='w') as fout:
            fout.write(s)
            fout.write("\n")


#################################################################################################

if __name__ == '__main__':
    
    lens = len(sys.argv)

    if (lens == 4) or (lens == 5):
        input_file_wqrts = sys.argv[1]
        stree_file = sys.argv[2]
        qscore_level = int(sys.argv[3]) # nothing done here.
        
        if lens == 5:
            qscore_output_file = sys.argv[4]
            main(input_file_wqrts, stree_file, qscore_level, qscore_output_file)
        else:
            main(input_file_wqrts, stree_file, qscore_level)

    else:
        printUsageExit()