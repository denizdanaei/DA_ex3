import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;

import java.util.ArrayList;
import jdk.nashorn.api.tree.ForInLoopTree;

public class Main{
    private static List<Node> Nodes = new ArrayList<Node>();
    public static void main (String[] args){
        int mat[][] = { { 0, 2, 3}, 
                        { 2, 0, 1}, 
                        { 3, 1, 0}};
        // Create Registry        
        try{
            Registry registry = LocateRegistry.createRegistry(1099);
        } catch (Exception e){
            System.err.println ("Could not create registry exception: " + e.toString()); 
            e.printStackTrace (); 
        }
        creategraph (mat);  
    }

    private static void creategraph (int[][] mat){
        Thread[] myThreads = new Thread [mat.length];
        for (int i = 0; i < mat.length; i++){
            try{
                Node node = new Node(i);
                Nodes.add(node);
                myThreads[i] = new Thread(node);
        } catch (RemoteException e){
                e.printStackTrace();
            }
        }
        for (int i = 0; i < mat.length; i++){
            for (int j = i+1; j < mat.length; j++){
                createlinks (mat[i][j], Nodes.get(i), Nodes.get(j));
            }
        }
        //run threads
        for (int i = 0; i < mat.length; i++){
            myThreads[i].start();
        }
    }
    private static void createlinks (int weight, Node node1, Node node2){
        Link link = new Link (weight, node1, node2);
        node1.links.add (link);
        node2.links.add (link);
    }

}