package Model.State.interfaces;

import Model.Player.PlayerData;

/**
 * This interface is used to manage the lobby of a game.
 * It allows to add and remove players from the game.
 * <p>
 * This interface is implemented by the {@link Model.State.LobbyState} class.
 */
public interface JoinableGame {
    /**
     * Adds/remove a player to the game.
     * @param player The player to add/remove
     * @param type 0 to add the player, 1 to remove the player
     */
    void manageLobby(PlayerData player, int type);
}
