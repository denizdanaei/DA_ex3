import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static Registry rmireg;

    public static void main(String[] args) throws FileNotFoundException {

        // Parse input args and read graph file
        if (args.length != 1) {
            System.out.println("Usage: java Main <inputFile>");
            System.exit(1);
        }
        int mat[][] = parseGraph(args[0]);
        System.out.println("Running example " + args[0]);


        // Create RMI registry
        try {
            rmireg = LocateRegistry.createRegistry(1099);
        } catch (Exception e) {
            System.out.println("Exception @creatingRegistry");
            System.exit(1);
        }

        // Create Nodes and register them to RMI registry
        Thread[] myThreads = new Thread[mat.length];
        List<NodeInterface> nodes = new ArrayList<NodeInterface>();
        
        for (int i = 0; i < mat.length; i++) {
            Node node = new Node(i);
            myThreads[i] = new Thread(node);
            myThreads[i].start();
            try {
                NodeInterface nodeStub = (NodeInterface) UnicastRemoteObject.exportObject(node, 0);
                nodes.add(nodeStub);
            } catch (Exception e) {
                System.out.println("Exception @createGraph");
                System.exit(1);
            }
        }
        System.out.println();

        // Create links
        for (int i = 0; i < mat.length; i++) {
            for (int j = i + 1; j < mat.length; j++) {
                if (mat[i][j] != 0) createlinks(mat[i][j], nodes.get(i), nodes.get(j));
            }
        }

        // Wake up nodes
        try {
            // for (NodeInterface n : nodes) n.wakeup();
            nodes.get(2).wakeup();
        } catch (Exception e) {
            System.out.println("Exception @wakeup");
            System.exit(1);
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
    // test mat_1 meta data
        try {
            (nodes.get(0)).set_fragmentID(1);
            (nodes.get(0)).set_fragmentLevel(1);;
            
        } catch (Exception e) {
            System.out.println("Exception @set_newFragment_details");
            System.exit(1);
        }
 */