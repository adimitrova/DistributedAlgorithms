import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the component in order for the Main Class what it can expect from the component.
 * 
 * @author Anelia Dimitrova (4501667) & Laurens Weijs (4503813)
 * @version	27.11.17
 */
public interface ComponentInterface extends Remote {

    /**
     * Receiving the left neighbour's ID and NNID.
     * @param NID (int) the ID of the the predecessor.
     * @param NNID (int) the maximum of the IDs of the predecessor and its predecessor.
     * @throws RemoteException
     */
    public void receive(int NID, int NNID) throws RemoteException;
	
	/**
     * Check if the current component is elected.
     * @return true or false
     * @throws RemoteException
     */
	boolean isElected() throws RemoteException;
}