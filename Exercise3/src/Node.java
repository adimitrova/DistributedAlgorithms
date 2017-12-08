import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class combines the information of a node in the system in order to reach consensus.
 * 
 * @author Anelia Dimitrova (4501667) & Laurens Weijs (4503813)
 * @version 08.12.2017
 */
public class Node implements Serializable{
    int nodeID;
    int ownValue;
    boolean decided;
    List<List<Integer>> proposalValues = new ArrayList<List<Integer>>();
    List<List<Integer>> notificationValues = new ArrayList<List<Integer>>();

    /**
     * Constructor for the node in the complete network
     * @param nodeID the ID of the luitanent
     */
    Node(int nodeID){
        this.nodeID = nodeID;
    }

    public void addPvalue(int r){
        if(proposalValues.size() < r){

        }
    }
}
