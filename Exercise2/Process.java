import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.TimeUnit;

/** 
 * The current process operates according to Peterson's election algorithm
 * in a unidirectional ring. 
 * Every process receives its downstream neighbour's id and compares that
 * with its own id. It sends the higher of the two values to its downstream neighbour
 * Condition for staying active: 
 * nid >= id && nid >= nnid
 * If the above are not met, the current process is set to passive
 * If the condition for staying active is true, the current process takes the highest
 * value of the ones he has, e.g. if id = 5, nid = 6 and nnid = 6, process stays 
 * active with value 6 (i.e. the value of nid ?? i think)
 * If condition is false, the cur process gets killed for now until its elected at some point. 
 * 
 * @author Anelia Dimitrova (4501667) & Laurens Weijs (4503813)
 * @version	27.11.17
 */

public class Process extends UnicastRemoteObject implements ProcessInterface {
	private List<String> ipPortList = new ArrayList<String>(); // List of all the ip addresses of the other processes.
	int ID;		// current process id: HOW DO WE DECIDE ON THIS ID's value? Random number? Consecutive number?
	int NID;	// the upstream (previous) neighbour's value of the current process
	int receivedNNID;	/** this value is received from the upstream (left/previous) neighbour of the curr process 
				* and is the MAX of 	receivedNNID = MAX(LeftNeighboursID,LeftNeighboursNID)
				* The current process will store this nnid and compute 
				* its own NNID from its upstream neighbour's NID and its own ID
				* and that will be the NNID it will send to the next process (curProcNNID)
				*/
	int curProcNNID;	// this process' NNID from comparing max(ID,NID)
	boolean status;		// indicates whether the current process is active or passive (relay)
	boolean elected;	// if the curr process received its own id, it's elected
	
	/**
	 * Constructor to setup the Process initially
	 * @param ipPortList
	 * @param currentIpPort
	 * @throws RemoteException
	 */
	public Process(List<String> ipPortList, String currentIpPort, int currentProcID) throws RemoteException{
		this.ipPortList = ipPortList; // The list of IPs and port combinations where the other processes are located.
		this.ID = currentProcID;
	}
	
	/**
	 * GETTER: for the current process' ID
	 * @return ID
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * GETTER: for the current process' left neighbour's ID
	 * @return NID
	 */
	public int getNID() {
		return NID;
	}
	
	/**
	 * GETTER: for the current process' left neighbour's NID, which is NNID for the curr process
	 * @return NNID
	 */
	public int getReceivedNNID() {
		return receivedNNID;
	}
	
	/**
	 * GETTER: for the current process' NNID, i.e. max(ID,NID)
	 * @return NNID
	 */
	public int getCurProcNNID() {
		return curProcNNID;
	}
	
	/**
	 * SETTER: for the current process' NNID, i.e. max(ID,NID)
	 */
	public void setCurProcNNID(int maxValue) {
		curProcNNID = maxValue;
	}
	
	// --------------- now the real methods ---------------- 
	@Override
	public void receive(int leftNeighbID, int leftNeighbNNID) throws RemoteException{
		receivedNNID = leftNeighbNNID;
		NID = leftNeighbID;
	}
	
	@Override
	public void send(int ownID, int ownNNID) throws RemoteException{
		// TODO not sure what to add here exactly (actually how)
		// somehow should send to the next element from the ring list
	}
	
	/**
	 * Check if current process is active or passive (relay)
	 * @return true for ACTIVE || false for INACTIVE
	 * also updates the status
	 */
	public boolean checkActiveOrRelay() {
		if(this.getNID() >= this.getID() && this.getNID() >= this.getReceivedNNID()) {
			this.status = true;		// set status as active
		} else {
			this.status = false;
		}
		return status;
	}
	
	// if the current process receives its own ID under the ReceivedNNID, then it's elected
	public boolean isElected(Process procIn) {
		if(this.getID() == this.getReceivedNNID()) {
			elected = true;
		} else {
			elected = false;
		}
		return elected;
	}	
}

