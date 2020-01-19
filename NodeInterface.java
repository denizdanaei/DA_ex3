import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeInterface extends Remote {
    
    public void sendMessage(Link link, Message message) throws RemoteException;
    public void onRecieve(int rxLinkWeight, Message message) throws RemoteException;

    public int getID() throws RemoteException;

    // Simulator stuff
    public void wakeup()                          throws RemoteException;
    public void addLink(Link link)                throws RemoteException;
    // TEMPORARY
    public Link getLink(int index) throws RemoteException;
}
