import java.io.Serializable;

public class Link implements Serializable {

    int weight;
    NodeInterface node1,node2;

    public Link (int weigth, NodeInterface node1, NodeInterface node2) {
        this.weight = weigth;
        this.node1 = node1;
        this.node2 = node2;
    }

    // JUR: Can't be used when remote
    // public int whoisNeighbour (NodeInterface node) {
    //     if(node.id==node1.id) return node2.id;
    //     else return node1.id;
    // }
}
