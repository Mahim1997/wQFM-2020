# wQFM-2020
wQFM implementation in Java 

## Initial Custom DataStructure
```
** WILL use single list of quartets.
```
*** No (row,col) to store a quartet. Now only an integer to store a quartet.

Table 1: List<Quartets> for list_of_quartets (we can sort  using TreeMap<wt, List<Integer>> quartets-indices-with-same-weight desc/asc order)

Table 2: TreeMap(descending order) of Pair<double,int> for list of <weight,row_index_to_table_1> values.

Table 3: HashMap<TaxaString, List<Integer> quartets> for relevant-quartets-per-tax

