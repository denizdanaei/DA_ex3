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
    
    private int id;
    private NodeState state;
     
    private int fragmentLevel; 
    private int fragmentID;
    
    private int best_weight; // weight of current candidate MOE
    private int find_count;                  // number of report messages expected

    private List<Link> links;
    private Link best_link;  // local direction of candidate MOE
    private Link in_branch; // edge towards core (sense of direction)
    private Link test_edge; // edge checked whether other end in same fragment
    
    private BlockingQueue<QueueItem> rxQueue;
    private Queue<QueueItem> queue;

    public Node(int id) {
        this.id = id;
        this.state = NodeState.SLEEPING;
        this.fragmentLevel = 0;
        this.fragmentID = -1;
        this.best_weight =  Integer.MAX_VALUE;
        this.find_count = 0; 
        this.links = new ArrayList<Link>();
        this.best_link = null;
        this.in_branch = null;
        this.test_edge = null;
        this.rxQueue = new LinkedBlockingQueue<QueueItem>();  // MESSAGE RECEIVING FIFO
        this.queue = new LinkedList<QueueItem>();    
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

    private synchronized void check_queue(){
        int size = queue.size();
    	if (size != 0){
            //if(id==0) System.out.println("N"+ id + " queue.size()="+ queue.size());
            for (int i = 0; i < size; i++) {
                QueueItem obj = queue.remove();
                //if(id==0) System.out.println("N"+id+" dequeuing " + obj.message.type + " from " + obj.linkWeight);
                execute(weightToLink(obj.linkWeight), obj.message);
            }
        }
    }

    private Link weightToLink(int weight) {
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
                    // System.out.println(this);
                    System.out.println("N" + id + " find_count=" + find_count);
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

    public synchronized void onRecieve(int rxLinkWeight, Message message) {
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
    
    private void execute(Link link, Message message) {
        switch (message.type) {
            case CONNECT:
                onConnect(link, message);
                break;

            case INITIATE:
                onInitiate(link, message);
                break;

            case TEST:
                onTest(link, message);
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

    private void onConnect(Link link, Message message) {

        // //if(id==0) System.out.println("N" + id  +" recieved CONNECT at link " + link.getWeight());

        // ABSORB
        if (message.fragmentLevel < this.fragmentLevel) {
            //if(id==0) System.out.println("N" + id  +" absorb link "+ link.getWeight());
            link.setState(LinkState.IN_MST);
            sendMessage(link, new Message(Type.INITIATE, fragmentLevel, fragmentID, state, best_weight));        
            if (this.state == NodeState.FIND) find_count++;        
        // ENQUEUE
        } else if (link.state == LinkState.UNKOWN) {
            queue.add(new QueueItem(link.getWeight(), message));
            //if(id==0) System.out.println("N" + id  +" enqueue CONNECT from " + link.getWeight());

        // MERGE
        } else {
            //if(id==0)  System.out.println("N" + id  +" merge w/ link "+ link.getWeight());
            
            sendMessage(link, new Message( Type.INITIATE, fragmentLevel+1, link.getWeight(), NodeState.FIND, best_weight));
            
        }
    }
    
    private void onInitiate(Link link, Message message){
    
        //if(id==0) System.out.println("Node " + id + " recieved INITIATE");

        this.fragmentID = message.fragmentID;
        this.fragmentLevel = message.fragmentLevel;
        this.state = message.state;
        this.in_branch = link;
        this.best_link = null;
        this.best_weight = Integer.MAX_VALUE;
        
        // Propagate INITIATE message (inside your fragment)
        for (Link l : links) {
            if (l.state == LinkState.IN_MST && l.weight != in_branch.weight) {
                sendMessage(l, new Message( Type.INITIATE, this.fragmentLevel, this.fragmentID, this.state, best_weight));                
                if (this.state == NodeState.FIND) this.find_count++;

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
        // best_weight = Integer.MAX_VALUE;
        //if(id==0) System.out.println("Node " + id + " findMOE");

        this.test_edge = findMoeCandidate();
        if (this.test_edge == null) {
            sendReport();
        } else {
            // this.best_weight = this.test_edge.getWeight();
            //if(id==0) System.out.println("Node " + id + " send TEST l/w=" + test_edge.getWeight());
            sendMessage(this.test_edge, new Message(Type.TEST, fragmentLevel, fragmentID, state, this.best_weight));
        }
    }
    
    private void onTest(Link link, Message message) {

        // System.out.println("N" + id + " recieves TEST");
        
        // ENQUEUE
        if (this.fragmentLevel < message.fragmentLevel) {
            //if(id==0) System.out.println("N" + id + " enqueue TEST from " + link.getWeight());
            queue.add(new QueueItem(link.getWeight(), message));

        // ACCEPT
        } else if (message.fragmentID != this.fragmentID) {
            //if(id==0) System.out.println("N" + id + " send ACCEPT to " + link.getWeight());
            sendMessage(link, new Message(Type.ACCEPT, this.fragmentLevel, this.fragmentID, this.state, best_weight));

        } else {
            // REJECT

            if (link.state == LinkState.UNKOWN) {
                link.setState(LinkState.NOT_IN_MST);
                // sendMessage(link, new Message(Type.REJECT, this.fragmentLevel, this.fragmentID, this.state, best_weight));
            }
            if (test_edge.getWeight() != link.getWeight()) {
                //if(id==0) System.out.println("N" + id + " send REJECT to " + link.getWeight());
                sendMessage(link, new Message(Type.REJECT, this.fragmentLevel, this.fragmentID, this.state, best_weight));
            
            }else{
                // findMoe();
            }
        }
    }    
    
    private void onAccept(Link link, Message message) {
        //if(id==0) System.out.println("N" + id + " on ACCEPT from " + link.getWeight());
        this.test_edge = null;
        if (link.weight < best_weight) {
            best_link = link;
            best_weight = link.getWeight();
        }
        sendReport();
    }  
    
    private void onReject(Link link, Message message) {
        //if(id==0) System.out.println("N" + id + " on REJECT from " + link.getWeight());        
        if (link.state == LinkState.UNKOWN) {
            link.setState(LinkState.NOT_IN_MST);   
        }
        findMoe();
    }
   
    private void sendReport() {
        if (find_count == 0 && test_edge == null) {
            //if(id==0) System.out.println("N"+id+" reporting to l/w" + in_branch.getWeight());
            this.state = NodeState.FOUND;
            sendMessage(in_branch, new Message(Type.REPORT, fragmentLevel, fragmentID, state, best_weight));
        } else {
            // System.out.println("N"+id+" cannot report, find count= " + find_count);
        }
    }

    private void onReport(Link link, Message message) {
        
        //if(id==0) System.out.println("N"+id+" on report from "+link.getWeight());
        //if(id==0) System.out.println(message);
        
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
        }
    }
    
    public String toString() {
        String string =  id + " " + state + " fragmentID=" + fragmentID + " fragmentLevel=" + fragmentLevel + " best_weight=" + best_weight;
        return string;
    }
}