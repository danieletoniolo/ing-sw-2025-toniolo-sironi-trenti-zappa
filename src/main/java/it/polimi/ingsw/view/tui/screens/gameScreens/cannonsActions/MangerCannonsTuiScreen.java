package it.polimi.ingsw.view.tui.screens.gameScreens.cannonsActions;

import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.List;

public abstract class MangerCannonsTuiScreen extends GameTuiScreen {
    protected static List<Integer> cannonsIDs;
    protected static List<Integer> batteriesIDs;

    public MangerCannonsTuiScreen(List<String> options) {
        super(options);
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.MenuCannons;
    }

    public void destroyStatic() {
        cannonsIDs = null;
        batteriesIDs = null;
        spaceShipView = clientPlayer.getShip();
    }
}
