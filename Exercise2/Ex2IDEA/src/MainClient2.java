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
public class MainClient2 {
    public static List<String> ipPortList; // global ip and port list of all processes
    public static List<Component> components;

    public static void main (String[] args) {
        ipPortList = new ArrayList<String>();
        components = new ArrayList<Component>();

        // initialization of unidirectional ring
        String ipLaurens = "145.94.152.192";
        String ipAni = "145.94.164.48";
        int[] IDsLaurens = {7, 4, 9, 12, 1};
        int[] IDsAni = {3, 8, 2, 6, 5};
        int[] portNumbersLaurens = {2007, 2004, 2009, 2012, 2001};
        int[] portNumbersAni = {2003, 2008, 2002, 2006, 2005};

        for (int i = 0; i< IDsLaurens.length; i++){
            if(i == IDsLaurens.length-1){
                bindRMIComponent(portNumbersLaurens[i],ipAni,"rmi://" + ipLaurens+ ":" + Integer.toString(portNumbersLaurens[0]),IDsAni[i]);
            } else {
                bindRMIComponent(portNumbersLaurens[i],ipAni,"rmi://" + ipAni + ":" + Integer.toString(portNumbersAni[i+1]),IDsAni[i]);
            }
        }

        // start the election in the unidirectional ring by sending an empty message a random node.
        try {
            TimeUnit.SECONDS.sleep(10);

            components.get(0).receive(0, 0);

            // wait a little and find the elected component
            TimeUnit.SECONDS.sleep(10);

//            for(Component comp : components){
//                if(comp.isElected()){
//                    System.out.println("Component with ID: " + Integer.toString(comp.getID()) + " is elected. Hooray!" );
//                }
//            }

            TimeUnit.MINUTES.sleep(5);

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

//"C:\Program Files\Java\jdk1.8.0_131\bin\java" -Djava.security.policy=security.policy "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2017.1.2\lib\idea_rt.jar=64845:C:\Program Files\JetBrains\IntelliJ IDEA 2017.1.2\bin" -Dfile.encoding=UTF-8 -classpath "C:\Program Files\Java\jdk1.8.0_131\jre\lib\charsets.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\deploy.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\access-bridge-64.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\cldrdata.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\dnsns.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\jaccess.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\jfxrt.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\localedata.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\nashorn.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\sunec.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\sunjce_provider.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\sunmscapi.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\sunpkcs11.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\ext\zipfs.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\javaws.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\jce.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\jfr.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\jfxswt.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\jsse.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\management-agent.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\plugin.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\resources.jar;C:\Program Files\Java\jdk1.8.0_131\jre\lib\rt.jar;C:\Users\Laurens\Documents\DistributedAlgorithms\Exercise2\Ex2IDEA\out\production\Ex2IDEA" Main
//        ID: 9 TID: 9 Active: falseElected: false
//        4...7
//        ID: 12 TID: 12 Active: falseElected: false
//        9...9
//        ID: 1 TID: 12 Active: trueElected: false
//        12...12
//        ID: 3 TID: 3 Active: falseElected: false
//        1...12
//        ID: 8 TID: 8 Active: falseElected: false
//        3...3
//        ID: 2 TID: 8 Active: trueElected: false
//        8...8
//        ID: 6 TID: 6 Active: falseElected: false
//        2...8
//        ID: 5 TID: 6 Active: trueElected: false
//        6...6
//        ID: 7 TID: 7 Active: falseElected: false
//        5...6
//        ID: 4 TID: 7 Active: trueElected: false
//        7...7
//        ID: 9 TID: 9 Active: falseElected: false
//        4...7
//        ID: 12 TID: 12 Active: falseElected: false
//        4...7
//        ID: 1 TID: 12 Active: falseElected: false
//        4...7
//        ID: 3 TID: 3 Active: falseElected: false
//        12...12
//        ID: 8 TID: 8 Active: falseElected: false
//        12...12
//        ID: 2 TID: 12 Active: trueElected: false
//        12...12
//        ID: 6 TID: 6 Active: falseElected: false
//        8...12
//        ID: 5 TID: 6 Active: falseElected: false
//        8...12
//        ID: 7 TID: 7 Active: falseElected: false
//        6...8
//        ID: 4 TID: 7 Active: falseElected: false
//        6...8
//        ID: 9 TID: 9 Active: falseElected: false
//        7...7
//        ID: 12 TID: 12 Active: falseElected: false
//        7...7
//        ID: 1 TID: 12 Active: falseElected: false
//        7...7
//        ID: 3 TID: 3 Active: falseElected: false
//        7...7
//        ID: 8 TID: 8 Active: falseElected: false
//        7...7
//        ID: 2 TID: 12 Active: falseElected: false
//        7...7
//        ID: 6 TID: 6 Active: falseElected: false
//        12...12
//        ID: 5 TID: 6 Active: falseElected: false
//        12...12
//        ID: 7 TID: 7 Active: falseElected: false
//        12...12
//        ID: 4 TID: 7 Active: falseElected: false
//        12...12
//        ID: 9 TID: 9 Active: falseElected: false
//        12...12
//        ID: 12 TID: 12 Active: falseElected: true
//        12...12
//        ID: 1 TID: 12 Active: falseElected: false
//        12...12
//        ID: 3 TID: 3 Active: falseElected: false
//        12...12
//        ID: 8 TID: 8 Active: falseElected: false
//        12...12
//        ID: 2 TID: 12 Active: falseElected: false
//        12...12
//        ID: 6 TID: 6 Active: falseElected: false
//        12...12
//        ID: 5 TID: 6 Active: falseElected: false
//        12...12
//        ID: 7 TID: 7 Active: falseElected: false
//        12...12
//        ID: 4 TID: 7 Active: falseElected: false
//        12...12
//        ID: 9 TID: 9 Active: falseElected: false
//        12...12
//
//        Process finished with exit code 1

