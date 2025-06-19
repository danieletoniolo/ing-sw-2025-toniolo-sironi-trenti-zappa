package it.polimi.ingsw.view.tui.screens.gameScreens.engineActions;

import it.polimi.ingsw.view.tui.screens.CardsGame;

import java.util.List;

public abstract class ManagerEnginesCards extends CardsGame {
    protected static List<Integer> enginesIDs;
    protected static List<Integer> batteriesIDs;

    public ManagerEnginesCards(List<String> options) {
        super(options);
    }

    public void destroyStatic() {
        enginesIDs = null;
        batteriesIDs = null;
        spaceShipView = clientPlayer.getShip();
    }
}
