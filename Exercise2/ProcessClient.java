import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class which creates the connection with the other processes with the help of RMI.
 * 
 * @author Anelia Dimitrova (4501667) & Laurens Weijs (4503813)
 * @version 22.11.2017
 */
public class ProcessClient{
    public static List<String> ipPortList; // global ip and port list of all processes

        public static void main (String[] args) {
            ipPortList = new ArrayList<String>();
/*          ipPortList.add("rmi://145.94.167.207:1099");	// Ani proc1
            ipPortList.add("rmi://145.94.167.207:2021");	// Ani proc2
            ipPortList.add("rmi://145.94.152.214:2020");	// Laurens
*/
            // Ani local ip with diff. port
            ipPortList.add("rmi://192.168.1.15:1099");	// proc1 / client 1
            ipPortList.add("rmi://192.168.1.15:2021");	// proc2 / client 2
            ipPortList.add("rmi://192.168.1.15:2020");	// server / client 3
            
            ProcessInterface process;
            try {
            	// ------------ setup the processes -------------
                System.setSecurityManager(new RMISecurityManager());
                Runtime.getRuntime().exec("rmiregistry 1099");
                LocateRegistry.createRegistry(1099);
                String ipPort1099 = "rmi://192.168.1.15:1099";	// own ip
                Process process1 = new Process(ipPortList, ipPort1099, 1);
                Naming.rebind("rmi://192.168.1.15:1099/process", process1);	// own ip
                
                System.setSecurityManager(new RMISecurityManager());
                Runtime.getRuntime().exec("rmiregistry 2021");
                LocateRegistry.createRegistry(2021);
                String ipPort2021 = "rmi://192.168.1.15:2021";	// own ip
                Process process2 = new Process(ipPortList, ipPort2021, 2);
                Naming.rebind("rmi://192.168.1.15:2021/process", process2);	// own ip
                
                System.out.println("RMI Registry configured");
                // ------------ end setup processes -------------
                
                
                // If running server execute the below code once
                boolean flag = true;
                while(flag){UnidirectionalRing ring = new UnidirectionalRing(3);
                	Process firstProc = ring.getProcessList().get(0);
                	
                	// first process received its left neighbour's data
                	firstProc.receive(ring.getProcessList().get(ring.getProcessList().size()-1).getID(), 
                			  	 	  ring.getProcessList().get(ring.getProcessList().size()-1).getCurProcNNID());
                	
                	// check status and add to one of the lists (wither active or passive process list)
                	ring.addToActiveOrPassiveList(firstProc);
                	firstProc.send(firstProc.getID(), firstProc.getReceivedNNID());
                	
                	
                	// main program
                	/**
                	 * setup the ring
                	 * get the first process
                	 * first process sets its id, nid and nnid
                	 * first process checks whether its nid >= id AND nid => id
                	 * if YES ==> Process is marked as Active		add to active list
                	 * if NO  ==> Process is marked as Relay (inactive), but stays in the process list		add to passive list
                	 * first process sends its id and nnid to the next process
                	 * repeat
                	 * ---
                	 * round 2
                	 * flush all inactive processes so we are left with active processes only
                	 * repeat the same steps for round 2
                	 * ---
                	 * repeat the rounds with survivors until one final process/id is elected
                	 * remove that process from the process list
                	 * ---
                	 * when the process is elected, start over with round 1 until the process list is empty
                	 * 
                	 * QUERY: WHAT HAPPENS WITH THE FIRST PROCESS in the round and what values does it get at initialization?
                	 * id from the last process in the list???? so that id has ID and NID and no NNID?
                	 */
                	
                	/*process2.receive(process1.getOwnId(),process1.getownNNID());
                    try{
                    TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e){
                    	System.out.println("error during broadcast: " + e);
                    }
                    process2.broadcast(new Message("---> MESSAGE: Msg 1 from process 2!", process2.getVectorClock()));
                    flag = false;*/
                }

            }catch (Exception e) {
                System.out.println("Client Exception: " + e);
            }
        }
}
