import java.util.ArrayList;
import java.util.List;


public class Node implements NodeInterface, Runnable {

    private enum NodeState {
        FOUND, FIND, SLEEPING
    }
    public int id;
    public NodeState state; 
    public int fragmentLevel; 
    public int fragmentName;

    public List<Link> links = new ArrayList<Link>();
    public Link best_link; // local direction of candidate MOE
    public int best_weight = Integer.MAX_VALUE; // weight of current candidate MOE

    // public boolean test_edge; // edge checked whether other end in same fragment
    // public int find_count; // number of report messages expected

    // public Link in_branch; // edge towards core (sense of direction)

    public Node(int id) {
        this.id = id;
        this.state = NodeState.SLEEPING;
        this.fragmentLevel = 0;
        this.fragmentName = 0;
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
        if (state == NodeState.SLEEPING){
            wakeup();
            
        }
    }

    public void wakeup() {
        state = NodeState.FIND;
        
        for (Link link : links) {
            if(link.state == LinkState.UNKOWN){     
                if (link.getWeight() < best_weight) {
                    best_link = link;
                    best_weight = link.getWeight();
                }
            } 
        }
        sendMessage(best_link, new Message(Type.TEST, fragmentLevel, fragmentName));
    }

    public void sendMessage(Link link, Message message) {
        try {
            (link.dst(id)).onRecieve(link, message);
        } catch (Exception e) {
            System.out.println("@onSend");
            System.exit(1);
        }
    }

    public void onRecieve(Link link, Message message) {
            if (state == NodeState.SLEEPING){
                wakeup();
            }
            execute(link, message);
    }
    
    public void execute(Link link, Message message) { 
        switch (message.type) {
            case TEST:
                test(link, message);
                break;
            case ACCEPT:
                accept(link, message);
                break;
        
            case REJECT:
                reject(link, message);
                break;
            case INITIANTE:
                
                break;
            case REPORT:
                
                break;
            case CONNECT:
                
                break;
            case ROOT_CHANGE:

                break;
            default:
                break;
        }


    }

    public void test(Link link, Message message){
        if (link.weight == best_weight && state == NodeState.FIND) {  
            // System.out.println("from node " + id + " Accept msg sent");
            sendMessage(link, new Message(Type.ACCEPT, fragmentLevel, fragmentName));
        }else{
            // System.out.println("from node " + id + " Rejecte msg sent");
            sendMessage(link ,new Message(Type.REJECT, fragmentLevel, fragmentName));
        }
    }
    public void accept(Link link, Message message){
        
        //report or connect?
    }  
    
    public void reject(Link link, Message message){

        //report or connect?
    }

    public void conect(){
                        // state = NodeState.FOUND;
                // local.setLinkState(LinkState.IN_MST);
                
                // if(message.fragmentLevel>  fragmentLevel)
                    // fragmentLevel = message.fragmentLevel; //absorb
                // else
                    // fragmentLevel++;           // merge
                // fragmentName=local.getWeight();                    
                // System.out.println(this); 
    }
    public String toString() {
        String string =  id + " " + state + " fragmentLevel=" + fragmentLevel + " fragmentName=" + fragmentName;
        return string;
    }
}