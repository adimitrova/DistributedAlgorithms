import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
        String ipLaurens = "145.94.5.48";
        String ipAni = "145.94.5.223";
        int[] IDsLaurens = {7, 4, 9, 12, 1};
        int[] IDsAni = {3, 8, 2, 6, 5, 10};
        //int[] IDsAni = {};
        //int[] IDsLaurens = {};

        int[] portNumbersLaurens = {2007, 2004, 2009, 2012, 2001};
        int[] portNumbersAni = {2021, 2026, 2023, 2024, 2025, 2010};
        //int[] portNumbersLaurens = {};
        //int[] portNumbersAni = {};
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
        	
        	nodeIp = nodeIp.substring(6);
            String[] ipPort = nodeIp.split(":");
            String ip = ipPort[0];
            int port = Integer.parseInt(ipPort[1]);
            List<String> tempIpPorts = new ArrayList<String>();            
            
            for (String otherNodeIp : ipPortList) {
                if (!otherNodeIp.equals(nodeIp)) {
                    tempIpPorts.add(otherNodeIp);
                }
            }
            if(ip.equals(ipAni)) {
	            if(ID == 0 || ID == 2 ) {
	                bindRMIComponent(port, ip, tempIpPorts, ID, (IDsAni.length + IDsLaurens.length), amountFaulty, 1);
	            } else{
	                bindRMIComponent(port, ip, tempIpPorts, ID, (IDsAni.length + IDsLaurens.length), amountFaulty, 0);
	            }
	            ID++;
            }
        }

        // Now let the general start with broadcasting
        Byzantine general = byzantines.get(0);
        byzantines.get(1).setTraitor('O');

        try {
        	TimeUnit.SECONDS.sleep(3);
            general.broadcast('N', 1, 1);

        } catch (Exception e){
            e.printStackTrace();
        }
            // now check whether agreement is reached
            boolean[] decidedArray = new boolean[byzantines.size()];
            for (int i = 0; i < byzantines.size(); i++) {
                int countDecided = 0;
                try {
                    if (byzantines.get(i).hasDecided()) {
                        decidedArray[i] = true;
                        countDecided++;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(countDecided == ipPortList.size()-amountFaulty){
                    System.out.println("All loyal processes have decided :)!");
                    for (Byzantine byzantine: byzantines) {
                        System.out.println("Byzantine with ID = " + byzantine.getID() + " has decided: " + byzantine.getValue());
                    }
                }
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
    private static void bindRMIComponent(int portNumber, String ip, List<String> nextIpPorts, int ID, int n, int f, int value){
        try{
            System.setSecurityManager(new RMISecurityManager());
            LocateRegistry.createRegistry(portNumber);
            String ipPort = "rmi://" + ip + ":" + Integer.toString(portNumber);
            Byzantine byzantine = new Byzantine(ID, nextIpPorts, n, f, value);
            Naming.rebind("rmi://" + ip + ":" +Integer.toString(portNumber) +"/byzantine", byzantine);
            byzantines.add(byzantine);
            
            if(portNumber == 24){
            	byzantine.setTraitor('R');
            }
        }catch (Exception e) {
            System.out.println("Client Exception: " + e);
        }
    }
}
