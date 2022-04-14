# COP4520-A3

## Problem 1

### Installation

1. Navigate to the folder where presentParty.java is located
2. Compile the program by running `javac presentParty.java`
3. Run the program by running `java presentParty`

### Output

When a servant adds a present to the chain
`Add present X to present chain.`

When a servant writes a thank you note
`Servant X writes thank you note for present Y`

When a servant checks a present is in the chain
`Present X is in the present chain!`

When a servant checks a present is not in the chain
`Present X is not in the present chain`

### Proof of Correctness

This program utilizes LazyList. Each node represents a gift and contains a lock. To perform an add or remove operation on the present chain, a servant will traverse and list, find corresponding predecessor and current node, lock the node and validate that they are the correct nodes to perform the operation on.

### Experimental Evaluation & Efficiency

Add and remove operation still involves lock which can be further imporved. The contain operation in this program will be wait-free.

## Problem 2

### Installation

1. Navigate to the folder where presentParty.java is located
2. Compile the program by running `javac temperature.java`
3. Run the program by running `java temperature`

### Output

A report will be generated every hour with the following formate

======================================================================  
Current Hour: T
Top 5 highest temperature: [X, X, X, X, X]  
Top 5 lowest temperature: [Y, Y, Y, Y, Y]  
Largest temperature difference of D was observed from M to N minute  

======================================================================  

### Proof of Correctness

This program simulates temperature reading for 10 hours with 8 threads running simultaneously. Temperature reading for every minute is simulated by having the thread generate a random number every 1 millisecond. Shared memory storage has two heaps storing the max 5 and min 5 temperature readings within an hour, and the data refreshes every hour. It also records the max temperature difference within every 10 minutes within an hours, this data also refreshes every 10 minutes. The program utilizes semaphore lock for the part where it updates the temperature list and max difference interval to ensure that only one thread can update the share memory space.

### Experimental Evaluation & Efficiency

Running 8 threads ensures the reading can be proceeded simultaneously, using semaphore lock ensures the shared memory space is being carefully handled.
