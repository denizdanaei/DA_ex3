import java.util.ArrayList;
import java.util.List;

public class Node implements NodeInterface, Runnable {
    private enum NodeState {
        FOUND, FIND, SLEEPING
    }

    public int id;
    public NodeState nodeState; 
    public int fragmentLevel; 
    public int fragmentName;

    public boolean test_edge; // edge checked whether other end in same fragment
    public int find_count; // number of report messages expected

    public List<Link> links = new ArrayList<Link>();
    public Link in_branch; // edge towards core (sense of direction)

    public Link best_link; // local direction of candidate MOE
    public int best_weight = Integer.MAX_VALUE; // weight of current candidate MOE

    public Node(int id) {
        this.id = id;
        this.nodeState = NodeState.SLEEPING;
        this.fragmentLevel = 0;
        this.fragmentName = 0;
        this.test_edge=false;
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

    public int getID() {
        return id;
    }

    // threads stuff
    public void run() {
        // System.out.println("Node " + id + " running");
        if (nodeState == NodeState.SLEEPING){
            wakeup();
            sendMessage(best_link, new Message("test_edge", fragmentLevel, fragmentName));
        }
    }

    public void wakeup() {
        nodeState = NodeState.FIND;
        
        for (Link link : links) {
            if(link.linkState == LinkState.UNKOWN){     
                if (link.getWeight() < best_weight) {
                    best_link = link;
                    best_weight = link.getWeight();
                }
            } 
        }
        System.out.println("node " + id + " best link " + best_weight);                 
    }

    public void sendMessage(Link link,  Message message) {
        try {
            if((link.dst(id)).onRecieve(link, message)){
                nodeState = NodeState.FOUND;
                fragmentName=link.getWeight();
                // System.out.println(message);                    
                // System.out.println(this);    
            }
        } catch (Exception e) {
            System.out.println("@onSend");
        }
    }

    public boolean onRecieve(Link link, Message message) {
        try {
            if (nodeState == NodeState.SLEEPING){
                wakeup();
            }
            if (link.getWeight() == best_weight && nodeState == NodeState.FIND) {    
                nodeState = NodeState.FOUND;
                link.setLinkState(LinkState.IN_MST);
                
                if(message.fragmentLevel>fragmentLevel)
                    fragmentLevel = message.fragmentLevel; //absorb
                else
                    fragmentLevel++;           // merge
                fragmentName=link.getWeight();                    
                // System.out.println(this); 
                return true;
                }
            else{
                return false;
                }
        } catch (Exception e) {
                System.out.println("@exceptiongetID");
                return false;
            }
    }
    public String toString() {
        String string =  id + " " + nodeState + " fragmentLevel=" + fragmentLevel + " fragmentName=" + fragmentName;
        return string;
    }
}