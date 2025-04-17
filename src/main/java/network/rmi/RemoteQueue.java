package network.rmi;

import network.exceptions.DisconnectedConnection;
import network.messages.Message;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

public class RemoteQueue extends UnicastRemoteObject implements IRemoteQueue {
    private final Queue<Message> queue;

    private final Object lock = new Object();

    public RemoteQueue() throws RemoteException {
        queue = new LinkedList<>();
    }

    @Override
    public void add(Message message) throws RemoteException {
        synchronized (lock) {
            queue.add(message);
            lock.notifyAll();
        }
    }

    @Override
    public Message poll() throws RemoteException {
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
