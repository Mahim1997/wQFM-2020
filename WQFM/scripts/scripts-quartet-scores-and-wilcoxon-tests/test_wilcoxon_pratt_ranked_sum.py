import sys
from scipy import stats


# stats.wilcoxon(x, y, zero_method='wilcox', correction=False)

# T, p_val = stats.wilcoxon(x, y, zero_method='wilcox', correction=False)

# print("T = ", T)
# print("P-val = ", p_val)


# x = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40]
# y = [1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31,33,35,37,39,41,43,45,47,49,51,53,55,57,59,61,63,65,67,69,71,73,75,77,79]

# for i in range(40):
#     print(2*i+1, end = ',')

# print("")



def process(string):
    # print("String is <", string, ">")
    arr = string.split("\t")
    # print(arr[0], "<>", arr[2])

    full_name = arr[0]
    values_all_4 = arr[2]

    arr1 = full_name.split("/")
    model_cond_name = arr1[0]
    file_name = arr1[1]
    # print(model_cond_name)

    arr2 = values_all_4.split(" ")
    fp = arr2[0]
    fn = arr2[1]
    est_edge = arr2[2]
    true_edge = arr2[3]

    rf_avg = float(fp) / float(est_edge)

    return model_cond_name, file_name, rf_avg


def obtain_dictionary_from_file(inputFile):
    dictionary = {} # Model condition: [list of rf rates]    
    with open(inputFile) as f:
        for x in f:  # each line
            if x != "":
                model_cond_name, rep_name, avg_rf = process(x)
                # print(model_cond_name, "|||", rep_name, "||", avg_rf)
                if model_cond_name not in list(dictionary.keys()):
                    dictionary[model_cond_name] = [] # initialize list
                dictionary[model_cond_name].append(avg_rf) # Append to list always    

    return dictionary




def run(inputFileName1, inputFileName2):
    dict1 = obtain_dictionary_from_file(inputFileName1)
    dict2 = obtain_dictionary_from_file(inputFileName2)

    # print(dict1)
    # print(dict2)
    
    if not PRINT_LIST_FLAG:
        print("Model Condition, T-value, P-value")

    for model_cond_name in list(dict1.keys()):
        list_val_1 = dict1[model_cond_name]
        list_val_2 = dict2[model_cond_name]

        check_if_all_same = (list_val_1 == list_val_2)

        if check_if_all_same == True:
            print(model_cond_name, ": x-y = 0 for all", len(list_val_1), " vals")
        else: 
            T, p_val = stats.wilcoxon(x = list_val_1, y = list_val_2, zero_method='pratt', correction=False)  
            if PRINT_LIST_FLAG == True:
                print(model_cond_name, ", T=", T, ",P-val=", p_val, "\nList1: ", list_val_1, "\nList2: ", list_val_2)
            else:
                # print(model_cond_name, ", T=", T, ",P-val=", p_val)
                p_val = round(p_val, 4) # to 4 DP
                print(f"{model_cond_name}, {T}, {p_val}")

###################################################################################
#################################### main #########################################






# inputFileName1 = "RF_11_Tax_WQFM_v1.txt"
# inputFileName2 = "RF_11_Tax_WQMC.txt"

if len(sys.argv) < 2:
    print("Usage: python3 test_wilcoxon_ranked_sum.py <file-1> <file-2> [PRINT_LIST_FLAG]")
    exit()

inputFileName1 = sys.argv[1]
inputFileName2 = sys.argv[2]

if1 = "RF_11_Tax_WQFM_v1.txt"
if2 = "RF_11_Tax_WQMC.txt"

PRINT_LIST_FLAG = False
if len(sys.argv) > 3:
    PRINT_LIST_FLAG = True

print("File 1 = ", inputFileName1, " File 2 = ", inputFileName2, " len args = ", len(sys.argv))

run(inputFileName1, inputFileName2)



