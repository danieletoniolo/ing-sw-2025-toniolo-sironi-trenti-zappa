package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.network.exceptions.DisconnectedConnection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

public class RemoteLinkedList extends UnicastRemoteObject implements RemoteQueue {
    /**
     * The internal queue that stores Event objects.
     * This queue follows FIFO (First In, First Out) ordering and is used
     * to manage the sequence of events in a thread-safe manner.
     */
    private final Queue<Event> queue;

    /**
     * Lock object used to synchronize access to the queue.
     * Ensures thread-safe operations by coordinating threads
     * that add or poll messages from the queue.
     */
    private final Object lock = new Object();

    /**
     * Class constructor.
     *
     * @throws RemoteException will be thrown in case of it.polimi.ingsw.network problems, or server communication issues.
     */
    public RemoteLinkedList() throws RemoteException {
        queue = new LinkedList<>();
    }

    /**
     * Adds a message to the queue.
     *
     * @param message will be added to the queue.
     * @throws RemoteException will be thrown in case of it.polimi.ingsw.network problems, or server communication issues.
     */
    @Override
    public void add(Event message) throws RemoteException {
        synchronized (lock) {
            queue.add(message);
            lock.notifyAll();
        }
    }

    /**
     * Polls a message from the queue (the oldest added message), removing it.
     *
     * @return the most old element in the queue will be removed and returned
     * @throws RemoteException will be thrown in case of it.polimi.ingsw.network problems, or server communication issues.
     */
    @Override
    public Event poll() throws RemoteException {
        synchronized (lock) {
            while (queue.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new DisconnectedConnection("Connection close while polling message", e);
                }
            }
            return queue.poll();
        }
    }
}
