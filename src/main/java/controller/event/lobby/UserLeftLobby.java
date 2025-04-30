package controller.event.lobby;

import controller.event.Event;

import java.io.Serializable;
import java.util.UUID;

public record UserLeftLobby(
        UUID userID
) implements Event, Serializable {
}
