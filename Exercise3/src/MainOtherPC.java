import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which creates an unidirectional connection with several components with the help of RMI.
 *
 * @author Anelia Dimitrova (4501667) & Laurens Weijs (4503813)
 * @version 22.11.2017
 */
public class MainOtherPC {
    public static List<String> ipPortList; // global ip and port list of all processes
    public static List<Byzantine> byzantines;

    public static void main (String[] args) {
        ipPortList = new ArrayList<String>();
        byzantines = new ArrayList<Byzantine>();

        // initialization of unidirectional ring
        String ipLaurens = "192.168.0.104";
        String ipAni = "192.168.0.109";
//        int[] IDsLaurens = {7, 4, 9, 12, 1};
        int[] IDsLaurens = {};
        int[] IDsAni = {3, 8, 2, 6, 5, 10};

//        int[] portNumbersLaurens = {2007, 2004, 2009, 2012, 2001};
        int[] portNumbersLaurens = {};
        int[] portNumbersAni = {2021, 2026, 2023, 2024, 2025, 2010};
        int amountFaulty = 1 ;

        // Create the fully connected Network
        for (int i = 0; i< IDsLaurens.length; i++) {
            ipPortList.add("rmi://" + ipLaurens + ":" + Integer.toString(portNumbersLaurens[i]));
        }

        for (int i = 0; i< IDsAni.length; i++) {
            ipPortList.add("rmi://" + ipAni + ":" + Integer.toString(portNumbersAni[i]));
        }
//        int testcase = 2;
//        int testcase = 3;
        int testcase = 5;
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
            // testcase 2, 4 zeroes and 1 one.
            if(testcase == 2) {
                if (ip.equals(ipAni)) {
                    if (ID == 0) {
                        bindRMIComponent(port, ip, tempIpPorts, ID, (IDsAni.length + IDsLaurens.length), amountFaulty, 1);
                    } else {
                        bindRMIComponent(port, ip, tempIpPorts, ID, (IDsAni.length + IDsLaurens.length), amountFaulty, 0);
                    }
                    ID++;
                }
            } else if (testcase == 3){
                if (ip.equals(ipAni)) {
                    if (ID == 0 || ID == 1) {
                        bindRMIComponent(port, ip, tempIpPorts, ID, (IDsAni.length + IDsLaurens.length), amountFaulty, 1);
                    } else {
                        bindRMIComponent(port, ip, tempIpPorts, ID, (IDsAni.length + IDsLaurens.length), amountFaulty, 0);
                    }
                    ID++;
                }
            } else if (testcase == 5){
                int upperbound = 10;
                for (int i = 0; i < upperbound; i++) {
                    int tempport = 1000;
                    tempport = tempport + i;
                    if (ID == 0 || ID == 1 || ID == 50 || ID == 100) {
                        bindRMIComponent(tempport, ip, tempIpPorts, ID, upperbound, amountFaulty, 1);
                    } else {
                        bindRMIComponent(tempport, ip, tempIpPorts, ID, upperbound, amountFaulty, 0);
                    }
                    ID++;
                }
            }
        }


        // testcase 2 (n = 6, f = 1 (nothing) )
        if(testcase == 2){
            byzantines.get(1).setTraitor('N'); // set the traitor to send Nothing
            try {
                byzantines.get(0).broadcast('N', 1, 1);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else if (testcase == 3 || testcase == 5){
            byzantines.get(3).setTraitor('R'); // set the traitor to send Random values
            try {
                byzantines.get(0).broadcast('N', 1, 1);
            } catch (Exception e){
                e.printStackTrace();
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
            Runtime.getRuntime().exec("rmiregistry " + Integer.toString(portNumber));
            LocateRegistry.createRegistry(portNumber);
            String ipPort = "rmi://" + ip + ":" + Integer.toString(portNumber);
            Byzantine byzantine = new Byzantine(ID, nextIpPorts, n, f, value);
            Naming.rebind("rmi://" + ip + ":" +Integer.toString(portNumber) +"/byzantine", byzantine);
            byzantines.add(byzantine);
        }catch (Exception e) {
            System.out.println("Client Exception: " + e);
        }
    }
}
