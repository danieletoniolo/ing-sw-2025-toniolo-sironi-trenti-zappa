package event.game.serverToClient;

import event.EventTransceiver;
import event.Responder;
import event.eventType.Event;

import java.io.Serializable;
import java.util.function.Function;

/**
 * This event is used when a player uses a shield.
 * @param nickname  is the nickname of the player who used the shield.
 * @param batteryID is the ID of the battery used to use the shield.
 */
public record ShieldUsed(
        String nickname,
        int batteryID
) implements Event, Serializable {}
