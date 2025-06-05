package it.polimi.ingsw.view.tui.screens.gameScreens.engineActions;

import it.polimi.ingsw.view.tui.screens.GameTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

import java.util.List;

public abstract class ManagerEnginesTuiScreen extends GameTuiScreen {
    protected static List<Integer> enginesIDs;
    protected static List<Integer> batteriesIDs;

    public ManagerEnginesTuiScreen(List<String> options) {
        super(options);
    }


    @Override
    public TuiScreens getType() {
        return TuiScreens.MenuCannons;
    }

    public void destroyStatic() {
        enginesIDs = null;
        batteriesIDs = null;
        spaceShipView = clientPlayer.getShip();
    }
}
