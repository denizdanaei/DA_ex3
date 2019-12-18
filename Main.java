import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;

import java.util.ArrayList;
import jdk.nashorn.api.tree.ForInLoopTree;


public class Main{
    

    
    public static void main(String[] args) {
        int mat[][] = { { 0, 2, 3}, 
                        { 2, 0, 1}, 
                        { 3, 1, 0}};
        creategraph(mat);    
    }
    private static void creategraph(int[][] mat){
    
        List<Node> Nodes=new ArrayList<Node>();
        for (int i=0; i<mat.length;i++){
            Node node = new Node(i);
            Nodes.add(node);
        }
        for (int i=0; i<mat.length;i++){
            for(int j=i+1;j<mat.length;j++){
                // System.out.println(mat[i][j]);
                Link link= new Link(mat[i][j],Nodes.get(i),Nodes.get(j));
            }
        }
    }



}