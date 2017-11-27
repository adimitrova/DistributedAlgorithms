import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent each unidirectional ring
 * @author Laurens & Ani
 * @version	27.11.17
 */
public class UnidirectionalRing {
	int nrOfProcesses;
	Process initialProcess;
	int roundNr;
	List<Process> processesInRing = new ArrayList<Process>();	// to hold all the processes in the list
	List<Process> activeProcInRing = new ArrayList<Process>();	// to hold all the active processes in the list (needed for the rounds)
	List<Process> inactiveProcInRing = new ArrayList<Process>();	// to hold all the inactive processes in the list (needed for the rounds)
	
	public UnidirectionalRing(int nrOfProcessesIn) {
		nrOfProcesses = nrOfProcessesIn;
	}
	
	/**
	 * set a certain process to be first so we know when the round is over (i.e. when the first processed is reached again)
	 * not sure if this is how we define a new round
	 * @param firstProc
	 */
	public void setFirstProcess(Process firstProc) {
		initialProcess = firstProc;
	}
	
	/**
	 * remove a process from the list once it's been elected
	 * @param procIn
	 */
	public void removeElectedProcess(Process procIn) {
		processesInRing.remove(procIn.getID());
	}
	
	/**
	 * Add new process to the ring
	 * @param procIn
	 */
	public void addProcess(Process procIn) {
		processesInRing.add(procIn);
	}
	
	// get relay process list
	public List<Process> getRelayProcessList(){
		return inactiveProcInRing;
	}
	
	// get active process list
		public List<Process> getActiveProcessList(){
			return activeProcInRing;
	}
		
	// get process list
		public List<Process> getProcessList(){
			return processesInRing;
	}
		
	// add the current process into the active or passive list of the ring to prepare for the next round
	public void addToActiveOrPassiveList(Process procIn) {
		Process tempProc = procIn;
		
		if(tempProc.checkActiveOrRelay()) {
    		// if active 
    		// add to list with active processes
    		getActiveProcessList().add(tempProc);
    	} else {
    		// if inactive 
    		// send its data further and add to an inactiveProcessesList
    		getRelayProcessList().add(tempProc);
    	}
	}
}
