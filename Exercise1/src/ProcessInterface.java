import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Laurens on 11/17/2017.
 */
public interface ProcessInterface extends Remote {
    public void broadcast(Message m) throws RemoteException;

    public void receive(Message m) throws RemoteException;

    public void deliver(Message m) throws RemoteException;
}