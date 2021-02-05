import dendropy
import sys


#####################################################################################################################



""" Get sorted quartets a,b|c,d: w
"""
def getSortedQuartet(line):
    stringsReplace = ["\n", ";", "(", ")"]
    for s in stringsReplace:
        line = line.replace(s, "") ## remove these chars
        
    line = line.replace(" ", ",")  ## replace WHITESPACE with COMMA    
    arr = line.split(",") ## split by COMMA

    # arr[0:1].sort() ## Maybe not necessary
    # arr[2:3].sort() ## Maybe not necessary

    return (arr[0], arr[1], arr[2], arr[3], float(arr[4]))


""" Process quartets
    1. Form a list of quartets
    2. Form a list of taxa
    3. Form a Map of <taxa: [list of qrt indices]>
"""
def processQuartets(inputQrtFile):
    list_quartets = []
    map_taxa_quartet = {}

    with open(inputQrtFile, 'r') as fin:
        line = fin.readline()
        while line:
            quartet = getSortedQuartet(line) # (t0, t1, t2, t3, w) = getSortedQuartet(line)
            # print(quartet)
            list_quartets.append(quartet)
            (t0, t1, t2, t3, w) = quartet
            for i in range(0, 4):
                if quartet[i] not in map_taxa_quartet:
                    map_taxa_quartet[quartet[i]] = [] ## initialize a list for the taxon NOT in map.
                idx_quartet = len(list_quartets) - 1
                ## Append this INDEX for each taxon.
                map_taxa_quartet[quartet[i]].append(idx_quartet)


            line = fin.readline()

    return list_quartets, map_taxa_quartet


""" Main function
"""
def run(inputQrtFile, inputStreeFile, outputFile):

    list_quartets, map_taxa_quartet = processQuartets(inputQrtFile)

    for key in map_taxa_quartet:
        print(key, map_taxa_quartet[key], "\n")


#####################################################################################################################

def printUsageExit():
    print(f"Usage: python3 {sys.argv[0]} <input-qrt-file> <input-STree-file> <output-file>")
    exit()




if __name__ == '__main__':
    if len(sys.argv) != 4:
        printUsageExit()
    
    inputQrtFile = sys.argv[1]
    inputStreeFile = sys.argv[2]
    outputFile = sys.argv[3]

    run(inputQrtFile, inputStreeFile, outputFile)

















