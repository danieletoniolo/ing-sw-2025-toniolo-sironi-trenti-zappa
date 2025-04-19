package Model.State.interfaces;

import Model.Player.PlayerData;

import java.util.List;

public interface UsableEngine {
    public void useEngine(PlayerData player, Float strength, List<Integer> batteriesID) throws IllegalStateException, IllegalArgumentException;
}
