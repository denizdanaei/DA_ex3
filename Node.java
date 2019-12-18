import java.util.ArrayList;
import java.util.List;

public class Node implements NodeInterface, Runnable{
    public int id;
    public List<Link> links = new ArrayList<Link>();
    
    public Node (int id) {
        this.id = id;
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

    public synchronized void receiveRequest (){
        System.out.println ("Request Accepted");
    }


    // RMI + threads stuff
    public void run(){        
        System.out.println("Node "+id+" running");
    }

}