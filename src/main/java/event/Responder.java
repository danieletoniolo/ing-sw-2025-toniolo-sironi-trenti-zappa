package event;

import event.eventType.Event;
import event.game.EventWrapper;
import event.receiver.CastEventReceiver;

import java.util.function.Function;

public class Responder<R extends Event, S extends Event> {
    public Responder(EventTransceiver transceiver, Function<R, S> response) {
        new CastEventReceiver<EventWrapper<R>>(transceiver).registerListener(data -> {
            transceiver.broadcast(new EventWrapper<S>(data.ID(), response.apply(data.event())));
        });
    }
}
