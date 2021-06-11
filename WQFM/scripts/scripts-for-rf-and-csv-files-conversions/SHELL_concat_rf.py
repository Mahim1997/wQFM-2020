import pandas as pd
import re
import sys

def get_folder_name(file_name):
    arr = file_name.split("/")
    return str(arr[0])


def compute_avg_stats(name_to_write, file_input, csv_file):
    # file_input = "RF.txt"
    list_file_names = []

    list_fn = []
    list_fp = []

    list_edges_est = []
    list_edges_true = []

    ### Open file and take lists ###
    with open(file_input) as fp:
        line = fp.readline()
        while line:
            line = line.replace("\n", "")
            line = re.sub('[\t ]+', ' ', line) ## removes tabs/spaces/newlines
            # print(line)

            arr_splits = line.split(" ")
            # print(arr_splits)

            line = fp.readline()

            list_file_names.append(arr_splits[0])
            list_fn.append(float(arr_splits[1]))
            list_fp.append(float(arr_splits[2]))
            list_edges_est.append(float(arr_splits[3]))
            list_edges_true.append(float(arr_splits[4]))


    list_of_tuples = zip(list_file_names, list_fn, list_fp, list_edges_est, list_edges_true)

    df = pd.DataFrame(list_of_tuples, columns=['File', 'FN', 'FP', 'EDGESEST', 'EDGESTRUE']) 

    df['Model-Condition'] = df.apply(lambda row : get_folder_name(row['File']), axis=1)

    df['FNAVG'] = (df['FN'])/(df['EDGESEST'])
    df['FPAVG'] = (df['FP'])/(df['EDGESTRUE'])
    df['RFAVG'] = ((df['FP']) + (df['FN']))/(2*df['EDGESTRUE'])

    df.drop(['File', 'FN', 'FP', 'FNAVG', 'FPAVG', 'EDGESEST', 'EDGESTRUE'], axis=1, inplace=True)

    mean = df.groupby(['Model-Condition'], sort=False).mean()
    count = df.groupby(['Model-Condition'], sort=False).count()

    # std_err = df.groupby(['Model-Condition']).sem()
    std_err = df.groupby(['Model-Condition'], sort=False).sem(ddof=0) ## population standard deviation

    mean.columns = ['Avg-RF']
    mean = mean['Avg-RF'].values
    # print(type(mean))


    df_read = pd.read_csv(csv_file)
    df_read[name_to_write] = mean


    # list_cols = df_read.columns
    # list_cols.remove(['temp'])
    # list_cols.append(name_to_write)
    # df_read.columns = list_cols 

    df_read.to_csv(csv_file, index=False)

    # print(df_read)

###################################################### main ######################################################

def usage():
    print("python3 script.py <name-to-write> <input-RF-file> <output-CSV-file-name>")
    exit()


if len(sys.argv) != 4:
    usage()


compute_avg_stats( str(sys.argv[1]), str(sys.argv[2]), str(sys.argv[3]) )

print("Done for ", str(sys.argv[1]), str(sys.argv[2]), str(sys.argv[3]))