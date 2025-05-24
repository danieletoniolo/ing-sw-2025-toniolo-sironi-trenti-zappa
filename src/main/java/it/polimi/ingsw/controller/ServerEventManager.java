package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.game.lobby.LobbyInfo;
import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * It is used by the states and the gameController to notify to the matchController that an it.polimi.ingsw.event need to be sent in broadcast
 */
public class ServerEventManager implements EventCallback, Serializable {
    private final LobbyInfo lobbyInfo;

    public ServerEventManager(LobbyInfo lobbyInfo) {
        this.lobbyInfo = lobbyInfo;
    }

    /**
     * This method is called by the states (as a callback) and the gameController to notify to the matchController that an it.polimi.ingsw.event need to be sent in broadcast
     * @param event is the it.polimi.ingsw.event to be sent in broadcast
     */
    @Override
    public void trigger(Event event) {
        try {
            MatchController.getInstance().getNetworkTransceiver(lobbyInfo).broadcast(event);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot get the it.polimi.ingsw.network transceiver for the lobby of: " + lobbyInfo.getFounderNickname());
        }
    }
}
