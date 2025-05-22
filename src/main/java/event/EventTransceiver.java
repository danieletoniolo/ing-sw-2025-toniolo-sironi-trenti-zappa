package event;

import event.eventType.Event;
import event.receiver.EventReceiver;
import event.trasmitter.EventTransmitter;

public interface EventTransceiver extends EventTransmitter, EventReceiver<Event> {
}
