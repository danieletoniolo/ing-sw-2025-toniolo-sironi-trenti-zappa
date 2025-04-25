package Model.State.interfaces;

import Model.Player.PlayerData;

/**
 * This interface represents the methods that are used to join and leave a lobby.
 * The method {@link #joinGame(PlayerData)} is used to add a player to the game, and the method
 * {@link #leaveGame(PlayerData)} is used to remove a player from the game.
 * <p>
 * This interface is implemented by the {@link Model.State.LobbyState} class.
 */
public interface JoinableGame {
    /**
     * This method is used to add a player to the game. The player is added to the list of players contained
     * in the {@link Model.Game.Board.Board} object.
     * @param player the player to add to the game.
     */
    void joinGame(PlayerData player);

    /**
     * This method is used to remove a player from the game. The player is removed from the list of players contained
     * in the {@link Model.Game.Board.Board} object.
     * @param player the player to remove from the game.
     */
    void leaveGame(PlayerData player);
}
