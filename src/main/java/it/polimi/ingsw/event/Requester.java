package it.polimi.ingsw.event;

import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.game.serverToClient.status.Tac;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.trasmitter.EventTransmitter;
import it.polimi.ingsw.utils.Logger;

import java.util.*;

/**
 * A generic requester class that handles event transmission and response processing.
 * This class manages the sending of events and waiting for appropriate responses.
 *
 * @param <S> the type of event that can be sent, must extend Event
 * @author Vittorio Sironi
 */
public class Requester<S extends Event> {
    /** Event receiver for handling Tac type responses */
    private final EventReceiver<Tac> receiverTac;

    /** Event receiver for handling Pota type responses */
    private final EventReceiver<Pota> receiverPota;

    /** Event transmitter for broadcasting requests */
    private final EventTransmitter transmitter;

    /** Queue to store pending status event responses */
    private final Queue<StatusEvent> pendingResponses = new LinkedList<>();

    /** Lock object for synchronizing response handling operations */
    private final Object responseLock;

    /**
     * Constructs a new Requester instance.
     *
     * @param transceiver the event transceiver used for both transmitting and receiving events
     * @param responseLock the lock object used for synchronizing response handling operations
     */
    public Requester(EventTransceiver transceiver, Object responseLock) {
        this.transmitter = transceiver;
        this.receiverTac = new CastEventReceiver<>(transceiver);
        this.receiverPota = new CastEventReceiver<>(transceiver);
        this.responseLock = responseLock;
    }

    /**
     * Sends a request event and waits for a response.
     * This method registers event listeners, broadcasts the request, and blocks until
     * a response is received. The listeners are automatically unregistered after receiving a response.
     *
     * @param request the event to be sent as a request
     * @return the first status event response received
     */
    public StatusEvent request(S request) {
        registerListeners(receiverTac, this::processTac);
        registerListeners(receiverPota, this::processPota);

        transmitter.broadcast(request);

        synchronized (responseLock) {
            while (pendingResponses.isEmpty()) {
                try {
                    responseLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            unregisterListeners(receiverTac, this::processTac);
            unregisterListeners(receiverPota, this::processPota);

            return pendingResponses.poll();
        }
    }

    /**
     * Registers an event listener for a specific type of status event.
     * This method synchronizes on the response lock to ensure thread-safe registration
     * of event listeners.
     *
     * @param <T> the type of status event, must extend StatusEvent
     * @param receiver the event receiver to register the listener with
     * @param responseHandler the event listener to be registered
     */
    public <T extends StatusEvent> void registerListeners(EventReceiver<T> receiver, EventListener<T> responseHandler) {
        synchronized (responseLock) {
            receiver.registerListener(responseHandler);
        }
    }

    /**
     * Unregisters an event listener for a specific type of status event.
     * This method synchronizes on the response lock and notifies all waiting threads
     * after unregistering the listener.
     *
     * @param <T> the type of status event, must extend StatusEvent
     * @param receiver the event receiver to unregister the listener from
     * @param responseHandler the event listener to be unregistered
     */
    public <T extends StatusEvent> void unregisterListeners(EventReceiver<T> receiver, EventListener<T> responseHandler) {
        synchronized (responseLock) {
            receiver.unregisterListener(responseHandler);
            responseLock.notifyAll();
        }
    }

    // We need to create two method, otherwise the compiler will not be able to infer the type of the response
    /**
     * Processes a Tac response event by adding it to the pending responses queue.
     * This method is synchronized to ensure thread-safe access to the shared queue
     * and notifies all waiting threads that a response has been received.
     *
     * @param response the Tac response event to be processed
     */
    private void processTac(Tac response) {
        synchronized (responseLock) {
            pendingResponses.add(response);
            responseLock.notifyAll();
        }
    }

    /**
     * Processes a Pota response event by adding it to the pending responses queue.
     * This method is synchronized to ensure thread-safe access to the shared queue
     * and notifies all waiting threads that a response has been received.
     *
     * @param response the Pota response event to be processed
     */
    private void processPota(Pota response) {
        synchronized (responseLock) {
            pendingResponses.add(response);
            responseLock.notifyAll();
        }
    }

}
