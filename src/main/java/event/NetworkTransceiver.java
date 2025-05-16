package event;

import network.Connection;
import network.exceptions.DisconnectedConnection;

import java.util.*;

public class NetworkTransceiver implements EventTransceiver{
    /**
     * Lock object used to synchronize listeners registration, removal and event handling.
     */
    private final Object lockListeners;

    /**
     * Lock object used to synchronize the sending of events over the connection.
     */
    private final Object lockConnectionSend = new Object();

    /**
     * Map of connections to the clients and the corresponding threads that receive events from them.
     */
    private final Map<Connection, Thread> connections;

    /**
     * It is the list of listeners registered on the transceiver.
     */
    private final List<EventListener<Event>> listeners = new ArrayList<>();

    /**
     * It is a queue used to implement a producer/consumer pattern for incoming events.
     * {@link NetworkTransceiver#connect(Connection)} has the producer thread; the consumer thread
     * receives the events from the connection.
     */
    private final Queue<Event> receivedQueue = new ArrayDeque<>();

    /**
     * It is a queue used to implement a producer/consumer pattern for outgoing events.
     * {@link NetworkTransceiver#broadcast(Event)} is the producer; the consumer thread sens
     * the events over the connection.
     */
    private final Queue<Event> sendQueue = new ArrayDeque<>();

    /**
     * It is true if the consumer thread which sends events over the connection has to remain alive.
     */
    private boolean hasToSend = true;

    public NetworkTransceiver() {
        this.lockListeners = new Object();
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

                    synchronized (lockListeners) {
                        List<EventListener<Event>> listenersCopy = new ArrayList<>(listeners);
                        for (EventListener<Event> listener : listenersCopy) {
                            try {
                                listener.handle(event);
                            } catch (ClassCastException e) {
                                // ignore the event of the wrong type
                            }
                        }
                    }
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

                synchronized (lockConnectionSend) {
                    try {
                        for (Connection connection : connections.keySet()) {
                            connection.send(event);
                        }
                    } catch (DisconnectedConnection e) {
                        // ignore the error
                    }
                }
            }
        }).start();
    }

    /**
     * Register a listener to the transceiver. The listener will be notified when an event is received.
     * @param listener The {@link EventListener} to register.
     */
    public <T extends Event> void registerListener(EventListener<T> listener) {
        synchronized (lockListeners) {
            listeners.add(data -> {
                try {
                    listener.handle((T) data);
                } catch (ClassCastException e) {
                    throw new IllegalStateException("The Class cannot be cast to the expected type");
                }
            });
        }
    }

    /**
     * Unregister a listener from the transceiver. The listener will no longer be notified when an event is received.
     * @param listener The {@link EventListener} to unregister.
     */
    public void unregisterListener(EventListener<Event> listener) {
        synchronized (lockListeners) {
            listeners.remove(listener);
        }
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

    /**
     * Broadcasts the given {@link Event} to all the {@link network.Connection} present in the transceiver.
     * @param data is the {@link Event} which will be broadcast.
     */
    @Override
    public void broadcast(Event data) {
        synchronized (sendQueue) {
            sendQueue.add(data);
            sendQueue.notifyAll();
        }
    }

    /**
     * Sends the given {@link Event} to the specified {@link network.Connection}.
     * It is used only for the errors message which do not need to be broadcasted.
     * @param connection is the {@link network.Connection} to which the event will be sent.
     * @param data is the {@link Event} to send.
     */
    @Override
    public void send(Connection connection, Event data) {
        synchronized (lockConnectionSend) {
            try {
                connection.send(data);
            } catch (DisconnectedConnection e) {
                // ignore the error
            }
        }
    }
}
