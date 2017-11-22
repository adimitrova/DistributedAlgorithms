import java.rmi.Naming;
import java.rmi.RMISecurityManager;

/**
 * Created by Laurens on 11/17/2017.
 */
public class AdditionClient {
    public static void main (String[] args) {
        AdditionInterfaceCl hello;
        try {
            System.setSecurityManager(new RMISecurityManager());
            hello = (AdditionInterfaceCl) Naming.lookup("rmi://localhost:2020/Hello");
            int result = hello.add(9, 10);
            System.out.println("Result is: " + result);
        }catch (Exception e) {
            System.out.println("HelloClient Exception: " + e);
        }
    }
}
