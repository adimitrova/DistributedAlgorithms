import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Random;

public class Byzantine extends UnicastRemoteObject implements ByzantineInterface{
	int round;	// round
//	int nodesParticipating;
	public static List<String> ipPortList; // global ip and port list of all processes
    public int faultTolerance = 0;         // faulty processes we allow
	public boolean decided;
	public int decidedValue;
	public Node node;
	public int amountNodes;
	public int id;
	private boolean traitor = false;
	private char traitorType;

	public Byzantine(int id, List<String> ipPorts, int n, int f) throws RemoteException{
        amountNodes = n;
        faultTolerance = f;
        ipPortList = ipPorts;
        node = new Node(id, ipPorts);
        round = 1;
        decided = false;
        this.id = id;
        System.out.println("Starting Byzantine, ID: " + id);
    }

	/**
	 * Traitor has two types of behaviour, set via this method:
	 * R = random value
	 * O = opposite of received value
	 * @param type
	 */
	public void setTraitor(char type){
		traitor = true;
		traitorType = type;
		System.out.println("ID: " + this.id + "| Traitor: " + traitor + " | Type: " + traitorType);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void broadcast(char MsgType, int round, int value) throws RemoteException{
		ByzantineInterface otherByzantine;
        if(!traitor){
        	for (int i = 0; i< ipPortList.size(); i++ ){
                try{
                    // connect to a second Byzantine process and send to it the broadcast.
                    // that second process receives the message.
                    System.setSecurityManager(new RMISecurityManager());
                    otherByzantine = (ByzantineInterface) Naming.lookup(ipPortList.get(i) +"/byzantine");
                    otherByzantine.receive(MsgType, round, value);
                    System.out.println("---> BROADCAST: "  + MsgType + " Value: " + value + " Round: " + round + " ID: " + this.id);
                }catch (Exception e) {
                    System.out.println("Broadcast Exception: " + e);
                }
            }
        } else if (traitor) {
        	// send random value
        	if(traitorType == 'R'){
        		int randomValue = 0;
        		for (int i = 0; i< ipPortList.size(); i++ ){
	                try{
	                    // connect to a second Byzantine process and send to it the broadcast.
	                    // that second process receives the message.
	                	randomValue = (int) (Math.round(Math.random()));
	                    System.setSecurityManager(new RMISecurityManager());
	                    otherByzantine = (ByzantineInterface) Naming.lookup(ipPortList.get(i) +"/byzantine");
	                    otherByzantine.receive(MsgType, round, randomValue);
	                    System.out.println("---> BROADCAST: " + MsgType + " Value: " + randomValue + " Round: " + round + " ID: " + this.id);
	                }catch (Exception e) {
	                    System.out.println("Broadcast Exception: " + e);
	                }
        		}	
        	// send opposite value
        	} else if(traitorType == 'O'){
        		int oppositeValue = value;
        		if (value == 0) {
        			oppositeValue = 1;
				} else if (value == 1){
					oppositeValue = 0;
				}
        		
        		for (int i = 0; i< ipPortList.size(); i++ ){
	                try{
	                    // connect to a second Byzantine process and send to it the broadcast.
	                    // that second process receives the message.
	                    System.setSecurityManager(new RMISecurityManager());
	                    otherByzantine = (ByzantineInterface) Naming.lookup(ipPortList.get(i) +"/byzantine");
	                    otherByzantine.receive(MsgType, round, oppositeValue);
	                    System.out.println("---> BROADCAST: " + MsgType + " Value: " + oppositeValue + " Round: " + round + " ID: " + this.id);
	                }catch (Exception e) {
	                    System.out.println("Broadcast Exception: " + e);
	                }
        		}
        	}
        }
	}
	
	@Override
	public void receive(char MsgType, int round, int value) throws RemoteException {
		System.out.println("<-- RECEIVED: " + MsgType + " Value: " + value + " Round: " + round + " ID: " + this.id);
		if(MsgType == 'N'){
		    // If the node receives a message for the first time, notify the others with that value
            if(node.getAmountNotified(round) == 0){
                int[] msg = {round, value};
                node.addNValue(msg);
                System.out.println("Ready to broadcast!\n");
                broadcast('N', round, value);
            } else {
                int[] msg = {round, value};
                node.addNValue(msg);
            }
            System.out.println("I, byzantine " + id + " have received: " + node.getAmountNotified(round) + " messages" );
            if(node.getAmountNotified(round) > (amountNodes - faultTolerance)){
                List<Integer> notifications = node.getNvalues(round);
                int countZero = 0;
                int countOne = 0;
                for (Integer elem: notifications) {
                    if(elem == 1){
                        countOne++;
                    }else if(elem == 0){
                        countZero++;
                    }
                }
                // send out proposal with either 0 (decide 0), 1(decide 1) or -1 (i don't know).
                if((countZero > (amountNodes + faultTolerance)/2)){
                    broadcast('P', round, 0);
                } else if((countOne > (amountNodes + faultTolerance)/2)){
                    broadcast('P', round, 1);
                } else {
                    broadcast('P', round, -1);
                }
            }
		}
		if(MsgType == 'P'){
            int[] msg = {round, value};
            node.addPValue(msg);
            if(node.getAmountProposed(round) > (amountNodes - faultTolerance)) {
                List<Integer> proposals = node.getPvalues(round);

                // Count the different values, if large than the half decide otherwise propose i don't know!
                int countZero = 0;
                int countOne = 0;
                for (Integer elem : proposals) {
                    if (elem == 1) {
                        countOne++;
                    } else if (elem == 0) {
                        countZero++;
                    }
                }

                //send out proposal with either 0 (decide 0), 1(decide 1) or -1 (i don't know).
                if (countZero > faultTolerance) {
                    node.setOwnValue(0);
                    if (countZero > 3 * faultTolerance) {
                        decidedValue = 0;
                        decided = true;
                    }
                } else if (countOne > faultTolerance) {
                    node.setOwnValue(1);
                    if (countOne > 3 * faultTolerance) {
                        decidedValue = 1;
                        decided = true;
                    }
                } else {
                    // decide a random value between 0 and 1
                    Random random = new Random();
                    node.setOwnValue(random.nextInt(1));
                }
                System.out.println("<< DECIDED >> byzantine " + id + ", value: " + decidedValue);
            }
        }
	}

	@Override
	public boolean hasDecided() throws RemoteException{
        return decided;
	}
}
