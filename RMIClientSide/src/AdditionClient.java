import java.rmi.Naming;
import java.rmi.RMISecurityManager;

/**
 * Created by Laurens on 11/17/2017.
 */
public class AdditionClient {
    public static void main (String[] args) {
        AdditionInterface hello;
        try {
            System.setSecurityManager(new RMISecurityManager());
            hello = (AdditionInterface) Naming.lookup("rmi://localhost/ABC");
            int result = hello.add(9, 10);
            System.out.println("Result is: " + result);

        }catch (Exception e) {
            System.out.println("HelloClient Exception: " + e);
        }
    }
}
