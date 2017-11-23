import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
		vectorClock.add(this.getProcNum(), numOfProcesses);	
	}
	
	/**
	 * GETTER: for process number
	 * @return int of the proc num
	 */
	public int getProcNum(){
		return this.procId;
	}
	
	/**
	 * GETTER: for local clock value of the process
	 * @return int of the clock value
	 */
	public int getLocalClock() {
		return vectorClock.get(this.getProcNum());
	}
	
	/**
	 * GETTER: for message number
	 * @return int of the clock value
	 */
	public int getNrMessagesRec() {
		return msgNr;
	}
	
	/**
	 * update the local clock value with nonce
	 */
	public void updLocalClock() {
		int clock = this.getLocalClock();
		clock += nonce;
	}
	
	/**
	 * Receive a message, check the order (i.e. the received message's clock) and if it satisfies the HB order, 
	 * deliver, otherwise put in buffer
	 */
	// if HB not satisfied, put in buffer and then call deliver() once HB satisfied
	@Override
	public void receive(Message msgIn, int vClockIn) throws RemoteException {
		Iterator bufferIterator = buffer.iterator(); 		// create iterator for buffer values
		Integer clockNonce = this.getLocalClock() + nonce;	// assign the current localClock+nonce value to variable
		if(vClockIn <= clockNonce){ 	// check if Dj(m) is satisfied
			deliver(msgIn);
			while(bufferIterator.hasNext()){			// while buffer still has elements, go over them and compare to the local clockNonce
				if( (Integer)bufferIterator.next() <= clockNonce) {	// if the buffer clock is smaller, deliver the message
					deliver(msgIn);
				}
			}
		} else {
			buffer.add(msgIn);
		}
	}
	
	/**
	 * Once HB order is satisfied, deliver the message content only, no need to keep the clock value too
	 */
	public void deliver(Message msgIn) throws RemoteException {
		msgNr++;
		updLocalClock();
		if(buffer.contains(msgIn)) {	// if the msg was in buffer, only then remove
			buffer.remove(msgIn);
		}
    }
	
	
	// send message from this thread to another
	@Override
	public void broadcast(Message msgIn) throws RemoteException {
        // TODO implement
        // before sending a msg increment by 1 the local clock
    }
}


// random note: for the registry each process will have an index that represents it