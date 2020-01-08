import java.util.ArrayList;
import java.util.List;

public class Node implements NodeInterface, Runnable {
    private enum nodeState {
        FOUND, FIND, SLEEPING
    }

    public int id;
    public nodeState SN; // state of the node(find/found)
    public int LN; // level of the current fragment it is part of
    public int FN; // name of the current fragment it is part of

    public boolean test_edge; // edge checked whether other end in same fragment
    public int find_count; // number of report messages expected

    public List<Link> links = new ArrayList<Link>();
    public Link in_branch; // edge towards core (sense of direction)

    public Link best_link; // local direction of candidate MOE
    public int best_weight = Integer.MAX_VALUE; // weight of current candidate MOE

    public Node(int id) {
        this.id = id;
        this.SN = nodeState.SLEEPING;
        this.LN = 0;
        this.FN = 0;
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
        if (SN == nodeState.SLEEPING){
            wakeup();
        }
    }

    public void wakeup() {
        SN = nodeState.FIND;
        int linkWeight = 0;
        best_weight = Integer.MAX_VALUE;
        for (Link link : links) {
            if(link.LS == linkState.is_IN_MST){
                linkWeight = link.getWeight();
                if (linkWeight > 0 && linkWeight < best_weight) {
                    best_link = link;
                    best_weight = link.getWeight();
                }
            } 
        }
        System.out.println(best_link);          
        sendMessage(best_link, new Message("test_edge", LN, FN));
    }

    public void sendMessage(Link link,  Message message) {
        try {
            // SN = nodeState.FIND;    
            if((link.dst(id)).onRecieve(link, message)){
                SN = nodeState.FOUND;
                FN=link.getWeight();
                // System.out.println(message);                    
                // System.out.println(this);    
            }
        } catch (Exception e) {
            System.out.println("@onSend");
        }
    }

    public boolean onRecieve(Link link, Message message) {
        try {
            if (link.getWeight() == best_weight && SN == nodeState.FIND) {    
                SN = nodeState.FOUND;
                link.setLS(linkState.IN_MST);
                
                if(message.LN>LN)
                    LN = message.LN; //absorb
                else
                    LN++;           // merge
                FN=link.getWeight();                    
                System.out.println(this); 
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
        String string = "node" + id + " LN=" + LN + " SN=" + SN + " FN=" + FN;
        return string;
    }
}