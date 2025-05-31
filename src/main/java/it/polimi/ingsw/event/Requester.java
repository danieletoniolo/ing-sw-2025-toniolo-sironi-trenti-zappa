package it.polimi.ingsw.event;

import it.polimi.ingsw.event.game.serverToClient.status.Pota;
import it.polimi.ingsw.event.game.serverToClient.status.Tac;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.type.StatusEvent;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.trasmitter.EventTransmitter;

import java.util.*;

public class Requester<S extends Event> {
    private final EventReceiver<Tac> receiverTac;
    private final EventReceiver<Pota> receiverPota;
    private final EventTransmitter transmitter;

    private final Queue<StatusEvent> pendingResponses = new LinkedList<>();

    private final Object responseLock;

    public Requester(EventTransceiver transceiver, Object responseLock) {
        this.transmitter = transceiver;
        this.receiverTac = new CastEventReceiver<>(transceiver);
        this.receiverPota = new CastEventReceiver<>(transceiver);
        this.responseLock = responseLock;
    }

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

    public <T extends StatusEvent> void registerListeners(EventReceiver<T> receiver, EventListener<T> responseHandler) {
        synchronized (responseLock) {
            receiver.registerListener(responseHandler);
        }
    }

    public <T extends StatusEvent> void unregisterListeners(EventReceiver<T> receiver, EventListener<T> responseHandler) {
        synchronized (responseLock) {
            receiver.unregisterListener(responseHandler);
            responseLock.notifyAll();
        }
    }

    // We need to create two method, otherwise the compiler will not be able to infer the type of the response
    private void processTac(Tac response) {
        synchronized (responseLock) {
            pendingResponses.add(response);
            responseLock.notifyAll();
        }
    }

    private void processPota(Pota response) {
        synchronized (responseLock) {
            pendingResponses.add(response);
            responseLock.notifyAll();
        }
    }

}
