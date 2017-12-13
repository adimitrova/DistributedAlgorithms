import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Byzantine extends UnicastRemoteObject implements ByzantineInterface{
	int round;	// round
//	int nodesParticipating;
	public static List<String> ipPortList; // global ip and port list of all processes
    public static List<Node> nodeList;
    public int faultTolerance = 0;        // faulty processes we allow
	public boolean decided;
	public Node node;
	public int amountNodes;

	protected Byzantine(int id, List<String> ipPorts, int n, int f) throws RemoteException {
		amountNodes = n;
		faultTolerance = f;
		node = new Node(id, ipPorts);
		round = 1;
		decided = false;

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

                if(decided) {
                    break;
                }



				round++;
			}
		}
	}

	@Override
	public void broadcast(char MsgType, int round, int value) throws RemoteException{
		if(MsgType == 'N'){

		}
		if(MsgType == 'P'){

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
	public void decide() throws RemoteException{
		
	}
}
