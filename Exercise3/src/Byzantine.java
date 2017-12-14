import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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

	protected Byzantine(int id, List<String> ipPorts, int n, int f) throws RemoteException {
		amountNodes = n;
		faultTolerance = f;
		ipPortList = ipPorts;
		node = new Node(id, ipPorts);
		round = 1;
		decided = false;

		// wait until all nodes are connected to the network.
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e){
            e.printStackTrace();
        }

		// the do forever loop in the Lecture Nodes
		while(true){
			broadcast('N',round, node.getOwnValue());
			if(node.getAmountNotified(round) > (amountNodes - faultTolerance)){
				List<Integer> notifications = node.getNvalues(round);

				// Count the different values, if large than the half decide otherwise propose i don't know!
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
                // stop if decided what to do in this round
                if(decided) {
                    break;
                } else if(node.getAmountProposed(round) > (amountNodes - faultTolerance)) {
                    List<Integer> proposals = node.getPvalues(round);
                    // Count the different values, if large than the half decide otherwise propose i don't know!
                    countZero = 0;
                    countOne = 0;
                    for (Integer elem : proposals) {
                        if (elem == 1) {
                            countOne++;
                        } else if (elem == 0) {
                            countZero++;
                        }
                    }

                    // send out proposal with either 0 (decide 0), 1(decide 1) or -1 (i don't know).
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
                }
				round++;
			}
		}
	}

	@Override
	public void broadcast(char MsgType, int round, int value) throws RemoteException{
        ByzantineInterface otherByzantine;
        for (int i = 0; i< ipPortList.size(); i++ ){
            try{
                System.setSecurityManager(new RMISecurityManager());
                System.out.println(ipPortList.get(i) + "/byzantine");
                otherByzantine = (ByzantineInterface) Naming.lookup(ipPortList.get(i) +"/byzantine");
                otherByzantine.receive(MsgType  , round, value);
            }catch (Exception e) {
                System.out.println("Broadcast Exception: " + e);
            }
        }
	}

	@Override
	public void receive(char MsgType, int round, int value) throws RemoteException {
		if(MsgType == 'N'){
		    int[] msg = {round, value};
            node.addNValue(msg);
		}
		if(MsgType == 'P'){
            int[] msg = {round, value};
            node.addPValue(msg);
		}
	}

	@Override
	public boolean hasDecided() throws RemoteException{
        return decided;
	}
}
