import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 
 * Representing Processes and their local clocks
 * 
 * @author Anelia Dimitrova
 * @version 22.11.2017
 */
public class Process extends UnicastRemoteObject implements ProcessInterface {
	// vectorClock stores the values of the process local clocks 
	// and we get it by getting the index that corresponds to the appropriate process
	// NOTE: the local vector clocks are all initialized with 0 when the system starts
	private List<Integer> vectorClock = new ArrayList<Integer>(); 
	public static int numOfProcesses = 0;
	private int procId;
	private int msgNr = 0;
    Set<Message> buffer = new HashSet<Message>();
    int nonce = 1;	// value by which we increment the local clock 
	
    // constructor
	public Process() throws RemoteException{
		numOfProcesses += 1;		// increase the numOfProc
		procId = numOfProcesses;		// add that to also be the id of the curr process
		// add that to be the Clock value of this new process on the index that is the id of the process
		vectorClock.add(this.getProcNum(), numOfProcesses);;	
	}
	
	public int getProcNum(){
		return this.procId;
	}
	
	public int getLocalClock() {
		return vectorClock.get(this.getProcNum());
	}
	
	// if HB not satisfied, put in buffer and then call deliver() once HB satisfied
	public void receive(Message msgIn, int vClockIn) throws RemoteException {
		if(vClockIn <= this.getLocalClock() + nonce){ 	// check if Dj(m) is satisfied
			
		}
		
		//pseudo code received msg
	}
	
	// deliver the message once the order is confirmed and delivers only the msg content
	public void deliver() throws RemoteException {
		msgNr++;
    }
	
	public int getNrMessagesRec() {
		return 0;
		// TODO implement
	}
	
	// send message from this thread to another
	public void broadcast() throws RemoteException {
        new Message("hello",vectorClock);
        // before sending a msg increment by 1 the local clock
    }
}


// random note: for the registry each process will have an index that represents it