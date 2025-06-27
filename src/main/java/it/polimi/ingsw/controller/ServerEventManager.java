package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.internal.EndGame;
import it.polimi.ingsw.model.game.lobby.LobbyInfo;
import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.UUID;

/**
 * It is used by the states and the gameController to notify to the matchController that an it.polimi.ingsw.event need to be sent in broadcast
 * @author Vittorio Sironi
 */
public class ServerEventManager implements EventCallback, Serializable {
    /** The lobby information associated with this event manager */
    private final LobbyInfo lobbyInfo;

    /**
     * Constructs a new ServerEventManager with the specified lobby information.
     * @param lobbyInfo the lobby information to associate with this event manager
     */
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

    /**
     * This method is called by the states (as a callback) and the gameController to notify to the matchController the player to which it will be sent the event.
     * @param event        is the it.polimi.ingsw.event to be sent in broadcast
     * @param targetUser is the userID of the player to which the event will be sent
     */
    @Override
    public void trigger(Event event, UUID targetUser) {
        try {
            MatchController.getInstance().getNetworkTransceiver(lobbyInfo).send(targetUser, event);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot get the it.polimi.ingsw.network transceiver for the lobby of: " + lobbyInfo.getFounderNickname());
        }
    }

    /**
     * This method is called by the states (as a callback) and the gameController to notify to the matchController that the game has ended.
     */
    @Override
    public void triggerEndGame() {
        try {
            MatchController.getInstance().getNetworkTransceiver(lobbyInfo).sendInternalEvent(new EndGame(lobbyInfo));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot get the it.polimi.ingsw.network transceiver for the lobby of: " + lobbyInfo.getFounderNickname());
        }
    }
}
