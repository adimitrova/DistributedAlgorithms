import java.rmi.*;

/**
 * Created by Laurens on 11/17/2017.
 */
public interface AdditionInterface extends Remote {
    public int add(int a, int b) throws RemoteException;
}
