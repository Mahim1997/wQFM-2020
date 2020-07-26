import sys

input_file_name_1 = sys.argv[1] # argv[0] is THIS script itself
input_file_name_2 = sys.argv[2] # argv[0] is THIS script itself

print(f"input_file_name_1 = {input_file_name_1}, input_file_name_2 = {input_file_name_2}")

# dictionary={
#     '((a,b),(c,d))': 1,
#     '((a,c),(b,d))': 2,
#     '((a,d),(b,c))': 3,
# }

dict1 = {} # empty dictionary
dict2 = {} # empty dictionary

# for line in input_stream:
with open(input_file_name_1) as f:
    for line in f:
        # print(line)
        # break

        line = line.replace("\n", "")
        # line = line + " "
        # print("<" + line + ">") # do something useful with each line
        line = line.replace(" ", "")
        line = line.replace(";", "")
        line = line.replace("(", "")
        line = line.replace(")", "")

        (t1, t2, t3, t4) = line.split(",")

        if t2 < t1: # swap left partition
            temp = t2
            t2 = t1
            t1 = temp

        if t4 < t3: # swap right partition
            temp = t4
            t4 = t3
            t3 = temp

        if t1 < t3: # swap left's with right's
            temp = t1 # t1 <-> t3
            t1 = t3
            t3 = temp

            temp = t2 # t2 <-> t4
            t2 = t4
            t4 = temp

        if (t1, t2, t3, t4) not in dict1:
            dict1[(t1, t2, t3, t4)] = 1 # initiate with value 1
        else:
            dict1[(t1, t2, t3, t4)] += 1 # increment


with open(input_file_name_2) as f2:
    for line in f2:
        # print(line)
        # break

        line = line.replace("\n", "")
        # line = line + " "
        # print("<" + line + ">") # do something useful with each line
        line = line.replace(" ", "")
        line = line.replace(";", "")
        line = line.replace("(", "")
        line = line.replace(")", "")

        (t1, t2, t3, t4) = line.split(",")

        if t2 < t1: # swap left partition
            temp = t2
            t2 = t1
            t1 = temp

        if t4 < t3: # swap right partition
            temp = t4
            t4 = t3
            t3 = temp

        if t1 < t3: # swap left's with right's
            temp = t1 # t1 <-> t3
            t1 = t3
            t3 = temp

            temp = t2 # t2 <-> t4
            t2 = t4
            t4 = temp

        if (t1, t2, t3, t4) not in dict2:
            dict2[(t1, t2, t3, t4)] = 1 # initiate with value 1
        else:
            dict2[(t1, t2, t3, t4)] += 1 # increment

cnt = 0

for key1 in dict1:
    if key1 in dict2:
        if dict1[key1] != dict2[key1]:
            cnt += 1


print("No. of mismatches = ", cnt)