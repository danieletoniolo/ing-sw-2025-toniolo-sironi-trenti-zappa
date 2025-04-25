package Model.State.interfaces;

import Model.Player.PlayerData;

import java.util.UUID;

public interface Buildable {
    /**
     * Flips the timer of the game.
     * @param uuid UUID of the player who is flipping the timer.
     * @throws InterruptedException if the thread is interrupted while waiting for the timer to finish.
     * @throws IllegalStateException if the condition to flip the timer is not met. Reason for this could be:
     * we are in the learning level and the timer cannot be flipped, the timer is already running, or we are
     * in the last flip and the player has not finished building.
     */
    void flipTimer(UUID uuid) throws InterruptedException, IllegalStateException;

    void showDeck(UUID uuid, int deckIndex);

    void leaveDeck(UUID uuid, int deckIndex);

    void placeMarker(UUID uuid, int position) throws IllegalStateException;

    void pickTileFromBoard(UUID uuid, int tileID);

    void pickTileFromReserve(UUID uuid, int tileID);

    void pickTileFromSpaceShip(UUID uuid, int tileID);

    void leaveTile(UUID uuid);

    void placeTile(UUID uuid, int row, int col);

    void reserveTile(UUID uuid);

    void rotateTile(UUID uuid);

    void pickTile(PlayerData player, int tileID);
}
