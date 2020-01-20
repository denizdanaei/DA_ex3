import java.io.Serializable;

 enum LinkState {
    UNKOWN, IN_MST, NOT_IN_MST
}

public class Link implements Serializable {

    int weight;
    NodeInterface node1, node2;
    boolean core;
    LinkState state;
    public Link(int weigth, NodeInterface node1, NodeInterface node2) {
        this.weight = weigth;
        this.node1 = node1;
        this.node2 = node2;
        this.core = false;
        this.state = LinkState.UNKOWN;
    }

    public int getWeight() {
        return weight;
    }

    public void setState(LinkState linkState) {
        this.state = linkState;
    }

    public NodeInterface dst(int id){
        int localID = -1;
        try {
            localID = node1.getID();
        }catch (Exception e) {
            System.out.println("@dst");
            System.exit(1);
        }
        if ( localID == id) {
            return node2;
        } else {
            return node1;
        }
    }

    public String toString() {
        String string = "null";
        try {
            string = "link " + node1.getID() + " to " + node2.getID() + " " + state + " weight=" + weight; //+ node1.getID() + " to " + node2.getID() 
        } catch (Exception e) {
            System.out.println("@link");
            System.exit(1);
        }
        return string;
    }

}
