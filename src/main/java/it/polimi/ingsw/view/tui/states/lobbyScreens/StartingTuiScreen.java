package it.polimi.ingsw.view.tui.states.lobbyScreens;

import it.polimi.ingsw.view.miniModel.MiniModel;
import it.polimi.ingsw.view.tui.states.LobbyTuiScreen;

public class StartingTuiScreen extends LobbyTuiScreen {

    public StartingTuiScreen() {
        isNewScreen = true;
        options.clear();
        message = MiniModel.getInstance().countDown.drawLineTui(0);
    }

    @Override
    protected String lineBeforeInput() {
        return "";
    }
}
