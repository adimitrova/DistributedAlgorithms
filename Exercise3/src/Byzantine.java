import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
	private List<Boolean> enteredNotification = new ArrayList<Boolean>();
    private List<Boolean> enteredProposal = new ArrayList<Boolean>();

	public Byzantine(int id, List<String> ipPorts, int n, int f, int value) throws RemoteException{
        amountNodes = n;
        faultTolerance = f;
        ipPortList = ipPorts;
        node = new Node(id, ipPorts);
        round = 1;
        decided = false;
        this.id = id;
        System.out.println("Starting Byzantine, ID: " + id + " with a value of " + value);
        node.setOwnValue(value);
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
//        // for testcase 4 (Asynchronous)
//        Random randomwait = new Random();
//        try {
//            TimeUnit.SECONDS.sleep(randomwait.nextInt(2));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        ByzantineInterface otherByzantine;
        if(!traitor){
        	for (int i = 0; i< ipPortList.size(); i++ ){
                try{
                    // connect to a second Byzantine process and send to it the broadcast.
                    // that second process receives the message.
                    System.setSecurityManager(new RMISecurityManager());
                    otherByzantine = (ByzantineInterface) Naming.lookup(ipPortList.get(i) +"/byzantine");
                    otherByzantine.receive(MsgType, round, value);
                }catch (Exception e) {
                    System.out.println("Broadcast Exception: " + e);
                }
            }
        } else if (traitor) {
        	// send random value
        	if(traitorType == 'R'){
        		Random random = new Random();
        		for (int i = 0; i< ipPortList.size(); i++ ){
	                try{
	                    // connect to a second Byzantine process and send to it the broadcast.
	                    // that second process receives the message.
	                	int randomValue = random.nextInt(2);
	                    System.setSecurityManager(new RMISecurityManager());
	                    otherByzantine = (ByzantineInterface) Naming.lookup(ipPortList.get(i) +"/byzantine");
	                    otherByzantine.receive(MsgType, round, randomValue);
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
	                }catch (Exception e) {
	                    System.out.println("Broadcast Exception: " + e);
	                }
        		}
        	}
        }
	}
	
	@Override
	public void receive(char MsgType, int round, int value) throws RemoteException {
//		System.out.println("<-- RECEIVED: " + MsgType + " Value: " + value + " Round: " + round + " ID: " + this.id);
		if(MsgType == 'N'){
		    // If the node receives a message for the first time, notify the others with that value
            // only in the first round the broadcast should be done otherwise is done at the end of this method.
            if(node.getAmountNotified(round) == 0 && round ==1){
                int[] msg = {round, value};
                node.addNValue(msg);
                broadcast('N', round, node.getOwnValue());
            }
            int[] msg = {round, value};
            node.addNValue(msg);

            if(enteredNotification.size() < round){
                enteredNotification.add(false);
            }
            if(node.getAmountNotified(round) > (amountNodes - faultTolerance) && !enteredNotification.get(round-1)){
                enteredNotification.set(round-1,true);
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
                if((countZero > (amountNodes + faultTolerance)/2)){
                    broadcast('P', round, 0);
                } else if((countOne > (amountNodes + faultTolerance)/2)){
                    broadcast('P', round, 1);
                } else {
                    broadcast('P', round, -1);
                }
            }
		} else if (MsgType == 'P') {

                int[] msg = {round, value};
                node.addPValue(msg);
                if(enteredProposal.size() < round){
                    enteredProposal.add(false);
                }
                if (node.getAmountProposed(round) == (amountNodes - faultTolerance) && !enteredProposal.get(round-1)) {
                    if(!decided) {
                        enteredNotification.set(round - 1, true);
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
                        if (countZero > 3 * faultTolerance) {
                            decidedValue = 0;
                            decided = true;
                            node.setOwnValue(decidedValue);
                            String extraString = "";
                            if (traitor) {
                                extraString = " (traitor) ";
                            }
                            System.out.println("<< DECIDED >> byzantine " + extraString + id + ", value: " + decidedValue + " After round: " + round);
                        } else if (countOne > 3 * faultTolerance) {
                            decidedValue = 1;
                            decided = true;
                            node.setOwnValue(decidedValue);
                            String extraString = "";
                            if (traitor) {
                                extraString = " (traitor) ";
                            }
                            System.out.println("<< DECIDED >> byzantine " + extraString + id + ", value: " + decidedValue + " After round: " + round);
                        } else {
                            Random random = new Random();
                            int rValue = random.nextInt(2);
                            node.setOwnValue(rValue);
                        }
                    }
                    round++;
                    broadcast('N', round, node.getOwnValue());
            }
        }
	}

	@Override
	public boolean hasDecided() throws RemoteException{
        return decided;
	}

	public int getValue(){
	    return node.getOwnValue();
    }

    public int getID(){
        return id;
    }

    public boolean isTraitor(){
        return traitor;
    }

}
