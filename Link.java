import java.io.Serializable;

enum LinkState {
    UNKOWN, IN_MST, NOT_IN_MST
}

public class Link implements Serializable {

    int weight;
    NodeInterface node1, node2;
    LinkState linkState;

    public Link(int weigth, NodeInterface node1, NodeInterface node2) {
        this.weight = weigth;
        this.node1 = node1;
        this.node2 = node2;
        this.linkState = LinkState.UNKOWN;
    }

    public int getWeight() {
        return weight;
    }

    public void setLinkState(LinkState linkState) {
        this.linkState = linkState;
    }

    public NodeInterface dst(int id) {
        int localID = -1;
        try {
            localID = node1.getID();
        } catch (Exception e) {
            System.out.println("@dst");
            System.exit(1);
        }
        if ( localID == id) {
            // System.out.println("node" + id + " best neighbour is " + node2.getID()); 
            return node2;
            } else {
                // System.out.println("node" + id + " best neighbour is " + node1.getID());
                return node1;
        }
    }

    public String toString() {
        String string;
        try {
            string = "link " + node1.getID() + " to " + node2.getID() + " linkState=" + linkState + " weight=" + weight;
            return string;
        } catch (Exception e) {
            System.out.println("@link");
        }
        return "null";
    }

}
