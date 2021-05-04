import sys

dict_4TaxSeq_to_totalWts = {} # Key: 4-tax-sequence, Value: Total-Cumulative-Weight-of-Quartets

def sort_4TaxSequence(tax1, tax2, tax3, tax4):
    list_custom = []
    list_custom.append(tax1)
    list_custom.append(tax2)
    list_custom.append(tax3)
    list_custom.append(tax4)
    list_custom.sort()
    (t1, t2, t3, t4) = list_custom
    return (t1, t2, t3, t4)
    # return list_custom

""" Appends one single quartet to dictionary
"""
def append_to_dictionary(quartet, max_mode):
    # (tax1, tax2, tax3, tax4, weight) = get_quartet(line)
    # (t1, t2, t3, t4) = sort_4TaxSequence(tax1, tax2, tax3, tax4) # any sorting order
    
    (t1, t2, t3, t4, weight) = quartet
    # print(tax1, tax2, tax3, tax4, weight, t1, t2, t3, t4)
    key_4Tax_seq = sort_4TaxSequence(t1, t2, t3, t4)

    if key_4Tax_seq not in dict_4TaxSeq_to_totalWts:
        dict_4TaxSeq_to_totalWts[key_4Tax_seq] = 0 ## initialize as ZERO

    if max_mode == False: ## use sum mode.
        dict_4TaxSeq_to_totalWts[key_4Tax_seq] = dict_4TaxSeq_to_totalWts[key_4Tax_seq] + weight
    else: ## use MAX mode
        dict_4TaxSeq_to_totalWts[key_4Tax_seq] = max(dict_4TaxSeq_to_totalWts[key_4Tax_seq], weight)
    

def get_normalized_quartet(qrt):
    (t1, t2, t3, t4, weight) = qrt
    key_4Tax_seq = sort_4TaxSequence(t1, t2, t3, t4)
    DENOMINATOR = dict_4TaxSeq_to_totalWts[key_4Tax_seq]
    weight = weight / float(DENOMINATOR)
    qrt = (t1, t2, t3, t4, weight)
    return qrt


def normalize_quartets_weights(list_quartets, max_mode=False):    
    for quartet in list_quartets:
        append_to_dictionary(quartet, max_mode=max_mode)
    
    new_list_quartets = [get_normalized_quartet(qrt) for qrt in list_quartets]
    
    # for qrt, qrt_new in zip(list_quartets, new_list_quartets):
        # print(qrt, qrt_new)
        
    return new_list_quartets
    
    
