import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** 
 * Representing Processes and their local clocks
 * 
 * @author Laurens Weijs & Anelia Dimitrova
 * @version 16.11.2017
 */
public class Process {
	// vectorClock stores the values of the process local clocks 
	// and we get it by getting the index that corresponds to the appropriate process
	private List<Integer> vectorClock; 
	private int msgNr = 0;
    Set<message> buffer;
	
	public Process() {
		
		//TODO: the rest
	}
	
	public int getLocalClock(Process procIn) {
		return 0;
		// TODO implement
	}
	
	// if HB not satisfied, put in buffer and then call deliver() once HB satisfied
	public void receive(message m) {
		
		msgNr++;
		//pseudo code received msg
	}
	
	// deliver the message once the order is confirmed and delivers only the msg content
	public void deliver(){

    }
	
	public int getNrMessagesRec() {
		return 0;
		// TODO implement
	}
	
	// send message from this thread to all the other processes
	public void broadcast(){
        new message("hello",vectorClock);
        // before sending a msg increment by 1 the local clock
    }
}


// random note: for the registry each process will have an index that represents it