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

// TODO: disconnectListener
public class Requester<S extends Event> {
    private final EventReceiver<EventWrapper<StatusEvent>> receiver;
    private final EventTransmitter transmitter;

    private final EventListener<EventWrapper<StatusEvent>> responseHandler;
    //private final EventListener<EventWrapper> disconnectionHandler;

    //private final EventListener<PlayerDisconnectedInternalEventData> disconnectedListener;

    private final Map<Integer, StatusEvent> pendingResponses = new HashMap<>();

    private final Object responseLock;

    private final Set<Integer> activeRequests = new HashSet<>();

    private int requestCounter;
    private final Object counterLock = new Object();

    private boolean isDisconnected;

    public Requester(EventTransceiver transceiver, Object responseLock) {
        this.transmitter = transceiver;
        this.receiver = new CastEventReceiver<>(transceiver);
        this.responseLock = responseLock;
        this.responseHandler = this::processResponse;

        /*
        this.disconnectListener = data -> {
            synchronized (responsesLock) {
                disconnected = true;
                this.responsesLock.notifyAll();
            }
        };
        */
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
            //transceiver.registerListener(disconnectListener);
            isDisconnected = false;
        }
    }

    public void unregisterListeners() {
        synchronized (responseLock) {
            receiver.unregisterListener(responseHandler);
            //transceiver.unregisterListener(disconnectedListener);
            isDisconnected = true;
            responseLock.notifyAll();
        }
    }

    private int generateRequestID() {
        synchronized (counterLock) {
            return requestCounter++;
        }
    }

    private void addRequestToQueue(int requestId) {
        synchronized (responseLock) {
            activeRequests.add(requestId);
        }
    }

    private StatusEvent waitForResponse(int requestId) {
        synchronized (responseLock) {
            while (!pendingResponses.containsKey(requestId)) {
                if (isDisconnected) {
                    throw new DisconnectedConnection();
                }

                try {
                    responseLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
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
