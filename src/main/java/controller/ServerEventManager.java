package controller;

import Model.Game.Lobby.LobbyInfo;
import event.Event;

/**
 * It is used by the states and the gameController to notify to the matchController that an event need to be sent in broadcast
 */
public class ServerEventManager implements EventCallback {
    private final LobbyInfo lobbyInfo;

    public ServerEventManager(LobbyInfo lobbyInfo) {
        this.lobbyInfo = lobbyInfo;
    }

    /**
     * This method is called by the states (as a callback) and the gameController to notify to the matchController that an event need to be sent in broadcast
     * @param event is the event to be sent in broadcast
     */
    @Override
    public void trigger(Event event) {
        MatchController.getInstance().broadcast(lobbyInfo, event);
    }
}
