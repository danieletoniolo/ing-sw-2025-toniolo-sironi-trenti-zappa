package it.polimi.ingsw.view.tui.screens.gameScreens.swapGoodsState;

import it.polimi.ingsw.view.tui.screens.GameTuiScreen;

import java.util.ArrayList;
import java.util.List;

public abstract class GoodManagerTuiScreen extends GameTuiScreen {
    protected static ArrayList<Integer> from;
    protected static ArrayList<Integer> to;

    public GoodManagerTuiScreen(List<String> options) {
        super(options);

        spaceShipView = clientPlayer.getShip().clone();
        setMessage("Chosen goods: ");
    }
}
