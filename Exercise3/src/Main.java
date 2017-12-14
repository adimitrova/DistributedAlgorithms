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
        String ipLaurens = "192.168.0.109";
        String ipAni = "145.94.167.195";
        int[] IDsLaurens = {7, 4, 9, 12, 1};
//        int[] IDsAni = {3, 8, 2, 6, 5};
        int[] IDsAni = {};

        int[] portNumbersLaurens = {2010, 2014, 2009, 2012, 2001};
//        int[] portNumbersAni = {2003, 2008, 2002, 2006, 2005};
        int[] portNumbersAni = {};
        int amountFaulty = 1 ;

        // Create the fully connected Network
        for (int i = 0; i< IDsLaurens.length; i++) {
            ipPortList.add("rmi://" + ipLaurens + ":" + Integer.toString(portNumbersLaurens[i]));
        }

        for (int i = 0; i< IDsAni.length; i++) {
            ipPortList.add("rmi://" + ipAni + ":" + Integer.toString(portNumbersAni[i]));
        }

        int ID = 0;
        // Create each byzantine node
        for(String nodeIp: ipPortList) {
            List<String> tempIpPorts = new ArrayList<String>();
            for (String otherNodeIp : ipPortList) {
                if (!otherNodeIp.equals(nodeIp)) {
                    tempIpPorts.add(otherNodeIp);
                }
            }
            nodeIp = nodeIp.substring(6);
            String[] ipPort = nodeIp.split(":");
            String ip = ipPort[0];
            int port = Integer.parseInt(ipPort[1]);
            bindRMIComponent(port, ip, tempIpPorts, ID, (IDsAni.length + IDsLaurens.length), amountFaulty);
            ID++;
        }

        // Now let the general start with broadcasting
        Byzantine general = byzantines.get(0);
        try {
//            TimeUnit.SECONDS.sleep(5);
            general.broadcast('N', 1, 1);    // NEVER START AT ROUND 0
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method to return a component which is binded to the IP and port specified.
     * @param portNumber port on which the RMI bind will be placed.
     * @param ip the ip on which the RMI will bind the component.
     * @param nextIpPorts the IP Portnumber combination of the next component in the unidirectional circle.
     * @param ID The ID of the current component.System.setSecurityManager(new RMISecurityManager()).
     * @param n the amount of nodes in the network (#length ipPortList + itself).
     * @param f the amount of faulty byzantines in the fully connected network.
     * @return
     */
    private static void bindRMIComponent(int portNumber, String ip, List<String> nextIpPorts, int ID, int n, int f){
        try{
            System.setSecurityManager(new RMISecurityManager());
            LocateRegistry.createRegistry(portNumber);
            String ipPort = "rmi://" + ip + ":" + Integer.toString(portNumber);
            Byzantine byzantine = new Byzantine(ID, nextIpPorts, n, f);
            System.out.println("rmi://" + ip + ":" +Integer.toString(portNumber) +"/byzantine -> bind a new byzantine");
            Naming.rebind("rmi://" + ip + ":" +Integer.toString(portNumber) +"/byzantine", byzantine);
            byzantines.add(byzantine);
        }catch (Exception e) {
            System.out.println("Client Exception: " + e);
        }
    }
}
