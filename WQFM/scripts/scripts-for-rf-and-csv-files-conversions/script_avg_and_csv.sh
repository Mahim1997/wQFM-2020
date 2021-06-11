#!/bin/bash

fileName="$1"

java -jar computeAvgRF.jar "$fileName" "AVG-""$fileName" "123"

python3 writer_csv_one_input_file.py "AVG-""$fileName" "CSV-AVG""$fileName"".csv"
