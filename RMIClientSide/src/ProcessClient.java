import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.List;

/**
 * Created by Laurens on 11/22/2017.
 */
public class ProcessClient{
    List<String> ipPortList; // global ip and port list of all processes

        public static void main (String[] args) {
            ProcessInterface_Laurens process;
            try {
                System.setSecurityManager(new RMISecurityManager());
                Runtime.getRuntime().exec("rmiregistry 2020");
                LocateRegistry.createRegistry(2020);
                process = new Process_LaurensLaurens();
                // ip = localhost (make sure)
//                getIp();
                Naming.rebind("rmi://localhost:2020", process);

                // WAITS

                // FROM own IP list

                // new process ( which also stores IP and Port list )

                // do something (broad of specific message of this process
                // maybe through console

            }catch (Exception e) {
                System.out.println("HelloClient Exception: " + e);
            }
        }
}
