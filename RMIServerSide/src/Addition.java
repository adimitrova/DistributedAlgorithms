import java.rmi.*;
import java.rmi.server.*;

/**
 * Created by Laurens on 11/17/2017.
 */
public class Addition extends UnicastRemoteObject implements AdditionInterfaceSrv {

    public Addition() throws RemoteException{

    }

    public int add(int a , int b) throws RemoteException {
        int result = a+b;
        return result;
    }
}
