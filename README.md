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
