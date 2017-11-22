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

            Runtime.getRuntime().exec("rmiregistry 1024");
            LocateRegistry.createRegistry(1024);
            Addition Hello = new Addition();
            Naming.rebind("rmi://localhost:1024/Hello", Hello);

            System.out.println("Addition Server is ready.");
        }catch (Exception e) {
            System.out.println("Addition Server failed: " + e);
        }
    }
}
