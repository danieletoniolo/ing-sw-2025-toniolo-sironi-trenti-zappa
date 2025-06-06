package it.polimi.ingsw.view.tui.screens.buildingScreens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.screens.BuildingTuiScreen;
import it.polimi.ingsw.view.tui.screens.TuiScreens;

public class WatchingTuiScreen extends BuildingTuiScreen {

    public WatchingTuiScreen() {
        super(null);
    }

    @Override
    protected String lineBeforeInput() {
        return "Waiting for other players...";
    }

    @Override
    public TuiScreens getType() {
        return TuiScreens.Watching;
    }
}
