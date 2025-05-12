package controller.event;

import network.Connection;
import network.exceptions.DisconnectedConnection;
import org.javatuples.Pair;

import java.util.*;

public class ServerNetworkTransceiver implements EventTransceiver{
    /**
     * Lock object used to synchronize listeners registration, removal and event handling.
     */
    private final Object lock;

    /**
     * Map of connections to the clients and the corresponding threads that receive events from them.
     */
    private final Map<Connection, Thread> connections;

    /**
     * It is a queue used to implement a producer/consumer pattern for incoming events.
     * {@link ServerNetworkTransceiver#connect(Connection)} has the producer thread; the consumer thread
     * receives the events from the connection.
     */
    private final Queue<Event> receivedQueue = new ArrayDeque<>();

    /**
     * It is a queue used to implement a producer/consumer pattern for outgoing events.
     * {@link ServerNetworkTransceiver#broadcast(Event)} is the producer; the consumer thread sens
     * the events over the connection.
     */
    private final Queue<Event> sendQueue = new ArrayDeque<>();

    /**
     * It is true if the consumer thread which sends events over the connection has to remain alive.
     */
    private boolean hasToSend = true;

    public ServerNetworkTransceiver(Object lock) {
        this.lock = lock;
        this.connections = new HashMap<>();

        new Thread(() -> {
            Event event;
            while (true) {
                synchronized (receivedQueue) {
                    while (receivedQueue.isEmpty()) {
                        try {
                            receivedQueue.wait();
                        } catch (InterruptedException e) {
                            // Handle interruption
                        }
                    }
                    event = receivedQueue.poll();

                    // TODO: Handle received event
                }
            }
        }).start();

        new Thread(() -> {
            Event event;
            while (true) {
                synchronized (sendQueue) {
                    while (sendQueue.isEmpty()) {
                        if (!hasToSend) {
                            // TODO: Understand if we need to break the loop
                            return;
                        }

                        try {
                            sendQueue.wait();
                        } catch (InterruptedException e) {
                            // Handle interruption
                        }
                    }
                    event = sendQueue.poll();
                }

                try {
                    for (Connection connection : connections.keySet()) {
                        connection.send(event);
                    }
                } catch (DisconnectedConnection e) {
                    return;
                }
            }
        }).start();
    }

    /**
     * Add a new connection to the transceiver and start the threads to receive and send events.
     * @param connection The {@link Connection} to add.
     */
    public void connect(Connection connection) {
        Thread receiveThread = new Thread(() -> {
            Event event;
            try {
                event = connection.receive();
            } catch (DisconnectedConnection e) {
                // Handle disconnection
                return;
            }
            synchronized (receivedQueue) {
                receivedQueue.add(event);
                receivedQueue.notifyAll();
            }
        });

        connections.put(connection, receiveThread);
        receiveThread.start();
    }

    /**
     * Disconnects the given connection from the transceiver. This cause the reception thread to stop,
     * but it doesn't disconnect the connection itself. This is done because we call this method to
     * change the transceiver of a connection, not to disconnect it.
     * @param connection The {@link Connection} to disconnect from the transceiver.
     */
    public void disconnect(Connection connection) {
        Thread receiveThread = connections.remove(connection);
        if (receiveThread != null) {
            receiveThread.interrupt();
        }

        synchronized (sendQueue) {
            hasToSend = false;
            sendQueue.notifyAll();
        }
    }

    @Override
    public void broadcast(Event data) {
        synchronized (sendQueue) {
            sendQueue.add(data);
            sendQueue.notifyAll();
        }
    }
}
