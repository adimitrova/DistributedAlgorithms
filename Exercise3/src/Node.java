import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class combines the message to send between nodes with the vector clock of the node to send.
 * 
 * @author Anelia Dimitrova (4501667) & Laurens Weijs (4503813)
 * @version 22.11.2017
 */
public class Node implements Serializable{
	
	public static void main(String[] args) {
		
	}
	
	private String ipPortUpstreamComp; // IP Port combination of the location of its upstream component.
    private int nodeID;
    private List<ArrayList<Integer>> proposalValues;
    private List<ArrayList<Integer>> notificationValues;
    private List<ArrayList<Integer>> decidedPerRound;
    private int ownValue;
    private boolean decided;
    
    /**
     * Constructor for the Node
     * Initialized ownValue to dummy -1 value, initializes decided to false and sets nodeID
     * also initializes the lists
     * @param nodeid
     */
    Node(int nodeid, String ipPort){
    	nodeID = nodeid;
    	ipPortUpstreamComp = ipPort;
    	decided = false;
    	ownValue = -1;
    	proposalValues = new ArrayList<ArrayList<Integer>>();
    	notificationValues = new ArrayList<ArrayList<Integer>>(); 
    	decidedPerRound = new ArrayList<ArrayList<Integer>>();
    }

    /** 
     * round value is added as first and the proposed value is second, in the form of an array
     * addPValue(2,0) will mean that it's round 2 and the value is 0 
     */
    public void addPValue(Integer[] roundAndValueIn) {
    	ArrayList<Integer> innerList = new ArrayList<Integer>();
		innerList.addAll(Arrays.asList(roundAndValueIn));
		proposalValues.add(innerList);
    }

    /** 
     * round value is added as first and the proposed value is second, in the form of an array
     * addPValue(2,0) will mean that it's round 2 and the value is 0 
     */
    public void addNValue(Integer[] roundAndValueIn) {
    	ArrayList<Integer> innerList = new ArrayList<Integer>();
		innerList.addAll(Arrays.asList(roundAndValueIn));
		notificationValues.add(innerList);
    }
    
    public void addDecidedRoundVaue(Integer[] roundAndValueIn) {
    	ArrayList<Integer> innerList = new ArrayList<Integer>();
		innerList.addAll(Arrays.asList(roundAndValueIn));
		decidedPerRound.add(innerList);
    }
    
    public List<Integer> getPvalues(int roundIn){
    	List<Integer> tempList = new ArrayList<Integer>();
    	for (ArrayList<Integer> list : proposalValues) {
			if(list.get(0) == roundIn) {
				tempList.addAll(list);
			}
		}
    	return tempList;
    }
    
    public List<Integer> getNvalues(int roundIn){
    	List<Integer> tempList = new ArrayList<Integer>();
    	for (ArrayList<Integer> list : notificationValues) {
			if(list.get(0) == roundIn) {
				tempList.addAll(list);
			}
		}
    	return tempList;
    }
    
    public List<Integer> getDecidedValues(int roundIn){
    	List<Integer> tempList = new ArrayList<Integer>();
    	for (ArrayList<Integer> list : decidedPerRound) {
			System.out.println(list);
		}
    	return tempList;
    }
    
    public void setOwnValue(int v) {
    	ownValue = v;
    }
    
    public void decide(int round, int w) {
    	decided = true;
    	ownValue = w;
    	Integer[] decision = {round, w};
    	System.out.println("Value decided: " + ownValue + " | round: " + round);
    }
}
