import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the Byzantine Agreement Algorithm 
 * 
 * @author Anelia Dimitrova (4501667) & Laurens Weijs (4503813)
 * @version	09.12.17
 */
public interface ByzantineInterface extends Remote {	
	public void broadcast(char MsgType, int round, int value) throws RemoteException;
	
	public void decide() throws RemoteException;
}
