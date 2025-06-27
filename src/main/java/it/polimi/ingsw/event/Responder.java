package it.polimi.ingsw.event;

import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.trasmitter.EventTransmitter;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.type.StatusEvent;

import java.util.UUID;
import java.util.function.Function;

/**
 * A responder that handles incoming events and sends responses through a transmitter.
 *
 * @param <R> the type of event this responder handles, must extend Event
 * @author Vittorio Sironi
 */
public class Responder<R extends Event> {
    /** The transmitter used to send response events */
    private EventTransmitter transmitter;
    /** The receiver that listens for incoming events */
    private final EventReceiver<R> receiver;

    /**
     * Constructs a new Responder with the given transceiver.
     *
     * @param transceiver the event transceiver that acts as both transmitter and receiver
     */
    public Responder(EventTransceiver transceiver) {
        this.transmitter = transceiver;
        this.receiver = new CastEventReceiver<>(transceiver);
    }

    /**
     * Registers a listener that handles events and allows dynamic transmitter selection.
     * The response handler can specify both the response event and the transmitter to use.
     *
     * @param responseHandler function that takes an event and returns a TransmitterEventWrapper
     *                       containing both the response event and the transmitter to use
     */
    public void registerListener(Function<R, TransmitterEventWrapper> responseHandler) {
        EventListener<R> eventListener = event -> {
            TransmitterEventWrapper wrapper = responseHandler.apply(event);
            StatusEvent response = wrapper.event();
            transmitter = wrapper.transmitter();

            transmitter.send(UUID.fromString(response.getUserID()), response);
        };
        receiver.registerListener(eventListener);
    }

    /**
     * Registers a listener that handles events and sends responses using the default transmitter.
     * The response handler should return a StatusEvent that will be sent to the user specified in the event.
     *
     * @param responseHandler function that takes an event and returns a StatusEvent response
     */
    public void registerListenerStatus(Function<R, StatusEvent> responseHandler) {
        EventListener<R> eventListener = event -> {
            StatusEvent response = responseHandler.apply(event);
            transmitter.send(UUID.fromString(response.getUserID()), response);
        };
        receiver.registerListener(eventListener);
    }
}
