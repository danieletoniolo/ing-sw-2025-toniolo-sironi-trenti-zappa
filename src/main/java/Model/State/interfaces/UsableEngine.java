package Model.State.interfaces;

import Model.Player.PlayerData;

public interface UsableEngine {
    public void useEngine(PlayerData player, Float strength) throws IllegalStateException, IllegalArgumentException;
}
