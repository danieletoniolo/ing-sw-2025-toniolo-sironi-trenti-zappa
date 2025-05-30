package it.polimi.ingsw.view.tui.screens.lobbyScreens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.screens.LobbyTuiScreen;

public class StartingTuiScreen extends LobbyTuiScreen {

    public StartingTuiScreen() {
        isNewScreen = true;
        options.clear();
        message = MiniModel.getInstance().getCountDown().drawLineTui(0);
    }

    @Override
    protected String lineBeforeInput() {
        return "";
    }
}
