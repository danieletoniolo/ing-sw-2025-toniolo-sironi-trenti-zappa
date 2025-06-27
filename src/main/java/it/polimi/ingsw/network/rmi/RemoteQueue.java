package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.event.type.Event;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for a queue that handles Event messages through RMI.
 * This interface extends Remote to allow for distributed communication
 * between client and server components in the network layer.
 * @author Daniele Toniolo
 */
public interface RemoteQueue extends Remote {
    /**
     * Adds a message to the queue.
     *
     * @param message that represent the it.polimi.ingsw.event will be added to the queue.
     * @throws RemoteException will be thrown in case of it.polimi.ingsw.network problems, or server communication issues.
     */
    void add(Event message) throws RemoteException;

    /**
     * Polls an it.polimi.ingsw.event that represents a message from the queue (the oldest added message), removing it.
     *
     * @return the most old element in the queue will be removed and returned
     * @throws RemoteException will be thrown in case of it.polimi.ingsw.network problems, or server communication issues.
     */
    Event poll() throws RemoteException;
}
