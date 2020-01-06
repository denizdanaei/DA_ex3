import java.io.Serializable;
enum linkState {
    is_IN_MST,
    IN_MST,
    NOT_IN_MST
}
public class Link implements Serializable {

    int weight;
    NodeInterface node1,node2;
    linkState LS = linkState.is_IN_MST; 

    public Link (int weigth, NodeInterface node1, NodeInterface node2) {
        this.weight = weigth;
        this.node1 = node1;
        this.node2 = node2;
    }

    public int getWeight(){
        return weight;
    }

    public void dst(NodeInterface nodeInterface){
        // TODO
    }
    // JUR: Can't be used when remote
    // public int whoisNeighbour (NodeInterface node) {
    //     if(node.id==node1.id) return node2.id;
    //     else return node1.id;
    // }
}
