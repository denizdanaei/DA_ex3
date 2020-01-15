import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


enum NodeState {
    FOUND, FIND, SLEEPING
}

public class Node implements NodeInterface, Runnable {

    private class QueueItem{
        Message message;
        int linkWeight;
        QueueItem(int linkWeight, Message message){
            this.linkWeight = linkWeight;
            this.message = message;
        }
    }
    private Queue<QueueItem> queue;

    public int id;
    public NodeState state;
     
    public int fragmentLevel; 
    public int fragmentID;
    
    public int best_weight = Integer.MAX_VALUE; // weight of current candidate MOE
    public int find_count = 0; // number of report messages expected
    public boolean core_Node;

    public List<Link> links = new ArrayList<Link>();
    public Link best_link; // local direction of candidate MOE
    private Link in_branch; // edge towards core (sense of direction)
    private Link test_edge; // edge checked whether other end in same fragment
    
    public Node(int id) {
        this.id = id;
        this.state = NodeState.SLEEPING;
        this.fragmentLevel = 0;
        this.fragmentID = -1;
        this.core_Node = false;
        this.queue = new LinkedList<QueueItem>();
    }

        // threads stuff
    public void run() {
        // System.out.println("Node " + id + " ready");
    }

    public void addLink(Link link) {
        this.links.add(link);
    }

    public int getID() {
        return id;
    }

    private void check_queue(){
    	
    	for (int i = 0; i < queue.size(); i++)
    	{
    		if (queue.size() != 0)
    		{
                // System.out.println("N" + id + " checks queue");
                QueueItem obj = queue.remove();
                execute(weightToLink(obj.linkWeight), obj.message);
    		}
    	}
    }

    public Link weightToLink(int weight) {
        Link link = null;
        for (Link l : this.links) {
            if (l.getWeight() == weight) link=l;
        }
        return link;
    }

    public void wakeup() {
        if (state != NodeState.SLEEPING) return;
        for (Link link : links) { 
            if (link.getWeight() < best_weight) {
                best_link = link;
                best_weight = link.getWeight();
            }
        }

        best_link.setState(LinkState.IN_MST);
        state = NodeState.FOUND;

        System.out.println("Node "+id+" awake");
        sendMessage(best_link, new Message(Type.CONNECT, this.fragmentLevel, this.fragmentID, this.state, best_weight));
    }

    public void sendMessage(Link link, Message message) {
        try {
            link.dst(id).onRecieve(link.getWeight(), message);
        } catch (Exception e) {
            System.out.println("@onSend from " + id);
            System.exit(1);
        }

    }

    public void onRecieve(int rxLinkWeight, Message message) {
        
        if (state == NodeState.SLEEPING) wakeup();
        execute(weightToLink(rxLinkWeight), message);
        check_queue();
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

            case INITIATE:
                initiate(link, message);
                break;

            case REPORT:
                report(link, message);
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

    public void connect(Link link, Message message){

        if (message.fragmentLevel < this.fragmentLevel) {       // ABSORB
            System.out.println("Fragment "+ fragmentID +" absorb fragment " + message.fragmentID);
            // System.out.println("Fragment"+ fragmentID +" absorb w/ link "+ link.getWeight() + " to fragment" + message.fragmentID);
            link.setState(LinkState.IN_MST);
            sendMessage(link, new Message(Type.INITIATE, fragmentLevel, fragmentID, state, best_weight));
            if (this.state == NodeState.FIND) find_count++;
        
        } else {
            if (link.state == LinkState.UNKOWN) {         // ENQUEUE
                System.out.println("APPEND to queue");
                queue.add(new QueueItem(link.getWeight(), message));
            } else {                                      // MERGE
                // System.out.println("Fragment"+ fragmentID +" merge w/ link "+ link.getWeight() + " to fragment" + message.fragmentID);
                this.fragmentLevel++;
                this.fragmentID = link.getWeight();
                // state = NodeState.FIND;
                in_branch = link;
                System.out.println("N" + id + " fragmentLevel= " + fragmentLevel + " fragmentID= " + fragmentID);
        
                sendMessage(link, new Message( Type.INITIATE, fragmentLevel, fragmentID, NodeState.FIND, best_weight));
            }
        }
    }
    
    public void initiate(Link link, Message message){
    
        // System.out.println("Node " + id + " recieved INITIATE");

        this.fragmentID = message.fragmentID;
        this.fragmentLevel = message.fragmentLevel;
        this.state = message.state;
        this.in_branch = link;
        this.best_link = null;
        this.best_weight = Integer.MAX_VALUE;
        System.out.println("N" + id + " fragmentLevel= " + fragmentLevel + " fragmentID= " + fragmentID);
        
        for (Link l : links) {
            if(l.state == LinkState.IN_MST && l.weight != in_branch.weight){
                sendMessage(l, new Message( Type.INITIATE, this.fragmentLevel, this.fragmentID, this.state, best_weight));
                if(this.state == NodeState.FIND){
                    this.find_count++;
                }
            }
        }      
        if(this.state == NodeState.FIND){
            find_MOE();
        }
    }
    
    private void find_MOE(){
        best_weight = Integer.MAX_VALUE;
        boolean localFlag = false;
        for (Link link : links) {
            if(link.state == LinkState.UNKOWN){
                localFlag = true;
                if (link.getWeight() < best_weight) {
                    this.test_edge = link;
                    best_weight = link.getWeight();
                }
            }
        }
        if(localFlag){
            sendMessage(this.test_edge, new Message(Type.TEST, fragmentLevel, fragmentID, state, best_weight));
                    
        }else{
            test_edge = null;
            if(find_count == 0){
                
                this.state = NodeState.FOUND;
                sendMessage(in_branch, new Message(Type.REPORT, fragmentLevel, fragmentID, state, best_weight));
            }
        }             
    }
    
    public void test(Link link, Message message){
        // System.out.println("N" + id + " recieves TEST");
        if(this.fragmentLevel < message.fragmentLevel){
            System.out.println("APPEND to queue");
            queue.add(new QueueItem(link.getWeight(), message));

        }else{
            if(message.fragmentID != this.fragmentID){
                sendMessage(link, new Message(Type.ACCEPT, this.fragmentLevel, this.fragmentID, this.state, best_weight));
            }else{  
                if(link.state == LinkState.UNKOWN){  
                    link.setState(LinkState.NOT_IN_MST);
                    sendMessage(link, new Message(Type.REJECT, this.fragmentLevel, this.fragmentID, this.state, best_weight));
                }    
                if(test_edge.weight !=link.weight){                    
                    sendMessage(link, new Message(Type.REJECT, this.fragmentLevel, this.fragmentID, this.state, best_weight));
                }else
                    find_MOE(); //sure??
            }

        }
    }    
    
    public void accept(Link link, Message message){
        
        this.test_edge = null;
        if(link.weight < this.best_weight){
            best_link = link;
            best_weight = link.getWeight();
        }
        if(find_count == 0){
            this.state = NodeState.FOUND;
            sendMessage(in_branch, new Message(Type.REPORT, fragmentLevel, fragmentID, state, best_weight));
        }
    }  
    
    public void reject(Link link, Message message){
        
        // if(link.state == LinkState.UNKOWN){
        link.setState(LinkState.NOT_IN_MST);    
        // }
        find_MOE();
    }


    public void report(Link link, Message message){
                
        if(link.weight != in_branch.weight){

            if(find_count>0) find_count--;           
            if(message.weight < best_weight){
                this.best_weight = message.weight;
                this.best_link = link;
            }
            if(find_count == 0 ){ // && test_edge == null
                this.state = NodeState.FOUND;
                sendMessage(in_branch, new Message(Type.REPORT, fragmentLevel, fragmentID, state, best_weight));
            }
        }else{  
            if(this.state == NodeState.FIND){
                System.out.println("APPEND to queue");
                queue.add(new QueueItem(link.getWeight(), message));
            }
            else{
                System.out.println("Halt");
                if(message.weight > best_weight)
                    change_root();
                else{
                    if(message.weight == Integer.MAX_VALUE && best_weight == Integer.MAX_VALUE ){
                        
                        System.exit(1);
                    }
                }
            }
        }
    }

    private void change_root(){}
    
    public String toString() {
        String string =  id + " " + state + " fragmentLevel=" + fragmentLevel + " fragmentID=" + fragmentID + " best_weight=" + best_weight;
        return string;
    }
}