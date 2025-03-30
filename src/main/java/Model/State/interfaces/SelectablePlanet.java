package Model.State.interfaces;

import Model.Player.PlayerData;

public interface SelectablePlanet {
    void selectPlanet(PlayerData player, int planetNumber) throws IllegalStateException;
}
