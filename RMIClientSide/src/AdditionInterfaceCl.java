import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Laurens on 11/17/2017.
 */
public interface AdditionInterfaceCl extends Remote {
    public int add(int a, int b) throws RemoteException;
}
