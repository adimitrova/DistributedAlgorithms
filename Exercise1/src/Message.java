import java.util.List;

public class Message {
    String m;
    List<Integer> clock;

    Message(String m, List<Integer> clock){
    this.m = m;
    this.clock = clock;
    }

    public String getMessage(){
        return m;
    }

    public List<Integer> getVectorClock(){
        return clock;
    }
}
