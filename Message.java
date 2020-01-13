import java.io.Serializable;

enum Type {
    TEST, INITIANTE, ACCEPT, REJECT, REPORT, CONNECT, ROOT_CHANGE
}

public class Message implements Serializable {

    Type type;
    int fragmentLevel;
    int fragmentID;
    
    public Message(Type type, int fragmentLevel, int fragmentID) {
        this.type = type;
        this.fragmentLevel = fragmentLevel;
        this.fragmentID = fragmentID;
    }
    
    public String toString() {
        return type + " fragmentID=" + fragmentID + " fragmentLevel=" + fragmentLevel;
    }

/**
public class TEST extends Message implements Serializable{

    public TEST(String string, int fragmentLevel, int fragmentID) {
        super(string, fragmentLevel, fragmentID);
    }
}


public class INITIANTE extends Message implements Serializable{

        public INITIANTE(String string, int fragmentLevel, int fragmentID) {
            super(string, fragmentLevel, fragmentID);
        }
}

public class ACCEPT extends Message implements Serializable{

    public ACCEPT(String string, int fragmentLevel, int fragmentID) {
        super(string, fragmentLevel, fragmentID);
    }
}

public class REJECT extends Message implements Serializable{

    public REJECT(String string, int fragmentLevel, int fragmentID) {
        super(string, fragmentLevel, fragmentID);
    }
}

public class REPORT extends Message implements Serializable{

    public REPORT(String string, int fragmentLevel, int fragmentID) {
        super(string, fragmentLevel, fragmentID);
    }
}

public class CONNECT extends Message implements Serializable{

    public CONNECT(String string, int fragmentLevel, int fragmentID) {
        super(string, fragmentLevel, fragmentID);
    }
}

public class ROOT_CHANGE extends Message implements Serializable{

    public ROOT_CHANGE(String string, int fragmentLevel, int fragmentID) {
        super(string, fragmentLevel, fragmentID);
    }
}
 */
}