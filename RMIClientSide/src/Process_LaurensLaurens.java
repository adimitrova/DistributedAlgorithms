import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 
 * Representing Processes and their local clocks
 * 
 * @author Laurens Weijs & Anelia Dimitrova
 * @version 16.11.2017
 */
public class Process_LaurensLaurens extends UnicastRemoteObject implements ProcessInterface_Laurens {
    private List<Integer> vectorClock;
    //	private List<String> otherClientRegistries;
    Set<message> buffer = new HashSet<message>();
    int nonce = 1;

    public Process_LaurensLaurens(int amountProcesses, iplist) throws RemoteException {
        // initialize an empty clock, based on the amount of process in the whole system
        vectorClock = new ArrayList<Integer>(amountProcesses);
    }

    @Override
    public void broadcast(message m) throws RemoteException {
        // TODO: in discussion how to know who all the other processes are.
    }

    @Override
    public void receive(message m) throws RemoteException {
        if (checkVectorClocks(m)) {
            deliver(m);
            while (!buffer.isEmpty()) {
                for (message elem : buffer) {
                    if (checkVectorClocks(elem)) {
                        deliver(m);
                    }
                }
            }
        } else {
            buffer.add(m);
        }
    }

    @Override
    public void deliver(message m) throws RemoteException {
        // update its own vectorclock
        updateVectorClock(m);

        // if message in buffer, remove it.
        buffer.remove(m);

        System.out.println(m.getMessage());
    }

    /**
     *
     * @param m
     * @return
     */
    private boolean checkVectorClocks(message m) {
        int length = m.getVectorClock().size();
        boolean deliverable = true;
        for (int i = 0; i < length; i++) {
            if (!(vectorClock.get(i) + nonce > m.getVectorClock().get(i))) {
                deliverable = false;
                break;
            }
        }
        return deliverable;
    }

    private void updateVectorClock(message m) {
        int length = m.getVectorClock().size();
        for (int i = 0; i < length; i++) {
            // This should actually only update the clock of the process it received the message from.
            if ((vectorClock.get(i) < m.getVectorClock().get(i))) {
                vectorClock.set(i, m.getVectorClock().get(i));
            }
        }
    }
}