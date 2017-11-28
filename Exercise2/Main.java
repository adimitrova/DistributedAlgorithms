import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

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
/*      ipPortList.add("rmi://145.94.167.207:1099");	// Ani proc1
        ipPortList.add("rmi://145.94.167.207:2021");	// Ani proc2
        ipPortList.add("rmi://145.94.152.214:2020");	// Laurens proc 1
*/
        String ip = "192.168.0.109";
        int[] IDs = {7, 4, 9, 12, 1, 3, 8, 2, 6, 5};
        int[] portNumbers = {2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010};

        for (int i = 0; i< IDs.length; i++){
            if(i == IDs.length-1){
                components.add(bindRMIComponent(portNumbers[i],ip,ip + Integer.toString(portNumbers[0]),IDs[i]));
            } else {
                components.add(bindRMIComponent(portNumbers[i],ip,ip + Integer.toString(portNumbers[i+1]),IDs[i]));
            }
        }

    }

    /**
     * Method to return a component which is binded to the IP and port specified.
     * @param portNumber port on which the RMI bind will be placed.
     * @param ip the ip on which the RMI will bind the component.
     * @param nextIpPort the IP Portnumber combination of the next component in the unidirectional circle.
     * @param ID The ID of the current component.
     * @return
     */
    private static Component bindRMIComponent(int portNumber, String ip, String nextIpPort, int ID){
        try{
            System.setSecurityManager(new RMISecurityManager());
            Runtime.getRuntime().exec("rmiregistry " + Integer.toString(portNumber));
            LocateRegistry.createRegistry(portNumber);
            String ipPort = "rmi://" + ip + ":" +Integer.toString(portNumber);	// own ip
            Component component = new Component(nextIpPort, ID);
            Naming.rebind("rmi://" + ip + ":" +Integer.toString(portNumber) +"/process", component);

            return component;
        }catch (Exception e) {
            System.out.println("Client Exception: " + e);
        }

        // Maybe fix by invariant :)
        return null;
    }

    // main program
    /**
     * setup the ring
     * get the first process
     * first process sets its id, nid and nnid
     * first process checks whether its nid >= id AND nid => id
     * if YES ==> Component is marked as Active		add to active list
     * if NO  ==> Component is marked as Relay (inactive), but stays in the process list		add to passive list
     * first process sends its id and nnid to the next process
     * repeat
     * ---
     * round 2
     * flush all inactive processes so we are left with active processes only
     * repeat the same steps for round 2
     * ---
     * repeat the rounds with survivors until one final process/id is elected
     * remove that process from the process list
     * ---
     * when the process is elected, start over with round 1 until the process list is empty
     *
     * QUERY: WHAT HAPPENS WITH THE FIRST PROCESS in the round and what values does it get at initialization?
     * id from the last process in the list???? so that id has ID and NID and no NNID?
     */

}
