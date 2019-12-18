import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;

public class Node extends UnicastRemoteObject implements Node_RMI, Runnable{
    private static final long serialVersionUID = 1L;
    public int id;
    public List<Link> links=new ArrayList<Link>();
    
    public Node (int id) throws RemoteException{
        super();
        this.id = id;
        bind();
    }

    private void bind (){
        try{
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(Integer.toString(id), this);
            System.err.println ("Node " + id + " is created");
        }catch (Exception e){
            System.err.println ("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void run(){        
        while (true){
            int wait = (int) (Math.random()*3000);
            try{
                Thread.sleep(wait);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            try{
                this.findMOE();
            }catch (MalformedURLException | RemoteException | NotBoundException e){
                e.printStackTrace();
            }
        }
    }
    public void findMOE() throws MalformedURLException, RemoteException, NotBoundException{
        
        Node_RMI reciever = (Node_RMI) Naming.lookup(Integer.toString(id));
        System.out.println ("Find MOE");
        reciever.receiveRequest();
    }

    public synchronized void receiveRequest (){
        System.out.println ("Request Accepted");
    }
    }