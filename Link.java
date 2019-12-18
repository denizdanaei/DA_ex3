import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Link {

    int weight;
    Node node1,node2;
    public Link(int weigth, Node node1, Node node2) {
        this.weight = weigth;
        this.node1 = node1;
        this.node2 = node2;
        node1.links.add(this);
        node2.links.add(this);
        System.out.println("Link between node"+node1.id+" and node"+node2.id+" with weight="+weigth);
    }

    public List getNodes() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(node1);
        nodes.add(node2);
        System.out.println(nodes);
        return nodes;
    }

    public int whoisNeighbour(Node node) {
        if(node.id==node1.id) return node2.id;
        else return node1.id;
    }



}
