import java.net.Inet4Address;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;

/**
 * Created by Laurens on 11/22/2017.
 */
public class ProcessClient{

        public static void main (String[] args) {
            ProcessInterface process;
            try {
                System.setSecurityManager(new RMISecurityManager());
                Runtime.getRuntime().exec("rmiregistry 2020");
                LocateRegistry.createRegistry(2020);
                process = new Process();
                Naming.rebind("rmi://localhost:2020", process);


            }catch (Exception e) {
                System.out.println("HelloClient Exception: " + e);
            }
        }
}
