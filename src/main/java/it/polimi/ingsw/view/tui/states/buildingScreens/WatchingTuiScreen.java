package it.polimi.ingsw.view.tui.states.buildingScreens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.miniModel.player.PlayerDataView;
import it.polimi.ingsw.view.tui.states.BuildingTuiScreen;
import it.polimi.ingsw.view.tui.states.TuiScreens;

public class WatchingTuiScreen extends BuildingTuiScreen {

    public WatchingTuiScreen() {
        super();

        options.clear();
        for (PlayerDataView p : MiniModel.getInstance().getOtherPlayers()) {
            options.add("View " + p.getUsername() + "'s spaceship");
        }
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
