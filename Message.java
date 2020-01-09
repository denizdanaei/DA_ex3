import java.io.Serializable;

public class Message implements Serializable {

    String string;
    int fragmentLevel;
    int fragmentName;
    public Message(String string, int fragmentLevel, int fragmentName) {
        this.string = string;
        this.fragmentLevel = fragmentLevel;
        this.fragmentName = fragmentName;
    }
    public String toString() {
        return string + " fragmentName=" + fragmentName + " fragmentLevel=" + fragmentLevel;
    }
//     public void initiate();
//     public void test();
//     public void accept();
//     public void reject();
//     public void report();
//     public void change_root();
//     public void connect();
}