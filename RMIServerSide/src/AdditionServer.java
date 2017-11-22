import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;

/**
 * Created by Laurens on 11/17/2017.
 */
public class AdditionServer {
    public static void main(String[] argv) {
        try {
            System.setSecurityManager(new RMISecurityManager());
            //System.setProperty("java.security.policy","file:./security.policy");
            Runtime.getRuntime().exec("rmiregistry 2020");
            LocateRegistry.createRegistry(2020);
            Addition Hello = new Addition();
            Naming.rebind("rmi://localhost:2020/Hello", Hello);

            System.out.println("Addition Server is ready.");
        }catch (Exception e) {
            System.out.println("Addition Server failed: " + e);
        }
    }
}
