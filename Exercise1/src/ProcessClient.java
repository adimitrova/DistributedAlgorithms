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
                Runtime.getRuntime().exec("rmiregistry 2021");
                LocateRegistry.createRegistry(2021);
                String ipPort2021 = "rmi://192.168.1.15:2021";	// own ip
                Process process2 = new Process(ipPortList, ipPort2021);
                Naming.rebind("rmi://192.168.1.15:2021/process", process2);	// own ip
                
                System.out.println("RMI Registry configured");
                
                // If running server execute the below code once
                boolean flag = true;
                while(flag){
                    process1.broadcast(new Message("---> MESSAGE: Msg 1 from process 1!", process1.getVectorClock()));
                    process1.broadcast(new Message("---> MESSAGE: Msg 2 from process 1!", process1.getVectorClock()));
                    try{
                    TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e){
                    	System.out.println("error during broadcast: " + e);
                    }
                    process2.broadcast(new Message("---> MESSAGE: Msg 1 from process 2!", process2.getVectorClock()));
                    flag = false;
                }
                // do something (broad of specific message of this process
                // maybe through console

            }catch (Exception e) {
                System.out.println("Client Exception: " + e);
            }
        }
}
