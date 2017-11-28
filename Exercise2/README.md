# Component class 

The current process operates according to Peterson's election algorithm in a unidirectional ring. Every process receives its downstream neighbour's id and compares that with its own id. It sends the higher of the two values to its downstream neighbour 
Condition for staying active: 
nid >= id && nid >= nnid
If the above are not met (**both of them**), the current process is set to PASSIVE
**NOT IMPLEMENTED YET** If the condition for staying active is true, the current process takes the highest value of the ones he has, e.g. if id = 5, nid = 6 and nnid = 6, process stays ACTIVE with value 6 (i.e. the value of nid ?? i think)
If condition is false, the curr process gets killed for now until its elected at some point. 

## Peterson's algorithm is explained by the teacher in previous years [HERE](https://collegerama.tudelft.nl/Mediasite/Play/cb6da7ce5002457fb804557758e222a11d?catalog=528e5b24-a2fc-4def-870e-65bd84b28a8c) at time: 0:43:51 