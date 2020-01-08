import java.io.Serializable;

enum linkState {
    is_IN_MST, IN_MST, NOT_IN_MST
}

public class Link implements Serializable {

    int weight;
    NodeInterface node1, node2;
    linkState LS;

    public Link(int weigth, NodeInterface node1, NodeInterface node2) {
        this.weight = weigth;
        this.node1 = node1;
        this.node2 = node2;
        this.LS = linkState.is_IN_MST;

    }

    public int getWeight() {
        return weight;
    }

    public void setLS(linkState lState) {
        this.LS = lState;
    }

    public NodeInterface dst(int id) {
        try {
            if (node1.getID() == id) {
                // System.out.println("node" + id + " best neighbour is " + node2.getID());
                return node2;
            } else {
                // System.out.println("node" + id + " best neighbour is " + node1.getID());
                return node1;
            }
        } catch (Exception e) {
            System.out.println("Exception @dst");
            return node1;
        }
    }

    public String toString() {
        String string;
        try {
            string = "link from " + node1.getID() + " to " + node2.getID() + " LS=" + LS + " weight=" + weight;
            return string;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("@link");
        }
        return "null";
    }

}
