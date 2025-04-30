package controller.event.lobby;

import controller.event.Event;
import network.User;

import java.io.Serializable;

public record UserJoinedLobby(
        User user
) implements Event, Serializable {
}
