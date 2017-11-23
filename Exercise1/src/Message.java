import java.util.List;

/**
 * This class combines the message to send between nodes with the vector clock of the node to send.
 */
public class Message {
    String m;
    List<Integer> clock;

    /**
     * Constructor for the message of the process.
     * @param m the message
     * @param clock the latest known vector clock of the process
     */
    Message(String m, List<Integer> clock){
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
    public List<Integer> getVectorClock(){
        return clock;
    }
}
