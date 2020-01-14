import java.util.ArrayList;
import java.util.List;


public class Node implements NodeInterface, Runnable {

    private enum NodeState {
        FOUND, FIND, SLEEPING
    }
    public int id;
    public NodeState state;
     
    public int fragmentLevel; 
    public int fragmentID;

    public List<Link> links = new ArrayList<Link>();
    public Link best_link; // local direction of candidate MOE
    public int best_weight = Integer.MAX_VALUE; // weight of current candidate MOE

    public boolean core_Node;
    // public boolean test_edge; // edge checked whether other end in same fragment
    // public int find_count; // number of report messages expected

    // public Link in_branch; // edge towards core (sense of direction)

    public Node(int id) {
        this.id = id;
        this.state = NodeState.SLEEPING;
        this.fragmentLevel = 0;
        this.fragmentID = id;
        this.core_Node = false;
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

    public int getID() {
        return id;
    }
    public void set_fragmentID(int newFragmentID) {
        this.fragmentID = newFragmentID;
    }
    public void set_fragmentLevel(int newLevel ){
        this.fragmentLevel = newLevel;
    }

    // threads stuff
    public void run() {
        System.out.println("Node " + id + " ready");
    }

    public void wakeup() {

        if (state != NodeState.SLEEPING) return;
        System.out.println("Node "+id+" awake");

        for (Link link : links) { 
            if (link.getWeight() < best_weight) {
                best_link = link;
                best_weight = link.getWeight();
            }
        }
        best_link.state = LinkState.IN_MST;
        state = NodeState.FOUND;
        sendMessage(best_link, new Message(Type.CONNECT, fragmentLevel, fragmentID));
    }

    public void sendMessage(Link link, Message message) {
        try {
            // System.out.println(this.id + " sends ");
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
                connect(link, message);
                break;
            case ROOT_CHANGE:

                break;
            default:
                break;
        }


    }

    public void test(Link link, Message message){

        if(message.fragmentID == this.fragmentID){
            link.state = LinkState.NOT_IN_MST;
            // System.out.println("from node " + id + " Rejecte msg sent");
            sendMessage(link ,new Message(Type.REJECT, fragmentLevel, fragmentID));            
        }else{
            if (link.weight == best_weight && state == NodeState.FIND) {  
                // System.out.println("from node " + id + " Accept msg sent");
                sendMessage(link, new Message(Type.ACCEPT, fragmentLevel, fragmentID));
            }else{
                // System.out.println("from node " + id + " Rejecte msg sent");
                sendMessage(link ,new Message(Type.REJECT, fragmentLevel, fragmentID));
            }
        }
    }
    public void accept(Link link, Message message){
        
        //report or connect?
    }  
    
    public void reject(Link link, Message message){

        //report or connect?
    }

    public void connect(Link link, Message message){

        // System.out.println("connect");
        if(this.state == NodeState.FOUND){
            if(this.fragmentLevel == message.fragmentLevel){
                this.core_Node = true;
                link.core = true;
                //direction to core?
                this.fragmentLevel++;
                this.fragmentID = link.getWeight();
                this.state = NodeState.FIND;

            }
            // System.out.println(link);                   
            // System.out.println(this); 
        }
    }
    public String toString() {
        String string =  id + " " + state + " fragmentLevel=" + fragmentLevel + " fragmentID=" + fragmentID;
        return string;
    }
}