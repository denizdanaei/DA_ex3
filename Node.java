import java.util.ArrayList;
import java.util.List;


enum NodeState {
    FOUND, FIND, SLEEPING
}

public class Node implements NodeInterface, Runnable {
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
        this.fragmentID = id;
        this.core_Node = false;
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
    
    public void set_fragmentID(int newFragmentID) {
        this.fragmentID = newFragmentID;
    }
    
    public void set_fragmentLevel(int newLevel ){
        this.fragmentLevel = newLevel;
    }

    public void wakeup() {
        if (state != NodeState.SLEEPING) return;
        // System.out.println("Node "+id+" awake");

        for (Link link : links) { 
            if (link.getWeight() < best_weight) {
                best_link = link;
                best_weight = link.getWeight();
            }
        }
        best_link.setState(LinkState.IN_MST);
        state = NodeState.FOUND;

        System.out.println("Node "+id+" awake");
        System.out.println(best_link);

        sendMessage(best_link, new Message(Type.CONNECT, this.fragmentLevel, this.fragmentID, this.state, best_weight));
    }

    public void sendMessage(Link link, Message message) {
        try {
            link.dst(id).onRecieve(link, message);
        } catch (Exception e) {
            System.out.println("@onSend from " + id);
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

    public void test(Link link, Message message){
        
        if(this.fragmentLevel < message.fragmentLevel){
            //add to queue
            
            System.out.println("fragmentLevel < message.fragmentLevel");
        }else{
            
            if(message.fragmentID != this.fragmentID){
                            
            System.out.println("accept");
                sendMessage(link, new Message(Type.ACCEPT, this.fragmentLevel, this.fragmentID, this.state, best_weight));
            }else{
                
                if(link.state == LinkState.UNKOWN){  
                    link.setState(LinkState.NOT_IN_MST);
                    // System.out.println("NOT_IN_MST");
                    sendMessage(link, new Message(Type.REJECT, this.fragmentLevel, this.fragmentID, this.state, best_weight));


                }    
                if(test_edge.weight !=link.weight){
                    System.out.println("test_edge.weight !=link.weight");
                    
                    sendMessage(link, new Message(Type.REJECT, this.fragmentLevel, this.fragmentID, this.state, best_weight));
                }else
                    // System.out.println("else find moe again");
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
        
        if(id == 2)    System.out.println(id + " is rejected");
        // System.out.println(link);        

        // if(link.state == LinkState.UNKOWN){
        link.setState(LinkState.NOT_IN_MST);
            System.out.println(link);        
        // }
        find_MOE();
    }

    public void connect(Link link, Message message){

        if(message.fragmentLevel < this.fragmentLevel){
            System.out.println("absorb");
            link.setState(LinkState.IN_MST);
            sendMessage(link, new Message(Type.INITIATE, fragmentLevel, fragmentID, state, best_weight));
            if(this.state == NodeState.FIND)
                find_count++;
        }else{
            if(link.state == LinkState.UNKOWN){  
            //APPEND to queue          
                System.out.println("APPEND to queue");
            }
            else{
                // System.out.println("merge");
                this.fragmentLevel++;
                this.fragmentID = link.getWeight();
                state = NodeState.FIND;

                System.out.println("Node "+ id +" is merging");
                System.out.println(this);
                // System.out.println("send initiate msg");
                sendMessage(link, new Message( Type.INITIATE, fragmentLevel, fragmentID, NodeState.FIND, best_weight));
            }
        } 
            
        
    }
    
    public void initiate(Link _link, Message message){
    
        System.out.println("Node " + id + " recieved initiate");

        this.fragmentID = message.fragmentID;
        this.fragmentLevel = message.fragmentLevel;
        this.state = message.state;
        this.in_branch = _link;
        this.best_link = null;
        this.best_weight = Integer.MAX_VALUE;

        // System.out.println("done changes");
        System.out.println(this);

        for (Link link : links) {
            if(link.state == LinkState.IN_MST && link.weight != in_branch.weight){
                System.out.println("LinkState.IN_MST && link.weight != in_branch.weight");
                sendMessage(link, new Message( Type.INITIATE, this.fragmentLevel, this.fragmentID, this.state, best_weight));
                if(this.state == NodeState.FIND)
                    this.find_count++;
            }
        }      
        if(this.state == NodeState.FIND){
            
        // System.out.println("NodeState.FIND->find_MOE");
            find_MOE();
        }
    }
    
    private void find_MOE(){
        // if(id == 2)    System.out.println("search again");
        boolean localFlag = false;
        for (Link link : links) {
            System.out.println(link);

            localFlag = false;
            if(link.state == LinkState.UNKOWN && link.weight != in_branch.weight){
                
                // if(id == 2) System.out.println(link);
                localFlag = true;
                if (link.getWeight() < best_weight) {
                    this.test_edge = link;
                    best_weight = link.getWeight();

                    // System.out.println("sendTest");
                    sendMessage(this.test_edge, new Message(Type.TEST, fragmentLevel, fragmentID, state, best_weight));
                    
                }
            }
        }
        if(!localFlag){
            // System.out.println("else->test_edge=null");
            test_edge = null;
            if(find_count == 0){
                System.out.println("no other links to test");
                this.state = NodeState.FOUND;
                System.out.println("Sending report from node:");
                System.out.println(this);
                // Message report = ;
                System.out.println(in_branch);

                sendMessage(in_branch, new Message(Type.REPORT, fragmentLevel, fragmentID, state, best_weight));
            }
        }             
    }

    public void report(Link link, Message message){
        System.out.println("in_branch.weight"+ in_branch.weight + " link.weight=" + link.weight);
        // System.out.println(best_weight);
                
        if(link.weight != in_branch.weight){

            if(find_count>0) find_count--;           
            if(message.weight < best_weight){
                // System.out.println("message.weight < best_weight");
                
                this.best_weight = message.weight;
                this.best_link = link;
            }
            System.out.println(test_edge); 
            if(find_count == 0 && test_edge == null){
                
                System.out.println("find_count == 0 && test_edge == null");
                
                this.state = NodeState.FOUND;
                sendMessage(in_branch, new Message(Type.REPORT, fragmentLevel, fragmentID, state, best_weight));
            }
        }else{
            System.out.println("else");
                
            if(this.state == NodeState.FIND)
                System.out.println("NodeState.FIND");
                //put to queue
            else{
                if(message.weight > best_weight)
                    change_root();
                else{
                    if(message.weight == best_weight && best_weight == Integer.MAX_VALUE ){
                        System.out.println("Halt");
                        System.exit(best_weight);
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