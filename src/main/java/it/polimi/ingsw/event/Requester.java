package it.polimi.ingsw.event;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.game.EventWrapper;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.trasmitter.EventTransmitter;
import it.polimi.ingsw.network.exceptions.DisconnectedConnection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Requester<S extends Event> {
    private final EventReceiver<EventWrapper<StatusEvent>> receiver;
    private final EventTransmitter transmitter;

    private final EventListener<EventWrapper<StatusEvent>> responseHandler;

    private final Map<Integer, StatusEvent> pendingResponses = new HashMap<>();

    private final Object responseLock;

    private final Set<Integer> activeRequests = new HashSet<>();

    private int requestCounter;
    private final Object counterLock = new Object();

    public Requester(EventTransceiver transceiver, Object responseLock) {
        this.transmitter = transceiver;
        this.receiver = new CastEventReceiver<>(transceiver);
        this.responseLock = responseLock;
        this.responseHandler = this::processResponse;
    }

    public StatusEvent request(S request) {
        int requestId = generateRequestID();

        addRequestToQueue(requestId);
        transmitter.broadcast(new EventWrapper<>(requestId, request));

        return waitForResponse(requestId);
    }

    public void registerListeners() {
        synchronized (responseLock) {
            receiver.registerListener(responseHandler);
        }
    }

    public void unregisterListeners() {
        synchronized (responseLock) {
            receiver.unregisterListener(responseHandler);
            responseLock.notifyAll();
        }
    }

    private int generateRequestID() {
        synchronized (counterLock) {
            return requestCounter++;
        }
    }

    private void addRequestToQueue(int requestId) {
        registerListeners();
        synchronized (responseLock) {
            activeRequests.add(requestId);
        }
    }

    private StatusEvent waitForResponse(int requestId) {
        synchronized (responseLock) {
            while (!pendingResponses.containsKey(requestId)) {
                try {
                    responseLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            unregisterListeners();
            return pendingResponses.remove(requestId);
        }
    }

    private void processResponse(EventWrapper<StatusEvent> responseWrapper) {
        synchronized (responseLock) {
            if (activeRequests.contains(responseWrapper.ID())) {
                activeRequests.remove(responseWrapper.ID());
                pendingResponses.put(responseWrapper.ID(), responseWrapper.event());
                responseLock.notifyAll();
            }
        }
    }

}
