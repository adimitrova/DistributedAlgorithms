import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** 
 * The actual implementation of process in a distributed algorithm.
 * 
 * @author Anelia Dimitrova & Laurens Weijs
 * @version 16.11.2017
 */
public class Process extends UnicastRemoteObject implements ProcessInterface {
	// vectorClock stores the values of the process local clocks 
	// and we get it by getting the index that corresponds to the appropriate process
	// NOTE: the local vector clocks are all initialized with 0 when the system starts

	private int[] vectorClock; // Vector clock to represent the state of the whole system by this process.
	private List<String> ipPortList = new ArrayList<String>(); // List of all the ip addresses of the other processes.
    Set<Message> buffer = new HashSet<Message>(); // Buffer to store not yet delivered messages
    int nonce = 1;	// value by which we increment the local clock 
	int indexLocalClock;

	/**
	 * Constructor for a single process in a distributed algorithm
	 * @param ipPortList
	 * @throws RemoteException
	 */
	public Process(List<String> ipPortList, String currentIpPort) throws RemoteException{
		// add that to be the Clock value of this new process on the index that is the id of the process
		this.ipPortList = ipPortList; // The list of IPs and port combinations the other processes are located.
		int amountProcesses = ipPortList.size();
		vectorClock = new int[amountProcesses]; // Create an vector clock with zeros corresponding to the logical clock
													// of the whole system.
//		String ip = null;
//		try {
//			ip = Inet4Address.getLocalHost().getHostAddress();
//		} catch(UnknownHostException e){
//			System.out.println("Process Exception: " + e);
//		}

		for(int i = 0; i < amountProcesses; i++){
		    if(ipPortList.get(i).equals(currentIpPort)){
		        indexLocalClock = i;
            }
//			int len = ipPortList.get(i).length();
//			String otherIp = ipPortList.get(i).substring(6,len-5);
//
//			if(otherIp.equals(ip)){
//				indexLocalClock = i;
//			}
		}


	}

	/**
	 * GETTER: for local clock value of the current process.
	 * @return int of the clock value
	 */
	public int getLocalClock() {
		return vectorClock[indexLocalClock];
	}

	/**
	 * Update the local clock value with nonce.
	 */
	public void updLocalClock() {
		vectorClock[indexLocalClock] += nonce;
	}
	
	/**
	 * Receive a message, check the order (i.e. the received message's clock) and if it satisfies the HB order, 
	 * deliver, otherwise put in buffer
	 */
	// if HB not satisfied, put in buffer and then call deliver() once HB satisfied
	@Override
	public void receive(Message msgIn, int processID) throws RemoteException {
		if (checkVectorClocks(msgIn, processID )) {
			deliver(msgIn);
			while (!buffer.isEmpty()) {
				for (Message elem : buffer) {
					if (checkVectorClocks(elem, processID)) {
						deliver(msgIn);
					}
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
		// update its own clock
		updateVectorClock(msgIn);

		// Maybe display the content of the message here.
		System.out.println(msgIn.getMessage());

		// remove from the buffer
		if(buffer.contains(msgIn)) {
			buffer.remove(msgIn);
		}
    }

	/**
	 * Broadcast a message to all other processes currently in the system
	 * @param msgIn the message to send
	 * @throws RemoteException
	 */
	@Override
	public void broadcast(Message msgIn) throws RemoteException {
		updLocalClock();

		ProcessInterface otherProcess;
		for (int i = 0; i< ipPortList.size() ;i++ ){
			if(i != indexLocalClock){
				try{
					System.setSecurityManager(new RMISecurityManager());
					otherProcess = (ProcessInterface) Naming.lookup(ipPortList.get(i) +"/process");
					Message msgOut = new Message(msgIn.getMessage(),msgIn.getVectorClock());
					otherProcess.receive(msgOut, i);
				}catch (Exception e) {
					System.out.println("Broadcast Exception: " + e);
				}
			}
		}

	}

	/**
	 * Checks whether the vector clock received follows the last received message of that process.
	 * @param m the message to compare its vector clock with
	 * @return TRUE, when the message received was indeed like expected
	 */
	private boolean checkVectorClocks(Message m, int index) {
		boolean deliverable = true;
		if (!(vectorClock[index] + nonce > m.getVectorClock()[index])) {
			deliverable = false;
		}
		return deliverable;
	}

	/**
	 * Update the vector clock of the current process, by taking the maximum of the elements of both vector clocks.
	 * @param m
	 */
	private void updateVectorClock(Message m) {
		int length = m.getVectorClock().length;
		for (int i = 0; i < length; i++) {
			if ((vectorClock[i]) < m.getVectorClock()[i]) {
				vectorClock[i] = m.getVectorClock()[i];
			}
		}
	}

	/**
	 * GETTER for the vector clock
	 */
	@Override
	public int[] getVectorClock(){
		return vectorClock;
	}
}

// random note: for the registry each process will have an index that represents it