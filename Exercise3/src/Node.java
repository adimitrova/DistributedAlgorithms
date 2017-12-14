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

	private List<String> ipPorts; // IP Port combination of the location of its upstream component.
    private int nodeID;
    private List<ArrayList<Integer>> proposalValues;
    private List<ArrayList<Integer>> notificationValues;
    private List<ArrayList<Integer>> decidedPerRound;
    private int ownValue = -1;
    
    /**
     * Constructor for the Node
     * Initialized ownValue to dummy -1 value, initializes decided to false and sets nodeID
     * also initializes the lists
     * @param nodeid
     */
    Node(int nodeid, List<String> ipPorts){
    	nodeID = nodeid;
        this.ipPorts = ipPorts;
    	proposalValues = new ArrayList<ArrayList<Integer>>();
    	notificationValues = new ArrayList<ArrayList<Integer>>(); 
    	decidedPerRound = new ArrayList<ArrayList<Integer>>();
    }

    /** 
     * round value is added as first and the proposed value is second, in the form of an array
     * addPValue(2,0) will mean that it's round 2 and the value is 0
     * the index of the inner list indicates the round number and we access it that way
     */
    public void addPValue(int[] roundAndValueIn) {
        int r = roundAndValueIn[0];
        int v = roundAndValueIn[1];
        // check whether the amount of round already encountered is like expectation,
        // otherwise grow the size of its own Database of Proposals with the according amount of rounds.
        int diff = proposalValues.size() - r;
        if(diff > 0) {
            for (int i = 0; i < diff; i++) {
                proposalValues.add(new ArrayList<Integer>());
            }
        }
        // Add the value of the proposal to its own list of proposals per round
        proposalValues.get(r-1).add(v);
    }

    /** 
     * round value is added as first and the proposed value is second, in the form of an array
     * addPValue(2,0) will mean that it's round 2 and the value is 0 
     */
    public void addNValue(int[] roundAndValueIn) {
        // documentation see addPValue
        int r = roundAndValueIn[0];
        int v = roundAndValueIn[1];
        int diff = notificationValues.size() - r;
        if(diff > 0) {
            for (int i = 0; i < diff; i++) {
                notificationValues.add(new ArrayList<Integer>());
            }
        }
        notificationValues.get(r-1).add(v);
    }
    
    public List<Integer> getPvalues(int roundIn){
    	return proposalValues.get(roundIn);
    }
    
    public List<Integer> getNvalues(int roundIn){
        return notificationValues.get(roundIn);
    }

    public void setOwnValue(int v) {
    	ownValue = v;
    }

    public int getOwnValue(){
        return ownValue;
    }

    public int getAmountNotified(int round){
        if(notificationValues.size() < round){
            return notificationValues.get(round-1).size();
        }
        return 0;
    }

    public int getAmountProposed(int round){
        if(proposalValues.size() < round){
            return proposalValues.get(round-1).size();
        }
        return 0;
    }
}
