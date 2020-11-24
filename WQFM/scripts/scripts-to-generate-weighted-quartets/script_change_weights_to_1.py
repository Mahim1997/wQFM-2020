import sys

inputFileName = sys.argv[1]
outputFileName = sys.argv[2]



with open(inputFileName, "r") as fin:
    with open(outputFileName, "w") as fout:
        line = fin.readline()
        while line:
            line = line.strip() # remove newline
            lines_split = line.split(" ") # split by space
            triplets_or_quartets = lines_split[0]

            new_str = triplets_or_quartets + " 1"
            # print(new_str)
            fout.write(new_str)
            fout.write("\n")
            ## read next line ##
            line = fin.readline()
