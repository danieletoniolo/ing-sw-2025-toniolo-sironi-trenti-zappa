package controller.event.lobby;

import Model.Game.Lobby.LobbyInfo;
import controller.event.Event;

import java.io.Serializable;

public record JoinLobbySuccessful(
        LobbyInfo lobby
) implements Event, Serializable {
}
