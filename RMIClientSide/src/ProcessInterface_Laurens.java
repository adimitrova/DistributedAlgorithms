import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

/**
 * Created by Laurens on 11/17/2017.
 */
public interface ProcessInterface_Laurens extends Remote {
    public void broadcast(message m) throws RemoteException;

    public void receive(message m) throws RemoteException;

    public void deliver(message m) throws RemoteException;
}
