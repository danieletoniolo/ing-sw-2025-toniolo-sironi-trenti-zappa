package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.type.Event;

import java.util.UUID;

/**
 * It is used by the states and the gameController to notify to the matchController that an it.polimi.ingsw.event need to be sent in broadcast
 */
public interface EventCallback {
    /**
     * This method is called by the states (as a callback) and the gameController to notify to the matchController that an it.polimi.ingsw.event need to be sent in broadcast
     * @param event is the it.polimi.ingsw.event to be sent in broadcast
     */
    void trigger(Event event);

    /**
     * This method is called by the states (as a callback) and the gameController to notify to the matchController the player to which it will be sent the event.
     * @param event        is the it.polimi.ingsw.event to be sent in broadcast
     * @param targetUser is the userID of the player to which the event will be sent
     */
    void trigger(Event event, UUID targetUser);
}
