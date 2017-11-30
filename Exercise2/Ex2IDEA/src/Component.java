import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;

/** 
 * The current component operates according to Peterson's election algorithm
 * in an unidirectional ring.
 * Every component receives its downstream neighbour's ID and compares that
 * with its own ID. It sends the higher of the two values to its upstream neighbour. If an component is supplied
 * by its own ID (ID), the ID of its predecessor (NID) and the maximum of the previous two predecessors (NNID). It will
 * be determined whether the node will be still active in the next round. A few rounds will be executed until an
 * node is elected (if it receives its own ID as input.
 *
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

public class Component extends UnicastRemoteObject implements ComponentInterface {
	private String ipPortUpstreamComp; // IP Port combination of the location of its upstream component.
    boolean elected;	// if the curr process received its own id, it's elected
    boolean status;		// indicates whether the current process is active or passive (relay)
    int ID = 0;		// current process id: HOW DO WE DECIDE ON THIS ID's value? Random number? Consecutive number?
    int TID = 0;
    int NID = 0;	// the upstream (previous) neighbour's value of the current process
    int NNID = 0;	/** this value is received from the upstream (left/previous) neighbour of the curr process
     * and is the MAX of 	receivedNNID = MAX(LeftNeighboursID,LeftNeighboursNID)
     * The current process will store this nnid and compute
     * its own NNID from its upstream neighbour's NID and its own ID
     * and that will be the NNID it will send to the next process (curProcNNID)
     */

	/**
	 * Constructor to setup the Component initially.
	 * @param ipPortString IP Port combination of the next component in line.
	 * @param ID ID of the current component.
	 * @throws RemoteException
	 */
	public Component(String ipPortString, int ID) throws RemoteException{
		this.ipPortUpstreamComp = ipPortString;
		this.ID = ID;
		this.TID = ID;
		this.status = true;
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
		return NNID;
	}

	@Override
	public void receive(int NID, int NNID) throws RemoteException{

        try {
            System.setSecurityManager(new RMISecurityManager());
			ComponentInterface otherComponent = (ComponentInterface) Naming.lookup( ipPortUpstreamComp +"/component");

			// needed for the initialization of the election, at the beginning the nodes do not have enough information.
			if( NID != 0){
				this.NID = NID;
			} else {
				otherComponent.receive(this.TID, 0);
			}

			if (NNID != 0) {
				this.NNID = NNID;
			} else {
				otherComponent.receive(this.TID, max(this.TID,this.NID));
			}

			if(elected){
                TimeUnit.MINUTES.sleep(1);
            }

            // If not one of the first nodes to initialize election then do the real magic
            if (status) {
                int sendTID = TID;
                if (this.NID == ID || this.NNID == ID) elected = true;
                if (this.NID >= TID && this.NID >= this.NNID) {
                    this.TID = this.NID;
                } else {
                    status = false;
                }
                System.out.println("ID: " + ID + " TID: " + TID + " Active: " + status + "Elected: " + elected);
                System.out.println(NID + "..." + NNID);
                otherComponent.receive(sendTID, max(sendTID,this.NID));
            } else {
                if (this.NID == ID) elected = true;
                System.out.println("ID: " + ID + " TID: " + TID + " Active: " + status + " Elected: " + elected);
                System.out.println(NID + "..." + NNID);
                otherComponent.receive(this.NID, this.NNID);
            }
        } catch (Exception e) {
            System.out.println("Receive Exception: " + e);
        }
    }

	/**
	 * Check if current process is active or passive (relay).
	 * @return true for ACTIVE || false for PASSIVE.
	 */
	public boolean checkActive() {
		return status;
	}

    /**
     * Checks whether the current component is elected.
     * @return when is elected, fale if not.
     */
	public boolean isElected() {
		return elected;
	}	
}

