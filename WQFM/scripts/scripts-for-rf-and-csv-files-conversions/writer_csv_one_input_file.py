import csv
import sys

def write_to_csv(inputFileName, outputFileName):
    with open(outputFileName, 'a', newline='') as f:
        fields = ['Model Condition', 'Avg Branch Rate', 'Std error', 'No. Valid Folders']
        
        thewriter = csv.DictWriter(f, fieldnames=fields)

        ############################### WRITE HEADERS #########################
        thewriter.writeheader()
        ######################### WRITE EACH ROW #############################
        with open(inputFileName) as f1:
            for x in f1:
                print("X = " + x)
                if "Folder Name" in x:
                    # for folder name
                    arr = x.split(":")
                    folderName = arr[1].strip()
                elif "No. Folders" in x:
                    arr = x.split(":")
                    num_folders = arr[1].strip()
                elif "Avg Branch rate" in x:
                    # compare both
                    arr = x.split(":")
                    method_avg_branch_rate = arr[1].strip()
                elif "Std. Error" in x:
                    arr = x.split(":")
                    method_std_error = arr[1].strip()
                elif "--------" in x:
                    # Now write to CSV ...
                    thewriter.writerow({'Model Condition': folderName,
                    'Avg Branch Rate': method_avg_branch_rate,
                    'Std error': method_std_error,
                    'No. Valid Folders': num_folders})
    
            thewriter.writerow({})  # new line print to separate for MODEL condition
    #################################################################################################################


def usage():
    print("python3 writer_csv_METHOD.py <input-file-name> <output-csv-file-name>")
    exit()

if len(sys.argv) != 3:
    usage()

inputFileName = sys.argv[1]
outputFileName = sys.argv[2]
write_to_csv(inputFileName, outputFileName)




