package it.polimi.ingsw.event.internal;

import it.polimi.ingsw.event.EventListener;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.type.Event;
import it.polimi.ingsw.model.game.lobby.LobbyInfo;

import java.io.Serializable;

/**
 * Event that represents the end of a game.
 * This event is fired when a game session concludes and contains
 * information about the lobby that hosted the game.
 *
 * @param lobby the lobby information associated with the ended game
 * @author Vittorio Sironi
 */
public record EndGame(
    LobbyInfo lobby
) implements Event, Serializable {
    /**
     * Registers an event handler for EndGame events.
     * Creates a new CastEventReceiver to handle the event registration
     * and binds the provided listener to EndGame events.
     *
     * @param transceiver the event transceiver to register the handler with
     * @param listener the event listener that will handle EndGame events
     */
    public static void registerHandler(EventTransceiver transceiver, EventListener<EndGame> listener) {
        new CastEventReceiver<EndGame>(transceiver).registerListener(listener);
    }
}
