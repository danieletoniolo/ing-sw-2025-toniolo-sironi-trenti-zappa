package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Event to set the nickname of a user.
 * The nickname is used to identify the user in the lobby.
 *
 * @param nickname The nickname of the user
 */
public record NicknameSet(
        String nickname
)  implements Event, Serializable {}
