import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    public static Registry rmireg;

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length != 1) {
            System.out.println("Usage: java Main <inputFile>");
            System.exit(1);
        }

        int mat[][] = parseGraph(args[0]);


        Thread[] myThreads = new Thread[mat.length];
        List<NodeInterface> nodes = new ArrayList<NodeInterface>();

        // Create RMI registry
        try {
            rmireg = LocateRegistry.createRegistry(1099);
        } catch (Exception e) {
            System.out.println("Exception @creatingRegistry");
            System.exit(1);
        }

        // Create Nodes and register them to RMI registry
        for (int i = 0; i < mat.length; i++) {
            Node node = new Node(i);
            myThreads[i] = new Thread(node);
            try {
                NodeInterface nodeStub = (NodeInterface) UnicastRemoteObject.exportObject(node, 0);
                nodes.add(nodeStub);
            } catch (Exception e) {
                System.out.println("Exception @createGraph");
                System.exit(1);
            }
        }

        // Create links
        for (int i = 0; i < mat.length; i++) {
            for (int j = i + 1; j < mat.length; j++) {
                if (mat[i][j] != 0)
                    createlinks(mat[i][j], nodes.get(i), nodes.get(j));
            }
        }

        for (int i = 0; i < mat.length; i++){   
            myThreads[i].start();
        }
    }

    private static int[][] parseGraph(String filename) throws FileNotFoundException {

        // Find matrix dimension
        int n = new Scanner(new File(filename)).nextLine().split(" ").length;
        int[][] matrix = new int[n][n];
        
        // Parse integers into array
        Scanner s = new Scanner(new File(filename));
        for (int i=0; i<n; i++) {
            for (int j=0; j<n; j++) {
                matrix[i][j] = s.nextInt();
            }
        }

        return matrix;
    }

    private static void createlinks (int weight, NodeInterface node1, NodeInterface node2){

        Link link = new Link (weight, node1, node2);
        try {
            node1.addLink(link);
            node2.addLink(link);
        } catch (Exception e) {
            System.out.println("Exception @createLinks");
            System.exit(1);
        }
    }

}

/**
 *         
 *          
           int mat[][] = { { 0, 1, 0, 0, 0, 0, 0, 8}, //node 0
                        { 1, 0, 5, 0, 0, 0, 0, 0}, //node 1
                        { 0, 5, 0, 3, 0, 0, 0, 0}, //node 2
                        { 0, 0, 3, 0, 7, 0, 0, 0}, //node 3
                        { 0, 0, 0, 7, 0, 2, 0, 0}, //node 4
                        { 0, 0, 0, 0, 2, 0, 6, 0}, //node 5
                        { 0, 0, 0, 0, 0, 6, 0, 4}, //node 6
                        { 8, 0, 0, 0, 0, 0, 4, 0}};//node 7
*
*
*
            int mat[][] = { { 0, 1, 3}, //node 0
                            {1, 0, 2} , //node 1
                            {3, 2, 0}};  //node 2 
*
*

        int mat[][] =  {{ 0, 2, 3, 0 },
                        { 2, 0, 1, 0 },
                        { 3, 1, 0, 4 },
                        { 0, 0, 4, 0 }};                                                       


    // test mat_1 meta data
        try {
            (nodes.get(0)).set_fragmentID(1);
            (nodes.get(0)).set_fragmentLevel(1);;
            
        } catch (Exception e) {
            System.out.println("Exception @set_newFragment_details");
            System.exit(1);
        }
 */