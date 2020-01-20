# Minimum Spanning Trees (GHS algorithm)
This program Executes the MST using the GHS algorithm. 

For more information about the algorithm see the paper "**A Distributed Algorithm for Spanning Trees Minimum-Weight**"
https://www.cs.tau.ac.il/~afek/p66-gallager.pdf

The algorithm is implemented in Java using RMI (Remote Method Invocation) API for communication between distributed processes, which are simulated using threads.

The implementation consists of four classes: *Main, Node, Link* and *Message*.

The `Main ` class is the starting point of the program, which takes an adjacency matrix as an input and constructs a network of nodes accordingly. Each node is registered in the RMI registry and starts its thread.

Next,  based on the adjacency matrix, the links are created and assigned to the nodes. Each link contains two remote-object references (RMI stubs), a weight and a state variable. 

Nodes are communicating by passing Message objects of various types and executing procedures accordingly. 

The algorithm starts by waking up at least one node (from the Main class) and finishes by printing out all the MST links.

## Usage

```
javac *.java

java Main .\inputs\graph1

```

## Running the tests
`javac *.java`  command compiles the project and  `java Main .\inputs\graph1` command runs the graph1 example.

The result shown in the terminal will be the nodes *waking up*, getting *merged/absorbed*  followed by all the *MST links* at the end of the program.

You can run any of the examples in the input folder or simply by creating a new file containing the adjacency matrix in the same format and running `javac *.java  && java Main .\<directory>\<filename>`

## Authors
- [Deniz Danaie](https://github.com/denizdanaie)
- [Jure Vidmar](https://github.com/jurc192) 
