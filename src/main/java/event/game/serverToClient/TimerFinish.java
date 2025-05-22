package event.game.serverToClient;

import event.eventType.Event;

import java.io.Serializable;

public record TimerFinish() implements Event, Serializable {
}
