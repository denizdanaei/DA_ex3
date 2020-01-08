import java.io.Serializable;

public class Message implements Serializable {

    String string;
    int FN,LN;
    public Message(String string, int LN, int FN) {
        this.string = string;
        this.LN = LN;
        this.FN = FN;
    }
    public String toString() {
        return string + " FN=" + FN + " LN="+LN;
    }
//     public void initiate();
//     public void test();
//     public void accept();
//     public void reject();
//     public void report();
//     public void change_root();
//     public void connect();
}