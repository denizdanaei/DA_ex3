import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ArrayList;

public class Main{

    public static Registry rmireg;
    
    public static void main (String[] args){
 
        int mat[][] = { { 0, 2, 3, 0}, 
                        { 2, 0, 1, 0}, 
                        { 3, 1, 0, 4},
                        { 0, 0, 4, 0}};
        createGraph(mat);  
    }

    private static void createGraph (int[][] mat){
        
        Thread[] myThreads = new Thread [mat.length];
        List<NodeInterface> nodes = new ArrayList<NodeInterface>();
        
        // Create RMI registry
        try {
            rmireg = LocateRegistry.createRegistry(1099);
        } catch (Exception e) {
            System.out.println("Exception @creatingRegistry");
            System.exit(1);
        }
        // Create Nodes and register them to RMI registry
        for (int i = 0; i < mat.length; i++){
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
        for (int i = 0; i < mat.length; i++){
            for (int j = i+1; j < mat.length; j++){
                if ( mat[i][j] != 0)
                createlinks (mat[i][j], nodes.get(i), nodes.get(j));
                
            }
        }
        for (int i = 0; i < mat.length; i++){
            
            myThreads[i].start();
        }
        // myThreads[1].start();

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


 */