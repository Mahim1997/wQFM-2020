import pandas as pd
import re
import sys

def get_folder_name(file_name):
    arr = file_name.split("/")
    return str(arr[0])


def compute_avg_stats(file_input, file_output):
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

    # std_err = (df.groupby('Model-Condition').agg(lambda x: x.std()/x.count().add(0).pow(0.5)))
    # std_err = (df.groupby('Model-Condition').agg(lambda x: x.std()/x.count().add(-1).pow(0.5)))


    # print(mean)
    # print(count)
    # print(std_err)

    # df_final = reduce(lambda left,right: pd.merge(left,right,on='name'), dfs)
    # print(df[['Model-Condition', 'FNAVG', 'RFAVG']].head(10))

    # df_final = pd.merge(pd.merge(mean,std_err,on='Model-Condition'),count,on='Model-Condition')
    # df_final.columns = ['Avg-RF', 'Std-Err', '#Folders']

    df_final = mean
    df_final.columns = ['Avg-RF']


    # print(df_final)
    df_final.to_csv(file_output, index=True)


###################################################### main ######################################################

def usage():
    print("python3 script.py <input-RF-file> <output-CSV-file-name>")
    exit()


if len(sys.argv) != 3:
    usage()


compute_avg_stats( str(sys.argv[1]), str(sys.argv[2]) )