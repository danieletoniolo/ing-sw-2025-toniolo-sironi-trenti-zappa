package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.view.tui.screens.Building;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

public class WatchingBuilding extends Building {

    public WatchingBuilding() {
        super(null);
    }

    @Override
    protected String lineBeforeInput() {
        return "Waiting for other players...";
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.WatchingBuilding;
    }
}
