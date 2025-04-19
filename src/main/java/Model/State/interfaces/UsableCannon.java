package Model.State.interfaces;

import Model.Player.PlayerData;

import java.util.List;

public interface UsableCannon {
    /**
     * Use the cannon with a given strength
     * @param player PlayerData of the player using the cannon
     * @param strength Strength of the cannon to be used
     * @param batteriesID List of Integers representing the batteryID from which
     * we take the energy to use the cannon
     */
    void useCannon(PlayerData player, Float strength, List<Integer> batteriesID) throws IllegalStateException;
}
