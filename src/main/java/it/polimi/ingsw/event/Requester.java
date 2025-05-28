package it.polimi.ingsw.event;

import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.trasmitter.EventTransmitter;
import it.polimi.ingsw.utils.Logger;

import java.util.*;

public class Requester<S extends Event> {
    private final EventReceiver<StatusEvent> receiver;
    private final EventTransmitter transmitter;

    private final EventListener<StatusEvent> responseHandler;

    private final Queue<StatusEvent> pendingResponses = new LinkedList<>();

    private final Object responseLock;

    public Requester(EventTransceiver transceiver, Object responseLock) {
        this.transmitter = transceiver;
        this.receiver = new CastEventReceiver<>(transceiver);
        this.responseLock = responseLock;
        this.responseHandler = this::processResponse;
    }

    public StatusEvent request(S request) {
        registerListeners();
        Logger.getInstance().log(Logger.LogLevel.INFO, "Sending request: " + request, false);
        transmitter.broadcast(request);

        synchronized (responseLock) {
            while (pendingResponses.isEmpty()) {
                try {
                    Logger.getInstance().log(Logger.LogLevel.INFO, "Waiting for response...", false);
                    responseLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            unregisterListeners();
            Logger.getInstance().log(Logger.LogLevel.INFO, "Received response: " + pendingResponses.peek(), false);
            return pendingResponses.poll();
        }
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

    private void processResponse(StatusEvent response) {
        synchronized (responseLock) {
            pendingResponses.add(response);
            responseLock.notifyAll();
        }
    }

}
