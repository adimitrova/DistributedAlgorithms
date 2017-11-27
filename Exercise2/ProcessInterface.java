import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the process in order for the client what it can expect from the process.
 * 
 * @author Anelia Dimitrova (4501667) & Laurens Weijs (4503813)
 * @version	27.11.17
 */
public interface ProcessInterface extends Remote {
    /**
     * send this process' id and nnid to the next process
     * @param own id (int)
     * @param own nnid (int)
     * @throws RemoteException
     */
    public void send(int ownID, int ownNNID) throws RemoteException;

    /**
     * Receiving the left neighbour's ID and NNID
     * @param leftNeighbNNID (int)
     * @param leftNeighbID (int)
     * @throws RemoteException
     */
    public void receive(int leftNeighbID, int leftNeighbNNID) throws RemoteException;
	
	/**
     * Check if the current process is elected
     * @param procIn
     * @return true or false
     * @throws RemoteException
     */
	boolean isElected(Process procIn) throws RemoteException;
}