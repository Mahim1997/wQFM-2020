import matplotlib.pyplot as plt
import numpy as np
import sys

dictionary_quartets = {} # dictionary to store quartets

def sort_quartets(tax1, tax2, tax3, tax4):
    list_custom = []
    list_custom.append(tax1)
    list_custom.append(tax2)
    list_custom.append(tax3)
    list_custom.append(tax4)
    list_custom.sort()
    # (t1, t2, t3, t4) = list_custom
    return list_custom

def is_within_range(v1,v2,threshold):
    return (v1-v2)/((v1+v2)/2)<=threshold


def append_to_dictionary(line):
    for ch in ['(',')', ';']:
        if ch in line:
            line = line.replace(ch, "")
    line = line.replace(" ", ",")
    (tax1, tax2, tax3, tax4, weight) = line.split(",")
    weight = float(weight)

    (t1, t2, t3, t4) = sort_quartets(tax1, tax2, tax3, tax4) # any sorting order

    # print(tax1, tax2, tax3, tax4, weight, t1, t2, t3, t4)
    key_current = (t1, t2, t3, t4)

    if key_current not in dictionary_quartets: # Initialize as list
        dictionary_quartets[key_current] = []

    dictionary_quartets[key_current].append((tax1, tax2, tax3, tax4, weight)) # append to the list



#######################################################
def find_stats(inputFile, THRESHOLD_TWO_QUARTETS=0.1, THRESHOLD_THREE_QUARTETS=0.15):

    with open(inputFile) as fileobject:
        for line in fileobject:
            append_to_dictionary(line)

    keys = list(dictionary_quartets.keys())
    four_tax_seq = keys[0:]

    list_names = four_tax_seq

    # list_four_tax_sequence = [(1,1,2,3,9),(1,1,2,4)]
    # x = [1,2]
    x = np.arange(len(four_tax_seq))
    list_four_tax_sequence = []
    features = 8
    '''Does this sequence contain only 1 type of quartet ? [Binary]
    Does this sequence contain only 2 types of quartets ? [Binary]
    Does this sequence contain all 3 types of quartets ? [Binary]
    Does this sequence have 2 top quartets weights <= 10%? [Binary]
    Does this sequence have 3 quartets weights <= 15% ? [Binary]
    '''
    results_table = np.zeros((len(four_tax_seq),features))

    ratios_table = np.zeros((len(four_tax_seq))) # it will be one dimensional only to store ratios

    idx_key = 0
    for key in four_tax_seq:
        list_four_tax_sequence.append([])
        vals = dictionary_quartets[key]
        for val in vals:
            (_1, _2, _3, _4, w) = val
            list_four_tax_sequence[idx_key].append(float(w))
        list_four_tax_sequence[idx_key].sort(reverse=True)
        #print(len(list_four_tax_sequence[idx_key]))
        if len(list_four_tax_sequence[idx_key])==1:
            results_table[idx_key][0] =  1
        if len(list_four_tax_sequence[idx_key])==2:
            results_table[idx_key][1] =  1
        if len(list_four_tax_sequence[idx_key])==3:
            results_table[idx_key][2] =  1
        if ( len(list_four_tax_sequence[idx_key])==2 or len(list_four_tax_sequence[idx_key])==3 ) and is_within_range(list_four_tax_sequence[idx_key][0],list_four_tax_sequence[idx_key][1], THRESHOLD_TWO_QUARTETS):
            results_table[idx_key][3] =  1  # AND condition so, we get Pr(A and B)
        if len(list_four_tax_sequence[idx_key])==3 and is_within_range(list_four_tax_sequence[idx_key][0],list_four_tax_sequence[idx_key][1], THRESHOLD_THREE_QUARTETS):
            results_table[idx_key][4] =  1
        if len(list_four_tax_sequence[idx_key])==3 and (2*list_four_tax_sequence[idx_key][0] - list_four_tax_sequence[idx_key][1] - list_four_tax_sequence[idx_key][2])<0:
            results_table[idx_key][5] =  1
        if len(list_four_tax_sequence[idx_key])==3 and (3*list_four_tax_sequence[idx_key][0] - 2*list_four_tax_sequence[idx_key][1] - 2*list_four_tax_sequence[idx_key][2])<0:
            results_table[idx_key][6] =  1
        if len(list_four_tax_sequence[idx_key])==3:
            results_table[idx_key][7] =  (list_four_tax_sequence[idx_key][0]/(list_four_tax_sequence[idx_key][1] + list_four_tax_sequence[idx_key][2]))
        if len(list_four_tax_sequence[idx_key]) == 3:  # custom ratios table
            ratios_table[idx_key] = (list_four_tax_sequence[idx_key][0]/(list_four_tax_sequence[idx_key][1] + list_four_tax_sequence[idx_key][2]))

        #print(list_four_tax_sequence[idx_key])
        #print(results_table[idx_key])
        idx_key += 1

    #print(len(list_four_tax_sequence))
    #print(list_four_tax_sequence)
    #print(results_table)
    #print("Overall statistics")
    sums = np.sum(results_table,axis=0)
    F1 = (sums[0]/len(list_four_tax_sequence))
    F2 = (sums[1]/len(list_four_tax_sequence))
    F3 = (sums[2]/len(list_four_tax_sequence))
    F4 = (sums[3]/(len(list_four_tax_sequence)-sums[0])) # Pr(A|B) = Pr(A and B) / Pr(B)
    F5 = (sums[4]/sums[2])
    F6 = (sums[5]/sums[2])
    F7 = (sums[6]/sums[2])
    F8 = (sums[7]/sums[2])
    return F1,F2,F3,F4,F5,F6,F7,F8, ratios_table




F1,F2,F3,F4,F5,F6,F7,F8, ratios_table = find_stats(sys.argv[1], THRESHOLD_TWO_QUARTETS=0.5, THRESHOLD_THREE_QUARTETS=0.6667) # Explanation will be provided.

# str_to_write = str(F1) + " " + str(F2) + " " + str(F3) + " " + str(F4) + " " + str(F5) + " " + str(F6) + " "+ str(F7) + "  " + str(F8)

# print(str_to_write)

# sorted_ratios_table = np.sort(ratios_table, axis=0)
ratios_table.sort() # ascending order [MIN is first element]
minRatio = ratios_table[0]
# print(ratios_table[0])

# for val in ratios_table:
#     print(val)

if len(sys.argv) == 2:
    # print(str_to_write) # Only print to terminal
    print(str(F5) + " , " + str(minRatio))
    # pass
else: # Print to output file
    f = open(str(sys.argv[2]), "w")
    f.write(str_to_write)
    f.close()
