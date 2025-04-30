package network.messages;

import java.io.Serializable;

public enum MessageType implements Serializable {
    HEARTBEAT,

    // Single user events
    JOIN_LOBBY_SUCCESSFUL,

    // Lobby events
    USER_JOINED_LOBBY,
    USER_LEFT_LOBBY,
    NO_LOBBY_AVAILABLE,


    // Game events
    GAME_START,
    GAME_END,
    USE_ENGINE,
    USE_CANNONS,
    REMOVE_CREW,
    SWAP_GOODS
}
