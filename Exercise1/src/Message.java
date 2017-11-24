import java.io.Serializable;
import java.util.List;

/**
 * This class combines the message to send between nodes with the vector clock of the node to send.
 * 
 * @author Anelia Dimitrova (4501667) & Laurens Weijs (4503813)
 * @version 22.11.2017
 */
public class Message implements Serializable{
    String m;
    int[] clock;

    /**
     * Constructor for the message of the process.
     * @param m the message
     * @param clock the latest known vector clock of the process
     */
    Message(String m, int[] clock){
    this.m = m;
    this.clock = clock;
    }

    /**
     * Getter for the content of the message.
     * @return the actual message
     */
    public String getMessage(){
        return m;
    }

    /**
     * Getter for the vector clock given by the process.
     * @return the actual vector clock
     */
    public int[] getVectorClock(){
        return clock;
    }
}
