import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class which creates the connection with the other processes with the help of RMI.
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
