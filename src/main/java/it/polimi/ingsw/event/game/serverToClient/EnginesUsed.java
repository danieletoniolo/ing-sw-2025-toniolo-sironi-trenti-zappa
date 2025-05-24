package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * This it.polimi.ingsw.event is used when the user wants to use the engines.
 * It is used to notify the other players that the user wants to use the engines.
 * @param nickname     The nickname of the user. Only the user know his nickname, so the it.polimi.ingsw.event is not faked.
 * @param enginesIDs   The IDs of the engines to use.
 * @param batteriesIDs The IDs of the batteries to use, in order to reach the power.
 */
public record EnginesUsed(
        String nickname,
        List<Integer> enginesIDs,
        List<Integer> batteriesIDs
) implements Event, Serializable {}
