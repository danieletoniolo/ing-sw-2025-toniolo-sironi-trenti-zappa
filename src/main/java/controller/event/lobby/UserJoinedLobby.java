package controller.event.lobby;

import controller.event.Event;
import network.User;

import java.io.Serializable;
import java.util.UUID;

public record UserJoinedLobby(
        UUID userID
) implements Event, Serializable {
}
