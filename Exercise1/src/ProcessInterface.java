import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the process in order for the client what it can expect from the process.
 */
public interface ProcessInterface extends Remote {
    /**
     * broadcast the message to all the other processes currently in the system.
     * @param m the message to broadcast
     * @throws RemoteException
     */
    public void broadcast(Message m) throws RemoteException;

    /**
     * Receiving of a message from another process, first is checked whether the message is received
     * in the expected order to achieve the HB relationship. If this is not the cause it will be put in
     * the buffer.
     * @param m the message to receive
     * @param vClockIn
     * @throws RemoteException
     */
    public void receive(Message m, int vClockIn, int processID) throws RemoteException;

    /**
     * The message is actually deliverd because it was in the right order.
     * @param m the message to deliver
     * @throws RemoteException
     */
    public void deliver(Message m) throws RemoteException;
}