import java.util.ArrayList;
import java.util.List;

public class Node implements NodeInterface, Runnable{
    private enum nodeState {
        FOUND,
        FIND,
        SLEEPING
    }
    
    public int id;
    public nodeState SN; //state of the node(find/found)
    public int LN; //level of the current fragment it is part of
    public int FN; //name of the current fragment it is part of

    public boolean test_edge; //edge checked whether other end in same fragment
    public int find_count; //number of report messages expected

    public List<Link> links = new ArrayList<Link>();
    public Link in_branch; //edge towards core (sense of direction)
    public Link best_link;  //local direction of candidate MOE
    public int best_weight = Integer.MAX_VALUE; //weight of current candidate MOE

    public Node (int id) {
        this.id = id;
        this.SN = nodeState.SLEEPING;
        this.LN = 0;
        this.FN = id;
    }

    public void addLink(Link link) {
        this.links.add(link);
    }
    // RMI + threads stuff
    public void run(){        
        System.out.println("Node "+id+" running");
        if(SN == nodeState.SLEEPING)
            wakeup();
    }

    public void wakeup(){
        SN=nodeState.FIND;
        initiate();
        // connect(null);
        
    }

    public void initiate(){ 
        int linkWeight=0;
        for(Link link: links){
            linkWeight = link.getWeight();
            if (linkWeight>0 && linkWeight<best_weight){
                best_link = link;
                best_weight = link.getWeight();
            }
        }
        System.out.println("Node "+id+" best link is "+ best_weight);
    }
/*
    public void connect(NodeInterface dst){
        // TODO: lookup destination
        if(same MOE)
            accept();
        else    
            reject();
    }

    public void accept(){
        change states of link and nodes
        FN,SN,LN, ...
        report(accepted)

    }

    public void reject(){
        report(rejected)
    }
    
    public report(){
        report back u aceepted/rejected for later changes in the 1st sender node
    }
*/
}