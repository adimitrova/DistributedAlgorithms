import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class which creates an unidirectional connection with several components with the help of RMI.
 * 
 * Peterson's algorithm is explained by teacher in previous years BELOW 
 * https://collegerama.tudelft.nl/Mediasite/Play/cb6da7ce5002457fb804557758e222a11d?catalog=528e5b24-a2fc-4def-870e-65bd84b28a8c
 * at time: 0:43:51 
 * 
 * @author Anelia Dimitrova (4501667) & Laurens Weijs (4503813)
 * @version 22.11.2017
 */
public class Main {
    public static List<String> ipPortList; // global ip and port list of all processes
    public static List<Component> components;

    public static void main (String[] args) {
        ipPortList = new ArrayList<String>();
        components = new ArrayList<Component>();
/*      ipPortList.add("rmi://145.94.167.207:1099");	// Ani proc1
        ipPortList.add("rmi://145.94.167.207:2021");	// Ani proc2
        ipPortList.add("rmi://145.94.152.214:2020");	// Laurens proc 1
*/
        // initialization of unidirectional ring
        String ip = "145.94.152.192";
//        String ip = "localhost";
//        String ip = "192.168.0.109";
        int[] IDs = {7, 4, 9, 12, 1, 3, 8, 2, 6, 5};
        int[] portNumbers = {2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010};

        for (int i = 0; i< IDs.length; i++){
            if(i == IDs.length-1){
                bindRMIComponent(portNumbers[i],ip,"rmi://" + ip+ ":" + Integer.toString(portNumbers[0]),IDs[i]);
            } else {
                bindRMIComponent(portNumbers[i],ip,"rmi://" + ip+ ":" + Integer.toString(portNumbers[i+1]),IDs[i]);
            }
        }

        // start the election in the unidirectional ring by sending an empty message a random node.
        try {
            components.get(0).receive(0, 0);

            // wait a little and find the elected component
            TimeUnit.SECONDS.sleep(30);

            for(Component comp : components){
                if(comp.isElected()){
                    System.out.println("Component with ID: " + Integer.toString(comp.getID()) + " is elected. Hooray!" );
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
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
            Component component = new Component(nextIpPort, ID);
            Naming.rebind("rmi://" + ip + ":" +Integer.toString(portNumber) +"/component", component);
            components.add(component);
        }catch (Exception e) {
            System.out.println("Client Exception: " + e);
        }
    }
}
