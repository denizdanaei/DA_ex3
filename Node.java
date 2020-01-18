import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


enum NodeState {
    FOUND, FIND, SLEEPING
}

public class Node implements NodeInterface, Runnable {

    private class QueueItem {
        int linkWeight;
        Message message;
        QueueItem(int linkWeight, Message message) {
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
    public int find_count = 0;                  // number of report messages expected

    public List<Link> links;
    public Link best_link;  // local direction of candidate MOE
    private Link in_branch; // edge towards core (sense of direction)
    private Link test_edge; // edge checked whether other end in same fragment
    
    private BlockingQueue<QueueItem> rxQueue;

    public Node(int id) {
        this.id = id;
        this.state = NodeState.SLEEPING;
        this.fragmentLevel = 0;
        this.fragmentID = -1;
        this.links = new ArrayList<Link>();
        this.queue = new LinkedList<QueueItem>();
        
        this.rxQueue = new LinkedBlockingQueue<QueueItem>();  // MESSAGE RECEIVING FIFO
    }
    
    public int getID() {
        return id;
    }
    
    public void addLink(Link link) {
        this.links.add(link);
    }

    public Link getLink(int index) {
        return this.links.get(index);
    }

    private void check_queue(){
    	if (queue.size() != 0){
    	    for (int i = 0; i < queue.size(); i++){
                QueueItem obj = queue.remove();
                // if (id==1) System.out.println("N"+id+" dequeuing " + obj.message.type + " from " + obj.linkWeight);
                execute(weightToLink(obj.linkWeight), obj.message);
            }
        }
    }

    public Link weightToLink(int weight) {
        Link link = null;
        for (Link l : this.links) {
            if (l.getWeight() == weight) link=l;
        }
        assert(link != null);
        return link;
    }

    public void run() {
        // System.out.println("Node " + id + " ready");

        while(true) {
            try {
                QueueItem rx = rxQueue.poll(3, TimeUnit.SECONDS);
                
                if (rx==null) {
                    System.out.println(this);
                    continue;
                }
                onMessage(weightToLink(rx.linkWeight), rx.message);
                
            } catch (Exception e) {
                System.out.println("N"+id+" exception at rxQueue.take()\n"+e);
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void sendMessage(Link link, Message message) {
        try {
            link.dst(id).onRecieve(link.getWeight(), message);
        } catch (Exception e) {
            System.out.println("@onSend from N" + id + " l/w" + link.weight + " " + message.type );
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void onRecieve(int rxLinkWeight, Message message) {
        try {
            this.rxQueue.put(new QueueItem(rxLinkWeight, message));
        } catch (Exception e) {
            System.out.println("N"+id+" exception at onReceive");
            System.out.println(e);
            System.exit(1);
        }
    }

    private void onMessage(Link link, Message message) {
        if (state == NodeState.SLEEPING) wakeup();
        execute(link, message);
        check_queue();
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
    
    public void execute(Link link, Message message) {
        switch (message.type) {
            case CONNECT:
                connect(link, message);
                break;

            case INITIATE:
                initiate(link, message);
                break;

            case TEST:
                test(link, message);
                break;

            case ACCEPT:
                onAccept(link, message);
                break;
        
            case REJECT:
                onReject(link, message);
                break;

            case REPORT:
                onReport(link, message);
                break;

            case ROOT_CHANGE:
                change_root();
                break;

            default:
                break;
        }
    }

    public void connect(Link link, Message message) {

        // System.out.println("N" + id  +" recieved CONNECT at link " + link.getWeight());

        // ABSORB
        if (message.fragmentLevel < this.fragmentLevel) {
            // System.out.println("N" + id  +" absorb link "+ link.getWeight());
            link.setState(LinkState.IN_MST); 
            if (this.state == NodeState.FIND) find_count++;
            sendMessage(link, new Message(Type.INITIATE, fragmentLevel, fragmentID, state, best_weight));        
        
        // ENQUEUE
        } else if (link.state == LinkState.UNKOWN) {
            queue.add(new QueueItem(link.getWeight(), message));
            // System.out.println("N" + id  +" enqueue CONNECT from " + link.getWeight());

        // MERGE
        } else {
            // System.out.println("N" + id  +" merge w/ link "+ link.getWeight());
            this.fragmentLevel++;
            this.fragmentID = link.getWeight();
            this.state = NodeState.FIND;
            this.in_branch = link;
            sendMessage(link, new Message( Type.INITIATE, fragmentLevel, fragmentID, NodeState.FIND, best_weight));
            // check_queue();   // shady one
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
        
        // Propagate INITIATE message (inside your fragment)
        for (Link l : links) {
            if (l.state == LinkState.IN_MST && l.weight != in_branch.weight) {
                if (this.state == NodeState.FIND) this.find_count++;
                sendMessage(l, new Message( Type.INITIATE, this.fragmentLevel, this.fragmentID, this.state, best_weight));                
            }
        }

        if (this.state == NodeState.FIND) {
            findMoe();
        }
    }
    
    // Return smallest unknown link
    private Link findMoeCandidate() {
        
        int minWeight = Integer.MAX_VALUE;
        Link candidate = null;
        
        for (Link link : links) {
            if (link.state == LinkState.UNKOWN && link.getWeight() < minWeight) {
                candidate = link;
                minWeight = link.getWeight();
            }
        }

        return candidate;
    }


    private void findMoe() {

        this.test_edge = findMoeCandidate();
        if (this.test_edge == null) {
            sendReport();
        } else {
            this.best_weight = this.test_edge.getWeight();
            sendMessage(this.test_edge, new Message(Type.TEST, fragmentLevel, fragmentID, state, this.best_weight));
        }
    }
    
    public void test(Link link, Message message) {

        // System.out.println("N" + id + " recieves TEST");
        
        // ENQUEUE
        if (this.fragmentLevel < message.fragmentLevel) {
            // System.out.println("N" + id + " enqueue TEST from " + link.getWeight());
            queue.add(new QueueItem(link.getWeight(), message));

        // ACCEPT
        } else if (message.fragmentID != this.fragmentID) {
            sendMessage(link, new Message(Type.ACCEPT, this.fragmentLevel, this.fragmentID, this.state, best_weight));

        } else {
            // REJECT
            if (link.state == LinkState.UNKOWN) {
                link.setState(LinkState.NOT_IN_MST);
            }
            sendMessage(link, new Message(Type.REJECT, this.fragmentLevel, this.fragmentID, this.state, best_weight));
            // check_queue();
        }
    }    
    
    public void onAccept(Link link, Message message) {
        System.out.println("N"+id+" received ACCEPT from " + link.getWeight());
        this.test_edge = null;
        if (link.weight <= best_weight) {
            best_link = link;
            best_weight = link.getWeight();
        }
        sendReport();
    }  
    
    public void onReject(Link link, Message message) {
        System.out.println("N"+id+" received REJECT from " + link.getWeight());
        
        if (link.state == LinkState.UNKOWN) {
            link.setState(LinkState.NOT_IN_MST);   
        }
        findMoe();
    }
   
    public void sendReport() {
        if (find_count == 0 && test_edge == null) {
            System.out.println("N"+id+" reporting to " + in_branch.getWeight());
            this.state = NodeState.FOUND;
            sendMessage(in_branch, new Message(Type.REPORT, fragmentLevel, fragmentID, state, best_weight));
            // check_queue();
        } else {
            System.out.println("N"+id+" cannot report, find count= " + find_count);
        }
    }

    public void onReport(Link link, Message message) {
        
        System.out.println("N"+id+" on report from "+link.getWeight());
        
        // From your own subtree
        if (link.weight != in_branch.weight) {
            assert(find_count > 0);
            find_count--;   
            if (message.weight < best_weight) {
                this.best_weight = message.weight;
                this.best_link = link;
            }
            sendReport();

        // Other subtree -> still finding MOE
        } else if (this.state == NodeState.FIND){
            queue.add(new QueueItem(link.getWeight(), message)); 
        
        // Other subtree -> found MOE
        } else {
    
            // Your subtree has the MOE
            if (message.weight > best_weight) {
                change_root();
    
            // Other subtree has the MOE
            } else if (message.weight == Integer.MAX_VALUE && best_weight == Integer.MAX_VALUE ) {
                System.out.println("N" + id + " halt");                        
            }
        }
    }

    private void change_root(){
        if(best_link.state == LinkState.IN_MST) sendMessage(best_link, new Message(Type.ROOT_CHANGE, fragmentLevel, fragmentID, state, best_weight));
        else{
            best_link.setState(LinkState.IN_MST);
            sendMessage(best_link, new Message(Type.CONNECT, fragmentLevel, fragmentID, state, best_weight));    
            // check_queue();
        }
    }
    
    public String toString() {
        String string =  id + " " + state + " fragmentID=" + fragmentID + " fragmentLevel=" + fragmentLevel + " best_weight=" + best_weight;
        return string;
    }
}