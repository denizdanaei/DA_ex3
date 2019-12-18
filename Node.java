import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;

public class Node {
    public int id;
    public List<Link> links=new ArrayList<Link>();
    
    public Node(final int id) {
        this.id = id;
        System.out.println("node " + id);
    }

    // public void addlink(Link link){
    //     links.add(link);
    //     int myneighbour=link.whoisNeighbour(this);
    //     System.out.println("node "+id+"neighbour "+myneighbour);
    // }

    }