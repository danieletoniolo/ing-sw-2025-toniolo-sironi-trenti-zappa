package it.polimi.ingsw.event;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.network.Connection;
import it.polimi.ingsw.network.exceptions.DisconnectedConnection;
import it.polimi.ingsw.utils.Logger;
import org.javatuples.Pair;

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
     * Map of ID and connections to the clients with the corresponding threads that receive events from them.
     */
    private final Map<UUID, Pair<Connection, Thread>> connections;

    /**
     * It is the list of listeners registered on the transceiver.
     */
    private final List<EventListener<Event>> listeners = new ArrayList<>();

    /**
     * It is a queue used to implement a producer/consumer pattern for incoming events.
     * {@link NetworkTransceiver#connect(UUID, Connection)} has the producer thread; the consumer thread
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
                            Logger.getInstance().log(Logger.LogLevel.INFO, "Waiting message...", false);
                            receivedQueue.wait();
                        } catch (InterruptedException e) {
                            // Handle interruption
                        }
                    }
                    event = receivedQueue.poll();
                    Logger.getInstance().log(Logger.LogLevel.INFO, "Received message: " + event.getClass().getSimpleName(), false);
                    Logger.getInstance().log(Logger.LogLevel.INFO, "Listeners list: " + listeners.stream().map(EventListener::getClass).map(Class::getSimpleName).reduce("", (a, b) -> a + ", " + b), false);

                    synchronized (lockListeners) {
                        List<EventListener<Event>> listenersCopy = new ArrayList<>(listeners);
                        for (EventListener<Event> listener : listenersCopy) {
                            listener.handle(event);
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
                        for (Pair<Connection, Thread> connection : connections.values()) {
                            connection.getValue0().send(event);
                        }
                    } catch (DisconnectedConnection e) {
                        // ignore the error
                    }
                }
            }
        }).start();
    }

    /**
     * Get the connection associated with the given nickname.
     * @param userID The {@link UUID} of the user.
     * @return The {@link Connection} associated with the nickname.
     */
    public Connection getConnection(UUID userID) {
        return connections.get(userID).getValue0();
    }

    /**
     * Register a listener to the transceiver. The listener will be notified when an event is received.
     * @param listener The {@link EventListener} to register.
     */
    public void registerListener(EventListener<Event> listener) {
        synchronized (lockListeners) {
            listeners.add(listener);
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
    public void connect(UUID userID, Connection connection) {
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

        connections.put(userID, new Pair<>(connection, receiveThread));
        receiveThread.start();
    }

    /**
     * Disconnects the given connection from the transceiver. This cause the reception thread to stop,
     * but it doesn't disconnect the connection itself. This is done because we call this method to
     * change the transceiver of a connection, not to disconnect it.
     * @param userID The {@link UUID} to disconnect from the transceiver.
     */
    public void disconnect(UUID userID) {
        Thread receiveThread = connections.remove(userID).getValue1();
        if (receiveThread != null) {
            receiveThread.interrupt();
        }

        synchronized (sendQueue) {
            hasToSend = false;
            sendQueue.notifyAll();
        }
    }

    /**
     * Broadcasts the given {@link Event} to all the {@link it.polimi.ingsw.network.Connection} present in the transceiver.
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
     * Sends the given {@link Event} to the specified {@link it.polimi.ingsw.network.Connection}.
     * It is used only for the errors message which do not need to be broadcasted.
     * @param uuid is the {@link UUID} of the {@link it.polimi.ingsw.network.Connection} to send the it.polimi.ingsw.event to.
     * @param data is the {@link Event} to send.
     */
    @Override
    public void send(UUID uuid, Event data) {
        synchronized (lockConnectionSend) {
            try {
                connections.get(uuid).getValue0().send(data);
            } catch (DisconnectedConnection e) {
                // ignore the error
            }
        }
    }
}
