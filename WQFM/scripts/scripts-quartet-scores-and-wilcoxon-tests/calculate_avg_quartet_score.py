import sys


fileName = sys.argv[1]


dictionary_model_conditions = {} # store per model-condition

def insert_to_dictionary(line):
    line = line.replace("\n", "")
    str_arr = line.split(" ")
    # print(str_arr)
    (file_name, qscore, total_weight, normalized_qscore) = str_arr
    str_arr2 = line.split("/")
    model_cond = str_arr2[0]
    # print(model_cond)
    # print(model_cond, qscore, normalized_qscore, total_weight)
    if model_cond not in list(dictionary_model_conditions.keys()):
        dictionary_model_conditions[model_cond] = []

    dictionary_model_conditions[model_cond].append((qscore, total_weight, normalized_qscore))


def calculate_avg_stuffs(dictionary_model_conditions):
    dictionary2 = {}
    for model in dictionary_model_conditions:
        # dictionary2[model] = []
        list_things = dictionary_model_conditions[model]
        mean_q_score = 0
        mean_total_weight = 0
        mean_normalized_score = 0

        for v in list_things:  
            mean_q_score += float(v[0])
            mean_total_weight += float(v[1])
            mean_normalized_score += float(v[2])

        mean_q_score /= len(list_things)
        mean_total_weight /= len(list_things)
        mean_normalized_score /= len(list_things)
        
        dictionary2[model] = ((mean_q_score, mean_total_weight, mean_normalized_score))

    return dictionary2


#################################### main ##################################
with open(fileName,'r') as fr:
    line = fr.readline()
    while line:
        # print(line)
        insert_to_dictionary(line)
        line = fr.readline()



dict2 = calculate_avg_stuffs(dictionary_model_conditions)

print("Model Condition,Meqn Quartet Score,Mean Total Weight, Mean Percent Score")

for model in dict2:
    # print(model, ": ", dict2[model])
    (mean_q_score, mean_total_weight, mean_normalized_score) = dict2[model]
    percent_score = round((mean_normalized_score*100), 3)

    print(model, ",", mean_q_score, ",", mean_total_weight, ",", percent_score)

