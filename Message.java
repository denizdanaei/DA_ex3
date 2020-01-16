import java.io.Serializable;

enum Type {
    TEST, INITIATE, ACCEPT, REJECT, REPORT, CONNECT, ROOT_CHANGE
}

public class Message implements Serializable {

    Type type;
    int fragmentLevel;
    int fragmentID;
    int weight;
    NodeState state;
    
    public Message(Type type, int fragmentLevel, int fragmentID, NodeState state, int weight) {
        this.type = type;
        this.fragmentLevel = fragmentLevel;
        this.fragmentID = fragmentID;
        this.state = state;
        this.weight = weight;
    }
    
    public String toString() {
        String string = type + " fragmentID=" + fragmentID + " fragmentLevel=" + fragmentLevel + " " + state + " weight" + weight;
        return string;
    }
}