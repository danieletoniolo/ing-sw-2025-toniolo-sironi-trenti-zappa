package it.polimi.ingsw.event.game.serverToClient;

import it.polimi.ingsw.event.type.Event;

import java.io.Serializable;
import java.util.List;

/**
 * This record represents an event where a player has picked an engine component from the board.
 * It is used to notify or track the details about the action of selecting an engine tile,
 * including information about the player and the attributes of the selected engine.
 *
 * @param nickname       The username or identifier of the player who picked the engine.
 * @param tileID         The unique identifier of the engine tile selected by the player.
 * @param connectors     A list of integers representing the connectors type associated with the selected engine.
 * @param cannonStrength The cannon strength associated with the selected engine.
 */
public record PickedEngineFromBoard(
        String nickname,
        int tileID,
        List<Integer> connectors,
        int cannonStrength
) implements Event, Serializable {
}
