package it.polimi.ingsw.event;

import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.trasmitter.EventTransmitter;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.type.StatusEvent;

import java.util.UUID;
import java.util.function.Function;

public class Responder<R extends Event> {
    private EventTransmitter transmitter;
    private final EventReceiver<R> receiver;

    public Responder(EventTransceiver transceiver) {
        this.transmitter = transceiver;
        this.receiver = new CastEventReceiver<>(transceiver);
    }

    public void registerListener(Function<R, TransmitterEventWrapper> responseHandler) {
        EventListener<R> eventListener = event -> {
            TransmitterEventWrapper wrapper = responseHandler.apply(event);
            StatusEvent response = wrapper.event();
            transmitter = wrapper.transmitter();

            transmitter.send(UUID.fromString(response.getUserID()), response);
        };
        receiver.registerListener(eventListener);
    }

    public void registerListenerStatus(Function<R, StatusEvent> responseHandler) {
        EventListener<R> eventListener = event -> {
            StatusEvent response = responseHandler.apply(event);
            transmitter.send(UUID.fromString(response.getUserID()), response);
        };
        receiver.registerListener(eventListener);
    }
}
