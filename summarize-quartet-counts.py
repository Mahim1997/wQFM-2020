import pandas as pd
import sys
from collections import OrderedDict

if not sys.stdin.isatty():
    input_stream = sys.stdin


output_file_name = sys.argv[1] # argv[0] is THIS script itself

# dictionary={
#     '((a,b),(c,d))': 1,
#     '((a,c),(b,d))': 2,
#     '((a,d),(b,c))': 3,
# }

dictionary = {} # empty dictionary

for line in input_stream:
    line = line.replace("\n", "")
    # line = line + " "
    # print("<" + line + ">") # do something useful with each line
    if line not in dictionary:
        dictionary[line] = 1 # initiate with value 1
    else:
        dictionary[line] += 1 # increment


# with open(input_file_name) as f:
#     for line in f:
#         print(line)

# print(dictionary)
sorted_dict = OrderedDict(sorted(dictionary.items()))
# (pd.DataFrame.from_dict(data=dictionary, orient='index')
#    .to_csv(output_file_name, header=False, sep=" "))

(pd.DataFrame.from_dict(data=sorted_dict, orient='index')
   .to_csv(output_file_name, header=False, sep=" "))
