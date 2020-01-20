# Minimum Spanning Trees (GHS algorithm)
Implementing the algorithm is based on the paper "**A Distributed Algorithm for Spanning Trees Minimum-Weight**"
https://www.cs.tau.ac.il/~afek/p66-gallager.pdf

The GHS algorithm is implemented in Java using RMI (Remote Method Invocation) API for communication between distributed processes, which are simulated using threads.

The implementation consists of four main classes: *Main, Node, Link* and *Message*.

Main class is the starting point of the program, which takes an adjacency matrix as an input and constructs a network of nodes accordingly. Each node is started in its own thread and registered in the RMI registry. 

Next, the links are being constructed and assigned to nodes based on the adjacency matrix. Each Link contains two remote-object references (RMI stubs), a weight and a state variable. 

Nodes are communicating by passing Message objects of various types and executing procedures accordingly. 

The algorithm execution is started by waking up at least one Node (from the Main class) and finished by printing out all links in the MST. All the examples presented below were run by waking up all nodes immediately.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites


## Usage

```
javac *.java

java Main .\inputs\graph1..7

```
# TODO
- A separated class for messages
    - Class Node implement messages maybe?
- Currently the algorithm works for the first step ONLY
    - Generalize
- Introduce dalays in threads
    - for now ONLY delay at starting the threads
    - later, introduce delays in messages
- think about queues as well

- sendmsg and recievemsg LN++ -> counts twice -> becuz sent at the sametime
- best_link how to change each time what it points to
    - stuck on the 1st value

# Report
- thread.start is now happening after creating the nodes and the links.
- states has added to both link and node classes *(check if there is a better option than enum)*
- MOE for each node is calculated *this is the 1st step*            