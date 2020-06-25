# wQFM-2020
wQFM implementation in Java 

## Need to have "lib" folder in same path as jar file. (Check github/astral at https://github.com/smirarab/ASTRAL for more details on lib [uses phylonet package])
### This is needed to reroot the tree with respect to an outgroup node.

## Initial & Custom DataStructures
```
** WILL use single list of quartets. (for now weights are stored in double ... maybe long will be needed for high weights ?)
```
*** No (row,col) to store a quartet. Now only an integer to store a quartet.
*** CustomDatastructuresPerLevel will contain everything required ... so no need to explicitly pass Q,P, etc.
*** InitialTable will be a separate class (maybe needed to implement multi-threading in the future).

DS 1: InitialTable List<Quartets>
  
DS 2: CustomDatastructuresPerLevel (will contain required Q,P,relevant-quartets-per-taxa-mapping, etc)


