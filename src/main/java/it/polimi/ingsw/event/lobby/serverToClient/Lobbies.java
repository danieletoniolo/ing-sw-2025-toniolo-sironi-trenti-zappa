package it.polimi.ingsw.event.lobby.serverToClient;

import it.polimi.ingsw.event.type.Event;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents the lobbies of the game. It is called by the LobbyState
 * @param lobbiesNames   a list of the names of the lobbies
 * @param lobbiesPlayers a list of pairs containing the current number of players and the maximum number of players of each lobby
 */
public record Lobbies(
        List<String> lobbiesNames,
        List<Pair<Integer, Integer>> lobbiesPlayers
) implements Event, Serializable {
}
