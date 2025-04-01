package Model.State.interfaces;

import Model.Player.PlayerData;

public interface UsableCannon {
    /**
     * Use the cannon with a given strength
     * @param player PlayerData of the player using the cannon
     * @param strength Strength of the cannon to be used
     */
    void useCannon(PlayerData player, Float strength) throws IllegalStateException;
}
