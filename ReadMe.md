# Minimum Spanning Trees (GHS algorithm)
Implementing the algorithm is based on the paper "**A Distributed Algorithm for Spanning Trees Minimum-Weight**"
https://www.cs.tau.ac.il/~afek/p66-gallager.pdf

The GHS algorithm is implemented in Java using RMI (Remote Method Invocation) API for communication between distributed processes, which are simulated using threads.

The implementation consists of four main classes: *Main, Node, Link* and *Message*.

Main class is the starting point of the program, which takes an adjacency matrix as an input and constructs a network of nodes accordingly. Each node is started in its own thread and registered in the RMI registry. 

Next, the links are being constructed and assigned to nodes based on the adjacency matrix. Each Link contains two remote-object references (RMI stubs), a weight and a state variable. 

Nodes are communicating by passing Message objects of various types and executing procedures accordingly. 

The algorithm execution is started by waking up at least one Node (from the Main class) and finished by printing out all links in the MST. All the examples presented below were run by waking up all nodes immediately.

## Prerequisites
libraries used?

## Usage

```
javac *.java

java Main .\inputs\graph1

```

## Running the tests
By using the command `javac *.java` you can compile the project and run one the examples in the `.\inputs\` folder with `java Main .\inputs\graph1` command.

The  result shown in the terminal will be the nodes *waking up*, getting *merged/absorbed* and at the end of the program the *MST links* will be printed out.

You can run your own examples by creating a file containing the adjacency matrix and simply running `java Main .\<directory>\<filename>`

## Authors
- [Deniz Danaie](https://github.com/denizdanaie)
- [Jure Vidmar](https://github.com/jurc192) 

## Acknowledgments
- timings?