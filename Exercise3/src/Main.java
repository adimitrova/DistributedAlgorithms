import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class which creates an unidirectional connection with several components with the help of RMI.
 *
 * @author Anelia Dimitrova (4501667) & Laurens Weijs (4503813)
 * @version 22.11.2017
 */
public class Main {
    public static List<String> ipPortList; // global ip and port list of all processes
    public static List<Byzantine> byzantines;

    public static void main (String[] args) {
        ipPortList = new ArrayList<String>();
        byzantines = new ArrayList<Byzantine>();

        // initialization of unidirectional ring
        String ipLaurens = "145.94.152.192";
        String ipAni = "145.94.164.48";
        int[] IDsLaurens = {7, 4, 9, 12, 1};
        int[] IDsAni = {3, 8, 2, 6, 5};
        int[] portNumbersLaurens = {2007, 2004, 2009, 2012, 2001};
        int[] portNumbersAni = {2003, 2008, 2002, 2006, 2005};

        for (int i = 0; i< IDsLaurens.length; i++){
            if(i == IDsLaurens.length-1){
                bindRMIComponent(portNumbersLaurens[i],ipLaurens,"rmi://" + ipAni+ ":" + Integer.toString(portNumbersAni[0]),IDsLaurens[i]);
            } else {
                bindRMIComponent(portNumbersLaurens[i],ipLaurens,"rmi://" + ipLaurens+ ":" + Integer.toString(portNumbersLaurens[i+1]),IDsLaurens[i]);
            }
        }

        // FROM EXERCISE 2
//
//        // start the election in the unidirectional ring by sending an empty message a random node.
//        try {
//            TimeUnit.SECONDS.sleep(10);
//
//            components.get(0).receive(0, 0);
//
//            // wait a little and find the elected component
//            TimeUnit.SECONDS.sleep(10);
//
////            for(Component comp : components){
////                if(comp.isElected()){
////                    System.out.println("Component with ID: " + Integer.toString(comp.getID()) + " is elected. Hooray!" );
////                }
////            }
//
//            TimeUnit.MINUTES.sleep(5);
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
    }

    /**
     * Method to return a component which is binded to the IP and port specified.
     * @param portNumber port on which the RMI bind will be placed.
     * @param ip the ip on which the RMI will bind the component.
     * @param nextIpPort the IP Portnumber combination of the next component in the unidirectional circle.
     * @param ID The ID of the current component.System.setSecurityManager(new RMISecurityManager());
     * @return
     */
    private static void bindRMIComponent(int portNumber, String ip, String nextIpPort, int ID){
        try{
            System.setSecurityManager(new RMISecurityManager());
            Runtime.getRuntime().exec("rmiregistry " + Integer.toString(portNumber));
            LocateRegistry.createRegistry(portNumber);
            String ipPort = "rmi://" + ip + ":" + Integer.toString(portNumber);
            Byzantine byzantine = new Byzantine(ID, nextIpPort);
            Naming.rebind("rmi://" + ip + ":" +Integer.toString(portNumber) +"/byzantine", byzantine);
            byzantines.add(byzantine);
        }catch (Exception e) {
            System.out.println("Client Exception: " + e);
        }
    }
}
