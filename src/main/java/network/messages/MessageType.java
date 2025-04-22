package network.messages;

import controller.event.EventType;
import controller.event.game.RemoveCrew;
import controller.event.game.UseCannons;

import java.io.Serializable;

public enum MessageType implements Serializable {
    // Type of message
    HEARTBEAT,

    // Game events
    GAME_START,
    USE_ENGINE,
    USE_CANNONS,
    REMOVE_CREW
}
