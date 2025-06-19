package it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions;

import it.polimi.ingsw.view.tui.screens.CardsGame;

import java.util.List;

public abstract class MangerCannonsCards extends CardsGame {
    protected static List<Integer> cannonsIDs;
    protected static List<Integer> batteriesIDs;

    public MangerCannonsCards(List<String> options) {
        super(options);
    }

    public void destroyStatic() {
        cannonsIDs = null;
        batteriesIDs = null;
        spaceShipView = clientPlayer.getShip();
    }
}
