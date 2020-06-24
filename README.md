# wQFM-2020
wQFM implementation in Java 

## Initial Custom DataStructure
```
** WILL use single list of quartets. (for now weights are stored in double ... maybe long will be needed for high weights ?)
```
*** No (row,col) to store a quartet. Now only an integer to store a quartet.
*** CustomDatastructuresPerLevel will contain everything required ... so no need to explicitly pass Q,P, etc.
*** InitialTable will be a separate class (maybe needed to implement multi-threading in the future).

DS 1: InitialTable List<Quartets>
DS 2: CustomDatastructuresPerLevel (will contain required Q,P,relevant-quartets-per-taxa-mapping, etc)
