package it.polimi.ingsw.event.internal;

import it.polimi.ingsw.event.EventListener;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.game.lobby.LobbyInfo;

import java.io.Serializable;

public record EndGame(
    LobbyInfo lobby
) implements Event, Serializable {
    public static void registerHandler(EventTransceiver transceiver, EventListener<EndGame> listener) {
        new CastEventReceiver<EndGame>(transceiver).registerListener(listener);
    }
}
