import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which creates the connection with the other processes with the help of RMI.
 */
public class ProcessClient{
    List<String> ipPortList; // global ip and port list of all processes

        public static void main (String[] args) {
            ProcessInterface process;
            try {
                System.setSecurityManager(new RMISecurityManager());
                Runtime.getRuntime().exec("rmiregistry 2020");
                LocateRegistry.createRegistry(2020);
                process = new Process(new ArrayList<String>());
                // ip = localhost (make sure)
//                getIp();
                Naming.rebind("rmi://localhost:2020", process);


                //TODO: the list below.
                // WAITS

                // FROM own IP list

                // new process ( which also stores IP and Port list )

                // do something (broad of specific message of this process
                // maybe through console

            }catch (Exception e) {
                System.out.println("Client Exception: " + e);
            }
        }
}
