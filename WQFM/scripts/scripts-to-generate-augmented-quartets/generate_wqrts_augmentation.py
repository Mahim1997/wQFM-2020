import sys
import random

## CONSTANTS
RANDOM_DOMINANT_MODE = False
DOMINANT_QUARTET_WEIGHT = 34
RANDOM_MODE=False


## MODES
DOMINANT_QUARTET_INITIAL_WEIGHT = 34
DOMINANT_QUARTET_FINAL_WEIGHT = 99


OTHER_TOPOLOGY_WEIGHT = 1


## Methods

def get_quartet(line):
    stringsReplace = ["\n", ";", "(", ")"]
    for s in stringsReplace:
        line = line.replace(s, "") ## remove these chars
    line = line.replace(" ", ",")  ## replace WHITESPACE with COMMA    
    arr = line.split(",") ## split by COMMA
    return (arr[0], arr[1], arr[2], arr[3], float(arr[4]))

def get_dominant_quartet_weight(initial, final):
    if RANDOM_DOMINANT_MODE == True:
        w = random.randint(initial, final)
    else:
        w = DOMINANT_QUARTET_WEIGHT
    return float(w)

def get_other_quartets(a,b,c,d,random_mode=False, lowest_weight = None, highest_weight=None):
    ## a,b|c,d -> a,c|b,d AND a,d|b,c
    if random_mode == True and lowest_weight is not None and highest_weight is not None:
        w1 = random.randint(int(lowest_weight), highest_weight)
        w2 = random.randint(int(lowest_weight), highest_weight)
        q1 = (a, c, b, d, float(w1))
        q2 = (a, d, b, c, float(w2))     
    else:
        q1 = (a, c, b, d, float(OTHER_TOPOLOGY_WEIGHT))
        q2 = (a, d, b, c, float(OTHER_TOPOLOGY_WEIGHT))
    return q1, q2

def get_newick(a,b,c,d,w):
    # return f"(({q[0]},{q[1]}),({q[2]},{q[3]})); {str(float(q[4]))}"
    q = (a,b,c,d,w)
    return f"(({q[0]},{q[1]}),({q[2]},{q[3]})); {str(float(q[4]))}\n"


def runner(inputFileName, outputFileName):
    with open(outputFileName, 'w') as fout:
        with open(inputFileName, 'r') as fin:
            line = fin.readline()
            while line:
                ## Read the dominant quartet
                q_dominant = get_quartet(line)
                
                (a,b,c,d,w) = q_dominant
                # w = float(DOMINANT_QUARTET_WEIGHT)
                w = get_dominant_quartet_weight(initial=DOMINANT_QUARTET_INITIAL_WEIGHT,
                                                final=DOMINANT_QUARTET_FINAL_WEIGHT)

                ## Get other two quartets and corresponding weights
                (q_other_1, q_other_2) = get_other_quartets(a,b,c,d, 
                                            random_mode=RANDOM_MODE,
                                            lowest_weight=0.4*w,
                                            highest_weight=w-1)

                (a1,b1,c1,d1,w1) = q_other_1
                (a2,b2,c2,d2,w2) = q_other_2

                ## Get newick representations
                n_dominant = get_newick(a,b,c,d,w)
                n_other_1  = get_newick(a1,b1,c1,d1,w1)
                n_other_2  = get_newick(a2,b2,c2,d2,w2)


                ## Write output to file
                fout.write(n_dominant)
                fout.write(n_other_1)
                fout.write(n_other_2)

                ## Keep reading new lines
                line = fin.readline()

#############################################################################

if len(sys.argv) != 3:
    print(f"Usage: python3 {sys.argv[0]} <input-file> <output-file>")
    exit()

inputFileName = sys.argv[1]
outputFileName = sys.argv[2]

runner(inputFileName, outputFileName)
print(f"inputFileName = {inputFileName}, outputFileName = {outputFileName}")

# print(f"Done running script for DOMINANT_QUARTET_WEIGHT = {DOMINANT_QUARTET_WEIGHT}, OTHER_TOPOLOGY_WEIGHT = {OTHER_TOPOLOGY_WEIGHT}")
