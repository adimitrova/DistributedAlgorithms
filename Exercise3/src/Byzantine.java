import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Byzantine extends UnicastRemoteObject implements ByzantineInterface{
	int round;	// round
	int nodesParticipating;
	public static List<String> ipPortList; // global ip and port list of all processes
    public static List<Node> nodeList;
    public int faultTolerance = 0;        // faulty processes we allow
	
	protected Byzantine() throws RemoteException {
		faultTolerance = nodeList.size()/5;
		round = 1;
	}
	
	public static void addNode(int ID, String ipPortString) {
		Node node = new Node(ID, ipPortString);
		nodeList.add(node);
	}
	
	public void setNextRound() {
		round++;
	}
	
	public void algorithm(int v) {
		System.out.println("Byzantine agreement started..");
		int nodesParticipating = nodeList.size();
		int traitors = faultTolerance;
		
		boolean flag = true;
		while(flag) {
			try {
				broadcast('N', round, v);
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void broadcast(char MsgType, int round, int value) throws RemoteException{
		
	}

	@Override
	public void decide() throws RemoteException{
		
	}
}
