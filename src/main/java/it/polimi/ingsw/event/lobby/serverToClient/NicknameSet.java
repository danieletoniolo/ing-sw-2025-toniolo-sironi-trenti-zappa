package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;

/**
 * Event to set the userID of a user.
 * The userID is used to identify the user in the lobby.
 *
 * @param nickname The userID of the user
 */
public record NicknameSet(
        String nickname
)  implements Event, Serializable {}
