import sys

NUM_DECIMAL_PLACES_ROUNDING = 20
multiply = 0.000001


input_file = sys.argv[1]
output_file = sys.argv[2]


with open(output_file, mode='w') as fout:
    with open(input_file, mode='r') as fin:
        lines = [l.strip() for l in fin.readlines()]
        
        for line in lines:
            arr = [x.strip() for x in line.split(";")] # ((10.10,2.2),(5.5,7.7)); 5
            
            quartet, weight = arr
            
            weight = float(weight) * multiply
            weight = round(weight, NUM_DECIMAL_PLACES_ROUNDING)
            
            weight = str(weight)
            
            line_new = quartet + "; " + weight
            # print(line_new)
            
            fout.write(line_new + "\n")
            
            
print(f"Completed writing to output_file = {output_file}")