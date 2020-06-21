# wQFM-2020
wQFM implementation in Java 

## Initial Custom DataStructure
```
Will not use single list of quartets because then dummy taxa containing quartets will mess up the strategy of sorting exactly once before everything begins.
```

Table 1: List< List<Quartet> > for double_dynamic_list_of_quartets joined by row on weight.
  
Table 2: TreeMap(descending order) of Pair<double,int> for list of <weight,row_index_to_table_1> values.

Table 3: HashMap<TaxaString, List<row,col> quartets> for relevant-quartets-per-tax

Table 4: List<row,col> quartets_row_columns_list for initial quartets-rows-and-columns indices to transfer to recursiveDNC function initially.
