import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeInterface extends Remote {
    
    public void sendMessage(Link link, Message message) throws RemoteException;
    public boolean onRecieve(Link link, Message message) throws RemoteException;
    // public void connect(int L) throws RemoteException;

    public void addLink(Link link) throws RemoteException;
    public int getID() throws RemoteException;
}
