package network.rmi;

import event.eventType.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteQueue extends Remote {
    /**
     * Adds a message to the queue.
     *
     * @param message that represent the event will be added to the queue.
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    void add(Event message) throws RemoteException;

    /**
     * Polls an event that represents a message from the queue (the oldest added message), removing it.
     *
     * @return the most old element in the queue will be removed and returned
     * @throws RemoteException will be thrown in case of network problems, or server communication issues.
     */
    Event poll() throws RemoteException;
}
