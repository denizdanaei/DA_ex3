import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeInterface extends Remote {
    
    // public void connect(NodeInterface nodeInterface) throws RemoteException;
    // Simulation helpers
    public void addLink(Link link) throws RemoteException;
}
