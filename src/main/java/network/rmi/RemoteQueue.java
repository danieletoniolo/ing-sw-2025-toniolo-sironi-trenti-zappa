package network.rmi;

import network.messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteQueue extends Remote {
    /**
     * Adds a message to the queue.
     *
     * @param message will be added to the queue.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    void add(Message message) throws RemoteException;

    /**
     * Polls a message from the queue (the oldest added message), removing it.
     *
     * @return the most old element in the queue will be removed and returned
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    Message poll() throws RemoteException;
}
