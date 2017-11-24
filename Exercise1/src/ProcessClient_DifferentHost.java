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
public class ProcessClient_DifferentHost {
    public static List<String> ipPortList; // global ip and port list of all processes

        public static void main (String[] args) {
            ipPortList = new ArrayList<String>();
            ipPortList.add("rmi://145.94.167.207:1099");
            ipPortList.add("rmi://145.94.167.207:2021");
            ipPortList.add("rmi://145.94.152.214:2020");

            ProcessInterface process;
            try {
            	/**
            	 * To test the ordering, we create two processes:
            	 * process1 sends two messages and the client then waits for 1s 
            	 * and then process2 sends one message. 
            	 * Output:
            	 * Process 1 message 1: [1, 0, 0]
            	 * Process 1 message 2: [2, 0, 0]
            	 * Process 2 message 1: [2, 1, 0]
            	 * if we let it repeat, output is:
            	 * Process 1 message 1: [3, 1, 0]
            	 * Process 1 message 2: [4, 1, 0]
            	 * Process 2 message 1: [4, 2, 0]
            	 */
                System.setSecurityManager(new RMISecurityManager());
                Runtime.getRuntime().exec("rmiregistry 1099");
                LocateRegistry.createRegistry(1099);
                String ipPort1099 = "rmi://192.168.1.15:1099";	// own ip
                Process process1 = new Process(ipPortList, ipPort1099);
                Naming.rebind("rmi://192.168.1.15:1099/process", process1);	// own ip
                
                System.setSecurityManager(new RMISecurityManager());

                Runtime.getRuntime().exec("rmiregistry 2020");
                LocateRegistry.createRegistry(2020);
                String ipPort = "rmi://145.94.152.214:2020";
                process = new Process(ipPortList, ipPort);
                Naming.rebind(ipPort + "/process", process);
                System.out.println("RMI Registry configured");

                //TODO: the list below.
                // wait for the other processes to connect to the RMI registry
//                TimeUnit.MINUTES.sleep(1);
                // do something (broad of specific message of this process
                // maybe through console
//                process.broadcast(new Message("This is an interesting message!", process.getVectorClock()));
//                TimeUnit.MINUTES.sleep(1);

            }catch (Exception e) {
                System.out.println("Client Exception: " + e);
            }
        }
}
