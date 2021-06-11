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
    key_4Tax_seq = (t1, t2, t3, t4)

    if key_4Tax_seq not in dictionary_quartets: # Initialize as list
        dictionary_quartets[key_4Tax_seq] = []

    dictionary_quartets[key_4Tax_seq].append((tax1, tax2, tax3, tax4, weight)) # append to the list



#######################################################
# def find_stats(inputFile, THRESHOLD_TWO_QUARTETS=0.1, THRESHOLD_THREE_QUARTETS=0.15):
def find_stats(inputFile):
    with open(inputFile) as fileobject:
        for line in fileobject:
            append_to_dictionary(line)

    list_4Tax_seq = list(dictionary_quartets.keys()) # keep only the keys.

    # list_weights_4Tax_seq = []


    idx_key = 0
    num_four_tax_seq_with_3_qrts = 0
    num_total_four_tax_seq = 0
    list_ratios = [] # ratio = q1.w/(q2.w + q3.w) # where q1 is highest-weighted-quartet, and q3 is lowest-weighted

    for key_4TaxSeq in list_4Tax_seq:
        num_total_four_tax_seq += 1
        # list_weights_4Tax_seq.append([])
        quartetsThis4TaxSeq = dictionary_quartets[key_4TaxSeq] # the three weights
        temp_list_weights_this4TaxSeq = []
        for quartet in quartetsThis4TaxSeq:
            (_, _, _, _, w) = quartet # unwarpping only the weights 
            temp_list_weights_this4TaxSeq.append(float(w)) # appending to list of weights
        # list_weights_4Tax_seq[idx_key].sort(reverse=True) # descending order reversal of three weights
        temp_list_weights_this4TaxSeq.sort(reverse=True)
        
        if len(temp_list_weights_this4TaxSeq) == 3:  # custom ratios table
            num_four_tax_seq_with_3_qrts += 1
            ratio = temp_list_weights_this4TaxSeq[0]/(temp_list_weights_this4TaxSeq[1] + temp_list_weights_this4TaxSeq[2])
            list_ratios.append(ratio)

    list_ratios.sort() # sort only in ascending order.
    
    return list_ratios, num_four_tax_seq_with_3_qrts, num_total_four_tax_seq

def create_bins(lower_bound, upper_bound, step_size):
    bins = []
    val = lower_bound
    while val < upper_bound:
        val_lower_3dp = float('%.3f'%(val))
        val += step_size
        val_higher_3dp = float('%.3f'%(val))
        _bin = (val_lower_3dp, val_higher_3dp)
        bins.append(_bin)

    return bins


def find_in_bins(list_ratios):
    bins = create_bins(lower_bound=0.5, upper_bound=1, step_size=0.05)
    thresh = 0.9

    # bins = [(0.5,0.6), (0.6,0.7), (0.7,0.8), (0.8,0.9), (0.9, 1)] # 0.5 to 1 are divided in bins, >= 1 is a separate bin [consider later]
    (_,upper_lim_of_last_bin) = bins[len(bins)-1] # last bin's upper-limit
    # print(upper_lim_of_last_bin)
    normal_range_counts = 0
    last_range_counts = 0
    total_counts_all = 0
    
    dictionary_bins = {}
    for _bin in bins:
        dictionary_bins[_bin] = 0 # initially each bin has 0 ratios/elements
    for ratio in list_ratios:
        total_counts_all += 1
        if ratio >= upper_lim_of_last_bin:
            last_range_counts += 1
        else:
            normal_range_counts += 1
        
        for _bin in bins:
            (lower_lim, upper_lim) = _bin # unwrap the bin
            if(ratio >= lower_lim and ratio < upper_lim):
                dictionary_bins[_bin] += 1 # incrememnt count for this bin


    # compute frequency wise mid-point splitting of classes.
    total_weights_before_thresh = 0
    cumulative_weighted_midpoint_before_thresh = 0

    for _bin in bins:
        (lower_lim, upper_lim) = _bin # unwrap
        if lower_lim < thresh: # ALL are before threshold
            mid_point = 0.5*(lower_lim + upper_lim)
            weighted_mid_ratio = mid_point*dictionary_bins[_bin]
            
            ####### compute cumulative things ########
            cumulative_weighted_midpoint_before_thresh += weighted_mid_ratio
            total_weights_before_thresh += dictionary_bins[_bin]
            

    weighted_avg_bin_ratio_before_thresh = cumulative_weighted_midpoint_before_thresh/total_weights_before_thresh

    total_weights_after_thresh = 0
    cumulative_weighted_midpoint_after_thresh = 0
    
    for _bin in bins:
        (lower_lim, upper_lim) = _bin # unwrap
        if lower_lim >= thresh: # ALL are after threshold
            mid_point = 0.5*(lower_lim + upper_lim)
            weighted_mid_ratio = mid_point*dictionary_bins[_bin]
            
            ####### compute cumulative things ########
            cumulative_weighted_midpoint_after_thresh += weighted_mid_ratio
            total_weights_after_thresh += dictionary_bins[_bin]
            

    weighted_avg_bin_ratio_after_thresh = cumulative_weighted_midpoint_after_thresh/total_weights_after_thresh

    return weighted_avg_bin_ratio_before_thresh, weighted_avg_bin_ratio_after_thresh, normal_range_counts, last_range_counts, total_counts_all, dictionary_bins

# F1,F2,F3,F4,F5,F6,F7,F8, ratios_table = find_stats(sys.argv[1], THRESHOLD_TWO_QUARTETS=0.5, THRESHOLD_THREE_QUARTETS=0.6667) # Explanation will be provided.

list_ratios, num_four_tax_seq_with_3_qrts, num_total_four_tax_seq = find_stats(sys.argv[1])

weighted_avg_bin_ratio_before_thresh, weighted_avg_bin_ratio_after_thresh, normal_range_counts, last_range_counts, total_counts_all, dictionary_bins = find_in_bins(list_ratios)

if len(sys.argv) == 2:
    print(f"{weighted_avg_bin_ratio_before_thresh} {weighted_avg_bin_ratio_after_thresh} {normal_range_counts/total_counts_all} {last_range_counts/total_counts_all}")
    # print(dictionary_bins)

    # print(proportion_with_greater_than_or_equal_to_1, weighted_avg_bin_ratio)
    # print(dictionary_bins)
    # print(num_four_tax_seq_with_3_qrts, num_total_four_tax_seq, (num_four_tax_seq_with_3_qrts/num_total_four_tax_seq))
    # print(list_ratios)
    # pass
else: # Print to output file
    f = open(str(sys.argv[2]), "w")
    f.write(str_to_write)
    f.close()
