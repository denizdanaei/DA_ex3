import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Node_RMI extends Remote {
    
    public void receiveRequest() throws RemoteException;
    
}
