import sys



def printUsageExit():
    print("python get_quartet_score.py <input-wqrts> <stree-reference> <quartet-score-level> [quartet-score-output-file]")
    sys.exit()

""" Get sorted quartets a,b|c,d: w """
def getSortedQuartet(line):
    stringsReplace = ["\n", ";", "(", ")"]
    for s in stringsReplace:
        line = line.replace(s, "") ## remove these chars
        
    line = line.replace(" ", ",")  ## replace WHITESPACE with COMMA    
    arr = line.split(",") ## split by COMMA

    # arr[0:1].sort() ## Maybe not necessary
    # arr[2:3].sort() ## Maybe not necessary

    return (arr[0], arr[1], arr[2], arr[3], float(arr[4]))

"""Get list of input weighted quartets"""
def get_input_wqrts(input_file_wqrts):

    with open(input_file_wqrts, mode='r') as fin:
        lines = [l.strip() for l in fin.readlines()]

    quartets = [getSortedQuartet(line) for line in lines]
    return quartets




if __name__ == '__main__':
    
    lens = len(sys.argv)

    if (lens == 4) or (lens == 5):
        input_file_wqrts = sys.argv[1]
        stree_file = sys.argv[2]
        qscore_level = int(sys.argv[3])
    
        if lens == 5:
            qscore_output_file = sys.argv[4]
    else:
        printUsageExit()