import java.util.List;

public class message {
    String m;
    List<Integer> clock;

    message(String m, List<Integer> clock){
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
